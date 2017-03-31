package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by georgetamate on 3/22/17.
 */
public class WearMessageListenerService extends WearableListenerService {

    private static final String START_ACTIVITY = "/start_activity";
    private static final String END_ACTIVITY = "/end_activity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("SERVICE LISTENER", "Message Received, path: " + messageEvent.getPath());
        Log.d("SERVICE LISTENER", "Message Received, data: " + messageEvent.getData().toString());
        if( messageEvent.getPath().equalsIgnoreCase( START_ACTIVITY ) ) {
            Log.d("SERVICE LISTENER", "Start Activity Intent");
            Intent intent = new Intent( this, MainActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity( intent );
        } else if( messageEvent.getPath().equalsIgnoreCase( END_ACTIVITY ) ) {
            Log.d("SERVICE LISTENER", "End Activity Intent");
            Intent intent = new Intent( this, StopActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity( intent );
        } else {
            Log.d("SERVICE LISTENER", "Called Super");
            super.onMessageReceived( messageEvent );
        }


    }
}