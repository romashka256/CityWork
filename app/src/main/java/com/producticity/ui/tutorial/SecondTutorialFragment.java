package com.producticity.ui.tutorial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import androidx.navigation.Navigation;

import com.producticity.R;

public class SecondTutorialFragment extends TutorialFragmentImpl {
    private String title;
    private String sunTitle;
    private int imageID;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = getResources().getString(R.string.second_tutor_title);
        sunTitle = getResources().getString(R.string.second_subtitle_title);
        imageID = R.drawable.img2;

        showData(title, sunTitle, imageID);
    }

    @Override
    public void onNextClick(View view) {
        super.onNextClick(view);
        Navigation.findNavController(view).navigate(R.id.action_secondTutorialFragment_to_thirdTutorialFragment);
        ((TutorialActivity) getActivity()).setSelectedTab(2);
    }

    @Override
    public void onSkilClick(View view) {
        super.onSkilClick(view);

        getActivity().finish();
    }
}
