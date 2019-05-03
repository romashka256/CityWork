package com.citywork.model.db;

import android.os.Build;
import android.view.View;

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
import com.citywork.utils.timer.TimerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
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
                building.setId(new Random().nextLong());
            }

            if (building.getPomodoro().getId() == null) {
                building.getPomodoro().setId(new Random().nextLong());
            }

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

            realm1.copyToRealmOrUpdate(city);
        });
    }

}
