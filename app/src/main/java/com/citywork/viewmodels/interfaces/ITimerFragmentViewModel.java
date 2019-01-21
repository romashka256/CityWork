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

    void onServiceConnected(Pomodoro pomodoro);

    LiveData<String> getChangeTimeEvent();
    LiveData<Building> getTimerCompleteEvent();

}
