package com.shivandev.btmonoforaudio;

import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.google.inject.Inject;

import roboguice.service.RoboService;

public class BtListenerSrv extends RoboService {
    @Inject private BtListenerBCastRec mBtListenerBCastRec;

    public BtListenerSrv() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mBtListenerBCastRec = new BtListenerBCastRec();
        IntentFilter filter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mBtListenerBCastRec, filter);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId); //Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBtListenerBCastRec);
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
