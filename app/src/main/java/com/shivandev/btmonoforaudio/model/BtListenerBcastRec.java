package com.shivandev.btmonoforaudio.model;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class BtListenerBCastRec extends BroadcastReceiver {
    private static final boolean IS_DEBUG_THIS_MODULE = true;

    void log(String str) {
        if (IS_DEBUG_THIS_MODULE) Log.e("SCO Service", str);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
        switch (state) {
            case BluetoothHeadset.STATE_CONNECTED:
                log("Headset is connected");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.startService(ScoProcessingSrv.createStartScoIntent(context));
                    }
                }, 2000);
                break;
            case BluetoothHeadset.STATE_DISCONNECTED:
                log("Headset is unplugged");
                break;
        }
    }
}
