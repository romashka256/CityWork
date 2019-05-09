package com.producticity.model.interfaces;

import com.producticity.model.db.models.Pomodoro;

import java.util.List;

public interface OnTasksLoadedListener {
    void onTasksLoaded(List<Pomodoro> pomodoros);
}
