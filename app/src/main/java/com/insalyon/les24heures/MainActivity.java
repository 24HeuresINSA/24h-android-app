package com.insalyon.les24heures;

import android.os.Bundle;


public class MainActivity extends BaseDynamicDataActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();




    @Override
    //dans NavigationActivity sauf startRightOutput  
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }




}

