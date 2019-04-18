package com.citywork.ui.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.citywork.R;
import com.citywork.ui.listeners.OnBarSelected;
import com.citywork.utils.chart.BarModeState;
import com.citywork.utils.chart.ChartBar;
import com.citywork.utils.chart.CustomChartUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class LineChart extends View {

    //DATA
    private List<ChartBar> chartBars;
    private List<String> xLabels;

    private Canvas canvas;

    private final int DEFAULT_WIDTH_BAR = 10;
    private final int DEFAULT_TOP_LABEL_MARGIN = 20;
    private final float DEFAULT_BOTTOM_LABEL_MARGIN_PERCENT = 0.05f;
    private final int DEFAULT_LEFT_BARS_MARGIN = 20;
    private final int DEFAULT_RIGHT_BARS_MARGIN = 20;
    private final int DEFAULT_GRID_WIDTH = 1;
    private final int DEFAULT_LABEL_TEXT_SIZE = 11;

    //SIZE
    private int width;
    private int height;
    private int heightForBars;
    private float barWidth;
    private float spaceBetweenBars;
    private float gridWidth;
    private float labelTextSize;

    //MARGINS
    private int bottomLabelMargin;
    private int topLabelMargin;
    private int barsLeftMargin;
    private int barsRightMargin;

    //SHAPES
    Path gridLine;
    private List<RectF> bars;

    //PAINT
    private Paint barPaint;
    private Paint selectedBarPaint;
    private Paint gridLinePaint;
    private Paint labelPaint;

    //UTILS
    private CustomChartUtils customChartUtils;

    //LISTENERS
    private OnBarSelected onBarSelected;

    //OTHER
    private int labelCount;
    private int spaceBetweenLabel;
    private int labelFontWidth;
    private int labelFontHeight;

    private int selectedIndex;
    private boolean selected;

    private BarModeState barModeState;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        bars = new ArrayList<>();
        customChartUtils = new CustomChartUtils();


        bottomLabelMargin = dpToPx(DEFAULT_WIDTH_BAR);
        topLabelMargin = dpToPx(DEFAULT_TOP_LABEL_MARGIN);

        barsRightMargin = dpToPx(DEFAULT_LEFT_BARS_MARGIN);
        gridWidth = dpToPx(DEFAULT_GRID_WIDTH);

        labelTextSize = dpToPx(DEFAULT_LABEL_TEXT_SIZE);

        gridLine = new Path();

        labelPaint = new Paint();
        labelPaint.setColor(getResources().getColor(R.color.barcolor));
        labelPaint.setTextSize(labelTextSize);

        barPaint = new Paint();
        barPaint.setColor(getResources().getColor(R.color.barcolor));

        selectedBarPaint = new Paint();
        selectedBarPaint.setColor(getResources().getColor(R.color.blue));

        gridLinePaint = new Paint();
        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setStrokeWidth(gridWidth);
        gridLinePaint.setColor(getResources().getColor(R.color.barcolor));
        gridLinePaint.setPathEffect(new DashPathEffect(new float[]{dpToPx(10), dpToPx(10)}, 0f));


    }

    public void setValues(List<ChartBar> values, List<String> labels) {
        selected = false;
        this.chartBars = values;
        this.xLabels = labels;
        this.labelCount = xLabels.size();
        this.labelFontHeight = (int) getFontHeight(labelPaint);
        this.labelFontWidth = (int) getFontWidth(labelPaint, labels.get(0));
        this.spaceBetweenLabel = ((width - (barsLeftMargin * 2)) / labelCount) - (labelFontWidth / 12);

        Pair<Integer, Integer> sizes = customChartUtils.calculateBarAndSpace(width - (barsLeftMargin + barsLeftMargin), values.size());

        barWidth = sizes.first;
        spaceBetweenBars = sizes.second;

        int widthUsed = barsLeftMargin;

        bars.clear();
        for (ChartBar chartBar : chartBars) {
            RectF rectF = new RectF();
            //TODO : ADD LABEL HEIGHT
            int barHeight = (heightForBars * chartBar.getYValue()) / 100;
            rectF.set(widthUsed, heightForBars - barHeight, widthUsed + barWidth, heightForBars);

            widthUsed += barWidth + spaceBetweenBars;
            bars.add(rectF);
        }

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        this.width = width;
        this.height = height;

        barsLeftMargin = (int) (DEFAULT_BOTTOM_LABEL_MARGIN_PERCENT * width);

        //TODO : ADD LABEL HEIGHT
        heightForBars = height - (topLabelMargin + bottomLabelMargin);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(getResources().getColor(R.color.totalwhite));

        for (int i = 0; i < bars.size(); i++) {
            RectF rectF = bars.get(i);
            if (selected) {
                if (selectedIndex == i) {
                    canvas.drawRect(rectF, selectedBarPaint);
                } else {
                    canvas.drawRect(rectF, barPaint);
                }
            } else {
                canvas.drawRect(rectF, barPaint);
            }

        }

        int widthUsed = barsLeftMargin;
        if (xLabels != null && !xLabels.isEmpty()) {
            for (String label : xLabels) {
                canvas.drawText(label, widthUsed, heightForBars + topLabelMargin, labelPaint);
                widthUsed += spaceBetweenLabel + labelFontWidth;
            }
        }

        int heightused = 0;
        int spaceBetweenGrid = heightForBars / 4;
        for (int i = 0; i < 5; i++) {
            gridLine.reset();
            gridLine.moveTo(0, heightused);
            gridLine.lineTo(width, heightused);
            canvas.drawPath(gridLine, gridLinePaint);

            heightused += spaceBetweenGrid;
        }


    }

    public int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        RectF rectF;
        for (int i = 0; i < bars.size(); i++) {
            rectF = bars.get(i);
            if (rectF.contains(event.getX(), event.getY())) {
                onBarSelected.onBarSelected(chartBars.get(i).getXValue());
                selected = true;
                selectedIndex = i;

                invalidate();
            }
        }

        return super.onTouchEvent(event);
    }

    public void setOnBarClickListener(OnBarSelected onBarSelected) {
        this.onBarSelected = onBarSelected;

    }

    private float getFontHeight(Paint paint) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }

    private float getFontWidth(Paint paint, String text) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, 1, rect);
        return rect.width();
    }

    private void clear() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }


}
