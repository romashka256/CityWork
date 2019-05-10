package com.producticity.ui;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.producticity.App;
import com.producticity.R;
import com.producticity.model.db.models.Pomodoro;
import com.producticity.ui.listeners.OnTaskClickListener;
import com.producticity.utils.commonutils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListVH> {

    private Context context;
    private OnTaskClickListener onTaskClickListener;
    @Getter
    private List<Pomodoro> pomodoros;
    private UIUtils UIUtils;

    int screenHeight;

    public TaskListAdapter(Context context, List<Pomodoro> pomodoros, OnTaskClickListener onTaskClickListener) {
        this.context = context;
        UIUtils = App.getsAppComponent().getFontUtils();
        this.pomodoros = pomodoros;
        this.onTaskClickListener = onTaskClickListener;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
    }

    @NonNull
    @Override
    public TaskListVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.pomo_task_item, null, false);


        return new TaskListVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListVH taskListVH, int i) {
        Pomodoro pomodoro = pomodoros.get(i);

        taskListVH.taskTextV.setText("#" + (i + 1) + " POMO");
        taskListVH.taskTextV.setTypeface(UIUtils.getMedium());

        if (pomodoro.getTasks() != null && pomodoro.getTasks().isEmpty()) {
            taskListVH.listView.setVisibility(View.GONE);
            taskListVH.taskTextV.setVisibility(View.GONE);
        }else{
            taskListVH.listView.setVisibility(View.VISIBLE);
            taskListVH.taskTextV.setVisibility(View.VISIBLE);
        }

        if (i == pomodoros.size() - 1) {
            taskListVH.taskTextV.setVisibility(View.GONE);
        }else{
            taskListVH.listView.setAlpha(0.6f);
        }

        taskListVH.listView.setAdapter(new PomoTaskListAdapter(context, pomodoro.getTasks(), onTaskClickListener));
        setListViewHeightBasedOnChildren(taskListVH.listView);


    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public long getItemId(int position) {
        return pomodoros.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return pomodoros.size();
    }

    public class TaskListVH extends RecyclerView.ViewHolder {
        @BindView(R.id.task_dialog_pomo_item_pomotext)
        TextView taskTextV;
        @BindView(R.id.task_dialog_pomo_item_list)
        ListView listView;

        public TaskListVH(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
