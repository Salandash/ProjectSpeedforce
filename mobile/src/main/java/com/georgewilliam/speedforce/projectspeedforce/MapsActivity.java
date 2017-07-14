/**
 * @file MapsActivity.java
 * @brief Fuente de la clase MapsActivity
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Clase que representa el Activity y Fragment del mapa de la aplicación móvil.
 * El Fragment de este Activity se encuentra definido en su respectivo archivo de Layout.
 */
public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    //private DatabaseHelper dbHelper;

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String START_WEAR_ACTIVITY = "/start_activity";
    private static final String END_WEAR_ACTIVITY = "/end_activity";
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    // A request object to store parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;
    private Location mPrevMilestone;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Anadido adicionalmente
    private static final int CAPTURE_INTERVAL = 1;
    private int count = 0;
    private boolean isInSession = false;
    private ArrayList<Location> route = new ArrayList<Location>();
    private ArrayList<Boolean> milestones = new ArrayList<Boolean>();
    private Chronometer chronometer;
    private Button startButton;
    private Button yesButton;
    private Button noButton;
    private TextView intendedRouteQuestionTextView;

    // for API calls
    private ProgressBar progressBar;

    // to convert to session json
    private JSONObject sessionJSON;
    private String sessionID; //uuid
    private String userID = "PlaceHolderUser";
    private String climateCondition = "Desconocido";
    private double averageBPM = -1;
    private String routeID; //uuid
    private String routeName = "NO NAME";
    private JSONArray coordinates;
    private String city = "Santo Domingo";
    private String country = "República Dominicana";
    private String startTime;
    private String endTime;
    private double distance;
    private double burntCalories = -1;
    private double relativeHumidity;
    private double temperature = -1000;
    private String trainingType;
    private String sessionStatus = "Local";

    // to calc calories
    private String gender;
    private double weight;
    private int age;
    private double minutesElapsed = 0;
    private long timeElapsed = 0;
    private double distanceKM = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("Username");
        scheduleAlarm();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        //dbHelper = new DatabaseHelper(this, null, null, 1);
        populateUserData();

        chronometer = (Chronometer) findViewById(R.id.chronometer_id);
        chronometer.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_id);
        progressBar.setVisibility(View.GONE);
        startButton = (Button) findViewById(R.id.start_button_id);
        yesButton = (Button) findViewById(R.id.yes_button_id);
        yesButton.setVisibility(View.GONE);
        noButton = (Button) findViewById(R.id.no_button_id);
        noButton.setVisibility(View.GONE);
        intendedRouteQuestionTextView = (TextView) findViewById(R.id.intended_route_question_textview_id);
        intendedRouteQuestionTextView.setVisibility(View.GONE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInSession) {
                    endSession();
                } else {
                    displayTrainingTypeSelectDialog(); // also starts the session
                    //startSession();
                }
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                yesClick();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                noClick();
            }
        });
    }

    private void populateUserData() {
        JSONObject json = DatabaseHelper.getInstance(this).getUser(userID);
        Log.d("Loaded User Data", json.toString());
        String birthDate = "";
        try {
            weight = json.getDouble("Weight");
            gender = json.getString("Sex");
            birthDate = json.getString("BirthDate");
            Log.d("Loaded BirthDate", birthDate);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "MapsActivity.populateUserData");
        }

