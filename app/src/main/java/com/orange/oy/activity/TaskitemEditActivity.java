package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.allinterface.OnTaskEditRefreshListener;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskEditInfo;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.CaptureWindow;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.util.TaskQuestionComparator;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.TaskCheckView;
import com.orange.oy.view.TaskEditView;
import com.orange.oy.view.TaskEditView2;
import com.orange.oy.view.TaskEditViewforCapture;
import com.orange.oy.view.TaskJudgeView;
import com.orange.oy.view.TaskRadioView;
import com.orange.oy.view.TaskTimeSelView;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.support.v4.widget.TextViewCompat.getMaxLines;

/**
 * 任务列表-问卷调查页
 */
public class TaskitemEditActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        OnTaskQuestionSumbitListener, AdapterView.OnItemClickListener, View.OnClickListener, OnTaskEditRefreshListener {

    private AppTitle taskitemedit_title;

    private void initTitle() {
        taskitemedit_title = (AppTitle) findViewById(R.id.taskitemedit_title);
        if (index != null && "0".equals(index)) {
            taskitemedit_title.settingName("问卷任务（预览）");
        } else {
            taskitemedit_title.settingName("问卷任务");
        }
        if (!"1".equals(newtask)) {//不是新手
            taskitemedit_title.showBack(this);
        }
        if ("1".equals(is_desc)) {
            taskitemedit_title.setIllustrate(new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    Intent intent = new Intent(TaskitemEditActivity.this, StoreDescActivity.class);
                    intent.putExtra("id", storeid);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("is_task", true);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CaptureWindow.closeWindow();
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (Record != null) {
            Record.stop(Urls.Record);
        }
        if (Recordup != null) {
            Recordup.stop(Urls.Recordup1_3);
        }
        if (service != null && !RecordService.isStart()) {
            service.setClass(this, RecordService.class);
            String filename = service.getStringExtra("fileName");
            service.putExtra("fileName", filename + "_" + Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss"));
            startService(service);
        }
    }

    private String taskid, tasktype, pid, storeid;
    private String newtask;//判断是否是新手任务 1是0否

    private void initNetworkConnection() {
        Record = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Recordup = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("pid", pid);
                params.put("usermobile", AppInfo.getName(TaskitemEditActivity.this));
                params.put("storeid", storeid);
                params.put("token", Tools.getToken());
                params.put("category1", category1);
                params.put("category2", category2);
                params.put("category3", category3);
                params.put("time", time);
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                return params;
            }
        };
        Recordup.setIsShowDialog(true);
    }

    private String answerJson;

    private ImageLoader imageLoader;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this);
        }
        PhotoView imageView = new PhotoView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageLoader.DisplayImage(picList.get(position), imageView);
        SelecterDialog.showView(this, imageView);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main: {
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            break;
            case R.id.taskitemedit_spread: {
                if (getMaxLines(taskitemedit_desc) == 1) {
                    taskitemedit_desc.setMaxLines(100);
                    if (picList != null && !picList.isEmpty()) {
                        example.setVisibility(View.VISIBLE);
                        taskitmpg_gridview.setVisibility(View.VISIBLE);
                    }
                    taskitemedit_spread.setImageResource(R.mipmap.spread_button_up);
                } else {
                    taskitemedit_desc.setMaxLines(1);
                    example.setVisibility(View.GONE);
                    taskitmpg_gridview.setVisibility(View.GONE);
                    taskitemedit_spread.setImageResource(R.mipmap.spread_button_down);
                }
            }
            break;
        }
    }

    class getAnswersAsyncTask extends AsyncTask {
        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(TaskitemEditActivity.this, "校验中...");
        }

        protected Object doInBackground(Object[] params) {
            answerJson = getAnswers();
            return null;
        }

        protected void onPostExecute(Object o) {
            CustomProgressDialog.Dissmiss();
            Tools.d(answerJson);
            if (answerJson != null && answerJson.startsWith("error")) {
                String toast = answerJson.replaceAll("error", "");
                Tools.showToast(TaskitemEditActivity.this, toast);
            } else if (answerJson != null) {
                recList.size();
                Recordup();
            }
        }
    }

    private UpdataDBHelper updataDBHelper;

    private void Recordup() {
        Recordup.sendPostRequest(Urls.Recordup1_3, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    String executeid = jsonObject.getString("executeid");
                    if (code == 200 || code == 2) {
                        String usermobile = AppInfo.getName(TaskitemEditActivity.this);
                        Map<String, String> parames = new HashMap<>();
                        parames.put("taskid", taskid);
                        parames.put("pid", pid);
                        parames.put("usermobile", usermobile);
                        parames.put("storeid", storeid);
                        parames.put("token", Tools.getToken());
                        parames.put("category1", category1);
                        parames.put("category2", category2);
                        parames.put("category3", category3);
                        parames.put("batch", batch);
                        parames.put("outlet_batch", outlet_batch);
                        parames.put("p_batch", p_batch);
                        parames.put("time", time);
                        try {
                            if (isHavREC && recList != null && !recList.isEmpty()) {// 有录音按照正常模式存储
                                String keys = null, paths = null;
                                int size = recList.size();
                                for (int i = 0; i < size; i++) {
                                    if (TextUtils.isEmpty(keys)) {
                                        keys = "path" + i;
                                    } else {
                                        keys = keys + ",path" + i;
                                    }
                                    if (TextUtils.isEmpty(paths)) {
                                        paths = recList.get(i)[1];
                                    } else {
                                        paths = paths + "," + recList.get(i)[1];
                                    }
                                }
                                Tools.d("有录音");
                                updataDBHelper.addUpdataTask(usermobile, projectid, project_name, store_num, null,
                                        storeid, store_name, pid,
                                        task_pack_name, AppInfo.TASKITEMEDIT_TASKTYPE, taskid, task_name, null, null, null,
                                        usermobile + projectid + storeid + pid + category1 + category2 + category3 + taskid
                                        , Urls.Recordup, keys, paths, UpdataDBHelper.Updata_file_type_video,
                                        paramsToMap(executeid), null, false, null, null, false);
                            } else {
                                Tools.d("没录音");
                                updataDBHelper.addUpdataTask(usermobile, projectid, project_name, store_num, null, storeid, store_name,
                                        pid, task_pack_name, "3", taskid, task_name, category1, category2, category3,
                                        usermobile + projectid + storeid + pid + category1 + category2 + category3 + taskid,
                                        Urls.Recordup1_3,
                                        null, null, UpdataDBHelper.Updata_file_type_video, parames, null, true, Urls.Recordup,
                                        paramsToString(), false);
                            }
                            TaskitemDetailActivity.isRefresh = true;
                            TaskitemDetailActivity_12.isRefresh = true;
                            TaskitemDetailActivity.taskid = taskid;
                            TaskitemDetailActivity_12.taskid = taskid;
                            TaskFinishActivity.isRefresh = true;
                            TaskitemListActivity.isRefresh = true;
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                            TaskitemListActivity_12.isRefresh = true;
                            if (code == 2) {
                                ConfirmDialog.showDialog(TaskitemEditActivity.this, null, jsonObject.getString("msg"), null,
                                        "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                            @Override
                                            public void leftClick(Object object) {

                                            }

                                            @Override
                                            public void rightClick(Object object) {
                                                baseFinish();
                                            }
                                        }).goneLeft();
                            } else if (code == 200) {
                                if ("1".equals(newtask)) {//新手任务
                                    ArrayList<TaskNewInfo> list = (ArrayList<TaskNewInfo>) getIntent().getBundleExtra("data").getSerializable("list");
                                    if (list != null) {
                                        if (!list.isEmpty()) {
                                            TaskNewInfo taskNewInfo = list.remove(0);
                                            String type = taskNewInfo.getTask_type();
                                            Intent intent = new Intent();
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("list", list);
                                            intent.putExtra("data", bundle);
                                            intent.putExtra("project_id", taskNewInfo.getProjectid());
                                            intent.putExtra("project_name", taskNewInfo.getProject_name());
                                            intent.putExtra("task_pack_id", "");
                                            intent.putExtra("task_pack_name", "");
                                            intent.putExtra("task_id", taskNewInfo.getTask_id());
                                            intent.putExtra("task_name", taskNewInfo.getTask_name());
                                            intent.putExtra("store_id", taskNewInfo.getStore_id());
                                            intent.putExtra("store_num", taskNewInfo.getStore_num());
                                            intent.putExtra("store_name", taskNewInfo.getStore_name());
                                            intent.putExtra("category1", "");
                                            intent.putExtra("category2", "");
                                            intent.putExtra("category3", "");
                                            intent.putExtra("is_desc", "");
                                            intent.putExtra("code", taskNewInfo.getCode());
                                            intent.putExtra("brand", taskNewInfo.getBrand());
                                            intent.putExtra("outlet_batch", taskNewInfo.getOutlet_batch());
                                            intent.putExtra("p_batch", taskNewInfo.getP_batch());
                                            intent.putExtra("newtask", "1");//判断是否是新手任务 1是0否
                                            intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                            if ("1".equals(type) || "8".equals(type)) {//拍照任务
                                                intent.setClass(TaskitemEditActivity.this, TaskitemPhotographyNextYActivity.class);
                                                startActivity(intent);
                                            } else if ("2".equals(type)) {//视频任务
                                                intent.setClass(TaskitemEditActivity.this, TaskitemShotActivity.class);
                                                startActivity(intent);
                                            } else if ("3".equals(type)) {//记录任务
                                                intent.setClass(TaskitemEditActivity.this, TaskitemEditActivity.class);
                                                startActivity(intent);
                                            } else if ("4".equals(type)) {//定位任务
                                                intent.setClass(TaskitemEditActivity.this, TaskitemMapActivity.class);
                                                startActivity(intent);
                                            } else if ("5".equals(type)) {//录音任务
                                                intent.setClass(TaskitemEditActivity.this, TaskitemRecodillustrateActivity.class);
                                                startActivity(intent);
                                            } else if ("6".equals(type)) {//扫码任务
                                                intent.setClass(TaskitemEditActivity.this, ScanTaskNewActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                }
                                baseFinish();
                            }
                        } catch (UnsupportedEncodingException e) {
                            Tools.showToast(TaskitemEditActivity.this, "数据传输存储失败！");
                        }
                    } else {
                        Tools.showToast(TaskitemEditActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemEditActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemEditActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在提交...");
    }

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        String question_ids = "";
        for (String[] strings : recList) {
            question_ids = strings[0] + "," + strings[1] + ";";
        }
        if (!TextUtils.isEmpty(question_ids))
            question_ids = question_ids.substring(0, question_ids.length() - 1);
        parames.put("question_ids", question_ids);
        parames.put("taskid", taskid);
        parames.put("pid", pid);
        parames.put("usermobile", AppInfo.getName(TaskitemEditActivity.this));
        parames.put("storeid", storeid);
        parames.put("answers", answerJson);
        parames.put("token", Tools.getToken());
        parames.put("category1", category1);
        parames.put("category2", category2);
        parames.put("category3", category3);
        parames.put("batch", batch);
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        String data = "";
        Iterator<String> iterator = parames.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            } else {
                data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            }
        }
        return data;
    }

    private Map<String, String> paramsToMap(String executeid) throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        String question_ids = "";
        for (String[] strings : recList) {
            question_ids = strings[0] + "," + strings[1] + ";";
        }
        if (!TextUtils.isEmpty(question_ids))
            question_ids = question_ids.substring(0, question_ids.length() - 1);
        parames.put("question_ids", question_ids);
        parames.put("executeid", executeid);
        parames.put("taskid", taskid);
        parames.put("pid", pid);
        parames.put("usermobile", AppInfo.getName(TaskitemEditActivity.this));
        parames.put("storeid", storeid);
        parames.put("answers", answerJson);
        parames.put("token", Tools.getToken());
        parames.put("category1", category1);
        parames.put("category2", category2);
        parames.put("category3", category3);
        parames.put("batch", batch);
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        return parames;
    }

    private String time;

    /**
     * TODO 获取答案
     *
     * @return json串格式
     */
    private String getAnswers() {
        time = Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss");
        try {
            int size = questionList.size();
            JSONObject answers = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            if (qusetionPosition != -1) {//单题模式
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = new JSONObject();
                    TaskEditInfo taskEditInfo = questionList.get(i);
                    jsonObject.put("question_id", taskEditInfo.getId());
                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                    jsonObject.put("answers", taskEditInfo.getAnswers());
                    jsonObject.put("note", taskEditInfo.getAnswersNote());
                    jsonArray.put(jsonObject);
                }
                answers.put("answers", jsonArray);
                answers.put("time", time);
                return answers.toString();
            } else {//多题模式
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = new JSONObject();
                    TaskEditInfo taskEditInfo = questionList.get(i);
                    /*判断题目类型*/
                    switch (Tools.StringToInt(taskEditInfo.getQuestion_type())) {
                        case 1: {
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskRadioView) {
                                TaskRadioView taskRadioView = (TaskRadioView) taskEditInfo.getView();
                                TaskQuestionInfo taskQuestionInfo = taskRadioView.getSelectAnswers();
                                if (taskEditInfo.getIsrequired().equals("1") && taskQuestionInfo == null
                                        && taskRadioView.getVisibility() == View.VISIBLE) {//必填
                                    return "error" + " 第" + taskEditInfo.getQuestion_num() + "题未填写！";
                                }
                                if (taskQuestionInfo != null && taskRadioView.getVisibility() == View.VISIBLE) {//如果做了
                                    if (taskQuestionInfo.getNoteEditext() != null && !TextUtils.isEmpty(taskQuestionInfo
                                            .getNoteEditext().getText().toString().trim())) {//判断备注
                                        jsonObject.put("note", taskQuestionInfo.getNoteEditext().getText().toString()
                                                .trim());
                                    } else if (taskQuestionInfo.isRequired()) {
                                        return "error 第" + taskEditInfo.getQuestion_num() + "道题单选选项备注有必填！";
                                    }
                                    jsonObject.put("answers", taskQuestionInfo.getId());
                                    jsonObject.put("question_id", taskEditInfo.getId());
                                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                } else {
                                    jsonObject.put("note", "");
                                    jsonObject.put("answers", "");
                                    jsonObject.put("question_id", taskEditInfo.getId());
                                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                }
                            } else {
                                return "error 数据错误，请重新打开页面";
                            }
                        }
                        break;
                        case 2: {
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskCheckView) {
                                TaskCheckView taskCheckView = (TaskCheckView) taskEditInfo.getView();
                                ArrayList<TaskQuestionInfo> taskQuestionInfos = taskCheckView.getSelectAnswer();
                                if (taskEditInfo.getIsrequired().equals("1") && taskQuestionInfos.isEmpty()
                                        && taskCheckView.getVisibility() == View.VISIBLE) {//必填
                                    return "error" + " 第" + taskEditInfo.getQuestion_num() + "题未填写！";
                                }
                                if (!taskQuestionInfos.isEmpty() && taskCheckView.getVisibility() == View.VISIBLE) {//如果做了
                                    int taskQuestionInfosSize = taskQuestionInfos.size();
                                    int maxOption = Tools.StringToInt(taskEditInfo.getMax_option());
                                    int minOptino = Tools.StringToInt(taskEditInfo.getMin_option());
                                    if (taskQuestionInfosSize < minOptino || (taskQuestionInfosSize > maxOption && maxOption > 0)) {
                                        return "error 第" + taskEditInfo.getQuestion_num() + "道题有选择数量应该大于" + minOptino +
                                                ",小于" + maxOption;
                                    }
                                    String ids = null, notes = null;
                                     /*判断选项备注*/
                                    for (int j = 0; j < taskQuestionInfosSize; j++) {
                                        TaskQuestionInfo taskQuestionInfo = taskQuestionInfos.get(j);
                                        if (taskQuestionInfo.getNoteEditext() != null && !TextUtils.isEmpty
                                                (taskQuestionInfo.getNoteEditext().getText().toString().trim())) {//判断备注
                                            String temp = taskQuestionInfo.getNoteEditext().getText().toString().trim()
                                                    .replaceAll("&&", "");
                                            if (TextUtils.isEmpty(temp)) {
                                                temp = " ";
                                            }
                                            if (notes == null) {
                                                notes = temp;
                                            } else {
                                                notes = notes + "&&" + temp;
                                            }
                                        } else if (taskQuestionInfo.isRequired()) {
                                            return "error 第" + taskEditInfo.getQuestion_num() + "道题多选选项备注有必填！";
                                        } else {//如果没有备注也要用分隔符分隔
                                            if (notes == null) {
                                                notes = "";
                                            } else {
                                                notes = notes + "&&";
                                            }
                                        }
                                        if (ids == null) {
                                            ids = taskQuestionInfo.getId();
                                        } else {
                                            ids = ids + "," + taskQuestionInfo.getId();
                                        }
                                    }
                                    jsonObject.put("answers", ids);
                                    jsonObject.put("note", notes);
                                    jsonObject.put("question_id", taskEditInfo.getId());
                                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                } else {
                                    jsonObject.put("note", "");
                                    jsonObject.put("answers", "");
                                    jsonObject.put("question_id", taskEditInfo.getId());
                                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                }
                            } else {
                                return "error 数据错误，请重新打开页面";
                            }
                        }
                        break;
                        case 3: {
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskJudgeView) {
                                TaskJudgeView taskJudgeView = (TaskJudgeView) taskEditInfo.getView();
                                if (taskEditInfo.getIsrequired().equals("1") && taskJudgeView.isRight() == -1
                                        && taskJudgeView.getVisibility() == View.VISIBLE) {//必填
                                    return "error" + " 第" + taskEditInfo.getQuestion_num() + "题未填写！";
                                }
                                if (taskJudgeView.isRight() != -1 && taskJudgeView.getVisibility() == View.VISIBLE) {
                                    jsonObject.put("answers", taskJudgeView.isRight() + "");
                                    jsonObject.put("note", "");
                                    jsonObject.put("question_id", taskEditInfo.getId());
                                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                } else {
                                    jsonObject.put("note", "");
                                    jsonObject.put("answers", "");
                                    jsonObject.put("question_id", taskEditInfo.getId());
                                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                }
                            } else {
                                return "error 数据错误，请重新打开页面";
                            }
                        }
                        break;
                        case 4: {
                            if (taskEditInfo.getView() != null) {
                                if (taskEditInfo.getView() instanceof TaskEditView) {
                                    TaskEditView taskEditView = (TaskEditView) taskEditInfo.getView();
                                    String text = taskEditView.getText();
                                    if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text)
                                            && taskEditView.getVisibility() == View.VISIBLE) {//必填
                                        return "error" + " 第" + taskEditInfo.getQuestion_num() + "题未填写！";
                                    }
                                    if (!TextUtils.isEmpty(text) && taskEditView.getVisibility() == View.VISIBLE) {
                                        int max = Tools.StringToInt(taskEditInfo.getMax_word_num());
                                        int min = Tools.StringToInt(taskEditInfo.getMin_word_num());
                                        int length = text.length();
                                        if (max == -1 && min == -1) {
                                            if (length > 500)
                                                return "error 第" + taskEditInfo.getQuestion_num() + "道题字数应小于500字";
                                        } else if ((length > max && max > 0) || length < min) {
                                            return "error 第" + taskEditInfo.getQuestion_num() + "道题字数要求大于" + min + "，小于"
                                                    + max;
                                        }
                                        jsonObject.put("note", "");
                                        jsonObject.put("answers", text);
                                        jsonObject.put("question_id", taskEditInfo.getId());
                                        jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                        jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                    } else {
                                        jsonObject.put("note", "");
                                        jsonObject.put("answers", "");
                                        jsonObject.put("question_id", taskEditInfo.getId());
                                        jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                        jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                    }
                                } else if (taskEditInfo.getView() instanceof TaskEditView2) {
                                    String gas = getAnswerForEditView2(jsonObject, taskEditInfo);
                                    if (gas != null) {
                                        return gas;
                                    }
                                } else if (taskEditInfo.getView() instanceof TaskEditViewforCapture) {//和普通填空题保持一致即可
                                    TaskEditViewforCapture taskEditView = (TaskEditViewforCapture) taskEditInfo.getView();
                                    String text = taskEditView.getText();
                                    if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text)
                                            && taskEditView.getVisibility() == View.VISIBLE) {//必填
                                        return "error" + " 第" + taskEditInfo.getQuestion_num() + "题未填写！";
                                    }
                                    if (!TextUtils.isEmpty(text) && taskEditView.getVisibility() == View.VISIBLE) {
                                        int max = Tools.StringToInt(taskEditInfo.getMax_word_num());
                                        int min = Tools.StringToInt(taskEditInfo.getMin_word_num());
                                        int length = text.length();
                                        if (max == -1 && min == -1) {
                                            if (length > 500)
                                                return "error 第" + taskEditInfo.getQuestion_num() + "道题字数应小于500字";
                                        } else if ((length > max && max > 0) || length < min) {
                                            return "error 第" + taskEditInfo.getQuestion_num() + "道题字数要求大于" + min + "，小于"
                                                    + max;
                                        }
                                        jsonObject.put("note", "");
                                        jsonObject.put("answers", text);
                                        jsonObject.put("question_id", taskEditInfo.getId());
                                        jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                        jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                    } else {
                                        jsonObject.put("note", "");
                                        jsonObject.put("answers", "");
                                        jsonObject.put("question_id", taskEditInfo.getId());
                                        jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                        jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                    }
                                } else {
                                    return "error 数据错误，请重新打开页面";
                                }
                            } else {
                                return "error 数据错误，请重新打开页面";
                            }
                        }
                        break;
                        case 5: {
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskTimeSelView) {
                                TaskTimeSelView taskTimeSelView = (TaskTimeSelView) taskEditInfo.getView();
                                String text = taskTimeSelView.getText();
                                if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text)
                                        && taskTimeSelView.getVisibility() == View.VISIBLE) {//必填
                                    return "error" + " 第" + taskEditInfo.getQuestion_num() + "题填写不完整！";
                                }
                                if (!TextUtils.isEmpty(text) && taskTimeSelView.getVisibility() == View.VISIBLE) {
                                    jsonObject.put("note", "");
                                    jsonObject.put("answers", text);
                                    jsonObject.put("question_id", taskEditInfo.getId());
                                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                } else {
                                    jsonObject.put("note", "");
                                    jsonObject.put("answers", "");
                                    jsonObject.put("question_id", taskEditInfo.getId());
                                    jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                    jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                }
                            } else {
                                return "error 数据错误，请重新打开页面";
                            }
                        }
                        break;
                        case 6: {//语音题1
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskEditView2) {
                                TaskEditView2 taskEditView2 = (TaskEditView2) taskEditInfo.getView();
                                String url = taskEditView2.getUrl();
                                if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(url)
                                        && taskEditView2.getVisibility() == View.VISIBLE) {//必填
                                    return "error" + " 第" + taskEditInfo.getQuestion_num() + "题未录音！";
                                }
                                jsonObject.put("note", "");
                                jsonObject.put("answers", "");
                                jsonObject.put("question_id", taskEditInfo.getId());
                                jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                if (!TextUtils.isEmpty(url) && taskEditView2.getVisibility() == View.VISIBLE) {
                                    String filename = new File(url).getName();
                                    jsonObject.put("answers_url", Urls.Endpoint2 + "/" + filename);
                                    recList.add(new String[]{taskEditInfo.getId(), taskEditView2.getUrl()});
                                } else {
                                    jsonObject.put("answers_url", "");
                                }
                            } else {
                                return "error 数据错误，请重新打开页面";
                            }
                        }
                        break;
                    }
                    jsonArray.put(jsonObject);
                }
                answers.put("answers", jsonArray);
                answers.put("time", time);
                return answers.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "error 数据错误，请重新打开页面";
        } catch (Exception e) {
            e.printStackTrace();
            return "error 数据错误，请重新打开页面";
        }
    }

    private ArrayList<String[]> recList = new ArrayList<>();//存储语音题的question_id和对应的url

    private String getAnswerForEditView2(JSONObject jsonObject, TaskEditInfo taskEditInfo) throws JSONException {
        TaskEditView2 taskEditView = (TaskEditView2) taskEditInfo.getView();
        String text = taskEditView.getText();
        String url = taskEditView.getUrl();
        if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text) && TextUtils.isEmpty(url)
                && taskEditView.getVisibility() == View.VISIBLE) {//必填
            return "error" + " 第" + taskEditInfo.getQuestion_num() + "题未填写！";
        }
        if (!TextUtils.isEmpty(text) && taskEditView.getVisibility() == View.VISIBLE) {
            int max = Tools.StringToInt(taskEditInfo.getMax_word_num());
            int min = Tools.StringToInt(taskEditInfo.getMin_word_num());
            int length = text.length();
            if (max == -1 && min == -1) {
                if (length > 500)
                    return "error 第" + taskEditInfo.getQuestion_num() + "道题字数应小于500字";
            } else if ((length > max && max > 0) || length < min) {
                return "error 第" + taskEditInfo.getQuestion_num() + "道题字数要求大于" + min + "，小于"
                        + max;
            }
            jsonObject.put("answers_url", "");
            jsonObject.put("note", "");
            jsonObject.put("answers", text);
            jsonObject.put("question_id", taskEditInfo.getId());
            jsonObject.put("question_type", taskEditInfo.getQuestion_type());
            jsonObject.put("question_num", taskEditInfo.getQuestion_num());
        } else if (!TextUtils.isEmpty(url) && taskEditView.getVisibility() == View.VISIBLE) {
            jsonObject.put("note", "");
            jsonObject.put("answers", text);
            jsonObject.put("question_id", taskEditInfo.getId());
            jsonObject.put("question_type", taskEditInfo.getQuestion_type());
            jsonObject.put("question_num", taskEditInfo.getQuestion_num());
            String filename = new File(url).getName();
            jsonObject.put("answers_url", Urls.Endpoint2 + "/" + filename);
            recList.add(new String[]{taskEditInfo.getId(), url});
        } else {
            jsonObject.put("answers_url", "");
            jsonObject.put("note", "");
            jsonObject.put("answers", "");
            jsonObject.put("question_id", taskEditInfo.getId());
            jsonObject.put("question_type", taskEditInfo.getQuestion_type());
            jsonObject.put("question_num", taskEditInfo.getQuestion_num());
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (!"1".equals(newtask)) {
            super.onBackPressed();
        }
    }

    private NetworkConnection Record, Recordup;
    private LinearLayout taskitemedit_question_layout;
    private TextView taskitemedit_name;
    private TextView taskitemedit_desc;
    private TextView taskitemedit_type;
    private View taskitemedit_progressbar;
    //进度条
    private ProgressBar progressbar;
    private int progressbarWidth;
    private ImageView progressbar_biaoshi, progressbar_jiedian3;
    private String category1 = "", category2 = "", category3 = "";
    private String projectid;
    private String project_name, store_name, store_num;
    private String is_desc;
    private String task_pack_name, task_name;
    private GridView taskitmpg_gridview;
    private TaskitemReqPgAdapter adapter;
    private ArrayList<String> picList = new ArrayList<>();
    private String index;//扫码任务预览
    private ImageView taskitemedit_spread;
    private View example, lin_Nodata, mainLayout;
    private TextView lin_Nodata_prompt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemedit);
        initNetworkConnection();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        index = data.getStringExtra("index");
        updataDBHelper = new UpdataDBHelper(this);
        projectid = data.getStringExtra("project_id");
        store_name = data.getStringExtra("store_name");
        project_name = data.getStringExtra("project_name");
        taskid = data.getStringExtra("task_id");
        pid = data.getStringExtra("task_pack_id");
        storeid = data.getStringExtra("store_id");
        tasktype = data.getStringExtra("tasktype");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        is_desc = data.getStringExtra("is_desc");
        task_pack_name = data.getStringExtra("task_pack_name");
        task_name = data.getStringExtra("task_name");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        store_num = data.getStringExtra("store_num");
        newtask = data.getStringExtra("newtask");
        lin_Nodata = findViewById(R.id.lin_Nodata);
        lin_Nodata_prompt = (TextView) findViewById(R.id.lin_Nodata_prompt);
        mainLayout = findViewById(R.id.mainLayout);
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        initTitle();
        example = findViewById(R.id.shili);
        taskitemedit_name = (TextView) findViewById(R.id.taskitemedit_name);
        taskitemedit_spread = (ImageView) findViewById(R.id.taskitemedit_spread);

        taskitemedit_desc = (TextView) findViewById(R.id.taskitemedit_desc);
        taskitemedit_type = (TextView) findViewById(R.id.taskitemedit_type);
        taskitemedit_question_layout = (LinearLayout) findViewById(R.id.taskitemedit_question_layout);
        taskitemedit_progressbar = findViewById(R.id.taskitemedit_progressbar);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressbar_biaoshi = (ImageView) findViewById(R.id.progressbar_biaoshi);
