package com.mobileinternet.waimai.businessedition.fragment.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class UsFragment extends Fragment {


    private TextView tv_version;
    private TextView tv_phone;
    private String number;

    public static UsFragment newInstance() {
        UsFragment fragment = new UsFragment();
        return fragment;
    }

    public UsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_about, container, false);
        initView(view);
        return view;

    }

    private void initView(View view){


        //联系我们
        view.findViewById(R.id.we_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (number==null)
                    return;

                Dialog dialog=new AlertDialog.Builder(UsFragment.this.getActivity())
                        .setMessage("将要向" + number + "拨打电话")
                        .setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                                UsFragment.this.getActivity().startActivity(intent);
                            }
                        }).create();

                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

            }
        });


        //使用说明
        view.findViewById(R.id.we_describe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //意见反馈啊
        view.findViewById(R.id.we_reback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //检查更新
        view.findViewById(R.id.we_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tv_version=(TextView)view.findViewById(R.id.we_version);
        tv_phone=(TextView)view.findViewById(R.id.we_phone);

        if (CodeUtil.checkNetState(this.getActivity())){
            fetchData();
        }


    }


    private void fetchData(){

        final Dialog dialog= DialogUtil.showProgressDialog(this.getActivity(),"加载中...");

        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this.getActivity());

        new HttpUtil(this.getActivity()).post(Share.url_custom_phone,jsonObject,new HttpUtil.OnHttpListener(){
            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }

            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();

                if (!isOk)
                    return;

                try {
                   number= jsonObject.getString("phone");
                    tv_phone.setText(number);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

}
