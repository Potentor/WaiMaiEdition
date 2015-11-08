package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.fragment.NavigationDrawerFragment;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.PickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimeModifyActivity extends ActionBarActivity {

    private TextView tv_day;
    private LinearLayout linearLayout;
    private List<ViewHolder> ls_data=new ArrayList<>();

    private String[] weeks=new String[]{"周一","周二","周三","周四","周五","周六","周日"};
    private boolean[] checks=new boolean[]{false,false,false,false,false,false,false};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_time_pre_order);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("营业时间修改");

        tv_day=(TextView)findViewById(R.id.time_pre_order_tv_week);
        linearLayout=(LinearLayout)findViewById(R.id.time_pre_order_ll);

        if(CodeUtil.checkNetState(this)){
            fetchData();
        }

    }


    private void fetchData(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("shopId", CodeUtil.getShopId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_buiness_time, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                try {
                    freightUI(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailre(String s) {

            }
        });


    }


    private void freightUI(JSONObject jsonObject)throws JSONException{

        JSONArray weekArray=jsonObject.getJSONArray("week");
        int size=weekArray.length();
        for(int i=0;i<size;i++){

            int position=weekArray.getInt(i);
            checks[position-1]=true;

        }


        updateDay();


        JSONArray arrayTime=jsonObject.getJSONArray("time");
        size=arrayTime.length();
        for(int i=0;i<size;i++){
            JSONObject time=arrayTime.getJSONObject(i);
            String start=time.getString("startTime");
            String end=time.getString("endTime");
            //initTimeView(DateUtil.getOnlyHourAndMinute(start),DateUtil.getOnlyHourAndMinute(end));
            initTimeView(start,end);
        }


    }


    /**
     * 保存修改设置
     */
    private void saveModification() throws JSONException {



        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this);
        JSONArray weekArray=new JSONArray();
        JSONArray closeArray=new JSONArray();

        for(int i=0;i<checks.length;i++){

            if (checks[i]){
                weekArray.put(i+1);
            }else{
                closeArray.put(i+1);
            }

        }

        jsonObject.put("week",weekArray);
        jsonObject.put("close",closeArray);



        JSONArray timeArray=new JSONArray();
        for(int i=0;i<ls_data.size();i++){

            ViewHolder viewHolder=ls_data.get(i);
            JSONObject object=new JSONObject();
            object.put("startTime",viewHolder.tv_start.getText().toString());
            object.put("endTime",viewHolder.tv_end.getText().toString());
            timeArray.put(object);

        }


        jsonObject.put("time", timeArray);

        new HttpUtil(this).post(Share.url_time_modify, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                    sucess();

            }

            @Override
            public void onFailre(String s) {

            }
        });







    }



    private void sucess(){

        //得到所有营业时间
        String[] result=new String[ls_data.size()];
        for (int i=0;i<ls_data.size();i++){
            ViewHolder viewHolder=ls_data.get(i);
            String start=viewHolder.tv_start.getText().toString();
            String end=viewHolder.tv_end.getText().toString();
            result[i]=start+"-"+end;
        }

        /**
         * 修改Status中本地存储的营业时间
         */

        //周状态
        for (int i=0;i<checks.length;i++){

            Status.busi_week[i]=checks[i];

        }

        //一天的营业时间段
        Status.busiTime.clear();
        for(int i=0;i<ls_data.size();i++){

            ViewHolder viewHolder=ls_data.get(i);
            Status.busiTime.add(viewHolder.tv_start.getText().toString());
            Status.busiTime.add(viewHolder.tv_end.getText().toString());

        }


        //通知，主界面中的侧滑界面，显示的当前营业状态进行修改
        if (!Status.isForceStopBusiness) {

            Message message=new Message();
            message.what=201;

            int hello=DateUtil.checkInBusinessTime();

            switch (hello) {
                case 1:
                    message.arg1=1;
                    break;
                case 2:
                    message.arg1=2;
                    break;
                case 3:
                    message.arg1=3;
                    break;
            }

            NavigationDrawerFragment.mHandler.sendMessage(message);
        }



        CodeUtil.toast(TimeModifyActivity.this, "修改成功");
        Intent intent = getIntent();
        intent.putExtra("time", result);
        setResult(200, intent);
        finish();
    }


    public void modifyWeek(View view){

        final boolean[] tempCheck;

        tempCheck=Arrays.copyOf(checks,7);

        new AlertDialog.Builder(this).setMultiChoiceItems(weeks,tempCheck,new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                tempCheck[i]=b;

            }
        })
        .setTitle("请选择营业天数")
        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //判断是否一星期中至少一天经营
                int j=0;
                for (;j<tempCheck.length;j++){

                    if (tempCheck[j])
                        break;
                }

                if (j==tempCheck.length){
                    CodeUtil.toast(TimeModifyActivity.this,"一星期至少有一天经营");
                    return;
                }

                checks=Arrays.copyOf(tempCheck,7);
                updateDay();

            }
        })
        .setNegativeButton("取消",null)
                .create().show();


    }


    private void updateDay(){

        StringBuilder builder=new StringBuilder();

        boolean isAllWeek=true;

        for (int i=0;i<7;i++){
            if (checks[i]){
                builder.append(weeks[i]);
                builder.append('、');
            }else {
                isAllWeek=false;
            }
        }

        if (isAllWeek){
            tv_day.setText("每天");
        }else{
            tv_day.setText(builder.substring(0,builder.length()-1));
        }


    }

    /**
     * 修改时间段起始时间
     * @param viewHolder
     */
    private void modifyTime(final ViewHolder viewHolder, final View container){


        //获取当前营业时间在ls_data的位置
        final int position=ls_data.indexOf(viewHolder);

        //实例化各picker
        View view=LayoutInflater.from(this).inflate(R.layout.dialog_modify_time,null);
        final PickerView pv_start_hour=(PickerView)view.findViewById(R.id.picker_start_hour);
        final PickerView pv_start_minute=(PickerView)view.findViewById(R.id.picker_start_minute);
        final PickerView pv_end_hour=(PickerView)view.findViewById(R.id.picker_end_hour);
        final PickerView pv_end_minute=(PickerView)view.findViewById(R.id.picker_end_minute);




        //获取起止时间，并进一步获取其中的具体小时、分钟
        String startTime=viewHolder.tv_start.getText().toString();
        String endTime=viewHolder.tv_end.getText().toString();


        //起始时间
        int at1=startTime.indexOf(":");
        final String str_start_hour=startTime.substring(0, at1);
        String str_start_minute=startTime.substring(at1 + 1);

        //截止时间
        int at2=endTime.indexOf(":");
        String str_end_hour=endTime.substring(0,at2);
        String str_end_minute=endTime.substring(at2+1);





        //picker加载数据
        List<String> ls_hour=new ArrayList<>();
        List<String> ls_minute=new ArrayList<>();
        List<String> ls_hour2=new ArrayList<>();
        List<String> ls_minute2=new ArrayList<>();

        for (int i=0;i<=9;i++){
            ls_hour.add("0"+i);
            ls_minute.add("0"+i);
            ls_hour2.add("0"+i);
            ls_minute2.add("0"+i);
        }

        for (int i=10;i<=23;i++){
            ls_hour.add(""+i);
            ls_minute.add(""+i);
            ls_hour2.add(""+i);
            ls_minute2.add(""+i);
        }

        for (int i=24;i<=59;i++){
            ls_minute.add(""+i);
            ls_minute2.add(""+i);
        }

        pv_end_hour.setData(ls_hour);
        pv_start_hour.setData(ls_hour2);
        pv_start_minute.setData(ls_minute);
        pv_end_minute.setData(ls_minute2);


       //picker加载当前显示的数据
        pv_start_hour.setSelected(str_start_hour);
        pv_start_minute.setSelected(str_start_minute);
        pv_end_hour.setSelected(str_end_hour);
        pv_end_minute.setSelected(str_end_minute);




        new AlertDialog.Builder(this).setView(view)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        //获取设置的时间中的小时、分钟
                        int hour1=Integer.parseInt(pv_start_hour.getSelected());
                        int hour2=Integer.parseInt(pv_end_hour.getSelected());
                        int minute1=Integer.parseInt(pv_start_minute.getSelected());
                        int minute2=Integer.parseInt(pv_end_minute.getSelected());


                        //检查设置的营业时间是否合法
                        boolean canPass=checkBusinessPeriod(position,hour1,hour2,minute1,minute2);


                        //如果设置的营业时间有问题,则设置失败
                        if (!canPass) {

                            //如果这是在进行添加营业时间操作
                            if (container!=null){
                                ls_data.remove(position);
                            }
                            return;
                        }


                        //更新界面对应时间的显示
                        viewHolder.tv_start.setText(pv_start_hour.getSelected() + ":" + pv_start_minute.getSelected());
                        viewHolder.tv_end.setText(pv_end_hour.getSelected()+":"+pv_end_minute.getSelected());

                        //如果是进行添加营业时间操作，则将新加的营业时间显示到界面中
                        if (container!=null) {
                            linearLayout.addView(container);
                        }

                    }
                })
                .setNegativeButton("取消", null)
                .create().show();

    }




    /**
     * 检查输入的营业时间是否合法
     * @param position
     * @param hour1
     * @param hour2
     * @param minute1
     * @param minute2
     * @return
     */
    private boolean checkBusinessPeriod(int position,int hour1,int hour2,int minute1,int minute2){

        if (hour2<hour1){

            Toast.makeText(this,"结束时间不能早于开始时间",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (hour1==hour2) {
            if (minute2<=minute1){
                Toast.makeText(this,"结束时间不能早于开始时间",Toast.LENGTH_SHORT).show();
                return false;
            }else{
                Toast.makeText(this,"营业时间不能太短",Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        int size=ls_data.size();
        for (int i=0;i<size;i++){


            //如果是自己本身，则跳过
            if (i==position)
                continue;


            ViewHolder viewHolder=ls_data.get(i);
            //获取起止时间，并进一步获取其中的具体小时、分钟
            String startTime=viewHolder.tv_start.getText().toString();
            String endTime=viewHolder.tv_end.getText().toString();

            int at1=startTime.indexOf(":");
            final String str_start_hour=startTime.substring(0, at1);
           // String str_start_minute=startTime.substring(at1 + 1);


            int at2=endTime.indexOf(":");

            String str_end_hour=endTime.substring(0,at2);
          //  String str_end_minute=endTime.substring(at2+1);

            int refe_hour1=Integer.parseInt(str_start_hour);
          //  int refe_minute1=Integer.parseInt(str_start_minute);
            int refe_hour2=Integer.parseInt(str_end_hour);
            //int refe_minute2=Integer.parseInt(str_end_minute);


            if (hour1>refe_hour1&&hour1<refe_hour2){
                Toast.makeText(this,"营业时间有重叠",Toast.LENGTH_SHORT).show();
                return false;
            }

            if (hour2>refe_hour1&&hour2<refe_hour2){
                Toast.makeText(this,"营业时间有重叠",Toast.LENGTH_SHORT).show();
                return false;
            }


            if (hour2>=refe_hour2&&hour1<=refe_hour1) {
                Toast.makeText(this,"营业时间有重叠",Toast.LENGTH_SHORT).show();
                return false;
            }


        }




//        Calendar mClendar=Calendar.getInstance();
//        mClendar.set(Calendar.HOUR,hour1);
//        mClendar.set(Calendar.MINUTE,minute1);
//        Date now_start=mClendar.getTime();
//        now_start=mClendar.getTime();
//
//        mClendar.set(Calendar.HOUR,hour2);
//        mClendar.set(Calendar.MINUTE,minute2);
//        Date now_end=mClendar.getTime();
//        now_end=mClendar.getTime();



        return true;

    }


    /**
     * 添加营业时间段
     * @param v
     */
    public void addTimeView(View v){

        if (ls_data.size()>2){

            Toast.makeText(this,"时间段的数量不能超过三个",Toast.LENGTH_SHORT).show();
            return;
        }



        View view=LayoutInflater.from(this).inflate(R.layout.item_add_business_time,null);

        TextView tv_start=(TextView)view.findViewById(R.id.tv_starttime);
        TextView tv_end=(TextView)view.findViewById(R.id.tv_endtime);

        ImageView imageView=(ImageView)view.findViewById(R.id.iv_delete);
        LinearLayout linear=(LinearLayout)view.findViewById(R.id.ll_container);

        final ViewHolder viewHolder=new ViewHolder();
        viewHolder.tv_end=tv_end;
        viewHolder.tv_start=tv_start;

        linear.setTag(viewHolder);
        imageView.setTag(viewHolder);

        ls_data.add(viewHolder);


        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modifyTime((ViewHolder)view.getTag(),null);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (ls_data.size()==1){

                    CodeUtil.toast(TimeModifyActivity.this,"至少有一个时间段营业");
                    return;
                }


                new AlertDialog.Builder(TimeModifyActivity.this).setMessage("你要删除这个营业时间段吗？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               // linearLayout.removeViewAt(v.getRootView());
                                int position =ls_data.lastIndexOf(viewHolder);
                                linearLayout.removeViewAt(position);
                                ls_data.remove(viewHolder);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create().show();

            }
        });


            modifyTime(viewHolder, view);

    }


    /**
     * 从服务器中拿到数据后，生成对应的时间段显示
     * @param start
     * @param end
     */
    private void initTimeView(String start,String end){

        View view=LayoutInflater.from(this).inflate(R.layout.item_add_business_time,null);

        TextView tv_start=(TextView)view.findViewById(R.id.tv_starttime);
        TextView tv_end=(TextView)view.findViewById(R.id.tv_endtime);

        ImageView imageView=(ImageView)view.findViewById(R.id.iv_delete);
        LinearLayout linear=(LinearLayout)view.findViewById(R.id.ll_container);

        final ViewHolder viewHolder=new ViewHolder();
        viewHolder.tv_end=tv_end;
        viewHolder.tv_start=tv_start;

        linear.setTag(viewHolder);
        imageView.setTag(viewHolder);




        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modifyTime((ViewHolder) view.getTag(), null);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (ls_data.size()==1){

                    CodeUtil.toast(TimeModifyActivity.this,"至少有一个时间段营业");
                    return;
                }


                new AlertDialog.Builder(TimeModifyActivity.this).setMessage("你要删除这个营业时间段吗？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // linearLayout.removeViewAt(v.getRootView());
                                int position =ls_data.lastIndexOf(viewHolder);
                                linearLayout.removeViewAt(position);
                                ls_data.remove(viewHolder);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create().show();

            }
        });


        tv_start.setText(start);
        tv_end.setText(end);

        linearLayout.addView(view);
        ls_data.add(viewHolder);
    }





    private class ViewHolder{
        TextView tv_start;
        TextView tv_end;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_pre_order, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(400,null);
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            if(!CodeUtil.checkNetState(this))
                return true;

            try {
                saveModification();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
