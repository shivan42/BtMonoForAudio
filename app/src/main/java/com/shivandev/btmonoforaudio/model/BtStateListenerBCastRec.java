package com.shivandev.btmonoforaudio.model;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.ui.Controller;

public class BtStateListenerBCastRec extends BroadcastReceiver {
    private static final boolean IS_DEBUG_THIS_MODULE = true;
    @Inject Controller mController;

    void log(String str) {
        if (IS_DEBUG_THIS_MODULE) Log.e("BtStateListener", str);
    }

    public BtStateListenerBCastRec() {

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
			case BluetoothAdapter.STATE_ON:
				mController.sendNotificationAboutBtListener();
                break;
        }
    }

}
