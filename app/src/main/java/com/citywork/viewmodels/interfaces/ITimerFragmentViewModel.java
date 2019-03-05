package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;
import android.util.Pair;

import com.citywork.model.db.models.Building;
import com.citywork.utils.timer.TimerState;

public interface ITimerFragmentViewModel {
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

    void onServiceConnected(Building building);

    LiveData<Long> getChangeTimeEvent();

    LiveData<Building> getTimerCompleteEvent();

    LiveData<Integer> getPeopleCountChangedEvent();

    LiveData<TimerState> getTimerStateChanged();

    LiveData<Integer> getChangeTimeEventInPercent();
    LiveData<String> getBuildingChanged();

    LiveData<Pair<Integer, Integer>> getProgressPeopleCountChangedEvent();
}
