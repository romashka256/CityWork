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
import java.util.Random;

public class ChartUtils {

    private final int dayDivider = 4;

    public ArrayList<IBarDataSet> getDataForToday(int colorResource, int colorHighlight) {
        ArrayList<BarEntry> values = new ArrayList<>();

        Date date;// given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance

        int curhour;

//        for (Building building : city.getBuildings()) {
//            date = new Date(building.getPomodoro().getStarttime());
//            calendar.setTime(date);
//
//            curhour = calendar.get(Calendar.HOUR_OF_DAY);
//
//            for (int i = dayDivider; i < 24; i += dayDivider) {
//                curhour -= dayDivider;
//                if (curhour >= dayDivider) {
//                    continue;
//                } else {
//                    values.add(new BarEntry(i, new Random().nextInt(100)));
//                    break;
//                }
//            }
//        }

        int index = 0;

        for (int o = dayDivider; o <= 24; o += dayDivider) {
            values.add(new BarEntry(o, 0));
        }

        for (int i = 0; i < 10; i++) {
            curhour = new Random().nextInt(24);
            for (int o = dayDivider; o < 24; o += dayDivider) {
                index++;
                curhour -= dayDivider;
                if (curhour >= dayDivider) {
                    continue;
                } else {
                    values.get(index).setY(values.get(index).getY() + new Random().nextInt(50));
                    index = 0;
                    break;
                }
            }
        }

        BarDataSet set1;

        set1 = new BarDataSet(values, "");
        set1.setDrawIcons(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        set1.setDrawValues(false);
        set1.setStackLabels(null);
        set1.setHighLightColor(colorHighlight);
        set1.setColor(colorResource);
        set1.setLabel("");

        return dataSets;
    }

}
