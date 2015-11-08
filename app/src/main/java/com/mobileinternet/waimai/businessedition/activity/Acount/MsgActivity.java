package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.support.v7.app.ActionBar;

import com.mobileinternet.waimai.businessedition.activity.BaseTabActivity;
import com.mobileinternet.waimai.businessedition.fragment.AcountCenter.MsgFragment;

import java.util.ArrayList;

public class MsgActivity extends BaseTabActivity {



    @Override
    protected void onStart() {
        super.onStart();

//        if(getIntent().getIntExtra("show",0)!=0){
//            mViewPager.setCurrentItem(4);
//        }
    }

    @Override
    protected void extendYourNeed() {
        ls_title=new ArrayList<>();
        ls_fragment=new ArrayList<>();

        ls_fragment.add(MsgFragment.newInstance(0));
        ls_fragment.add(MsgFragment.newInstance(1));
        ls_fragment.add(MsgFragment.newInstance(2));
        ls_fragment.add(MsgFragment.newInstance(3));
       // ls_fragment.add(MsgFragment.newInstance(4));

        ls_title.add("全部");
        ls_title.add("活动");
        ls_title.add("系统");
        ls_title.add("交易");
      //  ls_title.add("商家");

        mViewPager.setOffscreenPageLimit(5);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("消息通知");

    }
}
