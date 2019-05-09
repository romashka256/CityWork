package com.producticity.utils.chart;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomChartUtils {

    private final float DEFAULT_SPACE_WEIGHT = 1;
    private final float DEFAULT_BAR_WEIGHT = 1;

    public List<ChartBar> createBars(List<ChartBar> values) {
        if (values != null && !values.isEmpty()) {
            List<Integer> integers = new ArrayList<>();
            List<ChartBar> chartBars = new ArrayList<>();
            for (ChartBar chartBar : values) {
                chartBars.add(new ChartBar(chartBar.getYValue(), chartBar.getXValue()));
                integers.add(chartBar.getYValue());
            }

            int max = Collections.max(integers);

            if (max != 0)
                for (int i = 0; i < values.size(); i++) {
                    chartBars.get(i).setYValue((100 * chartBars.get(i).getYValue()) / max);
                }
            return chartBars;
        } else {
            return values;
        }
    }

    public Pair<Integer, Integer> calculateBarAndSpace(int width, int count) {
        Integer barWidth, spaceBetween;

        barWidth = (width / count) / 2;
        spaceBetween = (width / count) - (int) (barWidth * DEFAULT_BAR_WEIGHT) + (barWidth / (count - 1));

        return new Pair<>(barWidth, spaceBetween);
    }
}
