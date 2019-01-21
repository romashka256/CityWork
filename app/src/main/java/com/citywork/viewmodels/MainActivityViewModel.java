package com.citywork.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.Build;
import com.citywork.App;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.service.TimerService;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.TimerManager;
import com.citywork.utils.TimerState;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;

public class MainActivityViewModel extends ViewModel implements IMainActivityViewModel {

    MutableLiveData<Long> mChangeRemainingTimeEvent = new MutableLiveData<>();

    public MutableLiveData<Long> getChangeRemainingTimeEvent() {
        return mChangeRemainingTimeEvent;
    }

    private TimerManager timerManager;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private Context context;
    private DataBaseHelper dataBaseHelper;

    public MainActivityViewModel() {
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        context = App.getsAppComponent().getApplicationContext();
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
    }

    @Override
    public void onCreate() {
        timerManager = App.getsAppComponent().getTimerManager();
    }

    @Override
    public void onStop() {
        if (sharedPrefensecUtils.getTimerState() == TimerState.ONGOING) {
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
