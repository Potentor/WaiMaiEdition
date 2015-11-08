package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;

public class AcountActivity extends ActionBarActivity {

    private TextView tv_acount;



    public void modifyPwd(View view){

        Intent intent=new Intent(this,PwdActivity.class);
        startActivity(intent);

    }




    public void bindPhone(View view){
        Intent intent=new Intent(this,PhoneActivity.class);
        startActivity(intent);
    }





    private void fetchData(){

        IApplication iApplication=(IApplication)getApplication();
        tv_acount.setText(iApplication.getData(Share.user_name));
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);
        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("我的账户");

        tv_acount=(TextView)findViewById(R.id.my_acount_tv_acount);
        fetchData();


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}
