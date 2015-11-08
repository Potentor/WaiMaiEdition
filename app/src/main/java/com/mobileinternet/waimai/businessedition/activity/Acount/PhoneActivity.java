package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.fragment.main.AcountFragment;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneActivity extends ActionBarActivity {


    private EditText et_confirm_number;
    private EditText et_phone;






    public void fetchConfirmNumber(View view){



        String str_phone=et_phone.getText().toString();
        if ("".equals(str_phone)||"".equals(str_phone.trim())){
            Toast.makeText(this,"请输入绑定的手机号",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!CodeUtil.checkNetState(this))
            return;


        JSONObject jsonObject= new JSONObject();
        IApplication iApplication=(IApplication)getApplication();
        try {
            jsonObject.put("userId",iApplication.getData(Share.user_id));
            jsonObject.put("tel", str_phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new HttpUtil(this).post(Share.url_get_code_phone, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                CodeUtil.toast(PhoneActivity.this, "验证码请求已发送");
            }

            @Override
            public void onFailre(String s) {

            }
        });


    }


    public void applyModification(View view){


        String str_phone=et_phone.getText().toString();

        if ("".equals(str_phone)||"".equals(str_phone.trim())){
            Toast.makeText(this,"请输入绑定的手机号",Toast.LENGTH_SHORT).show();
            return;
        }

        String st_number=et_confirm_number.getText().toString();
        if("".equals(st_number)||"".equals(st_number.trim())){
            Toast.makeText(this,"请先获取验证码",Toast.LENGTH_SHORT).show();
            return;

        }


        JSONObject jsonObject=new JSONObject();
        IApplication iApplication=(IApplication)getApplication();
        try {
            jsonObject.put("userId",iApplication.getData(Share.user_id));
            jsonObject.put("tel",str_phone);
            jsonObject.put("code",st_number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog= DialogUtil.showProgressDialog(this,"绑定中...");

        new HttpUtil(this).post(Share.url_bind_phone, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();

                if (!isOk)
                    return;

                successBind();

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });


    }




    private void successBind(){
        AcountFragment.phone=et_phone.getText().toString();
        finish();
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("绑定手机");


        et_confirm_number=(EditText)findViewById(R.id.phone_et_confirm_number);
        et_phone=(EditText)findViewById(R.id.phone_et_phone);




    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
