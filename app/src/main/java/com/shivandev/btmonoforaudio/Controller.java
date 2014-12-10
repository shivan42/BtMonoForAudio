package com.shivandev.btmonoforaudio;

import android.content.Context;

import com.google.inject.Inject;

import java.util.Observer;

public class Controller {
    @Inject private Context context;
	@Inject private BtListenerBCastRec mBtListenerBCastRec;
    @Inject private ScoProcessingSrv mScoProcessingSrv;

	public void stopBtAdapterListener() {
		mBtListenerBCastRec.unregister();
    }

    public void startBtAdapterListener() {
		mBtListenerBCastRec.register();
    }

    public void stopSco() {
        context.startService(ScoProcessingSrv.createStopScoIntent(context.getApplicationContext()));
    }

    public void startSco() {
        context.startService(ScoProcessingSrv.createStartScoIntent(context.getApplicationContext()));
    }

    public boolean isBtListenerRunning(){
        return Prefs.IS_BT_LISTENER_RUN.getBool();
    }

    public void startScoListener(Observer observer) {
        mScoProcessingSrv.addListener(observer);
    }

    public void stopScoListener(Observer observer) {
        mScoProcessingSrv.deleteListener(observer);
    }
//    public boolean isBtListenerRunning(String serviceClassName){
//        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
//
//        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
//            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
//                return true;
//            }
//        }
//        return false;
//    }
}
