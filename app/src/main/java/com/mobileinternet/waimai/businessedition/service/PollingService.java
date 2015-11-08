package com.mobileinternet.waimai.businessedition.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PollingService extends Service{



    public static final String ACTION="com.mobileinternet.waimai.businessedition.service.PollingService";

    private boolean isNotFirst=false;

    private JSONObject pollingJson;



    public PollingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (pollingJson==null){

            pollingJson=new JSONObject();
            try {
                pollingJson.put("id",intent.getStringExtra("shopId"));
                pollingJson.put("token",intent.getStringExtra("token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(isNotFirst){

            if (Status.isIsConnectNet()) {
                new PollingThread(this.getApplicationContext()).start();
            }

        }else{
            isNotFirst=true;
        }

        return super.onStartCommand(intent, flags, startId);



    }




    public class PollingThread extends Thread{

        private Context mContext;

        public PollingThread(Context context){
            super();
            this.mContext=context;

        }

        @Override
        public void run() {
            super.run();




                new HttpUtil(mContext).post2(Share.url_polling, pollingJson, new HttpUtil.OnHttpListener() {
                    @Override
                    public void onSucess(JSONObject jsonObject, boolean isOk) {

                        try {
                            int code=jsonObject.getInt("code");
                            if (code==1002||code==1003){
                                /**
                                 *
                                 * 当账号在其他地方登录时，将发送此广播。
                                 *
                                 * 注释掉此代码可以去掉但账号登录功能
                                 *
                                 */
                                Intent intent=new Intent(Share.receivedMsg);
                                intent.putExtra("token",false);
                                sendOrderedBroadcast(intent, null);
                                return;

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        int refund=0;
                        int msg=0;
                        int order=0;

                        List<Integer> cancle=new ArrayList();


                        try {
                            refund=jsonObject.getInt("refundOdr");
                            msg=jsonObject.getInt("sysMsg");
                            order=jsonObject.getInt("newOdr");
                            JSONArray array=jsonObject.getJSONArray("cancelOdr");
                            int size=array.length();
                            for(int i=0;i<size;i++){
                                cancle.add(array.getInt(i));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        Intent intent=new Intent(Share.receivedMsg);

                        intent.putExtra("refund",refund);
                        intent.putExtra("msg", msg);

                        int ca[];

                        if (cancle.size()>0) {
                            ca=new int[cancle.size()];
                            for(int i=0;i<cancle.size();i++){
                                ca[i]=cancle.get(i);
                            }

                        }else{
                            ca=new int[1];
                            ca[0]=-1;
                        }

                        if (order>0) {
                            intent.putExtra("newOrder", true);
                        }else{
                            intent.putExtra("newOrder", false);
                        }

                        intent.putExtra("cancle",ca);


                        sendOrderedBroadcast(intent, null);

                    }

                    @Override
                    public void onFailre(String s) {

                    }
                });

        }
    }







}
