package com.citywork.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import butterknife.internal.Utils;

public class FixedMaxHeightRecylerView extends RecyclerView {
    public FixedMaxHeightRecylerView(@NonNull Context context) {
        super(context);
    }

    public FixedMaxHeightRecylerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedMaxHeightRecylerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec(dpToPx(500), MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }

    public int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

}
