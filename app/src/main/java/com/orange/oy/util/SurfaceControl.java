package com.orange.oy.util;

import android.graphics.Bitmap;
import android.view.View;

public class SurfaceControl {

    public static Bitmap screenshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }
}