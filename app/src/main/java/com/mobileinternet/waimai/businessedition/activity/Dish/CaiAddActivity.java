package com.mobileinternet.waimai.businessedition.activity.Dish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.activity.ClipImageActivity;
import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.util.BitMapUtils;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.DialogUtil;
import com.mobileinternet.waimai.businessedition.util.HttpUtil;
import com.mobileinternet.waimai.businessedition.util.ImageUtil;
import com.mobileinternet.waimai.businessedition.view.smartiv.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaiAddActivity extends ActionBarActivity {


    private static final int CAMARE = 0;// 拍照
    private static final int IMAGBOX = 1; // 相册

    private static final String IMAGE_UNSPECIFIED = "image/*";




    private File mFile;

    private String id;


    private LinearLayout ll_guige_container;
    private LinearLayout ll_att_container;

    private EditText et_cai_name;
   // private EditText et_cai_price;
  //  private EditText et_cai_store;
    private EditText et_min_buy;
    private EditText et_unit;
    private EditText et_box_unit_price;
    private EditText et_box_num;
    private EditText et_descripe;
  //  private EditText et_guige_name;

    private View view_name;//第一个规格的name view




    private ToggleButton tb_cai_store;


    private SmartImageView sv_picture;





    private List<Guige> ls_guige=new ArrayList<>();
    private List<Att> ls_att=new ArrayList<>();















    public void addGuige(final View vClick){

        if (ls_guige.size()==4){
            vClick.setVisibility(View.GONE);
        }

        //实例化组件
        final View view=LayoutInflater.from(this).inflate(R.layout.view_add_dish_guige,null);
        final Guige guige=new Guige();
        ls_guige.add(guige);

        guige.et_name=(EditText)view.findViewById(R.id.add_dish_et_guige_name);
        guige.et_price=(EditText)view.findViewById(R.id.add_dish_et_guige_price);
        guige.et_store=(EditText)view.findViewById(R.id.add_dish_et_guige_store);
        guige.et_store.setEnabled(false);

        ((ToggleButton)view.findViewById(R.id.add_dish_tb)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    guige.et_store.setEnabled(true);
                    guige.useStore=true;
                }else{
                    guige.et_store.setText("");
                    guige.et_store.setEnabled(false);
                    guige.useStore=false;
                }

            }
        });

        view.findViewById(R.id.add_dish_iv_guige_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_guige_container.removeView(view);
                ls_guige.remove(guige);
                vClick.setVisibility(View.VISIBLE);

                if (ls_guige.size()==1){
                    view_name.setVisibility(View.GONE);
                }

            }
        });
        view_name.setVisibility(View.VISIBLE);

        ll_guige_container.addView(view,ll_guige_container.getChildCount()-1);


    }


    public void addAtt(final View vClick){

        if (ls_att.size()==4){
            vClick.setVisibility(View.GONE);
        }

        final Att att=new Att();
        ls_att.add(att);

        final View view=LayoutInflater.from(this).inflate(R.layout.view_add_dish_att,null);

        att.et_name=(EditText)view.findViewById(R.id.add_dish_et_att_title);

        final LinearLayout container=(LinearLayout)view.findViewById(R.id.add_dish_ll_att_content);

        view.findViewById(R.id.add_dish_iv_att_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vClick.setVisibility(View.VISIBLE);
                ll_att_container.removeView(view);
                ls_att.remove(att);
            }
        });


        view.findViewById(R.id.add_dish_iv_att_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (att.child.size() == 3) {
                    Toast.makeText(CaiAddActivity.this, "只能添加三个", Toast.LENGTH_SHORT).show();
                    return;
                }

                addAttContent(container, att.child);


            }
        });






        ll_att_container.addView(view,ll_att_container.getChildCount()-1);




