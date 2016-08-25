package com.jojonarte.pjsipspike;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import android.os.Process;

import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;

/**
 * Created by dna on 23/08/2016.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";
    private Handler mHandler;
    private HandlerThread mWorkerThread;

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

    }

    @Override
    public void onCreate() {


        mWorkerThread = new HandlerThread(getClass().getSimpleName(), Process.THREAD_PRIORITY_FOREGROUND);
        mWorkerThread.setPriority(Thread.MAX_PRIORITY);
        mWorkerThread.start();
        mHandler = new Handler(mWorkerThread.getLooper());

        job(new Runnable() {
            @Override
            public void run() {
                load();

            }
        });
    }

    private void job(Runnable job) {
        mHandler.post(job);
    }
}
