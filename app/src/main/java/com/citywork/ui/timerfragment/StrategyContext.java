package com.citywork.ui.timerfragment;

import com.citywork.ui.interfaces.ITimerFragment;

import lombok.Setter;

public class StrategyContext {

    @Setter
    private TimerStrategy timerStrategy;

    private void completeEvent(ITimerFragment timerFragment){
        timerStrategy.completeEvent(timerFragment);
    }
}
