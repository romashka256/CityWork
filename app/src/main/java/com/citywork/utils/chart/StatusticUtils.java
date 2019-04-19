package com.citywork.utils.chart;

import android.util.Pair;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.ui.customviews.LineChart;
import com.citywork.utils.Calculator;
import com.citywork.utils.CityUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

public class StatusticUtils {

    @Getter
    @Setter
    private HashMap<BarModeState, Pair<ArrayList<ChartBar>, HashMap<Integer, List<Building>>>> statisticData;

    @Getter
    private List<City> cities;

    private CityUtils cityUtils;

    @Getter
    @Setter
    private Pair<ArrayList<ChartBar>, HashMap<Integer, List<Building>>> toReturn;

    private final int dayDivider = 4;
    private final int monthDivider = 7;

    public void prepareData(List<City> cityList) {
        this.cities = cityList;

        statisticData = new HashMap<>();

        getDataForToday(getTodayCity());
        getDataForWeek(cities);
        getDataForMonth(cities);

        //Collections.reverse(cityList);
    }

    private City getTodayCity() {
        if (cities != null && !cities.isEmpty() && cities.get(0) != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));
            Calendar cityDate = Calendar.getInstance();
            cityDate.setTime(cities.get(0).getDate());

            if (cityDate.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cityDate.get(Calendar.MONTH)) {
                return cities.get(0);
            } else {
                return new City();
            }
        } else {
            return new City();
        }
    }

    public void getDataForToday(City city) {
        if (city != null) {
            ArrayList<ChartBar> values = new ArrayList<>();
            HashMap<Integer, List<Building>> pomodoroHashMap = new HashMap<>();

            List<List<Building>> pomodoroList = new ArrayList<>();

            Date date;// given date
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance

            int curhour;

            int index = 0;

            for (int o = dayDivider; o <= 24; o += dayDivider) {
                values.add(new ChartBar(0,0));
                pomodoroList.add(new ArrayList<>());
            }

            Pomodoro pomodoro;

            for (Building building : city.getBuildings()) {
                pomodoro = building.getPomodoro();
                date = new Date(pomodoro.getStarttime());
                calendar.setTime(date);

                curhour = calendar.get(Calendar.HOUR_OF_DAY);

                for (int i = dayDivider; i < 24; i += dayDivider) {
                    curhour -= dayDivider;
                    index++;
                    if (curhour >= dayDivider) {
                        continue;
                    } else {
                        values.get(index).setXValue(i);
                        values.get(index).setYValue(values.get(index).getYValue() + Calculator.getTime(building.getPomodoro().getStarttime(), building.getPomodoro().getStoptime()));
                        pomodoroList.get(index).add(building);
                        pomodoroHashMap.put(i, pomodoroList.get(index));
                        index = 0;
                        break;
                    }
                }
            }
            toReturn = new Pair<>(values, pomodoroHashMap);
        } else {
            toReturn = new Pair<>(new ArrayList<>(), new HashMap<>());
        }

        statisticData.put(BarModeState.DAY, toReturn);
    }

    public void getDataForWeek(List<City> cities) {
        List<City> cityList = cities.subList(0, 7);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        ArrayList<ChartBar> values = new ArrayList<>();
        HashMap<Integer, List<Building>> citiesHashmap = new HashMap<>();

        List<List<Building>> week = new ArrayList<>();

        Calendar cityCalendar = Calendar.getInstance();

        for (int o = 1; o <= 7; o++) {
            values.add(new ChartBar(0,0));
            week.add(new ArrayList<>());
        }

        for (int i = 7; i >= 0; i--) {
            calendar.setTime(new Date(System.currentTimeMillis()));
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            for (City city : cityList) {
                cityCalendar.setTime(city.getDate());
                if (cityCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    for (Building building : city.getBuildings()) {
                        values.get(i).setXValue(i);
                        values.get(i).setYValue((values.get(i).getYValue() + new Random().nextInt(1000)));
                    }
                    for (Building building : city.getBuildings()) {
                        week.get(i).add(building);
                        citiesHashmap.put(i, week.get(i));
                    }
                }
            }
        }

        toReturn = new Pair<>(values, citiesHashmap);

        statisticData.put(BarModeState.WEEK, toReturn);
    }

    public void getDataForMonth(List<City> cities) {
        if (cities != null) {
            ArrayList<ChartBar> values = new ArrayList<>();
            HashMap<Integer, List<Building>> pomodoroHashMap = new HashMap<>();

            List<List<Building>> pomodoroList = new ArrayList<>();

            int index = 0;

            for (int o = monthDivider; o <= 28; o += monthDivider) {
                values.add(new ChartBar(0,0));
                pomodoroList.add(new ArrayList<>());
            }

            Pomodoro pomodoro;

            int lastIndex = 0;
            for (int o = monthDivider; o <= 28; o += monthDivider) {
                List<City> sevenDays = cities.subList(lastIndex, lastIndex + monthDivider);

                lastIndex = 0;
                for (City city : sevenDays) {
                    for (Building building : city.getBuildings()) {
                        pomodoro = building.getPomodoro();
                        values.get(index).setXValue(monthDivider);
                        values.get(index).setYValue(values.get(index).getYValue() + new Random().nextInt(1000));
                   //     values.get(index).setYValue(values.get(index).getYValue() + Calculator.getTime(pomodoro.getStarttime(), pomodoro.getStoptime()));
                        pomodoroList.get(index).add(building);
                        pomodoroHashMap.put(o, pomodoroList.get(index));
                        index = 0;
                    }
                }
            }
            toReturn = new Pair<>(values, pomodoroHashMap);
        } else {
            toReturn = new Pair<>(new ArrayList<>(), new HashMap<>());
        }

        statisticData.put(BarModeState.MONTH, toReturn);
    }

    public void getDataForYear(List<City> cities) {
        List<City> cityList = cities.subList(0, 28);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        ArrayList<ChartBar> values = new ArrayList<>();
        HashMap<Integer, List<Building>> citiesHashmap = new HashMap<>();

        List<List<Building>> week = new ArrayList<>();

        Calendar cityCalendar = Calendar.getInstance();

        for (int o = 1; o <= 30; o++) {
            values.add(new ChartBar(0,0));
            week.add(new ArrayList<>());
        }

        for (int i = 30; i >= 0; i--) {
            calendar.setTime(new Date(System.currentTimeMillis()));
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            for (City city : cityList) {
                cityCalendar.setTime(city.getDate());
                if (cityCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    for (Building building : city.getBuildings()) {
                        values.get(i).setXValue(i);
                        values.get(i).setYValue((values.get(i).getYValue() + new Random().nextInt(1000)));
                    }
                    for (Building building : city.getBuildings()) {
                        week.get(i).add(building);
                        citiesHashmap.put(i, week.get(i));
                    }
                }
            }
        }

        toReturn = new Pair<>(values, citiesHashmap);

        statisticData.put(BarModeState.YEAR, toReturn);
    }

}