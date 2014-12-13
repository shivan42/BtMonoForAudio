package com.shivandev.btmonoforaudio;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;

import java.util.List;
import java.util.Observer;

public class Controller {
    @Inject private Context context;
	@Inject private BtListenerBcastRec mBtListenerBcastRec;
    @Inject private ScoProcessingSrv mScoProcessingSrv;

	public void stopBtAdapterListener() {
//		mBtListenerBcastRec.unregister();
        context.stopService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
    }

    public void startBtAdapterListener() {
//		mBtListenerBcastRec.register();
        context.startService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
    }

    public void stopSco() {
        context.startService(ScoProcessingSrv.createStopScoIntent(context.getApplicationContext()));
    }

    public void startSco() {
        context.startService(ScoProcessingSrv.createStartScoIntent(context.getApplicationContext()));
    }

    public void startScoListener(Observer observer) {
        ScoProcessingSrv.addListener(observer);
    }

    public void stopScoListener(Observer observer) {
        ScoProcessingSrv.deleteListener(observer);
    }

//    public boolean isBtListenerRunning(){
//        return Prefs.IS_BT_LISTENER_RUN.getBool();
//    }

    public boolean isBtListenerRunning(String serviceClassName){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
}
