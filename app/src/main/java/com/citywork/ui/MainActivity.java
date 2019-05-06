package com.citywork.ui;

import android.app.FragmentTransaction;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.citywork.App;
import com.citywork.R;
import com.citywork.service.TimerService;
import com.citywork.ui.customviews.bottomnav.BubbleNavigationLinearView;
import com.citywork.ui.customviews.bottomnav.BubbleToggleView;
import com.citywork.ui.timerfragment.TimerFragment;
import com.citywork.ui.tutorial.TutorialActivity;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.commonutils.FontUtils;
import com.citywork.viewmodels.MainActivityViewModel;
import com.citywork.viewmodels.SharedViewModel;
import com.citywork.viewmodels.interfaces.IMainActivityViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    TimerService timerService;
    private boolean mBound = false;
    private Intent intent;

//    @BindView(R.id.nav_item)
//    BottomNaigationLayout bottomNaigationLayout;
//    @BindView(R.id.bottom_navigation_timer_btn)
//    BottomNavigationItemView timerTabBtn;
//    @BindView(R.id.bottom_navigation_city_btn)
//    BottomNavigationItemView cityTabBtn;

    @BindView(R.id.bottom_bar_city)
    BubbleToggleView cityBtn;
    @BindView(R.id.bottom_bar_timer)
    BubbleToggleView timerBtn;
    @BindView(R.id.bottom_bar)
    BubbleNavigationLinearView bottomBar;

//    @BindView(R.id.likes_icon_text)
//    TextView likesIconText;
//    @BindView(R.id.home_icon_text)
//    TextView homeIconText;
//    @BindView(R.id.home_icon)
//    ImageView homeIcon;
//    @BindView(R.id.likes_icon)
//    ImageView likesIcon;

    private int currentFragment;

    private IMainActivityViewModel iMainActivityViewModel;

    private FontUtils fontUtils;

    private SharedPrefensecUtils sharedPrefensecUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        }

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        intent = new Intent(getApplicationContext(), TimerService.class);

        iMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        fontUtils = App.getsAppComponent().getFontUtils();

        iMainActivityViewModel.getBuildingLiveData().observe(this, building -> {
            ViewModelProviders.of(this).get(SharedViewModel.class).getBuildingMutableLiveData().setValue(building);
            Timber.i("Last building posted");
        });

        iMainActivityViewModel.getCityMutableLiveData().observe(this, city -> {
            ViewModelProviders.of(this).get(SharedViewModel.class).getCityMutableLiveData().setValue(city);
            Timber.i("Last city posted");
        });
        MainActivity mainActivity = this;

        bottomBar.setNavigationChangeListener((view, position) -> {
            switch (position) {
                case 1:
                    Navigation.findNavController(mainActivity, R.id.nav_host).navigate(R.id.action_timerFragment_to_cityFragment);
                    break;
                case 0:
                    Navigation.findNavController(mainActivity, R.id.nav_host).navigate(R.id.action_cityFragment_to_timerFragment);
                    break;
            }
        });

        bottomBar.setTypeface(fontUtils.getRegular());

//        cityBtn.setOnClickListener(v -> {
//                Navigation.findNavController(this, R.id.nav_host).navigate(R.id.action_cityFragment_to_timerFragment);
//        });
//
//        timerBtn.setOnClickListener(v -> {
//                Navigation.findNavController(this, R.id.nav_host).navigate(R.id.action_timerFragment_to_cityFragment);
//        });
//
//        likesIconText.setTypeface(fontUtils.getRegular());
//        homeIconText.setTypeface(fontUtils.getRegular());

        iMainActivityViewModel.onCreate();

        if (sharedPrefensecUtils.isFirstRun()) {
            Intent intent = new Intent(this, TutorialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

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
        unbindService(serviceConnection);
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
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Timber.i("onServiceDisconnected");
        }
    };

    public void showBottomNav() {
        bottomBar.setVisibility(View.VISIBLE);
    }

    public void hideBottomNav() {
        bottomBar.setVisibility(View.GONE);
    }
}