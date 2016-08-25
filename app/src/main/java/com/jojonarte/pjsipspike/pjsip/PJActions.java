package com.jojonarte.pjsipspike.pjsip;

import android.content.Context;
import android.content.Intent;

/**
 * Created by dna on 23/08/2016.
 */

public class PJActions {
    public static final String TEST = "test";

    public static final String ACTION_START = "start";
    public static final String ACTION_CREATE_ACCOUNT = "account_create";
    public static final String ACTION_DELETE_ACCOUNT = "account_delete";
    public static final String ACTION_MAKE_CALL = "call_make";
    public static final String ACTION_HANGUP_CALL = "call_hangup";
    public static final String ACTION_ANSWER_CALL = "call_answer";
    public static final String ACTION_HOLD_CALL = "call_hold";
    public static final String ACTION_UNHOLD_CALL = "call_unhold";
    public static final String ACTION_XFER_CALL = "call_xfer";
    public static final String ACTION_DTMF_CALL = "call_dtmf";

    public static final String EVENT_STARTED = "com.jojonarte.account.started";
    public static final String EVENT_ACCOUNT_CREATED = "com.jojonarte.account.created";
    public static final String EVENT_REGISTRATION_CHANGED = "com.jojonarte.registration.changed";
    public static final String EVENT_CALL_CREATED = "com.jojonarte.call.created";
    public static final String EVENT_CALL_CHANGED = "com.jojonarte.call.changed";
    public static final String EVENT_CALL_TERMINATED = "com.jojonarte.call.terminated";
    public static final String EVENT_CALL_RECEIVED = "com.jojonarte.call.received";
    public static final String EVENT_HANDLED = "com.jojonarte.handled";

    public static Intent createAccountCreateIntent(int callbackId, Intent configuration, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_CREATE_ACCOUNT);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("username", configuration.getExtras().getString("username"));
        intent.putExtra("password", configuration.getExtras().getString("password"));
        intent.putExtra("host", configuration.getExtras().getString("host"));
        intent.putExtra("realm", configuration.getExtras().getString("realm"));

        if (configuration.hasExtra("port")) {
            intent.putExtra("port", configuration.getExtras().getInt("port"));
        }
        if (configuration.hasExtra("transport")) {
            intent.putExtra("transport", configuration.getExtras().getString("transport"));
        }

        return intent;
    }

    public static Intent createAccountDeleteIntent(int callbackId, int accountId, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_DELETE_ACCOUNT);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("account_id", accountId);

        return intent;
    }

    public static Intent createMakeCallIntent(int callbackId, int accountId, String destination, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_MAKE_CALL);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("account_id", accountId);
        intent.putExtra("destination", destination);

        return intent;
    }

    public static Intent createHangupCallIntent(int callbackId, int callId, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_HANGUP_CALL);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("call_id", callId);

        return intent;
    }

    public static Intent createAnswerCallIntent(int callbackId, int callId, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_ANSWER_CALL);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("call_id", callId);

        return intent;
    }

    public static Intent createHoldCallIntent(int callbackId, int callId, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_HOLD_CALL);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("call_id", callId);

        return intent;
    }

    public static Intent createUnholdCallIntent(int callbackId, int callId, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_UNHOLD_CALL);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("call_id", callId);

        return intent;
    }

    public static Intent createXFerCallIntent(int callbackId, int callId, String destination, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_XFER_CALL);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("call_id", callId);
        intent.putExtra("destination", destination);

        return intent;
    }

    public static Intent createDtmfCallIntent(int callbackId, int callId, String digits, Context context) {
        Intent intent = new Intent(context, PJSIPService.class);
        intent.setAction(PJActions.ACTION_DTMF_CALL);
        intent.putExtra("callback_id", callbackId);
        intent.putExtra("call_id", callId);
        intent.putExtra("digits", digits);

        return intent;
    }



}
