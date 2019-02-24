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

    public void createNewInstanceWithTime(long timerValue) {
        Timber.i("createNewInstance : %d", timerValue);

        long startTime = System.currentTimeMillis();
        long stopTime = startTime + timerValue * 1000;

        pomodoro = new Pomodoro(startTime, stopTime, TimerState.ONGOING);
        building = new Building(pomodoro, calculatePeopleCount(startTime, stopTime));
    }

    public void createEmptyInstance() {
        pomodoro = new Pomodoro(TimerState.NOT_ONGOING);
        building = new Building(pomodoro);
    }

    public void setTimeToPomodoro(long timerValue) {
        long startTime = System.currentTimeMillis();
        long stopTime = startTime + timerValue * 1000;

        pomodoro.setStarttime(startTime);
        pomodoro.setStoptime(stopTime);
        building.setPeople_count(calculatePeopleCount(startTime, stopTime));
    }

    public int calculatePeopleCount(long starttime, long stopTime) {
        return Calculator.calculatePeopleCount(Calculator.getTime(starttime, stopTime));
    }

    public int calculatePeopleCount(long time) {
        return Calculator.calculatePeopleCount(time);
    }

    public TimerState setComleted() {
        if (pomodoro.getTimerState() == TimerState.ONGOING) {
            pomodoro.setTimerState(TimerState.WORK_COMPLETED);
            return TimerState.WORK_COMPLETED;
        } else if (pomodoro.getTimerState() == TimerState.REST_ONGOING) {
            pomodoro.setTimerState(TimerState.COMPLETED);
            createEmptyInstance();
            return TimerState.COMPLETED;
        }

        return TimerState.WORK_COMPLETED;
    }

    public TimerState prepareBeforeStart() {
        if (pomodoro.getTimerState() != TimerState.WORK_COMPLETED) {
            pomodoro.setTimerState(TimerState.ONGOING);
            return TimerState.ONGOING;
        } else {
            pomodoro.setTimerState(TimerState.REST_ONGOING);
            return TimerState.REST_ONGOING;
        }
    }
}
