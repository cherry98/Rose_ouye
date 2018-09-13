package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.allinterface.OnTaskEditRefreshListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskEditInfo;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.TaskQuestionComparator;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.TaskCheckView;
import com.orange.oy.view.TaskEditView;
import com.orange.oy.view.TaskEditView2;
import com.orange.oy.view.TaskEditViewforCapture;
import com.orange.oy.view.TaskJudgeView;
import com.orange.oy.view.TaskRadioView;
import com.orange.oy.view.TaskTimeSelView;

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

/**
 * 记录任务详情页面
 */

public class TaskitemEditReset2Activity extends BaseActivity implements View.OnClickListener, AppTitle.OnBackClickForAppTitle, OnTaskEditRefreshListener {
    private boolean isedit = false;
    private Intent service;
    private AppTitle appTitle;

    private void initTitle(String str) {
        appTitle = (AppTitle) findViewById(R.id.taskitemedit_title_reset);
        appTitle.settingName("记录任务");
        appTitle.showBack(this);
        appTitle.settingExitColor(Color.parseColor("#ffF65D57"));
        appTitle.settingExit("编辑", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                isedit = !isedit;
                if (isedit) {
                    appTitle.settingExit("取消");
                    for (TaskEditInfo taskEditInfo : questionList) {
                        ((TaskEditClearListener) taskEditInfo.getView()).isSelect(isedit);
                    }
                    taskitemedit_button_reset.setVisibility(View.VISIBLE);
                } else {
                    if (isHavREC) {
                        service = RecordService.getIntent();
                        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
                    }
                    appTitle.settingExit("编辑");
                    taskitemedit_button_reset.setVisibility(View.GONE);
                    getData();
                }
            }
        });
    }

    private boolean isHavREC = false;

    protected void onStop() {
        super.onStop();
        if (edit != null) {
            edit.stop(Urls.TaskFinish);
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

    private void initNetworkConnection() {
        edit = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", taskid);
                return params;
            }
        };
        edit.setIsShowDialog(true);
        editReDo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", taskid);
                params.put("usermobile", username);
                return params;
            }
        };
        editReDo.setIsShowDialog(true);
        Recordup = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("pid", task_pack_id);
                params.put("usermobile", AppInfo.getName(TaskitemEditReset2Activity.this));
                params.put("storeid", store_id);
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


    private NetworkConnection edit, editReDo, Recordup;
    private UpdataDBHelper updataDBHelper;
    private String username;
    private String project_id, store_id, task_pack_id, category1, category2, category3, taskid, project_name,
            task_name, task_pack_name, store_num, store_name, outlet_batch, p_batch, is_desc, tasktype;
    private TextView taskitemedit_name_reset;
    private View taskitemedit_button_reset;
    private LinearLayout taskitemedit_questionlayout_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitem_edit_reset);
        Intent data = getIntent();
        username = AppInfo.getName(this);
        updataDBHelper = new UpdataDBHelper(this);
        initTitle(data.getStringExtra("task_name"));
        initNetworkConnection();
        project_id = data.getStringExtra("project_id");
        store_id = data.getStringExtra("store_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        taskid = data.getStringExtra("taskid");
        project_name = data.getStringExtra("project_name");
        task_name = data.getStringExtra("task_name");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_num = data.getStringExtra("store_num");
        store_name = data.getStringExtra("store_name");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        is_desc = data.getStringExtra("is_desc");
        tasktype = data.getStringExtra("tasktype");
        taskitemedit_button_reset = findViewById(R.id.taskitemedit_button_reset);
        taskitemedit_button_reset.setOnClickListener(this);
        taskitemedit_name_reset = (TextView) findViewById(R.id.taskitemedit_name_reset);
        taskitemedit_questionlayout_reset = (LinearLayout) findViewById(R.id.taskitemedit_questionlayout_reset);
        getData();
    }

    private ArrayList<TaskEditInfo> questionList = new ArrayList<TaskEditInfo>();
    private String batch = "1";

    public void getData() {
        edit.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.optInt("code");
                    batch = jsonObject.getString("batch");
                    taskitemedit_name_reset.setText(jsonObject.optString("task_name"));
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("questionlist");
                        int length = jsonArray.length();
                        if (questionList == null) {
                            questionList = new ArrayList<TaskEditInfo>();
                        } else {
                            questionList.clear();
                        }
                        String[] answers = null;
                        String[] notes = null;
                        String answers_url;
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            answers_url = jsonObject.getString("answers_url");
                            answers = jsonObject.getString("answers").split(",");
                            String note = jsonObject.optString("note");
                            if (!TextUtils.isEmpty(note)) {
                                notes = note.split("&&");
                            } else {
                                notes = null;
                            }
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
                            taskEditInfo.setSwitch_to_voice(jsonObject.getString("switch_to_voice"));
                            taskEditInfo.setQuestion_num(Tools.StringToInt(jsonObject.getString("question_num")));
                            taskEditInfo.setIs_scan(jsonObject.getString("is_scan"));
                            taskEditInfo.setAnswers_(answers);
                            taskEditInfo.setAnswers_url(answers_url);
                            taskEditInfo.setNotes(notes);
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
                        taskitemedit_questionlayout_reset.removeAllViews();
                        addView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskitemEditReset2Activity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemEditReset2Activity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
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
                taskRadioView.isSelect(false);
                optionList = taskEditInfo.getOptions();
                int length = optionList.size();
//                for (int j = 0; j < length; j++) {
//                    taskRadioView.addRadioButton(optionList.get(j).getId(), optionList.get(j).getOption_name());
//                }
                for (int j = 0; j < length; j++) {
                    TaskEditoptionsInfo taskEditoptionsInfo = optionList.get(j);
                    String isfill = taskEditoptionsInfo.getIsfill();
                    String note = "";
                    if (taskEditInfo.getNotes() != null && taskEditInfo.getNotes().length > 0) {
                        note = taskEditInfo.getNotes()[0];
                    }
                    if ("1".equals(isfill)) {
                        taskRadioView.addRadioButtonForFill(taskEditoptionsInfo.getId(), taskEditoptionsInfo
                                        .getOption_name(), taskEditoptionsInfo.getIsforcedfill(), taskEditoptionsInfo.getJump(),
                                taskEditoptionsInfo.getJumpquestion(),
                                note, taskEditInfo.getAnswers_()[0].equals(taskEditoptionsInfo.getId()), taskEditoptionsInfo.getPhoto_url());
                    } else {
                        taskRadioView.addRadioButton(taskEditoptionsInfo.getId(), taskEditoptionsInfo.getOption_name(),
                                taskEditoptionsInfo.getJump(), taskEditoptionsInfo.getJumpquestion(),
                                taskEditInfo.getAnswers_()[0].equals(taskEditoptionsInfo.getId()),
                                taskEditoptionsInfo.getPhoto_url());
                    }
                }
                taskitemedit_questionlayout_reset.addView(taskRadioView, lp);
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
                int anwsernum = -1;
                for (int j = 0; j < length; j++) {
                    TaskEditoptionsInfo taskEditoptionsInfo = optionList.get(j);
                    String isfill = taskEditoptionsInfo.getIsfill();
                    if ("1".equals(isfill)) {
                        boolean ishad = isHad(taskEditInfo.getAnswers_(), taskEditoptionsInfo.getId());
                        if (ishad) anwsernum++;
                        String note = "";
                        if (taskEditInfo.getNotes() != null && ishad && anwsernum < taskEditInfo.getNotes().length) {
                            note = taskEditInfo.getNotes()[anwsernum];
                        }
                        taskCheckView.addCheckBoxForFill(taskEditoptionsInfo, ishad, note);
                    } else {
                        taskCheckView.addCheckBox(taskEditoptionsInfo, isHad(taskEditInfo.getAnswers_(), taskEditoptionsInfo.getId()));
                    }
                }
                taskCheckView.isSelect(false);
                taskitemedit_questionlayout_reset.addView(taskCheckView, lp);
                taskEditInfo.setView(taskCheckView);
            } else if ("3".equals(temp1)) {
                TaskJudgeView taskJudgeView = new TaskJudgeView(this, taskEditInfo.getQuestion_num() + "." +
                        taskEditInfo.getQuestion_name(), taskEditInfo.getAnswers_()[0], isrequired);
                taskJudgeView.isSelect(false);
                taskitemedit_questionlayout_reset.addView(taskJudgeView, lp);
                taskEditInfo.setView(taskJudgeView);
            } else if ("4".equals(temp1)) {
                if ("1".equals(taskEditInfo.getSwitch_to_voice())) {//Urls.EndpointDir + "/" + objectKey
                    isHavREC = true;
                    TaskEditView2 taskEditView = new TaskEditView2(this, taskEditInfo.getQuestion_num() + "." +
                            taskEditInfo.getQuestion_name(), isrequired);
                    taskEditView.setEditValue(taskEditInfo.getAnswers_()[0]);
                    if (!TextUtils.isEmpty(taskEditInfo.getAnswers_url()) && !"null".equals(taskEditInfo.getAnswers_url())) {
                        taskEditView.setRecUrl(taskEditInfo.getAnswers_url());
                    }
                    taskEditView.isSelect(false);
                    taskitemedit_questionlayout_reset.addView(taskEditView, lp);
                    taskEditInfo.setView(taskEditView);
                } else if ("1".equals(taskEditInfo.getIs_scan())) {
                    TaskEditViewforCapture taskEditView = new TaskEditViewforCapture(this, taskEditInfo.getQuestion_num() + "." +
                            taskEditInfo.getQuestion_name(), isrequired, appTitle, true);
                    taskEditView.settingValue(taskEditInfo.getAnswers_()[0]);
                    taskEditView.isSelect(false);
                    taskitemedit_questionlayout_reset.addView(taskEditView, lp);
                    taskEditInfo.setView(taskEditView);
                } else {
                    TaskEditView taskEditView = new TaskEditView(this, taskEditInfo.getQuestion_num() + "." +
                            taskEditInfo.getQuestion_name(), taskEditInfo.getAnswers_()[0], isrequired);
                    taskEditView.isSelect(false);
                    taskitemedit_questionlayout_reset.addView(taskEditView, lp);
                    taskEditInfo.setView(taskEditView);
                }
            } else if ("5".equals(temp1)) {
                TaskTimeSelView taskTimeSelView = new TaskTimeSelView(this, taskEditInfo.getQuestion_num() + "." +
                        taskEditInfo.getQuestion_name(), taskEditInfo.getAnswers_()[0], isrequired);
                taskTimeSelView.isSelect(false);
                taskitemedit_questionlayout_reset.addView(taskTimeSelView, lp);
                taskEditInfo.setView(taskTimeSelView);
            } else if ("6".equals(temp1)) {//语音题
                isHavREC = true;
                TaskEditView2 taskEditView = new TaskEditView2(this, taskEditInfo.getQuestion_num() + "." +
                        taskEditInfo.getQuestion_name(), isrequired, true);
//                taskEditView.setEditValue(taskEditInfo.getAnswers_()[0]);
                if (!TextUtils.isEmpty(taskEditInfo.getAnswers_url()) && !"null".equals(taskEditInfo.getAnswers_url())) {
                    taskEditView.setRecUrl(taskEditInfo.getAnswers_url());
                }
                taskEditView.isSelect(false);
                taskitemedit_questionlayout_reset.addView(taskEditView, lp);
                taskEditInfo.setView(taskEditView);
            }
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.taskitemedit_button_reset) {
            new getAnswersAsyncTask().execute();
        }
    }

    private String answerJson;

    class getAnswersAsyncTask extends AsyncTask {
        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(TaskitemEditReset2Activity.this, "校验中...");
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
                Tools.showToast(TaskitemEditReset2Activity.this, toast);
            } else if (answerJson != null) {
                Recordup();
            }
        }
    }

    private void Recordup() {
        Recordup.sendPostRequest(Urls.Recordup3_11, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    String executeid = jsonObject.getString("executeid");
                    if (code == 200 || code == 2) {
                        String usermobile = AppInfo.getName(TaskitemEditReset2Activity.this);
                        Map<String, String> parames = new HashMap<>();
                        parames.put("taskid", taskid);
                        parames.put("pid", task_pack_id);
                        parames.put("usermobile", usermobile);
                        parames.put("storeid", store_id);
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
                                updataDBHelper.addUpdataTask(usermobile, project_id, project_name, store_num, null,
                                        store_id, store_name, task_pack_id,
                                        task_pack_name, AppInfo.TASKITEMEDIT_TASKTYPE, taskid, task_name, null, null, null,
                                        usermobile + project_id + store_id + task_pack_id + category1 + category2 + category3 + taskid
                                        , Urls.Recordup, keys, paths, UpdataDBHelper.Updata_file_type_video,
                                        paramsToMap(executeid), null, false, null, null, false);
                            } else {
                                updataDBHelper.addUpdataTask(usermobile, project_id, project_name, store_num, null, store_id, store_name,
                                        task_pack_id, task_pack_name, "3", taskid, task_name, category1, category2, category3,
                                        usermobile + project_id + store_id + task_pack_id + category1 + category2 + category3 + taskid,
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
                                ConfirmDialog.showDialog(TaskitemEditReset2Activity.this, null, jsonObject.getString("msg"), null,
                                        "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                            @Override
                                            public void leftClick(Object object) {

                                            }

                                            @Override
                                            public void rightClick(Object object) {
                                                baseFinish();
                                            }
                                        }).goneLeft();
                            } else {
                                baseFinish();
                            }
                        } catch (UnsupportedEncodingException e) {
                            Tools.showToast(TaskitemEditReset2Activity.this, "数据传输存储失败！");
                        }
                    } else {
                        Tools.showToast(TaskitemEditReset2Activity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemEditReset2Activity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemEditReset2Activity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在提交...");
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
        parames.put("pid", task_pack_id);
        parames.put("usermobile", AppInfo.getName(TaskitemEditReset2Activity.this));
        parames.put("storeid", store_id);
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

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        parames.put("taskid", taskid);
        parames.put("pid", task_pack_id);
        parames.put("usermobile", AppInfo.getName(TaskitemEditReset2Activity.this));
        parames.put("storeid", store_id);
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

    private String time;

    /**
     * 获取答案
     *
     * @return json串格式
     */
    private String getAnswers() {
        time = Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss");
        try {
            int size = questionList.size();
            JSONObject answers = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            {//多题模式
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = new JSONObject();
                    TaskEditInfo taskEditInfo = questionList.get(i);
                    /*判断题目类型*/
                    switch (Tools.StringToInt(taskEditInfo.getQuestion_type())) {
                        case 1: {
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskRadioView) {
                                TaskRadioView taskRadioView = (TaskRadioView) taskEditInfo.getView();
                                TaskQuestionInfo taskQuestionInfo = taskRadioView.getSelectAnswers();
                                if (taskEditInfo.getIsrequired().equals("1") && taskQuestionInfo == null &&
                                        taskRadioView.getVisibility() == View.VISIBLE) {//必填
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
                                if (taskEditInfo.getIsrequired().equals("1") && taskQuestionInfos.isEmpty() &&
                                        taskCheckView.getVisibility() == View.VISIBLE) {//必填
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
                                if (taskEditInfo.getIsrequired().equals("1") && taskJudgeView.isRight() == -1 &&
                                        taskJudgeView.getVisibility() == View.VISIBLE) {//必填
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
                                    if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text) &&
                                            taskEditView.getVisibility() == View.VISIBLE) {//必填
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
                                    if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text) &&
                                            taskEditView.getVisibility() == View.VISIBLE) {//必填
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
                                if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text) &&
                                        taskTimeSelView.getVisibility() == View.VISIBLE) {//必填
                                    return "error" + " 第" + taskEditInfo.getQuestion_num() + "题填写不完整！";
                                }
                                if (!TextUtils.isEmpty(text)) {
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
                        case 6: {//语音题
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskEditView2) {
                                TaskEditView2 taskEditView2 = (TaskEditView2) taskEditInfo.getView();
                                String url = taskEditView2.getUrl();
                                String netUrl = taskEditView2.getNetUrl();//之前的录音
                                if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(url) &&
                                        TextUtils.isEmpty(netUrl) && taskEditView2.getVisibility() == View.VISIBLE) {//必填
                                    return "error" + " 第" + taskEditInfo.getQuestion_num() + "题未录音！";
                                }
                                jsonObject.put("note", "");
                                jsonObject.put("answers", "");
                                jsonObject.put("question_id", taskEditInfo.getId());
                                jsonObject.put("question_type", taskEditInfo.getQuestion_type());
                                jsonObject.put("question_num", taskEditInfo.getQuestion_num());
                                if (TextUtils.isEmpty(url) && taskEditView2.getVisibility() == View.VISIBLE) {
                                    if (TextUtils.isEmpty(netUrl)) {
                                        jsonObject.put("answers_url", "");
                                    } else {
                                        jsonObject.put("answers_url", netUrl);
                                    }
                                } else {
                                    String filename = new File(url).getName();
                                    jsonObject.put("answers_url", Urls.Endpoint2 + "/" + filename);
                                    recList.add(new String[]{taskEditInfo.getId(), url});
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
        String netUrl = taskEditView.getNetUrl();
        if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text) && TextUtils.isEmpty(url) && TextUtils.isEmpty(netUrl)
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
            recList.add(new String[]{taskEditInfo.getId(), taskEditView.getUrl()});
        } else {
            if (TextUtils.isEmpty(netUrl)) {
                jsonObject.put("answers_url", "");
            } else {
                jsonObject.put("answers_url", netUrl);
            }
            jsonObject.put("note", "");
            jsonObject.put("answers", "");
            jsonObject.put("question_id", taskEditInfo.getId());
            jsonObject.put("question_type", taskEditInfo.getQuestion_type());
            jsonObject.put("question_num", taskEditInfo.getQuestion_num());
        }
        return null;
    }

    @Override
    public void onBack() {
        baseFinish();
    }


    private boolean isHad(String[] strs, String string) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals(string)) {
                return true;
            }
        }
        return false;
    }

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

}
