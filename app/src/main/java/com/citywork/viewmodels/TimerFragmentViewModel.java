package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Pair;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.utils.AlarmManagerImpl;
import com.citywork.utils.Calculator;
import com.citywork.utils.NotificationUtils;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.timer.TimerManager;
import com.citywork.utils.timer.TimerState;
import com.citywork.utils.timer.TimerStateListener;
import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import lombok.Getter;
import timber.log.Timber;

public class

TimerFragmentViewModel extends ViewModel implements ITimerFragmentViewModel {

    MutableLiveData<Long> mChangeTimeEvent = new MutableLiveData<>();
    MutableLiveData<Building> mCompleteEvent = new MutableLiveData<>();
    MutableLiveData<Integer> mPeopleCountChange = new MutableLiveData<>();
    MutableLiveData<TimerState> mTimerStateChangedEvent = new MutableLiveData<>();
    MutableLiveData<Integer> mChangeTimeEventInPercent = new MutableLiveData<>();
    MutableLiveData<Pair<Integer, Integer>> mProgressPeopleCountChangedEvent = new MutableLiveData<>();

    private TimerManager mTimerManager;
    private AlarmManagerImpl mAlarmManager;
    private DBHelper dataBaseHelper;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationUtils notificationUtils;
    private CompositeDisposable compositeDisposable;

    private PomodoroManger pomodoroManger;

    private Integer percent;

    @Getter
    private long timerValue = Constants.DEFAULT_MIN_TIMER_VALUE;

    private Context appContext;

    public TimerFragmentViewModel() {
        mTimerManager = App.getsAppComponent().getTimerManager();
        compositeDisposable = App.getsAppComponent().getCompositeDisposable();
        appContext = App.getsAppComponent().getApplicationContext();
        mAlarmManager = new AlarmManagerImpl(appContext);
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();
        //TODO INJECT
        notificationUtils = new NotificationUtils(appContext);


        mTimerManager.setTimerListener(new TimerStateListener() {
            @Override
            public void onStop() {
                mTimerStateChangedEvent.postValue(TimerState.CANCELED);
            }

            @Override
            public void onResume() {

            }

            @Override
            public void onStart() {
                pomodoroManger.prepareBeforeStart();
                mTimerStateChangedEvent.postValue(TimerState.ONGOING);
            }
        });
    }

    @Override
    public void onStartClicked() {
        initAndStartTimer();
    }

    private void initAndStartTimer() {
        pomodoroManger.setTimeToPomodoro(timerValue);
        dataBaseHelper.saveBuilding(pomodoroManger.getBuilding());
        startTimer(createTimer(timerValue));
    }

    @Override
    public void on5MinRestClicked() {
        startTimer(createTimer(300));
    }

    @Override
    public void on10MinRestClicked() {
        startTimer(createTimer(600));
    }

    private BehaviorSubject<Long> createTimer(long timerTime) {
        Timber.i("createTimer");
        return mTimerManager.startTimer(timerTime);
    }

    private BehaviorSubject<Long> getTimer() {
        return mTimerManager.getTimer();
    }

    private void startTimer(BehaviorSubject<Long> behaviorSubject) {
        compositeDisposable.add(behaviorSubject
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    mChangeTimeEvent.postValue(time);
                    if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
                        percent = Calculator.calculatePercentOfTime(time, Calculator.getTime(pomodoroManger.getPomodoro().getStarttime(), pomodoroManger.getPomodoro().getStoptime()));
                        mChangeTimeEventInPercent.postValue(percent);
                        mProgressPeopleCountChangedEvent.postValue(new Pair(pomodoroManger.getBuilding().getPeople_count(), Calculator.calculatePeopleCountByPercent(pomodoroManger.getBuilding().getPeople_count(), percent)));
                    }
                }, e -> {
                    mTimerStateChangedEvent.postValue(TimerState.CANCELED);
                    pomodoroManger.getPomodoro().setTimerState(TimerState.CANCELED);
                }, () -> {
                    //TODO SHOW WIN DIALOG
                    mTimerStateChangedEvent.postValue(pomodoroManger.setComleted());
                    notificationUtils.closeAlarmNotification();
                }));
    }

    @Override
    public void onStopClicked() {
        mTimerManager.stopTimer();
        mAlarmManager.deleteAlarmTask(pomodoroManger.getPomodoro().getId());
        if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING)
            pomodoroManger.getPomodoro().setTimerState(TimerState.CANCELED);
        else
            pomodoroManger.getPomodoro().setTimerState(TimerState.REST_CANCELED);
        pomodoroManger.createEmptyInstance();
    }

    @Override
    public void onDebugBtnClicked() {
        timerValue = 10;
        initAndStartTimer();
    }

    @Override
    public void onPause() {
        if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
            mAlarmManager.setAlarmForTime(pomodoroManger.getPomodoro().getStoptime(), pomodoroManger.getPomodoro().getId());
        }
    }

    @Override
    public void onStop() {
        mTimerManager.pauseTimer();
        compositeDisposable.clear();
        dataBaseHelper.saveBuilding(pomodoroManger.getBuilding());
    }

    @Override
    public void onResume() {
        if (pomodoroManger.getPomodoro() != null) {
            if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING ||
                    pomodoroManger.getPomodoro().getTimerState() == TimerState.REST_ONGOING)
                checkAndStartTimer(pomodoroManger.getPomodoro());
            mTimerStateChangedEvent.postValue(pomodoroManger.getPomodoro().getTimerState());
            mAlarmManager.deleteAlarmTask(pomodoroManger.getPomodoro().getId());
            notificationUtils.closeTimerNotification();
            notificationUtils.closeAlarmNotification();
        }
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
    public LiveData<TimerState> getTimerStateChanged() {
        return mTimerStateChangedEvent;
    }

    @Override
    public void onServiceConnected(Building building) {
        pomodoroManger.setBuilding(building);
        if (building.getPomodoro().getTimerState() == TimerState.WORK_COMPLETED) {
            //TODO CHANGE THIS
            mCompleteEvent.postValue(building);
        } else {
            checkAndStartTimer(building.getPomodoro());
        }
    }

    @Override
    public void onTimerValueChanged(long time) {
        int peopleCount = pomodoroManger.calculatePeopleCount(time);
        if (pomodoroManger.getPeopleCount() != peopleCount) {
            mPeopleCountChange.postValue(peopleCount);
            pomodoroManger.setPeopleCount(peopleCount);
        }
        timerValue = time;
    }

    @Override
    public void buildingReceived(Building building) {
        pomodoroManger.setBuilding(building);
        mPeopleCountChange.postValue(building.getPeople_count());
        checkAndStartTimer(building.getPomodoro());
    }

    private void checkAndStartTimer(Pomodoro pomodoro) {
        Timber.i("checkAndStartTimer");
        if (mTimerManager.getTimer() == null) {
            startTimer(createTimer(Calculator.getRemainingTime(pomodoro.getStoptime())));
        } else {
            startTimer(getTimer());
        }
    }

    @Override
    public LiveData<Integer> getPeopleCountChangedEvent() {
        return mPeopleCountChange;
    }

    @Override
    public LiveData<Integer> getChangeTimeEventInPercent() {
        return mChangeTimeEventInPercent;
    }


    @Override
    public LiveData<Pair<Integer, Integer>> getProgressPeopleCountChangedEvent() {
        return mProgressPeopleCountChangedEvent;
    }
}
