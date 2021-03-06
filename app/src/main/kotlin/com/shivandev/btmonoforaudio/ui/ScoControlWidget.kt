package com.shivandev.btmonoforaudio.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.shivandev.btmonoforaudio.R
import com.shivandev.btmonoforaudio.common.App
import com.shivandev.btmonoforaudio.common.Prefs

import roboguice.receiver.RoboAppWidgetProvider
import android.bluetooth.BluetoothAdapter

/**
 * Implementation of App Widget functionality.
 */

open class ScoControlWidget : RoboAppWidgetProvider() {
    protected val context: Context = App.getContext()
    protected val ACTION_SWITCH_SCO_FROM_WIDGET: String = context.getPackageName() + "switch_sco_from_widget"
//    open val ACTION_SCO_WIDGET_UPDATE: String = "com.shivandev.btmonoforaudio.action_sco_widget_update"

    inline protected val thisAppWidget: ComponentName = ComponentName(context.getPackageName(), javaClass.getName())

    override fun onHandleUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        val views = RemoteViews(context.getPackageName(), R.layout.sco_control_widget)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        Prefs.IS_SCO_WIDGET_ENABLED.set(true)
    }

    override fun onDisabled(context: Context) {
        Prefs.IS_SCO_WIDGET_ENABLED.set(false)
    }

    open fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, views: RemoteViews) {
        // Construct the RemoteViews object of ImageButton
        if (Controller.isScoProcessingRunning()) {
            views.setImageViewResource(R.id.widgets_btn_switchSco, R.drawable.bluetooth_on)
        } else {
            views.setImageViewResource(R.id.widgets_btn_switchSco, R.drawable.bluetooth_off)
        }
        val actionIntent = Intent(context, javaClass<ScoControlWidget>())
        actionIntent.setAction(ACTION_SWITCH_SCO_FROM_WIDGET)
        val pIntent = PendingIntent.getBroadcast(context, 0, actionIntent, 0)
        views.setOnClickPendingIntent(R.id.widgets_btn_switchSco, pIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

//    override fun update(observable: Observable, data: Any?) {
//        if (data is ScoStateObserve.ScoState && data == ScoState.SCO) {
//            updateWidgetByObservableObject(thisAppWidget)
//        }
//    }

    fun updateWidgetByObservableObject(thisAppWidget: ComponentName) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
        onHandleUpdate(context, appWidgetManager, ids)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val controller = Controller()
        super<RoboAppWidgetProvider>.onReceive(context, intent)
        // обрабатываем нажатие кнопки контроля SCO вещания
        val act = intent.getAction()
        when {
            act.equalsIgnoreCase(ACTION_SWITCH_SCO_FROM_WIDGET) -> controller.switchSco(context)
            act.equalsIgnoreCase(Controller.ACTION_SCO_WIDGET_UPDATE) -> updateWidgetByObservableObject(thisAppWidget)
        }
    }
}
