package com.mobileinternet.waimai.businessedition.activity.Dish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.DishListAdapter;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.DragSortListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CaiListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{





    private DragSortListView listView;
    private DishListAdapter adapter;
    private List<DishListAdapter.CaiListInfo> ls_data=new ArrayList<>();
    private String id;
    private int cat_position;
    private View v_bottom;//底部操作栏
    private TextView tv_edit;

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent=getIntent();
        intent.putExtra("sum",ls_data.size());
        intent.putExtra("position",cat_position);
        setResult(200,intent);
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Intent intent=getIntent();
        intent.putExtra("sum",ls_data.size());
        intent.putExtra("position",cat_position);
        setResult(200,intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        cat_position=intent.getIntExtra("position",0);


        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(intent.getStringExtra("name"));

        v_bottom=findViewById(R.id.dish_list_ll_bottom);


        tv_edit=(TextView)findViewById(R.id.dish_tv_edit);
        tv_edit.setOnClickListener(new View.OnClickListener() {

            boolean hasClick = false;

            @Override
            public void onClick(View view) {

                if (hasClick) {
                    try {
                        completeEdit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                } else {
                    v_bottom.setVisibility(View.VISIBLE);
                    //点击编辑
                    tv_edit.setText("完成");
                    adapter.setEditNow(true);

                }

                hasClick = !hasClick;

            }
        });



        listView=(DragSortListView)findViewById(R.id.dish_listview);
        adapter=new DishListAdapter(this,R.layout.item_dish_list_list,ls_data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);


        if(CodeUtil.checkNetState(this)) {
            fetchData();
        }

    }



    private void freightUI(JSONObject jsonObject)throws JSONException{

        JSONArray array=jsonObject.getJSONArray("data");
        int size=array.length();

        if (size==0){
            CodeUtil.toast(this,"此分类还没有任何菜品");
            return;
        }

        for (int i=0;i<size;i++){

            JSONObject dish=array.getJSONObject(i);
            DishListAdapter.CaiListInfo info=new DishListAdapter.CaiListInfo();
            info.name=dish.getString("title");
            info.id=dish.getString("id");
            info.price=(float) dish.getDouble("price");

            String image=dish.getString("thum_img");
            String url_image=java.net.URLDecoder.decode(image);
            info.url_image=url_image;

            info.serical=i;
            info.isSale=dish.getInt("isSale");
            info.hasGuige=dish.getBoolean("hasGuige");
            ls_data.add(info);
        }

        adapter.notifyDataSetChanged();


    }

    /**
     * 获取服务器数据
     */
    private void fetchData(){



        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("catgoyId",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this,"加载中...");
        new HttpUtil(this).post(Share.url_dish_list, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();
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
                dialog.dismiss();
            }
        });









    }


    /**
     * 完成编辑后，只有顺序的调整
     */
    private void completeEdit() throws JSONException {


        int length=ls_data.size();

        int j=0;
        for (;j<length;j++){
            if (j!=ls_data.get(j).serical)
                break;
        }
        if (length==j) {
            //点击完成
            tv_edit.setText("编辑");
            v_bottom.setVisibility(View.GONE);
            adapter.setEditNow(false);
            return;
        }


        //判断是否连接网络
        if (!CodeUtil.checkNetState(this))
            return;





        //构造请求参数
        JSONObject jsonObject=new JSONObject();
        JSONArray array=new JSONArray();
        int size=ls_data.size();
        for(int i=0;i<size;i++){

            DishListAdapter.CaiListInfo info=ls_data.get(i);
            JSONObject cai=new JSONObject();
            cai.put("id",info.id);
            cai.put("order", i);
            array.put(cai);

        }
        jsonObject.put("data",array);


        //提交到服务器
        final Dialog dialog= DialogUtil.showProgressDialog(this, "显示顺序修改中...");
        new HttpUtil(this).post(Share.url_dish_modify_order, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();
                if (!isOk) {
                    return;
                }

                //修改修改更新后的菜品序号
                for (int j=0;j<ls_data.size();j++){
                    ls_data.get(j).serical=j;
                }

                tv_edit.setText("编辑");
                v_bottom.setVisibility(View.GONE);
                adapter.setEditNow(false);
                CodeUtil.toast(CaiListActivity.this, "修改成功");

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });



    }



    public void deleteDish(View view){

        final int position =adapter.getSelectPosition();
        if (position==-1){
            return;
        }

        AlertDialog dialog=new AlertDialog.Builder(this)
                            .setMessage("确定删除此菜品")
                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    delete(position);
                                }
                            })
                            .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
        dialog.show();
    }

    /**
     * 删除菜品
     */
    private void delete(final int position){

        if (!CodeUtil.checkNetState(this)){
            return;
        }

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("dishId",ls_data.get(position).id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this,"删除中...");

        new HttpUtil(this).post(Share.url_dish_delete, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();
                if (!isOk)
                    return;
                ls_data.remove(position);
                adapter.notifyDataSetChanged();
                CodeUtil.toast(CaiListActivity.this,"删除成功");

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });


    }

    /**
     * 停售菜品
     */
    public void stopSale(View view){
        final int position =adapter.getSelectPosition();
        if (position==-1){
            return;
        }

        if (ls_data.get(position).isSale==0){
            CodeUtil.toast(this,"菜品已在停售状态");
            return;
        }

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("dishId",ls_data.get(position).id);
            jsonObject.put("cmd",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this,"正在停止菜品销售...");

        new HttpUtil(this).post(Share.url_dish_sail_stop, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();
                if (!isOk)
                    return;

                CodeUtil.toast(CaiListActivity.this,"已停售");
                ls_data.get(position).isSale=0;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });





    }


    /**
     * 恢复菜品的出售
     */
    public void restoreSale(View view){

        final int position =adapter.getSelectPosition();
        if (position==-1){
            return;
        }

        if (ls_data.get(position).isSale==1){
            CodeUtil.toast(this,"菜品已在销售状态");
            return;
        }

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("dishId",ls_data.get(position).id);
            jsonObject.put("cmd",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this,"菜品开启中...");

        new HttpUtil(this).post(Share.url_dish_sail_stop, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();
                if (!isOk)
                    return;

                CodeUtil.toast(CaiListActivity.this,"菜品已开启销售");
                ls_data.get(position).isSale=1;
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });




    }


























    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dish_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();

        if (d == R.id.action_settings) {

            Intent intent=new Intent(this,CaiAddActivity.class);
            intent.putExtra("id",this.id);
            startActivityForResult(intent,120);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        if (adapter.getEditNowState())
            return;

        DishListAdapter.CaiListInfo info=ls_data.get(position);


        Intent intent=new Intent(this,CaiModifyActivity.class);
        intent.putExtra("id",info.id);
        intent.putExtra("position",position);
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode!=200)
            return;

        //修改菜品返回
        if (requestCode==100){

            String name=data.getStringExtra("name");
            String image=data.getStringExtra("image");
            float price=data.getFloatExtra("price",0);
            int postion=data.getIntExtra("position",0);
            boolean hasGuige=data.getBooleanExtra("guige",false);



            DishListAdapter.CaiListInfo info=ls_data.get(postion);
            info.url_image=image;
            info.price=price;
            info.name=name;
            info.hasGuige=hasGuige;


            listView.setSelection(postion);

            adapter.notifyDataSetChanged();




            return;
        }

        //新增菜品返回
        if (requestCode==120){


            String name=data.getStringExtra("name");
            String image=data.getStringExtra("image");
            float price=data.getFloatExtra("price",0);
            String id=data.getStringExtra("id");
            boolean hasGuige=data.getBooleanExtra("guige",false);

            DishListAdapter.CaiListInfo info=new DishListAdapter.CaiListInfo();
            info.url_image=image;
            info.price=price;
            info.name=name;
            info.id=id;
            info.serical=ls_data.size();
            info.isSale=1;
            info.hasGuige=hasGuige;
            ls_data.add(info);
            listView.setSelection(listView.getBottom());
            adapter.notifyDataSetChanged();

            return;
        }


    }
}
