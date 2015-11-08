package com.mobileinternet.waimai.businessedition.util;

import java.util.UUID;

/**
 * Created by Administrator on 2015/3/30.
 */
public class UUIDUtil {
    public static String getUUID (){
        UUID uuid =UUID.randomUUID();
        return  uuid.toString().replaceAll("-","");
    }
}
