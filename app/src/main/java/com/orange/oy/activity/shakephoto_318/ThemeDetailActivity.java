package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.LargeImageInfo;
import com.orange.oy.info.ThemeDetailInfo;
import com.orange.oy.info.shakephoto.ShakeThemeInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.StaggeredLoadGridView;
import com.orange.oy.view.ThemeDetailItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/18.
 * 活动图片展示页
 */

public class ThemeDetailActivity extends BaseActivity implements View.OnClickListener {
    private NetworkConnection ThemePhotoAlbum;
    private StaggeredLoadGridView staggeredLoadGridView;
    private ArrayList<ThemeDetailInfo> list = new ArrayList<>();
    private String ai_id = "";
    private int page = 1;
    private int sort_type = 1;//1为按排名，2为时间倒序，3为评论数，4为点赞数，5时间正序
    private String is_join = "1";//是否是参与的活动，1为是，0为否
    private MyAdapter myAdapter;

    protected void onStop() {
        super.onStop();
        if (ThemePhotoAlbum != null) {
            ThemePhotoAlbum.stop(Urls.ThemePhotoAlbum);
        }
    }

    private void InitNetwork() {
        ThemePhotoAlbum = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ThemeDetailActivity.this));
                params.put("ai_id", ai_id);
                params.put("type", "1");
                params.put("page", page + "");
                params.put("sort_type", sort_type + "");
//                params.put("is_join", is_join);
                return params;
            }
        };
    }

    private TextView themedetail_item1;
    private TextView themedetail_item2;
    private TextView themedetail_item3;
    private TextView themedetail_item4;
    private NetworkView themedetail_network;
    private View themedetail_img_layout;
    private View themedetail_123;
    private ImageView themedetail_img;
    private TextView themedetail_img_number;
    private TextView themedetail_rank, themedetail_time;
    private ImageView themedetail_time_r;
    private View themedetail_rank_line, themedetail_time_line;
    private ImageLoader imageLoader = new ImageLoader(this);
    private String acname = "";
    private ShakeThemeInfo shakeThemeInfo;//点参与活动需要
    private View themedetail_top_s_line;//右边距：没参与15dp，已参与：96dp
    private TextView themedetail_my123;
    private View themedetail_1_sponsor, themedetail_1_ask;
    private View themedetail_button;
    private AppTitle themedetail_title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themedetail);
