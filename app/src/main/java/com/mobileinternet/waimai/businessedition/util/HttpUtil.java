package com.mobileinternet.waimai.businessedition.util;

import android.content.Context;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by 海鸥2012 on 2015/8/4.
 */
public class HttpUtil {

    private OnHttpListener mOnHttpListner;
    private Context mContext;


    public HttpUtil(Context context){
        this.mContext=context;
    }


    /**
     * code！=0时提示
     * @param url
     * @param jsonObject
     * @param httpListner
     */
    public void post(String url, final JSONObject jsonObject,OnHttpListener httpListner){


        LogUtil.debug(url);
        LogUtil.debug(jsonObject.toString());


        this.mOnHttpListner=httpListner;
        RequestParams params = new RequestParams();
        try {
            params.setBodyEntity(new StringEntity(jsonObject.toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        HttpUtils httpUtils=new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST,url, params,
                new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {

                        try {

                            JSONObject result=new JSONObject(responseInfo.result.toString());
                            //开发期间调试代码
                            LogUtil.debug(result.toString());
                            int code=result.getInt("code");
                            if (code != 0) {

                                Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
                                if (mOnHttpListner!=null){
                                    mOnHttpListner.onSucess(result,false);
                                }

                            } else {
                                if (mOnHttpListner!=null){
                                    mOnHttpListner.onSucess(result,true);
                                }
                                LogUtil.debug(result.getString("msg"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            LogUtil.debug("http回调，json解析错误：");
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(mContext, "连接服务器异常",Toast.LENGTH_SHORT).show();
                        LogUtil.debug(s);
                        if (mOnHttpListner!=null){
                            mOnHttpListner.onFailre(s);
                        }
                    }
                });
    }


    /**
     * code!=0时不提示
     * @param url
     * @param jsonObject
     * @param httpListner
     */
    public void post2(String url, final JSONObject jsonObject,OnHttpListener httpListner){


        LogUtil.debug(url);
        LogUtil.debug(jsonObject.toString());


        this.mOnHttpListner=httpListner;
        RequestParams params = new RequestParams();
        try {
            params.setBodyEntity(new StringEntity(jsonObject.toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        HttpUtils httpUtils=new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST,url, params,
                new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {

                        try {

                            JSONObject result=new JSONObject(responseInfo.result.toString());
                            //开发期间调试代码
                            LogUtil.debug(result.toString());
//                            int code=result.getInt("code");
//                            if (code != 0) {
//
//                                Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
//                                if (mOnHttpListner!=null){
//                                    mOnHttpListner.onSucess(result,false);
//                                }
//
//                            } else {
                                if (mOnHttpListner!=null){
                                    mOnHttpListner.onSucess(result,true);
                                }
                                LogUtil.debug(result.getString("msg"));
                       //     }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            LogUtil.debug("http回调，json解析错误：");
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                      //  Toast.makeText(mContext, "连接服务器失败",Toast.LENGTH_SHORT).show();
                        LogUtil.debug(s);
                        if (mOnHttpListner!=null){
                            mOnHttpListner.onFailre(s);
                        }
                    }
                });
    }



    public interface OnHttpListener{
         void onSucess(JSONObject jsonObject,boolean isOk);
         void onFailre(String s);
    }

}
