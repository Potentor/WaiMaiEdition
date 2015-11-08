package com.mobileinternet.waimai.businessedition.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  OrderBaseAdapter是所有关于订单的基模板，
 *
 *  在此基础上通过扩展 expandGenerate虚函数，来达到各种不同类型订单的不同。
 *
 *  以下适配器，是基于此适配器扩展而来
 *
 *  HisOrderAdapter   //昨天、前天订单，所有适配器
 *
 *  NewOrderAdapter   //新订单，所有适配器
 *
 *  RefundOrderAdapter  //退款中，未处理退款、已处理退款订单，所有适配器
 *
 *  TodayOrderAdapter   //今日订单（订单中心fragement中），已处理今日订单（今日订单fragment中），所有适配器
 *
 *  WaitOutAdapter      //待配送订单，所用适配器
 *
 *
 *
 *
 * Created by 海鸥2012 on 2015/7/28.
 */
public abstract class OrderBaseAdapter extends BaseAdapter {

    protected List<OrderInfo> ls_data;
    protected Context mContext;
    protected OnNewOrderListener mOnNewOrderListener;
    protected OnNumalListner mOnNumalListener;
    protected OnRefundOrderListener mOnRefundOrderListener;


//    private int type=0;    //0 新订单   1.待配送订单  2.已处理订单  3.新退款订单  4.已处理退款订单
//                            //5.昨日，前天订单


    public OrderBaseAdapter(Context context, List<OrderInfo> ls_data) {
        this.mContext = context;
        this.ls_data = ls_data;
    }


    /**
     * 添加数据
     *
     * @param extra
     */
    public void addData(List<OrderInfo> extra) {
        this.ls_data.addAll(ls_data.size(), extra);
        this.notifyDataSetChanged();
    }


    /**
     * 改变适配器的数据源
     *
     * 主要用于：解决使用搜索功能时，根据输入的电话号码，过滤不符合搜索条件的订单
     *
     *
     * @param ls_data
     */
    public void changeData(List<OrderInfo> ls_data){
        this.ls_data=ls_data;
    }


