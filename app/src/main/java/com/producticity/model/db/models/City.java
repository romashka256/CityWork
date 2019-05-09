package com.producticity.model.db.models;

import android.support.annotation.Nullable;

import com.producticity.RealmListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.util.Date;
import java.util.Objects;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.com_producticity_model_db_models_CityRealmProxy;
import lombok.Getter;
import lombok.Setter;

@Parcel(implementations = {com_producticity_model_db_models_CityRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {Pomodoro.class})
public class City extends RealmObject {

    public City() {
        buildings = new RealmList<>();
        date = new Date(System.currentTimeMillis());
    }

    public City(Date date) {
        this.date = date;
        buildings = new RealmList<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(getId(), city.getId()) &&
                Objects.equals(getBuildings(), city.getBuildings()) &&
                Objects.equals(getDate(), city.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBuildings(), getDate());
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("City{" +
                "id=" + id + "\n Buildings : \n");
        for (Building building : buildings) {
            s.append(building.toString()).append("\n");
        }

        s.append(", date=").append(date).append('}');

        return s.toString();
    }
}
