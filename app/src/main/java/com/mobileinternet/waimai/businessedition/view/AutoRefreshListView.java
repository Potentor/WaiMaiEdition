package com.mobileinternet.waimai.businessedition.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Status;

/**
 * Created by 海鸥2012 on 2015/7/23.
 */
public class AutoRefreshListView extends ListView implements AbsListView.OnScrollListener{


    private View footView;
    private TextView foot_tv;
    private View foot_progress;
    private OnGetToButtomListener mOnGetToButtomListener;
    private boolean ifAutoRefresh=true;
    private boolean hasFootView=true;




    public AutoRefreshListView(Context context) {
        super(context);
        init();
    }

    public AutoRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init(){
        this.setOnScrollListener(this);
    }


    /**
     * 是否自动滚动加载
     * @param cmd
     */
    public void setIfAutoScroll(boolean cmd){
        this.ifAutoRefresh=cmd;
//        if (!cmd&&footView!=null)
//        {
//            //footView.setVisibility(View.GONE);
//            this.removeFooterView(footView);
//        }


    }


    public void removeFootView(){
        if (this.footView!=null){
            this.removeFooterView(footView);
        }
        hasFootView=false;
    }


    /**
     * 设置滑动到底部时的监听器
     * @param onGetToButtomListener
     */
    public void setOnGetToButtomListener(OnGetToButtomListener onGetToButtomListener){
        this.mOnGetToButtomListener=onGetToButtomListener;
    }


    /**
     * footView根据网络连接状态进行相应改变
     */
    private void footViewActBycheckNetStatus(){
        if (Status.isIsConnectNet()){
            foot_progress.setVisibility(View.VISIBLE);
            foot_tv.setText("正在加载...");

            if (mOnGetToButtomListener!=null){
                mOnGetToButtomListener.onButtomAndNetOk();
            }

        }else{
            foot_progress.setVisibility(View.GONE);
            foot_tv.setText("无法连接到网络，请检查网络配置");
        }
    }


    public void setStateNoMoreData(){
        foot_progress.setVisibility(View.GONE);
        foot_tv.setText("点击加载");

    }

    public void setStateLoadMore(){
        foot_progress.setVisibility(View.GONE);
        foot_tv.setText("点击加载");
    }


    /**
     * 初始化footView
     */
    private void  initFootView(){

        if (hasFootView) {

            footView = LayoutInflater.from(this.getContext()).inflate(R.layout.view_foot_view, null);
            foot_progress = footView.findViewById(R.id.foot_progress);
            foot_tv = (TextView) footView.findViewById(R.id.foot_tv);

            this.addFooterView(footView);


            footView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    footViewActBycheckNetStatus();
                }
            });
        }
//        if (!ifAutoRefresh){
//            this.removeFooterView(footView);
//        }

    }



    @Override
    public void setAdapter(ListAdapter adapter) {

        initFootView();
        super.setAdapter(adapter);

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

        if(!ifAutoRefresh){
            return;
        }

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



    }




    /**
     * 滑动到底部
     */
    public interface OnGetToButtomListener{

         void onButtomAndNetOk();

    }


}
