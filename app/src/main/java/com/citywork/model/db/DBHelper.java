package com.citywork.model.db;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.interfaces.OnLastBuildingLoadedListener;
import com.citywork.model.interfaces.OnPomodoroLoaded;
import com.citywork.model.interfaces.OnTasksLoadedListener;

public interface DBHelper {
    void savePomodoro(Pomodoro pomodoro);
    void saveBuilding(Building building);
    void getLastPomodoro(OnPomodoroLoaded onPomodoroLoaded);
    void getLastBuilding(OnLastBuildingLoadedListener onLastBuildingLoadedListener);
    void getTasks(long timeAfter, OnTasksLoadedListener onTasksLoadedListener);
}
