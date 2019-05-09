package com.producticity;

import android.app.Application;

import com.producticity.BuildConfig;
import com.producticity.di.AppComponent;
import com.producticity.di.DaggerAppComponent;
import com.producticity.di.modules.AppModule;
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

        if (BuildConfig.DEBUG) {
            Timber.plant(new TimberDebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        Realm.init(getApplicationContext());

        RealmConfiguration mRealmConfiguration = new RealmConfiguration.Builder()
                .name("producticity.realm")
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
