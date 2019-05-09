package com.producticity.viewmodels.timerfragment;

public class WorkTimerStrategy implements TimerStrategy {

    @Override
    public void onTick(long time, TimerCallbacks timerCallbacks) {
        timerCallbacks.onWorkTimerTick(time);
    }

    @Override
    public void onComplete(TimerCallbacks timerCallbacks) {
        timerCallbacks.onWorkTimerComplete();
    }

    @Override
    public void onCancel(TimerCallbacks timerCallbacks) {
        timerCallbacks.onWorkTImerCancel();
    }
}
