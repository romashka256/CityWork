package com.producticity.ui;

import android.view.View;

import com.producticity.ui.listeners.OnButtomNavItemChangedListener;

import lombok.Getter;
import timber.log.Timber;

public class NavigationHandler {
    public static final int TODO_ITEM = 0;
    public static final int TIMER_ITEM = 1;
    public static final int CITY_ITEM = 2;

    private OnButtomNavItemChangedListener onButtomNavItemChangedListener;

    @Getter
    private int currentItem = 1;
    @Getter
    private int previousItem = 1;
    @Getter
    private View previousView;

    public void init(int item, View view) {
        currentItem = item;
        previousItem = item;
        previousView = view;
    }

    public void clicked(int item, View view) {
        Timber.i("clicked : " + item);
        if (currentItem != item) {
            Timber.i("clicked new current : " + item);
            currentItem = item;
            onButtomNavItemChangedListener.onNavigationChanged(view, item);
            previousItem = item;
            previousView = view;
        }
    }

    public void setOnButtomNavItemChangedListener(OnButtomNavItemChangedListener listener) {
        this.onButtomNavItemChangedListener = listener;
    }
}
