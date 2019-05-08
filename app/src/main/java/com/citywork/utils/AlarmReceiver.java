package com.citywork.utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.citywork.App;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Building;
import com.citywork.service.TimerService;
import com.citywork.utils.timer.TimerState;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {
    public final static String INTENT_MESSAGE_KEY = "intent_message";
    public final static String INTENT_POMODORO_ID_KEY = "intent_pomdoro_id";
    public final static String INTENT_MESSAGE = "show_notification";

    private NotificationUtils notificationUtils;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private DBHelper dataBaseHelper;

    private final CompositeDisposable disposables = new CompositeDisposable();

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
            if (sharedPrefensecUtils.getInNotifBar() && !isTimerServiceRunning(context))
                notificationUtils.showAlarmNotification();

            disposables.add(dataBaseHelper.getLastBuilding()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(building -> {
                        building.getPomodoro().setTimerState(TimerState.WORK_COMPLETED);
                        dataBaseHelper.saveBuilding(building);
                    }, throwable -> {

                    }));
        }

        disposables.clear();
    }

    private boolean isTimerServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TimerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
