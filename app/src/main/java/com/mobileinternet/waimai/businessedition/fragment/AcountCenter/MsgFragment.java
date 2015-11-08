package com.mobileinternet.waimai.businessedition.fragment.AcountCenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.activity.Acount.MsgDetailActivity;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DateUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.view.AutoRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 各种消息公有fragement
 */
public class MsgFragment extends Fragment {


    private int type=0;
    private AutoRefreshListView listView;
    private List<MsgInfo> ls_data=new ArrayList<>();
    private MAdapter adapter;
    private String loadDate;
    private TextView tv_load_result;
    private ProgressBar pb_load;

    private boolean hasShowOnce=false;
    private boolean hasInitView=false;
    private boolean isFirstPage=false;








    /**
     * 第一次从服务器上拉去数据
     */
    private void fetchData(){

        JSONObject jsonObject= CodeUtil.getJsonOnlyShopId(this.getActivity());

        String url=null;

        try {

            jsonObject.put("time",loadDate);

            switch (type)
            {
                case 0:
                    url= Share.url_all_msg;
                    break;
                case 1:
                    jsonObject.put("type",1);
                    url=Share.url_every_msg;
                    break;
                case 2:
                    jsonObject.put("type",2);
                    url=Share.url_every_msg;
                    break;
                case 3:
                    jsonObject.put("type",3);
                    url=Share.url_every_msg;
                    break;
//                case 4:
//                    url=Share.url_shop_msg;
//                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        new HttpUtil(this.getActivity()).post(url, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {


                if (!isOk) {
                    if (ls_data.size()==0){
                        setReloadAvailable();
                    }else{
                        setReloadGone();
                    }
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

                if (ls_data.size()==0){
                    setReloadAvailable();
                }else{
                    setReloadGone();
                }
            }
        });







    }


    private void freightUI(JSONObject jsonObject)throws JSONException{


        JSONArray array=jsonObject.getJSONArray("data");
        int size=array.length();

        if (size==0){

            if (ls_data.size()==0){
                setReloadAvailable();
            }else{
                setReloadGone();
            }
            return;
        }


        for (int i=0;i<size;i++){
            JSONObject msg=array.getJSONObject(i);
            MsgInfo info=new MsgInfo();
            info.id=msg.getString("id");
            info.content=msg.getString("title");
            info.date=msg.getString("dateline");
            ls_data.add(info);
        }

        setReloadGone();


        loadDate=DateUtil.subtractOneMinute(ls_data.get(ls_data.size()-1).date);
        adapter.notifyDataSetChanged();


    }





    private void initView(View view){

        tv_load_result=(TextView)view.findViewById(R.id.msg_tv_load_result);
        pb_load=(ProgressBar)view.findViewById(R.id.msg_pb_load);
        listView=(AutoRefreshListView)view.findViewById(R.id.msg_listview);
        adapter=new MAdapter();

        listView.setOnGetToButtomListener(new AutoRefreshListView.OnGetToButtomListener() {
            @Override
            public void onButtomAndNetOk() {
                fetchData();
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                MsgInfo info = ls_data.get(i);
                Intent intent = new Intent(MsgFragment.this.getActivity(), MsgDetailActivity.class);
                intent.putExtra("id", info.id);
                //  intent.putExtra("content", info.content);
                //intent.putExtra("type", type);
                startActivity(intent);

            }
        });

        listView.setIfAutoScroll(false);


        tv_load_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickToLoad();
            }
        });

        listView.setAdapter(adapter);


        //OnCreateView方法已经执行，所有的view引用已经拿到
        hasInitView=true;
        if (isFirstPage){
            if (Status.isIsConnectNet()){
                loadDate=DateUtil.getNowDate();
                setRefreshing();
                fetchData();
            }else{
                setReloadAvailable();
            }
            hasShowOnce = true;

        }





    }


    private void setReloadAvailable(){
        listView.setVisibility(View.GONE);
        tv_load_result.setVisibility(View.VISIBLE);
        pb_load.setVisibility(View.GONE);
    }

    private void setReloadGone(){
        listView.setVisibility(View.VISIBLE);
        tv_load_result.setVisibility(View.GONE);
        pb_load.setVisibility(View.GONE);
        listView.setStateNoMoreData();
    }

    private void setRefreshing(){
        listView.setVisibility(View.GONE);
        tv_load_result.setVisibility(View.GONE);
        pb_load.setVisibility(View.VISIBLE);
    }


     /**
     * 点击重试后执行的方法
     */
    private void clickToLoad(){

        setRefreshing();

        if (CodeUtil.checkNetState(this.getActivity())) {
            loadDate=DateUtil.getNowDate();
            fetchData();
        }else{
           setReloadAvailable();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (type==0){
            isFirstPage=true;
            return;
        }


        if (!hasShowOnce&&isVisibleToUser) {
            if (hasInitView) {
                if (Status.isIsConnectNet()) {
                    loadDate=DateUtil.getNowDate();
                   setRefreshing();
                    hasShowOnce = true;
                    fetchData();
                }else{
                    setReloadAvailable();
                }
            }
        }

    }

    private class MsgInfo{
        public String id;
        public String content;
        public String date;
    }



    private class MAdapter extends BaseAdapter{

        private View generateView(View view,int position){

            if (view==null){
                view=LayoutInflater.from(MsgFragment.this.getActivity()).inflate(R.layout.item_msg_list,null);
            }

                MsgInfo info=ls_data.get(position);
                ((TextView)view.findViewById(R.id.msg_item_tv_content)).setText(info.content);
                ((TextView)view.findViewById(R.id.msg_item_tv_date)).setText(info.date);


            return view;
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














    public static MsgFragment newInstance( int type) {
        MsgFragment fragment = new MsgFragment();
        fragment.setType(type);
        return fragment;
    }


    public void setType(int type){
        this.type=type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_base, container, false);
        initView(view);
        return view;
    }



}
