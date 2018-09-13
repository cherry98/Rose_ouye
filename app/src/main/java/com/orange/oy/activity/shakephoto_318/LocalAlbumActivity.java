package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_316.ConsultPhotoActivity;
import com.orange.oy.adapter.mycorps_314.LocalAlbumAdapter;
import com.orange.oy.baidmap.ClusterManager;
import com.orange.oy.baidmap.CoordinateTransformUtil;
import com.orange.oy.baidmap.PersonRenderer;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.clusterutil.clustering.Cluster;
import com.orange.oy.clusterutil.projection.Point;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.shakephoto.LocalPhotoInfo;
import com.orange.oy.info.shakephoto.ShakePhotoInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.orange.oy.baidmap.ClusterManager.MAX_DISTANCE_AT_ZOOM;
import static com.orange.oy.baidmap.NonHierarchicalDistanceBasedAlgorithm.PROJECTION;

/**
 * 甩图本地相册 V3.18
 */
public class LocalAlbumActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, LocalAlbumAdapter.
        OnItemCheckListener, AdapterView.OnItemClickListener, View.OnClickListener, BaiduMap.OnMapLoadedCallback {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.localalbum_title);
        appTitle.settingName("本地相册");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activityPhotoAlbum != null) {
            activityPhotoAlbum.stop(Urls.ActivityPhotoAlbum);
        }
        if (delPhoto != null) {
            delPhoto.stop(Urls.DelPhoto);
        }
    }

    private void initNetwork() {
        activityPhotoAlbum = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(LocalAlbumActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        delPhoto = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(LocalAlbumActivity.this));
                params.put("token", Tools.getToken());
                String fi_id = file_id.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "");
                fi_id = fi_id.replaceAll(" ", "");
                params.put("fi_id", fi_id);
                return params;
            }
        };
        delPhoto.setIsShowDialog(true);
        activityPhotoMap = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(LocalAlbumActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        activityPhotoMap.setIsShowDialog(true);
    }

    private LocalAlbumAdapter localAlbumAdapter;
    private PullToRefreshListView localalbum_listview;
    private NetworkConnection delPhoto, activityPhotoMap, activityPhotoAlbum;
    private String total_photo_num;//总照片数量
    private ArrayList<ShakePhotoInfo> list_map = new ArrayList<>();
    private ArrayList<LocalPhotoInfo> list_ablum;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private ClusterManager<ShakePhotoInfo> mClusterManager;
    MapStatus ms;
    private TextView localalbum_listnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_album);
        list_ablum = new ArrayList<>();
        initTitle();
        initNetwork();
        localalbum_listview = (PullToRefreshListView) findViewById(R.id.localalbum_listview);
        localalbum_listnum = (TextView) findViewById(R.id.localalbum_listnum);
        localAlbumAdapter = new LocalAlbumAdapter(this, list_ablum);
        localAlbumAdapter.setOnItemCheckListener(this);
        localalbum_listview.setAdapter(localAlbumAdapter);
        getData();
        localalbum_listview.setOnItemClickListener(this);
        findViewById(R.id.localalbum_list).setOnClickListener(this);
        //地图显示
        mMapView = (MapView) findViewById(R.id.shakealbum_map);
        mMapView.showZoomControls(false);
        mMapView.setLogoPosition(LogoPosition.logoPostionRightBottom);
