package com.mobileinternet.waimai.businessedition.fragment.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.activity.Dining.MyDiningActivity;
import com.mobileinternet.waimai.businessedition.activity.Dining.ShopIndexActivity;
import com.mobileinternet.waimai.businessedition.activity.Dining.PreOrderSuActivity;
import com.mobileinternet.waimai.businessedition.activity.Dining.ShopCommentActivity;
import com.mobileinternet.waimai.businessedition.activity.Dining.ShopNoticeActivity;
import com.mobileinternet.waimai.businessedition.activity.Dining.ShopStatusActivity;
import com.mobileinternet.waimai.businessedition.activity.Dining.QuanlityActivity;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.smartiv.SmartImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class DingFragment extends Fragment {


    public static Handler handler;


    private JSONObject postObject;


    private View ll_qualitification;
    private SmartImageView iv_logo;
    private TextView tv_shop_name;
    private TextView tv_acount_name;
    private TextView tv_comment;
    private TextView tv_jidan;
    private TextView tv_oot;
    private TextView tv_cuidan;
    private TextView tv_jidan_total;
    private TextView tv_oot_total;
    private TextView tv_cuidan_total;
    private TextView tv_status;
    private TextView tv_support;


    private boolean isSupportTest;
    private boolean isSupportBusiness;





    private void fetchData() {


       final Dialog dialog=DialogUtil.showProgressDialog(this.getActivity(),"加载中");
        new HttpUtil(this.getActivity()).post(Share.url_dining_info, postObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject,boolean isOk) {

                    dialog.dismiss();

                if (!isOk)
                    return;

                try {
                    freightUI(jsonObject);
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


    private void freightUI(JSONObject jsonObject) throws JSONException {


        IApplication iApplication = (IApplication) getActivity().getApplication();
        tv_acount_name.setText(iApplication.getData(Share.user_name));
        tv_shop_name.setText(iApplication.getData(Share.shop_name));
        iv_logo.setImageUrl(iApplication.getData(Share.logo));

        int totalOrder = jsonObject.getInt("allorder");
        int reminder = jsonObject.getInt("rmdOr");
        int oot = jsonObject.getInt("orNumOt");
        int jiedan=jsonObject.getInt("orderNum");

        int restSupport=jsonObject.getInt("restSupport");
        int busSpport = jsonObject.getInt("busSupport");
        int idenStatus = jsonObject.getInt("idenStatus");


        int score=jsonObject.getInt("score");

        tv_cuidan_total.setText(totalOrder + "");
        tv_jidan_total.setText(totalOrder + "");
        tv_oot_total.setText(totalOrder + "");

        tv_jidan.setText(jiedan + "");
        tv_oot.setText(oot + "");
        tv_cuidan.setText(reminder + "");


        tv_comment.setText(score+"");


        if (Status.isForceStopBusiness){
            tv_status.setText("休息中");
        }else {
            switch (DateUtil.checkInBusinessTime()) {
                case 3:
                    tv_status.setText("休息中");
                    break;
                case 1:
                    tv_status.setText("营业中");
                    break;
                case 2:
                    tv_status.setText("可接受预定单");
                    break;
            }
        }




        if (idenStatus == 1) {
            ll_qualitification.setVisibility(View.GONE);
        }


        String relax = null, business = null;

        if (restSupport != 0) {
            relax = "休息中";
            isSupportTest=true;
        }else{
            isSupportTest=false;
        }

        if (busSpport != 0) {
            business = "营业中";
            isSupportBusiness=true;
        }else{
            isSupportBusiness=false;
        }


        if (relax == null && business == null) {
            tv_support.setText("");
            return;
        }


        if (relax != null && business == null) {
            tv_support.setText(relax);
            return;
        }

        if (relax != null && business != null) {
            tv_support.setText(relax + "/" + business);
            return;
        }

        tv_support.setText(business);


    }


    public static DingFragment newInstance() {
        DingFragment fragment = new DingFragment();
        return fragment;
    }

    public DingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        DingFragment.handler = new MHandler();

        //实例化组件

        ll_qualitification = view.findViewById(R.id.shop_qualification);
        iv_logo = (SmartImageView) view.findViewById(R.id.shop_iv_logo);
        tv_shop_name = (TextView) view.findViewById(R.id.shop_tv_shop_name);
        tv_acount_name = (TextView) view.findViewById(R.id.shop_tv_acount_name);
        tv_comment = (TextView) view.findViewById(R.id.shop_tv_comment);
        tv_jidan = (TextView) view.findViewById(R.id.shop_index_tv_jidan);
        tv_oot = (TextView) view.findViewById(R.id.shop_index_tv_oot);
        tv_cuidan = (TextView) view.findViewById(R.id.shop_index_tv_cuidan);
        tv_jidan_total = (TextView) view.findViewById(R.id.shop_index_tv_total1);
        tv_oot_total = (TextView) view.findViewById(R.id.shop_index_tv_total2);
        tv_cuidan_total = (TextView) view.findViewById(R.id.shop_index_tv_total3);
        tv_status = (TextView) view.findViewById(R.id.shop_tv_status);
        tv_support = (TextView) view.findViewById(R.id.shop_tv_support);

        initViewEvent(view);


        postObject = new JSONObject();
        IApplication iApplication = (IApplication) getActivity().getApplication();
        try {
            postObject.put("shopId", iApplication.getData(Share.shop_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (CodeUtil.checkNetState(this.getActivity())) {
            fetchData();
        }

    }

    private void initViewEvent(View view) {


        //认证提醒
        view.findViewById(R.id.shop_qualification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DingFragment.this.getActivity(), QuanlityActivity.class);
                startActivity(intent);

            }
        });


        //我的dining
        view.findViewById(R.id.shop_myacount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DingFragment.this.getActivity(), MyDiningActivity.class);
                startActivity(intent);
            }
        });

        //餐厅评价
        view.findViewById(R.id.shop_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DingFragment.this.getActivity(), ShopCommentActivity.class);
                intent.putExtra("score",tv_comment.getText().toString());
                startActivity(intent);
            }
        });


        //餐厅指数
        view.findViewById(R.id.shop_index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DingFragment.this.getActivity(), ShopIndexActivity.class);
                startActivity(intent);
            }
        });


        //餐厅营业状态
        view.findViewById(R.id.shop_status_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DingFragment.this.getActivity(), ShopStatusActivity.class);
                startActivityForResult(intent, 120);
            }
        });


        //餐厅预订单支持
        view.findViewById(R.id.shop_support_pre_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DingFragment.this.getActivity(), PreOrderSuActivity.class);
                intent.putExtra("test",isSupportTest);
                intent.putExtra("business",isSupportBusiness);
                startActivityForResult(intent, 100);
            }
        });


        //餐厅公告
        view.findViewById(R.id.shop_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DingFragment.this.getActivity(), ShopNoticeActivity.class);
                startActivity(intent);
            }
        });


