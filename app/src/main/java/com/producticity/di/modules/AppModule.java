package com.producticity.di.modules;

import android.content.Context;

import com.producticity.model.db.DataBaseHelper;
import com.producticity.utils.CityManager;
import com.producticity.utils.SharedPrefensecUtils;
import com.producticity.utils.chart.StatusticUtils;
import com.producticity.utils.commonutils.UIUtils;
import com.producticity.viewmodels.timerfragment.TimerStrategyContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class AppModule {

    Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public DataBaseHelper provideDataBaseHelper() {
        return new DataBaseHelper();
    }

    @Provides
    @Singleton
    public SharedPrefensecUtils provideSharedPreferences() {
        return new SharedPrefensecUtils(context);
    }

    @Singleton
    @Provides
    public CityManager provideModoroManager() {
        return new CityManager();
    }

    @Singleton
    @Provides
    public StatusticUtils provideStatisticUtiles() {
        return new StatusticUtils();
    }

    @Singleton
    @Provides
    public UIUtils provideFontUtils() {
        return new UIUtils(context);
    }

    @Singleton
    @Provides
    public TimerStrategyContext provideTimerStrategyContext(){
        return new TimerStrategyContext();
    }
}
