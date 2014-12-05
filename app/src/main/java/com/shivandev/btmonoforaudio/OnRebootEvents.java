package com.shivandev.btmonoforaudio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnRebootEvents extends BroadcastReceiver {
	public OnRebootEvents() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// обнуляем флаг запуска Блютус наблюдателя, т.к. он не запущен после перезагрузки
		Prefs.IS_BT_LISTENER_RUN.set(false);
	}
}
