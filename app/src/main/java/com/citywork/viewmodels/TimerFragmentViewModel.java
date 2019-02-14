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
import com.citywork.utils.timer.TimerListener;
import com.citywork.utils.timer.TimerManager;
import com.citywork.utils.timer.TimerState;
import com.citywork.utils.timer.TimerStateListener;
import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;

import io.reactivex.disposables.CompositeDisposable;
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
    private CompositeDisposable mCompositeDisposable;
    private AlarmManagerImpl mAlarmManager;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationUtils notificationUtils;

    private PomodoroManger pomodoroManger;

    @Getter
    private long timerValue = Constants.DEFAULT_MIN_TIMER_VALUE;

    private Context appContext;

    public TimerFragmentViewModel() {
        mTimerManager = App.getsAppComponent().getTimerManager();
        mCompositeDisposable = App.getsAppComponent().getCompositeDisposable();
        appContext = App.getsAppComponent().getApplicationContext();
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();

        //TODO INJECT
        mAlarmManager = new AlarmManagerImpl(appContext);
        notificationUtils = new NotificationUtils(appContext);

        mTimerManager.setTimerStateListener(new TimerStateListener() {
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

        mTimerManager.setTimerListener(new TimerListener() {
            @Override
            public void onTimerTick(long time) {
                mChangeTimeEvent.postValue(time);
                int percent = Calculator.calculatePercentOfTime(time, Calculator.getTime(pomodoroManger.getPomodoro().getStarttime(), pomodoroManger.getPomodoro().getStoptime()));
                mChangeTimeEventInPercent.postValue(percent);

                mProgressPeopleCountChangedEvent.postValue(new Pair<>(pomodoroManger.getPeopleCount(), Calculator.calculatePeopleCountByPercent(pomodoroManger.getPeopleCount(), percent)));
            }

            @Override
            public void onTimerComplete() {
                if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
                    pomodoroManger.getPomodoro().setTimerState(TimerState.WORK_COMPLETED);
                    notificationUtils.closeTimerNotification();
                } else if (pomodoroManger.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
                    pomodoroManger.getPomodoro().setTimerState(TimerState.COMPLETED);
                }
                mTimerStateChangedEvent.postValue(pomodoroManger.getPomodoro().getTimerState());
            }

            @Override
            public void onTimerError() {
                pomodoroManger.getPomodoro().setTimerState(TimerState.NOT_ONGOING);
            }
        });
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStartClicked() {
        if (pomodoroManger.getPomodoro().getTimerState() != TimerState.ONGOING) {
            initAndStartTimer();
        }
    }

    private void initAndStartTimer() {
        pomodoroManger.createNewInstance(timerValue);
        dataBaseHelper.saveBuilding(pomodoroManger.getBuilding());

    }

    @Override
    public void on5MinRestClicked() {
        pomodoroManger.getPomodoro().setTimerState(TimerState.REST_ONGOING);
        startTimer(1800);
    }

    @Override
    public void on10MinRestClicked() {
        pomodoroManger.getPomodoro().setTimerState(TimerState.REST_ONGOING);
        startTimer(3600);
    }

    private void createTimer(long timerTime) {
        Timber.i("createTimer");
        startTimer(timerTime);
    }

    private BehaviorSubject<Long> getTimer() {
        Timber.i("getTimer");
        //Timber.i("isDisposed : " + mTimerManager.isDisposed() + "");
        return mTimerManager.getTimer();
    }

    private void startTimer(long time) {
        mTimerManager.startTimer(time);
    }

    @Override
    public void onStopClicked() {
        mTimerManager.stopTimer();
        mAlarmManager.deleteAlarmTask();
    }

    @Override
    public void onPause() {
        if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
            mAlarmManager.setAlarmForTime(pomodoroManger.getPomodoro().getStoptime());
        } else if (pomodoroManger.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
            mAlarmManager.setAlarmForTime(pomodoroManger.getPomodoro().getStopresttime());
        }
    }

    @Override
    public void onStop() {
        mTimerManager.pauseTimer();
        mCompositeDisposable.clear();
    }

    @Override
    public void onResume() {
        mTimerStateChangedEvent.postValue(pomodoroManger.getPomodoro().getTimerState());
        mAlarmManager.deleteAlarmTask();
        notificationUtils.closeTimerNotification();
        notificationUtils.closeAlarmNotification();

        if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
            checkAndStartTimer(pomodoroManger.getPomodoro());
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

    private void checkAndStartTimer(Pomodoro pomodoro) {

        Timber.i("checkAndStartTimer");
        if (pomodoro != null) {
            if (mTimerManager.getTimer() == null ||
                    mTimerManager.getTimer() == null ||
                    mTimerManager.isDisposed()) {
                startTimer(Calculator.getRemainingTime(pomodoro.getStoptime()));
            } else {
                startTimer(getTimer());
            }
        } else {
            Timber.i("pomodoro = null");
        }
    }

    @Override
    public void buildingReceived(Building building) {
        Timber.i("buildingReceived : " + building.toString());
        pomodoroManger.setBuilding(building);
        if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
            checkAndStartTimer(building.getPomodoro());
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
