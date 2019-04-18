package com.citywork.ui.customviews;

import android.animation.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.citywork.R;
import com.citywork.utils.VectorUtils;

import timber.log.Timber;


public class BottomNavigationItemView extends ViewGroup {
    private ImageView imageView;
    private TextView textView;

    private RectF backgroundRect;
    private Paint backgroundRectPaint;

    private final String IMAGEVIEW_TAG = "imageviewtag";

    private final int DEFAULT_IMAGE_TOP_PADDING = 32;
    private final int DEFAULT_IMAGE_BOTTOM_PADDING = 32;
    private final int DEFAULT_IMAGE_LEFT_PADDING = 64;
    private final int DEFAULT_IMAGE_RIGHT_PADDING = 64;
    private final int DEFAULT_PADDING_BETWEEN_IMAGEANDTEXT = 16;
    private final int DEFAULT_PADDING_TEXT_RIGHT = 16;

    private ValueAnimator backgroundAlphaAnim;
    private ValueAnimator containerSizeAmin;
    private AnimatorSet animatorSet;

    private NavItemState navItemState;

    private int iconId;
    private String text;

    private float currentwidth;
    private float widthWithText;
    private Context context;

    private ObjectAnimator objectAnimator;

    private Bitmap image;

    private int viewWidth;
    private int viewHeight;

    public NavItemState getNavItemState() {
        return navItemState;
    }

    public BottomNavigationItemView(Context context) {
        this(context, null);
        init(context);
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        backgroundRectPaint = new Paint();
        backgroundRectPaint.setColor(getResources().getColor(R.color.nav_bar_item_bg));
       // backgroundRectPaint.setAlpha(100);

        backgroundRect = new RectF();

        textView = new TextView(context);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(15);
        textView.setAlpha(0);
        textView.setVisibility(GONE);

        this.setBackground(getResources().getDrawable(R.drawable.tabbg));
        this.getBackground().setAlpha(0);


        imageView = new ImageView(context);

        imageView.setTag(IMAGEVIEW_TAG);
        imageView.setAlpha(0.5f);

        widthWithText = 0;

        navItemState = NavItemState.INACTIVE;

        addView(imageView);
        addView(textView);
    }

    public void makeActivated() {
        navItemState = NavItemState.ACTIVE;
        activeAnim();
    }

    public void setDataToImet(String text, int resId) {
        this.text = text;
        this.iconId = resId;

        image = VectorUtils.getBitmapFromVectorDrawable(context, resId);
        imageView.setImageBitmap(image);
        textView.setText(text);
    }

    private void activeAnim() {
        currentwidth = 0;
        float startPoint = imageView.getMeasuredWidth();
        final float fullwidth = widthWithText + startPoint;

        animatorSet = new AnimatorSet();

        backgroundAlphaAnim = ValueAnimator.ofInt(0, 210);
        backgroundAlphaAnim.setInterpolator(new AccelerateInterpolator());
        backgroundAlphaAnim.setDuration(100);
        final BottomNavigationItemView v = this;
        backgroundAlphaAnim.addUpdateListener(animation -> v.getBackground().setAlpha((Integer) animation.getAnimatedValue()));
        containerSizeAmin = ValueAnimator.ofFloat(0, widthWithText);
        containerSizeAmin.setDuration(100);

        Timber.i("widthWithText : " + widthWithText);
        Timber.i("startPoint : " + startPoint);
        Timber.i("fullwidth : " + fullwidth);

        containerSizeAmin.addUpdateListener(animation -> {
            Timber.i("Math.abs((Float) animation.getAnimatedValue() : " + Math.abs((Float) animation.getAnimatedValue()));
            if (textView.getVisibility() != VISIBLE) {
                if (Math.abs((Float) animation.getAnimatedValue()) > (widthWithText / 1.5)) {
                    showTextAnim();
                    showPressedImage();
                }
            }
            currentwidth = Math.abs((Float) animation.getAnimatedValue());
            requestLayout();
        });

        animatorSet.playTogether(containerSizeAmin, backgroundAlphaAnim);
        animatorSet.start();
    }


