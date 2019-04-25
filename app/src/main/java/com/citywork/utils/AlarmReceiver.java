package com.citywork.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.citywork.App;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Building;
import com.citywork.utils.timer.TimerState;

import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {
    public final static String INTENT_MESSAGE_KEY = "intent_message";
    public final static String INTENT_POMODORO_ID_KEY = "intent_pomdoro_id";
    public final static String INTENT_MESSAGE = "show_notification";

    private NotificationUtils notificationUtils;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private DBHelper dataBaseHelper;
    private Building building;

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(INTENT_MESSAGE_KEY);

        Timber.i("onReceive | message : %s", message);

        //TODO INJECT
        notificationUtils = new NotificationUtils(context);
        sharedPrefensecUtils = new SharedPrefensecUtils(context);
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();

        if (message.equals(INTENT_MESSAGE)) {
            Timber.i("showAlarmNotification");
            if (sharedPrefensecUtils.getInNotifBar())
                notificationUtils.showAlarmNotification();
            dataBaseHelper.getLastBuilding(building -> {
                this.building = building;
            });

            building.getPomodoro().setTimerState(TimerState.WORK_COMPLETED);
            dataBaseHelper.saveBuilding(building);
            //TODO SET POMODORO COMPLETED
        }

//        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(1000);
    }
}
