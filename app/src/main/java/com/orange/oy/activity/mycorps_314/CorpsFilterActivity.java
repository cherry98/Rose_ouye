package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_321.TeamSelectActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.FlowLayoutView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 加入战队筛选条件选择
 */
public class CorpsFilterActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.corpsfilter_title);
        appTitle.settingName("筛选");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        teamSpeciality = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpsFilterActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        allProvince = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    private FlowLayoutView corpsfilter_special, corpsfilter_identity, corpsfilter_num, corpsfilter_district;
    private NetworkConnection teamSpeciality, allProvince;
    private String[] identity = {"已认证", "未认证"};
    private String[] num = {"1-10人", "11-20人", "21-30人", "31-40人", "41-50人"};
    private String specialies = "", identities = "", nums = "", districts = "";
    private EditText cropsfilteer_search;
    private int length;
    private int isTeam; //判断是否  0是战队筛选  1是对谁可见来的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corps_filter);
        isTeam = getIntent().getIntExtra("isTeam", 0);
        initTitle();
        initNetwork();
        cropsfilteer_search = (EditText) findViewById(R.id.cropsfilteer_search);
        corpsfilter_special = (FlowLayoutView) findViewById(R.id.corpsfilter_special);
        corpsfilter_identity = (FlowLayoutView) findViewById(R.id.corpsfilter_identity);
        corpsfilter_num = (FlowLayoutView) findViewById(R.id.corpsfilter_num);
        corpsfilter_district = (FlowLayoutView) findViewById(R.id.corpsfilter_district);
        getTeamSpeciality();
        getIdentity();
        getNum();
        getDistrict();
        findViewById(R.id.corpsfilter_filter).setOnClickListener(this);
    }


    private void getDistrict() {
        allProvince.sendPostRequest(Urls.AllProvince, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                final TextView textView = new TextView(CorpsFilterActivity.this);
                                textView.setText(jsonArray.getJSONObject(i).getString("province"));
                                textView.setPadding(5, 5, 5, 5);
                                textView.setGravity(Gravity.CENTER);
                                textView.setTextSize(14);
                                textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                textView.setBackgroundResource(R.drawable.flowlayout_shape1);
                                textView.setId(0);
                                corpsfilter_district.addView(textView);
                                final int[] tag = {1};

                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (isTeam == 0) {  //0是战队筛选  1是对谁可见来的
                                            tag[0]++;
                                            if (tag[0] % 2 == 0) {
                                                textView.setTextColor(getResources().getColor(R.color.app_background2));
                                                textView.setBackgroundResource(R.drawable.flowlayout_shape2);
                                                if (districts == null || "".equals(districts)) {
                                                    districts = textView.getText().toString();
                                                } else {
                                                    if (!districts.contains(textView.getText().toString())) {
                                                        districts = districts + "," + textView.getText().toString();
                                                    }
                                                }
                                            } else {
                                                textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                                textView.setBackgroundResource(R.drawable.flowlayout_shape1);
                                                if (districts.equals(textView.getText().toString())) {
                                                    districts = districts.replace(textView.getText().toString(), "");
                                                } else if ((districts.substring(0, textView.getText().toString().length() - 1)).equals(textView.getText().toString())) {
                                                    districts = districts.replace(textView.getText().toString() + ",", "");
                                                } else {
                                                    districts = districts.replace("," + textView.getText().toString(), "");
                                                }
                                            }
                                        } else {
                                            int isCheck = v.getId();
                                            TextView textView = (TextView) v;
                                            if (isCheck == 0) {
                                                int count = corpsfilter_district.getChildCount();
                                                for (int i = 0; i < count; i++) {
                                                    TextView textView1 = (TextView) corpsfilter_district.getChildAt(i);
                                                    textView1.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                                    textView1.setBackgroundResource(R.drawable.flowlayout_shape1);
                                                    textView1.setId(0);
                                                }
                                                districts = textView.getText().toString();
                                                //
                                                v.setId(1);  //点击变红
                                                textView.setTextColor(getResources().getColor(R.color.app_background2));
                                                textView.setBackgroundResource(R.drawable.flowlayout_shape2);
                                            } else {  //点击变灰
                                                v.setId(0);
                                                textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                                textView.setBackgroundResource(R.drawable.flowlayout_shape1);

                                                int count = corpsfilter_district.getChildCount();
                                                for (int i = 0; i < count; i++) {
                                                    TextView textView1 = (TextView) corpsfilter_district.getChildAt(i);
                                                    textView1.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                                    textView1.setBackgroundResource(R.drawable.flowlayout_shape1);
                                                    textView1.setId(0);
                                                }
                                                districts = "";
                                            }
                                        }


                                        /**/
                                    }
                                });
                            }
                        }
                    } else {
                        Tools.showToast(CorpsFilterActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpsFilterActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpsFilterActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void getNum() {
        for (int i = 0; i < num.length; i++) {
            final TextView textView = new TextView(CorpsFilterActivity.this);
            textView.setText(num[i]);
            textView.setPadding(5, 5, 5, 5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(14);
            textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
            textView.setBackgroundResource(R.drawable.flowlayout_shape1);
            corpsfilter_num.addView(textView);
            final int[] tag = {1};
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tag[0]++;
                    if (tag[0] % 2 == 0) {
                        textView.setTextColor(getResources().getColor(R.color.app_background2));
                        textView.setBackgroundResource(R.drawable.flowlayout_shape2);
                        if (nums == null || "".equals(nums)) {
                            nums = textView.getText().toString();
                        } else {
                            if (!nums.contains(textView.getText().toString())) {
                                nums = nums + "," + textView.getText().toString();
                            }
                        }
                    } else {
                        textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                        textView.setBackgroundResource(R.drawable.flowlayout_shape1);
                        if (nums.equals(textView.getText().toString())) {
                            nums = nums.replace(textView.getText().toString(), "");
                        } else if ((nums.substring(0, textView.getText().toString().length() - 1)).equals(textView.getText().toString())) {
                            nums = nums.replace(textView.getText().toString() + ",", "");
                        } else {
                            nums = nums.replace("," + textView.getText().toString(), "");
                        }
                    }
                }
            });
        }
    }

    private void getIdentity() {
        for (int i = 0; i < identity.length; i++) {
            final TextView textView = new TextView(CorpsFilterActivity.this);
            textView.setText(identity[i]);
            textView.setPadding(5, 5, 5, 5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(14);
            textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
            textView.setBackgroundResource(R.drawable.flowlayout_shape1);
            corpsfilter_identity.addView(textView);
            final int[] tag = {1};
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tag[0]++;
                    if (tag[0] % 2 == 0) {
                        textView.setTextColor(getResources().getColor(R.color.app_background2));
                        textView.setBackgroundResource(R.drawable.flowlayout_shape2);
                        if (identities == null || "".equals(identities)) {
                            identities = textView.getText().toString();
                        } else {
                            if (!identities.contains(textView.getText().toString())) {
                                identities = identities + "," + textView.getText().toString();
                            }
                        }
                    } else {
                        textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                        textView.setBackgroundResource(R.drawable.flowlayout_shape1);
                        if (identities.equals(textView.getText().toString())) {
                            identities = identities.replace(textView.getText().toString(), "");
                        } else if ((identities.substring(0, textView.getText().toString().length() - 1)).equals(textView.getText().toString())) {
                            identities = identities.replace(textView.getText().toString() + ",", "");
                        } else {
                            identities = identities.replace("," + textView.getText().toString(), "");
                        }
                    }
                }
            });
        }
    }

    private void getTeamSpeciality() {
        teamSpeciality.sendPostRequest(Urls.TeamSpeciality, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                final TextView textView = new TextView(CorpsFilterActivity.this);
                                textView.setText(object.getString("speciality_name"));
                                textView.setPadding(5, 5, 5, 5);
                                textView.setGravity(Gravity.CENTER);
                                textView.setTextSize(14);
                                textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                textView.setBackgroundResource(R.drawable.flowlayout_shape1);
                                corpsfilter_special.addView(textView);
                                final int[] tag = {1};
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tag[0]++;
                                        if (tag[0] % 2 == 0) {
                                            textView.setTextColor(getResources().getColor(R.color.app_background2));
                                            textView.setBackgroundResource(R.drawable.flowlayout_shape2);
                                            if (specialies == null || "".equals(specialies)) {
                                                specialies = textView.getText().toString();
                                            } else {
                                                if (!specialies.contains(textView.getText().toString())) {
                                                    specialies = specialies + "," + textView.getText().toString();
                                                }
                                            }
                                        } else {

                                            textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                            textView.setBackgroundResource(R.drawable.flowlayout_shape1);
                                            if (specialies.equals(textView.getText().toString())) {
                                                specialies = specialies.replace(textView.getText().toString(), "");
                                            } else if ((specialies.substring(0, textView.getText().toString().length() - 1)).equals(textView.getText().toString())) {
                                                specialies = specialies.replace(textView.getText().toString() + ",", "");
                                            } else {
                                                specialies = specialies.replace("," + textView.getText().toString(), "");
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        Tools.showToast(CorpsFilterActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpsFilterActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpsFilterActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.corpsfilter_filter) {
            Intent intent = new Intent();
            String keword = cropsfilteer_search.getText().toString().trim();
            if (identities.length() == 2 || TextUtils.isEmpty(identities)) {
                identities = "";
            } else {
                if (identities.equals("已认证")) {
                    identities = "1";
                } else if (identities.equals("未认证")) {
                    identities = "0";
                } else {
                    identities = "";
                }
            }
            if (nums.length() == 5 || TextUtils.isEmpty(nums)) {
                nums = "";
            } else {
                nums = nums.replace("1-10人", "1");
                nums = nums.replace("11-20人", "2");
                nums = nums.replace("21-30人", "3");
                nums = nums.replace("31-40人", "4");
                nums = nums.replace("41-50人", "5");
            }
            if (0 == districts.split(",").length || TextUtils.isEmpty(districts)) {
                districts = "";
            }
            intent.putExtra("keword", keword);
            intent.putExtra("team_speciality", specialies);
            intent.putExtra("team_state", identities);
            intent.putExtra("user_num", nums);
            intent.putExtra("pvince", districts);
            setResult(AppInfo.REQUEST_CODE_FILTER, intent);
            baseFinish();
        }
    }
}
