package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.fragment.Comment.AllCommentFragment;
import com.mobileinternet.waimai.businessedition.fragment.Comment.BadNotReplyFragment;
import com.mobileinternet.waimai.businessedition.fragment.Comment.NotReplyFragment;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.MScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;

public class ShopCommentActivity extends ActionBarActivity {




//    private ViewPager viewPager;
//
//    private List<Fragment> ls_fragment=new ArrayList<>();



    private TextView rb_bad_not_reply;
    private TextView rb_not_reply;
    private TextView rb_all_comment;
    private TextView tv_total_score;
    private TextView tv_delivery_time;
    private TextView tv_total_people;
    private TextView tv_five_star;
    private TextView tv_four_star;
    private TextView tv_three_star;
    private TextView tv_two_star;
    private TextView tv_one_star;



    public void badNotReply(View view){


        hideAll();
        FragmentManager manager=getSupportFragmentManager();
        Fragment fragment=manager.findFragmentByTag(BadNotReplyFragment.tag);
        if (null==fragment){

            manager.beginTransaction().add(R.id.comment_fragmentlayout,BadNotReplyFragment.newInstance(),BadNotReplyFragment.tag).commit();

        }else{
            manager.beginTransaction().show(fragment).commit();
        }

        rb_bad_not_reply.setTextColor(Color.RED);
        rb_bad_not_reply.setBackgroundResource(R.drawable.cab_background_top_merchantstyle);
      //  rb_bad_not_reply.setChecked(true);
    }


    public void notReply(View view){

        hideAll();
        FragmentManager manager=getSupportFragmentManager();
        Fragment fragment=manager.findFragmentByTag(NotReplyFragment.tag);
        if (null==fragment){

            manager.beginTransaction().add(R.id.comment_fragmentlayout,NotReplyFragment.newInstance(),NotReplyFragment.tag).commit();

        }else{
            manager.beginTransaction().show(fragment).commit();
        }

        rb_not_reply.setTextColor(Color.RED);
        rb_not_reply.setBackgroundResource(R.drawable.cab_background_top_merchantstyle);
        //rb_not_reply.setChecked(true);
    }


    public void allComment(View view){

        hideAll();
        FragmentManager manager=getSupportFragmentManager();
        Fragment fragment=manager.findFragmentByTag(AllCommentFragment.tag);
        if (null==fragment){

            manager.beginTransaction().add(R.id.comment_fragmentlayout,AllCommentFragment.newInstance(),AllCommentFragment.tag).commit();

        }else{
            manager.beginTransaction().show(fragment).commit();
        }

        rb_all_comment.setTextColor(Color.RED);
        rb_all_comment.setBackgroundResource(R.drawable.cab_background_top_merchantstyle);

    }



    private void hideAll(){

        List<Fragment> ls=getSupportFragmentManager().getFragments();

        for(Fragment fragment:ls){

            if(fragment==null)
                continue;

            getSupportFragmentManager().beginTransaction().hide(fragment).commit();

        }

        rb_not_reply.setTextColor(Color.BLACK);
        rb_all_comment.setTextColor(Color.BLACK);
        rb_bad_not_reply.setTextColor(Color.BLACK);
//        rb_bad_not_reply.setChecked(false);
//        rb_not_reply.setChecked(false);
//        rb_all_comment.setChecked(false);
        rb_bad_not_reply.setBackgroundColor(Color.WHITE);
        rb_all_comment.setBackgroundColor(Color.WHITE);
        rb_not_reply.setBackgroundColor(Color.WHITE);

    }



    private void initView(){



        rb_all_comment=(TextView)findViewById(R.id.comment_rb_03);
        rb_bad_not_reply=(TextView)findViewById(R.id.comment_rb_01);
        rb_not_reply=(TextView)findViewById(R.id.comment_rb_02);

      //  rb_bad_not_reply.setChecked(true);
        rb_bad_not_reply.setTextColor(Color.RED);
        rb_bad_not_reply.setBackgroundResource(R.drawable.cab_background_top_merchantstyle);


        tv_delivery_time=(TextView)findViewById(R.id.comment_tv_consume_time);
        tv_total_people=(TextView)findViewById(R.id.comment_tv_total_people);
        tv_total_score=(TextView)findViewById(R.id.comment_tv_total_score);
        tv_five_star=(TextView)findViewById(R.id.comment_tv_five_people);
        tv_four_star=(TextView)findViewById(R.id.comment_tv_four_people);
        tv_three_star=(TextView)findViewById(R.id.comment_tv_three_people);
        tv_two_star=(TextView)findViewById(R.id.comment_tv_two_people);
        tv_one_star=(TextView)findViewById(R.id.comment_tv_one_people);


        String score=getIntent().getStringExtra("score");

        NumberFormat format=NumberFormat.getInstance();
        format.setMaximumFractionDigits(1);
        format.setMinimumFractionDigits(1);
        //设置餐厅的总体得分
        tv_total_score.setText(format.format(Float.parseFloat(score)));


        getSupportFragmentManager().beginTransaction().add(R.id.comment_fragmentlayout,BadNotReplyFragment.newInstance(),BadNotReplyFragment.tag).commit();


        if (Status.isIsConnectNet()){
            fetchData();
        }





    }


    private void fetchData(){

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("shopId",CodeUtil.getShopId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog= DialogUtil.showProgressDialog(this,"加载中...");

        new HttpUtil(this).post(Share.url_shop_comment, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();

                if (!isOk)
                    return;

                try {
                    freigntUI(jsonObject);
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

    private void freigntUI(JSONObject jsonObject)throws JSONException{



        tv_five_star.setText(jsonObject.getString("fiveStar"));
        tv_four_star.setText(jsonObject.getString("fourStar"));
        tv_three_star.setText(jsonObject.getString("threeStar"));
        tv_two_star.setText(jsonObject.getString("twoStar"));
        tv_one_star.setText(jsonObject.getString("oneStar"));
        tv_total_people.setText(jsonObject.getString("total"));


        //餐厅平均配送时间
        tv_delivery_time.setText(String.format("配送时间：%d分钟",jsonObject.getInt("minute")));



    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("餐厅评价");

        MScrollView mScrollView= (MScrollView)LayoutInflater.from(this).inflate(R.layout.activity_shop_comment,null);

        setContentView(mScrollView);

        initView();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_shop_comment, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
