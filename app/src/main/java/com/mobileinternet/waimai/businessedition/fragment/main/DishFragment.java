package com.mobileinternet.waimai.businessedition.fragment.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.activity.Dish.CaiListActivity;
import com.mobileinternet.waimai.businessedition.activity.Dish.CatModifyActivity;
import com.mobileinternet.waimai.businessedition.adapter.DishCatAdapter;
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

public class DishFragment extends Fragment implements AdapterView.OnItemClickListener{

    private DragSortListView listView;
    private DishCatAdapter adapter;
    private List<DishCatAdapter.CaiCatInfo> ls_data=new ArrayList<>();
    private TextView tv_edit;


    public static DishFragment newInstance() {
        DishFragment fragment = new DishFragment();
        return fragment;
    }

    public DishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_dish, container, false);
        initView(view);
        return view;

    }


    private void initView(View view){

        tv_edit=(TextView)view.findViewById(R.id.dish_tv_edit);

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

                    if (CodeUtil.checkNetState(DishFragment.this.getActivity())) {
                        //点击编辑
                        tv_edit.setText("完成");
                        adapter.setEditNow(true);
                    }

                }

                hasClick = !hasClick;

            }
        });



        listView=(DragSortListView)view.findViewById(R.id.dish_listview);
        adapter=new DishCatAdapter(this.getActivity(),R.layout.item_dish_cat_list,ls_data);
        listView.setAdapter(adapter);

        adapter.setOnDeleteListener(new DishCatAdapter.OnDragSortLinstener() {
            @Override
            public void onDelete(int postion) {
                deleteOne(postion);
            }

            @Override
            public void onEdit(int position) {
                Intent intent = new Intent(DishFragment.this.getActivity(), CatModifyActivity.class);
                DishCatAdapter.CaiCatInfo info = ls_data.get(position);
                intent.putExtra("name", info.name);
                intent.putExtra("des", info.des);
                intent.putExtra("position", position);
                intent.putExtra("id", info.id);

                startActivityForResult(intent, 100);

            }
        });

        listView.setOnItemClickListener(this);


        if(CodeUtil.checkNetState(this.getActivity())) {
            fetchData();
        }



    }

    private void freighUI(JSONObject jsonObject)throws JSONException{
        JSONArray array=jsonObject.getJSONArray("data");
        int size=array.length();

        if (size==0)
        {
            CodeUtil.toast(this.getActivity(),"还没有任何分类");
            return;
        }

        for(int i=0;i<size;i++){

            JSONObject cat=array.getJSONObject(i);
            DishCatAdapter.CaiCatInfo info=new DishCatAdapter.CaiCatInfo();
            info.id=cat.getString("id");
            info.name=cat.getString("name");
            info.cai_sum=Integer.parseInt(cat.getString("disheNum"));
            info.serial=i;
            info.des=cat.getString("des");
            ls_data.add(info);
        }

        adapter.notifyDataSetChanged();



    }


    private void fetchData(){


        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this.getActivity());

        final Dialog dialog=DialogUtil.showProgressDialog(this.getActivity(), "加载中...");
        new HttpUtil(this.getActivity()).post(Share.url_cat_dish_info, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();
                if (!isOk)
                    return;

                try {
                    freighUI(jsonObject);
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


    /*
    *
    * 完成编辑后，执行的操作
    *
    * */
    private void completeEdit() throws JSONException {


        /**
         * 判断菜品分类的顺序是否有更改
         */
        int si=ls_data.size();
        int length=0;
        for (;length<si;length++){
            if (length!=ls_data.get(length).serial)
                break;
        }

        //没有更改，则返回
        if (length==si) {
            //点击完成
            tv_edit.setText("编辑");
            adapter.setEditNow(false);

            return;
        }


        if (!CodeUtil.checkNetState(this.getActivity()))
            return;


        final Dialog dialog= DialogUtil.showProgressDialog(this.getActivity(),"分类显示顺序修改中...");

        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this.getActivity());

        JSONArray array=new JSONArray();

        int size=ls_data.size();
        for(int i=0;i<size;i++){

            DishCatAdapter.CaiCatInfo info=ls_data.get(i);
            JSONObject cat=new JSONObject();
            cat.put("id",info.id);
            cat.put("order",i);
            array.put(cat);

        }

        jsonObject.put("data",array);

        new HttpUtil(this.getActivity()).post(Share.url_cat_modify_cat_order, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();
                if (!isOk) {
                    return;
                }
                try {
                    freighUI(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //修改修改更新后的菜品序号
                for (int j=0;j<ls_data.size();j++){
                    ls_data.get(j).serial=j;
                }

                //点击完成
                tv_edit.setText("编辑");
                adapter.setEditNow(false);

                CodeUtil.toast(DishFragment.this.getActivity(),"修改成功");

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });




    }


    /*
    *
    * 点击删除后，执行的操作
    *
    * */
    private void deleteOne(final int position){

        if (!CodeUtil.checkNetState(this.getActivity()))
            return;


            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("catId",ls_data.get(position).id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        final Dialog dialog=DialogUtil.showProgressDialog(this.getActivity(),"删除中...");

            new HttpUtil(this.getActivity()).post(Share.url_cat_delete, jsonObject,
                    new HttpUtil.OnHttpListener() {
                @Override
                public void onSucess(JSONObject jsonObject, boolean isOk) {
                    dialog.dismiss();
                    if (!isOk)
                        return;

                    ls_data.remove(position);
                    adapter.notifyDataSetChanged();
                    CodeUtil.toast(DishFragment.this.getActivity(),"删除成功");

                }

                @Override
                public void onFailre(String s) {
                    dialog.dismiss();
                }
            });




    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode!=200)
            return;



        if (requestCode==100) {
            int position = data.getIntExtra("positon", 0);
            String name=data.getStringExtra("name");
            String des=data.getStringExtra("des");

            DishCatAdapter.CaiCatInfo info = ls_data.get(position);
            info.name=name;
            info.des=des;
            adapter.notifyDataSetChanged();
            return;
        }


        if (requestCode==120){
            String name=data.getStringExtra("name");
            String des=data.getStringExtra("des");
            String id=data.getStringExtra("id");
            DishCatAdapter.CaiCatInfo info=new DishCatAdapter.CaiCatInfo();
            info.des=des;
            info.name=name;
            info.id=id;
            info.cai_sum=0;
            info.serial=ls_data.size();
            ls_data.add(info);
            adapter.notifyDataSetChanged();
            return;

        }


        if (requestCode==140){

            int positon=data.getIntExtra("position",0);
            int sum=data.getIntExtra("sum",0);
            DishCatAdapter.CaiCatInfo info=ls_data.get(positon);
            info.cai_sum=sum;
            adapter.notifyDataSetChanged();

            return;

        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        if (adapter.getEditNowState())
            return;

        Intent intent=new Intent(DishFragment.this.getActivity(), CaiListActivity.class);
        DishCatAdapter.CaiCatInfo info=ls_data.get(position);

        intent.putExtra("id",info.id);
        intent.putExtra("name",info.name);
        intent.putExtra("position", position);
        startActivityForResult(intent, 140);

    }
}
