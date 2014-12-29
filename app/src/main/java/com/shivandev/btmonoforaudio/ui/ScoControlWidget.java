package com.shivandev.btmonoforaudio.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.shivandev.btmonoforaudio.R;
import com.shivandev.btmonoforaudio.common.App;

import java.util.Observable;
import java.util.Observer;

import roboguice.receiver.RoboAppWidgetProvider;

/**
 * Implementation of App Widget functionality.
 */
public class ScoControlWidget extends RoboAppWidgetProvider implements Observer{
    final static String ACTION_SWITCH_SCO_FROM_WIDGET = App.getContext().getPackageName()+"switch_sco_from_widget";
	protected Context context = App.getContext();

    @Override
    public void onHandleUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
		Controller.startScoListener(this);
    }

    @Override
    public void onDisabled(Context context) {
		Controller.stopScoListener(this);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object of ImageButton
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sco_control_widget);
        if (Controller.isScoProcessingRunning()) {
            views.setImageViewResource(R.id.scw_btn_switchSco, R.drawable.bluetooth_on);
        } else {
            views.setImageViewResource(R.id.scw_btn_switchSco, R.drawable.bluetooth_off);
        }
        Intent actionIntent = new Intent(context, ScoControlWidget.class);
        actionIntent.setAction(ACTION_SWITCH_SCO_FROM_WIDGET);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, actionIntent, 0);
        views.setOnClickPendingIntent(R.id.scw_btn_switchSco, pIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

	@Override
	public void update(Observable observable, Object data) {
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		onHandleUpdate(context, appWidgetManager, ids);
	}

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        // обрабатываем нажатие кнопки контроля SCO вещания
        if (intent.getAction().equalsIgnoreCase(ACTION_SWITCH_SCO_FROM_WIDGET)) {
            Controller.switchSco(context);
        }
    }
}
