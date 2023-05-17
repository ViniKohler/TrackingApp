package com.vkohler.gps;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Addresses extends Application {

    private static Addresses singleton;

    private static List<Location> myLocations;

    public static List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        Addresses.myLocations = myLocations;
    }

    public Addresses getInstance() {
        return singleton;
    }

    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
    }
}
