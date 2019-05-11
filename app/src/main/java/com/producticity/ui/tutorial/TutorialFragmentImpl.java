package com.producticity.ui.tutorial;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.producticity.App;
import com.producticity.R;
import com.producticity.utils.commonutils.UIUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;

public class TutorialFragmentImpl extends Fragment implements TutorialFragment {

    @BindView(R.id.tutorial_fragment_image)
    ImageView bgImage;
    @BindView(R.id.tutorial_fragment_subtitle)
    TextView mSubTitleTV;
    @BindView(R.id.tutorial_fragment_title)
    TextView mTitleTV;
    @BindView(R.id.tutorial_fragment_skip_btn)
    TextView mSkipTV;
    @BindView(R.id.tutorial_fragment_next_btn_tv)
    TextView mNextBtnTV;
    @BindView(R.id.tutorial_fragment_next_btn)
    ConstraintLayout mNextBtn;

    private String titleText;
    private String subTitleText;
    private int imageId;

    private UIUtils UIUtils;

    @Getter
    private TutorialActivity tutorialActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tutorialActivity = (TutorialActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorial_fragment, null, false);

        ButterKnife.bind(this, view);

        UIUtils = App.getsAppComponent().getFontUtils();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getArguments() != null) {
            titleText = getArguments().getString("titleText");
            subTitleText = getArguments().getString("subTitleText");
            imageId = getArguments().getInt("imageId");

            showData(titleText, subTitleText, imageId);
        }

        AssetManager am = getContext().getAssets();

        Typeface typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "Roboto-Bold.ttf"));

        mSkipTV.setTypeface(typeface);

        mNextBtn.setOnClickListener(this::onNextClick);

        mSkipTV.setOnClickListener(this::onSkilClick);

        mSkipTV.setTypeface(UIUtils.getLight());
        mNextBtnTV.setTypeface(UIUtils.getMedium());
        mSubTitleTV.setTypeface(UIUtils.getLight());
        mTitleTV.setTypeface(UIUtils.getBold());

    }

    @Override
    public void onNextClick(View view) {

    }

    @Override
    public void onSkilClick(View view) {

    }

    public void showData(String titleText, String subTitleText, int imageId) {
        mTitleTV.setText(titleText);
        mSubTitleTV.setText(subTitleText);
        bgImage.setImageResource(imageId);
    }
}
