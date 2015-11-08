package com.mobileinternet.waimai.businessedition.fragment.OrderCenter;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.mobileinternet.waimai.businessedition.app.Share;


/**
 * 订单中心中；昨天、前天fragment
 */
public class YestFragment extends COBaseFragment {


    public static Handler handler;

    @Override
    protected void initURL() {
        url= Share.url_yesterday_order;

        handler=new Handler(){
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);

                if (msg.what==200){
                    fetchData();
                }
            }
        };
    }

    public static Fragment newInstance(){
        return  new YestFragment();
    }


}
