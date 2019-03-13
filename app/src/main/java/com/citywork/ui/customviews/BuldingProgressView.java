package com.citywork.ui.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.citywork.App;
import com.citywork.R;
import com.citywork.utils.VectorUtils;

public class BuldingProgressView extends View {

    private Path path;
    private Paint boxPaint;
    private Paint textPaint;
    private int radius;
    private float DEFAULT_PADDING_LEFT = 10;
    private float DEFAULT_ARR_RADIUS = 8;
    private float DEFAULT_PADDING_RIGHT = 10;
    private float DEFAULT_PADDING_TOP = 15;
    private float DEFAULT_PADDING_BOTTOM = 15;

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

    private Bitmap bitmapOrg;
    private int screenWidth;

    private Paint bottomLine;
    private Paint progressPaint;
    private Paint bgpaint;

    private int bottomLineHeight;

    private int currentProgress = 0;
    private float progressStep;

    private float buildingX, buildingY;
    private float buildingWidth, buildingHeight;

    private int viewW, viewH;

    public BuldingProgressView(Context context) {
        super(context);
        init();
    }

    public BuldingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BuldingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BuldingProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setImage(Bitmap bitmapOrg) {
        this.bitmapOrg = bitmapOrg;
        invalidate();
    }

    private void init() {
        bitmapOrg = VectorUtils.getBitmapFromVectorDrawable(App.getsAppComponent().getApplicationContext(), R.drawable.ic_icon_building1);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;

        bottomLine = new Paint();
        progressPaint = new Paint();
        bgpaint = new Paint();

        bottomLineHeight = (int) dpToPx(2);
        currentProgress = 0;

        buildingWidth = bitmapOrg.getWidth();
        buildingHeight = bitmapOrg.getHeight();

        buildingX = (getWidth() / 2f) - bitmapOrg.getWidth() / 2f;

        bgpaint.setAntiAlias(true);
        bgpaint.setFilterBitmap(true);
        bgpaint.setDither(true);
        bgpaint.setAlpha(100);

        progressPaint.setAntiAlias(true);
        progressPaint.setFilterBitmap(true);
        progressPaint.setDither(true);
        progressPaint.setAlpha(255);

        bottomLine.setAlpha(150);
        bottomLine.setColor(0xfffffff);
        bottomLine.setAntiAlias(true);
        bottomLine.setStyle(Paint.Style.STROKE);
        bottomLine.setStrokeWidth(bottomLineHeight);

        path = new Path();
        boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setColor(getResources().getColor(R.color.white));
        float sp = spToPx(15);
        textPaint.setTextSize(sp);

        boxPaint.setColor(getResources().getColor(R.color.transoarent10_black));
        boxPaint.setStyle(Paint.Style.FILL);
        //TODO INITIATE
        text = "text";
        textWidth = textPaint.measureText(text);
        textHeight = getFontHeight(textPaint);

        radius_arr = dpToPx(DEFAULT_ARR_RADIUS);

        textpaddingRight = dpToPx(DEFAULT_PADDING_RIGHT);
        textpaddingBottom = dpToPx(DEFAULT_PADDING_BOTTOM);

        PathEffect pe1 = new CornerPathEffect(30);

        boxPaint.setPathEffect(pe1);
    }


    public void setProgress(int progress) {
        currentProgress = (int) (progress * progressStep);
        invalidate();
    }

    public void showBuildedTower() {
        setProgress(100);
    }

    public void setPeopleProgress(int peopleplus, int allpeople) {
        text = peopleplus + "/" + allpeople;
        textWidth = textPaint.measureText(text);
        invalidate();
    }

    public void setPeopleCount(int allpeople) {
        text = 0 + "/" + allpeople;
        textWidth = textPaint.measureText(text);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        buildingX = (getWidth() / 2f) - bitmapOrg.getWidth() / 2f;

        paddingLeft = dpToPx(radius / 2f + radius_arr) + buildingWidth + buildingX;
        paddingTop = dpToPx(radius / 2f) + buildingHeight / 2;

        textpaddingLeft = paddingLeft + dpToPx(DEFAULT_PADDING_LEFT);
        textpaddingTop = paddingTop + dpToPx(DEFAULT_PADDING_TOP);

        int top = (viewH - bitmapOrg.getHeight());

        canvas.drawBitmap(bitmapOrg, buildingX, top - bottomLineHeight, bgpaint);

        canvas.drawBitmap(cropBitmap1(bitmapOrg), (getWidth() / 2f) - bitmapOrg.getWidth() / 2f, top - bottomLineHeight, progressPaint);

        canvas.drawLine(-getWidth(), buildingHeight, getWidth(), buildingHeight, bottomLine);

        path.reset();
        path.moveTo(paddingLeft, paddingTop);
        path.lineTo(textpaddingLeft + textpaddingRight + textWidth, paddingTop);
        path.lineTo(textWidth + textpaddingLeft + textpaddingRight, textHeight + textpaddingTop + textpaddingBottom);
        path.lineTo(paddingLeft, textHeight + textpaddingTop + textpaddingBottom);
        path.lineTo(paddingLeft, textHeight + textpaddingTop);
        path.lineTo(paddingLeft - radius_arr, textHeight / 2f + textpaddingTop);
        path.lineTo(paddingLeft, textpaddingTop);
        path.lineTo(paddingLeft, paddingTop);
        path.close();

        canvas.drawPath(path, boxPaint);

        canvas.drawText(text, textpaddingLeft, textpaddingTop + textHeight, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(bitmapOrg.getWidth());
        int height = MeasureSpec.getSize(bitmapOrg.getHeight());

        width += screenWidth / 2;
        height += bottomLineHeight;

        progressStep = calculateStep(height);

        viewH = height;
        viewW = width;

        setMeasuredDimension(width, height);
    }

    private Bitmap cropBitmap1(Bitmap bitmap) {
        Bitmap bmOverlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas c = new Canvas(bmOverlay);
        c.drawBitmap(bitmap, 0, 0, null);
        c.drawRect(0, 0, bitmap.getWidth(), bitmapOrg.getHeight() - currentProgress, p);

        return bmOverlay;
    }

    private float calculateStep(int height) {
        float asd = (height - bottomLineHeight) / 100f;
        return asd;
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
