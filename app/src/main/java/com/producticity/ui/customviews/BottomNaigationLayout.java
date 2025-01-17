package com.producticity.ui.customviews;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.producticity.ui.listeners.OnTabClickLinetener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BottomNaigationLayout extends ConstraintLayout {

    private List<BottomNavigationItemView> bottomNavigationItemViews;
    private BottomNavigationItemView previousActiveView;

    public BottomNaigationLayout(Context context) {
        super(context);
        init();
    }

    public BottomNaigationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomNaigationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bottomNavigationItemViews = new ArrayList<>();
        Timber.i("getChildCount() : %d", getChildCount());
    }

    public void addItem(final BottomNavigationItemView bottomNavigationItemView, OnTabClickLinetener onTabClickLinetener) {
        bottomNavigationItemViews.add(bottomNavigationItemView);

        bottomNavigationItemView.setOnClickListener(v -> {
            if (activateTab(bottomNavigationItemView)) {
                onTabClickLinetener.onClick(v);
            }
        });
    }

    public boolean activateTab(BottomNavigationItemView bottomNavigationItemView) {
        if (bottomNavigationItemView.getNavItemState() == BottomNavigationItemView.NavItemState.INACTIVE) {
            if (previousActiveView != null)
                previousActiveView.makeDisabled();
            previousActiveView = bottomNavigationItemView;

            bottomNavigationItemView.makeActivated();

            return true;
        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
