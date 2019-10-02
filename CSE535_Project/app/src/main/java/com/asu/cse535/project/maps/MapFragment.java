package com.asu.cse535.project.maps;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.asu.cse535.project.LocationHistory;
import com.asu.cse535.project.R;
import com.asu.cse535.project.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.asu.cse535.project.Constant.BLUE_LIGHT;
import static com.asu.cse535.project.Constant.GOOGLE_MAPS_API_KEY;
import static com.asu.cse535.project.Constant.MAP_VIEW_BUNDLE_KEY;
import static com.asu.cse535.project.Constant.REQUEST_CODE_CURRENT_LOCATION;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    String UserID;
    //widgets
    private MapView mMapView;
    private boolean unSafe = false;

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    private double latitude,longitude;
    private int PROXIMITY_RADIUS = 5000;
    private static final float DEFAULT_ZOOM = 15f;
    private EditText mSearchText;
    private ImageView mGps;
    private UserLocation mUserLocation;
    FirebaseUser FireBaseSignInAccount;
    FirebaseFirestore firebaseDB;
    FirebaseAuth mAuth;
    private TextView mapText;

    DocumentReference userContactsDocRef;

    //private FusedLocationProviderClient mFusedLocationProviderClient;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,FirebaseAuth.getInstance().getUid());
        UserID = FirebaseAuth.getInstance().getUid();
        firebaseDB = FirebaseFirestore.getInstance();
//        if (getArguments() != null) {
//            mUserList = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
//        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safe_zones, container, false);
        mMapView = view.findViewById(R.id.nearby_map);
        mapText = view.findViewById(R.id.maps_text);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSearchText = view.findViewById(R.id.input_search);
        mGps = view.findViewById(R.id.ic_gps);
        initGoogleMap(savedInstanceState);


        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mGoogleMap.addMarker(options);
        }

        hideSoftKeyboard(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        //getLastKnownLocation();
//        String getArgument = getArguments().getString
//        unSafe = getArguments().getBoolean("Unsafe");
//        System.out.println("receive something Unsafe " + unSafe);
        if(this.getArguments()!= null) {
            unSafe = getArguments().getBoolean("Unsafe");
            System.out.println("receive something in Maps");
            System.out.println("unSafe in resume: " + unSafe);
            this.getArguments().remove("Unsafe");

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //DONT DELETE - Mario
        userContactsDocRef = firebaseDB.collection("users").document(UserID);
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    private void setCameraView () {
        Log.d(TAG,"latitudeLatitude: " + latitude + " longitudeLongitude: " + longitude);
        mMapBoundary = new LatLngBounds(
                new LatLng(latitude - .1,longitude - .1),
                new LatLng(latitude + .1,longitude + .1)
        );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary,0));
        Log.d(TAG,"setCameraView: called");

    }

    private void getLastKnownLocation() {
//        if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    Activity#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for Activity#requestPermissions for more details.
//            return;
//        }
//        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if(task.isSuccessful()) {
//                    Log.d(TAG, "getLastKnownLocation: called");
//                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(latitude,longitude);
//                    latitude = geoPoint.getLatitude();
//                    longitude = geoPoint.getLongitude();
//                    System.out.println("getLatitude: " + geoPoint.getLatitude() + " getLongitude: " + geoPoint.getLongitude());
                    Log.d(TAG,"geoPointgetLatitude: " + geoPoint.getLatitude() + " geoPointgetLongitude: " + geoPoint.getLongitude());
//                    Log.d(TAG,"locationgetLatitude: " + location.getLatitude() + " locationgetLongitude: " + location.getLongitude());
                    if(mUserLocation == null){
                        mUserLocation = new UserLocation();
                        mUserLocation.setGeo_point(geoPoint);
                        mUserLocation.setTimestamp(null);
                        mUserLocation.setUser_id(UserID);
                        saveUserLocation();
                    }
                    setCameraView();

                    //get near by
                    mGoogleMap.clear();
                    for (int i = 0; i < BLUE_LIGHT.length; i++) {
                        getNearbyPlaces("police");
                        getNearbyPlaces("hospital");
                    }


    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
        map.setMyLocationEnabled(true);
        Log.d(TAG,"First");

        // 1. create an intent pass class name or intnet action name
        Intent mapLocation = new Intent(getContext(), MapsActivity.class);
        mapLocation.putExtra("Activity",  "MapsFragment");
        mapLocation.putExtra("UserID",UserID);
//        // 2. put X, Y in intent
//        intent.putExtra("x",  etX.getText().toString());
//        intent.putExtra("y",  etY.getText().toString());

        // 3. start the activity
        startActivityForResult(mapLocation, REQUEST_CODE_CURRENT_LOCATION);


        mGoogleMap = map;
    }

    private void saveUserLocation(){

        if(mUserLocation != null){
            DocumentReference locationRef = firebaseDB
                    .collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());

            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude() +
                                "\n user: " + mUserLocation.getUser_id());
                    }
                }
            });


            //Mario Stuff don't delete!
            Map<String, Object> Locations = new HashMap<>();
            Locations.put("Locations", mUserLocation);

            userContactsDocRef
                    .set(Locations, SetOptions.merge())
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
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
//                getDeviceLocation();
                moveCamera(new LatLng(latitude, longitude),
                        DEFAULT_ZOOM,
                        "My Location");
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();
        Log.d(TAG, "Search String: " + searchString);
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

