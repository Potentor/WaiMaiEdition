package com.mobileinternet.waimai.businessedition.fragment.TodayOrder;

import android.os.Handler;
import android.os.Message;

import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.adapter.TodayOrderAdapter;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 海鸥2012 on 2015/7/31.
 */
public class HandledFragment extends BaseFragment implements
        OrderBaseAdapter.OnNumalListner{


    public static Handler mHanlder;



    public static HandledFragment newInstance( ) {
        HandledFragment fragment = new HandledFragment();
        return fragment;
    }

    @Override
    public void expandGenData(OrderBaseAdapter.OrderInfo info, JSONObject jsonObject) throws JSONException {


        info.isValid=jsonObject.getBoolean("isvalid");
        info.isPayOnline = jsonObject.getBoolean("isPayOln");

        if (!info.isValid) {
            info.cause=jsonObject.getString("cause");


            if (info.isPayOnline){
                info.refundStatus=jsonObject.getInt("status");
            }

        }else{
            info.status=jsonObject.getInt("category");
            switch (info.status)
            {
                case 4:
                    info.out_time= DateUtil.getHourAndMinute(jsonObject.getString("sendtime"));
                    break;
            }


        }

    }

    @Override
    protected void genUrl() {
        url=Share.url_today_order;
    }

    @Override
    protected void initAdapter() {
        adapter=new TodayOrderAdapter(this.getActivity(),ls_data);
        ((TodayOrderAdapter)adapter).setOnNumalListener(this);

        HandledFragment.mHanlder=new Handler(){
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);

                //刷新命令
                if (msg.what==200){
                    fetchData();
                    return;
                }

                //如果是其他订单界面处理后，传过来的订单数据
                if (msg.what==201){
                    setReloadGone();
                    ls_data.add(0, (OrderBaseAdapter.OrderInfo) msg.obj);
                    adapter.notifyDataSetChanged();
                    return;
                }


                if (msg.what==300){

                    ls_searchData.clear();
                    isSearch=true;
                    ((TodayOrderAdapter)adapter).changeData(ls_searchData);
                    String content=(String)msg.obj;

                    if ("close".equals(content)){

                        isSearch=false;
                        ((TodayOrderAdapter)adapter).changeData(ls_data);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    int size=ls_data.size();

                    for (int j=0;j<size;j++){
                        OrderBaseAdapter.OrderInfo info=ls_data.get(j);
                        if(info.phone.contains(content)){
                            ls_searchData.add(info);
                        }
                    }

                    adapter.notifyDataSetChanged();


                }



            }
        };
    }


    @Override
    public void invalid(OrderBaseAdapter.OrderInfo info, int position, String cause) {

        info.isValid=false;
        info.cause=cause;
        info.refundStatus=1;
        adapter.notifyDataSetChanged();

//        if (ls_data.size()==0){
//            setReloadAvailable();
//        }


    }

    @Override
    public void mark(OrderBaseAdapter.OrderInfo info, int position) {

        adapter.notifyDataSetChanged();
//        if (ls_data.size()==0){
//            setReloadAvailable();
//        }

    }


}
