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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DayOrderListActivity extends ActionBarActivity {

    private int type=0;
    private String date;
    private String id;
    private String name;
    private float allowance;
    private int orders;



    private TextView tv_summarize;
    private ListView listView;
    private MAdapter adapter;
    private List<DayOrder> ls_data=new ArrayList<>();






    private void fetchData(){


        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this);
        try {
            jsonObject.put("date",date);


            if (type==1){
                jsonObject.put("id",id);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url=null;

        if (type!=0) {

        }else{

        }

        switch (type)
        {

            case 1:
                url=Share.url_even_order_one_day;
                break;
            case 0:
                url=Share.url_one_day_online;
                break;
            case 2:
                url=Share.url_one_day_offline;
                break;
        }


        final Dialog dialog=DialogUtil.showProgressDialog(this, "加载中...");

        new HttpUtil(this).post(url, jsonObject, new HttpUtil.OnHttpListener() {
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



        ls_data.clear();
        JSONArray array=jsonObject.getJSONArray("data");
        int size=array.length();
        for (int i=0;i<size;i++) {

            JSONObject object=array.getJSONObject(i);
            DayOrder info = new DayOrder();
            info.money = (float)object.getDouble("money");
            info.orderId = object.getString("orderId");
            info.serial = object.getInt("number");
            ls_data.add(info);

        }

        adapter.notifyDataSetChanged();

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_order_list);


        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);


        tv_summarize=(TextView)findViewById(R.id.day_order_list_tv);
        listView=(ListView)findViewById(R.id.day_order_list_ls);


        Intent intent=getIntent();
        type=intent.getIntExtra("type", 0);
        date=intent.getStringExtra("date");


        switch (type)
        {
            case 0:
                mActionBar.setTitle(date+" 在线付款订单");
                tv_summarize.setVisibility(View.GONE);
                break;
            case 2:
                mActionBar.setTitle(date+" 餐到付款订单");
                tv_summarize.setVisibility(View.GONE);
                break;
            case 1:
                this.id=intent.getStringExtra("id");
                name=intent.getStringExtra("name");
                allowance=intent.getFloatExtra("money", 0f);
                orders=intent.getIntExtra("orders", 0);
                mActionBar.setTitle(name);


                String str=String.format("%s,%s,共%s单,￥%s",date,name,orders,allowance);
                tv_summarize.setText(str);
                break;
        }

//        if (type==0){
//            mActionBar.setTitle(date+" 全部订单");
//            tv_summarize.setVisibility(View.GONE);
//        }else{


//        }

        adapter=new MAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mIntent = new Intent(DayOrderListActivity.this, OrderDetailActivity.class);
                mIntent.putExtra("id", ls_data.get(i).orderId);
                startActivity(mIntent);
            }
        });


        if (CodeUtil.checkNetState(this)) {
            fetchData();
        }



    }



    private class MAdapter extends BaseAdapter {


        private View generateView(View view,int position){

            if (view==null) {
                view= LayoutInflater.from(DayOrderListActivity.this).inflate(R.layout.item_day_order_listl,null);
            }


            DayOrder info=ls_data.get(position);
            ((TextView)view.findViewById(R.id.day_order_list_item_tv_money)).setText(""+info.money);
            ((TextView)view.findViewById(R.id.day_order_list_item_tv_order_number)).setText(info.orderId);
            ((TextView)view.findViewById(R.id.day_order_list_item_tv_serial)).setText(""+info.serial);


            return view;

        }



        @Override
        public int getCount() {
            return ls_data.size();
        }

        @Override
        public Object getItem(int i) {
            return ls_data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return generateView(view,i);
        }
    }

    private class DayOrder{
        String orderId;
        float money;
        int serial;
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
