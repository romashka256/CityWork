package com.citywork.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.citywork.R;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListVH> {

    private Context context;
    private List<Task> tasks;
    private OnTaskClickListener onTaskClickListener;

    public TaskListAdapter(Context context, List<Pomodoro> pomodoros, OnTaskClickListener onTaskClickListener) {
        this.context = context;
        this.onTaskClickListener = onTaskClickListener;
        tasks = new ArrayList<>();
        for (Pomodoro pom : pomodoros) {
            tasks.addAll(pom.getTasks());
        }
    }

    @NonNull
    @Override
    public TaskListVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, null, false);

        return new TaskListVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListVH taskListVH, int i) {
        Task task = tasks.get(i);

        taskListVH.taskTextV.setText(task.getText());
        taskListVH.checkBox.setChecked(task.isDone());

        taskListVH.itemView.setOnClickListener(v -> {
            taskListVH.checkBox.setChecked(!task.isDone());
            onTaskClickListener.onClick(tasks.get(i));
        });

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskListVH extends RecyclerView.ViewHolder {
        @BindView(R.id.task_item_checkbox)
        CheckBox checkBox;
        @BindView(R.id.task_item_dragimageview)
        ImageView dragImageView;
        @BindView(R.id.task_item_tasktext)
        TextView taskTextV;

        public TaskListVH(@NonNull View itemView) {
            super(itemView);



            ButterKnife.bind(this, itemView);
        }
    }
}
