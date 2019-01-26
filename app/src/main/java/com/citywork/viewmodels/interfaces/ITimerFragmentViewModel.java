package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.utils.TimerState;

public interface ITimerFragmentViewModel {
    void onStartClicked();

    void onStopClicked();

    void on5MinRestClicked();

    void on10MinRestClicked();

    void onPause();

    void onResume();

    void onStop();

    long getTimerValue();

    void onTimerValueChanged(long time);

    void pomodoroReceived(Pomodoro pomodoro);

    void buildingReceived(Building building);

    void onServiceConnected(Pomodoro pomodoro);

    LiveData<Long> getChangeTimeEvent();

    LiveData<Building> getTimerCompleteEvent();

    LiveData<Integer> getPeopleCountChangedEvent();

    LiveData<TimerState> getTimerStateChanged();
}
