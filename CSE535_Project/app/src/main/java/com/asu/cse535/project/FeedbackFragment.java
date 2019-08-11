package com.asu.cse535.project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mario padilla, efren lopez
 */
public class FeedbackFragment extends Fragment {
    public Button submit_form;

    User user;
    String LOG = "MainActivity";

    FirebaseUser FireBaseSignInAccount;
    FirebaseFirestore firebasedb;
    String UserID;
    TextView name_edittext;
    TextView description_edittext;
    Map<String, Object> FeedbackStructure;
    Map<String, Object> FeedbackMap;
    DocumentReference userContactsDocRef;
    RadioGroup radioGroup;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view  = inflater.inflate(R.layout.fragment_feedback, container, false);

        name_edittext = (TextView) view.findViewById(R.id.name_edittext);
        description_edittext = (TextView) view.findViewById(R.id.description_edittext);
        submit_form = (Button) view.findViewById(R.id.submit_form);

        FireBaseSignInAccount = ((MainActivity) getActivity()).getAreYouSignInAccount();

        if(FireBaseSignInAccount != null) {
            firebasedb = ((MainActivity) getActivity()).initFirestore();
            UserID = FireBaseSignInAccount.getUid();
            Toast.makeText(getActivity(), UserID, Toast.LENGTH_SHORT).show();

        }

        radioGroup = (RadioGroup) view.findViewById(R.id.radioButtongroup);


        submit_form.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                String name = name_edittext.getText().toString();
                String text_comment = description_edittext.getText().toString();

                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton ratings =  view.findViewById(selectedId);
                String rating = ratings.getText().toString();


                FeedbackMap = new HashMap<>();
                FeedbackStructure = new HashMap<>();

                userContactsDocRef = firebasedb.collection("users").document(UserID);
                Log.i(LOG,userContactsDocRef.toString());

                if(name != null){
                    if (text_comment != null){

                        FeedbackMap.put("Name", name);
                        FeedbackMap.put("Comment", text_comment);
                        FeedbackMap.put("Rating", rating);

                        FeedbackStructure.put("Feedback", FeedbackMap);
                        Log.i(LOG,FeedbackMap.toString());


                        userContactsDocRef
                                .set(FeedbackStructure, SetOptions.merge() )
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "Feedback added", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();

                                    }
                                });


                    }
                }


            }
        });




        return view;
    }





}
