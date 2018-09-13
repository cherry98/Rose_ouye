package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.mycorps.TeammemberInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.FlowLayoutView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/25.
 * 队长备注队员页
 */

public class TeamheadRemarkActivity extends BaseActivity implements View.OnClickListener {
    private NetworkConnection teamMemberRemarkInfo;
    private NetworkConnection RemarkTeamMember;

    private void initNetWork() {
        teamMemberRemarkInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TeamheadRemarkActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("user_id", teammemberinfo.getUserId());
                return params;
            }
        };
        RemarkTeamMember = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TeamheadRemarkActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("user_id", teammemberinfo.getUserId());
                params.put("remark", teamheadremark_edit.getText().toString());
                return params;
            }
        };
        RemarkTeamMember.setIsShowDialog(true);
    }

    private EditText teamheadremark_edit;
    private FlowLayoutView teamheadremark_label_layout;
    private String team_id;
    private int position;
    private TeammemberInfo teammemberinfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamheadremark);
        initNetWork();
        team_id = getIntent().getStringExtra("team_id");
        teammemberinfo = (TeammemberInfo) getIntent().getSerializableExtra("teammemberinfo");
        position = getIntent().getIntExtra("position", -1);
        AppTitle appTitle = (AppTitle) findViewById(R.id.teamheadremark_title);
        appTitle.settingName("备注");
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        teamheadremark_edit = (EditText) findViewById(R.id.teamheadremark_edit);
        teamheadremark_label_layout = (FlowLayoutView) findViewById(R.id.teamheadremark_label_layout);
        CircularImageView teamheadremark_ico = (CircularImageView) findViewById(R.id.teamheadremark_ico);
        TextView teamheadremark_name = (TextView) findViewById(R.id.teamheadremark_name);
        ImageView teamheadremark_sex = (ImageView) findViewById(R.id.teamheadremark_sex);
        ImageView teamheadremark_type = (ImageView) findViewById(R.id.teamheadremark_type);
        String url = teammemberinfo.getIco();
        if (!"null".equals(url) && !TextUtils.isEmpty(url)) {
            new ImageLoader(this).DisplayImage(Urls.ImgIp + url, teamheadremark_ico, R.mipmap.grxx_icon_mrtx);
        } else {
            teamheadremark_ico.setImageResource(R.mipmap.grxx_icon_mrtx);
        }
        teamheadremark_name.setText(teammemberinfo.getName());
        if ("-1".equals(teammemberinfo.getSex())) {
            teamheadremark_sex.setVisibility(View.GONE);
        } else if ("0".equals(teammemberinfo.getSex())) {
            teamheadremark_sex.setImageResource(R.mipmap.sex_man);
        } else {
            teamheadremark_sex.setImageResource(R.mipmap.sex_woman);
        }
        if ("2".equals(teammemberinfo.getIdentity())) {
            teamheadremark_type.setImageResource(R.mipmap.item_teammember_identity2);
        } else {
            teamheadremark_type.setVisibility(View.INVISIBLE);
        }
        teamheadremark_edit.setText(teammemberinfo.getRemark());
        findViewById(R.id.teamheadremark_sumbit).setOnClickListener(this);
        getData();
    }

    private void getData() {
        teamMemberRemarkInfo.sendPostRequest(Urls.TeamMemberRemarkInfo, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if (200 == jsonObject.getInt("code")) {
                        jsonObject = jsonObject.getJSONObject("data");
                        if (!jsonObject.isNull("old_remark")) {
                            teamheadremark_edit.setText(jsonObject.getString("old_remark"));
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("history_remark");
                        int length = jsonArray.length();
                        FlowLayoutView.LayoutParams lp = new FlowLayoutView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                Tools.dipToPx(TeamheadRemarkActivity.this, 30));
                        teamheadremark_label_layout.removeAllViews();
                        for (int i = 0; i < length; i++) {
                            teamheadremark_label_layout.addView(getTextView(jsonArray.optString(i)), lp);
                        }
                    } else {
                        Tools.showToast(TeamheadRemarkActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamheadRemarkActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamheadRemarkActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void sendData() {
        RemarkTeamMember.sendPostRequest(Urls.RemarkTeamMember, new Response.Listener<String>() {
            public void onResponse(String s) {
                CustomProgressDialog.Dissmiss();
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if (200 == jsonObject.getInt("code")) {
                        Tools.showToast(TeamheadRemarkActivity.this, "设置成功");
                        Intent intent = new Intent();
                        intent.putExtra("remark", teamheadremark_edit.getText().toString());
                        intent.putExtra("position", position);
                        setResult(RESULT_OK, intent);
                        baseFinish();
                    } else {
                        Tools.showToast(TeamheadRemarkActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamheadRemarkActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TeamheadRemarkActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private TextView getTextView(String string) {
        TextView textView = new TextView(this);
        textView.setId(-1);
        textView.setText(string);
        textView.setBackgroundResource(R.drawable.teamheadremark_label_bg);
        textView.setTextSize(12);
        textView.setTextColor(Color.parseColor("#7C7C7C"));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(Tools.dipToPx(this, 10), 0, Tools.dipToPx(this, 10), 0);
        textView.setOnClickListener(this);
        return textView;
    }

    public void onClick(View v) {
        Tools.d("v.getTag().toString():" + v.getTag() + "");
        if (v.getId() == -1 && v instanceof TextView) {
            teamheadremark_edit.setText(((TextView) v).getText());
        } else {
            switch (v.getId()) {
                case R.id.teamheadremark_sumbit: {
                    sendData();
                }
                break;
            }
        }
    }
}
