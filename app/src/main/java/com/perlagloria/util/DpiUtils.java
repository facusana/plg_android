package com.perlagloria.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DpiUtils {
    public static int convertDipToPixels(Context context, float dips) {
        Resources r = context.getApplicationContext().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.getDisplayMetrics());
    }
}
