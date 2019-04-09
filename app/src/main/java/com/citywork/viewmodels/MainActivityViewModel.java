package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.Build;

import com.citywork.App;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.interfaces.OnCitiesLoadedListener;
import com.citywork.service.TimerService;
import com.citywork.utils.Calculator;
import com.citywork.utils.CityUtils;
import com.citywork.utils.NotificationUtils;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.chart.StatusticUtils;
import com.citywork.utils.timer.TimerManager;
import com.citywork.utils.timer.TimerState;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel implements IMainActivityViewModel {

    @Getter
    MutableLiveData<Long> mChangeRemainingTimeEvent = new MutableLiveData<>();
    @Getter
    MutableLiveData<Pomodoro> pomodoroMutableLiveData = new MutableLiveData<>();

    MutableLiveData<Building> buildingMutableLiveData = new MutableLiveData<>();

    @Getter
    MutableLiveData<City> cityMutableLiveData = new MutableLiveData<>();

    @Override
    public LiveData<Building> getBuildingLiveData() {
        return buildingMutableLiveData;
    }

    private TimerManager timerManager;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private Context context;
    private DBHelper dataBaseHelper;
    private PomodoroManger pomodoroManger;
    private StatusticUtils statusticUtils;
    private NotificationUtils notificationUtils;
    private CityUtils cityUtils;

    public MainActivityViewModel() {
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        context = App.getsAppComponent().getApplicationContext();
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();
        statusticUtils = App.getsAppComponent().getStatisticsUtils();

        //TODO INJECT
        notificationUtils = new NotificationUtils(context);
        cityUtils = new CityUtils();
    }

    @Override
    public void onCreate() {
        dataBaseHelper.loadCities(cityList -> {
            if (cityList != null) {
                if (!cityList.isEmpty()) {
                    statusticUtils.prepareData(cityUtils.getCityList(cityList));
                    City city = cityList.get(cityList.size() - 1);
                    if (city != null) {
                        Building building = city.getBuildings().get(city.getBuildings().size() - 1);

                        pomodoroManger.setCityPeopleCount(Calculator.calculatePeopleCount(cityList));
                        pomodoroManger.setCity(city);
                        if (building == null ||
                                (building.getPomodoro().getTimerState() == TimerState.CANCELED ||
                                        building.getPomodoro().getTimerState() == TimerState.REST_CANCELED ||
                                        building.getPomodoro().getTimerState() == TimerState.COMPLETED)) {
                            pomodoroManger.createEmptyInstance();
                        } else {
                            pomodoroManger.setBuilding(building);

                            if (Calculator.getRemainingTime(building.getPomodoro().getStoptime()) <= 0) {
                                Timber.i("building.getPomodoro().getStoptime()) <= 0");
                                return;
                            }
                            buildingMutableLiveData.postValue(building);
                        }
                    }
                } else {
                    statusticUtils.prepareData(cityUtils.getCityList(cityList));
                    pomodoroManger.createEmptyInstance();
                }
            }

            timerManager = App.getsAppComponent().getTimerManager();
        });
    }

    @Override
    public void closeNotifications() {
        notificationUtils.closeTimerNotification();
        notificationUtils.closeAlarmNotification();
    }

    @Override
    public void onStop() {
        if (pomodoroManger.getPomodoro().getTimerState() != TimerState.REST_ONGOING && pomodoroManger.getPomodoro() != null && pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
            //  dataBaseHelper.getLastPomodoro(pomodoro -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(TimerService.getIntent(context, pomodoroManger.getBuilding()));
            } else {
                context.startService(TimerService.getIntent(context, pomodoroManger.getBuilding()));
            }
            //  });
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public long getTimeToGo() {
        return timerManager.getRemainingTime();
    }

    @Override
    public void onServiceConnected(Pomodoro pomodoro) {
        dataBaseHelper.savePomodoro(pomodoro);
    }
}
