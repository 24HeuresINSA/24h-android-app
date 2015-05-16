package com.insalyon.les24heures;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewSwitcher;

/**
 * Created by remi on 16/05/15.
 */
public class HiddenActivity extends Activity {
    String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hidden);
    }

    public void clickHiddenCase(View v) {
        str += (String) v.getTag();

        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.qawsed);

        if (str.contains("1")) {
            str = "BITE";
            switcher.showNext();

        } else if (str.contains("BITE")) {
            str = "";
            switcher.showPrevious();
        }
    }

}
