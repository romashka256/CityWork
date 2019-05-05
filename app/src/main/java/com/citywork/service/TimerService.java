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
import com.citywork.utils.CityManager;
import com.citywork.utils.NotificationUtils;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.timer.TimerBase;
import com.citywork.utils.timer.TimerState;
import com.citywork.viewmodels.timerfragment.RestTimerStrategy;
import com.citywork.viewmodels.timerfragment.TimerCallbacks;
import com.citywork.viewmodels.timerfragment.TimerStrategyContext;
import com.citywork.viewmodels.timerfragment.WorkTimerStrategy;

import org.parceler.Parcels;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TimerService extends Service implements TimerCallbacks {

    private final IBinder mBinder = new TimerServiceBinder();

    public final static String TIMERSERVICE_BUILDING = "building";
    public final static String TIMERSERVICE_TAG = "timerservice";

    private TimerBase mTimerBase;

    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationUtils notificationUtils;
    private AlarmManagerImpl alarmManager;
    private CityManager cityManager;
    private DBHelper dbHelper;
    private TimerStrategyContext timerStrategyContext;

    private Building building;

    private int buildingImageId;

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
        timerStrategyContext = new TimerStrategyContext();

        dbHelper = App.getsAppComponent().getDataBaseHelper();
        cityManager = App.getsAppComponent().getPomdoromManager();
        mTimerBase = App.getsAppComponent().getTimerManager();
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

            if (building.getPomodoro().getTimerState() == TimerState.ONGOING) {
                Timber.i("set work strategy");
                timerStrategyContext.setTimerStrategy(new WorkTimerStrategy());
            } else if (building.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
                Timber.i("set rest strategy");
                timerStrategyContext.setTimerStrategy(new RestTimerStrategy());
            }

            startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notificationUtils.buildTimerNotification(Calculator.getMinutesAndSecondsFromSeconds(
                    Calculator.getRemainingTime(building.getPomodoro().getStoptime()))));

            buildingImageId = getResources().getIdentifier(building.getCityIconName(), "drawable", getPackageName());

            disposable = mTimerBase.getTimer()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(time -> {
                        timerStrategyContext.onTick(time, this);
                    }, e -> {
                        timerStrategyContext.onCancel(this);
                    }, () -> {
                        timerStrategyContext.onComplete(this);
                        stopForeground(true);
                    });
        }
        return Service.START_STICKY;
    }

    private void baseOnCompleteAct() {
        cityManager.setComleted();
        dbHelper.saveBuilding(cityManager.getBuilding());
    }

    @Override
    public void onWorkTimerTick(long time) {
        int percent = Calculator.calculatePercentOfTime(Calculator.getRemainingTime(building.getPomodoro().getStoptime()), Calculator.getTime(building.getPomodoro().getStarttime(), building.getPomodoro().getStoptime()));
        notificationUtils.updateTimerNotification(Calculator.getMinutesAndSecondsFromSeconds(time), percent, buildingImageId);
    }

    @Override
    public void onWorkTimerComplete() {
        Timber.i("onWorkTimerComplete : %s", cityManager.getBuilding().toString());
        baseOnCompleteAct();
        notificationUtils.showAlarmNotification();
    }

    @Override
    public void onWorkTImerCancel() {
        Timber.i("onWorkTImerCancel : %s", cityManager.getBuilding().toString());
        cityManager.setCanceled();
        dbHelper.saveBuilding(cityManager.getBuilding());
        createNewInstance();
    }

    @Override
    public void onRestTimerTick(long time) {

    }

    @Override
    public void onRestTimerComplete() {
        Timber.i("onRestTimerComplete : %s", cityManager.getBuilding().toString());
        baseOnCompleteAct();
        createNewInstance();
    }

    @Override
    public void onRestTImerCancel() {
        Timber.i("onRestTimerCancel : %s", cityManager.getBuilding().toString());
        cityManager.setCanceled();
        dbHelper.saveBuilding(cityManager.getBuilding());
        createNewInstance();
    }


    private void createNewInstance() {
        cityManager.createEmptyBuildingInstance();
        cityManager.getLastcity().getBuildings().add(cityManager.getBuilding());
        dbHelper.saveBuilding(cityManager.getBuilding());
        dbHelper.saveCity(cityManager.getLastcity());
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
