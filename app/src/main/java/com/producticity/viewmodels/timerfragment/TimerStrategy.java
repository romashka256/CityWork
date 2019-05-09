package com.producticity.viewmodels.timerfragment;

public interface TimerStrategy {
    void onTick(long time, TimerCallbacks timerCallbacks);

    void onComplete(TimerCallbacks timerCallbacks);

    void onCancel(TimerCallbacks timerCallbacks);
}
