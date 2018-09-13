package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置战队页面
 */
public class SetCorpsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.setcorps_title);
        appTitle.settingName("战队设置");
        appTitle.showBack(this);
    }

    public void setDissolveTeam() {
        if ("1".equals(is_dissolve) && isCaptain) {
            appTitle.settingExit("解散战队", getResources().getColor(R.color.homepage_notselect), new AppTitle.OnExitClickForAppTitle() {
                @Override
                public void onExit() {
                    if ("1".equals(personal_auth)) {
                        Intent intent = new Intent(SetCorpsActivity.this, PersonalDissolveActivity.class);
                        intent.putExtra("team_id", team_id);
                        startActivity(intent);
                    } else {
                        ConfirmDialog.showDialog(SetCorpsActivity.this, "提示！", 3, "您确认要解散战队吗？", "取消", "确定", null, false,
                                new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                        dissolveTeam();
                                    }
                                });
                    }
                }
            });
        }
    }

    private void dissolveTeam() {
        dissolveTeam.sendPostRequest(Urls.DissolveTeam, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(SetCorpsActivity.this, "战队已解散");
                        ScreenManager screenManager = ScreenManager.getScreenManager();
                        screenManager.finishActivity(TeamInformationActivity.class);
                        baseFinish();
                    } else {
                        Tools.showToast(SetCorpsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, 5000);
    }

    private void initNetwork() {
        teamSetting = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SetCorpsActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                return params;
            }
        };
        teamSettingUpdate = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SetCorpsActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                if (!TextUtils.isEmpty(invitation)) {
                    params.put("invitation", invitation);
                }
                if (!TextUtils.isEmpty(open)) {
                    params.put("open", open);
                }
                if (!TextUtils.isEmpty(open_total_amount)) {
                    params.put("open_total_amount", open_total_amount);
                }
                return params;
            }
        };
        deputySetting = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SetCorpsActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                if (!TextUtils.isEmpty(original_user_id)) {
                    params.put("original_user_id", original_user_id);
                }
                params.put("user_id", user_id);
                return params;
            }
        };
        dissolveTeam = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SetCorpsActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                return params;
            }
        };
        TeamSquare = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SetCorpsActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("close_square", squareOpen);//是否关闭广场（0：不关闭，1：关闭）
                return params;
            }
        };
    }

    private NetworkConnection teamSetting, teamSettingUpdate, deputySetting, dissolveTeam, TeamSquare;
    private String team_id, is_dissolve;
    private View setcorps_captain_set1, setcorps_captain_set2;
    private CircularImageView setcorps_img1, setcorps_img2;
    private TextView setcorps_name1, setcorps_name2, setcorps_phone1, setcorps_phone2;
    private String invitation, open, open_total_amount;
    private CheckBox setcorps_permission1, setcorps_permission2, setcorps_permission3, setcorps_permission4;
    private ImageLoader imageLoader;
    private String user_id, original_user_id, original_user_id1, original_user_id2;//新队副id 原队副id
    private String personal_auth, enterprise_auth;//个人认证 企业认证
    private boolean isCaptain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_corps);
        isCaptain = getIntent().getBooleanExtra("isCaptain", false);
        imageLoader = new ImageLoader(this);
        team_id = getIntent().getStringExtra("team_id");
        personal_auth = getIntent().getStringExtra("personal_auth");
        enterprise_auth = getIntent().getStringExtra("enterprise_auth");
        initNetwork();
        initView();
        getData();
        initTitle();
        setcorps_captain_set1.setOnClickListener(this);
        setcorps_captain_set2.setOnClickListener(this);
        setcorps_permission1.setOnClickListener(this);
        setcorps_permission2.setOnClickListener(this);
        setcorps_permission3.setOnClickListener(this);
        setcorps_permission4.setOnClickListener(this);
    }

    private void initView() {
        setcorps_captain_set1 = findViewById(R.id.setcorps_captain_set1);
        setcorps_captain_set2 = findViewById(R.id.setcorps_captain_set2);
        setcorps_img1 = (CircularImageView) findViewById(R.id.setcorps_img1);
        setcorps_img2 = (CircularImageView) findViewById(R.id.setcorps_img2);
        setcorps_name1 = (TextView) findViewById(R.id.setcorps_name1);
        setcorps_name2 = (TextView) findViewById(R.id.setcorps_name2);
        setcorps_phone1 = (TextView) findViewById(R.id.setcorps_phone1);
        setcorps_phone2 = (TextView) findViewById(R.id.setcorps_phone2);
        setcorps_permission1 = (CheckBox) findViewById(R.id.setcorps_permission1);
        setcorps_permission2 = (CheckBox) findViewById(R.id.setcorps_permission2);
        setcorps_permission3 = (CheckBox) findViewById(R.id.setcorps_permission3);
        setcorps_permission4 = (CheckBox) findViewById(R.id.setcorps_permission4);
    }

    private void getData() {
        teamSetting.sendPostRequest(Urls.TeamSetting, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        String invitation = jsonObject.getString("invitation");
                        if ("1".equals(invitation)) {
                            setcorps_permission1.setChecked(true);
                        } else {
                            setcorps_permission1.setChecked(false);
                        }
                        String open = jsonObject.getString("open");
                        if ("1".equals(open)) {
                            setcorps_permission2.setChecked(true);
                        } else {
                            setcorps_permission2.setChecked(false);
                        }
                        String open_total_amount = jsonObject.getString("open_total_amount");
                        if ("1".equals(open_total_amount)) {
                            setcorps_permission3.setChecked(true);
                        } else {
                            setcorps_permission3.setChecked(false);
                        }
                        squareOpen = jsonObject.getString("close_square");
                        if ("0".equals(squareOpen)) {
                            setcorps_permission4.setChecked(true);
                        } else {
                            setcorps_permission4.setChecked(false);
                        }
                        is_dissolve = jsonObject.getString("is_dissolve");
                        if (isCaptain) {//只有队长才能设置副队
                            findViewById(R.id.setcorps_captain).setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.optJSONArray("deputy");
                            if (jsonArray != null) {
                                int length = jsonArray.length();
                                if (length == 1) {
                                    JSONObject object = jsonArray.getJSONObject(0);
                                    original_user_id1 = object.getString("id");
                                    setcorps_name1.setText(object.getString("name"));
                                    setcorps_phone1.setText(object.getString("mobile"));
                                    String img = object.getString("img");
                                    if (!"null".equals(img) && !TextUtils.isEmpty(img)) {
                                        imageLoader.DisplayImage(Urls.ImgIp + img, setcorps_img1, R.mipmap.grxx_icon_mrtx);
                                    } else {
                                        setcorps_img1.setImageResource(R.mipmap.grxx_icon_mrtx);
                                    }
                                    setcorps_img2.setImageResource(R.mipmap.grxx_icon_mrtx);
                                    setcorps_name2.setVisibility(View.GONE);
                                    setcorps_phone2.setVisibility(View.GONE);
                                    findViewById(R.id.setcorps_text2).setVisibility(View.VISIBLE);
                                } else if (length == 2) {
                                    JSONObject object = jsonArray.getJSONObject(0);
                                    original_user_id1 = object.getString("id");
                                    setcorps_name1.setText(object.getString("name"));
                                    setcorps_phone1.setText(object.getString("mobile"));
                                    String img = object.getString("img");
                                    if (!"null".equals(img) && !TextUtils.isEmpty(img)) {
                                        imageLoader.DisplayImage(Urls.ImgIp + img, setcorps_img1, R.mipmap.grxx_icon_mrtx);
                                    } else {
                                        setcorps_img1.setImageResource(R.mipmap.grxx_icon_mrtx);
                                    }
                                    object = jsonArray.getJSONObject(1);
                                    img = object.getString("img");
                                    if (!"null".equals(img) && !TextUtils.isEmpty(img)) {
                                        imageLoader.DisplayImage(Urls.ImgIp + img, setcorps_img2, R.mipmap.grxx_icon_mrtx);
                                    } else {
                                        setcorps_img2.setImageResource(R.mipmap.grxx_icon_mrtx);
                                    }
                                    original_user_id2 = object.getString("id");
                                    setcorps_name2.setText(object.getString("name"));
                                    setcorps_phone2.setText(object.getString("mobile"));
                                    findViewById(R.id.setcorps_text1).setVisibility(View.GONE);
                                    findViewById(R.id.setcorps_text2).setVisibility(View.GONE);
                                } else if (length == 0) {
                                    setcorps_img1.setImageResource(R.mipmap.grxx_icon_mrtx);
                                    setcorps_img2.setImageResource(R.mipmap.grxx_icon_mrtx);
                                    setcorps_name1.setVisibility(View.GONE);
                                    setcorps_name2.setVisibility(View.GONE);
                                    setcorps_phone1.setVisibility(View.GONE);
                                    setcorps_phone2.setVisibility(View.GONE);
                                    findViewById(R.id.setcorps_text1).setVisibility(View.VISIBLE);
                                    findViewById(R.id.setcorps_text2).setVisibility(View.VISIBLE);
                                }
                            } else {
                                setcorps_img1.setImageResource(R.mipmap.grxx_icon_mrtx);
                                setcorps_img2.setImageResource(R.mipmap.grxx_icon_mrtx);
                                setcorps_name1.setVisibility(View.GONE);
                                setcorps_name2.setVisibility(View.GONE);
                                setcorps_phone1.setVisibility(View.GONE);
                                setcorps_phone2.setVisibility(View.GONE);
                                findViewById(R.id.setcorps_text1).setVisibility(View.VISIBLE);
                                findViewById(R.id.setcorps_text2).setVisibility(View.VISIBLE);
                            }
                        } else {//不显示设置副队
                            findViewById(R.id.setcorps_captain).setVisibility(View.GONE);
                        }
                        setDissolveTeam();
                    } else {
                        Tools.showToast(SetCorpsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setcorps_captain_set1: {
                Intent intent = new Intent(this, TeammemberActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("state", 2);
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.setcorps_captain_set2: {
                Intent intent = new Intent(this, TeammemberActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("state", 2);
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.setcorps_permission1: {
                if (setcorps_permission1.isChecked()) {
                    invitation = "1";
                } else {
                    invitation = "0";
                }
                teamSettingUpdate();
            }
            break;
            case R.id.setcorps_permission2: {
                if (setcorps_permission2.isChecked()) {
                    open = "1";
                } else {
                    open = "0";
                }
                teamSettingUpdate();
            }
            break;
            case R.id.setcorps_permission3: {
                if (setcorps_permission3.isChecked()) {
                    open_total_amount = "1";
                } else {
                    open_total_amount = "0";
                }
                teamSettingUpdate();
            }
            break;
            case R.id.setcorps_permission4: {
                if (setcorps_permission4.isChecked()) {
                    squareOpen = "0";
                } else {
                    squareOpen = "1";
                }
                teamSquareUpdate();
            }
            break;
        }
    }

    private String squareOpen = "1";//是否关闭广场（0：不关闭，1：关闭）

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_SETCAPTAIN) {
            if (requestCode == 0) {
                String user_id = data.getStringExtra("user_id");
                if (!user_id.equals(original_user_id1)) {
                    String team_img = data.getStringExtra("team_img");
                    if (!"null".equals(team_img) && !TextUtils.isEmpty(team_img)) {
                        imageLoader.DisplayImage(Urls.ImgIp + team_img, setcorps_img1, R.mipmap.grxx_icon_mrtx);
                    } else {
                        setcorps_img1.setImageResource(R.mipmap.grxx_icon_mrtx);
                    }
                    setcorps_name1.setText(data.getStringExtra("team_name"));
                    setcorps_phone1.setText(data.getStringExtra("team_phone"));
                    setcorps_name1.setVisibility(View.VISIBLE);
                    setcorps_phone1.setVisibility(View.VISIBLE);
                    findViewById(R.id.setcorps_text1).setVisibility(View.GONE);
                    original_user_id = original_user_id1;
                    this.user_id = user_id;
                    deputySetting();
                }
            } else if (requestCode == 1) {
                String user_id = data.getStringExtra("user_id");
                if (!user_id.equals(original_user_id2)) {
                    String team_img = data.getStringExtra("team_img");
                    if (!"null".equals(team_img) && !TextUtils.isEmpty(team_img)) {
                        imageLoader.DisplayImage(Urls.ImgIp + team_img, setcorps_img2, R.mipmap.grxx_icon_mrtx);
                    } else {
                        setcorps_img2.setImageResource(R.mipmap.grxx_icon_mrtx);
                    }
                    setcorps_name2.setText(data.getStringExtra("team_name"));
                    setcorps_phone2.setText(data.getStringExtra("team_phone"));
                    setcorps_name2.setVisibility(View.VISIBLE);
                    setcorps_phone2.setVisibility(View.VISIBLE);
                    findViewById(R.id.setcorps_text2).setVisibility(View.GONE);
                    original_user_id = original_user_id2;
                    this.user_id = user_id;
                    deputySetting();
                }
            }
        }
    }

    private void deputySetting() {
        deputySetting.sendPostRequest(Urls.DeputySetting, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(SetCorpsActivity.this, "操作成功");
                    } else {
                        Tools.showToast(SetCorpsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, 5000);
    }

    private void teamSettingUpdate() {
        teamSettingUpdate.sendPostRequest(Urls.TeamSettingUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(SetCorpsActivity.this, "操作成功");
                    } else {
                        Tools.showToast(SetCorpsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, 5000);
    }

    private void teamSquareUpdate() {
        TeamSquare.sendPostRequest(Urls.TeamSquare, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(SetCorpsActivity.this, "操作成功");
                    } else {
                        Tools.showToast(SetCorpsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SetCorpsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, 5000);
    }
}
