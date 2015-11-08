package com.mobileinternet.waimai.businessedition.fragment.TodayOrder;

import android.view.LayoutInflater;
import android.view.View;

import com.mobileinternet.waimai.businessedition.R;
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
public abstract class BaseFragment extends ModeFragment {


    private JSONObject postObject;
    protected String url;
    protected List<OrderBaseAdapter.OrderInfo> ls_searchData=new ArrayList<>();

    protected  boolean isSearch=false;


    /**
     * 针对不同的订单类型进行不同的加载设置
     *
     * @param info
     * @param jsonObject
     * @throws JSONException
     */
    public abstract void expandGenData(OrderBaseAdapter.OrderInfo info, JSONObject jsonObject)
            throws JSONException;


    /**
     * 对url赋值
     */
    protected abstract void genUrl();


    @Override
    protected void fetchData() {

        new HttpUtil(this.getActivity()).post(url, postObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {


                ((MyListView3) listView).stopRefreshing();

                if (!isOk) {

                    if (ls_data.size() == 0) {
                        setReloadAvailable();

                    } else {
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

                if (ls_data.size() == 0) {
                    setReloadAvailable();

                } else {
                    setReloadGone();
                }

            }


        });

    }


    private void freightUI(JSONObject jsonObject) throws JSONException {


        ls_data.clear();

        //获取整套订单的数据
        JSONArray array = jsonObject.getJSONArray("data");
        int size = array.length();

        if (size == 0) {
            setReloadAvailable();
            adapter.notifyDataSetChanged();
            CodeUtil.toast(BaseFragment.this.getActivity(), "暂时无订单");
            return;
        }

        for (int i = 0; i < size; i++) {

            //获取单个订单的数据
            JSONObject dataObject = array.getJSONObject(i);

            //创建订单存储数据结构
            OrderBaseAdapter.OrderInfo info = new OrderBaseAdapter.OrderInfo();
            CodeUtil.doOrderCommonDo(dataObject, info);
            expandGenData(info, dataObject);

            //将单个订单信息存储到新订单数据源中
            ls_data.add(info);

        }

        setReloadGone();

        adapter.notifyDataSetChanged();

    }

    @Override
    protected void initListView(View view) {
        listView = (MyListView3) view.findViewById(R.id.msg_listview);
        ((MyListView3) listView).closeAutoLoad();
        postObject = new JSONObject();
        ((MyListView3) listView).setOnRefreshLinstener(new MyListView3.OnRefreshLinstener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });


        IApplication iApplication = (IApplication) getActivity().getApplication();
        try {
            postObject.put("shopId", iApplication.getData(Share.shop_id));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        genUrl();
    }

    @Override
    protected View inflateLayout(LayoutInflater inflater) {

        return inflater.inflate(R.layout.fragment_base_today2, null);

    }


}
