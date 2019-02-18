package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Pair;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.model.db.DataBaseHelper;
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

public class TimerFragmentViewModel extends ViewModel implements ITimerFragmentViewModel {

    MutableLiveData<Long> mChangeTimeEvent = new MutableLiveData<>();
    MutableLiveData<Building> mCompleteEvent = new MutableLiveData<>();
    MutableLiveData<Integer> mPeopleCountChange = new MutableLiveData<>();
    MutableLiveData<TimerState> mTimerStateChangedEvent = new MutableLiveData<>();
    MutableLiveData<Integer> mChangeTimeEventInPercent = new MutableLiveData<>();
    MutableLiveData<Pair<Integer, Integer>> mProgressPeopleCountChangedEvent = new MutableLiveData<>();

    private TimerManager mTimerManager;
    private AlarmManagerImpl mAlarmManager;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationUtils notificationUtils;
    private CompositeDisposable compositeDisposable;

    private PomodoroManger pomodoroManger;

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
                mTimerStateChangedEvent.postValue(TimerState.NOT_ONGOING);
            }

            @Override
            public void onResume() {

            }

            @Override
            public void onStart() {
                mTimerStateChangedEvent.postValue(TimerState.ONGOING);
            }
        });
    }

    @Override
    public void onStartClicked() {
        initAndStartTimer();
    }

    private void initAndStartTimer() {
        pomodoroManger.createNewInstance(timerValue);
        dataBaseHelper.saveBuilding(pomodoroManger.getBuilding());
        startTimer(createTimer(timerValue, pomodoroManger.getPomodoro()));
    }

    @Override
    public void on5MinRestClicked() {

    }

    @Override
    public void on10MinRestClicked() {

    }

    private BehaviorSubject<Long> createTimer(long timerTime, Pomodoro pomodoro) {
        Timber.i("createTimer");
        return mTimerManager.startTimer(timerTime, pomodoro);
    }

    private BehaviorSubject<Long> getTimer() {
        return mTimerManager.getTimer();
    }

    private void startTimer(BehaviorSubject<Long> behaviorSubject) {
        compositeDisposable.add(behaviorSubject
                .doOnComplete(() -> {
                    //TODO SHOW WIN DIALOG
                    mTimerStateChangedEvent.postValue(TimerState.COMPLETED);
                    notificationUtils.closeTimerNotification();
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    mChangeTimeEvent.postValue(time);
                    mChangeTimeEventInPercent.postValue(Calculator.calculatePercentOfTime(time, Calculator.getTime(pomodoroManger.getPomodoro().getStarttime(), pomodoroManger.getPomodoro().getStoptime())));
                }, Timber::e));
    }

    @Override
    public void onStopClicked() {
        mTimerManager.stopTimer(pomodoroManger.getPomodoro());
        mAlarmManager.deleteAlarmTask();
    }

    @Override
    public void onPause() {
        if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
            mAlarmManager.setAlarmForTime(pomodoroManger.getPomodoro().getStoptime());
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
            mTimerStateChangedEvent.postValue(pomodoroManger.getPomodoro().getTimerState());
            mAlarmManager.deleteAlarmTask();
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
    public void onServiceConnected(Pomodoro pomodoro) {
        pomodoroManger.setPomodoro(pomodoro);
        if (pomodoro.getTimerState() == TimerState.WORK_COMPLETED) {
            //TODO CHANGE THIS
            mCompleteEvent.postValue(new Building(pomodoro, 60));
        } else {
            checkAndStartTimer(pomodoro);
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
        checkAndStartTimer(building.getPomodoro());
    }

    private void checkAndStartTimer(Pomodoro pomodoro) {
        Timber.i("checkAndStartTimer");
        if (mTimerManager.getTimer() == null) {
            startTimer(createTimer(Calculator.getRemainingTime(pomodoro.getStoptime()), pomodoro));
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
