package com.citywork.model.db.models;

import android.support.annotation.Nullable;

import com.citywork.RealmListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.com_citywork_model_db_models_CityRealmProxy;
import io.realm.com_citywork_model_db_models_PomodoroRealmProxy;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Parcel(implementations = {com_citywork_model_db_models_CityRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {Pomodoro.class})
public class City extends RealmObject {

    public City() {
        buildings = new RealmList<>();
        date = new Date(System.currentTimeMillis());
    }

    @PrimaryKey
    @Getter
    @Setter
    private Long id;
    @Setter
    private RealmList<Building> buildings;
    @Getter
    @Setter
    private Date date;

    @Nullable
    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<Building> getBuildings() {
        return buildings;
    }
}
