package com.shivandev.btmonoforaudio;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;

import java.util.List;

public class Controller {
    @Inject Context context;

//    public Controller(Context context) {
//        this.context = context;
//    }


    public void stopBtAdapterListener() {
        context.stopService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
    }

    public void startBtAdapterListener() {
        context.startService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
    }

    public void stopSco() {
        context.startService(ScoProcessingSrv.createStopScoIntent(context.getApplicationContext()));
    }

    public void startSco() {
        context.startService(ScoProcessingSrv.createStartScoIntent(context.getApplicationContext()));
    }

    public boolean isServiceRunning(String serviceClassName){
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
