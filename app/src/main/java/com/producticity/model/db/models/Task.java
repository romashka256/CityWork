package com.producticity.model.db.models;

import org.parceler.Parcel;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.com_producticity_model_db_models_TaskRealmProxy;
import lombok.Getter;
import lombok.Setter;

@Parcel(implementations = {com_producticity_model_db_models_TaskRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {Task.class})
public class Task extends RealmObject {

    public Task() {
    }

    public Task(String text) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
    }

    @PrimaryKey
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String text;
    @Getter
    @Setter
    private boolean done;

}
