package com.citywork.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.citywork.App;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Building;
import com.citywork.utils.AlarmManagerImpl;
import com.citywork.utils.Calculator;
import com.citywork.utils.NotificationUtils;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.timer.TimerManager;
import com.citywork.utils.timer.TimerState;

import org.parceler.Parcels;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TimerService extends Service {

    private final IBinder mBinder = new TimerServiceBinder();

    public final static String TIMERSERVICE_BUILDING = "building";
    public final static String TIMERSERVICE_TAG = "timerservice";

    private TimerManager mTimerManager;

    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationUtils notificationUtils;
    private AlarmManagerImpl alarmManager;
    private PomodoroManger pomodoroManger;
    private DBHelper dbHelper;

    private Building building;

    private Disposable disposable;

    public class TimerServiceBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    public static Intent getIntent(Context context, Building building) {
        Intent intent = new Intent(context, TimerService.class);
        intent.putExtra(TIMERSERVICE_BUILDING, Parcels.wrap(building));
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.i("onCreate");

        System.currentTimeMillis();

        //TODO INJECT
        notificationUtils = new NotificationUtils(getApplicationContext());
        alarmManager = new AlarmManagerImpl(App.getsAppComponent().getApplicationContext());

        dbHelper = App.getsAppComponent().getDataBaseHelper();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();
        mTimerManager = App.getsAppComponent().getTimerManager();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Timber.i("onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("onStartCommand");
        if (intent != null) {
            building = Parcels.unwrap(intent.getParcelableExtra(TIMERSERVICE_BUILDING));

            startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notificationUtils.buildTimerNotification(Calculator.getMinutesAndSecondsFromSeconds(
                    Calculator.getRemainingTime(building.getPomodoro().getStoptime()))));

            disposable = mTimerManager.getTimer()
                    .map(Calculator::getMinutesAndSecondsFromSeconds)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(time -> {
                        notificationUtils.updateTimerNotification(time);
                    }, e -> {
                        if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
                            pomodoroManger.getPomodoro().setTimerState(TimerState.CANCELED);
                        } else if (pomodoroManger.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
                            pomodoroManger.getPomodoro().setTimerState(TimerState.REST_CANCELED);
                        }
                    }, () -> {
                        notificationUtils.showAlarmNotification();
                        if (building.getPomodoro().getTimerState() == TimerState.ONGOING) {
                            building.getPomodoro().setTimerState(TimerState.WORK_COMPLETED);
                        } else if (building.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
                            building.getPomodoro().setTimerState(TimerState.REST_CANCELED);
                        }
                        dbHelper.saveBuilding(pomodoroManger.getBuilding());
                        stopForeground(true);
                    });
        }

        return Service.START_STICKY;
    }

    public void cancelTimer() {
        disposable.dispose();
    }

    public Building getPomodoro() {
        return building;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Timber.i("onTaskRemoved");

        notificationUtils.closeTimerNotification();

        disposable.dispose();
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy");
        if (!disposable.isDisposed())
            disposable.dispose();

        super.onDestroy();
    }
}
