package com.mobileinternet.waimai.businessedition.fragment.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.activity.Acount.AcountActivity;
import com.mobileinternet.waimai.businessedition.activity.Acount.FinanceActivity;
import com.mobileinternet.waimai.businessedition.activity.Acount.MsgActivity;
import com.mobileinternet.waimai.businessedition.activity.Acount.RefundActivity;
import com.mobileinternet.waimai.businessedition.activity.Acount.SettleInfoActivity;
import com.mobileinternet.waimai.businessedition.activity.Acount.StatisticsActivity;
import com.mobileinternet.waimai.businessedition.activity.LoginActivity;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.util.PollingUtil;
import com.mobileinternet.waimai.businessedition.view.smartiv.SmartImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class AcountFragment extends Fragment {

    public static String phone="";

    public static Handler handler;


    private TextView tv_status;


    public static AcountFragment newInstance() {
        AcountFragment fragment = new AcountFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        AcountFragment.handler=new Mhandler();

        View view = inflater.inflate(R.layout.fragment_acount, container, false);

        //点击我的账户
        view.findViewById(R.id.acount_acount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(AcountFragment.this.getActivity(), AcountActivity.class);
                startActivityForResult(intent, 100);

            }
        });

        //点击财务中心
        view.findViewById(R.id.acount_financeInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AcountFragment.this.getActivity(), FinanceActivity.class);
                startActivity(intent);

            }
        });


        //点击系统消息
        view.findViewById(R.id.acount_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AcountFragment.this.getActivity(), MsgActivity.class);
                startActivity(intent);
            }
        });


        //点击退款申请
        view.findViewById(R.id.acount_refund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AcountFragment.this.getActivity(), RefundActivity.class);
                startActivity(intent);

            }
        });


        //点击结算信息
        view.findViewById(R.id.acount_settleInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AcountFragment.this.getActivity(), SettleInfoActivity.class);
                startActivity(intent);

            }
        });

        //点击营业统计
        view.findViewById(R.id.acount_statistics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AcountFragment.this.getActivity(), StatisticsActivity.class);
                startActivity(intent);
            }
        });


        //退出账号
        view.findViewById(R.id.acount_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        IApplication iApplication=(IApplication)getActivity().getApplication();

        SmartImageView iv_logo=(SmartImageView)view.findViewById(R.id.acount_iv_logo);

        iv_logo.setImageUrl(iApplication.getData(Share.logo));

        TextView tv_acount=(TextView)view.findViewById(R.id.acount_name);
        tv_acount.setText(iApplication.getData(Share.user_name));

        tv_status=(TextView)view.findViewById(R.id.acount_phone_status);
        //tv_status.setText("");


        if (CodeUtil.checkNetState(this.getActivity())){
            fetchData();
        }

        return view;
    }


    /**
     * 退出账号
     */
    private void signOut(){
        SharedPreferences.Editor editor=getActivity().getSharedPreferences(Share.share_prefrence_name, Activity.MODE_PRIVATE).edit();
        editor.putBoolean(Share.isLogOut, true);
        editor.commit();

        //防止因静态数据因缓存在内存，不能及时清除，导致可以重新登录
        Status.setIsConfigFileDamage(true);


        startActivity(new Intent(this.getActivity(), LoginActivity.class));
        this.getActivity().finish();

    }



    private void fetchData(){

        JSONObject jsonObject=new JSONObject();
        IApplication iApplication=(IApplication)getActivity().getApplication();
        try {
            jsonObject.put("userId",iApplication.getData(Share.user_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final Dialog dialog=DialogUtil.showProgressDialog(this.getActivity(),"加载中...");

        new HttpUtil(this.getActivity()).post(Share.url_acount_info, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();

                if (!isOk)
                    return;


                String phone=null;
                try {
                    phone=jsonObject.getString("phone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ("".equals(phone)) {
                    tv_status.setText("未绑定");
                    AcountFragment.phone = "未绑定";
                } else {
                        tv_status.setText(phone);
                        AcountFragment.phone = phone;
                }


            }

            @Override
            public void onFailre(String s) {
                    dialog.dismiss();
            }
        });


    }




    public class Mhandler extends Handler{
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            if (msg.what==200){
                fetchData();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        tv_status.setText(AcountFragment.phone);
    }
}
