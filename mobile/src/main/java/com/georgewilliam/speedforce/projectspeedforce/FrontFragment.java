package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class FrontFragment extends Fragment implements OnClickListener {

    Button btnLogin;
    Button btnRegister;

    Button btnDebugMaps;

    private DatabaseHelper dbHelper;

    public FrontFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this.getActivity(), null, null,1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_front, container, false);

        btnLogin = (Button) view.findViewById(R.id.front_button_login_id);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) view.findViewById(R.id.front_button_register_id);
        btnRegister.setOnClickListener(this);

        btnDebugMaps = (Button) view.findViewById(R.id.front_debug_button_maps_id);
        btnDebugMaps.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.front_button_login_id:
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.front_button_register_id:
                intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.front_debug_button_maps_id:
                intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("Username", "jojikun");
                startActivity(intent);
                //insertSampleData();
                break;
        }

    }

    private void insertSampleData () {

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject location;

        try {

            location = new JSONObject();
            location.put("lat", 18.4579648);
            location.put("lng", -69.9612128);
            array.put(location);

            location = new JSONObject();
            location.put("lat", 18.4579325);
            location.put("lng", -69.9612684);
            array.put(location);

            location = new JSONObject();
            location.put("lat", 18.4578000);
            location.put("lng", -69.9614000);
            array.put(location);

            json.put("Username", "jojikun");
            json.put("Password", "sunsun");
            json.put("Role", "Atleta");
            json.put("Email", "georgetamate@gmail.com");
            json.put("Name", "George");
            json.put("LastName", "García");
            json.put("Sex", "Masculino");
            json.put("BirthDate", "12/24/1993");
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
            json.put("Coordinates", array);//arr
            json.put("StartTime", "NA");
            json.put("EndTime", "NA");
            json.put("Distance", 0);
            json.put("BurntCalories", 0);
            json.put("RelativeHumidity", 0);
            json.put("Temperature", 0);
            json.put("TrainingTypeID", "Distancia");
            json.put("SessionStatusID", "Pendiente");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ExceptionJSON", "TEST INSERT FAILED.");
        }

        dbHelper.insertUser(json);
        dbHelper.insertSession(json);

        Toast.makeText(this.getActivity(),"TEST INSERT SUCCESS?", Toast.LENGTH_SHORT).show();
        Log.i("insertSampleData", "TEST INSERT SUCCESS?");
    }

}
