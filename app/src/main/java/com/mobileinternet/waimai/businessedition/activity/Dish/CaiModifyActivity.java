package com.mobileinternet.waimai.businessedition.activity.Dish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class CaiModifyActivity extends ActionBarActivity {

    private static final int CAMARE = 0;// 拍照
    private static final int IMAGBOX = 1; // 相册

    private String id;
    private int positon;


    private List<Guige> ls_guige = new ArrayList<>();//存储所有规格adpter数据源
    private List<Att> ls_att = new ArrayList<>();//存储所有属性adapter数据源
    private List<AttAncestor> ls_att_ancestor = new ArrayList<>();
    private List<String> ls_guige_del = new ArrayList<>();     //被删除的规格id


    private boolean hasGuige;
    private boolean hasAtt;


    private LinearLayout ll_guige_container;
    private LinearLayout ll_att_container;

    private EditText et_cai_name;
    private EditText et_min_buy;
    private EditText et_unit;
    private EditText et_box_unit_price;
    private EditText et_box_num;
    private EditText et_descripe;

    private View view_name;//第一个规格的name view


    private ToggleButton tb_cai_store;
    private SmartImageView sv_picture;
    private File mFile;


    private String image_url;


    public void addGuige(final View vClick) {

        if (ls_guige.size() == 4) {
            vClick.setVisibility(View.GONE);
        }

        //实例化组件
        final View view = LayoutInflater.from(this).inflate(R.layout.view_add_dish_guige, null);
        final Guige guige = new Guige();
        ls_guige.add(guige);

        guige.et_name = (EditText) view.findViewById(R.id.add_dish_et_guige_name);
        guige.et_price = (EditText) view.findViewById(R.id.add_dish_et_guige_price);
        guige.et_store = (EditText) view.findViewById(R.id.add_dish_et_guige_store);
        view_name.setVisibility(View.VISIBLE);
        guige.et_store.setEnabled(false);


        ((ToggleButton) view.findViewById(R.id.add_dish_tb)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    guige.et_store.setEnabled(true);
                    guige.useStore = true;
                } else {
                    guige.et_store.setText("");
                    guige.et_store.setEnabled(false);
                    guige.useStore = false;
                }

            }
        });

        view.findViewById(R.id.add_dish_iv_guige_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_guige_container.removeView(view);
                ls_guige.remove(guige);
                vClick.setVisibility(View.VISIBLE);

                if (ls_guige.size() == 1) {
                    view_name.setVisibility(View.GONE);
                }

            }
        });


        ll_guige_container.addView(view, ll_guige_container.getChildCount() - 1);


    }


    public void addAtt(final View vClick) {

        if (ls_att.size() == 4) {
            vClick.setVisibility(View.GONE);
        }

        final Att att = new Att();
        ls_att.add(att);

        final View view = LayoutInflater.from(this).inflate(R.layout.view_add_dish_att, null);

        att.et_name = (EditText) view.findViewById(R.id.add_dish_et_att_title);

        final LinearLayout container = (LinearLayout) view.findViewById(R.id.add_dish_ll_att_content);

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
                    Toast.makeText(CaiModifyActivity.this, "只能添加三个", Toast.LENGTH_SHORT).show();
                    return;
                }

                addAttContent(container, att.child);


            }
        });


        ll_att_container.addView(view, ll_att_container.getChildCount() - 1);


