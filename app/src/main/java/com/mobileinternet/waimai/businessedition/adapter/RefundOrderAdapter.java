package com.mobileinternet.waimai.businessedition.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;

import java.util.List;

/**
 * Created by 海鸥2012 on 2015/7/29.
 */
public class RefundOrderAdapter extends OrderBaseAdapter {


    public RefundOrderAdapter(Context context, List<OrderInfo> ls_data) {
        super(context, ls_data);


    }

    @Override
    protected void expandGenerate(View view, final OrderInfo info, final int position) {

        view.findViewById(R.id.order_item_ll_share).setVisibility(View.GONE);
        view.findViewById(R.id.order_item_ll_mark).setVisibility(View.GONE);

        view.findViewById(R.id.order_item_tv_cause).setVisibility(View.VISIBLE);



        ((TextView)view.findViewById(R.id.order_item_tv_date)).setText("订单号:" + info.id);
        ((TextView)view.findViewById(R.id.order_item_tv_cause)).setText(info.cause);



        setColor(view, mContext.getResources().getColor(R.color.order_another_color));
        TextView tv_status=(TextView)view.findViewById(R.id.order_item_tv_status);


        /**
         * 折叠展开
         */
        CodeUtil.collapseAndExpand(info,view);

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
                view.findViewById(R.id.order_item_ll_yes_or_no).setVisibility(View.VISIBLE);
                break;
        }


        if (info.refundStatus==0) {

            view.findViewById(R.id.order_item_tv_agree).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    agreeRefund(info, position);
                }
            });
            view.findViewById(R.id.order_item_tv_refuse).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disAgreeRefund(info, position);
                }
            });
        }else{
            view.findViewById(R.id.order_item_ll_yes_or_no).setVisibility(View.GONE);
        }



    }





}
