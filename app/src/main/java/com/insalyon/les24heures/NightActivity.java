package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import butterknife.OnClick;

/**
 * Created by remi on 12/03/15.
 */
public class NightActivity extends BaseDynamicDataActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.night_activity);
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

        clearDrawerChoices();
        artistButton.setActivated(true);
        setSelectedMenuItem(artistButton, R.drawable.concert_bleu);
        drawerLayout.closeDrawer();
        startFragment();
        restoreTitle();
    }

    @OnClick(R.id.navigation_drawer_tickets)
    public void onClickTickets(View v) {
        super.onClickTickets(v);
        nextActivity = null;
        startFragment();
    }

    @OnClick(R.id.navigation_drawer_artists)
    public void onClickArtist(View v) {
        super.onClickArtist(v);
        nextActivity = null;
        startFragment();
    }

    void startFragment() {
        startFragment(nextStaticFragment);
    }


    public void startFragment(Class fragmentClssName) {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", selectedCategories);
        bundleArgs.putParcelableArrayList("resourcesList", nightResourceArrayList);
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);

        Fragment fragment = null;
        try {
            Constructor<?> ctor = fragmentClssName.getConstructor();
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

        fragment.setArguments(bundleArgs);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void restoreTitle() {
        String str = (getResources().getString(R.string.artist_fragment_appname));

        if (str != getActionBar().getTitle()) {
            setTitle(str);
        }
    }




}
