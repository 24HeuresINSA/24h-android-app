package com.insalyon.les24heures.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SearchView;

/**
 * Created by remi on 17/01/15.
 */
public class CustomSearchView extends SearchView {
    public CustomSearchView(Context context) {
        super(context);
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onActionViewExpanded() {
        super.onActionViewExpanded();
        Log.d("pouet","expand");
    }

    @Override
    public void onActionViewCollapsed() {
        super.onActionViewCollapsed();
        Log.d("pouet","collapse");

    }


}
