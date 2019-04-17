package com.citywork.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.citywork.R;
import com.citywork.ui.listeners.OnBreakValueSelected;

public class BreakDataSetAdapter extends BaseAdapter {

    private int[] dataSet;
    private Context context;
    private OnBreakValueSelected onBreakValueSelected;
    private int selected;

    public BreakDataSetAdapter(int[] dataSet, Context context, OnBreakValueSelected onBreakValueSelected, int selected) {
        this.dataSet = dataSet;
        this.selected = selected;
        this.context = context;
        this.onBreakValueSelected = onBreakValueSelected;
    }

    @Override
    public int getCount() {
        return dataSet.length;
    }

    @Override
    public Object getItem(int position) {
        return dataSet[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.break_list_item, parent, false);

        TextView textView = view.findViewById(R.id.break_choose_dialog_text);

        textView.setText(dataSet[position] + " " + context.getResources().getString(R.string.minute));

        if (selected == dataSet[position])
            textView.setTextColor(context.getResources().getColor(R.color.blue));

        view.setOnClickListener(v -> onBreakValueSelected.onBreakValueSelected(dataSet[position]));

        return view;
    }
}
