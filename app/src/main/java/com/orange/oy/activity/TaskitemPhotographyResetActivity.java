package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.ImageResetAdapter;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.allinterface.OnTaskEditRefreshListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskEditInfo;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.TaskCheckView;
import com.orange.oy.view.TaskRadioView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

public class TaskitemPhotographyResetActivity extends BaseActivity implements View.OnClickListener, AppTitle
        .OnBackClickForAppTitle, AppTitle.OnExitClickForAppTitle, AdapterView.OnItemClickListener,
        OnTaskEditRefreshListener, ImageResetAdapter.OnItemClickedListener {
    private AppTitle appTitle;

    private void initTitle(String str) {
        appTitle = (AppTitle) findViewById(R.id.taskitempgnexty_title_reset);
        appTitle.settingName("拍照任务");
        appTitle.showBack(this);
        appTitle.settingExit("编辑", getResources().getColor(R.color.homepage_select), this);
    }

    private boolean isDel = false;

    public void onExit() {
        if (isDel) {
            appTitle.settingExit("编辑");
            imageResetAdapter.setEdit(false);
            if (!TextUtils.isEmpty(id) && !"null".equals(id)) {
                ((TaskEditClearListener) taskEditInfo.getView()).isSelect(true);
            }
            list_single.removeAll(selectPhotoList);
            for (int i = 0; i < selectPhotoList.size(); i++) {
                String url = selectPhotoList.get(i).replaceAll("\"", "");
                if (!url.startsWith("http://")) {
                    selectImgList.remove(selectPhotoList.get(i));
                    appDBHelper.deletePhotoFromPath2(selectPhotoList.get(i));
                }
            }
            selectPhotoList.clear();
        } else {
            appTitle.settingExit("删除");
            imageResetAdapter.setEdit(true);
        }
        isDel = !isDel;
        taskitempgnexty_editreset.setFocusable(true);
        taskitempgnexty_editreset.setEnabled(true);
        taskitempgnexty_editreset.setFocusableInTouchMode(true);
        if (!list_single.contains("add_photo")) {
            list_single.add(list_single.size(), "add_photo");
        }
        findViewById(R.id.taskitempgnexty_button).setVisibility(View.VISIBLE);
        findViewById(R.id.taskitempgnexty_button).setOnClickListener(this);
        imageResetAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(CheckBox view, String photoUrl) {
        if (view.isChecked()) {
            selectPhotoList.add(photoUrl);
        } else {
            selectPhotoList.remove(photoUrl);
        }
        Tools.d("选择要删除的图片：" + selectPhotoList.toString());
    }

    private void initNetworkConnection() {
        photo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", task_id);
                return params;
            }
        };
        photo.setIsShowDialog(true);
        taskPhotoup_Add = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_id", task_id);
                params.put("user_mobile", AppInfo.getName(TaskitemPhotographyResetActivity.this));
                params.put("status", "0");
                params.put("task_pack_id", task_pack_id);
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("category1", category1);
                params.put("category2", category2);
                params.put("category3", category3);
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                params.put("is_fill", "0");
                if (taskEditInfo != null) {
                    params.put("question_id", taskEditInfo.getId());
                }
                if (answers != null) {
                    params.put("answers", answers);
                }
                if (notes != null) {
                    params.put("note", notes);
                }
                params.put("txt1", Tools.filterEmoji(taskitempgnexty_editreset.getText().toString().trim()));
                return params;
            }
        };
        taskPhotoup_Add.setIsShowDialog(true);
    }

    private NetworkConnection photo, taskPhotoup_Add;
    private UpdataDBHelper updataDBHelper;
    private SystemDBHelper systemDBHelper;
    private AppDBHelper appDBHelper;
    private String username;
    private String project_id, store_id, task_pack_id, category1, category2, category3, task_id,
            project_name, task_name, task_pack_name,
            store_num, store_name, outlet_batch, p_batch, photo_compression, codeStr, brand, is_desc, batch, task_type;
    private GridView taskitempgnexty_gridview_reset;
    private ArrayList<String> list_single = new ArrayList<String>();
    private ImageResetAdapter imageResetAdapter;
    private TextView taskitempgnexty_name_reset;
    private LinearLayout taskitempgnexty_bg2_reset;
    private ArrayList<String> selectImgList = new ArrayList();
    private ArrayList<String> originalImgList = new ArrayList<>();
    private MyGridView taskitempgnexty_gridview1_reset;
    private ArrayList<String> picList = new ArrayList<>();//示例图片
    private TaskitemReqPgAdapter adapter2;
    private TextView taskitempgnexty_desc_reset;
    private ImageView spread_button;
    private boolean isSpread;
    private String answers, notes;
    private ArrayList<String> selectPhotoList;//选择要删除的照片
    private EditText taskitempgnexty_editreset;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitem_photography_reset);
        Intent data = getIntent();
        updataDBHelper = new UpdataDBHelper(this);
        appDBHelper = new AppDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        username = AppInfo.getName(this);
        selectPhotoList = new ArrayList<>();
        initTitle(data.getStringExtra("task_name"));
        project_id = data.getStringExtra("project_id");
        store_id = data.getStringExtra("store_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        task_id = data.getStringExtra("task_id");
        project_name = data.getStringExtra("project_name");
        task_name = data.getStringExtra("task_name");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_num = data.getStringExtra("store_num");
        store_name = data.getStringExtra("store_name");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        photo_compression = data.getStringExtra("photo_compression");
        Tools.d("photo_compression:" + photo_compression);
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_desc = data.getStringExtra("is_desc");
        task_type = data.getStringExtra("task_type");
        appDBHelper.deletePhotoUrl(project_id, store_id, task_id);
        taskitempgnexty_editreset = (EditText) findViewById(R.id.taskitempgnexty_editreset);
        spread_button = (ImageView) findViewById(R.id.spread_button);
        taskitempgnexty_desc_reset = (TextView) findViewById(R.id.taskitempgnexty_desc_reset);
        taskitempgnexty_gridview1_reset = (MyGridView) findViewById(R.id.taskitempgnexty_gridview1_reset);
        taskitempgnexty_name_reset = (TextView) findViewById(R.id.taskitempgnexty_name_reset);
        taskitempgnexty_gridview_reset = (MyGridView) findViewById(R.id.taskitempgnexty_gridview_reset);
        imageResetAdapter = new ImageResetAdapter(this, list_single);
        taskitempgnexty_gridview_reset.setAdapter(imageResetAdapter);
        imageResetAdapter.setOnShowItemClickListener(this);
        taskitempgnexty_gridview_reset.setOnItemClickListener(this);
        taskitempgnexty_bg2_reset = (LinearLayout) findViewById(R.id.taskitempgnexty_bg2_reset);
        adapter2 = new TaskitemReqPgAdapter(this, picList);
        taskitempgnexty_gridview1_reset.setAdapter(adapter2);
        initNetworkConnection();
        getData();
    }

    public static final int TakeRequest = 0x100;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitempgnexty_button: {
                sendData();
            }
            break;
            case R.id.spread_button_layout: {
                if (!TextUtils.isEmpty(picStr) && !"null".equals(picStr)) {
                    if (taskitempgnexty_gridview1_reset.getVisibility() == View.VISIBLE) {
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        findViewById(R.id.shili).setVisibility(View.GONE);
                        taskitempgnexty_gridview1_reset.setVisibility(View.GONE);
                        taskitempgnexty_desc_reset.setSingleLine(true);
                    } else {
                        spread_button.setImageResource(R.mipmap.spread_button_up);
                        taskitempgnexty_desc_reset.setSingleLine(false);
                        findViewById(R.id.shili).setVisibility(View.VISIBLE);
                        taskitempgnexty_gridview1_reset.setVisibility(View.VISIBLE);
                    }
                } else {
                    findViewById(R.id.shili).setVisibility(View.GONE);
                    taskitempgnexty_gridview1_reset.setVisibility(View.GONE);
                    if (isSpread) {//说明展开
                        isSpread = false;
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        taskitempgnexty_desc_reset.setSingleLine(true);
                    } else {
                        isSpread = true;
                        spread_button.setImageResource(R.mipmap.spread_button_up);
                        taskitempgnexty_desc_reset.setSingleLine(false);
                    }
                }
            }
            break;
        }
    }

    private int code;
    private String picStr;
    private int max_num;
    private TaskEditInfo taskEditInfo;
    private String id;

    public void getData() {
        photo.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    code = jsonObject.optInt("code");
                    batch = jsonObject.optString("batch");
                    if (code == 200) {
                        taskitempgnexty_desc_reset.setText(jsonObject.getString("task_note"));
                        if (taskitempgnexty_desc_reset.getLineCount() > 1) {
                            taskitempgnexty_desc_reset.setSingleLine(true);
                            isSpread = false;
                            findViewById(R.id.spread_button_layout).setOnClickListener(TaskitemPhotographyResetActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.spread_button_layout).setVisibility(View.GONE);
                        }
                        String beizhu = jsonObject.getString("beizhu").replaceAll("\\[\"", "").replaceAll("\"]", "");
                        if (!TextUtils.isEmpty(beizhu) && !"null".equals(beizhu)) {
                            taskitempgnexty_editreset.setText(beizhu);
                        }
                        taskitempgnexty_editreset.setFocusable(false);
                        taskitempgnexty_editreset.setEnabled(false);
                        JSONObject phototask_data = jsonObject.getJSONObject("phototask_data");
                        max_num = Tools.StringToInt(phototask_data.getString("num"));
                        if (max_num < 0) {
                            max_num = 9;
                        }
                        picStr = phototask_data.getString("pics");
                        picStr = picStr.replaceAll("\\\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
                        picStr = URLDecoder.decode(picStr, "utf-8");
                        findViewById(R.id.shili).setVisibility(View.GONE);
                        taskitempgnexty_gridview1_reset.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(picStr) && !"null".equals(picStr)) {
                            findViewById(R.id.spread_button_layout).setOnClickListener(TaskitemPhotographyResetActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                            String[] pics = picStr.split(",");
                            for (int i = 0; i < pics.length; i++) {
                                String url = pics[i].replaceAll("\"", "").replaceAll("\\\\", "");
                                if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                                    url = Urls.ImgIp + url;
                                }
                                picList.add(url);
                            }
                            adapter2.notifyDataSetChanged();
                        }
                        String photos = null;
                        photos = URLDecoder.decode(jsonObject.getString("photo_datas"), "utf-8");
                        if (!TextUtils.isEmpty(photos) && !"null".equals(photos)) {
                            photos = photos.replaceAll("\\[\"", "").replaceAll("\"]", "");
                            String[] photo_datas = photos.split(",");
                            Collections.addAll(list_single, photo_datas);
                            imageResetAdapter.notifyDataSetChanged();
                        }
                        taskitempgnexty_name_reset.setText(jsonObject.optString("task_name"));
                        //拍照问题引入
                        String[] answers = null;
                        String[] notes = null;
                        jsonObject = jsonObject.optJSONObject("phototask_data");
                        id = jsonObject.getString("id");
                        if (!TextUtils.isEmpty(id) && !"null".equals(id)) {
                            if (jsonObject.optString("answers") != null) {
                                answers = jsonObject.getString("answers").split(",");
                            }
                            if (jsonObject.optString("note") != null) {
                                notes = jsonObject.getString("note").split(",");
                            }
                            taskEditInfo = new TaskEditInfo();
                            taskEditInfo.setId(id);
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
                            taskEditInfo.setQuestion_num(1);
                            taskEditInfo.setAnswers_(answers);
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
                            taskitempgnexty_bg2_reset.removeAllViews();
                            addView();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.showToast(TaskitemPhotographyResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemPhotographyResetActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void addView() {
        String temp1;
        ArrayList<TaskEditoptionsInfo> optionList;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 20;
        boolean isrequired = (taskEditInfo.getIsrequired().equals("1"));
        temp1 = taskEditInfo.getQuestion_type();
        if ("1".equals(temp1)) {
            TaskRadioView taskRadioView = new TaskRadioView(this);
            taskRadioView.setOnTaskEditRefreshListener(this);
            taskRadioView.settingData(taskEditInfo);
            taskRadioView.setTitle(taskEditInfo.getQuestion_num() + "." + taskEditInfo.getQuestion_name(), isrequired);
            taskRadioView.isSelect(false);
            String jumpQuestion = taskEditInfo.getJump_question();
            optionList = taskEditInfo.getOptions();
            int length = optionList.size();
            for (int j = 0; j < length; j++) {
                TaskEditoptionsInfo taskEditoptionsInfo = optionList.get(j);
                String isfill = taskEditoptionsInfo.getIsfill();
                String note = "";
                if (taskEditInfo.getNotes() != null && taskEditInfo.getNotes().length > 0) {
                    note = taskEditInfo.getNotes()[0];
                }
                if ("1".equals(isfill)) {
                    taskRadioView.addRadioButtonForFill(taskEditoptionsInfo.getId(), taskEditoptionsInfo
                                    .getOption_name(), taskEditoptionsInfo.getIsforcedfill(), "1", jumpQuestion,
                            note, taskEditInfo.getAnswers_()[0].equals(taskEditoptionsInfo.getId()), taskEditoptionsInfo.getPhoto_url());
                } else {
                    taskRadioView.addRadioButton(taskEditoptionsInfo.getId(), taskEditoptionsInfo.getOption_name(), "1", jumpQuestion, taskEditInfo.getAnswers_()[0].equals(taskEditoptionsInfo.getId()), taskEditoptionsInfo.getPhoto_url());
                }
            }
            taskitempgnexty_bg2_reset.addView(taskRadioView, lp);
            taskEditInfo.setView(taskRadioView);
        } else if ("2".equals(temp1)) {
            TaskCheckView taskCheckView = new TaskCheckView(this);
            taskCheckView.setOnTaskEditRefreshListener(this);
            taskCheckView.settingData(taskEditInfo);
            taskCheckView.setTitle(taskEditInfo.getQuestion_num() + "." + taskEditInfo.getQuestion_name(), isrequired);
            optionList = taskEditInfo.getOptions();
            int length = optionList.size();
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
            taskitempgnexty_bg2_reset.addView(taskCheckView, lp);
            taskEditInfo.setView(taskCheckView);
        }
    }

    private String getAnswers() {
        if (taskEditInfo != null) {
            if (Tools.StringToInt(taskEditInfo.getQuestion_type()) == 1) {
                if (taskEditInfo.getView() != null && taskEditInfo.getView() instanceof TaskRadioView) {
                    TaskRadioView taskRadioView = (TaskRadioView) taskEditInfo.getView();
                    TaskQuestionInfo taskQuestionInfo = taskRadioView.getSelectAnswers();
                    if (taskEditInfo.getIsrequired().equals("1") && taskQuestionInfo == null) {//必填
                        return "error 请认真答完题目！";
                    }
                    if (taskQuestionInfo != null) {//如果做了
                        notes = " ";
                        if (taskQuestionInfo.getNoteEditext() != null && !TextUtils.isEmpty(taskQuestionInfo
                                .getNoteEditext().getText().toString().trim())) {//判断备注
                            notes = taskQuestionInfo.getNoteEditext().getText().toString().trim();
                        } else if (taskEditInfo.getNotes() != null) {
                            notes = taskEditInfo.getNotes()[0];
                        } else if (taskQuestionInfo.isRequired()) {
                            return "error 被选项备注必填！";
                        }
                        answers = taskQuestionInfo.getId();
                    }
                } else if (Tools.StringToInt(taskEditInfo.getQuestion_type()) == 2) {
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
                            String ids = null;
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
                                        notes = notes + ",";
                                    }
                                }
                                if (ids == null) {
                                    ids = taskQuestionInfo.getId();
                                } else {
                                    ids = ids + "," + taskQuestionInfo.getId();
                                }
                            }
                            answers = ids;
                        }
                    }
                }
            }
        }
        return answers;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakeRequest:
                    String path = data.getStringExtra("path");
                    File file = new File(path);
                    if (!file.isFile()) {
                        Tools.showToast(this, "拍照方式错误");
                        return;
                    }
                    selectImgList.add(path);
                    list_single.add(list_single.size() - 1, path);
                    new zoomImageAsyncTask(path).executeOnExecutor(Executors.newCachedThreadPool());
                    break;
            }
        }
    }

    private boolean isHad(String[] strs, String string) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals(string)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private boolean isLegal(String path) {
        File file = new File(path);
        return file.length() > 51200;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ("add_photo".equals(list_single.get(position))) {
            if (list_single.size() - 1 < max_num) {
                Intent intent_add = new Intent(TaskitemPhotographyResetActivity.this, Camerase.class);
                intent_add.putExtra("projectid", project_id);
                intent_add.putExtra("storeid", store_id);
                intent_add.putExtra("packageid", task_pack_id);
                intent_add.putExtra("taskid", task_id);
                intent_add.putExtra("storecode", store_num);
                intent_add.putExtra("maxTake", 1);
                intent_add.putExtra("state", 4);
                if (task_type.equals("8")) {
                    intent_add.putExtra("isCFouce", true);
                }
                startActivityForResult(intent_add, TakeRequest);
            } else {
                Tools.showToast(TaskitemPhotographyResetActivity.this, "拍照数量已达到上限");
            }
        }
    }

    @Override
    public void changeView(Object src, int srcNum, String jumpNum, String oldSelectPosition) {
//        int qid = Tools.StringToInt(jumpNum);
//        if (qid != -1) {//多题模式下的题目隐藏控制
//            if (taskEditInfo.getQuestion_num() > i && taskEditInfo.getQuestion_num() < qid) {
//                View view = taskEditInfo.getView();
//                if (view instanceof TaskEditClearListener) {
//                    ((TaskEditClearListener) view).dataClear();
//                }
//                taskEditInfo.getView().setVisibility(View.GONE);
//            } else {
//                View view = taskEditInfo.getView();
//                if (view.getVisibility() == View.GONE) {
//                    taskEditInfo.getView().setVisibility(View.VISIBLE);
//                }
//            }
//        }
    }

    class zoomImageAsyncTask extends AsyncTask {
        String path, oPath;
        String msg = "图片压缩失败！";

        public zoomImageAsyncTask(String path) {
            this.path = path;
        }

        protected void onPreExecute() {
            if (photo_compression.equals("-1")) {
                CustomProgressDialog.showProgressDialog(TaskitemPhotographyResetActivity.this, "图片处理中...");
            } else {
                CustomProgressDialog.showProgressDialog(TaskitemPhotographyResetActivity.this, "图片压缩中...");
            }
            super.onPreExecute();
        }

        private File getTempFile(String oPath) throws FileNotFoundException {
            File returnvalue = null;
            FileInputStream fis = null;
            FileOutputStream fos = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                if (!isLegal(oPath)) {
                    return null;
                }
                File oldfile = new File(oPath);
                if (!oldfile.exists()) {
                    return null;
                }
                if (!oldfile.isFile()) {
                    return null;
                }
                if (!oldfile.canRead()) {
                    return null;
                }
                File f = new File(oPath + "temp");
                fis = new FileInputStream(oldfile);
                bis = new BufferedInputStream(fis);
                fos = new FileOutputStream(f);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                while (bis.read(b) != -1) {
                    for (int i = 0; i < b.length; i++) {
                        b[i] = (byte) (255 - b[i]);
                    }
                    bos.write(b);
                }
                bos.flush();
                if (isLegal(oPath + "temp")) {
                    returnvalue = f;
                } else {
                    returnvalue = null;
                    f.delete();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new FileNotFoundException();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                throw new OutOfMemoryError();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return returnvalue;
        }

        protected Object doInBackground(Object[] params) {
            try {
                oPath = systemDBHelper.searchForOriginalpath(path);
                Tools.d("opath照片2：" + oPath);
                if (!TextUtils.isEmpty(oPath) && isLegal(oPath) && checkPicture(oPath)) {
                    if (photo_compression.equals("-1")) {
                        if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, task_id)) {
                            if (!originalImgList.contains(oPath)) {
                                originalImgList.add(oPath);
                            } else {
                                msg = "发现重复照片，已自动去重，请重新提交";
                                selectImgList.remove(path);
                                isHadUnlegal = true;
                            }
                        } else {
                            selectImgList.remove(path);
                            isHadUnlegal = true;
                        }
                    } else {//加水印
                        File tempFile = getTempFile(oPath);//生成临时文件
                        if (tempFile == null) {
                            selectImgList.remove(path);
                            isHadUnlegal = true;
                        }
                        if (saveBitmap(imageZoom(Tools.getBitmap(tempFile.getPath(), 1024, 1024),
                                Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath) != null) {
                            if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, task_id)) {
                                if (!originalImgList.contains(oPath)) {
                                    originalImgList.add(oPath);
                                } else {
                                    msg = "发现重复照片，已自动去重，请重新提交";
                                    selectImgList.remove(path);
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(path);
                                isHadUnlegal = true;
                            }
                        } else {
                            selectImgList.remove(path);
                            isHadUnlegal = true;
                        }
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                    }
                } else {
                    selectImgList.remove(path);
                    if (!TextUtils.isEmpty(oPath)) {//判定为异常图片，执行删除操作
                        new File(oPath).delete();
                        new File(path).delete();
                        systemDBHelper.deletePicture(oPath);
                    }
                    msg = "有图片异常，已自动删除异常图片,请重新提交";
                    isHadUnlegal = true;
                }
            } catch (OutOfMemoryError e) {
                msg = "内存不足，请清理内存或重启手机";
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private boolean isHadUnlegal = false;

        protected void onPostExecute(Object o) {
            if (o == null || !(boolean) o || isHadUnlegal) {
                Tools.showToast(TaskitemPhotographyResetActivity.this, msg);
                if (originalImgList != null) {
                    originalImgList.clear();
                }
                CustomProgressDialog.Dissmiss();
            } else {
                String username = AppInfo.getName(TaskitemPhotographyResetActivity.this);
                String uniquelyNum = username + project_id + store_id + task_pack_id + category1 + category2 + category3 + task_id + originalImgList.size();
                boolean isSuccess1 = updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                        store_id, store_name, task_pack_id,
                        task_pack_name, "11", task_id, task_name, category1, category2, category3,
                        uniquelyNum, null, "img", oPath, UpdataDBHelper.Updata_file_type_img, null, photo_compression,
                        false, null, null, false);
                boolean isSuccess2 = appDBHelper.addPhotoUrlRecord(username, project_id, store_id, task_id, oPath, path);
                if (isSuccess1 && isSuccess2) {
                    appDBHelper.setFileNum(oPath, originalImgList.size() + "");
                    CustomProgressDialog.Dissmiss();
                }
                systemDBHelper.updataStateOPathTo3_2(oPath);
            }
            Intent service = new Intent("com.orange.oy.UpdataNewService");
            service.setPackage("com.orange.oy");
            startService(service);
            imageResetAdapter.notifyDataSetChanged();
        }

    }

    private Bitmap imageZoom(Bitmap bitMap, double maxSize) throws OutOfMemoryError {
        boolean isOutOfMemoryError = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            double mid = b.length / 1024;
            if (mid > maxSize) {
                double i = mid / maxSize;
                bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            isOutOfMemoryError = true;
            throw new OutOfMemoryError();
        } finally {
            if (isOutOfMemoryError && bitMap != null && !bitMap.isRecycled()) {
                bitMap.recycle();
            }
        }
        return bitMap;
    }

    private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        if (newBitmap != null) {
            bgimage.recycle();
        }
        return newBitmap;
    }

    private File saveBitmap(Bitmap bm, String tempPath, String oPath) throws
            FileNotFoundException,
            OutOfMemoryError {
        File returnvalue = null;
        FileOutputStream out = null;
        try {
            File f = new File(tempPath);
            out = new FileOutputStream(f);
            ExifInterface exifInterface = new ExifInterface(oPath);
            bm = addWatermark(bm, systemDBHelper.searchForWatermark(oPath));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            ExifInterface exifInterface1 = new ExifInterface(f.getPath());
            exifInterface1.setAttribute(ExifInterface.TAG_ORIENTATION, exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
            exifInterface1.saveAttributes();
            if (isLegal(f.getPath())) {
                if (encryptPicture(tempPath, oPath)) {
                    systemDBHelper.updataIswater(oPath);
                    returnvalue = new File(oPath);
                    f.delete();
                } else {
                    f.delete();
                    returnvalue = null;
                }
            } else {
                f.delete();
                returnvalue = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            throw new OutOfMemoryError();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bm != null) {
                    bm.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private boolean encryptPicture(String oldPath, String newPath) {
        boolean returnvalue = false;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            if (!isLegal(oldPath)) {
                return false;
            }
            File oldfile = new File(oldPath);
            if (!oldfile.exists()) {
                return false;
            }
            if (!oldfile.isFile()) {
                return false;
            }
            if (!oldfile.canRead()) {
                return false;
            }
            File f = new File(newPath);
            fis = new FileInputStream(oldfile);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            while (bis.read(b) != -1) {
                for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (255 - b[i]);
                }
                bos.write(b);
            }
            bos.flush();
            if (isLegal(f.getPath())) {
                returnvalue = true;
            } else {
                if (f.exists())
                    f.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private Bitmap addWatermark(Bitmap bitmap, String msg) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        int width = newBitmap.getWidth();
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAlpha(100);
        paint.setColor(Color.RED);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setTextSize(AppInfo.PaintSize);
        int xNum = width / AppInfo.PaintSize;
        String[] msgs = msg.split("\n");
        int xN = 1;
        for (String str : msgs) {
            if (paint.measureText(str) <= width) {
                canvas.drawText(str, 0, AppInfo.PaintSize * xN++, paint);
            } else {
                int yNum = (int) Math.ceil(str.length() * 1d / xNum);
                int yb = 0;
                for (int i = 1; i <= yNum; i++) {
                    int temp = yb + xNum;
                    if (temp > str.length()) {
                        temp = str.length();
                    }
                    canvas.drawText(str, yb, temp, 0, AppInfo.PaintSize * xN++, paint);
                    yb = temp;
                }
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();//存储
        return newBitmap;
    }

    private boolean checkPicture(String path) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(path);
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            for (int i = 0; i < b.length; i++) {
                b[i] = (byte) (255 - b[i]);
            }
            value = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value != null && (value.equals("FFD8FFE1") || value.equals("89504E47"));
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte aSrc : src) {
            hv = Integer.toHexString(aSrc & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    private void sendData() {
        answers = getAnswers();
        Tools.d(answers);
        if (answers != null && answers.startsWith("error")) {
            String toast = answers.replaceAll("error", "");
            Tools.showToast(TaskitemPhotographyResetActivity.this, toast);
            return;
        }
        list_single.remove("add_photo");
        for (int i = 0; i < list_single.size(); i++) {
            String url = list_single.get(i).replaceAll("\"", "");
            if (url.startsWith("http://")) {
                appDBHelper.addPhotoUrlRecord(username, project_id, store_id, task_id, url, null);
                appDBHelper.setFileNum(url, list_single.size() + "");
                appDBHelper.setPhotoUrl(url, url);
            }
        }
        taskPhotoup_Add.sendPostRequest(Urls.TaskPhotoup_Add, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String executeid = jsonObject.getString("executeid");
                    if (jsonObject.getInt("code") == 200) {
                        Map<String, String> params = new HashMap<>();
                        params.put("task_id", task_id);
                        params.put("user_mobile", username);
                        params.put("task_pack_id", task_pack_id);
                        params.put("storeid", store_id);
                        params.put("outlet_batch", outlet_batch);
                        params.put("p_batch", p_batch);
                        params.put("batch", batch);
                        params.put("executeid", executeid);
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                store_id, store_name, task_pack_id,
                                task_pack_name, "111", task_id, task_name, category1, category2, category3,
                                username + project_id +
                                        store_id + task_pack_id + category1 + category2 + category3 + task_id + Tools.getTimeSS() + "bp",
                                Urls.Filecomplete,
                                null, null, UpdataDBHelper.Updata_file_type_img, params, photo_compression,
                                true, Urls.TaskPhotoup_Add, paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskFinishActivity.isRefresh = true;
                        TaskitemListActivity.isRefresh = true;
                        baseFinish();
                    } else {
                        Tools.showToast(TaskitemPhotographyResetActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemPhotographyResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskitemPhotographyResetActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }


    private String paramsToString() {
        Map<String, String> params = new HashMap<>();
        params.put("task_id", task_id);
        params.put("user_mobile", AppInfo.getName(TaskitemPhotographyResetActivity.this));
        params.put("status", "0");
        params.put("task_pack_id", task_pack_id);
        params.put("storeid", store_id);
        params.put("token", Tools.getToken());
        params.put("category1", category1);
        params.put("category2", category2);
        params.put("category3", category3);
        params.put("batch", batch);
        params.put("outlet_batch", outlet_batch);
        params.put("p_batch", p_batch);
        params.put("is_fill", "0");
        if (taskEditInfo != null) {
            params.put("question_id", taskEditInfo.getId());
        }
        if (answers != null) {
            params.put("answers", answers);
        }
        if (notes != null) {
            params.put("note", notes);
        }
        params.put("txt1", Tools.filterEmoji(taskitempgnexty_editreset.getText().toString().trim()));
        String data = "";
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + params.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + params.get(key).trim();
                }
            }
        }
        return data;
    }
}
