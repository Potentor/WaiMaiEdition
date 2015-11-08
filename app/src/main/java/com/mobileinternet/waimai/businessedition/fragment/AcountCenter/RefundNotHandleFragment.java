package com.mobileinternet.waimai.businessedition.fragment.AcountCenter;

import android.app.Dialog;
import android.os.Message;
import android.view.View;

import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RefundNotHandleFragment extends RefundFragment {



    public static RefundNotHandleFragment newInstance() {
        RefundNotHandleFragment fragment = new RefundNotHandleFragment();
        return fragment;
    }


    @Override
    protected void freightUI(JSONObject jsonObject) throws JSONException {

        JSONArray array=jsonObject.getJSONArray("data");


        ls_data.clear();

        int size=array.length();

        if (size==0){
            setReloadAvailable();
            //CodeUtil.toast(this.getActivity(), "暂时还没有数据");
            return;
        }


        for (int i=0;i<size;i++){
            JSONObject order=array.getJSONObject(i);
            OrderBaseAdapter.OrderInfo info=new OrderBaseAdapter.OrderInfo();
            CodeUtil.doOrderCommonDo(order,info);
            info.cause=order.getString("cause");
            info.refundStatus=0;
            ls_data.add(info);

        }

        setReloadGone();
        adapter.notifyDataSetChanged();


    }

    private  void cmd(final OrderBaseAdapter.OrderInfo info, final int position, final int cmd){




        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",info.id);
            jsonObject.put("cmd",cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog= DialogUtil.showProgressDialog(this.getActivity(),"正在提交...");
        new HttpUtil(this.getActivity()).post(Share.url_agree_or_disagree_refund, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                if (!isOk) {
                    dialog.dismiss();
                    return;
                }

                if (cmd==0){
                    ls_data.remove(position);
                }else{
                    info.refundStatus=1;
                    Message message=new Message();
                    message.what=201;
                    message.obj=info;
                    RefundHandleFragment.handler.sendMessage(message);
                    ls_data.remove(position);
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();
                CodeUtil.toast(RefundNotHandleFragment.this.getActivity(),"提交成功");

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });



    }


    @Override
    protected void exceedInitView(View view) {



            adapter.setOnRefundOrderListener(new OrderBaseAdapter.OnRefundOrderListener() {
                @Override
                public void agree(OrderBaseAdapter.OrderInfo info, int position) {
                    cmd(info,position,1);
                }

                @Override
                public void disAgree(OrderBaseAdapter.OrderInfo info, int position) {
                    cmd(info,position,0);
                }
            });
        }

    @Override
    protected void fetchData() {

        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this.getActivity());
        new HttpUtil(this.getActivity()).post(Share.url_not_handled_refund, jsonObject, new HttpUtil.OnHttpListener() {
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


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isFirstPage=true;
    }
}
