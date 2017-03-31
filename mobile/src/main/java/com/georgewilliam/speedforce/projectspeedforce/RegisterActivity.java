package com.georgewilliam.speedforce.projectspeedforce;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
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
//    private EditText editTextUsername;
//    private EditText editTextPassword;
//    private EditText editTextConfirmPassword;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextBirthDate;
    private EditText editTextWeight;
    private EditText editTextHeight;
    ////
//    private EditText editTextGender;
    private EditText editTextTelephone;
//    private EditText editTextCountry;
    private EditText editTextCity;
    private EditText editTextBikerType;
    private EditText editTextBike;

    // Sign In
    private Button buttonNext;
    private RadioButton radioButtonM;
    private RadioButton radioButtonF;
    private Spinner spinnerCountry;

    private String email;
//    private String username;
//    private String password;
//    private String confirmPassword;
    private String name;
    private String lastName;
    private String birthDate;
    private String weight;
    private String height;
    private double weightDouble;
    private double heightDouble;
    ////
    private String gender;
    private String telephone;
    private String country;
    private String city;
    private String bikerType;
    private String bike;


    private Calendar myCalendar;

    private DatePickerDialog.OnDateSetListener date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = (EditText) findViewById(R.id.register_edittext_email_id);
//        editTextUsername = (EditText) findViewById(R.id.register_edittext_username_id);
//        editTextPassword = (EditText) findViewById(R.id.register_edittext_password_id);
//        editTextConfirmPassword = (EditText) findViewById(R.id.register_edittext_confirm_password_id);

        editTextName = (EditText) findViewById(R.id.register_edittext_name_id);
        editTextLastName = (EditText) findViewById(R.id.register_edittext_lastname_id);
        editTextBirthDate = (EditText) findViewById(R.id.register_edittext_birthdate_id);
        editTextWeight = (EditText) findViewById(R.id.register_edittext_weight_id);
        editTextHeight = (EditText) findViewById(R.id.register_edittext_height_id);

//        editTextGender = (EditText) findViewById(R.id.register_edittext_gender_id);
        editTextTelephone = (EditText) findViewById(R.id.register_edittext_telephone_id);
//        editTextCountry = (EditText) findViewById(R.id.register_edittext_country_id);
        editTextCity = (EditText) findViewById(R.id.register_edittext_city_id);
        editTextBikerType = (EditText) findViewById(R.id.register_edittext_bikertype_id);
        editTextBike = (EditText) findViewById(R.id.register_edittext_bike_id);

        buttonNext = (Button) findViewById(R.id.register_button_next_id);
        radioButtonM = (RadioButton) findViewById(R.id.register_radiobutton_masculine_id);
        radioButtonF = (RadioButton) findViewById(R.id.register_radiobutton_femenine_id);
        spinnerCountry = (Spinner) findViewById(R.id.register_spinner_country_id);

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
                // Hides soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextBirthDate.getWindowToken(), 0);

                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                final String phonePattern = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";

                email = String.valueOf(editTextEmail.getText());
//                username = String.valueOf(editTextUsername.getText());
//                password = String.valueOf(editTextPassword.getText());
//                confirmPassword = String.valueOf(editTextConfirmPassword.getText());
                name = String.valueOf(editTextName.getText());
                lastName = String.valueOf(editTextLastName.getText());
                birthDate = String.valueOf(editTextBirthDate.getText());
                weight = String.valueOf(editTextWeight.getText());
                height = String.valueOf(editTextHeight.getText());

//                gender = String.valueOf(editTextGender.getText());
                telephone = String.valueOf(editTextTelephone.getText());
//                country = String.valueOf(editTextCountry.getText());
                country = spinnerCountry.getSelectedItem().toString();
                city = String.valueOf(editTextCity.getText());
                bikerType = String.valueOf(editTextBikerType.getText());
                bike = String.valueOf(editTextBike.getText());


                // Validating Email
                if (!email.matches(emailPattern)) {
                    toastMessage("Email does not have a valid pattern.");
                    return;
                }

                // Validating Phone Number
                if (!telephone.matches(phonePattern)) {
                    toastMessage("Phone Number does not have a valid pattern.");
                    return;
                }

                // Validating Gender Selection
                if (!radioButtonM.isChecked() && !radioButtonF.isChecked()) {
                    toastMessage("A Gender was not selected.");
                    return;
                }

                // Validating passwords
                /*if (!password.equals(confirmPassword)) {
                    toastResponse("Passwords are not the same.");
                    return;
                }*/

                // Validating weight
                try {
                    weightDouble = Double.parseDouble(weight);
                } catch (NumberFormatException e) {
                    toastMessage("Weight value is not a number.");
                    return;
                }
                if (weightDouble < 0) {
                    toastMessage("Weight cannot a negative number.");
                    return;
                }

                // Validating height
                try {
                    heightDouble = Double.parseDouble(height);
                } catch (NumberFormatException e) {
                    toastMessage("Height value is not a number.");
                    return;
                }
                if (heightDouble < 0) {
                    toastMessage("Height cannot a negative number.");
                    return;
                }

                //new SingUpTask().execute();

                nextRegisterPage();
            }
        });

        radioButtonM.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (radioButtonM.isChecked()) {
                    gender = "Masculino";
                    toastMessage(gender);
                }
            }
        });

        radioButtonF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (radioButtonF.isChecked()) {
                    gender = "Femenino";
                    toastMessage(gender);
                }
            }
        });

    }

    public void updateLabel() {

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editTextBirthDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void nextRegisterPage() {
        Intent intent;
        intent = new Intent(this, RegisterUserActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("name", name);
        intent.putExtra("lastName", lastName);
        intent.putExtra("birthDate", birthDate);
        intent.putExtra("gender", gender);
        intent.putExtra("telephone", telephone); //TODO must be masked at input (only -)
        intent.putExtra("country", country);
        intent.putExtra("city", city);
        intent.putExtra("height", height);
        intent.putExtra("weight", weight);
        intent.putExtra("bikerType", bikerType);
        intent.putExtra("bike", bike); //TODO Eliminate from model
        startActivity(intent);
        // when MapsActivity closes
        finish();
    }

    /*public void login(String msg) {
        Intent intent;
        intent = new Intent(this, MapsActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("message", msg);
        startActivity(intent);
        // when MapsActivity closes
        finish();
    }*/

    /*class SingUpTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            try {
                final String API_URL = "http://26e76265.ngrok.io/register";
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
                json.put("gender", gender);
                json.put("telephone", telephone);
                json.put("country", country);
                json.put("city", city);
                json.put("cyclistType", cyclistType);
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
    }*/

}
