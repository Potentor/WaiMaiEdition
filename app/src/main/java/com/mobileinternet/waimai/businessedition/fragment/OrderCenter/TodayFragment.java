package com.mobileinternet.waimai.businessedition.fragment.OrderCenter;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.adapter.TodayOrderAdapter;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.fragment.ModeFragment;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.AutoRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 订单中心：今天订单
 */
public class TodayFragment extends ModeFragment implements OrderBaseAdapter.OnNumalListner{

    public static Handler handler;



    private void freightUI(JSONObject jsonObject)throws JSONException{


        ls_data.clear();

        JSONArray array=jsonObject.getJSONArray("data");

        int size=array.length();
        if (size==0){
            if (ls_data.size()==0){
                setReloadAvailable();
            }else{
                setReloadGone();
            }
            adapter.notifyDataSetChanged();
            CodeUtil.toast(TodayFragment.this.getActivity(), "没有订单");
            return;
        }


        for (int i=0;i<size;i++){

            JSONObject object=array.getJSONObject(i);
            OrderBaseAdapter.OrderInfo info=new OrderBaseAdapter.OrderInfo();
            CodeUtil.doOrderCommonDo(object,info);
            info.isValid=object.getBoolean("isvalid");
            info.isPayOnline=object.getBoolean("isPayOln");

            //是否有效
            if (!info.isValid){
                info.cause=object.getString("cause");

                //是否在线付款了
                if (info.isPayOnline){
                    info.refundStatus=object.getInt("status");

                }
            }else{
                info.status=object.getInt("category");
                if (info.status==4){
                    info.out_time = DateUtil.getHourAndMinute(object.getString("sendtime"));
                }
            }
            ls_data.add(info);
        }

        setReloadGone();

        adapter.notifyDataSetChanged();


    }


    @Override
    protected void fetchData() {

        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this.getActivity());

        final Dialog dialog;
        if (ls_data.size()>0){
            dialog=DialogUtil.showProgressDialog(this.getActivity(),"加载中...");
        }else{
            dialog=null;
        }

        new HttpUtil(this.getActivity()).post(Share.url_today_order, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {


                if (dialog!=null){
                    dialog.dismiss();
                }

                if (!isOk) {
                    if (ls_data.size()==0){
                        setReloadAvailable();
                    }else{
                        setReloadGone();
                    }
                    return;
                }

                try {
                    freightUI(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailre(String s) {
                if (ls_data.size()==0){
                    setReloadAvailable();
                }else{
                    setReloadGone();
                }

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

    }



    @Override
    protected void initAdapter() {
        adapter=new TodayOrderAdapter(this.getActivity(),ls_data);
        ((TodayOrderAdapter)adapter).setOnNumalListener(this);
    }

    @Override
    protected void initListView(View view) {
        TodayFragment.handler=new MHandler();
        listView=(AutoRefreshListView)view.findViewById(R.id.msg_listview);
        ((AutoRefreshListView)listView).setIfAutoScroll(false);
        ((AutoRefreshListView)listView).removeFootView();

    }

    @Override
    protected View inflateLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_base,null);
    }

    /**
     * 覆盖父类的此方法
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        isFirstPage=true;
    }

    public static TodayFragment newInstance( ) {
        TodayFragment fragment = new TodayFragment();
        return fragment;
    }


    @Override
    public void invalid(OrderBaseAdapter.OrderInfo info, int position, String cause) {

        info.isValid=false;
        info.cause=cause;
        info.refundStatus=1;
        adapter.notifyDataSetChanged();


    }



    @Override
    public void mark(OrderBaseAdapter.OrderInfo info, int position) {
        adapter.notifyDataSetChanged();
    }


    public class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            if (msg.what==200){
                 fetchData();
            }


        }
    }
}
