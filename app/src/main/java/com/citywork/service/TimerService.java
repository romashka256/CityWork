package com.citywork.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.citywork.App;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.utils.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.parceler.Parcels;
import timber.log.Timber;

public class TimerService extends Service {

    private final IBinder mBinder = new TimerServiceBinder();

    public final static String TIMERSERVICE_POMODORO = "pomodoro";
    public final static String TIMERSERVICE_TAG = "timerservice";

    private TimerManager mTimerManager;

    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationUtils notificationUtils;
    private AlarmManagerImpl alarmManager;

    private Pomodoro pomodoro;

    private Disposable disposable;

    public class TimerServiceBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    public static Intent getIntent(Context context, Pomodoro pomodoro) {
        Intent intent = new Intent(context, TimerService.class);
        intent.putExtra(TIMERSERVICE_POMODORO, Parcels.wrap(pomodoro));
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
            pomodoro = Parcels.unwrap(intent.getParcelableExtra(TIMERSERVICE_POMODORO));

            startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notificationUtils.buildTimerNotification(Calculator.getMinutesAndSecondsFromSeconds(
                    Calculator.getRemainingTime(pomodoro.getStoptime()))));

//            notificationUtils.showTimerNotification(
//                    );

            long timeRemaining = Calculator.getRemainingTime(pomodoro.getStoptime());

            //  disposable = mTimerManager.startTimer(timeRemaining)
            disposable = mTimerManager.getTimer()
                    .doOnComplete(() -> {
                        notificationUtils.showAlarmNotification();
                        pomodoro.setCompleted(true);
                    })
                    .map(Calculator::getMinutesAndSecondsFromSeconds)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(timeInString -> notificationUtils.updateTimerNotification(timeInString))
                    .subscribe();
        }

        return Service.START_STICKY;
    }

    public Pomodoro getPomodoro() {
        return pomodoro;
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
