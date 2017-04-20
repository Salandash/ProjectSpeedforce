package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.UUID;

public class TrainingListActivity extends AppCompatActivity {

    private ListView mList;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_list);

        dbHelper = new DatabaseHelper(this, null, null, 1);

        mList = (ListView) findViewById(R.id.traininglist_listview_id);
        
        populateListView();
        registerClickCallback();
    }

    private void populateListView() {
        // Crear lista de items
        //String[] items = {"Blue", "Green", "Purple", "Red"};

        JSONArray array = dbHelper.listSessions();
        ArrayList<String> sessions = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                sessions.add(array.getJSONObject(i).getString("SessionID"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONException", "TrainingListActivity.populateListView");
            }
        }

        // Construir Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.training_list_item,
                sessions
        );

        // Configurar el ListView
        mList.setAdapter(adapter);
    }


    private void registerClickCallback() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // TextView en el layout del item
                TextView textView = (TextView) view;
                String message = "Item:" + textView.getText().toString() + " id:" + Long.toString(id);
                toast(message);

                startSessionActivity(textView.getText().toString());
            }
        });
    }

    private void startSessionActivity(String sessionID) {
        // Temporal
        Intent intent = new Intent(this, ReceivedMapsActivity.class);
        intent.putExtra("SessionID", sessionID);
        intent.putExtra("UserID", "jojikun");
        intent.putExtra("RouteID", UUID.randomUUID().toString());
        intent.putExtra("TrainingType", "Distancia");
        intent.putExtra("SessionStatus", "Pendiente");
        startActivity(intent);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

//    private class Session {
//
//        private String mSessionID;
//        private String mUserID;
//        private String mRouteID;
//        private String mCoordinates;
//        private String mTrainingTypeID;
//        private String mCityName;
//        private String mCountryName;
//        private String mSessionStatusID;
//
//        public Session(String sessionID) {
//            mSessionID = sessionID;
//        }
//
//        public String getSessionID() {
//            return mSessionID;
//        }
//    }
}
