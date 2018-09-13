package com.orange.oy.activity.bright;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskEditInfo;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.TaskQuestionComparator;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.TaskCheckView;
import com.orange.oy.view.TaskEditView;
import com.orange.oy.view.TaskJudgeView;
import com.orange.oy.view.TaskRadioView;
import com.orange.oy.view.TaskTimeSelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BrightEditActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        OnTaskQuestionSumbitListener {

    private void initTitle() {
        AppTitle taskitemedit_title = (AppTitle) findViewById(R.id.taskitemedit_title);
        taskitemedit_title.settingName("问卷任务");
        taskitemedit_title.showBack(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Record != null) {
            Record.stop(Urls.Record);
        }
        if (assistantTask != null) {
            assistantTask.stop(Urls.AssistantTask);
        }
    }

    private void initNetworkConnection() {
        Record = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", task_id);
                params.put("tasktype", tasktype);
                params.put("token", Tools.getToken());
                params.put("batch", batch);
                return params;
            }
        };
        Record.setIsShowDialog(true);
        assistantTask = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(BrightEditActivity.this));
                params.put("clienttime", date);
                params.put("executeid", executeid);
                params.put("taskbatch", batch);
                return params;
            }
        };
    }

    private String date, project_id, project_name, codeStr, brand, store_num, store_id, task_name, store_name,
            executeid, task_id, batch, tasktype;
    private NetworkConnection Record, assistantTask;
    private UpdataDBHelper updataDBHelper;
    private LinearLayout taskitemedit_question_layout;
    private TextView taskitemedit_name;
    private TextView taskitemedit_type;
    private View taskitemedit_progressbar;
    private ProgressBar progressbar;
    private int progressbarWidth;
    private ImageView progressbar_biaoshi, progressbar_jiedian3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemedit);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        initTitle();
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date = sDateFormat.format(new java.util.Date());
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("projectname");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        store_num = data.getStringExtra("store_num");
        store_name = data.getStringExtra("store_name");
        store_id = data.getStringExtra("outletid");
        task_name = data.getStringExtra("taskName");
        executeid = data.getIntExtra("executeid", 0) + "";
        task_id = data.getIntExtra("taskid", 0) + "";
        batch = data.getStringExtra("batch");
        tasktype = data.getStringExtra("tasktype");
        taskitemedit_name = (TextView) findViewById(R.id.taskitemedit_name);
        taskitemedit_type = (TextView) findViewById(R.id.taskitemedit_type);
        taskitemedit_question_layout = (LinearLayout) findViewById(R.id.taskitemedit_question_layout);
        taskitemedit_progressbar = findViewById(R.id.taskitemedit_progressbar);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressbar_biaoshi = (ImageView) findViewById(R.id.progressbar_biaoshi);
