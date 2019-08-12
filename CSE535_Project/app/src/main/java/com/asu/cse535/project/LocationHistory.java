package com.asu.cse535.project;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class LocationHistory {
    //    private User user;
    private HashMap<String, Object> Button;
    private HashMap<String, Object> Voice;
    private HashMap<String, Object> Shaking;


//    public UserLocation(GeoPoint geo_point, Date timestamp) {
////        this.user = user;
//        this.geo_point = geo_point;
//        this.timestamp = timestamp;
//    }

    public LocationHistory() {

    }

    public HashMap<String, Object> getButton() {
        return Button;
    }


    public void setButton(HashMap<String, Object> button) {
        Button = button;
    }



    public HashMap<String, Object> getVoice() {
        return Voice;
    }


    public void setVoice(HashMap<String, Object> voice) {
        Voice = voice;
    }

    public HashMap<String, Object> getShaking() {
        return Shaking;
    }


    public void setShaking(HashMap<String, Object> shaking) {
        Shaking = shaking;
    }



}

