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
public class WaitOutAdapter extends OrderBaseAdapter {

    public WaitOutAdapter(Context context, List<OrderInfo> ls_data) {
        super(context, ls_data);
    }

    @Override
    protected void expandGenerate(final View view,final OrderInfo info , final int position) {


        /**
         * 仅判断是否在线付款
         *
         * 加载 打印  无效  标记送出 事件
         *
         *
         *
         */



        setColor(view, Color.parseColor("#ff99cc00"));
        ((TextView)view.findViewById(R.id.order_item_tv_date)).setText(info.date);
        ((TextView) view.findViewById(R.id.order_item_tv_status)).setText("订单已处理,制作中");


        if (!info.isPayOnline){
            ((TextView)view.findViewById(R.id.order_item_tv_paid_way)).setText("餐到付款");
            view.findViewById(R.id.order_item_iv_has_paid).setVisibility(View.GONE);
        }


        CodeUtil.collapseAndExpand(info,view);


        TextView tv_mark=(TextView)view.findViewById(R.id.order_item_tv_control_order);
        tv_mark.setText("标记餐送出");
        tv_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markOrderOut(info,position);
            }
        });


        view.findViewById(R.id.order_item_tv_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printOrder(info,position);
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
