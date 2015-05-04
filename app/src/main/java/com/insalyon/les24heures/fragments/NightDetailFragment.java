package com.insalyon.les24heures.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.insalyon.les24heures.BaseDynamicDataActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.NightResource;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by remi on 23/04/15.
 */
public class NightDetailFragment extends DetailFragment {
    @InjectView(R.id.detail_url)
    View detailUrlContainer;
    @InjectView(R.id.detail_url_facebook)
    TextView urlFacebook;
    @InjectView(R.id.detail_url_twitter)
    TextView urlTwitter;
    @InjectView(R.id.detail_url_web)
    TextView urlWeb;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.night_detail_fragment, container, false);
        ButterKnife.inject(this, view);

        super.onCreateView(inflater,container,savedInstanceState);

        return view;
    }



    /**
     * Fragment is alive
     */

    @Override
    public Boolean updateHeavyData() {
        if (super.updateHeavyData()) {

            urlFacebook.setText(((NightResource) resource).getFacebookUrl());
            urlWeb.setText(((NightResource) resource).getSiteUrl());
            urlTwitter.setText(((NightResource) resource).getTwitterUrl());
            slidingHeader.setBackground(this.getResources().getDrawable(R.color.primary_night));


            return true;
        }
        return false;
    }



}
