package com.mobileinternet.waimai.businessedition.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by 海鸥2012 on 2015/7/30.
 */
public class MLinearLayout extends LinearLayout {

    private static final long RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 300;
    private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final float PROGRESS_BAR_HEIGHT = 4;
//    private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
//    private static final int REFRESH_TRIGGER_DISTANCE = 120;
//    private float mDistanceToTriggerSync = -1;
    // private OnRefreshListener mListener;


    private SwipeProgressBar mProgressBar; //the thing that shows progress is going
 //   private MotionEvent mDownEvent;
    private int mProgressBarHeight;
    private final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
  //  private final AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);
    private boolean mReturningToStart=false;
    private float mFromPercentage = 0;
    private float mCurrPercentage = 0;
    private int mMediumAnimationDuration;
    private int mTouchSlop;
    private boolean mRefreshing = false;

    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    private Animation mShrinkTrigger = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float percent = mFromPercentage + ((0 - mFromPercentage) * interpolatedTime);
            mProgressBar.setTriggerPercentage(percent);
        }
    };


    private final Animation.AnimationListener mShrinkAnimationListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            mCurrPercentage = 0;
        }
    };


    // Cancel the refresh gesture and animate everything back to its original state.
    private final Runnable mCancel = new Runnable() {

        @Override
        public void run() {
            mReturningToStart = true;
            // Timeout fired since the user last moved their finger; animate the
            // trigger to 0 and put the target back at its original position
            if (mProgressBar != null) {
                mFromPercentage = mCurrPercentage;
                mShrinkTrigger.setDuration(mMediumAnimationDuration);
                mShrinkTrigger.setAnimationListener(mShrinkAnimationListener);
                mShrinkTrigger.reset();
                mShrinkTrigger.setInterpolator(mDecelerateInterpolator);
                startAnimation(mShrinkTrigger);
            }
        }

    };




    public MLinearLayout(Context context) {
        super(context);
    }

    public MLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
        setWillNotDraw(false);
        mProgressBar = new SwipeProgressBar(this);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mProgressBarHeight = (int) (metrics.density * PROGRESS_BAR_HEIGHT);
        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
      //  Log.i("tag","linearCreate");




    }

    public MLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }






    public void setTriggerPercentage(float percent) {
        if (percent == 0f) {
            // No-op. A null trigger means it's uninitialized, and setting it to zero-percent
            // means we're trying to reset state, so there's nothing to reset in this case.
            mCurrPercentage = 0;
            return;
        }
        mCurrPercentage = percent;
        mProgressBar.setTriggerPercentage(percent);
    }


    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            //ensureTarget();
            mCurrPercentage = 0;
            mRefreshing = refreshing;
            if (mRefreshing) {
                mProgressBar.start();
            } else {
                mProgressBar.stop();
            }
        }
    }


    public void startRefresh() {
        removeCallbacks(mCancel);
        setRefreshing(true);
        // mListener.onRefresh();
    }


    public boolean isRefreshing(){
        return mProgressBar.isRunning();
    }


    public void updatePositionTimeout() {
        removeCallbacks(mCancel);
        postDelayed(mCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mProgressBar.draw(canvas);
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {



    }


    /**
     * Simple AnimationListener to avoid having to implement unneeded methods in
     * AnimationListeners.
     */
    private class BaseAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mProgressBar.setBounds(0, 0, getWidth(),mProgressBarHeight);
        mProgressBar.setColorScheme(Color.BLUE,Color.RED, Color.YELLOW, Color.GREEN);
       // Log.i("tag","layoutLinear");

    }


}
