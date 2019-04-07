package com.citywork.utils;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.utils.timer.TimerState;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class PomodoroManger {

    @Getter
    @Setter
    private Pomodoro pomodoro;
    @Getter
    private Building building;
    @Setter
    @Getter
    private int peopleCount;
    @Getter
    private City lastcity;
    @Getter
    @Setter
    private int cityPeopleCount;

    public void setCity(City city) {
        this.lastcity = city;
        if (city == null)
            createEmptyInstance();
    }

    private Long timerValue;

    public void setBuilding(Building building) {
        this.building = building;
        this.pomodoro = building.getPomodoro();
        this.peopleCount = building.getPeople_count();
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

        if (lastcity == null) {
            lastcity = new City();
        }

        Calendar curcalendar = Calendar.getInstance();
        Calendar newcalendar = Calendar.getInstance();
        curcalendar.setTime(lastcity.getDate());
        newcalendar.setTime(new Date(startTime));

        if (curcalendar.get(Calendar.DAY_OF_YEAR) != newcalendar.get(Calendar.DAY_OF_YEAR) && curcalendar.get(Calendar.YEAR) != newcalendar.get(Calendar.YEAR)) {
            lastcity = new City();
        }

        lastcity.getBuildings().add(building);
    }

    public int calculatePeopleCount(long starttime, long stopTime) {
        return Calculator.calculatePeopleCount(Calculator.getTime(starttime, stopTime));
    }

    public int calculatePeopleCount(long time) {
        return Calculator.calculatePeopleCount(time);
    }

    public TimerState setComleted() {
        TimerState toreturn = null;
        if (pomodoro.getTimerState() == TimerState.ONGOING) {
            pomodoro.setTimerState(TimerState.WORK_COMPLETED);
            toreturn = TimerState.WORK_COMPLETED;
        } else if (pomodoro.getTimerState() == TimerState.REST_ONGOING) {

            pomodoro.setTimerState(TimerState.COMPLETED);
            toreturn = TimerState.COMPLETED;
        }

        cityPeopleCount += building.getPeople_count();

        return toreturn;
    }

    public void setCanceled() {
        if (pomodoro.getTimerState() == TimerState.ONGOING)
            pomodoro.setTimerState(TimerState.CANCELED);
        else
            pomodoro.setTimerState(TimerState.REST_CANCELED);
    }

    public TimerState prepareBeforeStart() {
        if (pomodoro.getTimerState() != TimerState.REST && pomodoro.getTimerState() != TimerState.REST_ONGOING) {
            pomodoro.setTimerState(TimerState.ONGOING);
            return TimerState.ONGOING;
        } else {
            pomodoro.setTimerState(TimerState.REST_ONGOING);
            return TimerState.REST_ONGOING;
        }
    }
}
