package com.citywork.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citywork.R;
import com.citywork.model.db.models.Building;
import com.citywork.ui.customviews.CityView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityVH> {

    private List<Pair<Date, List<Building>>> buildings;
    private Context context;
    private List<String> buildingNames;

    public CityAdapter(List<Pair<Date, List<Building>>> buildings, Context context) {
        this.buildings = buildings;
        this.context = context;
        buildingNames = new ArrayList<>();

    }

    @NonNull
    @Override
    public CityVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.city_item, null);
        return new CityVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityVH cityVH, int i) {
        Pair<Date, List<Building>> city = buildings.get(i);

        buildingNames.clear();

        for (Building building : city.second) {
            buildingNames.add(building.getIconName());
        }

        cityVH.cityView.setBuildings(buildingNames);
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public class CityVH extends RecyclerView.ViewHolder {

        @BindView(R.id.city_item_city)
        CityView cityView;

        public CityVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
