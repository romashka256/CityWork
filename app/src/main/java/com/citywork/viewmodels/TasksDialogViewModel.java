package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;
import com.citywork.viewmodels.interfaces.ITasksDialogViewModel;

import java.util.ArrayList;
import java.util.List;

public class TasksDialogViewModel extends ViewModel implements ITasksDialogViewModel {

    private DBHelper dataBaseHelper;
    private MutableLiveData<List<Pomodoro>> newPomodorosEvent = new MutableLiveData<>();
    private List<Pomodoro> pomodoros;

    @Override
    public void onCreate() {

        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();

        dataBaseHelper.getTasks(System.currentTimeMillis() - Constants.DEFAULT_TIME_AFTER_NOT_SHOW
                , pomodoros -> {
                    this.pomodoros = pomodoros;
                    newPomodorosEvent.postValue(pomodoros);
                });
    }


    @Override
    public void onPositionChanged() {

    }

    @Override
    public void onChecked() {

    }

    @Override
    public LiveData<List<Pomodoro>> getPomodoroLoadedEvent() {
        return newPomodorosEvent;
    }

    @Override
    public void addTask(String text) {

    }

    @Override
    public void addDebugTasks() {
        Pomodoro pomodoro = pomodoros.get(pomodoros.size() - 1);
        pomodoro.getTasks().add(new Task("Test task 1"));
        dataBaseHelper.savePomodoro(pomodoro);
        Pomodoro pomodoro1 = pomodoros.get(pomodoros.size() - 2);
        pomodoro1.getTasks().add(new Task("Test task 2"));
        dataBaseHelper.savePomodoro(pomodoro1);
        List<Pomodoro> pomodoroList = new ArrayList<>();
        pomodoroList.add(pomodoro);
        pomodoroList.add(pomodoro1);
        newPomodorosEvent.postValue(pomodoroList);
    }
}