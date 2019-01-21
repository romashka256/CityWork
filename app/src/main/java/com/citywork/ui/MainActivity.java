package com.citywork.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import com.citywork.R;
import com.citywork.service.TimerService;
import com.citywork.viewmodels.MainActivityViewModel;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    TimerService timerService;
    private boolean mBound = false;
    private Intent intent;

//    @BindView(R.id.view_pager)
//    ViewPager mViewPager;
//    @BindView(R.id.tab_layout)
//    TabLayout tabLayout;

    private IMainActivityViewModel iMainActivityViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(getApplicationContext(), TimerService.class);

//        CustomViewPagerAdapter customViewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager());
//        mViewPager.setAdapter(customViewPagerAdapter);
//        tabLayout.setupWithViewPager(mViewPager);
//
//        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab);
//        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab);

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

                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
                ((TimerFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).iTimerFragmentViewModel.onServiceConnected(timerService.getPomodoro());

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
