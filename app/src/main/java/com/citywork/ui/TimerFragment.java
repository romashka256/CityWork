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

        ViewModelProviders.of(this).get(SharedViewModel.class).getPomodoroMutableLiveData().observe(getActivity(), pomodoro -> {
            Timber.i("new ");
            iTimerFragmentViewModel.pomodoroReceived(pomodoro);
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
