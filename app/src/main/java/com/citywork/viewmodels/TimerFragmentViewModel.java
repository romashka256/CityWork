package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import com.citywork.App;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.utils.*;
import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class TimerFragmentViewModel extends ViewModel implements ITimerFragmentViewModel {

    MutableLiveData<Long> mChangeTimeEvent = new MutableLiveData<>();
    MutableLiveData<Building> mCompleteEvent = new MutableLiveData<>();

    private TimerManager mTimerManager;
    private CompositeDisposable mCompositeDisposable;
    private AlarmManagerImpl mAlarmManager;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationUtils notificationUtils;

    private Pomodoro currentPomodoro;
    private Building currentBuilding;

    private TimerState currentTimerState;

    private long timerValue;

    long startTimerTimeInMillis;
    long stopTimerTimeInMillis;

    private Context appContext;

    public TimerFragmentViewModel() {
        mTimerManager = App.getsAppComponent().getTimerManager();
        mCompositeDisposable = App.getsAppComponent().getCompositeDisposable();
        appContext = App.getsAppComponent().getApplicationContext();
        mAlarmManager = new AlarmManagerImpl(appContext);
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        //TODO INJECT
        notificationUtils = new NotificationUtils(appContext);
    }

    @Override
    public void onStartClicked() {
        if (sharedPrefensecUtils.getTimerState() != TimerState.ONGOING) {
            initAndStartTimer();
        }
    }

    private void initAndStartTimer() {
        startTimerTimeInMillis = System.currentTimeMillis();
        stopTimerTimeInMillis = startTimerTimeInMillis + timerValue * 1000;

        currentPomodoro = new Pomodoro(startTimerTimeInMillis, stopTimerTimeInMillis, null, false);
        currentBuilding = new Building(currentPomodoro, 50);

        dataBaseHelper.savePomodoro(currentPomodoro);

        startTimer(createTimer(timerValue));
    }

    private BehaviorSubject<Long> createTimer(long timerTime) {
        return mTimerManager.startTimer(timerTime);
    }

    private BehaviorSubject<Long> getTimer() {
        return mTimerManager.getTimer();
    }

    private void startTimer(BehaviorSubject<Long> behaviorSubject) {
        mCompositeDisposable.add(behaviorSubject
                .doOnComplete(() -> {
                    //TODO SHOW WIN DIALOG
                    mCompleteEvent.postValue(currentBuilding);
                    notificationUtils.closeTimerNotification();
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> mChangeTimeEvent.postValue(time), Timber::e));
    }

    @Override
    public void onStopClicked() {
        mTimerManager.stopTimer();
        mAlarmManager.deleteAlarmTask();
    }

    @Override
    public void onPause() {
        if (sharedPrefensecUtils.getTimerState() == TimerState.ONGOING) {
            mAlarmManager.setAlarmForTime(stopTimerTimeInMillis);
        }
    }

    @Override
    public void onStop() {
        // mTimerManager.stopTimer();
        mCompositeDisposable.clear();
    }

    @Override
    public void onResume() {
        currentTimerState = sharedPrefensecUtils.getTimerState();
        mAlarmManager.deleteAlarmTask();
        notificationUtils.closeTimerNotification();
        notificationUtils.closeAlarmNotification();
    }

    @Override
    public LiveData<Long> getChangeTimeEvent() {
        return mChangeTimeEvent;
    }

    @Override
    public LiveData<Building> getTimerCompleteEvent() {
        return mCompleteEvent;
    }

    @Override
    public void onServiceConnected(Pomodoro pomodoro) {
        if (pomodoro.isCompleted()) {

        } else {
            if (mTimerManager.getTimer() == null) {
                startTimer(createTimer(Calculator.getRemainingTime(pomodoro.getStoptime())));
            } else {
                startTimer(getTimer());
            }
        }
    }

    @Override
    public void onTimerValueChanged(long time) {
        timerValue = time;
    }
}
