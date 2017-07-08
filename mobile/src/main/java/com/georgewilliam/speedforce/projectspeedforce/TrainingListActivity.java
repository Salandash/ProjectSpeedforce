package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.UUID;

public class TrainingListActivity extends AppCompatActivity {

    private ListView mList;
    //private DatabaseHelper dbHelper;
    private String userID;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_list);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("Username");

        //dbHelper = new DatabaseHelper(this, null, null, 1);

        mList = (ListView) findViewById(R.id.traininglist_listview_id);
        
        populateListView();
        registerClickCallback();
    }

    private void populateListView() {
        // Crear lista de items
        //String[] items = {"Blue", "Green", "Purple", "Red"};

        JSONArray array = DatabaseHelper.getInstance(this).listSessions();
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
                //toast(message);

                startSessionActivity(textView.getText().toString());
            }
        });
    }

    private void startSessionActivity(String sessionID) {
        // Temporal
        Intent intent = new Intent(this, ReceivedMapsActivity.class);
        intent.putExtra("SessionID", sessionID);
        intent.putExtra("UserID", userID);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
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
                finish();
                break;
            case R.id.nav_training_id:
                break;
            case R.id.nav_history_id:
                intent = new Intent(this, HistoryActivity.class);
                intent.putExtra("Username", userID);
                startActivity(intent);
                break;
            case R.id.nav_logout_id:
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
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