//        is_join = getIntent().getStringExtra("is_join");//是否是参与的活动，1为是，0为否
        ai_id = getIntent().getStringExtra("ai_id");
        acname = getIntent().getStringExtra("acname");
        themedetail_title = (AppTitle) findViewById(R.id.themedetail_title);
        themedetail_title.settingName(acname);
        themedetail_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        InitNetwork();
        themedetail_1_sponsor = findViewById(R.id.themedetail_1_sponsor);
        themedetail_1_ask = findViewById(R.id.themedetail_1_ask);
        themedetail_img_number = (TextView) findViewById(R.id.themedetail_img_number);
        themedetail_img = (ImageView) findViewById(R.id.themedetail_img);
        themedetail_123 = findViewById(R.id.themedetail_123);
        themedetail_my123 = (TextView) findViewById(R.id.themedetail_my123);
        themedetail_img_layout = findViewById(R.id.themedetail_img_layout);
        themedetail_top_s_line = findViewById(R.id.themedetail_top_s_line);
        themedetail_network = (NetworkView) findViewById(R.id.themedetail_network);
        themedetail_item1 = (TextView) findViewById(R.id.themedetail_item1);
        themedetail_item2 = (TextView) findViewById(R.id.themedetail_item2);
        themedetail_item3 = (TextView) findViewById(R.id.themedetail_item3);
        themedetail_item4 = (TextView) findViewById(R.id.themedetail_item4);
        themedetail_rank = (TextView) findViewById(R.id.themedetail_rank);
        themedetail_time = (TextView) findViewById(R.id.themedetail_time);
        themedetail_time_r = (ImageView) findViewById(R.id.themedetail_time_r);
        themedetail_rank_line = findViewById(R.id.themedetail_rank_line);
        themedetail_time_line = findViewById(R.id.themedetail_time_line);
        themedetail_button = findViewById(R.id.themedetail_button);
        themedetail_button.setOnClickListener(this);
        themedetail_rank.setOnClickListener(this);
        themedetail_time.setOnClickListener(this);
        themedetail_time_r.setOnClickListener(this);
        themedetail_item3.setOnClickListener(this);
        staggeredLoadGridView = (StaggeredLoadGridView) findViewById(R.id.staggeredloadgridview);
        staggeredLoadGridView.setMode(PullToRefreshBase.Mode.BOTH);
        myAdapter = new MyAdapter();
        staggeredLoadGridView.setAdapter(myAdapter);
        staggeredLoadGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StaggeredGridView>() {
            public void onPullDownToRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                staggeredLoadGridView.setMode(PullToRefreshBase.Mode.BOTH);
                page = 1;
                getData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                page++;
                getData();
            }
        });
        staggeredLoadGridView.setScrollingWhileRefreshingEnabled(true);
        staggeredLoadGridView.setOnItemClickListener(onItemClickListener);
        onClick(themedetail_rank);
        page = 1;
        getData();
    }

    protected void onResume() {
        super.onResume();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<LargeImageInfo> largeImageInfos = new ArrayList<>();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                ThemeDetailInfo themeDetailInfo = list.get(i);
                LargeImageInfo largeImageInfo = new LargeImageInfo();
                largeImageInfo.setAi_id(ai_id);
                largeImageInfo.setFi_id(themeDetailInfo.getFi_id());
                largeImageInfo.setShare_num(themeDetailInfo.getShare_num());
                largeImageInfo.setSai_id(themeDetailInfo.getSai_id());
                largeImageInfo.setAd_links(themeDetailInfo.getAd_links());
                largeImageInfo.setComment_num(themeDetailInfo.getComment_num());
                largeImageInfo.setFile_url(themeDetailInfo.getFile_url());
                largeImageInfo.setIs_advertisement(themeDetailInfo.getIs_advertisement());
                largeImageInfo.setPraise_num(themeDetailInfo.getPraise_num());
                largeImageInfo.setPraise_state(themeDetailInfo.getPraise_state());
                largeImageInfo.setUser_img(themeDetailInfo.getUser_img());
                largeImageInfo.setUser_mobile(themeDetailInfo.getUser_mobile());
                largeImageInfo.setComment_state(themeDetailInfo.getComment_state());
                largeImageInfo.setUser_name(themeDetailInfo.getUser_name());
                largeImageInfo.setAcname(acname);
                largeImageInfos.add(largeImageInfo);
            }
            Intent intent = new Intent(ThemeDetailActivity.this, LargeImageActivity.class);
            intent.putExtra("list", largeImageInfos);
            intent.putExtra("position", position);
            startActivityForResult(intent, largeimageRequestcode);
        }
    };

    private final int largeimageRequestcode = 0x100;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppInfo.MyDetailRequestCodeForPick: {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(filePathColumns[0]);
                        String mPath = c.getString(columnIndex);
                        c.close();
                        Intent intent = new Intent(ThemeDetailActivity.this, ShakephotoActivity.class);
                        intent.putExtra("shakeThemeInfo", shakeThemeInfo);
                        intent.putExtra("isuppic", true);
                        intent.putExtra("mPath", mPath);
                        startActivity(intent);
                    }
                }
            }
            break;
            case largeimageRequestcode: {
                if (resultCode == RESULT_OK) {
                    ArrayList<LargeImageInfo> largeImagePageInfoList = (ArrayList<LargeImageInfo>) data.getSerializableExtra("list");
                    int size = largeImagePageInfoList.size();
                    if (list.size() < size) {
                        page = 1;
                        getData();
                    } else {
                        for (int i = 0; i < size; i++) {
                            LargeImageInfo largeImageInfo = largeImagePageInfoList.get(i);
                            ThemeDetailInfo themeDetailInfo = list.get(i);
                            themeDetailInfo.setShare_num(largeImageInfo.getShare_num());
                            themeDetailInfo.setPraise_num(largeImageInfo.getPraise_num());
                            themeDetailInfo.setComment_num(largeImageInfo.getComment_num());
                            themeDetailInfo.setPraise_state(largeImageInfo.getPraise_state());
                            themeDetailInfo.setComment_state(largeImageInfo.getComment_state());
                        }
                        int position = staggeredLoadGridView.getRefreshableView().getFirstVisiblePosition();
                        myAdapter.notifyDataSetChanged();
                        staggeredLoadGridView.getRefreshableView().setSelection(position);
                    }
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String photo_source_type;//1：可直接拍摄；2：可从甩吧相册选择；3：可从手机本地相册选择

    private void getData() {
        ThemePhotoAlbum.stop(Urls.ThemePhotoAlbum);
        ThemePhotoAlbum.sendPostRequest(Urls.ThemePhotoAlbum, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                staggeredLoadGridView.setVisibility(View.VISIBLE);
                themedetail_network.setVisibility(View.GONE);
                staggeredLoadGridView.onRefreshComplete();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if (jsonObject.optInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        if (jsonObject != null) {
                            JSONObject activity_info = jsonObject.optJSONObject("activity_info");
                            photo_source_type = activity_info.optString("photo_source_type");
                            //封装活动信息
                            shakeThemeInfo = new ShakeThemeInfo();
                            shakeThemeInfo.setAi_id(ai_id);
                            shakeThemeInfo.setActivity_name(acname);
                            shakeThemeInfo.setCat_id(activity_info.optString("cat_id"));
                            shakeThemeInfo.setTheme_name(activity_info.optString("theme_name"));
                            shakeThemeInfo.setLocation_type(activity_info.optString("location_type"));
                            shakeThemeInfo.setPlace_name(activity_info.optString("place_name"));
                            shakeThemeInfo.setProvince(activity_info.optString("province"));
                            shakeThemeInfo.setCity(activity_info.optString("city"));
                            shakeThemeInfo.setCounty(activity_info.optString("county"));
                            shakeThemeInfo.setAddress(activity_info.optString("address"));
                            is_join = activity_info.optString("is_join");
                            JSONArray jsonArray1 = activity_info.optJSONArray("key_cencent");
                            if (jsonArray1 != null) {
                                int l2 = jsonArray1.length();
                                String[] key_cencent = new String[l2];
                                for (int j = 0; j < l2; j++) {
                                    key_cencent[j] = jsonArray1.getString(j);
                                }
                                shakeThemeInfo.setKey_cencent(key_cencent);
                            }
                            shakeThemeInfo.setLongitude(activity_info.optString("longitude"));
                            shakeThemeInfo.setLatitude(activity_info.optString("latitude"));
                            String left_target = activity_info.optString("left_target");
                            String left_time = activity_info.optString("left_time");
                            //加载界面
                            if ("1".equals(is_join)) {//参与的活动
                                themedetail_img.setVisibility(View.VISIBLE);
                                themedetail_123.setVisibility(View.VISIBLE);
                                themedetail_my123.setVisibility(View.VISIBLE);
                                themedetail_img_layout.setVisibility(View.VISIBLE);
                                themedetail_item2.setVisibility(View.GONE);
                                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) themedetail_top_s_line.getLayoutParams();
                                lp.rightMargin = Tools.dipToPx(ThemeDetailActivity.this, 96);
                                themedetail_top_s_line.setLayoutParams(lp);
                                if ("0".equals(left_target)) {
                                    themedetail_item1.setText("人数已满");
                                } else {
                                    Spannable spannable = new SpannableString("还需邀请 " + left_target + " 人"
                                            + "\n还剩 " + left_time + " 天");
                                    spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), 4, 4 + left_target.length() + 1,
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new AbsoluteSizeSpan(Tools.spToPx(ThemeDetailActivity.this, 14)), 4, 4 + left_target.length() + 1,
                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    spannable.setSpan(new StyleSpan(Typeface.BOLD), 4, 4 + left_target.length() + 1,
                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), 5 + left_target.length() + 5
                                            , spannable.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new AbsoluteSizeSpan(Tools.spToPx(ThemeDetailActivity.this, 14)), 5 + left_target.length() + 5
                                            , spannable.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    spannable.setSpan(new StyleSpan(Typeface.BOLD), 5 + left_target.length() + 5
                                            , spannable.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    themedetail_item1.setText(spannable);
                                }
                            } else {
                                Spannable spannable = new SpannableString("还需邀请 " + left_target + " 人");
                                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), 4, spannable.length() - 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new AbsoluteSizeSpan(Tools.spToPx(ThemeDetailActivity.this, 14)), 4, spannable.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                spannable.setSpan(new StyleSpan(Typeface.BOLD), 4, spannable.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                themedetail_item1.setText(spannable);
                                Spannable spannable1 = new SpannableString("还剩 " + left_time + " 天");
                                spannable1.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), 2, spannable1.length() - 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable1.setSpan(new AbsoluteSizeSpan(Tools.spToPx(ThemeDetailActivity.this, 14)), 2, spannable1.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                spannable1.setSpan(new StyleSpan(Typeface.BOLD), 2, spannable1.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                themedetail_item2.setText(spannable1);
                            }
                            if ("0".equals(left_target) || "0".equals(left_time)) {//此时活动处于结束阶段
                                themedetail_1_ask.setBackgroundResource(R.drawable.bg_r_11_col_ffececec);
                                themedetail_1_sponsor.setBackgroundResource(R.drawable.bg_r_11_col_ffececec);
                                themedetail_button.setBackgroundColor(0xFFECECEC);
                                findViewById(R.id.themedetail_button_img).setVisibility(View.GONE);
                                findViewById(R.id.themedetail_button_txt).setVisibility(View.VISIBLE);
                                themedetail_button.setVisibility(View.VISIBLE);
                                themedetail_1_ask.setOnClickListener(null);
                                themedetail_1_sponsor.setOnClickListener(null);
                                themedetail_button.setOnClickListener(null);
                                themedetail_title.hideIllustrate();
                            } else {
                                themedetail_button.setBackgroundColor(0xFFF65D57);
                                findViewById(R.id.themedetail_button_img).setVisibility(View.VISIBLE);
                                findViewById(R.id.themedetail_button_txt).setVisibility(View.GONE);
                                themedetail_button.setVisibility(View.VISIBLE);
                                themedetail_button.setOnClickListener(ThemeDetailActivity.this);
                                themedetail_1_ask.setOnClickListener(ThemeDetailActivity.this);
                                themedetail_1_sponsor.setOnClickListener(ThemeDetailActivity.this);
                                themedetail_title.setIllustrate(R.mipmap.share2, new AppTitle.OnExitClickForAppTitle() {
                                    public void onExit() {
                                        UMShareDialog.showDialog(ThemeDetailActivity.this, true, new UMShareDialog.UMShareListener() {
                                            public void shareOnclick(int type) {
                                                String webUrl = Urls.ShareActivityIndex + "?&ai_id=" + ai_id + "&type=2";
                                                MyUMShareUtils.umShare_shakephoto(ThemeDetailActivity.this, type, webUrl);
                                            }
                                        }, true);
                                    }
                                });
                            }
                            String prize = activity_info.optString("prize");
                            if (!TextUtils.isEmpty(prize)) {
                                if (prize.length() > 7) {
                                    int width = Tools.dipToPx(ThemeDetailActivity.this, 102);
                                    TextPaint paint = themedetail_item3.getPaint();
                                    String result = prize.substring(0, 7);
                                    int length = prize.length();
                                    for (int i = 0; i < length; i++) {
                                        int them = (int) paint.measureText(result);
                                        if (them < width) {
                                            result = result + prize.substring(i + 7, i + 8);
                                        } else {
                                            break;
                                        }
                                    }
                                    if (result.length() < prize.length()) {
                                        prize = result + "...";
                                    } else {
                                        prize = result;
                                    }
                                }
                                Spannable spannable2 = new SpannableString("集赞大奖：[" + prize + "]");
                                spannable2.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), 5, spannable2.length(),
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable2.setSpan(new StyleSpan(Typeface.BOLD), 5, spannable2.length(),
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                themedetail_item3.setText(spannable2);
                            } else {
                                themedetail_item3.setVisibility(View.GONE);
                            }
                            //红包总金额
                            String sponsor_money = activity_info.optString("sponsor_money");
                            int idx = 0;
                            if ((idx = sponsor_money.indexOf(".")) != -1) {
                                sponsor_money = sponsor_money.substring(0, idx);
                            }
                            if (!TextUtils.isEmpty(sponsor_money)) {
                                Spannable spannable3 = new SpannableString("红包总金额 " + sponsor_money + "元");
                                spannable3.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), 6, spannable3.length(),
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable3.setSpan(new AbsoluteSizeSpan(Tools.spToPx(ThemeDetailActivity.this, 14)), 5, spannable3.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                spannable3.setSpan(new StyleSpan(Typeface.BOLD), 5, spannable3.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                themedetail_item4.setText(spannable3);
                            } else {
                                themedetail_item4.setText("");
                            }
                            JSONArray photo_list = jsonObject.optJSONArray("photo_list");
                            //图片列表
                            if (photo_list != null) {
                                if (page == 1) {
                                    list.clear();
                                    myAdapter.notifyDataSetChanged();
                                }
                                int length = photo_list.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject temp = photo_list.getJSONObject(i);
                                    ThemeDetailInfo themeDetailInfo = new ThemeDetailInfo();
                                    themeDetailInfo.setUser_name(temp.optString("user_name"));
                                    themeDetailInfo.setFi_id(temp.optString("fi_id"));
                                    themeDetailInfo.setAd_links(temp.optString("ad_links"));
                                    themeDetailInfo.setComment_num(temp.optString("comment_num"));
                                    themeDetailInfo.setCreate_time(temp.optString("create_time"));
                                    String photo_url = temp.optString("file_url");
                                    if (!(photo_url.startsWith("http://") || photo_url.startsWith("https://"))) {
                                        photo_url = Urls.Endpoint3 + photo_url;
                                    }
                                    themeDetailInfo.setFile_url(photo_url);
                                    themeDetailInfo.setIs_advertisement(temp.optString("is_advertisement"));
                                    themeDetailInfo.setKey_concent(temp.optString("key_concent"));
                                    themeDetailInfo.setPraise_num(temp.optString("praise_num"));
                                    themeDetailInfo.setPraise_state(temp.optString("praise_state"));
                                    themeDetailInfo.setRanking(temp.optString("ranking"));
                                    themeDetailInfo.setSai_id(temp.optString("sai_id"));
                                    themeDetailInfo.setShare_num(temp.optString("share_num"));
                                    themeDetailInfo.setUser_mobile(temp.optString("user_mobile"));
                                    themeDetailInfo.setUser_img(temp.optString("user_img"));
                                    themeDetailInfo.setComment_state(temp.optString("comment_state"));
                                    list.add(themeDetailInfo);
                                }
                                if (length == 0) {
                                    if (page == 1) {
                                        staggeredLoadGridView.setVisibility(View.GONE);
                                        themedetail_network.setVisibility(View.VISIBLE);
                                        themedetail_network.NoSearch("还没有图片呢～点击刷新");
                                        themedetail_network.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                themedetail_network.NoSearch("正在刷新...");
                                                page = 1;
                                                getData();
                                                themedetail_network.setOnClickListener(null);
                                            }
                                        });
                                    } else {
                                        staggeredLoadGridView.onRefreshComplete();
                                        staggeredLoadGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        staggeredLoadGridView.onRefreshComplete();
                                    }
                                }
                                myAdapter.notifyDataSetChanged();
                            } else {
                                if (page == 1) {
                                    staggeredLoadGridView.setVisibility(View.GONE);
                                    themedetail_network.setVisibility(View.VISIBLE);
                                    themedetail_network.NoSearch("还没有图片呢～点击刷新");
                                    themedetail_network.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            themedetail_network.NoSearch("正在刷新...");
                                            page = 1;
                                            getData();
                                            themedetail_network.setOnClickListener(null);
                                        }
                                    });
                                }
                            }
                            if ("1".equals(is_join)) {//参与的活动
                                //我的排名
                                String my_ranking = activity_info.optString("my_ranking");
                                if (!TextUtils.isEmpty(my_ranking)) {
                                    Spannable spannable4 = new SpannableString("我的排名\n" + my_ranking);
                                    spannable4.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), 5, spannable4.length(),
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable4.setSpan(new AbsoluteSizeSpan(Tools.spToPx(ThemeDetailActivity.this, 14)), 5, spannable4.length(),
                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    spannable4.setSpan(new StyleSpan(Typeface.BOLD), 5, spannable4.length(),
                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    themedetail_my123.setText(spannable4);
                                } else {
                                    themedetail_my123.setText("");
                                }
                                if ("1".equals(jsonObject.optString("have_prize")) && !ConfirmDialog.isShow()) {//弹出领奖提示框
                                    JSONObject prize_info = jsonObject.optJSONObject("prize_info");
                                    String prize_image_url = prize_info.optString("prize_image_url");
                                    if (!(prize_image_url.startsWith("http://") || prize_image_url.startsWith("https://"))) {
                                        prize_image_url = Urls.Endpoint3 + prize_image_url;
                                    }
                                    String msg1 = "恭喜您获得由“";
                                    String msg2 = "”提供的红包奖励“";
                                    String msg3 = "”";
                                    final String sponsor_name = prize_info.optString("sponsor_name");
                                    String string = msg1 + sponsor_name + msg2;
                                    int index = string.length() - 1;
                                    final String prize_name = prize_info.optString("prize_name");
                                    String prize_type = prize_info.optString("prize_type");
                                    if ("1".equals(prize_type)) {
                                        prize_type = "一等奖";
                                    } else if ("2".equals(prize_type)) {
                                        prize_type = "二等奖";
                                    } else if ("3".equals(prize_type)) {
                                        prize_type = "三等奖";
                                    }
                                    string = string + prize_name + msg3;
                                    Spannable msg = new SpannableString(string);
                                    msg.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), index, msg.length() - 1,
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    final String finalPrize_image_url = prize_image_url;
                                    final String finalPrize_type = prize_type;
                                    ConfirmDialog.showDialog(ThemeDetailActivity.this, prize_type, 2,
                                            0xFFF65D57, msg, 0, "", 0, "领取奖励", 0, null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                                public void leftClick(Object object) {
                                                }

                                                public void rightClick(Object object) {
                                                    Intent intent = new Intent(ThemeDetailActivity.this, MailAddressActivity.class);
                                                    intent.putExtra("ai_id", ai_id);
                                                    intent.putExtra("prize_image_url", finalPrize_image_url);
                                                    intent.putExtra("sponsor_name", sponsor_name);
                                                    intent.putExtra("prize_name", prize_name);
                                                    intent.putExtra("prize_type", finalPrize_type);
                                                    intent.putExtra("acname", acname);
                                                    startActivity(intent);
                                                }
                                            }).settingShowImage(ThemeDetailActivity.this, prize_image_url).goneLeft();
                                }
                                //我分享的照片数量
                                String photo_num = activity_info.optString("photo_num");
                                if (activity_info.isNull("photo_num") || TextUtils.isEmpty(photo_num)) {
                                    photo_num = "0";
                                }
                                String photo_url = activity_info.optString("photo_url");
                                if (!(photo_url.startsWith("http://") || photo_url.startsWith("https://"))) {
                                    photo_url = Urls.Endpoint3 + photo_url;
                                }
                                imageLoader.DisplayImage(photo_url, themedetail_img);
                                themedetail_img_number.setText(photo_num);
                                themedetail_123.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ThemeDetailActivity.this, RankingListActivity.class);
                                        intent.putExtra("acname", acname);
                                        intent.putExtra("ai_id", ai_id);
                                        startActivity(intent);
                                    }
                                });
                                themedetail_img_layout.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ThemeDetailActivity.this, MyPictureActivity.class);
                                        intent.putExtra("ai_id", ai_id);
                                        intent.putExtra("ac_name", acname);
                                        startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            staggeredLoadGridView.setVisibility(View.GONE);
                            themedetail_network.setVisibility(View.VISIBLE);
                            themedetail_network.NoSearch("还没有图片呢～点击刷新");
                            themedetail_network.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    themedetail_network.NoSearch("正在刷新...");
                                    page = 1;
                                    getData();
                                    themedetail_network.setOnClickListener(null);
                                }
                            });
                        }
                    } else {
                        Tools.showToast(ThemeDetailActivity.this, jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ThemeDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                staggeredLoadGridView.onRefreshComplete();
                staggeredLoadGridView.setVisibility(View.GONE);
                themedetail_network.setVisibility(View.VISIBLE);
                themedetail_network.NoNetwork(getResources().getString(R.string.network_fail) + "点击重试");
                themedetail_network.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        themedetail_network.NoNetwork("正在重试...");
                        page = 1;
                        getData();
                        themedetail_network.setOnClickListener(null);
                    }
                });
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.themedetail_1_sponsor: {//赞助
                if (TextUtils.isEmpty(AppInfo.getKey(ThemeDetailActivity.this))) {
                    ConfirmDialog.showDialog(ThemeDetailActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    Intent intent = new Intent(ThemeDetailActivity.this, IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                Intent intent = new Intent(ThemeDetailActivity.this, SponsorActivity.class);
                intent.putExtra("ai_id", ai_id);
                startActivity(intent);
            }
            break;
            case R.id.themedetail_1_ask: {//邀请
                if (TextUtils.isEmpty(AppInfo.getKey(ThemeDetailActivity.this))) {
                    ConfirmDialog.showDialog(ThemeDetailActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    Intent intent = new Intent(ThemeDetailActivity.this, IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                UMShareDialog.showDialog(ThemeDetailActivity.this, false, new UMShareDialog.UMShareListener() {
                    public void shareOnclick(int type) {
                        String webUrl = Urls.ShareActivityIndex + "?&ai_id=" + ai_id + "&type=2";
                        MyUMShareUtils.umShare_shakephoto(ThemeDetailActivity.this, type, webUrl);
                    }
                }, true);
            }
            break;
            case R.id.themedetail_rank: {//排名
                sort_type = 1;
                page = 1;
                themedetail_rank.setTextColor(0xFFF65D57);
                themedetail_rank_line.setVisibility(View.VISIBLE);
                themedetail_time.setTextColor(0xFFA0A0A0);
                themedetail_time_line.setVisibility(View.GONE);
                themedetail_network.setVisibility(View.GONE);
                themedetail_time_r.setImageResource(R.mipmap.themedetail_timer);
                getData();
            }
            break;
            case R.id.themedetail_item3: {
                Intent intent = new Intent(ThemeDetailActivity.this, AwardsShowActivity.class);
                intent.putExtra("ai_id", ai_id);
                startActivity(intent);
            }
            break;
            case R.id.themedetail_time_r:
            case R.id.themedetail_time: {//时间
                if (sort_type == 2 || sort_type == 5) {
                    if (sort_type == 5) {
                        sort_type = 2;
                        themedetail_time_r.setImageResource(R.mipmap.themedetail_dx);
                    } else {
                        sort_type = 5;
                        themedetail_time_r.setImageResource(R.mipmap.themedetail_zx);
                    }
                } else {
                    sort_type = 2;
                    themedetail_time_r.setImageResource(R.mipmap.themedetail_dx);
                }
                page = 1;
                themedetail_rank.setTextColor(0xFFA0A0A0);
                themedetail_rank_line.setVisibility(View.GONE);
                themedetail_time.setTextColor(0xFFF65D57);
                themedetail_time_line.setVisibility(View.VISIBLE);
                themedetail_network.setVisibility(View.GONE);
                getData();
            }
            break;
            case R.id.themedetail_button: {//参与活动
                if (TextUtils.isEmpty(AppInfo.getKey(ThemeDetailActivity.this))) {
                    ConfirmDialog.showDialog(ThemeDetailActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    Intent intent = new Intent(ThemeDetailActivity.this, IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                if (!TextUtils.isEmpty(photo_source_type)) {
                    SelectPhotoDialog selectPhotoDialog = SelectPhotoDialog.showPhotoSelecterAll(ThemeDetailActivity.this, new View.OnClickListener() {
                        public void onClick(View v) {
                            SelectPhotoDialog.dissmisDialog();
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
                        }
                    }, new View.OnClickListener() {
                        public void onClick(View v) {
                            SelectPhotoDialog.dissmisDialog();
                            Intent intent = new Intent(ThemeDetailActivity.this, LeftActivity.class);
                            intent.putExtra("dai_id", "");
                            intent.putExtra("shakeThemeInfo", shakeThemeInfo);
                            startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        public void onClick(View v) {
                            SelectPhotoDialog.dissmisDialog();
                            Intent intent = new Intent(ThemeDetailActivity.this, ShakephotoActivity.class);
                            intent.putExtra("shakeThemeInfo", shakeThemeInfo);
                            startActivity(intent);
                        }
                    }).initShowStr("手机本地相册", "甩吧相册", "拍照");
                    if ("1".equals(photo_source_type)) {
                        selectPhotoDialog.goneItem1().goneItem2();
                    } else if ("2".equals(photo_source_type)) {
                        selectPhotoDialog.goneItem1();
                    }
                }
            }
            break;
        }
    }

    private class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                viewHold = new ViewHold();
                convertView = Tools.loadLayout(ThemeDetailActivity.this, R.layout.item_themedetail);
                viewHold.item_themedetail = (ThemeDetailItem) convertView.findViewById(R.id.item_themedetail);
                viewHold.item_themedetail_b1_txt = (TextView) convertView.findViewById(R.id.item_themedetail_b1_txt);
                viewHold.item_themedetail_b3_txt = (TextView) convertView.findViewById(R.id.item_themedetail_b3_txt);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            ThemeDetailInfo themeDetailInfo = list.get(position);
            if ("1".equals(themeDetailInfo.getIs_advertisement())) {
                viewHold.item_themedetail.setHeightRatio(0.5);
                imageLoader.setGif(true).DisplayImage(themeDetailInfo.getFile_url() + "?x-oss-process=image/resize,l_350",
                        viewHold.item_themedetail, -1);
            } else {
                viewHold.item_themedetail.setHeightRatio(1);
                imageLoader.setGif(true).DisplayImage(themeDetailInfo.getFile_url() + "?x-oss-process=image/resize,l_250",
                        viewHold.item_themedetail);
//                imageLoader.setGif(true).DisplayImage("http://ouye.oss-cn-hangzhou.aliyuncs.com/SMVTT/giftest.gif?x-oss-process=image/resize,l_250", viewHold.item_themedetail);
            }
            viewHold.item_themedetail_b1_txt.setText(themeDetailInfo.getPraise_num());
            viewHold.item_themedetail_b3_txt.setText(themeDetailInfo.getComment_num());
            return convertView;
        }
    }

    private class ViewHold {
        ThemeDetailItem item_themedetail;
        TextView item_themedetail_b1_txt, item_themedetail_b2_txt, item_themedetail_b3_txt;
    }
}
