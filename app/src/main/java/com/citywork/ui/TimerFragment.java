package com.citywork.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.citywork.Constants;
import com.citywork.R;
import com.citywork.viewmodels.SharedViewModel;
import com.citywork.viewmodels.TimerFragmentViewModel;
import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;
import timber.log.Timber;

public class TimerFragment extends Fragment {

    @BindView(R.id.timer_fragment_start_button)
    Button startButton;
    @BindView(R.id.timer_fragment_stop_button)
    Button stopButton;
    @BindView(R.id.timer_fragment_time)
    TextView mTime;
    @BindView(R.id.timer_fragment_5min_rest)
    Button m5minRest;
    @BindView(R.id.timer_fragment_10min_rest)
    Button m10minRest;
    @BindView(R.id.circle_timer)
    CircleTimer circleTimer;


    ITimerFragmentViewModel iTimerFragmentViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");
        iTimerFragmentViewModel = ViewModelProviders.of(this).get(TimerFragmentViewModel.class);
        iTimerFragmentViewModel.getChangeTimeEvent().observe(this, time -> circleTimer.setProgress(time));
        iTimerFragmentViewModel.getTimerCompleteEvent().observe(this, building -> {
            Timber.i("TimerCompleted Event Received");
            circleTimer.disable();
            new SuccessDialogFragment().show(getFragmentManager(), Constants.DIALOG_SUCCESS_TAG);
        });
        iTimerFragmentViewModel.getPeopleCountChangedEvent().observe(this, peopleCount -> {
            mTime.setText(peopleCount + "");
        });
        iTimerFragmentViewModel.getTimerStateChanged().observe(this, timerState -> {
            switch (timerState) {
                case ONGOING:
                    stopButton.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.GONE);
                    m5minRest.setVisibility(View.GONE);
                    m10minRest.setVisibility(View.GONE);
                    break;
                case COMPLETED:
                    stopButton.setVisibility(View.GONE);
                    m5minRest.setVisibility(View.VISIBLE);
                    m10minRest.setVisibility(View.VISIBLE);
                    break;
                case NOT_ONGOING:
                    m5minRest.setVisibility(View.GONE);
                    m10minRest.setVisibility(View.GONE);
                    stopButton.setVisibility(View.GONE);
                    startButton.setVisibility(View.VISIBLE);
                    circleTimer.setTime(iTimerFragmentViewModel.getTimerValue());
                    break;
            }
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
        Timber.i("onActivityCreated");

        startButton.setOnClickListener(v -> {
            iTimerFragmentViewModel.onStartClicked();
        });

        stopButton.setOnClickListener(v -> {
            circleTimer.disable();
            iTimerFragmentViewModel.onStopClicked();
            mTime.setText("STOPPED");
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

        ViewModelProviders.of(this).get(SharedViewModel.class).getBuildingMutableLiveData().observe(getActivity(), building -> {
            Timber.i("new ");
            iTimerFragmentViewModel.buildingReceived(building);
        });

        m5minRest.setOnClickListener(v -> {
            iTimerFragmentViewModel.on5MinRestClicked();
        });

        m10minRest.setOnClickListener(v -> {
            iTimerFragmentViewModel.on10MinRestClicked();
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
    }

    @Override
    public void onStop() {
        super.onStop();

        Timber.i("onStop");
    }
}
