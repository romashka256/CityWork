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
import android.view.View;

import com.citywork.App;
import com.citywork.R;

public class BuldingProgressView extends View {
    Bitmap bitmapOrg;

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


    @Override
    protected void onDraw(Canvas canvas) {
        Paint bgpaint = new Paint();
        bgpaint.setAntiAlias(true);
        bgpaint.setFilterBitmap(true);
        bgpaint.setDither(true);
        bgpaint.setAlpha(100);

        canvas.drawBitmap(bitmapOrg, 0, 0, bgpaint);

        canvas.drawBitmap(cropBitmap1(bitmapOrg), 0, 0, bgpaint);

//        Paint progresspaint = new Paint();
//        progresspaint.setAntiAlias(true);
//        progresspaint.setFilterBitmap(true);
//        progresspaint.setDither(true);
//        progresspaint.setAlpha(155);
//
//        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,
//                bitmapOrg.getWidth(), bitmapOrg.getHeight()/2);
//
//        canvas.drawBitmap(croppedBmp, 0, 0, progresspaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(bitmapOrg.getWidth());
        int height = MeasureSpec.getSize(bitmapOrg.getHeight());

        setMeasuredDimension(width, height);
    }

    private Bitmap cropBitmap1(Bitmap bitmap) {
        Bitmap bmOverlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas c = new Canvas(bmOverlay);
        c.drawBitmap(bitmap, 0, 0, null);
        c.drawRect(0, 0, bitmap.getWidth(), bitmapOrg.getHeight() - 100, p);

        return bmOverlay;
    }
}
