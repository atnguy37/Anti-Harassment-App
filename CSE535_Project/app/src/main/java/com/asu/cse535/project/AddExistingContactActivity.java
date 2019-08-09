package com.asu.cse535.project;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mario padilla
 */
public class AddExistingContactActivity extends AppCompatActivity {
    FirebaseUser FireBaseSignInAccount;
    FirebaseFirestore firebaseDB;
    FirebaseAuth mAuth;
    String UserID;
    Button addContactsButton;
    Button searchContactButton;
    String name;
    String phoneNumber;
    TextView nameField;
    TextView phoneNumberField;
    Map<String, Object> contactsMap;
    Map<String, Object> UserStructureFB;
    DocumentReference userContactsDocRef;
    Intent contactIntent;
    public  static final int RequestPermissionCode  = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_existing_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addContactsButton = (Button) findViewById(R.id.addContactsButton);
        searchContactButton = (Button) findViewById(R.id.searchContact);

        nameField = (TextView) findViewById((R.id.name_field));
        phoneNumberField = (TextView) findViewById((R.id.phone_field));

        mAuth = FirebaseAuth.getInstance();
        FireBaseSignInAccount = mAuth.getCurrentUser();
        UserID = FireBaseSignInAccount.getUid();

        firebaseDB = FirebaseFirestore.getInstance();

       /* if(FireBaseSignInAccount != null) {
            text.setText("Name: " + FireBaseSignInAccount.getDisplayName() + " \nemail: " +FireBaseSignInAccount.getEmail());
        }*/

        EnableRuntimePermission();

        searchContactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactIntent, 7);


            }});


        addContactsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contactsMap = new HashMap<>();
                UserStructureFB = new HashMap<>();
                name = nameField.getText().toString();

                phoneNumber = phoneNumberField.getText().toString();
                String format_phone_number = phoneNumber.replaceAll("\\s+","");
                format_phone_number = format_phone_number.replace("(", "");
                format_phone_number = format_phone_number.replace(")", "");
                format_phone_number = format_phone_number.replace("-", "");

                if(!name.isEmpty()){
                    if(!phoneNumber.isEmpty()){
                        contactsMap.put(format_phone_number, name);
                        UserStructureFB.put("EmergencyContacts", contactsMap);
                        AddContact();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "phone empty", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "name empty", Toast.LENGTH_SHORT).show();
                }
            }});



    }

    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {

        super.onActivityResult(RequestCode, ResultCode, ResultIntent);

        switch (RequestCode) {

            case (7):
                if (ResultCode == Activity.RESULT_OK) {

                    Uri uri;
                    Cursor c1;
                    Cursor c2;
                    String ContactName;
                    String ContactNumber;
                    String ContactID;
                    String results;
                    int examResult;

                    uri = ResultIntent.getData();

                    c1 = getContentResolver().query(uri, null, null, null, null);

                    if (c1.moveToFirst()) {

                        ContactName = c1.getString(c1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        ContactID = c1.getString(c1.getColumnIndex(ContactsContract.Contacts._ID));
                        results = c1.getString(c1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        examResult = Integer.valueOf(results) ;

                        if (examResult == 1) {

                            c2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + ContactID, null, null);

                            while (c2.moveToNext()) {
                                ContactNumber = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                nameField.setText(ContactName);
                                phoneNumberField.setText(ContactNumber);
                            }
                        }
                    }
                }
                break;
        }
    }

    //Adds a new contact or updates the old one via the key (name)
    public void AddContact() {

        userContactsDocRef
                .set(UserStructureFB, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "New contanct added", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "contact initally failed", Toast.LENGTH_SHORT).show();

                    }
                });


    }
    @Override
    public void onStart() {
        super.onStart();

        userContactsDocRef = firebaseDB.collection("users").document(UserID);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }


    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(AddExistingContactActivity.this,
                Manifest.permission.READ_CONTACTS)){
            Toast.makeText(AddExistingContactActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        }
        else {
            ActivityCompat.requestPermissions(AddExistingContactActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RequestCode, String per[], int[] PResult) {
        switch (RequestCode) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddExistingContactActivity.this,"Permission Granted.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(AddExistingContactActivity.this,"Permission Canceled.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
