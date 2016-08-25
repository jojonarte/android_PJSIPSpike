package com.jojonarte.pjsipspike.pjsip;

import android.util.Log;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;

/**
 * Created by dna on 23/08/2016.
 */

public class PJSIPLogWriter extends LogWriter {

    private static String TAG = "PJSIPLogWriter";

    public void write(LogEntry entry) {
        Log.d(TAG, entry.getMsg());
    }
}
