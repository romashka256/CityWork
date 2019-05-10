package com.producticity.utils.commonutils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Typeface;

import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

public class UIUtils {

    private AssetManager assetManager;
    private Context context;

    @Getter
    private Typeface light;
    @Getter
    private Typeface regular;
    @Getter
    private Typeface bold;
    @Getter
    private Typeface medium;

    @Setter
    private Point screenSize;

    public UIUtils(Context context) {
        assetManager = context.getApplicationContext().getAssets();

        light = getFont("Roboto-Light.ttf");
        bold = getFont("Roboto-Bold.ttf");
        regular = getFont("Roboto-Regular.ttf");
        medium = getFont("Roboto-Medium.ttf");
    }

    public Typeface getFont(String name) {
        return Typeface.createFromAsset(assetManager,
                String.format(Locale.US, "fonts/%s", name));
    }


    public Point getScreenSize() {
        if (screenSize != null)
            return screenSize;
        else
            return new Point();
    }
}
