package com.jojonarte.pjsipspike.pjsip;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class PJSIPModule {

    private static final String TAG = "PJSIPModule";

    private static PJSIPBroadcastReceiver receiver;

    private Context context;

    public PJSIPModule() {    }

    public PJSIPModule(Context context) {
        this.context = context;

        Log.d(TAG, "PJSIPModule init");

        //module can be started a lot of times but we should only register the receiver once on first try
        if (receiver == null) {
            receiver = new PJSIPBroadcastReceiver(context);
        }

    }

    public void start(String callback) {
        Log.d(TAG, "Start intent");

        Intent intent = new Intent(this.context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_START);
        intent.putExtra("callback_id", receiver.register(callback));

        this.context.startService(intent);
    }

    public void createAccount(Intent configuration, String callback) {
        Log.d(TAG, "Start creating account");

        int id = receiver.register(callback);
        Intent intent = PJActions.createAccountCreateIntent(id, configuration, this.context);
        this.context.startService(intent);
    }

}
