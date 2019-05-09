package com.producticity.model.db;

import com.producticity.model.db.models.Building;
import com.producticity.model.db.models.City;
import com.producticity.model.db.models.Pomodoro;
import com.producticity.model.db.models.Task;

import java.util.List;

import io.reactivex.Single;

public interface DBHelper {
    void savePomodoro(Pomodoro pomodoro);

    void saveBuilding(Building building);

    void saveTask(Task task);

    Single<Building> getLastBuilding();

    Single<List<Pomodoro>> getTasks(long timeAfter);

    void saveCity(City city);

    Single<List<City>> loadCities();
}
