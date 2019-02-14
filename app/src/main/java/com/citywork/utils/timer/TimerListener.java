package com.citywork.utils.timer;

public interface TimerListener {
    void onTimerTick(long time);
    void onTimerComplete();
    void onTimerError();
}
