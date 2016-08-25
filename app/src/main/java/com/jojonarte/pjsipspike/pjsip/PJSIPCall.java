package com.jojonarte.pjsipspike.pjsip;

import android.util.Log;

import org.json.JSONObject;
import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaEventParam;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallMediaTransportStateParam;
import org.pjsip.pjsua2.OnCallRedirectedParam;
import org.pjsip.pjsua2.OnCallReplaceRequestParam;
import org.pjsip.pjsua2.OnCallReplacedParam;
import org.pjsip.pjsua2.OnCallRxOfferParam;
import org.pjsip.pjsua2.OnCallSdpCreatedParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.OnCallTransferRequestParam;
import org.pjsip.pjsua2.OnCallTransferStatusParam;
import org.pjsip.pjsua2.OnCallTsxStateParam;
import org.pjsip.pjsua2.OnCreateMediaTransportParam;
import org.pjsip.pjsua2.OnDtmfDigitParam;
import org.pjsip.pjsua2.OnStreamCreatedParam;
import org.pjsip.pjsua2.OnStreamDestroyedParam;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_redirect_op;
import org.pjsip.pjsua2.pjsua_call_flag;
import org.pjsip.pjsua2.pjsua_call_media_status;

/**
 * Created by dna on 23/08/2016.
 */

public class PJSIPCall  extends Call{
    private static String TAG = "PJSIPCall";

    private PJSIPAccount account;

    public PJSIPCall(PJSIPAccount acc, int call_id) {
        super(acc, call_id);
        this.account = acc;
    }

    public PJSIPCall(PJSIPAccount acc) {
        super(acc);
        this.account = acc;
    }

    public PJSIPService getService() {
        return account.getService();
    }

    public void putOnHold() throws Exception {
        setHold(new CallOpParam(true));
    }

    public void releaseFromHold() throws Exception {
        CallOpParam prm = new CallOpParam();
        prm.setOptions(pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue());

//        CallSetting opt = param.getOpt();
// opt.setFlag(pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue());  

        reinvite(prm);
    }

    @Override
    public void onCallState(OnCallStateParam prm) {
        try {
            if (getInfo().getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                getService().getEmitter().fireCallTerminated(this);
                getService().evict(this);
            } else {
                getService().getEmitter().fireCallChanged(this);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to handle call state event", e);
        }
    }

    @Override
    public void onCallTsxState(OnCallTsxStateParam prm) {
        Log.d(TAG, "onCallTsxState");

        super.onCallTsxState(prm);
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {

        CallInfo info;
        try {
            info = getInfo();
        } catch (Exception exc) {
            Log.w(TAG, "Error while getting call info", exc);
            return;
        }

        for (int i = 0; i < info.getMedia().size(); i++) {
            Media media = getMedia(i);
            CallMediaInfo mediaInfo = info.getMedia().get(i);

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                    && media != null
                    && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
                AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

                // connect the call audio media to sound device
                try {
                    AudDevManager mgr = account.getService().getAudDevManager();

                    try {
                        audioMedia.adjustRxLevel((float) 1.5);
                        audioMedia.adjustTxLevel((float) 1.5);
                    } catch (Exception exc) {
                        Log.e(TAG, "Error while adjusting levels", exc);
                    }

                    audioMedia.startTransmit(mgr.getPlaybackDevMedia());
                    mgr.getCaptureDevMedia().startTransmit(audioMedia);
                } catch (Exception exc) {
                    Log.e(TAG, "Error while connecting audio media to sound device", exc);
                }
            }

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO) {
                Log.d(TAG, "Media video getIndex: " + mediaInfo.getIndex());
                Log.d(TAG, "Media video getDir: " + mediaInfo.getDir());
                Log.d(TAG, "Media video getType: " + mediaInfo.getType());
                Log.d(TAG, "Media video getStatus: " + mediaInfo.getStatus());
                Log.d(TAG, "Media video getVideoIncomingWindowId: " + mediaInfo.getVideoIncomingWindowId());
                Log.d(TAG, "Media video getVideoCapDev: " + mediaInfo.getVideoCapDev());


            }
        }
    }

    @Override
    public void onCallSdpCreated(OnCallSdpCreatedParam prm) {
        Log.d(TAG, "onCallSdpCreated");

        super.onCallSdpCreated(prm);
    }

    @Override
    public void onStreamCreated(OnStreamCreatedParam prm) {
        Log.d(TAG, "onStreamCreated");

        super.onStreamCreated(prm);
    }

