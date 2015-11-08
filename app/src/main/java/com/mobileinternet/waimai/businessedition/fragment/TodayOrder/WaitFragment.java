package com.mobileinternet.waimai.businessedition.fragment.TodayOrder;

import android.os.Handler;
import android.os.Message;

import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.adapter.WaitOutAdapter;
import com.mobileinternet.waimai.businessedition.app.Share;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 海鸥2012 on 2015/7/31.
 */
public class WaitFragment extends BaseFragment implements
        OrderBaseAdapter.OnNumalListner{


    public static android.os.Handler mHanlder;



    public static WaitFragment newInstance( ) {
        WaitFragment fragment = new WaitFragment();
        return fragment;
    }

    @Override
    public void expandGenData(OrderBaseAdapter.OrderInfo info, JSONObject jsonObject) throws JSONException {

        info.isPayOnline=jsonObject.getBoolean("isPayOln");
    }

    @Override
    protected void genUrl() {
        url=Share.url_wait_order;
    }

    @Override
    protected void initAdapter() {
        adapter=new WaitOutAdapter(this.getActivity(),ls_data);
        ((WaitOutAdapter)adapter).setOnNumalListener(this);

        WaitFragment.mHanlder=new Handler(){

            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);

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

                    isSearch=true;
                    ls_searchData.clear();

                    ((WaitOutAdapter)adapter).changeData(ls_searchData);
                    String content=(String)msg.obj;

                    if ("close".equals(content)){

                        isSearch=false;
                        ((WaitOutAdapter)adapter).changeData(ls_data);
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
        if (info.isPayOnline){
            info.refundStatus=1;
        }

        if (isSearch){
            ls_searchData.remove(info);
        }

        ls_data.remove(info);

        Message message=new Message();
        message.what=201;
        message.obj=info;
        HandledFragment.mHanlder.sendMessage(message);

        adapter.notifyDataSetChanged();

        if (ls_data.size()==0){
            setReloadAvailable();
        }



    }

    @Override
    public void mark(OrderBaseAdapter.OrderInfo info, int position) {

        info.isValid=true;
        Message message=new Message();
        message.what=201;
        message.obj=info;
        HandledFragment.mHanlder.sendMessage(message);
        if (isSearch)
        {
            ls_searchData.remove(info);
        }

        ls_data.remove(info);
        adapter.notifyDataSetChanged();

        if (ls_data.size()==0){
            setReloadAvailable();
        }



    }




}
