package com.rohail.apps.nearme.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.rohail.apps.nearme.utilities.StaticData;
import com.rohail.apps.nearme.utilities.Utility;
import com.rohail.apps.nearme.utilities.PopupDialogs;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class BaseMapActivity extends FragmentActivity {

    protected GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected Location currentLocation;
    private Dialog dialog;

    public static boolean checkInternet(Context con) {

        ConnectivityManager connectivity = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setMarkerClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {

                View.OnClickListener listenerPath = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < StaticData.listLocations.size(); i++) {
                            if (marker.getTitle().equals(StaticData.listLocations.get(i).getName())) {
                                Intent intent = new Intent(BaseMapActivity.this, PathFindingActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("source", currentLocation);
                                bundle.putSerializable("destination", StaticData.listLocations.get(i));
                                intent.putExtras(bundle);
                                startActivity(intent);

                                dialog.dismiss();
                                break;
                            }
                        }
                    }
                };
                View.OnClickListener listenerCancel = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                };

                if (!marker.getTitle().equals("My Location") && !marker.getSnippet().equals("My currrent location on map.")) {
                    LatLng latLng = marker.getPosition();
                    String distance = Utility.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), latLng.latitude, latLng.longitude) + "";
                    distance = distance.substring(0, 4) + " KM,  ";
                    dialog = PopupDialogs.createConfirmationDialog(distance + marker.getSnippet(), marker.getTitle(),
                            BaseMapActivity.this, listenerPath, "Path", listenerCancel, "Cancel", PopupDialogs.Status.INFO);
                }

                return true;
            }

        });
    }

    public void setMarkerDragListener() {
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);

                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });
    }

    public void setMarkerColor(Marker marker, float color) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(color));
    }


}

