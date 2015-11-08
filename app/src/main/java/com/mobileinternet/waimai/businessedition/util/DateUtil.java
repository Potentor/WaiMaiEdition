package com.mobileinternet.waimai.businessedition.util;

import com.mobileinternet.waimai.businessedition.app.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 海鸥2012 on 2015/7/27.
 */
public class DateUtil {


    /**
     * 得到当前时间
     *
     * @return
     */
    public static String getNowDate() {

        Calendar mCalendar = Calendar.getInstance();
        Date mDate = mCalendar.getTime();
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return mDateFormat.format(mDate);
    }


    /**
     * 日期分钟上减一
     *
     * @param date
     * @return
     */
    public static String subtractOneMinute(String date) {

        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date mDate = null;
        try {
            mDate = mDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(mDate);
        mCalendar.add(Calendar.MINUTE, -1);
        Date final_date = mCalendar.getTime();
        return mDateFormat.format(final_date);

    }

    /**
     * 日期分钟上减一
     *
     * @param date
     * @return
     */
    public static String subtractOneDay(String date) {

        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date mDate = null;
        try {
            mDate = mDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(mDate);
        mCalendar.add(Calendar.DAY_OF_MONTH, -1);
        Date final_date = mCalendar.getTime();
        return mDateFormat.format(final_date);

    }


    /**
     * 获取日期中的时间部分
     *
     * @param date
     * @return
     */
    public static String getOnlyTime(String date) {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date mDate = null;
        try {
            mDate = mDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String time = String.format("%s:%s:%s", mDate.getHours(), mDate.getMinutes(), mDate.getSeconds());

        return time;
    }


    public static String getOnlyHourAndMinute(String date) {

        return date.substring(0, date.lastIndexOf(":"));

    }


    public static String getHourAndMinute(String date) {

        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date mDate = null;
        try {
            mDate = mDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int minute=mDate.getMinutes();
        if (minute<10) {

            return mDate.getHours() + ":0" +minute;
        }else{
            return mDate.getHours() + ":" +minute;
        }

    }


    public static String getOnlyHourAndMinuteNow() {

        Calendar mCalendar = Calendar.getInstance();
        Date mDate = mCalendar.getTime();

        int minute=mDate.getMinutes();
        if (minute<10) {

            return mDate.getHours() + ":0" +minute;
        }else{
            return mDate.getHours() + ":" +minute;
        }

    }

    /**
     *
     * @return  1.正在营业 2.预定中  3.休息中
     */
    public static int checkInBusinessTime() {

        if (!Status.hasGetBusinessTime)
            return -1;

        SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm");
        Calendar mCalendare = Calendar.getInstance();





        //检测今天是否营业
        int week=mCalendare.get(Calendar.DAY_OF_WEEK);

        week-=2;
        if(week==-1)
        {
            if(!Status.busi_week[6]) {
                return 3;
            }
        }else if (!Status.busi_week[week]) {
            return 3;
        }



        //检测一天的时间段
        int size = Status.busiTime.size();



        try {

            Date now1 = mCalendare.getTime();

            Date now=mDateFormat.parse(now1.getHours()+":"+now1.getMinutes());



            for (int i = 0; i < size; ) {
                Date start = mDateFormat.parse(Status.busiTime.get(i));
                Date end=mDateFormat.parse(Status.busiTime.get(i+1));



               if(now.before(end)&&now.after(start)) {
                   return 1;
               }

                i++;
                i++;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }



        //判断是否支持在休息状态下接收预定单
        if (Status.isIsSupportRelaxTime) {

            return 2;

        }else{
            return 3;
        }

    }


}
