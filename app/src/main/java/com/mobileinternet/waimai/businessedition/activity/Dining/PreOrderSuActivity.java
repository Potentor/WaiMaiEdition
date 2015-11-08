package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.fragment.NavigationDrawerFragment;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PreOrderSuActivity extends ActionBarActivity {

  // private TextView tv_relax;
   private TextView tv_business;
   private ToggleButton tb_relax;
   private ToggleButton tb_business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_order_su);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("预订单支持");

        tv_business=(TextView)findViewById(R.id.pre_order_tv_business);
      //  tv_relax=(TextView)findViewById(R.id.pre_order_tv_relax);

        tb_business=(ToggleButton)findViewById(R.id.pre_order_tb_business);
        tb_relax=(ToggleButton)findViewById(R.id.pre_order_tb_relax);


        Intent intent=getIntent();
        tb_relax.setChecked(intent.getBooleanExtra("test",false));
        tb_business.setChecked(intent.getBooleanExtra("business",false));


        tb_relax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                relaxChange(b);
            }
        });


        tb_business.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                businessChange(b);
            }
        });


        if(CodeUtil.checkNetState(this)) {
            fetchData();
        }

    }


    private void freightUI(JSONObject jsonObject)throws JSONException{

        StringBuilder builder=new StringBuilder();
        builder.append("营业时间：\n");

        JSONArray array=jsonObject.getJSONArray("time");
        int size=array.length();

        for(int i=0;i<size;i++){

            JSONObject object=array.getJSONObject(i);
           // builder.append(DateUtil.getOnlyHourAndMinute(object.getString("startTime")));
            builder.append(object.getString("startTime"));
            builder.append("-");
            //builder.append(DateUtil.getOnlyHourAndMinute(object.getString("endTime")));
            builder.append(object.getString("endTime"));
            builder.append("\n");


        }

        tv_business.setText(builder.toString());

    }


    private void fetchData(){


        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("shopId", CodeUtil.getShopId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new HttpUtil(this).post(Share.url_buiness_time, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
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

            }
        });


    }






    /**
     * 营业预定状态改变
     * @param state
     */
    private void businessChange(final boolean state){

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("shopId",CodeUtil.getShopId(this));
            if (tb_relax.isChecked()){
                jsonObject.put("rest",1);
            }else{
                jsonObject.put("rest",0);
            }

            if (state){
                jsonObject.put("buss",1);
            }else{
                jsonObject.put("buss",0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_modify_support_state, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk) {
                    return;
                }

                CodeUtil.toast(PreOrderSuActivity.this,"修改成功");

            }

            @Override
            public void onFailre(String s) {
                //tb_business.setChecked(!state);
            }
        });




    }


    /**
     * 休息预定状态改变
     * @param state
     */
    private void relaxChange(final boolean state){

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("shopId",CodeUtil.getShopId(this));
            if (tb_business.isChecked()){
                jsonObject.put("bues",1);
            }else{
                jsonObject.put("bues",0);
            }

            if (state){
                jsonObject.put("rest",1);
            }else{
                jsonObject.put("rest",0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_modify_support_state, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk) {
                    return;
                }

                noticeMainUiChangeShow();
                CodeUtil.toast(PreOrderSuActivity.this,"修改成功");

            }

            @Override
            public void onFailre(String s) {
               // tb_relax.setChecked(!state);
            }
        });


    }


    private void noticeMainUiChangeShow(){

        Status.isIsSupportRelaxTime=tb_relax.isChecked();

        //通知，主界面中的侧滑界面，显示的当前营业状态进行修改
        if (!Status.isForceStopBusiness) {

            Message message=new Message();
            message.what=201;

            int ch=DateUtil.checkInBusinessTime();
            switch (ch) {
                case 1:
                    message.arg1=1;
                    break;
                case 2:
                    message.arg1=2;
                    break;
                case 3:
                    message.arg1=3;
                    break;
            }

            NavigationDrawerFragment.mHandler.sendMessage(message);
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode!=200)
            return;

        if (requestCode!=100)
            return;

        String[] str_time=data.getStringArrayExtra("time");

        StringBuilder builder=new StringBuilder("");
        for (String str:str_time){

            builder.append(str);
            builder.append('\n');

        }

        tv_business.setText(builder.toString());




    }

    @Override
    public boolean onSupportNavigateUp() {

        Intent intent=getIntent();
        intent.putExtra("relax",tb_relax.isChecked());
        intent.putExtra("business",tb_business.isChecked());
        setResult(200,intent);
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        Intent intent=getIntent();
        intent.putExtra("relax",tb_relax.isChecked());
        intent.putExtra("business",tb_business.isChecked());
        setResult(200,intent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pre_order_su, menu);
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

            Intent intent=new Intent(this,TimeModifyActivity.class);
            startActivityForResult(intent,100);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
