package com.insalyon.les24heures.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.insalyon.les24heures.BaseDynamicDataActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.model.Schedule;

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
    @InjectView(R.id.schedule_item_patient)
    TextView scheduleItemPatient;

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

            NightResource nightResource = (NightResource) resource;
            urlFacebook = (nightResource.getFacebookUrl());
            urlWeb = (nightResource.getSiteUrl());
            urlTwitter = (nightResource.getTwitterUrl());
            slidingHeader.setBackground(this.getResources().getDrawable(R.color.primary_night));
            if(nightResource.getSchedules() != null
                    && !nightResource.getSchedules().isEmpty()
                    && nightResource.getSchedules().get(0).getStart() != null
                    && nightResource.getSchedules().get(0).getEnd() != null){
                Schedule schedule = nightResource.getSchedules().get(0);
                scheduleItemPatient.setText((schedule.getPrintableDay() + "  " +
                        schedule.getStart().getHours() + "h-" + schedule.getEnd().getHours() + "h").toUpperCase());
            }

            //TODO quickfix
            Schedule schedule;
            if(resource.getClass().isAssignableFrom(NightResource.class)){
                if(resource.getSchedules().isEmpty())
                    scheduleItemPatient.setText(getResources().getString(R.string.text_schedule_night_patient));
                else {
                    schedule = resource.getSchedules().get(0);
                    if (schedule.getStart() != null && schedule.getEnd() != null)
                        scheduleItemPatient.setText((schedule.getPrintableDay() + "  " +
                                schedule.getStart().getHours() + "h-" + schedule.getEnd().getHours() + "h").toUpperCase());
                    else
                        scheduleItemPatient.setText(getResources().getString(R.string.text_schedule_night_patient));
                }
            }



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
