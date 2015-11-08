package com.mobileinternet.waimai.businessedition.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;

/**
 * Created by 海鸥2012 on 2015/7/19.
 */
public class DialogUtil {

    public static Dialog showProgressDialog(Context mContext,String content){


        View view=LayoutInflater.from(mContext).inflate(R.layout.view_progress_dialog,null);
        ((TextView)view.findViewById(R.id.progress_tv_content)).setText(content);


        Dialog progressDialog;
        progressDialog = new Dialog(mContext, R.style.progress_dialog);
        progressDialog.setContentView(view);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;

    }


    public static Dialog showMessage(Context mContext,String title,String content){

        Dialog dialog=new AlertDialog.Builder(mContext,R.style.edit_dialog)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton("确定",null)
                    .create();

        dialog.setCanceledOnTouchOutside(true);

        dialog.show();

        return dialog;




    }



}
