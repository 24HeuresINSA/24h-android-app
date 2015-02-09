package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.ScheduleAdapter;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.utils.Day;
import com.insalyon.les24heures.view.DetailScrollView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by remi on 09/02/15.
 */
public class DetailFragment extends Fragment {
    private static final String TAG = OutputMapsFragment.class.getCanonicalName();
    View view;

    private static ResourceServiceImpl resourceService = ResourceServiceImpl.getInstance();

    @InjectView(R.id.detail_scrollView)
     DetailScrollView detailScrollView;
    @InjectView(R.id.detail_next_schedule)
     TextView nextSchedule;
    @InjectView(R.id.detail_favorites)
     ImageButton favoriteImageButton;
    @InjectView(R.id.detail_sliding_title)
     TextView detailSlidingTitle;
    @InjectView(R.id.detail_desciption_text)
     TextView detailSlidingDescription;
    @InjectView(R.id.detail_schedule_grid_layout)
    GridView schedulesGrid;

    Resource resource;
    private ScheduleAdapter scheduleAdapter;
    private ArrayList<Schedule> schedules;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        schedules = new ArrayList<>();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.sliding_layout_content,container,false);
        ButterKnife.inject(this, view);

        scheduleAdapter = new ScheduleAdapter(getActivity().getApplicationContext(),
                R.layout.schedule_grid_item,schedules);
        schedulesGrid.setAdapter(scheduleAdapter);

        return view;
    }





    /**
     * Fragment is alive
     */

    @OnClick(R.id.detail_favorites)
    public void onClickFav(View v){
        resource.setIsFavorites(!resource.isFavorites());
        if(resource.isFavorites())
            ((ImageButton) v).setImageResource(R.drawable.ic_favorites_checked);
        else
            ((ImageButton) v).setImageResource(R.drawable.ic_favorites_unchecked);

        //TODO notify dataset changed (surtout List)

    }

    public void notifyDataChanged(Resource res){
        resource = res;

        detailSlidingTitle.setText(resource.getTitle());
        detailSlidingDescription.setText(resource.getDescription());

        Schedule schedule = resourceService.getNextSchedule(resource);
        nextSchedule.setText(schedule.getPrintableDay()+"\n"+
                schedule.getStart().getHours()+"h-"+schedule.getEnd().getHours()+"h");

        if(resource.isFavorites())
            favoriteImageButton.setImageResource(R.drawable.ic_favorites_checked);
        else
            favoriteImageButton.setImageResource(R.drawable.ic_favorites_unchecked);

        //TODO mettre à jour le pannel

        //mini maps

        //schedules
        schedules.clear();
        schedules.addAll(resource.getSchedules());
        scheduleAdapter.notifyDataSetChanged();


        //optionals  pictures
    }
}