//        ms = new MapStatus.Builder().target(new LatLng(39.914935, 116.403119)).zoom(4).build();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMaxAndMinZoomLevel(21, 4);
        mBaiduMap.setOnMapLoadedCallback(this);
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));


        mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(false);
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<ShakePhotoInfo>(this, mBaiduMap);
        mClusterManager.setRenderer(new PersonRenderer(this, mBaiduMap, mClusterManager));

        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);
    }

    private void getData() {
        activityPhotoAlbum.sendPostRequest(Urls.ActivityPhotoAlbum, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list_ablum.isEmpty()) {
                            list_ablum.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        total_photo_num = jsonObject.getString("total_photo_num");
                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                LocalPhotoInfo localPhotoInfo = new LocalPhotoInfo();
                                localPhotoInfo.setTotal_photo_num(total_photo_num);
                                localPhotoInfo.setShowMap(true);
                                localPhotoInfo.setCreate_time(object.getString("create_time"));
                                localPhotoInfo.setArea(object.getString("area"));
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
                        Tools.showToast(LocalAlbumActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LocalAlbumActivity.this, getResources().getString(R.string.network_error));
                }
                localalbum_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LocalAlbumActivity.this, getResources().getString(R.string.network_volleyerror));
                localalbum_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onBack() {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (localAlbumAdapter != null) {
            if (localAlbumAdapter.isClick()) {//地图查看
                appTitle.hideIllustrate();
                localalbum_listview.setVisibility(View.GONE);
                findViewById(R.id.localalbum_list_ly).setVisibility(View.VISIBLE);
                getMapData();
            }
            localAlbumAdapter.setClick(false);
        }
    }

    private void getMapData() {
        activityPhotoMap.sendPostRequest(Urls.ActivityPhotoMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list_map.isEmpty()) {
                            list_map.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("photo_list");
                        if (jsonArray != null) {
                            localalbum_listnum.setText(jsonArray.length() + "");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                ShakePhotoInfo shakePhotoInfo = new ShakePhotoInfo();
                                shakePhotoInfo.setFi_id(object.getString("fi_id"));
                                shakePhotoInfo.setAitivity_name(object.getString("aitivity_name"));
                                shakePhotoInfo.setFile_url(object.getString("file_url"));
                                shakePhotoInfo.setProvince(object.getString("province"));
                                shakePhotoInfo.setCity(object.getString("city"));
                                shakePhotoInfo.setCounty(object.getString("county"));
                                shakePhotoInfo.setAddress(object.getString("address"));
                                shakePhotoInfo.setCreate_time(object.getString("create_time"));
                                shakePhotoInfo.setKey_concent(object.getString("key_concent"));
                                shakePhotoInfo.setLatitude(object.getString("latitude"));
                                shakePhotoInfo.setLongitude(object.getString("longitude"));
                                list_map.add(shakePhotoInfo);
                            }
                            initMap();
                        }
                    } else {
                        Tools.showToast(LocalAlbumActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LocalAlbumActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LocalAlbumActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void initMap() {
        location();

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ShakePhotoInfo>() {
            @Override
            public boolean onClusterClick(Cluster<ShakePhotoInfo> cluster) {
                String fi_ids = null;
                for (ShakePhotoInfo shakePhotoInfo : cluster.getItems()) {
                    if (fi_ids == null) {
                        fi_ids = shakePhotoInfo.getFi_id();
                    } else {
                        fi_ids = fi_ids + "," + shakePhotoInfo.getFi_id();
                    }
                }
                Intent intent = new Intent(LocalAlbumActivity.this, ConsultPhotoActivity.class);
                intent.putExtra("fi_ids", fi_ids);
                startActivity(intent);
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ShakePhotoInfo>() {
            @Override
            public boolean onClusterItemClick(ShakePhotoInfo item) {
                Intent intent = new Intent(LocalAlbumActivity.this, ConsultPhotoActivity.class);
                intent.putExtra("fi_ids", item.getFi_id());
                startActivity(intent);
                return false;
            }
        });

        //移动的回调
        mClusterManager.setOnMapStatusChangeFinishListener(new ClusterManager.OnMapStatusChangeFinish() {
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

                markerGetAndSet(mapStatus.zoom, mapStatus.bound);

            }
        });
    }

    //获取屏幕上的点，并且开始计算以及显示
    public void markerGetAndSet(final float zoom, final LatLngBounds visibleBounds) {

        Observable.fromCallable(new Callable<ArrayList<ShakePhotoInfo>>() {

            @Override
            public ArrayList<ShakePhotoInfo> call() {

                final double zoomSpecificSpan = MAX_DISTANCE_AT_ZOOM / Math.pow(2, zoom) / 256;
                //加大搜索的范围 ，重新计算出新的边界 Bounds
                final double halfZoomSpecificSpan = zoomSpecificSpan * 2; //一倍边长的长度（屏幕上的）
                Point northeastP = PROJECTION.toPoint(visibleBounds.northeast); //右上角
                Point southwestP = PROJECTION.toPoint(visibleBounds.southwest); //左下角

                //莫斯托投影，y值越小，纬度越大，x值越大经度越小
                northeastP = new Point(northeastP.x + halfZoomSpecificSpan, northeastP.y - halfZoomSpecificSpan);
                southwestP = new Point(southwestP.x - halfZoomSpecificSpan, southwestP.y + halfZoomSpecificSpan);

                LatLng northeast = PROJECTION.toLatLng(northeastP);
                LatLng southwest = PROJECTION.toLatLng(southwestP);
                LatLngBounds expandVisibleBounds = new LatLngBounds.Builder()
                        .include(northeast).include(southwest).build();

                //右上角的经纬度 wgs 格式
                double[] wgs_northeast = CoordinateTransformUtil.bd09towgs84(expandVisibleBounds.northeast.longitude,
                        expandVisibleBounds.northeast.latitude);
                //左下角的经纬度 WGS 格式，
                double[] wgs_southwest = CoordinateTransformUtil.bd09towgs84(expandVisibleBounds.southwest.longitude,
                        expandVisibleBounds.southwest.latitude);


                //传下去JNI 获取 区域内的相片

                //获取当前屏幕的点，目前是获取sdcard相册的点
                return list_map;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<ArrayList<ShakePhotoInfo>>() {

                    @Override
                    public void call(ArrayList<ShakePhotoInfo> localPictrues) {
                        mClusterManager.clearItems();
                        mClusterManager.addItems(localPictrues);
                        //算法计算聚合，并显示
                        mClusterManager.cluster(zoom, visibleBounds);
                    }
                });
    }

    /**
     * 向地图添加Marker点
     */
    public void location() {
        // 添加Marker点
        Observable.fromCallable(new Callable<List<ShakePhotoInfo>>() {

            @Override
            public List<ShakePhotoInfo> call() {

                return list_map;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<List<ShakePhotoInfo>>() {

                    @Override
                    public void call(List<ShakePhotoInfo> localPictrues) {
                        if (!localPictrues.isEmpty()) {
                            ms = new MapStatus.Builder().target(localPictrues.get(0).getPosition()).zoom(8).build();
                            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
                        }

                    }
                });
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    private void delPhoto() {
        delPhoto.sendPostRequest(Urls.DelPhoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(LocalAlbumActivity.this, "删除成功");
                        getData();
                    } else {
                        Tools.showToast(LocalAlbumActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LocalAlbumActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LocalAlbumActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.localalbum_list: {//列表显示
                if (!file_id.isEmpty()) {
                    file_id.clear();
                }
                localalbum_listview.setVisibility(View.VISIBLE);
                appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
            }
            break;
        }
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
                if (!file_id.isEmpty()) {
                    delPhoto();
                }
            }
        }
    };
}
