package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MsgDetailActivity extends ActionBarActivity {

    private TextView tv_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("消息详情");


        Intent intent=getIntent();

       // int type=intent.getIntExtra("type",0);
        String id=intent.getStringExtra("id");
        //String content=intent.getStringExtra("content");

        tv_content=(TextView)findViewById(R.id.msg_detail_tv);


        if(CodeUtil.checkNetState(this)) {
            fetchData(id);
        }
     //   ((TextView)findViewById(R.id.msg_detail_tv)).setText(content);
//        if (type==4){
//            markHasRead(id);
//        }

    }


    private void fetchData(String msgId){

        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this);
        try {
            jsonObject.put("msgId",msgId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog= DialogUtil.showProgressDialog(this,"加载中...");

        new HttpUtil(this).post(Share.url_msg_content, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();
                if (!isOk)
                    return;

                try {
                    String content=jsonObject.getString("data");
                    tv_content.setText(content);
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


//    /**
//     * 标记消息为已读
//     * @param id
//     */
//    private void markHasRead(String id){
//
//        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this);
//        try {
//            jsonObject.put("msgId",id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        new HttpUtil(this).post(Share.url_mark_msg, jsonObject, new HttpUtil.OnHttpListener() {
//            @Override
//            public void onSucess(JSONObject jsonObject, boolean isOk) {
//                if (!isOk)
//                    return;
//
//            }
//
//            @Override
//            public void onFailre(String s) {
//
//            }
//        });
//
//
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
