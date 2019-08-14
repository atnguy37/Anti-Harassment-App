package com.asu.cse535.group_number_420.Final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.asu.cse535.group_number_420.R;

public class FinalFragment extends Fragment {

    Button collectCop;
    Button collectHungry;
    Button collectHeadache;
    Button collectAbout;
    Button predictCop;
    Button predictHungry;
    Button predictHeadache;
    Button predictAbout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view  = inflater.inflate(R.layout.fragment_final, container, false);
        getActivity().setTitle("CSE535 FINAL");

        collectCop = (Button) view.findViewById(R.id.collect_cop_data);
        collectHungry = (Button) view.findViewById(R.id.collect_hungry_data);
        collectHeadache = (Button) view.findViewById(R.id.collect_headache_data);
        collectAbout = (Button) view.findViewById(R.id.collect_about_data);
        predictCop = (Button) view.findViewById(R.id.predict_cop_data);
        predictHungry = (Button) view.findViewById(R.id.predict_hungry_data);
        predictHeadache = (Button) view.findViewById(R.id.predict_headache_data);
        predictAbout = (Button) view.findViewById(R.id.predict_about_data);


        collectCop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(),"Collect cop", Toast.LENGTH_LONG).show();

            }
        });


        collectHungry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Collect hungry", Toast.LENGTH_LONG).show();

            }
        });


        collectHeadache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Collect headache", Toast.LENGTH_LONG).show();

            }
        });


        collectAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(),"Collect about", Toast.LENGTH_LONG).show();

            }
        });



        predictCop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(),"Predict Cop", Toast.LENGTH_LONG).show();

            }
        });


        predictHungry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Predict Hungry", Toast.LENGTH_LONG).show();

            }
        });


        predictHeadache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Predict Headache", Toast.LENGTH_LONG).show();

            }
        });


        predictAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(),"Predict About", Toast.LENGTH_LONG).show();

            }
        });
        return view;
    }
}
