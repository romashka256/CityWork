package com.producticity.viewmodels.timerfragment;

public interface TimerCallbacks {
    void onWorkTimerTick(long time);

    void onWorkTimerComplete();

    void onWorkTImerCancel();

    void onRestTimerTick(long time);

    void onRestTimerComplete();

    void onRestTImerCancel();
}
