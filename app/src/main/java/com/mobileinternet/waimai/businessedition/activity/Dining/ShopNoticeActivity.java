package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopNoticeActivity extends ActionBarActivity {

    private EditText et_notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_notice);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("商家公告");

        et_notice=(EditText)findViewById(R.id.notice_shop_et);

        if(CodeUtil.checkNetState(this)) {
            fetchData();
        }

    }



    private void fetchData(){

        JSONObject jsonObject=new JSONObject();
        IApplication iApplication=(IApplication)getApplication();
        try {
            jsonObject.put("shopId",iApplication.getData(Share.shop_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_get_shop_notice, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                try {
                    et_notice.setText(jsonObject.getString("content"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailre(String s) {

            }
        });

    }

    private void complete(){

        String content=et_notice.getText().toString();
        if ("".equals(content)){
            content=" ";
        }


        JSONObject jsonObject=new JSONObject();
        IApplication iApplication=(IApplication)getApplication();
        try {
            jsonObject.put("shopId",iApplication.getData(Share.shop_id));
            jsonObject.put("content",content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_modify_shop_notice, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                CodeUtil.toast(ShopNoticeActivity.this,"修改成功");
                finish();

            }

            @Override
            public void onFailre(String s) {

            }
        });


        finish();

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_notice, menu);
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
            complete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
