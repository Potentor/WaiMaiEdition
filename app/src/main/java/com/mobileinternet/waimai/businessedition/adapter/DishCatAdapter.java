package com.mobileinternet.waimai.businessedition.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.interfaces.ISortFace;

import java.util.List;


public class DishCatAdapter extends BaseAdapter implements ISortFace{
    protected static final String TAG = DishCatAdapter.class.getSimpleName();

    private Context context;
    private int layout;
    private List<CaiCatInfo> datas;
    private boolean isEditNow=false;

    private int dragSrcPosition = -1;
    private OnDragSortLinstener mOnDragSortLinstener;


    /**
     * @param context
     * @param layout
     * @param datas
     */
    public DishCatAdapter(Context context, int layout, List<CaiCatInfo> datas) {
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

       // if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(layout, parent, false);
        //}

        if (position == dragSrcPosition) {
            convertView.setVisibility(View.INVISIBLE);
            return convertView;
        }

        CaiCatInfo info=datas.get(position);

        TextView tv_name=(TextView)convertView.findViewById(R.id.item_dish_tv_name);
        TextView tv_sum=(TextView)convertView.findViewById(R.id.item_dish_tv_num);

        tv_name.setText(info.name);
        tv_sum.setText("菜品数："+info.cai_sum);


        //如果是非编辑状态
        if (!isEditNow){
            convertView.findViewById(R.id.item_dish_iv_edit).setVisibility(View.GONE);
            convertView.findViewById(R.id.item_dish_iv_move).setVisibility(View.GONE);
            convertView.findViewById(R.id.item_dish_iv_delete).setVisibility(View.GONE);

            return convertView;
        }else{
            convertView.findViewById(R.id.item_dish_iv_edit).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.item_dish_iv_move).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.item_dish_iv_delete).setVisibility(View.VISIBLE);
        }

        //点击删除
        convertView.findViewById(R.id.item_dish_iv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               final AlertDialog dialog=new AlertDialog.Builder(context).setMessage("将删除包括的所有菜品!")
                        .setPositiveButton("删除",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mOnDragSortLinstener!=null){
                                    mOnDragSortLinstener.onDelete(position);
                                }
                            }
                        })
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create();
                dialog.show();

            }
        });

        //点击编辑菜品
        convertView.findViewById(R.id.item_dish_iv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnDragSortLinstener!=null){
                    mOnDragSortLinstener.onEdit(position);
                }
            }
        });

        return convertView;
    }

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




    /*
    * 设置删除监听器
    * */
    public void setOnDeleteListener(OnDragSortLinstener deleteListener){
        mOnDragSortLinstener=deleteListener;
    }


    //设置编辑状态
    public void setEditNow(boolean isEditNow){
        this.isEditNow=isEditNow;
       this.notifyDataSetInvalidated();
    }


    /*
    *
    * 获取此刻是否被编辑状态
    *
    * */
    public boolean getEditNowState(){
        return this.isEditNow;
    }

    @Override
    public void setDragSrcPosition(int position) {
        this.dragSrcPosition = position;
    }

    @Override
    public void moveItem(int src, int des) {
        if (src == des){
            notifyDataSetChanged();
            return;
        }
        CaiCatInfo movingItem = datas.get(src);
        datas.remove(src);
        datas.add(des, movingItem);
        notifyDataSetChanged();
    }


    public static class CaiCatInfo{

        public String id;
        public String name;
        public String des;
        public int serial;
        public int cai_sum;


    }

    public interface OnDragSortLinstener{

         void onDelete(int position);

         void onEdit(int position);
    }
}