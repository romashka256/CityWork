package com.citywork.model.db;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.interfaces.OnLastBuildingLoadedListener;
import com.citywork.model.interfaces.OnPomodoroLoaded;
import io.realm.Realm;
import timber.log.Timber;

public class DataBaseHelper {

    public void savePomodoro(Pomodoro pomodoro) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(pomodoro));
        realm.close();
    }

    public void saveBuilding(Building building) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(building));
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
            Building building = realm.where(Building.class).findAll().last();
            onLastBuildingLoadedListener.OnLastBuildingLoaded(building);
            Timber.i("Last building loaded : %s", building.toString());
        });
    }
}
