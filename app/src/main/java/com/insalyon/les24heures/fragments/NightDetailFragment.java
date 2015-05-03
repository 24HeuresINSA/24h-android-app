package com.insalyon.les24heures.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.NightResource;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by remi on 23/04/15.
 */
public class NightDetailFragment extends DetailFragment {
    @InjectView(R.id.detail_url)
    View detailUrlContainer;
    @InjectView(R.id.detail_url_facebook)
    Button btnFacebook;
    @InjectView(R.id.detail_url_twitter)
    Button btnTwitter;
    @InjectView(R.id.detail_url_web)
    Button btnWeb;

    String urlFacebook;
    String urlTwitter;
    String urlWeb;


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

            urlFacebook = (((NightResource) resource).getFacebookUrl());
            urlWeb = (((NightResource) resource).getSiteUrl());
            urlTwitter = (((NightResource) resource).getTwitterUrl());
            slidingHeader.setBackground(this.getResources().getDrawable(R.color.primary_night));


            return true;
        }
        return false;
    }

    @OnClick(R.id.detail_url_facebook)
    public void onClickFacebook(View v){
        String url = urlFacebook;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.detail_url_twitter)
    public void onClickTwitter(View v){
        String url = urlTwitter;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.detail_url_web)
    public void onClickWeb(View v){
        String url = urlWeb;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
