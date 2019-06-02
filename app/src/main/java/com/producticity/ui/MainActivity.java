package com.producticity.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.producticity.App;
import com.producticity.R;
import com.producticity.service.TimerService;
import com.producticity.ui.tutorial.TutorialActivity;
import com.producticity.utils.SharedPrefensecUtils;
import com.producticity.utils.commonutils.UIUtils;
import com.producticity.viewmodels.MainActivityViewModel;
import com.producticity.viewmodels.SharedViewModel;
import com.producticity.viewmodels.interfaces.IMainActivityViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TimerService timerService;
    private boolean mBound = false;
    private Intent intent;

    @BindView(R.id.bottom_bar_city_btn_block)
    LinearLayout cityBtnBlock;
    @BindView(R.id.bottom_bar_timer_btn_block)
    LinearLayout timerBtnBlock;
    @BindView(R.id.bottom_bar_todo_btn_block)
    LinearLayout todoBtnBlock;
    @BindView(R.id.bottom_bar_city)
    ImageView cityBtn;
    @BindView(R.id.bottom_bar_timer)
    ImageView timerBtn;
    @BindView(R.id.bottom_bar_todo)
    ImageView todoBtn;
    @BindView(R.id.bottom_bar_city_text)
    TextView cityBtnTV;
    @BindView(R.id.bottom_bar_timer_text)
    TextView timerBtnTV;
    @BindView(R.id.bottom_bar_todo_text)
    TextView todoBtnTV;
    @BindView(R.id.bottom_bar)
    ConstraintLayout bottomBar;

    private NavigationHandler navigationHandler;
    private IMainActivityViewModel iMainActivityViewModel;

    private UIUtils UIUtils;

    private SharedPrefensecUtils sharedPrefensecUtils;

    private Unbinder unbinder;

    private int currentItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
        navigationHandler = new NavigationHandler();
        UIUtils = App.getsAppComponent().getFontUtils();
        intent = new Intent(getApplicationContext(), TimerService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        }

        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        iMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        iMainActivityViewModel.processIntent(getIntent());


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        UIUtils.setScreenSize(size);

        iMainActivityViewModel.getBuildingLiveData().observe(this, building -> {
            ViewModelProviders.of(this).get(SharedViewModel.class).getBuildingMutableLiveData().setValue(building);
            Timber.i("Last building posted");
        });

        iMainActivityViewModel.getCityMutableLiveData().observe(this, city -> {
            ViewModelProviders.of(this).get(SharedViewModel.class).getCityMutableLiveData().setValue(city);
            Timber.i("Last city posted");
        });

        iMainActivityViewModel.getWhatToShowLiveData().observe(this, what -> {
            ViewModelProviders.of(this).get(SharedViewModel.class).getWhatToShowLiveData().setValue(what);
            Timber.i("What to show posted");
        });

        initBottomNavigation();

        iMainActivityViewModel.onCreate();



        if (sharedPrefensecUtils.isFirstRun()) {
            Intent intent = new Intent(this, TutorialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        Timber.i("onClick");
        switch (v.getId()) {
            case R.id.bottom_bar_timer_btn_block:
                navigationHandler.clicked(NavigationHandler.TIMER_ITEM, v);
                break;
            case R.id.bottom_bar_city_btn_block:
                navigationHandler.clicked(NavigationHandler.CITY_ITEM, v);
                break;
            case R.id.bottom_bar_todo_btn_block:
                navigationHandler.clicked(NavigationHandler.TODO_ITEM, v);
                break;
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
        unbinder.unbind();
        Timber.i("onDestroy");
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Timber.i("onServiceConnected");

            mBound = true;
            TimerService.TimerServiceBinder timerServiceBinder = (TimerService.TimerServiceBinder) service;
            timerService = timerServiceBinder.getService();

            Timber.i("Stop Service and Timer");

            timerService.stopForeground(true);
            timerService.stopSelf();
            timerService.cancelTimer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Timber.i("onServiceDisconnected");
        }
    };

    private void initBottomNavigation(){
        todoBtnBlock.setOnClickListener(this);
        timerBtnBlock.setOnClickListener(this);
        cityBtnBlock.setOnClickListener(this);

        timerBtnBlock.setSelected(true);
        navigationHandler.init(1, timerBtnBlock);

        navigationHandler.setOnButtomNavItemChangedListener(((view, item) -> {
            switch (item) {
                case NavigationHandler.CITY_ITEM:
                    Navigation.findNavController(this, R.id.nav_host).navigate(R.id.action_timerFragment_to_cityFragment);

                    break;
                case NavigationHandler.TIMER_ITEM:
                    Navigation.findNavController(this, R.id.nav_host).navigate(R.id.action_cityFragment_to_timerFragment);

                    break;
                case NavigationHandler.TODO_ITEM:

                    break;
            }
            view.setSelected(true);
            if (navigationHandler.getPreviousView() != null)
                navigationHandler.getPreviousView().setSelected(false);
        }));
    }

    public void showBottomNav() {
        bottomBar.setVisibility(View.VISIBLE);
    }

    public void hideBottomNav() {
        bottomBar.setVisibility(View.GONE);
    }
}