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
public class TodayOrderAdapter extends OrderBaseAdapter {

    public TodayOrderAdapter(Context context, List<OrderInfo> ls_data) {
        super(context, ls_data);
    }

    @Override
    protected void expandGenerate(final View view, final OrderInfo info, final int position) {




        ((TextView)view.findViewById(R.id.order_item_tv_date)).setText(info.date);
        TextView tv_status=(TextView) view.findViewById(R.id.order_item_tv_status);
        TextView tv_valid=(TextView)view.findViewById(R.id.order_item_tv_invalid);



        CodeUtil.collapseAndExpand(info,view);



        //如果无效
        if (!info.isValid){

            setColor(view,mContext.getResources().getColor(R.color.order_another_color));
            tv_status.setText("订单已无效");

            view.findViewById(R.id.order_item_ll_yes_or_no).setVisibility(View.GONE);
            view.findViewById(R.id.order_item_ll_mark).setVisibility(View.GONE);

            //设置无效原因
            view.findViewById(R.id.order_item_tv_cause).setVisibility(View.VISIBLE);
            TextView tv_cause=(TextView)view.findViewById(R.id.order_item_tv_cause);
            tv_cause.setText(info.cause);

            tv_valid.setEnabled(false);

            //是否在线付款
            if (!info.isPayOnline){
                ((TextView)view.findViewById(R.id.order_item_tv_paid_way)).setText("餐到付款");
                view.findViewById(R.id.order_item_iv_has_paid).setVisibility(View.GONE);
                tv_status.setText("已处理");
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

        view.findViewById(R.id.order_item_ll_mark).setVisibility(View.VISIBLE);
        setColor(view, Color.parseColor("#ff99cc00"));
        view.findViewById(R.id.order_item_tv_cause).setVisibility(View.GONE);
        tv_valid.setEnabled(true);


        //如果订单有效，但没有在线付款
        if (!info.isPayOnline){
            ((TextView)view.findViewById(R.id.order_item_tv_paid_way)).setText("餐到付款");
            view.findViewById(R.id.order_item_iv_has_paid).setVisibility(View.GONE);
        }


        //
        TextView tv_control=(TextView)view.findViewById(R.id.order_item_tv_control_order);

        switch (info.status)
        {

            case 3:
                tv_control.setText("标记餐送出");
                tv_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        markOrderOut(info,position);
                    }
                });
                tv_status.setText("待配送");
                break;

            case 4:
                tv_control.setText(info.out_time+"送出");
                tv_status.setText("外卖已送出，待送达");
                tv_control.setEnabled(false);

                break;

            case 5:
                tv_control.setText("已完成");
                tv_status.setText("用户已确认收货");
                tv_valid.setEnabled(false);
                tv_control.setEnabled(false);
                break;

        }




        //打印订单
        view.findViewById(R.id.order_item_tv_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printOrder(info, position);
            }
        });


        if(tv_valid.isEnabled()) {
            //点击无效执行的方法
            tv_valid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (info.isValid) {
                        setOrderIvalid(info, position);
                    } else {
                        CodeUtil.toast(mContext, "此订单已为无效状态");
                    }
                }
            });
        }







    }



}
