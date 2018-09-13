package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.AttributeShellView;

import java.util.ArrayList;

/**
 * 属性选择页面
 */
public class OfflineSelectAttributeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {
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
    }

    private String projectid, task_pack_id, storeid;
    private LinearLayout attributeselect_item1, attributeselect_item2, attributeselect_item3;
    private View attributeselect_item1_layout, attributeselect_item2_layout, attributeselect_item3_layout;
    private TextView attributeselect_item1_name, attributeselect_item2_name, attributeselect_item3_name;
    private OfflineDBHelper offlineDBHelper;
    private int task_size;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attributeselect);
        offlineDBHelper = new OfflineDBHelper(this);
        initTitle();
        Intent data = getIntent();
        projectid = data.getStringExtra("project_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        storeid = data.getStringExtra("storeid");
        task_size = data.getIntExtra("task_size", 0);
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
        ArrayList<String> list = offlineDBHelper.getCategorys(AppInfo.getName(this), projectid, storeid, task_pack_id);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String[] ss = list.get(i).split(",");
            switch (i) {
                case 0: {
                    attributeselect_item1_layout.setVisibility(View.VISIBLE);
                    attributeselect_item1_name.setText("一级分类-" + ss[0]);
                    creatView(ss, 1);
                }
                break;
                case 1: {
                    attributeselect_item2_layout.setVisibility(View.VISIBLE);
                    attributeselect_item2_name.setText("二级分类-" + ss[0]);
                    creatView(ss, 2);
                }
                break;
                case 2: {
                    attributeselect_item3_layout.setVisibility(View.VISIBLE);
                    attributeselect_item3_name.setText("三级分类-" + ss[0]);
                    creatView(ss, 3);
                }
                break;
            }
        }
    }

    private void Checkcomplete() {
        if (offlineDBHelper.isCompletedForCategory(AppInfo.getName(this), projectid, storeid, task_pack_id, str1, str2, str3,
                task_size)) {
            ConfirmDialog.showDialog(this, "此分类已完成过", null, "取消", "继续", null, true, new
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
            Intent intent = new Intent();
            intent.putExtra("classfiy1", str1);
            intent.putExtra("classfiy2", str2);
            intent.putExtra("classfiy3", str3);
            setResult(RESULT_OK, intent);
            baseFinish();
        }
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
                autoAddTab(attributeselect_item1, tabs, classfiy);
            }
            break;
            case 2: {
                autoAddTab(attributeselect_item2, tabs, classfiy);
            }
            break;
            case 3: {
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
        for (int i = 1; i < tabInfos.length; i++) {
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    textWidth + selectattribute_shell, (int) getResources().getDimension(R.dimen.selectattribute_height));
            if (isAddMar) {
                params.rightMargin = tabMar;
            }
            tempLayout.addView(tv, params);
            tv.addTextView();
        }
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
