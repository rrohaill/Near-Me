package com.rohail.apps.nearme.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rohail.apps.nearme.R;
import com.rohail.apps.nearme.adapters.RecyclerViewAdapter;
import com.rohail.apps.nearme.models.LocationModel;
import com.rohail.apps.nearme.utilities.Constants;
import com.rohail.apps.nearme.utilities.FetchPlacesDataTask;
import com.rohail.apps.nearme.utilities.Logger;
import com.rohail.apps.nearme.utilities.PopupDialogs;
import com.rohail.apps.nearme.utilities.StaticData;

import java.util.ArrayList;
import java.util.HashMap;


public class MapActivity extends BaseMapActivity implements LocationListener,
        RecyclerViewAdapter.ClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    public static final String KEY_RADIUS_5KM = "5000";
    public static final String KEY_RADIUS_10KM = "10000";
    public static final String KEY_RADIUS_15KM = "15000";
    public static final String KEY_RADIUS_20KM = "20000";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 121;
    private static final HashMap<String, Integer> hashMap = new HashMap<String, Integer>(
            6);

    static {
        hashMap.put(KEY_RADIUS_5KM, R.id.radio_5km);
        hashMap.put(KEY_RADIUS_10KM, R.id.radio_10km);
        hashMap.put(KEY_RADIUS_15KM, R.id.radio_15km);
        hashMap.put(KEY_RADIUS_20KM, R.id.radio_20km);

    }

    String placeTypesLabels[] = {"Accounting", "Airport", "Amusement Park", "Aquarium", "Art Gallery", "ATM", "Bakery", "Bank",
            "Bicycle Store", "Book Store", "Bowling Alley", "Bus Station", "Cafe", "Camp Ground", "Car Dealer",
            "Car Rental", "Car Repair", "Car Wash", "Cemetery", "City Hall", "Clothing Store", "Convenience Store", "Court House",
            "Dentist", "Department Store", "Doctor", "Electrician", "Electronics Store", "Embassy", "Fire Station", "Florist",
            "Funeral Home", "Furniture Store", "Gas Station", "Grocery/Supermarket", "Gym", "Hair Care", "Hardware Store",
            "Home Goods Store", "Hospital", "Jewelry Store", "Laundry", "Lawyer", "Library", "Local Government Office", "Locksmith",
            "Lodging", "Meal Delivery", "Meal Takeaway", "Mosque", "Moving Company", "Museum", "Painter", "Park", "Parking",
            "Pet Store", "Pharmacy", "Plumber", "Police", "Post Office", "Real Estate Agency", "Restaurant", "Roofing Contractor",
            "RV Park", "School", "Shoe Store", "Shopping Mall", "Stadium", "Storage", "Store", "Subway Station", "Taxi Stand",
            "Train Station", "Transit Station", "Travel Agency", "University", "Veterinary Care", "Zoo"};
    String placeTypesValues[] = {"accounting", "airport", "amusement_park", "aquarium", "art_gallery", "atm", "bakery", "bank",
            "bicycle_store", "book_store", "bowling_alley", "bus_station", "cafe", "campground", "car_dealer",
            "car_rental", "car_repair", "car_wash", "cemetery", "city_hall", "clothing_store", "convenience_store", "courthouse",
            "dentist", "department_store", "doctor", "electrician", "electronics_store", "embassy", "fire_station", "florist",
            "funeral_home", "furniture_store", "gas_station", "grocery_or_supermarket", "gym", "hair_care", "hardware_store",
            "home_goods_store", "hospital", "jewelry_store", "laundry", "lawyer", "library", "local_government_office", "locksmith",
            "lodging", "meal_delivery", "meal_takeaway", "mosque", "moving_company", "museum", "painter", "park", "parking",
            "pet_store", "pharmacy", "plumber", "police", "post_office", "real_estate_agency", "restaurant", "roofing_contractor",
            "rv_park", "school", "shoe_store", "shopping_mall", "stadium", "storage", "store", "subway_station", "taxi_stand",
            "train_station", "transit_station", "travel_agency", "university", "veterinary_care", "zoo"};
    private ArrayList<Marker> markerList;
    //    private GPSTracker gpsTracker;
    private String strCurrentLat, strCurrentLong, strRadius, strPlace;
    private double currentLat, currentLng;
    private LinearLayout mapLayout;
    private Button btnSelectPlace, btnSelectRadius;
    private Location mLastLocation;
    private Dialog dialog;
    private GoogleApiClient mGoogleApiClient = null;
    private AdView mAdView;
    private InterstitialAd interstitialAds = null;
    private String notificatioId = "";
    private String notificatioMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fetchIntents();
        loadAds();
        loadInterstitialAds();

        getFCMToken();

        generateNotificationId();

        markerList = new ArrayList<Marker>();
        strRadius = KEY_RADIUS_5KM;
        strPlace = "restaurant";

        btnSelectPlace = (Button) findViewById(R.id.btnSelectPlace);
        btnSelectRadius = (Button) findViewById(R.id.btnSelectRadius);
        btnSelectPlace.setText("Restaurant");
        btnSelectRadius.setText("5 KM");

        if (!checkInternet(MapActivity.this)) {
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        recreate();
                    }
                }
            };
            dialog = PopupDialogs.createConfirmationDialog("Internet connection problem!\n" +
                    " Connect to the internet and try again.", "Alert Notification", MapActivity.this, listenerOk, "OK", listenerTry, "Try", PopupDialogs.Status.INFO);
            return;
        } else {
            //todo
            //Get it to work And use the getApplicationContext and check it if it is not null
//            gpsTracker = new GPSTracker(MapActivity.this);

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
//            if (gpsTracker.canGetLocation()) {
//                currentLocation = new Location("");
//                currentLocation.setLatitude(gpsTracker.getLatitude());
//                currentLocation.setLongitude(gpsTracker.getLongitude());
//                strCurrentLat = String.valueOf(currentLocation.getLatitude());
//                strCurrentLong = String.valueOf(currentLocation.getLongitude());
////                Logger.i("Current Loc : " + strCurrentLat + "  " + strCurrentLong);
////                Toast.makeText(MapActivity.this, "Loc : " + strCurrentLat + " " + strCurrentLong, Toast.LENGTH_LONG).show();
////                setUpMapIfNeeded();
//                mGoogleApiClient.connect();
//            }
//
//            strCurrentLat = "33.718105";
//            strCurrentLong = "73.037483";
        }

        final ListView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strPlaceAtPosition = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < placeTypesLabels.length; i++) {
                    if (placeTypesLabels[i].equals(strPlaceAtPosition)) {
                        strPlace = placeTypesValues[i];
                        btnSelectPlace.setText(placeTypesLabels[i]);
                        break;
                    }
                }
