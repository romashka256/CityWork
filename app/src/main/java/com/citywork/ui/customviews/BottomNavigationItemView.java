package com.citywork.ui.customviews;

import android.animation.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private final int DEFAULT_PADDING_BETWEEN_IMAGEANDTEXT = 10;
    private final int DEFAULT_PADDING_TEXT_RIGHT = 16;

    private ValueAnimator backgroundAlphaAnim;
    private ValueAnimator containerSizeAmin;
    private AnimatorSet animatorSet;

    private NavItemState navItemState;

    private float currentwidth;
    private float widthWithText;

    private ObjectAnimator objectAnimator;

    private Bitmap image;

    public NavItemState getNavItemState() {
        return navItemState;
    }

    public BottomNavigationItemView(Context context) {
        this(context, null);
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        backgroundRectPaint = new Paint();
        backgroundRectPaint.setColor(getResources().getColor(R.color.colorPrimary));
        backgroundRectPaint.setAlpha(100);

        backgroundRect = new RectF();

        textView = new TextView(context);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(15);
        textView.setText("Таймер");
        textView.setAlpha(0);
        textView.setVisibility(GONE);

        this.setBackground(getResources().getDrawable(R.drawable.tabbg));
        this.getBackground().setAlpha(0);

        image = VectorUtils.getBitmapFromVectorDrawable(context, R.drawable.ic_timer_icon_focused);

        imageView = new ImageView(context);
        imageView.setImageBitmap(image);
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

//    private void initAnimations(int alphaBackgroundStart, int alphaBackgroundEnd,
////                                int backgroundAnimDur, int containerSizeStart,
////                                int containerSizeEnd, int containerSizeAminDur) {

    private void activeAnim() {
        currentwidth = 0;
        float startPoint = imageView.getMeasuredWidth();
        final float fullwidth = widthWithText + startPoint;

        animatorSet = new AnimatorSet();

        backgroundAlphaAnim = ValueAnimator.ofInt(0, 140);
        backgroundAlphaAnim.setInterpolator(new AccelerateInterpolator());
        backgroundAlphaAnim.setDuration(100);
        final BottomNavigationItemView v = this;
        backgroundAlphaAnim.addUpdateListener(animation -> v.getBackground().setAlpha((Integer) animation.getAnimatedValue()));
        containerSizeAmin = ValueAnimator.ofFloat(0, widthWithText);
        containerSizeAmin.setDuration(100);

        Log.i("TAG", "widthWithText : " + widthWithText);
        Log.i("TAG", "startPoint : " + startPoint);
        Log.i("TAG", "fullwidth : " + fullwidth);

        containerSizeAmin.addUpdateListener(animation -> {
            Log.i("TAG", "Math.abs((Float) animation.getAnimatedValue() : " + Math.abs((Float) animation.getAnimatedValue()));
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

        Log.i("TAG", "widthWithText : " + widthWithText);
        Log.i("TAG", "startPoint : " + startPoint);
        Log.i("TAG", "fullwidth : " + fullwidth);

        containerSizeAmin.addUpdateListener(animation -> {
            Log.i("TAG", "Math.abs((Float) animation.getAnimatedValue() : " + Math.abs((Float) animation.getAnimatedValue()));
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

        setMeasuredDimension((int) (width + currentwidth + DEFAULT_IMAGE_LEFT_PADDING + DEFAULT_IMAGE_RIGHT_PADDING), height + DEFAULT_IMAGE_TOP_PADDING + DEFAULT_IMAGE_BOTTOM_PADDING);
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
                DEFAULT_IMAGE_TOP_PADDING,
                DEFAULT_IMAGE_LEFT_PADDING + imageView.getMeasuredWidth() + DEFAULT_PADDING_BETWEEN_IMAGEANDTEXT + textView.getMeasuredWidth(),
                DEFAULT_IMAGE_TOP_PADDING + textView.getMeasuredHeight());

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
}
