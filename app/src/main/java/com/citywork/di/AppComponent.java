package com.citywork.di;

import android.content.Context;

import com.citywork.di.modules.AppModule;
import com.citywork.di.modules.TimerManagerModule;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.utils.commonutils.FontUtils;
import com.citywork.utils.CityManager;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.chart.StatusticUtils;
import com.citywork.utils.timer.Timer;
import com.citywork.utils.timer.TimerBase;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.disposables.CompositeDisposable;

@Component(modules = {AppModule.class, TimerManagerModule.class})
@Singleton
public interface AppComponent {
    CompositeDisposable getCompositeDisposable();

    TimerBase getTimerManager();

    Context getApplicationContext();

    DataBaseHelper getDataBaseHelper();

    SharedPrefensecUtils getSharedPrefs();

    CityManager getPomdoromManager();

    StatusticUtils getStatisticsUtils();

    Timer getTimer();

    FontUtils getFontUtils();
}
