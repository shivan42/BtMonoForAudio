package com.shivandev.btmonoforaudio.ui;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.common.App;
import com.shivandev.btmonoforaudio.common.Prefs;
import com.shivandev.btmonoforaudio.model.BtHeadsetStateListenerBCastRec;
import com.shivandev.btmonoforaudio.model.BtListenerSrv;
import com.shivandev.btmonoforaudio.model.ScoProcessingSrv;
import com.shivandev.btmonoforaudio.model.ScoStateObserve;
import com.shivandev.btmonoforaudio.utils.ServiceUtils;

import java.util.Observer;

import static com.shivandev.btmonoforaudio.model.ScoStateObserve.ScoState.BT_LISTENER;
import static com.shivandev.btmonoforaudio.model.ScoStateObserve.ScoState.SCO;

public class Controller {
    private static ScoStateObserve observeNotifier = new ScoStateObserve();
	public static String ACTION_SCO_WIDGET_UPDATE = "com.shivandev.btmonoforaudio.action_sco_widget_update";
	public static String ACTION_BT_LISTENER_WIDGET_UPDATE = "com.shivandev.btmonoforaudio.action_bt_listener_widget_update";

    @Inject private Context context;
    @Inject private BtHeadsetStateListenerBCastRec mBtHeadsetStateListenerBCastRec;
//    @Inject private ScoProcessingSrv mScoProcessingSrv;
    @Inject private ServiceUtils mServiceUtils;
    @Inject NotificationManager mNotificationManager;
    @Inject NotifyFactory mNotifyFactory;
    private Boolean restartBtListener = null;

    public void stopBtAdapterListener() {
        BtListenerSrv.stopService(context);
    }

    public void startBtAdapterListener() {
        BtListenerSrv.startService(context);
    }

    public static void switchBtListener(Context context) {
        if (isBtListenerRunning()) {
            BtListenerSrv.stopService(context);
        } else {
            BtListenerSrv.startService(context);
        }
    }
    public void switchSco(Context context) {
        if (isScoProcessingRunning()) {
            ScoProcessingSrv.stopService(context);
        } else {
            if (!isBluetoothAvailable()) {
				if (Prefs.IS_CHECK_BT_ADAPTER_IS_ON_OPTION.getBool()) {
					requestEnableBt(context);
				} else {
					msg("Bluetooth-адаптер не запущен", context);
				}
			} else {
                ScoProcessingSrv.startService(context);
            }
        }
    }

	public void msg(String textMsg, Context ctx) {
		Toast.makeText(ctx, textMsg, Toast.LENGTH_SHORT).show();
	}

	public void startSco() {
		ScoProcessingSrv.startService(context);
	}

    public void stopSco() {
		ScoProcessingSrv.stopService(context);
    }

    public static void startScoListener(Observer observer) {
        observeNotifier.addObserver(observer);
    }

    public static void stopScoListener(Observer observer) {
        observeNotifier.deleteObserver(observer);
    }

    public static boolean isBtListenerRunning(){
        return BtListenerSrv.isBtListenerRun(); //mServiceUtils.isServiceRunning(BtListenerSrv.class.getName());
    }

    public static boolean isScoProcessingRunning() {
        return ScoProcessingSrv.isScoOn();  // mAudioManager.isBluetoothScoOn();
    }

    public void notifyAboutScoStateChanged() {
        if (isScoProcessingRunning()) {
            mNotificationManager.notify(NotifyFactory.ID_NOTIFY, mNotifyFactory.getNotification(NotifyFactory.EventType.SCO_SERVICE_RUN));
        } else btListenerStateNotify(true);
        observeNotifier.scoStateChanged(SCO);
		if (Prefs.IS_SCO_WIDGET_ENABLED.getBool()) context.sendBroadcast(new Intent(ACTION_SCO_WIDGET_UPDATE));
		if (Prefs.IS_BT_LISTENER_WIDGET_ENABLED.getBool()) context.sendBroadcast(new Intent(ACTION_BT_LISTENER_WIDGET_UPDATE));
	}

    public void notifyAboutBtListenerStateChanged() {
        observeNotifier.scoStateChanged(BT_LISTENER);
        if (Prefs.IS_BT_LISTENER_WIDGET_ENABLED.getBool()) context.sendBroadcast(new Intent(ACTION_BT_LISTENER_WIDGET_UPDATE));
    }

    public void sendNotificationAboutBtListener() {
        if (!isScoProcessingRunning()) {
            if (isNeedToNotifyAboutBtListener()) {
                btListenerStateNotify(true);
            } else {
                btListenerStateNotify(false);
            }
        }
    }

    private boolean isNeedToNotifyAboutBtListener() {
        return (isBluetoothAvailable() || !Prefs.IS_NOTIFY_BT_SERVICE_IF_BT_ADAPTER_IS_ON.getBool());
    }

    public void btListenerStateNotify(boolean isBtAdapterOn) {
        if (isBtListenerRunning() && isBtAdapterOn) {
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
        if (isBtListenerRunning() && !isBluetoothAvailable() && restartBtListener == null) {
            BtListenerSrv.startService(App.getContext());
        }
    }

    public void setCheckBtAdapterIsOnOption(boolean isNeeded) {
        Prefs.IS_CHECK_BT_ADAPTER_IS_ON_OPTION.set(isNeeded);
    }

    private boolean isBluetoothAvailable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());
    }

    public void menuCall() {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

	private void requestEnableBt(Context context) {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(enableBtIntent);
	}
}
