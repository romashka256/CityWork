package com.citywork.model.db;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;
import com.citywork.utils.timer.TimerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class DataBaseHelper implements DBHelper {

    public DataBaseHelper() {
    }

    @Override
    public Single<List<City>> loadCities() {
        return Single.create(emitter -> {
            Realm realm = Realm.getDefaultInstance();

            realm.executeTransaction(realm1 -> {
                List<City> list = realm1.copyFromRealm(realm1.where(City.class).findAll());

                if (list == null) {
                    list = new ArrayList<>();
                }

                emitter.onSuccess(list);
            });
            realm.close();
        });

    }

    @Override
    public void savePomodoro(Pomodoro pomodoro) {
        try {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(realm1 -> {
                if (pomodoro.getId() == null) {
                    pomodoro.setId(UUID.randomUUID().toString());
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
        realm.executeTransactionAsync(realm1 -> {
            realm1.copyToRealmOrUpdate(task);
        });
        realm.close();
    }

    @Override
    public void saveBuilding(Building building) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            if (building.getId() == null) {
                building.setId(UUID.randomUUID().toString());
            }

            if (building.getPomodoro().getId() == null) {
                building.getPomodoro().setId(UUID.randomUUID().toString());
            }

            Timber.i("saving Building : %s", building.toString());
            realm1.copyToRealmOrUpdate(building);
        });
        realm.close();
    }

    @Override
    public Single<Building> getLastBuilding() {
        return Single.create(emitter -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {

                RealmResults<Building> realmResults = realm1.where(Building.class).findAll();
                if (realmResults.isEmpty()) {
                    emitter.onSuccess(null);
                } else {
                    Building building = realm1.copyFromRealm(realmResults.last());
                    Timber.i("Last building loaded : %s \n with Pomodoro : %s", building.toString(), building.getPomodoro().toString());
                    emitter.onSuccess(building);
                }
            });
            realm.close();
        });
    }

    @Override
    public Single<List<Pomodoro>> getTasks(long timeAfter) {
        return Single.create(emitter -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {

                RealmResults<Pomodoro> realmResults = realm1.where(Pomodoro.class).greaterThan("stoptime", timeAfter)
                        .or()
                        .equalTo("timerState", TimerState.NOT_ONGOING)
                        .findAll();

                List<Pomodoro> pomodoroList = realm1.copyFromRealm(realmResults);
                Pomodoro last;
                try {
                    last = realm1.where(Pomodoro.class).findAll().last();
                } catch (IndexOutOfBoundsException e) {
                    last = null;
                }

                if (last != null && pomodoroList.isEmpty()) {
                    last = realm1.copyFromRealm(last);
                    pomodoroList.add(last);
                }

                if (realmResults != null)
                    emitter.onSuccess(pomodoroList);
                else
                    emitter.onSuccess(new ArrayList<>());

            });
            realm.close();
        });
    }


    @Override
    public void saveCity(City city) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            if (city.getId() == null) {
                city.setId(new Random().nextLong());
            }

            Timber.i("save city : %s", city.toString());
            realm1.copyToRealmOrUpdate(city);
        });
    }

}
