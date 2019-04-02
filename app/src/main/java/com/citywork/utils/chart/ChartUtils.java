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
    private Pair<ArrayList<IBarDataSet>, HashMap<Integer, List<Pomodoro>>> toReturn;


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

    public Pair<ArrayList<IBarDataSet>, HashMap<Integer, List<Pomodoro>>> getDataForToday(City city) {
        if (city != null) {
            ArrayList<BarEntry> values = new ArrayList<>();
            HashMap<Integer, List<Pomodoro>> pomodoroHashMap = new HashMap<>();

            List<List<Pomodoro>> pomodoroList = new ArrayList<>();

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
                        pomodoroList.get(index).add(pomodoro);
                        pomodoroHashMap.put(i, pomodoroList.get(index));
                        index = 0;
                        break;
                    }
                }
            }

            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);

            dataSets = new ArrayList<>();
            dataSets.add(set1);

            toReturn = new Pair<>(dataSets, pomodoroHashMap);

            return toReturn;
        } else {
            return null;
        }
    }

    public Pair<ArrayList<IBarDataSet>, HashMap<Integer, List<Pomodoro>>> getDataForWeek(List<City> cities) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        List<City> week = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

        }

        for (int i = 7; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, i);
            for (City city : cities) {

            }
        }

        return toReturn;
    }
}
