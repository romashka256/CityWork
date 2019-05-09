package com.producticity.ui.timerfragment;

import com.producticity.model.db.models.Pomodoro;

import java.util.List;

public interface ITaskDialog {
    void updateList(List<Pomodoro> pomodoroList);
}