//        progressbar_jiedian2 = (ImageView) findViewById(R.id.progressbar_jiedian2);
        progressbar_jiedian3 = (ImageView) findViewById(R.id.progressbar_jiedian3);
        findViewById(R.id.taskitemedit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//TODO 确定按钮
                new GetAnswersAsyncTask().execute();
            }
        });
        qusetionPosition = -1;
        getData();
    }

    private ArrayList<TaskEditInfo> questionList;//所有题目链表

    private void getData() {
        Record.sendPostRequest(Urls.Record, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String questionnaire_type = jsonObject.getString("questionnaire_type");// 问卷形式，1为单题形式，2为多题形式
                        taskitemedit_name.setText(jsonObject.getString("task_name"));
//                        String note = jsonObject.getString("note");
//                        if ("null".equals(note)) {
//                            note = "";
//                        }
//                        taskitemedit_value.setText(note);
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
                        Tools.showToast(BrightEditActivity.this, jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(BrightEditActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightEditActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void sendData() {
        assistantTask.sendPostRequest(Urls.AssistantTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        String username = AppInfo.getName(BrightEditActivity.this);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("usermobile", username);
                        map.put("executeid", executeid);
                        map.put("answers", answerJson);
                        try {
                            updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                    store_id, store_name, null, null, "3-3", task_id, task_name,
                                    null, null, null, username + project_id + store_id
                                            + task_id, Urls.AssistantTaskRecordUp,
                                    null, null, UpdataDBHelper.Updata_file_type_video, map,
                                    null, true, Urls.AssistantTask, paramsToString(), false);
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                            BrightBallotResultActivity.isRefresh = true;
                            BrightBallotActivity.isRefresh = true;
                            BrightPersonInfoActivity.isRefresh = true;
                            baseFinish();
                        } catch (UnsupportedEncodingException e) {
                            Tools.showToast(BrightEditActivity.this, "数据传输存储失败！");
                        }
                    } else {
                        Tools.showToast(BrightEditActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightEditActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightEditActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(BrightEditActivity.this));
        parames.put("clienttime", date);
        parames.put("executeid", executeid);
        parames.put("taskbatch", batch);
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

    @Override
    public void onBack() {
        baseFinish();
    }

    private String answerJson;

    class GetAnswersAsyncTask extends AsyncTask {
        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(BrightEditActivity.this, "校验中...");
        }

        protected Object doInBackground(Object[] params) {
            answerJson = getAnswers();
            return null;
        }

        protected void onPostExecute(Object o) {
            CustomProgressDialog.Dissmiss();
            Tools.d(answerJson);
            if (answerJson != null && answerJson.startsWith("error")) {
                Tools.showToast(BrightEditActivity.this, answerJson);
            } else if (answerJson != null) {
                sendData();
            }
        }
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
                                if (taskEditInfo.getIsrequired().equals("1") && taskQuestionInfo == null) {//必填
                                    return "error 请认真答完题目！";
                                }
                                if (taskQuestionInfo != null) {//如果做了
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
                                if (taskEditInfo.getIsrequired().equals("1") && taskQuestionInfos.isEmpty()) {//必填
                                    return "error 请认真答完题目！";
                                }
                                if (!taskQuestionInfos.isEmpty()) {//如果做了
                                    int taskQuestionInfosSize = taskQuestionInfos.size();
                                    int maxOption = Tools.StringToInt(taskEditInfo.getMax_option());
                                    int minOptino = Tools.StringToInt(taskEditInfo.getMin_option());
                                    if (taskQuestionInfosSize < minOptino || taskQuestionInfosSize > maxOption) {
                                        return "error 第" + taskEditInfo.getQuestion_num() + "道题有多选题选择数量不正确";
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
                                if (taskEditInfo.getIsrequired().equals("1") && taskJudgeView.isRight() == -1) {//必填
                                    return "error 请认真答完题目！";
                                }
                                if (taskJudgeView.isRight() != -1) {
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
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskEditView) {
                                TaskEditView taskEditView = (TaskEditView) taskEditInfo.getView();
                                String text = taskEditView.getText();
                                if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text)) {//必填
                                    return "error 请认真答完题目！";
                                }
                                if (!TextUtils.isEmpty(text)) {
                                    int max = Tools.StringToInt(taskEditInfo.getMax_word_num());
                                    int min = Tools.StringToInt(taskEditInfo.getMin_word_num());
                                    int length = text.length();
                                    if (max == -1 && min == -1) {
                                        if (length > 500)
                                            return "error 第" + taskEditInfo.getQuestion_num() + "道题字数应小于500字";
                                    } else if (length > max || length < min) {
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
                        }
                        break;
                        case 5: {
                            if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskTimeSelView) {
                                TaskTimeSelView taskTimeSelView = (TaskTimeSelView) taskEditInfo.getView();
                                String text = taskTimeSelView.getText();
                                if (taskEditInfo.getIsrequired().equals("1") && TextUtils.isEmpty(text)) {//必填
                                    return "error 日期题目填写不完整！";
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

    private int qusetionPosition;//单题模式下，题目链表中当前题目的下标

    /**
     * TODO 添加题目布局（单题用）
     */
    private void addView(TaskEditInfo taskEditInfo) {
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
        int questionListSize = questionList.size();
        if (taskitemedit_progressbar != null && taskitemedit_progressbar.getVisibility() == View.VISIBLE)
            settingProgressbar(taskEditInfo.getQuestion_num());//设置进度条
        if ("1".equals(temp1)) {
            TaskRadioView taskRadioView = new TaskRadioView(this);
            taskRadioView.setTitle(taskEditInfo.getQuestion_num() + "、" + taskEditInfo.getQuestion_name(), isrequired);
            if (questionListSize == qusetionPosition) {
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
            taskCheckView.setTitle(taskEditInfo.getQuestion_num() + "、" + taskEditInfo.getQuestion_name(), isrequired);
            if (questionListSize == qusetionPosition) {
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
            TaskJudgeView taskJudgeView = new TaskJudgeView(this, taskEditInfo.getQuestion_num() + "、" +
                    taskEditInfo.getQuestion_name(), isrequired);
            if (questionListSize == qusetionPosition) {
                taskJudgeView.setSubmitText("完成");
            }
            taskJudgeView.setOnTaskQuestionSumbitListener(this);
            taskitemedit_question_layout.addView(taskJudgeView, lp);
            taskEditInfo.setView(taskJudgeView);
        } else if ("4".equals(temp1)) {
            TaskEditView taskEditView = new TaskEditView(this, taskEditInfo.getQuestion_num() + "、" + taskEditInfo
                    .getQuestion_name(), isrequired);
            if (questionListSize == qusetionPosition) {
                taskEditView.setSubmitText("完成");
            }
            taskEditView.setOnTaskQuestionSumbitListener(this);
            taskitemedit_question_layout.addView(taskEditView, lp);
            taskEditInfo.setView(taskEditView);
        } else if ("5".equals(temp1)) {
            TaskTimeSelView taskTimeSelView = new TaskTimeSelView(this, taskEditInfo.getQuestion_num() + "、" + taskEditInfo
                    .getQuestion_name(), isrequired);
            if (questionListSize == qusetionPosition) {
                taskTimeSelView.setSubmitText("完成");
            }
            taskTimeSelView.setOnTaskQuestionSumbitListener(this);
            taskitemedit_question_layout.addView(taskTimeSelView, lp);
            taskEditInfo.setView(taskTimeSelView);
        }
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
                taskRadioView.setTitle(taskEditInfo.getQuestion_num() + "、" + taskEditInfo.getQuestion_name(), isrequired);
                optionList = taskEditInfo.getOptions();
                int length = optionList.size();
//                for (int j = 0; j < length; j++) {
//                    taskRadioView.addRadioButton(optionList.get(j).getId(), optionList.get(j).getOption_name());
//                }
                for (int j = 0; j < length; j++) {
                    String isfill = optionList.get(j).getIsfill();
                    if ("1".equals(isfill)) {
                        taskRadioView.addRadioButtonForFill(optionList.get(j).getId(), optionList.get(j)
                                .getOption_name(), optionList.get(j).getIsforcedfill(), optionList.get(j).getPhoto_url());
                    } else {
                        taskRadioView.addRadioButton(optionList.get(j).getId(), optionList.get(j).getOption_name(), optionList.get(j).getPhoto_url());
                    }
                }
                taskitemedit_question_layout.addView(taskRadioView, lp);
                taskEditInfo.setView(taskRadioView);
            } else if ("2".equals(temp1)) {
                TaskCheckView taskCheckView = new TaskCheckView(this);
                taskCheckView.setTitle(taskEditInfo.getQuestion_num() + "、" + taskEditInfo.getQuestion_name(), isrequired);
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
                TaskJudgeView taskJudgeView = new TaskJudgeView(this, taskEditInfo.getQuestion_num() + "、" +
                        taskEditInfo.getQuestion_name(), isrequired);
                taskitemedit_question_layout.addView(taskJudgeView, lp);
                taskEditInfo.setView(taskJudgeView);
            } else if ("4".equals(temp1)) {
                TaskEditView taskEditView = new TaskEditView(this, taskEditInfo.getQuestion_num() + "、" +
                        taskEditInfo.getQuestion_name(), isrequired);
                taskitemedit_question_layout.addView(taskEditView, lp);
                taskEditInfo.setView(taskEditView);
            } else if ("5".equals(temp1)) {
                TaskTimeSelView taskTimeSelView = new TaskTimeSelView(this, taskEditInfo.getQuestion_num() + "、" +
                        taskEditInfo.getQuestion_name(), isrequired);
                taskitemedit_question_layout.addView(taskTimeSelView, lp);
                taskEditInfo.setView(taskTimeSelView);
            }
        }
    }

    /**
     * TODO 单题模式下，题目的提交按钮
     */
    public void sumbit(TaskQuestionInfo[] answers, String[] notes) {
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
                        if ("1".equals(answers[i].getJump()) && Tools.StringToInt(answers[i].getJumpquestion()) != -1) {//选项强制跳题
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
                    if ("1".equals(answers[0].getJump()) && Tools.StringToInt(answers[0].getJumpquestion()) != -1) {//选项强制跳题
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
                /*存储答案*/
                if (!isEmpty(answers)) {
                    taskEditInfo.setAnswers(answers[0].getId());
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
        if (qusetionPosition == questionList.size()) {
            new GetAnswersAsyncTask().execute();
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
                }
            }
            addView(questionList.get(qusetionPosition++));
        }
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
