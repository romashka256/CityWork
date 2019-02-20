package com.citywork.ui;

import android.arch.lifecycle.ViewModelProviders;
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

import com.citywork.Constants;
import com.citywork.R;
import com.citywork.ui.customviews.BuldingProgressView;
import com.citywork.ui.customviews.CircleTimer;
import com.citywork.viewmodels.SharedViewModel;
import com.citywork.viewmodels.TimerFragmentViewModel;
import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;
import timber.log.Timber;

public class TimerFragment extends Fragment {

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
    @BindView(R.id.timer_fragment_settings)
    ImageView mSettingsBtn;
    @BindView(R.id.timer_fragment_building)
    BuldingProgressView mBuidlingView;
    @BindView(R.id.timer_fragment_todobtn)
    Button mTodoBtn;

    ITimerFragmentViewModel iTimerFragmentViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");
        iTimerFragmentViewModel = ViewModelProviders.of(this).get(TimerFragmentViewModel.class);
        iTimerFragmentViewModel.getChangeTimeEventInPercent().observe(this, percent -> mBuidlingView.setProgress(percent));
        iTimerFragmentViewModel.getChangeTimeEvent().observe(this, time -> circleTimer.setProgress(time));
        iTimerFragmentViewModel.getTimerCompleteEvent().observe(this, building -> {
            Timber.i("TimerCompleted Event Received");
            circleTimer.disable();
            new SuccessDialogFragment().show(getFragmentManager(), Constants.DIALOG_SUCCESS_TAG);
        });
        iTimerFragmentViewModel.getPeopleCountChangedEvent().observe(this, peopleCount -> {

        });
        iTimerFragmentViewModel.getProgressPeopleCountChangedEvent().observe(this, pair -> {
            mBuidlingView.setProgress(pair.second, pair.first);
        });
        iTimerFragmentViewModel.getTimerStateChanged().observe(this, timerState -> {
            switch (timerState) {
                case ONGOING:
                    timerongoingView();
                    break;
                case WORK_COMPLETED:
                    restView();
                    break;
                case NOT_ONGOING:
                    notongoingView();
                    break;
                case REST_ONGOING:
                    timerongoingView();
                    break;
                case CANCELED:
                    notongoingView();
                    break;
                case REST_CANCELED:
                    notongoingView();
                    break;
                case COMPLETED:
                    notongoingView();
                    break;
            }
        });
    }

    private void timerongoingView() {
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        m5minRest.setVisibility(View.GONE);
        m10minRest.setVisibility(View.GONE);
    }

    private void restView() {
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        m5minRest.setVisibility(View.VISIBLE);
        m10minRest.setVisibility(View.VISIBLE);
    }

    private void notongoingView() {
        m5minRest.setVisibility(View.GONE);
        m10minRest.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        circleTimer.setTime(iTimerFragmentViewModel.getTimerValue());
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

        startButton.setOnClickListener(v -> {
            iTimerFragmentViewModel.onStartClicked();
        });

        stopButton.setOnClickListener(v -> {
            circleTimer.disable();
            iTimerFragmentViewModel.onStopClicked();

        });

        mBuidlingView.setOnClickListener(v -> {
            iTimerFragmentViewModel.onDebugBtnClicked();
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

        m5minRest.setOnClickListener(v -> {
            iTimerFragmentViewModel.on5MinRestClicked();
        });

        m10minRest.setOnClickListener(v -> {
            iTimerFragmentViewModel.on10MinRestClicked();
        });

        mSettingsBtn.setOnClickListener(v -> {

        });

        mTodoBtn.setOnClickListener(v -> {

            TasksDialog.getInstance().show(getActivity().getSupportFragmentManager(), TasksDialog.TAG);
            // Navigation.findNavController(v).navigate(R.id.action_timerFragment_to_tasksDialog);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("onPause");

        iTimerFragmentViewModel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("onResume");

        iTimerFragmentViewModel.onResume();

        ViewModelProviders.of(getActivity()).get(SharedViewModel.class).getBuildingMutableLiveData().observe(getActivity(), building -> {
            iTimerFragmentViewModel.buildingReceived(building);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("onStop");

        iTimerFragmentViewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy");

    }
}
