package com.citywork.utils;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;

public class UIUtils {
    public static int spToPx(Resources resources, float px) {
        return (int) (px * resources.getDisplayMetrics().scaledDensity);
    }

    public static int dpToPx(Resources resources, float dp) {
        return (int) (dp * resources.getDisplayMetrics().density);
    }

    public static float getFontHeight(Paint paint) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }
}
