package com.shivandev.btmonoforaudio.views;

import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.common.Prefs;
import com.shivandev.btmonoforaudio.model.BtListenerBCastRec;
import com.shivandev.btmonoforaudio.model.BtListenerSrv;
import com.shivandev.btmonoforaudio.model.ScoProcessingSrv;
import com.shivandev.btmonoforaudio.utils.ServiceUtils;

import java.util.Observable;
import java.util.Observer;

public class Controller {
    private static ScoStateObserve notifier = new ScoStateObserve();
    @Inject private Context context;
    @Inject private BtListenerBCastRec mBtListenerBCastRec;
    @Inject private ScoProcessingSrv mScoProcessingSrv;
    @Inject private ServiceUtils serviceUtils;

    public void stopBtAdapterListener() {
        context.stopService(new Intent(context.getApplicationContext(), BtListenerSrv.class));
    }

    public void startBtAdapterListener() {
        // TODO Провести рефакторинг модели оповещений, они как и UI должны проходить через контроллер, и может собрать их в отдельный класс, а не запускать в мдулях сервисов
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
        return serviceUtils.isServiceRunning(BtListenerSrv.class.getName());
    }

    public static void scoStateChanged() {
        notifier.scoStateChanged();
    }

    public void setControlMusicPlayerOption(boolean isNeeded) {
        Prefs.IS_MUSIC_PLAYER_CONTROL_NEEDED.set(isNeeded);
    }

    public void setStartServiceAfterRebootOption(boolean isNeeded) {
        Prefs.IS_BT_SERVICE_START_AFTER_REBOOT.set(isNeeded);
    }

    static class ScoStateObserve extends Observable {
        public void scoStateChanged() {
            setChanged();
            notifyObservers();
        }
    }
}
