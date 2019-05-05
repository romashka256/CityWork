package com.citywork.model.db.models;

import com.citywork.Constants;
import com.citywork.utils.Calculator;

import org.parceler.Parcel;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.com_citywork_model_db_models_BuildingRealmProxy;
import lombok.Getter;
import lombok.Setter;

@Parcel(implementations = {com_citywork_model_db_models_BuildingRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {Building.class})
public class Building extends RealmObject {

    public Building(Pomodoro pomodoro, int people_count, String iconName) {
        this.pomodoro = pomodoro;
        this.iconName = iconName;
        this.people_count = people_count;
    }

    public Building(Pomodoro pomodoro, String iconName) {
        this.pomodoro = pomodoro;
        this.iconName = iconName;
        this.people_count = Calculator.calculatePeopleCount(Constants.DEFAULT_MIN_TIMER_VALUE);
    }

    public Building(Pomodoro pomodoro) {
        this.pomodoro = pomodoro;
        this.people_count = Calculator.calculatePeopleCount(Constants.DEFAULT_MIN_TIMER_VALUE);
    }

    public Building() {
    }

    @PrimaryKey
    @Setter
    @Getter
    private String id;
    @Getter
    @Setter
    private Pomodoro pomodoro;
    @Getter
    @Setter
    private int people_count;
    @Getter
    @Setter
    String iconName;
    @Getter
    @Setter
    String cityIconName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return getPeople_count() == building.getPeople_count() &&
                Objects.equals(getId(), building.getId()) &&
                Objects.equals(getPomodoro(), building.getPomodoro()) &&
                Objects.equals(getIconName(), building.getIconName()) &&
                Objects.equals(getCityIconName(), building.getCityIconName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPomodoro(), getPeople_count(), getIconName(), getCityIconName());
    }

    @Override
    public String toString() {
        return "Building{" +
                "id='" + id + '\'' +
                ", pomodoro=" + pomodoro +
                ", people_count=" + people_count +
                ", iconName='" + iconName + '\'' +
                ", cityIconName='" + cityIconName + '\'' +
                '}';
    }
}
