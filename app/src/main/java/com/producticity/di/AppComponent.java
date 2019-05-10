package com.producticity.di;

import android.content.Context;

import com.producticity.di.modules.AppModule;
import com.producticity.di.modules.TimerManagerModule;
import com.producticity.model.db.DataBaseHelper;
import com.producticity.utils.CityManager;
import com.producticity.utils.SharedPrefensecUtils;
import com.producticity.utils.chart.StatusticUtils;
import com.producticity.utils.commonutils.UIUtils;
import com.producticity.utils.timer.Timer;
import com.producticity.utils.timer.TimerBase;
import com.producticity.viewmodels.timerfragment.TimerStrategyContext;

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

    UIUtils getFontUtils();

    TimerStrategyContext getTimerStrategyContext();
}
