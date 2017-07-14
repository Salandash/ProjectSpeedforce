package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    private ListView mList;
    private String userID;
    private ProgressBar progressBar;
    private JSONArray historyJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("Username");

        mList = (ListView) findViewById(R.id.history_listview_id);

        progressBar = (ProgressBar) findViewById(R.id.history_progressbar_id);
        progressBar.setVisibility(View.GONE);

        new FetchHistoryTask().execute();
        new LogSessionsTask().execute();
    }

    private void populateListView(JSONArray array) {

        ArrayList<String> sessions = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                sessions.add(array.getJSONObject(i).getString("StartTime").replace("T", " "));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONException", "HistoryActivity.populateListView");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.training_list_item,
                sessions
        );

        mList.setAdapter(adapter);
    }

    private void registerClickCallback() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                goToResults(i);
            }
        });
    }

    private void goToResults(int index) {
        //sessionId former param
        Intent intent = null;

//        for (int i = 0; i < historyJSONArray.length(); i++) {
//            try {
//                if(historyJSONArray.getJSONObject(i).getString("SessionID").equals(sessionId)) {
//                    JSONObject session = historyJSONArray.getJSONObject(i);
//                    intent = new Intent(this, ResultsActivity.class);
//                    intent.putExtra("TrainingTypeID", session.getString("TrainingTypeID"));
//                    intent.putExtra("Distance", session.getDouble("Distance"));
//                    intent.putExtra("TimeElapsed", 0);
//                    intent.putExtra("AverageBPM", session.getDouble("AverageBPM"));
//                    intent.putExtra("BurntCalories", session.getDouble("BurntCalories"));
//                    intent.putExtra("ClimateConditionID", session.getString("ClimateConditionID"));
//                    intent.putExtra("Temperature", session.getDouble("Temperature"));
//                    break;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//                Log.e("JSONException", "HistoryActivity.goToResults");
//            }
//        }

        try {
            JSONObject session = historyJSONArray.getJSONObject(index);
            intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("TrainingTypeID", session.getString("TrainingTypeID"));
            intent.putExtra("Distance", session.getDouble("Distance"));
            intent.putExtra("AverageBPM", session.getDouble("AverageBPM"));
            intent.putExtra("BurntCalories", session.getDouble("BurntCalories"));
            intent.putExtra("ClimateConditionID", session.getString("ClimateConditionID"));
            intent.putExtra("Temperature", session.getDouble("Temperature"));
            intent.putExtra("TimeElapsed", getDurationInMillis(session.getString("StartTime"), session.getString("EndTime")));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "HistoryActivity.goToResults");
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    private long getDurationInMillis(String startTimeStr, String finishTimeStr) {

        Calendar startTime = dateStringToCalendar(startTimeStr);
        Calendar finishTime = dateStringToCalendar(finishTimeStr);

        return (finishTime.getTimeInMillis() - startTime.getTimeInMillis());
    }

    private Calendar dateStringToCalendar(String dateStr) {

        if (dateStr.contains(".")) {
            dateStr = dateStr.split(".", 2)[0];
        }
        if (dateStr.contains(" ")) {
            dateStr = dateStr.replace(" ", "T");
        }

        String[] dateTime = dateStr.split("T", 2);
        String[] date = dateTime[0].split("-", 3);
        String[] time = dateTime[1].split(":", 3);

        Calendar calendar = Calendar.getInstance();
        calendar.set(
                Integer.parseInt(date[0]),
                Integer.parseInt(date[1]),
                Integer.parseInt(date[2]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[1]),
                Integer.parseInt(time[2])
        );

        return calendar;
    }

    class FetchHistoryTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            try {
                final String API_URL = "http://speedforceservice.azurewebsites.net/api/training/sessionlist/" + userID;
                URL url = new URL(API_URL);
                Log.d("URL", "Fetch History URL: " + url.toString());
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
            if(response != null) {
                Log.d("JSON", "Fetch History RESPONSE: Is Not Null");
                try {
                    historyJSONArray = new JSONArray(response);
                    populateListView(historyJSONArray);
                    registerClickCallback();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON", "HistoryActivity.FetchHistoryTask.onPostExecute.JSONArrayInit");
                }
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    class LogSessionsTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            Log.d("LogSessionTask", "Starting Background Log Task.");
        }
        protected String doInBackground(Void... urls) {
            DatabaseHelper.getInstance(HistoryActivity.this).logSessions();
            return "Background Session Logging Finished";
        }
        protected void onPostExecute(String msg) {
            Log.d("LogSessionTask", msg);
            Log.d("LogSessionTask", "Ending Log Task.");
        }
    }
}
