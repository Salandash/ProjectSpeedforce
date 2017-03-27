package com.georgewilliam.speedforce.projectspeedforce;

import android.app.Notification;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    // User name
    private EditText editTextUsername;
    // Password
    private EditText editTextPassword;
    // Sign In
    private Button buttonSignIn;
    // Message
    private TextView textViewMessage;

    private String username;

    private String password;

    private ProgressBar progressBar;

    private TextView responseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.login_edittext_username_id);
        editTextPassword = (EditText) findViewById(R.id.login_edittext_password_id);
        buttonSignIn = (Button) findViewById(R.id.login_button_signin_id);

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

        sendNotification();
    }

    public void toastResponse(String msg) {
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

    //////
    public void sendNotification() {
        String toSend = "Login Activity Opened!!";
        if(toSend.isEmpty())
            toSend = "You sent an empty notification";
        Notification notification = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Speedforce")
                .setContentText(toSend)
                .extend(new NotificationCompat.WearableExtender().setHintShowBackgroundOnly(true))
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
        int notificationId = 1;
        notificationManager.notify(notificationId, notification);
    }

    class SingInTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                //final String API_URL = String.valueOf(R.string.ngrok_url);
                final String API_URL = "http://26e76265.ngrok.io/login";
                //final String API_URL = "http://9805f273.ngrok.io/api/speedforce/users/loginA";
                URL url = new URL(API_URL);

                JSONObject json = new JSONObject();
                json.put("Username", username);
                json.put("Password", password);
                json.put("Role", "Athlete");
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
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            //responseView.setText(response);
            // TODO: check this.exception
            // TODO: do something with the feed

            boolean authenticated = false;
            String msg = "NO MESSAGE...";

            try {
                JSONObject responseJSON = new JSONObject(response);
                authenticated = responseJSON.getBoolean("success");
                //msg = responseJSON.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //toastResponse(msg);

            if (authenticated) {
                msg = "Login Successful!!";
                toastResponse(msg);
                login(msg);
            } else {
                toastResponse("Login Failure.");
            }
        }
    }

}
