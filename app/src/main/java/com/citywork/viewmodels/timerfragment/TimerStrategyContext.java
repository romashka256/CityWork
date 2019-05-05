package com.citywork.viewmodels.timerfragment;

import lombok.Setter;

public class TimerStrategyContext {

    @Setter
    private TimerStrategy timerStrategy;

    public void onTick(long time, TimerCallbacks timerCallbacks) {
        timerStrategy.onTick(time, timerCallbacks);
    }

    public void onComplete(TimerCallbacks timerCallbacks) {
        timerStrategy.onComplete(timerCallbacks);
    }

    public void onCancel(TimerCallbacks timerCallbacks) {
        timerStrategy.onCancel(timerCallbacks);
    }
}
