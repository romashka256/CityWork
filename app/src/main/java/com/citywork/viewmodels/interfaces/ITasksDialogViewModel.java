package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;

import com.citywork.SingleLiveEvent;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;

import java.util.List;

public interface ITasksDialogViewModel {
    void onCreate();
    void onAddClicked();


    void onTaskClicked(Task task);

    SingleLiveEvent<List<Pomodoro>> getPomodoroLoadedEvent();

    SingleLiveEvent<Boolean> getNoTasksEvent();

    SingleLiveEvent<Integer> getUpdatePomodoroListEvent();

    void onTextChanged(String s);

    void onDismiss();

    void onStop();
}
