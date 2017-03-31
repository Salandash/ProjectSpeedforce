package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;

    private String username;
    private String password;
    private String confirmPassword;

    private String email;
    private String name;
    private String lastName;
    private String birthDate;
    private String gender;
    private String telephone;
    private String country;
    private String city;
    private double height;
    private double weight;
    private String bikerType;
    private String bike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        name = extras.getString("name");
        lastName = extras.getString("lastName");
        birthDate = extras.getString("birthDate");
        gender = extras.getString("gender");
        telephone = extras.getString("telephone");
        country = extras.getString("country");
        city = extras.getString("city");
        height = extras.getDouble("height");
        weight = extras.getDouble("weight");
        bikerType = extras.getString("bikerType");
        bike = extras.getString("bike");

        editTextUsername = (EditText) findViewById(R.id.register_edittext_username_id);
        editTextPassword = (EditText) findViewById(R.id.register_edittext_password_id);
        editTextConfirmPassword = (EditText) findViewById(R.id.register_edittext_confirm_password_id);

        Button buttonSignUp;
        buttonSignUp = (Button) findViewById(R.id.register_button_signup_id);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                username = String.valueOf(editTextUsername.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());

                // Validating passwords
                if (!password.equals(confirmPassword)) {
                    toastMessage("Passwords are not the same.");
                    return;
                }

                new SingUpTask().execute();
            }
        });
    }

    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void login(String msg) {
        Intent intent;
        intent = new Intent(this, MapsActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("message", msg);
        startActivity(intent);
        // when MapsActivity closes
        finish();
    }

    class SingUpTask extends AsyncTask<Void, Void, String> {

//        private Exception exception;

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            try {
                final String API_URL = "http://26e76265.ngrok.io/register";
                //final String API_URL = "http://26e76265.ngrok.io/api/speedforce/users/registerA";
                URL url = new URL(API_URL);

                JSONObject json = new JSONObject();
                json.put("Username", username);
                json.put("Password", password);
                json.put("Role", "Atleta");
                json.put("Email", email);
                json.put("Name", name);
                json.put("LastName", lastName);
                json.put("Sex", gender);
                json.put("BirthDate", birthDate);
                json.put("CityName", city);
                json.put("CountryName", country);
                json.put("TelephoneNumber", telephone);
                json.put("Height", height);
                json.put("Weight", weight);
                json.put("BikerType", bikerType);
                //json.put("Bike", bike); TODO eliminate from model
                String requestBody = json.toString();

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

            boolean created = false;
            String msg = "NO MESSAGE...";

            try {
                JSONObject responseJSON = new JSONObject(response);
                created = responseJSON.getBoolean("success");
                msg = responseJSON.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            toastMessage(msg);

            if (created) {
                login(msg);
            }
        }
    }
}
