package com.insalyon.les24heures.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by remi on 15/01/15.
 */
public class TranslationLinearLayout extends FrameLayout {

    public TranslationLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TranslationLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction() {
            return getX() / getWidth(); // TODO: guard divide-by-zero
        }

        public void setXFraction(float xFraction) {
            // TODO: cache width
            final int width = getWidth();
            setX((width > 0) ? (xFraction * width) : -9999);
        }


}
