package com.citywork.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CustomViewPagerAdapter extends FragmentPagerAdapter {

    public CustomViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0: return new TimerFragment();
            case 1: return new CityFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


}