//        String[] date = birthDate.split("/", 3);
//
//        Calendar dob = Calendar.getInstance();
//        Calendar today = Calendar.getInstance();
//
//        dob.set(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]));
//
//        age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
//
//        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
//            age--;
//        }

        age = SessionUtility.getAgeFromDateString(birthDate);
        Log.d("User Age", "AGE: " + Integer.toString(age));
    }

    /**
     * Get the device location and nearby places when the activity is restored after a pause.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
        updateMarkers();
    }

    /**
     * Stop location updates when the activity is no longer in focus, to reduce battery consumption.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Gets the device's current location and builds the map
     * when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Handles the callback when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        if (isInSession) {
            if (count <= 0) {
                count = CAPTURE_INTERVAL;
                if(milestones.isEmpty() || (mPrevMilestone.distanceTo(location) >= SessionUtility.MIN_MILESTONE_DISTANCE)) {
                    milestones.add(true);
                    mPrevMilestone = location;
                } else {
                    milestones.add(false);
                }
                route.add(location);
                Log.d("location capture", Double.toString(location.getLatitude()) +
                        ", " + Double.toString(location.getLongitude()));
            }
            count--;
            Log.d("location capture", "COUNT: " + Integer.toString(count));
            distance += mCurrentLocation.distanceTo(location);
        }

        mCurrentLocation = location;
        updateMarkers();
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Add markers for nearby places.
        updateMarkers();

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });
        /*
         * Set the map's camera position to the current location of the device.
         * If the previous state was saved, set the position to the saved state.
         * If the current location is unknown, use a default position and zoom value.
         */
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Builds a GoogleApiClient.
     * Uses the addApi() method to request the Google Places API and the Fused Location Provider.
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApiIfAvailable(Wearable.API)
                .build();
        createLocationRequest();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.nav_main_map_id:
                break;
            case R.id.nav_training_id:
                intent = new Intent(this, TrainingListActivity.class);
                intent.putExtra("Username", userID);
                startActivity(intent);
                break;
            case R.id.nav_history_id:
                intent = new Intent(this, HistoryActivity.class);
                intent.putExtra("Username", userID);
                startActivity(intent);
                break;
            case R.id.nav_logout_id:
                cancelAlarm();
                finish();
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }

    /**
     * Sets up the location request.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        /*
         * Sets the desired interval for active location updates. This interval is
         * inexact. You may not receive updates at all if no location sources are available, or
         * you may receive them slower than requested. You may also receive updates faster than
         * requested if other applications are requesting location at a faster interval.
         */
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        /*
         * Sets the fastest rate for active location updates. This interval is exact, and your
         * application will never receive updates faster than this value.
         */
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Gets the current location of the device and starts the location update notifications.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * Also request regular updates about the device location.
         */
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Adds markers for places nearby the device and turns the My Location feature on or off,
     * provided location permission has been granted.
     */
    private void updateMarkers() {
        if (mMap == null) {
            return;
        }

        if (route.size() > 0) {
            LatLng latlng = new LatLng(route.get(0).getLatitude(), route.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title("INICIO")
                    .snippet(latlng.toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }

        if ( route.size() >= 2 )
        {
            PolylineOptions options = new PolylineOptions();
            options.color( Color.parseColor( "#2196F3" ) );
            options.width( 10 );
            options.visible( true );
            for ( Location location : route )
            {
                options.add( new LatLng( location.getLatitude(),
                        location.getLongitude() ) );
            }
            mMap.addPolyline( options );
        }

        // asking intended route
        if (providedRouteIndex >= 0 && providedRouteIndex < providedRouteList.size()) {
            if (providedRouteList.get(providedRouteIndex).size() >= 2) {
                PolylineOptions options = new PolylineOptions();
                options.color( Color.parseColor( "#CCDDBB00" ) );
                options.width( 15 );
                options.visible( true );
                for ( Location location : providedRouteList.get(providedRouteIndex) )
                {
                    options.add( new LatLng( location.getLatitude(),
                            location.getLongitude() ) );
                }
                mMap.addPolyline( options );
            }
        }

    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }

    private void sendMessageToWear( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                Log.d("MESSAGE", "Getting Nodes");
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for(Node node : nodes.getNodes()) {
                    Log.d("MESSAGE", node + ": sending Msg");
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(), path, text.getBytes(Charset.forName("UTF-8")) ).await();
                    Log.d("MESSAGE", node + ": Msg sent");
                    if (!result.getStatus().isSuccess()) {
                        Log.d("MESSAGE", node + ": Msg send success!");
                    }
                }

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        // extra action
                    }
                });
            }
        }).start();
    }

    private void startSession() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startButton.setEnabled(false);
        sendMessageToWear(START_WEAR_ACTIVITY, "Session Started");
        sessionID = UUID.randomUUID().toString();
        distance = 0;
        sessionStatus = "Local";
        fetchCityAndCountry();
        isInSession = true;

        startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        Log.d("time capture", "Start Time: " + startTime);

        // Calling Weather API
        new FetchWeatherTask().execute();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        //chronometer.setVisibility(View.VISIBLE);
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDangerRed));
        startButton.setText(getResources().getString(R.string.maps_button_stop_string));
        startButton.setEnabled(true);
    }

    private void endSession() {
        startButton.setEnabled(false);
        sendMessageToWear(END_WEAR_ACTIVITY, "Session Ended");
        isInSession = false;
        routeID = UUID.randomUUID().toString();

        endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        //Log.d("time capture", "Finish Time: " + endTime);

        distanceKM = (double)Math.round(distance / 100) / 10.0; // m -> km
        timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
        minutesElapsed = ((double)timeElapsed) / 60000;
        Log.d("Time", "Minutes Elapsed: " + Double.toString(minutesElapsed));
        chronometer.stop();
        chronometer.setVisibility(View.GONE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }

        //sessionJSON = getSessionJSON();
        //Log.d("JSON", "Session in JSON: " + sessionJSON.toString());

        new PostRouteParamsTask().execute();
        //new PostResultsTask().execute();
        coordinates = getCoordinatesJSON();
        setAverageBPM(); // also sets calories
        count = 0;

//        route.clear();
//        milestones.clear();
//        mMap.clear();
//        updateMarkers();
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSuccessBlue));
        startButton.setText(getResources().getString(R.string.maps_button_start_string));
        startButton.setEnabled(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void fetchCityAndCountry() {
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
            if (addresses.size() > 0) {
                city = addresses.get(0).getLocality();
                country = addresses.get(0).getCountryName();
                if (country.equals("Dominican Republic")) {
                    country = "República Dominicana";
                }
                Log.d("address capture", "City: " + city + ", Country: " + country);
            }
        }
        catch (IOException e) {
            Log.e("Address ERROR", "Address retrieve failed");
            e.printStackTrace();
        }
    }

    private JSONObject getSessionJSON() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("SessionID", sessionID);
            jsonObj.put("UserID", userID);
            jsonObj.put("ClimateConditionID", climateCondition);
            jsonObj.put("AverageBPM", averageBPM);
            jsonObj.put("RouteID", routeID);
            jsonObj.put("RouteName", routeName);
            jsonObj.put("Coordinates", coordinates);
            jsonObj.put("CityName", city);
            jsonObj.put("CountryName", country);
            jsonObj.put("StartTime", startTime);
            jsonObj.put("EndTime", endTime);
            jsonObj.put("Distance", distanceKM);
            jsonObj.put("BurntCalories", burntCalories);
            jsonObj.put("RelativeHumidity", relativeHumidity);
            jsonObj.put("Temperature", temperature);
            jsonObj.put("TrainingTypeID", trainingType);
            jsonObj.put("SessionStatusID", sessionStatus);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "MapsActivity.getSessionJSON");
        }
        return jsonObj;
    }

    private JSONArray getCoordinatesJSON()  {
        JSONArray jsonLocationArray = new JSONArray();
        int index = 0;
        for (Location location : route) {
            JSONObject jsonLocation = new JSONObject();
            try {
                jsonLocation.put("lat", location.getLatitude());
                jsonLocation.put("lng", location.getLongitude());
                jsonLocation.put("isMilestone", milestones.get(index));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONException", "MapsActivity.getCoordinatesJSON");
            }
            jsonLocationArray.put(jsonLocation);
            index++;
        }
        return jsonLocationArray;
    }

    private JSONObject getParamsJSON() {
        JSONObject jsonParams = new JSONObject();
        try {
            JSONObject jsonLocation = new JSONObject();
            jsonLocation.put("lat", route.get(0).getLatitude());
            jsonLocation.put("lng", route.get(0).getLongitude());
            jsonLocation.put("isMilestone", milestones.get(0));
            jsonParams.put("StartPoint", jsonLocation);
            jsonLocation = new JSONObject();
            jsonLocation.put("lat", route.get(route.size() - 1).getLatitude());
            jsonLocation.put("lng", route.get(route.size() - 1).getLongitude());
            jsonLocation.put("isMilestone", milestones.get(milestones.size() - 1));
            jsonParams.put("EndPoint", jsonLocation);
            jsonParams.put("Distance", distanceKM);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "MapsActivity.getParamsJSON");
        }
        return jsonParams;
    }

    private void displayTrainingTypeSelectDialog() {
        final String[] items = getResources().getStringArray(R.array.maps_dialog_trainingtype_items_stringarray);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.maps_dialog_trainingtype_title_string)
                .setItems(R.array.maps_dialog_trainingtype_items_stringarray, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        trainingType = items[i];
                        dialogInterface.dismiss();
                        Log.d("Dialog Select", "TRAINING TYPE: " + trainingType);
                        toastMessage("Seleccionó: " + trainingType);

                        startSession();
                    }
                });
        dialog.show();
    }

    private void setRouteName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.maps_dialog_routename_title_string);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        // input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.maps_button_accept_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                routeName = input.getText().toString();
                sessionJSON = getSessionJSON();
                endPostPrep();
            }
        });
