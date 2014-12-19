package com.shivandev.btmonoforaudio.model;

import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.ui.Controller;

import roboguice.service.RoboService;

public class BtListenerSrv extends RoboService {
    private static boolean isBtListenerRun = false;

    @Inject private BtListenerBCastRec mBtListenerBCastRec;
    @Inject Controller mController;

    public BtListenerSrv() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mBtListenerBCastRec, filter);
        isBtListenerRun = true;
        mController.notifyAboutBtListenerStateChanged();
//        startForeground(ServiceUtils.ID_NOTIFY, mServiceUtils.getNotification(ServiceUtils.NotificationType.BT_LISTENER_SERVICE_RUN));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId); //Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBtListenerBCastRec);
        isBtListenerRun = false;
        mController.notifyAboutBtListenerStateChanged();
//        stopForeground(true);
        super.onDestroy();
    }

    public static boolean isBtListenerRun() {
        return isBtListenerRun;
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
