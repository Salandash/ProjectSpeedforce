/**
 * @file MapsActivity.java
 * @brief Fuente de la clase MapsActivity
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Clase que representa el Activity y Fragment del mapa de la aplicación móvil.
 * El Fragment de este Activity se encuentra definido en su respectivo archivo de Layout.
 */
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
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
    private static final int DEFAULT_ZOOM = 40; //15
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;

    // Start location and end location.
    private LatLng mStartLocation = new LatLng(18.450915, -69.952788);
    private LatLng mFinishLocation = new LatLng(18.453427, -69.941788);

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Anadido adicionalmente
    private static final int CAPTURE_INTERVAL = 3;
    private int count = 0;
    private boolean isInSession = false;
    private ArrayList<Location> route = new ArrayList<Location>();
    private Chronometer chronometer;
    private Button startButton;
    private JSONObject sessionJSON;

    // for API calls
    private TextView responseView;
    private ProgressBar progressBar;

    // to convert to session json
    private String sessionID; //uuid
    private String userID = "PlaceHolderUser";
    private String climateCondition = "Desconocido";
    private double averageBPM = 85.33; //TODO
    private String routeID; //uuid
    private String city = "Desconocido";
    private String country = "Desconocido";
    //coordenadas
    private String startTime;
    private String endTime;
    private float distance;
    private double burntCalories = 830.65; //TODO
    private double relativeHumidity;
    private double temperature;
    private String trainingType;
    private String sessionStatus = "Local";


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
        // Build the Play services client for use by the Fused Location Provider and the Places API.
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("username");

        chronometer = (Chronometer) findViewById(R.id.chronometer_id);
        chronometer.setVisibility(View.GONE);
        startButton = (Button) findViewById(R.id.start_button_id);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInSession) {
                    endSession();
                } else {
                    displayTrainingTypeSelectDialog();
                    //startSession();
                }
            }
        });
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
                .build();
        createLocationRequest();
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

        /*if (mLocationPermissionGranted) {
            // Get the businesses and other points of interest located
            // nearest to the device's current location.
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Add a marker for each place near the device's current location, with an
                        // info window showing place information.
                        String attributions = (String) placeLikelihood.getPlace().getAttributions();
                        String snippet = (String) placeLikelihood.getPlace().getAddress();
                        if (attributions != null) {
                            snippet = snippet + "\n" + attributions;
                        }

                        mMap.addMarker(new MarkerOptions()
                                .position(placeLikelihood.getPlace().getLatLng())
                                .title((String) placeLikelihood.getPlace().getName())
                                .snippet(snippet));
                    }
                    // Release the place likelihood buffer.
                    likelyPlaces.release();
                }
            });
        } else {
            mMap.addMarker(new MarkerOptions()
                    .position(mDefaultLocation)
                    .title(getString(R.string.default_info_title))
                    .snippet(getString(R.string.default_info_snippet)));
        }*/


        mMap.addMarker(new MarkerOptions()
                .position(mStartLocation)
                .title("INICIO")
                .snippet(mStartLocation.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.addMarker(new MarkerOptions()
                .position(mFinishLocation)
                .title("FIN")
                .snippet(mFinishLocation.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        LatLng latlng;
        int i = 0;
        for (Location location : route) {
            latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title("Checkpoint " + Integer.toString(++i))
                    .snippet(latlng.toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

        if ( route.size() >= 2 )
        {
            PolylineOptions options = new PolylineOptions();
            options.color( Color.parseColor( "#CC0000FF" ) );
            options.width( 5 );
            options.visible( true );
            for ( Location location : route )
            {
                options.add( new LatLng( location.getLatitude(),
                        location.getLongitude() ) );
            }
            mMap.addPolyline( options );
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

    // Inicia y detiene la sesion de entrenamiento
/*    public void toggleSession(View view) {
        if (isInSession) {
            isInSession = false;
            count = 0;
            route.clear();
            mMap.clear();
            updateMarkers();
        } else {
            isInSession = true;
        }
    }*/

    private void startSession() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startButton.setEnabled(false);
        sessionID = UUID.randomUUID().toString();
        distance = 0;
        fetchCityAndCountry();
        isInSession = true;

        startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        Log.d("time capture", "Start Time: " + startTime);

        // Calling Weather API
        new FetchWeatherTask().execute();

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        chronometer.setVisibility(View.VISIBLE);
        startButton.setText("Stop");
        startButton.setEnabled(true);
    }

    private void endSession() {
        startButton.setEnabled(false);
        isInSession = false;
        routeID = UUID.randomUUID().toString();

        endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        //Log.d("time capture", "Finish Time: " + endTime);

        chronometer.stop();
        chronometer.setVisibility(View.GONE);

        sessionJSON = getSessionJSON();

        Log.d("JSON", "Session in JSON: " + sessionJSON.toString());
        new PostResultsTask().execute();

        count = 0;
        route.clear();
        mMap.clear();
        updateMarkers();
        startButton.setText("Start");
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
            jsonObj.put("Coordinates", getCoordinatesJSON()); //pin
            jsonObj.put("CityName", city); //pin
            jsonObj.put("CountryName", country); //pin
            jsonObj.put("StartTime", startTime);
            jsonObj.put("EndTime", endTime);
            jsonObj.put("Distance", distance);
            jsonObj.put("BurntCalories", burntCalories);
            jsonObj.put("RelativeHumidity", relativeHumidity);
            jsonObj.put("Temperature", temperature);
            jsonObj.put("TrainingTypeID", trainingType);
            jsonObj.put("SessionStatusID", sessionStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    private JSONArray getCoordinatesJSON()  {
        JSONArray jsonLocationArray = new JSONArray();
        for ( Location location : route) {
            JSONObject jsonLocation = new JSONObject();
            try {
                jsonLocation.put("lat", location.getLatitude());
                jsonLocation.put("lng", location.getLongitude());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonLocationArray.put(jsonLocation);
        }
        return jsonLocationArray;
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
                        toastMessage("Selected: " + trainingType);

                        startSession();
                    }
                });
        dialog.show();
    }

    private void goToResults(boolean success, String msg) {
        if (success) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Results Post FAILED", Toast.LENGTH_LONG).show();
        }
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

        private Exception exception;

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            try {
                final String API_URL = "http://26e76265.ngrok.io/session";
                URL url = new URL(API_URL);

                String requestBody = sessionJSON.toString();

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
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);

            boolean posted = false;
            String msg = "NO MESSAGE...";

            try {
                JSONObject responseJSON = new JSONObject(response);
                posted = responseJSON.getBoolean("success");
                msg = responseJSON.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
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
            conditions.put("11", "Tormenta Electrica");
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

}











