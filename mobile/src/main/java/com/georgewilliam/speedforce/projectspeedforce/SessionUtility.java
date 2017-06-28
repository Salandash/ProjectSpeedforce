package com.georgewilliam.speedforce.projectspeedforce;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by georgetamate on 5/30/17.
 */
public class SessionUtility {

    public static final double MAX_HAUSDORFF_DISTANCE = 10;
    public static final double MIN_MILESTONE_DISTANCE = 20;
    public static final int MAX_SESSIONS_TO_EVALUATE = 3;

    private static double distanceFromClosest(JSONObject coordinates, JSONArray route) {
        double shortest = MAX_HAUSDORFF_DISTANCE + 1;
        double distance;
        Location baseLocation = new Location("");
        try {
            baseLocation.setLatitude(coordinates.getDouble("lat"));
            baseLocation.setLongitude(coordinates.getDouble("lng"));
            for (int i = 0; i < route.length(); i++) {
                Location location = new Location("");
                if (route.getJSONObject(i).getBoolean("isMilestone")) {
                    location.setLatitude(route.getJSONObject(i).getDouble("lat"));
                    location.setLongitude(route.getJSONObject(i).getDouble("lng"));
                    distance = baseLocation.distanceTo(location);
                    if (distance < shortest) {
                        shortest = distance;
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return shortest;
    }

    public static boolean isHausdorffValid(JSONArray baseRoute, JSONArray providedRoute) {
        double distance;
//        for (int i = 0; i < baseRoute.length(); i++) {
//            try {
//                JSONObject coordinates = baseRoute.getJSONObject(i);
//                if (coordinates.getBoolean("isMilestone")) {
//                    distance = distanceFromClosest(coordinates, providedRoute);
//                    if (distance > MAX_HAUSDORFF_DISTANCE) {
//                        return false;
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        for (int i = 0; i < baseRoute.length(); i++) {
            try {
                JSONObject coordinates = baseRoute.getJSONObject(i);
                if (coordinates.getBoolean("isMilestone")) {
                    if (!isAnyValidDistance(coordinates, providedRoute)) {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static boolean isAnyValidDistance(JSONObject coordinates, JSONArray route) {
        double distance;
        Location baseLocation = new Location("");
        try {
            baseLocation.setLatitude(coordinates.getDouble("lat"));
            baseLocation.setLongitude(coordinates.getDouble("lng"));
            for (int i = 0; i < route.length(); i++) {
                Location location = new Location("");
                if (route.getJSONObject(i).getBoolean("isMilestone")) {
                    location.setLatitude(route.getJSONObject(i).getDouble("lat"));
                    location.setLongitude(route.getJSONObject(i).getDouble("lng"));
                    distance = baseLocation.distanceTo(location);
                    if (distance <= MAX_HAUSDORFF_DISTANCE) {
                        return true;
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Coordinates> ParseRoute(JSONArray route) {
        ArrayList<Coordinates> list = new ArrayList<Coordinates>();
        for(int i = 0; i < route.length(); i++) {
            Coordinates coordinates = new Coordinates();
            try {
                JSONObject coordinatesJSON = route.getJSONObject(i);
                coordinates.location.setLatitude(coordinatesJSON.getDouble("lat"));
                coordinates.location.setLongitude(coordinatesJSON.getDouble("lng"));
                coordinates.isMilestone = coordinatesJSON.getBoolean("isMilestone");
                list.add(coordinates);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private class Coordinates {

        public Location location;
        public boolean isMilestone;

        public Coordinates() {
            location = new Location("");
            isMilestone = false;
        }

        public Coordinates(double latitude, double longitude) {
            location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            isMilestone = false;
        }

        public Coordinates(double latitude, double longitude, boolean milestone) {
            location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            isMilestone = milestone;
        }
    }
}
