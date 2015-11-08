package com.mobileinternet.waimai.businessedition.activity.Acount;

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
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.AutoRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private AutoRefreshListView mListView;

    private List<Business> ls_data=new ArrayList<>();

    private  MAdapter adapter;

    private String loadDate;



    private void freightUI(JSONObject jsonObject)throws JSONException{


        JSONArray array=jsonObject.getJSONArray("data");

        int size=array.length();
        if (size==0){
            CodeUtil.toast(this,"没有更多数据了");
            mListView.setStateNoMoreData();
            return;

        }

        for (int i=0;i<size;i++){

            JSONObject object=array.getJSONObject(i);
            Business settle9=new Business();
            settle9.date=object.getString("date");
            settle9.money=(float)object.getDouble("cash");
            ls_data.add(settle9);

        }



        adapter.notifyDataSetChanged();
        loadDate=DateUtil.subtractOneDay(ls_data.get(ls_data.size()-1).date);
        mListView.setStateNoMoreData();

    }




    private void fetchData(){


        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this);
        try {
            jsonObject.put("date",loadDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new HttpUtil(this).post(Share.url_statitics, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                if (!isOk) {
                    mListView.setStateNoMoreData();
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
                mListView.setStateNoMoreData();
            }
        });






    }



















    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("营业统计");



        mListView=(AutoRefreshListView)findViewById(R.id.settle_listview);
        mListView.setOnGetToButtomListener(new AutoRefreshListView.OnGetToButtomListener() {
            @Override
            public void onButtomAndNetOk() {
                fetchData();
            }
        });

       // mListView.setIfAutoScroll(false);


        adapter=new MAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);



        if (CodeUtil.checkNetState(this)) {
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar mCa=Calendar.getInstance();

            loadDate=mDateFormat.format(mCa.getTime());

            fetchData();
        }else{
            mListView.setStateNoMoreData();
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
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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

            Intent intent=new Intent(this,TrendActivity.class);
            startActivity(intent);



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent(this,BusDayDetailActivity.class);
        intent.putExtra("date",ls_data.get(i).date);
        startActivity(intent);
    }


    private class MAdapter extends BaseAdapter {


        private View generateView(View view,int position){

            if (view==null){
                Business info=ls_data.get(position);
                view= LayoutInflater.from(StatisticsActivity.this).inflate(R.layout.item_business_day_list,null);
                ((TextView)view.findViewById(R.id.settle_date)).setText(info.date);
                ((TextView)view.findViewById(R.id.settle_money)).setText("￥"+info.money);
            }


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

    private class Business{
        String date;
        float money;
    }



}
