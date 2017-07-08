package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    // User name
    private EditText editTextUsername;
    // Password
    private EditText editTextPassword;
    // Sign In
    private Button buttonSignIn;
    // Message
    private TextView textViewRegister;

    private String username;

    private String password;

    private ProgressBar progressBar;

    private ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.login_edittext_username_id);
        editTextPassword = (EditText) findViewById(R.id.login_edittext_password_id);
        buttonSignIn = (Button) findViewById(R.id.login_button_signin_id);
        textViewRegister = (TextView) findViewById(R.id.login_textview_create_account_id);
        imageViewIcon = (ImageView) findViewById(R.id.login_imageview_icon_id);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Stores User name
                username = String.valueOf(editTextUsername.getText());
                // Stores Password
                password = String.valueOf(editTextPassword.getText());

                // Validates the User name and Password for admin, admin
                /*if (username.equals("admin") && password.equals("admin")) {

                } else {

                }*/

                new SingInTask().execute();
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        imageViewIcon.setOnClickListener(new View.OnClickListener() { //TODO

            @Override
            public void onClick(View view) {
                Log.d("Login", "Bypassing login process. Username: jojikun");
                bypassMaps();
            }
        });
    }

    private void createAccount() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void toastResponse(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void login(String msg) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Username", username);
        intent.putExtra("message", msg);
        startActivity(intent);
        // when MapsActivity closes
        //finish();
    }

    class SingInTask extends AsyncTask<Void, Void, String> {

        //private Exception exception;

        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                //final String API_URL = String.valueOf(R.string.ngrok_url);
                final String API_URL = "http://speedforceservice.azurewebsites.net/api/users/loginA";
                //final String API_URL = "http://9805f273.ngrok.io/api/speedforce/users/loginA";
                URL url = new URL(API_URL);

                JSONObject json = new JSONObject();
                json.put("Username", username);
                json.put("Password", password);
                json.put("Role", "Atleta");
                String requestBody = json.toString();

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    //for output
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    ////OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                    ////BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                    bufferedWriter.write(requestBody);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    ////outputStream.close();

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

            boolean authenticated = false;
            String msg = "No Hubo Mensaje...";
            String user = null;

            JSONObject responseJSON;
            Log.d("Login User Response", response);
            try {
                responseJSON = new JSONObject(response);
                //authenticated = responseJSON.getBoolean("success");
                user = responseJSON.getString("Username");
                //msg = responseJSON.getString("message");

                if (user != null && user.equals(username)) {
//                    if(!dbHelper.userExists(username)) {
//                        dbHelper.insertUser(responseJSON);
//                    }
                    if(!DatabaseHelper.getInstance(LoginActivity.this).userExists(username)) {
                        DatabaseHelper.getInstance(LoginActivity.this).insertUser(responseJSON);
                    }
                    authenticated = true;
                    msg = "Login Exitoso";
                } else {
                    msg = "Login Fallido";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                msg = "Login Fallido";
            }

            toastResponse(msg);

            if (authenticated) {
                login(msg);
            }

        }
    }

    private void bypassMaps() { //TODO
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Username", "jojikun");
        startActivity(intent);
        //insertSampleData();
    }

    private void insertSampleData () { //TODO

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject location;

        try {

            location = new JSONObject();
            location.put("lat", 18.4514340);
            location.put("lng", -69.9444650);
            location.put("milestone", true);
            array.put(location);

            location = new JSONObject();
            location.put("lat", 18.4524980);
            location.put("lng", -69.9422170);
            location.put("milestone", false);
            array.put(location);

            location = new JSONObject();
            location.put("lat", 18.4509970);
            location.put("lng", -69.9411820);
            location.put("milestone", false);
            array.put(location);

            json.put("Username", "jojikun");
            json.put("Password", "sunsun");
            json.put("Role", "Atleta");
            json.put("Email", "georgetamate@gmail.com");
            json.put("Name", "George");
            json.put("LastName", "García");
            json.put("Sex", "Masculino");
            json.put("BirthDate", "12/24/1991");
            json.put("CityName", "Santo Domingo");
            json.put("CountryName", "República Dominicana");
            json.put("TelephoneNumber", "809-534-5822");
            json.put("Weight", 170);
            json.put("Height", 1.65);
            json.put("BikerType", "Sprinter");

            json.put("SessionID", UUID.randomUUID().toString());//uu
            json.put("UserID", "jojikun");
            json.put("ClimateConditionID", "NA");
            json.put("AverageBPM", 0);
            json.put("RouteID", UUID.randomUUID().toString());//uu
            json.put("RouteName", "TestRoute00");
            json.put("Coordinates", array);//arr
            json.put("StartTime", "NA");
            json.put("EndTime", "NA");
            json.put("Distance", 0);
            json.put("BurntCalories", 0);
            json.put("RelativeHumidity", 0);
            json.put("Temperature", 0);
            json.put("TrainingTypeID", "Distancia");
            json.put("SessionStatusID", "Pendiente");

            if (!DatabaseHelper.getInstance(this).userExists(json.getString("UserID"))) {
                Log.d("TestInsert", "User does no exist. Inseting user.");
                DatabaseHelper.getInstance(this).insertUser(json);
                Toast.makeText(this, json.getJSONArray("Coordinates").toString(), Toast.LENGTH_SHORT).show();
            }

            //DatabaseHelper.getInstance(this).insertSession(json);
            Toast.makeText(this,json.getJSONArray("Coordinates").toString(), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ExceptionJSON", "TEST INSERT FAILED.");
        }

//        try {
//            Toast.makeText(this,json.getJSONArray("Coordinates").toString(), Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.i("insertSampleData", "TEST INSERT SUCCESS?");
    }

}
