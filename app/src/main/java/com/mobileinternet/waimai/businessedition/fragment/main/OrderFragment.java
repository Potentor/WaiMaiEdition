package com.mobileinternet.waimai.businessedition.fragment.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.fragment.OrderCenter.BeforeYestFragment;
import com.mobileinternet.waimai.businessedition.fragment.OrderCenter.TodayFragment;
import com.mobileinternet.waimai.businessedition.fragment.OrderCenter.YestFragment;
import com.mobileinternet.waimai.businessedition.view.PagerSlidingTabStrip;

import java.util.ArrayList;

/**
 * 订单中心fragment
 */
public class OrderFragment extends Fragment {

    public static Handler handler;


    private ViewPager mViewPager;
    //   protected PagerTabStrip mPagerTabStrip;
    private PagerSlidingTabStrip mPagerTitleStrip;

    private ArrayList<Fragment> ls_fragment;
    private ArrayList<String>    ls_title;
    private int itemOfShowing=0;




    private void initView(View view){

        mViewPager = (ViewPager)view.findViewById(R.id.base_viewpager);

        ls_fragment=new ArrayList<>();
        ls_title=new ArrayList<>();

        ls_fragment.add(TodayFragment.newInstance());
        ls_fragment.add(YestFragment.newInstance());
        ls_fragment.add(BeforeYestFragment.newInstance());


        ls_title.add("今天");
        ls_title.add("昨天");
        ls_title.add("前天");




        mPagerTitleStrip=(PagerSlidingTabStrip)view.findViewById(R.id.base_pagertab);


        mViewPager.setAdapter(new MyViewPagerAdapter(getChildFragmentManager()));
        mPagerTitleStrip.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                itemOfShowing=position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }





    public class MyViewPagerAdapter extends FragmentPagerAdapter {
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
    public void onDestroyView() {
        super.onDestroyView();

    }

    public static OrderFragment newInstance() {
        OrderFragment fragment = new OrderFragment();
        return fragment;
    }

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        OrderFragment.handler=new MHandler();
        View view=inflater.inflate(R.layout.fragment_order, container, false);
        initView(view);
        return view;
    }


    public class MHandler extends Handler{
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            if (msg.what==200){

                switch (itemOfShowing)
                {
                    case 0:
                        TodayFragment.handler.sendEmptyMessage(200);
                        break;
                    case 1:
                        YestFragment.handler.sendEmptyMessage(200);
                        break;
                    case 2:
                        BeforeYestFragment.handler.sendEmptyMessage(200);
                        break;
                }

            }


        }
    }



}
