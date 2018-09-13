package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.AsPhoneSelectAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.LableMerInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * beibei  按手机号挑选页面
 */
public class AsdPhoneSelectActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AsPhoneSelectAdapter.PhoneEdit {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.add_title);
        appTitle.settingName("按手机号挑选");
        appTitle.showBack(this);
    }

    public void onStop() {
        super.onStop();
        if (labelList != null) {
            labelList.stop(Urls.LabelEdit);
        }

    }

    private void iniNetworkConnection() {
        labelList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(AsdPhoneSelectActivity.this));
                params.put("token", Tools.getToken());
                Tools.d("tag", params.toString());
                return params;
            }
        };
    }


    private MyListView mListView;
    private LinearLayout lin_addphone;  //添加按钮
    private TextView tv_addphone;
    private AsPhoneSelectAdapter addPhoneAdapter;
    private NetworkConnection labelList;
    private ArrayList<LableMerInfo> mData;
    private String Isvisible;
    private String ischart;

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asd_phone);
        initTitle();
        mData = new ArrayList<>();
        iniNetworkConnection();
        Isvisible = getIntent().getStringExtra("Isvisible"); //  1可见  2 不可见
        ischart = getIntent().getStringExtra("ischart");
        mListView = (MyListView) findViewById(R.id.list_view);
        lin_addphone = (LinearLayout) findViewById(R.id.lin_addphone);
        tv_addphone = (TextView) findViewById(R.id.tv_addphone);
        addPhoneAdapter = new AsPhoneSelectAdapter(this, mData);
        mListView.setAdapter(addPhoneAdapter);
        addPhoneAdapter.setPhoneEditListener(this);
        add();
    }


    private void getData() {
        labelList.sendPostRequest(Urls.LabelList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!mData.isEmpty()) {
                            mData.clear();
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            if (null != jsonArray) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    LableMerInfo lableMerInfo = new LableMerInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    lableMerInfo.setLabel_id(object.getString("label_id"));
                                    lableMerInfo.setLabel_name(object.getString("label_name"));
                                    lableMerInfo.setUsermobile_list(object.getString("usermobile_list"));
                                    mData.add(lableMerInfo);
                                }
                            }
                            if (addPhoneAdapter != null) {
                                addPhoneAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(AsdPhoneSelectActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AsdPhoneSelectActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AsdPhoneSelectActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void add() { //系统跳转到“添加手机号”页面，在此页面显示标签中包含的所有手机号信息，用户可根据需要在此标签中进行添加和删除手机号；
        lin_addphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AsdPhoneSelectActivity.this, AddPhoneActivity.class);
                intent.putExtra("Isvisible", Isvisible);
                intent.putExtra("ischart", ischart);
                startActivityForResult(intent, 1); //添加手机号页面
            }
        });
        tv_addphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AsdPhoneSelectActivity.this, AddPhoneActivity.class);
                intent.putExtra("Isvisible", Isvisible);
                intent.putExtra("ischart", ischart);
                startActivityForResult(intent, 1); //添加手机号页面
            }
        });

    }

    @Override
    public void onBack() {
        baseFinish();
    }


    @Override
    public void itemclick(int pos) {
        LableMerInfo lableMerInfo = mData.get(pos);
        Intent intent = new Intent(AsdPhoneSelectActivity.this, AddPhoneActivity.class);
        intent.putExtra("label_id", lableMerInfo.getLabel_id());
        intent.putExtra("label_name", lableMerInfo.getLabel_name());
        intent.putExtra("usermobile_list", lableMerInfo.getUsermobile_list());
        intent.putExtra("Isvisible", Isvisible);
        intent.putExtra("ischart",ischart);
        startActivityForResult(intent, 0); //添加手机号页面
    }

    private String invisible_label, usermobile_list, invisible_type;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_TAGLIST) {
            switch (requestCode) {
                case 0: {  //标签页面
                    if (data != null) {

                        invisible_label = data.getStringExtra("invisible_label");
                        usermobile_list = data.getStringExtra("usermobile_list");
                        invisible_type = data.getStringExtra("invisible_type");
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", invisible_type);
                        intent.putExtra("invisible_label", invisible_label);
                        intent.putExtra("usermobile_list", usermobile_list);
                        setResult(AppInfo.REQUEST_CODE_ISVISIBLE, intent);
                        Tools.d("tag", "addphone-====手机变页面=>>>>" + usermobile_list + "    " + invisible_label + "  " + invisible_type);
                        baseFinish();
                    }
                }
                break;
            }
        } else if (resultCode == AppInfo.REQUEST_CODE_ISVISIBLE) {
            if (requestCode == 1) {
                if (data != null) {
                    invisible_label = data.getStringExtra("invisible_label");
                    usermobile_list = data.getStringExtra("usermobile_list");
                    invisible_type = data.getStringExtra("invisible_type");
                    Intent intent = new Intent();
                    intent.putExtra("invisible_type", invisible_type);
                    intent.putExtra("invisible_label", invisible_label);
                    intent.putExtra("usermobile_list", usermobile_list);
                    setResult(AppInfo.REQUEST_CODE_ISVISIBLE, intent);
                    Tools.d("tag", "addphone-====创建标签回来页面=>>>>" + usermobile_list + "    " + invisible_label + "  " + invisible_type);
                    baseFinish();
                }
            }
        }
    }
}
