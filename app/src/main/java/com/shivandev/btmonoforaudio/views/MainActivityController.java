package com.shivandev.btmonoforaudio.views;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.model.BtListenerBCastRec;
import com.shivandev.btmonoforaudio.model.BtListenerSrv;
import com.shivandev.btmonoforaudio.model.ScoProcessingSrv;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivityController {
    private static ScoStateObserve notifier = new ScoStateObserve();
    @Inject private Context context;
    @Inject private BtListenerBCastRec mBtListenerBCastRec;
    @Inject private ScoProcessingSrv mScoProcessingSrv;

//    public static void addListener(Observer observer) {
//        notifier.addObserver(observer);
//    }
//
//    public static void deleteListener(Observer observer) {
//        notifier.deleteObserver(observer);
//    }

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
//        addListener(observer);
        notifier.addObserver(observer);
    }

    public void stopScoListener(Observer observer) {
//        deleteListener(observer);
        notifier.deleteObserver(observer);
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

    public static void scoStateChanged() {
        notifier.scoStateChanged();
    }

    static class ScoStateObserve extends Observable {
        public void scoStateChanged() {
            setChanged();
            notifyObservers();
        }
    }
}
