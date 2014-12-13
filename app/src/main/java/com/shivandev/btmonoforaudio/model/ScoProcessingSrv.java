package com.shivandev.btmonoforaudio.model;

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

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.common.Prefs;
import com.shivandev.btmonoforaudio.views.Controller;

import roboguice.service.RoboService;

public class ScoProcessingSrv extends RoboService {

    private static final boolean IS_DEBUG_THIS_MODULE = true;
    public static final String EXTRA_MODE = "EXTRA_MODE";

    @Inject private AudioManager mAudioManager;
    @Inject private Handler handler;
    private ScoStateUpdatedBCastRec mScoStateUpdatedBCastRec;
    private BroadcastReceiver phoneCallListenerRec = null;
    private boolean isScoOn;
    private boolean restartAfterCall;
    //    private int oldMediaVolume = -1;
//    private int oldBtVolume = -1;

    public static enum Mode {
        START_SCO,
        STOP_SCO
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
        super.onCreate();
//        handler = new Handler();
//        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mScoStateUpdatedBCastRec = new ScoStateUpdatedBCastRec();

//        phoneCallListenerRec = new BluetoothStateBCastRec();
//        registerReceiver(phoneCallListenerRec, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

    }

    @Override
    public void onDestroy() {
        togglePlayAndPauseAndroidMusicService();
        stopSCO();
        if (phoneCallListenerRec != null) {
            unregisterReceiver(phoneCallListenerRec);
//            phoneCallListenerRec = null;
        }
//        stopForeground(true);
        super.onDestroy();
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
        super.onStartCommand(intent, flags, startId);
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
        stopForeground(true);
        mAudioManager.setStreamVolume(3, this.old_media_volume, 0);
        mAudioManager.setStreamVolume(6, this.old_bt_volume, 0);
        */
        isScoOn = false;
        unregisterReceiver(mScoStateUpdatedBCastRec);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        mAudioManager.stopBluetoothSco();
        mAudioManager.setBluetoothScoOn(false);
        Controller.scoStateChanged();
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
        if (IS_DEBUG_THIS_MODULE) Log.e("SCO Service", str);
    }

    /**
     * отслеживаем состояние телефона, реагируем на выходящие/исходящие звонки - прекращением работу сервиса и возобновляем после звонка
     */
    class PhoneStateBCastRec extends BroadcastReceiver {
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
                        phoneCallListenerRec = new PhoneStateBCastRec();
                        registerReceiver(phoneCallListenerRec, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
                    }
                    Controller.scoStateChanged();

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
                                togglePlayAndPauseAndroidMusicService();
                            }
                            restartAfterCall = false;
                        }
                    }, 1000);
                    //                mAudioManager.setStreamVolume(3, mAudioManager.getStreamMaxVolume(3), 0);
                    break;
                case AudioManager.SCO_AUDIO_STATE_DISCONNECTED:
                    log("SCO_AUDIO_STATE_DISCONNECTED");
                    stopSelf();
                    break;
                case AudioManager.SCO_AUDIO_STATE_ERROR:
                    log("SCO_AUDIO_STATE_ERROR");
                    break;
            }
        }
    }

    private void togglePlayAndPauseAndroidMusicService() {
        if (Prefs.IS_MUSIC_PLAYER_CONTROL_NEEDED.getBool()) {
            // TODO надо добавить проверку - запущен ли musicService и если нет, то принудительно его запустить до отпавки команд
            getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
//                                getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.play"));
            log("togglepause");
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
