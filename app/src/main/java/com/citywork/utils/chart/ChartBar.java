package com.citywork.utils.chart;

import lombok.Getter;
import lombok.Setter;

public class ChartBar {

    public ChartBar(int yValue) {
        this.yValue = yValue;
    }

    @Getter
    @Setter
    private int yValue;

}
