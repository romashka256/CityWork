package com.producticity.ui.tutorial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TutorialAdapter extends FragmentStatePagerAdapter {
    public TutorialAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new FirstTutorialFragment();
            case 1:
                return new SecondTutorialFragment();
            case 2:
                return new ThirdTutorialFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
