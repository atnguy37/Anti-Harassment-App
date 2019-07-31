package com.asu.cse535.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mario padilla
 */
public class MyContactFragment extends Fragment implements UserAdapter.OnUserListener {

    View view;
    FirebaseUser FireBaseSignInAccount;
    FirebaseFirestore firebasedb;
    String UserID;
    private UserAdapter adapter;
    private CollectionReference contactRef;
    DocumentReference test;
    RecyclerView recyclerView;
    public User user;
    FloatingActionButton addContact;
    UserAdapter.OnUserListener mOnNoteListener;
    FloatingActionButton addNewContact;
    FloatingActionButton addExistingContact;
    boolean isOpenMenuButtonOpen = false;
    TextView fab1_text;
    TextView fab2_text;
    View overlayView;
    ArrayList<String> keys;
    ArrayList<Object> values;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view  = inflater.inflate(R.layout.fragment_my_contact, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_contact_list);
        addContact = view.findViewById(R.id.add_contacs);
        mOnNoteListener = this;
        FireBaseSignInAccount = ((MainActivity) getActivity()).getAreYouSignInAccount();
        firebasedb = ((MainActivity) getActivity()).initFirestore();
        UserID = FireBaseSignInAccount.getUid();

        overlayView = view.findViewById(R.id.overlayView);
        fab1_text = view.findViewById(R.id.fab1_text);
        fab2_text = view.findViewById(R.id.fab2_text);
        FloatingActionButton openMenuButton = (FloatingActionButton) view.findViewById(R.id.add_contacs);
        addNewContact = (FloatingActionButton) view.findViewById(R.id.fab1);
        addExistingContact = (FloatingActionButton) view.findViewById(R.id.fab2);

        openMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOpenMenuButtonOpen){
                    showButtonMenu();
                }else{
                    closeButtonMenu();
                }
            }
        });

        addNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeButtonMenu();
                Intent AddNewContact = new Intent(getActivity(), AddNewContactActivity.class);
                startActivity(AddNewContact);
            }
        });

        addExistingContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeButtonMenu();
                Intent AddExistingContact = new Intent(getActivity(), AddExistingContactActivity.class);
                startActivity(AddExistingContact);
            }
        });

        contactRef = firebasedb.collection("users");
        test = firebasedb.collection("users").document(UserID);
        getReadData();

        return  view;

    }

    private void showButtonMenu(){
        overlayView.setVisibility((View.VISIBLE));
        isOpenMenuButtonOpen=true;

        fab1_text.setVisibility(View.VISIBLE);
        fab2_text.setVisibility(View.VISIBLE);
        fab1_text.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
        fab2_text.animate().translationY(-getResources().getDimension(R.dimen.standard_110));

        addNewContact.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        addExistingContact.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
    }

    private void closeButtonMenu(){
        overlayView.setVisibility((View.GONE));
        isOpenMenuButtonOpen=false;
        addNewContact.animate().translationY(0);
        addExistingContact.animate().translationY(0);
        fab1_text.animate().translationY(0);
        fab2_text.animate().translationY(0);

        fab1_text.setVisibility(View.GONE);
        fab2_text.setVisibility(View.GONE);

    }
    public void onStart() {

        super.onStart();
        getReadData();
    }


    public User getReadData(){
        user = new User();

        DocumentReference docReference = firebasedb.collection("users").document(UserID);

        docReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class);
                    Toast.makeText(getActivity(),"document found", Toast.LENGTH_SHORT).show();

                    values = new ArrayList<>();
                    keys = new ArrayList<>();

                    keys = new ArrayList<String>(user.getEmergencyContacts().keySet());
                    values = new ArrayList<Object>(user.getEmergencyContacts().values());

                    adapter = new UserAdapter(keys, values, mOnNoteListener);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(),"No document present", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"FAILED", Toast.LENGTH_SHORT).show();
            }
        });

        return user;
    }

    @Override
    public void onResume(){
        super.onResume();
        // getData();
//        adapter.startListening();

    }

    @Override
    public void onUserClick(int position) {
        Toast.makeText(getActivity(),String.valueOf(position), Toast.LENGTH_SHORT).show();

    }

    //DOESN"T WORK YET
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            //ArrayList<String> keys = adapter.keys;
            //ArrayList<Object> values = adapter.values;

            //
            keys.remove(viewHolder.getAdapterPosition());
            values.remove(viewHolder.getAdapterPosition());

            Map<String, Object> contactsMap = new HashMap<>();
            Map<String, Object> UserStructureFB= new HashMap<>();

            for (int j=0; j < keys.size(); j++) {
                contactsMap.put(keys.get(j), values.get(j));

            }
            UserStructureFB.put("EmergencyContacts", contactsMap);

            DocumentReference docReference = firebasedb.collection("users").document(UserID);


            //deleting old one
            Map<String,Object> updates = new HashMap<>();
            updates.put("EmergencyContacts", FieldValue.delete());

            docReference.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                // [START_EXCLUDE]
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getActivity(),"deleted", Toast.LENGTH_SHORT).show();

                }
                // [START_EXCLUDE]
            });

            docReference
                    .set(UserStructureFB, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Readded", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed 2", Toast.LENGTH_SHORT).show();

                        }
                    });
            adapter.notifyDataSetChanged();

        }
    };



    /*@Override
    public void onStart(){
        super.onStart();
//        adapter.startListening();

    }



    @Override
    public void onStop(){
        super.onStop();
       // adapter.stopListening();

    }*/
}

