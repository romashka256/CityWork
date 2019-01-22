package com.citywork.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import androidx.navigation.fragment.NavHostFragment;

import com.citywork.R;
import com.citywork.service.TimerService;
import com.citywork.utils.Timer;
import com.citywork.viewmodels.MainActivityViewModel;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    TimerService timerService;
    private boolean mBound = false;
    private Intent intent;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    CustomTabLayout tabLayout;

    private IMainActivityViewModel iMainActivityViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        intent = new Intent(getApplicationContext(), TimerService.class);

        CustomViewPagerAdapter customViewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(customViewPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab).getCustomView().findViewById(R.id.custom_tab_iv).setBackgroundResource(R.drawable.ic_timer_icon_focused);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab).getCustomView().findViewById(R.id.custom_tab_iv).setBackgroundResource(R.drawable.ic_city_icon);

        tabLayout.setTabRippleColor(null);

        tabLayout.setSelectedListener();

        iMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        iMainActivityViewModel.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume");

        bindService(intent, serviceConnection, 0);

        if (mBound) {
            Timber.i("Stop Timer");
            iMainActivityViewModel.onServiceConnected(timerService.getPomodoro());

            timerService.stopSelf();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.i("onStop");

        iMainActivityViewModel.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy");

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Timber.i("onServiceConnected");

            mBound = true;
            TimerService.TimerServiceBinder timerServiceBinder = (TimerService.TimerServiceBinder) service;
            timerService = timerServiceBinder.getService();

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                Timber.i("Stop Timer");

                try {
                    TimerFragment timerFragment = (TimerFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 0);

//                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
//                ((TimerFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).iTimerFragmentViewModel.onServiceConnected(timerService.getPomodoro());

                    timerFragment.iTimerFragmentViewModel.onServiceConnected(timerService.getPomodoro());
                } catch (NullPointerException e) {
                    Timber.e(e);
                }

                timerService.stopSelf();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Timber.i("onServiceDisconnected");
        }
    };
}