    /**
     * 适配器原来getView()方法的实现
     *
     * @param view
     * @param position
     * @return
     */
    private View generateView(View view, int position) {


        LayoutInflater inflater = LayoutInflater.from(mContext);


        /**
         * 如果当前显示的view不为空，则只改变上面的数据和组件的显隐
         * 不再重新实例化一个布局实例
         */
        if (view == null) {

            view = inflater.inflate(R.layout.item_order, null);
        }


        /**
         * 获取当前位置的订单数据
         */
        final OrderInfo info = ls_data.get(position);


        ((TextView) view.findViewById(R.id.order_item_tv_serial)).setText(info.serial + "号");
        if (info.isAnony) {
            ((TextView) view.findViewById(R.id.order_item_tv_user)).setText("匿名");
        } else {
            ((TextView) view.findViewById(R.id.order_item_tv_user)).setText(info.name + "(" + info.sex + ")");
        }
        ((TextView) view.findViewById(R.id.order_item_tv_phone)).setText(info.phone);
        ((TextView) view.findViewById(R.id.order_item_tv_address)).setText(info.address);
        ((TextView) view.findViewById(R.id.order_item_tv_money)).setText(info.money + "元");



        LinearLayout ll_dish = (LinearLayout) view.findViewById(R.id.order_item_ll_dishes);
        ll_dish.removeAllViews();
        int size = info.ls_dish.size();

        //加载菜品
        for (int i = 0; i < size; i++) {

            DisheInfo disheInfo = info.ls_dish.get(i);
            View dish_view = inflater.inflate(R.layout.view_order_dish, null);
            ((TextView) dish_view.findViewById(R.id.order_item_tv_dish_name)).setText(disheInfo.name);
            ((TextView) dish_view.findViewById(R.id.order_item_tv_dish_num)).setText("X" + disheInfo.num);
            ((TextView) dish_view.findViewById(R.id.order_item_tv_dish_price)).setText(disheInfo.price + "元");
            ll_dish.addView(dish_view, ll_dish.getChildCount());

        }

        LinearLayout ll_present = (LinearLayout) view.findViewById(R.id.order_item_ll_other_outlay);


        //加载活动
        size = info.ls_present.size();

       // int childCount=ll_present.getChildCount();

        ll_present.removeViews(1,ll_present.getChildCount()-1);
        if (size==0) {

            View present_view = inflater.inflate(R.layout.view_order_present, null);
            ((TextView) present_view.findViewById(R.id.order_item_tv_present_name)).setText("无");
            ll_present.addView(present_view, ll_present.getChildCount());

        }



        for (int i = 0; i < size; i++) {

            PresentInfo presentInfo = info.ls_present.get(i);
            View present_view = inflater.inflate(R.layout.view_order_present, null);
            ((TextView) present_view.findViewById(R.id.order_item_tv_present_name)).setText(presentInfo.name);
            ((TextView) present_view.findViewById(R.id.order_item_tv_present_money)).setText("-" + presentInfo.price);
            ll_present.addView(present_view, ll_present.getChildCount());

        }


        view.findViewById(R.id.order_item_ll_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareThisOrder(info);
            }
        });


        /**
         * 根据不同类型的订单，扩展不同的实现
         */
        expandGenerate(view, info, position);


       // view.setTag(info);
        return view;
    }

    protected abstract void expandGenerate(View view, final OrderInfo info, int postion);


    /**
     * 设置整个订单的基础颜色，不同类型的订单基础颜色不同
     * @param view   订单布局引用
     * @param color  颜色值，非颜色资源的应用
     */
    protected void setColor(View view, int color) {

        ((TextView) view.findViewById(R.id.order_item_tv_serial)).setTextColor(color);
        ((TextView) view.findViewById(R.id.order_item_tv_status)).setTextColor(color);
        ((TextView) view.findViewById(R.id.order_item_tv_date)).setTextColor(color);
        view.findViewById(R.id.order_item_v_divider).setBackgroundColor(color);
        ((TextView) view.findViewById(R.id.order_item_tv_serial)).setTextColor(color);
        ((TextView) view.findViewById(R.id.order_item_tv_phone)).setTextColor(color);


    }


    /**
     * 处理新订单
     *
     * @param info
     * @param position
     */
    protected void handleOrder(OrderInfo info, int position) {
        if (mOnNewOrderListener != null) {
            mOnNewOrderListener.handle(info, position);
        }
    }

    /**
     * 将订单处理为无效
     */
    protected void setOrderIvalid(final OrderInfo info, final int position) {

        if (!CodeUtil.checkNetState(mContext))
            return;

        final String[] cause = mContext.getResources().getStringArray(R.array.invalid_cause);

        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("无效原因")
                .setItems(cause, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        applyInvalidToServer(info,position,cause[i]);
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }


    /**
     * 无效订单的前期处理
     * @param info
     * @param position
     * @param cause
     */
    private void applyInvalidToServer(final OrderInfo info,final int position,final String cause){


        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",info.id);
            jsonObject.put("cause",cause);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(mContext).post(Share.url_set_invalid_order, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                if (mOnNewOrderListener != null) {
                    mOnNewOrderListener.invalid(info, position, cause);
                }

                if (mOnNumalListener != null) {
                    mOnNumalListener.invalid(info, position, cause);
                }
            }

            @Override
            public void onFailre(String s) {

            }
        });


    }


    /**
     *     打印订单
     *
     *      所有可以被打印的订单的打印操作都是这个，
     *
     *      实现打印操作，只需在这里实现就行
     *
     * @param info   订单的详细信息数据，不同类型的订单，数据不同
     * @param position
     */
    protected void printOrder(OrderInfo info, int position) {


    }


    /**
     * 标记餐已送出
     *
     * @param info
     */
    protected void markOrderOut(OrderInfo info, int positon) {

        //检测是否网络可用
        if (!CodeUtil.checkNetState(mContext))
            return;



        //检查获取现在的营业状态
      int whichState=DateUtil.checkInBusinessTime();


        if (whichState==1){//如果状态是：营业中
            info.status=4;
            info.out_time=DateUtil.getOnlyHourAndMinuteNow();
        }else{

            /**
             * 如果订单已经被标记为，已处理_待配送状态
             */
            if (info.status==3){//如果状态是：
                CodeUtil.toast(mContext,"还没有到营业时间，不能执行此操作");
                return;
            }

            //订单的状态标记为：已处理_待配送状态
            info.status=3;
        }

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",info.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        applyMarkOrderOut(info, positon, jsonObject);

    }


    /**
     * 提交标记订单送出请求到服务器
     * @param info
     * @param position
     * @param jsonObject
     */
    private void applyMarkOrderOut(final OrderBaseAdapter.OrderInfo info, final int position,
                                   JSONObject jsonObject){

        new HttpUtil(mContext).post(Share.url_set_out_order, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                //回调标记订单接口
                if (mOnNumalListener != null) {
                    mOnNumalListener.mark(info, position);
                }


            }

            @Override
            public void onFailre(String s) {
            }
        });



    }


    /**
     * 所有可以执行分享操作的订单的分享，都执行此方法，
     * 分享操作只需将代码写到此方法中接口
     * @param content  分享的文字
     */
    public void shareText(String content) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent.setType("text/plain");

        //设置分享列表的标题，并且每次都显示分享列表
        mContext.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


    /**
     * 分享这个订单
     * 所有可以执行分享操作的订单的分享，都执行此方法，
     * 分享操作只需将代码写到此方法中接口
     * @param info
     */
    private void shareThisOrder(OrderInfo info) {
       // Toast.makeText(mContext, "share 中...", Toast.LENGTH_SHORT).show();

        StringBuilder builder=new StringBuilder();


        builder.append("付款类型: ");

        if (info.isPayOnline) {
            builder.append("已在线付款");
        }else{
            builder.append("餐到付款");
        }

        builder.append("\n");

        builder.append("姓   名: ");
        if(info.isAnony){
            builder.append("匿名");
        }else{
            builder.append(info.name+" "+info.sex);
        }
        builder.append("\n");

        builder.append("地   址: ");
        builder.append(info.address);
        builder.append("\n");

        builder.append("电话号码: ");
        builder.append(info.phone);
        builder.append("\n\n");


        builder.append("所购菜品: ");
        builder.append('\n');
        for (DisheInfo dish:info.ls_dish){

            builder.append(dish.name+" × "+dish.num+"  "+dish.price+"元");
            builder.append('\n');

        }

        builder.append("\n");
        builder.append("合计"+info.money+"元");
        builder.append("\n");
        builder.append("\n");


        shareText(builder.toString());
    }

    /**
     * 同意退款申请
     *
     * @param info
     */
    protected void agreeRefund(OrderInfo info, int positon) {

        if (mOnRefundOrderListener != null) {
            mOnRefundOrderListener.agree(info, positon);
        }
    }

    /**
     * 拒绝退款申请
     *
     * @param info
     */
    protected void disAgreeRefund(OrderInfo info, int position) {

        if (mOnRefundOrderListener != null) {
            mOnRefundOrderListener.disAgree(info, position);
        }
    }


    public void setOnNumalListener(OnNumalListner mOnNewOrderListener) {
        this.mOnNumalListener = mOnNewOrderListener;
    }


    public void setOnNewOrderListener(OnNewOrderListener mOnNewOrderListener) {
        this.mOnNewOrderListener = mOnNewOrderListener;
    }


    public void setOnRefundOrderListener(OnRefundOrderListener mOnRefundOrderListener) {
        this.mOnRefundOrderListener = mOnRefundOrderListener;
    }


    /**
     * 退款订单处理接口
     */
    public interface OnRefundOrderListener {

         void agree(OrderInfo info, int position);

         void disAgree(OrderInfo info, int position);

    }


    /**
     * 新订单处理接口
     */
    public interface OnNewOrderListener {

          void invalid(OrderInfo info, int position,String cause);

          void handle(OrderInfo info, int position);

    }

    /**
     * 待配送订单，今日已处理订单的操作接口
     */
    public interface OnNumalListner {

        /**
         * 设置订单无效
         * @param info
         * @param position
         * @param cause
         */
         void invalid(OrderInfo info, int position,String cause);

        /**
         * 标记订单为：已处理_送出状态 或 已处理_待配送状态
         * @param info
         * @param position
         */
         void mark(OrderInfo info, int position);

    }


    /**
     * 订单数据结构
     *
     * 不同类型的订单，使用此类中不同的变量。
     *
     * 其中 id,date,serial,isAnony,name,sex,address,phone,money,ls_dish,ls_present一定会用到，
     *
     * 其它的变量会根据订单类型的不同，而用到不同的变量。
     *
     *
     */
    public static class OrderInfo {

        public String id;
        public String date;
        public int status;            //订单状态 1.新订单 2.待配送订单 3.已处理_预定状态 4.已处理_营业状态   5.交易完成
        public int serial;
        public boolean isAnony = false;  //是否是匿名订单   default 不是
        public String name;
        public String sex = "男士";    //用户性别     default 男士
        public String address;
        public boolean isPayOnline = true;     //是否已在线支付   default 是
        public String phone;
        public float money;
        public String timeToOut;
        public boolean isValid = false;//是否是无效订单  （default 不是）
        public String cause;          //无效原因
        public List<DisheInfo> ls_dish=new ArrayList<>();  //订单中的菜品内容
        public List<PresentInfo> ls_present=new ArrayList<>();  //订单中的活动内容
        public int refundStatus;          //退款状态  （0.未处理、1.退款中、2.退款成功
        public String out_time;     //外卖送出的时间
        public boolean hasCollapse = false;  //是否折叠了


    }


    /**
     * 菜品数据结构
     */
    public static class DisheInfo {

        public String name;        //菜名+菜规格名+菜属性   三者的名字连接而成
        public int num;            //菜菜品购买的数量
        public float price;        //单道菜的价格

    }

    /**
     * 优惠数据结构
     */
    public static class PresentInfo {
        public String name;          //活动的名字
        public float price;          //活动减去的钱，不减钱的活动将不适用此变量
    }


    @Override
    public int getCount() {
        return ls_data.size();
    }

    @Override
    public Object getItem(int i) {
        return ls_data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return generateView(view, i);
    }
}
