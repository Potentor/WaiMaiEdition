package com.mobileinternet.waimai.businessedition.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;

import java.util.List;

/**
 * Created by 海鸥2012 on 2015/7/29.
 */
public class HisOrderAdapter extends OrderBaseAdapter {


    public HisOrderAdapter(Context context, List<OrderInfo> ls_data) {
        super(context, ls_data);
    }

    @Override
    protected void expandGenerate(View view, final OrderInfo info, final int position) {


        ((TextView)view.findViewById(R.id.order_item_tv_date)).setText(info.date);
        TextView tv_status=(TextView) view.findViewById(R.id.order_item_tv_status);
        /**
         * 折叠展开
         */

        CodeUtil.collapseAndExpand(info,view);

        if (!info.isValid){

            setColor(view, Color.parseColor("#444547"));

            
            tv_status.setText("订单已无效");
            view.findViewById(R.id.order_item_ll_mark).setVisibility(View.GONE);
            view.findViewById(R.id.order_item_tv_cause).setVisibility(View.VISIBLE);
            TextView tv_cause=(TextView)view.findViewById(R.id.order_item_tv_cause);
            tv_cause.setText(info.cause);


            if (!info.isPayOnline){
                ((TextView)view.findViewById(R.id.order_item_tv_paid_way)).setText("餐到付款");
                ((ImageView)view.findViewById(R.id.order_item_iv_has_paid)).setImageResource(R.drawable.img_refunded);
            }else {

                switch (info.refundStatus)
                {
                    case 1:
                        //退款中
                        view.findViewById(R.id.order_item_iv_has_paid).setVisibility(View.INVISIBLE);
                        tv_status.setText("退款中");
                        break;
                    case 2:
                        //退款成功
                        ((ImageView)view.findViewById(R.id.order_item_iv_has_paid)).setImageResource(R.drawable.img_refunded);
                        tv_status.setText("已退款");
                        break;
                    case 0:
                        //未处理
                        view.findViewById(R.id.order_item_iv_has_paid).setVisibility(View.INVISIBLE);
                        tv_status.setText("新退款申请");
                        //view.findViewById(R.id.order_item_ll_yes_or_no).setVisibility(View.VISIBLE);
                        break;
                }
            }

            return;


        }


        setColor(view, Color.parseColor("#ff99cc00"));


        if (!info.isPayOnline){
            ((TextView)view.findViewById(R.id.order_item_tv_paid_way)).setText("餐到付款");
            view.findViewById(R.id.order_item_iv_has_paid).setVisibility(View.GONE);
        }


        view.findViewById(R.id.order_item_tv_cause).setVisibility(View.GONE);


        //
        TextView tv_control=(TextView)view.findViewById(R.id.order_item_tv_control_order);

        switch (info.status)
        {

            case 4:
                tv_control.setText("已送出");
                tv_status.setText("外卖已送出，待送达");
                break;

            case 5:
                tv_control.setText(info.out_time+"送出");
                tv_status.setText("用户已确认收货");
                break;

        }




        //打印订单
        view.findViewById(R.id.order_item_tv_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printOrder(info,position);
            }
        });






    }




}
