package com.mobileinternet.waimai.businessedition.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 今日订单中：已处理fragment
 */
public abstract class ModeFragment extends Fragment {



    protected ListView listView;
    protected List<OrderBaseAdapter.OrderInfo> ls_data=new LinkedList<>();
    protected BaseAdapter adapter;
    protected TextView tv_load_result;
    protected ProgressBar pb_load;
    protected boolean hasShowOnce=false;//是否已经显示了一次
    protected boolean hasInitView=false; //是否fragment已经加载完
    protected boolean isFirstPage=false;  //是否是多个fragment中的第一个显示的fragment





    protected abstract void fetchData();

    protected abstract void initAdapter();

    protected abstract void initListView(View view);

    protected abstract View inflateLayout(LayoutInflater inflater);

    protected void setReloadAvailable(){
        tv_load_result.setVisibility(View.VISIBLE);
        pb_load.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
       // tv_load_result.setText("点击刷新");
    }

    protected void setReloadGone(){
        tv_load_result.setVisibility(View.GONE);
        pb_load.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    protected void setRefreshing(){
        tv_load_result.setVisibility(View.GONE);
        pb_load.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

//    protected abstract boolean judgeIsThisPage();


    private View initView(View view){

        tv_load_result=(TextView)view.findViewById(R.id.msg_tv_load_result);
        pb_load=(ProgressBar)view.findViewById(R.id.msg_pb_load);

        //实例化ListView
        initListView(view);

        //初始化适配器
        initAdapter();

        listView.setAdapter(adapter);


        tv_load_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickToLoad();
            }
        });


        //OnCreateView方法已经执行，所有的view引用已经拿到
        hasInitView=true;
        if (isFirstPage){
            if (CodeUtil.checkNetState(this.getActivity())) {
                listView.setVisibility(View.VISIBLE);
                pb_load.setVisibility(View.VISIBLE);
                tv_load_result.setVisibility(View.GONE);
                fetchData();
            }else{
                setReloadAvailable();
            }
            hasShowOnce = true;

        }

        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        //第一次可见时
        if (!hasShowOnce&&isVisibleToUser) {

            if (hasInitView) {
                if (Status.isIsConnectNet()) {
                    listView.setVisibility(View.VISIBLE);
                    pb_load.setVisibility(View.VISIBLE);
                    tv_load_result.setVisibility(View.GONE);
                    hasShowOnce = true;
                    fetchData();
                }else{
                    setReloadAvailable();
                }
            }
        }
    }

    /**
     * 点击重试后执行的方法
     */
    private void clickToLoad(){


        setRefreshing();

        if (CodeUtil.checkNetState(this.getActivity())) {
            fetchData();
        }else{
            setReloadAvailable();
        }

    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  View view=inflater.inflate(R.layout.fragment_base_today2, container, false);

        return initView(inflateLayout(inflater));
    }


}
