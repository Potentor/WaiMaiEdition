package com.mobileinternet.waimai.businessedition.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.activity.Acount.MsgActivity;
import com.mobileinternet.waimai.businessedition.activity.Acount.RefundActivity;
import com.mobileinternet.waimai.businessedition.activity.Dish.CatAddActivity;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.fragment.TodayOrder.HandledFragment;
import com.mobileinternet.waimai.businessedition.fragment.TodayOrder.WaitFragment;
import com.mobileinternet.waimai.businessedition.fragment.main.UsFragment;
import com.mobileinternet.waimai.businessedition.fragment.main.AcountFragment;
import com.mobileinternet.waimai.businessedition.fragment.main.DishFragment;
import com.mobileinternet.waimai.businessedition.fragment.NavigationDrawerFragment;
import com.mobileinternet.waimai.businessedition.fragment.TodayOrder.NewFragment;
import com.mobileinternet.waimai.businessedition.fragment.main.OrderFragment;
import com.mobileinternet.waimai.businessedition.fragment.main.SettingFragment;
import com.mobileinternet.waimai.businessedition.fragment.main.DingFragment;
import com.mobileinternet.waimai.businessedition.fragment.main.TDOdrFragment;
import com.mobileinternet.waimai.businessedition.service.NetStateService;
import com.mobileinternet.waimai.businessedition.service.PollingService;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.util.PollingUtil;
import com.mobileinternet.waimai.businessedition.view.reddot.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    public static Handler handler;


    //今日订单fragment对应的tag
    private final String TDORDER_TAG="TODAY";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ActionBar mActionBar;

    private BadgeView bv_refund;
    private BadgeView bv_msg;
    private TextView tv_refund;
    private TextView tv_msg;
    private EditText et_search;
    private ImageView iv_search;

    private ImageView iv_refresh_acount;
    private ImageView iv_refresh_orders;
    private ImageView iv_refresh_shop;

    private TextView tv_add;






    private void showViewOfActionBarByPosition(int position){
        hideAllViewInActionBar();
        switch (position)
        {
            case 1:

                iv_search.setVisibility(View.VISIBLE);
                if ((Boolean)et_search.getTag()){
                    et_search.setVisibility(View.VISIBLE);
                    bv_msg.setVisibility(View.GONE);
                    bv_refund.setVisibility(View.GONE);
                }else{
                    tv_refund.setVisibility(View.VISIBLE);
                    tv_msg.setVisibility(View.VISIBLE);
                    bv_msg.setVisibility(View.VISIBLE);
                    bv_refund.setVisibility(View.VISIBLE);


                }
                break;
            case 2:
                iv_refresh_shop.setVisibility(View.VISIBLE);
                break;
            case 3:
                tv_add.setVisibility(View.VISIBLE);
                break;
            case 4:
                iv_refresh_acount.setVisibility(View.VISIBLE);
                break;
            case 5:
                iv_refresh_orders.setVisibility(View.VISIBLE);
                break;

        }
    }

    /**
     * 隐藏ActionBar上所有的view
     */
    private void hideAllViewInActionBar(){
        tv_add.setVisibility(View.GONE);
        tv_msg.setVisibility(View.GONE);
        tv_refund.setVisibility(View.GONE);
        iv_refresh_acount.setVisibility(View.GONE);
        iv_refresh_orders.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        iv_refresh_shop.setVisibility(View.GONE);
        et_search.setVisibility(View.GONE);
    }





    /*
   *
   * 接收新订单广播
   * */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Share.receivedMsg)) {
                boolean tokenAlive=intent.getBooleanExtra("token",true);
                if (!tokenAlive){


                    SharedPreferences.Editor editor=getSharedPreferences(Share.share_prefrence_name, Activity.MODE_PRIVATE).edit();
                    editor.putBoolean(Share.isLogOut, true);
                    editor.commit();

                    //防止因静态数据因缓存在内存，不能及时清除，导致可以重新登录
                    Status.setIsConfigFileDamage(true);

                    //停止轮询
                    PollingUtil.stopPollingService(MainActivity.this);

                    Dialog dialog=new AlertDialog.Builder(MainActivity.this)
                            .setTitle("警告")
                            .setMessage("你的账号在别处登录，若不是本人操作，请尽快修改密码")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    MainActivity.this.finish();
                                    dialog.dismiss();
                                }
                            })
                            .create();

                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.show();
                    return;
                }


                boolean newOrder=intent.getBooleanExtra("newOrder", false);
                int msg=intent.getIntExtra("msg", 0);
                int refund=intent.getIntExtra("refund",0);
                int cancle[]=intent.getIntArrayExtra("cancle");



                //消息
                if (msg>0){
                    bv_msg.setText(msg+"");
                    bv_msg.show();
                }

                //退款申请消息
                if (refund>0){
                    bv_refund.setText(refund+"");
                    bv_refund.show();
                }

                //取消订单消息
                if (cancle[0]!=-1){

                    StringBuilder builder=new StringBuilder();
                    for (int i=0;i<cancle.length;i++){
                        builder.append(cancle[i]+" ");
                    }

                    builder.append("订单已被用户取消，请不要再配送");
                    DialogUtil.showMessage(MainActivity.this, "警告", builder.toString());


                    Message message=new Message();
                    message.what=201;
                    message.obj=cancle;
                    NewFragment.mHanlder.sendMessage(message);

                }


                //新订单
                if (newOrder) {
                    NewFragment.mHanlder.sendEmptyMessage(202);
                }

                abortBroadcast();

                return;
            }



            if (action.equals(Share.receiveApplayBusinessTime)){
                applyBusinessTime();
                abortBroadcast();
                return;
            }
        }
    };









    private void initView(){

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        mActionBar=getSupportActionBar();
        mActionBar.setTitle("今日订单");
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);


        TDOdrFragment fragment=TDOdrFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container,fragment,TDORDER_TAG).commit();



        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),this);
        mNavigationDrawerFragment.close();



        //注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Share.receivedMsg);
        intentFilter.addAction(Share.receiveApplayBusinessTime);
        registerReceiver(mReceiver, intentFilter);



        //打开监听网络状态变化监听器
        Intent intent=new Intent(this, NetStateService.class);
        startService(intent);


        //启动轮询服务
        PollingUtil.startPollingService(this);


    }




    @Override
    public void onNavigationDrawerItemSelected(int position) {
//
//        if (position==0) {
//
//            return;
//        }
//        if (position==8){
//            finish();
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragmentList=fragmentManager.getFragments();

        if (fragmentList==null){
            return;
        }

        for(Fragment fragment:fragmentList){

            if (fragment==null)
                continue;

            if (TDORDER_TAG.equals(fragment.getTag()))
                continue;

            fragmentManager.beginTransaction().remove(fragment).commit();

        }

        Fragment fragment=null;

        switch (position)
        {

            case 1:
                mActionBar.setTitle("今日订单");
                showViewOfActionBarByPosition(1);
              //  fragment= TDOdrFragment.newInstance();
                break;
            case 2:
                mActionBar.setTitle("餐厅管理");
                showViewOfActionBarByPosition(2);
                fragment= DingFragment.newInstance();
                break;
            case 3:
                mActionBar.setTitle("菜品管理");
                showViewOfActionBarByPosition(3);
                fragment= DishFragment.newInstance();
                break;
            case 0:
            case 4:
                mActionBar.setTitle("账户中心");
                showViewOfActionBarByPosition(4);
                fragment= AcountFragment.newInstance();
                break;
            case 5:
                mActionBar.setTitle("订单中心");
                showViewOfActionBarByPosition(5);
                fragment= OrderFragment.newInstance();
                break;
            case 6:
                mActionBar.setTitle("设置/打印机");
                showViewOfActionBarByPosition(6);
                fragment= SettingFragment.newInstance();
                break;
            case 7:
                mActionBar.setTitle("关于我们");
                showViewOfActionBarByPosition(7);
                fragment= UsFragment.newInstance();
                break;

        }

       if (fragment!=null){

            fragmentManager.beginTransaction().add(R.id.container,fragment,"other").commit();
        }

    }




    private void initMenu(Menu menu){

        View view=LayoutInflater.from(this).inflate(R.layout.menu_option,null);
        MenuItem item=  menu.findItem(R.id.action_menu);
        item.setActionView(view);


        //获取之前用户是否有消息没有查看情况
        SharedPreferences preferences=getSharedPreferences(
                Share.share_prefrence_name,
                Activity.MODE_PRIVATE);

        int int_refund=preferences.getInt(Share.config_has_refund, 0);
        int int_msg=preferences.getInt(Share.config_has_msg, 0);

        et_search=(EditText)view.findViewById(R.id.menu_item_et);
        ViewGroup.LayoutParams params=et_search.getLayoutParams();
        params.width=getWindowManager().getDefaultDisplay().getWidth()/5*3;
        et_search.setLayoutParams(params);
        et_search.setTag(false);


        //待退款
        tv_refund=(TextView)view.findViewById(R.id.menu_item_refund);
        tv_refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bv_refund.hide();
                Intent intent = new Intent(MainActivity.this, RefundActivity.class);
                startActivity(intent);
                SharedPreferences.Editor editor = getSharedPreferences(
                        Share.share_prefrence_name,
                        Activity.MODE_PRIVATE).edit();

                editor.putInt(Share.config_has_refund, 0);
                editor.commit();

            }
        });



        //退款提示红点
        bv_refund=new BadgeView(this,view.findViewById(R.id.menu_item_refund));
        if (int_refund>0) {
            bv_refund.show();
            bv_refund.setText(""+int_refund);
        }



        //系统消息
        tv_msg=(TextView)view.findViewById(R.id.menu_item_msg);
        tv_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bv_msg.hide();
                Intent intent = new Intent(MainActivity.this, MsgActivity.class);
              //  intent.putExtra("show",1);
                startActivity(intent);
                SharedPreferences.Editor editor = getSharedPreferences(
                        Share.share_prefrence_name,
                        Activity.MODE_PRIVATE).edit();

                editor.putInt(Share.config_has_msg, 0);
                editor.commit();
            }
        });

        //消息红点
        bv_msg=new BadgeView(this,view.findViewById(R.id.menu_item_msg));
        if (int_msg>0) {
            bv_msg.show();
            bv_msg.setText(""+int_msg);
        }


        //添加分类
        tv_add=(TextView)view.findViewById(R.id.menu_item_add);
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CatAddActivity.class);
                startActivityForResult(intent,120);


            }
        });


        //账户中心刷新
        iv_refresh_acount=(ImageView)view.findViewById(R.id.menu_item_refresh_acount);

        iv_refresh_acount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CodeUtil.checkNetState(MainActivity.this)) {
                    AcountFragment.handler.sendEmptyMessage(200);
                }

            }
        });


        //餐厅管理刷新
        iv_refresh_shop=(ImageView)view.findViewById(R.id.menu_item_refresh_shop);
        iv_refresh_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DingFragment.handler.sendEmptyMessage(200);
            }
        });


        //订单中心刷新
        iv_refresh_orders=(ImageView)view.findViewById(R.id.menu_item_refresh_orders);
        iv_refresh_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OrderFragment.handler.sendEmptyMessage(200);


            }
        });



        //搜索
        iv_search=(ImageView)view.findViewById(R.id.menu_item_search);
        iv_search.setImageResource(R.drawable.ic_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_search.getVisibility()== View.VISIBLE) {
                    //关闭搜索
                    et_search.setVisibility(View.GONE);
                    tv_msg.setVisibility(View.VISIBLE);
                    tv_refund.setVisibility(View.VISIBLE);
                    iv_search.setImageResource(R.drawable.ic_search);
                    et_search.setTag(false);
                    et_search.setText("");
                    search("close");

                }else{
                    //开始搜索
                    et_search.setVisibility(View.VISIBLE);
                    tv_refund.setVisibility(View.GONE);
                    tv_msg.setVisibility(View.GONE);
                    iv_search.setImageResource(R.drawable.abc_ic_clear_normal);
                    et_search.setTag(true);
                }

            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("".equals(s)){

                    search("close");
                }else {
                    search(s.toString());
                }
            }
        });



    }


    private void search(String number){

        TDOdrFragment tdOdrFragment=(TDOdrFragment)getSupportFragmentManager().findFragmentByTag(TDORDER_TAG);
        int postion=tdOdrFragment.getSelectPosition();

        Message message=new Message();
        message.obj=number;
        message.what=300;


        switch (postion)
        {
            case 0:
                NewFragment.mHanlder.sendMessage(message);
                break;
            case 1:
                WaitFragment.mHanlder.sendMessage(message);
                break;
            case 2:
                HandledFragment.mHanlder.sendMessage(message);
                break;
        }

    }


    private void applyBusinessTime() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("shopId", CodeUtil.getShopId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_buiness_time, jsonObject, new HttpUtil.OnHttpListener() {
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


        Message message=new Message();
        message.what=201;


        if (Status.isForceStopBusiness){

            message.arg1=3;

        }else {
            switch (DateUtil.checkInBusinessTime()) {
                case 1:
                    message.arg1 = 1;
                    break;
                case 2:
                    message.arg1 = 2;
                    break;
                case 3:
                    message.arg1 = 3;
                    break;
            }
        }

        NavigationDrawerFragment.mHandler.sendMessage(message);



    }








    @Override
    public boolean onSupportNavigateUp() {
        if (mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.close();
        }else {
            mNavigationDrawerFragment.open();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);

        initMenu(menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==120){
            DishFragment fragment=(DishFragment)getSupportFragmentManager().findFragmentByTag("other");
            fragment.onActivityResult(requestCode,resultCode,data);
        }
    }



    @Override
    protected void onDestroy() {


        stopService(new Intent(this, NetStateService.class));
        unregisterReceiver(mReceiver);
        PollingUtil.stopPollingService(this);
        stopService(new Intent(this, PollingService.class));

        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }



    private boolean isTurnOff=false;
    @Override
    public void onBackPressed() {




        if(!mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.open();
            return;
        }else {

            if (!isTurnOff) {

                isTurnOff=true;
                Toast.makeText(this, "再次按下返回按钮退出应用", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isTurnOff=false;
                    }
                }, 1000);

            }else{
                finish();
            }
        }



    }
}
