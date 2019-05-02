package com.citywork.viewmodels.timerfragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Pair;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.R;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.utils.AlarmManagerImpl;
import com.citywork.utils.Calculator;
import com.citywork.utils.NotificationUtils;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.timer.TimerBase;
import com.citywork.utils.timer.TimerState;
import com.citywork.utils.timer.TimerStateListener;
import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

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
    MutableLiveData<Integer> mTimerStateChangedEvent = new MutableLiveData<>();
    MutableLiveData<Integer> mChangeTimeEventInPercent = new MutableLiveData<>();
    MutableLiveData<String> mBuidingChanged = new MutableLiveData<>();
    MutableLiveData<Pair<Integer, Integer>> mProgressPeopleCountChangedEvent = new MutableLiveData<>();
    MutableLiveData<Integer> mCityPeopleCountChangeEvent = new MutableLiveData<>();

    private TimerBase mTimerBase;
    private AlarmManagerImpl mAlarmManager;
    private DBHelper dataBaseHelper;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationUtils notificationUtils;
    private CompositeDisposable compositeDisposable;

    private PomodoroManger pomodoroManger;

    private Integer percent;

    private TimerStrategyContext timerStrategyContext;

    @Getter
    private long timerValue = Constants.DEFAULT_MIN_TIMER_VALUE;

    private Context appContext;
    private List<String> buildingNames;
    private List<String> cityBuildingNames;

    public TimerFragmentViewModel() {
        mTimerBase = App.getsAppComponent().getTimerManager();
        compositeDisposable = App.getsAppComponent().getCompositeDisposable();
        appContext = App.getsAppComponent().getApplicationContext();
        mAlarmManager = new AlarmManagerImpl(appContext);
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();
        timerStrategyContext = new TimerStrategyContext();
        //TODO INJECT
        notificationUtils = new NotificationUtils(appContext);

        buildingNames = new ArrayList<>();
        cityBuildingNames = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building0_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building0));
                    break;
                case 1:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building1_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building1));
                    break;
                case 2:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building2_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building2));
                    break;
                case 3:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building3_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building3));
                    break;
                case 4:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building4_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building4));
                    break;
            }
        }

        mTimerBase.setTimerListener(new TimerStateListener() {
            @Override
            public void onStop() {
                mTimerStateChangedEvent.postValue(TimerState.CANCELED);
            }

            @Override
            public void onResume() {

            }

            @Override
            public void onStart() {
                int state = pomodoroManger.prepareBeforeStart();

                if (state == TimerState.ONGOING) {
                    timerStrategyContext.setTimerStrategy(new WorkTimerStrategy());
                } else {
                    timerStrategyContext.setTimerStrategy(new RestTimerStrategy());
                }

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
        int index = Calculator.calculateBuidling(timerValue);
        pomodoroManger.getBuilding().setIconName(buildingNames.get(index));
        pomodoroManger.getBuilding().setCityIconName(cityBuildingNames.get(index));
        saveBuidlingToDB();
        saveCityToDB();
        startTimer(createTimer(timerValue));
    }

    private void saveBuidlingToDB() {
        dataBaseHelper.saveBuilding(pomodoroManger.getBuilding());
    }

    private void saveCityToDB() {
        dataBaseHelper.saveCity(pomodoroManger.getLastcity());
    }

    @Override
    public void on5MinRestClicked() {
        startTimer(createTimer(sharedPrefensecUtils.getShortBreak()));
        pomodoroManger.getPomodoro().setStopresttime(System.currentTimeMillis() + (sharedPrefensecUtils.getShortBreak() * 1000));
        pomodoroManger.getPomodoro().setReststarttime(System.currentTimeMillis());
        //TODO SAVE POMODORO INSTEAD BUILDING
        saveBuidlingToDB();
    }

    @Override
    public void on10MinRestClicked() {
        startTimer(createTimer(sharedPrefensecUtils.getLongBreak()));
        pomodoroManger.getPomodoro().setStopresttime(System.currentTimeMillis() + (sharedPrefensecUtils.getLongBreak() * 1000));
        pomodoroManger.getPomodoro().setReststarttime(System.currentTimeMillis());
        //TODO SAVE POMODORO INSTEAD BUILDING
        saveBuidlingToDB();
    }

    private BehaviorSubject<Long> createTimer(long timerTime) {
        Timber.i("createTimer");
        return mTimerBase.startTimer(timerTime);
    }

    @Override
    public int getLongBreakValue() {
        return sharedPrefensecUtils.getLongBreak() / 60;
    }

    @Override
    public int getShortBreakValue() {
        return sharedPrefensecUtils.getShortBreak() / 60;
    }

    private BehaviorSubject<Long> getTimer() {
        return mTimerBase.getTimer();
    }

    private void startTimer(BehaviorSubject<Long> behaviorSubject) {
        compositeDisposable.add(behaviorSubject
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    mChangeTimeEvent.postValue(time);

                    timerStrategyContext.onTick(time, this);
                }, e -> {
                    timerStrategyContext.onCancel(this);
                }, () -> {
                    mCityPeopleCountChangeEvent.postValue(pomodoroManger.getCityPeopleCount());

                    timerStrategyContext.onComplete(this);

                    mCityPeopleCountChangeEvent.postValue(pomodoroManger.getCityPeopleCount());
                    saveBuidlingToDB();
                    mTimerStateChangedEvent.postValue(pomodoroManger.getPomodoro().getTimerState());
                    notificationUtils.closeAlarmNotification();
                }));
    }

    @Override
    public void onWorkTimerTick(long time) {
        percent = Calculator.calculatePercentOfTime(time, Calculator.getTime(pomodoroManger.getPomodoro().getStarttime(), pomodoroManger.getPomodoro().getStoptime()));
        mChangeTimeEventInPercent.postValue(percent);
        mProgressPeopleCountChangedEvent.postValue(new Pair(pomodoroManger.getBuilding().getPeople_count(), Calculator.calculatePeopleCountByPercent(pomodoroManger.getBuilding().getPeople_count(), percent)));
    }

    @Override
    public void onWorkTimerComplete() {
        saveBuidlingToDB();
        pomodoroManger.createEmptyInstance();
        saveBuidlingToDB();
    }

    @Override
    public void onWorkTImerCancel() {
        mTimerStateChangedEvent.postValue(TimerState.CANCELED);
        pomodoroManger.getPomodoro().setTimerState(TimerState.CANCELED);
    }

    @Override
    public void onStopClicked() {
        mTimerBase.stopTimer();
        mAlarmManager.deleteAlarmTask(pomodoroManger.getPomodoro().getId());
        pomodoroManger.setCanceled();
        timerStrategyContext.setTimerStrategy(new WorkTimerStrategy());
        saveBuidlingToDB();
        pomodoroManger.createEmptyInstance();
        saveBuidlingToDB();
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
        mTimerBase.pauseTimer();
        compositeDisposable.clear();
        saveBuidlingToDB();
    }

    @Override
    public void onResume() {
        if (pomodoroManger.getPomodoro() != null) {
            if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
                if (!(Calculator.getRemainingTime(pomodoroManger.getPomodoro().getStoptime()) <= 0)) {
                    checkAndStartTimer(pomodoroManger.getPomodoro().getStoptime());
                } else {
                    pomodoroManger.getPomodoro().setTimerState(TimerState.WORK_COMPLETED);
                    dataBaseHelper.savePomodoro(pomodoroManger.getPomodoro());
                    mTimerStateChangedEvent.postValue(TimerState.WORK_COMPLETED);
                }
            } else if (pomodoroManger.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
                if (!((Calculator.getRemainingTime(pomodoroManger.getPomodoro().getStopresttime()) <= 0))) {
                    checkAndStartTimer(pomodoroManger.getPomodoro().getStopresttime());
                } else {
                    pomodoroManger.getPomodoro().setTimerState(TimerState.COMPLETED);
                    dataBaseHelper.savePomodoro(pomodoroManger.getPomodoro());
                    mTimerStateChangedEvent.postValue(TimerState.COMPLETED);
                }
            }

            mTimerStateChangedEvent.postValue(pomodoroManger.getPomodoro().getTimerState());

            if (pomodoroManger.getBuilding().getIconName() != null) {
                mBuidingChanged.postValue(pomodoroManger.getBuilding().getIconName());
            } else {
                mBuidingChanged.postValue(buildingNames.get(Calculator.calculateBuidling(timerValue)));
            }

            if (pomodoroManger.getPomodoro().getId() != null)
                mAlarmManager.deleteAlarmTask(pomodoroManger.getPomodoro().getId());

            notificationUtils.closeTimerNotification();
            notificationUtils.closeAlarmNotification();
            mCityPeopleCountChangeEvent.postValue(pomodoroManger.getCityPeopleCount());
        }
    }

    @Override
    public void onSuccessDialogShowed() {
        pomodoroManger.getPomodoro().setTimerState(TimerState.REST);
        mTimerStateChangedEvent.postValue(TimerState.REST);
        dataBaseHelper.savePomodoro(pomodoroManger.getPomodoro());
    }

    @Override
    public Building getBuilding() {
        return pomodoroManger.getBuilding();
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
    public LiveData<Integer> getTimerStateChanged() {
        return mTimerStateChangedEvent;
    }

    @Override
    public void onServiceConnected(Building building) {
        pomodoroManger.setBuilding(building);
        if (building.getPomodoro().getTimerState() == TimerState.WORK_COMPLETED) {
            //TODO CHANGE THIS
            mCompleteEvent.postValue(building);
        } else {
            checkAndStartTimer(building.getPomodoro().getStoptime());
        }
    }

    @Override
    public void onTimerValueChanged(long time) {
        int peopleCount = pomodoroManger.calculatePeopleCount(time);
        if (pomodoroManger.getPeopleCount() != peopleCount) {
            mPeopleCountChange.postValue(peopleCount);
            pomodoroManger.setPeopleCount(peopleCount);
        }
        String iconName = buildingNames.get(Calculator.calculateBuidling(time));
        if (pomodoroManger.getBuilding().getIconName() == null || !pomodoroManger.getBuilding().getIconName().equals(iconName)) {
            pomodoroManger.getBuilding().setIconName(iconName);
            mBuidingChanged.postValue(iconName);
        }

        timerValue = time;
    }

    @Override
    public void buildingReceived(Building building) {
        if (pomodoroManger.getBuilding() != null) {
            if (building.getPomodoro().getTimerState() == TimerState.ONGOING && Calculator.getRemainingTime(building.getPomodoro().getStoptime()) <= 0) {
                building.getPomodoro().setTimerState(TimerState.WORK_COMPLETED);
            } else if (building.getPomodoro().getTimerState() == TimerState.REST_ONGOING && Calculator.getRemainingTime(building.getPomodoro().getStoptime()) <= 0) {
                building.getPomodoro().setTimerState(TimerState.COMPLETED);
            }

            pomodoroManger.setBuilding(building);
            mPeopleCountChange.postValue(building.getPeople_count());

            if (building.getPomodoro().getTimerState() == TimerState.ONGOING) {
                checkAndStartTimer(building.getPomodoro().getStoptime());
            } else if (building.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
                checkAndStartTimer(building.getPomodoro().getStopresttime());
            }
        }
    }

    @Override
    public void cityReceived(City city) {

    }

    private void checkAndStartTimer(long stoptime) {
        Timber.i("checkAndStartTimer");
        if (!(stoptime <= 0)) {
            if (mTimerBase.getTimer() == null) {
                startTimer(createTimer(Calculator.getRemainingTime(stoptime)));
            } else {
                startTimer(getTimer());
            }
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

    @Override
    public LiveData<String> getBuildingChanged() {
        return mBuidingChanged;
    }

    @Override
    public LiveData<Integer> getCityPeopleCountChangeEvent() {
        return mCityPeopleCountChangeEvent;
    }
}
