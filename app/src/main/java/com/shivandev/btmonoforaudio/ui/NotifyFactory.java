package com.shivandev.btmonoforaudio.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.R;

public class NotifyFactory {

    @Inject Context context;
    public static final int ID_NOTIFY = 1337;

    public enum EventType {
        SCO_SERVICE_RUN, BT_LISTENER_SERVICE_RUN
    }

    public Notification getNotification(EventType type) {
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

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder.build();
    }
}
