package com.citywork.model.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

public class Building extends RealmObject {

    public Building(Pomodoro pomodoro, int people_count) {
        this.pomodoro = pomodoro;
        this.people_count = people_count;
    }

    public Building(Pomodoro pomodoro) {
        this.pomodoro = pomodoro;
    }

    public Building() {
    }

    @PrimaryKey
    @Setter
    @Getter
    private Integer id;
    @Getter
    @Setter
    private Pomodoro pomodoro;
    @Getter
    @Setter
    private int people_count;


}
