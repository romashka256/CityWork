package com.citywork.di.modules;

import com.citywork.utils.timer.Timer;
import com.citywork.utils.timer.TimerImpl;
import com.citywork.utils.timer.TimerManager;
import com.citywork.utils.timer.TimerManagerImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TimerManagerModule {

    @Provides
    @Singleton
    public TimerManager provideTimerManager(Timer timer) {
        return new TimerManagerImpl(timer);
    }

    @Provides
    public Timer provideTimer() {
        return new TimerImpl();
    }

}
