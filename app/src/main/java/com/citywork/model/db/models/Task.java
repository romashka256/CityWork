package com.citywork.model.db.models;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.com_citywork_model_db_models_TaskRealmProxy;
import lombok.Getter;
import lombok.Setter;

@Parcel(implementations = {com_citywork_model_db_models_TaskRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {Task.class})
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
