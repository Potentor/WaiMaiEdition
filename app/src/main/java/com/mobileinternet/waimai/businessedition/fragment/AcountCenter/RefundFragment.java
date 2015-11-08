package com.mobileinternet.waimai.businessedition.fragment.AcountCenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.adapter.RefundOrderAdapter;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.view.AutoRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class RefundFragment extends Fragment {



    protected AutoRefreshListView listView;
    protected List<OrderBaseAdapter.OrderInfo> ls_data=new ArrayList<>();
    protected RefundOrderAdapter adapter;
    private TextView tv_load_result;
    protected ProgressBar pb_load;

    private boolean hasShowOnce=false;
    private boolean hasInitView=false;
    protected boolean isFirstPage=false;



    protected abstract void freightUI(JSONObject jsonObject)throws JSONException;


    protected abstract void exceedInitView(View view);

    protected abstract void fetchData();

    protected void setReloadAvailable(){
        listView.setVisibility(View.GONE);
        tv_load_result.setVisibility(View.VISIBLE);
        pb_load.setVisibility(View.GONE);
        //tv_load_result.setText("点击刷新");
    }

    protected void setReloadGone(){
        listView.setVisibility(View.VISIBLE);
        tv_load_result.setVisibility(View.GONE);
        pb_load.setVisibility(View.GONE);
    }

    protected void setRefreshing(){
        listView.setVisibility(View.GONE);
        tv_load_result.setVisibility(View.GONE);
        pb_load.setVisibility(View.VISIBLE);
    }


    private void initView(View view){

        tv_load_result=(TextView)view.findViewById(R.id.msg_tv_load_result);
        pb_load=(ProgressBar)view.findViewById(R.id.msg_pb_load);
        listView=(AutoRefreshListView)view.findViewById(R.id.msg_listview);
        adapter=new RefundOrderAdapter(this.getActivity(),ls_data);




//        listView.setOnGetToButtomListener(new AutoRefreshListView.OnGetToButtomListener() {
//            @Override
//            public void onButtomAndNetOk() {
//                fetchData();
//            }
//        });

        tv_load_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickToLoad();
            }
        });

        listView.setAdapter(adapter);
        listView.setIfAutoScroll(false);
        listView.removeFootView();


        exceedInitView(view);



        //OnCreateView方法已经执行，所有的view引用已经拿到
        hasInitView=true;
        if (isFirstPage){
            if (Status.isIsConnectNet()){
                setRefreshing();
                fetchData();
            }else{
                setReloadAvailable();
            }
            hasShowOnce = true;

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!hasShowOnce&&isVisibleToUser) {
            if (hasInitView) {
                if (Status.isIsConnectNet()) {
                    setRefreshing();
                    hasShowOnce = true;
                    fetchData();
                }else{
                    setReloadAvailable();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_base, container, false);
        initView(view);
        return view;
    }


}
