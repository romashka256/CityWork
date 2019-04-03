package com.citywork.ui.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.citywork.R;
import com.citywork.utils.chart.ChartBar;

import java.util.List;

import androidx.annotation.Nullable;

public class LineChart extends View {

    //DATA
    private List<ChartBar> chartBars;

    private final float DEFAULT_WIDTH_BAR = 10f;

    private float barWidth;

    //PAINT
    private Paint barPaint;
    private Paint selectedBarPaint;

    public LineChart(Context context) {
        super(context);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        barWidth = DEFAULT_WIDTH_BAR;

        barPaint = new Paint();
        barPaint.setColor(getResources().getColor(R.color.barcolor));

        selectedBarPaint = new Paint();
        selectedBarPaint.setColor(getResources().getColor(R.color.blue));
    }

    public void setValues(List<ChartBar> values){
        this.chartBars = values;
        invalidate();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
