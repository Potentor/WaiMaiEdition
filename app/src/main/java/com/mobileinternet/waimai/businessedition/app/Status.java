package com.mobileinternet.waimai.businessedition.app;

import java.util.ArrayList;

/**
 * Created by 海鸥2012 on 2015/7/17.
 *
 *
 * 记录应用的运行时的一些基本状态，便于应用程序随时了解环境变化
 */
public class Status {


    /**
     * 是否支持休息时间预定
     */
    public static boolean isIsSupportRelaxTime=true;
    /**
     * 营业时间周
     */
    public static boolean[] busi_week=new boolean[]{false,false,false,false,false,false,false};
    /**
     * 一天中的营业时间段
     */
    public static ArrayList<String> busiTime=new ArrayList<>();
    /**
     * 是否已经从服务器上获取的营业时间
     */
    public static boolean hasGetBusinessTime=false;
    /**
     * 是否强行停止营业
     *
     * 当用户强行停止营业，则此变量为true
     *
     * 默认为false
     *
     * 用户可在ShopStatusActivity中改变此值
     *
     */
    public static boolean isForceStopBusiness=false;






    private static boolean isConfigFileDamage=true;

    private static boolean isConnectNet=true;

    /**
     * 1.营业状态
     *
     * 2.预定状态
     *
     * 3.休息中
     */
    private static int business_status=1;































    public static boolean isIsConfigFileDamage() {
        return isConfigFileDamage;
    }

    public static void setIsConfigFileDamage(boolean isConfigFileDamage) {
        Status.isConfigFileDamage = isConfigFileDamage;
    }

    public static boolean isIsConnectNet() {
        return isConnectNet;
    }

    public static void setIsConnectNet(boolean isConnectNet) {
        Status.isConnectNet = isConnectNet;
    }

    public static int getBusiness_status() {
        return business_status;
    }

    public static void setBusiness_status(int business_status) {
        Status.business_status = business_status;
    }
}
