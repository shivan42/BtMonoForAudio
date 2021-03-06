package com.shivandev.btmonoforaudio.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.common.Prefs;
import com.shivandev.btmonoforaudio.ui.Controller;

import roboguice.service.RoboService;

public class BtListenerSrv extends RoboService {
    private static boolean isBtListenerRun = false;

    @Inject private BtHeadsetStateListenerBCastRec mBtHeadsetStateListenerBCastRec;
    @Inject private BtStateListenerBCastRec mBtStateListenerBCastRec;
    @Inject Controller mController;
    private boolean isBtListenerAlreadyRun = false;


    private static Intent getServiceIntent(Context context) {
        return new Intent(context, BtListenerSrv.class);
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
        IntentFilter filter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mBtHeadsetStateListenerBCastRec, filter);
        isBtListenerRun = true;
        mController.notifyAboutBtListenerStateChanged(); // оповещаем контроллер об изменении состояния сервиса (включен/выключен)
//        startForeground(ServiceUtils.ID_NOTIFY, mServiceUtils.getNotification(ServiceUtils.NotificationType.BT_LISTENER_SERVICE_RUN));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Prefs.IS_NOTIFY_BT_SERVICE_IF_BT_ADAPTER_IS_ON.getBool()) {
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBtStateListenerBCastRec, filter);
        } else {
            if (isBtListenerAlreadyRun) unregisterReceiver(mBtStateListenerBCastRec);
        }
        isBtListenerAlreadyRun = true;
        mController.sendNotificationAboutBtListener();
        return super.onStartCommand(intent, flags, startId); //Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBtHeadsetStateListenerBCastRec);
        if (Prefs.IS_NOTIFY_BT_SERVICE_IF_BT_ADAPTER_IS_ON.getBool()) {
            unregisterReceiver(mBtStateListenerBCastRec);
        }
        isBtListenerRun = false;
        mController.notifyAboutBtListenerStateChanged();
        mController.sendNotificationAboutBtListener();
//        stopForeground(true);
        super.onDestroy();
    }

    public static boolean isBtListenerRun() {
        return isBtListenerRun;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
