package com.shivandev.btmonoforaudio.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.google.inject.Inject;

import java.util.List;

public class ServiceUtils {
    @Inject Context context;

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
