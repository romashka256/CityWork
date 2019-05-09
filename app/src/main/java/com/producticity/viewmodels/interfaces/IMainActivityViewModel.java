package com.producticity.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;
import com.producticity.model.db.models.Building;
import com.producticity.model.db.models.City;
import com.producticity.model.db.models.Pomodoro;

public interface IMainActivityViewModel {
    void onCreate();
    void onStop();
    void onServiceConnected(Pomodoro pomodoro);
    LiveData<Building> getBuildingLiveData();
    LiveData<City> getCityMutableLiveData();
    long getTimeToGo();
    void closeNotifications();
    void onDestroy();
    boolean isFirstRun();
}
