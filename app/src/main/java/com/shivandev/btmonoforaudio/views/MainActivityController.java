package com.shivandev.btmonoforaudio.views;

import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.model.BtListenerBCastRec;
import com.shivandev.btmonoforaudio.model.BtListenerSrv;
import com.shivandev.btmonoforaudio.model.ScoProcessingSrv;
import com.shivandev.btmonoforaudio.utils.ServiceUtils;

import java.util.Observable;
import java.util.Observer;

public class MainActivityController {
    private static ScoStateObserve notifier = new ScoStateObserve();
    @Inject private Context context;
    @Inject private BtListenerBCastRec mBtListenerBCastRec;
    @Inject private ScoProcessingSrv mScoProcessingSrv;
    @Inject private ServiceUtils serviceUtils;

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

    public void startScoListener(Observer observer) {
        notifier.addObserver(observer);
    }

    public void stopScoListener(Observer observer) {
        notifier.deleteObserver(observer);
    }

    public boolean isBtListenerRunning(){
        return serviceUtils.isServiceRunning(BtListenerSrv.class.getName());
    }

    public static void scoStateChanged() {
        notifier.scoStateChanged();
    }

    public void setControlMusicPlayerOption(boolean isNeeded) {

    }

    public void setStartServiceAfterRebootOption(boolean isNeeded) {

    }

    static class ScoStateObserve extends Observable {
        public void scoStateChanged() {
            setChanged();
            notifyObservers();
        }
    }
}
