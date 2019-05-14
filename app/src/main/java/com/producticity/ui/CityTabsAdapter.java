package com.producticity.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.producticity.R;
import com.producticity.model.db.models.City;
import com.producticity.ui.listeners.OnCityTabClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import timber.log.Timber;

public class CityTabsAdapter extends RecyclerView.Adapter<CityTabsAdapter.CityTabsAdapterVH> {

    public List<City> cities;
    public List<String> dates;
    private Context context;
    @Getter
    private int itemWidth;

    private int selected;
    private OnCityTabClickListener onClickListener;

    private TextView previousSelected;

    public CityTabsAdapter(List<City> cities, Context context, OnCityTabClickListener onClickListener) {
        this.cities = cities;
        this.context = context;
        dates = new ArrayList<>();
        selected = 0;
        this.onClickListener = onClickListener;

        setHasStableIds(true);

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
        Timber.i("set selected : " + selected);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull CityTabsAdapterVH cityTabsAdapterVH, int i) {
        cityTabsAdapterVH.textView.setText(dates.get(i));

        Timber.i("to default adapter");
        cityTabsAdapterVH.textView.setPressed(false);

        Timber.i("pos : " + i + " // selected : " + selected);
//        if (selected == i) {
//            cityTabsAdapterVH.textView.setPressed(true);
//        }

        if (i == 0)
            itemWidth = cityTabsAdapterVH.textView.getWidth();

        cityTabsAdapterVH.itemView.setOnClickListener(v -> onClickListener.onTabClicked(cityTabsAdapterVH.getAdapterPosition()));

        previousSelected = cityTabsAdapterVH.textView;
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
