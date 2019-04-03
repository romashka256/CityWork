package com.citywork.utils.chart;

import android.util.Pair;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.utils.Calculator;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ChartUtils {

    private int color;
    private int selectedColor;

    private BarDataSet set1;
    private ArrayList<IBarDataSet> dataSets;
    private Pair<ArrayList<IBarDataSet>, HashMap<Integer, List<Building>>> toReturn;


    public ChartUtils(int color, int selectedColor) {
        this.color = color;
        this.selectedColor = selectedColor;

        set1 = new BarDataSet(new ArrayList<>(), "");
        set1.setDrawIcons(false);


        set1.setDrawValues(false);
        set1.setStackLabels(null);
        set1.setHighLightColor(selectedColor);
        set1.setColor(color);
        set1.setLabel("");
    }

    private final int dayDivider = 4;

    public Pair<ArrayList<IBarDataSet>, HashMap<Integer, List<Building>>> getDataForToday(City city) {
        if (city != null) {
            ArrayList<BarEntry> values = new ArrayList<>();
            HashMap<Integer, List<Building>> pomodoroHashMap = new HashMap<>();

            List<List<Building>> pomodoroList = new ArrayList<>();

            Date date;// given date
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance

            int curhour;

            int index = 0;

            for (int o = dayDivider; o <= 24; o += dayDivider) {
                values.add(new BarEntry(o, 0));
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
                        values.get(index).setY(values.get(index).getY() + Calculator.getTime(building.getPomodoro().getStarttime(), building.getPomodoro().getStoptime()));
                        pomodoroList.get(index).add(building);
                        pomodoroHashMap.put(i, pomodoroList.get(index));
                        index = 0;
                        break;
                    }
                }
            }

            set1.setValues(values);

            dataSets = new ArrayList<>();
            dataSets.add(set1);

            toReturn = new Pair<>(dataSets, pomodoroHashMap);
        } else {
            toReturn = new Pair<>(new ArrayList<>(), new HashMap<>());
        }
        return toReturn;
    }

    public Pair<ArrayList<IBarDataSet>, HashMap<Integer, List<Building>>> getDataForWeek(List<City> cities) {
        List<City> cityList = cities.subList(0, 7);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        ArrayList<BarEntry> values = new ArrayList<>();
        HashMap<Integer, List<Building>> citiesHashmap = new HashMap<>();

        List<List<Building>> week = new ArrayList<>();

        Calendar cityCalendar = Calendar.getInstance();

        for (int o = 1; o <= 7; o++) {
            values.add(new BarEntry(o * 4, 0));
            week.add(new ArrayList<>());
        }

        for (int i = 7; i >= 0; i--) {
            calendar.setTime(new Date(System.currentTimeMillis()));
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            for (City city : cityList) {
                cityCalendar.setTime(city.getDate());
                if (cityCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    for (Building building : city.getBuildings()) {
                        values.get(i).setY((values.get(i).getY() + new Random().nextInt(1000)));
                    }
                    for (Building building : city.getBuildings()) {
                        week.get(i).add(building);
                        citiesHashmap.put(i, week.get(i));
                    }
                }
            }
        }

        // Collections.reverse(values);

        set1.setValues(values);

        dataSets = new ArrayList<>();
        dataSets.add(set1);

        toReturn = new Pair<>(dataSets, citiesHashmap);

        return toReturn;
    }
}
