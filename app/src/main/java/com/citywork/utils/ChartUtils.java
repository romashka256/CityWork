package com.citywork.utils;

import com.citywork.model.db.models.City;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartUtils {

    private ArrayList<IBarDataSet> getDataForday(City city) {
        ArrayList<BarEntry> values = new ArrayList<>();

        //TODO: CREATE LIST OF DATA

        BarDataSet set1;

        set1 = new BarDataSet(values, "The year 2017");
        set1.setDrawIcons(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return dataSets;
    }

}
