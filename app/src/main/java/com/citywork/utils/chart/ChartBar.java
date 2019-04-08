package com.citywork.utils.chart;

import lombok.Getter;
import lombok.Setter;

public class ChartBar {

    public ChartBar(Integer yValue, Integer xValue) {
        this.yValue = yValue;
        this.xValue = xValue;
    }

    @Getter
    @Setter
    private Integer yValue;

    @Getter
    @Setter
    private Integer xValue;

}