    private void disableAnim() {
        currentwidth = 0;
        float startPoint = imageView.getMeasuredWidth();
        final float fullwidth = widthWithText + startPoint;

        animatorSet = new AnimatorSet();

        backgroundAlphaAnim = ValueAnimator.ofInt(140, 0);
        backgroundAlphaAnim.setInterpolator(new AccelerateInterpolator());
        backgroundAlphaAnim.setDuration(100);
        final BottomNavigationItemView v = this;
        backgroundAlphaAnim.addUpdateListener(animation -> v.getBackground().setAlpha((Integer) animation.getAnimatedValue()));
        containerSizeAmin = ValueAnimator.ofFloat(widthWithText, 0);
        containerSizeAmin.setDuration(100);

        Timber.i("widthWithText : " + widthWithText);
        Timber.i("startPoint : " + startPoint);
        Timber.i("fullwidth : " + fullwidth);

        containerSizeAmin.addUpdateListener(animation -> {
            Timber.i("Math.abs((Float) animation.getAnimatedValue() : " + Math.abs((Float) animation.getAnimatedValue()));
            currentwidth = Math.abs((Float) animation.getAnimatedValue());
            requestLayout();
        });

        showInactiveImage();
        hideTextAnim();
        animatorSet.playTogether(containerSizeAmin, backgroundAlphaAnim);
        animatorSet.start();
    }

    private void showPressedImage() {
        imageView.setAlpha(1f);
    }

    private void showInactiveImage() {
        imageView.setAlpha(0.5f);
    }

    private void showTextAnim() {
        textView.setVisibility(VISIBLE);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f);
        ObjectAnimator o = ObjectAnimator.ofPropertyValuesHolder(textView, pvhX, alpha).setDuration(40);
        o.start();
    }

    private void hideTextAnim() {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -50f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f);
        ObjectAnimator o = ObjectAnimator.ofPropertyValuesHolder(textView, pvhX, alpha).setDuration(60);
        o.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                textView.setVisibility(GONE);
            }
        });
        o.start();
    }

    public void makeDisabled() {
        navItemState = NavItemState.INACTIVE;
        disableAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(image.getWidth());
        int height = MeasureSpec.getSize(image.getHeight());

        measureChild(imageView, width, height);
        measureChild(textView, widthMeasureSpec, heightMeasureSpec);

        widthWithText = textView.getMeasuredWidth();

        this.viewWidth = (int) (width + currentwidth + DEFAULT_IMAGE_LEFT_PADDING + DEFAULT_IMAGE_RIGHT_PADDING);
        this.viewHeight = height + DEFAULT_IMAGE_TOP_PADDING + DEFAULT_IMAGE_BOTTOM_PADDING;

        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int right, bottom;
        right = imageView.getMeasuredWidth();
        bottom = imageView.getMeasuredHeight();

        imageView.layout(DEFAULT_IMAGE_LEFT_PADDING,
                DEFAULT_IMAGE_TOP_PADDING,
                right + DEFAULT_IMAGE_RIGHT_PADDING,
                bottom + DEFAULT_IMAGE_BOTTOM_PADDING);

        textView.layout(DEFAULT_IMAGE_LEFT_PADDING + imageView.getMeasuredWidth() + DEFAULT_PADDING_BETWEEN_IMAGEANDTEXT,
                (int) ((viewHeight / 2) - (textView.getMeasuredHeight() / 2)),
                DEFAULT_IMAGE_LEFT_PADDING + imageView.getMeasuredWidth() + DEFAULT_PADDING_BETWEEN_IMAGEANDTEXT + textView.getMeasuredWidth(),
                (int) ((viewHeight / 2) + (textView.getMeasuredHeight() / 2)));

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.drawRoundRect(backgroundRect, 15, 15, backgroundRectPaint);
        // call block() here if you want to draw behind children
        super.dispatchDraw(canvas);
        // call block() here if you want to draw over children
    }

    public enum NavItemState {
        ACTIVE, INACTIVE
    }

    private float getFontHeight(Paint paint) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }
}
