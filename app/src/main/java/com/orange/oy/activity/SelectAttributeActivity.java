package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.AttributeShellView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性选择页面
 */
public class SelectAttributeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.attributeselect_title);
        appTitle.settingName("属性设置");
        appTitle.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        Packagecategory.stop(Urls.Packagecategory);
    }

    private void initNetworkConnection() {
        Packagecategory = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pid", task_pack_id);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Packagecategory.setIsShowDialog(true);
        Checkcomplete = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pid", task_pack_id);
                params.put("token", Tools.getToken());
                params.put("storeid", storeid);
                params.put("category1", str1);
                params.put("category2", str2);
                params.put("category3", str3);
                return params;
            }
        };
        Checkcomplete.setIsShowDialog(true);
    }

    private NetworkConnection Packagecategory, Checkcomplete;
    private String task_pack_id, storeid;
    private LinearLayout attributeselect_item1, attributeselect_item2, attributeselect_item3;
    private View attributeselect_item1_layout, attributeselect_item2_layout, attributeselect_item3_layout;
    private TextView attributeselect_item1_name, attributeselect_item2_name, attributeselect_item3_name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attributeselect);
        initTitle();
        initNetworkConnection();
        Intent data = getIntent();
        task_pack_id = data.getStringExtra("task_pack_id");
        storeid = data.getStringExtra("storeid");
        attributeselect_item1 = (LinearLayout) findViewById(R.id.attributeselect_item1);
        attributeselect_item2 = (LinearLayout) findViewById(R.id.attributeselect_item2);
        attributeselect_item3 = (LinearLayout) findViewById(R.id.attributeselect_item3);
        attributeselect_item1_layout = findViewById(R.id.attributeselect_item1_layout);
        attributeselect_item2_layout = findViewById(R.id.attributeselect_item2_layout);
        attributeselect_item3_layout = findViewById(R.id.attributeselect_item3_layout);
        attributeselect_item1_name = (TextView) findViewById(R.id.attributeselect_item1_name);
        attributeselect_item2_name = (TextView) findViewById(R.id.attributeselect_item2_name);
        attributeselect_item3_name = (TextView) findViewById(R.id.attributeselect_item3_name);
        findViewById(R.id.attributeselect_left).setOnClickListener(this);
        findViewById(R.id.attributeselect_right).setOnClickListener(this);
        getData();
    }

    private void getData() {
        Packagecategory.sendPostRequest(Urls.Packagecategory, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String category1Name = jsonObject.getString("category1Name");
                        String category2Name = jsonObject.getString("category2Name");
                        String category3Name = jsonObject.getString("category3Name");
                        if (!TextUtils.isEmpty(category1Name) && !category1Name.equals("null")) {
                            attributeselect_item1_layout.setVisibility(View.VISIBLE);
                            attributeselect_item1_name.setText(category1Name);
                            String category1Content = jsonObject.getString("category1Content");
                            String[] category1Contents = category1Content.replaceAll("\\[\"", "").replaceAll("\"]", "").split
                                    ("\",\"");
                            creatView(category1Contents, 1);
                        }
                        if (!TextUtils.isEmpty(category2Name) && !category2Name.equals("null")) {
                            attributeselect_item2_layout.setVisibility(View.VISIBLE);
                            attributeselect_item2_name.setText(category2Name);
                            String category2Content = jsonObject.getString("category2Content");
                            String[] category2Contents = category2Content.replaceAll("\\[\"", "").replaceAll("\"]", "").split
                                    ("\",\"");
                            creatView(category2Contents, 2);
                        }
                        if (!TextUtils.isEmpty(category3Name) && !category3Name.equals("null")) {
                            attributeselect_item3_layout.setVisibility(View.VISIBLE);
                            attributeselect_item3_name.setText(category3Name);
                            String category3Content = jsonObject.getString("category3Content");
                            String[] category3Contents = category3Content.replaceAll("\\[\"", "").replaceAll("\"]", "").split
                                    ("\",\"");
                            creatView(category3Contents, 3);
                        }
                        CustomProgressDialog.Dissmiss();
                    } else {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(SelectAttributeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(SelectAttributeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(SelectAttributeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void Checkcomplete() {
        Checkcomplete.sendPostRequest(Urls.Checkcomplete, new Response.Listener<String>() {
            public void onResponse(String s) {
                CustomProgressDialog.Dissmiss();
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 100) {
                        Intent intent = new Intent();
                        intent.putExtra("classfiy1", str1);
                        intent.putExtra("classfiy2", str2);
                        intent.putExtra("classfiy3", str3);
                        setResult(RESULT_OK, intent);
                        baseFinish();
                    } else if (code == 200) {
                        ConfirmDialog.showDialog(SelectAttributeActivity.this, "此分类已完成过", null, "取消", "继续", null, true, new
                                ConfirmDialog.OnSystemDialogClickListener() {
                                    public void leftClick(Object object) {
                                    }

                                    public void rightClick(Object object) {
                                        Intent intent = new Intent();
                                        intent.putExtra("classfiy1", str1);
                                        intent.putExtra("classfiy2", str2);
                                        intent.putExtra("classfiy3", str3);
                                        setResult(RESULT_OK, intent);
                                        baseFinish();
                                    }
                                });
                    } else {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(SelectAttributeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(SelectAttributeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(SelectAttributeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在校验...");
    }

    /**
     * 生成布局
     *
     * @param tabs     标签数组
     * @param classfiy 所属分类
     */
    private void creatView(String[] tabs, int classfiy) {
        switch (classfiy) {
            case 1: {
//                attributeselect_item1.addView(linearLayout, lp);
//                for (int i = 0; i < tabs.length; i++) {
//                    TextView textView = creatTextView();
//                    textView.setTag(classfiy);
//                    textView.setId(i);
//                    textView.setText(tabs[i]);
//                    if (i < 3) {
//                        if (i == 2) {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = 0;
//                            linearLayout.addView(textView, tabLp);
//                        } else {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = selectattribute_margin;
//                            linearLayout.addView(textView, tabLp);
//                        }
//                    } else if ((j = i % 3) != 0) {
//                        if (j == 2) {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = 0;
//                            linearLayout.addView(textView, tabLp);
//                        } else {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = selectattribute_margin;
//                            linearLayout.addView(textView, tabLp);
//                        }
//                    } else {
//                        linearLayout = creatLinearLayout();
//                        attributeselect_item1.addView(linearLayout, lp);
//                        LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                        tabLp.rightMargin = selectattribute_margin;
//                        linearLayout.addView(textView, tabLp);
//                    }
//                }
                autoAddTab(attributeselect_item1, tabs, classfiy);
            }
            break;
            case 2: {
//                attributeselect_item2.addView(linearLayout, lp);
//                for (int i = 0; i < tabs.length; i++) {
//                    TextView textView = creatTextView();
//                    textView.setTag(classfiy);
//                    textView.setId(i);
//                    textView.setText(tabs[i]);
//                    if (i < 3) {
//                        if (i == 2) {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = 0;
//                            linearLayout.addView(textView, tabLp);
//                        } else {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = selectattribute_margin;
//                            linearLayout.addView(textView, tabLp);
//                        }
//                    } else if ((j = i % 3) != 0) {
//                        if (j == 2) {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = 0;
//                            linearLayout.addView(textView, tabLp);
//                        } else {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = selectattribute_margin;
//                            linearLayout.addView(textView, tabLp);
//                        }
//                    } else {
//                        linearLayout = creatLinearLayout();
//                        attributeselect_item2.addView(linearLayout, lp);
//                        LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                        tabLp.rightMargin = selectattribute_margin;
//                        linearLayout.addView(textView, tabLp);
//                    }
//                }
                autoAddTab(attributeselect_item2, tabs, classfiy);
            }
            break;
            case 3: {
//                attributeselect_item3.addView(linearLayout, lp);
//                for (int i = 0; i < tabs.length; i++) {
//                    TextView textView = creatTextView();
//                    textView.setTag(classfiy);
//                    textView.setId(i);
//                    textView.setText(tabs[i]);
//                    if (i < 3) {
//                        if (i == 2) {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = 0;
//                            linearLayout.addView(textView, tabLp);
//                        } else {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = selectattribute_margin;
//                            linearLayout.addView(textView, tabLp);
//                        }
//                    } else if ((j = i % 3) != 0) {
//                        if (j == 2) {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = 0;
//                            linearLayout.addView(textView, tabLp);
//                        } else {
//                            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                            tabLp.rightMargin = selectattribute_margin;
//                            linearLayout.addView(textView, tabLp);
//                        }
//                    } else {
//                        linearLayout = creatLinearLayout();
//                        attributeselect_item3.addView(linearLayout, lp);
//                        LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(tabWidth, tabHeight);
//                        tabLp.rightMargin = selectattribute_margin;
//                        linearLayout.addView(textView, tabLp);
//                    }
//                }
                autoAddTab(attributeselect_item3, tabs, classfiy);
            }
            break;
        }
    }

    /**
     * 生成标签
     *
     * @param tabInfos
     */
    private void autoAddTab(LinearLayout tabInfoLinearLayout1, String[] tabInfos, int classfiy) {
        final int selectattribute_shell = (int) getResources().getDimension(R.dimen.selectattribute_shell) * 2;
        final int mar = (int) getResources().getDimension(R.dimen.selectattribute_layout_margin);
        final int windowWidth = Tools.getScreeInfoWidth(this) - mar * 2;
        final int tabMar = mar / 3;
        int layoutwidth = mar * 2;
        LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tabParams.leftMargin = mar;
        tabParams.rightMargin = mar;
        tabParams.topMargin = (int) getResources().getDimension(R.dimen.selectattribute_layout_margin_top);
        LinearLayout tempLayout = new LinearLayout(this);
        tempLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabInfoLinearLayout1.addView(tempLayout, tabParams);
        boolean isAddMar = false;
        for (int i = 0; i < tabInfos.length; i++) {
            String temp = tabInfos[i];
            int length = temp.length();
            AttributeShellView tv = creatTextView(tabInfos[i]);
            tv.setTag(classfiy);
            tv.setId(i);
            TextPaint paint = tv.getTextView().getPaint();
            int minus = length - 4;
            int textWidth;
            if (minus <= 0) {
                textWidth = (int) (tv.getTextView().getTextSize() * 4 + 1);
            } else {
                textWidth = (int) (paint.measureText(temp));
            }
            layoutwidth = layoutwidth + textWidth + tabMar + selectattribute_shell;
            isAddMar = true;
            if (layoutwidth >= windowWidth) {
                layoutwidth = layoutwidth - tabMar;
                if (layoutwidth >= windowWidth) {
                    layoutwidth = mar * 2 + textWidth + tabMar + selectattribute_shell;
                    tempLayout = new LinearLayout(this);
                    tempLayout.setOrientation(LinearLayout.HORIZONTAL);
                    tabInfoLinearLayout1.addView(tempLayout, tabParams);
                } else {
                    isAddMar = false;
                }
            }
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    textWidth, (int) (tv.getTextView().getTextSize() * 2));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    textWidth + selectattribute_shell, (int) getResources().getDimension(R.dimen.selectattribute_height));
            if (isAddMar) {
                params.rightMargin = tabMar;
            }
            tempLayout.addView(tv, params);
            tv.addTextView();
        }
        // if (tabInfos.length > 0) {
        // findViewById(R.id.tablinetop).setVisibility(View.VISIBLE);
        // findViewById(R.id.tablinebottom).setVisibility(View.VISIBLE);
        // }
    }

    private LinearLayout creatLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setHorizontalGravity(LinearLayout.HORIZONTAL);
        return linearLayout;
    }

    private AttributeShellView creatTextView(String text) {
        AttributeShellView attributeShellView = new AttributeShellView(this);
        TextView textView = new TextView(this);
        textView.setTextSize(15);
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
        textView.setSingleLine();
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(0);
        textView.setText(text);
        attributeShellView.hideBg();
        attributeShellView.setTextView(textView);
        attributeShellView.setOnClickListener(tabOnclickListener);
        return attributeShellView;
    }

    private int tab1Id = -1, tab2Id = -1, tab3Id = -1;
    private View.OnClickListener tabOnclickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Object object = v.getTag();
            if (object == null) return;
            AttributeShellView textView = (AttributeShellView) v;
            int id = v.getId();
            switch ((int) object) {
                case 1: {
                    if (tab1Id != id) {
                        if (tab1Id != -1) {
                            AttributeShellView temView = (AttributeShellView) attributeselect_item1.findViewById(tab1Id);
                            temView.settingTextBg(0);
                            temView.hideBg();
                        }
                        textView.settingTextBg(R.mipmap.selatt_bg_03);
                        textView.showBg();
                        tab1Id = id;
                    }
                }
                break;
                case 2: {
                    if (tab2Id != id) {
                        if (tab2Id != -1) {
                            AttributeShellView temView = (AttributeShellView) attributeselect_item2.findViewById(tab2Id);
                            temView.settingTextBg(0);
                            temView.hideBg();
                        }
                        textView.settingTextBg(R.mipmap.selatt_bg_03);
                        textView.showBg();
                        tab2Id = id;
                    }
                }
                break;
                case 3: {
                    if (tab3Id != id) {
                        if (tab3Id != -1) {
                            AttributeShellView temView = (AttributeShellView) attributeselect_item3.findViewById(tab3Id);
                            temView.settingTextBg(0);
                            temView.hideBg();
                        }
                        textView.settingTextBg(R.mipmap.selatt_bg_03);
                        textView.showBg();
                        tab3Id = id;
                    }
                }
                break;
            }
        }
    };

    String str1 = "";
    String str2 = "";
    String str3 = "";

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attributeselect_left: {
                baseFinish();
            }
            break;
            case R.id.attributeselect_right: {
                if (attributeselect_item1_layout.getVisibility() == View.VISIBLE) {
                    if (tab1Id == -1) {
                        Tools.showToast(this, "请选择第一分类属性！");
                        return;
                    }
                    str1 = ((AttributeShellView) attributeselect_item1.findViewById(tab1Id)).getTextView().getText().toString();
                }
                if (attributeselect_item2_layout.getVisibility() == View.VISIBLE) {
                    if (tab2Id == -1) {
                        Tools.showToast(this, "请选择第二分类属性！");
                        return;
                    }
                    str2 = ((AttributeShellView) attributeselect_item2.findViewById(tab2Id)).getTextView().getText().toString();
                }
                if (attributeselect_item3_layout.getVisibility() == View.VISIBLE) {
                    if (tab3Id == -1) {
                        Tools.showToast(this, "请选择第三分类属性！");
                        return;
                    }
                    str3 = ((AttributeShellView) attributeselect_item3.findViewById(tab3Id)).getTextView().getText().toString();
                }
                Checkcomplete();
            }
            break;
        }
    }
}
