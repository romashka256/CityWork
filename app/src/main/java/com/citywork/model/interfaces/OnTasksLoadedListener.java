package com.citywork.model.interfaces;

import com.citywork.model.db.models.Pomodoro;

import java.util.List;

public interface OnTasksLoadedListener {
    void onTasksLoaded(List<Pomodoro> pomodoros);
}
