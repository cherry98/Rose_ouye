package com.orange.oy.activity.black;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.allinterface.IType;
import com.orange.oy.allinterface.BlackShotlifeListener;
import com.orange.oy.allinterface.BlackworkCloseListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.Message1;
import com.orange.oy.info.WXListInfo;
import com.orange.oy.info.WXOptionInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 进店后的总activity
 * <p>
 * 仿微信页的记录任务存上传数据的tasktype为：wx3 切记！
 */
public class BlackWorkActivity extends BaseActivity implements BlackworkCloseListener, MediaRecorder.OnErrorListener,
        BlackWXStartFragment.BlackWXSelectListener, BlackShotFragment.ShotstateChangeListener, BlackBackDropFragment.BlackBackListener
        , View.OnClickListener {
    private FragmentManager fMgr;
    private String dirName, fileName;
    private String project_id, project_name;
    private String store_id, store_name, store_num;
    private NetworkConnection Newtasklist, Lockscreen, complete, newtasklistup, fileNum;
    private BlackShotFragment blackShotFragment;
    private BlackWXStartFragment blackWXStartFragment;
    private ArrayList<WXListInfo> WXlist = new ArrayList<>();
    private String taskid, taskbatch, batch;
    private JSONObject answer = new JSONObject();
    private JSONArray answerArray = new JSONArray();
    private String qusetionTime = "";
    private UpdataDBHelper updataDBHelper;
    private int indexRecord = 0, indexMovie = 0;
    private String recordKeys = "";
    private String recordValues = "";
    private int nowState = 0;//0：没开始；1：录像阶段；2：录音阶段
    private LinearLayout fragmentRoot;
    private int windowSize = 0;//0小窗口 1大窗口 2图标
    private ImageView fragmentRoot_imageview;
    private boolean isOnTouch = false;
    private int windowWidth = 0, windowHeight = 0;
    private BlackBackDropFragment blackBackDropFragment;
    private String clienttime, answers;
    private TextView black_notice;
    private String filetype;//文件类型（1为视频，2为录音，3为图片）
    private int filenum;//文件数量

    protected void onStop() {
        super.onStop();
    }

    private void initNetworkConnection() {
        Newtasklist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                return params;
            }
        };
        Newtasklist.setIsShowDialog(true);
        Lockscreen = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                params.put("usermobile", AppInfo.getName(BlackWorkActivity.this));
                params.put("state", state + "");
                return params;
            }
        };
        complete = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                params.put("usermobile", AppInfo.getName(BlackWorkActivity.this));
                params.put("time", clienttime);
                params.put("taskid", taskid);
                params.put("answers", answers);
                params.put("batch", batch);
                params.put("taskbatch", taskbatch);
                return params;
            }
        };
        newtasklistup = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("storeid", store_id);
                params.put("answers", answer.toString());
                params.put("token", Tools.getToken());
                params.put("batch", batch);
                params.put("taskbatch", taskbatch);
                params.put("usermobile", AppInfo.getName(BlackWorkActivity.this));
                return params;
            }
        };
        fileNum = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("projectid", project_id);
                params.put("filetype", filetype);
                params.put("usermobile", AppInfo.getName(BlackWorkActivity.this));
                params.put("filenum", filenum + "");
                return params;
            }
        };
    }

    private int state = 0;

    private BroadcastReceiver UpProgressbarBroadcast = new BroadcastReceiver() {
        private String action = null;

        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                state = 1;
                sendData();
                changeState();
//                stopTime();
//                blackBackDropFragment.setMessage(content2, true);
//                changeFragmentRoot(1);
//                if (!WXlist.isEmpty()) {
//                    questionList.addAll(WXlist);
//                    WXlist.clear();
//                }
//                fragmentRoot_imageview.setVisibility(View.GONE);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                state = 1;
                sendData();
                changeState();
//                if (fMgr == null) {
//                    fMgr = getSupportFragmentManager();
//                }
//                fMgr.popBackStack();
//                try {
//                    startRecord();
//                } catch (IOException e) {
//                    MobclickAgent.reportError(BlackWorkActivity.this, "BlackWorkActivity recorder onError 3:" + e.getMessage());
//                    errorDialog("录音异常！");
//                }
            }
