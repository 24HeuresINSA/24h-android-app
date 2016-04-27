package com.insadelyon.les24heures.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by remi on 11/02/15.
 */
public class CustomDrawerLayout extends DrawerLayout {
    private static final String TAG = CustomDrawerLayout.class.getCanonicalName();


    private View drawerView;

    //isDrawerOpen only switches when the drawer is fully opened
    private Boolean isDrawerOpen = false;


    public CustomDrawerLayout(Context context) {
        super(context);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

     public CustomDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void disabledDrawerSwipe() {
        this.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void enabledDrawerSwipe() {
        this.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public boolean isDrawerOpen() {
        return isDrawerOpen;
    }

    public void openDrawer() {
        this.openDrawer(drawerView);
    }

    public void closeDrawer() {
        this.closeDrawer(drawerView);
    }

    public Boolean isDrawerVisible() {
        return this.isDrawerVisible(drawerView);
    }


    public void toggleDrawer() {
        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            openDrawer();
        }

    }

    public void setDrawerView(View drawerView) {
        this.drawerView = drawerView;
    }


    public void setIsDrawerOpen(Boolean isDrawerOpen) {
        this.isDrawerOpen = isDrawerOpen;
    }


}
