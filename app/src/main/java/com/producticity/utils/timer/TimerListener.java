package com.producticity.utils.timer;

public interface TimerListener {
    void onTimerTick(long time);
    void onTimerComplete();
    void onTimerError();
}
