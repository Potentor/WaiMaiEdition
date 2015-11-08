package com.mobileinternet.waimai.businessedition.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;

import java.util.List;

/**
 * Created by 海鸥2012 on 2015/7/29.
 */
public class NewOrderAdapter extends OrderBaseAdapter {


    public NewOrderAdapter(Context context, List<OrderInfo> ls_data) {
        super(context, ls_data);
    }


    @Override
    protected void expandGenerate(View view, final OrderInfo info, final int position) {

        /**
         * 仅判断是否在线付款
         *
         * 加载  无效  标记处理 事件
         *
         *
         *
         */


        //改变显示的颜色
        setColor(view, Color.RED);

        //头部显示
        ((TextView) view.findViewById(R.id.order_item_tv_date)).setText(info.date);
        ((TextView) view.findViewById(R.id.order_item_tv_status)).setText("新订单");
        view.findViewById(R.id.order_item_ll_yes_or_no).setVisibility(View.GONE);
        view.findViewById(R.id.order_item_tv_cause).setVisibility(View.GONE);


        CodeUtil.collapseAndExpand(info, view);


        //分享按钮
        view.findViewById(R.id.order_item_ll_share).setVisibility(View.GONE);


        //如果没有在线付款
        if (!info.isPayOnline) {
            ((TextView) view.findViewById(R.id.order_item_tv_paid_way)).setText("餐到付款");
            //已付款标志
            view.findViewById(R.id.order_item_iv_has_paid).setVisibility(View.GONE);
        }


        //打印本单按钮
        view.findViewById(R.id.order_item_tv_print).setVisibility(View.GONE);


        TextView tv_control=(TextView)view.findViewById(R.id.order_item_tv_control_order);
        tv_control.setText("处理");
        tv_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOrder(info,position);
            }
        });


        //点击无效执行的方法
        view.findViewById(R.id.order_item_tv_invalid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOrderIvalid(info,position);
            }
        });


    }



}
