package com.jojonarte.pjsipspike.pjsip;

import android.util.Log;

import org.json.JSONObject;
import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnIncomingSubscribeParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnInstantMessageStatusParam;
import org.pjsip.pjsua2.OnMwiInfoParam;
import org.pjsip.pjsua2.OnRegStartedParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.OnTypingIndicationParam;

/**
 * Created by dna on 23/08/2016.
 */

public class PJSIPAccount extends Account {

    private static String TAG = "PJSIPAccount";

    /*
    *  Last registration reason.
    * */

    private String reason;

    private PJSIPService service;

    private int transportId;

    public PJSIPAccount(PJSIPService service, int transportId) {
        this.service = service;
        this.transportId = transportId;
    }

    public PJSIPService getService() {
        return service;
    }

    public int getTransportId() {
        return transportId;
    }

    @Override
    public void onRegStarted(OnRegStartedParam prm) {
//        super.onRegStarted(prm);
        Log.d(TAG, "onRegStarted: "+ prm.getRenew());
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        reason = prm.getReason();
        service.getEmitter().fireRegistrationChangeEvent(this);
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        Log.d(TAG, "onIncomingCall");
        Log.d(TAG, "onIncomingCall getWholeMsg: " + prm.getRdata().getWholeMsg());
        Log.d(TAG, "onIncomingCall getInfo: " + prm.getRdata().getInfo());
        Log.d(TAG, "onIncomingCall getSrcAddress: " + prm.getRdata().getSrcAddress());

        PJSIPCall call = new PJSIPCall(this, prm.getCallId());

        service.getEmitter().fireCallReceivedEvent(call);
    }

    @Override
    public void onIncomingSubscribe(OnIncomingSubscribeParam prm) {
        super.onIncomingSubscribe(prm);
        Log.d(TAG, "onIncomingSubscribe");
    }

    @Override
    public void onInstantMessage(OnInstantMessageParam prm) {
        super.onInstantMessage(prm);
        Log.d(TAG, "onInstantMessage");
    }

    @Override
    public void onInstantMessageStatus(OnInstantMessageStatusParam prm) {
        super.onInstantMessageStatus(prm);
        Log.d(TAG, "onInstantMessageStatus");
    }

    @Override
    public void onTypingIndication(OnTypingIndicationParam prm) {
        super.onTypingIndication(prm);
        Log.d(TAG, "onTypingIndication");
    }

    @Override
    public void onMwiInfo(OnMwiInfoParam prm) {
        super.onMwiInfo(prm);
        Log.d(TAG, "onMwiInfo");
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            JSONObject registration = new JSONObject();
            registration.put("status", getInfo().getRegStatus());
            registration.put("statusText", getInfo().getRegStatusText());
            registration.put("active", getInfo().getRegIsActive());
            registration.put("reason", reason);

            json.put("id", getId());
            json.put("uri", getInfo().getUri());
            json.put("registration", registration);

            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toJsonString() {
        return toJson().toString();
    }

}
