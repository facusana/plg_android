package com.perlagloria.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perlagloria.R;

public class FontManager {
    private static final String TAG = FontManager.class.getSimpleName();

    // singleton instance
    private static FontManager mInstance;

    private Typeface helveticaNeueBold;
    private Typeface helveticaNeueMedium;
    private Typeface helveticaNeueLight;

    protected FontManager() {
        // enforce singleton
        super();
    }

    public static synchronized FontManager getInstance() {
        if (mInstance == null) {
            mInstance = new FontManager();
        }
        return mInstance;
    }

    public synchronized Typeface getFont(Fonts fontType, Context context) {
        switch (fontType) {
            case HELVETICA_NEUE_BOLD:
                if (helveticaNeueBold == null) {
                    helveticaNeueBold = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.helvetica_neue_bold));
                }
                return helveticaNeueBold;
            case HELVETICA_NEUE_MEDIUM:
                if (helveticaNeueMedium == null) {
                    helveticaNeueMedium = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.helvetica_neue_medium));
                }
                return helveticaNeueMedium;
            case HELVETICA_NEUE_LIGHT:
                if (helveticaNeueLight == null) {
                    helveticaNeueLight = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.helvetica_neue_light));
                }
                return helveticaNeueLight;
            default:
                return null;
        }
    }

    public void replaceFonts(ViewGroup viewTree, Typeface typeface) {
        View child;
        for (int i = 0; i < viewTree.getChildCount(); ++i) {
            child = viewTree.getChildAt(i);
            if (child instanceof ViewGroup) {
                replaceFonts((ViewGroup) child, typeface);
            } else if (child instanceof TextView) {
                ((TextView) child).setTypeface(typeface);
            }
        }
    }

    public enum Fonts {
        HELVETICA_NEUE_BOLD,
        HELVETICA_NEUE_MEDIUM,
        HELVETICA_NEUE_LIGHT
    }
}