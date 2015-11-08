package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class FinanceActivity extends ActionBarActivity {

    private TextView tv_settle_way;
    private TextView tv_min_cash;
    private TextView tv_my_money;
    private TextView tv_bank_name;
    private TextView tv_bank_user;
    private TextView tv_bank_number;


    private float min_money_for_tixian;
    private float total_money;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("财务中心");


        tv_bank_name = (TextView) findViewById(R.id.finance_tv_bank_name);
        tv_bank_number = (TextView) findViewById(R.id.finance_tv_bank_number);
        tv_bank_user = (TextView) findViewById(R.id.finance_tv_bank_user);
        tv_min_cash = (TextView) findViewById(R.id.finance_tv_min_cash);
        tv_my_money = (TextView) findViewById(R.id.finance_tv_my_money);
        tv_settle_way = (TextView) findViewById(R.id.finance_tv_settle_way);

        if(CodeUtil.checkNetState(this)) {

            fetchData();
        }


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    /**
     * 从服务器获取信息
     */
    private void fetchData() {


        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this);

        final Dialog dialog=DialogUtil.showProgressDialog(this,"加载中...");

        new HttpUtil(this).post(Share.url_finance_center, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();

                if (!isOk){
                    return;
                }


                try {
                    tv_settle_way.setText(jsonObject.getString("way"));
                    tv_bank_name.setText(jsonObject.getString("bank"));

                    total_money=(int)jsonObject.getDouble("remain");
                    tv_my_money.setText("￥"+total_money);
                    min_money_for_tixian=(float)jsonObject.getDouble("minMnyPay");
                    tv_min_cash.setText("￥" + min_money_for_tixian);
                    String bankId=jsonObject.getString("bankId");
                   // tv_bank_number.setText("********"+bankId.substring(bankId.length()-5,bankId.length()-1));
                    tv_bank_number.setText(bankId);
                    tv_bank_user.setText(jsonObject.getString("userName"));
                    if (dialog!=null){
                        dialog.dismiss();
                    }

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

    /*
    *
    * 提现按钮点击
    * */
    public void takeOutMoney(View view) {

        Intent intent = new Intent(this, TakeOutMnyActivity.class);
        intent.putExtra("min_tixian", min_money_for_tixian);
        intent.putExtra("total",total_money);
        startActivityForResult(intent, 100);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != 200)
            return;

        float all_money = data.getFloatExtra("total", 0.0f);

        tv_my_money.setText("" + all_money);

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
            if (CodeUtil.checkNetState(this)){
                fetchData();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
