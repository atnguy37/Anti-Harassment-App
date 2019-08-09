package com.asu.cse535.project;

import java.util.HashMap;

/**
 * @author mario padilla
 */
public class User {
    private HashMap<String, Object> EmergencyContacts;
    private HashMap<String, Object> Locations;

    public HashMap<String, Object> getLocations() {
        return Locations;
    }

    public void setLocations(HashMap<String, Object> locations) {
        this.Locations = locations;
    }

    public User(){

    }

    public HashMap<String, Object> getEmergencyContacts() {
        return EmergencyContacts;
    }


    public void setEmergencyContacts(HashMap<String, Object> emergencyContacts) {
        EmergencyContacts = emergencyContacts;
    }

    public User(HashMap<String, Object> EmergencyContacts){
        this.EmergencyContacts = EmergencyContacts;
    }
}
