package com.shivandev.btmonoforaudio.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.R;

import java.util.Observable;
import java.util.Observer;

import roboguice.receiver.RoboAppWidgetProvider;

/**
 * Implementation of App Widget functionality.
 */
public class ScoControlWidget extends RoboAppWidgetProvider implements Observer{
    private static final String UPDATE_ALL_SCO_WIDGETS = "update_all_sco_widgets";
    @Inject private Controller controller;
	@Inject private Context context;

    public static Intent createUpdateIntent() {
        return new Intent(UPDATE_ALL_SCO_WIDGETS);
//        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onHandleUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        final Controller controller = new Controller();
//        boolean prefIdsChanged = false;
//        HashSet<String> prefIds = Prefs.SCO_WIDGET_IDS.getStrSet();
        for (int appWidgetId : appWidgetIds) {
//            if (!prefIds.contains("" + appWidgetId)) {
//                prefIds.add(""+appWidgetId);
//                prefIdsChanged = true;
//            }
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
//        if (prefIdsChanged) {
//            Prefs.SCO_WIDGET_IDS.set(prefIds);
//        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
		controller.startScoListener(this);
    }

    @Override
    public void onDisabled(Context context) {
		controller.stopScoListener(this);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        // TODO дорабтать обновление картинки виджета. Надо еще прикрутить получение оповещений от Обсервера из контроллера, возможно оно заменит ресивер.
        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_SCO_WIDGETS)) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onHandleUpdate(context, appWidgetManager, ids);
        }
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        if (controller.isScoProcessingRunning()) {
            // Construct the RemoteViews object of ImageButton
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sco_control_widget);
            views.setImageViewResource(R.id.scw_btn_switchSco, R.drawable.bluetooth_on);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

	@Override
	public void update(Observable observable, Object data) {
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		onHandleUpdate(context, appWidgetManager, ids);
	}
}


