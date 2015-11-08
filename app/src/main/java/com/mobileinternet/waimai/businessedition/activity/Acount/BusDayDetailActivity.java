package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.app.Dialog;
import android.content.Intent;
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
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusDayDetailActivity extends ActionBarActivity {


    private TextView tv_total;
    private TextView tv_total_online;
    private TextView tv_total_hand;
    private TextView tv_total_allowance;
    private TextView tv_online_order;
    private TextView tv_online_total;
    private TextView tv_hand_order;
    private TextView tv_hand_total;

    private LinearLayout ll_ll;
    private LinearLayout ll_present;


    private String date;
    private Dialog dialog;










    public void onlineDetail(View view){

        Intent intent=new Intent(BusDayDetailActivity.this,DayOrderListActivity.class);
        intent.putExtra("type",0);
        intent.putExtra("date",date);

        startActivity(intent);

    }

    public void handDetail(View view){

        Intent intent=new Intent(BusDayDetailActivity.this,DayOrderListActivity.class);
        intent.putExtra("type",2);
        intent.putExtra("date",date);

        startActivity(intent);
    }



    private void fetchData(){

        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this);
        try {
            jsonObject.put("date",date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this,"加载中...");


        new HttpUtil(this).post(Share.url_even_one_day_business, jsonObject, new HttpUtil.OnHttpListener() {
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


    private void freightUI(JSONObject jsonObject)throws JSONException{

        float onlineTotal=(float)jsonObject.getDouble("online");
        float handTotal=(float)jsonObject.getDouble("offline");



        tv_total.setText("￥" + jsonObject.getInt("total"));
        tv_total_online.setText("￥" + onlineTotal);
        tv_hand_total.setText("￥" + handTotal);
        tv_online_order.setText(jsonObject.getInt("onorders") + "");
        tv_online_total.setText("￥"+onlineTotal);


        tv_hand_order.setText(jsonObject.getInt("offorders")+"");
        tv_total_hand.setText("￥"+handTotal);

        tv_total_allowance.setText("￥" + jsonObject.getInt("premoney"));



        JSONArray array=jsonObject.getJSONArray("present");
        int size=array.length();
        LayoutInflater inflater= LayoutInflater.from(this);


        ll_present.removeAllViews();

        for (int i=0;i<size;i++){

            JSONObject present=array.getJSONObject(i);
            final String name=present.getString("name");
            final String id=present.getString("id");
            final int orders=present.getInt("orders");
            final double money=present.getDouble("money");

            View view=inflater.inflate(R.layout.view_business_day_detail,null);

            ((TextView)view.findViewById(R.id.day_detail_item_tv_activity))
                    .setText(name);

            ((TextView)view.findViewById(R.id.day_detail_item_tv_order))
                    .setText(orders+"");

            ((TextView)view.findViewById(R.id.day_detail_item_tv_allowance))
                    .setText("" + money);

            ll_present.addView(view, ll_present.getChildCount());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(BusDayDetailActivity.this,DayOrderListActivity.class);
                    intent.putExtra("type",1);
                    intent.putExtra("id",id);
                    intent.putExtra("date",date);
                    intent.putExtra("name",name);
                    intent.putExtra("money",(float)money);
                    intent.putExtra("orders",orders);

                    startActivity(intent);
                }
            });

        }

    }



    private void initView(){

        tv_online_order=(TextView)findViewById(R.id.day_detail_tv_online_order);
        tv_online_total=(TextView)findViewById(R.id.day_detail_tv_online_total);

        tv_hand_order=(TextView)findViewById(R.id.day_detail_tv_hand_order);
        tv_hand_total=(TextView)findViewById(R.id.day_detail_tv_hand_total);

        tv_total=(TextView)findViewById(R.id.day_detail_tv_total_total);
        tv_total_allowance=(TextView)findViewById(R.id.day_detail_tv_total_allowance);
        tv_total_online=(TextView)findViewById(R.id.day_detail_tv_total_online);
        tv_total_hand=(TextView)findViewById(R.id.day_detail_tv_total_hand);


        ll_ll=(LinearLayout)findViewById(R.id.day_detail_ll);
        ll_present=(LinearLayout)findViewById(R.id.day_detail_ll_present);

        date=getIntent().getStringExtra("date");

        if (CodeUtil.checkNetState(this)) {
            fetchData();
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_day_detail);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("日营业详情");
        initView();

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

            if (CodeUtil.checkNetState(this)) {

                fetchData();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
