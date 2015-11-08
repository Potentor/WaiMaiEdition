package com.mobileinternet.waimai.businessedition.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.view.PagerSlidingTabStrip;

import java.util.ArrayList;

public abstract class BaseTabActivity extends ActionBarActivity {

    protected ViewPager mViewPager;
 //   protected PagerTabStrip mPagerTabStrip;
    protected PagerSlidingTabStrip mPagerTitleStrip;

    protected ArrayList<Fragment> ls_fragment;
    protected ArrayList<String>    ls_title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_tab);


        mViewPager = (ViewPager)findViewById(R.id.base_viewpager);

        extendYourNeed();

        if (ls_fragment==null||ls_title==null){
            return;
        }

        if (ls_fragment.size()==0||ls_title.size()==0){
            return;
        }

        if (ls_fragment.size()!=ls_title.size()){
            return;
        }



        mPagerTitleStrip=(PagerSlidingTabStrip)findViewById(R.id.base_pagertab);


        mViewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        mPagerTitleStrip.setViewPager(mViewPager);


    }

    protected abstract void extendYourNeed();


    public class MyViewPagerAdapter extends FragmentPagerAdapter{
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return ls_fragment.get(arg0);
        }

        @Override
        public int getCount() {
            return ls_fragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ls_title.get(position);
        }


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
