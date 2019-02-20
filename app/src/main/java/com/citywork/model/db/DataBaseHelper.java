package com.citywork.model.db;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;
import com.citywork.model.interfaces.OnLastBuildingLoadedListener;
import com.citywork.model.interfaces.OnPomodoroLoaded;
import com.citywork.model.interfaces.OnTasksLoadedListener;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class DataBaseHelper implements DBHelper {

    private Realm realm;

    public DataBaseHelper() {
    }

    @Override
    public void savePomodoro(Pomodoro pomodoro) {
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realm1 -> {
            Task task;
            for (int i = 0; i < pomodoro.getTasks().size() - 1; i++) {
                task = pomodoro.getTasks().get(i);
                if (task.getId() == null) {
                    Number currentIdNum = realm.where(Task.class).max("id");
                    int nextId;
                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1 + i;
                    }
                    task.setId(nextId);
                }
            }
            realm1.copyToRealmOrUpdate(pomodoro);
        }, error -> {
            error.getMessage();
        });
        realm.close();
    }

    @Override
    public void saveBuilding(Building building) {
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            if (building.getId() == null) {
                Number currentIdNum = realm.where(Building.class).max("id");
                int nextId;
                if (currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }

                building.setId(nextId);
            }

            if (building.getPomodoro().getId() == null) {
                Number currentIdNum = realm.where(Pomodoro.class).max("id");
                int nextId;
                if (currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }

                building.getPomodoro().setId(nextId);
            }

            realm1.copyToRealmOrUpdate(building);
        });
        realm.close();
    }

    @Override
    public void getLastPomodoro(OnPomodoroLoaded onPomodoroLoaded) {
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Pomodoro pomodoro = realm1.where(Pomodoro.class).findAll().last();
            onPomodoroLoaded.onLoaded(pomodoro);
            Timber.i("Last pomodoro loaded : %s", pomodoro.toString());
        });
    }

    @Override
    public void getLastBuilding(OnLastBuildingLoadedListener onLastBuildingLoadedListener) {
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm -> {

            RealmResults<Building> realmResults = realm.where(Building.class).findAll();
            if (realmResults.isEmpty()) {
                onLastBuildingLoadedListener.OnLastBuildingLoaded(null);
            } else {
                Building building = realm.copyFromRealm(realmResults.last());
                Timber.i("Last building loaded : %s \n with Pomodoro : %s", building.toString(), building.getPomodoro().toString());
                onLastBuildingLoadedListener.OnLastBuildingLoaded(building);

            }
        });
    }

    @Override
    public void getTasks(long timeAfter, OnTasksLoadedListener onTasksLoadedListener) {
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            RealmResults<Pomodoro> realmResults = realm1.where(Pomodoro.class).greaterThan("stoptime", timeAfter).findAll();
            if (realmResults != null)
                onTasksLoadedListener.onTasksLoaded(realm1.copyFromRealm(realmResults));
            else
                onTasksLoadedListener.onTasksLoaded(new ArrayList<>());
        });
        realm.close();
    }

    @Override
    public void closeDB() {
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
