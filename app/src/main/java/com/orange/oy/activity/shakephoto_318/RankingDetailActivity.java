package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.RankingDetailAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.RankingListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 排名照片详情 V3.18
 */
public class RankingDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.rankingdetail_title);
        appTitle.settingName(acname);
        appTitle.showBack(this);
    }

    private void initNetwork() {
        joinActivityPhotoAlbum = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(RankingDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                params.put("join_usermobile", join_usermobile);
                return params;
            }
        };
    }

    private TextView rankingdetail_ranking, rankingdetail_name;
    private CircularImageView rankingdetail_img;
    private GridView rankingdetail_gridview;
    private RankingDetailAdapter rankingDetailAdapter;
    private NetworkConnection joinActivityPhotoAlbum;
    private String ai_id, join_usermobile;
    private ImageLoader imageLoader;
    private ArrayList<RankingListInfo> list;
    private String acname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_detail);
        list = new ArrayList<>();
        Intent data = getIntent();
        ai_id = data.getStringExtra("ai_id");
        acname = data.getStringExtra("acname");
        join_usermobile = data.getStringExtra("join_usermobile");
        initTitle();
        initNetwork();
        imageLoader = new ImageLoader(this);
        rankingdetail_ranking = (TextView) findViewById(R.id.rankingdetail_ranking);
        rankingdetail_name = (TextView) findViewById(R.id.rankingdetail_name);
        rankingdetail_img = (CircularImageView) findViewById(R.id.rankingdetail_img);
        rankingdetail_gridview = (GridView) findViewById(R.id.rankingdetail_gridview);
        String ranking = data.getStringExtra("ranking");
        if (TextUtils.isEmpty(ranking)) {
            rankingdetail_ranking.setVisibility(View.INVISIBLE);
        } else {
            rankingdetail_ranking.setVisibility(View.VISIBLE);
            rankingdetail_ranking.setText(ranking);
        }
        imageLoader.DisplayImage(Urls.ImgIp + data.getStringExtra("user_img"),
                rankingdetail_img, R.mipmap.grxx_icon_mrtx);
        rankingdetail_name.setText(data.getStringExtra("user_name"));
        rankingDetailAdapter = new RankingDetailAdapter(this, list);
        rankingdetail_gridview.setAdapter(rankingDetailAdapter);
        getData();
    }

    private void getData() {
        joinActivityPhotoAlbum.sendPostRequest(Urls.JoinActivityPhotoAlbum, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("photo_list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                RankingListInfo rankingListInfo = new RankingListInfo();
                                rankingListInfo.setFi_id(object.getString("fi_id"));
                                rankingListInfo.setFile_url(object.getString("file_url"));
                                rankingListInfo.setCreate_time(object.getString("create_time"));
                                rankingListInfo.setKey_concent(object.getString("key_concent"));
                                rankingListInfo.setRanking(object.getString("ranking"));
                                rankingListInfo.setComment_num(object.getString("comment_num"));
                                rankingListInfo.setPraise_num(object.getString("praise_num"));
                                rankingListInfo.setShare_num(object.getString("share_num"));
                                rankingListInfo.setUser_img(object.getString("user_img"));
                                rankingListInfo.setUser_mobile(object.getString("user_mobile"));
                                rankingListInfo.setPraise_state(object.getString("praise_state"));
                                list.add(rankingListInfo);
                            }
                            if (rankingDetailAdapter != null) {
                                rankingDetailAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(RankingDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(RankingDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(RankingDetailActivity.this, getResources().getString(R.string.network_batch_error));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
