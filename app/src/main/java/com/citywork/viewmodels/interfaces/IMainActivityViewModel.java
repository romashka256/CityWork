package com.citywork.viewmodels.interfaces;

import com.citywork.model.db.models.Pomodoro;

public interface IMainActivityViewModel {
    void onCreate();
    void onStop();
    void onServiceConnected(Pomodoro pomodoro);
    long getTimeToGo();
}
