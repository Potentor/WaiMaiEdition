package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class TakeOutMnyActivity extends ActionBarActivity {

    private EditText et_money;
    private TextView tv_total;

    private float min_money_for_tixian;
    private float total_money;
    private float tixian=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_out_mny);


        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("提现申请");


        et_money=(EditText)findViewById(R.id.tixian_et_money);
        tv_total=(TextView)findViewById(R.id.tixian_tv_total);

        min_money_for_tixian=getIntent().getFloatExtra("min_tixian",10);
        total_money=getIntent().getFloatExtra("total",0);

        tv_total.setText("￥"+total_money);




    }


    public void takeOutMoney(View view){


        if (!Status.isIsConnectNet()){
            Toast.makeText(this,"网络异常，请检查网络连接",Toast.LENGTH_SHORT).show();
            return;
        }


        String str_mny=et_money.getText().toString().replaceAll(" ","");

        if ("".equals(str_mny)||"".equals(str_mny.trim())){
            Toast.makeText(this,"您还没输入金额",Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            tixian=Float.parseFloat(str_mny);
        }catch (NumberFormatException e){
            return;
        }

        //判断是否低于最小金额
        if (tixian<min_money_for_tixian){
            Toast.makeText(this,"输入金额不能小于最低提款金额",Toast.LENGTH_SHORT).show();
            return;
        }


        //启动提现程序，发起服务器访问

        final Dialog mDialog= DialogUtil.showProgressDialog(this, "提交申请...");
        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this);
        try {
            jsonObject.put("money",tixian);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_tixian, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                mDialog.dismiss();
                if (!isOk)
                    return;

                //成功后更改余额
                total_money -= tixian;
                tv_total.setText("￥" + total_money);
                et_money.setText("");

                CodeUtil.toast(TakeOutMnyActivity.this,"已申请提现...");

            }

            @Override
            public void onFailre(String s) {
                mDialog.dismiss();

            }
        });





    }



    @Override
    public boolean onSupportNavigateUp() {
        getIntent().putExtra("total",total_money);
        setResult(200,getIntent());
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        getIntent().putExtra("total",total_money);
        setResult(200,getIntent());
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_out_mny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