//        progressbar_jiedian2 = (ImageView) findViewById(R.id.progressbar_jiedian2);
        progressbar_jiedian3 = (ImageView) findViewById(R.id.progressbar_jiedian3);
        if (index != null && "0".equals(index)) {
            taskitemedit_question_layout.setVisibility(View.GONE);
            findViewById(R.id.taskitemedit_button).setVisibility(View.GONE);
        } else {
            taskitemedit_question_layout.setVisibility(View.VISIBLE);
            findViewById(R.id.taskitemedit_button).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.taskitemedit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (index != null && "0".equals(index)) {
                    Tools.showToast(TaskitemEditActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }
                new getAnswersAsyncTask().execute();
            }
        });
        qusetionPosition = -1;
        taskitmpg_gridview = (GridView) findViewById(R.id.taskitemedit_gridview);
        adapter = new TaskitemReqPgAdapter(this, picList);
        taskitmpg_gridview.setAdapter(adapter);
        taskitmpg_gridview.setOnItemClickListener(this);
        taskitemedit_spread.setOnClickListener(this);
        mainLayout.setVisibility(View.GONE);
        lin_Nodata.setVisibility(View.VISIBLE);
        getData();
    }

    protected void onResume() {
        super.onResume();
        progressbarWidth = 0;
        Tools.d("---onResume---");
    }

    /**
     * 设置进度条（单题模式）
     *
     * @param num 当前题目编号
     */
    private void settingProgressbar(int num) {
        if (questionList == null || questionList.isEmpty()) return;
        int size = questionList.size() - 1;
        num--;
        int progress = (int) (num * 1.0f / size * 100f);
        float pe;
        if (size == num) {
            pe = 1;
        } else {
            pe = num * 1.0f / size;
        }
        progressbar.setProgress(progress);
        if (progressbarWidth == 0) {
            progressbarWidth = progressbar.getWidth();
        }
        if (progress >= 50) {
//            progressbar_jiedian2.setImageResource(R.mipmap.taskitemedit_jiedian);
            if (progress == 100) {
                progressbar_jiedian3.setImageResource(R.mipmap.start_edit);
            }
        } else {
//            progressbar_jiedian2.setImageResource(R.mipmap.taskitemedit_zhongdian);
            progressbar_jiedian3.setImageResource(R.mipmap.end_edit);
        }
        int temp = (int) (progressbarWidth * pe - getResources().getDimension(R.dimen.ouye_progressbarbiaoshi) / 2 +
                getResources().getDimension(R.dimen.ouye_progressbarmargin));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) progressbar_biaoshi.getLayoutParams();
        lp.leftMargin = temp;
        progressbar_biaoshi.setLayoutParams(lp);
    }

    private ArrayList<TaskEditInfo> questionList;//所有题目链表
    private String batch, outlet_batch, p_batch;

    /**
     * 说明：
     * question_type:问题类型，1为单选，2为多选，3为判断，4为填空
     * question_name：问题题目
     * prompt：提示
     * max_option：最多选择选项
     * min_option：最少选择选项
     * max_word_num：最多填写字数
     * min_word_num：最少填写字数
     * high：高度
     * isrequired：是否必填
     * forced_jump：是否强制跳题：0否、1是
     * jump_question：跳题
     * options： 选项（
     * id:选项id
     * option_name：选项名字
     * option_num：选项序号
     * isfill：是否可填
     * isforcedfill：是否必填
     * mutex_id：互斥选项id）
     */
    private void getData() {
        Record.sendPostRequest(Urls.Record, new Response.Listener<String>() {
            public void onResponse(String s) {
                mainLayout.setVisibility(View.VISIBLE);
                lin_Nodata.setVisibility(View.GONE);
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String questionnaire_type = jsonObject.optString("questionnaire_type");// 问卷形式，1为单题形式，2为多题形式
                        questionnaire_type = "2";
                        taskitemedit_name.setText(jsonObject.getString("task_name"));
                        String note = jsonObject.getString("note");
                        taskitemedit_desc.setText(note);
                        batch = jsonObject.getString("batch");
                        String picStr = jsonObject.getString("pics");
                        if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 4) {
                            example.setVisibility(View.GONE);
                            taskitmpg_gridview.setVisibility(View.GONE);
                            Tools.d(" taskitemedit_desc.getLineCount()=====>>" + taskitemedit_desc.getLineCount());
                            if (taskitemedit_desc.getLineCount() < 3) {
                                taskitemedit_spread.setVisibility(View.GONE);
                            } else {
                                taskitemedit_spread.setVisibility(View.VISIBLE);
                            }
                        } else {
                            picStr = picStr.substring(1, picStr.length() - 1);
                            String[] pics = picStr.split(",");
                            for (int i = 0; i < pics.length; i++) {
                                String str = pics[i].replaceAll("\"", "").replaceAll("\\\\", "");
                                if (!str.startsWith("http")) {
                                    if (str.startsWith("GZB/")) {
                                        str = Urls.Endpoint3 + str+"?x-oss-process=image/resize,l_350";
                                    } else {
                                        str = Urls.ImgIp + str;
                                    }
                                }
                                picList.add(str);
                            }
//                            if (pics.length > 0) {
//                                int t = (int) Math.ceil(pics.length / 3d);
//                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmpg_gridview.getLayoutParams();
//                                lp.height = (int) ((Tools.dipToPx(TaskitemEditActivity.this, 100) +
//                                        getResources().getDimension(R.dimen.taskphoto_gridview_mar)) * t);// gridview已经重写
//                                taskitmpg_gridview.setLayoutParams(lp);
//                            }
                            adapter.notifyDataSetChanged();
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        if (questionList == null) {
                            questionList = new ArrayList<TaskEditInfo>();
                        } else {
                            questionList.clear();
                        }
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            TaskEditInfo taskEditInfo = new TaskEditInfo();
                            taskEditInfo.setId(jsonObject.getString("id"));
                            taskEditInfo.setQuestion_type(jsonObject.getString("question_type"));
                            taskEditInfo.setQuestion_name(jsonObject.getString("question_name"));
                            taskEditInfo.setPrompt(jsonObject.getString("prompt"));
                            taskEditInfo.setMax_option(jsonObject.getString("max_option"));
                            taskEditInfo.setMin_option(jsonObject.getString("min_option"));
                            taskEditInfo.setMax_word_num(jsonObject.getString("max_word_num"));
                            taskEditInfo.setMin_word_num(jsonObject.getString("min_word_num"));
                            taskEditInfo.setIsrequired(jsonObject.getString("isrequired"));
                            taskEditInfo.setForced_jump(jsonObject.getString("forced_jump"));
                            taskEditInfo.setJump_question(jsonObject.getString("jump_question"));//15101179567
                            taskEditInfo.setQuestion_num(Tools.StringToInt(jsonObject.getString("question_num")));
                            taskEditInfo.setSwitch_to_voice(jsonObject.getString("switch_to_voice"));
                            taskEditInfo.setIs_scan(jsonObject.getString("is_scan"));
                            JSONArray tempArray = jsonObject.optJSONArray("options");
                            if (tempArray != null) {
                                int tempLength = tempArray.length();
                                ArrayList<TaskEditoptionsInfo> optionList = new ArrayList<TaskEditoptionsInfo>();
                                for (int j = 0; j < tempLength; j++) {
                                    jsonObject = tempArray.getJSONObject(j);
                                    TaskEditoptionsInfo taskEditoptionsInfo = new TaskEditoptionsInfo();
                                    taskEditoptionsInfo.setId(jsonObject.getString("id"));
                                    taskEditoptionsInfo.setOption_name(jsonObject.getString("option_name"));
                                    taskEditoptionsInfo.setOption_num(jsonObject.getInt("option_num"));
                                    taskEditoptionsInfo.setIsfill(jsonObject.getString("isfill"));
                                    taskEditoptionsInfo.setIsforcedfill(jsonObject.getString("isforcedfill"));
                                    taskEditoptionsInfo.setMutex_id(jsonObject.getString("mutex_id"));
                                    taskEditoptionsInfo.setJump(jsonObject.getString("jump"));
                                    taskEditoptionsInfo.setJumpquestion(jsonObject.getString("jumpquestion"));
                                    taskEditoptionsInfo.setPhoto_url(jsonObject.getString("photo_url"));
                                    optionList.add(taskEditoptionsInfo);
                                }
                                Collections.sort(optionList, new Comparator<TaskEditoptionsInfo>() {
                                    public int compare(TaskEditoptionsInfo lhs, TaskEditoptionsInfo rhs) {
                                        return lhs.getOption_num() - rhs.getOption_num();
                                    }
                                });
                                taskEditInfo.setOptions(optionList);
                            }
                            questionList.add(taskEditInfo);
                        }
                        Collections.sort(questionList, new TaskQuestionComparator());
                        if ("1".equals(questionnaire_type)) {
                            if (taskitemedit_progressbar != null) {
                                if (questionList.size() < 5) {
                                    taskitemedit_progressbar.setVisibility(View.GONE);
                                } else {
                                    taskitemedit_progressbar.setVisibility(View.VISIBLE);
                                }
                            }
                            qusetionPosition = 1;
                            if (questionList.size() == 1) {
                                showEndButton = true;
                            } else {
                                showEndButton = false;
                            }
                            addView(questionList.get(0));
                            findViewById(R.id.taskitemedit_button).setVisibility(View.GONE);
                            if (taskitemedit_type != null) {
                                taskitemedit_type.setText("单题模式");
                            }
                        } else if ("2".equals(questionnaire_type)) {
                            addView();
                            if (taskitemedit_type != null) {
                                taskitemedit_type.setText("多题模式");
                            }
                            if (taskitemedit_progressbar != null) {
                                taskitemedit_progressbar.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Tools.showToast(TaskitemEditActivity.this, jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(TaskitemEditActivity.this, getResources().getString(R.string.network_error));
                } finally {
                    if (isHavREC && RecordService.isStart()) {
                        Tools.showToast(TaskitemEditActivity.this, "您的录音任务还没有结束，请先提交后开始下一个任务~");
                        baseFinish();
                    }
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                lin_Nodata_prompt.setText("网络连接中断，\n请检查下您的网络吧！");
//                Tools.showToast(TaskitemEditActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private int qusetionPosition;//单题模式下，题目链表中当前题目的下标
    private Boolean showEndButton;//是否显示完成按钮
    private int qusetionnum = 1;

    /**
     * TODO 添加题目布局（单题强制跳转用）
     */
    private void addView(TaskEditInfo taskEditInfo) {
        int questionListSize = questionList.size();
        if ("-1".equals(taskEditInfo.getJump_question()) && "1".equals(taskEditInfo.getForced_jump())) {
            showEndButton = true;
        } else if ("0".equals(taskEditInfo.getForced_jump()) && questionListSize == qusetionnum) {
            showEndButton = true;
        } else {
            showEndButton = false;
        }
        taskitemedit_question_layout.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        String temp1;
        temp1 = taskEditInfo.getQuestion_type();
        boolean isrequired = (taskEditInfo.getIsrequired().equals("1"));
//        if (!taskEditInfo.getPrompt().equals("")) {
//            isfillstr = "（" + taskEditInfo.getPrompt() + "）" + isfillstr;
//        }
        ArrayList<TaskEditoptionsInfo> optionList;
        if (taskitemedit_progressbar != null && taskitemedit_progressbar.getVisibility() == View.VISIBLE) {
            //设置进度条
            if (showEndButton) {//如果是最后一题 则进度条填满
                settingProgressbar(questionListSize);
            } else {
                settingProgressbar(qusetionnum);
            }
        }
        if ("1".equals(temp1)) {
            TaskRadioView taskRadioView = new TaskRadioView(this);
            taskRadioView.setTitle(qusetionnum + "." + taskEditInfo.getQuestion_name(), isrequired);
            if (showEndButton) {
                taskRadioView.setSubmitText("完成");
            }
            taskRadioView.setOnTaskQuestionSumbitListener(this);
            optionList = taskEditInfo.getOptions();
            int length = optionList.size();
            for (int j = 0; j < length; j++) {
                String isfill = optionList.get(j).getIsfill();
                if ("1".equals(isfill)) {
                    taskRadioView.addRadioButtonForFill(optionList.get(j).getId(), optionList.get(j).getOption_name()
                            , optionList.get(j).getIsforcedfill(), optionList.get(j).getJump(), optionList.get(j)
                                    .getJumpquestion(), optionList.get(j).getPhoto_url());
                } else {
                    taskRadioView.addRadioButton(optionList.get(j).getId(), optionList.get(j).getOption_name(),
                            optionList.get(j).getJump(), optionList.get(j).getJumpquestion(), optionList.get(j).getPhoto_url());
                }
            }
            taskitemedit_question_layout.addView(taskRadioView, lp);
            taskEditInfo.setView(taskRadioView);
        } else if ("2".equals(temp1)) {
            TaskCheckView taskCheckView = new TaskCheckView(this);
            taskCheckView.setTitle(qusetionnum + "." + taskEditInfo.getQuestion_name(), isrequired);
            if (showEndButton) {
                taskCheckView.setSubmitText("完成");
            }
            taskCheckView.setOnTaskQuestionSumbitListener(this);
            optionList = taskEditInfo.getOptions();
            int length = optionList.size();
            for (int j = 0; j < length; j++) {
                String isfill = optionList.get(j).getIsfill();
                if ("1".equals(isfill)) {
                    taskCheckView.addCheckBoxForFill(optionList.get(j));
                } else {
                    taskCheckView.addCheckBox(optionList.get(j));
                }
            }
            taskitemedit_question_layout.addView(taskCheckView, lp);
            taskEditInfo.setView(taskCheckView);
        } else if ("3".equals(temp1)) {
            TaskJudgeView taskJudgeView = new TaskJudgeView(this, qusetionnum + "." +
                    taskEditInfo.getQuestion_name(), isrequired);
            if (showEndButton) {
                taskJudgeView.setSubmitText("完成");
            }
            taskJudgeView.setOnTaskQuestionSumbitListener(this);
            taskitemedit_question_layout.addView(taskJudgeView, lp);
            taskEditInfo.setView(taskJudgeView);
        } else if ("4".equals(temp1)) {
            boolean isScan = false;
            if ("1".equals(taskEditInfo.getIs_scan())) {
                isScan = true;
            } else {
                isScan = false;
            }
            TaskEditViewforCapture taskEditView = new TaskEditViewforCapture(this, qusetionnum + "." + taskEditInfo
                    .getQuestion_name(), isrequired, taskitemedit_title, isScan);
            if (showEndButton) {
                taskEditView.setSubmitText("完成");
            }
            taskEditView.setOnTaskQuestionSumbitListener(this);
            taskitemedit_question_layout.addView(taskEditView, lp);
            taskEditInfo.setView(taskEditView);
        } else if ("5".equals(temp1)) {
            TaskTimeSelView taskTimeSelView = new TaskTimeSelView(this, qusetionnum + "." + taskEditInfo
                    .getQuestion_name(), isrequired);
            if (showEndButton) {
                taskTimeSelView.setSubmitText("完成");
            }
            taskTimeSelView.setOnTaskQuestionSumbitListener(this);
            taskitemedit_question_layout.addView(taskTimeSelView, lp);
            taskEditInfo.setView(taskTimeSelView);
        }
        qusetionnum++;
    }


    /**
     * TODO 添加布局(多题形式用)
     */
    private void addView() {
        int size = questionList.size();
        TaskEditInfo taskEditInfo;
        String temp1;
        ArrayList<TaskEditoptionsInfo> optionList;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 20;
        TextView textView = new TextView(this);
        textView.setTextSize(14);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setText("注意：请按照题目顺序答题哦");
        textView.setBackgroundColor(Color.WHITE);
        taskitemedit_question_layout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < size; i++) {
            taskEditInfo = questionList.get(i);
            boolean isrequired = (taskEditInfo.getIsrequired().equals("1"));
//            if (!taskEditInfo.getPrompt().equals("")) {
//                isfillstr = "（" + taskEditInfo.getPrompt() + "）" + isfillstr;
//            }
            temp1 = taskEditInfo.getQuestion_type();
            if ("1".equals(temp1)) {
                TaskRadioView taskRadioView = new TaskRadioView(this);
                taskRadioView.setOnTaskEditRefreshListener(this);
                taskRadioView.settingData(taskEditInfo);
                taskRadioView.setTitle(taskEditInfo.getQuestion_num() + "." + taskEditInfo.getQuestion_name(), isrequired);
                optionList = taskEditInfo.getOptions();
                int length = optionList.size();
//                for (int j = 0; j < length; j++) {
//                    taskRadioView.addRadioButton(optionList.get(j).getId(), optionList.get(j).getOption_name());
//                }
                for (int j = 0; j < length; j++) {
                    TaskEditoptionsInfo taskEditoptionsInfo = optionList.get(j);
                    String isfill = taskEditoptionsInfo.getIsfill();
                    if ("1".equals(isfill)) {
                        taskRadioView.addRadioButtonForFill(taskEditoptionsInfo.getId(), taskEditoptionsInfo
                                        .getOption_name(), taskEditoptionsInfo.getIsforcedfill(), taskEditoptionsInfo.getJump(),
                                taskEditoptionsInfo.getJumpquestion(), taskEditoptionsInfo.getPhoto_url());
                    } else {
                        taskRadioView.addRadioButton(taskEditoptionsInfo.getId(), taskEditoptionsInfo.getOption_name(),
                                taskEditoptionsInfo.getJump(), taskEditoptionsInfo.getJumpquestion(),
                                taskEditoptionsInfo.getPhoto_url());
                    }
                }
                taskitemedit_question_layout.addView(taskRadioView, lp);
                taskEditInfo.setView(taskRadioView);
            } else if ("2".equals(temp1)) {
                TaskCheckView taskCheckView = new TaskCheckView(this);
                taskCheckView.setOnTaskEditRefreshListener(this);
                taskCheckView.settingData(taskEditInfo);
                taskCheckView.setTitle(taskEditInfo.getQuestion_num() + "." + taskEditInfo.getQuestion_name(), isrequired);
                optionList = taskEditInfo.getOptions();
                int length = optionList.size();
//                for (int j = 0; j < length; j++) {
//                    taskCheckView.addCheckBox(optionList.get(j));
//                }
                for (int j = 0; j < length; j++) {
                    String isfill = optionList.get(j).getIsfill();
                    if ("1".equals(isfill)) {
                        taskCheckView.addCheckBoxForFill(optionList.get(j));
                    } else {
                        taskCheckView.addCheckBox(optionList.get(j));
                    }
                }
                taskitemedit_question_layout.addView(taskCheckView, lp);
                taskEditInfo.setView(taskCheckView);
            } else if ("3".equals(temp1)) {
                TaskJudgeView taskJudgeView = new TaskJudgeView(this, taskEditInfo.getQuestion_num() + "." +
                        taskEditInfo.getQuestion_name(), isrequired);
                taskitemedit_question_layout.addView(taskJudgeView, lp);
                taskEditInfo.setView(taskJudgeView);
            } else if ("4".equals(temp1)) {
                if ("1".equals(taskEditInfo.getSwitch_to_voice())) {//Urls.EndpointDir + "/" + objectKey
                    isHavREC = true;
                    TaskEditView2 taskEditView = new TaskEditView2(this, taskEditInfo.getQuestion_num() + "." +
                            taskEditInfo.getQuestion_name(), isrequired);
                    taskitemedit_question_layout.addView(taskEditView, lp);
                    taskEditInfo.setView(taskEditView);
                } else if ("1".equals(taskEditInfo.getIs_scan())) {
                    TaskEditViewforCapture taskEditView = new TaskEditViewforCapture(this, taskEditInfo.getQuestion_num() +
                            "." + taskEditInfo.getQuestion_name(), isrequired, taskitemedit_title, true);
                    taskitemedit_question_layout.addView(taskEditView, lp);
                    taskEditInfo.setView(taskEditView);
                } else {
                    TaskEditView taskEditView = new TaskEditView(this, taskEditInfo.getQuestion_num() + "." +
                            taskEditInfo.getQuestion_name(), isrequired);
                    taskitemedit_question_layout.addView(taskEditView, lp);
                    taskEditInfo.setView(taskEditView);
                }
            } else if ("5".equals(temp1)) {
                TaskTimeSelView taskTimeSelView = new TaskTimeSelView(this, taskEditInfo.getQuestion_num() + "." +
                        taskEditInfo.getQuestion_name(), isrequired);
                taskitemedit_question_layout.addView(taskTimeSelView, lp);
                taskEditInfo.setView(taskTimeSelView);
            } else if ("6".equals(temp1)) {//语音题
                isHavREC = true;
                TaskEditView2 taskEditView = new TaskEditView2(this, taskEditInfo.getQuestion_num() + "." +
                        taskEditInfo.getQuestion_name(), isrequired, true);//只录音
                taskitemedit_question_layout.addView(taskEditView, lp);
                taskEditInfo.setView(taskEditView);
            }
        }
        if (isHavREC) {
            service = RecordService.getIntent();
            stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
        }
    }

    private Intent service;
    private boolean isHavREC = false;

    public void changeView(Object src, int srcNum, String jumpNum, String oldSelectPosition) {//页面刷新
        int qid = Tools.StringToInt(jumpNum);
        int size;
        boolean needSetting = false;
        if (oldSelectPosition != null) {//通过之前选项判断波及范围
            if (src instanceof TaskRadioView) {
                TaskQuestionInfo taskQuestionInfo = ((TaskRadioView) src).getQuestionInfo(Tools.StringToInt(oldSelectPosition));
                String jumpq = taskQuestionInfo.getJumpquestion();
                if ("-1".equals(jumpq)) {
                    if ("1".equals(taskQuestionInfo.getJump())) {
                        size = questionList.size();
                    } else {
                        if (qid == -1) {
                            needSetting = true;
                            size = questionList.size();
                        } else {
                            size = qid;
                        }
                    }
                } else {
                    int temp = Tools.StringToInt(jumpq);
                    size = temp > qid ? temp : qid;
                }
            } else {
                if (qid == -1) {
                    needSetting = true;
                    size = questionList.size();
                } else {
                    size = qid;
                }
            }
        } else {
            size = questionList.size();
        }
        if (srcNum <= size) {
            TaskEditInfo taskEditInfo = questionList.get(srcNum - 1);
            if (qid == -1) {
                if (src instanceof TaskRadioView) {//单选选项需要判断跳题
                    TaskQuestionInfo taskQuestionInfo = ((TaskRadioView) src).getSelectAnswers();
                    if ("1".equals(taskQuestionInfo.getJump())) {//跳最后一题
                        jumpLastQuestion(srcNum, size);
                    } else {
                        if ("1".equals(taskEditInfo.getForced_jump())) {//题目是否强制跳题
                            qid = Tools.StringToInt(taskEditInfo.getJump_question());
                            if (qid == -1) {//跳最后一题
                                jumpLastQuestion(srcNum, size);
                            } else {//跳指定题目
                                if (oldSelectPosition == null || needSetting) {
                                    size = qid;
                                }
                                jumpQuestion(srcNum, qid, size);
                            }
                        }
                    }
                } else {
                    if ("1".equals(taskEditInfo.getForced_jump())) {//题目是否强制跳题
                        qid = Tools.StringToInt(taskEditInfo.getJump_question());
                        if (qid == -1) {//跳最后一题
                            jumpLastQuestion(srcNum, size);
                        } else {//跳指定题目
                            if (oldSelectPosition == null) {
                                size = qid;
                            }
                            jumpQuestion(srcNum, qid, size);
                        }
                    }
                }
            } else {//跳指定题目
                if (oldSelectPosition == null) {
                    size = qid;
                }
                jumpQuestion(srcNum, qid, size);
            }
        }
    }

    /**
     * 跳转指定题目
     *
     * @param srcNum 原题号
     * @param qid    跳转题号
     * @param size   问题链表长度
     */
    private void jumpQuestion(int srcNum, int qid, int size) {
        ArrayList<Integer> commitg = new ArrayList<>();//待执行隐藏链表
        ArrayList<Integer> commitv = new ArrayList<>();//待执行显示链表
        View srcview = questionList.get(srcNum - 1).getView();
        TaskRadioView srcRadioview = null;
        if (srcview instanceof TaskRadioView) {
            srcRadioview = (TaskRadioView) srcview;
        }
        for (int i = srcNum; i < size; i++) {
            TaskEditInfo taskEditInfo = questionList.get(i);
            if (taskEditInfo.getQuestion_num() > i && taskEditInfo.getQuestion_num() < qid) {
                View view = taskEditInfo.getView();
                if (view instanceof TaskRadioView) {
                    TaskRadioView taskRadioView = (TaskRadioView) view;
                    if (taskRadioView.getSelectAnswers() != null && taskRadioView.getSelectAnswers().getJump().equals("1")) {
                        Tools.showToast(this, "此选择与第" + taskEditInfo.getQuestion_num() + "题选择冲突");
                        if (srcRadioview != null) {
                            srcRadioview.settingOldPosition();
                        } else {
                            if (srcview instanceof TaskEditClearListener) {
                                ((TaskEditClearListener) srcview).dataClear();
                            }
                        }
                        return;
                    }
                }
                commitg.add(i);
            } else {
                commitv.add(i);
            }
        }
        for (Integer integer : commitg) {
            TaskEditInfo taskEditInfo = questionList.get(integer);
            View view = taskEditInfo.getView();
            if (view instanceof TaskEditClearListener) {
                ((TaskEditClearListener) view).dataClear();
            }
            taskEditInfo.getView().setVisibility(View.GONE);
        }
        for (Integer integer : commitv) {
            TaskEditInfo taskEditInfo = questionList.get(integer);
            View view = taskEditInfo.getView();
            if (view.getVisibility() == View.GONE) {
                taskEditInfo.getView().setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 跳转到最后一题
     *
     * @param srcNum 原题号
     * @param size   问题链表长度
     */
    private void jumpLastQuestion(int srcNum, int size) {
        for (int i = srcNum; i < size; i++) {
            TaskEditInfo taskEditInfo = questionList.get(i);
            if (taskEditInfo.getQuestion_num() > i && taskEditInfo.getQuestion_num() < size) {
                View view = taskEditInfo.getView();
                if (view instanceof TaskEditClearListener) {
                    ((TaskEditClearListener) view).dataClear();
                }
                taskEditInfo.getView().setVisibility(View.GONE);
            } else {
                View view = taskEditInfo.getView();
                if (view.getVisibility() == View.GONE) {
                    taskEditInfo.getView().setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * TODO 单题模式下，题目的提交按钮
     */
    public void sumbit(TaskQuestionInfo[] answers, String[] notes) {
        if (index != null && "0".equals(index) && showEndButton) {
            Tools.showToast(TaskitemEditActivity.this, "抱歉，预览时任务无法执行。");
            return;
        }
        boolean isAnswersJump = false;
        String JumpQustionNum = null;
        /*获取题目信息类*/
        TaskEditInfo taskEditInfo = questionList.get(qusetionPosition - 1);
        /*判断是否为必填项，检测题目答案正确性*/
        if (taskEditInfo.getIsrequired().equals("1") && isEmpty(answers)) {//必填
            Tools.showToast(this, "此题为必填!");
            return;
        }
        /*存储答案与备注*/
        switch (Tools.StringToInt(taskEditInfo.getQuestion_type())) {
            case 2: {//多选
                /*存储答案*/
                if (!isEmpty(answers)) {
                    /*选择数量是否正确*/
                    int maxOption = Tools.StringToInt(taskEditInfo.getMax_option());
                    int minOptino = Tools.StringToInt(taskEditInfo.getMin_option());
                    int size;
                    if (isEmpty(answers)) {
                        size = 0;
                    } else {
                        size = answers.length;
                    }
                    if ((size < minOptino && minOptino >= 0) || (size > maxOption && maxOption > 0)) {
                        Tools.showToast(this, "选择的项目要大于" + minOptino + "，小于" + maxOption);
                        return;
                    }
                    String temp = null;
                    String note = null;
                    for (int i = 0; i < answers.length; i++) {
                        if (temp == null) {
                            temp = answers[i].getId();
                        } else {
                            temp = temp + "," + answers[i].getId();
                        }
                        //&& Tools.StringToInt(answers[i].getJumpquestion()) != -1
                        if ("1".equals(answers[i].getJump())) {//选项强制跳题
                            JumpQustionNum = answers[i].getJumpquestion();
                            isAnswersJump = true;
                        }
                        if (answers[i].getNoteEditext() != null && !TextUtils.isEmpty(answers[i].getNoteEditext()
                                .getText().toString().trim())) {//存储备注
                            String tempNote = answers[i].getNoteEditext().getText().toString().trim();
                            if (TextUtils.isEmpty(tempNote)) {
                                tempNote = " ";
                            }
                            if (note == null) {
                                note = tempNote;
                            } else {
                                note = note + "&&" + tempNote;
                            }
                        } else if (answers[i].isRequired()) {
                            Tools.showToast(this, "多选备注有必填项！");
                            return;
                        }
                    }
                    taskEditInfo.setAnswers(temp);
                }
            }
            break;
            case 1: {
                if (!isEmpty(answers)) {
                    if (answers[0].getNoteEditext() != null && !TextUtils.isEmpty(answers[0].getNoteEditext().getText
                            ().toString().trim())) {//存储备注
                        taskEditInfo.setAnswersNote(answers[0].getNoteEditext().getText().toString().trim());
                    } else if (answers[0].isRequired()) {
                        Tools.showToast(this, "单选备注有必填项！");
                        return;
                    }
                }
                /*存储答案*/
                if (!isEmpty(answers)) {
                    taskEditInfo.setAnswers(answers[0].getId());
                    if ("1".equals(answers[0].getJump())) {//选项强制跳题
                        JumpQustionNum = answers[0].getJumpquestion();
                        isAnswersJump = true;
                    }
                }
            }
            break;
            case 3: {
                /*存储答案*/
                if (!isEmpty(answers)) {
                    taskEditInfo.setAnswers(answers[0].getId());
                }
            }
            break;
            case 4: {
                /*判断填空题字数*/
                if (taskEditInfo.getIsrequired().equals("1")) {
                    int max = Tools.StringToInt(taskEditInfo.getMax_word_num());
                    int min = Tools.StringToInt(taskEditInfo.getMin_word_num());
                    int length;
                    if (isEmpty(answers)) {
                        length = 0;
                    } else {
                        length = answers[0].getId().length();
                    }
                    if (max == -1 && min == -1) {
                        if (length > 500) {
                            Tools.showToast(this, "字数应小于500字");
                            return;
                        }
                    } else if ((length > max && max > 0) || (length < min && min >= 0)) {
                        Tools.showToast(this, "字数要求大于" + min + "，小于" + max);
                        return;
                    }
                } else if (!isEmpty(answers)) {
                    int max = Tools.StringToInt(taskEditInfo.getMax_word_num());
                    int min = Tools.StringToInt(taskEditInfo.getMin_word_num());
                    int length;
                    if (isEmpty(answers)) {
                        length = 0;
                    } else {
                        length = answers[0].getId().length();
                    }
                    if (max == -1 && min == -1) {
                        if (length > 500) {
                            Tools.showToast(this, "字数应小于500字");
                            return;
                        }
                    } else if ((length > max && max > 0) || (length < min && min >= 0)) {
                        Tools.showToast(this, "字数要求大于" + min + "，小于" + max);
                        return;
                    }
                }
                /*存储答案*/
                if (!isEmpty(answers)) {
                    taskEditInfo.setAnswers(filterEmoji(answers[0].getId()));
                }
            }
            break;
            case 5: {
                /*存储答案*/
                if (!isEmpty(answers)) {
                    taskEditInfo.setAnswers(answers[0].getId());
                }
            }
            break;
        }
        if (showEndButton) {
            new getAnswersAsyncTask().execute();
        } else {
            if (isAnswersJump) {//选项跳题优先
                int qid = Tools.StringToInt(JumpQustionNum);
                if (qid != -1) {//跳题
                    int size = questionList.size();
                    for (int i = 0; i < size; i++) {
                        if (qid == questionList.get(i).getQuestion_num()) {
                            qusetionPosition = i;
                            break;
                        }
                    }
                    addView(questionList.get(qusetionPosition++));
                } else {
                    if (index != null && "0".equals(index)) {
                        Tools.showToast(TaskitemEditActivity.this, "抱歉，预览时任务无法执行。");
                        return;
                    }
                    new getAnswersAsyncTask().execute();
                }
            } else if (taskEditInfo.getForced_jump().equals("1")) {
                int qid = Tools.StringToInt(taskEditInfo.getJump_question());
                if (qid != -1) {//跳题
                    int size = questionList.size();
                    for (int i = 0; i < size; i++) {
                        if (qid == questionList.get(i).getQuestion_num()) {
                            qusetionPosition = i;
                            break;
                        }
                    }
                    addView(questionList.get(qusetionPosition++));
                } else {
                    new getAnswersAsyncTask().execute();
                }
            }

        }
    }

    public static String filterEmoji(String source) {//删除特殊字符和表情

        if (!containsEmoji(source)) {
            return source;// 如果不包含，直接返回
        }
        // 到这里铁定包含
        StringBuilder buf = null;

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }

                buf.append(codePoint);
            } else {
            }
        }

        if (buf == null) {
            return source;// 如果没有找到 emoji表情，则返回源字符串
        } else {
            if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }

    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 检测是否有emoji字符
     *
     * @param source
     * @return FALSE，包含图片
     */
    public static boolean containsEmoji(String source) {
        if (source.equals("")) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                // do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    /**
     * 判断字符数组是否包含字符
     *
     * @param strs
     * @param s
     * @return
     */
    private boolean isCon(String[] strs, String s) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符数组是否为空
     *
     * @param strs
     * @return
     */
    private boolean isEmpty(TaskQuestionInfo[] strs) {
        if (strs == null || strs.length == 0) {
            return true;
        } else {
            int i;
            for (i = 0; i < strs.length; i++) {
                if (strs[i] != null && !TextUtils.isEmpty(strs[i].getId())) {
                    break;
                }
            }
            if (i == strs.length) {
                return true;
            } else {
                return false;
            }
        }
    }
}
