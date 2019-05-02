package com.citywork.di;


import com.citywork.di.modules.TimerManagerModule;
import com.citywork.utils.timer.TimerBase;

import dagger.Component;

import javax.inject.Singleton;

@Component(modules = TimerManagerModule.class)
@Singleton
public interface TimerManagerComponent {
    TimerBase getTimerManger();
}
