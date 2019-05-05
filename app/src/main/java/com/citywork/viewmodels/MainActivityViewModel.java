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
import com.citywork.service.TimerService;
import com.citywork.utils.Calculator;
import com.citywork.utils.CityManager;
import com.citywork.utils.CityUtils;
import com.citywork.utils.NotificationUtils;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.chart.StatusticUtils;
import com.citywork.utils.commonutils.ListUtils;
import com.citywork.utils.timer.TimerBase;
import com.citywork.utils.timer.TimerState;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
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

    private TimerBase timerBase;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private Context context;
    private DBHelper dataBaseHelper;
    private CityManager cityManager;
    private StatusticUtils statusticUtils;
    private NotificationUtils notificationUtils;
    private CityUtils cityUtils;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainActivityViewModel() {
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        context = App.getsAppComponent().getApplicationContext();
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        cityManager = App.getsAppComponent().getPomdoromManager();
        statusticUtils = App.getsAppComponent().getStatisticsUtils();

        //TODO INJECT
        notificationUtils = new NotificationUtils(context);
        cityUtils = new CityUtils();
    }

    @Override
    public void onCreate() {
        compositeDisposable.add(dataBaseHelper.loadCities()
                .doOnSuccess(cities -> {
                    Timber.i("onSuccess city list : %s", cities);
                    statusticUtils.prepareData(cityUtils.getCityList(cities));
                    cityManager.setCityPeopleCount(Calculator.calculatePeopleCount(cities));
                })
                .map(cities -> {
                    City lastCity = ListUtils.getLastElement(cities);
                    cityManager.setCity(lastCity);

                    cityManager.setBuilding(ListUtils.getLastElement(cityManager.getLastcity().getBuildings()));

                    return cityManager.getBuilding();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(building -> {
                    buildingMutableLiveData.postValue(building);
                }, Timber::e));

        timerBase = App.getsAppComponent().getTimerManager();
    }

    @Override
    public void closeNotifications() {
        notificationUtils.closeTimerNotification();
        notificationUtils.closeAlarmNotification();
    }

    @Override
    public void onStop() {
        if (sharedPrefensecUtils.getInNotifBar()) {
            Timber.i(cityManager.getPomodoro().toString());
            if (cityManager.getPomodoro().getTimerState() != TimerState.REST_ONGOING &&
                    cityManager.getPomodoro().getTimerState() == TimerState.ONGOING) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(TimerService.getIntent(context, cityManager.getBuilding()));
                } else {
                    context.startService(TimerService.getIntent(context, cityManager.getBuilding()));
                }
            }
        }

        compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public long getTimeToGo() {
        return timerBase.getRemainingTime();
    }

    @Override
    public void onServiceConnected(Pomodoro pomodoro) {
        dataBaseHelper.savePomodoro(pomodoro);
    }

    @Override
    public boolean isFirstRun() {
        return sharedPrefensecUtils.isFirstRun();
    }
}
