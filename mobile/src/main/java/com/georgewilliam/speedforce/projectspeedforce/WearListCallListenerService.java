package com.georgewilliam.speedforce.projectspeedforce;

import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by georgetamate on 3/19/17.
 */
public class WearListCallListenerService extends WearableListenerService {

    public static String SERVICE_CALLED_WEAR = "WearListClicked";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        String event = messageEvent.getPath();

        Log.d("Listclicked", event);

        String [] message = event.split("--");

        if (message[0].equals(SERVICE_CALLED_WEAR)) {
            Intent intent = new Intent(this, RegisterActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

//            startActivity(new Intent((Intent) MainActivity.getInstance()
//                    .tutorials.get(message[1]))
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
