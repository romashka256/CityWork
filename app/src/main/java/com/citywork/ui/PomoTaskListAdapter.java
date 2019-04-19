package com.citywork.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.citywork.R;
import com.citywork.model.db.models.Task;
import com.citywork.ui.listeners.OnTaskClickListener;

import java.util.List;

public class PomoTaskListAdapter extends BaseAdapter {
    private List<Task> tasks;
    private LayoutInflater layoutInflater;
    private OnTaskClickListener onTaskClickListener;

    public PomoTaskListAdapter(Context context, List<Task> tasks, OnTaskClickListener onTaskClickListener) {
        this.tasks = tasks;
        this.onTaskClickListener = onTaskClickListener;
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
        CheckBox checkBox = view.findViewById(R.id.task_item_checkbox);
        checkBox.setChecked(task.isDone());

        view.setOnClickListener(v -> {
            onclickAction(tasks.get(position));
            checkBox.setChecked(!checkBox.isChecked());
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
