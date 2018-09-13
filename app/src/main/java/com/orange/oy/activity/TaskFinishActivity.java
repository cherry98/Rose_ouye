package com.orange.oy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.BaseView;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.ScanTaskInfo;
import com.orange.oy.info.TaskFinishInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.reord.AudioManager;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CallTaskView;
import com.orange.oy.view.FinishScanView;
import com.orange.oy.view.FinishcategoryView;
import com.orange.oy.view.FinisheditView;
import com.orange.oy.view.FinishmapView;
import com.orange.oy.view.FinishpackageView;
import com.orange.oy.view.FinishrecodeView;
import com.orange.oy.view.FinishshotView;
import com.orange.oy.view.FinishtaskView;
import com.orange.oy.view.MyVideoView;
import com.orange.oy.view.RecodePlayView;
import com.orange.oy.view.TaskCheckNoEditView;
import com.orange.oy.view.TaskEditNoEditView;
import com.orange.oy.view.TaskEditView2;
import com.orange.oy.view.TaskJudgeNoEditView;
import com.orange.oy.view.TaskRadioNoEditView;
import com.orange.oy.view.TaskTimeSelNoEditView;
import com.orange.oy.view.WebpageTaskView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务已完成页-资料已回收
 */
public class TaskFinishActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private AppTitle taskfinish_title;
    private boolean isShare = false;

    private void initTitle(String title) {
        taskfinish_title = (AppTitle) findViewById(R.id.taskfinish_title);
        taskfinish_title.settingName(title);
        taskfinish_title.showBack(this);
        if (isShare) {
            taskfinish_title.setIllustrate(R.mipmap.share2, exitLeft);
        }
    }

    private AppTitle.OnExitClickForAppTitle exitLeft = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            sign();
        }
    };

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        AudioManager.stopPlaying();
        if (taskdetail != null) {
            taskdetail.stop(Urls.Taskdetail);
        }
        if (Sign != null) {
            Sign.stop(Urls.Sign);
        }
