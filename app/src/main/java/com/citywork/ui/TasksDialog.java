package com.citywork.ui;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.citywork.App;
import com.citywork.R;
import com.citywork.viewmodels.TasksDialogViewModel;
import com.citywork.viewmodels.interfaces.ITasksDialogViewModel;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksDialog extends DialogFragment {

    private Context context;
    @BindView(R.id.tasks_dialog_rv)
    RecyclerView recyclerView;
    @BindView(R.id.tasks_dialog_closeiv)
    ImageView closeIV;
    @BindView(R.id.tasks_dialog_edittext)
    EditText editText;
    @BindView(R.id.tasks_dialog_sendiv)
    ImageView sendIV;
    @BindView(R.id.tasks_dialog_settings)
    ImageView settingIV;

    public static final String TAG = "TasksDialog";

    private ITasksDialogViewModel iTasksDialogViewModel;
    private TaskListAdapter taskListAdapter;

    public static TasksDialog getInstance() {
        return new TasksDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = App.getsAppComponent().getApplicationContext();

        iTasksDialogViewModel = ViewModelProviders.of(this).get(TasksDialogViewModel.class);
        iTasksDialogViewModel.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_dialog, null, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        iTasksDialogViewModel.getPomodoroLoadedEvent().observe(this, pomodoros -> {
            taskListAdapter = new TaskListAdapter(context, pomodoros, task -> {
                iTasksDialogViewModel.onTaskClicked(task);
            });
            recyclerView.setAdapter(taskListAdapter);
            taskListAdapter.notifyDataSetChanged();
        });

        closeIV.setOnClickListener(v -> {
            dismiss();
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                iTasksDialogViewModel.onTextChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendIV.setOnClickListener(v -> {
            iTasksDialogViewModel.onAddClicked();
            editText.setText("");
        });

        settingIV.setOnClickListener(v -> {
            dismiss();
            Navigation.findNavController(getActivity(), R.id.toolbar_settings_iv).navigate(R.id.action_timerFragment_to_settingsFragment);
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        iTasksDialogViewModel.onDismiss();
    }
}
