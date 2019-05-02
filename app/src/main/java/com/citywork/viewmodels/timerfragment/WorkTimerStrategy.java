package com.citywork.viewmodels.timerfragment;

import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;

public class WorkTimerStrategy implements TimerStrategy {

    @Override
    public void onTick(long time, ITimerFragmentViewModel timerFragmentViewModel) {
        timerFragmentViewModel.onWorkTimerTick(time);
    }

    @Override
    public void onComplete(ITimerFragmentViewModel timerFragmentViewModel) {
        timerFragmentViewModel.onWorkTimerComplete();
    }

    @Override
    public void onCancel(ITimerFragmentViewModel timerFragmentViewModel) {
        timerFragmentViewModel.onWorkTImerCancel();
    }
}
