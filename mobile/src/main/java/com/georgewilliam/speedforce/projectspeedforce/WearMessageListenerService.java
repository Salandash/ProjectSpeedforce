package com.georgewilliam.speedforce.projectspeedforce;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.Charset;

/**
 * Created by georgetamate on 3/31/17.
 */
public class WearMessageListenerService extends WearableListenerService {

    private static final String WEAR_MESSAGE_PATH = "/bpm_data";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("SERVICE LISTENER", "Message Received, path: " + messageEvent.getPath());
        String msg = new String(messageEvent.getData(), Charset.forName("UTF-8"));
        Log.d("SERVICE LISTENER", "Message Received, data: " + msg);
        if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH ) ) {
//            Intent intent = new Intent( this, LoginActivity.class );
//            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//            startActivity( intent );
            try {
                ((SpeedforceApplication) this.getApplication()).setAverageBPM(Double.parseDouble(msg));
            } catch (NumberFormatException e) {
                Log.e("SERVICE LISTENER", "Error parsing BPM: " + msg);
            }
        } else {
            Log.d("SERVICE LISTENER", "Called Super");
            super.onMessageReceived( messageEvent );
        }
    }
}