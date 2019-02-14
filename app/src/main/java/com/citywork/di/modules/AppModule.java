package com.citywork.di.modules;

import android.content.Context;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.SharedPrefensecUtils;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Singleton;

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
    public PomodoroManger provideModoroManager(){
        return new PomodoroManger();
    }
}
