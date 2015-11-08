package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PayForOotActivity extends ActionBarActivity {



    private String number;



    public void connectManager(View view){

        if(null==number) {
            return;
        }

        Dialog dialog=new AlertDialog.Builder(this)
                .setMessage("将要向" + number + "拨打电话")
                .setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        startActivity(intent);
                    }
                }).create();

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_oot);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("开通超时赔付");

        if (CodeUtil.checkNetState(this)){
            fetchData();
        }


    }

    private void fetchData(){

        final Dialog dialog= DialogUtil.showProgressDialog(this, "加载中...");

        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this);

        new HttpUtil(this).post(Share.url_custom_phone, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }

            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();

                if (!isOk)
                    return;

                try {
                    number = jsonObject.getString("phone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
