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
import com.citywork.utils.CityManager;
import com.citywork.utils.NotificationUtils;
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

    private CityManager cityManager;

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
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        cityManager = App.getsAppComponent().getPomdoromManager();
        timerStrategyContext = App.getsAppComponent().getTimerStrategyContext();

        //TODO INJECT
        notificationUtils = new NotificationUtils(appContext);
        mAlarmManager = new AlarmManagerImpl(appContext);
        buildingNames = new ArrayList<>();
        cityBuildingNames = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building3_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building3));
                    break;
                case 1:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building2_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building2));
                    break;
                case 2:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building0_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building0));
                    break;
                case 3:
                    buildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building1_white));
                    cityBuildingNames.add(appContext.getResources().getResourceEntryName(R.drawable.icon_building1));
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
                int state = cityManager.prepareBeforeStart();
                Timber.i("onStart with building : %s", cityManager.getBuilding().toString());

                setTimerStrategy(state);
            }
        });
    }

    private void setTimerStrategy(int state) {
        if (state == TimerState.ONGOING) {
            Timber.i("set work strategy");
            timerStrategyContext.setTimerStrategy(new WorkTimerStrategy());
            timerStateChangedEvent.postValue(TimerState.ONGOING);
        } else if (state == TimerState.REST_ONGOING) {
            Timber.i("set rest strategy");
            timerStrategyContext.setTimerStrategy(new RestTimerStrategy());
            timerStateChangedEvent.postValue(TimerState.REST_ONGOING);
        }
    }

    @Override
    public void onStartClicked() {
        initAndStartTimer();
    }

    private void initAndStartTimer() {
        if (cityManager.deleteBuildingFromOldCity()) {
            saveCityToDB();
        }
        cityManager.setTimeToPomodoro(timerValue);
        int index = Calculator.calculateBuidling(timerValue);
        cityManager.setIconsForBuilding(buildingNames.get(index), cityBuildingNames.get(index));
        startTimer(createTimer(timerValue));
        saveBuidlingToDB();
        saveCityToDB();
    }

    private void saveBuidlingToDB() {
        dataBaseHelper.saveBuilding(cityManager.getBuilding());
    }

    private void saveCityToDB() {
        dataBaseHelper.saveCity(cityManager.getLastcity());
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
        cityManager.getPomodoro().setStopresttime(System.currentTimeMillis() + (time * 1000));
        cityManager.getPomodoro().setReststarttime(System.currentTimeMillis());
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
                    timerStrategyContext.onComplete(this);
                }));
    }

    private void baseOnCompleteAct() {
        cityManager.setComleted();
        saveBuidlingToDB();

        timerStateChangedEvent.postValue(cityManager.getPomodoro().getTimerState());
        notificationUtils.closeAlarmNotification();
    }

    @Override
    public void onWorkTimerTick(long time) {
        percent = Calculator.calculatePercentOfTime(time, Calculator.getTime(cityManager.getPomodoro().getStarttime(), cityManager.getPomodoro().getStoptime()));
        changeTimeEventInPercent.postValue(percent);
        mProgressPeopleCountChangedEvent.postValue(new Pair(cityManager.getBuilding().getPeople_count(), Calculator.calculatePeopleCountByPercent(cityManager.getBuilding().getPeople_count(), percent)));
    }

    @Override
    public void onWorkTimerComplete() {
        Timber.i("onWorkTimerComplete : %s", cityManager.getBuilding().toString());
        baseOnCompleteAct();

        cityPeopleCountChangeEvent.postValue(cityManager.getCityPeopleCount());
    }

    @Override
    public void onWorkTImerCancel() {
        Timber.i("onWorkTImerCancel : %s", cityManager.getBuilding().toString());
        cityManager.setCanceled();
        saveBuidlingToDB();
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
        saveBuidlingToDB();
        createNewInstance();
    }

    private void createNewInstance() {
        cityManager.createEmptyBuildingInstance();
        cityManager.getLastcity().getBuildings().add(cityManager.getBuilding());
        saveBuidlingToDB();
        saveCityToDB();
    }

    @Override
    public void onStopClicked() {
        mTimerBase.stopTimer();
        timerStrategyContext.onCancel(this);
    }

    @Override
    public void onDebugBtnClicked() {
        timerValue = 10;
        initAndStartTimer();
    }

    @Override
    public void onPause() {
        if (cityManager.getPomodoroState() == TimerState.ONGOING) {
            mAlarmManager.setAlarmForTime(cityManager.getPomodoro().getStoptime(), cityManager.getPomodoro().getId());
        }
    }

    @Override
    public void onStop() {
        mTimerBase.pauseTimer();
        compositeDisposable.clear();
    }

    @Override
    public void onResume() {
        cityManager.getPeopleCount();
        if (cityManager.getPomodoro() != null) {
            if (cityManager.getPomodoro().getTimerState() == TimerState.ONGOING) {
                if (!(Calculator.getRemainingTime(cityManager.getPomodoro().getStoptime()) <= 0)) {
                    checkAndStartTimer(cityManager.getPomodoro().getStoptime());
                } else {
                    cityManager.getPomodoro().setTimerState(TimerState.WORK_COMPLETED);
                    dataBaseHelper.savePomodoro(cityManager.getPomodoro());
                    timerStateChangedEvent.postValue(TimerState.WORK_COMPLETED);
                }
            } else if (cityManager.getPomodoro().getTimerState() == TimerState.REST_ONGOING) {
                if (!((Calculator.getRemainingTime(cityManager.getPomodoro().getStopresttime()) <= 0))) {
                    checkAndStartTimer(cityManager.getPomodoro().getStopresttime());
                } else {
                    cityManager.getPomodoro().setTimerState(TimerState.COMPLETED);
                    dataBaseHelper.savePomodoro(cityManager.getPomodoro());
                    timerStateChangedEvent.postValue(TimerState.COMPLETED);
                }
            }

            int state = cityManager.getPomodoro().getTimerState();
            timerStateChangedEvent.postValue(state);
            setTimerStrategy(state);

            if (cityManager.getBuilding().getIconName() != null) {
                buidingChanged.postValue(cityManager.getBuilding().getIconName());
            } else {
                buidingChanged.postValue(buildingNames.get(Calculator.calculateBuidling(timerValue)));
            }

            if (cityManager.getPomodoro().getId() != null)
                mAlarmManager. deleteAlarmTask(cityManager.getPomodoro().getId());

            notificationUtils.closeTimerNotification();
            notificationUtils.closeAlarmNotification();
        } else {
            Timber.i("cityManager.getPomodoro() == null");
        }
        cityPeopleCountChangeEvent.postValue(cityManager.getCityPeopleCount());
    }

    @Override
    public void onSuccessDialogShowed() {
        cityManager.getPomodoro().setTimerState(TimerState.REST);
        timerStateChangedEvent.postValue(TimerState.REST);
        dataBaseHelper.savePomodoro(cityManager.getPomodoro());
    }

    @Override
    public Building getBuilding() {
        return cityManager.getBuilding();
    }

    @Override
    public void onServiceConnected(Building building) {
        Timber.i("onServiceConnected");
//        cityManager.setBuilding(building);
//        if (building.getPomodoro().getTimerState() == TimerState.WORK_COMPLETED) {
//            //TODO CHANGE THIS
//            completeEvent.postValue(building);
//        } else {
//            checkAndStartTimer(building.getPomodoro().getStoptime());
//        }
    }

    @Override
    public void onTimerValueChanged(long time) {
        int peopleCount = Calculator.calculatePeopleCount(time);

        peopleCountChange.postValue(peopleCount);

        int index = Calculator.calculateBuidling(time);
        if (cityManager.getBuilding().getIconName() == null || !cityManager.getBuilding().getIconName().equals(buildingNames.get(index))) {
            cityManager.setIconsForBuilding(buildingNames.get(index), cityBuildingNames.get(index));
            buidingChanged.postValue(buildingNames.get(index));
        }

        timerValue = time;
    }

    @Override
    public void buildingReceived(Building building) {
        Timber.i("buildingReceived");
        if (building != null) {

            if (cityManager.setComleted() == TimerState.COMPLETED) {
                saveBuidlingToDB();
                createNewInstance();
            }

            if (building.getIconName() == null) {
                int index = Calculator.calculateBuidling(timerValue);
                cityManager.setIconsForBuilding(buildingNames.get(index), cityBuildingNames.get(index));
            }

            buidingChanged.setValue(building.getIconName());

            peopleCountChange.setValue(building.getPeople_count());

            timerStateChangedEvent.postValue(cityManager.getPomodoro().getTimerState());

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
