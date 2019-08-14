package com.asu.cse535.group_number_420;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.asu.cse535.group_number_420.Assignment2.Assignment2Fragment;
import com.asu.cse535.group_number_420.Final.FinalFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting the content view to activity_main.xml
        // comment
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

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FinalFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_final);

        }




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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return false;
    }

    //This is for the left side navigation menu and tells you what
    //happens when you click on an option.
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_final:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FinalFragment()).commit();
                break;
            case R.id.nav_assignment2:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Assignment2Fragment()).commit();
                break;
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}