package com.citywork.utils.chart;

import lombok.Getter;
import lombok.Setter;

public class ChartBar {

    public ChartBar(Integer yValue) {
        this.yValue = yValue;
    }

    @Getter
    @Setter
    private Integer yValue;

}
