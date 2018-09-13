package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.MyImageViewAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.shakephoto.ShakePhotoInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 相册查看==我参与的活动(活动详情) V3.16
 */
public class ShakeAlbumDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, AdapterView.OnItemClickListener {
    private AppTitle appTitle;

    private void initTitle(String name) {
        appTitle = (AppTitle) findViewById(R.id.shakedetail_title);
        appTitle.settingName(name);
        appTitle.showBack(this);
    }

    private void initNetwork() {
        activityPhotoDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ShakeAlbumDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activityPhotoDetail != null) {
            activityPhotoDetail.stop(Urls.ActivityPhotoDetail);
        }
    }

    private MyImageViewAdapter myImageViewAdapter;
    private MyGridView shakedetail_gridview;
    private NetworkConnection activityPhotoDetail;
    private String ai_id;
    private TextView shakedetail_finishnum, shakedetail_target, shakedetail_rate1;
    private ProgressBar shakedetail_rate2;
    private View shakedetail_redly;
    private ArrayList<ShakePhotoInfo> list;
    private String time, tv_theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_album_detail);
        list = new ArrayList<>();
        Intent data = getIntent();
        initTitle(data.getStringExtra("title"));
        ai_id = data.getStringExtra("ai_id");
        time = data.getStringExtra("time");
        tv_theme = data.getStringExtra("tv_theme");
        initNetwork();
        shakedetail_finishnum = (TextView) findViewById(R.id.shakedetail_finishnum);
        shakedetail_target = (TextView) findViewById(R.id.shakedetail_target);
        shakedetail_rate1 = (TextView) findViewById(R.id.shakedetail_rate1);
        shakedetail_rate2 = (ProgressBar) findViewById(R.id.shakedetail_rate2);
        shakedetail_redly = findViewById(R.id.shakedetail_redly);
        shakedetail_gridview = (MyGridView) findViewById(R.id.shakedetail_gridview);
        myImageViewAdapter = new MyImageViewAdapter(this, list);
        shakedetail_gridview.setAdapter(myImageViewAdapter);
        ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 20);
        findViewById(R.id.shakedetail_button).setOnClickListener(this);
        shakedetail_gridview.setOnItemClickListener(this);
        getData();
    }

    public static boolean isRefresh = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            getData();
        }
    }

    private void getData() {
        activityPhotoDetail.sendPostRequest(Urls.ActivityPhotoDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        appTitle.settingExit(jsonObject.getString("photo_num"));
                        shakedetail_finishnum.setText(jsonObject.getString("complete_num"));
                        shakedetail_target.setText("/" + jsonObject.getString("target_num"));
                        int redpack_progress = Tools.StringToInt(Tools.removePoint(jsonObject.getString("redpack_progress")));
                        shakedetail_rate1.setText(redpack_progress + "%");
                        shakedetail_rate2.setProgress(redpack_progress);
                        if (redpack_progress == 100) {
                            shakedetail_redly.setBackgroundResource(R.drawable.redpacket_bg2);
                            shakedetail_redly.setOnClickListener(ShakeAlbumDetailActivity.this);
                        } else {
                            shakedetail_redly.setBackgroundResource(R.drawable.redpacket_bg);
                            shakedetail_redly.setOnClickListener(null);
                        }
                        ImageView shakedetail_redimg = (ImageView) findViewById(R.id.shakedetail_redimg);
                        TextView shakedetail_text = (TextView) findViewById(R.id.shakedetail_text);
                        if ("0".equals(jsonObject.getString("open_status"))) {//未打开
                            shakedetail_redimg.setImageResource(R.mipmap.redpacket_notopen);
                            shakedetail_text.setText("拆红包");
                        } else {
                            shakedetail_text.setText("已拆");
                            shakedetail_redimg.setImageResource(R.mipmap.redpacket_opened);
                            shakedetail_redly.setBackgroundResource(R.drawable.redpacket_bg);
                            shakedetail_redly.setOnClickListener(null);
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("photo_list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                ShakePhotoInfo shakePhotoInfo = new ShakePhotoInfo();
                                shakePhotoInfo.setAddress(object.getString("address"));
                                shakePhotoInfo.setAitivity_name(object.getString("aitivity_name"));
                                shakePhotoInfo.setProvince(object.getString("province"));
                                shakePhotoInfo.setCity(object.getString("city"));
                                shakePhotoInfo.setCounty(object.getString("county"));
                                shakePhotoInfo.setCreate_time(object.getString("create_time"));
                                shakePhotoInfo.setFi_id(object.getString("fi_id"));
                                shakePhotoInfo.setFile_url(object.getString("file_url"));
                                shakePhotoInfo.setKey_concent(object.getString("key_concent"));
                                shakePhotoInfo.setLatitude(object.getString("latitude"));
                                shakePhotoInfo.setLongitude(object.getString("longitude"));
                                shakePhotoInfo.setMoney(object.getString("money"));
                                shakePhotoInfo.setShow_address(object.getString("show_address"));
                                list.add(shakePhotoInfo);
                            }
                            if (myImageViewAdapter != null) {
                                myImageViewAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(ShakeAlbumDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ShakeAlbumDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ShakeAlbumDetailActivity.this, getResources().getString(R.string.network_volleyerror));
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
            case R.id.shakedetail_redly: {//拆红包
                Intent intent = new Intent(this, RedPackageStateActivity.class);
                intent.putExtra("time", time);
                intent.putExtra("tv_theme", tv_theme);
                intent.putExtra("ai_id", ai_id);
                startActivity(intent);
            }
            break;
            case R.id.shakedetail_button: {
                final String webUrl = Urls.InviteToActivity + "usermobile=" +
                        AppInfo.getName(ShakeAlbumDetailActivity.this) + "&ai_id=" + ai_id;
                UMShareDialog.showDialog(ShakeAlbumDetailActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        MyUMShareUtils.umShare_shakephoto(ShakeAlbumDetailActivity.this, type, webUrl);
                    }
                });
            }
            break;
        }
    }

    private ArrayList<LargeImagePageInfo> largeImagePageInfos;

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myImageViewAdapter != null && list != null && !list.isEmpty()) {
            if (largeImagePageInfos == null) {
                largeImagePageInfos = new ArrayList<>();
                for (ShakePhotoInfo shakePhotoInfo : list) {
                    LargeImagePageInfo largeImagePageInfo = new LargeImagePageInfo();
                    largeImagePageInfo.setKey_concent(shakePhotoInfo.getKey_concent());
                    largeImagePageInfo.setFile_url(shakePhotoInfo.getFile_url());
                    largeImagePageInfo.setAddress(shakePhotoInfo.getAddress());
                    largeImagePageInfo.setShow_address(shakePhotoInfo.getShow_address());
                    largeImagePageInfo.setCreate_time(shakePhotoInfo.getCreate_time());
                    largeImagePageInfo.setAitivity_name(shakePhotoInfo.getAitivity_name());
                    largeImagePageInfos.add(largeImagePageInfo);
                }
            }
            Intent intent = new Intent(this, LargeImagePageActivity.class);
            intent.putExtra("isList", true);
            intent.putExtra("list", largeImagePageInfos);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }
}
