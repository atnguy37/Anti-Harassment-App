package com.asu.cse535.project;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {
//    private User user;
    private GeoPoint geo_point;
    private @ServerTimestamp Date timestamp;
    private String user_id;

    public UserLocation(GeoPoint geo_point, Date timestamp) {
//        this.user = user;
        this.geo_point = geo_point;
        this.timestamp = timestamp;
    }

    public UserLocation() {

    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public void setUser_id(String id) {
        this.user_id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                ", geo_point=" + geo_point +
                ", timestamp=" + timestamp +
                "user= " + user_id + '}';
    }

    //get is more important

}