    @Override
    public void onStreamDestroyed(OnStreamDestroyedParam prm) {
        Log.d(TAG, "onStreamDestroyed");

        super.onStreamDestroyed(prm);
    }

    @Override
    public void onDtmfDigit(OnDtmfDigitParam prm) {
        Log.d(TAG, "onDtmfDigit");

        super.onDtmfDigit(prm);
    }

    @Override
    public void onCallTransferRequest(OnCallTransferRequestParam prm) {
        Log.d(TAG, "onCallTransferRequest");

        super.onCallTransferRequest(prm);
    }

    @Override
    public void onCallTransferStatus(OnCallTransferStatusParam prm) {
        Log.d(TAG, "onCallTransferStatus");

        super.onCallTransferStatus(prm);
    }

    @Override
    public void onCallReplaceRequest(OnCallReplaceRequestParam prm) {
        Log.d(TAG, "onCallReplaceRequest");

        super.onCallReplaceRequest(prm);
    }

    @Override
    public void onCallReplaced(OnCallReplacedParam prm) {
        Log.d(TAG, "onCallReplaced");

        super.onCallReplaced(prm);
    }

    @Override
    public void onCallRxOffer(OnCallRxOfferParam prm) {
        Log.d(TAG, "onCallRxOffer");

        super.onCallRxOffer(prm);
    }

    @Override
    public pjsip_redirect_op onCallRedirected(OnCallRedirectedParam prm) {
        Log.d(TAG, "onCallRedirected");

        return super.onCallRedirected(prm);
    }

    @Override
    public void onCallMediaTransportState(OnCallMediaTransportStateParam prm) {
        Log.d(TAG, "onCallMediaTransportState");

        super.onCallMediaTransportState(prm);
    }

    @Override
    public void onCallMediaEvent(OnCallMediaEventParam prm) {
        Log.d(TAG, "onCallMediaEvent");

        super.onCallMediaEvent(prm);
    }

    @Override
    public void onCreateMediaTransport(OnCreateMediaTransportParam prm) {
        Log.d(TAG, "onCreateMediaTransport");

        super.onCreateMediaTransport(prm);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", getId());
            json.put("callId", getInfo().getCallIdString());
            json.put("accountId", account.getId());

            // -----
            json.put("localContact", getInfo().getLocalContact());
            json.put("localUri", getInfo().getLocalUri());
            json.put("remoteContact", getInfo().getRemoteContact());
            json.put("remoteUri", getInfo().getRemoteUri());

            // -----
            json.put("state", getInfo().getState());
            json.put("stateText", getInfo().getStateText());
            json.put("connectDuration", getInfo().getConnectDuration().getSec());
            json.put("totalDuration", getInfo().getTotalDuration().getSec());

            /**
             try {
             info.put("lastStatusCode", getInfo().getLastStatusCode());
             } catch (Exception e) {
             info.put("lastStatusCode", null);
             }
             info.put("lastReason", getInfo().getLastReason());
             */

            // -----
            json.put("remoteOfferer", getInfo().getRemOfferer());
            json.put("remoteAudioCount", getInfo().getRemAudioCount());
            json.put("remoteVideoCount", getInfo().getRemVideoCount());

            // -----
            json.put("audioCount", getInfo().getSetting().getAudioCount());
            json.put("videoCount", getInfo().getSetting().getVideoCount());

            // ... getMedia
            // ... ... getIndex
            // ... ... getType
            // ... ... getDir
            // ... ... getStatus
            // ... ... getAudioConfSlot
            // ... ... getVideoIncomingWindowId
            // ... ... getVideoCapDev
            // ... getProvMedia
            // ... ... getIndex
            // ... ... getType
            // ... ... getDir
            // ... ... getStatus
            // ... ... getAudioConfSlot
            // ... ... getVideoIncomingWindowId
            // ... ... getVideoCapDev

            // getMedia
            // getMedTransportInfo

            // getStreamInfo (med_idx)
            // ... getType
            // ... getProto
            // ... getDir
            // ... getRemoteRtpAddress
            // ... getRemoteRtcpAddress
            // ... getTxPt
            // ... getRxPt
            // ... getCodecName
            // ... getCodecClockRate

            // getStreamStat (med_idx)
            // ... getRtcp
            // ... getJbuf

            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toJsonString() {
        return toJson().toString();
    }

}
