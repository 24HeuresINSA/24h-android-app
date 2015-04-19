package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.insalyon.les24heures.fragments.TclFragment;

/**
 * Created by remi on 19/04/15.
 */
public class StaticDataActivity extends BaseActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.static_activity);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        tclButton.setActivated(true);

        startFragment();
        restoreTitle();
    }


    private void startFragment() {
        Fragment fragment = new TclFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void restoreTitle() {
        String str = (getResources().getString(R.string.tcl_fragment_appname));

        if (str != getActionBar().getTitle()) {
            setTitle(str);
        }
    }
}
