package com.mobileinternet.waimai.businessedition.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Status;



public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);


        getSupportActionBar().hide();


        final Intent intent=new Intent();

        if (Status.isIsConfigFileDamage()){
            intent.setClass(this,LoginActivity.class);
        }else{
            intent.setClass(this,MainActivity.class);
        }


        Handler mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FlashActivity.this.startActivity(intent);
                finish();
            }
        },3000);


    }


}
