package com.orange.oy.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.orange.oy.adapter.GridImageAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.TaskCheckView;
import com.orange.oy.view.TaskRadioView;
import com.umeng.analytics.MobclickAgent;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 任务分类-拍照任务-任务说明页-信息录入页
 */
public class OfflineTaskitemPhotographyNextYActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle(String title) {
        AppTitle taskitempgnext_title = (AppTitle) findViewById(R.id.taskitempgnexty_title);
        taskitempgnext_title.settingName("拍照任务");
        taskitempgnext_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
    }

    private String isphoto, photo_type, taskid, tasktype, task_pack_id, store_id, task_name, task_pack_name, store_name,
            project_id, project_name, store_num;
    private String id;
    private LinearLayout taskitempgnexty_questionlayout;
    private ListView taskitempgnexty_bg2;
    private EditText taskitempgnexty_edit;
    private GridImageAdapter adapter;
    private ArrayList<String> selectImgList = new ArrayList<>();
    private ArrayList<String> originalImgList = new ArrayList<>();
    private static int selectIndex;
    private int maxSelect, minSelect;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private String photo_compression;
    private OfflineDBHelper offlineDBHelper;
    private GridView checkreqpgnext_gridview;
    private int gridViewItemHeigth;
    private int is_watermark;
    private String code, brand;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitempgnext_y);
        offlineDBHelper = new OfflineDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        selectIndex = 0;
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        gridViewItemHeigth = (Tools.getScreeInfoWidth(this) - Tools.dipToPx(this, 60)) / 3 + Tools.dipToPx(this, 10);
        String num = data.getStringExtra("num");
        String min_num = data.getStringExtra("min_num");
        String title = data.getStringExtra("task_name");
        initTitle(title);
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        isphoto = data.getStringExtra("isphoto");
        photo_type = data.getStringExtra("photo_type");
        taskid = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_name = data.getStringExtra("store_name");
        task_name = data.getStringExtra("task_name");
        tasktype = data.getStringExtra("tasktype");
        store_id = data.getStringExtra("store_id");
        store_num = data.getStringExtra("store_num");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        batch = data.getStringExtra("batch");
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        is_watermark = data.getIntExtra("is_watermark", 0);
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        photo_compression = data.getStringExtra("photo_compression");
        maxSelect = Tools.StringToInt(num);
        minSelect = Tools.StringToInt(min_num);
        if (maxSelect == -1) {
            maxSelect = 9;
        }
        selectImgList.add("camera_default");
        if ("1".equals(photo_type)) {//单备注
            checkreqpgnext_gridview = (GridView) findViewById(R.id.taskitempgnexty_gridview);
            taskitempgnexty_edit = (EditText) findViewById(R.id.taskitempgnexty_edit);
            adapter = new GridImageAdapter(this, selectImgList);
            checkreqpgnext_gridview.setAdapter(adapter);
            checkreqpgnext_gridview.setOnItemClickListener(this);
        } else if ("2".equals(photo_type)) {//多备注
            findViewById(R.id.taskitempgnexty_bg1).setVisibility(View.GONE);
            taskitempgnexty_bg2 = (ListView) findViewById(R.id.taskitempgnexty_bg2);
            taskitempgnexty_bg2.setVisibility(View.VISIBLE);
            myAdapter = new MyAdapter();
            taskitempgnexty_bg2.setAdapter(myAdapter);
        }
        ((TextView) findViewById(R.id.taskitempgnexty_name)).setText(title);
        taskitempgnexty_questionlayout = (LinearLayout) findViewById(R.id.taskitempgnexty_questionlayout);
        findViewById(R.id.taskitempgnexty_button).setOnClickListener(this);
        initLocation();
        getData();
    }

    private MyAdapter myAdapter;

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return selectImgList.size();
        }

        public Object getItem(int position) {
            return selectImgList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = null;
            if (convertView == null) {
                convertView = Tools.loadLayout(OfflineTaskitemPhotographyNextYActivity.this, R.layout.view_checkreqpgnext_add);
                imageView = (ImageView) convertView.findViewById(R.id.view_checkreqpgnext_img);
                imageView.setOnClickListener(onClickListener);
                convertView.setTag(imageView);
            } else {
                imageView = (ImageView) convertView.getTag();
            }
            imageView.setTag(position);
            String path = selectImgList.get(position);
            if (path.equals("camera_default")) {
                imageView.setImageResource(R.mipmap.camera_default);
            } else {
                imageView.setImageBitmap(Tools.getBitmap(path, 200, 200));
            }
            return convertView;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                selectIndex = Integer.parseInt(v.getTag().toString());
                if ("0".equals(isphoto)) {//不可用相册
                    takeListener.onClick(null);
                } else {
                    SelectPhotoDialog.showPhotoSelecter(OfflineTaskitemPhotographyNextYActivity.this, takeListener, pickListener);
                }
            } catch (NumberFormatException exception) {
                selectIndex = 0;
                Tools.showToast(OfflineTaskitemPhotographyNextYActivity.this, "应用异常");
            }
        }
    };
    private String answers = null, notes = null;
    private boolean isrequired;
    private int min_option, max_option;

    /**
     * 题目校验&赋值
     *
     * @return
     */
    private boolean checkQuestion() {
        if (taskRadioView != null) {//单选
            TaskQuestionInfo taskQuestionInfo = taskRadioView.getSelectAnswers();
            if ("1".equals(isrequired) && taskQuestionInfo == null) {//必填
                Tools.showToast(this, "请完成题目！");
                return false;
            } else {
                if (taskQuestionInfo != null) {
                    notes = " ";
                    if (taskQuestionInfo.getNoteEditext() != null && !TextUtils.isEmpty(taskQuestionInfo
                            .getNoteEditext().getText().toString().trim())) {//判断备注
                        notes = taskQuestionInfo.getNoteEditext().getText().toString().trim();
                    } else if (taskQuestionInfo.isRequired()) {
                        Tools.showToast(this, "被选项备注必填！");
                        return false;
                    }
                    answers = taskQuestionInfo.getId();
                }
                return true;
            }
        }
        if (taskCheckView != null) {//多选
            ArrayList<TaskQuestionInfo> taskQuestionInfos = taskCheckView.getSelectAnswer();
            if ("1".equals(isrequired) && taskQuestionInfos.isEmpty()) {//必填
                Tools.showToast(this, "请完成题目！");
                return false;
            } else {
                notes = "";
                int taskQuestionInfosSize = taskQuestionInfos.size();
                if (taskQuestionInfosSize < min_option || taskQuestionInfosSize > max_option) {
                    if (max_option == min_option) {
                        Tools.showToast(this, "请选择" + min_option + "个选项");
                    } else {
                        Tools.showToast(this, "选择的选项应该大于" + min_option + "，小于" + max_option);
                    }
                    return false;
                } else {
                    /*判断选项备注*/
                    for (int j = 0; j < taskQuestionInfosSize; j++) {
                        TaskQuestionInfo taskQuestionInfo = taskQuestionInfos.get(j);
                        if (taskQuestionInfo.getNoteEditext() != null && !TextUtils.isEmpty
                                (taskQuestionInfo.getNoteEditext().getText().toString().trim())) {//判断备注
                            String temp = taskQuestionInfo.getNoteEditext().getText().toString().trim().replaceAll
                                    ("&&", "");
                            if (TextUtils.isEmpty(temp)) {
                                temp = " ";
                            }
                            if (TextUtils.isEmpty(notes)) {
                                notes = temp;
                            } else {
                                notes = notes + "&&" + temp;
                            }
                        } else if (taskQuestionInfo.isRequired()) {
                            Tools.showToast(this, "选项备注必填！");
                            return false;
                        } else {//如果没有备注也要用分隔符分隔
                            if (notes == null) {
                                notes = " ";
                            } else {
                                notes = notes + "&& ";
                            }
                        }
                        if (answers == null) {
                            answers = taskQuestionInfo.getId();
                        } else {
                            answers = answers + "," + taskQuestionInfo.getId();
                        }
                    }
                }
            }
            return true;
        }
        return true;
    }

    private UpdataDBHelper updataDBHelper;

    private void sendData() throws UnsupportedEncodingException {
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        String username = AppInfo.getName(this);
        if (TextUtils.isEmpty(task_pack_id)) {
            offlineDBHelper.completedTask(username, project_id, store_id, taskid);
        } else {
            offlineDBHelper.insertOfflineCompleted(username, project_id, store_id, task_pack_id, taskid, category1,
                    category2, category3);
        }
        Map<String, String> params = new HashMap<>();
        params.put("task_id", taskid);
        params.put("user_mobile", username);
        params.put("status", "1");
        params.put("task_pack_id", task_pack_id);
        params.put("storeid", store_id);
        params.put("category1", category1);
        params.put("category2", category2);
        params.put("category3", category3);
        params.put("outlet_batch", outlet_batch);
        params.put("p_batch", p_batch);
        params.put("batch", batch);
        String imgs = "";
        int size;
        String key = "";
        size = originalImgList.size();
        for (int i = 0; i < size; i++) {
            String path = originalImgList.get(i);
            if (path.equals("camera_default")) {
                break;
            }
            if (TextUtils.isEmpty(imgs)) {
                imgs = originalImgList.get(i);
            } else {
                imgs = imgs + "," + originalImgList.get(i);
            }
            if (TextUtils.isEmpty(key)) {
                key = "img" + (i + 1);
            } else {
                key = key + ",img" + (i + 1);
            }
        }
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand, store_id, store_name, task_pack_id,
                task_pack_name, "1", taskid, task_name, category1, category2, category3,
                username + project_id +
                        store_id + task_pack_id + category1 + category2 + category3 + taskid, Urls
                        .Filecomplete,
                key, imgs, UpdataDBHelper.Updata_file_type_img, params, photo_compression, true, Urls.Taskphotoup,
                paramsToString(), true);
        systemDBHelper.updataStateOPathTo3(originalImgList.toArray(new String[size]));
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
        OfflinePackageActivity.isRefresh = true;
        OfflineTaskActivity.isRefresh = true;
        baseFinish();

    }

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        parames.put("task_id", taskid);
        parames.put("user_mobile", AppInfo.getName(this));
        parames.put("status", "0");
        parames.put("task_pack_id", task_pack_id);
        parames.put("storeid", store_id);
        parames.put("token", Tools.getToken());
        parames.put("category1", category1);
        parames.put("category2", category2);
        parames.put("category3", category3);
        parames.put("batch", batch);
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        parames.put("is_fill", "0");
        int size;
        if (taskRadioView != null && taskRadioView.getSelectId() != null) {//单选
            parames.put("question_id", id);
            parames.put("answers", answers);
            parames.put("note", notes);
        }
        if (taskCheckView != null && taskCheckView.getSelectId() != null) {//多选
            parames.put("question_id", id);
            parames.put("answers", answers);
            parames.put("note", notes);
        }
        if ("1".equals(photo_type)) {//单备注
            parames.put("txt1", taskitempgnexty_edit.getText().toString().trim());
        } else {//多备注
            size = selectImgList.size();
            for (int i = 0; i < size; i++) {
                if (selectImgList.get(i).equals("camera_default")) {
                    continue;
                }
                parames.put("txt" + (i + 1), ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                        .view_checkreqpgnext_edit)).getText().toString().trim());
            }
        }
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

    private String batch, outlet_batch = null, p_batch = null;

    private void getData() {
        String username = AppInfo.getName(this);
        String result = offlineDBHelper.getTaskConetnt(username, project_id, store_id, task_pack_id, taskid);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            String question_type = jsonObject.getString("question_type");
            id = jsonObject.getString("id");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            isrequired = "1".equals(jsonObject.getString("isrequired"));
            max_option = jsonObject.getInt("max_option");
            min_option = jsonObject.getInt("min_option");
            String prompt = jsonObject.getString("prompt");
            if (TextUtils.isEmpty(prompt) || prompt.equals("null")) {
                prompt = "";
            } else {
                prompt = "(" + prompt + ")";
            }
            if ("1".equals(question_type)) {
                findViewById(R.id.taskitempgnexty_questionhint).setVisibility(View.VISIBLE);
                addQuestionRadioView(jsonObject.getString("question_name") + prompt, jsonObject.getJSONArray
                        ("options"), isrequired);
                taskRadioView.setTag(jsonObject.getString("id"));
                taskitempgnexty_questionlayout.addView(taskRadioView, lp);
            } else if ("2".equals(question_type)) {
                findViewById(R.id.taskitempgnexty_questionhint).setVisibility(View.VISIBLE);
                addQuestionCheckView(jsonObject.getString("question_name") + prompt, jsonObject.getJSONArray
                        ("options"), isrequired);
                taskCheckView.setTag(jsonObject.getString("id"));
                taskitempgnexty_questionlayout.addView(taskCheckView, lp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TaskRadioView taskRadioView;
    private TaskCheckView taskCheckView;
    private String questionInfo = "";

    /**
     * 添加单选
     *
     * @param title
     * @param jsonArray
     * @throws JSONException
     */
    private void addQuestionRadioView(String title, JSONArray jsonArray, boolean isrequired) throws JSONException {
        taskRadioView = new TaskRadioView(this);
        taskRadioView.setTitle(title, isrequired);
        questionInfo += title;
        int length = jsonArray.length();
        JSONObject jsonObject;
        for (int i = 0; i < length; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            String optionName = jsonObject.getString("option_name");
            if ("1".equals(jsonObject.getString("isfill"))) {
                taskRadioView.addRadioButtonForFill(jsonObject.getString("id"), optionName, jsonObject.getString("isforcedfill"), null);
            } else {
                taskRadioView.addRadioButton(jsonObject.getString("id"), optionName, null);
            }
            questionInfo = questionInfo + "_" + optionName;
        }
    }

    /**
     * 添加多选
     *
     * @param title
     * @param jsonArray
     * @throws JSONException
     */
    private void addQuestionCheckView(String title, JSONArray jsonArray, boolean isrequired) throws JSONException {
        taskCheckView = new TaskCheckView(this);
        taskCheckView.setTitle(title, isrequired);
        int length = jsonArray.length();
        JSONObject jsonObject;
        for (int i = 0; i < length; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            if ("1".equals(jsonObject.getString("isfill"))) {
                taskCheckView.addCheckBoxForFill(jsonObject.getString("option_name"), jsonObject.getString
                        ("isforcedfill"), jsonObject.getString("id"), jsonObject.getInt("option_num"), jsonObject
                        .getString("mutex_id"));
            } else {
                TaskEditoptionsInfo taskEditoptionsInfo = new TaskEditoptionsInfo();
                taskEditoptionsInfo.setOption_name(jsonObject.getString("option_name"));
                taskEditoptionsInfo.setId(jsonObject.getString("id"));
                taskEditoptionsInfo.setOption_num(jsonObject.getInt("option_num"));
                taskEditoptionsInfo.setMutex_id(jsonObject.getString("mutex_id"));
                taskCheckView.addCheckBox(taskEditoptionsInfo);
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitempgnexty_button: {
                if (!checkQuestion()) {//校验题目
                    answers = null;
                    notes = null;
                    return;
                }
//                if (taskitempgnexty_bg2 == null && TextUtils.isEmpty(taskitempgnexty_edit.getText().toString())) {
//                    Tools.showToast(TaskitemPhotographyNextYActivity.this, "请填写备注");
//                    return;
//                }
                if (selectImgList == null || selectImgList.isEmpty() || selectImgList.get(0).equals("camera_default")) {
                    Tools.showToast(OfflineTaskitemPhotographyNextYActivity.this, "请拍照");
                    return;
                }
                int size = selectImgList.size();
                if (selectImgList.get(size - 1).contains("default")) {
                    size--;
                }
                if (size < minSelect) {
                    Tools.showToast(OfflineTaskitemPhotographyNextYActivity.this, "拍照数量不足" + minSelect + "张");
                    return;
                }
                new OfflineTaskitemPhotographyNextYActivity.zoomImageAsyncTask().executeOnExecutor(Executors
                        .newCachedThreadPool());
            }
            break;
        }
    }

    public static final int TakeRequest = 0x100;
    public static final int PickRequest = 0x101;
    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(OfflineTaskitemPhotographyNextYActivity.this, Camerase.class);
            intent.putExtra("projectid", project_id);
            intent.putExtra("storeid", store_id);
            intent.putExtra("packageid", task_pack_id);
            intent.putExtra("taskid", taskid);
            intent.putExtra("storecode", store_num);
            intent.putExtra("maxTake", 1);
            intent.putExtra("state", 1);
            startActivityForResult(intent, TakeRequest);
        }
    };

    //相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(OfflineTaskitemPhotographyNextYActivity.this, AlbumActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("maxsize", maxSelect - selectIndex);
            bundle.putString("projectid", project_id);
            bundle.putString("storeid", store_id);
            bundle.putString("packetid", task_pack_id);
            bundle.putString("taskid", taskid);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        }
    };

    private void settingImgPath(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            Tools.showToast(this, "拍照方式错误");
            return;
        }
        startZoomImageAsyncTask(path, "");
    }

    private SystemDBHelper systemDBHelper;

    public void startZoomImageAsyncTask(String path, String name) {
        if (selectIndex < selectImgList.size()) {
            String temp = selectImgList.remove(selectIndex);
            if (!temp.contains("default")) {
                systemDBHelper.updataStateTo2(new String[]{temp});
            }
            if (selectIndex == selectImgList.size()) {
                selectImgList.add(path);
            } else {
                selectImgList.add(selectIndex, path);
            }
        } else {
            selectImgList.add(path);
        }
        refreshUI();
    }

    public void startZoomImageAsyncTask(String name, ArrayList<String> list) {
        int size = list.size();
        if (size <= maxSelect - selectIndex) {
            int oldSize = selectImgList.size();
            for (int i = selectIndex, j = 0; j < size; j++, i++) {
                if (i < oldSize) {
                    systemDBHelper.updataStateTo2(new String[]{selectImgList.get(i)});
                    selectImgList.set(i, list.get(j));
                } else {
                    selectImgList.add(list.get(j));
                }
            }
            systemDBHelper.updataStateTo1(list.toArray(new String[size]));
            refreshUI();
        } else {
            Tools.showToast(this, "选择异常，请重新选择图片！");
        }
    }

    class zoomImageAsyncTask extends AsyncTask {
        String msg = "图片压缩失败！";

        protected void onPreExecute() {
            if (photo_compression.equals("-1")) {
                CustomProgressDialog.showProgressDialog(OfflineTaskitemPhotographyNextYActivity.this, "图片处理中...");
            } else {
                CustomProgressDialog.showProgressDialog(OfflineTaskitemPhotographyNextYActivity.this, "图片压缩中...");
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
                int size = selectImgList.size();
                for (int i = 0; i < size; i++) {
                    String tPath = selectImgList.get(i);
                    if ("camera_default".equals(tPath)) {
                        continue;
                    }
                    String oPath = systemDBHelper.searchForOriginalpath(tPath);
                    if (!TextUtils.isEmpty(oPath) && isLegal(oPath) && checkPicture(oPath)) {
                        if (photo_compression.equals("-1")) {
                            if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
                                if (!originalImgList.contains(oPath)) {
                                    originalImgList.add(oPath);
                                } else {
                                    msg = "发现重复照片，已自动去重，请重新提交";
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                            }
                        } else {//加水印
                            File tempFile = getTempFile(oPath);//生成临时文件
                            if (tempFile == null) {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                                continue;
                            }
                            if (saveBitmap(imageZoom(Tools.getBitmap(tempFile.getPath(), 1024, 1024),
                                    Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath) != null) {
                                Tools.d(oPath);
                                if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
                                    if (!originalImgList.contains(oPath)) {
                                        originalImgList.add(oPath);
                                    } else {
                                        msg = "发现重复照片，已自动去重，请重新提交";
                                        selectImgList.remove(i);
                                        i--;
                                        size--;
                                        isHadUnlegal = true;
                                    }
                                } else {
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                            }
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    } else {
                        selectImgList.remove(i);
                        i--;
                        size--;
                        if (!TextUtils.isEmpty(oPath)) {//判定为异常图片，执行删除操作
                            new File(oPath).delete();
                            new File(tPath).delete();
                            systemDBHelper.deletePicture(oPath);
                        }
                        msg = "有图片异常，已自动删除异常图片,请重新提交";
                        isHadUnlegal = true;
                    }
                }
            } catch (OutOfMemoryError e) {
                msg = "内存不足，请清理内存或重启手机";
                return false;
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        private boolean isHadUnlegal = false;

        protected void onPostExecute(Object o) {
            if (o == null || !(boolean) o || isHadUnlegal) {
                Tools.showToast(OfflineTaskitemPhotographyNextYActivity.this, msg);
                refreshUI();
                CustomProgressDialog.Dissmiss();
            } else {
                CustomProgressDialog.Dissmiss();
                try {
                    if (outlet_batch != null && p_batch != null) {
                        sendData();
                    } else {
                        Tools.showToast(OfflineTaskitemPhotographyNextYActivity.this,
                                getResources().getString(R.string.batch_error));
                    }
                } catch (UnsupportedEncodingException e) {
                    Tools.showToast(OfflineTaskitemPhotographyNextYActivity.this, "存储失败，未知异常！");
                    MobclickAgent.reportError(OfflineTaskitemPhotographyNextYActivity.this, "offline task y:" + e.getMessage());
                }
            }
        }
    }

    private void refreshUI() {
        if (taskitempgnexty_bg2 != null) {
            int size = selectImgList.size();
            if (size < maxSelect && (size == 0 || !selectImgList.get(size - 1).equals("camera_default"))) {
                selectImgList.add("camera_default");
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitempgnexty_bg2.getLayoutParams();
            lp.height = (int) (selectImgList.size() * (getResources().getDimension(R.dimen.taskitemphotoY_item_height) +
                    Tools.dipToPx(OfflineTaskitemPhotographyNextYActivity.this, 10)));
            taskitempgnexty_bg2.setLayoutParams(lp);
            myAdapter.notifyDataSetChanged();
        } else {
            int size = selectImgList.size();
            if (size > 0) {
                if (size < maxSelect && !selectImgList.get(size - 1).equals("camera_default")) {
                    selectImgList.add("camera_default");
                }
            } else {
                selectImgList.add("camera_default");
            }
            int t = (int) Math.ceil(selectImgList.size() / 3d);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) checkreqpgnext_gridview.getLayoutParams();
            lp.height = t * gridViewItemHeigth;
            checkreqpgnext_gridview.setLayoutParams(lp);
            adapter.notifyDataSetChanged();
        }
    }

    //检测加密图片是否正常
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

    private boolean isLegal(String path) {
        File file = new File(path);
        return file.length() > 51200;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakeRequest: {
//                    settingImgPath(FileCache.getDirForPhoto(OfflineTaskitemPhotographyNextYActivity.this, AppInfo.getName
//                            (OfflineTaskitemPhotographyNextYActivity.this) + "/" + taskid + tasktype + photo_type +
//                            categoryPath).getPath() + "/" + selectIndex + ".jpg");
                    settingImgPath(data.getStringExtra("path"));
                }
                break;
                case PickRequest: {
                    Uri selectedImage = data.getData();
                    String filePath = null;
                    try {
                        if (selectedImage.toString().startsWith("content")) {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                filePath = cursor.getString(columnIndex);
                            }
                            cursor.close();
                        } else {
                            filePath = selectedImage.getPath();
                        }
                    } catch (Exception e) {
                    }
                    if (filePath == null) {
                        Tools.showToast(this, "图片读取失败");
                        return;
                    }
                    settingImgPath(filePath);
                }
                break;
                case 0: {
                    Bundle bundle = data.getExtras();
                    ArrayList<String> tDataList = (ArrayList<String>) bundle.getSerializable("dataList");
                    startZoomImageAsyncTask(Tools.getTimeSS() + Tools.getDeviceId(OfflineTaskitemPhotographyNextYActivity.this)
                            + taskid, tDataList);
                }
                break;
            }
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
        bgimage.recycle();
        return newBitmap;
    }

    //加密图片
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

    private File saveBitmap(Bitmap bm, String tempPath, String oPath) throws FileNotFoundException,
            OutOfMemoryError {
        File returnvalue = null;
        FileOutputStream out = null;
        try {
            File f = new File(tempPath);
            out = new FileOutputStream(f);
            ExifInterface exif = null;
            int pointIndex = oPath.lastIndexOf(".");
            exif = new ExifInterface(oPath.substring(0, pointIndex) + "_2.ouye");
            if (is_watermark == 1 && !systemDBHelper.isBindForPicture(oPath)) {
                if (locationStr == null) {
                    locationStr = "";
                } else {
                    locationStr = "\n" + locationStr;
                }
                bm = addWatermark(bm, systemDBHelper.searchForWatermark(oPath));
            }
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            if (exif != null) {
                ExifInterface exif2 = new ExifInterface(f.getPath());
                exif2.setAttribute(ExifInterface.TAG_ORIENTATION, exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                exif2.saveAttributes();
            }
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

    private Bitmap addWatermark(Bitmap bitmap, String msg) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        int width = newBitmap.getWidth();
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAlpha(90);
        paint.setColor(Color.WHITE);
        paint.setDither(true);
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

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    public static double location_latitude, location_longitude;
    public String locationStr = "";

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        }
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        setLocationOption();
        mLocationClient.start();
    }

    // 设置相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng point = new LatLng(location_latitude, location_longitude);
            if (mSearch != null)
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }

    private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            ReverseGeoCodeResult.AddressComponent addressComponent = reverseGeoCodeResult.getAddressDetail();
            int index = addressComponent.streetNumber.lastIndexOf("号");
            if (index > 0) {
                try {
                    String str = String.valueOf(addressComponent.streetNumber.charAt(index - 1));
                    Tools.d(str);
                    if (Tools.StringToInt(str) != -1) {
                        addressComponent.streetNumber = addressComponent.streetNumber.substring(0, index + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (addressComponent.province.endsWith("市")) {
                locationStr = addressComponent.city + addressComponent.district +
                        addressComponent.street + addressComponent.streetNumber;
            } else {
                locationStr = addressComponent.province + addressComponent.city +
                        addressComponent.district + addressComponent.street + addressComponent.streetNumber;
            }
        }
    };

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int size = selectImgList.size();
        if (position < size) {
            selectIndex = position;
            if ("0".equals(isphoto)) {//不可用相册
                takeListener.onClick(null);
            } else {
                SelectPhotoDialog.showPhotoSelecter(OfflineTaskitemPhotographyNextYActivity.this, takeListener, pickListener);
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        if (mSearch != null) {
            mSearch.destroy();
            mSearch = null;
        }
    }
}
