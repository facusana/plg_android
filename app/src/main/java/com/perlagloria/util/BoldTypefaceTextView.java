package com.perlagloria.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class BoldTypefaceTextView extends TextView{
    public BoldTypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BoldTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldTypefaceTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "/fonts/HelveticaNeue_Bold.ttf");
        setTypeface(tf);
    }
}
