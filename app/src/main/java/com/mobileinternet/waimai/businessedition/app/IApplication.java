package com.mobileinternet.waimai.businessedition.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by 海鸥2012 on 2015/7/19.
 */
public class IApplication extends Application {


    /*
      * 存储应用运行中需要的基本数据
      * */
    public Map<String,String> map_basic_data=new HashMap<>();




    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences=getSharedPreferences(Share.share_prefrence_name, Activity.MODE_PRIVATE);

        //是可否注销账号
        boolean isLogout=preferences.getBoolean(Share.isLogOut, true);
        if (isLogout){
            Status.setIsConfigFileDamage(true);
            return;
        }


        //用户名
        String user_name=preferences.getString(Share.user_name, "");
        if ("".equals(user_name)){
            Status.setIsConfigFileDamage(true);
            return;
        }
        storeData(Share.user_name, user_name);
        //用户id
        String user_id=preferences.getString(Share.user_id,"");
        if ("".equals(user_id)){
            Status.setIsConfigFileDamage(true);
            return;
        }
        storeData(Share.user_id, user_id);
        //店铺名
        String shop_name=preferences.getString(Share.shop_name,"");
        if ("".equals(shop_name)){
            Status.setIsConfigFileDamage(true);
            return;
        }
        storeData(Share.shop_name, shop_name);
        //店铺id
        String shop_id=preferences.getString(Share.shop_id,"");
        if ("".equals(shop_id)){
            Status.setIsConfigFileDamage(true);
            return;
        }
        storeData(Share.shop_id,shop_id);


        //token
        String token=preferences.getString(Share.token,"");
        if ("".equals(token)){
            Status.setIsConfigFileDamage(true);
            return;
        }
        storeData(Share.token, token);


//        String url_logo=preferences.getString(Share.logo,"");
//
//        if ("".equals(url_logo)){
//            Status.setIsConfigFileDamage(true);
//            return;
//        }
//
//        storeData(Share.logo,url_logo);

        Status.setIsConfigFileDamage(false);

    }




    /*
    * 存储数据
    * */
    public void storeData(String key,String values){
        map_basic_data.put(key,values);
    }


    /*
    * 读取数据
    * */
    public String getData(String key){
        return map_basic_data.get(key);
    }




}
