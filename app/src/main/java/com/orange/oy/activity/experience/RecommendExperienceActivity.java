package com.orange.oy.activity.experience;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.RecommendAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.LocationInfo;
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
 * 体验项目---推荐体验页~~~
 */
public class RecommendExperienceActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.recommendep_title);
        appTitle.settingName("推荐体验");
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            @Override
            public void onBack() {
                ConfirmDialog.showDialog(RecommendExperienceActivity.this, "您的体验任务已完成，确定返回首页吗？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                    @Override
                    public void leftClick(Object object) {

                    }

                    @Override
                    public void rightClick(Object object) {
                        baseFinish();
                    }
                });
            }
        }, "首页");
    }

    @Override
    public void onBackPressed() {
        ConfirmDialog.showDialog(RecommendExperienceActivity.this, "您的体验任务已完成，确定返回首页吗？", true, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {

            }

            @Override
            public void rightClick(Object object) {
                RecommendExperienceActivity.super.onBackPressed();
            }
        });
    }

    private void initNetworkConnection() {
        recommendOutletList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(RecommendExperienceActivity.this));
                params.put("projectid", projectid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("page", page + "");
                return params;
            }
        };
        rob = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(RecommendExperienceActivity.this));
                params.put("storeid", storeid);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        rob.setIsShowDialog(true);
        checkinvalid = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(RecommendExperienceActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", storeid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recommendOutletList != null) {
            recommendOutletList.stop(Urls.RecommendOutletList);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
        if (rob != null) {
            rob.stop(Urls.rob);
        }
    }

    private RecommendAdapter recommendAdapter;
    private PullToRefreshListView recommendep_listview;
    private NetworkConnection recommendOutletList, rob, checkinvalid;
    private double longitude, latitude;
    private LocationClient locationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private int page = 1;
    private ArrayList<LocationInfo> list;
    private String projectid, storeid, address;
    private int locType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_experience);
        list = new ArrayList<>();
        initTitle();
        checkPermission();
        initLocation();
        projectid = getIntent().getStringExtra("projectid");
        initNetworkConnection();
        recommendep_listview = (PullToRefreshListView) findViewById(R.id.recommendep_listview);
        recommendAdapter = new RecommendAdapter(this, list);
        recommendep_listview.setAdapter(recommendAdapter);
        recommendep_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
                page++;
            }
        });
        onItemClickListener();
    }

    private void onItemClickListener() {
        recommendep_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LocationInfo locationInfo = list.get(position - 1);
                storeid = locationInfo.getStoreid();
                if (recommendAdapter != null) {
                    ConfirmDialog.showDialog(RecommendExperienceActivity.this, "是否确认申请该网点？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {

                        }

                        @Override
                        public void rightClick(Object object) {
                            rob(locationInfo);
                        }
                    });
                }
            }
        });
    }

    private void rob(final LocationInfo locationInfo) {
        rob.sendPostRequest(Urls.rob, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String msg = jsonObject.getString("msg");
                    int code = jsonObject.getInt("code");
                    final String max_num = jsonObject.getString("max_num");
                    if (code == 200) {
                        ConfirmDialog.showDialog(RecommendExperienceActivity.this, "申请成功", msg, "继续申请", "开始执行", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                if ("1".equals(max_num)) {
                                    baseFinish();
                                } else {
                                    if (recommendAdapter != null) {
                                        recommendAdapter.notifyDataSetChanged();
                                        refreshData();
                                    }
                                }
                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(locationInfo);
                            }
                        });
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(RecommendExperienceActivity.this, "申请成功", msg, null, "开始执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(locationInfo);
                            }
                        }).goneLeft();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(RecommendExperienceActivity.this, "申请失败", msg, null, "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                    } else {
                        Tools.showToast(RecommendExperienceActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(RecommendExperienceActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(RecommendExperienceActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }

    private void doSelectType(final LocationInfo locationInfo) {
        if (locType == 61 || locType == 161) {
            checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            doExecute(locationInfo);
                        } else if (code == 2) {
                            ConfirmDialog.showDialog(RecommendExperienceActivity.this, null, jsonObject.getString("msg"), null,
                                    "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                        }
                                    }).goneLeft();
                        } else if (code == 3) {
                            ConfirmDialog.showDialog(RecommendExperienceActivity.this, null, jsonObject.getString("msg"), "取消",
                                    "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            doExecute(locationInfo);
                                        }
                                    });
                        } else {
                            Tools.showToast(RecommendExperienceActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        Tools.showToast(RecommendExperienceActivity.this, getResources().getString(R.string.network_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Tools.showToast(RecommendExperienceActivity.this, getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else if (locType == 167) {
            Tools.showToast2(this, "请您检查是否开启权限，尝试重新请求定位");
        } else {
            Tools.showToast2(this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
        }
    }

    private void doExecute(LocationInfo locationInfo) {
        Intent intent = new Intent(RecommendExperienceActivity.this, ExperienceillActivity.class);
        intent.putExtra("id", locationInfo.getProjectid());
        intent.putExtra("projectName", locationInfo.getProjectName());
        intent.putExtra("storeNum", locationInfo.getStoreNum());
        intent.putExtra("storeName", locationInfo.getStoreName());
        intent.putExtra("store_id", locationInfo.getStoreid());
        intent.putExtra("city", locationInfo.getCity());
        intent.putExtra("money_unit", locationInfo.getMoney_unit());
        intent.putExtra("end_date", locationInfo.getEnd_date());
        intent.putExtra("check_time", locationInfo.getCheck_time());
        intent.putExtra("begin_date", locationInfo.getBegin_date());
        intent.putExtra("project_name", locationInfo.getProjectName());
        intent.putExtra("longitude", locationInfo.getLongtitude());
        intent.putExtra("latitude", locationInfo.getLatitude());
        intent.putExtra("project_person", locationInfo.getProject_person());
        intent.putExtra("standard_state", locationInfo.getStandard_state());
        intent.putExtra("is_watermark", locationInfo.getIs_watermark());
        intent.putExtra("photo_compression", locationInfo.getPhoto_compression());
        intent.putExtra("brand", locationInfo.getBrand());
        startActivity(intent);
        baseFinish();
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private void getData() {
        if (locType == 61 || locType == 161) {
            recommendOutletList.sendPostRequest(Urls.RecommendOutletList, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.getInt("code") == 200) {
                            if (list == null) {
                                list = new ArrayList<LocationInfo>();
                            } else {
                                if (page == 1) {
                                    list.clear();
                                }
                            }
                            JSONArray jsonArray = jsonObject.getJSONArray("datas");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                LocationInfo locationInfo = new LocationInfo();
                                locationInfo.setProjectid(object.getString("projectid"));
                                locationInfo.setProjectName(object.getString("projectName"));
                                locationInfo.setBegin_date(object.getString("begin_date"));
                                locationInfo.setEnd_date(object.getString("end_date"));
                                locationInfo.setCheck_time(object.getString("check_time"));
                                locationInfo.setMoney_unit(object.getString("money_unit"));
                                locationInfo.setStoreid(object.getString("storeid"));
                                locationInfo.setStoreNum(object.getString("storeNum"));
                                locationInfo.setStoreName(object.getString("storeName"));
                                locationInfo.setProvince(object.getString("province"));
                                locationInfo.setCity(object.getString("city"));
                                locationInfo.setAddress(object.getString("address"));
                                locationInfo.setLongtitude(object.getDouble("longtitude"));
                                locationInfo.setLatitude(object.getDouble("latitude"));
                                locationInfo.setDist(object.getDouble("dist"));
                                locationInfo.setStandard_state(object.getString("standard_state"));
                                locationInfo.setOutletMoney(object.getString("outletMoney"));
                                locationInfo.setProject_person(object.getString("project_person"));
                                locationInfo.setPhotoUrl(object.getString("photoUrl"));
                                locationInfo.setIs_watermark(object.getString("is_watermark"));
                                locationInfo.setPhoto_compression(object.getString("photo_compression"));
                                locationInfo.setIs_takephoto(object.getString("is_takephoto"));
                                locationInfo.setCode(object.getString("code"));
                                locationInfo.setBrand(object.getString("brand"));
                                list.add(locationInfo);
                            }
                            if (recommendAdapter != null) {
                                recommendAdapter.notifyDataSetChanged();
                            }
                            recommendep_listview.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                recommendep_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                recommendep_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        } else {
                            Tools.showToast(RecommendExperienceActivity.this, jsonObject.getString("msg"));
                        }
                        CustomProgressDialog.Dissmiss();
                    } catch (JSONException e) {
                        Tools.showToast(RecommendExperienceActivity.this, getResources().getString(R.string.network_error));
                    }
                    recommendep_listview.onRefreshComplete();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    recommendep_listview.onRefreshComplete();
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(RecommendExperienceActivity.this, getResources().getString(R.string.network_volleyerror));
                }
            });
        } else if (locType == 167) {
            Tools.showToast2(this, "请您检查是否开启权限，尝试重新请求定位");
        } else {
            Tools.showToast2(this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
        }
    }

    /**
     * 初始化定位
     */

    private void initLocation() {
        if (locationClient != null && locationClient.isStarted()) {
            Tools.showToast(RecommendExperienceActivity.this, "正在定位...");
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
                Tools.showToast(RecommendExperienceActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            locType = bdLocation.getLocType();
            address = bdLocation.getAddrStr();
            getData();
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
                    Tools.showToast(RecommendExperienceActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(RecommendExperienceActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
