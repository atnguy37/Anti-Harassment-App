package com.asu.cse535.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;

public class ContactFragment extends Fragment {



    FirebaseUser AreYouSignInAccount;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view  = inflater.inflate(R.layout.fragment_contact, container, false);
        TextView test = (TextView) view.findViewById(R.id.checker);
        AreYouSignInAccount = ((MainActivity) getActivity()).getAreYouSignInAccount();

        if(AreYouSignInAccount != null) {
            test.setText("Name: " + AreYouSignInAccount.getDisplayName() + " \nemail: " +AreYouSignInAccount.getEmail());
        }

        return  view;

    }





}