//        builder.setNegativeButton(R.string.maps_button_cancel_string, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
        builder.show();
    }

    private void goToResults(boolean success, String msg) {
        if (success) {
            sessionStatus = "Sincronizada";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else {
            sessionStatus = "Local";
            Toast.makeText(this,
                    "No se pudo conectar con servicio.", //+ Double.toString(((SpeedforceApplication) this.getApplication()).getAverageBPM()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        DatabaseHelper.getInstance(this).insertSession(getSessionJSON());

        Intent intent = new Intent(this, ResultsActivity.class);

        intent.putExtra("TrainingTypeID", trainingType);
        intent.putExtra("Distance", distanceKM);
        intent.putExtra("TimeElapsed", timeElapsed);
        intent.putExtra("AverageBPM", averageBPM);
        intent.putExtra("BurntCalories", burntCalories);
        intent.putExtra("ClimateConditionID", climateCondition);
        intent.putExtra("Temperature", temperature);

        startActivity(intent);
    }

    public void setAverageBPM() {
        new Thread( new Runnable() {
            @Override
            public void run() {
                boolean successBPM = false;
                Date stamp = new Date();
                while((new Date()).getTime() - stamp.getTime() <= 10000) {
                    if(getBPM()) {
                        successBPM = true;
                        burntCalories = getCalories();
                        break;
                    }
                }
                if(!successBPM) {
                    averageBPM = 0;
                }

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        //extra action
                    }
                });
            }
        }).start();
    }

    public boolean getBPM() {
        if(((SpeedforceApplication) getApplication()).isChanged()
                && !((SpeedforceApplication) this.getApplication()).isExpired(new Date())) {
            averageBPM = ((SpeedforceApplication) this.getApplication()).getAverageBPM();
            return true;
        }
        return false;
    }

    public double getCalories() {
        double athleteFactor = 0;
        double durationFactor = 0;

        if (gender.equals("Masculino")) {
            athleteFactor = ((double)age * 0.2017) + (weight * 0.453592 * 0.1988) + (averageBPM * 0.6309) - 55.0969;
        } else if (gender.equals("Femenino")) {
            athleteFactor = ((double)age * 0.074) + (weight * 0.453592 * 0.1263) + (averageBPM * 0.4472) - 20.4022;
        }

        durationFactor = minutesElapsed / 4.184;

        return (double)Math.round(athleteFactor * durationFactor * 10) / 10.0;
    }

    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    class FetchWeatherTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            // progressBar.setVisibility(View.VISIBLE);
            // responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            final String OWM_API_KEY = "1eda29af092a8d886c1aef0409cd566c";
            final String BASE_API_URL = "http://api.openweathermap.org/data/2.5/weather?";

            final String LATITUDE_PARAM_NAME = "lat";
            final String LONGITUDE_PARAM_NAME = "lon";
            final String API_KEY_PARAM_NAME = "APPID";
            final String UNIT_PARAM_NAME = "units";

            final String units = "metric";
            String lat = Double.toString(mCurrentLocation.getLatitude());
            String lng = Double.toString(mCurrentLocation.getLongitude());

            Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                    .appendQueryParameter(LATITUDE_PARAM_NAME, lat)
                    .appendQueryParameter(LONGITUDE_PARAM_NAME, lng)
                    .appendQueryParameter(UNIT_PARAM_NAME, units)
                    .appendQueryParameter(API_KEY_PARAM_NAME, OWM_API_KEY)
                    .build();

            try {
                URL url = new URL(builtUri.toString());
                Log.d("URL", "Fetch Weather URL: " + url.toString()); //Check built URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            //progressBar.setVisibility(View.GONE);
            //Log.i("INFO", response);
            Log.d("JSON", "WEATHER RESPONSE: " + response);

            try {
                JSONObject currentWeather = new JSONObject(response);
                JSONObject weather = currentWeather.getJSONArray("weather").getJSONObject(0);
                String description = weather.getString("description");
                climateCondition = new WeatherConditionMap().getCondition(weather.getString("icon"));
                temperature = currentWeather.getJSONObject("main").getDouble("temp");
                relativeHumidity = currentWeather.getJSONObject("main").getDouble("humidity");

                Log.d("FILTERED RESPONSE 2", "description: " + description
                        + ", icon: " + climateCondition
                        + ", temp: " + Double.toString(temperature)
                        + ", humidity: " + Double.toString(relativeHumidity));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    class PostResultsTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            try {
                final String API_URL = "http://speedforceservice.azurewebsites.net/api/training/logsession";
                URL url = new URL(API_URL);

                //setAverageBPM();

                //sessionJSON = getSessionJSON();
                Log.d("JSON", "Session in JSON: " + sessionJSON.toString());
                sessionJSON.put("SessionStatusID", "Sincronizada");
                String requestBody = sessionJSON.toString();
                sessionJSON.put("SessionStatusID", sessionStatus);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    //for output
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                    bufferedWriter.write(requestBody);
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    //for input
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            progressBar.setVisibility(View.GONE);
            route.clear();
            milestones.clear();
            providedRouteList.clear();
            providedRouteIndex = -1;
            indexedRouteIdList.clear();
            indexedRouteNameList.clear();
            providedRouteArrayIndexes.clear();
            mMap.clear();
            updateMarkers();

            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);

            boolean posted = false;
            String msg = "No Hubo Mensaje...";
            String sess = null;

            try {
                JSONObject responseJSON = new JSONObject(response);
                //posted = responseJSON.getBoolean("success");
                sess = responseJSON.getString("SessionID");
                //msg = responseJSON.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (sess != null && sess.equals(sessionID)) {
                posted = true;
                msg = "Sesión Enviada Exitosamente";
            } else {
                posted = false;
                msg = "No se pudo enviar la sesión";
            }

            goToResults(posted, msg);
        }
    }


    class WeatherConditionMap {

        private final Hashtable<String, String> conditions;

        public WeatherConditionMap() {
            conditions = new Hashtable<>();
            conditions.put("01", "Despejado");
            conditions.put("02", "Nublado");
            conditions.put("03", "Nublado");
            conditions.put("04", "Nublado");
            conditions.put("09", "Llovizna");
            conditions.put("10", "Lluvioso");
            conditions.put("11", "Tormenta Eléctrica");
            conditions.put("13", "Nevando");
            conditions.put("50", "Neblina");
        }

        public String getCondition(String code) {
            String value;
            String key = code.substring(0, code.length() - 1);
            if (conditions.containsKey(key)) {
                value = conditions.get(key);
            } else {
                value = "Desconocido";
            }
            return value;
        }
    }



    class PostRouteParamsTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            try {
                final String API_URL = "http://speedforceservice.azurewebsites.net/api/training/routes";
                URL url = new URL(API_URL);

                JSONObject paramsJSON = getParamsJSON();
                Log.d("JSON", "Params Request in JSON: " + paramsJSON.toString());
                String requestBody = paramsJSON.toString();

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    //for output
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                    bufferedWriter.write(requestBody);
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    //for input
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            progressBar.setVisibility(View.GONE);
            if (response == null) {
                response = "ERROR: null response. Replacing for empty brackets.";
                Log.d("JSON", "ROUTE ARRAY RESPONSE: " + response);
                response = "[]";
            } else {
                Log.d("JSON", "ROUTE ARRAY RESPONSE: " + response);
            }

            try {
                progressBar.setVisibility(View.VISIBLE);
                providedRouteJsonArray = new JSONArray(response);
                //HAUSDORFF APPROVED - Up to 3
                //loop through session array
                providedRouteArrayIndexes = new ArrayList<>();
                for (int i = 0; i < providedRouteJsonArray.length(); i++) {
                    JSONObject routeInstance = providedRouteJsonArray.getJSONObject(i);
                    String routeInstanceId = routeInstance.getString("RouteID");
                    String routeInstanceName = routeInstance.getString("RouteName");
                    JSONArray coordinatesArray = routeInstance.getJSONArray("Coordinates");
                    if (SessionUtility.isHausdorffValid(getCoordinatesJSON(), coordinatesArray)) {
                        // Creating Location List to add route

                        JSONObject coordinatesObj;
                        Location location;
                        ArrayList<Location> locationList = new ArrayList<>();
                        for (int x = 0; x < coordinatesArray.length(); x++) {
                            coordinatesObj = coordinatesArray.getJSONObject(x);
                            location = new Location("");
                            location.setLatitude(coordinatesObj.getDouble("lat"));
                            location.setLongitude(coordinatesObj.getDouble("lng"));
                            locationList.add(location);
                        }
                        providedRouteList.add(locationList);
                        indexedRouteIdList.add(routeInstanceId);
                        indexedRouteNameList.add(routeInstanceName);
                        providedRouteArrayIndexes.add(i);
                        if (providedRouteArrayIndexes.size() >= SessionUtility.MAX_SESSIONS_TO_EVALUATE) {
                            // if size of list of valid sessions is ge 3, break loop.
                            break;
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
                toastMessage("Error analizando rutas similares");
                providedRouteList.clear();
                providedRouteArrayIndexes.clear();
                progressBar.setVisibility(View.GONE);
            }
            // ask user if one of the routes in list was intended. Paint the route.
            // ask for name for the route.
            askIntendedRoute();

        }
    }



    //-- Intended Route Asking Members and Methods --//

    private ArrayList<ArrayList<Location>> providedRouteList = new ArrayList<>();
    private int providedRouteIndex = -1;
    private ArrayList<String> indexedRouteIdList = new ArrayList<>();
    private ArrayList<String> indexedRouteNameList = new ArrayList<>();
    private JSONArray providedRouteJsonArray;
    private ArrayList<Integer> providedRouteArrayIndexes = new ArrayList<>();

    private void askIntendedRoute() {
        if (providedRouteList.size() > 0) { // if provided route list has at least one element.

            providedRouteIndex = 0;
            updateMarkers();

            startButton.setVisibility(View.GONE);
            yesButton.setVisibility(View.VISIBLE);
            yesButton.setVisibility(View.VISIBLE);
            intendedRouteQuestionTextView.setVisibility(View.VISIBLE);

        } else {
            postNewRoute();
        }
    }

    // on click method
    private void yesClick() {
        postProvidedRoute();
        providedRouteIndex = -1;
    }

    // on click method
    private void noClick() {
        if (providedRouteList.size() > (providedRouteIndex + 1)) {
            providedRouteIndex++;
            updateMarkers();
        } else {
            postNewRoute();
            providedRouteIndex = -1;
        }
    }

    private void postNewRoute() {
        setRouteName();
    }

    private void postProvidedRoute() {
        // set provided data to session json
        try {
            sessionJSON = getSessionJSON();
            sessionJSON.put("RouteID", indexedRouteIdList.get(providedRouteIndex));
            sessionJSON.put("RouteName", indexedRouteNameList.get(providedRouteIndex));
            int index = providedRouteArrayIndexes.get(providedRouteIndex);
            JSONObject routeInstance = providedRouteJsonArray.getJSONObject(index);
            sessionJSON.put("Coordinates", routeInstance.getJSONArray("Coordinates"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        endPostPrep();
    }

    private void endPostPrep() {
        yesButton.setVisibility(View.GONE);
        yesButton.setVisibility(View.GONE);
        intendedRouteQuestionTextView.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);

        new PostResultsTask().execute();
    }





///////////////////////////////////////

    //-- Background Sync Service Members and Methods --//

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), StartServiceReceiver.class);
        intent.putExtra("Username", userID);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, StartServiceReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                (AlarmManager.INTERVAL_FIFTEEN_MINUTES/3), pIntent);
        Log.d("AlarmManager", "Alarm set for SyncService.");
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), StartServiceReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, StartServiceReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
        Log.d("AlarmManager", "Alarm canceled.");
    }





    class FetchSessionTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {

            try {
                final String API_URL = "http://speedforceservice.azurewebsites.net/api/training/challenge/" + userID;
                URL url = new URL(API_URL);
                Log.d("URL", "Fetch Session URL: " + url.toString()); //Check built URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            boolean success = true;
            String msg = "Sesión Habilitada";
            if(response == null) {
                success = false;
                response = "THERE WAS AN ERROR";
                msg = "No Hubo Respuesta";
            }
            Log.d("JSON", "Fetch Session RESPONSE: " + response);

            if (success) {
                try {
                    JSONObject json = new JSONObject(response);
                    DatabaseHelper.getInstance(MapsActivity.this).insertSession(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("FetchSessionTaskERROR", "OnPostExecute.");
                    msg = "Sesión Corrupta";
                    toastMessage(msg);
                }
                toastMessage(msg);
            } else {
                toastMessage(msg);
            }
        }
    }

}











