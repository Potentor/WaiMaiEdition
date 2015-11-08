package com.mobileinternet.waimai.businessedition.activity.Dining;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.BitMapUtils;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.util.ImageUtil;
import com.mobileinternet.waimai.businessedition.view.smartiv.SmartImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class QuanlityActivity extends AppCompatActivity {


    private static final int CAMARE = 0;
    private static final int IMAGBOX = 1;


    private static final int m_zhizhao=0;
    private static final int m_fuwuxuke=1;
    private static final int m_idcard=2;
    private static final int m_health=3;



    private SmartImageView sv_zhizhao;
    private SmartImageView sv_fuwuxuke;
    private SmartImageView sv_idcard;
    private SmartImageView sv_health;

    private TextView tv_zhizhao;
    private TextView tv_fuwuxuke;
    private TextView tv_idcard;
    private TextView tv_health;


    private boolean isZhizhaoCollapse=true;
    private boolean isFuwuxukeCollapse=true;
    private boolean isIdcardCollapse=true;
    private boolean isHealthCollapse=true;


    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qualification);

        android.support.v7.app.ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("资质认证");

        sv_fuwuxuke=(SmartImageView)findViewById(R.id.license_sv_fuwuxuke);
        sv_health=(SmartImageView)findViewById(R.id.license_sv_health);
        sv_idcard=(SmartImageView)findViewById(R.id.license_sv_idcard);
        sv_zhizhao=(SmartImageView)findViewById(R.id.license_sv_zhizhao);

        tv_health=(TextView)findViewById(R.id.license_tv_health);
        tv_zhizhao=(TextView)findViewById(R.id.license_tv_zhizhao);
        tv_fuwuxuke=(TextView)findViewById(R.id.license_tv_fuwuxuke);
        tv_idcard=(TextView)findViewById(R.id.license_tv_idcard);


        sv_fuwuxuke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
                requestCode=QuanlityActivity.m_fuwuxuke;
            }
        });


        sv_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setImage();
                requestCode=QuanlityActivity.m_health;
            }
        });

        sv_zhizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setImage();
                requestCode=QuanlityActivity.m_zhizhao;
            }
        });

        sv_idcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setImage();
                requestCode=QuanlityActivity.m_idcard;
            }
        });



        if (CodeUtil.checkNetState(this)){
            fetchData();
        }


    }


    private void setImage(){

        Dialog dialog=new AlertDialog.Builder(this)
                .setItems(new String[]{"照相", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which==0){
                            camera();
                            dialog.dismiss();
                            return;
                        }

                        if (which==1){
                            imageBox();
                            dialog.dismiss();
                            return;
                        }

                    }
                }).create();
        dialog.show();

    }


    private void camera(){


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
        startActivityForResult(intent, QuanlityActivity.CAMARE);

    }

    private void imageBox(){


        startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), QuanlityActivity.IMAGBOX);

    }



    private void fetchData(){


        JSONObject jsonObject=CodeUtil.getJsonOnlyShopId(this);

        final Dialog dialog= DialogUtil.showProgressDialog(this, "加载中..");

        new HttpUtil(this).post(Share.url_identity_status, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();

                if (!isOk)
                    return;


                try {
                    freightUI(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });




    }


    private void freightUI(JSONObject jsonObject)throws JSONException{


        //健康证
        JSONObject health=jsonObject.getJSONObject("health");

        int health_status=health.getInt("status");
        String health_image=health.getString("image");

        switch (health_status)
        {
            case 0:
                tv_health.setText("未上传");
                break;
            case 1:
                tv_health.setText("审核中");
                break;
            case 2:
                tv_health.setText("审核通过");
                break;
            case 3:
                tv_health.setText("审核未通过");
                break;

        }
        sv_health.setImageUrl(health_image);


        //营业执照
        JSONObject zhizhao=jsonObject.getJSONObject("license");

        int zhizhao_status=zhizhao.getInt("status");
        String zhizhao_image=zhizhao.getString("image");

        switch (zhizhao_status)
        {
            case 0:
                tv_zhizhao.setText("未上传");
                break;
            case 1:
                tv_zhizhao.setText("审核中");
                break;
            case 2:
                tv_zhizhao.setText("审核通过");
                break;
            case 3:
                tv_zhizhao.setText("审核未通过");
                break;

        }
        sv_zhizhao.setImageUrl(zhizhao_image);

        //法人身份证

        JSONObject idcard=jsonObject.getJSONObject("idcard");

        int idcard_status=idcard.getInt("status");
        String idcard_image=idcard.getString("image");

        switch (idcard_status)
        {
            case 0:
                tv_idcard.setText("未上传");
                break;
            case 1:
                tv_idcard.setText("审核中");
                break;
            case 2:
                tv_idcard.setText("审核通过");
                break;
            case 3:
                tv_idcard.setText("审核未通过");
                break;

        }
        sv_idcard.setImageUrl(idcard_image);



        //服务许可证
        JSONObject fuwuxuke=jsonObject.getJSONObject("serve");

        int fuwuxuke_status=fuwuxuke.getInt("status");
        String fuwuxuke_image=fuwuxuke.getString("image");

        switch (fuwuxuke_status)
        {
            case 0:
                tv_fuwuxuke.setText("未上传");
                break;
            case 1:
                tv_fuwuxuke.setText("审核中");
                break;
            case 2:
                tv_fuwuxuke.setText("审核通过");
                break;
            case 3:
                tv_fuwuxuke.setText("审核未通过");
                break;

        }

        sv_fuwuxuke.setImageUrl(fuwuxuke_image);


    }

    public void idcard(View v){

        if (isIdcardCollapse){
            sv_idcard.setVisibility(View.VISIBLE);
            expand(tv_idcard,tv_idcard.getText().toString());
        }else{
            sv_idcard.setVisibility(View.GONE);
            collapse(tv_idcard,tv_idcard.getText().toString());
        }

        isIdcardCollapse=!isIdcardCollapse;

    }


    public void health(View v){

        if (isHealthCollapse){
            sv_health.setVisibility(View.VISIBLE);
            expand(tv_health,tv_health.getText().toString());
        }else{
            sv_health.setVisibility(View.GONE);
            collapse(tv_health,tv_health.getText().toString());
        }

        isHealthCollapse=!isHealthCollapse;

    }


    public void fuwuxuke(View v){

        if (isFuwuxukeCollapse){
            sv_fuwuxuke.setVisibility(View.VISIBLE);
            expand(tv_fuwuxuke,tv_fuwuxuke.getText().toString());
        }else{
            sv_fuwuxuke.setVisibility(View.GONE);
            collapse(tv_fuwuxuke,tv_fuwuxuke.getText().toString());
        }

        isFuwuxukeCollapse=!isFuwuxukeCollapse;
    }


    public void zhizhao(View v){

        if (isZhizhaoCollapse){
            sv_zhizhao.setVisibility(View.VISIBLE);
            expand(tv_zhizhao,tv_zhizhao.getText().toString());
        }else{
            sv_zhizhao.setVisibility(View.GONE);
            collapse(tv_zhizhao,tv_zhizhao.getText().toString());
        }

        isZhizhaoCollapse=!isZhizhaoCollapse;
    }


    /**
     * 折叠extView drawableRight
     * @param tv
     */
    private void collapse(TextView tv,String content){

        Drawable drawable=getResources().getDrawable(R.drawable.ic_collapse);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, null, drawable, null);
        // tv.setText(content);

    }

    /**
     * 展开TextView drawableRight
     * @param tv
     */
    private void expand(TextView tv,String content){

        Drawable drawable=getResources().getDrawable(R.drawable.ic_expand);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, null, drawable, null);
        // tv.setText(content);

    }


    /**
     * 上传资质认证图片
     */
    private void uploadImage(File mFile, final int type){


        if (mFile==null) {
            return;
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this,"上传中...");

        IApplication iApplication=(IApplication)getApplication();

        Map<String,String> map=new HashMap<>();
        map.put("shopId", iApplication.getData(Share.shop_id));
        map.put("type", type + "");



        new ImageUtil().uploadPortrait(Share.url_identity, mFile, map, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {


                dialog.dismiss();
                try {
                    int code = jsonObject.getInt("code");
                    if (code != 0) {

                        CodeUtil.toast(QuanlityActivity.this, "上传失败");
                        return;

                    }


                    //String image=jsonObject.getString("image");
                    switch (type)
                    {
                        case 2:
                            tv_health.setText("审核中");
                            break;
                        case 4:
                            tv_zhizhao.setText("审核中");
                            break;
                        case 1:
                            tv_fuwuxuke.setText("审核中");
                            break;
                        case 3:
                            tv_idcard.setText("审核中");
                            break;
                    }

                    CodeUtil.toast(QuanlityActivity.this, "上传成功");



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
                CodeUtil.toast(QuanlityActivity.this, "上传失败");
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode==RESULT_CANCELED)
            return;

        if (requestCode!=QuanlityActivity.CAMARE&&requestCode!=QuanlityActivity.IMAGBOX)
            return;

        File mFile =null;
        String path=null;


        if (requestCode==QuanlityActivity.CAMARE){
            path= Environment.getExternalStorageDirectory() + "/temp.jpg";
            mFile = new File(path);
        }

        if (requestCode==IMAGBOX){

            Uri localUri = data.getData();
            if (localUri == null) {
                return;
            }

            String[] proj={MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(localUri, proj, null, null, null);
            int column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path=cursor.getString(column);
            mFile = new File(path);
        }

        if (mFile==null)
            return;

        if (path==null)
            return;

        switch (this.requestCode)
        {
            case QuanlityActivity.m_fuwuxuke:
                sv_fuwuxuke.setImageBitmap(BitMapUtils.getLoacalBitmap(path));
                uploadImage(mFile,1);
                break;
            case QuanlityActivity.m_health:
                sv_health.setImageBitmap(BitMapUtils.getLoacalBitmap(path));
                uploadImage(mFile, 2);
                break;
            case QuanlityActivity.m_idcard:
                sv_idcard.setImageBitmap(BitMapUtils.getLoacalBitmap(path));
                uploadImage(mFile, 3);
                break;
            case QuanlityActivity.m_zhizhao:
                sv_zhizhao.setImageBitmap(BitMapUtils.getLoacalBitmap(path));
                uploadImage(mFile,4);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (CodeUtil.checkNetState(this)) {
                fetchData();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
