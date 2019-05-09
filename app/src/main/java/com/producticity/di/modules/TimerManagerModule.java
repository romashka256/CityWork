package com.producticity.di.modules;

import com.producticity.utils.timer.Timer;
import com.producticity.utils.timer.TimerBase;
import com.producticity.utils.timer.TimerImpl;
import com.producticity.utils.timer.TimerBaseImpl;

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
