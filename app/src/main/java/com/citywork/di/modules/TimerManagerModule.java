package com.citywork.di.modules;

import com.citywork.utils.Timer;
import com.citywork.utils.TimerImpl;
import com.citywork.utils.TimerManager;
import com.citywork.utils.TimerManagerImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

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
