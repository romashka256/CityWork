package com.citywork.ui.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.citywork.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CityView extends ViewGroup {

    private Context context;

    private final int DEFAULT_MARGIN_BETWEEN_BUILDINGS = 12;
    private final int DEFAULT_CITY_HEIGHT = 200;

    private float buildingsMargin;
    private int minCityWidth;
    private int minCityHeight;

    private List<Bitmap> buildingBitmaps;
    private List<String> buildingIconNames;

    private int cityWidth;
    private int cityHeight;


    public CityView(Context context) {
        super(context);
        init(context);
    }

    public CityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CityView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        this.context = context;

        buildingsMargin = dpToPx(DEFAULT_MARGIN_BETWEEN_BUILDINGS);
        buildingBitmaps = new ArrayList<>();
        minCityHeight = (int) dpToPx(DEFAULT_CITY_HEIGHT);
    }

    public void clear() {
        this.buildingBitmaps = new ArrayList<>();
        invalidate();
    }

    public void setBuildings(List<String> buildings) {
        this.buildingIconNames = buildings;
        buildingBitmaps.clear();
        this.removeAllViews();

        for (String buildingName : buildingIconNames) {

            try {
                Bitmap bitmap;
                int id = context.getResources().getIdentifier(buildingName, "drawable", context.getPackageName());

                Drawable drawable = getResources().getDrawable(id);
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                buildingBitmaps.add(bitmap);
            } catch (OutOfMemoryError e) {

                Timber.e(e);
            }
        }

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width;

        minCityWidth = context.getResources().getDisplayMetrics().widthPixels / 2;

        int fullWidth = 0;

        for (Bitmap buildingBitmap : buildingBitmaps) {
            fullWidth += (buildingBitmap.getWidth() + buildingsMargin);
        }

        fullWidth += buildingsMargin;

        if (minCityWidth <= fullWidth) {
            width = fullWidth;
        } else {
            width = minCityWidth;
        }

        cityWidth = width;
        cityHeight = (int) dpToPx(DEFAULT_CITY_HEIGHT);

        setMeasuredDimension(width, cityHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int widthUsed = 0;
        int left = 0;
        int right = 0;
        int top = 0;
        int bot = 0;

        for (Bitmap buildingBitmap : buildingBitmaps) {
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(buildingBitmap);

            int buildingW = buildingBitmap.getWidth();
            int buildingH = buildingBitmap.getHeight();

            left = (int) (widthUsed + buildingsMargin);
            top = minCityHeight - buildingH;

            addView(imageView);

            imageView.layout(left,
                    top,
                    left + buildingW,
                    top + buildingH);
            widthUsed += (buildingW + buildingsMargin);
        }

        ImageView divider = new ImageView(context);
        divider.setBackgroundResource(R.drawable.city_divider);
        divider.layout((int) (cityWidth - dpToPx(5)), 0, cityWidth, cityHeight);
    }

    public float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
