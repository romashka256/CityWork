package com.producticity.ui.timerfragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.producticity.App;
import com.producticity.Constants;
import com.producticity.R;
import com.producticity.ui.MainActivity;
import com.producticity.ui.customviews.BuldingProgressView;
import com.producticity.ui.customviews.CircleTimer;
import com.producticity.ui.interfaces.ITimerFragment;
import com.producticity.ui.timerfragment.dialogs.StopDialog;
import com.producticity.ui.timerfragment.dialogs.SuccessDialogFragment;
import com.producticity.ui.timerfragment.dialogs.TasksDialog;
import com.producticity.utils.Calculator;
import com.producticity.utils.commonutils.UIUtils;
import com.producticity.utils.commonutils.VectorUtils;
import com.producticity.viewmodels.SharedViewModel;
import com.producticity.viewmodels.interfaces.ITimerFragmentViewModel;
import com.producticity.viewmodels.timerfragment.TimerFragmentViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import timber.log.Timber;

import static com.producticity.utils.timer.TimerState.CANCELED;
import static com.producticity.utils.timer.TimerState.COMPLETED;
import static com.producticity.utils.timer.TimerState.NOT_ONGOING;
import static com.producticity.utils.timer.TimerState.ONGOING;
import static com.producticity.utils.timer.TimerState.REST;
import static com.producticity.utils.timer.TimerState.REST_CANCELED;
import static com.producticity.utils.timer.TimerState.REST_ONGOING;
import static com.producticity.utils.timer.TimerState.WORK_COMPLETED;

public class TimerFragment extends Fragment implements ITimerFragment {

    @BindView(R.id.timer_fragment_start_button)
    Button startButton;
    @BindView(R.id.timer_fragment_stop_button)
    Button stopButton;
    @BindView(R.id.timer_fragment_5min_rest)
    Button m5minRest;
    @BindView(R.id.timer_fragment_10min_rest)
    Button m10minRest;
    @BindView(R.id.circle_timer)
    CircleTimer circleTimer;
    @BindView(R.id.toolbar_settings_iv)
    ImageView mSettingsBtn;
    @BindView(R.id.timer_fragment_building)
    BuldingProgressView mBuidlingView;
    @BindView(R.id.timer_fragment_resttv)
    TextView mRestTV;
    @BindView(R.id.toolbar_share_iv)
    ImageView mShareIV;
    @BindView(R.id.toolbar_city_people_count)
    TextView mCityPeopleCountTV;
    @BindView(R.id.toolbar_city_people_count_text)
    TextView mCityPeopleCountTextTV;
    @BindView(R.id.timer_fragment_contraint)
    ConstraintLayout constraintLayout;
    @BindView(R.id.timer_fragment_resttopblock)
    LinearLayout restTimerBlock;

    @Getter
    public final static int fragmnetIndex = 0;

    ITimerFragmentViewModel iTimerFragmentViewModel;
    private MainActivity mainActivity;
    private SuccessDialogFragment successDialogFragment;
    private UIUtils UIUtils;

    private StopDialog stopDialog;


