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
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PwdActivity extends ActionBarActivity {


    private EditText et_original;
    private EditText et_new;
    private EditText et_confirm;








    public void  applyModification(View view){

        String st_old=et_original.getText().toString();
        if("".equals(st_old)||"".equals(st_old.trim())){
            Toast.makeText(this,"请输入原来的密码",Toast.LENGTH_SHORT).show();
            return;
        }


        String st_new=et_new.getText().toString();
        if("".equals(st_new)||"".equals(st_new.trim())){
            Toast.makeText(this,"您还没有输入新密码",Toast.LENGTH_SHORT).show();
            return;
        }


        String st_confirm=et_confirm.getText().toString();
        if("".equals(st_confirm)||"".equals(st_confirm.trim())){
            Toast.makeText(this,"请确认你的新密码",Toast.LENGTH_SHORT).show();
            return;
        }


        st_confirm=st_confirm.trim();
        st_new=st_new.trim();
        st_old=st_old.trim();

        if(!st_new.equals(st_confirm)){
            Toast.makeText(this,"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
            return;
        }



        IApplication iApplication=(IApplication)getApplication();

        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put("userId",iApplication.getData(Share.user_id));
            jsonObject.put("oldPwd",st_old);
            jsonObject.put("newPwd",st_new);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final Dialog dialog= DialogUtil.showProgressDialog(this,"正在提交修改...");

        new HttpUtil(this).post(Share.url_pwd_modify, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();
                if (!isOk)
                    return;

                CodeUtil.toast(PwdActivity.this,"修改成功");
                finish();

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });

        //提交到服务器



    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);


        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("修改密码");

        et_confirm=(EditText)findViewById(R.id.pwd_et_confirm);
        et_new=(EditText)findViewById(R.id.pwd_et_new);
        et_original=(EditText)findViewById(R.id.pwd_et_original);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
