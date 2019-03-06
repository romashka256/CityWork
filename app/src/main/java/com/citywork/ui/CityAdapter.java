package com.citywork.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.citywork.R;
import com.citywork.model.db.models.Building;
import com.citywork.ui.customviews.PeopleCountView;
import com.citywork.utils.timer.TimerState;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityVH> {

    private List<Pair<Date, List<Building>>> buildings;
    private Context context;

    public CityAdapter(List<Pair<Date, List<Building>>> buildings, Context context) {
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
        Pair<Date, List<Building>> city = buildings.get(i);

        for (Building building : city.second) {
            ImageView buildingView = new ImageView(context);
            buildingView.setImageResource(context.getResources().getIdentifier(building.getIconName(), "drawable", context.getPackageName()));

            cityVH.linearLayout.addView(buildingView);
        }

        ImageView divider = new ImageView(context);
        divider.setImageResource(R.drawable.city_divider);
        cityVH.linearLayout.addView(divider);

//        Building building = buildings.get(i);
//        date = new Date(building.getPomodoro().getStoptime());
//        prevdate = new Date(building.getPomodoro().getStoptime());
//        calendar.setTime(date);
//
//        if (prevCalendar.get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR)) {
//            //TODO ADD DIVIDER
//            prevCalendar.setTime(prevdate);
//        }
    }

    @Override
    public int getItemCount() {
//        int i = 0;
//        for (Building building : buildings) {
//            date = new Date(building.getPomodoro().getStoptime());
//            prevdate = new Date(building.getPomodoro().getStoptime());
//            calendar.setTime(date);
//            if (prevCalendar.get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR) || building.getPomodoro().getTimerState() == TimerState.COMPLETED) {
//                i++;
//                prevCalendar.setTime(prevdate);
//            }
//        }
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