//        if (views != null) {
//            for (BaseView baseView : views) {
//                baseView.onStop(null);
//            }
//        }
        RecodePlayView.closeAllRecodeplay();
        RecodePlayView.clearRecodePlayViewMap();
        MyVideoView.closeAllMyVideoView();
        MyVideoView.clearMyVideoViewMap();
        AudioManager.stopPlaying();
    }

    private String store_id;

    private void initNetworkConnection() {
        if (taskdetail == null)
            taskdetail = new NetworkConnection(this) {
                public Map<String, String> getNetworkParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("store_id", store_id);
                    params.put("state", state);
                    params.put("token", Tools.getToken());
                    return params;
                }
            };
        taskdetail.setIsShowDialog(true);
        Sign = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", "outlet_id=" + store_id + "&usermobile=" + AppInfo.getName(TaskFinishActivity.this));
                return params;
            }
        };
    }

    private NetworkConnection taskdetail, Sign;
    private LinearLayout taskfinish_layout;
    private String state;//状态 1为执行完成，2为资料回收
    private String photo_compression;
    private String projectname, store_name, store_num, project_id;
    private int is_watermark;
    private String code, brand;
    private boolean isAgain;
    private UpdataDBHelper updataDBHelper;
    private String username;
    private Intent data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskfinish);
        registerReceiver(this);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        username = AppInfo.getName(this);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        String titleName = data.getStringExtra("store_name");
        isShare = data.getBooleanExtra("isShare", false);
        initTitle(titleName);
        store_id = data.getStringExtra("store_id");
        projectname = data.getStringExtra("projectname");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        project_id = data.getStringExtra("project_id");
        state = data.getStringExtra("state");
        photo_compression = data.getStringExtra("photo_compression");
        is_watermark = Tools.StringToInt(data.getStringExtra("is_watermark"));
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        isAgain = data.getBooleanExtra("isAgain", true);
        taskfinish_layout = (LinearLayout) findViewById(R.id.taskfinish_layout);
        getData();
        isRefresh = false;
    }

    public static boolean isRefresh;

    protected void onResume() {
        super.onResume();
        initNetworkConnection();
        if (isRefresh) {
            taskfinish_layout.removeAllViews();
            getData();
            isRefresh = false;
        }
    }

    private ArrayList<BaseView> views = new ArrayList<>();

    protected void onPause() {
        super.onPause();
        if (views != null) {
            for (BaseView baseView : views) {
                baseView.onPause(null);
            }
        }
        if (taskdetail != null) {
            taskdetail.stop(Urls.Taskdetail);
            taskdetail = null;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this);
        if (views != null) {
            for (BaseView baseView : views) {
                baseView.onDestory(null);
            }
        }
    }

    private void sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        final String msg = jsonObject.getString("msg");
                        UMShareDialog.showDialog(TaskFinishActivity.this, false, new UMShareDialog.UMShareListener() {
                            public void shareOnclick(int type) {
                                String webUrl = Urls.ShareTaskDetail + "?&outlet_id=" + store_id + "&usermobile=" + AppInfo.getName(TaskFinishActivity.this)
                                        + "&sign=" + msg;
                                MyUMShareUtils.umShare(TaskFinishActivity.this, type, webUrl);
                            }
                        });
                    } else {
                        Tools.showToast(TaskFinishActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskFinishActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskFinishActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    /**
     * 获取数据&加载布局
     */
    private void getData() {
        isRefreshing = true;
        taskdetail.sendPostRequest(Urls.Taskdetail, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        taskfinish_layout.removeAllViews();
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            FinishpackageView finishpackageView = new FinishpackageView(TaskFinishActivity.this,
                                    jsonObject.getString("pname"));
                            finishpackageView.setPackid(jsonObject.getString("pid"));
                            finishpackageView.setIntent(data);
                            String isclose = jsonObject.getString("isclose");
                            if (TextUtils.isEmpty(isclose) || isclose.equals("null")) {//底层任务
                                creatView(null, new TaskFinishInfo(), jsonObject.getJSONArray("datas").getJSONObject(0)
                                        .getJSONArray("datas"));
                            } else {
                                finishpackageView.setTag(jsonObject);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.topMargin = Tools.dipToPx(TaskFinishActivity.this, 15);
                                taskfinish_layout.addView(finishpackageView, lp);
                            }
                        }
                    } else {
                        Tools.showToast(TaskFinishActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskFinishActivity.this, getResources().getString(R.string.network_error));
                }
                isRefreshing = false;
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                isRefreshing = false;
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskFinishActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void creatView(FinishcategoryView finishcategoryView, TaskFinishInfo taskFinishInfo1, JSONArray jsonArray) throws
            JSONException {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = Tools.dipToPx(this, 15);
        if (jsonArray == null) {
            return;
        }
        String pid = taskFinishInfo1.getPid();
        int length = jsonArray.length();
        JSONObject jsonObject;
        for (int i = 0; i < length; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            String task_type = jsonObject.getString("task_type");
            TaskFinishInfo taskFinishInfo = new TaskFinishInfo();
            taskFinishInfo.setPid(taskFinishInfo1.getPid());
            taskFinishInfo.setCategory1(taskFinishInfo1.getCategory1());
            taskFinishInfo.setCategory2(taskFinishInfo1.getCategory2());
            taskFinishInfo.setCategory3(taskFinishInfo1.getCategory3());
            taskFinishInfo.setStoreid(store_id);
            taskFinishInfo.setProjectname(projectname);
            taskFinishInfo.setStorename(store_name);
            taskFinishInfo.setStorenum(store_num);
            taskFinishInfo.setProjectid(project_id);
            taskFinishInfo.setIs_watermark(is_watermark);
            taskFinishInfo.setCode(code);
            taskFinishInfo.setBrand(brand);
            taskFinishInfo.setName(jsonObject.getString("task_name"));
            taskFinishInfo.setOutlet_batch(jsonObject.getString("outlet_batch"));
            taskFinishInfo.setP_batch(jsonObject.getString("p_batch"));
            if ("1".equals(task_type) || "8".equals(task_type)) {//拍照任务
                taskFinishInfo.setTaskid(jsonObject.getString("task_id"));
                String state = jsonObject.getString("state");
                taskFinishInfo.setCompression(photo_compression);
                String wuxiao = jsonObject.getString("wuxiao");//1为无效没有照片，0为有照片
                if ("1".equals(wuxiao)) {
                    FinishshotView finishshotView = new FinishshotView(TaskFinishActivity.this, taskFinishInfo, true, isAgain);
                    if ("0".equals(state)) {//等待上传
                        finishshotView.settingName(taskFinishInfo.getName());
                        finishshotView.setIsProgress(true);
                    } else {
                        String beizhu = jsonObject.getString("beizhu");
                        if (!TextUtils.isEmpty(beizhu) && !"null".equals(beizhu)) {
                            beizhu = beizhu.replaceAll("\\[\"", "").replaceAll("\"]", "");
                        } else {
                            beizhu = "";
                        }
                        finishshotView.settingNote(beizhu);
                        String video_datas = jsonObject.getString("photo_datas");
                        try {
                            video_datas = URLDecoder.decode(video_datas, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(video_datas) && !"null".equals(video_datas)) {
                            video_datas = video_datas.replaceAll("\\[\"", "").replaceAll("\"]", "");
                            if (!TextUtils.isEmpty(video_datas)) {//有链接
                                String[] vs = video_datas.split("\",\"");
                                finishshotView.settingValue(jsonObject.getString("task_name"), vs);
                            }
                        }
                    }
                    if (finishcategoryView != null) {
                        finishcategoryView.addView(finishshotView);
                    } else {
                        taskfinish_layout.addView(finishshotView, lp);
                    }
                } else if ("0".equals(wuxiao)) {
                    FinishtaskView finishtaskView = new FinishtaskView(TaskFinishActivity.this, taskFinishInfo, isAgain);
                    views.add(finishtaskView);
                    if ("0".equals(state)) {
                        finishtaskView.settingName(taskFinishInfo.getName());
                        finishtaskView.setIsProgress(true);
                    } else {
                        finishtaskView.setTask_type(task_type);
                        if ("1".equals(jsonObject.getString("note_type"))) {//单备注
                            String photos = jsonObject.getString("photo_datas");
                            try {
                                photos = URLDecoder.decode(photos, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            ArrayList<String> list = new ArrayList<String>();
                            if (!TextUtils.isEmpty(photos) && !"null".equals(photos)) {
                                photos = photos.replaceAll("\\[\"", "").replaceAll("\"]", "");
                                String[] photo_datas = photos.split(",");
                                Collections.addAll(list, photo_datas);
                            } else {
                                String[] paths = updataDBHelper.getTaskFiles(username, taskFinishInfo.getProjectid(),
                                        taskFinishInfo.getStoreid(),
                                        taskFinishInfo.getPid(), taskFinishInfo.getCategory1(), taskFinishInfo.getCategory2(),
                                        taskFinishInfo.getCategory3(), taskFinishInfo.getTaskid());
                                if (paths != null) {
                                    Collections.addAll(list, paths);
                                }
                            }
                            finishtaskView.settingValue(jsonObject.getString("task_name"), list, jsonObject
                                    .getString("beizhu").replaceAll("\\[\"", "").replaceAll("\"]", ""));
                        } else {//多备注
                            String photos = jsonObject.getString("photo_datas");
                            try {
                                photos = URLDecoder.decode(photos, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            ArrayList<String[]> list = new ArrayList<String[]>();
                            if (!TextUtils.isEmpty(photos) && !"null".equals(photos)) {
                                String[] photo_datas = photos.replaceAll("\\[\"", "").replaceAll("\"]", "").split("\",\"");
                                String[] beizhu = jsonObject.getString("beizhu").replaceAll("\\[\"", "")
                                        .replaceAll("\"]", "").split("\",\"");
                                for (int index = 0; index < photo_datas.length; index++) {
                                    String[] str = new String[2];
                                    str[0] = photo_datas[index];
                                    if (index < beizhu.length) {
                                        str[1] = beizhu[index];
                                    } else {
                                        str[1] = "";
                                    }
                                    if ("null".equals(str[1])) {
                                        str[1] = "";
                                    }
                                    list.add(str);
                                }
                            } else {
                                String[] paths = updataDBHelper.getTaskFiles2(username, taskFinishInfo.getProjectid(),
                                        taskFinishInfo.getStoreid(),
                                        taskFinishInfo.getPid(), taskFinishInfo.getCategory1(), taskFinishInfo.getCategory2(),
                                        taskFinishInfo.getCategory3(), taskFinishInfo.getTaskid());
                                for (int index = 0; index < paths.length; index++) {
                                    String[] str = new String[2];
                                    String[] temps = paths[index].split("&");
                                    if (temps.length > 1) {
                                        str[0] = temps[0];
                                        str[1] = temps[1];
                                    } else {
                                        str[0] = temps[0];
                                        str[1] = "";
                                    }
                                    list.add(str);
                                }
                            }
                            finishtaskView.settingValue(jsonObject.getString("task_name"), list);
                        }
                        //拍照问题载入
                        JSONArray questionlist = jsonObject.optJSONArray("questionlist");
                        if (questionlist != null && questionlist.length() >= 1) {
                            JSONObject question = questionlist.getJSONObject(0);
                            String question_type = question.getString("question_type");
                            String[] answers = question.getString("answers").split(",");
                            String[] notes = question.getString("note").split("&&");
                            if (answers != null && !answers[0].equals("null")) {
                                boolean isrequired = "1".equals(question.getString("isrequired"));
                                if ("1".equals(question_type)) {//单选
                                    TaskRadioNoEditView taskRadioNoEditView = new TaskRadioNoEditView(this);
                                    taskRadioNoEditView.setTitle(question.getString("question_name"), isrequired);
                                    JSONArray jsonArray1 = question.getJSONArray("options");
                                    int count = jsonArray1.length();
                                    for (int j = 0; j < count; j++) {
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                        String id = jsonObject1.getString("id");
                                        if ("1".equals(jsonObject1.getString("isfill"))) {
                                            String note = "";
                                            if (notes.length > 0) {
                                                note = notes[0];
                                            }
                                            taskRadioNoEditView.addRadioButtonForFill(id, jsonObject1.getString
                                                    ("option_name"), jsonObject1.getString("isforcedfill"), note
                                                    .trim(), answers[0].equals(id), jsonObject1.getString("photo_url").replaceAll("\"", ""));
                                        } else {
                                            taskRadioNoEditView.addRadioButton(id, jsonObject1.getString("option_name"),
                                                    answers[0].equals(id), jsonObject1.getString("photo_url").replaceAll("\"", ""));
                                        }
                                    }
                                    finishtaskView.insertQuestion(taskRadioNoEditView);
                                } else if ("2".equals(question_type)) {//多选
                                    TaskCheckNoEditView taskCheckView = new TaskCheckNoEditView(this);
                                    taskCheckView.setTitle(question.getString("question_name"), isrequired);
                                    JSONArray jsonArray1 = question.getJSONArray("options");
                                    int count = jsonArray1.length();
                                    boolean ishad;
                                    int anwsernum = -1;
                                    for (int j = 0; j < count; j++) {
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                        String id = jsonObject1.getString("id");
                                        ishad = isHad(answers, id);
                                        if ("1".equals(jsonObject1.getString("isfill"))) {
                                            if (ishad) anwsernum++;
                                            String note = "";
                                            if (ishad && anwsernum < notes.length) {
                                                note = notes[anwsernum];
                                            }
                                            taskCheckView.addCheckBoxForFill(jsonObject1.getString("option_name"), jsonObject1
                                                    .getString("isforcedfill"), jsonObject1.getString("id"), 0, note, ishad, jsonObject1.getString("photo_url"));
                                        } else {
                                            taskCheckView.addCheckBox(id, jsonObject1.getString("option_name"), ishad, jsonObject1.getString("photo_url"));
                                        }
                                    }
                                    finishtaskView.insertQuestion(taskCheckView);
                                }
                            }
                        }
                    }
                    if (finishcategoryView != null) {
                        finishcategoryView.addView(finishtaskView);
                    } else {
                        taskfinish_layout.addView(finishtaskView, lp);
                    }
                }
            } else if ("2".equals(task_type)) {//视频任务
                taskFinishInfo.setTaskid(jsonObject.getString("task_id"));
                String state = jsonObject.getString("state");//判断是否上传完成 0为执行完成，1为资料回收
                FinishshotView finishshotView = new FinishshotView(TaskFinishActivity.this, taskFinishInfo, false, isAgain);
                views.add(finishshotView);
                if ("0".equals(state)) {//上传未完成
                    finishshotView.settingName(taskFinishInfo.getName());
                    finishshotView.setIsProgress(true);
                } else {
                    String video_datas = jsonObject.getString("video_datas");
                    String[] beizhu = jsonObject.getString("beizhu").replaceAll("\\[\"", "").replaceAll("\"]", "").split("\",\"");
                    if (beizhu.length > 0) {
                        finishshotView.settingNote(beizhu[0]);
                    }
                    if (!TextUtils.isEmpty(video_datas) && !"null".equals(video_datas)) {
                        video_datas = video_datas.replaceAll("\\[\"", "").replaceAll("\"]", "");
                        if (!TextUtils.isEmpty(video_datas)) {//有链接
                            String[] vs = video_datas.split("\",\"");
                            finishshotView.settingValue(jsonObject.getString("task_name"), vs);
                        }
                    }
                }
                taskfinish_layout.addView(finishshotView, lp);
            } else if ("3".equals(task_type)) {//记录任务
                if (finishcategoryView != null) {
                    finishcategoryView.addView(settingQuestion(jsonObject, taskFinishInfo));
                } else {
                    taskfinish_layout.addView(settingQuestion(jsonObject, taskFinishInfo), lp);
                }
            } else if ("4".equals(task_type)) {//定位任务
                String picStr = jsonObject.getString("positionpic");
                taskFinishInfo.setTaskid(jsonObject.getString("task_id"));
                String state = jsonObject.getString("state");
                FinishmapView finishmapView = new FinishmapView(TaskFinishActivity.this, taskFinishInfo, isAgain);
                views.add(finishmapView);
                if ("0".equals(state)) {//上传未完成
                    finishmapView.setIsprogress(true);
                } else {
                    if (!TextUtils.isEmpty(picStr) && !"null".equals(picStr)) {
                        finishmapView.settingValue(jsonObject.getString("task_name"), picStr);
                    } else {
                        String[] paths = updataDBHelper.getTaskFiles(username, taskFinishInfo.getProjectid(),
                                taskFinishInfo.getStoreid(),
                                taskFinishInfo.getPid(), taskFinishInfo.getCategory1(), taskFinishInfo.getCategory2(),
                                taskFinishInfo.getCategory3(), taskFinishInfo.getTaskid());
                        if (paths != null)
                            finishmapView.settingValue(jsonObject.getString("task_name"), paths[0]);
                    }
                }
                if (finishcategoryView != null) {
                    finishcategoryView.addView(finishmapView);
                } else {
                    taskfinish_layout.addView(finishmapView, lp);
                }
            } else if ("5".equals(task_type)) {//录音任务
                String soundStr = jsonObject.getString("sound_datas");
                taskFinishInfo.setTaskid(jsonObject.getString("task_id"));
                String state = jsonObject.getString("state");
                String[] soundStrs = null;
                FinishrecodeView finishrecodeView = new FinishrecodeView(TaskFinishActivity.this, jsonObject.getString
                        ("task_name"), taskFinishInfo, isAgain);
                if ("0".equals(state)) {
                    finishrecodeView.setIsProgress(true);
                } else {
                    try {
                        soundStr = URLDecoder.decode(soundStr.replaceAll("\\[\"", "").replaceAll("\"]", ""), "utf-8");
                        soundStrs = soundStr.split("&&");
                    } catch (UnsupportedEncodingException e) {
                        MobclickAgent.reportError(TaskFinishActivity.this, "录音地址解析ERROR：" + soundStr);
                        Tools.showToast(TaskFinishActivity.this, "录音路径解析失败");
                    }
                    if (soundStrs == null || soundStrs.length == 0) {
                        String[] paths = updataDBHelper.getTaskFiles(username, taskFinishInfo.getProjectid(),
                                taskFinishInfo.getStoreid(),
                                taskFinishInfo.getPid(), taskFinishInfo.getCategory1(), taskFinishInfo.getCategory2(),
                                taskFinishInfo.getCategory3(), taskFinishInfo.getTaskid());
                        if (paths != null)
                            soundStrs = paths;
                    }
                    finishrecodeView.settingRecs(this, soundStrs);
                    finishrecodeView.settingNote(jsonObject.getString("beizhu"));
                }
                if (finishcategoryView != null) {
                    finishcategoryView.addView(finishrecodeView);
                } else {
                    taskfinish_layout.addView(finishrecodeView, lp);
                }
            } else if ("6".equals(task_type)) {//扫码任务
                if (finishcategoryView != null) {
                    finishcategoryView.addView(settingView(jsonObject, taskFinishInfo));
                } else {
                    taskfinish_layout.addView(settingView(jsonObject, taskFinishInfo), lp);
                }
            } else if ("7".equals(task_type)) {//电话任务
                if (finishcategoryView != null) {
                    finishcategoryView.addView(settingCallView(jsonObject, taskFinishInfo));
                } else {
                    taskfinish_layout.addView(settingCallView(jsonObject, taskFinishInfo), lp);
                }
            } else if ("9".equals(task_type)) {//网页体验任务
                taskFinishInfo.setTaskid(jsonObject.getString("task_id"));
                WebpageTaskView webpageTaskView = new WebpageTaskView(this, taskFinishInfo, isAgain);
                webpageTaskView.setTaskName(jsonObject.getString("task_name"));
                if ("0".equals(state)) {
                    webpageTaskView.setIsProgress(true);
                }
                if (finishcategoryView != null) {
                    finishcategoryView.addView(webpageTaskView);
                } else {
                    taskfinish_layout.addView(webpageTaskView, lp);
                }
            } else {
                continue;
            }
        }
    }

    /**
     * 扫码任务查看详情
     */
    private FinishScanView settingView(JSONObject data, TaskFinishInfo taskFinishInfo) throws JSONException {
        FinishScanView finishScanView = new FinishScanView(this, taskFinishInfo, isAgain);
        views.add(finishScanView);
        taskFinishInfo.setTaskid(data.getString("task_id"));
        String state = data.getString("state");
        finishScanView.settingValue(data.getString("task_name"));
        if ("0".equals(state)) {
            finishScanView.setIsProgress(true);
        } else {
            String scannum = data.getString("scannum");
            String unscannum = data.getString("unscannum");
            ArrayList<ScanTaskInfo> list = new ArrayList<>();
            if (Tools.StringToInt(unscannum) > 0) {
                JSONArray jsonArray = data.getJSONArray("standard");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ScanTaskInfo scanTaskInfo = new ScanTaskInfo();
                    scanTaskInfo.setName(object.getString("name"));
                    scanTaskInfo.setBarcode(object.getString("barcode"));
                    scanTaskInfo.setSize(object.getString("size"));
                    scanTaskInfo.setPicurl(object.getString("picurl"));
                    list.add(scanTaskInfo);
                }
            }
            finishScanView.setData(this, scannum, unscannum, list);
        }
        return finishScanView;
    }

    /**
     * 电话任务查看详情
     */
    public CallTaskView settingCallView(JSONObject data, TaskFinishInfo taskFinishInfo) throws JSONException {
        CallTaskView callTaskView = new CallTaskView(this, taskFinishInfo, isAgain);
        taskFinishInfo.setTaskid(data.getString("task_id"));
        views.add(callTaskView);
        String task_name = data.getString("task_name");
        String wuxiao = data.getString("wuxiao");
        String state = data.getString("state");
        if ("0".equals(state)) {
            callTaskView.setIsProgress(true);
        } else {
            if ("1".equals(wuxiao)) {//是否执无效
                String note = data.getString("note");
                callTaskView.setData2(task_name, wuxiao, note);
            } else {
                String soundStr = data.getString("sound_datas");
                try {
                    soundStr = URLDecoder.decode(soundStr.replaceAll("\\[\"", "").replaceAll("\"]", ""), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    MobclickAgent.reportError(TaskFinishActivity.this, "录音地址解析ERROR：" + soundStr);
                    Tools.showToast(TaskFinishActivity.this, "录音地址异常");
                }
                callTaskView.settingRecSrc(soundStr);
            }
        }
        return callTaskView;
    }

    /**
     * TODO 显示问题
     */
    private FinisheditView settingQuestion(JSONObject data, TaskFinishInfo taskFinishInfo) throws JSONException {
        FinisheditView finisheditView = new FinisheditView(this, taskFinishInfo, isAgain);
        String state = data.getString("state");
        if ("0".equals(state)) {
            finisheditView.settingValue(data.getString("task_name"));
            finisheditView.setIsProgress(true);
            return finisheditView;
        }
        taskFinishInfo.setTaskid(data.getString("task_id"));
        finisheditView.settingValue(data.getString("task_name"));
        JSONArray jsonArray = data.getJSONArray("questionlist");
        JSONArray jsonArrayAswer = null;
        int length = jsonArray.length();
        if ("1".equals(state)) {
            String answerstr = updataDBHelper.getEditTextAnswers(username, taskFinishInfo.getProjectid(),
                    taskFinishInfo.getStoreid(),
                    taskFinishInfo.getPid(), taskFinishInfo.getCategory1(), taskFinishInfo.getCategory2(),
                    taskFinishInfo.getCategory3(), taskFinishInfo.getTaskid());
            if (!TextUtils.isEmpty(answerstr)) {
                try {
                    answerstr = URLDecoder.decode(answerstr, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Tools.showToast(this, "记录任务答案解析失败");
                    return finisheditView;
                }
                jsonArrayAswer = new JSONObject(answerstr).getJSONArray("answers");
            }
        }
        JSONObject jsonObject;
        for (int i = 0; i < length; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            String type = jsonObject.getString("question_type");
            boolean isrequired = "1".equals(jsonObject.getString("isrequired"));//true 必填 false 不必填
            String[] answers = null;
            String[] notes = null;
            if ("1".equals(state) && jsonArrayAswer != null) {
                JSONObject jsonObjectAnswer = jsonArrayAswer.getJSONObject(i);
                answers = jsonObjectAnswer.getString("answers").split(",");
                String note = jsonObjectAnswer.optString("note");
                if (!TextUtils.isEmpty(note)) {
                    notes = note.split("&&");
                }
            } else {
                answers = jsonObject.getString("answers").split(",");
                String note = jsonObject.optString("note");
                if (!TextUtils.isEmpty(note)) {
                    notes = note.split("&&");
                }
            }
            if ("1".equals(type)) {//单选
                TaskRadioNoEditView taskRadioNoEditView = new TaskRadioNoEditView(this);
                taskRadioNoEditView.setTitle(jsonObject.getString("question_num") + "、" + jsonObject.getString("question_name"), isrequired);
                JSONArray jsonArray1 = jsonObject.getJSONArray("options");
                int count = jsonArray1.length();
                for (int j = 0; j < count; j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String id = jsonObject1.getString("id");
                    if ("1".equals(jsonObject1.getString("isfill"))) {
                        String note = "";
                        if (notes != null && notes.length > 0) {
                            note = notes[0];
                        }
                        taskRadioNoEditView.addRadioButtonForFill(id, jsonObject1.getString("option_name"), jsonObject1
                                .getString("isforcedfill"), note.trim(), answers[0].equals(id), jsonObject1.getString("photo_url").replaceAll("\"", ""));
                    } else {
                        taskRadioNoEditView.addRadioButton(id, jsonObject1.getString("option_name"), answers[0].equals(id), jsonObject1.getString("photo_url"));
                    }
                }
                finisheditView.addChildView(taskRadioNoEditView);
            } else if ("2".equals(type)) {//多选
                TaskCheckNoEditView taskCheckView = new TaskCheckNoEditView(this);
                taskCheckView.setTitle(jsonObject.getString("question_num") + "、" + jsonObject.getString("question_name"), isrequired);
                JSONArray jsonArray1 = jsonObject.getJSONArray("options");
                int count = jsonArray1.length();
                boolean ishad;
                int anwsernum = -1;
                for (int j = 0; j < count; j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String id = jsonObject1.getString("id");
                    ishad = isHad(answers, id);
                    if ("1".equals(jsonObject1.getString("isfill"))) {
                        if (ishad) anwsernum++;
                        String note = "";
                        if (notes != null && ishad && anwsernum < notes.length) {
                            note = notes[anwsernum];
                        }
                        taskCheckView.addCheckBoxForFill(jsonObject1.getString("option_name"), jsonObject1.getString
                                ("isforcedfill"), jsonObject1.getString("id"), 0, note, ishad, jsonObject1.getString("photo_url"));
                    } else {
                        taskCheckView.addCheckBox(id, jsonObject1.getString("option_name"), ishad, jsonObject1.getString("photo_url"));
                    }
                }
                finisheditView.addChildView(taskCheckView);
            } else if ("3".equals(type)) {//判断
                TaskJudgeNoEditView taskJudgeNoEditView = new TaskJudgeNoEditView(this, jsonObject.getString("question_num") + "、" + jsonObject.getString
                        ("question_name"), answers[0], isrequired);
                finisheditView.addChildView(taskJudgeNoEditView);
            } else if ("4".equals(type)) {//填空
                if ("1".equals(jsonObject.optString("switch_to_voice"))) {//Urls.EndpointDir + "/" + objectKey
                    TaskEditView2 taskEditView = new TaskEditView2(this, jsonObject.getString("question_num") + "、" + jsonObject.getString
                            ("question_name"), isrequired);
                    String netUrl = jsonObject.getString("answers_url");
                    taskEditView.setEditValue(answers[0]);
                    if (!TextUtils.isEmpty(netUrl) && !"null".equals(netUrl)) {
                        taskEditView.setRecUrl(netUrl);
                    }
                    taskEditView.isSelect(false);
                    finisheditView.addChildView(taskEditView);
                } else {
                    TaskEditNoEditView taskEditNoEditView = new TaskEditNoEditView(this, jsonObject.getString("question_num") + "、" + jsonObject.getString
                            ("question_name"), answers[0], isrequired);
                    finisheditView.addChildView(taskEditNoEditView);
                }
            } else if ("5".equals(type)) {//时间
                TaskTimeSelNoEditView taskTimeSelNoEditView = new TaskTimeSelNoEditView(this, jsonObject.getString("question_num") + "、" + jsonObject.getString
                        ("question_name"), answers[0], isrequired);
                finisheditView.addChildView(taskTimeSelNoEditView);
            } else if ("6".equals(type)) {//语音题
                TaskEditView2 taskEditView = new TaskEditView2(this, jsonObject.getString("question_num") + "、" + jsonObject.getString
                        ("question_name"), isrequired, true);
                taskEditView.setEditValue(answers[0]);
                String netUrl = jsonObject.getString("answers_url");
                taskEditView.setRecUrl(jsonObject.getString("answers_url"));
                if (!TextUtils.isEmpty(netUrl) && !"null".equals(netUrl)) {
                    taskEditView.setRecUrl(netUrl);
                }
                taskEditView.isSelect(false);
                finisheditView.addChildView(taskEditView);
            }
        }
//        taskfinish_layout.addView(finisheditView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
//                .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return finisheditView;
    }

    private boolean isHad(String[] strs, String string) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals(string)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRefreshing = false;
    private BroadcastReceiver UpProgressbarBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(AppInfo.BroadcastReceiver_TAKEPHOTO)) {
                String username = intent.getStringExtra("username");
                String projectid = intent.getStringExtra("projectid");
                String storeid = intent.getStringExtra("storeid");
                String taskpackid = intent.getStringExtra("taskpackid");
                String taskid = intent.getStringExtra("taskid");
                int progress = intent.getIntExtra("size", 0);
                if (!isRefreshing && TextUtils.isEmpty(taskpackid) && !TextUtils.isEmpty(taskid) &&
                        AppInfo.getName(TaskFinishActivity.this).equals(username) &&
                        !TextUtils.isEmpty(projectid) && projectid.equals(project_id) &&
                        !TextUtils.isEmpty(storeid) && storeid.equals(TaskFinishActivity.this.store_id)) {
                    for (BaseView baseView : views) {
                        if (isRefreshing) {
                            break;
                        }
                        if (((TaskFinishInfo) baseView.getBaseData()).getTaskid().equals(taskid)) {
                            ((FinishTaskProgressRefresh) baseView).setProgress(progress);
                            if (progress == 100) {
                                getData();
                            }
//                            temp.getTaskitemDetail_12View().settingProgressbar(progress);
                            break;
                        }
                    }
                }
            }
        }
    };

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(UpProgressbarBroadcast, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(UpProgressbarBroadcast);
    }
}
