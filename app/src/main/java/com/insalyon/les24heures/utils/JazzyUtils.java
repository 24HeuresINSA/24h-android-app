package com.insalyon.les24heures.utils;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by remi on 10/02/15.
 */
public class JazzyUtils {

        public static int dpToPx(Resources res, int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
        }

}
