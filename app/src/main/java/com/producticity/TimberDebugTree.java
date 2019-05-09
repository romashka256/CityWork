package com.producticity;

import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class TimberDebugTree extends Timber.DebugTree {
    @Override
    protected void log(int priority, String tag, @NotNull String message, Throwable t) {
        super.log(priority, "Timber " + tag, message, t);
    }
}
