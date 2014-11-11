package com.shivandev.btmonoforaudio;

import android.app.Service;
import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class BtListenerSrv extends Service {
    private BtListenerBCastRec mBtListenerBCastRec;

    public BtListenerSrv() {
    }

    @Override
    public void onCreate() {
        mBtListenerBCastRec = new BtListenerBCastRec();
        IntentFilter filter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mBtListenerBCastRec, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBtListenerBCastRec);
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
