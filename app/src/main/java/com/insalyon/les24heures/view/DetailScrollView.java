package com.insalyon.les24heures.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by remi on 01/02/15.
 */
public class DetailScrollView extends ScrollView {

    private Boolean isScrollEnable = true;

    public DetailScrollView(Context context) {
        super(context);
    }

    public DetailScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Boolean getIsScrollEnable() {
        return isScrollEnable;
    }

    public void setIsScrollEnable(Boolean isScrollEnable) {
        this.isScrollEnable = isScrollEnable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScrollEnable) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }
}
