package com.producticity;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class CrashReportingTree extends Timber.DebugTree {
    @Override
    protected void log(int priority, String tag, @NotNull String message, Throwable t) {
        super.log(priority, tag, message, t);

        if (t != null) {
            Crashlytics.logException(t);
        }
    }
}
