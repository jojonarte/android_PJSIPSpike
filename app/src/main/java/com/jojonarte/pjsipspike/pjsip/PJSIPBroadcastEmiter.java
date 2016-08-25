package com.jojonarte.pjsipspike.pjsip;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by dna on 23/08/2016.
 */

public class PJSIPBroadcastEmiter {
    private static String TAG = "PjSipBroadcastEmiter";

    private Context context;

    public PJSIPBroadcastEmiter(Context context) {
        this.context = context;
    }

    public void fireStarted(Intent original, List<PJSIPAccount> accounts, List<PJSIPCall> calls) {
        Log.d(TAG, "fireStarted");

        try {
            JSONArray dataAccounts = new JSONArray();
            for (PJSIPAccount account : accounts) {
                dataAccounts.put(account.toJson());
            }

            JSONArray dataCalls = new JSONArray();
            for (PJSIPCall call : calls) {
                dataCalls.put(call.toJson());
            }

            JSONObject data = new JSONObject();
            data.put("accounts", dataAccounts);
            data.put("calls", dataCalls);

            Intent intent = new Intent();
            intent.setAction(PJActions.EVENT_STARTED);
            intent.putExtra("callback_id", original.getIntExtra("callback_id", -1));
            intent.putExtra("data", data.toString());

            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send ACCOUNT_CREATED event", e);
        }
    }


    public void fireIntentHandled(Intent original) {
        Intent intent = new Intent();
        intent.setAction(PJActions.EVENT_HANDLED);
        intent.putExtra("callback_id", original.getIntExtra("callback_id", -1));

        context.sendBroadcast(intent);
    }

    public void fireIntentHandled(Intent original, Exception e) {
        Intent intent = new Intent();
        intent.setAction(PJActions.EVENT_HANDLED);
        intent.putExtra("callback_id", original.getIntExtra("callback_id", -1));
        intent.putExtra("exception", e.getMessage());

        context.sendBroadcast(intent);
    }

    public void fireAccountCreated(Intent original, PJSIPAccount account) {
        Intent intent = new Intent();
        intent.setAction(PJActions.EVENT_ACCOUNT_CREATED);
        intent.putExtra("callback_id", original.getIntExtra("callback_id", -1));
        intent.putExtra("data", account.toJsonString());

        context.sendBroadcast(intent);
    }

    public void fireRegistrationChangeEvent(PJSIPAccount account) {
        Intent intent = new Intent();
        intent.setAction(PJActions.EVENT_REGISTRATION_CHANGED);
        intent.putExtra("data", account.toJsonString());

        context.sendBroadcast(intent);
    }

    public void fireCallCreated(Intent original, PJSIPCall call) {
        Intent intent = new Intent();
        intent.setAction(PJActions.EVENT_CALL_CREATED);
        intent.putExtra("callback_id", original.getIntExtra("callback_id", -1));
        intent.putExtra("data", call.toJsonString());

        context.sendBroadcast(intent);
    }

    public void fireCallReceivedEvent(PJSIPCall call) {
        Intent intent = new Intent();
        intent.setAction(PJActions.EVENT_CALL_RECEIVED);
        intent.putExtra("data", call.toJsonString());

        context.sendBroadcast(intent);
    }

    public void fireCallChanged(PJSIPCall call) {
        Intent intent = new Intent();
        intent.setAction(PJActions.EVENT_CALL_CHANGED);
        intent.putExtra("data", call.toJsonString());

        context.sendBroadcast(intent);
    }

    public void fireCallTerminated(PJSIPCall call) {
        Intent intent = new Intent();
        intent.setAction(PJActions.EVENT_CALL_TERMINATED);
        intent.putExtra("data", call.toJsonString());

        context.sendBroadcast(intent);
    }


}
