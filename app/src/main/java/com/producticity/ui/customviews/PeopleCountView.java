package com.producticity.ui.customviews;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import com.producticity.R;

public class PeopleCountView extends View {

    private Path path;
    private Paint boxPaint;
    private Paint textPaint;
    private int radius;
    private float DEFAULT_PADDING_LEFT = 15;
    private float DEFAULT_ARR_RADIUS = 8;
    private float DEFAULT_PADDING_RIGHT = 15;
    private float DEFAULT_PADDING_TOP = 10;
    private float DEFAULT_PADDING_BOTTOM = 10;

    private float paddingLeft;
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;

    private float radius_arr;

    private float textpaddingLeft;
    private float textpaddingTop;
    private float textpaddingRight;
    private float textpaddingBottom;

    private String text;
    private float textWidth;
    private float textHeight;

    public PeopleCountView(Context context) {
        super(context);
        init();
    }

    public PeopleCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PeopleCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PeopleCountView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        path = new Path();
        boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setColor(getResources().getColor(R.color.colorAccent));
        float sp = spToPx(15);
        textPaint.setTextSize(sp);

        text = "Text";

        boxPaint.setColor(getResources().getColor(R.color.colorAccent));
        boxPaint.setStrokeWidth(3);
        boxPaint.setStyle(Paint.Style.STROKE);
        textWidth = textPaint.measureText(text);
        textHeight = getFontHeight(textPaint);

        radius_arr = dpToPx(DEFAULT_ARR_RADIUS);

        paddingLeft = dpToPx(radius / 2f + radius_arr);
        paddingTop = dpToPx(radius / 2f);

        textpaddingLeft = paddingLeft + dpToPx(DEFAULT_PADDING_LEFT);
        textpaddingTop = paddingTop + dpToPx(DEFAULT_PADDING_TOP);
        textpaddingRight = dpToPx(DEFAULT_PADDING_RIGHT);
        textpaddingBottom = dpToPx(DEFAULT_PADDING_BOTTOM);


        PathEffect pe1 = new CornerPathEffect(30);

        boxPaint.setPathEffect(pe1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.moveTo(paddingLeft, paddingTop);
        path.lineTo(textWidth + textpaddingLeft + textpaddingRight, paddingTop);
        path.lineTo(textWidth + textpaddingLeft + textpaddingRight, textHeight + textpaddingTop + textpaddingBottom);
        path.lineTo(paddingLeft, textHeight + textpaddingTop + textpaddingBottom);
        path.lineTo(paddingLeft, textHeight + textpaddingTop);
        path.lineTo(paddingLeft - radius_arr, textHeight / 2f + textpaddingTop);
        path.lineTo(paddingLeft, textpaddingTop);
        path.lineTo(paddingLeft, paddingTop);
        path.close();

        canvas.drawText(text, textpaddingLeft, textpaddingTop + textHeight, textPaint);

        canvas.drawPath(path, boxPaint);
    }

    public class Builder {
        public Builder() {
            radius = 30;
            text = "Text";
        }

        public Builder setRadius(int radius) {
            PeopleCountView.this.radius = radius;

            return this;
        }

        public Builder setText(String text) {
            PeopleCountView.this.text = text;

            return this;
        }

        public PeopleCountView build() {
            return PeopleCountView.this;
        }
    }


    public float spToPx(float px) {
        return px * getResources().getDisplayMetrics().scaledDensity;
    }

    public float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float getFontHeight(Paint paint) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }
}
