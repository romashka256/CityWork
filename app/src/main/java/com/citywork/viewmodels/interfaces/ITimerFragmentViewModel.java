package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;
import android.util.Pair;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;

public interface ITimerFragmentViewModel {

    void onStartClicked();

    void onStopClicked();

    void on5MinRestClicked();

    void on10MinRestClicked();

    void onSuccessDialogShowed();

    void onDebugBtnClicked();

    void onWorkTimerTick(long time);

    void onWorkTimerComplete();

    void onWorkTImerCancel();

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
