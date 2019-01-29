package com.citywork.ui.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.citywork.App;
import com.citywork.R;

import timber.log.Timber;

public class BuldingProgressView extends View {
    private Bitmap bitmapOrg;
    private int screenWidth;


    private Paint bottomLine;
    private Paint progressPaint;
    private Paint bgpaint;

    private int bottomLineHeight;

    private int currentProgress = 0;
    private float progressStep;

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

    private void init() {
        bitmapOrg = getBitmapFromVectorDrawable(App.getsAppComponent().getApplicationContext(), R.drawable.ic_icon_building1);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;

        bottomLine = new Paint();
        progressPaint = new Paint();
        bgpaint = new Paint();

        bottomLineHeight = 6;
        currentProgress = 0;


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
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void setProgress(int progress) {
        Timber.i("setProgress to building view : %d", progress);
        currentProgress = (int) (progress * progressStep);
        Timber.i("current progress in px : %d", currentProgress);
        Timber.i("progress step in px : " + progressStep);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmapOrg, (getWidth() / 2) - bitmapOrg.getWidth() / 2, 0, bgpaint);

        canvas.drawBitmap(cropBitmap1(bitmapOrg), (getWidth() / 2) - bitmapOrg.getWidth() / 2, 0, progressPaint);

        canvas.drawLine(-getWidth(), bitmapOrg.getHeight(), getWidth(), bitmapOrg.getHeight(), bottomLine);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(bitmapOrg.getWidth());
        int height = MeasureSpec.getSize(bitmapOrg.getHeight());

        width += screenWidth / 2;
        height += bottomLineHeight;

        progressStep = calculateStep(height);

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
}
