package com.citywork.ui;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.R;
import com.citywork.ui.customviews.BuldingProgressView;
import com.citywork.ui.customviews.CircleTimer;
import com.citywork.utils.VectorUtils;
import com.citywork.viewmodels.SharedViewModel;
import com.citywork.viewmodels.TimerFragmentViewModel;
import com.citywork.viewmodels.interfaces.ITimerFragmentViewModel;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
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
    LinearLayout mTodoBtn;
    @BindView(R.id.timer_fragment_todotv)
    TextView mTodoTV;

    ITimerFragmentViewModel iTimerFragmentViewModel;
    private MainActivity mainActivity;
    private SuccessDialogFragment successDialogFragment;

    @Override
    public void onAttach(Context context) {
        this.mainActivity = (MainActivity) context;
        super.onAttach(context);
    }

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
        });
        iTimerFragmentViewModel.getPeopleCountChangedEvent().observe(this, peopleCount -> {
            mBuidlingView.setPeopleCount(peopleCount);
        });
        iTimerFragmentViewModel.getProgressPeopleCountChangedEvent().observe(this, pair -> {
            mBuidlingView.setPeopleProgress(pair.second, pair.first);
        });
        iTimerFragmentViewModel.getTimerStateChanged().observe(this, timerState -> {
            switch (timerState) {
                case ONGOING:
                    timerongoingView();
                    break;
                case WORK_COMPLETED:
                    Timber.i("WORK_COMPLETED POSTED");
                    showSuccessDialog();
                    restView();
                    break;
                case REST:
                    Timber.i("REST POSTED");
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
                    circleTimer.disable();
                    break;
            }
        });

        iTimerFragmentViewModel.getBuildingChanged().observe(this, iconName -> {
            mBuidlingView.setImage(VectorUtils.getBitmapFromVectorDrawable(App.getsAppComponent().getApplicationContext(), getResources().getIdentifier(iconName, "drawable", mainActivity.getPackageName())));
        });
    }

    private void showSuccessDialog() {
        Timber.i("Show Success Diagog");
        successDialogFragment = SuccessDialogFragment.newInstance(iTimerFragmentViewModel.getBuilding());

        FragmentManager fm = getFragmentManager();

        if (successDialogFragment.getDialog() == null) {
            successDialogFragment.show(fm, Constants.DIALOG_SUCCESS_TAG);

            fm.executePendingTransactions();

            iTimerFragmentViewModel.onSuccessDialogShowed();

//            successDialogFragment.getDialog().setOnDismissListener(dialog -> {
//                iTimerFragmentViewModel.onSuccessDialogDismiss();
//            });
//
//            successDialogFragment.getDialog().setOnCancelListener(dialog -> {
//                iTimerFragmentViewModel.onSuccessDialogDismiss();
//            });
        }
    }

    private void timerongoingView() {
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        m5minRest.setVisibility(View.GONE);
        mTodoTV.setVisibility(View.VISIBLE);
        m10minRest.setVisibility(View.GONE);
    }

    private void restView() {
        Timber.i("Show Rest View");
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        m5minRest.setVisibility(View.VISIBLE);
        m10minRest.setVisibility(View.VISIBLE);
        mTodoTV.setVisibility(View.GONE);
        circleTimer.enable();
    }

    private void notongoingView() {
        m5minRest.setVisibility(View.GONE);
        m10minRest.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        mTodoTV.setVisibility(View.VISIBLE);
        circleTimer.disable();
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
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Вы уверены, что хотите остановить ?")
                    .setPositiveButton("Остановить", (dialog, which) -> {
                        circleTimer.disable();
                        iTimerFragmentViewModel.onStopClicked();
                        dialog.cancel();
                    })
                    .setNegativeButton("Отмена", (dialog, which) -> dialog.cancel())
                    .show();
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
            Navigation.findNavController(v).navigate(R.id.action_timerFragment_to_settingsFragment);
        });

        mTodoBtn.setOnClickListener(v -> {

            TasksDialog.getInstance().show(mainActivity.getSupportFragmentManager(), TasksDialog.TAG);
            // Navigation.findNavController(v).navigate(R.id.action_timerFragment_to_tasksDialog);
        });
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
}