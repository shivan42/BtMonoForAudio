package com.shivandev.btmonoforaudio;

import android.content.Context;

import com.google.inject.Inject;

public class Controller {
    @Inject Context context;
	@Inject private BtListenerBCastRec mBtListenerBCastRec;

	public void stopBtAdapterListener() {
		mBtListenerBCastRec.unregister();
//        context.stopService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
    }

    public void startBtAdapterListener() {
		mBtListenerBCastRec.register();
//        context.startService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
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
