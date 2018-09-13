package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.adapter.mycorps_314.RankingListAdapter;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 参与的活动相册排行榜 V3.18
 */

public class RankingListActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener, View.OnClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.rankinglist_title);
        appTitle.settingName(acname + "排行榜");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.illustrate_icon, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Intent intent = new Intent(RankingListActivity.this, BrowserActivity.class);
                intent.putExtra("title", "排名说明");
                intent.putExtra("flag", BrowserActivity.flag_question);
                intent.putExtra("content", Urls.RankingDescription);
                startActivity(intent);
            }
        });
    }

    private void initNetwork() {
        rankingInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(RankingListActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                params.put("type", "1");
                return params;
            }
        };
    }

    private CircularImageView rankingtop1_img, rankingtop2_img, rankingtop3_img;
    private PullToRefreshListView rankinglist_listview;
    private RankingListAdapter rankingListAdapter;
    private NetworkConnection rankingInfo;
    private String ai_id;
    private ImageLoader imageLoader;
    private TextView rankingtop1_name, rankingtop2_name, rankingtop3_name;
    private String user_mobile1, user_mobile2, user_mobile3;
    private String ranking1, ranking2, ranking3;
    private String user_img1, user_img2, user_img3;
    private String user_name1, user_name2, user_name3;
    private ArrayList<RankingListInfo> list;
    private String acname;//标题名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_list);
        Intent data = getIntent();
        list = new ArrayList<>();
        ai_id = data.getStringExtra("ai_id");
        acname = data.getStringExtra("acname");
        initTitle();
        initNetwork();
        imageLoader = new ImageLoader(this);
        rankingtop1_img = (CircularImageView) findViewById(R.id.rankingtop1_img);
        rankingtop2_img = (CircularImageView) findViewById(R.id.rankingtop2_img);
        rankingtop3_img = (CircularImageView) findViewById(R.id.rankingtop3_img);
        rankingtop1_name = (TextView) findViewById(R.id.rankingtop1_name);
        rankingtop2_name = (TextView) findViewById(R.id.rankingtop2_name);
        rankingtop3_name = (TextView) findViewById(R.id.rankingtop3_name);
        rankinglist_listview = (PullToRefreshListView) findViewById(R.id.rankinglist_listview);
        rankingListAdapter = new RankingListAdapter(this, list);
        rankinglist_listview.setAdapter(rankingListAdapter);
        rankinglist_listview.setOnItemClickListener(this);
        getData();
        rankingtop1_img.setOnClickListener(this);
        rankingtop2_img.setOnClickListener(this);
        rankingtop3_img.setOnClickListener(this);
        rankinglist_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
    }

    private void getData() {
        rankingInfo.sendPostRequest(Urls.RankingInfo, new Response.Listener<String>() {
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
                        JSONArray top_three = jsonObject.optJSONArray("top_three");//top3排名
                        if (top_three != null && top_three.length() > 0) {
                            if (top_three.length() == 1 || top_three.length() == 2 || top_three.length() == 3) {
                                findViewById(R.id.rankingtop1_ly).setVisibility(View.VISIBLE);
                                JSONObject object = top_three.getJSONObject(0);
                                rankingtop1_name.setText(object.getString("user_name"));
                                user_mobile1 = object.getString("user_mobile");
                                ranking1 = object.getString("ranking");
                                user_img1 = object.getString("user_img");
                                user_name1 = object.getString("user_name");
                                if (!Tools.isEmpty(user_img1) && user_img1.startsWith("http://")) {
                                    imageLoader.DisplayImage(user_img1, rankingtop1_img, R.mipmap.grxx_icon_mrtx);
                                } else {
                                    imageLoader.DisplayImage(Urls.ImgIp + user_img1, rankingtop1_img, R.mipmap.grxx_icon_mrtx);
                                }
                                if (top_three.length() == 2 || top_three.length() == 3) {
                                    findViewById(R.id.rankingtop2_ly).setVisibility(View.VISIBLE);
                                    JSONObject object1 = top_three.getJSONObject(1);
                                    rankingtop2_name.setText(object1.getString("user_name"));
                                    user_mobile2 = object1.getString("user_mobile");
                                    ranking2 = object1.getString("ranking");
                                    user_img2 = object1.getString("user_img");
                                    user_name2 = object1.getString("user_name");
                                    if (!Tools.isEmpty(user_img2) && user_img2.startsWith("http://")) {
                                        imageLoader.DisplayImage(user_img2, rankingtop2_img, R.mipmap.grxx_icon_mrtx);
                                    } else {
                                        imageLoader.DisplayImage(Urls.ImgIp + user_img2, rankingtop2_img, R.mipmap.grxx_icon_mrtx);
                                    }
                                }
                                if (top_three.length() == 3) {
                                    findViewById(R.id.rankingtop3_ly).setVisibility(View.VISIBLE);
                                    JSONObject object2 = top_three.getJSONObject(2);
                                    rankingtop3_name.setText(object2.getString("user_name"));
                                    user_mobile3 = object2.getString("user_mobile");
                                    ranking3 = object2.getString("ranking");
                                    user_img3 = object2.getString("user_img");
                                    user_name3 = object2.getString("user_name");
                                    if (!Tools.isEmpty(user_img3) && user_img3.startsWith("http://")) {
                                        imageLoader.DisplayImage(user_img3, rankingtop3_img, R.mipmap.grxx_icon_mrtx);
                                    } else {
                                        imageLoader.DisplayImage(Urls.ImgIp + user_img3, rankingtop3_img, R.mipmap.grxx_icon_mrtx);
                                    }
                                }
                            }
                        }

                        int length = 0;
                        //我的排名
                        JSONArray myranking_list = jsonObject.optJSONArray("myranking_list");
                        if (myranking_list != null) {
                            length = myranking_list.length();
                            for (int i = 0; i < myranking_list.length(); i++) {
                                JSONObject object = myranking_list.getJSONObject(i);
                                RankingListInfo rankingListInfo = new RankingListInfo();
                                rankingListInfo.setFi_id(object.getString("fi_id"));
                                rankingListInfo.setFile_url(object.getString("file_url"));
                                rankingListInfo.setCreate_time(object.getString("create_time"));
                                rankingListInfo.setKey_concent(object.getString("key_concent"));
                                rankingListInfo.setRanking(object.getString("ranking"));
                                rankingListInfo.setComment_num(object.getString("comment_num"));
                                rankingListInfo.setPraise_num(object.getString("praise_num"));
                                rankingListInfo.setShare_num(object.getString("share_num"));
                                rankingListInfo.setUser_name(object.getString("user_name"));
                                rankingListInfo.setUser_img(object.getString("user_img"));
                                rankingListInfo.setUser_mobile(object.getString("user_mobile"));
                                list.add(rankingListInfo);
                            }
                        }

                        //全部排名
                        JSONArray allranking_list = jsonObject.optJSONArray("allranking_list");
                        if (allranking_list != null) {
                            for (int i = 0; i < allranking_list.length(); i++) {
                                JSONObject object = allranking_list.getJSONObject(i);
                                RankingListInfo rankingListInfo = new RankingListInfo();
                                rankingListInfo.setFi_id(object.getString("fi_id"));
                                rankingListInfo.setFile_url(object.getString("file_url"));
                                rankingListInfo.setCreate_time(object.getString("create_time"));
                                rankingListInfo.setKey_concent(object.getString("key_concent"));
                                rankingListInfo.setRanking(object.getString("ranking"));
                                rankingListInfo.setComment_num(object.getString("comment_num"));
                                rankingListInfo.setPraise_num(object.getString("praise_num"));
                                rankingListInfo.setShare_num(object.getString("share_num"));
                                rankingListInfo.setUser_name(object.getString("user_name"));
                                rankingListInfo.setUser_img(object.getString("user_img"));
                                rankingListInfo.setUser_mobile(object.getString("user_mobile"));
                                list.add(rankingListInfo);
                            }
                        }
                        if (rankingListAdapter != null) {
                            rankingListAdapter.setLength(length);
                            rankingListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(RankingListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(RankingListActivity.this, getResources().getString(R.string.network_error));
                }
                rankinglist_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(RankingListActivity.this, getResources().getString(R.string.network_volleyerror));
                rankinglist_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RankingListInfo rankingListInfo = list.get(position - 1);
        Intent intent = new Intent(this, RankingDetailActivity.class);
        intent.putExtra("join_usermobile", rankingListInfo.getUser_mobile());
        intent.putExtra("ai_id", ai_id);
        intent.putExtra("ranking", rankingListInfo.getRanking());
        intent.putExtra("user_img", rankingListInfo.getUser_img());
        intent.putExtra("user_name", rankingListInfo.getUser_name());
        intent.putExtra("acname", acname);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String user_mobile = null;
        String ranking = null;
        String user_img = null;
        String user_name = null;
        switch (v.getId()) {
            case R.id.rankingtop1_img: {//第一
                user_mobile = user_mobile1;
                ranking = ranking1;
                user_img = user_img1;
                user_name = user_name1;
            }
            break;
            case R.id.rankingtop2_img: {//第二
                user_mobile = user_mobile2;
                ranking = ranking2;
                user_img = user_img2;
                user_name = user_name2;
            }
            break;
            case R.id.rankingtop3_img: {//第三
                user_mobile = user_mobile3;
                ranking = ranking3;
                user_img = user_img3;
                user_name = user_name3;
            }
            break;
        }
        if (!Tools.isEmpty(user_mobile)) {
            Intent intent = new Intent(this, RankingDetailActivity.class);
            intent.putExtra("join_usermobile", user_mobile);
            intent.putExtra("ai_id", ai_id);
            intent.putExtra("ranking", ranking);
            intent.putExtra("user_img", user_img);
            intent.putExtra("user_name", user_name);
            intent.putExtra("acname", acname);
            startActivity(intent);
        }
    }
}