//    private void getDeviceLocation(){
//        Log.d(TAG, "getDeviceLocation: getting the devices current location");
//
////        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
////
////        try{
////            if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
////
////                final Task location = mFusedLocationClient.getLastLocation();
////                location.addOnCompleteListener(new OnCompleteListener() {
////                    @Override
////                    public void onComplete(@NonNull Task task) {
////                        if(task.isSuccessful()){
////                            Log.d(TAG, "onComplete: found location!");
////                            Location currentLocation = (Location) task.getResult();
//
//                            moveCamera(new LatLng(latitude, longitude),
//                                    DEFAULT_ZOOM,
//                                    "My Location");
//
////                        }else{
////                            Log.d(TAG, "onComplete: current location is null");
////                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });
////            }
////        }catch (SecurityException e){
////            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
////        }
//    }private void getDeviceLocation(){
//        Log.d(TAG, "getDeviceLocation: getting the devices current location");
//
////        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
////
////        try{
////            if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
////
////                final Task location = mFusedLocationClient.getLastLocation();
////                location.addOnCompleteListener(new OnCompleteListener() {
////                    @Override
////                    public void onComplete(@NonNull Task task) {
////                        if(task.isSuccessful()){
////                            Log.d(TAG, "onComplete: found location!");
////                            Location currentLocation = (Location) task.getResult();
//
//                            moveCamera(new LatLng(latitude, longitude),
//                                    DEFAULT_ZOOM,
//                                    "My Location");
//
////                        }else{
////                            Log.d(TAG, "onComplete: current location is null");
////                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });
////            }
////        }catch (SecurityException e){
////            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
////        }
//    }

    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+GOOGLE_MAPS_API_KEY);

        Log.d(TAG, "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    public void getNearbyPlaces(String safe) {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        //mGoogleMap.clear();
        String url = getUrl(latitude, longitude, safe);
        dataTransfer[0] = mGoogleMap;
        dataTransfer[1] = url;

        getNearbyPlacesData.execute(dataTransfer);
//        if(!unSafe)
//            Toast.makeText(getActivity(), "Showing Nearby Locations", Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(getActivity(), "Showing Unsafe Zones", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"getNearbyPlaces: called");
        Log.d(TAG,"latitudeLatitude: " + latitude + " longitudeLongitude: " + longitude);

    }
    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CURRENT_LOCATION) {
            if(resultCode == RESULT_OK){
                System.out.println("MapFragment: " + unSafe);
                latitude = data.getDoubleExtra("latitude",0.0);
                longitude = data.getDoubleExtra("longitude",0.0);
//            tvResult.setText(data.getIntExtra("result",-1)+"");
                System.out.println("MapFragment");
                if(!unSafe)
                    getLastKnownLocation();
                else {
                    mapText.setText(getString(R.string.maps_unsafe));
                    showUnsafeZones();
                }

                init();
            }
        }

    }


    private void showUnsafeZones(){

        DocumentReference locationRef = firebaseDB.collection(getString(R.string.collection_user_locations)).document(getString(R.string.user_location_db_id));
        locationRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    HashMap<Double, Double> data = new HashMap<Double, Double>();
//                    SmsManager smgr = SmsManager.getDefault();
//                    user = documentSnapshot.toObject(User.class);
                    System.out.println("Get Object: " + documentSnapshot);
                    LocationHistory location = documentSnapshot.toObject(LocationHistory.class);

                    ArrayList<Object> temp_values_button = new ArrayList<>();
                    ArrayList<Object> temp_values_voice = new ArrayList<>();
                    ArrayList<Object> temp_values_shaking = new ArrayList<>();
//                    ArrayList<String> temp_keys = new ArrayList<>();
//                    ArrayList<String> phone_keys;
//                    phone_keys = new ArrayList<>();

                    temp_values_button = new ArrayList<Object>(location.getButton().values()); //phones
                    temp_values_voice = new ArrayList<Object>(location.getVoice().values());
                    temp_values_shaking = new ArrayList<Object>(location.getShaking().values());
                    System.out.println("Get Object Button: " + temp_values_button.size());
                    for (int i = 0; i < temp_values_button.size(); i++){
//                        String phone_number = "+" + phone_keys.get(i);
//                        smgr.sendTextMessage(phone_keys.get(i),null,textMessage,null,null);
//                        Log.i(LOG, "message send to " + phone_number);
//                        System.out.println("Get Object: " + i + " " + temp_values_button.get(i).toString());
                        getGeoPoint(temp_values_button.get(i).toString());

                        if(data.isEmpty() || !data.containsKey(latitude) || data.get(latitude) != longitude ) {
                            data.put(latitude,longitude);
                        }
//                        System.out.println("Get Object: " + i + " latitude: " + latitude + " longitude: " + longitude);
//                        DocumentSnapshot userSnapshot = (DocumentSnapshot) temp_values_button.get(i);
//                        UserLocation user = userSnapshot.toObject(UserLocation.class);

//                        Map<String, Object> map = (Map<String, Object>) temp_values_button.get(i).getValue();
//                        System.out.println("Time: " + i + " " + user.getTimestamp());
                    }
                    for (int i = 0; i < temp_values_voice.size(); i++){
                        getGeoPoint(temp_values_voice.get(i).toString());

                        if(data.isEmpty() || !data.containsKey(latitude) || data.get(latitude) != longitude ) {
                            data.put(latitude,longitude);
                        }
//
                    }

                    for (int i = 0; i < temp_values_shaking.size(); i++){
                        getGeoPoint(temp_values_shaking.get(i).toString());
                        if(data.isEmpty() || !data.containsKey(latitude) || data.get(latitude) != longitude ) {
                            data.put(latitude,longitude);
                        }
                    }

                    if(data.size() > 0)
                        showUnsafeZoneOnMap(data);

                }
            }
            });


    }


    private void getGeoPoint(String data) {
//        print(leng)
        int start = data.indexOf("latitude");
        int end = data.indexOf(",",start);
        double latitude = Double.parseDouble(data.substring(start+"latitude".length() + 1, end));
//        System.out.println(start + " " + end);
        start = data.indexOf("longitude");
        end = data.indexOf("}",start);
        double longitude = Double.parseDouble(data.substring(start+"longitude".length() + 1, end-1));
//        System.out.println(start + " " + end);
    }

    private void showUnsafeZoneOnMap(HashMap<Double, Double> data) {

        MarkerOptions markerOptions = new MarkerOptions();
//        HashMap<String, String> googlePlace = nearbyPlaceList.get(i);
//
//        String placeName = googlePlace.get("place_name");
//        String vicinity = googlePlace.get("vicinity");
//        double lat = Double.parseDouble( googlePlace.get("lat"));
//        double lng = Double.parseDouble( googlePlace.get("lng"));
//        String icon = googlePlace.get("icon");

        LatLng latLng = new LatLng( latitude, longitude);
        markerOptions.position(latLng);
//        markerOptions.title(placeName + " : "+ vicinity);

//        Log.d("Fragment",icon);

//            if (icon == null || icon.isEmpty())
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            else {
//
//                try {
//                    URL url = new URL(icon);
//                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
        HashMap<Integer, String> hash_map = new HashMap<Integer, String>();
        mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

    }
}