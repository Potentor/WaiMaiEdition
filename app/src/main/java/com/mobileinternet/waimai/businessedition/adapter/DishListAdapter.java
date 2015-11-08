package com.mobileinternet.waimai.businessedition.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.interfaces.ISortFace;
import com.mobileinternet.waimai.businessedition.view.smartiv.SmartImageView;

import java.util.List;

public class DishListAdapter extends BaseAdapter implements ISortFace {

    private Context context;
    private int layout;
    private List<CaiListInfo> datas;
    private boolean isEditNow = false;

    private int dragSrcPosition = -1;

    private int selectPostion = -1;//用户选中的位置


    /**
     * @param context
     * @param layout
     * @param datas
     */
    public DishListAdapter(Context context, int layout, List<CaiListInfo> datas) {
        super();
        this.context = context;
        this.layout = layout;
        this.datas = datas;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (position == dragSrcPosition) {
            convertView.setVisibility(View.INVISIBLE);
            return convertView;
        }

       // if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, parent, false);
            RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.item_dish_rb);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPostion = position;
                    notifyDataSetChanged();
                }
            });
     //   }

        convertView.setVisibility(View.VISIBLE);

        CaiListInfo info = datas.get(position);

        TextView tv_name = (TextView) convertView.findViewById(R.id.item_dish_tv_name);
        TextView tv_price = (TextView) convertView.findViewById(R.id.item_dish_tv_price);


        tv_name.setText(info.name);
        tv_price.setText("￥" + info.price);

        if (info.isSale==0){
            convertView.setBackgroundColor(Color.GRAY);
        }else {
            convertView.setBackgroundResource(R.drawable.ic_list_item_normal);
        }


        TextView tv_guige=(TextView)convertView.findViewById(R.id.item_dish_tv_guige);
        if (info.hasGuige){
            tv_guige.setVisibility(View.VISIBLE);
        }else{
            tv_guige.setVisibility(View.INVISIBLE);
        }


        if ("".equals(info.url_image)) {
            ((SmartImageView) convertView.findViewById(R.id.item_dish_iv_image)).setImageResource(R.drawable.img_food_info_default_big);
        }else{
            ((SmartImageView) convertView.findViewById(R.id.item_dish_iv_image)).setImageUrl(info.url_image);
        }


        //如果是非编辑状态
        if (!isEditNow) {
            convertView.findViewById(R.id.item_dish_iv_move).setVisibility(View.GONE);
            convertView.findViewById(R.id.item_dish_rb).setVisibility(View.GONE);

        } else {
            convertView.findViewById(R.id.item_dish_iv_move).setVisibility(View.VISIBLE);
           // RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.item_dish_rb);
            radioButton.setVisibility(View.VISIBLE);
            radioButton.setChecked(position == selectPostion);
        }


        return convertView;
    }


//    private class ViewHolder{
//
//        public RadioButton rb;
//        public TextView tv_name;
//        public TextView tv_price;
//        public SmartImageView iv_image;
//        public ImageView iv_move;
//
//    }

    /**
     * @return
     */
    @Override
    public int getCount() {
        return datas.size();
    }

    /**
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }


    //设置编辑状态
    public void setEditNow(boolean isEditNow) {
        this.isEditNow = isEditNow;
        this.notifyDataSetInvalidated();
    }


    /*
    *
    * 获取此刻是否被编辑状态
    *
    * */
    public boolean getEditNowState() {
        return this.isEditNow;
    }

    /**
     * 获取当前选中的位置
     *
     * @return
     */
    public int getSelectPosition() {
        return this.selectPostion;
    }

    @Override
    public void setDragSrcPosition(int position) {
        this.dragSrcPosition = position;
    }

    @Override
    public void moveItem(int src, int des) {
        if (src == des) {
            notifyDataSetChanged();
            return;
        }
        CaiListInfo movingItem = datas.get(src);
        datas.remove(src);
        datas.add(des, movingItem);
        selectPostion = des;
        notifyDataSetChanged();

    }


    public static class CaiListInfo {

        public String id;
        public String name;
        public float price;
        public String url_image;
        public int serical;
        public int isSale = 1;  //0.停售  1.开售
        public boolean hasGuige=false;

    }

}