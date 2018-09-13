package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.AddPhoneActivity;
import com.orange.oy.activity.createtask_317.AsdPhoneSelectActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.LableMerInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * beibei  对谁不可见  and  可见
 * <p>
 * 甩图-->相册-->我参与的活动 / 全部活动-->赞助-->对谁可见-->谁不可见红包
 * 2、用户点击“按地区挑选”按钮，系统跳转至“按地区挑选”页面；
 */
public class ToWhomInVisibleRedActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private String City; //回显的数据

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.whom_title);
        visible_type = getIntent().getStringExtra("visible_type");
        City = getIntent().getStringExtra("city");
        if (!Tools.isEmpty(visible_type)) {
            if (visible_type.equals("4")) {
                appTitle.settingName("谁可见红包");
            } else {
                appTitle.settingName("谁不可见红包");
            }
        }
        appTitle.showBack(this);
    }

    private View view_line;
    private LinearLayout lin_invisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_whom_invisible);
        ImageView iv_pic = (ImageView) findViewById(R.id.iv_pic);
        TextView tv_des = (TextView) findViewById(R.id.tv_des);
        view_line = findViewById(R.id.view_line);
        lin_invisible = (LinearLayout) findViewById(R.id.lin_invisible);
        iv_pic.setVisibility(View.GONE);
        tv_des.setText("按地区挑选");
        view_line.setVisibility(View.GONE);
        lin_invisible.setVisibility(View.GONE);
        initTitle();
        findViewById(R.id.lin_phone).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_phone: { //地点选择
                Intent intent = new Intent(this, AreaActivity.class);
                intent.putExtra("visible_type", visible_type);
                intent.putExtra("city", City);
                startActivityForResult(intent, 0);

            }
            break;
        }
    }

    private String visible_type;
    private String city;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_ISVISIBLE) {
            switch (requestCode) {
                case 0: {  // 地址返回的
                    if (data != null) {
                        city = data.getStringExtra("city");
                        visible_type = data.getStringExtra("visible_type");
                        Intent intent = new Intent();
                        intent.putExtra("visible_type", visible_type);
                        intent.putExtra("city", city);
                        setResult(RESULT_OK, intent);
                        baseFinish();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
