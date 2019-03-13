package com.citywork.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citywork.App;
import com.citywork.R;
import com.citywork.viewmodels.CityFragmentViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityFragment extends Fragment {

    @BindView(R.id.city_fragment_rv)
    RecyclerView recyclerView;
    @BindView(R.id.city_fragment_share)
    ImageView mShareIV;
    @BindView(R.id.city_fragment_settings)
    ImageView mSettings;
    @BindView(R.id.city_fragment_city_people_count)
    TextView mPeopleCountTV;

    private CityFragmentViewModel cityFragmentViewModel;
    private CityAdapter adapter;

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = App.getsAppComponent().getApplicationContext();
        cityFragmentViewModel = ViewModelProviders.of(this).get(CityFragmentViewModel.class);

        cityFragmentViewModel.onCreate();

        cityFragmentViewModel.getCitiesLoaded();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_fragment, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        cityFragmentViewModel.getCitiesLoaded().observe(this, pairs -> {
            adapter = new CityAdapter(pairs, context);
            recyclerView.setAdapter(adapter);
        });

        mShareIV.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Пашел нахуй";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }
}
