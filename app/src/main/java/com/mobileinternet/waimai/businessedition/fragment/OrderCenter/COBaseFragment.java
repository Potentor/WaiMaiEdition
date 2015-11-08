package com.mobileinternet.waimai.businessedition.fragment.OrderCenter;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.HisOrderAdapter;
import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.fragment.ModeFragment;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.AutoRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 订单中心中；昨天、前天fragment
 */
public abstract class COBaseFragment extends ModeFragment {

    protected String url;


    private void freightUI(JSONObject jsonObject)throws JSONException{

        ls_data.clear();

        JSONArray array=jsonObject.getJSONArray("data");

        int size=array.length();
        if (size==0){
            CodeUtil.toast(COBaseFragment.this.getActivity(), "没有订单");
            if (ls_data.size()==0){
                setReloadAvailable();
            }else{
                setReloadGone();
            }
            adapter.notifyDataSetChanged();
            return;
        }



        for (int i=0;i<size;i++){

            JSONObject object=array.getJSONObject(i);
            OrderBaseAdapter.OrderInfo info=new OrderBaseAdapter.OrderInfo();
            CodeUtil.doOrderCommonDo(object, info);

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


                if (object.opt("category")==null){
                    info.status=5;
                    info.out_time =DateUtil.getHourAndMinute(object.getString("sendtime"));

                }else{
                    info.status = object.optInt("category");
                    if (info.status==5) {
                        info.out_time =DateUtil.getHourAndMinute(object.getString("sendtime"));
                    }
                }

            }
            ls_data.add(info);
        }

        setReloadGone();
        adapter.notifyDataSetChanged();


    }


    protected abstract void initURL();

    @Override
    protected void fetchData() {

        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this.getActivity());

        final Dialog dialog;
        if (ls_data.size()>0){
            dialog= DialogUtil.showProgressDialog(this.getActivity(),"加载中...");
        }else{
            dialog=null;
        }
        new HttpUtil(this.getActivity()).post(url, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                if (dialog!=null){
                    dialog.dismiss();;
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

                if (dialog!=null){
                    dialog.dismiss();;
                }
                if (ls_data.size()==0){
                    setReloadAvailable();
                }else{
                    setReloadGone();
                }
            }
        });
    }




    @Override
    protected void initAdapter() {
        adapter=new HisOrderAdapter(this.getActivity(),ls_data);
    }

    @Override
    protected void initListView(View view) {

        listView=(AutoRefreshListView)view.findViewById(R.id.msg_listview);
        ((AutoRefreshListView)listView).setIfAutoScroll(false);
        ((AutoRefreshListView)listView).removeFootView();
        initURL();

    }

    @Override
    protected View inflateLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_base, null);
    }



}
