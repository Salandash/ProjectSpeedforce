package com.georgewilliam.speedforce.projectspeedforce;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by georgetamate on 6/28/17.
 */
public class StartServiceReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    //public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent triggerIntent = new Intent(context, SyncService.class);
        triggerIntent.putExtra("Username", intent.getExtras().getString("Username"));
        context.startService(triggerIntent);
        Log.d("StartServiceReceiver", "Called context.startService from StartServiceReceiver.onReceive");
    }

//    try {
//        final String userID = intent.getExtras().getString("Username");
//    } catch (Exception e) {
//        Log.i("ERROR", "Could not get username from Intent Extras Bundle.");
//        //Log.e("ERROR", e.getMessage(), e);
//    }
}
