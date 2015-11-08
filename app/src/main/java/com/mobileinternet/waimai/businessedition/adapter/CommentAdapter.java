package com.mobileinternet.waimai.businessedition.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

    /**
     * Created by 海鸥2012 on 2015/7/25.
     */
    public class CommentAdapter extends BaseAdapter {


        private List<CommentInfo> ls_data;
        private Context mContext;



        public CommentAdapter(Context context,List<CommentInfo> ls_data){
            this.mContext=context;
            this.ls_data=ls_data;
        }


        /**
     * 设置数据源
     * @param ls_data
     */
    public void setLs_data(List<CommentInfo> ls_data){
        this.ls_data=ls_data;
        this.notifyDataSetChanged();
    }


    /**
     * 向数据尾部添加新数据
     * @param extraData
     */
    public void addData(List<CommentInfo> extraData){
        ls_data.addAll(ls_data.size(),extraData);
        this.notifyDataSetChanged();
    }








    private View generateView(View view ,int position){



        if (view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.item_comment_list,null);
        }

            final CommentInfo info=ls_data.get(position);



            ((TextView)view.findViewById(R.id.comment_order_tv_dispatch)).setText(info.dispatchTime+"分钟");
            ((TextView)view.findViewById(R.id.comment_order_tv_time)).setText(info.orderDate);
            ((TextView)view.findViewById(R.id.comment_order_tv_comment)).setText(info.content_comment);



            String str_user=String.format("%s***%s",info.user.charAt(0),info.user.charAt(info.user.length()-1));
            ((TextView)view.findViewById(R.id.comment_order_tv_user)).setText(str_user);

            final TextView tv_reply=(TextView) view.findViewById(R.id.comment_order_tv_reply);
            View bt_reply=view.findViewById(R.id.comment_order_bt);

            //判断是否商家已回复
            if (info.hasReply) {
                tv_reply.setText(info.content_reply);
                tv_reply.setVisibility(View.VISIBLE);
                bt_reply.setVisibility(View.GONE);

            }else{
                tv_reply.setVisibility(View.GONE);
                bt_reply.setVisibility(View.VISIBLE);
                //点击回复事件
                bt_reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                      clickReply(tv_reply,view,info.id);
                    }
                });
            }

            final LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.comment_order_ll_detail);
            int size=info.ls_dish.size();
            linearLayout.removeViews(1,linearLayout.getChildCount()-1);


            TextView tv_order_click=(TextView)view.findViewById(R.id.comment_order_tv_detail);

            //订单详情
            if (info.hasClickDetail){
                Drawable drawable=mContext.getResources().getDrawable(R.drawable.ic_collapse_small);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_order_click.setCompoundDrawables(null, null, drawable, null);

                    for(int i=0;i<size;i++){
                        TextView tv_dish=(TextView)LayoutInflater.from(mContext).inflate(R.layout.view_order_detail_textview,null);
                        tv_dish.setText(info.ls_dish.get(i));
                        linearLayout.addView(tv_dish, i + 1);
                        tv_dish.setVisibility(View.VISIBLE);

                    }
            }else{
                Drawable drawable=mContext.getResources().getDrawable(R.drawable.ic_expand_small);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                tv_order_click.setCompoundDrawables(null, null, drawable, null);

                    for(int i=0;i<size;i++){
                        TextView tv_dish=(TextView)LayoutInflater.from(mContext).inflate(R.layout.view_order_detail_textview,null);
                        tv_dish.setText(info.ls_dish.get(i));
                        linearLayout.addView(tv_dish, i + 1);
                        tv_dish.setVisibility(View.GONE);

                    }
            }


            //点击订单详情事件
            tv_order_click.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (!info.hasClickDetail){
                        //现在展开
                        int size=linearLayout.getChildCount();
                        for(int i=1;i<size;i++){
                            linearLayout.getChildAt(i).setVisibility(View.VISIBLE);

                        }

                        Drawable drawable=mContext.getResources().getDrawable(R.drawable.ic_collapse_small);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        ((TextView)view).setCompoundDrawables(null, null, drawable, null);


                    }else{
                        //现在关闭
                        int size=linearLayout.getChildCount();
                        for(int i=1;i<size;i++){
                            linearLayout.getChildAt(i).setVisibility(View.GONE);
                        }

                        Drawable drawable=mContext.getResources().getDrawable(R.drawable.ic_expand_small);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                        ((TextView)view).setCompoundDrawables(null,null,drawable,null);

                    }
                    info.hasClickDetail=!info.hasClickDetail;

                  //  stateIsOpenPrevious=!stateIsOpenPrevious;

                }
            });


            //根据评论的星星数，改变成相应的界面
                ViewGroup include=(ViewGroup)view.findViewById(R.id.comment_order_inlude);


                //int start=5-info.stars;
                for (int i=info.stars;i<5;i++){

                    ImageView imageView=(ImageView)include.getChildAt(i);
                    if(imageView==null)
                        continue;

                    imageView.setImageResource(R.drawable.ic_comment_star_small_empty);

            }


        return view;

    }


    /**
     * 点击回复按钮后，执行的方法
     * @param tv_reply
     * @param click
     */
    private void clickReply(final TextView tv_reply,final View click,final String id){

        final EditText editText=(EditText)LayoutInflater.from(mContext).inflate(R.layout.view_edittext,null);

        Dialog dialog=new AlertDialog.Builder(mContext,R.style.edit_dialog)
                .setView(editText)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

//                        tv_reply.setText();
//                        tv_reply.setVisibility(View.VISIBLE);
//                        click.setVisibility(View.GONE);
                        String content=editText.getText().toString();
                        if ("".equals(content)||"".equals(content.trim())){
                            CodeUtil.toast(mContext,"回复不能为空");
                            return;
                        }

                        replay(content,tv_reply,click,id);


                    }
                })
                .setNegativeButton("取消",null)
                .setTitle("回复")
                .create();
        dialog.show();
        dialog.getWindow().setLayout(500, 400);
        dialog.getWindow().setGravity(Gravity.CENTER);


    }


        /**
         * 提交回复
         * @param content
         * @param tv_reply
         * @param click
         * @param id
         */

        private void replay(final String content,final TextView tv_reply, final View click,String id){


            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("orderId",id);
                jsonObject.put("content",content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new HttpUtil(this.mContext).post(Share.url_comment_replay, jsonObject, new HttpUtil.OnHttpListener() {
                @Override
                public void onSucess(JSONObject jsonObject, boolean isOk) {

                    if (!isOk)
                        return;
                    tv_reply.setText(content);
                    tv_reply.setVisibility(View.VISIBLE);
                    click.setVisibility(View.GONE);

                }

                @Override
                public void onFailre(String s) {

                }
            });





        }


    public static class CommentInfo{

        public String id;//订单id
        public String user;//下单人姓名
        public int dispatchTime;//配送时间
        public String orderDate;//下单时间
        public String content_comment;//评论内容
        public int stars=5;//评论了几个星
        public List<String> ls_dish=new ArrayList<>();//订单中的菜
        public boolean hasReply=false;//是否回复
        public String content_reply;//回复内容
        public boolean hasClickDetail=false;//是否点击了订单详情

    }


    private class ViewHolder{

        public TextView tv_reply;
        public LinearLayout ll_detail;
        public Button bt_reply;


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
        return generateView(view,i);
    }
}
