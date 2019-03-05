package com.citywork.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.citywork.R;
import com.citywork.model.db.models.Building;
import com.citywork.ui.customviews.PeopleCountView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityVH> {

    private List<Building> buildings;
    private Context context;

    public CityAdapter(List<Building> buildings, Context context) {
        this.buildings = buildings;
        this.context = context;
    }

    @NonNull
    @Override
    public CityVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.city_item, null);
        return new CityVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityVH cityVH, int i) {
        
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public class CityVH extends RecyclerView.ViewHolder {

        @BindView(R.id.city_item_city)
        LinearLayout linearLayout;

        public CityVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
