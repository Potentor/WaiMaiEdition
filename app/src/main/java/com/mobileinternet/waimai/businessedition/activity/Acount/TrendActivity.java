package com.mobileinternet.waimai.businessedition.activity.Acount;

import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TrendActivity extends AppCompatActivity {


    private LineChartView lcv_money;
    private LineChartView lcv_order;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_chart);  ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("七日趋势统计");

        lcv_order=(LineChartView)findViewById(R.id.trend_lcv_order);
        lcv_money=(LineChartView)findViewById(R.id.trend_lcv_money);


        if (CodeUtil.checkNetState(this)) {
            fetchData();
        }

    }


    /**
     * 从服务器获取数据
     */
    private void fetchData(){

        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this);

        final Dialog dialog= DialogUtil.showProgressDialog(this, "加载中...");;
        new HttpUtil(this).post(Share.url_seven_day_trend, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();
                if (!isOk) {
                    return;
                }

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


    /**
     * 将数据显示到界面上
     * @param jsonObject
     * @throws JSONException
     */
    private void freightUI(JSONObject jsonObject)throws JSONException{



        //获取服务器传来的数据
        JSONArray sales=jsonObject.getJSONArray("sales");
        JSONArray orders=jsonObject.getJSONArray("orders");

        int size=sales.length();


        if (size<2){
            CodeUtil.toast(this,"您的营业时间还么有超过两天，不能进行趋势统计");
            return;
        }



        SimpleDateFormat mDateFormat=new SimpleDateFormat("MM-dd");
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.add(Calendar.DAY_OF_MONTH,-size);
        String sevenBefore=mDateFormat.format(mCalendar.getTime());
        int day=mCalendar.get(Calendar.DAY_OF_MONTH);

        String[] stg_date=new String[size];
        float[] flt_order=new float[size];
        float[] flt_money=new float[size];

        stg_date[0]=sevenBefore;
        flt_order[0]=(float)orders.getDouble(0);
        flt_money[0]=(float)sales.getDouble(0);


        float order_max=flt_order[0];
        float order_min=flt_order[0];
        float money_max=flt_money[0];
        float money_min=flt_money[0];

        int i=1;
        while(i<size)
        {
            day++;
            stg_date[i]=""+day;
            flt_order[i]=(float)orders.getDouble(i);
            flt_money[i]=(float)sales.getDouble(i);

            if (order_max<flt_order[i]){
                order_max=flt_order[i];
            }else if (order_min>flt_order[i]){
                order_min=flt_order[i];
            }


            if (money_max<flt_money[i]){

                money_max=flt_money[i];
            }else if (money_min>flt_money[i]){

                money_min=flt_money[i];
            }

            i++;

        }





        LineSet lineSet_order=new LineSet(stg_date,flt_order);
        LineSet lineSet_money=new LineSet(stg_date,flt_money);

        configTips(lcv_money);
        configTips(lcv_order);

        configDataSet(lineSet_money, lcv_money);
        configDataSet(lineSet_order,lcv_order);


       // Arrays

        money_max=(int)Math.ceil(money_max);

        int money_step=(int)(money_max-money_min)/5;
        int order_step=(int)(order_max-order_min)/5;

        if (money_step==0){
            money_step=5;
        }

        if (order_step==0){
            order_step=5;
        }

        if (money_min<10){
            money_min=0;
        }

        if (order_min<10){
            order_min=0;
        }

        if (money_max<5){
            money_max=5;
        }

        if (order_max<5){
            order_max=5;
        }


        configChart(lcv_money,(int)money_min,(int)money_max, money_step);
        configChart(lcv_order,(int)order_min,(int)order_max,order_step);



    }




    /**
     * 配置折线图的样式
     * @param dataset
     * @param chart
     */
    private void configDataSet(LineSet dataset,LineChartView chart){

        dataset.setColor(Color.RED)
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF58C674"))
                .setDotsColor(Color.parseColor("#eef1f6"))
                .setSmooth(true);
        chart.addData(dataset);
    }


    /**
     * 配置点击圆点后显示的内容
     * @param chart
     */
    private void configTips(LineChartView chart){
        Tooltip tip = new Tooltip(this, R.layout.linechart_three_tooltip, R.id.value);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));

            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X,0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y,0f));
        }

        chart.setTooltips(tip);
    }

    /**
     * 设置图表的样式
     * @param chart
     */
    private void configChart(LineChartView chart,int min,int max,int step){

//        Paint gridPaint = new Paint();
//        gridPaint.setColor(Color.parseColor("#308E9196"));
//        gridPaint.setStyle(Paint.Style.STROKE);
//        gridPaint.setAntiAlias(true);
//        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));


        chart.setTopSpacing(Tools.fromDpToPx(15))
                .setBorderSpacing(Tools.fromDpToPx(0))
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.BLACK)
                .setAxisBorderValues(min,max,step)
                .setXAxis(true)
                .setYAxis(true);

        chart.show();
    }


}
