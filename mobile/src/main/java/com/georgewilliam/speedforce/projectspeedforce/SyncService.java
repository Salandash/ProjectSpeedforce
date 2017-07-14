package com.georgewilliam.speedforce.projectspeedforce;

import android.app.IntentService;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by georgetamate on 6/28/17.
 */
public class SyncService extends IntentService {

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SyncService", "Creating Threads for HTTP Requests.");

        final String userID = intent.getExtras().getString("Username");
        //final DatabaseHelper dbHelper = new DatabaseHelper(this);

        Log.d("SyncService", "Intent Username: " + userID);
//        if (DatabaseHelper.getInstance(this).userExists(userID)) {
//            Log.d("SyncService", "UserExists in database!");
//        }



        // Fetch Sessions in Background
        new Thread( new Runnable() {
            @Override
            public void run() {
                String response;
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
                        response = stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                }
                catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    response = null;
                }
                if(response == null) {
                    Log.e("Sync - FetchSession", "Could not get response");
                } else {
                    Log.d("Sync - FetchSession", "RESPONSE: " + response);
                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("SessionStatusID");
                        if (!status.equals("Pendiente")) {
                            Log.d("Sync - FetchSession", "CORRUPTED STATUS: " + status + ". Changing to -> Pendiente");
                            json.put("SessionStatusID", "Pendiente");
                        }
                        String country = json.getString("CountryName");
                        if (country.equals("null")) {
                            Log.d("Sync - FetchSession", "NULL COUNTRY: " + country + ". Changing to -> República Dominicana");
                            json.put("CountryName", "República Dominicana");
                        }
                        String city = json.getString("CityName");
                        if (city.equals("null")) {
                            Log.d("Sync - FetchSession", "NULL CITY: " + city + ". Changing to -> Santo Domingo");
                            json.put("CityName", "Santo Domingo");
                        }
                        DatabaseHelper.getInstance(SyncService.this).insertSession(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Sync - FetchSession", "Could not insert session into database");
                    }
                }
            }
        }).start();

        // Upload Sessions in Background
        new Thread( new Runnable() {
            @Override
            public void run() {
                String response;
                JSONObject sessionJSON = new JSONObject();
                // only do upload http request if local sessions exists
                if(DatabaseHelper.getInstance(SyncService.this).localSessionExists()) {
                    Log.i("SyncService", "Local Session exists in database. Attempting to upload.");
                    try {
                        final String API_URL = "http://speedforceservice.azurewebsites.net/api/training/logsession";
                        URL url = new URL(API_URL);

                        sessionJSON = DatabaseHelper.getInstance(SyncService.this).getSessionToUpload();
                        Log.d("JSON", "Session to Upload: " + sessionJSON.toString());
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
                            response = stringBuilder.toString();
                        }
                        finally{
                            urlConnection.disconnect();
                        }
                    }
                    catch(Exception e) {
                        Log.e("ERROR", e.getMessage(), e);
                        response = null;
                    }
                    if(response == null) {
                        Log.e("Sync - UploadSession", "Could not get response");
                    } else {
                        Log.d("Sync - UploadSession", "RESPONSE: " + response);
                        try {
                            Log.i("SyncService", "Local Session Uploaded. Attempting to update Session Status in database.");
                            DatabaseHelper.getInstance(SyncService.this).updateSessionStatusToSync(sessionJSON.getString("SessionID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Sync - UploadSession", "Could not update session status");
                        }
                    }

                }

            }
        }).start();

//        // Fetch Sessions in Background
//        new Thread( new Runnable() {
//            @Override
//            public void run() {
//                String response;
//                try {
//                    final String API_URL = "http://a4ea5004.ngrok.io/syncget";
//                    URL url = new URL(API_URL);
//                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                    urlConnection.setRequestMethod("GET");
//                    try {
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                        StringBuilder stringBuilder = new StringBuilder();
//                        String line;
//                        while ((line = bufferedReader.readLine()) != null) {
//                            stringBuilder.append(line).append("\n");
//                        }
//                        bufferedReader.close();
//                        response = stringBuilder.toString();
//                        Log.d("Sync - FetchSession", "Response Received");
//                    }
//                    finally{
//                        urlConnection.disconnect();
//                    }
//                }
//                catch(Exception e) {
//                    Log.e("ERROR", e.getMessage(), e);
//                    response = null;
//                }
//                if(response == null) {
//                    Log.d("Sync - FetchSession", "Could not get response");
//                } else {
//                    Log.d("Sync - FetchSession", "RESPONSE: " + response);
//                }
//            }
//        }).start();
//
//        // Upload Sessions in Background
//        new Thread( new Runnable() {
//            @Override
//            public void run() {
//                String response;
//                try {
//                    final String API_URL = "http://a4ea5004.ngrok.io/syncpost";
//                    URL url = new URL(API_URL);
//
//                    JSONObject json = new JSONObject();
//                    json.put("Username", userID);
//                    json.put("Message", "HELLO SPEEDFORCE!");
//                    json.put("Version", 0.7);
//                    String requestBody = json.toString();
//
//                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                    try {
//                        //for output
//                        urlConnection.setDoOutput(true);
//                        urlConnection.setRequestMethod("POST");
//                        urlConnection.setRequestProperty("Content-Type", "application/json");
//                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
//                        bufferedWriter.write(requestBody);
//                        bufferedWriter.flush();
//                        bufferedWriter.close();
//                        //for input
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                        StringBuilder stringBuilder = new StringBuilder();
//                        String line;
//                        while ((line = bufferedReader.readLine()) != null) {
//                            stringBuilder.append(line).append("\n");
//                        }
//                        bufferedReader.close();
//                        response = stringBuilder.toString();
//                        Log.d("Sync - UploadSession", "Response Received");
//                    }
//                    finally{
//                        urlConnection.disconnect();
//                    }
//                }
//                catch(Exception e) {
//                    Log.e("ERROR", e.getMessage(), e);
//                    response = null;
//                }
//                if(response == null) {
//                    Log.d("Sync - UploadSession", "Could not get response");
//                } else {
//                    Log.d("Sync - UploadSession", "RESPONSE: " + response);
//                }
//            }
//        }).start();

        Log.d("SyncService", "Finished creating Threads.");

    }
}
