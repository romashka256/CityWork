package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;

public interface ITimerFragmentViewModel {

    void onStartClicked();

    void onStopClicked();

    void onPause();

    void onResume();

    void onStop();

    void onTimerValueChanged(long time);

    void onServiceConnected(Pomodoro pomodoro);

    LiveData<Long> getChangeTimeEvent();
    LiveData<Building> getTimerCompleteEvent();

}
