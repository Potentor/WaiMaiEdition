package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.fragment.NavigationDrawerFragment;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopStatusActivity extends ActionBarActivity {


    /**
     * 用户店的实际营业状态
     *
     * 1.正在营业中
     *
     * 2.预定状态
     *
     * 3.休息状态
     *
     */
    private int shop_status;


    /**
     * 1.开启营业状态   3.停止营业状态
     *
     * 只是为了标志  停止/开启按钮  点击的当前状态
     */
    private int temp_status;


    private TextView tv_summarize;
    private TextView tv_instruction;
    private TextView tv_business_time;
    private TextView tv_bt;



    /**
     * 开启或停止营业
     * @param view
     */
    public void stopOrOpenBusiness(final View view){

        JSONObject jsonObject=new JSONObject();
        try {

            jsonObject.put("shopId",CodeUtil.getShopId(this));

            if (temp_status==1){
                jsonObject.put("cmd", 1);

            }else{
                jsonObject.put("cmd", 0);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        new HttpUtil(this).post(Share.url_modify_shop_status, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                if (!isOk)
                    return;


               if (temp_status==1) {
                   temp_status=3;
                   updateUIAccordingToStatus(3);
                   Status.isForceStopBusiness=true;
                    noticeMainUiChangeShow(3);


               }else{
                   temp_status=1;
                   updateUIAccordingToStatus(shop_status);
                   Status.isForceStopBusiness=false;
                   noticeMainUiChangeShow(shop_status);
               }


            }

            @Override
            public void onFailre(String s) {

            }
        });









    }

    /**
     * 通知，主界面中的侧滑界面，显示的当前营业状态进行修改
     * @param status
     */
    private void noticeMainUiChangeShow(int status){

        Message message=new Message();
        message.what=201;

        switch (status) {
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



    /**
     * 根据当前的营业状态更新界面中的状态显示
     */
    private void updateUIAccordingToStatus(int status){

        switch (status)
        {
            case 3://停止营业状态
                tv_summarize.setText("休息中");
                Drawable drawable=getResources().getDrawable(R.drawable.img_ytzyy);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_summarize.setCompoundDrawables(drawable, null, null, null);
                tv_instruction.setText(getString(R.string.status_rest));


                break;
            case 2://预定状态
                tv_summarize.setText("仅接受预定");
                Drawable drawable1=getResources().getDrawable(R.drawable.img_jjsyd);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                tv_summarize.setCompoundDrawables(drawable1, null, null, null);
                tv_instruction.setText(getString(R.string.status_recive));

                break;
            case 1://正常营业状态
                tv_summarize.setText("营业中");
                Drawable drawable2=getResources().getDrawable(R.drawable.img_yyz);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                tv_summarize.setCompoundDrawables(drawable2, null, null, null);
                tv_instruction.setText(getString(R.string.status_business));
                break;
        }

        if (temp_status==3){
            tv_bt.setText("开始营业");
        }else{
            tv_bt.setText("停止营业");
        }
    }

    /**
     * 从服务器获取数据
     */
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


        StringBuilder builder=new StringBuilder();

        JSONArray array=jsonObject.getJSONArray("time");
        int size=array.length();

        for(int i=0;i<size;i++){

            JSONObject object=array.getJSONObject(i);
            builder.append(object.getString("startTime"));
            builder.append("-");
            builder.append(object.getString("endTime"));
            builder.append(" ");


        }

        tv_business_time.setText(builder.toString());

    }


    /**
     * 初始化组件
     */
    private void initView(){

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("营业状态");

        tv_business_time=(TextView)findViewById(R.id.status_tv_time);
        tv_instruction=(TextView)findViewById(R.id.status_tv_instruction);
        tv_summarize=(TextView)findViewById(R.id.status_tv_summarize);
        tv_bt=(TextView)findViewById(R.id.status_tv_click);

        shop_status=DateUtil.checkInBusinessTime();

        if (Status.isForceStopBusiness){
            temp_status=3;
            updateUIAccordingToStatus(3);

        }else{
            temp_status=1;
            updateUIAccordingToStatus(shop_status);

        }


        if(CodeUtil.checkNetState(this)) {
            fetchData();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode!=200)
            return;

        if (requestCode==100){

            //
            String[] str_time=data.getStringArrayExtra("time");

            StringBuilder builder=new StringBuilder("");
            for (String str:str_time){

                builder.append(str);
                builder.append('\n');

            }
            tv_business_time.setText(builder.toString());

            shop_status=DateUtil.checkInBusinessTime();

            if (Status.isForceStopBusiness){
                updateUIAccordingToStatus(3);
                temp_status=3;
            }else{
                updateUIAccordingToStatus(shop_status);
                temp_status=1;
            }

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_status);

        initView();


    }


    @Override
    public boolean onSupportNavigateUp() {

        if (temp_status==3) {
            setResult(200, null);
        }else{

            Intent intent=getIntent();
            intent.putExtra("status",shop_status);
            setResult(200,intent);

        }
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (temp_status==3) {
            setResult(200, null);
        }else{

            Intent intent=getIntent();
            intent.putExtra("status",shop_status);
            setResult(200,intent);

        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,TimeModifyActivity.class);
            startActivityForResult(intent,100);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
