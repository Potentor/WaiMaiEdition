package com.mobileinternet.waimai.businessedition.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.Md5Util;

import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {


    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private Dialog dialog;



    private EditText et_acount;
    private EditText et_pwd;





    public void login(View view)throws JSONException{

        checkNetState();


        if(!CodeUtil.checkNetState(this))
            return;

        String acount=et_acount.getText().toString();
        if ("".equals(acount)||"".equals(acount.trim())){
            Toast.makeText(this, "您还没有输入账号", Toast.LENGTH_SHORT).show();
            return;
        }

        String pwd=et_pwd.getText().toString();
        if ("".equals(pwd)||"".equals(pwd.trim())){
            Toast.makeText(this, "您还没有输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        acount=acount.trim();
        pwd=pwd.trim();

        String signed= Md5Util.md5(acount+pwd);

        JSONObject logObject=new JSONObject();

        logObject.put("sign",signed);
        logObject.put("password",pwd);
        logObject.put("acount", acount);



        dialog= DialogUtil.showProgressDialog(this," 登录中... ");
        new HttpUtil(this).post2(Share.url_login, logObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {


                dialog.dismiss();



                try {

                    int code=jsonObject.getInt("code");
                    if (code!=0){
                        CodeUtil.toast(LoginActivity.this,"账户名或密码错误");
                        return;
                    }

                    handLoginSucess(jsonObject);
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


    private void handLoginSucess(JSONObject jsonObject) throws JSONException{



        String shopId=jsonObject.getString("shopId");
        String userId=jsonObject.getString("userId");
        String imaUrl=jsonObject.getString("imaUrl");
        String userName=jsonObject.getString("userName");
        String shopName=jsonObject.getString("shopName");
        String token=jsonObject.getString("token");
      //  String status=jsonObject.getString("status");
 //营业时间
//        JSONArray time=jsonObject.getJSONArray("time");
//        int size=time.length();
//       Set<String> set=new HashSet<>();
//        while (-1==(size--)){
//
//           // JSONObject feild=time.getJSONObject(size);
////            set.add(feild.getString("startTime"));
////            set.add(feild.getString("endTime"));
//            set.add(time.getString(size));
//
//        }



        IApplication app=(IApplication)getApplication();
        app.storeData(Share.shop_id,shopId);
        app.storeData(Share.user_id,userId);
        app.storeData(Share.logo,imaUrl);
        app.storeData(Share.user_name,userName);
     //   app.storeData(Share.status, status);
        app.storeData(Share.shop_name, shopName);
        app.storeData(Share.token, token);


        SharedPreferences.Editor editor=getSharedPreferences(Share.share_prefrence_name, Activity.MODE_PRIVATE).edit();
        editor.putString(Share.shop_id,shopId);
        editor.putString(Share.user_id,userId);
        editor.putString(Share.logo, imaUrl);
        editor.putString(Share.user_name, userName);
        editor.putString(Share.shop_name, shopName);
        editor.putString(Share.token, token);
       // editor.putStringSet(Share.buse_time,set);




        editor.putBoolean(Share.isLogOut, false);



        editor.commit();
        startActivity(new Intent(this, MainActivity.class));
        dialog.dismiss();
        finish();
        Status.setIsConfigFileDamage(false);


    }

    private void checkNetState(){


        info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            Status.setIsConnectNet(true);
            return;
        }
        Status.setIsConnectNet(false);
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle("商家登录");

        connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        et_acount=(EditText)findViewById(R.id.login_acount);
        et_pwd=(EditText)findViewById(R.id.login_pwd);

        IApplication iApplication=(IApplication)getApplication();
        String name=iApplication.getData(Share.user_name);

        if (name!=null){
            et_acount.setText(name);
        }



    }




}
