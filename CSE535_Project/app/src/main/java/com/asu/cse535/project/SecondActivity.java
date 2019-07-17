package com.asu.cse535.project;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class SecondActivity extends MainActivity   {
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Intent intent = getIntent();
        //String value = intent.getStringExtra("key"); //if it's a string you stored.
    }

}
