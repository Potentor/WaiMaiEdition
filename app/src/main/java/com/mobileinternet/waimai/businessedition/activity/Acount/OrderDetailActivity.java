package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class OrderDetailActivity extends ActionBarActivity {


    private String id;


    private void freightUI(JSONObject jsonObject) throws JSONException {

        OrderBaseAdapter.OrderInfo info = new OrderBaseAdapter.OrderInfo();

        JSONObject jsonData=jsonObject.getJSONObject("data");

        CodeUtil.doOrderCommonDo(jsonData, info);

        info.isPayOnline=jsonData.getBoolean("isonpay");


        freightView(info);


    }



    private void freightView(OrderBaseAdapter.OrderInfo info) {


        //订单号
        ((TextView) findViewById(R.id.order_item_tv_serial)).setText(info.serial + "号");
        //下单日期
        ((TextView) findViewById(R.id.order_item_tv_date)).setText(info.date);

        //下单人姓名
        if (info.isAnony) {
            ((TextView) findViewById(R.id.order_item_tv_user)).setText("匿名");
        } else {
            ((TextView) findViewById(R.id.order_item_tv_user)).setText(info.name + "(" + info.sex + ")");
        }

        //下担人电话号码、地址
        ((TextView) findViewById(R.id.order_item_tv_phone)).setText(info.phone);
        ((TextView) findViewById(R.id.order_item_tv_address)).setText(info.address);


        LayoutInflater inflater = LayoutInflater.from(this);

        //加载菜品
        LinearLayout ll_dish = (LinearLayout) findViewById(R.id.order_item_ll_dishes);

        ll_dish.removeAllViews();
        int size = info.ls_dish.size();
        for (int i = 0; i < size; i++) {
            OrderBaseAdapter.DisheInfo disheInfo = info.ls_dish.get(i);
            View dish_view = inflater.inflate(R.layout.view_order_dish, null);
            ((TextView) dish_view.findViewById(R.id.order_item_tv_dish_name)).setText(disheInfo.name);
            ((TextView) dish_view.findViewById(R.id.order_item_tv_dish_num)).setText("X" + disheInfo.num);
            ((TextView) dish_view.findViewById(R.id.order_item_tv_dish_price)).setText(disheInfo.price + "元");
            ll_dish.addView(dish_view, ll_dish.getChildCount());
        }

        if (!info.isPayOnline){
            ((TextView) findViewById(R.id.order_item_tv_paid_way)).setText("餐到付款");
        }


        //加载活动
        LinearLayout ll_present = (LinearLayout) findViewById(R.id.order_item_ll_other_outlay);


        //加载活动
        size = info.ls_present.size();

        // int childCount=ll_present.getChildCount();

        ll_present.removeViews(1, ll_present.getChildCount() - 1);

        if (size==0) {

            View present_view = inflater.inflate(R.layout.view_order_present, null);
            ((TextView) present_view.findViewById(R.id.order_item_tv_present_name)).setText("无");
            ll_present.addView(present_view, ll_present.getChildCount());

        }


        for (int i = 0; i < size; i++) {
            OrderBaseAdapter.PresentInfo presentInfo = info.ls_present.get(i);
            View present_view = inflater.inflate(R.layout.view_order_present, null);
            ((TextView) present_view.findViewById(R.id.order_item_tv_present_name)).setText(presentInfo.name);
            ((TextView) present_view.findViewById(R.id.order_item_tv_present_money)).setText("-" + presentInfo.price);
            ll_present.addView(present_view, ll_present.getChildCount());
        }

//        //餐到付款还是在线支付
//        if (!info.isPayOnline) {
//            ((TextView) findViewById(R.id.order_item_tv_paid_way)).setText("餐到付款");
//            findViewById(R.id.order_item_iv_has_paid).setVisibility(View.GONE);
//        }

        ((TextView)findViewById(R.id.order_item_tv_status)).setText("用户已确认收货");

        //单子总价
        ((TextView) findViewById(R.id.order_item_tv_money)).setText(info.money + "元");


    }


    private void fetchData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this, "加载中...");


        new HttpUtil(this).post(Share.url_order_detail, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();
                if (!isOk)
                    return;

                try {
                    freightUI(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        id = getIntent().getStringExtra("id");

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("订单详情");

        findViewById(R.id.order_item_ll_mark).setVisibility(View.GONE);
        findViewById(R.id.order_item_ll_yes_or_no).setVisibility(View.GONE);
        findViewById(R.id.order_item_ll_share).setVisibility(View.GONE);
        findViewById(R.id.order_item_tv_cause).setVisibility(View.GONE);


        if (CodeUtil.checkNetState(this)) {
            fetchData();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            fetchData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
