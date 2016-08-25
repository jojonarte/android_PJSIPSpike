package com.jojonarte.pjsipspike.pjsip;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


import java.util.HashMap;

public class PJSIPBroadcastReceiver extends BroadcastReceiver {
    private static String TAG = "PJSIPBroadcastReceiver";

    private  Context context;

    private int seq = 0;

    private HashMap<Integer, String> callbacks = new HashMap<>();

    public PJSIPBroadcastReceiver(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(PJActions.EVENT_STARTED);
        filter.addAction(PJActions.EVENT_ACCOUNT_CREATED);
        filter.addAction(PJActions.EVENT_REGISTRATION_CHANGED);
        filter.addAction(PJActions.EVENT_CALL_RECEIVED);
        filter.addAction(PJActions.EVENT_CALL_CREATED);
        filter.addAction(PJActions.EVENT_CALL_CHANGED);
        filter.addAction(PJActions.EVENT_CALL_TERMINATED);
        filter.addAction(PJActions.EVENT_HANDLED);

        return filter;
    }

    public int register(String callback) {
        int id = ++seq;
        callbacks.put(id, callback);
        return id;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());

        String action = intent.getAction();

        switch (action) {
            case PJActions.EVENT_STARTED:
                onCallback(intent);
                break;
            case PJActions.EVENT_ACCOUNT_CREATED:
                onCallback(intent);
                break;
            case PJActions.EVENT_REGISTRATION_CHANGED:
                onRegistrationChanged(intent);
                break;
            case PJActions.EVENT_CALL_RECEIVED:
                onCallReceived(intent);
                break;
            case PJActions.EVENT_CALL_CHANGED:
                onCallChanged(intent);
                break;
            case PJActions.EVENT_CALL_TERMINATED:
                onCallTerminated(intent);
                break;
            default:
                onCallback(intent);
                break;
        }
    }

    private void onRegistrationChanged(Intent intent) {
//        String json = intent.getStringExtra("data");
//        Object params = ArgumentUtils.fromJson(json);
//        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("pjSipRegistrationChanged", params);
    }

    private void onCallReceived(Intent intent) {
//        String json = intent.getStringExtra("data");
//        Object params = ArgumentUtils.fromJson(json);
//        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("pjSipCallReceived", params);
    }

    private void onCallChanged(Intent intent) {
//        String json = intent.getStringExtra("data");
//        Object params = ArgumentUtils.fromJson(json);
//        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("pjSipCallChanged", params);
    }

    private void onCallTerminated(Intent intent) {
//        String json = intent.getStringExtra("data");
//        Object params = ArgumentUtils.fromJson(json);
//        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("pjSipCallTerminated", params);
    }

    private void onCallback(Intent intent) {
        // Define callback
//        Callback callback = null;
//
//        if (intent.hasExtra("callback_id")) {
//            int id = intent.getIntExtra("callback_id", -1);
//            if (callbacks.containsKey(id)) {
//                callback = callbacks.remove(id);
//            } else {
//                Log.w(TAG, "Callback with \""+ id +"\" identifier not found (\""+ intent.getAction() +"\")");
//            }
//        }
//
//        if (callback == null) {
//            return;
//        }
//
//        // -----
//        if (intent.hasExtra("exception")) {
//            Log.w(TAG, "Callback executed with exception state: " + intent.getStringExtra("exception"));
//            callback.invoke(false, intent.getStringExtra("exception"));
//        } else if (intent.hasExtra("data")) {
//            Object params = ArgumentUtils.fromJson(intent.getStringExtra("data"));
//            callback.invoke(true, params);
//        } else {
//            callback.invoke(true, true);
//        }
    }
}
