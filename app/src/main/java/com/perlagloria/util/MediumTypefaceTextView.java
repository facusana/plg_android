package com.perlagloria.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MediumTypefaceTextView extends TextView{
    public MediumTypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MediumTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediumTypefaceTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "/fonts/HelveticaNeue_Medium.ttf");
        setTypeface(tf);
    }
}

