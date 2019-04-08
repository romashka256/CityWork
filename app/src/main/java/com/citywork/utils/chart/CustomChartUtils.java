package com.citywork.utils.chart;

import android.util.Pair;

import com.github.mikephil.charting.charts.Chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

public class CustomChartUtils {

    private final float DEFAULT_SPACE_WEIGHT = 1;
    private final float DEFAULT_BAR_WEIGHT = 1;

    @Getter
    private List<String> daily = new ArrayList<>(Arrays.asList("04:00", "08:00", "12:00", "16:00", "20:00", "24:00"));
    @Getter
    private List<String> weekly = new ArrayList<>(Arrays.asList("пн", "вт", "ср", "чт", "пт", "сб", "вс"));
    @Getter
    private List<String> monthly = new ArrayList<>(Arrays.asList("1 неделя", "2 неделя", "3 неделя", "4 неделя"));

    public List<ChartBar> createBars(List<Integer> values) {
        int max = Collections.max(values);

        List<ChartBar> chartBars = new ArrayList<>();

        for (Integer value : values) {
            chartBars.add(new ChartBar((100 * value) / max, 0));
        }

        return chartBars;
    }

    public Pair<Integer, Integer> calculateBarAndSpace(int width, int count) {
        Integer barWidth, spaceBetween;

        barWidth = (width / count) / 2;
        spaceBetween = (width / count) - (int) (barWidth * DEFAULT_BAR_WEIGHT) + (barWidth / (count - 1));

        return new Pair<>(barWidth, spaceBetween);
    }
}
