package com.mobileinternet.waimai.businessedition.fragment.TodayOrder;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.NewOrderAdapter;
import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.fragment.ModeFragment;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.MyListView3;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 海鸥2012 on 2015/7/31.
 */
public class NewFragment extends ModeFragment implements
        OrderBaseAdapter.OnNewOrderListener{


    public static android.os.Handler mHanlder;
    private boolean isMsgFromPolling=false;
    private JSONObject postObject;
    private List<OrderBaseAdapter.OrderInfo> ls_searchData=new ArrayList<>();
    private boolean isSearch=false;

    @Override
    protected void fetchData() {

        new HttpUtil(this.getActivity()).post(Share.url_new_order, postObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject,boolean isOk) {

                ((MyListView3) listView).stopRefreshing();

                if (!isOk){
                    isMsgFromPolling=false;
                    if (ls_data.size()==0) {
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

                ((MyListView3) listView).stopRefreshing();
                isMsgFromPolling=false;

                if (ls_data.size()==0) {
                    setReloadAvailable();

                }else{
                    setReloadGone();
                }

            }


        });

    }


    private void freightUI(JSONObject jsonObject) throws JSONException {

        ls_data.clear();


        //获取整套订单的数据
        JSONArray array=jsonObject.getJSONArray("data");
        int size=array.length();

        if (size==0){

            setReloadAvailable();
            CodeUtil.toast(NewFragment.this.getActivity(), "没有新订单");
            adapter.notifyDataSetChanged();

            return;

        }



        for (int i=0;i<size;i++){

            //获取单个订单的数据
            JSONObject dataObject=array.getJSONObject(i);

            //创建订单存储数据结构
            OrderBaseAdapter.OrderInfo info=new OrderBaseAdapter.OrderInfo();

            CodeUtil.doOrderCommonDo(dataObject, info);
            info.isPayOnline=dataObject.getBoolean("isPayOln");

            //将单个订单信息存储到新订单数据源中
            ls_data.add(info);

        }


        adapter.notifyDataSetChanged();
        setReloadGone();

        if (isMsgFromPolling) {
           // MusicService.playNewOrderMusic(NewFragment.this.getActivity());
            isMsgFromPolling=false;
        }

    }

    @Override
    protected void initListView(View view) {
        listView=(MyListView3)view.findViewById(R.id.msg_listview);
        ((MyListView3)listView).closeAutoLoad();
        postObject=new JSONObject();
        IApplication iApplication=(IApplication)getActivity().getApplication();
        try {
            postObject.put("shopId",iApplication.getData(Share.shop_id));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((MyListView3) listView).setOnRefreshLinstener(new MyListView3.OnRefreshLinstener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });
    }

    @Override
    protected View inflateLayout(LayoutInflater inflater) {
        NewFragment.mHanlder=new NewOrderHandler();
        return inflater.inflate(R.layout.fragment_base_today2,null);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isFirstPage=true;
    }

    @Override
    public void invalid(OrderBaseAdapter.OrderInfo info, int position, String cause) {


        info.isValid=false;
        info.cause=cause;
        if (info.isPayOnline){
            info.refundStatus=1;
        }

        ls_data.remove(info);
        ls_searchData.remove(info);

       // ls_data.remove(position);

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
    public void handle(final OrderBaseAdapter.OrderInfo info, int position) {


        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",info.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this.getActivity()).post(Share.url_set_wait_order, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                if (!isOk)
                    return;


                if(isSearch){
                    ls_searchData.remove(info);
                }

                info.status=2;
                Message message=new Message();
                message.what=201;
                message.obj=info;
                WaitFragment.mHanlder.sendMessage(message);

                ls_data.remove(info);
                adapter.notifyDataSetChanged();

                if (ls_data.size()==0){
                    setReloadAvailable();
                }

                CodeUtil.toast(NewFragment.this.getActivity(), "订单已处理");


            }

            @Override
            public void onFailre(String s) {

            }
        });

    }

    @Override
    protected void initAdapter() {

        adapter=new NewOrderAdapter(this.getActivity(),ls_data);
        ((NewOrderAdapter)adapter).setOnNewOrderListener(this);

    }


    public class NewOrderHandler extends android.os.Handler{

        @Override
        public void dispatchMessage(Message msg) {

            if (msg.what==200){
                isMsgFromPolling=true;
                fetchData();
                return;
            }



            //有取消订单
            if (msg.what==201){

                int cancel[]=(int[])msg.obj;


                for (int i=0;i<cancel.length;i++){

                    //去掉ls_data数据源中的无效订单
                    for (int j=0;j<ls_data.size();j++){

                        if (ls_data.get(j).serial==cancel[i]){


                            OrderBaseAdapter.OrderInfo info=ls_data.get(j);
                            info.isValid=false;
                            info.cause="用户主动取消订单";
                            if (info.isPayOnline){
                                info.refundStatus=1;
                            }

                            Message message=new Message();
                            message.what=201;
                            message.obj=ls_data.get(j);
                            HandledFragment.mHanlder.sendMessage(message);
                            ls_data.remove(j);

                            continue;
                        }

                    }

                    //去掉ls_searchData数据源中的无效订单
                    if (isSearch){

                        for (int j=0;j<ls_searchData.size();j++){

                            if (ls_searchData.get(j).serial==cancel[i]){
                                ls_searchData.remove(j);
                                continue;
                            }

                        }

                    }

                    adapter.notifyDataSetChanged();

                }

                if (ls_data.size()==0){
                    setReloadAvailable();
                }

                return;

            }

            //有新订单
            if (msg.what==202){

               isMsgFromPolling=true;
               fetchData();
                return;

            }



            //搜索订单
            if (msg.what==300){

                //设置查询模式标志
                isSearch=true;

                ls_searchData.clear();

                ((NewOrderAdapter)adapter).changeData(ls_searchData);
                String content=(String)msg.obj;

                if ("close".equals(content)){

                    isSearch=false;
                    ((NewOrderAdapter)adapter).changeData(ls_data);
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
    }


    public static NewFragment newInstance( ) {
        NewFragment fragment = new NewFragment();
        return fragment;
    }

}
