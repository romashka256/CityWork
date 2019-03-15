package com.citywork.model.db;

import android.arch.lifecycle.LiveData;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;
import com.citywork.model.interfaces.OnBuildingsLoadedListener;
import com.citywork.model.interfaces.OnCityLoadedListener;
import com.citywork.model.interfaces.OnLastBuildingLoadedListener;
import com.citywork.model.interfaces.OnPomodoroLoaded;
import com.citywork.model.interfaces.OnTasksLoadedListener;
import com.citywork.utils.timer.TimerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import timber.log.Timber;

public class DataBaseHelper implements DBHelper {

    public DataBaseHelper() {
    }

    @Override
    public void savePomodoro(Pomodoro pomodoro) {
        try {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
                Task task;
                Number currentIdNum = realm.where(Task.class).max("id");
                Integer nextId = null;
                for (int i = 0; i < pomodoro.getTasks().size(); i++) {
                    task = pomodoro.getTasks().get(i);
                    if (task.getId() == null) {
                        if (currentIdNum == null && nextId == null) {
                            nextId = 1;
                            currentIdNum = 0;
                        } else {
                            nextId = currentIdNum.intValue() + 1;
                        }
                        task.setId(nextId);
                    }
                }
                realm1.copyToRealmOrUpdate(pomodoro);
            });

            realm.close();
        } catch (IllegalStateException e) {
            Timber.e(e);
        } catch (NullPointerException e) {
            Timber.e(e);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void saveTask(Task task) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            realm1.copyToRealmOrUpdate(task);
        });
        realm.close();
    }

    @Override
    public void saveBuilding(Building building) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            if (building.getId() == null) {
                building.setId(System.currentTimeMillis() / 1000);
            }

            if (building.getPomodoro().getId() == null) {
                building.getPomodoro().setId(System.currentTimeMillis());
            }

            realm1.copyToRealmOrUpdate(building);
        });
        realm.close();
    }

    @Override
    public void getLastPomodoro(OnPomodoroLoaded onPomodoroLoaded) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Pomodoro pomodoro = realm1.where(Pomodoro.class).findAll().last();
            onPomodoroLoaded.onLoaded(pomodoro);
            Timber.i("Last pomodoro loaded : %s", pomodoro.toString());
        });
        realm.close();
    }

    @Override
    public void getLastBuilding(OnLastBuildingLoadedListener onLastBuildingLoadedListener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {

            RealmResults<Building> realmResults = realm1.where(Building.class).findAll();
            if (realmResults.isEmpty()) {
                onLastBuildingLoadedListener.OnLastBuildingLoaded(null);
            } else {
                Building building = realm1.copyFromRealm(realmResults.last());
                Timber.i("Last building loaded : %s \n with Pomodoro : %s", building.toString(), building.getPomodoro().toString());
                onLastBuildingLoadedListener.OnLastBuildingLoaded(building);
            }
        });
        realm.close();
    }

    @Override
    public void getTasks(long timeAfter, OnTasksLoadedListener onTasksLoadedListener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            RealmResults<Pomodoro> realmResults = realm1.where(Pomodoro.class).greaterThan("stoptime", timeAfter).findAll();
            List<Pomodoro> pomodoroList = realm1.copyFromRealm(realmResults);
            Pomodoro last = realm1.where(Pomodoro.class).findAll().last();
            if (last != null && pomodoroList.isEmpty()) {
                last = realm1.copyFromRealm(last);
                pomodoroList.add(last);
            }

            if (realmResults != null)
                onTasksLoadedListener.onTasksLoaded(pomodoroList);
            else
                onTasksLoadedListener.onTasksLoaded(new ArrayList<>());
        });
        realm.close();
    }

    @Override
    public void loadAllCompletedBuildings(OnBuildingsLoadedListener onBuildingsLoadedListener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            List<Building> realmList = realm1.copyFromRealm(realm1.where(Building.class)
                    .equalTo("pomodoro.timerState", TimerState.COMPLETED.toString())
                    .findAll());

            onBuildingsLoadedListener.onBuildingsLoaded(realmList);
        });
        realm.close();
    }

    @Override
    public void loadLastCity(OnCityLoadedListener onCityLoadedListener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            if (!realm1.where(City.class).findAll().isEmpty()) {
                City city = realm1.where(City.class).findAll().last();
                if (city != null) {
                    city = realm1.copyFromRealm(city);
                    onCityLoadedListener.onLoadedListener(city);
                } else {
                    onCityLoadedListener.onLoadedListener(null);
                }
            } else {
                onCityLoadedListener.onLoadedListener(null);
            }
        });
        realm.close();
    }

    @Override
    public void saveCity(City city) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            if (city.getId() == null) {
                city.setId(System.currentTimeMillis());
            }

            realm1.copyToRealmOrUpdate(city);
        });
    }
}
