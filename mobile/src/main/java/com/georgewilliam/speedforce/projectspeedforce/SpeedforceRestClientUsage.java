package com.georgewilliam.speedforce.projectspeedforce;

import com.loopj.android.http.*;
import org.json.*;

import cz.msebera.android.httpclient.entity.mime.Header;


/**
 * Created by georgetamate on 11/8/16.
 */
public class SpeedforceRestClientUsage {

    public void getPublicTimeline() throws JSONException {
        SpeedforceRestClient.get("statuses/public_timeline.json", null, new JsonHttpResponseHandler() {
           // @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }

            //@Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) throws JSONException {
                // Pull out the first event on the public timeline
                JSONObject firstEvent = (JSONObject) timeline.get(0);
                String tweetText = firstEvent.getString("text");

                // Do something with the response
                System.out.println(tweetText);
            }
        });
    }
}
