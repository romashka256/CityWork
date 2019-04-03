package com.citywork.utils.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomChartUtils {

    public List<ChartBar> createBars(List<Integer> values) {
        int max = Collections.max(values);

        List<ChartBar> chartBars = new ArrayList<>();

        for (Integer value : values) {
            chartBars.add(new ChartBar((100 * value) / max));
        }

        return chartBars;
    }
}
