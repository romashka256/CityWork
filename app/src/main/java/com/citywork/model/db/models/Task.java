package com.citywork.model.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

public class Task extends RealmObject {

    public Task() {
    }

    public Task(String text) {
        this.text = text;
    }

    @PrimaryKey
    @Getter
    @Setter
    private Integer id;
    @Getter
    @Setter
    private String text;
    @Getter
    @Setter
    private boolean done;

}
