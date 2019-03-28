package com.citywork.utils;

import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ChartUtils {

    private final int dayDivider = 4;

    private ArrayList<IBarDataSet> getDataForday(@NotNull City city) {
        ArrayList<BarEntry> values = new ArrayList<>();

        Date date;// given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance

        int curhour;

        for (Building building : city.getBuildings()) {
            date = new Date(building.getPomodoro().getStarttime());
            calendar.setTime(date);

            curhour = calendar.get(Calendar.HOUR_OF_DAY);

            for (int i = dayDivider; i < 24; i += dayDivider) {
                curhour -= dayDivider;
                if (curhour > dayDivider) {
                    continue;
                } else {
                    values.add(new BarEntry(i, calendar.get(Calendar.HOUR_OF_DAY)));
                }
            }
        }

        BarDataSet set1;

        set1 = new BarDataSet(values, "The year 2017");
        set1.setDrawIcons(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return dataSets;
    }

}
