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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.citywork.R;
import com.citywork.service.TimerService;
import com.citywork.ui.customviews.BottomNaigationLayout;
import com.citywork.ui.customviews.BottomNavigationItemView;
import com.citywork.viewmodels.MainActivityViewModel;
import com.citywork.viewmodels.SharedViewModel;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;
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
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        NavController navController = Navigation.findNavController(this, R.id.nav_host);

        intent = new Intent(getApplicationContext(), TimerService.class);

        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        iMainActivityViewModel = mainActivityViewModel;

        //Sending pomodoro object to Fragment
//        mainActivityViewModel.getPomodoroMutableLiveData().observe(this, pomodoro -> {
//            ViewModelProviders.of(this).get(SharedViewModel.class).getPomodoroMutableLiveData().postValue(pomodoro);
//        });

        mainActivityViewModel.getBuildingMutableLiveData().observe(this, building -> {
            ViewModelProviders.of(this).get(SharedViewModel.class).getBuildingMutableLiveData().postValue(building);
        });

        bottomNaigationLayout.addItem(timerTabBtn, () -> navController.navigate(R.id.action_cityFragment_to_timerFragment));
        bottomNaigationLayout.addItem(cityTabBtn, () -> navController.navigate(R.id.action_timerFragment_to_cityFragment));

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

                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
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