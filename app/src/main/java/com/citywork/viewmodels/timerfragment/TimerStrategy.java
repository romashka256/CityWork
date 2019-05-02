package com.citywork.viewmodels.timerfragment;

import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;

public interface TimerStrategy {
    void onTick(ITimerFragmentViewModel timerFragmentViewModel);
    void onComplete(ITimerFragmentViewModel timerFragmentViewModel);
    void onCancel(ITimerFragmentViewModel timerFragmentViewModel);
}
