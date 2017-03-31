package com.georgewilliam.speedforce.projectspeedforce;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

public class ApiActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String START_ACTIVITY = "/start_activity";
    private static final String END_ACTIVITY = "/end_activity";
    private static final String WEAR_MESSAGE_PATH = "/message";
    GoogleApiClient mApiClient;

    EditText emailText;
    TextView responseView;
    ProgressBar progressBar;
    //static final String API_KEY = "USE_YOUR_OWN_API_KEY";
    static final String API_URL = "http://45.55.77.201:8085/movies/json";
    //static final String API_URL = "https://api.fullcontact.com/v2/person.json?";

    //for tenting purposes
//    int p1;
//    int p2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ACTIVITY", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

//        Bundle extras = getIntent().getExtras();
//        p1 = extras.getInt("param1");
//        p2 = extras.getInt("param2");

        responseView = (TextView) findViewById(R.id.responseView);
        emailText = (EditText) findViewById(R.id.emailText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new RetrieveFeedTask().execute();
//                toastInt();

//                Collection<String> nodes = (Collection<String>) Wearable.NodeApi.getConnectedNodes( mApiClient );
//
//                //For each node - send the message across
//                for (String node : nodes) {
//                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
//                            mApiClient, node, START_ACTIVITY , emailText.getText().toString().getBytes()).await();
//                    if (!result.getStatus().isSuccess()) {
//                        Log.d("MESSAGE", node + ": Msg send success!");
//                    }
//                }

                String text = emailText.getText().toString();
                if ( !TextUtils.isEmpty( text ) ) {
                    responseView.setText(text);
                    if (text.equals("e")) {
                        sendMessage(END_ACTIVITY, text);
                    } else {
                        sendMessage( START_ACTIVITY, text );
                    }

                }
            }
        });

        initGoogleApiClient();
        Log.d("ACTIVITY", "onCreate End");
    }

    public void toastInt() {
//        Toast.makeText(this, "INT params are: " + Integer.toString(p1)+Integer.toString(p2), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("ACTIVITY", "onConnected");
        //sendMessage( START_ACTIVITY, "" );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("ACTIVITY", "onConnectionSuspended");
    }

    private void initGoogleApiClient() {
        Log.d("API CLIENT", "Init");
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        Log.d("ACTIVITY", "onStart");
        super.onStart();
        mApiClient.connect();
    }

    @Override
    protected void onStop() {
        Log.d("ACTIVITY", "onStop");
        mApiClient.disconnect();
        super.onStop();
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                Log.d("MESSAGE", "Getting Nodes");
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    Log.d("MESSAGE", node + ": sending Msg");
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    Log.d("MESSAGE", node + ": Msg sent");
                    if (!result.getStatus().isSuccess()) {
                        Log.d("MESSAGE", node + ": Msg send success!");
                    }
                }

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        emailText.setText( "" );
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        Log.d("ACTIVITY", "onDestroy");
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("ACTIVITY", "onConnectionFailed");

    }


    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////


    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            //String email = emailText.getText().toString();
            // Do some validation here

            try {
                //URL url = new URL(API_URL + "email=" + email + "&apiKey=" + API_KEY);
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            //responseView.setText(response);
            // TODO: check this.exception
            // TODO: do something with the feed

            try {
                String moviesString = "";
                JSONArray movies = new JSONArray(response);
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject movie = movies.getJSONObject(i);

                    moviesString += Integer.toString(i+1) + ".\n";
                    moviesString += "NAME: " + movie.getString("name") + "\n";
                    moviesString += "IMAGE: " + movie.getString("image") + "\n\n";
                }
                responseView.setText(moviesString);
                //JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                //String requestID = object.getString("requestId");
                //int likelihood = object.getInt("likelihood");
                //JSONArray photos = object.getJSONArray("photos");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
