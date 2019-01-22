package com.citywork.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

public class CirclerTimer extends View {

    //Params
    private float mCx;
    private float mCy;
    private float mRadius;
    private float mCurrentRadian;
    private float mPreRadian;

    //Dimens
    private float mCircleLineWidth;
    private float mCircleButtonRadius;
    private float mTimerTextSize;
    private float mTimerNumbersSize;
    private float mGapBetweenNumberAndLine;
    private float mNumberSize;
    private float mLineLength;
    private float mLongerLineLength;
    private float mLineWidth;
    private float mCircleStrokeWidth;
    private float mGapBetweenTimerNumberAndText;

    //Paint
    private Paint mCirclePaint;
    private Paint mProgressCirclePaint;
    private Paint mTimeNumberPaint;
    private Paint mTimeTextPaint;
    private Paint mCircleButtonPaint;

    // Default dimension in dp/pt
    private static final float DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE = 5;
    private static final float DEFAULT_GAP_BETWEEN_NUMBER_AND_LINE = 5;
    private static final float DEFAULT_NUMBER_SIZE = 10;
    private static final float DEFAULT_LINE_LENGTH = 14;
    private static final float DEFAULT_LONGER_LINE_LENGTH = 23;
    private static final float DEFAULT_LINE_WIDTH = 0.5f;
    private static final float DEFAULT_CIRCLE_BUTTON_RADIUS = 15;
    private static final float DEFAULT_CIRCLE_WIDTH = 1;
    private static final float DEFAULT_TIMER_NUMBER_SIZE = 50;
    private static final float DEFAULT_TIMER_TEXT_SIZE = 14;
    private static final float DEFAULT_GAP_BETWEEN_TIMER_NUMBER_AND_TEXT = 30;

    //Default olors
    private final int DEFAULT_CIRCLE_COLOR = 0x1A000000;
    private final int DEFAULT_PROGRESS_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_TIMER_NUMBER_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_TIMER_TEXT_COLOR = 0x80FFFFFF;
    private final int DEFAULT_TIMER_MINUTES_COLOR = 0x59000000;
    private final int DEFAULT_CIRCLE_BUTTON_LINES_COLOR = 0xC4C4C4;

    //Colors
    private int mCircleColor;
    private int mColorPorgress;
    private int mTimerNumberColor;
    private int mTimerTextColor;
    private int mNumberColor;
    private int mCircleButtonLinesColor;

    public CirclerTimer(Context context) {
        super(context);
        init();
    }

    public CirclerTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclerTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CirclerTimer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        mCircleColor = DEFAULT_CIRCLE_COLOR;
        mColorPorgress = DEFAULT_PROGRESS_COLOR;
        mTimerNumberColor = DEFAULT_TIMER_NUMBER_COLOR;
        mTimerTextColor = DEFAULT_TIMER_TEXT_COLOR;
        mNumberColor = DEFAULT_TIMER_MINUTES_COLOR;
        mCircleButtonLinesColor = DEFAULT_CIRCLE_BUTTON_LINES_COLOR;

        mGapBetweenNumberAndLine = DEFAULT_GAP_BETWEEN_NUMBER_AND_LINE;
        mNumberSize = DEFAULT_NUMBER_SIZE;
        mLineLength = DEFAULT_LINE_LENGTH;
        mLongerLineLength = DEFAULT_LONGER_LINE_LENGTH;
        mLineWidth = DEFAULT_LINE_WIDTH;
        mCircleStrokeWidth = DEFAULT_CIRCLE_BUTTON_RADIUS;
        mGapBetweenTimerNumberAndText = DEFAULT_CIRCLE_WIDTH;

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        this.mCx = width / 2;
        this.mCy = height / 2;
        mRadius = width / 2;

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCx, mCy, mRadius, mCirclePaint);
        canvas.save();
        canvas.restore();
        super.onDraw(canvas);
    }
}
