package com.jojonarte.pjsipspike;

import net.hockeyapp.android.CrashManagerListener;

public class MyCustomCrashManagerListener extends CrashManagerListener {
    @Override
    public boolean shouldAutoUploadCrashes() {
        return true;
    }
}