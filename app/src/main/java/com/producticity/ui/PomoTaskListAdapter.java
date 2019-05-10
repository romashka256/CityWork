package com.producticity.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.producticity.App;
import com.producticity.R;
import com.producticity.model.db.models.Task;
import com.producticity.ui.listeners.OnTaskClickListener;
import com.producticity.utils.commonutils.UIUtils;

import java.util.List;

public class PomoTaskListAdapter extends BaseAdapter {
    private List<Task> tasks;
    private LayoutInflater layoutInflater;
    private OnTaskClickListener onTaskClickListener;
    private UIUtils UIUtils;

    public PomoTaskListAdapter(Context context, List<Task> tasks, OnTaskClickListener onTaskClickListener) {
        this.tasks = tasks;
        this.onTaskClickListener = onTaskClickListener;
        UIUtils = App.getsAppComponent().getFontUtils();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.task_item, parent, false);
        }

        Task task = tasks.get(position);

        TextView taskText = view.findViewById(R.id.task_item_tasktext);
        taskText.setText(task.getText());
        taskText.setTypeface(UIUtils.getRegular());
        CheckBox checkBox = view.findViewById(R.id.task_item_checkbox);
        checkBox.setChecked(task.isDone());

        view.setOnClickListener(v -> {
            onclickAction(tasks.get(position));
            checkBox.setChecked(!checkBox.isChecked());
        });

        checkBox.setOnClickListener(v -> {
            onclickAction(tasks.get(position));
        });

        return view;
    }

    Task getTask(int position) {
        return ((Task) getItem(position));
    }

    private void onclickAction(Task task) {
        task.setDone(!task.isDone());
        onTaskClickListener.onClick(task);
    }
}
