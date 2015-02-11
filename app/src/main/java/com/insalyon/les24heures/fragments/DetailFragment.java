package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.insalyon.les24heures.JazzyViewPager.JazzyViewPager;
import com.insalyon.les24heures.JazzyViewPager.OutlineContainer;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.ScheduleAdapter;
import com.insalyon.les24heures.eventbus.ResourceSelectedEvent;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.view.DetailScrollView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by remi on 09/02/15.
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback {
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
    @InjectView(R.id.detail_sliding_layout_header)
    View slidingHeader;

    Resource resource;
    private ScheduleAdapter scheduleAdapter;
    private ArrayList<Schedule> schedules;
    private GoogleMap googleMap;

    private Boolean heavyDataUpdated = false;
    private EventBus eventBus;

    private JazzyViewPager mJazzy;
    private android.content.Context appContext;
    private MainAdapter picturePagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();


        schedules = new ArrayList<>();

        appContext = getActivity().getApplicationContext();

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

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.detail_mini_maps);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        slidingHeader.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateHeavyData();
                return false;
            }
        });

        setupJazziness(JazzyViewPager.TransitionEffect.Standard);

        return view;
    }

    private void setupJazziness(JazzyViewPager.TransitionEffect effect) {
        mJazzy = (JazzyViewPager) view.findViewById(R.id.jazzy_pager);
        mJazzy.setTransitionEffect(effect);
        picturePagerAdapter = new MainAdapter();
        mJazzy.setAdapter(picturePagerAdapter);
        mJazzy.setPageMargin(30);

    }

    private class MainAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            TextView text = new TextView(appContext);
            text.setGravity(Gravity.CENTER);
            text.setTextSize(30);
            text.setTextColor(Color.WHITE);
            text.setText("Page " + position);
            text.setPadding(30, 30, 30, 30);
            int bg = Color.rgb((int) Math.floor(Math.random()*128)+64,
                    (int) Math.floor(Math.random()*128)+64,
                    (int) Math.floor(Math.random()*128)+64);
            text.setBackgroundColor(bg);
            container.addView(text, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mJazzy.setObjectForPosition(text, position);
            return text;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView(mJazzy.findViewFromObject(position));
        }
        @Override
        public int getCount() {
            return 10;
        }
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            if (view instanceof OutlineContainer) {
                return ((OutlineContainer) view).getChildAt(0) == obj;
            } else {
                return view == obj;
            }
        }
    }





    /**
     * Fragment is alive
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        if(resource != null) {
            addMarkerAndMoveCam();
        }else{
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));
        }

        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void addMarkerAndMoveCam() {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
//                                .title(resource.getTitle() + " " + resource.getCategory().getName())
//                                .snippet(resource.getDescription())
                .position(resource.getLoc()));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(resource.getLoc(), 15));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                eventBus.postSticky(new ResourceSelectedEvent(resource));
            }
        });
    }

    @OnClick(R.id.detail_favorites)
    public void onClickFav(View v){
        resource.setIsFavorites(!resource.isFavorites());
        if(resource.isFavorites())
            ((ImageButton) v).setImageResource(R.drawable.ic_favorites_checked);
        else
            ((ImageButton) v).setImageResource(R.drawable.ic_favorites_unchecked);

        //TODO notify dataset changed (surtout List)

    }

    private void updateHeavyData(){
        if(!heavyDataUpdated) {

            //TODO mettre à jour le panel

            //mini maps
            //the map update
            addMarkerAndMoveCam();


            //schedules
            schedules.clear();
            schedules.addAll(resource.getSchedules());
            scheduleAdapter.notifyDataSetChanged();


            //optionals  pictures

            heavyDataUpdated = true;

        }
    }

    public void notifyDataChanged(Resource res) {
        resource = res;
        heavyDataUpdated = false;

        detailSlidingTitle.setText(resource.getTitle());
        detailSlidingDescription.setText(resource.getDescription());

        Schedule schedule = resourceService.getNextSchedule(resource);
        nextSchedule.setText(schedule.getPrintableDay()+"\n"+
                schedule.getStart().getHours()+"h-"+schedule.getEnd().getHours()+"h");

        if(resource.isFavorites())
            favoriteImageButton.setImageResource(R.drawable.ic_favorites_checked);
        else
            favoriteImageButton.setImageResource(R.drawable.ic_favorites_unchecked);

    }
}
