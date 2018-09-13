package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 队员基本信息页
 */
public class TeamMemberInfoActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mydetail_title);
        appTitle.settingName("队员信息");
        appTitle.showBack(this);
    }

    public void onStop() {
        super.onStop();
        if (teamMemberInfo != null) {
            teamMemberInfo.stop(Urls.TeamMemBerInfo);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private void initNetworkConnection() {

        teamMemberInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamMemberInfoActivity.this));
                params.put("accessed_num", accessed_num);//	队员账号
                return params;
            }
        };

    }

    private ImageView mydetail_img;
    private ImageLoader imageLoader;
    private TextView mydetail_name, mydetail_phone, mydetail_age, mydetail_citys, mydetail_freetime, mydetail_speciality;
    private TextView mydetail_city;
    private Bitmap bitmap;
    private NetworkConnection teamMemberInfo;
    private String age, accessed_num;
    private ArrayList<String> team_usualplace = new ArrayList<>();
    private ArrayList<String> free_time = new ArrayList<>();
    private ArrayList<String> personal_specialty = new ArrayList<>();
    private String usualplace = null;
    private String freetime = null;
    private String personalspecialty = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail_info);
        initTitle();
        initNetworkConnection();
        personalSquare();
        accessed_num = getIntent().getStringExtra("accessed_num");
        imageLoader = new ImageLoader(this);
        mydetail_img = (ImageView) findViewById(R.id.mydetail_img);
        mydetail_name = (TextView) findViewById(R.id.mydetail_name);
        mydetail_phone = (TextView) findViewById(R.id.mydetail_phone);
        mydetail_age = (TextView) findViewById(R.id.mydetail_age);
        mydetail_city = (TextView) findViewById(R.id.mydetail_city);
        mydetail_citys = (TextView) findViewById(R.id.mydetail_citys);
        mydetail_freetime = (TextView) findViewById(R.id.mydetail_freetime);
        mydetail_speciality = (TextView) findViewById(R.id.mydetail_speciality);

        findViewById(R.id.tv_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + user_mobile);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }


    private String img_url, user_address, user_mobile, user_name;

    private void personalSquare() {
        teamMemberInfo.sendPostRequest(Urls.TeamMemBerInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {

                        user_name = jsonObject.getString("user_name");
                        user_mobile = jsonObject.getString("user_mobile");
                        user_address = jsonObject.getString("user_address");
                        img_url = jsonObject.getString("img_url");
                        age = jsonObject.getString("age");
                        if (!TextUtils.isEmpty(img_url)) {
                            imageLoader.DisplayImage(Urls.ImgIp + img_url, mydetail_img, R.mipmap.grxx_icon_mrtx);
                        } else {
                            mydetail_img.setImageResource(R.mipmap.grxx_icon_mrtx);
                        }
                        if (!TextUtils.isEmpty(user_address) && !"null".equals(user_address)) {
                            mydetail_city.setText(user_address);
                        }
                        if (!TextUtils.isEmpty(user_name)) {
                            mydetail_name.setText(user_name);
                        }
                        if (!TextUtils.isEmpty(user_mobile)) {
                            mydetail_phone.setText(user_mobile);
                        }
                        if (!TextUtils.isEmpty(age)) {
                            mydetail_age.setText(age);
                        }
                        JSONArray jsonArray2 = jsonObject.optJSONArray("usual_place");  //常去地点
                        if (jsonArray2 != null) {
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                String string = jsonArray2.getString(i);
                                team_usualplace.add(string);
                            }
                            if (!team_usualplace.isEmpty()) {
                                usualplace = team_usualplace.toString();
                                mydetail_citys.setText(usualplace.substring(1, usualplace.length() - 1));
                            }
                        }


                        JSONArray jsonArray3 = jsonObject.optJSONArray("free_time");  //空闲时间
                        if (jsonArray3 != null) {
                            for (int i = 0; i < jsonArray3.length(); i++) {
                                String string = jsonArray3.getString(i);
                                free_time.add(string);
                            }
                            if (!free_time.isEmpty()) {
                                freetime = free_time.toString();
                                mydetail_freetime.setText(freetime.substring(1, freetime.length() - 1));
                            }
                        }

                        JSONArray jsonArray4 = jsonObject.optJSONArray("personal_specialty");  //个人特长
                        if (jsonArray4 != null) {
                            for (int i = 0; i < jsonArray4.length(); i++) {
                                String string = jsonArray4.getString(i);
                                personal_specialty.add(string);
                            }
                            if (!personal_specialty.isEmpty()) {
                                personalspecialty = personal_specialty.toString();
                                mydetail_speciality.setText(personalspecialty.substring(1, usualplace.length() - 1));
                            }
                        }

                    } else {
                        Tools.showToast(TeamMemberInfoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamMemberInfoActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamMemberInfoActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
