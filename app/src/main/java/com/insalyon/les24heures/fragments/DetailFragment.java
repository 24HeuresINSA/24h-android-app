package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.insalyon.les24heures.JazzyViewPager.JazzyViewPager;
import com.insalyon.les24heures.JazzyViewPager.OutlineContainer;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.ScheduleAdapter;
import com.insalyon.les24heures.eventbus.ResourceUpdatedEvent;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.impl.ScheduleServiceImpl;
import com.insalyon.les24heures.view.DetailScrollView;
import com.insalyon.les24heures.view.viewpagerindicator.CirclePageIndicator;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import de.greenrobot.event.EventBus;

/**
 * Created by remi on 09/02/15.
 */
public abstract class DetailFragment extends Fragment {
    private static final String TAG = DayMapsFragment.class.getCanonicalName();
    private static ScheduleServiceImpl scheduleService = ScheduleServiceImpl.getInstance();
    View view;
    @InjectView(R.id.detail_scrollView)
    DetailScrollView detailScrollView;
    @Optional@InjectView(R.id.detail_next_schedule)
    TextView nextSchedule;
    @InjectView(R.id.detail_favorites)
    ImageButton favoriteImageButton;
    @InjectView(R.id.detail_sliding_title)
    TextView detailSlidingTitle;
    @InjectView(R.id.detail_description_text)
    TextView detailSlidingDescription;
    @InjectView(R.id.detail_schedule_grid_layout)
    GridView schedulesGrid;
    @InjectView(R.id.detail_sliding_layout_header)
    View slidingHeader;


    @InjectView(R.id.detail_carousel_layout)
    View carouselLayout;

    Resource resource;
    ScheduleAdapter scheduleAdapter;
    ArrayList<Schedule> schedules;

    private Boolean heavyDataUpdated = false;
    EventBus eventBus;

    private JazzyViewPager mJazzy;
    private android.content.Context appContext;
    private MainAdapter picturePagerAdapter;
    private Picasso picasso;
    private ImageView parallaxImageHeader;
    private View parallaxHeader;
    private CirclePageIndicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();


        schedules = new ArrayList<>();

        appContext = getActivity().getApplicationContext();

        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("resource") != null) {
                resource = savedInstanceState.getParcelable("resource");
                schedules.addAll(resource.getSchedules());
            }
        }

        picasso = new Picasso.Builder(getActivity().getApplicationContext()).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        }).build();

//        picasso.setLoggingEnabled(true);
//        picasso.setIndicatorsEnabled(true);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);


        scheduleAdapter = new ScheduleAdapter(getActivity().getApplicationContext(),
                R.layout.schedule_grid_item, schedules);
        schedulesGrid.setAdapter(scheduleAdapter);


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




   void setupJazziness(JazzyViewPager.TransitionEffect effect) {
        mJazzy = (JazzyViewPager) view.findViewById(R.id.jazzy_pager);
        mJazzy.setTransitionEffect(effect);
        picturePagerAdapter = new MainAdapter();
        mJazzy.setAdapter(picturePagerAdapter);
        mJazzy.setPageMargin(30);
        mIndicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
        mIndicator.setViewPager(mJazzy);

    }

    /**
     * Fragment is alive
     */


    @OnClick(R.id.detail_favorites)
    public void onClickFav(View v) {
        resource.setIsFavorites(!resource.isFavorites());
        if (resource.isFavorites()) {
            ((ImageButton) v).setImageResource(R.drawable.ic_action_favorite);
        }
        else {
            ((ImageButton) v).setImageResource(R.drawable.ic_action_favorite_uncheck);
        }

        eventBus.post(new ResourceUpdatedEvent());

    }

    public Boolean updateHeavyData() {
        if (!heavyDataUpdated) {

            //schedules
            schedules.clear();
            schedules.addAll(resource.getSchedules());
            scheduleAdapter.notifyDataSetChanged();

            //optionals  pictures
            if(resource.getPictures().size() == 0){
                carouselLayout.setVisibility(View.INVISIBLE);
            } else {
                mJazzy.setAdapter(picturePagerAdapter);
                carouselLayout.setVisibility(View.VISIBLE);
            }

            heavyDataUpdated = true;
            return true;
        }
        return false;
    }

    public void notifyDataChanged(final Resource res) {
        if (res != null)
            resource = res;
        if(resource == null)
            return;
        heavyDataUpdated = false;

        detailSlidingTitle.setText(resource.getTitle());
        detailSlidingTitle.setSelected(true);
        detailSlidingDescription.setText(resource.getDescription());

        Schedule schedule = scheduleService.getNextSchedule(resource);
        if(schedule != null)
        nextSchedule.setText((schedule.getPrintableDay() + "  " +
                schedule.getStart().getHours() + "h-" + schedule.getEnd().getHours() + "h").toUpperCase());
        else
            nextSchedule.setText(getResources().getString(R.string.no_more_schedule));

        if (resource.isFavorites()) {
            favoriteImageButton.setImageResource(R.drawable.ic_action_favorite);
        }
        else {
            favoriteImageButton.setImageResource(R.drawable.ic_action_favorite_uncheck);
        }


        if(resource.getMainPictureUrl() == null || resource.getMainPictureUrl() == ""){
            parallaxHeader.setVisibility(View.GONE);
            parallaxHeader.setSelected(true); //to prevent the slidingUp to display it
        }else {
            parallaxHeader.setVisibility(View.VISIBLE);
            parallaxHeader.setSelected(false); //to allow slidingUp to do its job
            try {
                picasso.load(URLDecoder.decode(resource.getMainPictureUrl()))
                        .placeholder(R.drawable.ic_waiting)
                        .error(R.drawable.ic_error)
                        .into(parallaxImageHeader);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }


    }

    /**
     * Fragment is no more alive
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("resource", resource);
    }

    public void setParallaxImageHeader(ImageView parallaxImageHeader) {
        this.parallaxImageHeader = parallaxImageHeader;
    }


    public void setParallaxHeader(View parallaxHeader) {
        this.parallaxHeader = parallaxHeader;
    }



    private class MainAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView image = new ImageView(appContext);

            try {
                picasso.load(URLDecoder.decode(resource.getPictures().get(position)))
                        .placeholder(R.drawable.ic_waiting)
                        .error(R.drawable.ic_error)
                        .into(image);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            container.addView(image, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);



            mJazzy.setObjectForPosition(image, position);
            return image;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView(mJazzy.findViewFromObject(position));
        }

        @Override
        public int getCount() {
            if(resource == null) return 0;
            return resource.getPictures().size();
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
}
