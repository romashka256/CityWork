package com.citywork.di.modules;

import com.citywork.utils.timer.Timer;
import com.citywork.utils.timer.TimerBase;
import com.citywork.utils.timer.TimerImpl;
import com.citywork.utils.timer.TimerBaseImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TimerManagerModule {

    @Provides
    @Singleton
    public TimerBase provideTimerManager(Timer timer) {
        return new TimerBaseImpl(timer);
    }

    @Provides
    public Timer provideTimer() {
        return new TimerImpl();
    }

}
