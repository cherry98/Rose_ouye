package com.orange.oy.activity.experience;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.orange.oy.R;
import com.orange.oy.activity.SystemAlbumActivity;
import com.orange.oy.adapter.AlbumGridViewAdapter;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.FlowLayoutView;
import com.orange.oy.view.RatingView;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 体验项目--体验评论页面~~~
 */
public class ExperienceCommentActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        RatingView.OnRatingViewClickListener, View.OnClickListener {

    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.epcomment_title);
        appTitle.settingName("用户评论");
        if ("1".equals(source)) {
            appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
                @Override
                public void onBack() {
                    baseFinish();
                }
            });
        } else {
            appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
                @Override
                public void onBack() {
                    ConfirmDialog.showDialog(ExperienceCommentActivity.this, null, "您是否未进行分享？如未进行分享，可能导致您已填写的内容丢失，请您谨慎核实操作", "否", "是", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                            baseFinish();
                        }

                        @Override
                        public void rightClick(Object object) {

                        }
                    });
                }
            }, "首页");
        }
        appTitle.settingExit(selectedDataList.size() + "/9");
    }

    @Override
    public void onBackPressed() {
        if ("1".equals(source)) {
            super.onBackPressed();
        } else {
            ConfirmDialog.showDialog(ExperienceCommentActivity.this, null, "您是否未进行分享？如未进行分享，可能导致您已填写的内容丢失，请您谨慎核实操作", "否", "是", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {
                    baseFinish();
                }

                @Override
                public void rightClick(Object object) {

                }
            });
        }
    }

    private void initNetworkConnection() {
        experienceComment = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ExperienceCommentActivity.this));
                params.put("storeid", storeid);
                return params;
            }
        };
        experienceCommentUp = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", storeid);
                params.put("usermobile", AppInfo.getName(ExperienceCommentActivity.this));
                params.put("score", score);
                params.put("multiselect", multiselect);
                params.put("comment", comment);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                return params;
            }
        };
        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", key);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (experienceComment != null) {
            experienceComment.stop(Urls.ExperienceComment);
        }
        if (experienceCommentUp != null) {
            experienceCommentUp.stop(Urls.ExperienceCommentUp);
        }
        if (Sign != null) {
            Sign.stop(Urls.Sign);
        }
    }

    private double longitude, latitude;
    private LocationClient locationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private SystemDBHelper systemDBHelper;
    private ArrayList<String> dataList = new ArrayList<>();
    private ArrayList<String> selectedDataList = new ArrayList<>();
    private NetworkConnection experienceComment, experienceCommentUp, Sign;
    private String storeid, taskid, projectid, packageid, projectname, photo_compression, storecode, brand, storeName;
    private ImageLoader imageLoader;
    private LinearLayout epconmment_rating;
    private AlbumGridViewAdapter gridViewAdapter;
    private GridView epconmment_gridview;
    private EditText epconmment_edittext;
    private TextView epconmment_count;
    private String score, multiselect, comment, key, sign;//生成签名信息 签名
    private HashMap<String, String> hashMap = new HashMap<>();
    private UpdataDBHelper updataDBHelper;
    private String source;//0 执行任务 1查看详情

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_comment);
        imageLoader = new ImageLoader(this);
        updataDBHelper = new UpdataDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        storeid = getIntent().getStringExtra("storeid");
        taskid = getIntent().getStringExtra("taskid");
        source = getIntent().getStringExtra("source");
        projectid = getIntent().getStringExtra("projectid");
        packageid = getIntent().getStringExtra("packageid");
        photo_compression = getIntent().getStringExtra("photo_compression");
        projectname = getIntent().getStringExtra("project_name");
        storecode = getIntent().getStringExtra("storecode");
        storeName = getIntent().getStringExtra("storeName");
        brand = getIntent().getStringExtra("brand");
        initTitle();
        key = "storeid=" + storeid + "&usermobile=" + AppInfo.getName(this);
        initNetworkConnection();
        epconmment_rating = (LinearLayout) findViewById(R.id.epconmment_rating);
        epconmment_gridview = (GridView) findViewById(R.id.epconmment_gridview);
        epconmment_edittext = (EditText) findViewById(R.id.epconmment_edittext);
        epconmment_count = (TextView) findViewById(R.id.epconmment_count);
        findViewById(R.id.epconmment_button).setOnClickListener(this);
        getData();
        Sign();
        epconmment_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = epconmment_edittext.getText().toString();
                epconmment_count.setText(content.length() + "/" + 500);

            }
        });
        checkPermission();
        initLocation();
    }

    private void Sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                        configPlatforms();
                    } else {
                        Tools.showToast(ExperienceCommentActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(ExperienceCommentActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperienceCommentActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void onItemClickListener() {
        gridViewAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ToggleButton view, int position, String path, boolean isChecked) {
                if (selectedDataList.size() >= 9) {
                    view.setChecked(false);
                    Tools.showToast(ExperienceCommentActivity.this, "您只能选择9张图片");
                    removePath(path);
                    return;
                }
                if (position == 0) {
                    Intent intent = new Intent(ExperienceCommentActivity.this, SystemAlbumActivity.class);
                    intent.putExtra("num", 9 - selectedDataList.size());
                    startActivityForResult(intent, 0);
                } else {
                    if (isChecked) {
                        selectedDataList.add(path);
                        appTitle.settingExit(selectedDataList.size() + "/9");
                    } else {
                        removePath(path);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//返回相册返回的图片路径
            if (requestCode == 0) {
                Bundle bundle = data.getExtras();
                ArrayList<String> mDataList = (ArrayList<String>) bundle.getSerializable("dataList");
                selectedDataList.addAll(mDataList);
                dataList.addAll(mDataList);
                gridViewAdapter.notifyDataSetChanged();
                appTitle.settingExit(selectedDataList.size() + "/9");
            }
        }
    }

    private void removePath(String path) {
        removeOneData(selectedDataList, path);
        appTitle.settingExit(selectedDataList.size() + "/9");
    }

    private void removeOneData(ArrayList<String> arrayList, String s) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equals(s)) {
                arrayList.remove(i);
                return;
            }
        }
    }

    private RatingView ratingView;
    private FlowLayoutView flowLayoutView;

    private void getData() {
        experienceComment.sendPostRequest(Urls.ExperienceComment, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        ImageView epconmment_img = (ImageView) findViewById(R.id.epconmment_img);
                        imageLoader.DisplayImage(Urls.ImgIp + jsonObject.getString("photoUrl"), epconmment_img);
                        ((TextView) findViewById(R.id.epconmment_name)).setText(jsonObject.getString("storeName"));
                        ((TextView) findViewById(R.id.epconmment_addr)).setText(jsonObject.getString("storeAddress"));
                        JSONArray jsonArray1 = jsonObject.optJSONArray("selectType");
                        if ("1".equals(source)) {//是否评论过
                            appTitle.hideExit();
                            findViewById(R.id.epconmment_button).setVisibility(View.GONE);
                            findViewById(R.id.epconmment_layout).setVisibility(View.GONE);
                            String temp = jsonObject.optString("comment");
                            if (!TextUtils.isEmpty(temp) && !"null".equals(temp)) {
                                JSONObject jsonObject1 = new JSONObject(temp);
                                String score = jsonObject1.getString("score");
                                if (!TextUtils.isEmpty(score) && score != null) {
                                    String[] scores = score.split(",");
                                    for (int i = 0; i < scores.length; i++) {
                                        int index = scores[i].indexOf(":");
                                        RatingView ratingView = new RatingView(ExperienceCommentActivity.this);
                                        ratingView.setData2(scores[i].substring(0, index), scores[i].substring(index + 1, scores[i].length()));
                                        epconmment_rating.addView(ratingView);
                                    }
                                }
                                epconmment_edittext.setFocusable(false);
                                epconmment_edittext.setClickable(false);
                                epconmment_edittext.setText(jsonObject1.getString("content"));
                                if (jsonArray1 != null) {
                                    JSONArray multiselects = jsonObject1.getJSONArray("multiselect");
                                    flowLayoutView = (FlowLayoutView) findViewById(R.id.flowlayout);
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        TextView textView = new TextView(ExperienceCommentActivity.this);
                                        textView.setText(jsonArray1.getString(i));
                                        textView.setPadding(5, 5, 5, 5);
                                        textView.setGravity(Gravity.CENTER);
                                        textView.setTextSize(14);
                                        flowLayoutView.addView(textView);
                                        for (int j = 0; j < multiselects.length(); j++) {
                                            if (jsonArray1.getString(i).equals(multiselects.getString(j))) {
                                                textView.setTextColor(getResources().getColor(R.color.experience_notselect));
                                                textView.setBackgroundResource(R.drawable.shape_item);
                                            } else {
                                                textView.setTextColor(getResources().getColor(R.color.app_background2));
                                                textView.setBackgroundResource(R.drawable.shape_item2);
                                            }
                                        }
                                    }
                                }
                                JSONArray photolist = jsonObject1.getJSONArray("photolist");
                                ArrayList<String> photolists = new ArrayList<String>();
                                for (int i = 0; i < photolist.length(); i++) {
                                    photolists.add(photolist.getString(i));
                                }
                                TaskitemReqPgAdapter adapter = new TaskitemReqPgAdapter(ExperienceCommentActivity.this, photolists, true);
                                epconmment_gridview.setAdapter(adapter);
                            } else {
                                epconmment_edittext.setFocusable(false);
                                epconmment_edittext.setClickable(false);
                                epconmment_edittext.setText("");
                                epconmment_edittext.setHint("");
                            }
                        } else {
                            findViewById(R.id.epconmment_button).setVisibility(View.VISIBLE);
                            findViewById(R.id.epconmment_layout).setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.optJSONArray("scoreType");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ratingView = new RatingView(ExperienceCommentActivity.this);
                                    ratingView.setOnRatingViewClickListener(ExperienceCommentActivity.this);
                                    ratingView.setData(jsonArray.getString(i));
                                    epconmment_rating.addView(ratingView);
                                }
                            }
                            if (jsonArray1 != null) {
                                flowLayoutView = (FlowLayoutView) findViewById(R.id.flowlayout);
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    final TextView textView = new TextView(ExperienceCommentActivity.this);
                                    textView.setText(jsonArray1.getString(i));
                                    textView.setPadding(5, 5, 5, 5);
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setTextSize(14);
                                    textView.setTextColor(getResources().getColor(R.color.app_background2));
                                    textView.setBackgroundResource(R.drawable.shape_item2);
                                    flowLayoutView.addView(textView);
                                    final int[] tag = {1};
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            tag[0]++;
                                            if (tag[0] % 2 == 0) {
                                                textView.setTextColor(getResources().getColor(R.color.experience_notselect));
                                                textView.setBackgroundResource(R.drawable.shape_item);
                                                if (multiselect == null || "".equals(multiselect)) {
                                                    multiselect = textView.getText().toString();
                                                } else {
                                                    if (!multiselect.contains(textView.getText().toString())) {
                                                        multiselect = multiselect + "," + textView.getText().toString();
                                                    }
                                                }
                                            } else {
                                                textView.setTextColor(getResources().getColor(R.color.app_background2));
                                                textView.setBackgroundResource(R.drawable.shape_item2);
                                                if (multiselect.equals(textView.getText().toString())) {
                                                    multiselect = multiselect.replace(textView.getText().toString(), "");
                                                } else if ((multiselect.substring(0, textView.getText().toString().length() - 1)).equals(textView.getText().toString())) {
                                                    multiselect = multiselect.replace(textView.getText().toString() + ",", "");
                                                } else {
                                                    multiselect = multiselect.replace("," + textView.getText().toString(), "");
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                            refreshData();
                            gridViewAdapter = new AlbumGridViewAdapter(ExperienceCommentActivity.this, dataList, selectedDataList, true);
                            epconmment_gridview.setAdapter(gridViewAdapter);
                            onItemClickListener();
                        }
                    } else {
                        Tools.showToast(ExperienceCommentActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(ExperienceCommentActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperienceCommentActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void refreshData() {
        new AsyncTask<Void, Void, ArrayList<String>>() {

            protected void onPreExecute() {
                super.onPreExecute();
            }

            protected ArrayList<String> doInBackground(Void... params) {
                ArrayList<String> list;
                if (TextUtils.isEmpty(taskid)) {
                    list = systemDBHelper.getPictureThumbnail(AppInfo.getName(ExperienceCommentActivity.this), projectid, storeid);
                } else {
                    list = systemDBHelper.getPictureThumbnail(AppInfo.getName(ExperienceCommentActivity.this), projectid,
                            storeid, packageid, taskid);
                }
                if (!list.isEmpty() && list != null) {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        String path = systemDBHelper.searchForOriginalpath(list.get(i));
                        File file = new File(path);
                        if (!file.exists() || !file.isFile()) {
                            list.remove(list.get(i));
                        }
                        size--;
                        i--;
                    }
                }
                return list;
            }

            protected void onPostExecute(ArrayList<String> tmpList) {
                if (ExperienceCommentActivity.this == null || ExperienceCommentActivity.this.isFinishing()) {
                    return;
                }
                dataList.clear();
                dataList.add("bendi");//本地相册
                dataList.addAll(tmpList);
                gridViewAdapter.notifyDataSetChanged();
                return;

            }
        }.execute();

    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void click(String text, String score) {
        hashMap.put(text, score);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.epconmment_button) {
            score = "";
            Iterator iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (score == null || "".equals(score)) {
                    score = key + ":" + value;
                } else {
                    score = score + "," + key + ":" + value;
                }
            }
            comment = epconmment_edittext.getText().toString();
            if (ratingView != null) {
                if (score == null || "".equals(score)) {
                    Tools.showToast(this, "请给店铺打分~");
                    return;
                }
            }
            if (flowLayoutView != null) {
                if (multiselect == null || "".equals(multiselect)) {
                    Tools.showToast(this, "请给店铺评价~");
                    return;
                }
            }
            if (comment == null || "".equals(comment)) {
                Tools.showToast(this, "请填写内容~");
                return;
            }
            if (selectedDataList.size() == 0) {
                Tools.showToast(this, "请选择图片~");
                return;
            }
            sendData();
        }
    }

    private String recommend, select;//是否有查看更多推荐按钮。1为有，0为没有 是否有参与评选按钮，1为有，0为没有。

    private void sendData() {
        experienceCommentUp.sendPostRequest(Urls.ExperienceCommentUp, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d("体验评价执行完成：" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        String executeid = jsonObject.getString("executeid");//executeid执行的id
                        recommend = jsonObject.getString("recommend");
                        select = jsonObject.getString("select");
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("executeid", executeid);
                        String imgs = "", key = "";
                        int size = selectedDataList.size();
                        for (int i = 0; i < size; i++) {
                            String path = systemDBHelper.searchForOriginalpath(selectedDataList.get(i));
                            if (path != null && !"".equals(path)) {
                                selectedDataList.remove(i);
                                selectedDataList.add(i, path);
                            }
                            if ("bendi".equals(path)) {
                                continue;
                            }
                            if (TextUtils.isEmpty(imgs)) {
                                imgs = selectedDataList.get(i);
                            } else {
                                imgs = imgs + "," + selectedDataList.get(i);
                            }
                            if (TextUtils.isEmpty(key)) {
                                key = "img" + (i + 1);
                            } else {
                                key = key + ",img" + (i + 1);
                            }
                        }//8-9体验任务==体验评论分享的九张图片
                        Tools.d("体验评论imgs：" + imgs);
                        String username = AppInfo.getName(ExperienceCommentActivity.this);
                        updataDBHelper.addUpdataTask(username, projectid, projectname, storecode, brand, storeid, storeName,
                                packageid, null, "8-9", taskid, null, null, null, null, username + projectid +
                                        storeid + packageid + taskid + "typl", Urls.ExperienceCommentComplete, key, imgs,
                                UpdataDBHelper.Updata_file_type_img, params, photo_compression, true, Urls.ExperienceCommentUp, paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        share();
                    } else {
                        Tools.showToast(ExperienceCommentActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(ExperienceCommentActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperienceCommentActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void share() {
        mShareAction.open();
    }

    private String paramsToString() {
        Map<String, String> params = new HashMap<>();
        params.put("storeid", storeid);
        params.put("usermobile", AppInfo.getName(ExperienceCommentActivity.this));
        params.put("score", score);
        params.put("multiselect", multiselect);
        params.put("comment", comment);
        params.put("lon", longitude + "");
        params.put("lat", latitude + "");
        String data = "";
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + params.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + params.get(key).trim();
                }
            }
        }
        return data;
    }

    private ShareAction mShareAction;

    private void configPlatforms() {
        PlatformConfig.setQQZone(getResources().getString(R.string.qq_appid), getResources().getString(R.string.qq_appkey));
        UMImage umImage = new UMImage(this, BitmapFactory.decodeResource(getResources(), R.mipmap.login_icon));
        UMWeb web = new UMWeb(Urls.ShareOutlet + "?storeid=" + storeid + "&usermobile=" + AppInfo.getName(this) + "&sign=" + sign);
        web.setTitle(getResources().getString(R.string.share_title));//标题
        web.setThumb(umImage);
        web.setDescription("下载偶业 领取奖励金");//描述
//        SharedPreferences sharedPreferences = getSharedPreferences("tempsp", Context.MODE_PRIVATE);
        String wx_appid = getResources().getString(R.string.wx_appid);
        String wx_appsecret = getResources().getString(R.string.wx_appsecret);
        if (TextUtils.isEmpty(wx_appid) || TextUtils.isEmpty(wx_appsecret)) {
            mShareAction = new ShareAction(this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE).withMedia(web).setCallback(umShareListener);
        } else {
            PlatformConfig.setWeixin(wx_appid, wx_appsecret);
            mShareAction = new ShareAction(this).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                    .withMedia(web).setCallback(umShareListener);
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        public void onStart(SHARE_MEDIA share_media) {
        }

        //分享成功的回调
        public void onResult(SHARE_MEDIA platform) {
            Tools.d(platform.toString());
            if ("1".equals(recommend) && "1".equals(select)) {//有推荐 有评选
                ConfirmDialog.showDialog(ExperienceCommentActivity.this, null, "恭喜您分享成功!", "查看更多推荐",
                        "参与评选", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                Intent intent = new Intent(ExperienceCommentActivity.this, RecommendExperienceActivity.class);
                                intent.putExtra("projectid", projectid);
                                startActivity(intent);
                                baseFinish();
                            }

                            @Override
                            public void rightClick(Object object) {
                                Intent intent = new Intent(ExperienceCommentActivity.this, CommentSelectActivity.class);
                                intent.putExtra("projectid", projectid);
                                startActivity(intent);
                                baseFinish();
                            }
                        });
            } else if ("1".equals(recommend) && "0".equals(select)) {
                ConfirmDialog.showDialogForHint(ExperienceCommentActivity.this, "恭喜您分享成功!",
                        "查看更多推荐", new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                Intent intent = new Intent(ExperienceCommentActivity.this, RecommendExperienceActivity.class);
                                intent.putExtra("projectid", projectid);
                                startActivity(intent);
                                baseFinish();
                            }
                        });
            } else if ("0".equals(recommend) && "1".equals(select)) {
                ConfirmDialog.showDialogForHint(ExperienceCommentActivity.this, "恭喜您分享成功!",
                        "参与评选", new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                Intent intent = new Intent(ExperienceCommentActivity.this, CommentSelectActivity.class);
                                intent.putExtra("projectid", projectid);
                                startActivity(intent);
                                baseFinish();
                            }
                        });
            } else {
                baseFinish();
            }
        }

        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ConfirmDialog.showDialogForHint(ExperienceCommentActivity.this, "分享失败,您需要重新分享");
        }

        public void onCancel(SHARE_MEDIA share_media) {
            Tools.d("cancel:。。。。。。。");
        }
    };

    /**
     * 初始化定位
     */

    private void initLocation() {
        if (locationClient != null && locationClient.isStarted()) {
            Tools.showToast(ExperienceCommentActivity.this, "正在定位...");
            return;
        }
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(myListener);
        setLocationOption();
        locationClient.start();
    }

    public void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        locationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            locationClient.stop();
            if (bdLocation == null) {
                Tools.showToast(ExperienceCommentActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppInfo
                        .REQUEST_CODE_ASK_LOCATION);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(ExperienceCommentActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(ExperienceCommentActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
