package com.shivandev.btmonoforaudio.model;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.common.Prefs;
import com.shivandev.btmonoforaudio.utils.ServiceUtils;
import com.shivandev.btmonoforaudio.views.Controller;

import roboguice.service.RoboService;

public class ScoProcessingSrv extends RoboService {

    private static final boolean IS_DEBUG_THIS_MODULE = true;

    @Inject private AudioManager mAudioManager;
    @Inject private Handler handler;
    @Inject private ServiceUtils mServiceUtils;
    @Inject NotificationManager mNotificationManager;
    private ScoStateUpdatedBCastRec mScoStateUpdatedBCastRec;
    private BroadcastReceiver phoneCallListenerRec = null;
    private boolean isScoOn;
    private boolean restartAfterCall;
//    private int oldMediaVolume = -1;
//    private int oldBtVolume = -1;


	private static Intent getServiceIntent(Context context) {
		return new Intent(context, ScoProcessingSrv.class);
	}

	public static void startService(Context context) {
		context.startService(getServiceIntent(context));
	}

	public static void stopService(Context context) {
		context.stopService(getServiceIntent(context));
	}

    @Override
    public void onCreate() {
        super.onCreate();
        mScoStateUpdatedBCastRec = new ScoStateUpdatedBCastRec();
    }

    @Override
    public void onDestroy() {
        PauseAndroidMusicService();
//        togglePlayAndPauseAndroidMusicService();
        stopSCO();
        if (phoneCallListenerRec != null) {
            unregisterReceiver(phoneCallListenerRec);
        }
//        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        isScoOn = mAudioManager.isBluetoothScoOn();
		startSco();
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
        stopForeground(true);
        // проверяем запущен ли сервис наблюдения за БТ подключением, и если да, то перезапускаем его Notify
        if (mServiceUtils.isServiceRunning(BtListenerSrv.class.getName())) {
            mNotificationManager.notify(ServiceUtils.ID_NOTIFY, mServiceUtils.getNotification(ServiceUtils.NotificationType.BT_LISTENER_SERVICE_RUN));
        }
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
//                        restartAfterCall = true;
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
                    startForeground(ServiceUtils.ID_NOTIFY, mServiceUtils.getNotification(ServiceUtils.NotificationType.SCO_SERVICE_RUN));
                    // оповещаем контроллер об изменении состояния сервиса (включен/выключен)
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
                            playAndroidMusicService();
//                            if (!restartAfterCall) {
//                                togglePlayAndPauseAndroidMusicService();
//                            }
//                            restartAfterCall = false;
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

    private void playAndroidMusicService() {
        if (Prefs.IS_MUSIC_PLAYER_CONTROL_NEEDED.getBool()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                long eventTime = SystemClock.uptimeMillis() - 1;
                KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                mAudioManager.dispatchMediaKeyEvent(downEvent);
                eventTime++;
                KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                mAudioManager.dispatchMediaKeyEvent(upEvent);
            } else {
                Intent player = new Intent(Intent.ACTION_MEDIA_BUTTON);
                player.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
                sendOrderedBroadcast(player, null);
                player.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
                sendOrderedBroadcast(player, null);
            }
//            getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
            log("play music");
        }
    }
    private void PauseAndroidMusicService() {
        if (Prefs.IS_MUSIC_PLAYER_CONTROL_NEEDED.getBool()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                long eventTime = SystemClock.uptimeMillis() - 1;
                KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
                mAudioManager.dispatchMediaKeyEvent(downEvent);
                eventTime++;
                KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
                mAudioManager.dispatchMediaKeyEvent(upEvent);
            } else {
                Intent player = new Intent(Intent.ACTION_MEDIA_BUTTON);
                player.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE));
                sendOrderedBroadcast(player, null);
                player.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE));
                sendOrderedBroadcast(player, null);
            }
//            getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
            log("pause music");
        }
    }
    private void togglePlayAndPauseAndroidMusicService() {
        if (Prefs.IS_MUSIC_PLAYER_CONTROL_NEEDED.getBool()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                long eventTime = SystemClock.uptimeMillis() - 1;
                KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                mAudioManager.dispatchMediaKeyEvent(downEvent);
                eventTime++;
                KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                mAudioManager.dispatchMediaKeyEvent(upEvent);
            } else {
                Intent player = new Intent(Intent.ACTION_MEDIA_BUTTON);
                player.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
                sendOrderedBroadcast(player, null);
                player.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
                sendOrderedBroadcast(player, null);
            }
//            getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
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
