package com.insalyon.les24heures.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.Resource;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by remi on 04/02/15.
 */
public class DetailSlidingUpPanelLayout extends SlidingUpPanelLayout {
    private static final String TAG = DetailSlidingUpPanelLayout.class.getCanonicalName();



    DetailScrollView detailScrollView;
    View nextScheduleView;
    View favoriteView;
    TextView detailSlidingTitle;

    public DetailSlidingUpPanelLayout(Context context) {
        super(context);
    }

    public DetailSlidingUpPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailSlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setUpSlidingDetail(Activity activity) {
        detailScrollView = (DetailScrollView) activity.findViewById(R.id.detail_scrollView);
        nextScheduleView =  activity.findViewById(R.id.detail_next_schedule);
        favoriteView = activity.findViewById(R.id.detail_favorites);
        detailSlidingTitle = (TextView) activity.findViewById(R.id.detail_sliding_title);

        this.setAnchorPoint(0.7f);
        this.hidePanel();  //2.0.4 sera remplacé par mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN); à la prochaine release


//        this.setDragView(wholeSlidingLayout); //default but to be clear
        detailScrollView.setIsScrollEnable(false);
        this.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                if (slideOffset == 0) {
                    detailScrollView.setIsScrollEnable(true);
                }

            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                detailScrollView.setIsScrollEnable(true);
                nextScheduleView.setVisibility(View.GONE);
                favoriteView.setVisibility(View.VISIBLE);
//                this.setDragView(slidingDetailHeader);
                //TODO set AppName = title
                //TODO delete titleLayout

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                nextScheduleView.setVisibility(View.VISIBLE);
                favoriteView.setVisibility(View.GONE);
//                this.setDragView(wholeSlidingLayout);
                detailScrollView.setIsScrollEnable(false);
                detailScrollView.fullScroll(ScrollView.FOCUS_UP);


            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
                nextScheduleView.setVisibility(View.GONE);
                favoriteView.setVisibility(View.VISIBLE);
                detailScrollView.setIsScrollEnable(false);

            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

    }

    public void showDetailPannel(Resource resource){
        //TODO mettre à jour le pannel
        detailSlidingTitle.setText(resource.getTitle());
        this.showPanel();

    }

    public Boolean hideDetailPannel(){
        if(!this.isPanelHidden()) {
            this.hidePanel();
            return true;
        }else{
            return false;
        }

    }



}
