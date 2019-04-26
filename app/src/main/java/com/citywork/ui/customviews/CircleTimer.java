package com.citywork.ui.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.citywork.utils.Calculator;

import java.util.Locale;

public class CircleTimer extends View {

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
    private float mLongerLineWidth;
    private float mLineWidth;
    private float mCircleButtonWidth;
    private float mGapBetweenTimerNumberAndText;
    private float mGapBetweenCircleAndLine;
    private float lineLength;

    //Paint
    private Paint mCirclePaint;
    private Paint mProgressCirclePaint;
    private Paint mProgressCirclePaintMoving;
    private Paint mTimeNumberPaint;
    private Paint mTimeNumbersPaint;
    private Paint mTimeTextPaint;
    private Paint mCircleButtonPaint;
    private Paint mLinePaint;

    // Default dimension in dp/pt
    private static final float DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE = 0;
    private static final float DEFAULT_GAP_BETWEEN_NUMBER_AND_LINE = 5;
    private static final float DEFAULT_NUMBER_SIZE = 50;
    private static final float DEFAULT_LINE_LENGTH = 8;
    private static final float DEFAULT_LONGER_LINE_LENGTH = 15;
    private static final float DEFAULT_LINE_WIDTH = 1f;
    private static final float DEFAULT_LONGER_LINE_WIDTH = 3f;
    private static final float DEFAULT_CIRCLE_BUTTON_RADIUS = 12;
    private static final float DEFAULT_CIRCLE_WIDTH = 3f;
    private static final float DEFAULT_GAM_BETWEEN_LINE_AND_CIRCLE = 1;
    private static final float DEFAULT_TIMER_NUMBER_SIZE = 12;
    private static final float DEFAULT_TIMER_TEXT_SIZE = 12;
    private static final float DEFAULT_GAP_BETWEEN_TIMER_NUMBER_AND_TEXT = 12;

    //Default olors
    private final int DEFAULT_CIRCLE_COLOR = 0x1A000000;
    private final int DEFAULT_CIRCLE_BUTTON_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_PROGRESS_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_TIMER_NUMBER_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_TIMER_NUMBERS_COLOR = 0x5C000000;
    private final int DEFAULT_TIMER_TEXT_COLOR = 0x80FFFFFF;
    private final int DEFAULT_TIMER_MINUTES_COLOR = 0x59000000;
    private final int DEFAULT_CIRCLE_BUTTON_LINES_COLOR = 0xC4C4C4;

    //Colors
    private int mCircleColor;
    private int mCircleButtonColor;
    private int mColorPorgress;
    private int mTimerNumberColor;
    private int mTimerTextColor;
    private int mNumberColor;
    private int mCircleButtonLinesColor;


    private RectF mProgressArc;
    private boolean mInCircleButton;
    private long mCurrentTime;
    private final String HINT_TEXT = "минут";
    private boolean isEnabled;
    private int minTime;
    private int maxTime;
    private float minRadian;

    private final int DEFAULT_MIN_TIME = 600;
    private final int DEFAULT_MAX_TIME = 3600;

    private CircleTimerListener circleTimerListener;

    public CircleTimer(Context context) {
        super(context);
        init();
    }

    public CircleTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleTimer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mCircleColor = DEFAULT_CIRCLE_COLOR;
        mColorPorgress = DEFAULT_PROGRESS_COLOR;
        mTimerNumberColor = DEFAULT_TIMER_NUMBER_COLOR;
        mTimerTextColor = DEFAULT_TIMER_TEXT_COLOR;
        mNumberColor = DEFAULT_TIMER_NUMBERS_COLOR;
        mCircleButtonLinesColor = DEFAULT_CIRCLE_BUTTON_LINES_COLOR;
        mCircleButtonColor = DEFAULT_CIRCLE_BUTTON_COLOR;

        mGapBetweenNumberAndLine = dpToPx(DEFAULT_GAP_BETWEEN_NUMBER_AND_LINE);
        mNumberSize = dpToPx(DEFAULT_NUMBER_SIZE);
        mTimerNumbersSize = dpToPx(DEFAULT_TIMER_NUMBER_SIZE);
        mLineLength = dpToPx(DEFAULT_LINE_LENGTH);
        mLongerLineLength = dpToPx(DEFAULT_LONGER_LINE_LENGTH);
        mLineWidth = dpToPx(DEFAULT_LINE_WIDTH);
        mLongerLineWidth = dpToPx(DEFAULT_LONGER_LINE_WIDTH);
        mCircleButtonRadius = dpToPx(DEFAULT_CIRCLE_BUTTON_RADIUS);
        mGapBetweenTimerNumberAndText = dpToPx(DEFAULT_GAP_BETWEEN_TIMER_NUMBER_AND_TEXT);
        mCircleLineWidth = dpToPx(DEFAULT_CIRCLE_WIDTH);
        mGapBetweenCircleAndLine = dpToPx(DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE);
        mTimerTextSize = dpToPx(DEFAULT_TIMER_TEXT_SIZE);

