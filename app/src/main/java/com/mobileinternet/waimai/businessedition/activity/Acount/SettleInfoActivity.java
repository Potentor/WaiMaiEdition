package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

public class SettleInfoActivity extends ActionBarActivity {

    private AutoRefreshListView mListView;

    private List<Settle> ls_data=new ArrayList<>();

    private  MAdapter adapter;

    private String loadDate;

    private boolean isLoadRestart=false;



    private void fetchData(){

        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this);
        try {
            jsonObject.put("date",loadDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new HttpUtil(this).post(Share.url_record_tixian, jsonObject, new HttpUtil.OnHttpListener() {
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


    private void freightUI(JSONObject jsonObject)throws JSONException{


        if (isLoadRestart) {
            ls_data.clear();
            isLoadRestart = false;
        }

        JSONArray array=jsonObject.getJSONArray("data");

        int size=array.length();
        if (size==0){
            mListView.setStateNoMoreData();
            CodeUtil.toast(this,"没有更多数据了");
            return;
        }


        for (int i=0;i<size;i++){

            JSONObject object=array.getJSONObject(i);
            Settle settle9=new Settle();
            settle9.date=object.getString("cashTime");
            settle9.money=(float)object.getDouble("cash");
            settle9.status=object.getInt("status");
            ls_data.add(settle9);

        }


        mListView.setStateLoadMore();
        adapter.notifyDataSetChanged();
//        loadDate=DateUtil.subtractOneMinute(ls_data.get(ls_data.size()-1).date);



        loadDate=DateUtil.subtractOneMinute(ls_data.get(ls_data.size()-1).date);


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_info);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("提现明细");


        mListView=(AutoRefreshListView)findViewById(R.id.settle_listview);
        mListView.setOnGetToButtomListener(new AutoRefreshListView.OnGetToButtomListener() {
            @Override
            public void onButtomAndNetOk() {
                fetchData();
            }
        });

        mListView.setIfAutoScroll(false);


        adapter=new MAdapter();
        mListView.setAdapter(adapter);


        if (CodeUtil.checkNetState(this)) {
            loadDate = DateUtil.getNowDate();
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_refresh, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            if (CodeUtil.checkNetState(this)){
//              //  if (loadDate==null){
//                loadDate=DateUtil.getNowDate();
//                isLoadRestart=true;
//                //}
//                fetchData();
//            }
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private class MAdapter extends BaseAdapter{


        private View generateView(View view,int position){

            if (view==null){
                view= LayoutInflater.from(SettleInfoActivity.this).inflate(R.layout.item_settle_list,null);
            }

                Settle info=ls_data.get(position);



                ((TextView)view.findViewById(R.id.settle_date)).setText(info.date);
                ((TextView)view.findViewById(R.id.settle_money)).setText("￥"+info.money);
                switch (info.status)
                {
                    case 0:
                        ((TextView)view.findViewById(R.id.settle_status)).setText("等待系统处理");
                        break;
                    case 1:
                        ((TextView)view.findViewById(R.id.settle_status)).setText("提现成功");
                        break;
                    case 2:
                        ((TextView)view.findViewById(R.id.settle_status)).setText("提现失败");
                        break;
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

    private class Settle{
        String date;
        float money;
        int status;
    }


}
