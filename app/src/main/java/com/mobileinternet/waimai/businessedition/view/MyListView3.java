package com.mobileinternet.waimai.businessedition.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;

/**
 * Created by 海鸥2012 on 2015/7/23.
 */
public class MyListView3 extends ListView implements AbsListView.OnScrollListener{


    private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
    private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
    private static final int REFRESH_TRIGGER_DISTANCE = 120;


    private float mDistanceToTriggerSync = -1;

     private OnRefreshLinstener mListener;


    private MotionEvent mDownEvent;
    private final AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);
    private boolean mReturningToStart=false;
    private float mFromPercentage = 0;
    private float mCurrPercentage = 0;
    private int mMediumAnimationDuration;
    private int mTouchSlop;
    private boolean mRefreshing = false;

    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    private MLinearLayout mLinearLayout;


    // private int scrollMode=OnScrollListener.SCROLL_STATE_FLING;
    private View footView;
    private TextView foot_tv;
    private View foot_progress;
    private OnGetToButtomListener mOnGetToButtomListener;
    //   private GestureDetector detector;


    //   private int mTouchSlop;
    private boolean isScroll = false;
  //  private boolean ifInteruptTouchEvent = false;//是否上层截断传播
    private boolean isAtTop = false;//是否滑动到顶端

    private boolean isAutoLoadData = true;








    public MyListView3(Context context) {
        super(context);
        //init(context);
    }

    public MyListView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(context);

    }

    public MyListView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init(context);


    }

    private void init(Context context) {


        setOnScrollListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
        setWillNotDraw(false);
//        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
//        setEnabled(a.getBoolean(0, true));
//        a.recycle();
        //detector=new GestureDetector(context,this);
        // mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }


    /**
     * 设置滑动到底部时的监听器
     *
     * @param onGetToButtomListener
     */
    public void setOnGetToButtomListener(OnGetToButtomListener onGetToButtomListener) {
        this.mOnGetToButtomListener = onGetToButtomListener;
    }


    /**
     * 设置刷新适配器
     * @param mListener
     */
    public void setOnRefreshLinstener(OnRefreshLinstener mListener){
        this.mListener=mListener;
    }



    public void stopRefreshing(){
        if (mLinearLayout!=null) {
            mLinearLayout.setRefreshing(false);
        }
    }






    /**
     * footView根据网络连接状态进行相应改变
     */
    private void footViewActBycheckNetStatus() {
        if (Status.isIsConnectNet()) {
            foot_progress.setVisibility(View.VISIBLE);
            foot_tv.setText("正在加载...");

            if (mOnGetToButtomListener != null) {
                mOnGetToButtomListener.onButtomAndNetOk();
            }

        } else {
            foot_progress.setVisibility(View.GONE);
            foot_tv.setText("无法连接到网络，请检查网络配置");
            Toast.makeText(this.getContext(), "无法连接到网络，请检查网络配置", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化footView
     */
    private void initFootView() {

        footView = LayoutInflater.from(this.getContext()).inflate(R.layout.view_foot_view, null);
        foot_progress = footView.findViewById(R.id.foot_progress);
        foot_tv = (TextView) footView.findViewById(R.id.foot_tv);

        this.addFooterView(footView);

        footViewActBycheckNetStatus();

        footView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                footViewActBycheckNetStatus();
            }
        });

    }

    public void setMLinearLayout(MLinearLayout mLinearLayout){
        this.mLinearLayout=mLinearLayout;
    }


    /**
     * 关闭自动加载
     */
    public void closeAutoLoad() {
        if (footView != null) {
            this.removeFooterView(footView);
        }

        isAutoLoadData = false;
    }

    public void setStateNoMoreData(){
        foot_progress.setVisibility(View.GONE);
        foot_tv.setText("点击加载");
    }


    @Override
    public void setAdapter(ListAdapter adapter) {

        if (isAutoLoadData) {
            initFootView();
        }
        super.setAdapter(adapter);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


        if (mDistanceToTriggerSync == -1) {
            if (getParent() != null && ((View)getParent()).getHeight() > 0) {
                final DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDistanceToTriggerSync = (int) Math.min(
                        ((View) getParent()) .getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                        REFRESH_TRIGGER_DISTANCE * metrics.density);
                mDistanceToTriggerSync*=2;
            }
        }
        mReturningToStart=false;
//
//        if (this.getFirstVisiblePosition()==0){
//          if (this.getTop() == getChildAt(0).getTop()) {
//              isAtTop = true;
//              ifInteruptTouchEvent = false;
//          }
//        }
//
//        ifInteruptTouchEvent=false;
//        requestDisallowInterceptTouchEvent(true);


        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (!isAtTop) {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                        mLinearLayout.updatePositionTimeout();
                    break;
            }

            return super.onTouchEvent(event);
        }




        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurrPercentage = 0;
                mDownEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_MOVE:

                if (mDownEvent != null && !mReturningToStart) {
                    //获取现在的Y坐标
                    final float eventY = event.getY();
                    //计算向下滑动的距离
                    float yDiff = eventY - mDownEvent.getY();
                    //判断是否大于最小滑动距离
                    if (yDiff > mTouchSlop) {

                        //判断是否大于激发刷新的滑动距离
                        if (yDiff > mDistanceToTriggerSync) {

                            //判断是否正在刷新中
                            if (!mLinearLayout.isRefreshing()) {

                                //判断是否网络允许
                                if (CodeUtil.checkNetState(this.getContext())) {
                                    mLinearLayout.startRefresh();
                                    if (mListener != null) {
                                        mListener.onRefresh();
                                    }
                                }else{
                                    mLinearLayout.setTriggerPercentage(
                                            mAccelerateInterpolator.getInterpolation(
                                                    yDiff / mDistanceToTriggerSync));
                                }
                            }
                            break;
                        } else {

                            mLinearLayout.setTriggerPercentage(
                                    mAccelerateInterpolator.getInterpolation(
                                            yDiff / mDistanceToTriggerSync));
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mDownEvent != null) {
                    mDownEvent.recycle();
                    mDownEvent = null;
                    mLinearLayout.updatePositionTimeout();
                }
                break;
        }
      //  return handled;


        return super.onTouchEvent(event);

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

        if (!isAutoLoadData)
            return;

        //  scrollMode=scrollState;
        switch (scrollState) {
            // 当不滚动时
            case OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if (absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    footViewActBycheckNetStatus();
                }
                break;
        }

    }




    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {


        if (isScroll) {
            if (this.getFirstVisiblePosition() == 0) {
                View firstItem = this.getChildAt(0);
                if (firstItem==null){
                    return;
                }
                if (this.getTop() == firstItem.getTop()) {
                    isAtTop = true;
                } else {
                    isAtTop = false;
                }

            } else {
                isAtTop = false;
            }
        }


    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (mLinearLayout==null){
            setMLinearLayout((MLinearLayout) (getParent().getParent().getParent()));
            init(getContext());
            isScroll = true;
        }

        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 滑动到底部
     */
    public interface OnGetToButtomListener {

         void onButtomAndNetOk();

    }


    public interface OnRefreshLinstener {
         void onRefresh();
    }




}