    @Override
    public void onAttach(Context context) {
        this.mainActivity = (MainActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");

        UIUtils = App.getsAppComponent().getFontUtils();

        iTimerFragmentViewModel = ViewModelProviders.of(this).get(TimerFragmentViewModel.class);
        iTimerFragmentViewModel.getChangeTimeEventInPercent().observe(this, percent -> mBuidlingView.setProgress(percent));
        iTimerFragmentViewModel.getChangeTimeEvent().observe(this, time -> circleTimer.setProgress(time));
        iTimerFragmentViewModel.getTimerCompleteEvent().observe(this, building -> {
            Timber.i("TimerCompleted Event Received");
            circleTimer.disable();
        });

        iTimerFragmentViewModel.getPeopleCountChangedEvent().observe(this, peopleCount -> {
            mBuidlingView.setPeopleCount(peopleCount);
        });

        iTimerFragmentViewModel.getProgressPeopleCountChangedEvent().observe(this, pair -> {
            mBuidlingView.setPeopleProgress(pair.second, pair.first);
        });

        iTimerFragmentViewModel.getCityPeopleCountChangeEvent().observe(this, count -> {
            mCityPeopleCountTV.setText(count + " человек");
        });

        iTimerFragmentViewModel.getTimerStateChanged().observe(this, timerState -> {
            switch (timerState) {
                case ONGOING:
                    showTimerongoingView();
                    break;
                case WORK_COMPLETED:
                    Timber.i("WORK_COMPLETED POSTED");
                    showSuccessDialog();
                    showRestView();
                    break;
                case REST:
                    Timber.i("REST POSTED");
                    showRestView();
                    mRestTV.setText("Выберите время отдыха");
                    break;
                case NOT_ONGOING:
                    showNotOngoingView();
                    break;
                case REST_ONGOING:
                    showTimerongoingView();
                    restTimerBlock.setVisibility(View.VISIBLE);
                    mBuidlingView.setVisibility(View.INVISIBLE);
                    mRestTV.setText("Отдыхайте...");
                    break;
                case CANCELED:
                    showNotOngoingView();
                    break;
                case REST_CANCELED:
                    showNotOngoingView();
                    break;
                case COMPLETED:
                    showNotOngoingView();
                    circleTimer.disable();
                    break;
            }
        });

        iTimerFragmentViewModel.getBuildingChanged().observe(this, iconName -> {
            mBuidlingView.setImage(VectorUtils.getBitmapFromVectorDrawable(App.getsAppComponent().getApplicationContext(), getResources().getIdentifier(iconName, "drawable", mainActivity.getPackageName())));
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_fragment, null, false);

        ButterKnife.bind(this, view);


        int width = UIUtils.getScreenSize().x;

        circleTimer.setCircleRadius((int) (width * 0.3f));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.i("onViewCreated");

        setFonts();

        startButton.setOnClickListener(v -> iTimerFragmentViewModel.onStartClicked());

        stopDialog = new StopDialog(mainActivity, v1 -> {
            circleTimer.disable();
            iTimerFragmentViewModel.onStopClicked();
        });

        stopButton.setOnClickListener(v -> {
            stopDialog.show();
        });

        mBuidlingView.setOnClickListener(v -> {
            //    iTimerFragmentViewModel.onDebugBtnClicked();
        });

        circleTimer.setCircleTimeListener(new CircleTimer.CircleTimerListener() {
            @Override
            public void onTimerTimingValueChanged(long time) {
                iTimerFragmentViewModel.onTimerValueChanged(time);
            }


            @Override
            public void onTimerSetValueChanged(int time) {

            }

            @Override
            public void onTimerSetValueChange(int time) {

            }
        });


        m5minRest.setText(iTimerFragmentViewModel.getShortBreakValue() + " " + getResources().getString(R.string.minute));
        m10minRest.setText(iTimerFragmentViewModel.getLongBreakValue() + " " + getResources().getString(R.string.minute));

        m5minRest.setOnClickListener(v -> {
            iTimerFragmentViewModel.on5MinRestClicked();
        });

        m10minRest.setOnClickListener(v -> {
            iTimerFragmentViewModel.on10MinRestClicked();
        });

        mSettingsBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_timerFragment_to_settingsFragment);
        });

