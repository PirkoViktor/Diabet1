package com.pirko.diabetolog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import Data.Notification;

/**
 * Created by Jaison on 17/06/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                LocalData localData = new LocalData(context);
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min());
                return;
            }
        }
        String title ="", text="";
        LocalData localData = new LocalData(context);

        title="Час прийому ліків.";
                text="Заплановано: 10 одиниць. Примітка: Прийняти таблетки";



        Log.d(TAG, ": ");

        //Trigger the notification
        NotificationScheduler.showNotification(context, ActivitySettings.class,
                title, text);

    }
}


