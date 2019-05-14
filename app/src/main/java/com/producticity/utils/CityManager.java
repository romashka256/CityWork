package com.producticity.utils;

import com.producticity.model.db.models.Building;
import com.producticity.model.db.models.City;
import com.producticity.model.db.models.Pomodoro;
import com.producticity.model.db.models.Task;
import com.producticity.utils.timer.TimerState;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class CityManager {

    @Getter
    private Pomodoro pomodoro;
    @Getter
    private Building building;
    @Getter
    private int peopleCount;
    @Getter
    private City lastcity;
    @Getter
    @Setter
    private int cityPeopleCount = 0;

    //set todays city. or create new if first run.
    public void setCity(City city) {
        this.lastcity = city;
        if (lastcity == null) {
            createNewCity(null);
        }
    }

    public void setBuilding(Building building) {
        if (building != null) {
            this.building = building;
            this.pomodoro = building.getPomodoro();
            this.peopleCount = building.getPeople_count();
        } else {
            createEmptyBuildingInstance();
        }
    }

    public int getPomodoroState() {
        if (pomodoro != null) {
            return pomodoro.getTimerState();
        } else {
            return -1;
        }
    }

    /**
     * @param task - new task
     *             If city is "old" then create new city.
     *             For what ? 'cause if we will add  without creating new city this building will attach to the old city.
     *             Only tasks can make city levitate without date. Only after some building will attach city will get a date.
     */

    public void addTask(Task task) {
        if (changeCity(System.currentTimeMillis())) {
            createNewCity(null);
        }

        building.getPomodoro().getTasks().add(task);
    }

    //Create a new city
    public void createNewCity(Date date) {
        lastcity = new City(date);
    }

    //Create a new instance of building
    public void createEmptyBuildingInstance() {
        pomodoro = new Pomodoro(TimerState.NOT_ONGOING);
        building = new Building(pomodoro);
    }

    public boolean deleteBuildingFromOldCity() {
        long startTime = System.currentTimeMillis();
        if (changeCity(startTime)) {
            lastcity.getBuildings().remove(building);
            return true;
        }
        return false;
    }

    public void setTimeToPomodoro(long timerValue) {
        long startTime = System.currentTimeMillis();
        long stopTime = startTime + timerValue * 1000;

        pomodoro.setStarttime(startTime);
        pomodoro.setStoptime(stopTime);
        building.setPeople_count(calculatePeopleCount(startTime, stopTime));


        if (lastcity.getDate() == null) {
            lastcity.setDate(new Date(System.currentTimeMillis()));
        }

        if (changeCity(startTime)) {
            createNewCity(new Date(System.currentTimeMillis()));
        }

        if (!lastcity.getBuildings().contains(building))
            lastcity.getBuildings().add(building);
    }


    private int calculatePeopleCount(long starttime, long stopTime) {
        return Calculator.calculatePeopleCount(Calculator.getTime(starttime, stopTime));
    }

    public int setComleted() {
        int toreturn = 0;
      //  if (pomodoro.getTimerState() == TimerState.ONGOING && Calculator.getRemainingTime(building.getPomodoro().getStoptime()) <= 0) {
        if (pomodoro.getTimerState() == TimerState.ONGOING) {
            pomodoro.setTimerState(TimerState.WORK_COMPLETED);
            cityPeopleCount += building.getPeople_count();
            toreturn = TimerState.WORK_COMPLETED;
     //   } else if (pomodoro.getTimerState() == TimerState.REST_ONGOING && Calculator.getRemainingTime(building.getPomodoro().getStopresttime()) <= 0) {
        } else if (pomodoro.getTimerState() == TimerState.REST_ONGOING) {
            pomodoro.setTimerState(TimerState.COMPLETED);
            toreturn = TimerState.COMPLETED;
        }

        return toreturn;
    }

    public void setCanceled() {
        if (pomodoro.getTimerState() == TimerState.ONGOING)
            pomodoro.setTimerState(TimerState.CANCELED);
        else
            pomodoro.setTimerState(TimerState.REST_CANCELED);
    }

    public int prepareBeforeStart() {
        if (pomodoro.getTimerState() != TimerState.REST && pomodoro.getTimerState() != TimerState.REST_ONGOING && pomodoro.getTimerState() != TimerState.WORK_COMPLETED) {
            pomodoro.setTimerState(TimerState.ONGOING);
            return TimerState.ONGOING;
        } else {
            pomodoro.setTimerState(TimerState.REST_ONGOING);
            return TimerState.REST_ONGOING;
        }
    }

    private boolean changeCity(long compareTime) {
        boolean result = false;
        Calendar curcalendar = Calendar.getInstance();
        Calendar newcalendar = Calendar.getInstance();

        if (lastcity.getDate() == null) {
            return false;
        }

        curcalendar.setTime(lastcity.getDate());
        newcalendar.setTime(new Date(compareTime));

        if (curcalendar.get(Calendar.DAY_OF_MONTH) != newcalendar.get(Calendar.DAY_OF_MONTH) && curcalendar.get(Calendar.YEAR) == newcalendar.get(Calendar.YEAR)) {
            result = true;
        }

        return result;
    }

    public void setIconsForBuilding(String icon, String cityIcon) {
        building.setIconName(icon);
        building.setCityIconName(cityIcon);
    }
}
