package com.producticity.di;


import com.producticity.di.modules.TimerManagerModule;
import com.producticity.utils.timer.TimerBase;

import dagger.Component;

import javax.inject.Singleton;

@Component(modules = TimerManagerModule.class)
@Singleton
public interface TimerManagerComponent {
    TimerBase getTimerManger();
}
