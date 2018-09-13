package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.LargeImageInfo;
import com.orange.oy.info.shakephoto.RankingListInfo;
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
 * Created by Administrator on 2018/7/27.
 * 我的照片
 */

public class MyPictureActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private StaggeredLoadGridView mypicture_gridview;
    private NetworkView mypicture_networkview;
    private NetworkConnection joinActivityPhotoAlbum, delPhoto;
    private String ai_id;
    private ImageLoader imageLoader;
    private ArrayList<RankingListInfo> list = new ArrayList<>();
    private MyAdapter rankingDetailAdapter;

    private void initNetwork() {
        joinActivityPhotoAlbum = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                String usermobile = AppInfo.getName(MyPictureActivity.this);
                params.put("usermobile", usermobile);
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                params.put("join_usermobile", usermobile);
                return params;
            }
        };
        delPhoto = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MyPictureActivity.this));
                params.put("token", Tools.getToken());
                params.put("fi_id", fi_ids);
                return params;
            }
        };
        delPhoto.setIsShowDialog(true);
    }

    protected void onStop() {
        super.onStop();
        if (joinActivityPhotoAlbum != null) {
            joinActivityPhotoAlbum.stop(Urls.JoinActivityPhotoAlbum);
        }
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle1 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.hideIllustrate();
            appTitle.settingExit("完成", onExitClickForAppTitle2);
            if (list != null) {
                for (RankingListInfo rankingListInfo : list) {
                    rankingListInfo.setSelect(false);
                }
            }
            if (rankingDetailAdapter != null) {
                isShowSel = true;
                rankingDetailAdapter.notifyDataSetChanged();
            }
        }
    };
    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle2 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.hideExit();
            appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
            fi_ids = "";
            if (rankingDetailAdapter != null) {
                isShowSel = false;
                rankingDetailAdapter.notifyDataSetChanged();
                for (RankingListInfo rankingListInfo : list) {
                    if (rankingListInfo.isSelect()) {
                        if (TextUtils.isEmpty(fi_ids)) {
                            fi_ids = rankingListInfo.getFi_id();
                        } else {
                            fi_ids = fi_ids + "," + rankingListInfo.getFi_id();
                        }
                    }
                }
                if (!TextUtils.isEmpty(fi_ids)) {
                    delPhoto();
                }
            }
        }
    };
    private String fi_ids = "";
    private AppTitle appTitle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypicture);
        imageLoader = new ImageLoader(this);
        ai_id = getIntent().getStringExtra("ai_id");
        appTitle = (AppTitle) findViewById(R.id.mypicture_title);
        appTitle.settingName(getIntent().getStringExtra("ac_name") + "");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
        initNetwork();
        mypicture_gridview = (StaggeredLoadGridView) findViewById(R.id.mypicture_gridview);
        mypicture_networkview = (NetworkView) findViewById(R.id.mypicture_networkview);
        mypicture_gridview.setMode(PullToRefreshBase.Mode.DISABLED);
        rankingDetailAdapter = new MyAdapter();
        mypicture_gridview.setAdapter(rankingDetailAdapter);
        mypicture_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<LargeImageInfo> largeImageInfos = new ArrayList<>();
                if (isShowSel) {
                    RankingListInfo rankingListInfo = list.get(position);
                    rankingListInfo.setSelect(!rankingListInfo.isSelect());
                    rankingDetailAdapter.notifyDataSetChanged();
                } else {
                    for (RankingListInfo themeDetailInfo : list) {
                        LargeImageInfo largeImageInfo = new LargeImageInfo();
                        largeImageInfo.setAi_id(ai_id);
                        largeImageInfo.setFi_id(themeDetailInfo.getFi_id());
                        largeImageInfo.setShare_num(themeDetailInfo.getShare_num());
                        largeImageInfo.setComment_num(themeDetailInfo.getComment_num());
                        largeImageInfo.setFile_url(themeDetailInfo.getFile_url());
                        largeImageInfo.setIs_advertisement("0");
                        largeImageInfo.setPraise_num(themeDetailInfo.getPraise_num());
                        largeImageInfo.setPraise_state(themeDetailInfo.getPraise_state());
                        largeImageInfo.setUser_img(themeDetailInfo.getUser_img());
                        largeImageInfo.setUser_mobile(themeDetailInfo.getUser_mobile());
                        largeImageInfo.setComment_state(themeDetailInfo.getComment_state());
                        largeImageInfos.add(largeImageInfo);
                    }
                    Intent intent = new Intent(MyPictureActivity.this, LargeImageActivity.class);
                    intent.putExtra("list", largeImageInfos);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        joinActivityPhotoAlbum.sendPostRequest(Urls.JoinActivityPhotoAlbum, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                mypicture_gridview.setVisibility(View.VISIBLE);
                mypicture_networkview.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!list.isEmpty()) {
                        list.clear();
                        if (rankingDetailAdapter != null) {
                            rankingDetailAdapter.notifyDataSetChanged();
                        }
                    }
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("photo_list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                RankingListInfo rankingListInfo = new RankingListInfo();
                                rankingListInfo.setFi_id(object.getString("fi_id"));
                                String photo_url = object.getString("file_url");
                                if (!(photo_url.startsWith("http://") || photo_url.startsWith("https://"))) {
                                    photo_url = Urls.Endpoint3 + photo_url;
                                }
                                rankingListInfo.setFile_url(photo_url);
                                rankingListInfo.setCreate_time(object.getString("create_time"));
                                rankingListInfo.setKey_concent(object.getString("key_concent"));
                                rankingListInfo.setRanking(object.getString("ranking"));
                                rankingListInfo.setComment_num(object.getString("comment_num"));
                                rankingListInfo.setPraise_num(object.getString("praise_num"));
                                rankingListInfo.setShare_num(object.getString("share_num"));
//                                rankingListInfo.setUser_name(object.getString("user_name"));
                                rankingListInfo.setUser_img(object.getString("user_img"));
                                rankingListInfo.setPraise_state(object.getString("praise_state"));
                                rankingListInfo.setUser_mobile(object.getString("user_mobile"));
                                rankingListInfo.setComment_state(object.getString("comment_state"));
                                list.add(rankingListInfo);
                            }
                            if (rankingDetailAdapter != null) {
                                rankingDetailAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(MyPictureActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyPictureActivity.this, getResources().getString(R.string.network_error));
                }
                mypicture_gridview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                mypicture_gridview.onRefreshComplete();
                mypicture_gridview.setVisibility(View.GONE);
                mypicture_networkview.setVisibility(View.VISIBLE);
                mypicture_networkview.NoNetwork(getResources().getString(R.string.network_fail) + "点击重试");
                mypicture_networkview.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mypicture_networkview.NoNetwork("正在重试...");
                        getData();
                        mypicture_networkview.setOnClickListener(null);
                    }
                });
            }
        });
    }

    private void delPhoto() {
        delPhoto.sendPostRequest(Urls.DelPhoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(MyPictureActivity.this, "删除成功");
                        if (!list.isEmpty()) {
                            list.clear();
                            if (rankingDetailAdapter != null) {
                                rankingDetailAdapter.notifyDataSetChanged();
                            }
                        }
                        getData();
                    } else {
                        Tools.showToast(MyPictureActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyPictureActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyPictureActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    public void onBack() {
        baseFinish();
    }

    private boolean isShowSel = false;

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
                convertView = Tools.loadLayout(MyPictureActivity.this, R.layout.item_themedetail);
                viewHold.item_themedetail = (ThemeDetailItem) convertView.findViewById(R.id.item_themedetail);
                viewHold.item_themedetail_b1_txt = (TextView) convertView.findViewById(R.id.item_themedetail_b1_txt);
                viewHold.item_themedetail_b3_txt = (TextView) convertView.findViewById(R.id.item_themedetail_b3_txt);
                viewHold.item_themedetail_sel = (ImageView) convertView.findViewById(R.id.item_themedetail_sel);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            RankingListInfo themeDetailInfo = list.get(position);
            viewHold.item_themedetail.setHeightRatio(1);
            imageLoader.DisplayImage(themeDetailInfo.getFile_url() + "?x-oss-process=image/resize,l_200",
                    viewHold.item_themedetail);
            viewHold.item_themedetail_b1_txt.setText(themeDetailInfo.getPraise_num());
            viewHold.item_themedetail_b3_txt.setText(themeDetailInfo.getComment_num());
            if (isShowSel) {
                viewHold.item_themedetail_sel.setVisibility(View.VISIBLE);
                if (themeDetailInfo.isSelect()) {
                    viewHold.item_themedetail_sel.setImageResource(R.mipmap.image_check);
                } else {
                    viewHold.item_themedetail_sel.setImageResource(R.mipmap.image_uncheck);
                }
            } else {
                viewHold.item_themedetail_sel.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    private class ViewHold {
        ThemeDetailItem item_themedetail;
        TextView item_themedetail_b1_txt, item_themedetail_b2_txt, item_themedetail_b3_txt;
        ImageView item_themedetail_sel;
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
