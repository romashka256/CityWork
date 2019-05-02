package com.citywork.model.db;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;
import com.citywork.model.interfaces.OnBuildingsLoadedListener;
import com.citywork.model.interfaces.OnCitiesLoadedListener;
import com.citywork.model.interfaces.OnCityLoadedListener;
import com.citywork.model.interfaces.OnLastBuildingLoadedListener;
import com.citywork.model.interfaces.OnPomodoroLoaded;
import com.citywork.model.interfaces.OnTasksLoadedListener;

import java.util.List;

import io.reactivex.Single;

public interface DBHelper {
    void savePomodoro(Pomodoro pomodoro);

    void saveBuilding(Building building);

    void saveTask(Task task);

    void getLastPomodoro(OnPomodoroLoaded onPomodoroLoaded);

    void getLastBuilding(OnLastBuildingLoadedListener onLastBuildingLoadedListener);

    void getTasks(long timeAfter, OnTasksLoadedListener onTasksLoadedListener);

    void loadAllCompletedBuildings(OnBuildingsLoadedListener onBuildingsLoadedListener);

    void saveCity(City city);

    void loadLastCity(OnCityLoadedListener onCityLoadedListener);

    Single<List<City>> loadCities();
}
