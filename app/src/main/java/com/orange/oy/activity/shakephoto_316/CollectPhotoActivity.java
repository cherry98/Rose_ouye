package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.ToWhomVisibleActivity;
import com.orange.oy.activity.shakephoto_318.PhotosourceActivity;
import com.orange.oy.activity.shakephoto_318.SetPrizeActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.ObtainMoreDialog;
import com.orange.oy.dialog.SponsorshipDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.FlowLayoutView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的-->随手发任务-->集图活动 V3.16
 */
public class CollectPhotoActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.collectphoto_title);
        appTitle.settingName("集图活动");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        createActivity = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CollectPhotoActivity.this));
                params.put("token", Tools.getToken());
                isEmpty(params, "cat_id", cat_id);
                isEmpty(params, "theme_name", theme_name);
                isEmpty(params, "activity_name", activity_name);
                isEmpty(params, "key_concent", key_concent);
                isEmpty(params, "descriptiones", descriptiones);
                isEmpty(params, "begin_date", begin_date);
                isEmpty(params, "end_date", end_date);
                isEmpty(params, "target_num", collect_photo.getText().toString().trim());
                isEmpty(params, "location_type", location_type);
                isEmpty(params, "cap_id", cap_id);
                isEmpty(params, "place_name", place_name);
                isEmpty(params, "city", city);
                isEmpty(params, "province", province);
                isEmpty(params, "county", country);
                isEmpty(params, "address", address);
                isEmpty(params, "dai_id", dai_id);
                isEmpty(params, "sponsorship", sponsorship);
                if (!"4".equals(which_page) && !TextUtils.isEmpty(ai_id)) {
                    isEmpty(params, "ai_id", ai_id);
                }
                isEmpty(params, "type", type);//0为存草稿 1为提交
                isEmpty(params, "longitude", longitude);
                isEmpty(params, "latitude", latitude);
                isEmpty(params, "ts_id", ts_id);
                isEmpty(params, "style_url", style_url);
                isEmpty(params, "prize_info", prize_info);
                isEmpty(params, "invisible_type", invisible_type);
                isEmpty(params, "invisible_label", invisible_label);
                isEmpty(params, "invisible_team", invisible_team);
                isEmpty(params, "invisible_mobile", invisible_mobile);
                isEmpty(params, "sponsor_name", sponsor_name);
                isEmpty(params, "ad_links", ad_links);
                isEmpty(params, "ad_url", ad_url);
                isEmpty(params, "cover_url", cover_url);
                isEmpty(params, "photo_source_type", photo_source_type);
                return params;
            }
        };
        selectActivityInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CollectPhotoActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                return params;
            }
        };
        bigCustomersSubmit = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CollectPhotoActivity.this));
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                params.put("phone_number", phone_number);
                params.put("company_name", company_name);
                params.put("position", position);
                params.put("name", name);
                params.put("sex", sex);
                return params;
            }
        };
        activityTemplateDetail = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CollectPhotoActivity.this));
                params.put("token", Tools.getToken());
                params.put("template_id", template_id);
                return params;
            }
        };
        Republish2 = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CollectPhotoActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (selectActivityInfo != null) {
            selectActivityInfo.stop(Urls.SelectActivityInfo);
        }
        if (createActivity != null) {
            createActivity.stop(Urls.CreateActivity);
        }
    }

    public void isEmpty(Map<String, String> params, String name, String str) {
        if (!Tools.isEmpty(str)) {
            params.put(name, str);
        }
    }

    public void onBackPressed() {
        type = "0";
        if (!fillEmpty()) {
            ConfirmDialog.showDialog(CollectPhotoActivity.this, "提示", 2, "需要存为草稿吗？", "不需要", "需要", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                    baseFinish();
                }

                public void rightClick(Object object) {
                    createActivity();
                    baseFinish();
                }
            });
        } else {
            baseFinish();
        }
    }

    private TextView collect_classify, collect_theme, collect_time, collect_location, collect_prize, collect_source;
    private FlowLayoutView collect_key;
    private EditText collect_edittext, collect_photo, collect_money, collect_sponsorname, collect_advlinks;
    private TextView collect_textnum, collect_addr_type, collect_addr;
    private NetworkConnection createActivity, selectActivityInfo, bigCustomersSubmit, activityTemplateDetail;
    private NetworkConnection Republish2;
    private String cat_id, theme_name, activity_name, key_concent, begin_date, end_date,
            location_type, cap_id, place_name, province, city, country, target_num, descriptiones, address, dai_id,
            ai_id, type, sponsorship;
    private String template_id;
    private String latitude, longitude;
    private String which_page;//0 随手发任务->创建 1 已投放->编辑 2 草稿箱->编辑 3 模板->创建 4->再次投放
    private String ts_id, style_url, prize_info, invisible_type, invisible_label, invisible_team, invisible_mobile, sponsor_name,
            ad_links, ad_url, cover_url, photo_source_type;//V3.18
    private ImageView collect_coverimg, collect_advimg;
    private String filePath_cover, filePath_adv;
    private TextView collect_premission;
    private String cover_path, ad_path;//文件名
    private ImageLoader imageLoader;
    private String project_id, phone_number, company_name, position, name, sex;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_photo);
        imageLoader = new ImageLoader(this);
        initTitle();
        initView();
        initNetwork();
        ai_id = getIntent().getStringExtra("ai_id");
        which_page = getIntent().getStringExtra("which_page");
        template_id = getIntent().getStringExtra("template_id");
        findViewById(R.id.collect_button).setOnClickListener(this);
        collect_edittext.addTextChangedListener(new MyTextWatcher(1));//活动描述
        collect_photo.addTextChangedListener(new MyTextWatcher(2));//照片数量
        collect_coverimg.setOnClickListener(this);
        collect_advimg.setOnClickListener(this);
        if (!"0".equals(which_page)) {
            if ("3".equals(which_page)) {
                getModel();
            } else {
                if ("4".equals(which_page)) {
                    findViewById(R.id.collect_theme_ly).setOnClickListener(this);
                    findViewById(R.id.collect_time_ly).setOnClickListener(this);
                    findViewById(R.id.collect_location_ly).setOnClickListener(this);
                    findViewById(R.id.collect_premission_ly).setOnClickListener(this);
                    findViewById(R.id.collect_source_ly).setOnClickListener(this);
                    findViewById(R.id.collect_prize_ly).setOnClickListener(this);
                    getRepublish();
                } else {
                    selectActivityInfo();
                }
            }
        } else {
//            getModel();
            findViewById(R.id.collect_theme_ly).setOnClickListener(this);
            findViewById(R.id.collect_time_ly).setOnClickListener(this);
            findViewById(R.id.collect_location_ly).setOnClickListener(this);
            findViewById(R.id.collect_premission_ly).setOnClickListener(this);
            findViewById(R.id.collect_source_ly).setOnClickListener(this);
            findViewById(R.id.collect_prize_ly).setOnClickListener(this);
        }
    }

    private void getRepublish() {
        Republish2.sendPostRequest(Urls.Republish2, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        cat_id = jsonObject.optString("cat_id");
                        theme_name = jsonObject.optString("theme_name");
                        activity_name = jsonObject.optString("activity_name");
                        key_concent = jsonObject.optString("key_concent");
                        descriptiones = jsonObject.optString("description");
                        target_num = jsonObject.optString("target_num");
                        location_type = jsonObject.optString("location_type");
                        cap_id = jsonObject.optString("cap_id");
                        place_name = jsonObject.optString("place_name");
                        province = jsonObject.optString("province");
                        city = jsonObject.optString("city");
                        country = jsonObject.optString("county");
                        address = jsonObject.optString("address");
                        dai_id = jsonObject.optString("dai_id");
                        sponsorship = jsonObject.optString("sponsorship");
                        latitude = jsonObject.optString("latitude");
                        longitude = jsonObject.optString("longitude");
                        //V3.18添加内容
                        prize_info = jsonObject.optString("prize_info");
                        invisible_type = jsonObject.optString("invisible_type");
                        invisible_mobile = jsonObject.optString("invisible_mobile");
//                        invisible_label = jsonObject.optString("invisible_label");
                        ad_links = jsonObject.optString("ad_links");
                        ad_url = jsonObject.optString("ad_url");
                        cover_url = jsonObject.optString("cover_url");
                        ts_id = jsonObject.optString("ts_id");
                        photo_source_type = jsonObject.optString("photo_source_type");
                        sponsor_name = jsonObject.optString("sponsor_name");
                        style_url = jsonObject.optString("style_url");

                        if (!Tools.isEmpty(prize_info)) {
                            collect_prize.setText("已设置");
                        }
                        if ("1".equals(invisible_type)) {
                            collect_premission.setText("全部可见");
                        } else {
                            collect_premission.setText("部分可见");
                        }
                        if (!Tools.isEmpty(sponsor_name)) {
                            collect_sponsorname.setText(sponsor_name);
                        }
                        if (!Tools.isEmpty(ad_links)) {
                            collect_advlinks.setText(ad_links);
                        }
                        if (!Tools.isEmpty(cover_url)) {
                            if (cover_url.startsWith("http://")) {
                                imageLoader.DisplayImage(cover_url, collect_coverimg);
                            } else {
                                imageLoader.DisplayImage(Urls.Endpoint3 + cover_url, collect_coverimg);
                            }
                        }
                        if (!Tools.isEmpty(ad_url)) {
                            if (ad_url.startsWith("http://")) {
                                imageLoader.DisplayImage(ad_url, collect_advimg);
                            } else {
                                imageLoader.DisplayImage(Urls.Endpoint3 + ad_url, collect_advimg);
                            }
                        }
                        if ("1".equals(photo_source_type)) {
                            collect_source.setText("直接拍摄");
                        } else if ("2".equals(photo_source_type)) {
                            collect_source.setText("从甩吧相册选择");
                        } else if ("3".equals(photo_source_type)) {
                            collect_source.setText("从本地相册选择");
                        }
                        //活动主题
                        if (!TextUtils.isEmpty(ts_id) && !"null".equals(ts_id)) {
                            findViewById(R.id.collect_classify_ly).setVisibility(View.VISIBLE);
                            collect_classify.setText(theme_name);
                            collect_theme.setText(activity_name);
                            findViewById(R.id.collect_theme_ly).setOnClickListener(CollectPhotoActivity.this);
                            findViewById(R.id.collect_them_state).setVisibility(View.GONE);
                            //关键内容
                            findViewById(R.id.collect_key_ly).setVisibility(View.VISIBLE);
                            findViewById(R.id.collect_key_ly).setOnClickListener(CollectPhotoActivity.this);
                            collect_key.setVisibility(View.VISIBLE);
                            if (!Tools.isEmpty(key_concent)) {
                                collect_key.setVisibility(View.VISIBLE);
                                String[] str = key_concent.split(",");
                                for (String aStr : str) {
                                    TextView textView = new TextView(CollectPhotoActivity.this);
                                    textView.setText(aStr);
                                    textView.setPadding(15, 5, 15, 5);
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setTextSize(12);
                                    textView.setTextColor(getResources().getColor(R.color.homepage_select));
                                    textView.setBackgroundResource(R.drawable.teamspecialty_label_bg);
                                    collect_key.addView(textView);
                                }
                            }
                        } else {
                            theme_name = "";
                            activity_name = "";
                            key_concent = "";
                        }
                        //描述
                        collect_edittext.setText(descriptiones);
                        collect_textnum.setText(descriptiones.length() + "/300");
                        collect_photo.setFocusable(true);
                        collect_photo.setEnabled(true);
                        collect_photo.setFocusableInTouchMode(true);
                        findViewById(R.id.collect_time_ly).setOnClickListener(CollectPhotoActivity.this);
                        if (!TextUtils.isEmpty(target_num) && !"null".equals(target_num)) {
                            collect_photo.setText(target_num);
                        }
                        findViewById(R.id.collect_location_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_premission_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_source_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_prize_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_coverimg).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_advimg).setOnClickListener(CollectPhotoActivity.this);
                        if ("1".equals(location_type)) {//精准投放
                            collect_location.setText("精确位置");
                            collect_addr_type.setText("位置地址");
                            collect_addr.setText(address);
                            findViewById(R.id.collect_addr_ly).setVisibility(View.VISIBLE);
                        } else if ("2".equals(location_type)) {
                            collect_location.setText("模糊位置");
                            collect_addr_type.setText("场景类型");
                            collect_addr.setText(place_name);
                            findViewById(R.id.collect_addr_ly).setVisibility(View.VISIBLE);
                        }
                        //赞助费
                        findViewById(R.id.collect_money_ly).setVisibility(View.VISIBLE);
                        if ("1".equals(which_page)) {
                            collect_time.setTextColor(0xFFA0A0A0);
                            collect_location.setTextColor(0xFFA0A0A0);
                            collect_addr.setTextColor(0xFFA0A0A0);
                            collect_money.setFocusable(false);
                            collect_money.setFocusableInTouchMode(false);
                        }
                        if (!TextUtils.isEmpty(sponsorship) && !"null".equals(sponsorship)) {
                            collect_money.setText(Tools.removePoint(sponsorship));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CollectPhotoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void getModel() {
        activityTemplateDetail.sendPostRequest(Urls.ActivityTemplateDetail, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        cat_id = jsonObject.optString("cat_id");
                        theme_name = jsonObject.optString("theme_name");
                        activity_name = jsonObject.optString("activity_name");
                        ts_id = jsonObject.getString("ts_id");
                        style_url = jsonObject.getString("style_url");
                        key_concent = jsonObject.optString("key_concent");
                        descriptiones = jsonObject.optString("description");
//                        begin_date = jsonObject.optString("begin_date");
//                        end_date = jsonObject.optString("end_date");
                        target_num = jsonObject.optString("target_num");
                        photo_source_type = jsonObject.getString("photo_source_type");
                        //活动主题
                        findViewById(R.id.collect_classify_ly).setVisibility(View.VISIBLE);
                        collect_classify.setText(theme_name);
                        collect_theme.setText(activity_name);
                        findViewById(R.id.collect_theme_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_them_state).setVisibility(View.GONE);
                        findViewById(R.id.collect_key_ly).setVisibility(View.VISIBLE);
                        findViewById(R.id.collect_key_ly).setOnClickListener(CollectPhotoActivity.this);
                        if (!Tools.isEmpty(key_concent)) {
                            collect_key.setVisibility(View.VISIBLE);
                            String[] str = key_concent.split(",");
                            for (String aStr : str) {
                                TextView textView = new TextView(CollectPhotoActivity.this);
                                textView.setText(aStr);
                                textView.setPadding(15, 5, 15, 5);
                                textView.setGravity(Gravity.CENTER);
                                textView.setTextSize(12);
                                textView.setTextColor(getResources().getColor(R.color.homepage_select));
                                textView.setBackgroundResource(R.drawable.teamspecialty_label_bg);
                                collect_key.addView(textView);
                            }
                        }
                        collect_edittext.setText(descriptiones);
                        collect_textnum.setText(descriptiones.length() + "/300");
//                        if (!TextUtils.isEmpty(begin_date) && !"null".equals(begin_date)) {
//                            collect_time.setText(begin_date + "~" + end_date);
//                        }
                        findViewById(R.id.collect_time_ly).setOnClickListener(CollectPhotoActivity.this);
                        if (!TextUtils.isEmpty(target_num) && !"null".equals(target_num)) {
                            collect_photo.setText(target_num);
                        }
                        if ("1".equals(photo_source_type)) {
                            collect_source.setText("直接拍摄");
                        } else if ("2".equals(photo_source_type)) {
                            collect_source.setText("从甩吧相册选择");
                        } else if ("3".equals(photo_source_type)) {
                            collect_source.setText("从本地相册选择");
                        }
                        if (!TextUtils.isEmpty(sponsorship) && !"null".equals(sponsorship)) {
                            collect_money.setText(Tools.removePoint(sponsorship));
                        }
                        if (!Tools.isEmpty(prize_info)) {
                            collect_prize.setText("已设置");
                        }
                        findViewById(R.id.collect_location_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_premission_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_source_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_prize_ly).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_coverimg).setOnClickListener(CollectPhotoActivity.this);
                        findViewById(R.id.collect_advimg).setOnClickListener(CollectPhotoActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CollectPhotoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void selectActivityInfo() {
        selectActivityInfo.sendPostRequest(Urls.SelectActivityInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        cat_id = jsonObject.optString("cat_id");
                        theme_name = jsonObject.optString("theme_name");
                        activity_name = jsonObject.optString("activity_name");
                        key_concent = jsonObject.optString("key_concent");
                        descriptiones = jsonObject.optString("description");
                        if (!"4".equals(which_page)) {
                            begin_date = jsonObject.optString("begin_date");
                            end_date = jsonObject.optString("end_date");
                        }
                        target_num = jsonObject.optString("target_num");
                        location_type = jsonObject.optString("location_type");
                        cap_id = jsonObject.optString("cap_id");
                        place_name = jsonObject.optString("place_name");
                        province = jsonObject.optString("province");
                        city = jsonObject.optString("city");
                        country = jsonObject.optString("county");
                        address = jsonObject.optString("address");
                        dai_id = jsonObject.optString("dai_id");
                        sponsorship = jsonObject.optString("sponsorship");
                        ai_id = jsonObject.optString("ai_id");
                        latitude = jsonObject.optString("latitude");
                        longitude = jsonObject.optString("longitude");
                        //V3.18添加内容
                        prize_info = jsonObject.getString("prize_info");
                        invisible_type = jsonObject.getString("invisible_type");
                        invisible_mobile = jsonObject.getString("invisible_mobile");
//                        invisible_label = jsonObject.getString("invisible_label");
                        ad_links = jsonObject.getString("ad_links");
                        ad_url = jsonObject.getString("ad_url");
                        cover_url = jsonObject.getString("cover_url");
                        ts_id = jsonObject.getString("ts_id");
                        photo_source_type = jsonObject.getString("photo_source_type");
                        sponsor_name = jsonObject.getString("sponsor_name");
                        style_url = jsonObject.getString("style_url");

                        if (!Tools.isEmpty(prize_info)) {
                            collect_prize.setText("已设置");
                        }
                        if ("1".equals(invisible_type)) {
                            collect_premission.setText("全部可见");
                        } else {
                            collect_premission.setText("部分可见");
                        }
                        if (!Tools.isEmpty(sponsor_name)) {
                            collect_sponsorname.setText(sponsor_name);
                        }
                        if (!Tools.isEmpty(ad_links)) {
                            collect_advlinks.setText(ad_links);
                        }
                        if (!Tools.isEmpty(cover_url)) {
                            if (cover_url.startsWith("http://")) {
                                imageLoader.DisplayImage(cover_url, collect_coverimg);
                            } else {
                                imageLoader.DisplayImage(Urls.Endpoint3 + cover_url, collect_coverimg);
                            }
                        }
                        if (!Tools.isEmpty(ad_url)) {
                            if (ad_url.startsWith("http://")) {
                                imageLoader.DisplayImage(ad_url, collect_advimg);
                            } else {
                                imageLoader.DisplayImage(Urls.Endpoint3 + ad_url, collect_advimg);
                            }
                        }
                        if ("1".equals(photo_source_type)) {
                            collect_source.setText("直接拍摄");
                        } else if ("2".equals(photo_source_type)) {
                            collect_source.setText("从甩吧相册选择");
                        } else if ("3".equals(photo_source_type)) {
                            collect_source.setText("从本地相册选择");
                        }
                        //活动主题
                        findViewById(R.id.collect_classify_ly).setVisibility(View.VISIBLE);
                        collect_classify.setText(theme_name);
                        collect_theme.setText(activity_name);
                        if ("2".equals(which_page)) {//草稿箱可修改
                            findViewById(R.id.collect_theme_ly).setOnClickListener(CollectPhotoActivity.this);
                            findViewById(R.id.collect_them_state).setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.collect_theme_ly).setOnClickListener(null);
                            findViewById(R.id.collect_them_state).setVisibility(View.VISIBLE);
                        }
                        //关键内容
                        findViewById(R.id.collect_key_ly).setVisibility(View.VISIBLE);
                        findViewById(R.id.collect_key_ly).setOnClickListener(CollectPhotoActivity.this);
                        collect_key.setVisibility(View.VISIBLE);
                        if (!Tools.isEmpty(key_concent)) {
                            collect_key.setVisibility(View.VISIBLE);
                            String[] str = key_concent.split(",");
                            for (String aStr : str) {
                                TextView textView = new TextView(CollectPhotoActivity.this);
                                textView.setText(aStr);
                                textView.setPadding(15, 5, 15, 5);
                                textView.setGravity(Gravity.CENTER);
                                textView.setTextSize(12);
                                textView.setTextColor(getResources().getColor(R.color.homepage_select));
                                textView.setBackgroundResource(R.drawable.teamspecialty_label_bg);
                                collect_key.addView(textView);
                            }
                        }
                        //描述
                        collect_edittext.setText(descriptiones);
                        collect_textnum.setText(descriptiones.length() + "/300");
                        //目标人数 起止日期
                        if ("1".equals(which_page)) {//已投放不可编辑
                            collect_photo.setFocusable(false);
                            collect_photo.setEnabled(false);
                        } else {
                            collect_photo.setFocusable(true);
                            collect_photo.setEnabled(true);
                            collect_photo.setFocusableInTouchMode(true);
                        }
                        findViewById(R.id.collect_time_ly).setOnClickListener(CollectPhotoActivity.this);
                        if (!TextUtils.isEmpty(target_num) && !"null".equals(target_num)) {
                            collect_photo.setText(target_num);
                        }
                        if (!"4".equals(which_page) && !Tools.isEmpty(begin_date) && !Tools.isEmpty(end_date)) {
                            collect_time.setText(begin_date + "~" + end_date);
                        }
                        //投放类型
                        String have_photo = jsonObject.getString("have_photo");
                        if ("1".equals(have_photo) && "1".equals(which_page)) {//有图片不可编辑
                            findViewById(R.id.collect_location_ly).setOnClickListener(null);
                            findViewById(R.id.collect_premission_ly).setOnClickListener(null);
                            findViewById(R.id.collect_source_ly).setOnClickListener(null);
                            findViewById(R.id.collect_prize_ly).setOnClickListener(null);
                        } else {
                            findViewById(R.id.collect_location_ly).setOnClickListener(CollectPhotoActivity.this);
                            findViewById(R.id.collect_premission_ly).setOnClickListener(CollectPhotoActivity.this);
                            findViewById(R.id.collect_source_ly).setOnClickListener(CollectPhotoActivity.this);
                            findViewById(R.id.collect_prize_ly).setOnClickListener(CollectPhotoActivity.this);
                            findViewById(R.id.collect_coverimg).setOnClickListener(CollectPhotoActivity.this);
                            findViewById(R.id.collect_advimg).setOnClickListener(CollectPhotoActivity.this);
                        }
                        if ("1".equals(location_type)) {//精准投放
                            collect_location.setText("精确位置");
                            collect_addr_type.setText("位置地址");
                            collect_addr.setText(address);
                            findViewById(R.id.collect_addr_ly).setVisibility(View.VISIBLE);
                        } else if ("2".equals(location_type)) {
                            collect_location.setText("模糊位置");
                            collect_addr_type.setText("场景类型");
                            collect_addr.setText(place_name);
                            findViewById(R.id.collect_addr_ly).setVisibility(View.VISIBLE);
                        }
                        //赞助费
                        findViewById(R.id.collect_money_ly).setVisibility(View.VISIBLE);
                        if ("1".equals(which_page)) {
                            collect_time.setTextColor(0xFFA0A0A0);
                            collect_location.setTextColor(0xFFA0A0A0);
                            collect_addr.setTextColor(0xFFA0A0A0);
                            collect_money.setFocusable(false);
                            collect_money.setFocusableInTouchMode(false);
                        }
                        if (!TextUtils.isEmpty(sponsorship) && !"null".equals(sponsorship)) {
                            collect_money.setText(Tools.removePoint(sponsorship));
                        }
                    } else {
                        Tools.showToast(CollectPhotoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CollectPhotoActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CollectPhotoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void initView() {
        collect_classify = (TextView) findViewById(R.id.collect_classify);
        collect_theme = (TextView) findViewById(R.id.collect_theme);
        collect_time = (TextView) findViewById(R.id.collect_time);
        collect_location = (TextView) findViewById(R.id.collect_location);
        collect_textnum = (TextView) findViewById(R.id.collect_textnum);
        collect_addr_type = (TextView) findViewById(R.id.collect_addr_type);
        collect_addr = (TextView) findViewById(R.id.collect_addr);
        collect_key = (FlowLayoutView) findViewById(R.id.collect_key);
        collect_edittext = (EditText) findViewById(R.id.collect_edittext);
        collect_photo = (EditText) findViewById(R.id.collect_photo);
        collect_money = (EditText) findViewById(R.id.collect_money);
        collect_sponsorname = (EditText) findViewById(R.id.collect_sponsorname);
        collect_advlinks = (EditText) findViewById(R.id.collect_advlinks);
        collect_coverimg = (ImageView) findViewById(R.id.collect_coverimg);
        collect_advimg = (ImageView) findViewById(R.id.collect_advimg);
        collect_premission = (TextView) findViewById(R.id.collect_premission);
        collect_prize = (TextView) findViewById(R.id.collect_prize);
        collect_source = (TextView) findViewById(R.id.collect_source);
    }

    private void createActivity() {
        createActivity.sendPostRequest(Urls.CreateActivity, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if ("1".equals(type)) {//查询余额
                            jsonObject = jsonObject.optJSONObject("data");
                            ai_id = jsonObject.getString("ai_id");
                            if (!"1".equals(which_page)) {//编辑页面无需跳赞助费
                                if (Tools.StringToInt(collect_photo.getText().toString().trim()) <= 1000) {//目标人数超过1000人
                                    Intent intent = new Intent(CollectPhotoActivity.this, GrantInAidActivity.class);
                                    intent.putExtra("account_money", jsonObject.getString("account_money"));
                                    intent.putExtra("ai_id", ai_id);
                                    intent.putExtra("sponsorship", sponsorship);
                                    intent.putExtra("target_num", target_num);
                                    intent.putExtra("Isnormal", ("2".equals(which_page)) ? "1" : "0");//创建
                                    startActivity(intent);
                                } else {
                                    ObtainMoreDialog.showDialog(CollectPhotoActivity.this, new ObtainMoreDialog.OnObtainMoreListener() {
                                        @Override
                                        public void onSubmit(String phone_number, String company_name, String position, String name, String sex) {
                                            CollectPhotoActivity.this.phone_number = phone_number;
                                            CollectPhotoActivity.this.company_name = company_name;
                                            CollectPhotoActivity.this.position = position;
                                            CollectPhotoActivity.this.name = name;
                                            CollectPhotoActivity.this.sex = sex;
                                            project_id = ai_id;
                                            bigCustomersSubmit();
                                        }

                                        @Override
                                        public void cancel() {

                                        }
                                    });
                                }
                            } else {
                                baseFinish();
                            }
                        }
                    } else {
                        Tools.showToast(CollectPhotoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CollectPhotoActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CollectPhotoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void bigCustomersSubmit() {
        bigCustomersSubmit.sendPostRequest(Urls.BigCustomersSubmit, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        ConfirmDialog.showDialog(CollectPhotoActivity.this, "提示", 1, "您的资料客服已收到，我们会尽快联系您！", "", "我知道了",
                                null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                    public void leftClick(Object object) {
                                    }

                                    public void rightClick(Object object) {
                                        baseFinish();
                                    }
                                }).goneLeft();
                    } else {
                        Tools.showToast(CollectPhotoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CollectPhotoActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CollectPhotoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        type = "0";
        if (!fillEmpty()) {
            ConfirmDialog.showDialog(CollectPhotoActivity.this, "提示", 2, "需要存为草稿吗？", "不需要", "需要", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                    baseFinish();
                }

                public void rightClick(Object object) {
                    createActivity();
                    baseFinish();
                }
            });
        } else {
            baseFinish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect_theme_ly: {//活动主题
                Intent intent = new Intent(this, CollectThemeActivity.class);
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.collect_time_ly: {//时间选择
                Intent intent = new Intent(this, TimeSelectActivity.class);
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.collect_location_ly: {//地址选择
                Intent intent = new Intent(this, SelectLocationActivity.class);
                intent.putExtra("location_type", location_type);
                if ("2".equals(location_type)) {
                    intent.putExtra("place_name", place_name);
                    intent.putExtra("cap_id", cap_id);
                } else if ("1".equals(location_type)) {
                    intent.putExtra("address", address);
                    intent.putExtra("dai_id", dai_id);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                }
                intent.putExtra("city", city);
                intent.putExtra("county", country);
                intent.putExtra("province", province);
                startActivityForResult(intent, 2);
            }
            break;
            case R.id.collect_key_ly: {//关键内容修改
                Intent intent = new Intent(this, EditContentActivity.class);
                intent.putExtra("key_concent", key_concent);
                startActivityForResult(intent, 3);
            }
            break;
            case R.id.collect_coverimg: {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 4);
            }
            break;
            case R.id.collect_advimg: {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 5);
            }
            break;
            case R.id.collect_premission_ly: {//可见权限选择
                Intent intent = new Intent(this, ToWhomVisibleActivity.class);
                intent.putExtra("isFrist", "1");
                intent.putExtra("ischart", "1");
                startActivityForResult(intent, 8);
            }
            break;
            case R.id.collect_prize_ly: {//设置大奖（code值9）
                Intent intent = new Intent(this, SetPrizeActivity.class);
                intent.putExtra("prize_info", prize_info);
                startActivityForResult(intent, 9);
            }
            break;
            case R.id.collect_source_ly: {//照片来源（10）
                Intent intent = new Intent(this, PhotosourceActivity.class);
                startActivityForResult(intent, 10);
            }
            break;
            case R.id.collect_button: {//信息提交
                if (TextUtils.isEmpty(collect_theme.getText().toString().trim())) {
                    Tools.showToast(this, "请选择主题分类");
                    return;
                }
                descriptiones = Tools.filterEmoji(collect_edittext.getText().toString().trim());
                if (TextUtils.isEmpty(descriptiones)) {
                    Tools.showToast(this, "请输入活动描述");
                    return;
                }
                if (TextUtils.isEmpty(collect_time.getText().toString().trim())) {
                    Tools.showToast(this, "请选择起始日期");
                    return;
                }
                target_num = collect_photo.getText().toString().trim();
                if (TextUtils.isEmpty(target_num)) {
                    Tools.showToast(this, "请输入目标人数");
                    return;
                }
                if (Tools.StringToInt(target_num) <= 0) {
                    Tools.showToast(this, "请输入大于0的人数");
                    return;
                }
                if (TextUtils.isEmpty(collect_location.getText().toString().trim())) {
                    Tools.showToast(this, "请选择投放位置");
                    return;
                }
//                if (TextUtils.isEmpty(collect_prize.getText().toString().trim())) {
//                    Tools.showToast(this, "请设置活动大奖");
//                    return;
//                }
                if (TextUtils.isEmpty(collect_premission.getText().toString().trim())) {
                    Tools.showToast(this, "请设置对谁可见");
                    return;
                }
                sponsor_name = collect_sponsorname.getText().toString().trim();
                if (TextUtils.isEmpty(sponsor_name)) {
                    Tools.showToast(this, "请输入赞助商名称");
                    return;
                }
                ad_links = collect_advlinks.getText().toString().trim();
                sponsorship = collect_money.getText().toString().trim();
                if (TextUtils.isEmpty(sponsorship)) {
                    Tools.showToast(this, "请输入赞助金额");
                    return;
                }
                if (Tools.StringToDouble(sponsorship) < Tools.StringToDouble(target_num)) {
                    Tools.showToast(this, "请输入不能小于" + target_num + "的金额");
                    collect_money.setHint("请输入不能小于" + target_num + "的金额");
                    return;
                }
                if (Tools.isEmpty(cover_url)) {
                    Tools.showToast(this, "请上传封面图");
                    return;
                }
                if (Tools.isEmpty(ad_url)) {
                    Tools.showToast(this, "请上传广告图");
                    return;
                }
                if (Tools.isEmpty(collect_source.getText().toString().trim())) {
                    Tools.showToast(this, "请设置照片来源");
                    return;
                }
                type = "1";
                double scale = Tools.StringToDouble(collect_money.getText().toString().trim()) / Tools.StringToDouble(target_num);
                if (scale < 1) {//弹窗
                    SponsorshipDialog.showDialog(CollectPhotoActivity.this, collect_money.getText().toString().trim(), target_num, new SponsorshipDialog.OnSelectClickListener() {
                        @Override
                        public void onConfirm(String sum) {
                            sponsorship = sum;
                            collect_money.setText(sum);
                            createActivity();
                        }
                    });
                } else {
                    createActivity();
                }
            }
            break;
        }
    }

    public boolean fillEmpty() {
        return TextUtils.isEmpty(collect_theme.getText().toString().trim()) &&
                TextUtils.isEmpty(collect_time.getText().toString().trim()) &&
                TextUtils.isEmpty(collect_photo.getText().toString().trim()) &&
                TextUtils.isEmpty(collect_location.getText().toString().trim()) &&
                TextUtils.isEmpty(collect_photo.getText().toString().trim()) &&
                TextUtils.isEmpty(collect_money.getText().toString().trim()) &&
                Tools.isEmpty(collect_prize.getText().toString().trim()) &&
                Tools.isEmpty(collect_premission.getText().toString().trim()) &&
                Tools.isEmpty(ad_url) && Tools.isEmpty(cover_url) &&
                Tools.isEmpty(collect_source.getText().toString().trim());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_COLLECT) {
            switch (requestCode) {
                case 0: {//主题分类
                    if (data != null) {
                        findViewById(R.id.collect_classify_ly).setVisibility(View.VISIBLE);
                        cat_id = data.getStringExtra("cat_id");
                        theme_name = data.getStringExtra("theme_name");
                        collect_classify.setText(theme_name);
                        activity_name = data.getStringExtra("activity_name");
                        collect_theme.setText(activity_name);
                        key_concent = data.getStringExtra("key_concent");
                        findViewById(R.id.collect_key_ly).setVisibility(View.VISIBLE);
                        collect_key.setVisibility(View.VISIBLE);
                        collect_key.removeAllViews();
                        String[] str = key_concent.split(",");
                        for (String aStr : str) {
                            TextView textView = new TextView(CollectPhotoActivity.this);
                            textView.setText(aStr);
                            textView.setPadding(15, 5, 15, 5);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextSize(12);
                            textView.setTextColor(getResources().getColor(R.color.homepage_select));
                            textView.setBackgroundResource(R.drawable.teamspecialty_label_bg);
                            collect_key.addView(textView);
                        }
                        ts_id = data.getStringExtra("ts_id");
                        style_url = data.getStringExtra("style_url");
                    }
                }
                break;
                case 1: {//时间选择
                    if (data != null) {
                        begin_date = data.getStringExtra("begin_time");
                        end_date = data.getStringExtra("end_time");
                        collect_time.setText(begin_date + "~" + end_date);
                    }
                }
                break;
                case 2: {//位置选择
                    if (data != null) {
                        findViewById(R.id.collect_addr_ly).setVisibility(View.VISIBLE);
                        location_type = data.getStringExtra("location_type");
                        if ("1".equals(location_type)) {//准确位置
                            collect_addr_type.setText("位置地址");
                            collect_location.setText("精确地址");
                            latitude = data.getStringExtra("latitude");
                            longitude = data.getStringExtra("longitude");
                            dai_id = data.getStringExtra("dai_id");
                            city = data.getStringExtra("city");
                            country = data.getStringExtra("county");
                            province = data.getStringExtra("province");
                            address = data.getStringExtra("address");
                            collect_addr.setText(address);
                        } else {
                            collect_location.setText("模糊地址");
                            collect_addr_type.setText("场景类型");
                            place_name = data.getStringExtra("place_name");
                            cap_id = data.getStringExtra("cap_id");
                            collect_addr.setText(place_name);
                            city = data.getStringExtra("city");
                            country = data.getStringExtra("county");
                            province = data.getStringExtra("province");
                        }
                    }
                }
                break;
                case 3: {//编辑时修改的关键内容
                    if (data != null) {
                        collect_key.removeAllViews();
                        key_concent = data.getStringExtra("key_concent2");
                        String[] str = key_concent.split(",");
                        for (String aStr : str) {
                            TextView textView = new TextView(CollectPhotoActivity.this);
                            textView.setText(aStr);
                            textView.setPadding(15, 5, 15, 5);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextSize(12);
                            textView.setTextColor(getResources().getColor(R.color.homepage_select));
                            textView.setBackgroundResource(R.drawable.teamspecialty_label_bg);
                            collect_key.setVisibility(View.VISIBLE);
                            collect_key.addView(textView);
                        }
                    }
                }
                break;
                case 8: {//可见权限
                    if (data != null) {
                        invisible_type = data.getStringExtra("invisible_type");
                        invisible_mobile = data.getStringExtra("usermobile_list");
                        invisible_label = data.getStringExtra("invisible_label");
                        invisible_team = data.getStringExtra("invisible_team");
                        if ("2".equals(invisible_type)) {
                            collect_premission.setText("部分可见");
                        } else if ("1".equals(invisible_type)) {
                            collect_premission.setText("全部可见");
                        } else {
                            collect_premission.setText("部分可见");
                        }
                    }
                }
                break;
                case 9: {//设置大奖
                    if (data != null) {
                        prize_info = data.getStringExtra("prize_info");
                        collect_prize.setText("已设置");
                    }
                }
                break;
                case 10: {//照片来源
                    if (data != null) {
                        photo_source_type = data.getStringExtra("photo_source_type");
                        if ("1".equals(photo_source_type)) {
                            collect_source.setText("直接拍摄");
                        } else if ("2".equals(photo_source_type)) {
                            collect_source.setText("从甩吧相册选择");
                        } else if ("3".equals(photo_source_type)) {
                            collect_source.setText("从本地相册选择");
                        }
                    }
                }
                break;
            }
        } else if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 4: {//封面图
                    if (data != null) {
                        Uri uri = data.getData();
                        cover_path = "/coverImg.jpg";
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(uri, "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 18);
                        intent.putExtra("aspectY", 13);
                        intent.putExtra("outputX", 540);
                        intent.putExtra("outputY", 390);
                        intent.putExtra("scale", true);//黑边
                        intent.putExtra("scaleUpIfNeeded", true);//黑边
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + cover_path)));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, 6);
                    }
                }
                break;
                case 5: {//广告图
                    if (data != null) {
                        Uri uri = data.getData();
                        ad_path = "/adImg.jpg";
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(uri, "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 2);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 1000);
                        intent.putExtra("outputY", 500);
                        intent.putExtra("scale", true);//黑边
                        intent.putExtra("scaleUpIfNeeded", true);//黑边
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + ad_path)));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, 7);
                    }
                }
                break;
                case 6: {//封面图
                    filePath_cover = FileCache.getDirForPhoto(this).getPath() + cover_path;
                    collect_coverimg.setImageBitmap(Tools.getBitmap(filePath_cover, 200, 200));
                    sendOSSData("1", filePath_cover);
                }
                break;
                case 7: {//广告图
                    filePath_adv = FileCache.getDirForPhoto(this).getPath() + ad_path;
                    collect_advimg.setImageBitmap(Tools.getBitmap(filePath_adv, 200, 200));
                    sendOSSData("2", filePath_adv);
                }
                break;
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                sendOSSData("1", filePath_cover);
            } else if (msg.what == 2) {
                sendOSSData("2", filePath_adv);
            }
            Tools.d("handler");
        }
    };

    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;

    public void sendOSSData(final String type, String s) {//1 封面图 2广告图
        try {
            CustomProgressDialog.showProgressDialog(this, "正在上传");
            Tools.d(s);
            File file = new File(s);
            if (!file.exists() || !file.isFile()) {
                return;
            }
            String objectKey = file.getName();
            objectKey = Urls.EndpointDir + "/" + Tools.getTimeSS() + objectKey;
            if (credentialProvider == null)
                credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                        "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
            if (oss == null)
                oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, s);
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                Log.i("-------", "图片上传中-----");
                    Tools.d("currentSize: " + currentSize + " totalSize: " + totalSize);
                }
            });
            final String finalObjectKey = objectKey;
            task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    CustomProgressDialog.Dissmiss();
                    if ("1".equals(type)) {//封面图
                        cover_url = finalObjectKey;
//                        imageLoader.setShowWH(300).DisplayImage(cover_url, collect_coverimg, -2);
//                        new File(filePath_cover).delete();
                        Tools.d("封面图片地址：" + cover_url);
                        if (Tools.isEmpty(ad_url) && !Tools.isEmpty(filePath_adv)) {
                            handler.sendEmptyMessage(1);
                        }
                    } else {
                        ad_url = finalObjectKey;
//                        imageLoader.setShowWH(300).DisplayImage(ad_url, collect_advimg, -2);
//                        new File(filePath_adv).delete();
                        Tools.d("广告图片地址：" + ad_url);
                        if (Tools.isEmpty(cover_url) && !Tools.isEmpty(filePath_cover)) {
                            handler.sendEmptyMessage(2);
                        }
                    }
                    Tools.d("上传成功");
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    CustomProgressDialog.Dissmiss();
                    ConfirmDialog.showDialog(CollectPhotoActivity.this, null, 3, "网络异常，图片上传失败", "", "知道了", null, true, null)
                            .goneLeft();
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                    }
                    if (serviceException != null) {
                        // 服务异常
                        serviceException.printStackTrace();
                        Tools.d("-------图片上传失败");
                    }
                    task.cancel();
//                task.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.Dissmiss();
        }
    }

    public class MyTextWatcher implements TextWatcher {
        private int type;

        MyTextWatcher(int type) {
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (type == 1) {
                collect_textnum.setText(collect_edittext.getText().toString().trim().length() + "/300");
            } else if (type == 2) {
                int price = Tools.StringToInt(s.toString());
                if (price > 0) {
                    findViewById(R.id.collect_money_ly).setVisibility(View.VISIBLE);
                    collect_money.setHint("请输入不能小于" + price + "的金额");
                } else {
                    findViewById(R.id.collect_money_ly).setVisibility(View.GONE);
                }
            }
        }
    }
}
