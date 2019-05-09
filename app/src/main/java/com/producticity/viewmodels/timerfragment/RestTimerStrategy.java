package com.producticity.viewmodels.timerfragment;

public class RestTimerStrategy implements TimerStrategy {

    @Override
    public void onTick(long time, TimerCallbacks timerCallbacks) {
        timerCallbacks.onRestTimerTick(time);
    }

    @Override
    public void onComplete(TimerCallbacks timerCallbacks) {
        timerCallbacks.onRestTimerComplete();
    }

    @Override
    public void onCancel(TimerCallbacks timerCallbacks) {
        timerCallbacks.onRestTImerCancel();
    }
}
