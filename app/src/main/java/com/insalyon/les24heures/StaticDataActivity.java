package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.insalyon.les24heures.fragments.FacilitiesFragment;
import com.insalyon.les24heures.fragments.ParamsFragment;
import com.insalyon.les24heures.fragments.TclFragment;
import com.insalyon.les24heures.fragments.TicketsFragment;
import com.insalyon.les24heures.fragments.ConsoFragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import butterknife.OnClick;

/**
 * Created by remi on 19/04/15.
 */
public class StaticDataActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.static_activity);
        if (savedInstanceState != null)
            if(savedInstanceState.getString("nextStaticFragment")!= null){
                String nextStaticFragmentClassName = savedInstanceState.getString("nextStaticFragment");
                try {
                    Class<?> clazz = Class.forName(nextStaticFragmentClassName.toString());
                    startFragment(clazz);
                    nextStaticFragment = clazz;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }

        if (getIntent() != null && nextStaticFragment == null) {
            if (getIntent().getStringExtra("nextStaticFragment") != null) {
                String nextStaticFragmentClassName = getIntent().getStringExtra("nextStaticFragment");
                try {
                    Class<?> clazz = Class.forName(nextStaticFragmentClassName.toString());
                    startFragment(clazz);
                    nextStaticFragment = clazz;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        restoreTitle();
    }

    @OnClick(R.id.navigation_drawer_tickets)
    public void onClickTickets(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.billeterie_bleu);
        drawerLayout.closeDrawer();
        nextStaticFragment = TicketsFragment.class;
        startFragment();
    }

    @OnClick(R.id.navigation_drawer_conso)
    public void onClickConso(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.ic_beer_bleu);
        drawerLayout.closeDrawer();
        nextStaticFragment = ConsoFragment.class;
        startFragment();
    }

    @OnClick(R.id.navigation_drawer_tcl)
    public void onClickTcl(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.tcl_rouge);
        drawerLayout.closeDrawer();
        nextStaticFragment = TclFragment.class;
        startFragment();
    }

    @OnClick(R.id.navigation_drawer_facilities)
    public void onClickFacilities(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.drapeau_bleu);
        drawerLayout.closeDrawer();
        nextStaticFragment = FacilitiesFragment.class;
        startFragment();
    }

    @OnClick(R.id.navigation_drawer_params)
    public void onClickParams(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.ic_action_settings_bleu);
        drawerLayout.closeDrawer();
        nextStaticFragment = ParamsFragment.class;
        startFragment();
    }

    void startFragment() {
        startFragment(nextStaticFragment);
    }

    void startFragment(Class fragmentClassName) {
        Fragment fragment = null;
        try {
            Constructor<?> ctor = fragmentClassName.getConstructor();
            fragment = (Fragment) ctor.newInstance(new Object[]{});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void restoreTitle() {
        String str = (getResources().getString(R.string.app_name));
        if (nextStaticFragment != null) {
            if (nextStaticFragment.equals(TicketsFragment.class))
                str = (getResources().getString(R.string.tickets_fragment_appname));
            if (nextStaticFragment.equals(TclFragment.class))
                str = (getResources().getString(R.string.tcl_fragment_appname));
            if (nextStaticFragment.equals(FacilitiesFragment.class))
                str = (getResources().getString(R.string.facilities_fragment_appname));
            if (nextStaticFragment.equals(ParamsFragment.class))
                str = (getResources().getString(R.string.params_fragment_appname));
            if (nextStaticFragment.equals(ConsoFragment.class))
                str = (getResources().getString(R.string.conso_fragment_appname));
        }

        if (str != getActionBar().getTitle()) {
            setTitle(str);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nextStaticFragment",nextStaticFragment.getCanonicalName());
    }
}
