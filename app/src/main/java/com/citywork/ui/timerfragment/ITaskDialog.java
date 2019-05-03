package com.citywork.ui.timerfragment;

import com.citywork.model.db.models.Pomodoro;

import java.util.List;

public interface ITaskDialog {
    void updateList(List<Pomodoro> pomodoroList);
}
