package com.georgewilliam.speedforce.projectspeedforce;

import android.app.Application;

import java.util.Date;

/**
 * Created by georgetamate on 4/1/17.
 */
public class SpeedforceApplication extends Application {

    private static final int EXPIRATION_MILLISECS = 10000;
    private double averageBPM = 0;
    private boolean changed = false;
    private Date lastChanged = new Date();

    public double getAverageBPM() {
        changed = false;
        return averageBPM;
    }

    public void setAverageBPM(double bpm) {
        averageBPM = bpm;
        lastChanged = new Date();
        changed = true;
    }

    public boolean isChanged() {
        return changed;
    }

    public boolean isExpired(Date stamp) {
        return (stamp.getTime() - lastChanged.getTime() >= EXPIRATION_MILLISECS);
    }

}
