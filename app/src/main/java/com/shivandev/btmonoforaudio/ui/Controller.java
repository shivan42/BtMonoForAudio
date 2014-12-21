package com.shivandev.btmonoforaudio.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.common.Prefs;
import com.shivandev.btmonoforaudio.model.BtHeadsetStateListenerBCastRec;
import com.shivandev.btmonoforaudio.model.BtListenerSrv;
import com.shivandev.btmonoforaudio.model.ScoProcessingSrv;
import com.shivandev.btmonoforaudio.utils.ServiceUtils;

import java.util.Observable;
import java.util.Observer;

public class Controller {
    private static ScoStateObserve notifier = new ScoStateObserve();

    @Inject private Context context;
    @Inject private BtHeadsetStateListenerBCastRec mBtHeadsetStateListenerBCastRec;
    @Inject private ScoProcessingSrv mScoProcessingSrv;
    @Inject private ServiceUtils mServiceUtils;
    @Inject NotificationManager mNotificationManager;
    @Inject NotifyFactory mNotifyFactory;

    public void stopBtAdapterListener() {
        context.stopService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
    }

    public void startBtAdapterListener() {
        context.startService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
    }

	public void startSco() {
		ScoProcessingSrv.startService(context);
	}

    public void stopSco() {
		ScoProcessingSrv.stopService(context);
    }

    public void startScoListener(Observer observer) {
        notifier.addObserver(observer);
    }

    public void stopScoListener(Observer observer) {
        notifier.deleteObserver(observer);
    }

    public boolean isBtListenerRunning(){
        return mServiceUtils.isServiceRunning(BtListenerSrv.class.getName());
    }

    public void notifyAboutScoStateChanged() {
        if (ScoProcessingSrv.isScoOn()) {
            mNotificationManager.notify(NotifyFactory.ID_NOTIFY, mNotifyFactory.getNotification(NotifyFactory.EventType.SCO_SERVICE_RUN));
        } else btListenerStateNotify();
        notifier.scoStateChanged();
    }

    public void notifyAboutBtListenerStateChanged() {
        if (!ScoProcessingSrv.isScoOn()) {
            btListenerStateNotify();
        }

    }

    private void btListenerStateNotify() {
        if (BtListenerSrv.isBtListenerRun()) {
            mNotificationManager.notify(NotifyFactory.ID_NOTIFY, mNotifyFactory.getNotification(NotifyFactory.EventType.BT_LISTENER_SERVICE_RUN));
        } else {
            mNotificationManager.cancel(NotifyFactory.ID_NOTIFY);
        }
    }

    public void setControlMusicPlayerOption(boolean isNeeded) {
        Prefs.IS_MUSIC_PLAYER_CONTROL_NEEDED.set(isNeeded);
    }

    public void setStartServiceAfterRebootOption(boolean isNeeded) {
        Prefs.IS_BT_SERVICE_START_AFTER_REBOOT.set(isNeeded);
    }

    public void setNotifyAboutBtServiceIfBtAdapterIsOnOption(boolean isNeeded) {
        Prefs.IS_NOTIFY_BT_SERVICE_IF_BT_ADAPTER_IS_ON.set(isNeeded);
    }

    static class ScoStateObserve extends Observable {
        public void scoStateChanged() {
            setChanged();
            notifyObservers();
        }
    }
}
