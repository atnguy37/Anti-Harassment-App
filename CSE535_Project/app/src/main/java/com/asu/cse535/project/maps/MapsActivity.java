package com.asu.cse535.project.maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.asu.cse535.project.R;
import com.asu.cse535.project.User;
import com.asu.cse535.project.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.asu.cse535.project.Constant.MAP_VIEW_BUNDLE_KEY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    FirebaseFirestore firebaseDB;
    DocumentReference userLocations;
    private GoogleMap mMap;
    private static final String TAG = "MapActivity";
    private FusedLocationProviderClient mFusedLocationClient;
    private MapView mMapView;
    private FirebaseUser AreYouSignInAccount;
    private double latitude,longitude;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"MapActivity");
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        getLastKnownLocation();
//        mSearchText = view.findViewById(R.id.input_search);
//        mGps = view.findViewById(R.id.ic_gps);
        //initGoogleMap(savedInstanceState);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady");
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(TAG,"getLastKnownLocation");
        mMap.setMyLocationEnabled(true);
        //Log.d(TAG,"First");
        getLastKnownLocation();
        mMap = googleMap;
//        init();

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

//    private void initGoogleMap(Bundle savedInstanceState){
//        // *** IMPORTANT ***
//        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
//        // objects or sub-Bundles.
//        Bundle mapViewBundle = null;
//        if (savedInstanceState != null) {
//            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
//        }
//
//        mMapView.onCreate(mapViewBundle);
//
//        mMapView.getMapAsync(this);
//    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()) {
                    int returnCode;
                    Log.d(TAG, "getLastKnownLocation: called");
                    Location location = task.getResult();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    System.out.println("Click on authen: " + latitude + " " + longitude);


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("latitude",latitude);
                    returnIntent.putExtra("longitude",longitude);
                    Intent receive = getIntent();
                    UserID = receive.getStringExtra("UserID");

////                    System.out.println("Activity: " + receive.getStringExtra("Activity"));
//                    if( receive.getExtras() != null)
//                    {
//                        setResult(100,returnIntent);
//                        System.out.println("Activity: " + receive.getStringExtra("Activity"));
//                    }
//
//                    else
                    setResult(RESULT_OK,returnIntent);
                    saveUserLocation(receive.getStringExtra("Activity"));

                    finish();

//                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
//                    latitude = geoPoint.getLatitude();
//                    longitude = geoPoint.getLongitude();
////                    System.out.println("getLatitude: " + geoPoint.getLatitude() + " getLongitude: " + geoPoint.getLongitude());
//                    Log.d(TAG,"geoPointgetLatitude: " + geoPoint.getLatitude() + " geoPointgetLongitude: " + geoPoint.getLongitude());
////                    Log.d(TAG,"locationgetLatitude: " + location.getLatitude() + " locationgetLongitude: " + location.getLongitude());
//                    if(mUserLocation == null){
//                        mUserLocation = new UserLocation();
//                        mUserLocation.setGeo_point(geoPoint);
//                        mUserLocation.setTimestamp(null);
//                        mUserLocation.setUser_id(UserID);
//                        saveUserLocation();
//                    }
//                    setCameraView();
//                    getNearbyPlaces();
                }

            }
        });
    }


    private void saveUserLocation(String activity) {
        String saveLocation;
//        DocumentReference locationRef = firebaseDB
//                .collection(getString(R.string.collection_user_locations))
//                .document(FirebaseAuth.getInstance().getUid());
        System.out.println("Activity: " + activity);
        if (activity.equals("MainActivity"))
            saveLocation = "Shaking";
        else if (activity.equals("Button"))
            saveLocation = "Button";
        else if (activity.equals("Voice"))
            saveLocation = "Voice";
        else return;
        firebaseDB = initFirestore();
        userLocations = firebaseDB.collection(getString(R.string.collection_user_locations)).document(getString(R.string.user_location_db_id));
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
//        JSONObject geoPoint = new JSONObject();
//        ArrayList<String> geoPoint = new ArrayList();
        Map<String, Object> Locations = new HashMap<>();
        Map<String, Object> userLocationStr = new HashMap<>();
//        System.out.println("geoPoint: " + geoPoint[0]);
//        System.out.println("geoPoint: " + geoPoint[1]);
        Date date= new Date();

        long time = date.getTime();
        JSONObject obj = new JSONObject();
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis(),0);
        try {
//            geoPoint.put("latitude",Double.toString(latitude));
//            geoPoint.put("longitude",Double.toString(longitude));
//            Map<String, Object> geoMap = new Gson().fromJson(
//                    geoPoint.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());

            obj.put("geo_point",geoPoint);
            obj.put("timestamp", new Timestamp(time));
            obj.put("user_id", UserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("JSONObject: " + obj);
        System.out.println("geoPoint222: " + geoPoint);
        Map<String, Object> retMap = new Gson().fromJson(
                obj.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
        Locations.put(Long.toString(time), retMap);
        userLocationStr.put(saveLocation,Locations);
//        System.out.println("Location: " + Locations);
//        System.out.println("userLocationStr: " + userLocationStr);
//        Locations.put("Locations", geoPoint);
        userLocations.set(userLocationStr, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                        }
                    });

        }

    public FirebaseFirestore initFirestore() {
        FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
        return firebaseDB;
    }
}
