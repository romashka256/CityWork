package com.citywork.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.citywork.R;
import com.citywork.service.TimerService;
import com.citywork.ui.customviews.BottomNaigationLayout;
import com.citywork.ui.customviews.BottomNavigationItemView;
import com.citywork.viewmodels.MainActivityViewModel;
import com.citywork.viewmodels.SharedViewModel;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    TimerService timerService;
    private boolean mBound = false;
    private Intent intent;
    @BindView(R.id.nav_item)
    BottomNaigationLayout bottomNaigationLayout;
    @BindView(R.id.bottom_navigation_timer_btn)
    BottomNavigationItemView timerTabBtn;
    @BindView(R.id.bottom_navigation_city_btn)
    BottomNavigationItemView cityTabBtn;

    private IMainActivityViewModel iMainActivityViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        }

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        intent = new Intent(getApplicationContext(), TimerService.class);

        iMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        iMainActivityViewModel.getBuildingLiveData().observe(this, building -> {
            ViewModelProviders.of(this).get(SharedViewModel.class).getBuildingMutableLiveData().postValue(building);
            Timber.i("Last building posted");
        });

        iMainActivityViewModel.getCityMutableLiveData().observe(this, city ->{
            ViewModelProviders.of(this).get(SharedViewModel.class).getCityMutableLiveData().postValue(city);
            Timber.i("Last city posted");
        });

        timerTabBtn.setDataToImet("Таймер", R.drawable.ic_timer_icon_focused);
        cityTabBtn.setDataToImet("Город", R.drawable.ic_city_icon_focused);

        bottomNaigationLayout.addItem(timerTabBtn, v -> {
            Navigation.findNavController(this, R.id.nav_host).navigate(R.id.action_cityFragment_to_timerFragment);
        });
        bottomNaigationLayout.addItem(cityTabBtn, (v) -> {
            Navigation.findNavController(this, R.id.nav_host).navigate(R.id.action_timerFragment_to_cityFragment);
        });

        bottomNaigationLayout.activateTab(timerTabBtn);

        iMainActivityViewModel.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume");
        bindService(intent, serviceConnection, 0);
        if (mBound) {
            timerService.stopForeground(true);
            timerService.stopSelf();
            timerService.cancelTimer();
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
        iMainActivityViewModel.onDestroy();
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

                timerService.stopForeground(true);
                timerService.stopSelf();
                timerService.cancelTimer();

//                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
//                ((TimerFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).iTimerFragmentViewModel.onServiceConnected(timerService.getPomodoro());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Timber.i("onServiceDisconnected");
        }
    };

    public void showBottomNav() {
        bottomNaigationLayout.setVisibility(View.VISIBLE);
    }

    public void hideBottomNav() {
        bottomNaigationLayout.setVisibility(View.GONE);
    }
}