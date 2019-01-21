package com.citywork.model.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject {

    @PrimaryKey
    private int id;
    private String text;
    private boolean done;

}
