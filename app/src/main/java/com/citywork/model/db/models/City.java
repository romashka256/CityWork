package com.citywork.model.db.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;

public class City extends RealmObject {

    @PrimaryKey
    private int id;
    private RealmList<Building> buildings;
    private Date date;

}
