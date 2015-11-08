package com.mobileinternet.waimai.businessedition.fragment.AcountCenter;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RefundHandleFragment extends RefundFragment {


    public static Handler handler;

    public static RefundHandleFragment newInstance( ) {
        RefundHandleFragment fragment = new RefundHandleFragment();
        return fragment;
    }


    @Override
    protected void freightUI(JSONObject jsonObject) throws JSONException {

        JSONArray array=jsonObject.getJSONArray("data");


        ls_data.clear();
        int size=array.length();

        if (size==0){
            setReloadAvailable();
           // CodeUtil.toast(this.getActivity(), "暂时还没有数据");
            adapter.notifyDataSetChanged();
            return;
        }



        for (int i=0;i<size;i++){

            JSONObject order=array.getJSONObject(i);
            OrderBaseAdapter.OrderInfo info=new OrderBaseAdapter.OrderInfo();
            CodeUtil.doOrderCommonDo(order,info);
            info.cause=order.getString("cause");
            info.refundStatus=order.getInt("status");


           ls_data.add(info);

        }


         setReloadGone();
         adapter.notifyDataSetChanged();



    }

    @Override
    protected void exceedInitView(View view) {

        RefundHandleFragment.handler=new Handler(){
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);

                if (msg.what==201) {
                    setReloadGone();
                    ls_data.add(0,(OrderBaseAdapter.OrderInfo) msg.obj);
                    adapter.notifyDataSetChanged();
                }
            }
        };

    }

    @Override
    protected void fetchData() {

        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this.getActivity());
        new HttpUtil(this.getActivity()).post(Share.url_handled_refund, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk){
                    setReloadAvailable();
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
               setReloadAvailable();
            }
        });




    }



}
