package com.citywork.utils;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.utils.timer.TimerState;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public class PomodoroManger {

    @Getter
    @Setter
    //TODO INITIATE
    private Pomodoro pomodoro;
    @Getter
    private Building building;
    @Setter
    @Getter
    private int peopleCount;

    public void setBuilding(Building building) {
        this.building = building;
        this.pomodoro = building.getPomodoro();
        this.peopleCount = building.getPeople_count();
    }

    public void createNewInstance(long timerValue) {
        Timber.i("createNewInstance : %d", timerValue);
        long startTime = System.currentTimeMillis();
        long stopTime = startTime + timerValue * 1000;

        pomodoro = new Pomodoro(startTime, stopTime, TimerState.ONGOING);
        building = new Building(pomodoro, calculatePeopleCount(startTime, stopTime));
    }

    public int calculatePeopleCount(long starttime, long stopTime) {
        return Calculator.calculatePeopleCount(Calculator.getTime(starttime, stopTime));
    }

    public int calculatePeopleCount(long time) {
        return Calculator.calculatePeopleCount(time);
    }
}
