package com.asu.cse535.project;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
public class AddNewContactActivity extends AppCompatActivity {
    FirebaseUser FireBaseSignInAccount;
    FirebaseFirestore firebaseDB;
    FirebaseAuth mAuth;
    String UserID;
    Button addContactsButton;
    String name;
    String phoneNumber;
    EditText nameField;
    EditText phoneNumberField;
    Map<String, Object> contactsMap;
    Map<String, Object> UserStructureFB;
    DocumentReference userContactsDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView text = (TextView) findViewById(R.id.title_aanc);
        addContactsButton = (Button) findViewById(R.id.addContactsButton);

        nameField = (EditText) findViewById((R.id.name_field));
        phoneNumberField = (EditText) findViewById((R.id.phone_field));

        mAuth = FirebaseAuth.getInstance();
        FireBaseSignInAccount = mAuth.getCurrentUser();
        UserID = FireBaseSignInAccount.getUid();

        firebaseDB = FirebaseFirestore.getInstance();

       /* if(FireBaseSignInAccount != null) {
            text.setText("Name: " + FireBaseSignInAccount.getDisplayName() + " \nemail: " +FireBaseSignInAccount.getEmail());
        }*/

        addContactsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contactsMap = new HashMap<>();
                UserStructureFB = new HashMap<>();
                name = nameField.getText().toString();
                phoneNumber = phoneNumberField.getText().toString();

                if(!name.isEmpty()){
                    if(!phoneNumber.isEmpty()){
                        contactsMap.put(phoneNumber, name);
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

}