        mCurrentTime = DEFAULT_MIN_TIME;
        minTime = DEFAULT_MIN_TIME;
        maxTime = DEFAULT_MAX_TIME;

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeNumbersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressCirclePaintMoving = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTimeTextPaint.setColor(mTimerTextColor);
        mTimeTextPaint.setTextSize(mTimerTextSize);
        mTimeTextPaint.setTextAlign(Paint.Align.CENTER);

        mTimeNumberPaint.setColor(mTimerNumberColor);
        mTimeNumberPaint.setTextSize(mNumberSize);
        mTimeNumberPaint.setTextAlign(Paint.Align.CENTER);

        mTimeNumbersPaint.setColor(mNumberColor);
        mTimeNumbersPaint.setTextAlign(Paint.Align.CENTER);
        mTimeNumbersPaint.setTextSize(mTimerNumbersSize);

        mCircleButtonPaint.setColor(mCircleButtonColor);
        mCircleButtonPaint.setAntiAlias(true);
        mCircleButtonPaint.setStyle(Paint.Style.FILL);

        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleLineWidth);

        mProgressCirclePaint.setColor(mColorPorgress);
        mProgressCirclePaint.setStyle(Paint.Style.STROKE);
        mProgressCirclePaint.setStrokeWidth(mCircleLineWidth);


        mProgressCirclePaintMoving.setColor(mCircleColor);
        mProgressCirclePaintMoving.setStyle(Paint.Style.STROKE);
        mProgressCirclePaintMoving.setStrokeWidth(mCircleLineWidth);

        mProgressArc = new RectF();

        setMinTime(minTime);
        mCurrentRadian = minRadian;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        this.mCx = width / 2;
        this.mCy = width / 2;
        mRadius = width / 2 - mCircleLineWidth / 2 - (mCircleButtonRadius - mCircleLineWidth / 2);

        mProgressArc.set(mCx - mRadius, mCy - mRadius, mCx + mRadius, mCy + mRadius);


        setMeasuredDimension(width, width);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCx, mCy, mRadius, mCirclePaint);
        canvas.drawArc(mProgressArc, 270, (float) Math.toDegrees(mCurrentRadian), false, mProgressCirclePaint);
        canvas.save();

        if (!isEnabled) {
            for (int i = 0; i < 20; i++) {
                canvas.save();
                canvas.rotate(360 / 20 * i, mCx, mCy);

                if (i % 5 == 0) {
                    lineLength = mLongerLineLength;
                    mProgressCirclePaintMoving.setStrokeWidth(mLongerLineWidth);
                } else {
                    lineLength = mLineLength;
                    mProgressCirclePaintMoving.setStrokeWidth(mLineWidth);
                }

                canvas.drawLine(mCx,
                        getMeasuredHeight() / 2 - mRadius + mCircleLineWidth + mGapBetweenCircleAndLine,
                        mCx,
                        getMeasuredHeight() / 2 - mRadius + mCircleLineWidth + lineLength + mGapBetweenCircleAndLine,
                        mProgressCirclePaintMoving);

                canvas.restore();
            }

            canvas.restore();

            // Number it is rubbish code
            float textLength = mTimeNumbersPaint.measureText("15");
            canvas.drawText("60", mCx, getMeasuredHeight() / 2 - mRadius + mGapBetweenCircleAndLine + mLongerLineLength + mGapBetweenNumberAndLine + mCircleLineWidth / 2 + mCircleButtonRadius / 2 + getFontHeight(mTimeNumbersPaint) / 2, mTimeNumbersPaint);
            canvas.drawText("15", mCx + mRadius - mCircleLineWidth / 2 - mGapBetweenCircleAndLine - mLongerLineLength -
                    textLength / 2
                    - mGapBetweenNumberAndLine, mCy + getFontHeight(mTimeNumbersPaint) / 2, mTimeNumbersPaint);
            canvas.drawText("30", mCx, getMeasuredHeight() / 2 + mRadius - mCircleLineWidth / 2 - mGapBetweenCircleAndLine -
                    mLongerLineLength - mGapBetweenNumberAndLine, mTimeNumbersPaint);
            canvas.drawText("45", mCx - mRadius + mCircleLineWidth / 2 + mGapBetweenCircleAndLine +
                            mLongerLineLength + mGapBetweenNumberAndLine + textLength / 2, mCy + getFontHeight(mTimeNumbersPaint) / 2,
                    mTimeNumbersPaint);
            canvas.save();

            // Circle button
            canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);

            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius, mCircleButtonRadius, mCircleButtonPaint);
            canvas.restore();
            // Timer Text
            canvas.save();
            canvas.drawText(HINT_TEXT, mCx, mCy + getFontHeight(mTimeNumberPaint) / 2 + mGapBetweenTimerNumberAndText + getFontHeight
                    (mTimeTextPaint), mTimeTextPaint);
            canvas.restore();
        }

        //TimerNumber
        canvas.save();
        canvas.drawText(Calculator.getMinutesAndSecondsFromSeconds(mCurrentTime), mCx, mCy + getFontHeight(mTimeNumberPaint) / 2, mTimeNumberPaint);
        canvas.restore();


        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mInCircleButton(event.getX(), event.getY())) {
                    mInCircleButton = true;
                    mPreRadian = getRadian(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInCircleButton && !isEnabled) {
                    float temp = getRadian(event.getX(), event.getY());
                    if (mPreRadian > Math.toRadians(270) && temp < Math.toRadians(90)) {
                        mPreRadian -= 2 * Math.PI;
                    } else if (mPreRadian < Math.toRadians(90) && temp > Math.toRadians(270)) {
                        mPreRadian = (float) (temp + (temp - 2 * Math.PI) - mPreRadian);
                    }
                    mCurrentRadian += (temp - mPreRadian);
                    mPreRadian = temp;
                    if (mCurrentRadian > 2 * Math.PI) {
                        mCurrentRadian = (float) (2 * Math.PI);
                    } else if (mCurrentRadian < 0) {
                        mCurrentRadian = 0;
                    }
                    if (mCurrentRadian >= minRadian) {
                        mCurrentTime = (int) (60 / (2 * Math.PI) * mCurrentRadian * 60);
                    } else {
                        mCurrentRadian = minRadian;
                        mCurrentTime = minTime;
                    }
                    if (circleTimerListener != null)
                        circleTimerListener.onTimerTimingValueChanged(mCurrentTime);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mInCircleButton) {
                    mInCircleButton = false;
                }
                break;
        }
        return true;
    }


    private float getFontHeight(Paint paint) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }

    // Use tri to cal radian
    private float getRadian(float x, float y) {
        float alpha = (float) Math.atan((x - mCx) / (mCy - y));
        // Quadrant
        if (x > mCx && y > mCy) {
            // 2
            alpha += Math.PI;
        } else if (x < mCx && y > mCy) {
            // 3
            alpha += Math.PI;
        } else if (x < mCx && y < mCy) {
            // 4
            alpha = (float) (2 * Math.PI + alpha);
        }
        return alpha;
    }

    // Whether the down event inside circle button
    private boolean mInCircleButton(float x, float y) {
        float r = mRadius;
        float x2 = (float) (mCx + r * Math.sin(mCurrentRadian));
        float y2 = (float) (mCy - r * Math.cos(mCurrentRadian));
        if (Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)) < mCircleButtonRadius) {
            return true;
        }
        return false;
    }

    public void setProgress(long currentTime) {
        this.isEnabled = true;
        this.mCurrentTime = currentTime;
        mCurrentRadian = calculateRadianByTime(mCurrentTime);
        // mCurrentRadian -= (2 * Math.PI) / 3600;
        invalidate();
    }

    public void disable() {
        this.isEnabled = false;
        invalidate();
    }

    public void enable() {
        this.isEnabled = true;
        invalidate();
    }

    public void setCircleTimeListener(CircleTimerListener circleTimeListener) {
        this.circleTimerListener = circleTimeListener;
    }

    private float calculateRadianByTime(long time) {
        return (float) (((2 * Math.PI) / 3600) * time * (3600 / maxTime));
    }

    public void setMinTime(int minTime) {
        minRadian = calculateRadianByTime(minTime);
        this.minTime = minTime;
    }

    public void setTime(long time) {
        this.mCurrentTime = time;
        mCurrentRadian = calculateRadianByTime(time);
        invalidate();
    }

    public void setTimerTypeface(Typeface timerTypeface) {
        mTimeNumberPaint.setTypeface(timerTypeface);
        invalidate();
    }

    public void setSubtitleTypeface(Typeface typeface) {
        mTimeTextPaint.setTypeface(typeface);
        invalidate();
    }

    public void setNumbersTypeface(Typeface typeface) {
        mTimeNumbersPaint.setTypeface(typeface);
        invalidate();
    }



    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getMinTime() {
        return minTime;
    }

    public interface CircleTimerListener {
        /**
         * launch timer timing value changed event
         *
         * @param time
         */
        void onTimerTimingValueChanged(long time);

        /**
         * launch timer set value changed event
         *
         * @param time
         */
        void onTimerSetValueChanged(int time);


        /**
         * launch timer set value chang event
         *
         * @param time
         */
        void onTimerSetValueChange(int time);
    }

    public float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
