package com.citywork.viewmodels.timerfragment;

import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;

import lombok.Setter;

public class TimerStrategyContext {
    @Setter
    private TimerStrategy timerStrategy;

    private void onTick(ITimerFragmentViewModel timerFragmentViewModel){
        timerFragmentViewModel.getBuilding();
    }

}
