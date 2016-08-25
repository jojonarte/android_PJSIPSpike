package com.jojonarte.pjsipspike.pjsip;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pj_qos_type;
import org.pjsip.pjsua2.pjsip_transport_type_e;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Process;

/**
 * Created by dna on 23/08/2016.
 */

public class PJSIPService extends Service {

    private static String TAG = "PJSIPService";

    private static HandlerThread mWorkerThread;

    private Handler mHandler;

    private Endpoint mEndpoint;

    private PJSIPLogWriter mLogWriter;

    private PJSIPBroadcastEmiter mEmitter;

    private List<PJSIPAccount> mAccounts = new ArrayList<>();

    private List<PJSIPCall> mCalls = new ArrayList<>();

    private List<Object> mTrash = new LinkedList<>();

    private AudioManager mAudioManager;

    public PJSIPBroadcastEmiter getEmitter() {
        return mEmitter;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        super.onCreate();

        mWorkerThread = new HandlerThread(getClass().getSimpleName(), Process.THREAD_PRIORITY_FOREGROUND);
        mWorkerThread.setPriority(Thread.MAX_PRIORITY);
        mWorkerThread.start();
        mHandler = new Handler(mWorkerThread.getLooper());

        mEmitter = new PJSIPBroadcastEmiter(this);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run(){
//                job(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, "service tick");
//                    }
//                });
//            }
//        }, 0, 1000);

        job(new Runnable() {
            @Override
            public void run() {
                load();

//                Logger.debug(TAG, "Creating SipService with priority: " + Thread.currentThread().getPriority());
//
//                loadNativeLibraries();
//
//                mRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(SipService.this, RingtoneManager.TYPE_RINGTONE);

//                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//                mBroadcastEmitter = new BroadcastEventEmitter(SipService.this);
//                loadConfiguredAccounts();
//                addAllConfiguredAccounts();
//
//                Logger.debug(TAG, "SipService created!");
            }
        });
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
        try {
            mEndpoint = new Endpoint();
            mEndpoint.libCreate();
            mEndpoint.libRegisterThread(Thread.currentThread().getName());

            EpConfig epConfig = new EpConfig();

            epConfig.getLogConfig().setLevel(4);
            epConfig.getLogConfig().setConsoleLevel(4);

            mLogWriter = new PJSIPLogWriter();
            epConfig.getLogConfig().setWriter(mLogWriter);

            // epConfig.getUaConfig().setUserAgent("");
            epConfig.getMedConfig().setHasIoqueue(true);
            epConfig.getMedConfig().setClockRate(8000);
            epConfig.getMedConfig().setQuality(4);
            epConfig.getMedConfig().setEcOptions(1);
            epConfig.getMedConfig().setEcTailLen(200);
            epConfig.getMedConfig().setThreadCnt(2);
            mEndpoint.libInit(epConfig);

            mTrash.add(epConfig);

            mEndpoint.libStart();
        } catch (Exception e) {
            Log.e(TAG, "Error while starting PJSIP", e);
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        job(new Runnable() {
            @Override
            public void run() {
                handle(intent);
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mWorkerThread.quitSafely();
        }
        super.onDestroy();
    }

    private void job(Runnable job) {
        mHandler.post(job);
    }

    protected synchronized AudDevManager getAudDevManager() {
        return mEndpoint.audDevManager();
    }

    public void evict(final PJSIPAccount account) {
        if (mHandler.getLooper().getThread() != Thread.currentThread()) {
            job(new Runnable() {
                @Override
                public void run() {
                    evict(account);
                }
            });
            return;
        }

        // Remove link to account
        mAccounts.remove(account);

        // Remove transport
        try {
            mEndpoint.transportClose(account.getTransportId());
        } catch (Exception e) {
            Log.w(TAG, "Failed to close transport for account", e);
        }

        // Remove account in PjSip
        account.delete();

    }

    public void evict(final PJSIPCall call) {
        if (mHandler.getLooper().getThread() != Thread.currentThread()) {
            job(new Runnable() {
                @Override
                public void run() {
                    evict(call);
                }
            });
            return;
        }

        // Remove link to call
        mCalls.remove(call);

        // Remove call in PjSip
        call.delete();
    }


    private void handle(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        switch (intent.getAction()) {
            // General actions
            case PJActions.ACTION_START:
                handleStart(intent);
                break;

            // Account actions
            case PJActions.ACTION_CREATE_ACCOUNT:
                handleAccountCreate(intent);
                break;
            case PJActions.ACTION_DELETE_ACCOUNT:
                handleAccountDelete(intent);
                break;

            // Call actions
            case PJActions.ACTION_MAKE_CALL:
                handleCallMake(intent);
                break;
            case PJActions.ACTION_HANGUP_CALL:
                handleCallHangup(intent);
                break;
            case PJActions.ACTION_ANSWER_CALL:
                handleCallAnswer(intent);
                break;
            case PJActions.ACTION_HOLD_CALL:
                handleCallSetOnHold(intent);
                break;
            case PJActions.ACTION_UNHOLD_CALL:
                handleCallReleaseFromHold(intent);
                break;
            case PJActions.ACTION_XFER_CALL:
                // TODO: handleCallXFer(intent);
                break;
            case PJActions.ACTION_DTMF_CALL:
                // TODO: handleCallDtmf(intent);
                break;
        }
    }

    /**
     * @param intent
     */
    private void handleStart(Intent intent) {
        mEmitter.fireStarted(intent, mAccounts, mCalls);
    }

    /**
     * @param intent
     */
    private void handleAccountCreate(Intent intent) {
        try {
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            String host = intent.getStringExtra("host");
            String realm = intent.getStringExtra("realm");
            String port = intent.getStringExtra("port");
            String transport = intent.getStringExtra("transport");
            String uri = port != null && !port.isEmpty() ? host + ":" + port : host;

            // Create transport
            TransportConfig transportConfig = new TransportConfig();
            transportConfig.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);

            pjsip_transport_type_e transportType = pjsip_transport_type_e.PJSIP_TRANSPORT_UDP;

            if (transport != null && !transport.isEmpty() && !transport.equals("TCP")) {
                switch (transport) {
                    case "UDP":
                        transportType = pjsip_transport_type_e.PJSIP_TRANSPORT_TCP;
                        break;
                    case "TLS":
                        transportType = pjsip_transport_type_e.PJSIP_TRANSPORT_TLS;
                        break;
                    default:
                        Log.w(TAG, "Illegal \""+ transport +"\" transport (possible values are UDP, TCP or TLS) use TCP instead");
                        break;
                }
            }

            int transportId = mEndpoint.transportCreate(transportType, transportConfig);

            // Create account
            AccountConfig cfg = new AccountConfig();
            cfg.setIdUri("sip:"+ username + "@" + realm);
            cfg.getRegConfig().setRegistrarUri("sip:" + uri);
            AuthCredInfo cred = new AuthCredInfo("Digest", realm, username, 0, password);
            cfg.getSipConfig().getAuthCreds().add(cred);
            cfg.getSipConfig().setTransportId(transportId);
            cfg.getMediaConfig().getTransportConfig().setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);
            cfg.getRegConfig().setRegisterOnAdd(true);
            cfg.getVideoConfig().setAutoTransmitOutgoing(true);

            // TODO: Pass username, password, host, realm into Account object for further retrieval
            PJSIPAccount account = new PJSIPAccount(this, transportId);
            account.create(cfg);

            mTrash.add(cfg);
            mTrash.add(cred);
            mTrash.add(transportConfig);

            mAccounts.add(account);
            mEmitter.fireAccountCreated(intent, account);
        } catch (Exception e) {
            mEmitter.fireIntentHandled(intent, e);
        }
    }

    private void handleAccountDelete(Intent intent) {
        try {
            int accountId = intent.getIntExtra("account_id", -1);
            PJSIPAccount account = null;

            for (PJSIPAccount a : mAccounts) {
                if (a.getId() == accountId) {
                    account = a;
                    break;
                }
            }

            if (account == null) {
                throw new Exception("Account with \""+ accountId +"\" id not found");
            }

            evict(account);

            // -----
            mEmitter.fireIntentHandled(intent);
        } catch (Exception e) {
            mEmitter.fireIntentHandled(intent, e);
        }
    }

    private void handleCallMake(Intent intent) {
        try {
            Log.d(TAG, "handleCallMake start");

            int accountId = intent.getIntExtra("account_id", -1);
            String destination = intent.getStringExtra("destination");

            // -----
            PJSIPAccount account = findAccount(accountId);

            // -----
            CallOpParam prm = new CallOpParam(true);
            // TODO: Allow to send also headers and other information

            // -----
            PJSIPCall call = new PJSIPCall(account);
            call.makeCall(destination, prm);

            mCalls.add(call);
            mEmitter.fireCallCreated(intent, call);

            Log.d(TAG, "handleCallMake end");
        } catch (Exception e) {
            mEmitter.fireIntentHandled(intent, e);
        }
    }

    private void handleCallHangup(Intent intent) {
        try {
            int callId = intent.getIntExtra("call_id", -1);

            // -----
            PJSIPCall call = findCall(callId);
            call.hangup(new CallOpParam(true));

            mEmitter.fireIntentHandled(intent);
        } catch (Exception e) {
            mEmitter.fireIntentHandled(intent, e);
        }
    }

    private void handleCallAnswer(Intent intent) {
        try {
            int callId = intent.getIntExtra("call_id", -1);

            // -----
            PJSIPCall call = findCall(callId);
            call.answer(new CallOpParam(true));

            mEmitter.fireIntentHandled(intent);
        } catch (Exception e) {
            mEmitter.fireIntentHandled(intent, e);
        }
    }

    private void handleCallSetOnHold(Intent intent) {
        try {
            int callId = intent.getIntExtra("call_id", -1);

            // -----
            PJSIPCall call = findCall(callId);
            call.putOnHold();

            mEmitter.fireIntentHandled(intent);
        } catch (Exception e) {
            mEmitter.fireIntentHandled(intent, e);
        }
    }

    private void handleCallReleaseFromHold(Intent intent) {
        try {
            int callId = intent.getIntExtra("call_id", -1);

            // -----
            PJSIPCall call = findCall(callId);
            call.releaseFromHold();

            mEmitter.fireIntentHandled(intent);
        } catch (Exception e) {
            mEmitter.fireIntentHandled(intent, e);
        }
    }



    private PJSIPAccount findAccount(int id) throws Exception {
        for (PJSIPAccount account : mAccounts) {
            if (account.getId() == id) {
                return account;
            }
        }

        throw new Exception("Account with specified \""+ id +"\" id not found");
    }

    private PJSIPCall findCall(int id) throws Exception {
        for (PJSIPCall call : mCalls) {
            if (call.getId() == id) {
                return call;
            }
        }

        throw new Exception("Call with specified \""+ id +"\" id not found");
    }

}
