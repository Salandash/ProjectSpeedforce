package com.georgewilliam.speedforce.projectspeedforce;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextBirthDate;
    private EditText editTextWeight;
    private EditText editTextHeight;

    // Sign In
    private Button buttonSignUp;

    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private String name;
    private String lastName;
    private String birthDate;
    private String weight;
    private String height;
    private double weightDouble;
    private double heightDouble;


    private Calendar myCalendar;

    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = (EditText) findViewById(R.id.register_edittext_email_id);
        editTextUsername = (EditText) findViewById(R.id.register_edittext_username_id);
        editTextPassword = (EditText) findViewById(R.id.register_edittext_password_id);
        editTextConfirmPassword = (EditText) findViewById(R.id.register_edittext_confirm_password_id);

        editTextName = (EditText) findViewById(R.id.register_edittext_name_id);
        editTextLastName = (EditText) findViewById(R.id.register_edittext_lastname_id);
        editTextBirthDate = (EditText) findViewById(R.id.register_edittext_birthdate_id);
        editTextWeight = (EditText) findViewById(R.id.register_edittext_weight_id);
        editTextHeight = (EditText) findViewById(R.id.register_edittext_height_id);

        buttonSignUp = (Button) findViewById(R.id.register_button_signup_id);

        // DatePicker Init
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        editTextBirthDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                boolean isError = false;
                String errorMsg;
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                email = String.valueOf(editTextEmail.getText());
                username = String.valueOf(editTextUsername.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());
                name = String.valueOf(editTextName.getText());
                lastName = String.valueOf(editTextLastName.getText());
                birthDate = String.valueOf(editTextBirthDate.getText());
                weight = String.valueOf(editTextWeight.getText());
                height = String.valueOf(editTextHeight.getText());


                // Validating Email
                if (!email.matches(emailPattern)) {
                    toastResponse("Email does not have a valid pattern.");
                    return;
                }

                // Validating passwords
                if (!password.equals(confirmPassword)) {
                    toastResponse("Passwords are not the same.");
                    return;
                }

                // Validating weight
                try {
                    weightDouble = Double.parseDouble(weight);
                } catch (NumberFormatException e) {
                    toastResponse("Weight value is not a number.");
                    return;
                }
                if (weightDouble < 0) {
                    toastResponse("Weight is a negative number.");
                    return;
                }

                // Validating height
                try {
                    heightDouble = Double.parseDouble(height);
                } catch (NumberFormatException e) {
                    toastResponse("Height value is not a number.");
                    return;
                }
                if (heightDouble < 0) {
                    toastResponse("Height is a negative number.");
                    return;
                }

                new SingUpTask().execute();
            }
        });
    }

    public void updateLabel() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editTextBirthDate.setText(sdf.format(myCalendar.getTime()));
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

    class SingUpTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            try {
                final String API_URL = "http://f881be2d.ngrok.io/register";
                URL url = new URL(API_URL);

                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("username", username);
                json.put("password", password);
                json.put("name", name);
                json.put("lastName", lastName);
                json.put("birthDate", birthDate);
                json.put("weight", weightDouble);
                json.put("height", heightDouble);
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
                created = responseJSON.getBoolean("created");
                msg = responseJSON.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            toastResponse(msg);

            if (created) {
                login(msg);
            }
        }
    }

}
