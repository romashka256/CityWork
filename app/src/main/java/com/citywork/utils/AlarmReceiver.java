package com.citywork.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {
    public final static String INTENT_MESSAGE_KEY = "intent_message";
    public final static String INTENT_MESSAGE = "show_notification";

    private NotificationUtils notificationUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(INTENT_MESSAGE_KEY);

        Timber.i("onReceive | message : %s", message);

        //TODO INJECT
        notificationUtils = new NotificationUtils(context);

        if (message.equals(INTENT_MESSAGE)) {
            Timber.i("showAlarmNotification");
            notificationUtils.showAlarmNotification();
        }

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }
}
