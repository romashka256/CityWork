package com.citywork.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.citywork.App;
import com.citywork.R;
import com.citywork.model.db.models.Building;
import com.citywork.ui.customviews.BuldingProgressView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuccessDialogFragment extends DialogFragment {

    private Context context;

    @BindView(R.id.success_dialog_close_iv)
    ImageView vCloseIV;
    @BindView(R.id.success_dialog_to_city_btn)
    Button vToCityBtn;
    @BindView(R.id.success_dialog_buidling_view)
    BuldingProgressView mBuidlingView;

    private Building building;
    private static final String BUILDING = "buidling";

    public static SuccessDialogFragment newInstance(Building building) {
        SuccessDialogFragment f = new SuccessDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(BUILDING, Parcels.wrap(building));
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = App.getsAppComponent().getApplicationContext();

        building = Parcels.unwrap((Parcelable) getArguments().get(BUILDING));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.success_dialog, container, false);

        ButterKnife.bind(this, view);

        vCloseIV.setOnClickListener(v -> dismiss());

        mBuidlingView.setPeopleProgress(building.getPeople_count(), building.getPeople_count());
        mBuidlingView.setProgress(100);

        return view;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

       // window.setLayout((int) (size.x * 0.80),(int) (size.y * 0.5));
        window.setGravity(Gravity.CENTER);

        super.onResume();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }
}
