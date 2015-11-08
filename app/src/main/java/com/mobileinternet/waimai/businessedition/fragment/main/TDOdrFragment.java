package com.mobileinternet.waimai.businessedition.fragment.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.fragment.TodayOrder.HandledFragment;
import com.mobileinternet.waimai.businessedition.fragment.TodayOrder.NewFragment;
import com.mobileinternet.waimai.businessedition.fragment.TodayOrder.WaitFragment;
import com.mobileinternet.waimai.businessedition.view.MLinearLayout;
import com.mobileinternet.waimai.businessedition.view.PagerSlidingTabStrip;

import java.util.ArrayList;


public class TDOdrFragment extends Fragment {




    private MLinearLayout view;
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mStrip;

    private ArrayList<Fragment> ls_fragment;
    private ArrayList<String>    ls_title;
    private MyViewPagerAdapter adapter;

//    private NewOrderFragment mNewOrderFragment;
//    private WaitOrderFragment3 mWaitOrderFragment;
//    private HandledOrderFragment3 mHandledOrderFragment;








//    private void refresh(){
//
//        if (!Status.isIsConnectNet()){
//            Toast.makeText(this.getActivity(),"网络连接异常,请检查网络连接",Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        //mSwipe.setRefreshing(true);
//
//
//
//
//    }

    public int getSelectPosition(){
        return mViewPager.getCurrentItem();
    }



    private void initView(){

        mViewPager=(ViewPager)view.findViewById(R.id.today_viewpager);
        mStrip=(PagerSlidingTabStrip)view.findViewById(R.id.today_psts);


        ls_fragment=new ArrayList<>();
        ls_title=new ArrayList<>();






        ls_fragment.add(NewFragment.newInstance());
        ls_fragment.add(WaitFragment.newInstance());
        ls_fragment.add(HandledFragment.newInstance());


        ls_title.add("新订单");
        ls_title.add("待配送");
        ls_title.add("已处理");



        adapter=new MyViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mStrip.setViewPager(mViewPager);

        mViewPager.setOffscreenPageLimit(3);




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

    public static TDOdrFragment newInstance() {
        TDOdrFragment fragment = new TDOdrFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view=(MLinearLayout)inflater.inflate(R.layout.fragment_today_order, container, false);

        initView();

        return view;
    }



}
