package com.mobileinternet.waimai.businessedition.util;

import android.os.Message;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 海鸥2012 on 2015/6/3.
 */
public class ImageUtil {

    private static final int TIME_OUT = 10 * 10000000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码

    public HttpUtil.OnHttpListener mOnHttpListener;


    public  void uploadPortrait(final String url,final File file, final Map<String,String> map,HttpUtil.OnHttpListener onHttpListener){

        this.mOnHttpListener=onHttpListener;

        if (map.keySet().size()==0)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadFile(url,file,map);
            }
        }).start();
    }



    private  void uploadFile(String murl,File file,Map<String,String> map) {
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String RequestURL = murl;
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);



            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                OutputStream outputSteam = conn.getOutputStream();

                DataOutputStream dos = new DataOutputStream(outputSteam);


                /**
                 *
                 */
                String str_con="Content-Disposition:form-data;name=\"data3\""+LINE_END+LINE_END;
                String str_content="no mean"+LINE_END;
                dos.write(str_con.getBytes());
                dos.write(str_content.getBytes());
                dos.write((PREFIX + BOUNDARY + LINE_END).getBytes());





                Set<String> keys=map.keySet();
                Iterator<String> iterator=keys.iterator();

                while(iterator.hasNext()){
                    String key=iterator.next();
                    String value=map.get(key);

                    /**
                     */
                    String str_con1="Content-Disposition:form-data;name=\""+key+"\""+LINE_END+LINE_END;
                    String str_content1= value+LINE_END;
                    dos.write(str_con1.getBytes());
                    dos.write(str_content1.getBytes());
                    dos.write((PREFIX + BOUNDARY + LINE_END).getBytes());


                }




                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);

                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len ;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());

                byte[] end_data2 = (PREFIX + BOUNDARY + PREFIX+LINE_END)
                        .getBytes();
                dos.write(end_data2);

                dos.flush();




                InputStream in=conn.getInputStream();
                DataInputStream inputStream=new DataInputStream(in);

                byte[] buffer=new byte[1024];
                int length;
                StringBuilder builder=new StringBuilder();
                while((length=inputStream.read(buffer))!=-1){
                    builder.append(new String(buffer,0,length,"utf-8"));
                }

                try {

                    JSONObject jsonObject=new JSONObject(builder.toString());
                    Message message=new Message();
                    message.what=200;
                    message.obj=jsonObject;
                    mHandler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(404);
        } catch (IOException e) {
            mHandler.sendEmptyMessage(404);
        }
    }



   private  android.os.Handler mHandler=new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what==200){

                if (mOnHttpListener!=null)
                {
                    mOnHttpListener.onSucess((JSONObject)msg.obj,true);
                }

                return;

            }

            if (msg.what==404){

                if (mOnHttpListener!=null)
                {
                    mOnHttpListener.onFailre("失败");
                }

                return;
            }

        }
    };

}
