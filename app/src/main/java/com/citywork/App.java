package com.citywork;

import android.app.Application;
import com.citywork.di.AppComponent;
import com.citywork.di.DaggerAppComponent;
import com.citywork.di.modules.AppModule;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

public class App extends Application {

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Timber.plant(new TimberDebugTree());

        Realm.init(getApplicationContext());

        RealmConfiguration mRealmConfiguration = new RealmConfiguration.Builder()
                .name("citywork.realm")
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(mRealmConfiguration);

        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();

    }

    public static AppComponent getsAppComponent() {
        return sAppComponent;
    }
}
