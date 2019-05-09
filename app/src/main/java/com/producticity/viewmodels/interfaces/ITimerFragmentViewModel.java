package com.producticity.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;
import android.util.Pair;

import com.producticity.model.db.models.Building;
import com.producticity.model.db.models.City;
import com.producticity.viewmodels.timerfragment.TimerCallbacks;

public interface ITimerFragmentViewModel extends TimerCallbacks {

    void onStartClicked();

    void onStopClicked();

    void on5MinRestClicked();

    void on10MinRestClicked();

    void onSuccessDialogShowed();

    void onDebugBtnClicked();

    void onPause();

    void onResume();

    void onStop();

    long getTimerValue();

    Building getBuilding();

    void onTimerValueChanged(long time);

    void buildingReceived(Building building);

    void cityReceived(City building);

    void onServiceConnected(Building building);

    int getLongBreakValue();

    int getShortBreakValue();

    LiveData<Long> getChangeTimeEvent();

    LiveData<Building> getTimerCompleteEvent();

    LiveData<Integer> getPeopleCountChangedEvent();

    LiveData<Integer> getCityPeopleCountChangeEvent();

    LiveData<Integer> getTimerStateChanged();

    LiveData<Integer> getChangeTimeEventInPercent();

    LiveData<String> getBuildingChanged();

    LiveData<Pair<Integer, Integer>> getProgressPeopleCountChangedEvent();

}
