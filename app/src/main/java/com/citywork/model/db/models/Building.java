package com.citywork.model.db.models;

import com.citywork.Constants;
import com.citywork.utils.Calculator;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;
import io.realm.annotations.Required;
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
    private Long id;
    @Getter
    @Setter
    private Pomodoro pomodoro;
    @Getter
    @Setter
    private int people_count;
    @Getter
    @Setter
    String iconName;
}
