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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListVH> {

    private Context context;
    private List<Pomodoro> pomodoros;

    public TaskListAdapter(Context context, List<Pomodoro> pomodoros) {
        this.context = context;
        this.pomodoros = pomodoros;
    }

    @NonNull
    @Override
    public TaskListVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, null, false);

        return new TaskListVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListVH taskListVH, int i) {

    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Pomodoro pomodoro : pomodoros) {
            count += pomodoro.getTasks().size();
        }
        return count;
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
