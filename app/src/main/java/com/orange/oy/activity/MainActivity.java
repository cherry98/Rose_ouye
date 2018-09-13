package com.orange.oy.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.mycorps_314.LoginActivity;
import com.orange.oy.activity.shakephoto_318.ThemeDetailActivity;
import com.orange.oy.allinterface.OnCheckVersionResult;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.CalendarFragment;
import com.orange.oy.fragment.CitysearchFragment;
import com.orange.oy.fragment.MiddleFragment;
import com.orange.oy.fragment.MyDetailFragment;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.fragment.ShakeBarFragment;
import com.orange.oy.fragment.ShakephotoFragment;
import com.orange.oy.fragment.TaskNewFragment;
import com.orange.oy.fragment.TaskPublishFragment;
import com.orange.oy.info.shakephoto.ShakeThemeInfo;
import com.orange.oy.network.CheckVersion;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.service.UpdataNewService;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.StartloadingView;
import com.sobot.chat.SobotApi;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, OnGetGeoCoderResultListener,
        CitysearchFragment.OnCitysearchExitClickListener,
        CitysearchFragment.OnCitysearchItemClickListener, MyFragment.OnLocationOpenChangeListener,
        MyDetailFragment.OnBackClickForMyDetailFragment, MyDetailFragment.OnSelectCityListener,
        TaskNewFragment.OnCitysearchClickListener, TaskNewFragment.OnShowCalendarListener,
        CalendarFragment.OnBackClickForCalendarListener, View.OnClickListener {

    private BroadcastReceiver ChangeRedPointBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if (intent.getAction().equals(AppInfo.LOCATIONINFO)) {
                if (taskNewFragment != null && taskNewFragment.isVisible())
                    taskNewFragment.listener(context, intent);
            } else if (intent.getAction().equals(AppInfo.BroadcastReceiverMyFragment_Redpoint)) {
                if (myFragment != null && myFragment.isVisible()) {
                    myFragment.listener(context, intent);
                }
            }
        }
    };

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.LOCATIONINFO);
        filter.addAction(AppInfo.BroadcastReceiverMyFragment_Redpoint);
        context.registerReceiver(ChangeRedPointBroadcastReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(ChangeRedPointBroadcastReceiver);
    }

    public void onBack() {
        baseFinish();
    }

    private FragmentManager fMgr;
    private ShakephotoFragment shakephotoFragment;
    private CitysearchFragment citysearchFragment;
    private MyFragment myFragment;
    private MyDetailFragment myDetailFragment;
    private TaskNewFragment taskNewFragment;//新的任务界面
    private MiddleFragment middleFragment;
    private CalendarFragment calendarFragment;
    private static Map<String, String> localMap;//定位到的地方
    public static String localCity;
    public static String getCityNameState = null;//城市选择的多种情况
