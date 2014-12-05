package com.shivandev.btmonoforaudio;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.google.inject.Inject;

public class BtListenerBCastRec extends BroadcastReceiver {
    private static final boolean IS_DEBUG_THIS_MODULE = true;

	@Inject Context context;

    void log(String str) {
        if (IS_DEBUG_THIS_MODULE) Log.e("SCO Service", str);
    }

	public void register() {
		IntentFilter filter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
		context.registerReceiver(this, filter);
		Prefs.IS_BT_LISTENER_RUN.set(true);
	}

	public void unregister() {
		context.unregisterReceiver(this);
		Prefs.IS_BT_LISTENER_RUN.set(false);
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
