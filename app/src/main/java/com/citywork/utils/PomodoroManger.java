package com.citywork.utils;

import android.os.Build;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import lombok.Getter;
import lombok.Setter;

public class PomodoroManger {

    @Getter
    @Setter
    public Pomodoro pomodoro;
    @Getter
    @Setter
    public Building building;
    @Setter
    @Getter
    private int peopleCount;

    public void createNewInstance(long timerValue) {
        long startTime = System.currentTimeMillis();
        long stopTime = startTime + timerValue * 1000;

        pomodoro = new Pomodoro(startTime, stopTime);
        building = new Building(pomodoro, calculatePeopleCount(startTime, stopTime));
    }

    public int calculatePeopleCount(long starttime, long stopTime) {
        return Calculator.calculatePeopleCount(Calculator.getTime(starttime, stopTime));
    }

    public int calculatePeopleCount(long time) {
        return Calculator.calculatePeopleCount(time);
    }
}
