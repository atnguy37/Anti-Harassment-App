package com.asu.cse535.project;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class SendTextMessage {



    String LOG = "MainActivity";

    private static final String TAG = "";
    private User user;
    int RC_SIGN_IN = 0;
    private SignInButton signInButton;
    private com.google.android.gms.auth.api.signin.GoogleSignInClient GoogleSignInClient;
    private NavigationView navigationView;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private FirebaseUser AreYouSignInAccount;
    private FirebaseFirestore FBDB;
    FirebaseFirestore firebaseDB;
    String textMessage = "";

    DocumentReference userContactsDocRef;
    ArrayList<String> phone_keys;



    public void sendMessage(final Context context){

        try{
            firebaseDB = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            AreYouSignInAccount = mAuth.getCurrentUser();

            String UserID = AreYouSignInAccount.getUid();

            userContactsDocRef = firebaseDB.collection("users").document(UserID);

            userContactsDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        SmsManager smgr = SmsManager.getDefault();
                        user = documentSnapshot.toObject(User.class);

                        ArrayList<Object> temp_values = new ArrayList<>();
                        ArrayList<String> temp_keys = new ArrayList<>();
                        phone_keys = new ArrayList<>();

                        phone_keys = new ArrayList<String>(user.getEmergencyContacts().keySet()); //phones
                        temp_keys = new ArrayList<String>(user.getLocations().keySet());
                        temp_values = new ArrayList<Object>(user.getLocations().values());

                        GeoPoint geopoint = (GeoPoint) temp_values.get(1);
                        String lat = Double.toString(geopoint.getLatitude());
                        String longt = Double.toString(geopoint.getLongitude());
                        Log.i(LOG, "lat is: " + lat);
                        Log.i(LOG, "long is: " + longt);
                        Log.i(LOG, "number of messages: " + phone_keys.size());

                        String url = "http://maps.google.com?q=" + lat + "," +longt;
                        textMessage = "HELP! my location is: " + url;

                        Log.i(LOG, "1 number of messages: " + phone_keys.size());
                        for (int i = 0; i < phone_keys.size(); i++){
                            String phone_number = "+" + phone_keys.get(i);
                            smgr.sendTextMessage(phone_keys.get(i),null,textMessage,null,null);
                            Log.i(LOG, "message send to " + phone_number);
                        }

                        Toast.makeText(context.getApplicationContext(), "SMS Sent Successfully - " + phone_keys.size()  + " msg send", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(context.getApplicationContext(),"No document present", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context.getApplicationContext(),"FAILED", Toast.LENGTH_SHORT).show();
                }
            });


        }
        catch (Exception e){
            Log.i(LOG, "Message failed:  " + e.toString());
        }



    }
}
