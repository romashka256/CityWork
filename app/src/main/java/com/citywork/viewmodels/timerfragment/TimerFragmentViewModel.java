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

public class TimerFragmentViewModel extends ViewModel implements ITimerFragmentViewModel {

    private MutableLiveData<Long> changeTimeEvent = new MutableLiveData<>();
    private MutableLiveData<Building> completeEvent = new MutableLiveData<>();
    private MutableLiveData<Integer> peopleCountChange = new MutableLiveData<>();
    private MutableLiveData<Integer> timerStateChangedEvent = new MutableLiveData<>();
    private MutableLiveData<Integer> changeTimeEventInPercent = new MutableLiveData<>();
    private MutableLiveData<String> buidingChanged = new MutableLiveData<>();
    private MutableLiveData<Pair<Integer, Integer>> mProgressPeopleCountChangedEvent = new MutableLiveData<>();
    private MutableLiveData<Integer> cityPeopleCountChangeEvent = new MutableLiveData<>();

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
                timerStateChangedEvent.postValue(TimerState.CANCELED);
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

                timerStateChangedEvent.postValue(TimerState.ONGOING);
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
        startRestTimer(sharedPrefensecUtils.getShortBreak());
    }

    @Override
    public void on10MinRestClicked() {
        startRestTimer(sharedPrefensecUtils.getLongBreak());
    }
     
    private void startRestTimer(long time) {
        startTimer(createTimer(time));
        pomodoroManger.getPomodoro().setStopresttime(System.currentTimeMillis() + (time * 1000));
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
                    changeTimeEvent.postValue(time);
                    timerStrategyContext.onTick(time, this);
                }, e -> {
                    timerStrategyContext.onCancel(this);
                }, () -> {
                    cityPeopleCountChangeEvent.postValue(pomodoroManger.getCityPeopleCount());
                    timerStrategyContext.onComplete(this);

                    saveBuidlingToDB();
                    timerStateChangedEvent.postValue(pomodoroManger.getPomodoro().getTimerState());
                    notificationUtils.closeAlarmNotification();
                }));
    }

    @Override
    public void onWorkTimerTick(long time) {
        percent = Calculator.calculatePercentOfTime(time, Calculator.getTime(pomodoroManger.getPomodoro().getStarttime(), pomodoroManger.getPomodoro().getStoptime()));
        changeTimeEventInPercent.postValue(percent);
        mProgressPeopleCountChangedEvent.postValue(new Pair(pomodoroManger.getBuilding().getPeople_count(), Calculator.calculatePeopleCountByPercent(pomodoroManger.getBuilding().getPeople_count(), percent)));
    }

    @Override
    public void onWorkTimerComplete() {
        saveBuidlingToDB();
        pomodoroManger.createEmptyInstance();
    }

    @Override
    public void onWorkTImerCancel() {
        timerStateChangedEvent.postValue(TimerState.CANCELED);
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
                    timerStateChangedEvent.postValue(TimerState.WORK_COMPLETED);
                }
            } else if (pomodoroManger.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
                if (!((Calculator.getRemainingTime(pomodoroManger.getPomodoro().getStopresttime()) <= 0))) {
                    checkAndStartTimer(pomodoroManger.getPomodoro().getStopresttime());
                } else {
                    pomodoroManger.getPomodoro().setTimerState(TimerState.COMPLETED);
                    dataBaseHelper.savePomodoro(pomodoroManger.getPomodoro());
                    timerStateChangedEvent.postValue(TimerState.COMPLETED);
                }
            }

            timerStateChangedEvent.postValue(pomodoroManger.getPomodoro().getTimerState());

            if (pomodoroManger.getBuilding().getIconName() != null) {
                buidingChanged.postValue(pomodoroManger.getBuilding().getIconName());
            } else {
                buidingChanged.postValue(buildingNames.get(Calculator.calculateBuidling(timerValue)));
            }

            if (pomodoroManger.getPomodoro().getId() != null)
                mAlarmManager.deleteAlarmTask(pomodoroManger.getPomodoro().getId());

            notificationUtils.closeTimerNotification();
            notificationUtils.closeAlarmNotification();
            cityPeopleCountChangeEvent.postValue(pomodoroManger.getCityPeopleCount());
        }
    }

    @Override
    public void onSuccessDialogShowed() {
        pomodoroManger.getPomodoro().setTimerState(TimerState.REST);
        timerStateChangedEvent.postValue(TimerState.REST);
        dataBaseHelper.savePomodoro(pomodoroManger.getPomodoro());
    }

    @Override
    public Building getBuilding() {
        return pomodoroManger.getBuilding();
    }


    @Override
    public void onServiceConnected(Building building) {
        pomodoroManger.setBuilding(building);
        if (building.getPomodoro().getTimerState() == TimerState.WORK_COMPLETED) {
            //TODO CHANGE THIS
            completeEvent.postValue(building);
        } else {
            checkAndStartTimer(building.getPomodoro().getStoptime());
        }
    }

    @Override
    public void onTimerValueChanged(long time) {
        int peopleCount = pomodoroManger.calculatePeopleCount(time);
        if (pomodoroManger.getPeopleCount() != peopleCount) {
            peopleCountChange.postValue(peopleCount);
            pomodoroManger.setPeopleCount(peopleCount);
        }
        String iconName = buildingNames.get(Calculator.calculateBuidling(time));
        if (pomodoroManger.getBuilding().getIconName() == null || !pomodoroManger.getBuilding().getIconName().equals(iconName)) {
            pomodoroManger.getBuilding().setIconName(iconName);
            buidingChanged.postValue(iconName);
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
            peopleCountChange.postValue(building.getPeople_count());

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
        return peopleCountChange;
    }

    @Override
    public LiveData<Integer> getChangeTimeEventInPercent() {
        return changeTimeEventInPercent;
    }


    @Override
    public LiveData<Pair<Integer, Integer>> getProgressPeopleCountChangedEvent() {
        return mProgressPeopleCountChangedEvent;
    }

    @Override
    public LiveData<String> getBuildingChanged() {
        return buidingChanged;
    }

    @Override
    public LiveData<Integer> getCityPeopleCountChangeEvent() {
        return cityPeopleCountChangeEvent;
    }

    @Override
    public LiveData<Long> getChangeTimeEvent() {
        return changeTimeEvent;
    }

    @Override
    public LiveData<Building> getTimerCompleteEvent() {
        return completeEvent;
    }

    @Override
    public LiveData<Integer> getTimerStateChanged() {
        return timerStateChangedEvent;
    }

}
