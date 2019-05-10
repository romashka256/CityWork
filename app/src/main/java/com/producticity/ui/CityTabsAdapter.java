package com.producticity.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.producticity.R;
import com.producticity.model.db.models.City;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

public class CityTabsAdapter extends RecyclerView.Adapter<CityTabsAdapter.CityTabsAdapterVH> {

    public List<City> cities;
    public List<String> dates;
    private Context context;
    @Getter
    private int itemWidth;

    private int selected;

    public CityTabsAdapter(List<City> cities, Context context) {
        this.cities = cities;
        this.context = context;
        dates = new ArrayList<>();
        selected = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM");

        for (City city : cities) {
            dates.add(simpleDateFormat.format(city.getDate()));
        }
    }

    @NonNull
    @Override
    public CityTabsAdapterVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.city_tab_item, null);

        return new CityTabsAdapterVH(view);
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    public void onBindViewHolder(@NonNull CityTabsAdapterVH cityTabsAdapterVH, int i) {
        cityTabsAdapterVH.textView.setText(dates.get(i));
        if (selected == i) {
            cityTabsAdapterVH.textView.setTextColor(Color.BLACK);
            cityTabsAdapterVH.textView.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.city_tab_bg_selected, null));
        } else {
            cityTabsAdapterVH.textView.setTextColor(Color.WHITE);
            cityTabsAdapterVH.textView.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.city_tab_bg, null));
        }
        if (i == 0)
            itemWidth = cityTabsAdapterVH.textView.getWidth();
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public class CityTabsAdapterVH extends RecyclerView.ViewHolder {

        @BindView(R.id.city_tab_item_tv)
        TextView textView;

        public CityTabsAdapterVH(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
