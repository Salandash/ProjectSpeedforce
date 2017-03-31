package com.georgewilliam.speedforce.projectspeedforce;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.renderscript.Double2;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener {

    private static final String WEAR_MESSAGE_PATH = "/bpm_data";
    //private static final int TYPE_PASSIVE_WELLNESS = 65538;

    private TextView mTextView;
    private Button mButton;
    private Chronometer mChronometer;

    private boolean operating = false;
    private double averageBPM = 0;
    private int bpmCounter = 0;

    //Sensor and SensorManager
    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Keep the Wear screen always on (for testing only!)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGoogleApiClient();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.wText);
                mChronometer = (Chronometer) findViewById(R.id.wTimer);
                mButton = (Button) stub.findViewById(R.id.wButton);

                mChronometer.setText("00:00:00");

                mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    public void onChronometerTick(Chronometer c) {
                        int cTextSize = c.getText().length();
                        String cText = c.getText().toString();
                        if (cTextSize == 5) {
                            cText = "00:" + cText;
                        } else if (cTextSize == 7) {
                            cText = "0" + cText;
                        }
                        mChronometer.setText(cText);
                    }
                });

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("WEAR", "OnClick");
                        if (!operating) {
                            //START
                            startSession();
                        } else {
                            //STOP
                            endSession();
                        }
                    }
                });

                Log.d("ACTIVITY", "End of OnLayoutInflatedListener/OnCreate");
            }
        });

    }

    @Override
    protected void onStart() {
        Log.d("WEAR", "onStart");
        super.onStart();
        mApiClient.connect();
        startSession();
    }

    @Override
    protected void onResume() {
        Log.d("WEAR", "OnResume");
        super.onResume();
        //Register the listener
        if (mSensorManager != null) {
            Log.d("SENSOR", "Sensor Manager NOT NULL!");
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        Log.d("WEAR", "OnPause");
        Toast.makeText(this, Double.toString(averageBPM), Toast.LENGTH_SHORT).show();//TODO
        sendMessage(WEAR_MESSAGE_PATH, Double.toString(averageBPM));
        super.onPause();
        //Unregister the listener
        if (mSensorManager!=null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        Log.d("WEAR", "onStop");
        mApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("WEAR", "OnDestroy");
        super.onDestroy();
        mApiClient.disconnect();
    }

    private void startSession() {
        averageBPM = 0;
        bpmCounter = 0;
        operating = true;
        mButton.setText("Stop");
        mChronometer.setBase(SystemClock.elapsedRealtime());
        activateSensor();
        mChronometer.start();
        mTextView.setText("bpm: Loading...");
    }

    private void endSession() {
        operating = false;
        mButton.setText("Start");
        deactivateSensor();
        mChronometer.stop();
        mTextView.setText("bpm: OFF");
    }

    private void activateSensor() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (mSensorManager != null){
            Log.d("SENSOR", "Sensor Manager NOT NULL! - Register Listener");
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
//            List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//            for (Sensor sensor1 : sensors) {
//                Log.i("SENSOR LIST", sensor1.getName() + ": " + sensor1.getType());
//            }
        }
    }

    private void deactivateSensor() {
        //Unregister the listener
        if (mSensorManager!=null)
            mSensorManager.unregisterListener(this);
    }

    private void initGoogleApiClient() {
        Log.d("WEAR", "Init GoogleApiClient");
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("WEAR", "OnConnected");
        Wearable.MessageApi.addListener( mApiClient, this );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("WEAR", "OnConnectionSuspended");
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        Log.d("WEAR", "OnMessageReceived");

        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH ) ) {
                    mTextView.setText(new String( messageEvent.getData() ));
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("WEAR", "OnSensorChanged");
        String bpm;
        //Update your data. This check is very raw. You should improve it when the sensor is unable to calculate the heart rate
        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            if ((int)sensorEvent.values[0]>0) {
                //mCircledImageView.setCircleColor(getResources().getColor(R.color.green));
                ++bpmCounter;
                averageBPM += (sensorEvent.values[0] - averageBPM) / bpmCounter;
                bpm = "bpm: " + (int) sensorEvent.values[0];
                mTextView.setText(bpm);
                Log.d("BPM", bpm);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d("WEAR", "OnAccChanged: " + Integer.toString(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("WEAR", "onConnectionFailed");
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
                        //extra action
                    }
                });
            }
        }).start();
    }
}
