package com.shivandev.btmonoforaudio.model;

import android.app.NotificationManager;
import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.utils.ServiceUtils;

import roboguice.service.RoboService;

public class BtListenerSrv extends RoboService {
    @Inject private BtListenerBcastRec mBtListenerBcastRec;
    @Inject private ServiceUtils mServiceUtils;
    @Inject private NotificationManager mNotificationManager;

    public BtListenerSrv() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mBtListenerBcastRec, filter);
        startForeground(ServiceUtils.ID_NOTIFY, mServiceUtils.getNotification(ServiceUtils.NotificationType.BT_LISTENER_SERVICE_RUN));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId); //Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBtListenerBcastRec);
        stopForeground(true);
        if (mServiceUtils.isServiceRunning(ScoProcessingSrv.class.getName())) {
            mNotificationManager.notify(ServiceUtils.ID_NOTIFY, mServiceUtils.getNotification(ServiceUtils.NotificationType.SCO_SERVICE_RUN));
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
