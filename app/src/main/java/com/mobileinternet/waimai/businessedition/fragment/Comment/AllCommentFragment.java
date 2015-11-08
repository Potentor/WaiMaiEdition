package com.mobileinternet.waimai.businessedition.fragment.Comment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.adapter.CommentAdapter;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.MyListView2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllCommentFragment extends Fragment {

    public static final String tag="allComment";



    private MyListView2 listView;
    private CommentAdapter adapter;
    private List<CommentAdapter.CommentInfo> ls_data=new ArrayList<>();
    private String loadDate;



    private void freightUI(JSONObject jsonObject)throws JSONException{

        JSONArray array=jsonObject.getJSONArray("data");
        int size=array.length();
        if (size==0){
            CodeUtil.toast(this.getActivity(), "已加载完全部数据");
            return;
        }


        for(int i=0;i<size;i++) {

            JSONObject data = array.getJSONObject(i);

            CommentAdapter.CommentInfo info = new CommentAdapter.CommentInfo();
            info.user=data.getString("orderuser");
            info.id = data.getString("orderId");
            info.dispatchTime = data.getInt("cosmTime");
            info.orderDate = data.getString("orderTime");
            info.content_comment = data.getString("content");
            info.stars = data.getInt("star");
            info.hasReply = data.getBoolean("isReply");
            if (info.hasReply) {
                info.content_reply = data.getString("reply");
            }


            JSONArray dishArray = data.getJSONArray("dish");
            int length = dishArray.length();
            for (int j = 0; j < length; j++) {

                info.ls_dish.add(dishArray.getString(j));
            }

            ls_data.add(info);

            }

        loadDate = DateUtil.subtractOneMinute(ls_data.get(ls_data.size()-1).orderDate);
        adapter.notifyDataSetChanged();


    }





    private void fetchData(){


        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this.getActivity());
        try {
            jsonObject.put("time",loadDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this.getActivity()).post(Share.url_comment_all, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                listView.setStateNoMoreData();
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
                listView.setStateNoMoreData();
            }
        });


    }



    private void initView(View view){

        listView=(MyListView2)view.findViewById(R.id.not_reply_list);
        adapter=new CommentAdapter(this.getActivity(),ls_data);
        listView.setAdapter(adapter);


        listView.setOnGetToButtomListener(new MyListView2.OnGetToButtomListener() {
            @Override
            public void onButtomAndNetOk() {
                fetchData();
            }
        });

        listView.closeAutoLoad();


        if (Status.isIsConnectNet()) {
            loadDate= DateUtil.getNowDate();
            fetchData();
        }

    }





    public static AllCommentFragment newInstance() {
        AllCommentFragment fragment = new AllCommentFragment();
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_comment_base2, container, false);
        initView(view);
        return view;
    }



}
