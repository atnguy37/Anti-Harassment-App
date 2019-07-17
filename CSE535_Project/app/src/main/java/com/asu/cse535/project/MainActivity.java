package com.asu.cse535.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "";
    int RC_SIGN_IN = 0;
    private SignInButton signInButton;
    private GoogleSignInClient GoogleSignInClient;
    private NavigationView navigationView;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private FirebaseUser AreYouSignInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting the content view to activity_main.xml
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

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);

        }
    }


    public FirebaseUser getAreYouSignInAccount(){
        AreYouSignInAccount = mAuth.getCurrentUser();

        return AreYouSignInAccount;
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
           navigationView.getMenu().findItem(R.id.nav_contacts).setVisible(true);
           navigationView.getMenu().findItem(R.id.nav_share).setVisible(true);

       }
       else{
           navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(true);
           navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
           navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(false);
           navigationView.getMenu().findItem(R.id.nav_contacts).setVisible(false);
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
                //Intent ContactsIntent = new Intent(this, SecondActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                //this.startActivity(ContactsIntent);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_contacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactFragment()).commit();
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
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();

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

}

