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

    private PendingIntent getPendingIntent(String id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.INTENT_MESSAGE_KEY, AlarmReceiver.INTENT_MESSAGE);
        intent.putExtra(AlarmReceiver.INTENT_POMODORO_ID_KEY, id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, UNIQUE_REQUEST_CODE,
                intent, 0);
        return pendingIntent;
    }

    public void setAlarmForTime(long time, String id) {
        Timber.i("alarm time set : %d", time);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, getPendingIntent(id));
    }

    public void deleteAlarmTask(String id) {
        Timber.i("deleteAlarmTask with id : %s", id);
        alarmManager.cancel(getPendingIntent(id));
    }

}
