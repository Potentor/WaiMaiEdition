package com.mobileinternet.waimai.businessedition.activity.Dish;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CatModifyActivity extends ActionBarActivity {

    private EditText et_name;
    private EditText et_des;

    private int position;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);


        setContentView(R.layout.activity_modify_dish_cat);
        //setContentView(R.layout.activity_statistics);
        ActionBar mActionBar=getSupportActionBar();

        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("修改菜品分类");

        et_des=(EditText)findViewById(R.id.dish_edit_cat_des);
        et_name=(EditText)findViewById(R.id.dish_edit_cat_name);


        String name=getIntent().getStringExtra("name");
        et_name.setText(name);

        String des=getIntent().getStringExtra("des");
        et_des.setText(des);

        position=getIntent().getIntExtra("position",0);

        id=getIntent().getStringExtra("id");



    }


    @Override
    public boolean onSupportNavigateUp() {
        setResult(400, getIntent());
        finish();
        return super.onSupportNavigateUp();

    }


    @Override
    public void onBackPressed() {
        setResult(400,getIntent());
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dish_cat_edit) {
           applyEdit();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    *
    *
    * 完成编辑
    *
    * */
    private void applyEdit(){

        if (!CodeUtil.checkNetState(this))
            return;


        String name=et_name.getText().toString();
        if ("".equals(name)||"".equals(name.trim())){
            Toast.makeText(this,"您还没有输入分类的名字",Toast.LENGTH_SHORT).show();
            return;
        }

        String des=et_des.getText().toString();
        if ("".equals(des)){
            des=" ";
        }

        //提交服务器

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("catId",id);
            jsonObject.put("name",name);
            jsonObject.put("des",des);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final Dialog dialog= DialogUtil.showProgressDialog(this,"正在提交...");

        new HttpUtil(this).post(Share.url_cat_modify_info, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();

                if (!isOk)
                    return;

                CodeUtil.toast(CatModifyActivity.this,"修改成功");
                editSuccess();

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });

    }

    /*
    *
    * 提交服务器完成后的操作
    *
    * */
    private void editSuccess(){

        Intent intent=getIntent();
        intent.putExtra("name",et_name.getText().toString());
        intent.putExtra("des",et_des.getText().toString());
        intent.putExtra("positon",position);
        setResult(200,intent);

        //1.得到InputMethodManager对象
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //2.调用hideSoftInputFromWindow方法隐藏软键盘
        imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0); //强制隐藏键盘
        finish();


    }
}
