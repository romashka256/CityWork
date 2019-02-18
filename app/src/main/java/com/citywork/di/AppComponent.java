package com.citywork.di;

import android.content.Context;

import com.citywork.di.modules.AppModule;
import com.citywork.di.modules.TimerManagerModule;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.timer.Timer;
import com.citywork.utils.timer.TimerManager;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.disposables.CompositeDisposable;

@Component(modules = {AppModule.class, TimerManagerModule.class})
@Singleton
public interface AppComponent {
    CompositeDisposable getCompositeDisposable();

    TimerManager getTimerManager();

    Context getApplicationContext();

    DataBaseHelper getDataBaseHelper();

    SharedPrefensecUtils getSharedPrefs();

    PomodoroManger getPomdoromManager();

    Timer getTimer();
}