//        att.et_name=




    }


    private void addAttContent(final LinearLayout linearLayout ,final List<String> value){

        final EditText editText=(EditText)LayoutInflater.from(this).inflate(R.layout.view_edittext,null);
        Dialog dialog=new AlertDialog.Builder(this,R.style.edit_dialog)
                .setView(editText)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String name1=editText.getText().toString();

                        if ("".equals(name1)){
                            hideInput(editText);
                            return;
                        }

                        final String name=name1.trim();
                        if ("".equals(name)){
                            hideInput(editText);
                            return;
                        }



                        if (value.contains(name)){

                            Toast.makeText(CaiAddActivity.this,"此属性值已添加",Toast.LENGTH_SHORT).show();
                           hideInput(editText);
                            return;
                        }

                        //实例化组件
                        final TextView textView=(TextView)LayoutInflater.from(CaiAddActivity.this).inflate(R.layout.view_add_dish_att_content,null);

                        //设置显示添加的属性值
                        textView.setText(name);

                        //添加点击删除监听器
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayout.removeView(textView);
                                value.remove(name);

                            }
                        });

                        //将心添加的属性值，存入到list保存
                        value.add(name);

                        //加载到界面上
                        linearLayout.addView(textView,linearLayout.getChildCount());


                        hideInput(editText);



                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideInput(editText);
                    }
                })
                .setTitle("添加属性值")
                .create();
        dialog.show();


        dialog.show();
        dialog.getWindow().setLayout(600, 400);
    }

    private void hideInput(EditText editText){

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }



    public void preview(View view){}


    /**
     * 检测添加的属性和规格是否合法，以及各必要的信息值是否填写
     */
    private boolean checkIsLegal(){

        //检测必填处是否有未填写
        String cai_name=et_cai_name.getText().toString();
        if("".equals(cai_name)||"".equals(cai_name.trim())){

            Toast.makeText(this,"菜品名还未填写",Toast.LENGTH_SHORT).show();
            return false;
        }



       //判断规格是否合法
        int size=ls_guige.size();
        if(size==1){

            Guige guige=ls_guige.get(0);
            String price=guige.et_price.getText().toString();
            if ("".equals(price)||"".equals(price.trim())){
                Toast.makeText(this,"菜品价格还未填写",Toast.LENGTH_SHORT).show();
                return false;
            }

            guige.price=Float.parseFloat(price);



        }else{

            //检测各规格名是否未填写，规格价格未填写
            for(int i=0;i<size;i++){

                Guige guige=ls_guige.get(i);

                //name
                guige.name=guige.et_name.getText().toString().trim();
                if("".equals(guige.name)||"".equals(guige.name.trim())){

                    Toast.makeText(this,"有规格还未命名",Toast.LENGTH_SHORT).show();
                    return false;

                }

                //price
                String price=guige.et_price.getText().toString().trim();
                if ("".equals(price)||"".equals(price.trim())){
                    Toast.makeText(this,"有规格还未给出价格",Toast.LENGTH_SHORT).show();
                    return false;
                }

                guige.price=Float.parseFloat(price);

            }


            //判断规格名是否重复
            for(int i=0;i<size-1;i++){

                String name=ls_guige.get(i).name;
                for(int j=i+1;j<size;j++){

                    if (name.equals(ls_guige.get(j).name)){
                        Toast.makeText(this,name+"规格重复",Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }

            }

        }


        //检测属性合法性

        for (Att att:ls_att){


            att.name=att.et_name.getText().toString();
            if ("".equals(att.name)||"".equals(att.name.trim())){
                Toast.makeText(this,"存在未命名属性类别",Toast.LENGTH_SHORT).show();
                return false;
            }

            if (att.child.size()==0){
                Toast.makeText(this,att.name+"属性中没有值",Toast.LENGTH_SHORT).show();
                return false;
            }


        }

        return true;

    }


    /**
     * 提交到服务器
     */
    private void save(){

        if (!checkIsLegal()){
            return;
        }


        JSONObject jsonObject=new JSONObject();

        String shopId=((IApplication)getApplication()).getData(Share.shop_id);

        try {



            jsonObject.put("shopId", shopId);
            jsonObject.put("categoyId",id);
            jsonObject.put("name",et_cai_name.getText().toString().trim());

            //装载规格数据
            if (ls_guige.size()==1){

                //无规格时
                jsonObject.put("hasGuige",false);
                Guige guige=ls_guige.get(0);

                if (guige.useStore){
                    jsonObject.put("haslimit",true);
                    jsonObject.put("num",guige.et_store.getText().toString());

                }else{
                    jsonObject.put("haslimit",false);

                }

                jsonObject.put("price",guige.price);

            }else{

                //有多规格时
                jsonObject.put("hasGuige",true);
                JSONArray jsonArray=new JSONArray();

                //装载各规格
                for (Guige guige:ls_guige){

                    JSONObject json=new JSONObject();
                    json.put("name",guige.name);

                    if (guige.useStore){
                        json.put("haslimit",true);
                        json.put("num",guige.et_store.getText().toString());

                    }else{
                        json.put("haslimit",false);
                    }

                    json.put("price",guige.price);

                    jsonArray.put(json);

                }

                jsonObject.put("guige",jsonArray);

            }



            //装载属性
            if (ls_att.size()>0){
                jsonObject.put("hasAttr",true);
                JSONArray jsonArray=new JSONArray();

                for (Att att:ls_att){

                    JSONObject json=new JSONObject();

                    //加载单个属性名字
                    json.put("name",att.name);

                    //加载单个属性所有值
                    JSONArray arry=new JSONArray();
                    //arry.put(att.child);
                    for (String str:att.child){
                        arry.put(str);
                    }

                    json.put("data",arry);


                    //将此属性整个加入上层JsonArray中
                    jsonArray.put(json);

                }

                jsonObject.put("attr",jsonArray);

            }else{
                jsonObject.put("hasAttr",false);
            }


            //加载其他属性

            //加载单位
            String unit=et_unit.getText().toString().trim();
            if ("".equals(unit)||"".equals(unit.trim())){
                jsonObject.put("unit",et_unit.getHint());
            }else{
                jsonObject.put("unit",unit);
            }

            //加载最小购买量
            String minBuy=et_min_buy.getText().toString().trim();
            if ("".equals(minBuy)||"".equals(minBuy.trim())){
                jsonObject.put("minBuy",et_min_buy.getHint());
            }else{
                jsonObject.put("minBuy",unit);
            }


            //加载所需餐盒数
            String box=et_box_num.getText().toString().trim();
            if ("".equals(box)||"".equals(box.trim())){
                jsonObject.put("box",et_box_num.getHint());
            }else{
                jsonObject.put("box",box);
            }


            //加载每个餐盒单价
            String boxPrice=et_box_unit_price.getText().toString().trim();
            if ("".equals(boxPrice)||"".equals(boxPrice.trim())){
                jsonObject.put("boxPrice",et_box_unit_price.getHint());
            }else{
                jsonObject.put("boxPrice",boxPrice);
            }

            //加载菜品描述信息
            String des=et_descripe.getText().toString().trim();
            if ("".equals(des)){
                jsonObject.put("des"," ");
            }else{
                jsonObject.put("des",des);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




        //大功搞成
        final Dialog dialog=DialogUtil.showProgressDialog(this, "正在创建菜品...");

        new HttpUtil(this).post(Share.url_dish_add, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                if (!isOk)
                    return;

                CodeUtil.toast(CaiAddActivity.this, "菜品创建成功...");
                try {

                    String id=jsonObject.getString("dishId");
                    dialog.dismiss();

                    uploadImage(id);
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



    private void allIsOkReturnUpLevel(String mId,String image){

        Intent intent=getIntent();
        intent.putExtra("name",et_cai_name.getText().toString());
        intent.putExtra("image",image);
        intent.putExtra("price",ls_guige.get(0).price);
        intent.putExtra("id",mId);
        if (ls_guige.size()>1){
            intent.putExtra("guige",true);
        }else{
            intent.putExtra("guige",false);
        }


        setResult(200,intent);
        finish();

    }


    private void initView(){

        final Guige guige=new Guige();
        ls_guige.add(guige);

        ll_att_container=(LinearLayout)findViewById(R.id.add_dish_ll_add_att_container);
        ll_guige_container=(LinearLayout)findViewById(R.id.add_dish_ll_add_guige_container);

        et_box_num=(EditText)findViewById(R.id.add_dish_et_box_num);
        et_box_unit_price=(EditText)findViewById(R.id.add_dish_et_unit_price);
        et_cai_name=(EditText)findViewById(R.id.add_dish_et_cai_name);
        //et_cai_price=(EditText)findViewById(R.id.add_dish_et_guige_price);
        //et_cai_store=(EditText)findViewById(R.id.add_dish_et_guige_store);
        et_descripe=(EditText)findViewById(R.id.add_dish_et_descripe);
        et_min_buy=(EditText)findViewById(R.id.add_dish_et_min_buy);
        et_unit=(EditText)findViewById(R.id.add_dish_et_unit);
      //  et_guige_name=(EditText)findViewById(R.id.add_dish_et_guige_name);

        guige.et_name=(EditText)findViewById(R.id.add_dish_et_guige_name);
        guige.et_price=(EditText)findViewById(R.id.add_dish_et_guige_price);
        guige.et_store=(EditText)findViewById(R.id.add_dish_et_guige_store);

        guige.et_store.setEnabled(false);

        findViewById(R.id.add_dish_iv_guige_delete).setVisibility(View.GONE);
        view_name=findViewById(R.id.add_dish_ll_nouse_name);
        view_name.setVisibility(View.GONE);

        sv_picture=(SmartImageView)findViewById(R.id.add_dish_sv);


        sv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCaiImage();
            }
        });




        tb_cai_store=(ToggleButton)findViewById(R.id.add_dish_tb);
        tb_cai_store.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    guige.et_store.setEnabled(true);
                    guige.useStore=true;
                }else{
                    guige.et_store.setText("");
                    guige.et_store.setEnabled(false);
                    guige.useStore=false;
                }
            }
        });




    }




       private void setCaiImage(){

        Dialog dialog=new AlertDialog.Builder(this)
                .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
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

        Intent intent=new Intent(this, ClipImageActivity.class);
        intent.putExtra("type",ClipImageActivity.CAMARE);
       startActivityForResult(intent, CaiAddActivity.CAMARE);

    }

    private void imageBox(){

        Intent intent=new Intent(this, ClipImageActivity.class);
        intent.putExtra("type",ClipImageActivity.IMAGBOX);
        startActivityForResult(intent,CaiAddActivity.IMAGBOX);

    }


    /**
     * 上传菜品图片
     * @param caiId
     */
    private void uploadImage(final String caiId){


        if (mFile==null) {
            allIsOkReturnUpLevel(caiId,"");
            return;
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this,"正在上传菜品图片...");

        Map<String,String> map=new HashMap<>();
        map.put("id", caiId);


        new ImageUtil().uploadPortrait(Share.url_cai_image, mFile, map, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();
                try {
                    int code=jsonObject.getInt("code");
                    if (code!=0){

                        CodeUtil.toast(CaiAddActivity.this,"图片上传失败,可在修改菜品界面重新上传");
                        allIsOkReturnUpLevel(caiId,"");
                        return;

                    }

                    String image=jsonObject.getString("image");
                    allIsOkReturnUpLevel(caiId,image);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
                CodeUtil.toast(CaiAddActivity.this,"图片上传失败,可在修改菜品界面重新上传");
                allIsOkReturnUpLevel(caiId,"");
            }
        });
        
        
        
    }







    private class Guige{
        public EditText et_name;
        public EditText et_price;
        public EditText et_store;
        public String name;
        public boolean useStore=false;
        public int store=-1;
        public float price;
//        //1.无名字  2.无价格  3.有重复  4.legal
//        public boolean ifNoName=true;
//        public boolean ifNoPrice=true;

    }

    private class Att{

        public EditText et_name;
        public String name;
        public List<String> child=new ArrayList<>();
       //无类型名字  2.无内容   3.类型有重复  4.legal
//        public boolean ifNoName=true;
//        public boolean ifNoContent=true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_CANCELED)
            return;

        if(requestCode!=CaiAddActivity.IMAGBOX&&requestCode!=CaiAddActivity.CAMARE)
            return;

        if(!data.hasExtra("path"))
            return;

        String path=data.getStringExtra("path");

        mFile=new File(path);
        sv_picture.setImageBitmap(BitMapUtils.getLoacalBitmap(path));
        return;
        
        
        
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cai_add);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("新增菜品");


        id=getIntent().getStringExtra("id");

        initView();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cai_add, menu);
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
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
