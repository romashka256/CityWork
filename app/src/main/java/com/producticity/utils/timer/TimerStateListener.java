package com.producticity.utils.timer;

public interface TimerStateListener {
    void onStop();
    void onResume();
    void onStart();
}
