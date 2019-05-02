package com.citywork.ui.timerfragment;

import com.citywork.ui.interfaces.ITimerFragment;

public interface TimerStrategy {

    void showUI(ITimerFragment timerFragment);
    void completeEvent(ITimerFragment timerFragment);

}