//            else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                state = 1;
//                changeState();
//                stopTime();
//                if (fMgr == null) {
//                    fMgr = getSupportFragmentManager();
//                }
//                fMgr.popBackStack();
//                try {
//                    startRecord();
//                } catch (IOException e) {
//                    MobclickAgent.reportError(BlackWorkActivity.this, "BlackWorkActivity recorder onError 3:" + e.getMessage());
//                    errorDialog("录音异常！");
//                }
//            }
        }
    };


    private void sendData() {
        Lockscreen.sendPostRequest(Urls.Lockscreen, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.d("异常");
            }
        });
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
//        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(UpProgressbarBroadcast, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(UpProgressbarBroadcast);
    }

    private boolean isCall = false;
    private TelephonyManager telManager;
    private PhoneStateListener listener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: /* 无任何状态时 */
                    if (isCall) {
                        isCall = false;
                        changeState();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: /* 接起电话时 */
                    break;
                case TelephonyManager.CALL_STATE_RINGING: /* 电话进来时 */
                    isCall = true;
                    state = 1;
                    changeState();
                    break;
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isWait) {
            blackBackDropFragment.setMessage(content2, true);
            changeFragmentRoot(1);
            if (!WXlist.isEmpty()) {
                questionList.addAll(WXlist);
                WXlist.clear();
            }
            fragmentRoot_imageview.setVisibility(View.GONE);
        }
    }

    private void phoneListener() {
        // 取得电话服务
        telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //监听电话的状态
        telManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void experienceComplete() {
        try {
            if (answerArray.length() == 0) {
                answers = "";
            } else {
                answer.put("answers", answerArray);
                if (TextUtils.isEmpty(qusetionTime)) {
                    answer.put("time", Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss"));
                } else {
                    answer.put("time", qusetionTime);
                }
                answers = answer.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        complete.sendPostRequest(Urls.Complete, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s + "操作");
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(BlackWorkActivity.this, "操作成功");
                        stopRecord();
                        BlackillustrateActivity.isUpdata = "2";
                        BlackillustrateActivity.isNormal = false;
                        baseFinish();
                    } else {
                        Tools.showToast(BlackWorkActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }, null);
    }

    private void changeState() {
        switch (nowState) {
            case 1: {
                if (isOnResume) {
                    if (isCall || state == 1) {
                        if (blackShotFragment != null) {
                            blackShotFragment.stopRecordMovie();
                        }
                    } else {
                        startMovieRecordThread();
                    }
                }
            }
            break;
            case 2: {
                if (isCall) {
                    stopRecord();
                } else {
                    try {
                        startRecord();
                    } catch (IOException e) {
                        MobclickAgent.reportError(BlackWorkActivity.this, "BlackWorkActivity recorder onError 3:" + e
                                .getMessage());
                        errorDialog("录音异常！");
                    }
                }
            }
            break;
        }
    }

    private void initWindowWH() {
        if (windowWidth == 0) {
            windowWidth = Tools.getScreeInfoWidth(this);
        }
        if (windowHeight == 0) {
            windowHeight = Tools.getScreeInfoHeight(this);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_blackwork);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        clienttime = sDateFormat.format(new java.util.Date());
        registerReceiver(this);
        phoneListener();
        windowSize = AppInfo.getSize(this);
        updataDBHelper = new UpdataDBHelper(this);
        initWindowWH();
        initNetworkConnection();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        content1 = new ArrayList<>();
        content2 = new ArrayList<>();
        content3 = new ArrayList<>();
        blackBackDropFragment = (BlackBackDropFragment) fMgr.findFragmentById(R.id.black_fragment);
        blackBackDropFragment.setBlackBackListener(this);
        fragmentRoot = (LinearLayout) findViewById(R.id.fragmentRoot);
        black_notice = (TextView) findViewById(R.id.black_notice);
        fragmentRoot.setOnTouchListener(onTouchListener);
        fragmentRoot_imageview = (ImageView) findViewById(R.id.fragmentRoot_imageview);
        fileName = data.getStringExtra("fileName");
        dirName = data.getStringExtra("dirName");
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        store_id = data.getStringExtra("store_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        fMgr = getSupportFragmentManager();
        nowState = 2;
        startRecordThread();
        getData();
//        if (!NotificationsUtils.isNotificationEnabled(this)) {
//            ConfirmDialog.showDialogForHint(this, "检测到通知栏权限未开启，请开启后继续答题", "去开启", new ConfirmDialog.OnSystemDialogClickListener() {
//                public void leftClick(Object object) {
//                }
//
//                public void rightClick(Object object) {
//                    Intent localIntent = new Intent();
//                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
//                    startActivity(localIntent);
//                }
//            });
//        }
        black_notice.setOnClickListener(this);
    }

    private boolean isOnResume = false;

    protected void onResume() {
        super.onResume();
        isOnResume = true;
        if (isPause) {
            isPause = false;
            if (fMgr == null) {
                fMgr = getSupportFragmentManager();
            }
            fMgr.popBackStack();
            try {
                startRecord();
            } catch (IOException e) {
                MobclickAgent.reportError(BlackWorkActivity.this, "BlackWorkActivity recorder onError 3:" + e.getMessage());
                errorDialog("录音异常！");
            }
            nowState = 2;
        } else if (nowState == 1 && blackShotFragment != null) {
            blackShotFragment.resumeRecordMovie();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isWait) {
            if (nowState == 1) {
                isPause = true;
                stopTime();
                blackBackDropFragment.setMessage(content2, true);
                changeFragmentRoot(1);
                if (!WXlist.isEmpty()) {
                    questionList.addAll(WXlist);
                    WXlist.clear();
                }
                fragmentRoot_imageview.setVisibility(View.GONE);
            }
        }
    }

    private boolean isPause = false;

    protected void onPause() {
        super.onPause();
        isOnResume = false;
    }

    private ArrayList<IType> content1;//录屏
    private ArrayList<IType> content2;//解屏
    private ArrayList<IType> content3;//等待
    private String questionid;

    private void getData() {
        Newtasklist.sendPostRequest(Urls.Newtasklist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        taskid = jsonObject.getString("taskid");
                        taskbatch = jsonObject.getString("taskbatch");
                        batch = jsonObject.getString("batch");
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            WXListInfo wxListInfo = new WXListInfo();
                            JSONObject json = jsonArray.getJSONObject(i);
                            wxListInfo.setId(json.getString("id"));
                            questionid = json.getString("id");
                            wxListInfo.setNum(json.getString("question_num"));
                            wxListInfo.setType(json.getString("question_type"));
                            String name = json.getString("question_name");
                            if (!TextUtils.isEmpty(name)) {
                                if (name.startsWith("[\"")) {
                                    name = name.substring(2);
                                }
                                if (name.endsWith("\"]")) {
                                    name = name.substring(0, name.length() - 2);
                                }
                                wxListInfo.setName(name.split("\",\""));
                            }
                            JSONArray array = json.getJSONArray("options");
                            int lenght = array.length();
                            for (int j = 0; j < lenght; j++) {
                                WXOptionInfo wxOptionInfo = new WXOptionInfo();
                                JSONObject temp = array.getJSONObject(j);
                                wxOptionInfo.setId(temp.getString("id"));
                                wxOptionInfo.setName(temp.getString("option_name"));
                                wxOptionInfo.setNum(temp.getString("option_num"));
                                if (j == 0) {//最多两个选项
                                    wxListInfo.setOption1(wxOptionInfo);
                                } else {
                                    wxListInfo.setOption2(wxOptionInfo);
                                }
                            }
                            wxListInfo.setTime(Tools.StringToInt(json.getString("time")));
                            wxListInfo.setVideoTime(Tools.StringToInt(json.getString("videotime")));
                            wxListInfo.setVideo("1".equals(json.getString("isvideo")));
                            WXlist.add(wxListInfo);
                        }
                        JSONArray jsonArray1 = jsonObject.optJSONArray("content1");
                        if (content1 == null) {
                            content1 = new ArrayList<IType>();
                        }
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            Message1 message1 = new Message1();
                            message1.setContent(jsonArray1.getString(i));
                            content1.add(message1);
                        }//录屏时微信页内容
                        blackBackDropFragment.setMessage(content1, "2");
                        blackBackDropFragment.setData(store_id, taskid, questionid);
                        JSONArray jsonArray2 = jsonObject.optJSONArray("content2");
                        if (content2 == null) {
                            content2 = new ArrayList<IType>();
                        }
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            Message1 message1 = new Message1();
                            message1.setContent(jsonArray2.getString(i));
                            content2.add(message1);
                        }//解屏进入偶业时微信页内容
                        JSONArray jsonArray3 = jsonObject.optJSONArray("content3");
                        if (content3 == null) {
                            content3 = new ArrayList<IType>();
                        }
                        for (int i = 0; i < jsonArray3.length(); i++) {
                            Message1 message1 = new Message1();
                            message1.setContent(jsonArray3.getString(i));
                            content3.add(message1);
                        }//等待答题时微信页内容
                        if (!WXlist.isEmpty()) {
                            creatWXStartFragment();
                            CustomProgressDialog.Dissmiss();
                        } else {
                            Tools.showToast(BlackWorkActivity.this, "没有暗访任务");
                            baseFinish();
                        }
                    } else {
                        Tools.showToast(BlackWorkActivity.this, jsonObject.getString("msg"));
                        baseFinish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(BlackWorkActivity.this, getResources().getString(R.string.network_error));
                    baseFinish();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(BlackWorkActivity.this, getResources().getString(R.string.network_volleyerror));
                baseFinish();
            }
        });
    }

    private void creatShotFragment() {
        stopRecord();
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        blackShotFragment = (BlackShotFragment) fMgr.findFragmentByTag("blackShotFragment");
        if (blackShotFragment == null) {
            blackShotFragment = new BlackShotFragment();
        }
        blackShotFragment.setBlackworkCloseListener(this);
        blackShotFragment.setShotstateChangeListener(this);
        blackShotFragment.setBlackShotlifeListener(blackShotlifeListener);
        Bundle bundle = new Bundle();
        bundle.putString("fileName", fileName + Tools.getTimeSS());
        bundle.putString("dirName", dirName);
        bundle.putInt("videotime", questionList.get(0).getVideoTime());
        blackShotFragment.setArguments(bundle);
        ft.add(R.id.fragmentRoot, blackShotFragment, "blackShotFragment");
        ft.addToBackStack("blackShotFragment");
        ft.commit();
        nowState = 1;
    }

    private int nowWinW = 0, nowWinH = 0;

    //更改fragment大小
    private void changeFragmentRoot(int state) {//0小窗口 1大窗口 2图标
        switch (state) {
            case 1: {
                fragmentRoot_imageview.setVisibility(View.GONE);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fragmentRoot.getLayoutParams();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.leftMargin = 0;
                lp.topMargin = 0;
                fragmentRoot.setLayoutParams(lp);
                isOnTouch = false;
            }
            break;
            case 0: {
                initWindowWH();
                fragmentRoot_imageview.setVisibility(View.GONE);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fragmentRoot.getLayoutParams();
                lp.width = nowWinW = 100;
                lp.height = nowWinH = 150;
                lp.leftMargin = windowWidth - 100;
                lp.topMargin = windowHeight / 2 - 75;
                fragmentRoot.setLayoutParams(lp);
                isOnTouch = true;
            }
            break;
            case 2: {
                initWindowWH();
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fragmentRoot.getLayoutParams();
                lp.width = nowWinW = 120;
                lp.height = nowWinH = 120;
                lp.leftMargin = windowWidth - nowWinW;
                lp.topMargin = windowHeight - nowWinH * 3;
                fragmentRoot.setLayoutParams(lp);
                isOnTouch = true;
                lp = (FrameLayout.LayoutParams) fragmentRoot_imageview.getLayoutParams();
                lp.width = nowWinW;
                lp.height = nowWinH;
                lp.leftMargin = windowWidth - nowWinW;
                lp.topMargin = windowHeight - nowWinH * 3;
                fragmentRoot_imageview.setLayoutParams(lp);
                fragmentRoot_imageview.setVisibility(View.VISIBLE);
                fragmentRoot_imageview.bringToFront();
            }
            break;
        }
    }

    private BlackShotlifeListener blackShotlifeListener = new BlackShotlifeListener() {
        public void onResume() {
            startMovieRecordThread();
        }

        public void onPause() {
        }
    };

    private void creatWXStartFragment() {//只调用一次
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        blackWXStartFragment = (BlackWXStartFragment) fMgr.findFragmentByTag("blackWXStartFragment");
        if (blackWXStartFragment == null) {
            blackWXStartFragment = new BlackWXStartFragment();
        }
        blackWXStartFragment.setBlackworkCloseListener(this);
        blackWXStartFragment.setBlackWXSelectListener(this);
        WXListInfo wxListInfo = WXlist.remove(0);
        questionList.add(wxListInfo);
        if (wxListInfo.isVideo()) {
            blackWXStartFragment.showFirst(wxListInfo.getName());
        } else {
            blackWXStartFragment.showQuestion(wxListInfo.getName(), wxListInfo.getOption1().getName(),
                    wxListInfo.getOption2().getName());
        }
        ft.add(R.id.fragmentRoot, blackWXStartFragment, "blackWXStartFragment");
        ft.commit();
    }

    private void hideWXFragment() {
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        if (blackWXStartFragment == null) {
            blackWXStartFragment = (BlackWXStartFragment) fMgr.findFragmentByTag("blackWXStartFragment");
        }
        ft.hide(blackWXStartFragment);
        ft.commit();
    }

    private void showWXFragment() {
        isWait = false;
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        if (blackWXStartFragment == null) {
            blackWXStartFragment = (BlackWXStartFragment) fMgr.findFragmentByTag("blackWXStartFragment");
        }
        WXListInfo wxListInfo = questionList.get(0);
        if (wxListInfo.isVideo()) {
            blackWXStartFragment.showFirst2(wxListInfo.getName());
        } else {
            blackWXStartFragment.showQuestion(wxListInfo.getName(), wxListInfo.getOption1().getName(),
                    wxListInfo.getOption2().getName());
        }
        ft.show(blackWXStartFragment);
        ft.commit();
//        deletNotification(wxListInfo.getTime());
        Message message = handler.obtainMessage();
        message.what = 2;
        handler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        if (isWait) {
            showWXFragment();
        } else {
            Tools.showToast(BlackWorkActivity.this, "请先继续答题");
        }
    }

    private void showWXFragment2() {
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        if (blackWXStartFragment == null) {
            blackWXStartFragment = (BlackWXStartFragment) fMgr.findFragmentByTag("blackWXStartFragment");
        }
        ft.show(blackWXStartFragment);
        ft.commit();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    private static MediaRecorder mediaRecorder;
    private static File mRecordFile;
    private int time = 0;
    private boolean isStart;
    private static final long[] pattern = {100, 400};
    private Handler Timerhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case startMovieState: {
                    Tools.d("startRecordMovie");
                    blackShotFragment.startRecordMovie();
                }
                return;
            }
        }
    };

    public void on() {//视频开启
        state = 4;
        sendData();
    }

    public void off() {//视频中断
        state = 3;
        sendData();
    }

    private static Timer mTimer;

    private void startTime() {
        if (mTimer != null) {
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public long scheduledExecutionTime() {
                return super.scheduledExecutionTime();
            }

            public void run() {
                time++;
                Tools.d(time + "---");
                if (WXlist != null && !WXlist.isEmpty()) {
                    if (time == WXlist.get(0).getTime()) {
                        addQuestionList();
                        Tools.d("addQuestionList");
                    }
                }
            }
        }, 0, 1000);
    }

    private void stopTime() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startRecord() throws IOException, IllegalStateException {
        if (isStart) {
            return;
        }
        if (!createRecordDir(dirName, fileName + (indexRecord++))) {
            Tools.showToast(this, "录音文件创建失败！启动失败！");
            baseFinish();
            return;
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        Tools.d(mRecordFile.getAbsolutePath());
        mediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.prepare();
        mediaRecorder.start();
        isStart = true;
        startTime();
        isAgain = false;
    }

    private boolean createRecordDir(String dirName, String name) {
        File vecordDir = FileCache.getDirForRecord(this, dirName);
        try {
            mRecordFile = new File(vecordDir, name + ".amr");
            if (mRecordFile.exists()) {
                mRecordFile.delete();
            }
            mRecordFile.createNewFile();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void stopRecord() {
        if (mediaRecorder == null) {
            return;
        }
        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        try {
            mediaRecorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder = null;
        isStart = false;
        if (mRecordFile != null && new File(mRecordFile.getAbsolutePath()).exists()) {
//            if (TextUtils.isEmpty(recordKeys)) {
//                recordKeys = index + "";
//            } else {
//                recordKeys = recordKeys + "," + index;
//            }
//            if (TextUtils.isEmpty(recordValues)) {
//                recordValues = mRecordFile.getAbsolutePath();
//            } else {
//                recordValues = recordValues + "," + mRecordFile.getAbsolutePath();
//            }
            indexRecord++;
            String usermobile = AppInfo.getName(BlackWorkActivity.this);
            updataDBHelper.addUpdataTask(usermobile, project_id, project_name, store_num, null,
                    store_id, store_name + "_" + store_num, null, null, "wxly", taskid, "神秘客户任务", null, null, null,
                    usermobile + project_id + store_id + taskid + "wxly", null,
                    "recordKey", mRecordFile.getAbsolutePath(), UpdataDBHelper.Updata_file_type_video, null, null, false, null, null, true, true);
            Intent service = new Intent("com.orange.oy.UpdataNewService");
            service.setPackage("com.orange.oy");
            startService(service);
        }
    }

    private boolean isAgain = false;

    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null) {
                mr.reset();
                if (!isAgain) {
                    isAgain = true;
                    startRecord();
                } else {
                    MobclickAgent.reportError(this, "BlackWorkActivity recorder onError 0: what" + what + ";extra:" + extra);
                    errorDialog("录音异常！");
                }
            }
        } catch (IllegalStateException e) {
            MobclickAgent.reportError(this, "BlackWorkActivity recorder onError 1:" + e.getMessage());
            errorDialog("录音异常！");
        } catch (Exception e) {
            MobclickAgent.reportError(this, "BlackWorkActivity recorder onError 2:" + e.getMessage());
            errorDialog("录音异常！");
        }
    }

    public void close(Bundle bundle) {
        if (bundle == null) {
            baseFinish();
        } else {
            questionList.remove(0);
            nowState = 2;
            String keys = bundle.getString("keys");
            String values = bundle.getString("values");
            indexMovie++;
            String usermobile = AppInfo.getName(BlackWorkActivity.this);
            updataDBHelper.addUpdataTask(usermobile, project_id, project_name, store_num, null,
                    store_id, store_name + "_" + store_num, null, null, "wxsp", taskid, "神秘客户任务", null, null, null,
                    usermobile + project_id + store_id + taskid + Tools.getTimeSS() + "wxsp", null,
                    keys, values, UpdataDBHelper.Updata_file_type_video, null, null, false, null, null, true, true);
            if (fMgr == null) {
                fMgr = getSupportFragmentManager();
            }
            fMgr.popBackStack();
//            startRecordThread();
            try {
                startRecord();
            } catch (IOException e) {
                MobclickAgent.reportError(BlackWorkActivity.this, "BlackWorkActivity recorder onError 3:" + e.getMessage());
                errorDialog("录音异常！");
            }
            showWXFragment2();
            checkQuestionList();
        }
    }

    private static StartRecordThread startRecordThread;

    private void startRecordThread() {
        if (startRecordThread == null) {
            startRecordThread = new StartRecordThread();
            startRecordThread.start();
        }
    }

    @Override
    public void selectContinue() {
//        if (fMgr == null) {
//            fMgr = getSupportFragmentManager();
//        }
//        FragmentTransaction ft = fMgr.beginTransaction();
//        if (blackWXStartFragment == null) {
//            blackWXStartFragment = (BlackWXStartFragment) fMgr.findFragmentByTag("blackWXStartFragment");
//        }
//        ft.show(blackWXStartFragment);
//        ft.commit();
        showWXFragment();
    }

    @Override
    public void selectComplete() {
        experienceComplete();
    }


    class StartRecordThread extends Thread {
        public void run() {
//            try {
//                sleep(1300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            if (!isCall) {
                try {
                    startRecord();
                } catch (IOException e) {
                    MobclickAgent.reportError(BlackWorkActivity.this, "BlackWorkActivity recorder onError 3:" + e
                            .getMessage());
                    errorDialog("录音异常！");
                }
            }
        }
    }

    private static StartMovieRecordThread startMovieRecordThread;
    private static final int startMovieState = 100;

    private void startMovieRecordThread() {
        if (startMovieRecordThread == null) {
            startMovieRecordThread = new StartMovieRecordThread();
            startMovieRecordThread.start();
        }
    }

    class StartMovieRecordThread extends Thread {
        public void run() {
            try {
                sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (blackShotFragment != null && !isCall && (state == 0 || state == 3)) {
                Timerhandler.sendEmptyMessage(startMovieState);
            }
            startMovieRecordThread = null;
        }
    }

    //知道了按钮监听
    public void know() {
        state = 0;
        qusetionTime = Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss");
        hideWXFragment();
        changeFragmentRoot(windowSize);
        creatShotFragment();
        blackBackDropFragment.setMessage(content1, "2");
        blackBackDropFragment.setData(store_id, taskid, questionid);
    }

    public void selectLeft() {
        WXListInfo wxListInfo = questionList.remove(0);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("question_id", wxListInfo.getId());
            jsonObject.put("question_type", wxListInfo.getType());
            jsonObject.put("question_num", wxListInfo.getNum());
            jsonObject.put("answers", wxListInfo.getOption1().getId());
            answerArray.put(jsonObject);
            checkQuestionList();
        } catch (JSONException e) {
            MobclickAgent.reportError(this, "BlackWorkActivity json onError 01:" + e.getMessage());
            errorDialog("题目存储异常！");
        }
    }

    public void selectRight() {
        WXListInfo wxListInfo = questionList.remove(0);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("question_id", wxListInfo.getId());
            jsonObject.put("question_type", wxListInfo.getType());
            jsonObject.put("question_num", wxListInfo.getNum());
            jsonObject.put("answers", wxListInfo.getOption2().getId());
            answerArray.put(jsonObject);
            checkQuestionList();
        } catch (JSONException e) {
            MobclickAgent.reportError(this, "BlackWorkActivity json onError 02:" + e.getMessage());
            errorDialog("题目存储异常！");
        }
    }

    public void lastPage() {//退出到最后一页
        stopRecord();
//        String usermobile = AppInfo.getName(BlackWorkActivity.this);
//        updataDBHelper.addUpdataTask(usermobile, project_id, project_name, store_num, null,
//                store_id, store_name + "_" + store_num, null, null, "wxly", taskid, "神秘客户任务", null, null, null,
//                usermobile + project_id + store_id + taskid + "wxly", null,
//                recordKeys, recordValues, UpdataDBHelper.Updata_file_type_video, null, null, false, null, null, true, true);
//        Intent service = new Intent("com.orange.oy.UpdataNewService");
//        service.setPackage("com.orange.oy");
//        startService(service);
        recordFileNum(indexRecord, "2");
        BlackillustrateActivity.isUpdata = "1";
        BlackillustrateActivity.isNormal = false;
        baseFinish();
    }

    private void recordFileNum(int filenum, final String filetype) {
        this.filenum = filenum;
        this.filetype = filetype;
        fileNum.sendPostRequest(Urls.FileNum, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d("文件类型：" + filetype + "暗访项目文件数量：" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if ("2".equals(filetype)) {
                            recordFileNum(indexMovie, "1");
                        }
                    } else {
                        Tools.showToast(BlackWorkActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private boolean isWait = false;

    private void checkQuestionList() {
        if (questionList.isEmpty() && WXlist.isEmpty()) {//如果题目已完成
            changeFragmentRoot(1);
            try {
                answer.put("answers", answerArray);
                if (TextUtils.isEmpty(qusetionTime)) {
                    answer.put("time", Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss"));
                } else {
                    answer.put("time", qusetionTime);
                }
//                String usermobile = AppInfo.getName(BlackWorkActivity.this);
//                updataDBHelper.addUpdataTask(usermobile, project_id, project_name, null, null,
//                        store_id, store_name + "_" + store_num, null, null, "wx3", taskid, "神秘客户任务", null, null, null,
//                        usermobile + project_id + store_id + taskid, null,
//                        null, null, UpdataDBHelper.Updata_file_type_video, null, null, true, Urls.Newtasklistup,
//                        paramsToString(), true);
                sendWXData();
                blackWXStartFragment.showLast();
            } catch (JSONException e) {
                MobclickAgent.reportError(this, "BlackWorkActivity json onError 03:" + e.getMessage());
                errorDialog("题目存储异常！");
            }
//            catch (UnsupportedEncodingException e) {
//                MobclickAgent.reportError(this, "BlackWorkActivity json onError 04:" + e.getMessage());
//                errorDialog("题目存储异常！");
//            }
        } else {
            if (questionList.isEmpty()) {
                isWait = true;
                changeFragmentRoot(1);
                hideWXFragment();
                if (blackBackDropFragment != null) {
                    blackBackDropFragment.setMessage(content3, "1");
                    blackBackDropFragment.setData(store_id, taskid, questionid);
                }
            } else {
                isWait = false;
                WXListInfo wxListInfo = questionList.get(0);
                if (wxListInfo.isVideo()) {
                    blackWXStartFragment.showFirst2(wxListInfo.getName());
                } else {
                    changeFragmentRoot(1);
                    blackWXStartFragment.showQuestion(wxListInfo.getName(), wxListInfo.getOption1().getName(),
                            wxListInfo.getOption2().getName());
                }
//                deletNotification(wxListInfo.getTime());
                Message message = handler.obtainMessage();
                message.what = 3;
                handler.sendMessage(message);
            }
        }
    }

    public void sendWXData() {
        newtasklistup.sendPostRequest(Urls.Newtasklistup, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.d("执行完成-----");
                    } else {
                        Tools.showToast(BlackWorkActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BlackWorkActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(BlackWorkActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

//    private String paramsToString() throws UnsupportedEncodingException {
//        Map<String, String> parames = new HashMap<>();
//        parames.put("taskid", taskid);
//        parames.put("storeid", store_id);
//        parames.put("answers", answer.toString());
//        parames.put("token", Tools.getToken());
//        parames.put("batch", batch);
//        parames.put("taskbatch", taskbatch);
//        parames.put("usermobile", AppInfo.getName(BlackWorkActivity.this));
//        String data = "";
//        Iterator<String> iterator = parames.keySet().iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            if (TextUtils.isEmpty(data)) {
//                data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
//            } else {
//                data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
//            }
//        }
//        return data;
//    }

    private ArrayList<WXListInfo> questionList = new ArrayList<>();//显示问题队列
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                black_notice.setVisibility(View.VISIBLE);
                black_notice.setText(name);
            } else if (msg.what == 2) {
                black_notice.setVisibility(View.GONE);
            } else if (msg.what == 3) {
                black_notice.setVisibility(View.GONE);
            }
        }
    };
    private String name;
//    private int num = 1;

    //添加问题队列
    private void addQuestionList() {
        WXListInfo temp = WXlist.remove(0);
        questionList.add(temp);
        Timerhandler.sendEmptyMessage(0);
//        startNotification(getResources().getString(R.string.app_name), temp.getName()[0], temp.getTime());
        name = temp.getName()[0];
        Message message = handler.obtainMessage();
        message.what = 1;
        handler.sendMessage(message);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this);
        stopRecord();
        stopTime();
        Timerhandler = null;
        startRecordThread = null;
        if (telManager != null) {
            telManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public void baseFinish() {
        CustomProgressDialog.Dissmiss();
        super.baseFinish();
    }

    private void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void errorDialog(String message) {
        ConfirmDialog.showDialogForHint(this, message, new ConfirmDialog.OnSystemDialogClickListener() {
            public void leftClick(Object object) {
            }

            public void rightClick(Object object) {
                // 退到出店页面
                Intent intent = new Intent(BlackWorkActivity.this, BlackillustrateActivity.class);
                intent.putExtra("isUpdata", "1");
                intent.putExtra("isNormal", false);
                startActivity(intent);
                BlackillustrateActivity.isUpdata = "1";
                BlackillustrateActivity.isNormal = false;
                baseFinish();
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        public int getCount() {
            return questionList.size();
        }

        public Object getItem(int position) {
            return questionList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView == null) {
                convertView = Tools.loadLayout(BlackWorkActivity.this, R.layout.item_blackwork);
                textView = (TextView) convertView.findViewById(R.id.item_blackwork_text);
            } else {
                textView = (TextView) convertView.findViewById(R.id.item_blackwork_text);
            }
            textView.setText(questionList.get(position).getName()[0]);
            return convertView;
        }
    }

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private void startNotification(String title, String message, int index) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this);
        }
        mBuilder.setContentTitle(title).setContentText(message)
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                .setTicker(title) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setVibrate(pattern)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH); //设置该通知优先级
        }
        Intent intent = new Intent(this, BlackWorkActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(index, notification);
    }

    public PendingIntent getDefalutIntent(int flags) {
        return PendingIntent.getActivity(this, 1, new Intent(), flags);
    }

    private void deletNotification(int index) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(index);
    }

//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        if (isWait) {
//            showWXFragment();
//        }
//    }

    private int x;
    private int y;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (!isOnTouch) {
                return false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    x = (int) event.getX();
                    y = (int) event.getY();
                }
                break;
                case MotionEvent.ACTION_MOVE: {
                    int sx = (int) (event.getX() - x);
                    int sy = (int) (event.getY() - y);
                    if (Math.abs(sx) > 5 || Math.abs(sy) > 5) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fragmentRoot.getLayoutParams();
                        int tempX = lp.leftMargin + sx;
                        int tempY = lp.topMargin + sy;
                        if (tempX < 0) {
                            tempX = 0;
                        } else if (tempX > windowWidth - nowWinW) {
                            tempX = windowWidth - nowWinW;
                        }
                        if (tempY < 0) {
                            tempY = 0;
                        } else if (tempY > windowHeight - nowWinH) {
                            tempY = windowHeight - nowWinH;
                        }
                        lp.leftMargin = tempX;
                        lp.topMargin = tempY;
                        if (windowSize == 2) {
                            FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) fragmentRoot_imageview.getLayoutParams();
                            lp2.leftMargin = tempX;
                            lp2.topMargin = tempY;
                            fragmentRoot_imageview.setLayoutParams(lp2);
                        }
                        fragmentRoot.setLayoutParams(lp);
                    }
                    x = (int) event.getX();
                    y = (int) event.getY();
                }
                break;
                case MotionEvent.ACTION_UP: {
                }
                break;
            }
            return true;
        }
    };
}
