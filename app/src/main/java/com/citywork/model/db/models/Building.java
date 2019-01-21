package com.citywork.model.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Building extends RealmObject {

    public Building(Pomodoro pomodoro, int people_count) {
        this.pomodoro = pomodoro;
        this.people_count = people_count;
    }

    public Building() {
    }

    @PrimaryKey
    private int id;
    private Pomodoro pomodoro;
    private int people_count;


}
