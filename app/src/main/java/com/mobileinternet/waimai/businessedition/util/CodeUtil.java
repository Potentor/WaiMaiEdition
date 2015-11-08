package com.mobileinternet.waimai.businessedition.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.OrderBaseAdapter;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 海鸥2012 on 2015/8/4.
 *
 * 一些共有的代码段，将已静态方法的形式编写在这里。其他界面中可调用这些方法，以避免多次重复的编写同一套代码
 *
 *
 */
public class CodeUtil {


    /**
     * 检查当前的网络状态，若网络不可用则，显示Toast通知用户
     *
     * @param context
     * @return  网络是否可用
     */
    public static boolean checkNetState(Context context) {
        if (!Status.isIsConnectNet()) {

            Toast.makeText(context, "网络异常，请检查网络连接", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }


    /**
     * 消息显示，省去设置显示时间
     *
     * @param context
     * @param content
     */
    public static void toast(Context context,String content){
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取餐厅的id号
     *
     * @param context
     * @return
     */
    public static String getShopId(Activity context){
        IApplication iApplication=(IApplication)context.getApplication();
        return iApplication.getData(Share.shop_id);
    }


    /**
     * 生成数据为：
     *
     * {
     *     “shopId”，“joijoijafeawfwaefwaf”
     * }
     *
     * 的JSONObject 对象
     *
     * @param context
     * @return
     */
    public static JSONObject getJsonOnlyShopId(Activity context){
        JSONObject jsonObject=new JSONObject();
        IApplication iApplication=(IApplication)context.getApplication();
        try {
            jsonObject.put("shopId",iApplication.getData(Share.shop_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    /**
     *
     *
     * 将从服务器上传来的订单json数据中，订单的共有数据存储到OrderInfo中，
     * 所有类型的订单都可调用此方法，将共有的数据解析并存储起来。
     *
     * 订单id
     * 订单序号
     * 地址
     * 序号
     * 时间
     * 电话
     * 金额
     * 是否匿名
     *       姓名
     *      性别
     *
     * 菜品： 规格、属性
     *
     *
     * @param dataObject  单个订单的json
     * @param info     存储单个订单的数据结构
     * @throws JSONException
     */
    public static void doOrderCommonDo(JSONObject dataObject,OrderBaseAdapter.OrderInfo info)throws JSONException{


        info.id=dataObject.getString("orderId");
        info.date=dataObject.getString("time");
        info.serial=dataObject.getInt("number");
        info.isAnony=dataObject.getBoolean("isAnony");
        info.phone=dataObject.getString("phone");
        //判断是否订单未匿名
        if (!info.isAnony){
            info.name=dataObject.getString("userName");
            info.sex=dataObject.getString("sex");
        }
        info.address=dataObject.getString("addr");
        info.money=(float)dataObject.getDouble("total");
        //得到订单的所有菜品信息
        JSONArray dishArry=dataObject.getJSONArray("dishes");
        int length=dishArry.length();
        for(int j=0;j<length;j++){

            //得到单个菜品的信息
            JSONObject dishObject=dishArry.getJSONObject(j);

            //创建存储单个菜品信息的数据结构
            OrderBaseAdapter.DisheInfo  dishInfo=new OrderBaseAdapter.DisheInfo();

            //使用StringBulder存储菜品的名字、规格、属性信息
            StringBuilder builder=new StringBuilder();


            builder.append(dishObject.getString("name"));
            dishInfo.num=dishObject.getInt("num");
            dishInfo.price=(float)dishObject.getDouble("sum");
            //判断菜品是否有规格
            boolean hasGuige=dishObject.getBoolean("hasGuige");
           // boolean hasGuige=dishObject.getBoolean("hasguige");
            if (hasGuige){

                String guige=dishObject.getString("guige");
                builder.append(" ");
                builder.append(guige);

            }



            //判断菜品是否有都种属性
            boolean hasAttr=dishObject.getBoolean("hasAttr");
            if (hasAttr){
                JSONArray attArray=dishObject.getJSONArray("attr");
                int unit=attArray.length();
                for(int z=0;z<unit;z++){

                    JSONObject attObjcet=attArray.getJSONObject(z);
                    builder.append(" ");
                    builder.append(attObjcet.getString("value"));

                }


            }
            dishInfo.name=builder.toString();

            //保存单道菜品到订单存储中
            info.ls_dish.add(dishInfo);

        }


        //订单的优惠

        JSONArray presentArray=dataObject.optJSONArray("other");

        if (presentArray==null)
            return;

        length=presentArray.length();

        for (int j=0;j<length;j++){

            JSONObject present=presentArray.getJSONObject(j);
            OrderBaseAdapter.PresentInfo info_present=new OrderBaseAdapter.PresentInfo();
            info_present.name=present.getString("name");
            int type=present.getInt("category");
            if (type==1){
                info_present.price=(float)present.getDouble("sum");
            }else{
                info_present.price=0;
            }
            info.ls_present.add(info_present);

        }

    }


    /**
     * 设置订单的展开、折叠状态，及加载相应的事件，公有代码
     *
     * 关于订单的适配器中，可调用此方法，设置订单的展开、折叠状态，及加载相应的事件
     *
     * @param info
     * @param view
     */
    public static void collapseAndExpand(final OrderBaseAdapter.OrderInfo info,final View view){

        /**
         * 折叠展开
         */
        final View ll_dish= view.findViewById(R.id.order_item_ll_dishes);
        final View ll_outlay=view.findViewById(R.id.order_item_ll_other_outlay);
        final View ll_mark=view.findViewById(R.id.order_item_ll_mark);
        final TextView tv_status=(TextView)view.findViewById(R.id.order_item_tv_status);

        //是否处在未折叠状态
        if (!info.hasCollapse){
            //
            ll_dish.setVisibility(View.VISIBLE);
            ll_outlay.setVisibility(View.VISIBLE);
            //ll_mark.setVisibility(View.VISIBLE);
            Drawable drawable=view.getContext().getResources().getDrawable(R.drawable.ic_expand_small);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_status.setCompoundDrawables(null, null, drawable, null);

        }else{
            //折叠
            ll_dish.setVisibility(View.GONE);
           // ll_mark.setVisibility(View.GONE);
            ll_outlay.setVisibility(View.GONE);
            Drawable drawable=view.getContext().getResources().getDrawable(R.drawable.ic_collapse_small);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_status.setCompoundDrawables(null, null, drawable, null);
        }




        view.findViewById(R.id.order_item_ll_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if (info.hasCollapse){
                 //   view.setTag(true);
                    //展开
                    ll_dish.setVisibility(View.VISIBLE);
                    ll_outlay.setVisibility(View.VISIBLE);

//                    if (!info.isValid||!info.isPayOnline)
//                        ll_mark.setVisibility(View.VISIBLE)


                    Drawable drawable=view.getContext().getResources().getDrawable(R.drawable.ic_expand_small);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_status.setCompoundDrawables(null, null, drawable, null);



                }else{
                    //折叠

                  //  view.setTag(false);
                    ll_dish.setVisibility(View.GONE);
                   // ll_mark.setVisibility(View.GONE);
                    ll_outlay.setVisibility(View.GONE);

                    Drawable drawable=view.getContext().getResources().getDrawable(R.drawable.ic_collapse_small);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_status.setCompoundDrawables(null, null, drawable, null);
                }
                info.hasCollapse=!info.hasCollapse;

            }
        });


    }






}
