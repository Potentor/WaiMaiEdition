package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class ShopIndexActivity extends ActionBarActivity {


    private TextView tv_jidan;
    private TextView tv_oot;
    private TextView tv_cuidan;
    private TextView tv_jidan_total;
    private TextView tv_oot_total;
    private TextView tv_cuidan_total;
    private TextView tv_rate_cuian;
    private TextView tv_rate_oot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_index);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("餐厅指数");

        initView();
        fetchData();

    }

    private void initView(){

        tv_jidan=(TextView)findViewById(R.id.shop_index_tv_jidan);
        tv_oot=(TextView)findViewById(R.id.shop_index_tv_oot);
        tv_cuidan=(TextView)findViewById(R.id.shop_index_tv_cuidan);
        tv_jidan_total=(TextView)findViewById(R.id.shop_index_tv_total1);
        tv_oot_total=(TextView)findViewById(R.id.shop_index_tv_total2);
        tv_cuidan_total=(TextView)findViewById(R.id.shop_index_tv_total3);

        tv_rate_cuian=(TextView)findViewById(R.id.index_rate_urge);
        tv_rate_oot=(TextView)findViewById(R.id.index_rate_oot);


    }


    /**
     * 从服务器获取数据
     */
    private void fetchData(){


        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("shopId", CodeUtil.getShopId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog= DialogUtil.showProgressDialog(this,"加载中...");


        new HttpUtil(this).post(Share.url_dining_index, jsonObject, new HttpUtil.OnHttpListener() {
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


    public void  freightUI(JSONObject jsonObject)throws JSONException{

        //总订单
        int total=jsonObject.getInt("allorder");
        tv_cuidan_total.setText(total+"");
        tv_jidan_total.setText(total+"");
        tv_oot_total.setText(total+"");


        //总处理订单量
        int total_recieve=jsonObject.getInt("orderNum");
        tv_jidan.setText(total_recieve+"");


        //超时接单量
        tv_oot.setText(jsonObject.getInt("orNumOt")+"");
        //催单量
        tv_cuidan.setText(jsonObject.getInt("rmdOr")+"");


        //30日催单率
        tv_rate_cuian.setText(jsonObject.getString("rmdTate"));
        //30日超时接单率
        tv_rate_oot.setText(jsonObject.getString("ootRate"));



    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_dining, menu);
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

            if (CodeUtil.checkNetState(this)){
                fetchData();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
