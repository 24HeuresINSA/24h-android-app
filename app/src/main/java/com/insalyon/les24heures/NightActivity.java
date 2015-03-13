package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.insalyon.les24heures.fragments.ArtistFragment;

/**
 * Created by remi on 12/03/15.
 */
public class NightActivity extends BaseDynamicDataActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.night_activity);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        startFragment();

    }




    private void startFragment() {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", selectedCategories);
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);
        Fragment fragment = new ArtistFragment();
        fragment.setArguments(bundleArgs);

        FragmentTransaction ft = fragmentManager.beginTransaction();

         bundleArgs.putParcelableArrayList("resourcesList", nightResourceArrayList);


        ft.replace(R.id.content_frame, fragment).commit();
    }
}
