package com.mobileinternet.waimai.businessedition.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Status;

/**
 * Created by 海鸥2012 on 2015/7/23.
 */
public class MyListView2 extends ListView implements AbsListView.OnScrollListener{




    private MotionEvent mDownEvent;
    private int mTouchSlop;


   // private MLinearLayout mLinearLayout;


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








    public MyListView2(Context context) {
        super(context);
        //init(context);
    }

    public MyListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(context);

    }

    public MyListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init(context);


    }

    private void init(Context context) {


        setOnScrollListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);

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

        requestDisallowInterceptTouchEvent(true);

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (!isAtTop) {

            return super.onTouchEvent(event);
        }




        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_MOVE:

                if (mDownEvent != null) {
                    //获取现在的Y坐标
                    final float eventY = event.getY();
                    //计算向下滑动的距离
                    float yDiff = eventY - mDownEvent.getY();
                    //判断是否大于最小滑动距离
                    if (yDiff > mTouchSlop) {

                       requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mDownEvent != null) {
                    mDownEvent.recycle();
                    mDownEvent = null;
                }
                break;
        }


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

            isScroll = true;
            init(getContext());

        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 滑动到底部
     */
    public interface OnGetToButtomListener {

         void onButtomAndNetOk();

    }


}
