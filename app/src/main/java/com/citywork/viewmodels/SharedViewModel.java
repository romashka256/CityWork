package com.citywork.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.citywork.model.db.models.Pomodoro;

public class SharedViewModel extends ViewModel {
    MutableLiveData<Pomodoro> pomodoroMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Pomodoro> getPomodoroMutableLiveData() {
        return pomodoroMutableLiveData;
    }
}
