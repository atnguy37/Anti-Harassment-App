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

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view  = inflater.inflate(R.layout.fragment_home, container, false);

        TextView test = (TextView) view.findViewById(R.id.welcome);

        //Access mainActivity
        FirebaseUser AreYouSignInAccount = ((MainActivity) getActivity()).getAreYouSignInAccount();


        if(AreYouSignInAccount != null) {
            test.setText("Welcome " + AreYouSignInAccount.getDisplayName());


        }

        return view;
    }


}
