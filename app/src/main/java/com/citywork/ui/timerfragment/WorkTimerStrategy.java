package com.citywork.ui.timerfragment;

import com.citywork.ui.interfaces.ITimerFragment;

public class WorkTimerStrategy implements TimerStrategy {


    @Override
    public void showUI(ITimerFragment timerFragment) {
        timerFragment.showTimerongoingView();
    }

    @Override
    public void completeEvent(ITimerFragment timerFragment) {
        timerFragment.showSuccessDialog();
        timerFragment.showRestView();
    }
}
