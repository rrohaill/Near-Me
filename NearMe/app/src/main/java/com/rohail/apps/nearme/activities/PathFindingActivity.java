package com.rohail.apps.nearme.activities;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rohail.apps.nearme.R;
import com.rohail.apps.nearme.models.LocationModel;
import com.rohail.apps.nearme.utilities.PopupDialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PathFindingActivity extends BaseMapActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1221;
    private int locatorType, markerIcon;
    private SupportMapFragment fragmentMapView;
    private LocationModel targetLocation;
    private Dialog dialog;
    private double currentLatitude, targetLatitude, currentLongitude, targetLongitude;
    private AdView mAdView;
    private InterstitialAd interstitialAds = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_finding);
        fetchIntents();

        loadAds();
        loadInterstitialAds();

        if (!checkInternet(this)) {
            View.OnClickListener listenerOk = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            };
            View.OnClickListener listenerTry = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    recreate();
                }
            };
            dialog = PopupDialogs.createConfirmationDialog("Internet connection problem!\n" +
                    " Connect to the internet and try again.", "Alert Notification", PathFindingActivity.this, listenerOk, "OK", listenerTry, "Try", PopupDialogs.Status.INFO);
//
        } else {
            if (mMap == null) {
                fragmentMapView = ((SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentMapView));
                mMap = fragmentMapView.getMap();
            }
        }

        if (mMap != null) {
            LatLng lastLatLng = new LatLng(targetLatitude, targetLongitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(lastLatLng) // Sets the center of the map to
                    // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(40) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            // target tag

            mMap.addMarker(new MarkerOptions().title(targetLocation.getName())
                    .position(new LatLng(targetLatitude, targetLongitude))
                    .snippet(targetLocation.getVicinity())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_light_green)));

            // current location tag
            mMap.addMarker(new MarkerOptions()
                    .title("Source Location")
                    .position(new LatLng(currentLatitude, currentLongitude))
                    .snippet("Source Location on map for path")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_light_blue)));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                    return;
                }
            }
            mMap.setMyLocationEnabled(true);
            new connectAsyncTask(makeURL(currentLatitude, currentLongitude,
                    targetLatitude, targetLongitude)).execute();
        }
    }


    private void loadInterstitialAds() {
        this.interstitialAds = new InterstitialAd(PathFindingActivity.this);
        this.interstitialAds.setAdUnitId(getResources().getString(R.string.interstitial_ad_id));

        AdRequest adr = new AdRequest.Builder().build();
        // add your test device here
        //adr.addTestDevice("8E452640BC83C672B070CDCA8AB9B06B");
        interstitialAds.loadAd(adr);

        // Prepare an Interstitial Ad Listener
        interstitialAds.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                displayInterstitial();
            }
        });
    }

    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitialAds.isLoaded()) {
            interstitialAds.show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    onCreate(getIntent().getExtras());
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    PopupDialogs.createAlertDialog("Permission Denied", "Alert", getApplicationContext(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }, PopupDialogs.Status.ERROR);
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    private void loadAds() {
        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID
        // set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.adView);

        // Create an ad request. Check your logcat output for the hashed device
        // ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(
                AdRequest.DEVICE_ID_EMULATOR).build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    // new methods
    public String makeURL(double sourcelat, double sourcelog, double destlat,
                          double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    // drawing of poly line
    public void drawPath(String result) {
        try {
            // Transform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(8).color(Color.parseColor("#636161")).geodesic(true));
            }
        } catch (JSONException e) {
        }
    }

    // decode poly
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void fetchIntents() {
        Location location = (Location) getIntent().getExtras().getParcelable("source");
        targetLocation = (LocationModel) getIntent().getExtras().getSerializable("destination");
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        targetLatitude = Double.parseDouble(targetLocation.getLatitude());
        targetLongitude = Double.parseDouble(targetLocation.getLongitude());
    }

    // async
    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        String url;
        private ProgressDialog progressDialog;

        connectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PathFindingActivity.this);
            progressDialog.setMessage("Fetching route\nPlease wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
            }
        }
    }

    // json parser class
    public class JSONParser {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // constructor
        public JSONParser() {
        }

        public String getJSONFromUrl(String strUrl) {

            URL url = null;
            try {
                url = new URL(strUrl);
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    is = conn.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            return json;
        }
    }

}