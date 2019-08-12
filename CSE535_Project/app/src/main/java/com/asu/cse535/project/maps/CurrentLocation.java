package com.asu.cse535.project.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.asu.cse535.project.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

public class CurrentLocation {
    private double latitude,longitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context context;
    private FragmentActivity activity;


    public  CurrentLocation (Activity activity) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
    }

    public  CurrentLocation (Context context,FragmentActivity activity,FusedLocationProviderClient mFusedLocationClient) {
        this.context = context;
        this.activity = activity;
        this.mFusedLocationClient = mFusedLocationClient;
        System.out.println("context: " + this.context + " activity: " + this.activity);
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);
    }


    public  CurrentLocation (FusedLocationProviderClient mFusedLocationClient) {
        this.mFusedLocationClient = mFusedLocationClient;
//        this.activity = activity;
//        System.out.println("context: " + this.context + " activity: " + this.activity);
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);
    }


    public void getCurrentLocation(){
        System.out.println("Come here");
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                System.out.println("Can not");
                return;
            }
            System.out.println("OK");
            Task mlocation = mFusedLocationClient.getLastLocation();
            System.out.println("mlocation: " + mlocation.getResult());
            mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    System.out.println("location 2: " + location);
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
//                        Location location = task.getResult();
                        System.out.println("Comehere again");
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        latitude = geoPoint.getLatitude();
                        longitude = geoPoint.getLongitude();
                        System.out.println("getLatitude in new Class: " + geoPoint.getLatitude() + " getLongitude: " + geoPoint.getLongitude());
                    }
                }
            });
        } catch (SecurityException e) {
            System.out.println("getDeviceLocation: SecurityException: " + e.getMessage());
        }
//        {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                System.out.println("Comehere again");
//                if(task.isSuccessful()) {
//                    //Log.d(TAG, "getLastKnownLocation: called");
//                    System.out.println("but not here");
//                    Location location = task.getResult();
//                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
//                    latitude = geoPoint.getLatitude();
//                    longitude = geoPoint.getLongitude();
//                    System.out.println("getLatitude in new Class: " + geoPoint.getLatitude() + " getLongitude: " + geoPoint.getLongitude());
////                    Log.d(TAG,"geoPointgetLatitude: " + geoPoint.getLatitude() + " geoPointgetLongitude: " + geoPoint.getLongitude());
////                    Log.d(TAG,"locationgetLatitude: " + location.getLatitude() + " locationgetLongitude: " + location.getLongitude());
//
//                }
//
//            }
//        });
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
