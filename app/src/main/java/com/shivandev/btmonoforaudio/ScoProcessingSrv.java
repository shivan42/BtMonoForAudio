package com.shivandev.btmonoforaudio;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ScoProcessingSrv extends Service {

    private static final boolean isDebugThisModule = true;
    public static final String EXTRA_MODE = "EXTRA_MODE";
    private int oldMediaVolume = -1;
    private int oldBtVolume = -1;
    private AudioManager mAudioManager;
    private ScoStateUpdatedBCastRec mScoStateUpdatedBCastRec;
    private BroadcastReceiver phoneCallListenerRec = null;
    private Handler handler;
    private boolean isScoOn;
    private boolean restartAfterCall;

    public static enum Mode {
        START_SCO,
        STOP_SCO;
    }

    public static Intent createStartScoIntent(Context context) {
        Intent intent = new Intent(context, ScoProcessingSrv.class);
        intent.putExtra(EXTRA_MODE, Mode.START_SCO);
        return intent;
    }

    public static Intent createStopScoIntent(Context context) {
        Intent intent = new Intent(context, ScoProcessingSrv.class);
        intent.putExtra(EXTRA_MODE, Mode.STOP_SCO);
        return intent;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mScoStateUpdatedBCastRec = new ScoStateUpdatedBCastRec();
//        phoneCallListenerRec = new BluetoothStateBCastRec();
//        registerReceiver(phoneCallListenerRec, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

    }

    @Override
    public void onDestroy() {
        getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.pause"));
        log("togglepause");
        stopSCO();
        if (phoneCallListenerRec != null) {
            unregisterReceiver(phoneCallListenerRec);
//            phoneCallListenerRec = null;
        }
//        stopForeground(true);
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isScoOn = mAudioManager.isBluetoothScoOn();
        if (intent != null) {
            Mode modeCommand = ((Mode) intent.getSerializableExtra(EXTRA_MODE));
            switch (modeCommand) {
                case START_SCO:
                    startSco();
                    break;
                case STOP_SCO:
                    stopSelf();
//                    stopSCO();
                    break;
            }
        }
        return Service.START_STICKY;
    }

    private void startSco() {
//        oldMediaVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        oldBtVolume = mAudioManager.getStreamVolume(6);
        mAudioManager.startBluetoothSco();
        isScoOn = true;
        registerReceiver(mScoStateUpdatedBCastRec, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
    }

    private void stopSCO() {
        /*
        removeScoWatcher();
        stopForeground(true);
        mAudioManager.setStreamVolume(3, this.old_media_volume, 0);
        mAudioManager.setStreamVolume(6, this.old_bt_volume, 0);
        */
        isScoOn = false;
        unregisterReceiver(mScoStateUpdatedBCastRec);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        mAudioManager.stopBluetoothSco();
        mAudioManager.setBluetoothScoOn(false);
        log("STOP BluetoothSco");
        /*
        this.mNM.cancel(1001);
        localMBTApplication.status = 1;
        localMBTApplication.notifyChangeListener();
        localMBTApplication.showToast(getApplicationContext(), l(2131230732));
        */
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void log(String str) {
        if (isDebugThisModule) Log.e("SCO Service", str);
    }

    /**
     * отслеживаем состояние блютуса, и реагируем на выключение прекращением работы сервиса и SCO
     */
    class BluetoothStateBCastRec extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if ((TelephonyManager.EXTRA_STATE_RINGING.equals(str)) || (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(str))) {
                if (isScoOn)stopSCO();
            }
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(str)) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        restartAfterCall = true;
                        startSco();
                    }
                }, 1500);
            }
//            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
//            switch (state) {
//                case BluetoothAdapter.STATE_OFF:
//                    stopSCO();
//                    break;
//            }
        }
    }

    class ScoStateUpdatedBCastRec extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, AudioManager.SCO_AUDIO_STATE_ERROR);
            switch (state) {
                case AudioManager.SCO_AUDIO_STATE_CONNECTED:
                    log("SCO_AUDIO_STATE_CONNECTED");
                    /*
                    startForeground(SERVICE_ID, this.mBuilder.getNotification());
                    if (MBTPreferences.media_volume != -1) {
                        mAudioManager.setStreamVolume(3, MBTPreferences.media_volume, 0);
                    }

                    if (MBTPreferences.bluetooth_volume != -1) {
                        mAudioManager.setStreamVolume(6, MBTPreferences.bluetooth_volume, 0);
                    }
                    */
                    mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    mAudioManager.setBluetoothScoOn(true);
                    if (phoneCallListenerRec == null) {
                        phoneCallListenerRec = new BluetoothStateBCastRec();
                        registerReceiver(phoneCallListenerRec, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
                    }

//                    unregisterReceiver(this);
                    /*
                    showStatusMessage(0);
                    localMBTApplication.notifyChangeListener();
                    localMBTApplication.showToast(getApplicationContext(), l(2131230734));
                    */
                    //                playStartSound();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!restartAfterCall) {
                                getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
//                                getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.play"));
                                log("togglepause");
                            }
                            restartAfterCall = false;
                        }
                    }, 1000);
                    //                    setupScoWatcher();
                    //                mAudioManager.setStreamVolume(3, mAudioManager.getStreamMaxVolume(3), 0);
                    break;
                case AudioManager.SCO_AUDIO_STATE_DISCONNECTED:
                    log("SCO_AUDIO_STATE_DISCONNECTED");
//                    stopSCO();
                    stopSelf();
                    break;
                case AudioManager.SCO_AUDIO_STATE_ERROR:
                    log("SCO_AUDIO_STATE_ERROR");
                    break;
            }
        }
    }

//    void playStartSound()
//    {
//        if (!MBTPreferences.playsound) {
//            return;
//        }
//        if (this.mute_sound)
//        {
//            this.mute_sound = false;
//            return;
//        }
//        try
//        {
//            MediaPlayer.create(this, 2131034113).start();
//            return;
//        }
//        catch (Exception localException)
//        {
//            localException.printStackTrace();
//        }
//    }

}
