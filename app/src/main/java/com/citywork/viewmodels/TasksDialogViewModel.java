package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.viewmodels.interfaces.ITasksDialogViewModel;

import java.util.List;

public class TasksDialogViewModel extends ViewModel implements ITasksDialogViewModel {

    private DataBaseHelper dataBaseHelper;
    private MutableLiveData<List<Pomodoro>> mutableLiveData = new MutableLiveData<>();
    private List<Pomodoro> pomodoros;

    @Override
    public void onCreate() {

        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();

        dataBaseHelper.getTasks(System.currentTimeMillis() - Constants.DEFAULT_TIME_AFTER_NOT_SHOW
                , pomodoros -> mutableLiveData.postValue(pomodoros));

    }


    @Override
    public void onPositionChanged() {

    }

    @Override
    public void onChecked() {

    }

    @Override
    public LiveData<List<Pomodoro>> getPomodoroLoadedEvent() {
        return mutableLiveData;
    }

    @Override
    public void addTask(String text) {
        
    }
}