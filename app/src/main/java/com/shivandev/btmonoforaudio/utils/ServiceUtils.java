package com.shivandev.btmonoforaudio.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.R;
import com.shivandev.btmonoforaudio.views.MainActivity;

import java.util.List;

public class ServiceUtils {
    public static final int ID_NOTIFY = 1337;
    @Inject Context context;

    public enum NotificationType {
        SCO_SERVICE_RUN, BT_LISTENER_SERVICE_RUN
    }

    public Notification getNotification(NotificationType type) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        switch (type) {
            case SCO_SERVICE_RUN:
                mBuilder.setSmallIcon(R.drawable.bluetooth_on)
                    .setContentTitle("Аудиопоток перенаправлен")
                    .setContentText("на Bluetooth канал");
                break;
            case BT_LISTENER_SERVICE_RUN:
                mBuilder.setSmallIcon(R.drawable.bluetooth_off)
                        .setContentTitle("Ожидание подключения")
                        .setContentText("Bluetooth гарнитуры");
                break;
        }
        mBuilder.setOngoing(true);
        // интент для запуска MainActivity
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //                            PendingIntent.getActivity(ScoProcessingSrv.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        //                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        //                    mNotificationManager.notify(mId, mBuilder.build());

        //                    note.flags|=Notification.FLAG_NO_CLEAR;
        return mBuilder.build();
    }

    public boolean isServiceRunning(String serviceClassName){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
}
