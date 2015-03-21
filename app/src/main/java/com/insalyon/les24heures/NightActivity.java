package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.insalyon.les24heures.fragments.ArtistFragment;

import butterknife.InjectView;

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

        artistButton.setActivated(true);


        startFragment();
    }


    private void startFragment() {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", selectedCategories);
        bundleArgs.putParcelableArrayList("resourcesList", nightResourceArrayList);
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);

        Fragment fragment = new ArtistFragment();
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
