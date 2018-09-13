package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_321.TeamSelectActivity;
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
 * 用户点击“按手机号挑选”按钮，系统判断用户是否创建过标签群组，
 * 如创建过则跳转至“按手机号挑选”页面；如未创建过则跳转至“添加手机号”页面；
 */
public class ToWhomInVisibleActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.whom_title);
        Isvisible = getIntent().getStringExtra("Isvisible");
        ischart = getIntent().getStringExtra("ischart");
        if (!Tools.isEmpty(Isvisible)) {
            if (Isvisible.equals("1")) {
                appTitle.settingName("谁可见任务");
            } else {
                appTitle.settingName("谁不可见任务");
            }
        }
        appTitle.showBack(this);
    }

    private void iniNetworkConnection() {
        labelList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ToWhomInVisibleActivity.this));
                params.put("token", Tools.getToken());
                Tools.d("tag", params.toString());
                return params;
            }
        };
    }

    private String Isvisible; //对谁可见 1   不可见 2
    private String ischart;  //  //  1是集图活动   2是任务模板

    private NetworkConnection labelList;
    private ArrayList<LableMerInfo> lableMerInfoArrayList;

    public void onStop() {
        super.onStop();
        if (labelList != null) {
            labelList.stop(Urls.LabelEdit);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lableMerInfoArrayList = new ArrayList<>();
        setContentView(R.layout.activity_to_whom_invisible);
        initTitle();
        iniNetworkConnection();
        findViewById(R.id.lin_phone).setOnClickListener(this);
        findViewById(R.id.lin_invisible).setOnClickListener(this);
        getData();
    }

    private void getData() {
        labelList.sendPostRequest(Urls.LabelList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (lableMerInfoArrayList == null) {
                            lableMerInfoArrayList = new ArrayList<>();
                        }

                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                LableMerInfo lableMerInfo = new LableMerInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                lableMerInfo.setLabel_id(object.getString("label_id"));
                                lableMerInfo.setLabel_name(object.getString("label_name"));
                                lableMerInfo.setUsermobile_list(object.getString("usermobile_list"));
                                lableMerInfoArrayList.add(lableMerInfo);
                            }
                        }
                    } else {
                        Tools.showToast(ToWhomInVisibleActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ToWhomInVisibleActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ToWhomInVisibleActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_phone: {
                if (null != lableMerInfoArrayList && lableMerInfoArrayList.size() > 0) {
                    //去标签页面
                    Intent intent = new Intent(this, AsdPhoneSelectActivity.class);
                    intent.putExtra("Isvisible", Isvisible); //1可见  2 不可见
                    intent.putExtra("ischart", ischart);
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent(this, AddPhoneActivity.class);
                    intent.putExtra("Isvisible", Isvisible);
                    intent.putExtra("ischart", ischart);
                    startActivityForResult(intent, 1);
                }
            }
            break;
            case R.id.lin_invisible: {  // “按战队挑选”页面
                Intent intent = new Intent(this, TeamSelectActivity.class);
                intent.putExtra("isFromCorp", true);
                intent.putExtra("Isvisible", Isvisible);
                intent.putExtra("ischart", ischart);
                startActivityForResult(intent, 2);
            }
            break;
        }
    }

    private String invisible_type, invisible_team, outlet_package_type;
    private String usermobile_list; //手机号
    private String invisible_label;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_ISVISIBLE) {
            switch (requestCode) {
                case 0: {  //标签页面
                    if (data != null) {
                        invisible_type = data.getStringExtra("invisible_type");
                        usermobile_list = data.getStringExtra("usermobile_list");
                        invisible_label = data.getStringExtra("invisible_label");
                     /*   Tools.d("tag", "标签页面返回=====ToWhomInVisibleActivity>>>>>>" + usermobile_list + "  "
                                + invisible_type + "  " + invisible_label);*/
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", invisible_type);
                        intent.putExtra("usermobile_list", usermobile_list);
                        intent.putExtra("invisible_label", invisible_label);
                        setResult(AppInfo.REQUEST_CODE_ALL, intent);
                        finish();
                    }
                }
                break;
                case 1: {//添加手机号页面
                    if (data != null) {
                        invisible_type = data.getStringExtra("invisible_type");
                        usermobile_list = data.getStringExtra("usermobile_list");
                        invisible_label = data.getStringExtra("invisible_label");
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", invisible_type);
                        intent.putExtra("usermobile_list", usermobile_list);
                        intent.putExtra("invisible_label", invisible_label);
                        setResult(AppInfo.REQUEST_CODE_ALL, intent);
                        finish();
                    }
                }
                break;
                case 2: { //按战队挑选任务
                    if (data != null) {
                        invisible_type = data.getStringExtra("invisible_type"); //对谁可见或不可见
                        invisible_team = data.getStringExtra("invisible_team");
                        outlet_package_type = data.getStringExtra("outlet_package_type");
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", invisible_type);
                        intent.putExtra("invisible_team", invisible_team); // 战队id，多个以英文逗号分隔
                        intent.putExtra("outlet_package_type", outlet_package_type); // 网点打包类型（1：按省份打包；2：按城市打包）
                        setResult(AppInfo.REQUEST_CODE_ALL, intent);
                        finish();
                    }
                }
            }
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