//        att.et_name=


    }


    private void addAttContent(final LinearLayout linearLayout, final List<String> value) {

        final EditText editText = (EditText) LayoutInflater.from(this).inflate(R.layout.view_edittext, null);
        Dialog dialog = new AlertDialog.Builder(this, R.style.edit_dialog)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String name1 = editText.getText().toString();

                        if ("".equals(name1)) {
                            hideInput(editText);
                            return;
                        }

                        final String name = name1.trim();
                        if ("".equals(name)) {
                            hideInput(editText);
                            return;
                        }


                        if (value.contains(name)) {

                            Toast.makeText(CaiModifyActivity.this, "此属性值已添加", Toast.LENGTH_SHORT).show();
                            hideInput(editText);
                            return;
                        }

                        //实例化组件
                        final TextView textView = (TextView) LayoutInflater.from(CaiModifyActivity.this).inflate(R.layout.view_add_dish_att_content, null);

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
                        linearLayout.addView(textView, linearLayout.getChildCount());


                        hideInput(editText);


                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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


    private void hideInput(EditText editText) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    /**
     * 检测添加的属性和规格是否合法，以及各必要的信息值是否填写
     */
    private boolean checkIsLegal() {

        //检测必填处是否有未填写
        String cai_name = et_cai_name.getText().toString();
        if ("".equals(cai_name) || "".equals(cai_name.trim())) {

            Toast.makeText(this, "菜品名还未填写", Toast.LENGTH_SHORT).show();
            return false;
        }


        //判断规格是否合法
        int size = ls_guige.size();
        if (size == 1) {

            Guige guige = ls_guige.get(0);
            String price = guige.et_price.getText().toString();
            if ("".equals(price) || "".equals(price.trim())) {
                Toast.makeText(this, "菜品价格还未填写", Toast.LENGTH_SHORT).show();
                return false;
            }

            guige.price = Float.parseFloat(price);


        } else {

            //检测各规格名是否未填写，规格价格未填写
            for (int i = 0; i < size; i++) {

                Guige guige = ls_guige.get(i);

                //name
                guige.name = guige.et_name.getText().toString().trim();
                if ("".equals(guige.name) || "".equals(guige.name.trim())) {

                    Toast.makeText(this, "有规格还未命名", Toast.LENGTH_SHORT).show();
                    return false;

                }

                //price
                String price = guige.et_price.getText().toString().trim();
                if ("".equals(price) || "".equals(price.trim())) {
                    Toast.makeText(this, "有规格还未给出价格", Toast.LENGTH_SHORT).show();
                    return false;
                }

                guige.price = Float.parseFloat(price);

            }


            //判断规格名是否重复
            for (int i = 0; i < size - 1; i++) {

                String name = ls_guige.get(i).name;
                for (int j = i + 1; j < size; j++) {

                    if (name.equals(ls_guige.get(j).name)) {
                        Toast.makeText(this, name + "规格重复", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }

            }

        }


        //检测属性合法性

        for (Att att : ls_att) {


            att.name = att.et_name.getText().toString();
            if ("".equals(att.name) || "".equals(att.name.trim())) {
                Toast.makeText(this, "存在未命名属性类别", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (att.child.size() == 0) {
                Toast.makeText(this, att.name + "属性中没有值", Toast.LENGTH_SHORT).show();
                return false;
            }


        }

        return true;

    }


    /**
     * 提交到服务器
     */
    private void save() {

        if (!checkIsLegal()) {
            return;
        }


        JSONObject jsonObject = new JSONObject();


        try {

            jsonObject.put("dishId", id);
            jsonObject.put("name", et_cai_name.getText().toString().trim());

            //加载单位
            String unit = et_unit.getText().toString().trim();
            if ("".equals(unit) || "".equals(unit.trim())) {
                jsonObject.put("unit", et_unit.getHint());
            } else {
                jsonObject.put("unit", unit);
            }

            //加载最小购买量
            String minBuy = et_min_buy.getText().toString().trim();
            if ("".equals(minBuy) || "".equals(minBuy.trim())) {
                jsonObject.put("minBuy", et_min_buy.getHint());
            } else {
                jsonObject.put("minBuy", unit);
            }


            //加载所需餐盒数
            String box = et_box_num.getText().toString().trim();
            if ("".equals(box) || "".equals(box.trim())) {
                jsonObject.put("box", et_box_num.getHint());
            } else {
                jsonObject.put("box", box);
            }


            //加载每个餐盒单价
            String boxPrice = et_box_unit_price.getText().toString().trim();
            if ("".equals(boxPrice) || "".equals(boxPrice.trim())) {
                jsonObject.put("boxPrice", et_box_unit_price.getHint());
            } else {
                jsonObject.put("boxPrice", boxPrice);
            }

            //加载菜品描述信息
            String des = et_descripe.getText().toString().trim();
//            if ("".equals(des)) {
//                jsonObject.put("des", " ");
//            } else {
//                jsonObject.put("des", des);
//            }
            jsonObject.put("des", des);


            if (hasGuige) {
                makeGuigeModifyResultIfHasGuigeIsTrue(jsonObject);
            } else {
                makeGuigeModifyResultIfHasGuigeIsfalse(jsonObject);
            }


            if (hasAtt) {
                makeAttModifyRsultIfHasAttIsTrue(jsonObject);
            } else {
                makeAttModifyRsultIfHasAttIsfalse(jsonObject);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        //大功搞成
        final Dialog dialog=DialogUtil.showProgressDialog(this, "正提交到服务器");

        new HttpUtil(this).post(Share.url_dish_modify, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();
                if (!isOk)
                    return;


                if (mFile!=null){
                    uploadImage(mFile);
                }else{
                    allIsOkReturnUpLevel();
                }



            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });


    }


    /**
     * 如果原始状态hasAtt是false
     *
     * @param jsonObject
     * @throws JSONException
     */
    private void makeAttModifyRsultIfHasAttIsfalse(JSONObject jsonObject) throws JSONException {

        //装载属性
        if (ls_att.size() > 0) {
            jsonObject.put("hasAttr", true);
            JSONArray jsonArray = new JSONArray();

            for (Att att : ls_att) {

                JSONObject json = new JSONObject();

                //加载单个属性名字
                json.put("name", att.name);

                //加载单个属性所有值
                JSONArray arry = new JSONArray();
                arry.put(att.child);
                json.put("data", arry);

                //将此属性整个加入上层JsonArray中
                jsonArray.put(json);

            }

        } else {
            jsonObject.put("hasAttr", false);
        }

    }


    /**
     * 如果原始状态hasAtt是true
     *
     * @param jsonObject
     * @throws JSONException
     */
    private void makeAttModifyRsultIfHasAttIsTrue(JSONObject jsonObject) throws JSONException {

        //装载属性
        if (ls_att.size() > 0) {
            jsonObject.put("hasAttr", true);


            //加载新添加的属性种类，及属性中各值
            JSONArray jsonArray = new JSONArray();
            for (Att att : ls_att) {

                if (att.id != null)
                    continue;

                JSONObject json = new JSONObject();

                //一。加载属性值

                //1.加载单个属性名字
                json.put("name", att.name);

                //2.加载单个属性所有值
                JSONArray arry = new JSONArray();
                for (String str : att.child) {
                    arry.put(str);
                }
                json.put("data", arry);


                //二。将此属性整个加入上层JsonArray中
                jsonArray.put(json);

            }

            jsonObject.put("attr", jsonArray);


            //把该加载的全加载了
            JSONArray newValueArray = new JSONArray();
            JSONArray modifyAttNameArray = new JSONArray();
            JSONArray delAttArray = new JSONArray();
            JSONArray delValueArray = new JSONArray();


            for (AttAncestor ancestor : ls_att_ancestor) {

                //如果标记为不可用了
                if (!ancestor.isUse) {
                    delAttArray.put(ancestor.id);
                    continue;
                }


                //如果属性类别名字改了
                if (ancestor.name != null) {

                    JSONObject modifyObject = new JSONObject();
                    modifyObject.put("id", ancestor.id);
                    modifyObject.put("name", ancestor.name);

                }

                //如果添加新属性了
                for (String value : ancestor.child_new) {

                    JSONObject valueObject = new JSONObject();
                    valueObject.put("catid", ancestor.id);
                    valueObject.put("name", value);
                    newValueArray.put(valueObject);

                }


                //如果删除了一些旧属性值
                for (String oldValueId : ancestor.child_del_id) {
                    delValueArray.put(oldValueId);
                }

            }

            //新增加的属性值
            jsonObject.put("addattr", newValueArray);

            //修改的属性类别名
            jsonObject.put("mattcat", modifyAttNameArray);

            //删除的属性类别
            jsonObject.put("attrcat", delAttArray);

            //删除的属性值
            jsonObject.put("attchild", delValueArray);


        } else {
            jsonObject.put("hasAttr", false);
        }

    }


    /**
     * 如果原始状态hasGuige是false
     *
     * @param jsonObject
     * @throws JSONException
     */
    private void makeGuigeModifyResultIfHasGuigeIsfalse(JSONObject jsonObject) throws JSONException {

        //装载规格数据
        if (ls_guige.size() == 1) {

            //无规格时
            jsonObject.put("hasGuige", false);
            Guige guige = ls_guige.get(0);

            if (guige.useStore) {
                jsonObject.put("haslimit", true);
                jsonObject.put("num", guige.et_store.getText().toString());

            } else {
                jsonObject.put("haslimit", false);
            }

            jsonObject.put("price", guige.et_price.getText().toString());

        } else {

            //有多规格时
            jsonObject.put("hasGuige", true);

            JSONArray jsonArray = new JSONArray();

            //装载各规格
            for (Guige guige : ls_guige) {

                JSONObject json = new JSONObject();

                if (guige.useStore) {
                    json.put("haslimit", true);
                    json.put("num", guige.et_store.getText().toString());

                } else {
                    json.put("haslimit", false);
                }

                json.put("price", guige.et_price.getText().toString());
                json.put("name", guige.et_name.getText().toString());

                jsonArray.put(json);

            }

            jsonObject.put("guige", jsonArray);


            //纯粹是满足接口格式
            jsonObject.put("guigedel", new JSONArray());

        }


    }


    /**
     * 如果原始状态hasGuige是true
     *
     * @param jsonObject
     * @throws JSONException
     */
    private void makeGuigeModifyResultIfHasGuigeIsTrue(JSONObject jsonObject) throws JSONException {

        //装载规格数据
        if (ls_guige.size() == 1) {

            /**
             * 无规格时
             */


            jsonObject.put("hasGuige", false);
            Guige guige = ls_guige.get(0);

            if (guige.useStore) {
                jsonObject.put("haslimit", true);
                jsonObject.put("num", guige.et_store.getText().toString());

            } else {
                jsonObject.put("haslimit", false);
            }

            jsonObject.put("price", guige.et_price.getText().toString());

        } else {

            /**
             *  有多规格时
             */


            jsonObject.put("hasGuige", true);


            //2.加载添加或修改的规格
            JSONArray jsonArray = new JSONArray();

            for (Guige guige : ls_guige) {


                JSONObject json = new JSONObject();

                if (guige.id != null && !guige.isModify)
                    continue;


                if (guige.id != null && guige.isModify) {
                    json.put("id", guige.id);
                }

                json.put("name", guige.et_name.getText().toString());


                if (guige.useStore) {
                    json.put("haslimit", true);
                    json.put("num", guige.et_store.getText().toString());

                } else {
                    json.put("haslimit", false);
                }

                json.put("price", guige.et_price.getText().toString());


                jsonArray.put(json);

            }

            jsonObject.put("guige", jsonArray);


            //3.添加关于删除的规格的修改
            JSONArray del_array = new JSONArray();

            for (String id : ls_guige_del) {
                del_array.put(id);
            }
            jsonObject.put("guigedel", del_array);

        }
    }


    /**
     * 处理第一个规格的显示
     *
     * @param guige
     * @param name
     * @param price
     * @param haslimit
     * @param store
     * @throws JSONException
     */
    private void firstGuige(final Guige guige, String name, String price, boolean haslimit, int store) throws JSONException {

        guige.et_name.setText(name);
        guige.et_price.setText(price + "");
        guige.useStore = haslimit;
        ToggleButton tb = (ToggleButton) guige.et_name.getTag();

        if (guige.useStore) {
            tb.setChecked(true);
            guige.et_store.setText(store + "");
        } else {
            tb.setChecked(false);
            guige.et_store.setEnabled(false);
        }

        guige.et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                guige.isModify = true;
            }
        });
        guige.et_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                guige.isModify = true;
            }
        });
        guige.et_store.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                guige.isModify = true;
            }
        });
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                guige.isModify = true;
                guige.useStore = b;
                if (b) {
                    guige.et_store.setEnabled(true);
                } else {
                    guige.et_store.setEnabled(false);
                }
            }
        });

        if (guige.useStore) {
            guige.et_store.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    guige.isModify = true;
                }
            });
        }


    }


    /**
     * 初始化规格UI
     *
     * @param jsonObject
     * @throws JSONException
     */
    private void guigeInitView(JSONObject jsonObject) throws JSONException {

        //实例化组件
        final View view = LayoutInflater.from(this).inflate(R.layout.view_add_dish_guige, null);
        final Guige guige = new Guige();
        ls_guige.add(guige);


        guige.et_name = (EditText) view.findViewById(R.id.add_dish_et_guige_name);
        guige.et_price = (EditText) view.findViewById(R.id.add_dish_et_guige_price);
        guige.et_store = (EditText) view.findViewById(R.id.add_dish_et_guige_store);


        guige.id = jsonObject.getString("id");
        guige.name = jsonObject.getString("name");
        guige.et_name.setText(guige.name);
        guige.price = (float) jsonObject.getDouble("price");
        guige.et_price.setText(guige.price + "");


        //设置显示规格
        guige.useStore = jsonObject.getBoolean("haslimit");
        ToggleButton tb_store = (ToggleButton) view.findViewById(R.id.add_dish_tb);
        if (guige.useStore) {
            tb_store.setChecked(true);
            guige.store = jsonObject.getInt("num");
            guige.et_store.setText(guige.store + "");

        } else {
            tb_store.setChecked(false);
            guige.et_store.setEnabled(false);
        }


        guige.et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                guige.isModify = true;
            }
        });


        guige.et_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                guige.isModify = true;
            }
        });


        //规格开关监听器
        tb_store.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    guige.et_store.setEnabled(true);
                    guige.useStore = true;
                } else {
                    guige.et_store.setText("");
                    guige.et_store.setEnabled(false);
                    guige.useStore = false;
                }

                guige.isModify = true;

            }
        });


        view.findViewById(R.id.add_dish_iv_guige_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_guige_container.removeView(view);
                ls_guige.remove(guige);
                findViewById(R.id.add_dish_ll_add_guige).setVisibility(View.VISIBLE);

                if (ls_guige.size() == 1) {
                    view_name.setVisibility(View.GONE);
                }

                ls_guige_del.add(guige.id);


            }
        });


        guige.et_store.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                guige.isModify = true;
            }
        });


        ll_guige_container.addView(view, ll_guige_container.getChildCount() - 1);


    }


    /**
     * 初始化属性UI
     *
     * @param jsonObject
     */
    private void attInitView(JSONObject jsonObject) throws JSONException {


        final Att att = new Att();
        ls_att.add(att);

        final AttAncestor ancestor = new AttAncestor();
        ls_att_ancestor.add(ancestor);

        //实例化属性整个布局
        final View view = LayoutInflater.from(this).inflate(R.layout.view_add_dish_att, null);
        //获取布局中对应组件的引用
        att.et_name = (EditText) view.findViewById(R.id.add_dish_et_att_title);
        final LinearLayout container = (LinearLayout) view.findViewById(R.id.add_dish_ll_att_content);
        //保存数据，并显示数据到界面上
        att.name = jsonObject.getString("name");
        att.et_name.setText(att.name);
        att.id = jsonObject.getString("catid");

        //保存id到ancestor，为用户修改属性做准备
        ancestor.id = att.id;
        //获取属性值数据。并初始化属性值界面
        JSONArray childArray = jsonObject.getJSONArray("data");
        int size = childArray.length();
        for (int i = 0; i < size; i++) {

            JSONObject childObject = childArray.getJSONObject(i);

            String childName = childObject.getString("name");
            //获取属性值的名字
            att.child.add(childName);
            //获取属性值的id
            String childId = childObject.getString("id");
            att.ls_child_id.add(childId);

            //实例化属性值的UI
            attChildInitView(container, childName, childId, att, ancestor);

        }

        //名字修改监听
        att.et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ancestor.name = editable.toString();  //将修改后的name保存到ancestor
            }
        });


        view.findViewById(R.id.add_dish_iv_att_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.add_dish_ll_add_att).setVisibility(View.VISIBLE);
                ll_att_container.removeView(view);
                ls_att.remove(att);
                ancestor.isUse = false;//标记此属性种类不再使用
            }
        });


        view.findViewById(R.id.add_dish_iv_att_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (att.child.size() == 3) {
                    Toast.makeText(CaiModifyActivity.this, "只能添加三个", Toast.LENGTH_SHORT).show();
                    return;
                }

                addChildNewAttContent(container, att, ancestor);


            }
        });


        ll_att_container.addView(view, ll_att_container.getChildCount() - 1);

    }


    /**
     * 向原始的属性中，添加新属性值
     *
     * @param linearLayout
     * @param att
     * @param ancestor
     */
    private void addChildNewAttContent(final LinearLayout linearLayout, final Att att, final AttAncestor ancestor) {

        final EditText editText = (EditText) LayoutInflater.from(this).inflate(R.layout.view_edittext, null);
        Dialog dialog = new AlertDialog.Builder(this, R.style.edit_dialog)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String name1 = editText.getText().toString();

                        if ("".equals(name1)) {
                            hideInput(editText);
                            return;
                        }

                        final String name = name1.trim();
                        if ("".equals(name)) {
                            hideInput(editText);
                            return;
                        }


                        if (att.child.contains(name)) {

                            Toast.makeText(CaiModifyActivity.this, "此属性值已添加", Toast.LENGTH_SHORT).show();
                            hideInput(editText);
                            return;
                        }

                        //实例化组件
                        final TextView textView = (TextView) LayoutInflater.from(CaiModifyActivity.this).inflate(R.layout.view_add_dish_att_content, null);

                        //设置显示添加的属性值
                        textView.setText(name);


                        //添加点击删除监听器
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayout.removeView(textView);
                                //从相应的数据存储区删除此值，及此值得标记
                                att.child.remove(name);
                                int position = att.child.indexOf(name);
                                att.ls_child_id.remove(position);
                                ancestor.child_new.remove(name);

                            }
                        });


                        //加载到界面上
                        linearLayout.addView(textView, linearLayout.getChildCount());

                        //记录该添加修改
                        att.child.add(name);
                        att.ls_child_id.add("no");
                        ancestor.child_new.add(name);

                        //隐藏输入法
                        hideInput(editText);


                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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


    /**
     * 初始化原来的属性值所用方法
     *
     * @param linearLayout
     * @param name
     * @param id
     * @param att
     * @param ancestor
     */
    private void attChildInitView(final LinearLayout linearLayout, final String name, final String id, final Att att, final AttAncestor ancestor) {

        //实例化组件
        final TextView textView = (TextView) LayoutInflater.from(CaiModifyActivity.this).inflate(R.layout.view_add_dish_att_content, null);

        //设置显示添加的属性值
        textView.setText(name);

        //添加点击删除监听器
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeView(textView);
                att.child.remove(name);
                att.ls_child_id.remove(id);
                ancestor.child_del_id.add(id);  //记录到ancestor

            }
        });


        //加载到界面上
        linearLayout.addView(textView, linearLayout.getChildCount());

    }


    /**
     * 从服务器上得到数据后，加载到UI中
     *
     * @param jsonObject
     */
    private void freightUI(JSONObject jsonObject) {


        try {

            JSONObject dishObject = jsonObject.getJSONObject("dish");

            et_cai_name.setText(dishObject.getString("name"));
            String image=dishObject.getString("thum_img");
            String url_image=java.net.URLDecoder.decode(image);
            sv_picture.setImageUrl(url_image);
            this.image_url=url_image;

            String unit = dishObject.getString("unit");
            if (!"".equals(unit))
                et_unit.setText(unit);

            et_min_buy.setText(dishObject.getInt("minBuy") + "");
            et_box_num.setText(dishObject.getInt("box") + "");
            et_box_unit_price.setText(dishObject.getDouble("boxPrice") + "");
            String des = dishObject.getString("des");
            if (!"".equals(des)) {
                et_descripe.setText(des);
            }

            hasGuige = dishObject.getBoolean("hasGuige");
            if (hasGuige) {

                view_name.setVisibility(View.VISIBLE);
                JSONArray array = dishObject.getJSONArray("guige");
                int size = array.length();

                //如果已经有了5个规格，则隐藏掉加规格的按钮
                if (size == 5) {

                    findViewById(R.id.add_dish_ll_add_guige).setVisibility(View.GONE);
                }


                //对第一个规格做特殊设置
                JSONObject firstGuige = array.getJSONObject(0);
                final Guige guige = ls_guige.get(0);

                String name = firstGuige.getString("name");
               // float price = (float) firstGuige.getDouble("price");
                String price =  firstGuige.getString("price");
                boolean hasLimit = firstGuige.getBoolean("haslimit");
                int store = 0;
                if (hasLimit) {
                    store = firstGuige.getInt("num");
                } else {
                    guige.et_store.setEnabled(false);
                }
                guige.id = firstGuige.getString("id");

                firstGuige(guige, name, price, hasLimit, store);


                for (int i = 1; i < size; i++) {

                    JSONObject guigeObject = array.getJSONObject(i);
                    //实例化UI并保存数据
                    guigeInitView(guigeObject);
                }

            } else {

                final Guige guige = ls_guige.get(0);
                // String name=jsonObject.getString("name");
                String price = dishObject.getString("price");
                boolean hasLimit = dishObject.getBoolean("haslimit");
                int store = 0;

             //   boolean limit=false;
//                if (hasLimit == 1) {
//                    limit=true;
//                    store = dishObject.getInt("num");
//                }
               // guige.id = dishObject.getString("id");


                firstGuige(guige, "", price, hasLimit, store);

            }

            hasAtt = dishObject.getBoolean("hasAttr");
            if (hasAtt) {


                JSONArray array = dishObject.getJSONArray("attr");
                int size = array.length();
                if (size == 5) {
                    findViewById(R.id.add_dish_ll_add_att).setVisibility(View.GONE);
                }


                for (int i = 0; i < size; i++) {

                    JSONObject arrObject = array.getJSONObject(i);
                    attInitView(arrObject);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * 从服务器拉去数据
     */
    private void fetchData() {

        final Dialog dialog = DialogUtil.showProgressDialog(this, "加载中...");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dishId", this.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtil(this).post(Share.url_dish_info, jsonObject, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {
                dialog.dismiss();
                if (!isOk) return;

                freightUI(jsonObject);

            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
            }
        });


    }


    private class Guige {
        public EditText et_name;
        public EditText et_price;
        public EditText et_store;
        public String name;
        public boolean useStore = false;
        public int store = -1;
        public float price;
        public String id;       //存储本来就有的规格的id，新加的规格的id将为null
        public boolean isModify = false;


    }


    private class Att {

        public EditText et_name;
        public String name;
        public List<String> child = new ArrayList<>();
        public String id;  //存储本来就有的属性的id，新加的属性的id将为null

        //对应位置上，本来就有的属性值有id，新加的的id将为null
        public List<String> ls_child_id = new ArrayList<>();

    }


    private class AttAncestor {
        public String id;
        public String name;
        public List<String> child_del_id = new ArrayList<>();
        public List<String> child_new = new ArrayList<>();
        public boolean isUse = true;         //是否任然使用

    }


    private void initView() {


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        positon = intent.getIntExtra("position", 0);


        final Guige guige = new Guige();
        ls_guige.add(guige);

        ll_att_container = (LinearLayout) findViewById(R.id.add_dish_ll_add_att_container);
        ll_guige_container = (LinearLayout) findViewById(R.id.add_dish_ll_add_guige_container);

        et_box_num = (EditText) findViewById(R.id.add_dish_et_box_num);
        et_box_unit_price = (EditText) findViewById(R.id.add_dish_et_unit_price);
        et_cai_name = (EditText) findViewById(R.id.add_dish_et_cai_name);
        et_descripe = (EditText) findViewById(R.id.add_dish_et_descripe);
        et_min_buy = (EditText) findViewById(R.id.add_dish_et_min_buy);
        et_unit = (EditText) findViewById(R.id.add_dish_et_unit);

        guige.et_name = (EditText) findViewById(R.id.add_dish_et_guige_name);
        guige.et_price = (EditText) findViewById(R.id.add_dish_et_guige_price);
        guige.et_store = (EditText) findViewById(R.id.add_dish_et_guige_store);

        //  guige.et_store.setEnabled(false);

        findViewById(R.id.add_dish_iv_guige_delete).setVisibility(View.GONE);
        view_name = findViewById(R.id.add_dish_ll_nouse_name);
        view_name.setVisibility(View.GONE);

        sv_picture = (SmartImageView) findViewById(R.id.add_dish_sv);

        tb_cai_store = (ToggleButton) findViewById(R.id.add_dish_tb);
        guige.et_name.setTag(tb_cai_store);


        sv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CodeUtil.checkNetState(CaiModifyActivity.this)){
                    setCaiImage();
                }
            }
        });


        if (CodeUtil.checkNetState(this)) {
            fetchData();
        }


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
        startActivityForResult(intent, CaiModifyActivity.CAMARE);

    }

    private void imageBox(){

        Intent intent=new Intent(this, ClipImageActivity.class);
        intent.putExtra("type",ClipImageActivity.IMAGBOX);
        startActivityForResult(intent,CaiModifyActivity.IMAGBOX);

    }



    /**
     * 最终修改完毕后，将需要的数据返回上一界面
     */
    private void allIsOkReturnUpLevel() {

        Intent intent = getIntent();
        intent.putExtra("name", et_cai_name.getText().toString());
        intent.putExtra("image", image_url);
        intent.putExtra("price", ls_guige.get(0).price);
        intent.putExtra("id", id);
        intent.putExtra("position", positon);

        if (ls_guige.size() > 1) {
            intent.putExtra("guige", true);
        } else {
            intent.putExtra("guige", false);
        }


        setResult(200, intent);

        CodeUtil.toast(this, "修改成功");

        finish();

    }

    /**
     * 上传菜品图片
     */
    private void uploadImage(File mFile){


        if (mFile==null) {
            return;
        }

        final Dialog dialog=DialogUtil.showProgressDialog(this,"正在上传菜品图片...");

        Map<String,String> map=new HashMap<>();
        map.put("id", this.id);


        new ImageUtil().uploadPortrait(Share.url_cai_image, mFile, map, new HttpUtil.OnHttpListener() {
            @Override
            public void onSucess(JSONObject jsonObject, boolean isOk) {

                dialog.dismiss();
                try {
                    int code = jsonObject.getInt("code");
                    if (code != 0) {

                        CodeUtil.toast(CaiModifyActivity.this, "图片上传失败");

                        return;

                    }

                    image_url = jsonObject.getString("image");
                   // CodeUtil.toast(CaiModifyActivity.this, "图片上传成功");
                    allIsOkReturnUpLevel();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailre(String s) {
                dialog.dismiss();
                CodeUtil.toast(CaiModifyActivity.this, "图片上传失败");
            }
        });



    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_CANCELED)
            return;


        if(requestCode!=CaiModifyActivity.IMAGBOX&&requestCode!=CaiModifyActivity.CAMARE)
            return;

        if(!data.hasExtra("path"))
            return;

        String path=data.getStringExtra("path");


        sv_picture.setImageBitmap(BitMapUtils.getLoacalBitmap(path));

        mFile=new File(path);

        return;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cai_add);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("修改菜品");


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        positon = intent.getIntExtra("position", 0);


        initView();

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
