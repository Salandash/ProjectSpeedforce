package com.georgewilliam.speedforce.projectspeedforce;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by georgetamate on 1/26/17.
 */
public class PolylinePath {

    private int interval;

    private int count;

    private ArrayList<Location> locationArrayList;

    public PolylinePath() {
        interval = 1000;
        count = interval;
    }

    public PolylinePath(int interval) {
        this.interval = interval;
        count = this.interval;
    }

    public void addLocation(Location location) {
        if (count < 0) {
            locationArrayList.add(location);
            count = interval;
        }
        count--;
    }

    public ArrayList<Location> getLocationArrayList() {
        return locationArrayList;
    }
}
