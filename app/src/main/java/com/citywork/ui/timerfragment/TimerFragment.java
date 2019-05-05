package com.citywork.ui.timerfragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.R;
import com.citywork.ui.MainActivity;
import com.citywork.ui.customviews.BuldingProgressView;
import com.citywork.ui.customviews.CircleTimer;
import com.citywork.ui.interfaces.ITimerFragment;
import com.citywork.ui.timerfragment.dialogs.StopDialog;
import com.citywork.ui.timerfragment.dialogs.SuccessDialogFragment;
import com.citywork.utils.Calculator;
import com.citywork.utils.commonutils.FontUtils;
import com.citywork.utils.commonutils.VectorUtils;
import com.citywork.viewmodels.SharedViewModel;
import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;
import com.citywork.viewmodels.timerfragment.TimerFragmentViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import timber.log.Timber;

import static com.citywork.utils.timer.TimerState.CANCELED;
import static com.citywork.utils.timer.TimerState.COMPLETED;
import static com.citywork.utils.timer.TimerState.NOT_ONGOING;
import static com.citywork.utils.timer.TimerState.ONGOING;
import static com.citywork.utils.timer.TimerState.REST;
import static com.citywork.utils.timer.TimerState.REST_CANCELED;
import static com.citywork.utils.timer.TimerState.REST_ONGOING;
import static com.citywork.utils.timer.TimerState.WORK_COMPLETED;

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
    @BindView(R.id.timer_fragment_todobtn)
    LinearLayout mTodoBtn;
    @BindView(R.id.timer_fragment_todotv)
    TextView mTodoTV;
    @BindView(R.id.timer_fragment_resttv)
    TextView mRestTV;
    @BindView(R.id.toolbar_share_iv)
    ImageView mShareIV;
    @BindView(R.id.toolbar_city_people_count)
    TextView mCityPeopleCountTV;
    @BindView(R.id.toolbar_city_people_count_text)
    TextView mCityPeopleCountTextTV;

    @Getter
    public final static int fragmnetIndex = 0;

    ITimerFragmentViewModel iTimerFragmentViewModel;
    private MainActivity mainActivity;
    private SuccessDialogFragment successDialogFragment;
    private FontUtils fontUtils;



    @Override
    public void onAttach(Context context) {
        this.mainActivity = (MainActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");

        fontUtils = App.getsAppComponent().getFontUtils();

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
                    break;
                case NOT_ONGOING:
                    showNotOngoingView();
                    break;
                case REST_ONGOING:
                    showTimerongoingView();
                    mRestTV.setVisibility(View.VISIBLE);
                    mBuidlingView.setVisibility(View.GONE);
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.i("onViewCreated");

        setFonts();

        startButton.setOnClickListener(v -> iTimerFragmentViewModel.onStartClicked());

        stopButton.setOnClickListener(v -> new StopDialog(mainActivity, v1 -> {
            circleTimer.disable();
            iTimerFragmentViewModel.onStopClicked();
        }).show());

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

        mTodoBtn.setOnClickListener(v -> {

            TasksDialog.getInstance().show(mainActivity.getSupportFragmentManager(), TasksDialog.TAG);
            // Navigation.findNavController(v).navigate(R.id.action_timerFragment_to_tasksDialog);
        });

        mShareIV.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Пашел нахуй";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
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
        mRestTV.setVisibility(View.GONE);
        m5minRest.setVisibility(View.GONE);
        m10minRest.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        mTodoTV.setVisibility(View.VISIBLE);
        circleTimer.disable();
        mBuidlingView.setVisibility(View.VISIBLE);
        circleTimer.setTime(iTimerFragmentViewModel.getTimerValue());
    }

    @Override
    public void showRestView() {
        Timber.i("Show Rest View");
        mRestTV.setVisibility(View.GONE);
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        m5minRest.setVisibility(View.VISIBLE);
        m10minRest.setVisibility(View.VISIBLE);
        mTodoTV.setVisibility(View.GONE);
        mBuidlingView.setVisibility(View.VISIBLE);
        circleTimer.setTime(0);
        circleTimer.enable();
    }

    @Override
    public void showTimerongoingView() {
        mRestTV.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        m5minRest.setVisibility(View.GONE);
        mTodoTV.setVisibility(View.VISIBLE);
        m10minRest.setVisibility(View.GONE);
        mBuidlingView.setVisibility(View.VISIBLE);
    }

    private void setFonts() {
        mTodoTV.setTypeface(fontUtils.getLight());
        startButton.setTypeface(fontUtils.getRegular());
        mCityPeopleCountTV.setTypeface(fontUtils.getBold());
        mCityPeopleCountTextTV.setTypeface(fontUtils.getLight());
        circleTimer.setTimerTypeface(fontUtils.getRegular());
        circleTimer.setSubtitleTypeface(fontUtils.getLight());
        circleTimer.setNumbersTypeface(fontUtils.getRegular());
        mBuidlingView.setTextTypeface(fontUtils.getRegular());
        stopButton.setTypeface(fontUtils.getRegular());
    }
}