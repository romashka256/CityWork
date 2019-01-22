package com.citywork.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.citywork.R;

public class CustomTabLayout extends TabLayout {
    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSelectedListener(){
        addOnTabSelectedListener(new OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ImageView imageView = tab.getCustomView().findViewById(R.id.custom_tab_iv);
                switch (tab.getPosition()) {
                    case 0:
                        imageView.setBackgroundResource(R.drawable.ic_timer_icon_focused);
                        break;
                    case 1:
                        imageView.setBackgroundResource(R.drawable.ic_city_icon_focused);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ImageView imageView = tab.getCustomView().findViewById(R.id.custom_tab_iv);
                switch (tab.getPosition()) {
                    case 0:
                        imageView.setBackgroundResource(R.drawable.ic_timer_icon);
                        break;
                    case 1:
                        imageView.setBackgroundResource(R.drawable.ic_city_icon);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void addOnTabSelectedListener(@NonNull BaseOnTabSelectedListener listener) {

        super.addOnTabSelectedListener(listener);
    }

    @Override
    public void addTab(@NonNull Tab tab) {
        super.addTab(tab);
    }
}
