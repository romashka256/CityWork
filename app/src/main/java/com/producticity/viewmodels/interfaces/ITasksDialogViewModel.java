package com.producticity.viewmodels.interfaces;

import com.producticity.SingleLiveEvent;
import com.producticity.model.db.models.Pomodoro;
import com.producticity.model.db.models.Task;

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
