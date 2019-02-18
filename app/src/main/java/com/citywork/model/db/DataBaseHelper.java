package com.citywork.model.db;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.interfaces.OnLastBuildingLoadedListener;
import com.citywork.model.interfaces.OnPomodoroLoaded;

import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class DataBaseHelper {

    public void savePomodoro(Pomodoro pomodoro) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(pomodoro));
        realm.close();
    }

    public void saveBuilding(Building building) {
        Realm realm = Realm.getDefaultInstance();
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

            realm1.copyToRealmOrUpdate(building);
        });
        realm.close();
    }

    public void getLastPomodoro(OnPomodoroLoaded onPomodoroLoaded) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Pomodoro pomodoro = realm1.where(Pomodoro.class).findAll().last();
            onPomodoroLoaded.onLoaded(pomodoro);
            Timber.i("Last pomodoro loaded : %s", pomodoro.toString());
        });
        realm.close();
    }

    public void getLastBuilding(OnLastBuildingLoadedListener onLastBuildingLoadedListener) {
        Realm realm1 = Realm.getDefaultInstance();
        realm1.executeTransaction(realm -> {

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
}
