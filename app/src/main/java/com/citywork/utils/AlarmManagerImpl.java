package com.citywork.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import timber.log.Timber;

public class AlarmManagerImpl {

    private AlarmManager alarmManager;
    private Context context;

    public static final int UNIQUE_REQUEST_CODE = 0;

    public AlarmManagerImpl(Context context) {
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.context = context;
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.INTENT_MESSAGE_KEY, AlarmReceiver.INTENT_MESSAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, UNIQUE_REQUEST_CODE,
                intent,0);
        return pendingIntent;
    }

    public void setAlarmForTime(long time) {
        Timber.i("alarm time set : %d", time);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, getPendingIntent());
    }

    public void deleteAlarmTask() {
        Timber.i("Canceling Alarm");
        alarmManager.cancel(getPendingIntent());
    }

}