        mShareIV.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Producticity");
            String shareBody = "https://play.google.com/apps/testing/com.producticity";
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });

        mBuidlingView.setPeopleCount(Calculator.calculatePeopleCount(Constants.DEFAULT_MIN_TIMER_VALUE));

        mCityPeopleCountTV.setPadding(0, mBuidlingView.getMeasuredHeight() / 2, 0, mBuidlingView.getMeasuredHeight() / 2);
    }

    @Override
    public void showSuccessDialog() {
        Timber.i("Show Success Diagog");
        successDialogFragment = SuccessDialogFragment.newInstance(iTimerFragmentViewModel.getBuilding());

        FragmentManager fm = getFragmentManager();

        if (successDialogFragment.getDialog() == null) {
            successDialogFragment.show(fm, Constants.DIALOG_SUCCESS_TAG);

            fm.executePendingTransactions();

            iTimerFragmentViewModel.onSuccessDialogShowed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("onPause");

        iTimerFragmentViewModel.onPause();

        if (successDialogFragment != null && successDialogFragment.getDialog() != null && successDialogFragment.getDialog().isShowing()) {
            successDialogFragment.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("onResume");

        iTimerFragmentViewModel.onResume();

        ViewModelProviders.of(mainActivity).get(SharedViewModel.class).getBuildingMutableLiveData().observe(mainActivity, building -> {
            iTimerFragmentViewModel.buildingReceived(building);
        });

        ViewModelProviders.of(mainActivity).get(SharedViewModel.class).getCityMutableLiveData().observe(mainActivity, city -> {
            iTimerFragmentViewModel.cityReceived(city);
        });

        ViewModelProviders.of(mainActivity).get(SharedViewModel.class).getWhatToShowLiveData().observe(mainActivity, what -> {
            switch (what) {
                case Constants.TIMER_NOT_INTENT_STOP:
                    stopDialog.show();
                    break;
                case Constants.TIMER_NOT_INTENT_TASKS:
                    TasksDialog.getInstance().show(mainActivity.getSupportFragmentManager(), TasksDialog.TAG);
                    break;
                case Constants.TIMER_NOT_INTENT_SMALLREST:
                    Timber.i("TIMER_NOT_INTENT_SMALLREST");
                    iTimerFragmentViewModel.on5MinRestClicked();
                    break;
                case Constants.TIMER_NOT_INTENT_BIGREST:
                    Timber.i("TIMER_NOT_INTENT_BIGREST");
                    iTimerFragmentViewModel.on10MinRestClicked();
                    break;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("onStop");

        if (successDialogFragment != null && successDialogFragment.isResumed()) {
            successDialogFragment.dismiss();
        }

        iTimerFragmentViewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy");

    }

    @Override
    public void showNotOngoingView() {
        restTimerBlock.setVisibility(View.GONE);
        m5minRest.setVisibility(View.GONE);
        m10minRest.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        circleTimer.disable();
      //  refreshBuildingView();
        mBuidlingView.setVisibility(View.VISIBLE);
        circleTimer.setTime(iTimerFragmentViewModel.getTimerValue());
    }

    @Override
    public void showRestView() {
        Timber.i("Show Rest View");
        restTimerBlock.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        m5minRest.setVisibility(View.VISIBLE);
        m10minRest.setVisibility(View.VISIBLE);
        mBuidlingView.setVisibility(View.INVISIBLE);
        circleTimer.setTime(0);
        circleTimer.enable();
    }

    private void refreshBuildingView() {
        mBuidlingView.setProgress(0);
        mBuidlingView.setPeopleProgress(0, 0);
    }

    @Override
    public void showTimerongoingView() {
        restTimerBlock.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        m5minRest.setVisibility(View.GONE);
        m10minRest.setVisibility(View.GONE);
        mBuidlingView.setVisibility(View.VISIBLE);
    }

    private void setFonts() {
        startButton.setTypeface(UIUtils.getRegular());
        mRestTV.setTypeface(UIUtils.getLight());
        mCityPeopleCountTV.setTypeface(UIUtils.getBold());
        mCityPeopleCountTextTV.setTypeface(UIUtils.getLight());
        circleTimer.setTimerTypeface(UIUtils.getRegular());
        circleTimer.setSubtitleTypeface(UIUtils.getLight());
        circleTimer.setNumbersTypeface(UIUtils.getRegular());
        mBuidlingView.setTextTypeface(UIUtils.getRegular());
        stopButton.setTypeface(UIUtils.getRegular());
    }
}