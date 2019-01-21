package com.citywork.di;

import android.content.Context;
import com.citywork.di.modules.AppModule;
import com.citywork.di.modules.TimerManagerModule;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.TimerManager;
import dagger.Component;
import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Singleton;

@Component(modules = {AppModule.class, TimerManagerModule.class})
@Singleton
public interface AppComponent {
    CompositeDisposable getCompositeDisposable();
    TimerManager getTimerManager();
    Context getApplicationContext();
    DataBaseHelper getDataBaseHelper();
    SharedPrefensecUtils getSharedPrefs();
}
