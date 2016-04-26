package com.insalyon.les24heures.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.utils.Day;
import com.insalyon.les24heures.utils.Stage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    @InjectView(R.id.detail_stage)
    TextView stageLabel;
    @InjectView(R.id.stage_detail_icon)
    ImageView stageIcon;

    String urlFacebook;
    String urlTwitter;
    String urlWeb;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.night_detail_fragment, container, false);
        ButterKnife.inject(this, view);

        super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }


    /**
     * Fragment is alive
     */

    @Override
    public Boolean notifyDataChanged(final Resource res) {
        if (super.notifyDataChanged(res)) {

            stageLabel.setText("Scene " + ((NightResource) res).getStage().toString());
            if (((NightResource) res).getStage() == Stage.BIG)
                stageIcon.setImageResource(R.drawable.ic_live);
            else
                stageIcon.setImageResource(R.drawable.ic_north);
            return true;
        }
        return false;
    }


    @Override
    public Boolean updateHeavyData() {
        if (super.updateHeavyData()) {

            NightResource nightResource = (NightResource) resource;
            urlFacebook = (nightResource.getFacebookUrl());
            urlWeb = (nightResource.getSiteUrl());
            urlTwitter = (nightResource.getTwitterUrl());
            slidingHeader.setBackground(this.getResources().getDrawable(R.color.primary_night));

            if (nightResource.getSchedules() != null
                    && !nightResource.getSchedules().isEmpty()
                    && nightResource.getSchedules().get(0).getStart() != null
                    && nightResource.getSchedules().get(0).getEnd() != null
                    && this.isItTimeToDisplaySchedule(nightResource.getSchedules())) {
                Schedule schedule = nightResource.getSchedules().get(0);
                scheduleItemPatient.setText((schedule.getPrintableDay() + "  " +
                        schedule.getStart().getHours() + "h-" + schedule.getEnd().getHours() + "h").toUpperCase());
            } else {
                scheduleItemPatient.setText(getResources().getString(R.string.text_schedule_night_patient));
            }

            return true;
        }
        return false;
    }

    /**
     * only display schedule if schedule is for tonight and after DISPLAY_ARTIST_HOUR params
     * @param scheduleList
     * @return
     */
    private boolean isItTimeToDisplaySchedule(List<Schedule> scheduleList) {
        //assert that night resource have only ONE schedule;
        Schedule schedule = scheduleList.get(0);
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        if (schedule.getDay().equals(Day.FRIDAY)) {
            //get friday event from params
            Date friday = null;
            String dateInString = new java.text.SimpleDateFormat("EEEE, "+getResources().getString(R.string.FRIDAY_DATE)).format(cal.getTime());
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy");
            try {
                friday = formatter.parse(dateInString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //is friday today or already in the past ?
            if (now.after(friday)) {
                //is it time to display schedule ?
                if (now.getHours() >= getResources().getInteger(R.integer.DISPLAY_ARTIST_HOUR)) {
                    return true;
                }
            }
            return false;
        } else if (schedule.getDay().equals(Day.SATURDAY)) {
            //get saturday event from params
            Date saturday = null;
            String dateInString = new java.text.SimpleDateFormat("EEEE, "+getResources().getString(R.string.SATURDAY_DATE)).format(cal.getTime());
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy");
            try {
                saturday = formatter.parse(dateInString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //is saturday today or already in the past ?
            if (now.after(saturday)) {
                //is it time to display schedule ?
                if (now.getHours() >= getResources().getInteger(R.integer.DISPLAY_ARTIST_HOUR)) {
                    return true;
                }
            }
            return false;
        } else if (schedule.getDay().equals(Day.SUNDAY)) {
            //get sunday event from params
            Date sunday = null;
            String dateInString = new java.text.SimpleDateFormat("EEEE, "+getResources().getString(R.string.SUNDAY_DATE)).format(cal.getTime());
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy");
            try {
                sunday = formatter.parse(dateInString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //is sunday today or already in the past ?
            if (now.after(sunday)) {
                //is it time to display schedule ?
                if (now.getHours() >= getResources().getInteger(R.integer.DISPLAY_ARTIST_HOUR)) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }


    @OnClick(R.id.detail_url_facebook)
    public void onClickFacebook(View v) {
        String url = urlFacebook;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.detail_url_twitter)
    public void onClickTwitter(View v) {
        String url = urlTwitter;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.detail_url_web)
    public void onClickWeb(View v) {
        String url = urlWeb;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
