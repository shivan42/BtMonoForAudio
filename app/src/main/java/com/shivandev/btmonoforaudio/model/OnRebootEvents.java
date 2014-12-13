package com.shivandev.btmonoforaudio.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.common.Prefs;
import com.shivandev.btmonoforaudio.views.Controller;

public class OnRebootEvents extends BroadcastReceiver {
    @Inject
    Controller controller;

	@Override
	public void onReceive(Context context, Intent intent) {
        // запускаем сервис-наблюдатель за блютус подключениями, если установлена соответсвующая опция
        if (Prefs.IS_BT_SERVICE_START_AFTER_REBOOT.getBool()) {
            controller.startBtAdapterListener();
        }
	}
}
