package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;

import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;

import java.util.List;

public interface ITasksDialogViewModel {
    void onCreate();

    void onPositionChanged();
    void onAddClicked();

    void onChecked();
    void onTaskClicked(Task task);

    void addTask(String text);

    LiveData<List<Pomodoro>> getPomodoroLoadedEvent();

    void onTextChanged(String s);

    void onDismiss();
}
