package com.insalyon.les24heures.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.Resource;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by remi on 04/02/15.
 */
public class DetailSlidingUpPanelLayout extends SlidingUpPanelLayout{
    private static final String TAG = DetailSlidingUpPanelLayout.class.getCanonicalName();



    DetailScrollView detailScrollView;
    View nextScheduleView;
    View favoriteView;
    TextView detailSlidingTitle;
    TextView detailSlidingDescription;

    View paralaxHeader;
    private View mainContent;
    private View mainContentFake;

    public DetailSlidingUpPanelLayout(Context context) {
        super(context);
    }

    public DetailSlidingUpPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailSlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    View contentFrame;

    public void setUpSlidingDetail(final Activity activity) {
        findDetailView(activity);

        //TODO recuperer valeur depuis les dims
        final float anchored = 0.7f;
        final int scrollingHeaderHeight = 200; //header scrolling

        //TODO get from real
        final int wideHeight =  1557;
        
        final int parallaxHeight = (int) ((wideHeight - scrollingHeaderHeight) * (1-anchored));//407; //paralax height

        this.setAnchorPoint(anchored);
        this.hidePanel();  //2.0.4 sera remplacé par mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN); à la prochaine release


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) paralaxHeader.getLayoutParams();
        params.height = parallaxHeight;
        paralaxHeader.setLayoutParams(params);
        paralaxHeader.setTranslationY(wideHeight-parallaxHeight);


//        this.setDragView(wholeSlidingLayout); //default but to be clear
        detailScrollView.setIsScrollEnable(false);
        this.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                float newParallaxHeaderPos;

                float parallaxContentFrame =   (wideHeight-scrollingHeaderHeight)*slideOffset/2.25f;

                if(slideOffset < anchored) { //parallax
                    newParallaxHeaderPos = (wideHeight - scrollingHeaderHeight) * (1 - slideOffset / (anchored));
                    newParallaxHeaderPos = newParallaxHeaderPos + parallaxContentFrame;
                    paralaxHeader.setTranslationY(newParallaxHeaderPos);
                }
//                else{ //normal TODO
//                    newParallaxHeaderPos = parallaxHeight*slideOffset/(1-anchored);
////                  newParallaxHeaderPos = (height * (1 - slideOffset));
//                    newParallaxHeaderPos = newParallaxHeaderPos + parallaxContentFrame;
//                }

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

    private void findDetailView(Activity activity) {
        detailScrollView = (DetailScrollView) activity.findViewById(R.id.detail_scrollView);
        nextScheduleView =  activity.findViewById(R.id.detail_next_schedule);
        favoriteView = activity.findViewById(R.id.detail_favorites);
        detailSlidingTitle = (TextView) activity.findViewById(R.id.detail_sliding_title);
        detailSlidingDescription = (TextView) activity.findViewById(R.id.detail_desciption_text);
        paralaxHeader = activity.findViewById(R.id.detail_paralax_header);
    }

    public void showDetailPannel(Resource resource){
        //TODO mettre à jour le pannel
        detailSlidingTitle.setText(resource.getTitle());
        detailSlidingDescription.setText(resource.getDescription());
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

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        //ce sont les dimensions en pixels sans la action bar
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        Log.d("onMeasure",width+" "+height);


    }



}
