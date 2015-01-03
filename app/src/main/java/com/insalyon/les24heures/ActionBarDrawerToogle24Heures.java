package com.insalyon.les24heures;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by remi on 03/01/15.
 */
public class ActionBarDrawerToogle24Heures extends ActionBarDrawerToggle {

    Activity activity;

    public ActionBarDrawerToogle24Heures(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.activity = activity;
    }


    @Override
    public void setToolbarNavigationClickListener(View.OnClickListener onToolbarNavigationClickListener) {
        super.setToolbarNavigationClickListener(onToolbarNavigationClickListener);
    }

    public ActionBarDrawerToogle24Heures(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    @Override
    public void syncState() {
        super.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setHomeAsUpIndicator(Drawable indicator) {
        super.setHomeAsUpIndicator(indicator);
    }

    @Override
    public void setHomeAsUpIndicator(int resId) {
        super.setHomeAsUpIndicator(resId);
    }

    @Override
    public boolean isDrawerIndicatorEnabled() {
        return super.isDrawerIndicatorEnabled();
    }

    @Override
    public void setDrawerIndicatorEnabled(boolean enable) {
        super.setDrawerIndicatorEnabled(enable);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);


    }

    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public View.OnClickListener getToolbarNavigationClickListener() {
        return super.getToolbarNavigationClickListener();
    }


}
