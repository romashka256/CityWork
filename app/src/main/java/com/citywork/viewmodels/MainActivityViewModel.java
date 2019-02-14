package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.Build;

import com.citywork.App;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.service.TimerService;
import com.citywork.utils.Calculator;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.timer.TimerManager;
import com.citywork.utils.timer.TimerState;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;

import lombok.Getter;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel implements IMainActivityViewModel {

    @Getter
    MutableLiveData<Long> mChangeRemainingTimeEvent = new MutableLiveData<>();
    @Getter
    MutableLiveData<Pomodoro> pomodoroMutableLiveData = new MutableLiveData<>();

    MutableLiveData<Building> buildingMutableLiveData = new MutableLiveData<>();

    @Override
    public LiveData<Building> getBuildingLiveData() {
        return buildingMutableLiveData;
    }

    private TimerManager timerManager;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private PomodoroManger pomodoroManger;

    public MainActivityViewModel() {
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        context = App.getsAppComponent().getApplicationContext();
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();
    }

    @Override
    public void onCreate() {
            dataBaseHelper.getLastBuilding(building -> {
                if (building == null) {
                    return;
                }

                pomodoroManger.setBuilding(building);

                if (Calculator.getRemainingTime(building.getPomodoro().getStoptime()) <= 0) {
                    Timber.i("building.getPomodoro().getStoptime()) <= 0");
                    return;
                }

                buildingMutableLiveData.postValue(building);
            });

        timerManager = App.getsAppComponent().getTimerManager();
    }

    @Override
    public void onStop() {
        if (pomodoroManger.getPomodoro().getTimerState() == TimerState.ONGOING) {
            dataBaseHelper.getLastPomodoro(pomodoro -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(TimerService.getIntent(context, pomodoro));
                } else {
                    context.startService(TimerService.getIntent(context, pomodoro));
                }
            });
        }
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
