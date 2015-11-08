package com.mobileinternet.waimai.businessedition.fragment;

import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.activity.Dining.TimeModifyActivity;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.smartiv.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerFragment extends Fragment {


    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    public static Handler mHandler;


    private NavigationDrawerCallbacks mCallbacks;

    private Context mContext;


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;


    private TextView tv_acount;
    private TextView tv_status;
    private SmartImageView iv_logo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }


        NavigationDrawerFragment.mHandler = new Handler() {

            @Override
            public void dispatchMessage(Message msg) {

                super.dispatchMessage(msg);

                if (msg.what == 201) {

                    switch (msg.arg1) {
                        case 1:
                            tv_status.setText("营业中");
                            break;
                        case 2:
                            tv_status.setText("可接受预定单");
                            break;
                        case 3:
                            tv_status.setText("休息中");
                            break;
                    }
                    return;

                }

            }
        };

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDrawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);


        this.mContext=getActivity();

        list_text.add("今日订单");
        list_text.add("餐厅管理");
        list_text.add("菜品管理");
        list_text.add("账户中心");
        list_text.add("订单中心");
        list_text.add("设置/打印机");
        list_text.add("关于我们");


        list_R.add(R.drawable.drawer_order_today);
        list_R.add(R.drawable.drawer_poi);
        list_R.add(R.drawable.drawer_food);
        list_R.add(R.drawable.drawer_account);
        list_R.add(R.drawable.drawer_orders);
        list_R.add(R.drawable.drawer_setting);
        list_R.add(R.drawable.drawer_about);


        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });


        View view = inflater.inflate(R.layout.heard_drawer_list, null);
        tv_acount = (TextView) view.findViewById(R.id.drawer_heard_tv_acount);
        tv_status = (TextView) view.findViewById(R.id.drawer_heard_tv_status);
        iv_logo = (SmartImageView) view.findViewById(R.id.drawer_heard_iv);

        mDrawerListView.addHeaderView(view);
        final Context context=NavigationDrawerFragment.this.getActivity();
        tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, TimeModifyActivity.class);
                startActivity(intent);

            }
        });

        iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(0);
            }
        });

        tv_acount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(0);
            }
        });

 //       View view_foot = inflater.inflate(R.layout.view_drag_footer, null);
//        mDrawerListView.addFooterView(view_foot);


        mDrawerListView.setAdapter(new NavigateAdapter());
        IApplication iApplication = (IApplication) getActivity().getApplication();
        iv_logo.setImageUrl(iApplication.getData(Share.logo));
        tv_acount.setText(iApplication.getData(Share.user_name));


      if (Status.isIsConnectNet()){
          applyBusinessTime();
      }

        return mDrawerListView;
    }


    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, Context context) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        if (!mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

    }

    public void close() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    public void open() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }


    public void selectItem(int position) {

        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }


    private void applyBusinessTime() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("shopId", CodeUtil.getShopId(this.getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this.getActivity()).post(Share.url_buiness_time, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                try {
                    handleBusiTime(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailre(String s) {

            }
        });


    }




    private void handleBusiTime(JSONObject jsonObject) throws JSONException {


        JSONArray weekArray = jsonObject.getJSONArray("week");
        int size = weekArray.length();
        //保存营业时间

        for (int i = 0; i < size; i++) {

            int position = weekArray.getInt(i);
            Status.busi_week[--position] = true;

        }


        JSONArray arrayTime = jsonObject.getJSONArray("time");
        size = arrayTime.length();
        for (int i = 0; i < size; i++) {
            JSONObject time = arrayTime.getJSONObject(i);
            String start = time.getString("startTime");
            String end = time.getString("endTime");

            Status.busiTime.add(start);
            Status.busiTime.add(end);

        }

        int rest=jsonObject.getInt("rest");
        if (rest==1){
            Status.isIsSupportRelaxTime=true;
        }else{
            Status.isIsSupportRelaxTime=false;
        }

        int foreStop=jsonObject.getInt("freStop");
        if (foreStop==1){
            Status.isForceStopBusiness=true;
        }else{
            Status.isForceStopBusiness=false;
        }



        Status.hasGetBusinessTime = true;

        if (Status.isForceStopBusiness){
            tv_status.setText("休息中");
            return;
        }

        switch (DateUtil.checkInBusinessTime()) {
            case 1:
                tv_status.setText("营业中");
                break;
            case 2:
                tv_status.setText("可接受预定单");
                break;
            case 3:
                tv_status.setText("休息中");
                break;
        }


    }


    public interface  NavigationDrawerCallbacks{
        void onNavigationDrawerItemSelected(int position);
    }





    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }



    private List<String> list_text = new ArrayList();
    private List<Integer> list_R = new ArrayList();

    private class NavigateAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list_text.size();
        }

        @Override
        public Object getItem(int i) {
            return list_text.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            // View mView=null;

            // if (NavigationDrawerFragment.this.getActivity()!=null){
            //   mView=LayoutInflater.from(NavigationDrawerFragment.this.getActivity()).inflate(R.layout.item_draw_list,null);
            //  }else{
            View mView = LayoutInflater.from(mContext).inflate(R.layout.item_drag_list, null);
            //  }


            ImageView imageView = (ImageView) mView.findViewById(R.id.image);
            TextView textView = (TextView) mView.findViewById(R.id.text);

            textView.setText(list_text.get(i));
            imageView.setImageResource(list_R.get(i));

            return mView;
        }
    }


}
