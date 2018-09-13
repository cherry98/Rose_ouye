package com.orange.oy.activity.shakephoto_316;

import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.LocalAlbumAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.shakephoto.LocalPhotoInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 地图页面->点击图片 查看图片 V3.16
 */
public class ConsultPhotoActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, LocalAlbumAdapter.OnItemCheckListener {
    AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.consultphoto_title);
        appTitle.settingName("地点");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle1 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.hideIllustrate();
            appTitle.settingExit("完成", onExitClickForAppTitle2);
            if (localAlbumAdapter != null) {
                localAlbumAdapter.setShow(true);
                localAlbumAdapter.notifyDataSetChanged();
            }
        }
    };
    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle2 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.hideExit();
            appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
            if (localAlbumAdapter != null) {
                localAlbumAdapter.setShow(false);
                localAlbumAdapter.notifyDataSetChanged();
                if (file_id.isEmpty()) {
                    Tools.showToast(ConsultPhotoActivity.this, "请至少选择一张图片");
                } else {
                    delPhoto();
                }
            }
        }
    };

    private void initNetwork() {
        activityPhotoAlbum = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ConsultPhotoActivity.this));
                params.put("token", Tools.getToken());
                params.put("fi_ids", fi_ids);
                return params;
            }
        };
        delPhoto = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ConsultPhotoActivity.this));
                params.put("token", Tools.getToken());
                String fi_id = file_id.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "");
                fi_id = fi_id.replaceAll(" ", "");
                params.put("fi_id", fi_id);
                return params;
            }
        };
        delPhoto.setIsShowDialog(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private NetworkConnection activityPhotoAlbum, delPhoto;
    private String fi_ids;
    private ArrayList<LocalPhotoInfo> list_ablum;
    private LocalAlbumAdapter localAlbumAdapter;
    private PullToRefreshListView consultphoto_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult_photo);
        list_ablum = new ArrayList<>();
        initTitle();
        initNetwork();
        fi_ids = getIntent().getStringExtra("fi_ids");
        consultphoto_listview = (PullToRefreshListView) findViewById(R.id.consultphoto_listview);
        getData();
        localAlbumAdapter = new LocalAlbumAdapter(this, list_ablum);
        localAlbumAdapter.setOnItemCheckListener(this);
        consultphoto_listview.setAdapter(localAlbumAdapter);
        consultphoto_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        activityPhotoAlbum.sendPostRequest(Urls.ActivityPhotoAlbum, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list_ablum.isEmpty()) {
                            list_ablum.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                LocalPhotoInfo localPhotoInfo = new LocalPhotoInfo();
                                localPhotoInfo.setCreate_time(object.getString("create_time"));
                                localPhotoInfo.setArea(object.getString("area"));
                                localPhotoInfo.setShowMap(false);
                                localPhotoInfo.setPhoto_num(object.getString("photo_num"));
                                JSONArray jsonArray2 = object.optJSONArray("photo_list");
                                ArrayList<LocalPhotoInfo.PhotoListBean> photo_list = new ArrayList<>();
                                if (jsonArray2 != null) {
                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                        JSONObject object2 = jsonArray2.optJSONObject(j);
                                        LocalPhotoInfo.PhotoListBean photoListBean = new LocalPhotoInfo.PhotoListBean();
                                        photoListBean.setFi_id(object2.getString("fi_id"));
                                        photoListBean.setAitivity_name(object2.getString("aitivity_name"));
                                        photoListBean.setFile_url(object2.getString("file_url"));
                                        photoListBean.setProvince(object2.getString("province"));
                                        photoListBean.setCity(object2.getString("city"));
                                        photoListBean.setCounty(object2.getString("county"));
                                        photoListBean.setAddress(object2.getString("address"));
                                        photoListBean.setCreate_time(object2.getString("create_time"));
                                        photoListBean.setLongitude(object2.getString("longitude"));
                                        photoListBean.setLatitude(object2.getString("latitude"));
                                        photoListBean.setKey_concent(object2.getString("key_concent"));
                                        photo_list.add(photoListBean);
                                    }
                                }
                                localPhotoInfo.setPhoto_list(photo_list);
                                list_ablum.add(localPhotoInfo);
                            }
                            if (localAlbumAdapter != null) {
                                localAlbumAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(ConsultPhotoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ConsultPhotoActivity.this, getResources().getString(R.string.network_error));
                }
                consultphoto_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ConsultPhotoActivity.this, getResources().getString(R.string.network_volleyerror));
                consultphoto_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onBack() {
//        ShakeAlbumActivity.isRefresh1= true;
        baseFinish();
    }

    /**
     * 拼接需要删除的file_url
     *
     * @param photoListBean
     */
    private ArrayList<String> file_id = new ArrayList<>();

    @Override
    public void onItemCheck(LocalPhotoInfo.PhotoListBean photoListBean) {
        String fi_id = photoListBean.getFi_id();
        if (photoListBean.isCheck()) {
            if (!file_id.contains(fi_id)) {
                file_id.add(fi_id);
            }
        } else {
            if (file_id.contains(fi_id)) {
                file_id.remove(fi_id);
            }
        }
    }

    private void delPhoto() {
        delPhoto.sendPostRequest(Urls.DelPhoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(ConsultPhotoActivity.this, "删除成功");
                        getData();
                    } else {
                        Tools.showToast(ConsultPhotoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ConsultPhotoActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ConsultPhotoActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

}
