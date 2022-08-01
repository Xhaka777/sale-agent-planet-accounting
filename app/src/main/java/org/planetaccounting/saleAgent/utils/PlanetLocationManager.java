package org.planetaccounting.saleAgent.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class PlanetLocationManager {

    private Location currentLocation;

    private LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean gpsEnable;


    public PlanetLocationManager(Activity activity) {
        getLastKnowLocation(activity);

    }

    public Location getCurrentLocation (){
        return currentLocation;
    }

    public String getLatitude(){

        if (currentLocation == null) {
             return "0";
        }
        return  currentLocation.getLatitude()+ "";
    }


    public String getLongitude(){
        if (currentLocation == null) {
            return "0";
        }
        return  currentLocation.getLongitude()+ "";
    }


    private Location getLastKnowLocation(Activity activity) {
        Context context = activity.getApplicationContext();
        try {
            String locationProvider = LocationManager.GPS_PROVIDER;
            if (locationManager == null) {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                fusedLocation(activity);

            } else {

                if (!gpsEnable) {

                    fusedLocation(activity);

                } else {

                    currentLocation =  locationManager.getLastKnownLocation(locationProvider);

                }
            }


            return currentLocation;
        } catch (SecurityException e) {
            return null;
        }
    }

    private void fusedLocation(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            currentLocation = location;
                        }
                    });
        }
    }
}