//                strPlace = placeTypesValues[position];
//                btnSelectPlace.setText(placeTypesLabels[position]);
                dialog.dismiss();
                fetchGoogleNearbyPlaces();
            }
        };
        btnSelectPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = PopupDialogs.createListDialog(
                        "Select Place", placeTypesLabels, MapActivity.this, listener, PopupDialogs.Status.PLACE);
            }
        });

        btnSelectRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MapActivity.this, PathFindingActivity.class);
//                startActivity(intent);
                dialog = PopupDialogs.createRadioDialog(
                        "Select Radius",
                        new OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group,
                                                         int checkedId) {
                                if (dialog != null && dialog.isShowing()) {
                                    if (checkedId == R.id.radio_5km) {
                                        strRadius = KEY_RADIUS_5KM;
                                        btnSelectRadius.setText("5 KM");
                                    } else if (checkedId == R.id.radio_10km) {
                                        strRadius = KEY_RADIUS_10KM;
                                        btnSelectRadius.setText("10 KM");
                                    } else if (checkedId == R.id.radio_15km) {
                                        strRadius = KEY_RADIUS_15KM;
                                        btnSelectRadius.setText("15 KM");
                                    } else if (checkedId == R.id.radio_20km) {
                                        strRadius = KEY_RADIUS_20KM;
                                        btnSelectRadius.setText("20 KM");
                                    }
                                    dialog.dismiss();
                                    fetchGoogleNearbyPlaces();
                                }
                            }
                        }, MapActivity.this, PopupDialogs.Status.DISTANCE, hashMap
                                .get(strRadius));

            }
        });

    }

    private void generateNotificationId() {
        if (notificatioId!=null) {
            PopupDialogs.createConfirmationDialog(notificatioMsg, "Notification", this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.rohail.apps.nearme")));
                    finish();
                }
            });
        }
    }

    private void fetchIntents() {
        if (getIntent().getExtras() != null) {
            notificatioId = (String) getIntent().getExtras().get(Constants.IntentKeys.KEY_NOTIFICATION_ID);
            notificatioMsg = (String) getIntent().getExtras().get(Constants.IntentKeys.KEY_NOTIFICATION_MSG);
        }
    }

    private void getFCMToken() {
        Logger.i(FirebaseInstanceId.getInstance().getToken());
    }

    private void loadInterstitialAds() {
        this.interstitialAds = new InterstitialAd(MapActivity.this);
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

    @Override
    protected void onStart() {
        if (!checkInternet(MapActivity.this)) {
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
                    Intent intent = new Intent(MapActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            dialog = PopupDialogs.createConfirmationDialog("Internet connection problem!\n" +
                    " Connect to the internet and try again.", "Alert Notification", MapActivity.this, listenerOk, "OK", listenerTry, "Try", PopupDialogs.Status.INFO);
            super.onStart();
            return;
        } else {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                fetchGoogleNearbyPlaces();

            }
        }
    }

    private void setUpMap() {
        if (mMap != null) {
//            addMarker(33.713156, 73.039579, "Warid", "Main Head Branch/Franchise");
            markerList.clear();
            addMarker(Double.parseDouble(strCurrentLat), Double.parseDouble(strCurrentLong), "My Location", "My currrent location on map.");

//            addMarker(currentLocation.getLatitude(), currentLocation.getLongitude(), "My Location", "My currrent location on map");
            if (StaticData.listLocations != null) {
                for (int i = 0; i < StaticData.listLocations.size(); i++) {
                    LocationModel location = StaticData.listLocations.get(i);
                    addMarker(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()), location.getName(), location.getVicinity());
                }
            }
            markerList.get(0).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_light_blue));
