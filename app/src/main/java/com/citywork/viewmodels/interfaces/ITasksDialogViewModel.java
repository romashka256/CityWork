package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;

import com.citywork.model.db.models.Pomodoro;

import java.util.List;

public interface ITasksDialogViewModel {
    void onCreate();

    void onPositionChanged();

    void onChecked();

    void addTask(String text);

    LiveData<List<Pomodoro>> getPomodoroLoadedEvent();

    void addDebugTasks();

}
