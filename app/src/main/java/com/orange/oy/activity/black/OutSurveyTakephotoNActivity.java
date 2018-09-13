package com.orange.oy.activity.black;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.ShotActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DataUploadDialog;
import com.orange.oy.info.BlackoutstoreInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/***
 * 暗访拍照置无效
 */
public class OutSurveyTakephotoNActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle taskitempgnext_title = (AppTitle) findViewById(R.id.photon_title_outsurvey);
        taskitempgnext_title.settingName("无法执行");
        taskitempgnext_title.showBack(this);
    }

    private void initNetworkConnection() {
        blackCloseTakephotoNFinish = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", storeid);
                params.put("taskid", taskid);
                params.put("taskbatch", taskbatch);
                params.put("batch", batch);
                params.put("note", filterEmoji(photon_edit_outsurvey.getText().toString().trim()));
                params.put("usermobile", AppInfo.getName(OutSurveyTakephotoNActivity.this));
                return params;
            }
        };
        blackCloseTakephotoNFinish.setIsShowDialog(true);
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

    private NetworkConnection blackCloseTakephotoNFinish;
    private ArrayList<BlackoutstoreInfo> list;
    private BlackoutstoreInfo blackoutstoreInfo;
    private String storeid, taskid, taskbatch, batch, note, task_name, project_id, project_name, store_name, store_num;
    private ImageView photon_video2_outsurvey, photon_video3_outsurvey;
    private MyImageView photon_video1_outsurvey;
    private EditText photon_edit_outsurvey;
    private TextView photon_name_outsurvey;
    private UpdataDBHelper updataDBHelper;
    private AppDBHelper appDBHelper;
    private String categoryPath;
    private boolean isBackEnable = true;//是否可返回上一页

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_survey_takephoto_n);
        initTitle();
        appDBHelper = new AppDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        list = (ArrayList<BlackoutstoreInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        blackoutstoreInfo = list.remove(0);
        storeid = blackoutstoreInfo.getStroeid();
        taskid = blackoutstoreInfo.getTaskid();
        taskbatch = blackoutstoreInfo.getTaskbatch();
        batch = blackoutstoreInfo.getBatch();
        note = blackoutstoreInfo.getNote();
        store_num = blackoutstoreInfo.getStorenum();
        store_name = blackoutstoreInfo.getStorename();
        project_id = blackoutstoreInfo.getProjectid();
        project_name = blackoutstoreInfo.getProjectname();
        task_name = blackoutstoreInfo.getTaskname();
        categoryPath = Tools.toByte(project_id);
        initNetworkConnection();
        registerReceiver(this);
        photon_video1_outsurvey = (MyImageView) findViewById(R.id.photon_video1_outsurvey);
        photon_video1_outsurvey.setImageResource(R.mipmap.tianjiayuxi);
        photon_video1_outsurvey.setScaleType();
        photon_video2_outsurvey = (ImageView) findViewById(R.id.photon_video2_outsurvey);
        photon_video3_outsurvey = (ImageView) findViewById(R.id.photon_video3_outsurvey);
        photon_edit_outsurvey = (EditText) findViewById(R.id.photon_edit_outsurvey);
        photon_name_outsurvey = (TextView) findViewById(R.id.photon_name_outsurvey);
        photon_name_outsurvey.setText(task_name);
        findViewById(R.id.photon_button_outsurvey).setOnClickListener(this);
        photon_video1_outsurvey.setOnClickListener(this);
        photon_video2_outsurvey.setOnClickListener(this);
        photon_video3_outsurvey.setOnClickListener(this);
    }

    protected void onStop() {
        super.onStop();
        if (blackCloseTakephotoNFinish != null) {
            blackCloseTakephotoNFinish.stop(Urls.BlackCloseTakephotoNFinish);
        }
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(takePhotoBroadcastReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(takePhotoBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataUploadDialog.dissmisDialog();
        unregisterReceiver(this);
    }

    private boolean isComplete = false;
    private boolean isLoading = false;

    private BroadcastReceiver takePhotoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent in) {
            String type2 = in.getStringExtra("type");
            if ("1".equals(type2)) {//可更新UI
                String path = in.getStringExtra("path");
                if (photon_video1_outsurvey.getTag() != null) {
                    if (photon_video1_outsurvey.getTag().toString().equals(path)) {
                        String rate = in.getStringExtra("rate");
                        if ("0".equals(rate)) {
                            photon_video1_outsurvey.setText(rate + "%" + "\n等待上传");
                        } else if ("100".equals(rate)) {
                            photon_video1_outsurvey.setText(rate + "%" + "\n上传完成");
                        } else {
                            photon_video1_outsurvey.setText(rate + "%" + "\n正在上传");
                        }
                    }
                }
            } else if ("2".equals(type2) && !isBackEnable) {//资料回收完成
                isComplete = true;
            }
        }
    };

    private void sendData() {
        if (isComplete) {
            goStep();
            baseFinish();
            return;
        }
        if (!isLoading) {
            blackCloseTakephotoNFinish.sendPostRequest(Urls.BlackCloseTakephotoNFinish, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200 || code == 2) {
                            isLoading = true;
                            String executeid = jsonObject.getString("executeid");
                            String username = AppInfo.getName(OutSurveyTakephotoNActivity.this);
                            Map<String, String> params = new HashMap<>();
                            params.put("storeid", storeid);
                            params.put("batch", batch);
                            params.put("executeid", executeid);
                            params.put("usermobile", username);
                            updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, null, storeid,
                                    store_name, null,
                                    null, "111", taskid, task_name, null, null, null,
                                    username + project_id + storeid + taskid, Urls.BlackCloseTakephotoComplete,
                                    null, null, UpdataDBHelper.Updata_file_type_video, params, null,
                                    true, Urls.BlackCloseTakephotoNFinish, paramsToString(), true);
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
//                            if (code == 2) {
//                                ConfirmDialog.showDialog(OutSurveyTakephotoNActivity.this, null, jsonObject.getString("msg"), null,
//                                        "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
//                                            @Override
//                                            public void leftClick(Object object) {
//
//                                            }
//
//                                            @Override
//                                            public void rightClick(Object object) {
//                                                baseFinish();
//                                            }
//                                        }).goneLeft();
//                            } else if (code == 200) {
                            String fileurl = appDBHelper.getAllPhotoUrl(username, project_id, storeid, taskid);
                            if (fileurl != null && (appDBHelper.getPhotoUrlIsCompete(fileurl, project_id, storeid, taskid))) {
                                goStep();
                                baseFinish();
                            } else {
                                selectUploadMode();
                            }
//                            }
                        } else {
                            Tools.showToast(OutSurveyTakephotoNActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        Tools.showToast(OutSurveyTakephotoNActivity.this, getResources().getString(R.string
                                .network_error));
                    }
                    CustomProgressDialog.Dissmiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(OutSurveyTakephotoNActivity.this, getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else {
            selectUploadMode();
        }
    }

    private void goStep() {
        if (list != null && !list.isEmpty()) {
            String tasktype = list.get(0).getTasktype();
            if (tasktype.equals("3")) {//tasktype为3的时候是记录任务
                Intent intent = new Intent(OutSurveyTakephotoNActivity.this, OutSurveyEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", list);
                intent.putExtra("data", bundle);
                startActivity(intent);
            } else if (tasktype.equals("5")) {//tasktype为5的时候是录音任务
                Intent intent = new Intent(OutSurveyTakephotoNActivity.this, OutSurveyRecordillustrateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", list);
                intent.putExtra("data", bundle);
                startActivity(intent);
            } else if (tasktype.equals("4")) {//tasktype为4的时候是定位任务
                Intent intent = new Intent(OutSurveyTakephotoNActivity.this, OutSurveyMapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", list);
                intent.putExtra("data", bundle);
                startActivity(intent);
            } else if (tasktype.equals("1")) {//tasktype为1的时候是拍照任务
                Intent intent = new Intent(OutSurveyTakephotoNActivity.this, OutSurveyTakephotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", list);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        } else {
            BlackDZXListActivity.isRefresh = true;
        }
        if (OutSurveyTakephotoActivity.activity != null) {
            OutSurveyTakephotoActivity.activity.finish();
        }
    }

    private boolean isClick = false;//是否勾选本店下所有任务

    private void selectUploadMode() {
        if (!isComplete) {
            String mode = appDBHelper.getDataUploadMode(storeid);
            Tools.d("上传模式....." + mode);
            if ("1".equals(mode)) {//弹框选择==1
                DataUploadDialog.showDialog(OutSurveyTakephotoNActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(storeid, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(storeid, "2");
                        goStep();
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(storeid, "3");
                        goStep();
                        baseFinish();
                    }
                });
            } else if ("2".equals(mode)) {//弹框选择===2
                DataUploadDialog.showDialog(OutSurveyTakephotoNActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(storeid, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(storeid, "2");
                        goStep();
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(storeid, "3");
                        goStep();
                        baseFinish();
                    }
                });
            } else if ("3".equals(mode)) {//直接关闭
                appDBHelper.addDataUploadRecord(storeid, "3");
                goStep();
                baseFinish();
            }
        }
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("storeid", storeid);
        parames.put("taskid", taskid);
        parames.put("taskbatch", taskbatch);
        parames.put("note", filterEmoji(photon_edit_outsurvey.getText().toString().trim()));
        parames.put("usermobile", AppInfo.getName(this));
        parames.put("batch", batch);
        String data = "";
        Iterator<String> iterator = parames.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + parames.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + parames.get(key).trim();
                }
            }
        }
        return data;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!isBackEnable) {
                Tools.showToast(OutSurveyTakephotoNActivity.this, "请提交资料，稍后返回");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBack() {
        if (isBackEnable) {
            baseFinish();
        } else {
            Tools.showToast(OutSurveyTakephotoNActivity.this, "请提交资料，稍后返回");
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photon_video1_outsurvey: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(OutSurveyTakephotoNActivity.this) +
                        taskid + 1);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + storeid + taskid +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.photon_video2_outsurvey: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 2);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(OutSurveyTakephotoNActivity.this) +
                        taskid + 2);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + storeid + taskid +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.photon_video3_outsurvey: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 3);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(OutSurveyTakephotoNActivity.this) +
                        taskid + 3);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + project_id + storeid + taskid +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.photon_button_outsurvey: {
                if (TextUtils.isEmpty(photon_edit_outsurvey.getText().toString().trim())) {
                    Tools.showToast(this, "请填写备注");
                    return;
                }
                if (photon_video1_outsurvey.getTag() == null && photon_video2_outsurvey.getTag() == null &&
                        photon_video3_outsurvey.getTag() == null) {
                    Tools.showToast(this, "请拍摄视频");
                    return;
                }
                sendData();
            }
            break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppInfo.TaskitemPhotogpnCodeForShot: {
                if (resultCode == AppInfo.ShotSuccessResultCode) {
                    appDBHelper.deletePhotoUrl(project_id, storeid, taskid);
                    isBackEnable = false;
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    switch (index) {
                        case 1: {
                            photon_video1_outsurvey.setOnClickListener(null);
                            photon_video1_outsurvey.setAlpha(0.4f);
                            photon_video1_outsurvey.setmImageThumbnail(path);
                            photon_video1_outsurvey.setTag(path);
                            upDataVideo(path);
                        }
                        break;
//                        case 2: {
//                            photon_video2_outsurvey.setImageBitmap(Tools.createVideoThumbnail(path));
//                            photon_video2_outsurvey.setTag(path);
//                        }
//                        break;
//                        case 3: {
//                            photon_video3_outsurvey.setImageBitmap(Tools.createVideoThumbnail(path));
//                            photon_video3_outsurvey.setTag(path);
//                        }
//                        break;
                    }
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upDataVideo(String path) {
        String username = AppInfo.getName(OutSurveyTakephotoNActivity.this);
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, "",
                storeid, store_name, "",
                "", "11", taskid, task_name, "", "", "",
                username + project_id + storeid + taskid + "1", null,
                "key", path, UpdataDBHelper.Updata_file_type_video,
                null, null, false, null, paramsToString(), false);
        appDBHelper.addPhotoUrlRecord(username, project_id, storeid, taskid, path, null);
        appDBHelper.setFileNum(path, "1");
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
    }
}
