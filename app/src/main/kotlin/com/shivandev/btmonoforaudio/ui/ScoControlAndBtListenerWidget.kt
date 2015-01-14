package com.shivandev.btmonoforaudio.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.shivandev.btmonoforaudio.R

import java.util.Observable
import com.shivandev.btmonoforaudio.common.Prefs

/**
 * Implementation of App Widget functionality.
 */
public class ScoControlAndBtListenerWidget : ScoControlWidget() {
    protected val ACTION_SWITCH_BT_LISTENER_FROM_WIDGET: String = context.getPackageName() + "switch_bt_listener_from_widget"

    override fun onHandleUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        val views = RemoteViews(context.getPackageName(), R.layout.sco_control_and_bt_listener_widget)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        Prefs.IS_BT_LISTENER_WIDGET_ENABLED.set(true)
        //        Controller.startScoListener(this)
    }

    override fun onDisabled(context: Context) {
//        Controller.stopScoListener(this)
        Prefs.IS_BT_LISTENER_WIDGET_ENABLED.set(false)
    }

    override fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, views: RemoteViews) {
        // Construct the RemoteViews object of ImageButton
        if (Controller.isBtListenerRunning()) {
            views.setImageViewResource(R.id.scblw_btn_switchBtListenerSrv, R.drawable.bluetooth_phone)
        } else {
            views.setImageViewResource(R.id.scblw_btn_switchBtListenerSrv, R.drawable.bluetooth_off)
        }
        val actionIntent = Intent(context, javaClass<ScoControlAndBtListenerWidget>())
        actionIntent.setAction(ACTION_SWITCH_BT_LISTENER_FROM_WIDGET)
        val pIntent = PendingIntent.getBroadcast(context, 0, actionIntent, 0)
        views.setOnClickPendingIntent(R.id.scblw_btn_switchBtListenerSrv, pIntent)
        super.updateAppWidget(context, appWidgetManager, appWidgetId, views)
    }

    override fun update(observable: Observable, data: Any?) {
        updateWidgetByObservableObject(thisAppWidget)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super<ScoControlWidget>.onReceive(context, intent)
        // обрабатываем нажатие кнопки контроля Сервиса наблюдей за БТ адаптером
        if (intent.getAction().equalsIgnoreCase(ACTION_SWITCH_BT_LISTENER_FROM_WIDGET)) {
            Controller.switchBtListener(context)
        }
    }
}


