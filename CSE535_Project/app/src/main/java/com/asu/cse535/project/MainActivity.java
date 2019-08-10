package com.asu.cse535.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.asu.cse535.project.maps.MapFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.asu.cse535.project.Constant.ERROR_DIALOG_REQUEST;
import static com.asu.cse535.project.Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.asu.cse535.project.Constant.PERMISSIONS_REQUEST_ENABLE_GPS;

/**
 * @author mariogp18, anh nguyen
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageButton btnShare;
    Intent shareIntent;
    String shareBody = "Share";
    String LOG = "MainActivity";

    private static final String TAG = "";
    private User user;
    int RC_SIGN_IN = 0;
    private SignInButton signInButton;
    private GoogleSignInClient GoogleSignInClient;
    private NavigationView navigationView;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private FirebaseUser AreYouSignInAccount;
    private  FirebaseFirestore FBDB;
    FirebaseFirestore firebaseDB;
    String textMessage = "";

    Button alert;
    DocumentReference userContactsDocRef;
    ArrayList<String> phone_keys;


    public boolean mLocationPermissionGranted = false;

    // Shaking Alarm
    private SensorManager sm;
    private float acelVal; // current acceleration including gravity
    private float acelLast; // last acceleration including gravity
    private float shake; // acceleration apart from gravity
    private boolean shaking = false;
    public boolean wait = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Shaking Alarm
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        shake = 0.00f;
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;



        //Setting the content view to activity_main.xml
        // comment
        setContentView(R.layout.activity_main);


        //Creating a Toolbar of @+id/toolbar in app_bar_main.xml
        //and setting the actionBar to this.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creating a DrawerLayout of "@+id/drawer_layout" in acitivity_main.xml
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        //Creating a NavigationView of "@+id/nav_view" in acitivity_main.xml
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Connects the drawerLayout and the actionbar
        ActionBarDrawerToggle actionBarToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_open_drawer, R.string.navigation_close_drawer);
        drawer.addDrawerListener(actionBarToggle);
        actionBarToggle.syncState();

        mAuth = FirebaseAuth.getInstance();
        FBDB = initFirestore();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);

        }

        addIDDocument();


    }

    public void addIDDocument(){
        firebaseDB = initFirestore();
        AreYouSignInAccount = getAreYouSignInAccount();

        if (AreYouSignInAccount != null){
            String UserID = AreYouSignInAccount.getUid();

            userContactsDocRef = firebaseDB.collection("users").document(UserID);

            Map<String, Object> id = new HashMap<>();
            id.put("id", UserID);


            userContactsDocRef
                    .set(id, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "id added", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "failed id", Toast.LENGTH_SHORT).show();

                        }
                    });


            user = new User();

            userContactsDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        if(user.getEmergencyContacts() == null){
                            Map<String, Object> contactsMap = new HashMap<>();
                            //contactsMap.put(name, phoneNumber);

                            Map<String, Object> emergencyContacts = new HashMap<>();
                            emergencyContacts.put("EmergencyContacts", contactsMap);

                            userContactsDocRef
                                    .set(emergencyContacts, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "id added", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "failed id", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }

                        if(user.getLocations() == null){
                            Map<String, Object> subLocations = new HashMap<>();

                            UserLocation temp_UL = new UserLocation();

                            Map<String, Object> Locations = new HashMap<>();
                            Locations.put("Locations", subLocations);

                            userContactsDocRef
                                    .set(Locations, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //  Toast.makeText(getApplicationContext(), "id added", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(getApplicationContext(), "failed id", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }


                    } else {
                        //User Exists
                        Toast.makeText(getApplicationContext(), "EC structure Exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }



    }
    public FirebaseFirestore initFirestore() {
        FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
        return firebaseDB;
    }


    public FirebaseUser getAreYouSignInAccount(){
        FirebaseUser signInAccount = mAuth.getCurrentUser();

        return signInAccount;
    }

   @Override
    protected void onStart(){

       mAuth = FirebaseAuth.getInstance();
       //AreYouSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
       AreYouSignInAccount = mAuth.getCurrentUser();

       if(AreYouSignInAccount != null) {
           View headerView = navigationView.getHeaderView(0);
           TextView nav_header_title = (TextView)headerView.findViewById(R.id.nav_header_titles);
           TextView nav_header_subtitle = (TextView)headerView.findViewById(R.id.nav_header_subtitles);
           ImageView nav_imageView = (ImageView)headerView.findViewById(R.id.nav_imageView);

           String personName = AreYouSignInAccount.getDisplayName();
           String personEmail = AreYouSignInAccount.getEmail();
           Uri personImage = AreYouSignInAccount.getPhotoUrl();

           nav_header_title.setText(personName);
           if(personEmail != null){
               nav_header_subtitle.setText(personEmail);
           }
           if(personImage != null){
               Glide.with(this).load(personImage).fitCenter().apply(new RequestOptions().override(108, 108)).placeholder(R.drawable.ic_launcher_foreground).into(nav_imageView);
           }


           navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(true);
           navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
           navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(false);
           navigationView.getMenu().findItem(R.id.nav_my_contacts).setVisible(true);
           navigationView.getMenu().findItem(R.id.nav_map).setVisible(true);

           navigationView.getMenu().findItem(R.id.nav_share).setVisible(true);

       }
       else{
           navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(true);
           navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
           navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(false);
           navigationView.getMenu().findItem(R.id.nav_my_contacts).setVisible(false);
           navigationView.getMenu().findItem(R.id.nav_map).setVisible(false);
           navigationView.getMenu().findItem(R.id.nav_share).setVisible(false);

       }
       super.onStart();
   }



    //Closes the left side navigation
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Creates the optional menu (three dots that contains settings)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(mLocationPermissionGranted){
                //getChatrooms();
                Log.d(TAG, "onActivityResult: get Location Permission.");
            }
            else{
                getLocationPermission();
            }
        }
    }


    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //getChatrooms();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            System.out.println("isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    // This is for the optional menu and handles what happens when you click on the
    // options like settings.
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This is for the left side navigation menu and tells you what
    //happens when you click on an option.
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_my_contacts:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyContactFragment()).commit();
                break;
            case R.id.nav_log_in:

                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getResources().getString(R.string.client_id))
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                GoogleSignInClient = GoogleSignIn.getClient(this, gso);

                signInGoogle();
                break;
            case R.id.nav_log_out:

                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getResources().getString(R.string.client_id))
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                GoogleSignInClient = GoogleSignIn.getClient(this, gso);

                //Sign out of Firebase
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                //Sign out of Google account
                GoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent=new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                break;

            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                break;

            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();

                shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "CSE535_Project");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent,"Share"));


                break;
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*Google Signin Authentication Portion*/

    private void signInGoogle() {
        Intent intentSignIn = GoogleSignInClient.getSignInIntent();
        //calls the onActivityResult function
        startActivityForResult(intentSignIn, RC_SIGN_IN);
    }


    //After the user is signed in, you can access the user's info by getting a GoogleSignInAccount object
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> SignInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount GoogleAccount = SignInTask.getResult(ApiException.class);
                //GoogleAccount.getName()....
                //String token = GoogleAccount.getIdToken();
                //Send info Firebase
                firebaseSignInWithGoogleFunction(GoogleAccount);

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                firebaseSignInWithGoogleFunction(null);
            }
        }
    }

    //Refresh the main activity (calls the OnStart())
    private void firebaseSignInWithGoogleFunction(GoogleSignInAccount GoogleAccount){

        AuthCredential credential = GoogleAuthProvider.getCredential(GoogleAccount.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            refreshMainActivitySignIn(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            refreshMainActivitySignIn(null);
                        }
                    }
                });
    }

    //Refresh the main activity (calls the OnStart())
    private void refreshMainActivitySignIn(FirebaseUser user){
        Intent signInIntent = new Intent(this, MainActivity.class);
        this.startActivity(signInIntent);
    }


    private final SensorEventListener sensorListener = new SensorEventListener()

    {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                index++;
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                acelLast = acelVal;
                acelVal = (float) Math.sqrt((double) (x * x + y * y + z * z));

                float delta = acelVal - acelLast;
                shake = shake * 0.9f + delta; // perform low-cut filter
                //System.out.println("Shake: " + shake);
                if ((shake > 1 || shake < -1)) {
//                    Toast toast = Toast.makeText(getApplicationContext(), "DO NOT SHAKE ME", Toast.LENGTH_LONG);
//                    toast.show();

//                    System.out.println("Shake In: " + shaking);
//                    System.out.println("Wait In: " + wait);
                    shaking = true;
                    if (!wait) {
                        wait = true;
                        //makeSound();
                       // SendTextMessage stm = new SendTextMessage();
                        //stm.sendMessage(MainActivity.this, false);

                        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        FragmentManager fm = getSupportFragmentManager();

                        fragment.ClickFunction();

                    }

//                System.out.println("DO NOT SHAKE ME");
                } else
                    shaking = false;
            }
//            try {
//                TimeUnit.MILLISECONDS.sleep(200);
//            }
//            catch (Exception e) {
//            e.printStackTrace();
//            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor,int accuracy) {
        }
    };


    //Not using it :(
    private void makeSound () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Thread.sleep(1000);
//                    System.out.println("Shake: " + shaking);
//                    System.out.println("Wait: " + wait);
                    // we add 100 new entries
                    if (wait && shaking) {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                        Thread.sleep(10000);
                        wait = false;
                        r.stop();

                    }
                    wait = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

