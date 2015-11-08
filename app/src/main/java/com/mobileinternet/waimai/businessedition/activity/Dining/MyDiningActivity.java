package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyDiningActivity extends ActionBarActivity {


    private JSONObject postObject;

    private TextView tv_pay_oot;
    private TextView tv_rate_intime;
    private TextView tv_better_intime;
    private TextView tv_speed;
    private TextView tv_better_speed;
    private TextView tv_confirm;
    private TextView tv_better_confirm;
    private TextView tv_phone;
    private TextView tv_address;
    private TextView tv_time;
    private TextView tv_qulification;
    private LinearLayout linearLayout;
    private LinearLayout ll_present;


    /**
     * 认证资质
     * @param view
     */
    public void qulification(View view){

        Intent intent=new Intent(this,QuanlityActivity.class);
        startActivity(intent);

    }

    /**
     * 订餐电话
     * @param view
     */
    public void phone(View view){

        final EditText editText=(EditText)LayoutInflater.from(this).inflate(R.layout.view_edittext,null);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setText(tv_phone.getText().toString());


        Dialog dialog=new AlertDialog.Builder(this,R.style.edit_dialog)
                .setView(editText)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        applyPhone(editText.getText().toString());
                    }
                })
                .setNegativeButton("取消",null)
                .setTitle("修改订餐电话")
                .create();
        dialog.show();


        dialog.show();
        dialog.getWindow().setLayout(600, 400);


    }

    /**
     * 提交电话号码修改到服务器
     * @param phone
     */
    private void applyPhone(final String phone){
        JSONObject jsonPhone=new JSONObject();
        IApplication iApplication=(IApplication)getApplication();
        try {
            jsonPhone.put("shopId",iApplication.getData(Share.shop_id));
            jsonPhone.put("phone",phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_modify_phone, jsonPhone, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk) {
                    return;
                }

                tv_phone.setText(phone);

                CodeUtil.toast(MyDiningActivity.this, "修改成功");

            }

            @Override
            public void onFailre(String s) {

            }
        });

    }

    /**
     * 餐厅地址
     * @param view
     */
    public void address(View view){

        final EditText editText=(EditText)LayoutInflater.from(this).inflate(R.layout.view_edittext,null);
        editText.setText(tv_address.getText().toString());


        Dialog dialog=new AlertDialog.Builder(this,R.style.edit_dialog)
                .setView(editText)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        applyAddress(editText.getText().toString());
                    }
                })
                .setNegativeButton("取消",null)
                .setTitle("修改餐厅地址")
                .create();
        dialog.show();
        dialog.getWindow().setLayout(500, 400);


    }

    /**
     * 提交餐厅地址修改到服务器
     * @param address
     */
    private void applyAddress(final String address){

        JSONObject jsonAddress=new JSONObject();
        IApplication iApplication=(IApplication)getApplication();
        try {
            jsonAddress.put("shopId",iApplication.getData(Share.shop_id));
            jsonAddress.put("address",address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_modify_address, jsonAddress, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk) {
                    return;
                }

                tv_address.setText(address);

                CodeUtil.toast(MyDiningActivity.this, "修改成功");

            }

            @Override
            public void onFailre(String s) {

            }
        });


       
    }

    /**
     * 超时赔付
     */
    public void payForOot(View view){

        Intent intent=new Intent(this,PayForOotActivity.class);
        startActivity(intent);
    }



    private void fetchData(){

        final Dialog dialog= DialogUtil.showProgressDialog(this,"加载中...");
        dialog.dismiss();
        new HttpUtil(this).post(Share.url_dining_info2, postObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                if (!isOk) {
                    return;
                }

                try {
                    freigntUt(jsonObject);
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

    private void freigntUt(JSONObject jsonObject) throws JSONException{


        //商家的认证状态
        int idenStatus=jsonObject.getInt("idenStatus");
        if (idenStatus==0){
            tv_qulification.setText("还有证件未上传，或未审核通过");
        }else{
            tv_qulification.setText("已全部认证通过");
        }


        //商家的营业时间
        JSONArray timeArray=jsonObject.getJSONArray("busTime");
        int size=timeArray.length();
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<size;i++){
            builder.append(timeArray.getString(i));
            builder.append(" ");

        }
        tv_time.setText(builder.toString());


        //商家与其他商家相比
        String mode="快于周围%s的商家";
        tv_confirm.setText(jsonObject.getString("cfimTime"));
        tv_better_confirm.setText(String.format(mode, jsonObject.getString("cfimThan")));

        tv_speed.setText(jsonObject.getString("cosmTime"));
        tv_better_speed.setText(String.format(mode, jsonObject.getString("consmThan")));

        tv_rate_intime.setText(jsonObject.getString("iTmRate"));
        tv_better_intime.setText(String.format(mode, jsonObject.getString("iTmThan")));


        //商家是否开通超时赔付
        int hasOPFOT=jsonObject.getInt("hasOPFOT");
        if (hasOPFOT==0){
            tv_pay_oot.setText("未开通");
        }else{
            tv_pay_oot.setText("已开通");
        }


        //商家都享有哪些优惠活动
        JSONArray present=jsonObject.getJSONArray("present");
        size=present.length();
        ll_present.removeAllViews();
        LayoutInflater inflater=LayoutInflater.from(this);
        for (int i=0;i<size;i++){

            TextView textView=(TextView)inflater.inflate(R.layout.view_textview,null);
            textView.setText(present.getString(i));
            ll_present.addView(textView);

        }

        //却好商家的电话、商家的地址
        tv_phone.setText(jsonObject.getString("phone"));
        tv_address.setText(jsonObject.getString("addr"));



    }




    private void initView(){

        tv_address=(TextView)findViewById(R.id.dining_address);
        tv_better_confirm=(TextView)findViewById(R.id.dining_better_confirm);
        tv_better_intime=(TextView)findViewById(R.id.dining_better_intime);
        tv_better_speed=(TextView)findViewById(R.id.dining_better_speed);
        tv_confirm=(TextView)findViewById(R.id.dining_minute_confirm);
        tv_pay_oot=(TextView)findViewById(R.id.dining_pay_oot);
        tv_phone=(TextView)findViewById(R.id.dining_phone);
        tv_qulification=(TextView)findViewById(R.id.dining_qulification);
        tv_rate_intime=(TextView)findViewById(R.id.dining_rate_intime);
        tv_speed=(TextView)findViewById(R.id.dining_minute_speed);
        tv_time=(TextView)findViewById(R.id.dining_time_business);
        linearLayout=(LinearLayout)findViewById(R.id.dining_ll);
        ll_present=(LinearLayout)findViewById(R.id.dining_ll_present);




        postObject = new JSONObject();
        IApplication iApplication = (IApplication) this.getApplication();
        try {
            postObject.put("shopId", iApplication.getData(Share.shop_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }






        if (CodeUtil.checkNetState(this)) {
            fetchData();
        }

    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dining);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        String shop_name=((IApplication)getApplication()).getData(Share.shop_name);
        mActionBar.setTitle(shop_name);



        initView();
    }


    /**
     * 修改时间
     * @param view
     */
    public void modifyTime(View view){
        Intent intent=new Intent(this,TimeModifyActivity.class);
        startActivityForResult(intent,100);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode!=200)
            return;

        if (requestCode==100){

            //
            String[] str_time=data.getStringArrayExtra("time");

            StringBuilder builder=new StringBuilder("");
            for (String str:str_time){

                builder.append(str+" ");

            }
            tv_time.setText(builder.toString());

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}