//            setMarkerColor(markerList.get(0), BitmapDescriptorFactory.HUE_GREEN);

//            setMarkerClickListener();
            LatLng latlng = new LatLng(Double.parseDouble(strCurrentLat), Double.parseDouble(strCurrentLong));
//            LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng) // Sets the center of the map to Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(90) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

//            setMarkerDragListener();
            mMap.addCircle(new CircleOptions().center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .radius(500)
                    .strokeColor(Color.DKGRAY)
                    .fillColor(Color.TRANSPARENT)
                    .strokeWidth(2));

            setMarkerClickListener();
        }
    }

    public void addMarker(double lat, double lng, String title, String snippet) {

        LatLng latlng = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_light_green))
        );
//        .draggable(true)

//        setMarkerColor(marker, BitmapDescriptorFactory.HUE_AZURE);
        markerList.add(marker);

//		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));

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
            }
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public void fetchGoogleNearbyPlaces() {
        if (!checkInternet(MapActivity.this)) {
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
                    fetchGoogleNearbyPlaces();
                }
            };
            dialog = PopupDialogs.createConfirmationDialog("Internet connection problem!\n" +
                    " Connect to the internet and try again.", "Alert Notification", MapActivity.this, listenerOk, "OK", listenerTry, "Try", PopupDialogs.Status.INFO);
            return;
        } else {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//            sb.append("location=" + 33.718105 + "," + 73.037483);
            sb.append("location=" + strCurrentLat + "," + strCurrentLong);
            sb.append("&radius=" + strRadius);
            sb.append("&types=" + strPlace);
            sb.append("&key=".concat(getResources().getString(R.string.google_maps_key)));

//      Finaly url is as follow
//      https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.718105,73.037483&radius=5000&sensor=true&key=AIzaSyB6KARtxszk1XZr0-0P0uEaQjAZEQ8RlsQ

            // Creating a new non-ui thread task in FetchPlacesDataTask class to download json data
            FetchPlacesDataTask fetchPlacesDataTask = new FetchPlacesDataTask(MapActivity.this);

            fetchPlacesDataTask.execute(sb.toString());
        }

    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent = new Intent(MapActivity.this, PathFindingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("source", currentLocation);
        bundle.putSerializable("destination", StaticData.listLocations.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void populatePlaces() {
        if (StaticData.listLocations != null) {
            mMap.clear();
            setUpMap();
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(RecyclerViewAdapter.Type.linear, StaticData.listLocations, currentLocation, MapActivity.this);
            adapter.setClickListener(MapActivity.this);


            LinearLayoutManager layoutManager = new LinearLayoutManager(MapActivity.this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1000);
            itemAnimator.setRemoveDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);

        }
        StaticData.progressDialog.hide();
    }

    @Override
    public void onConnected(Bundle bundle) {
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            currentLocation = new Location("");
            currentLocation = mLastLocation;
//            currentLocation.setLatitude(gpsTracker.getLatitude());
//            currentLocation.setLongitude(gpsTracker.getLongitude());
            strCurrentLat = String.valueOf(currentLocation.getLatitude());
            strCurrentLong = String.valueOf(currentLocation.getLongitude());
//            Toast.makeText(MapActivity.this, "Loc : " + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            setUpMapIfNeeded();
        } else {
            showSettingsAlert(MapActivity.this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    onConnected(getIntent().getExtras());

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void showSettingsAlert(final Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            currentLocation = new Location("");
            currentLocation = mLastLocation;
            strCurrentLat = String.valueOf(currentLocation.getLatitude());
            strCurrentLong = String.valueOf(currentLocation.getLongitude());
            setUpMapIfNeeded();
        } else {
            showSettingsAlert(MapActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        View.OnClickListener listenerExit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        PopupDialogs.createConfirmationDialog("Do you want to exit application?", "Alert Notification", MapActivity.this, listenerExit);
    }

}

