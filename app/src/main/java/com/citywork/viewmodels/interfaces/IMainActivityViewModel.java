package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.db.models.Pomodoro;

public interface IMainActivityViewModel {
    void onCreate();
    void onStop();
    void onServiceConnected(Pomodoro pomodoro);
    LiveData<Building> getBuildingLiveData();
    LiveData<City> getCityMutableLiveData();
    long getTimeToGo();
    void closeNotifications();
    void onDestroy();

}
