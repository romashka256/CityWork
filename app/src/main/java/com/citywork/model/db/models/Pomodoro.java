package com.citywork.model.db.models;

import android.support.annotation.Nullable;
import com.citywork.RealmListParcelConverter;
import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;
import io.realm.com_citywork_model_db_models_PomodoroRealmProxy;
import lombok.Getter;
import lombok.Setter;
import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.util.Objects;

@Parcel(implementations = {com_citywork_model_db_models_PomodoroRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {Pomodoro.class})
public class Pomodoro extends io.realm.RealmObject {

    public Pomodoro() {
    }

    public Pomodoro(long starttime, long stoptime) {
        this.starttime = starttime;
        this.stoptime = stoptime;
        this.tasks = new RealmList<>();
        this.completed = false;
    }

    public Pomodoro(long starttime, long stoptime, RealmList<Task> tasks, boolean completed) {
        this.starttime = starttime;
        this.completed = completed;
        this.stoptime = stoptime;
        this.tasks = tasks;
    }

    @PrimaryKey
    @Getter
    int id;
    @Getter
    @Setter
    long starttime;
    @Getter
    @Setter
    long stoptime;

    RealmList<Task> tasks;
    @Getter
    @Setter
    boolean completed;

    @Nullable
    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(@Nullable RealmList<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Pomodoro{" +
                "id=" + id +
                ", starttime=" + starttime +
                ", stoptime=" + stoptime +
                ", tasks=" + tasks +
                ", completed=" + completed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pomodoro pomodoro = (Pomodoro) o;
        return getId() == pomodoro.getId() &&
                getStarttime() == pomodoro.getStarttime() &&
                getStoptime() == pomodoro.getStoptime() &&
                isCompleted() == pomodoro.isCompleted() &&
                Objects.equals(getTasks(), pomodoro.getTasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStarttime(), getStoptime(), getTasks(), isCompleted());
    }
}
