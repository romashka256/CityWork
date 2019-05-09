package com.producticity.utils.chart;

import lombok.Getter;
import lombok.Setter;

public class ChartBar {

    public ChartBar(int yValue, int xValue) {
        this.yValue = yValue;
        this.xValue = xValue;
    }

    @Getter
    @Setter
    private int yValue;

    @Getter
    @Setter
    private int xValue;

}