//        //我的门店
//        view.findViewById(R.id.shop_myshop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(ShopFragment.this.getActivity(), MyShopActivity.class);
//                startActivity(intent);
//            }
//        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != 200)
            return;

        //预订单支持修改
        if (requestCode == 100) {

            String relax = null, business = null;

            if (data.getBooleanExtra("relax", false)) {
                relax = "休息中";
                isSupportTest=true;
            }else{
                isSupportTest=false;
            }

            if (data.getBooleanExtra("business", false)) {
                business = "营业中";
                isSupportBusiness=true;
            }else{
                isSupportBusiness=false;
            }

            if (relax == null && business == null) {
                tv_support.setText("");
                return;
            }


            if (relax != null && business == null) {
                tv_support.setText(relax);
                return;
            }

            if (relax != null && business != null) {
                tv_support.setText(relax + "/" + business);
                return;
            }

            tv_support.setText(business);

            return;
        }


        if (requestCode == 120) {



            if (Status.isForceStopBusiness){
                tv_status.setText("休息中");
            }else{

                int status=data.getIntExtra("status",0);
                switch (status) {
                    case 3:
                        tv_status.setText("休息中");
                        break;
                    case 2:
                        tv_status.setText("预定中");
                        break;
                    case 1:
                        tv_status.setText("营业中");
                        break;
                }
            }



        }

    }


    public class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            if (msg.what == 200) {

                fetchData();

            }


        }
    }
}
