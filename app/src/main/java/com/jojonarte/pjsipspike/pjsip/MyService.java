package com.jojonarte.pjsipspike.pjsip;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;

/**
 * Created by dna on 23/08/2016.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void load() {
        // Load native libraries
        try {
            System.loadLibrary("openh264");
        } catch (UnsatisfiedLinkError error) {
            Log.e(TAG, "Error while loading OpenH264 native library", error);
            throw new RuntimeException(error);
        }

        try {
            System.loadLibrary("yuv");
        } catch (UnsatisfiedLinkError error) {
            Log.e(TAG, "Error while loading libyuv native library", error);
            throw new RuntimeException(error);
        }

        try {
            System.loadLibrary("pjsua2");
        } catch (UnsatisfiedLinkError error) {
            Log.e(TAG, "Error while loading PJSIP pjsua2 native library", error);
            throw new RuntimeException(error);
        }

        // Start stack
//        try {
//            mEndpoint = new Endpoint();
//            mEndpoint.libCreate();
//            mEndpoint.libRegisterThread(Thread.currentThread().getName());
//
//            EpConfig epConfig = new EpConfig();
//
//            epConfig.getLogConfig().setLevel(4);
//            epConfig.getLogConfig().setConsoleLevel(4);
//
//            mLogWriter = new PJSIPLogWriter();
//            epConfig.getLogConfig().setWriter(mLogWriter);
//
//            // epConfig.getUaConfig().setUserAgent("");
//            epConfig.getMedConfig().setHasIoqueue(true);
//            epConfig.getMedConfig().setClockRate(8000);
//            epConfig.getMedConfig().setQuality(4);
//            epConfig.getMedConfig().setEcOptions(1);
//            epConfig.getMedConfig().setEcTailLen(200);
//            epConfig.getMedConfig().setThreadCnt(2);
//            mEndpoint.libInit(epConfig);
//
//            mTrash.add(epConfig);
//
//            mEndpoint.libStart();
//        } catch (Exception e) {
//            Log.e(TAG, "Error while starting PJSIP", e);
//        }
    }
}