//    private TaskPublishFragment taskPublishFragment;//随手发任务 V3.19
//    private ShakeBarFragment shakeBarFragment;//甩吧首页 V3.19

    public static Map<String, String> getLocalMap() {
        return localMap;
    }

    public String getLocalCity() {
        return localCity;
    }

    protected void onStop() {
        super.onStop();
        getCityNameState = null;
        if (checkOpenSquare != null) {
            checkOpenSquare.stop(Urls.CheckOpenSquare);
        }
//        ConfirmDialog.dissmisDialog();
    }

    //城市关闭监听
    public void exitClick() {
        if (TextUtils.isEmpty(getCityNameState)) {
            switch (getNowPage()) {
                case 2: {
                    creatTaskNewFragment();
                }
                break;
                case -1: {
                    onClick(meau_square);
                }
                break;
            }
        } else {
            getCityNameState = null;
            creatMyDetailFragment();
        }
    }

    //城市选择监听
    public void ItemClick(Map<String, String> map) {
        if (TextUtils.isEmpty(getCityNameState)) {
            localMap = map;
            switch (getNowPage()) {
                case 2: {
                    creatTaskNewFragment();
                }
                break;
                case -1: {
                    onClick(meau_square);
                }
                break;
            }
        } else {
            getCityNameState = map.get("name");
            creatMyDetailFragment();
        }
    }

    //开关定位
    public void locationChange(boolean open) {
        if (open) {
            checkLocation();
        } else {
            if (mLocationClient != null && mLocationClient.isStarted()) {
                mLocationClient.stop();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Uri uri = getIntent().getDataLeft();//打开h5界拿到的参数
//        Tools.d("h5拿到的uri：233333" + uri);
//        if (uri != null) {
//            h5projectid = uri.getQueryParameter("h5projectid");
//            h5usermobile = uri.getQueryParameter("h5usermobile");
//        }
//        Tools.d("h5usermobile:" + h5usermobile + "h5projectid:" + h5projectid);
//        AppInfo.setH5Data(this, h5projectid, h5usermobile);
        checkopenSquare();
        int need_update = AppInfo.getNeedUpdata(this);
        if (need_update == 1) {
//            CheckVersion.check(getBaseContext(), onCheckVersionResult);//检查更新
        }
        if (clickCenter && !TextUtils.isEmpty(AppInfo.getKey(MainActivity.this))) {
            clickCenter = false;
            onClick(meau_center);
        }
        QbSdk.preInit(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Tools.d("onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Tools.d("onViewInitFinished=" + b);
            }
        });
        //tbs内核下载跟踪
        QbSdk.setTbsListener(this.tbsListener);
        //判断是否要自行下载内核
        boolean needDownload = TbsDownloader.needDownload(this, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
//        if (needDownload && isNetworkWifi(this)) {
        //isNetworkWifi(this)是我
        //自己写的一个方法，这里可以判断wifi下再下载
        Tools.d("needDownload=" + needDownload);
        if (needDownload)
            TbsDownloader.startDownload(this);
    }

    private TbsListener tbsListener = new TbsListener() {
        public void onDownloadFinish(int i) {
            Tools.d("onDownloadFinish=" + i);
        }

        public void onInstallFinish(int i) {
            Tools.d("onInstallFinish=" + i);
        }

        public void onDownloadProgress(int i) {
            Tools.d("progress=" + i);
        }
    };

    //开始搜索城市
    public void click() {
        creatCitysearchFragment();
    }

    //我的详情-选择常住地
    public void onSelectClick() {
        creatCitysearchFragment();
    }

    //任务城市选择
    public void clickforTask() {
        creatCitysearchFragment();
    }

    //个人信息页返回按钮
    public void onBackForMyDetail() {
        MainActivity.getCityNameState = null;
        creatMyFragment();
    }

    //任务页-点击日历
    public void showCalendar() {
        creatCalendarFragment();
    }

    //日历页返回按钮
    public void backCalendar() {
        creatTaskNewFragment();
    }

    private NetworkConnection sendData;
    private NetworkConnection checkOpenSquare;

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", getResources().getString(R.string.app_name));
                try {
                    params.put("versionnum", Tools.getVersionName(MainActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("logintime", Tools.getTimeByPattern("yyyy-MM-dd HH-mm-ss"));
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(MainActivity.this));
                params.put("resolution", Tools.getScreeInfoWidth(MainActivity.this) + "*" + Tools
                        .getScreeInfoHeight(MainActivity.this));
                params.put("mac", Tools.getLocalMacAddress(MainActivity.this));
                params.put("imei", Tools.getDeviceId(MainActivity.this));
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MainActivity.this));
                return params;
            }
        };
        checkOpenSquare = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MainActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        checkOpenSquare.setTimeCount(true);
        checkOpenSquare.setOnOutTimeListener(new NetworkConnection.OnOutTimeListener() {
            public void outTime() {
                networkView.NoNetwork(getResources().getString(R.string.network_slow));
            }
        });
    }


    private void sendData() {
        sendData.sendPostRequest(Urls.Addstatisticsstart, new Response.Listener<String>() {
            public void onResponse(String s) {
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }

    private boolean isOpenSquare = true;//是否开启广场 默认开启
    private boolean isHavpersonalTask = true;//是否有个人任务 默认有
    private boolean isJoind = false;//是否加入或创建过战队
    private boolean isBindidcard = false;//是否认证过身份

    /**
     * 检测是否开启广场
     */
    private void checkopenSquare() {
        checkOpenSquare.sendPostRequest(Urls.CheckOpenSquare, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        isOpenSquare = "0".equals(jsonObject.optString("close_square"));
                        if (isOpenSquare) {
                            isHavpersonalTask = true;
                        } else {
                            isHavpersonalTask = "1".equals(jsonObject.optString("personal_task"));
                        }
                        isJoind = "1".equals(jsonObject.optString("team_state"));
                        isBindidcard = "1".equals(jsonObject.optString("bindidcard"));
                    } else {
                        String msg = jsonObject.getString("msg");
                        if (msg.contains("请重新登录")) {
                            SobotApi.exitSobotChat(MainActivity.this);
                            AppInfo.clearKey(MainActivity.this);
                            JPushInterface.clearAllNotifications(MainActivity.this);
                            JPushInterface.setAlias(MainActivity.this, "", null);
                            JPushInterface.stopPush(MainActivity.this);
                            Intent intent11 = new Intent("com.orange.oy.VRService");
                            intent11.setPackage("com.orange.oy");
                            stopService(intent11);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                networkView.setVisibility(View.GONE);
                if (!isInitView) {
                    checkLocationPermission();
                } else {
                    if (middleFragment != null) {
                        middleFragment.setJoind(isJoind, isBindidcard);
                    }
                    if (isOpenSquare) {
                        meau_square.setVisibility(View.VISIBLE);
                        meau_square.setOnClickListener(MainActivity.this);
                    } else {
                        meau_square.setVisibility(View.GONE);
                        if (isSquare) {
                            creatMiddleFragment();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                checkOpenSquare.stopTimer();
                networkView.setVisibility(View.VISIBLE);
                networkView.NoNetwork(getResources().getString(R.string.network_fail) + "点击重试");
                networkView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        networkView.NoNetwork("正在重试...");
                        checkopenSquare();
                        networkView.setOnClickListener(null);
                    }
                });
            }
        });
    }

    private int getNowPage() {
        if (isTask) {
            return 1;
        } else if (isSquare) {
            return 2;
        } else if (isMy) {
            return 3;
        } else {
            return -1;
        }
    }

    private OnCheckVersionResult onCheckVersionResult = new OnCheckVersionResult() {
        public void checkversion(String versionName) {
            if (versionName != null) {
                ConfirmDialog.showDialogForHint(MainActivity.this, "发现新版！正在更新...");
            }
        }
    };
    //3.3大改版修改的内容
    private View meau_square, meau_task, meau_my, meau_shakephoto;
    private ImageView meau_center;
    private boolean isSquare, isTask, isMy, shakephoto;//判断是否选中了底部的导航栏---同RadioButton的isChecked
    private boolean isInitView = false;
    private ImageView square_img, task_img, my_img, shakephoto_img;
    private TextView square_text, task_text, my_text, shakephoto_text;
    private NetworkView networkView;
    private View fragmentRoot;

    private void initView() {
        if (isInitView) {
            return;
        }
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, AppInfo
                    .REQUEST_CODE_ASK_READ_PHONE_STATE);
        }
        String key = AppInfo.getKey(this);
        if (key != null) {
            if (JPushInterface.isPushStopped(this)) {//开启极光推送服务
                JPushInterface.resumePush(this);
                Tools.d("resumePush");
            }
        }
        if (AppInfo.isFirst(this)) {
            new FirstOpenAsyncTask().executeOnExecutor(Executors.newCachedThreadPool());//更新数据
        } else {
            new checkService().execute();//检测上传服务是否正常
        }
        registerReceiver(this);
        sendData();
        fMgr = getSupportFragmentManager();
        meau_task.setOnClickListener(this);
        meau_my.setOnClickListener(this);
        meau_shakephoto.setOnClickListener(this);
        meau_center.setOnClickListener(this);
        if (isOpenSquare) {
            meau_square.setVisibility(View.VISIBLE);
            meau_square.setOnClickListener(this);
            int showPageIndex = getIntent().getIntExtra("pageindex", 0);
            if (showPageIndex == 1) {//中间页
                onClick(meau_task);
            } else {
                creatTaskNewFragment();
            }
        } else {
            meau_square.setVisibility(View.GONE);
            onClick(meau_task);
        }
        isInitView = true;
    }

    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    private View bottomList, bottomList_line;
    private StartloadingView startloading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNetworkConnection();
        networkView = (NetworkView) findViewById(R.id.networkview);
        meau_square = findViewById(R.id.meau_square);
        meau_task = findViewById(R.id.meau_task);
        meau_my = findViewById(R.id.meau_my);
        meau_shakephoto = findViewById(R.id.meau_shakephoto);
        meau_center = (ImageView) findViewById(R.id.meau_center);
        square_text = (TextView) findViewById(R.id.square_text);
        task_text = (TextView) findViewById(R.id.task_text);
        my_text = (TextView) findViewById(R.id.my_text);
        shakephoto_text = (TextView) findViewById(R.id.shakephoto_text);
        shakephoto_img = (ImageView) findViewById(R.id.shakephoto_img);
        square_img = (ImageView) findViewById(R.id.square_img);
        task_img = (ImageView) findViewById(R.id.task_img);
        my_img = (ImageView) findViewById(R.id.my_img);
        bottomList = findViewById(R.id.bottomList);
        bottomList_line = findViewById(R.id.bottomList_line);
        fragmentRoot = findViewById(R.id.fragmentRoot);
        startloading = (StartloadingView) findViewById(R.id.startloading);
        startloading.setOnStartloadingListener(new StartloadingView.OnStartloadingListener() {
            public void onStartloadingEnd() {
                myHandler.removeMessages(2);
                myHandler.sendEmptyMessageDelayed(2, 100);
            }
        });
        int time = getIntent().getIntExtra("time", 0);
        startloading.startLoad(getIntent().getStringExtra("photo_url"), getIntent().getStringExtra("link_url"));
        myHandler.sendEmptyMessageDelayed(2, time * 1000);
    }

    /**
     * 设置底部是否显示
     *
     * @param isShow
     */
    public void settingBottom(boolean isShow) {
        if (isShow) {
            bottomList.setVisibility(View.VISIBLE);
            bottomList_line.setVisibility(View.VISIBLE);
        } else {
            bottomList.setVisibility(View.GONE);
            bottomList_line.setVisibility(View.GONE);
        }
    }

    /**
     * 检查权限，权限通过才能初始化UI
     */
    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppInfo
                        .REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE);
            } else {
                goon();
            }
        } else {
            goon();
        }
    }

    private void goon() {
        initView();
        if (!AppInfo.isOpenLocation(this)) {
            ConfirmDialog.showDialog(this, "是否开启定位？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                    AppInfo.setOpenLocation(MainActivity.this, false);
                }

                public void rightClick(Object object) {
                    AppInfo.setOpenLocation(MainActivity.this, true);
                    checkLocation();
                }
            });
        } else {
            checkLocation();
        }
    }

    private void hideFragment(FragmentTransaction ft, BaseFragment baseFragment) {
        if (baseFragment != null) {
            ft.hide(baseFragment);
        }
    }

    private void creatMyDetailFragment() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
            }
        }
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        myDetailFragment = (MyDetailFragment) fMgr.findFragmentByTag("myDetailFragment");
        if (myDetailFragment == null) {
            myDetailFragment = new MyDetailFragment();
        }
        myDetailFragment.setOnSelectCityListener(this);
        myDetailFragment.setOnBackClickForMyDetailFragment(this);
        ft.replace(R.id.fragmentRoot, myDetailFragment, "myDetailFragment");
        ft.addToBackStack("myDetailFragment");
        ft.commit();
    }

    private MyHandler myHandler = new MyHandler();

    //WRITE_EXTERNAL_STORAGE
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Tools.d("1-1");
                    if (isClick_meau_shakephoto) {
                        myHandler.sendEmptyMessageDelayed(1, 200);
                    }
                } else {
                    Tools.showToast(MainActivity.this, "摄像头权限获取失败");
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
                    Tools.d("2-1");
                } else {
                    Tools.showToast(MainActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(MainActivity.this, false);
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Tools.d("3-1");
                    goon();
                } else {
                    Tools.showToast(MainActivity.this, "SD卡权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Tools.d("4-1");
                } else {
                    Tools.showToast(MainActivity.this, "读取电话状态权限获取失败");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void creatTaskNewFragment() {//广场
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        isLoginShow = true;
        FragmentTransaction ft = fMgr.beginTransaction();
        taskNewFragment = (TaskNewFragment) fMgr.findFragmentByTag("taskNewFragment");
        if (taskNewFragment == null) {
            taskNewFragment = new TaskNewFragment();
        }
        taskNewFragment.setOnCitysearchClickListener(this);
        taskNewFragment.setOnShowCalendarListener(this);
        ft.replace(R.id.fragmentRoot, taskNewFragment, "taskNewFragment");
        ft.addToBackStack("taskNewFragment");
        ft.commitAllowingStateLoss();//
//        }
    }

    private void creatCalendarFragment() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        calendarFragment = (CalendarFragment) fMgr.findFragmentByTag("calendarFragment");
        if (calendarFragment == null) {
            calendarFragment = new CalendarFragment();
        }
        calendarFragment.setOnBackClickForCalendarListener(this);
        ft.replace(R.id.fragmentRoot, calendarFragment, "calendarFragment");
        ft.addToBackStack("calendarFragment");
        ft.commit();
    }

    private void creatMiddleFragment() {//任务
        creatMiddleFragment(false);
    }

    private void creatMiddleFragment(boolean isShowRight) {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        middleFragment = (MiddleFragment) fMgr.findFragmentByTag("middleFragment");
        if (middleFragment == null) {
            middleFragment = new MiddleFragment();
        }
        middleFragment.setJoind(isJoind, isBindidcard);
        middleFragment.setShowRight(isShowRight);
        middleFragment.setHavpersonalTask(isHavpersonalTask);
        ft.replace(R.id.fragmentRoot, middleFragment, "middleFragment");
        ft.addToBackStack("middleFragment");
        ft.commit();
    }

    public void createTaskPublishFragment() {//随手发任务 中间按钮 V3.19
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        TaskPublishFragment taskPublishFragment = (TaskPublishFragment) fMgr.findFragmentByTag("taskPublishFragment");
        if (taskPublishFragment == null) {
            taskPublishFragment = new TaskPublishFragment();
        }
        ft.replace(R.id.fragmentRoot, taskPublishFragment, "taskPublishFragment");
        ft.addToBackStack("taskPublishFragment");
        ft.commit();
    }

    public void createShakeBarFragment() {//甩吧首页 V3.19
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        ShakeBarFragment shakeBarFragment = (ShakeBarFragment) fMgr.findFragmentByTag("shakeBarFragment");
        if (shakeBarFragment == null) {
            shakeBarFragment = new ShakeBarFragment();
        }
        ft.replace(R.id.fragmentRoot, shakeBarFragment, "shakeBarFragment");
        ft.addToBackStack("shakeBarFragment");
        ft.commit();
    }

    private void creatCitysearchFragment() {//城市选择
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        citysearchFragment = (CitysearchFragment) fMgr.findFragmentByTag("citysearchFragment");
        if (citysearchFragment == null) {
            citysearchFragment = new CitysearchFragment();
        }
        citysearchFragment.setOnCitysearchExitClickListener(this);
        citysearchFragment.setOnCitysearchItemClickListener(this);
        ft.replace(R.id.fragmentRoot, citysearchFragment, "citysearchFragment");
        ft.addToBackStack("citysearchFragment");
        ft.commit();
    }

    private void creatMyFragment() {//我的
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        myFragment = (MyFragment) fMgr.findFragmentByTag("myFragment");
        if (myFragment == null) {
            myFragment = new MyFragment();
        }
        myFragment.setOnLocationOpenChangeListener(this);
        ft.replace(R.id.fragmentRoot, myFragment, "myFragment");
        ft.addToBackStack("myFragment");
        ft.commit();
    }

    private void creatShakephotoFragment(ShakeThemeInfo shakeThemeInfo) {//甩吧
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isClick_meau_shakephoto = false;
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        shakephotoFragment = (ShakephotoFragment) fMgr.findFragmentByTag("shakephotoFragment");
        if (shakephotoFragment == null) {
            shakephotoFragment = new ShakephotoFragment();
        }
        if (shakeThemeInfo != null) {
            shakephotoFragment.initjoinActivity(shakeThemeInfo);
        }
        ft.replace(R.id.fragmentRoot, shakephotoFragment, "shakephotoFragment");
        ft.addToBackStack("shakephotoFragment");
        ft.commit();
    }

    private boolean isLoginShow = false;

    private boolean isShowFind() {
        if (TextUtils.isEmpty(AppInfo.getKey(this))) {
            return isLoginShow;
        } else {
            taskNewFragment = (TaskNewFragment) fMgr.findFragmentByTag("taskNewFragment");
            if (taskNewFragment == null) {
                return false;
            }
            return taskNewFragment.isVisible();
        }
    }


    private boolean isFirstBack;
    private Handler handler;

    public void onBackPressed() {
        if (bottomList.getVisibility() == View.GONE) {
            return;
        }
        if (isShowFind()) {
            if (isFirstBack) {
//                baseFinish();
//                ScreenManager.AppExit(this);

//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                startActivity(intent);
                AppInfo.setShakePhotoShowAutoTextview(this, true);
                Tools.closeTost(MainActivity.this);
                moveTaskToBack(true);
            } else {
                Tools.showToast(MainActivity.this, getResources().getString(R.string.app_back_message));
                isFirstBack = true;
                if (handler == null) {
                    handler = new Handler();
                }
                handler.postDelayed(new Runnable() {
                    public void run() {
                        isFirstBack = false;
                    }
                }, 2000);
            }
        } else {
            myDetailFragment = (MyDetailFragment) fMgr.findFragmentByTag("myDetailFragment");
            calendarFragment = (CalendarFragment) fMgr.findFragmentByTag("calendarFragment");
            if (myDetailFragment != null && myDetailFragment.isVisible()) {
                creatMyFragment();
            } else if (calendarFragment != null && calendarFragment.isVisible()) {
                creatTaskNewFragment();
            } else {
                if (isSquare) {
                    creatTaskNewFragment();
                } else {
                    onClick(meau_square);
                }
            }
        }
    }

    public interface MainLocationListener {
        void success(Intent intent);

        void fail(Object object);
    }

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    private MainLocationListener mainLocationListener;
    public static double location_latitude, location_longitude;

    public void checkLocation(MainLocationListener mainLocationListener) {
        if (this.mainLocationListener == null) {
            this.mainLocationListener = mainLocationListener;
            checkLocation();
        }
    }

    private void checkLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppInfo
                        .REQUEST_CODE_ASK_LOCATION);
            } else {
                initLocation();
            }
        } else {
            initLocation();
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        location_latitude = 0;
        location_longitude = 0;
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(this);
        }
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this);
            mLocationClient.registerLocationListener(myListener);
            setLocationOption();
        }
        mLocationClient.start();
    }

    // 设置相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private boolean isClick_meau_shakephoto = false;

    public void onClick(View v) {
        if (!isClickTab) {
            return;
        }
        isClickTab = false;
        myHandler.sendEmptyMessageDelayed(3, 1000);
        switch (v.getId()) {
            case R.id.meau_square: {
                isSquare = true;
                isTask = false;
                shakephoto = false;
                isMy = false;
                square_img.setImageResource(R.mipmap.meau_square);
                task_img.setImageResource(R.mipmap.meau_task2);
                my_img.setImageResource(R.mipmap.meau_my2);
                shakephoto_img.setImageResource(R.mipmap.meau_shake_no);
                shakephoto_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                square_text.setTextColor(getResources().getColor(R.color.homepage_select));
                task_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                my_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                meau_center.setImageResource(R.mipmap.meau_center_no);
                creatTaskNewFragment();
            }
            break;
            case R.id.meau_task: {
                click_meau_task(false);
            }
            break;
            case R.id.meau_my: {
                isSquare = false;
                isTask = false;
                shakephoto = false;
                isMy = true;
                square_img.setImageResource(R.mipmap.meau_square2);
                task_img.setImageResource(R.mipmap.meau_task2);
                my_img.setImageResource(R.mipmap.meau_my);
                shakephoto_img.setImageResource(R.mipmap.meau_shake_no);
                shakephoto_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                square_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                task_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                my_text.setTextColor(getResources().getColor(R.color.homepage_select));
                meau_center.setImageResource(R.mipmap.meau_center_no);
                creatMyFragment();
            }
            break;
            case R.id.meau_shakephoto: {//甩吧
//                click_meau_shakephoto(null);
                isSquare = false;
                isTask = false;
                shakephoto = false;
                isMy = false;
                square_img.setImageResource(R.mipmap.meau_square2);
                task_img.setImageResource(R.mipmap.meau_task2);
                my_img.setImageResource(R.mipmap.meau_my2);
                shakephoto_img.setImageResource(R.mipmap.meau_shakephoto);
                shakephoto_text.setTextColor(getResources().getColor(R.color.homepage_select));
                square_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                task_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                my_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                meau_center.setImageResource(R.mipmap.meau_center_no);
                createShakeBarFragment();
            }
            break;
            case R.id.meau_center: {//中间按钮 随手发任务
                if (TextUtils.isEmpty(AppInfo.getKey(MainActivity.this))) {
                    clickCenter = true;
                    ConfirmDialog.showDialog(MainActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    Intent intent = new Intent(MainActivity.this, IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                isSquare = false;
                isTask = false;
                shakephoto = false;
                isMy = false;
                square_img.setImageResource(R.mipmap.meau_square2);
                task_img.setImageResource(R.mipmap.meau_task2);
                my_img.setImageResource(R.mipmap.meau_my2);
                shakephoto_img.setImageResource(R.mipmap.meau_shake_no);
                shakephoto_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                square_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                task_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                my_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
                meau_center.setImageResource(R.mipmap.meau_center);
                createTaskPublishFragment();
            }
            break;
        }
    }

    private boolean clickCenter = false;

    private void click_meau_task(boolean showRight) {
        isSquare = false;
        shakephoto = false;
        isTask = true;
        isMy = false;
        square_img.setImageResource(R.mipmap.meau_square2);
        task_img.setImageResource(R.mipmap.meau_task);
        my_img.setImageResource(R.mipmap.meau_my2);
        shakephoto_img.setImageResource(R.mipmap.meau_shake_no);
        shakephoto_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
        square_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
        task_text.setTextColor(getResources().getColor(R.color.homepage_select));
        my_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
        meau_center.setImageResource(R.mipmap.meau_center_no);
        creatMiddleFragment(showRight);
    }

    private void click_meau_shakephoto(ShakeThemeInfo shakeThemeInfo) {
        isClick_meau_shakephoto = true;
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
        }
        isSquare = false;
        isTask = false;
        shakephoto = true;
        isMy = false;
        square_img.setImageResource(R.mipmap.meau_square2);
        task_img.setImageResource(R.mipmap.meau_task2);
        my_img.setImageResource(R.mipmap.meau_my2);
        shakephoto_img.setImageResource(R.mipmap.meau_shakephoto);
        shakephoto_text.setTextColor(getResources().getColor(R.color.homepage_select));
        square_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
        task_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
        my_text.setTextColor(getResources().getColor(R.color.homepage_notselect));
        meau_center.setImageResource(R.mipmap.meau_center_no);
        creatShakephotoFragment(shakeThemeInfo);
    }

    public class MyLocationListenner implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                if (mainLocationListener != null) {
                    mainLocationListener.fail(null);
                }
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng ptCenter = new LatLng(location_latitude, location_longitude);
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }

    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
    }

    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            if (mainLocationListener != null) {
                mainLocationListener.fail(null);
            }
            return;
        }

        String province = reverseGeoCodeResult.getAddressDetail().province;
        localCity = reverseGeoCodeResult.getAddressDetail().city;
        String county = reverseGeoCodeResult.getAddressDetail().district;
        if (TextUtils.isEmpty(county)) {
            county = "";
        }
        if (TextUtils.isEmpty(province)) {
            province = "";
        }
        if (TextUtils.isEmpty(localCity)) {
            localCity = "";
        }
        if (!TextUtils.isEmpty(localCity)) {
            localMap = new HashMap<>();
            localMap.put("name", localCity);
            localMap.put("id", null);
            localMap.put("county", county);
            localMap.put("province", province);
            Intent send = new Intent();
            send.setAction(AppInfo.LOCATIONINFO);
            if (reverseGeoCodeResult.getPoiList() != null && !reverseGeoCodeResult.getPoiList().isEmpty()) {
                send.putExtra("name", reverseGeoCodeResult.getPoiList().get(0).name);
            } else {
                send.putExtra("name", reverseGeoCodeResult.getAddress());
            }
            send.putExtra("city", localCity);
            send.putExtra("county", county);
            send.putExtra("province", province);
            send.putExtra("latitude", reverseGeoCodeResult.getLocation().latitude + "");
            send.putExtra("longitude", reverseGeoCodeResult.getLocation().longitude + "");
            send.putExtra("address", reverseGeoCodeResult.getAddress());
            send.putExtra("type", "1");//main发送的广播
            if (mainLocationListener != null) {
                mainLocationListener.success(send);
                mainLocationListener = null;
            } else {
                sendBroadcast(send);
            }
        } else {
//            Tools.showToast(MainActivity.this, getResources().getString(R.string.location_fail));
        }
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient = null;
        }
        if (mSearch != null) {
            mSearch.destroy();
            mSearch = null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.RevisePasswordResultCode) {
            AppInfo.clearKey(this);
            JPushInterface.clearAllNotifications(this);
            JPushInterface.stopPush(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("flag", 1);
            startActivity(intent);
            baseFinish();
        } else if (resultCode == AppInfo.SettingLocationResultCode) {
            boolean isOpenLocation = data.getBooleanExtra("isOpenLocation", false);
            locationChange(isOpenLocation);
        } else if (resultCode == AppInfo.RESULT_MAIN_SHOWMIDDLE_RIGHT) {//开启middle页战队列表
            if (isTask && middleFragment != null) {
                middleFragment.clickRight();
            } else {
                if (handler == null) {
                    handler = new Handler();
                }
                handler.postDelayed(new Runnable() {
                    public void run() {
                        click_meau_task(true);
                    }
                }, 500);
            }
        } else if (resultCode == AppInfo.RESULT_MAIN_SHOWSHAKEPHOTO_AI) {//甩拍页指定活动
            if (shakephoto && shakephotoFragment != null) {
                ShakeThemeInfo shakeThemeInfo = (ShakeThemeInfo) data.getSerializableExtra("shakeThemeInfo");
                shakephotoFragment.joinActivity(shakeThemeInfo);
            } else {
                final ShakeThemeInfo shakeThemeInfo = (ShakeThemeInfo) data.getSerializableExtra("shakeThemeInfo");
                if (handler == null) {
                    handler = new Handler();
                }
                handler.postDelayed(new Runnable() {
                    public void run() {
                        click_meau_shakephoto(shakeThemeInfo);
                    }
                }, 500);
            }
        } else if (requestCode == 0) {
            creatTaskNewFragment();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void onPause() {
        super.onPause();
        isClickTab = true;
        Tools.closeTost(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        unregisterReceiver(this);
    }

    public static void deleteFiles(File file) {
        if (file.exists() && file.isFile()) {
            Tools.d("delete:" + file.getPath());
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                if (file.exists() && f.isFile()) {
                    Tools.d("delete:" + f.getPath());
                    f.delete();
                } else {
//                    if (file.exists() && f.isDirectory()) {
//                        deleteFiles(f);
//                        Tools.d("delete:" + f.getPath());
//                        f.delete();
//                    }
                }
            }
        }
    }

    private class checkService extends AsyncTask {
        protected Object doInBackground(Object[] params) {
            boolean ishav = new UpdataDBHelper(MainActivity.this).isHave();
            if (!UpdataNewService.isServiceWork(MainActivity.this, "com.orange.oy.service.UpdataNewService") && ishav) {
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                startService(service);
            } else {
                if (!ishav) {
                    deleteFiles(FileCache.getDirForPhoto(MainActivity.this));
                }
            }
            return null;
        }

        protected void onPostExecute(Object o) {
        }

        protected void onPreExecute() {
        }
    }

    private class FirstOpenAsyncTask extends AsyncTask {

        protected Object doInBackground(Object[] params) {
            UpdataDBHelper updataDBHelper = new UpdataDBHelper(MainActivity.this);
            updataDBHelper.upisUpTo1();
            return null;
        }

        protected void onPostExecute(Object o) {
            AppInfo.setIsFirst(MainActivity.this);
            new checkService().execute();//检测上传服务是否正常
            CustomProgressDialog.Dissmiss();
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(MainActivity.this, "正在更新数据");
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private boolean isClickTab = true;

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {//开启甩吧
                    onClick(meau_shakephoto);
                }
                break;
                case 2: {//隐藏广告
                    startloading.setVisibility(View.GONE);
                    fragmentRoot.setVisibility(View.VISIBLE);
                }
                break;
                case 3: {//不允许快速切换tab
                    isClickTab = true;
                }
                break;
            }
        }
    }
}
