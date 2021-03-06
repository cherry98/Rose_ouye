package com.orange.oy.activity.black;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.CloseTaskitemShotActivity;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.BlackoutstoreInfo;
import com.orange.oy.info.TaskEditInfo;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.util.TaskQuestionComparator;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.orange.oy.view.TaskCheckView;
import com.orange.oy.view.TaskEditView;
import com.orange.oy.view.TaskJudgeView;
import com.orange.oy.view.TaskRadioView;
import com.orange.oy.view.TaskTimeSelView;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 任务列表-问卷调查页(出店)
 */
public class OutSurveyEditActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        OnTaskQuestionSumbitListener {

    private void initTitle() {
        AppTitle taskitemedit_title = (AppTitle) findViewById(R.id.taskitemedit_title);
        taskitemedit_title.settingName("问卷任务");
        taskitemedit_title.showBack(this);
    }

    public void onBack() {
        BlackDZXListActivity.isRefresh = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    protected void onStop() {
        super.onStop();
//        if (Record != null) {
//            Record.stop(Urls.Record);
//        }
        if (Recordup != null) {
            Recordup.stop(Urls.OutSurvey_Recordfinish);
        }
    }

    private String taskid, tasktype, pid, storeid;

    private void initNetworkConnection() {
//        Record = new NetworkConnection(this) {
//            public Map<String, String> getNetworkParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("taskbatch", taskbatch);
//                params.put("taskid", taskid);
//                params.put("usermobile", AppInfo.getName(OutSurveyEditActivity.this));
//                params.put("storeid", storeid);
//                params.put("taskbatch", taskbatch);
//                params.put("token", Tools.getToken());
//
//                return params;
//            }
//        };
//        Record.setIsShowDialog(true);
        Recordup = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskbatch", taskbatch);
                params.put("taskid", taskid);
                params.put("usermobile", AppInfo.getName(OutSurveyEditActivity.this));
                params.put("storeid", storeid);
                params.put("taskbatch", taskbatch);
                params.put("token", Tools.getToken());
                params.put("batch", batch);
                return params;
            }
        };
        Recordup.setIsShowDialog(true);
    }

    private String answerJson;

    class getAnswersAsyncTask extends AsyncTask {
        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(OutSurveyEditActivity.this, "校验中...");
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
                Tools.showToast(OutSurveyEditActivity.this, toast);
            } else if (answerJson != null) {
                Recordup();
            }
        }
    }

    private UpdataDBHelper updataDBHelper;

    private void Recordup() {
        Recordup.sendPostRequest(Urls.OutSurvey_Recordfinish, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200 || code == 2) {
                        String usermobile = AppInfo.getName(OutSurveyEditActivity.this);
                        Map<String, String> parames = new HashMap<>();
                        parames.put("taskid", taskid);
                        parames.put("usermobile", AppInfo.getName(OutSurveyEditActivity.this));
                        parames.put("storeid", storeid);
                        parames.put("answers", answerJson);
                        parames.put("taskbatch", taskbatch);
                        parames.put("token", Tools.getToken());
                        parames.put("batch", batch);
                        parames.put("time", time);
                        try {
                            updataDBHelper.addUpdataTask(usermobile, projectid, projectname, null, null, stroeid, storename,
                                    null, null, "3", taskid, taskname, null, null, null,
                                    usermobile + projectid + storeid + taskid,
                                    Urls.OutSurvey_Recordfinish,
                                    null, null, UpdataDBHelper.Updata_file_type_video, parames, null, true, Urls
                                            .OutrvSuey_RecordUp, paramsToString(), true);
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                            //根据tasktype判断录音、记录、拍照、定位任务
                            if (list != null && !list.isEmpty()) {
                                String tasktype = list.get(0).getTasktype();
                                if (tasktype.equals("3")) {//tasktype为3的时候是记录任务
                                    Intent intent = new Intent(OutSurveyEditActivity.this, OutSurveyEditActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("list", list);
                                    intent.putExtra("data", bundle);
                                    startActivity(intent);
                                } else if (tasktype.equals("5")) {//tasktype为5的时候是录音任务
                                    Intent intent = new Intent(OutSurveyEditActivity.this, OutSurveyRecordillustrateActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("list", list);
                                    intent.putExtra("data", bundle);
                                    startActivity(intent);
                                } else if (tasktype.equals("4")) {//tasktype为4的时候是定位任务
                                    Intent intent = new Intent(OutSurveyEditActivity.this, OutSurveyMapActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("list", list);
                                    intent.putExtra("data", bundle);
                                    startActivity(intent);
                                } else if (tasktype.equals("1")) {//tasktype为1的时候是拍照任务
                                    Intent intent = new Intent(OutSurveyEditActivity.this,
                                            OutSurveyTakephotoActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("list", list);
                                    intent.putExtra("data", bundle);
                                    intent.putExtra("tasktype", tasktype);
                                    startActivity(intent);
                                } else if (tasktype.equals("8")) {//tasktype为1的时候是防翻拍-拍照任务
                                    Intent intent = new Intent(OutSurveyEditActivity.this,
                                            OutSurveyTakephotoActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("list", list);
                                    intent.putExtra("data", bundle);
                                    intent.putExtra("tasktype", tasktype);
                                    startActivity(intent);
                                }
                            } else {
                                BlackDZXListActivity.isRefresh = true;
                            }
                            if (code == 2) {
                                ConfirmDialog.showDialog(OutSurveyEditActivity.this, null, jsonObject.getString("msg"), null,
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
                                baseFinish();
                            }
                        } catch (UnsupportedEncodingException e) {
                            Tools.showToast(OutSurveyEditActivity.this, "数据传输存储失败！");
                        }
                    } else {
                        Tools.showToast(OutSurveyEditActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OutSurveyEditActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(OutSurveyEditActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在提交...");
    }

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        parames.put("taskid", taskid);
        parames.put("usermobile", AppInfo.getName(OutSurveyEditActivity.this));
        parames.put("storeid", storeid);
        parames.put("answers", answerJson);
        parames.put("taskbatch", taskbatch);
        parames.put("token", Tools.getToken());
        parames.put("batch", batch);
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

    private NetworkConnection Recordup;
    private LinearLayout taskitemedit_question_layout;
    private TextView taskitemedit_name;
    private TextView taskitemedit_type;
    private View taskitemedit_progressbar;
    //进度条
    private ProgressBar progressbar;
    private int progressbarWidth;
    private ImageView progressbar_biaoshi, progressbar_jiedian3;
    private String category1 = "", category2 = "", category3 = "";
    private String projectid;
    private GridView taskitemedit_gridview;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter;
    private TextView taskitemedit_desc;
    private ImageLoader imageLoader;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemedit);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        imageLoader = new ImageLoader(this);
        initTitle();
        taskitemedit_name = (TextView) findViewById(R.id.taskitemedit_name);
        taskitemedit_type = (TextView) findViewById(R.id.taskitemedit_type);
        taskitemedit_question_layout = (LinearLayout) findViewById(R.id.taskitemedit_question_layout);
        taskitemedit_progressbar = findViewById(R.id.taskitemedit_progressbar);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressbar_biaoshi = (ImageView) findViewById(R.id.progressbar_biaoshi);
//        progressbar_jiedian2 = (ImageView) findViewById(R.id.progressbar_jiedian2);
        progressbar_jiedian3 = (ImageView) findViewById(R.id.progressbar_jiedian3);
        taskitemedit_gridview = (GridView) findViewById(R.id.taskitemedit_gridview);
        taskitemedit_desc = (TextView) findViewById(R.id.taskitemedit_desc);
        findViewById(R.id.taskitemedit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//TODO 确定按钮
                new getAnswersAsyncTask().execute();
            }
        });
        qusetionPosition = -1;
        adapter = new TaskitemReqPgAdapter(this, picList);
        taskitemedit_gridview.setAdapter(adapter);
        getData();
        taskitemedit_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoView imageView = new PhotoView(OutSurveyEditActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageLoader.DisplayImage(picList.get(position), imageView);
                SelecterDialog.showView(OutSurveyEditActivity.this, imageView);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        progressbarWidth = 0;
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
        int temp = (int) (progressbarWidth * pe - getResources().getDimension(R.dimen.ouye_progressbarbiaoshi) + getResources().getDimension(R.dimen.ouye_progressbarmargin));
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

    /**
     * 出店问卷记录上传参数
     */
    private String projectname;
    private String stroeid;
    private String storenum;
    private String storename;
    private String taskbatch;
    private String taskname;
    private String note;
    private JSONArray datas;
    private String questionnaire_type;
    private BlackoutstoreInfo blackoutstoreInfo;
    private ArrayList<BlackoutstoreInfo> list;

    private void getData() {
        //TODO 接收上个页面传来的数据集合
        list = (ArrayList<BlackoutstoreInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        blackoutstoreInfo = list.remove(0);
        projectid = blackoutstoreInfo.getProjectid();
        projectname = blackoutstoreInfo.getProjectname();
        storeid = blackoutstoreInfo.getStroeid();
        storenum = blackoutstoreInfo.getStorenum();
        storename = blackoutstoreInfo.getStorename();
        tasktype = blackoutstoreInfo.getTasktype();
        taskname = blackoutstoreInfo.getTaskname();
        note = blackoutstoreInfo.getNote();
        taskid = blackoutstoreInfo.getTaskid();
        taskbatch = blackoutstoreInfo.getTaskbatch();
        stroeid = blackoutstoreInfo.getStroeid();
        JSONArray jsonArray = blackoutstoreInfo.getDatas();
        questionnaire_type = blackoutstoreInfo.getQuestionnaire_type();
        batch = blackoutstoreInfo.getBatch();
        taskitemedit_name.setText(taskname);
        taskitemedit_desc.setText(blackoutstoreInfo.getNote());
        String picStr = blackoutstoreInfo.getPics();
        if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 4) {
            findViewById(R.id.shili).setVisibility(View.GONE);
            taskitemedit_gridview.setVisibility(View.GONE);
        } else {
            picStr = picStr.substring(1, picStr.length() - 1);
            String[] pics = picStr.split(",");
            for (int i = 0; i < pics.length; i++) {
                picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
            }
            if (pics.length > 0) {
                int t = (int) Math.ceil(pics.length / 3d);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitemedit_gridview.getLayoutParams();
                lp.height = (int) ((Tools.getScreeInfoWidth(OutSurveyEditActivity.this) -
                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                taskitemedit_gridview.setLayoutParams(lp);
            }
            adapter.notifyDataSetChanged();
        }
        if (questionnaire_type.equals("1")) {
            taskitemedit_type.setText("单题模式");
        } else if (questionnaire_type.equals("2")) {
            taskitemedit_type.setText("多题模式");
        }
        try {
            JSONObject jsonObject;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int qusetionPosition;//单题模式下，题目链表中当前题目的下标
    private Boolean showEndButton;//是否显示完成按钮
    private int qusetionnum = 1;

    /**
     * TODO 添加题目布局（单题用）
     */
    private void addView(TaskEditInfo taskEditInfo) {
        if ("-1".equals(taskEditInfo.getJump_question())) {
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
        int questionListSize = questionList.size();
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
            TaskEditView taskEditView = new TaskEditView(this, qusetionnum + "." + taskEditInfo
                    .getQuestion_name(), isrequired);
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
                taskRadioView.setTitle(taskEditInfo.getQuestion_num() + "." + taskEditInfo.getQuestion_name(), isrequired);
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
                TaskEditView taskEditView = new TaskEditView(this, taskEditInfo.getQuestion_num() + "." +
                        taskEditInfo.getQuestion_name(), isrequired);
                taskEditInfo.setView(taskEditView);
            } else if ("5".equals(temp1)) {
                TaskTimeSelView taskTimeSelView = new TaskTimeSelView(this, taskEditInfo.getQuestion_num() + "." +
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

