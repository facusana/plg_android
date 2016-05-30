package com.perlagloria.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class LightTypefaceTextView extends TextView {
    public LightTypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LightTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LightTypefaceTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "/fonts/HelveticaNeue_Light.ttf");
        setTypeface(tf);
    }
}
