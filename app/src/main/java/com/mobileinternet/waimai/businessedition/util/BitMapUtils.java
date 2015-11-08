package com.mobileinternet.waimai.businessedition.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2015/4/15.
 */
public class BitMapUtils {
    /**
     * 将剪辑的图片存入sd卡  page/imas
     *
     * @param mFilePath
     * @param mFileName
     * @param paramBitmap
     * @param mFormat
     * @return
     */
    public static boolean saveBitmap(String mFilePath, String mFileName, final Bitmap paramBitmap, final Bitmap.CompressFormat mFormat) {



        //检测sd卡的状态
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        //检测SD卡中是否有图片储存的路径
        //1.无建立
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    //    myFilePath = rootPath + "/page/imas";

        FileOutputStream mFileOutPutStream = null;
        try {
            mFileOutPutStream = new FileOutputStream(new File(mFilePath, mFileName));
            paramBitmap.compress(mFormat, 80, mFileOutPutStream);
            mFileOutPutStream.flush();
            mFileOutPutStream.close();

        } catch (IOException mIoexception) {
            try {
                if (mFileOutPutStream != null)
                    mFileOutPutStream.close();
            } catch (IOException exception2) {
            }

            return false;
        }
        return true;

    }

    /**
     * 加载本地图片
     * @return
     */
    public static Bitmap getLoacalBitmap(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }



}
