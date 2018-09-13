package com.orange.oy.activity.createtask_321;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.orange.oy.R;
import com.orange.oy.adapter.ImageResetAdapter2;
import com.orange.oy.adapter.ImageResetAdapter2ViewHold;
import com.orange.oy.adapter.mycorps_314.TaskRecordAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.TaskPhotoInfo;
import com.orange.oy.info.shakephoto.QuestionListInfo;
import com.orange.oy.info.shakephoto.TaskListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.MyListView;

import java.io.File;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 添加问卷任务 V3.21
 */
public class TaskRecordActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, TaskRecordAdapter.OnTaskRecordListener, AdapterView.OnItemClickListener, ImageResetAdapter2.OnDeleteClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskrecord_title);
        appTitle.settingName("问卷任务");
        appTitle.showBack(this);
    }

    private String question_num;
    private ArrayList<QuestionListInfo> list;
    private MyListView taskrecord_listview;
    private TaskRecordAdapter taskRecordAdapter;
    private MyGridView taskrecord_gridview;
    private ArrayList<TaskPhotoInfo> list_url;
    private final int MAXPHOTONUM = 9;
    private ImageResetAdapter2 imageResetAdapter;
    private String imagePath, which_page;//0-->添加 1-->编辑
    private TaskListInfo taskListInfo;
    private ArrayList<String> photourls;
    private EditText taskrecord_name, taskrecord_desc;

    private void getData() {//编辑时回显数据
        if (photourls != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String pic : photourls) {
                        if (!(pic.startsWith("http://") || pic.startsWith("https://"))) {
                            TaskPhotoInfo taskPhotoInfo = new TaskPhotoInfo();
                            taskPhotoInfo.setPath(Urls.ImgIp + pic);
                            taskPhotoInfo.setUpUrl(Urls.ImgIp + pic);
                            taskPhotoInfo.setUped(true);
                            taskPhotoInfo.setLocal(true);
                            list_url.add(taskPhotoInfo);
                        } else {
                            TaskPhotoInfo taskPhotoInfo = new TaskPhotoInfo();
                            taskPhotoInfo.setPath(pic);
                            taskPhotoInfo.setUpUrl(pic);
                            taskPhotoInfo.setUped(true);
                            taskPhotoInfo.setLocal(true);
                            list_url.add(taskPhotoInfo);
                        }
                    }
                    addDefaultPhoto();
                    handler.sendEmptyMessage(5);
                }
            }).start();
        } else {
            addDefaultPhoto();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_record);
        list = new ArrayList<>();
        list_url = new ArrayList<>();
        photourls = new ArrayList<>();
        Intent data = getIntent();
        which_page = data.getStringExtra("which_page");
        initTitle();
        taskrecord_listview = (MyListView) findViewById(R.id.taskrecord_listview);
        taskrecord_gridview = (MyGridView) findViewById(R.id.taskrecord_gridview);
        taskrecord_name = (EditText) findViewById(R.id.taskrecord_name);
        taskrecord_desc = (EditText) findViewById(R.id.taskrecord_desc);
        findViewById(R.id.taskrecord_addsubject).setOnClickListener(this);
        findViewById(R.id.taskrecord_submit).setOnClickListener(this);
        if ("1".equals(which_page)) {//编辑
            taskListInfo = (TaskListInfo) data.getBundleExtra("data").getSerializable("taskListInfo");
            findViewById(R.id.taskrecord_named).setVisibility(View.VISIBLE);
            findViewById(R.id.taskrecord_descd).setVisibility(View.VISIBLE);
            findViewById(R.id.taskrecord_named).setOnClickListener(this);
            findViewById(R.id.taskrecord_descd).setOnClickListener(this);
            taskrecord_name.setText(taskListInfo.getTask_name());
            taskrecord_desc.setText(taskListInfo.getNote());
            photourls = taskListInfo.getPhotourl();
            if (taskListInfo.getQuestion_list() != null) {
                list = taskListInfo.getQuestion_list();
            }
            getData();
        } else {
            addDefaultPhoto();
        }
        imageResetAdapter = new ImageResetAdapter2(this, list_url);
        taskrecord_gridview.setAdapter(imageResetAdapter);
        taskrecord_gridview.setOnItemClickListener(this);
        if ("1".equals(which_page)) {
            imageResetAdapter.setRecord(true);
            imageResetAdapter.setEdit(true);
            imageResetAdapter.setOnDeleteClickListener(this);
        }
        taskRecordAdapter = new TaskRecordAdapter(this, list);
        taskrecord_listview.setAdapter(taskRecordAdapter);
        taskRecordAdapter.setOnTaskRecordListener(this);
        checkPermission();
    }

    public void addDefaultPhoto() {
        if (list_url.size() < MAXPHOTONUM) {
            TaskPhotoInfo addphtot = new TaskPhotoInfo();
            addphtot.setPath("add_photo");
            list_url.add(addphtot);
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskrecord_addsubject: {
                question_num = (list.size() + 1) + "";//从最后一题开始添加
                Intent intent = new Intent(this, AddSubjectActivity.class);
                intent.putExtra("question_num", question_num);
                intent.putExtra("which_page", "0");
                startActivityForResult(intent, 0);//添加题目
            }
            break;
            case R.id.taskrecord_named: {
                taskrecord_name.setText("");
            }
            break;
            case R.id.taskrecord_descd: {
                taskrecord_desc.setText("");
            }
            break;
            case R.id.taskrecord_submit: {
                String task_name = taskrecord_name.getText().toString().trim();
                String note = taskrecord_desc.getText().toString().trim();
                if (Tools.isEmpty(task_name)) {
                    Tools.showToast(this, "请填写任务名称");
                    return;
                }
                if (Tools.isEmpty(note)) {
                    note = "";
                }
                if (list_url.size() >= 1) {//示例图片
                    boolean isUpfinish = false;
                    for (TaskPhotoInfo taskPhotoInfo : list_url) {
                        if (!taskPhotoInfo.getPath().startsWith("add") && !taskPhotoInfo.isUped()) {
                            isUpfinish = true;
                            break;
                        }
                    }
                    if (isUpfinish) {
                        Tools.showToast(this, "图片还没有上传完呢~");
                        return;
                    }
                    if (photourls != null) {
                        photourls.clear();
                    } else {
                        photourls = new ArrayList<>();
                    }
                    for (TaskPhotoInfo taskPhotoInfo : list_url) {
                        if (!Tools.isEmpty(taskPhotoInfo.getUpUrl())) {
                            photourls.add(taskPhotoInfo.getUpUrl());
                        }
                    }
                }
                if (list.isEmpty()) {
                    Tools.showToast(this, "请至少添加一道题目");
                    return;
                }
                TaskListInfo taskListInfo = new TaskListInfo();
                taskListInfo.setTask_type("3");
                taskListInfo.setTask_name(task_name);
                taskListInfo.setNote(note);
                taskListInfo.setIs_watermark("");
                taskListInfo.setPhotourl(photourls);
                taskListInfo.setLocal_photo("");
                taskListInfo.setSta_location("");
                taskListInfo.setOnline_store_name("");
                taskListInfo.setOnline_store_url("");
                taskListInfo.setQuestion_list(list);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskListInfo", taskListInfo);
                intent.putExtra("data", bundle);
                intent.putExtra("position", getIntent().getIntExtra("position", 0));
                intent.putExtra("which_page", which_page);
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//添加题目
            switch (requestCode) {
                case 0: {
                    if (data != null) {//添加题目
                        QuestionListInfo questionListInfo = (QuestionListInfo) data.getBundleExtra("data").getSerializable("questionListInfo");
                        list.add(questionListInfo);
                        taskRecordAdapter.notifyDataSetChanged();
                    }
                }
                break;
                case 1: {
                    if (data != null) {//编辑
                        QuestionListInfo questionListInfo = (QuestionListInfo) data.getBundleExtra("data").getSerializable("questionListInfo");
                        int positon = data.getIntExtra("position", 0);
                        list.set(positon, questionListInfo);
                        taskRecordAdapter.notifyDataSetChanged();
                    }
                }
                break;
                case 2: {//拍照
                    if ("1".equals(which_page)) {
                        imageResetAdapter.setEdit(false);
                    }
                    boolean isAgain = false;
                    for (TaskPhotoInfo taskPhotoInfo : list_url) {
                        if (taskPhotoInfo.getPath().equals(imagePath)) {
                            isAgain = true;
                            break;
                        }
                    }
                    if (isAgain) {
                        Tools.showToast(this, "这张图片已经选择过了哦～再去选一张吧～");
                        return;
                    }
                    TaskPhotoInfo taskPhotoInfo = new TaskPhotoInfo();
                    taskPhotoInfo.setPath(imagePath);
                    taskPhotoInfo.setLocal(false);
                    int size = list_url.size();
                    list_url.set(size - 1, taskPhotoInfo);
                    if (canUpdata) {
                        sendOSSData(size - 1);
                    }
                    imageResetAdapter.notifyDataSetChanged();
                    handler.sendEmptyMessageDelayed(4, 500);
                    handler.sendEmptyMessageDelayed(4, 1000);
                    handler.sendEmptyMessageDelayed(4, 2000);
                }
                break;
                case 3: {//从相册选取
                    if ("1".equals(which_page)) {
                        imageResetAdapter.setEdit(false);
                    }
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        cursor.close();
                        boolean isAgain = false;
                        for (TaskPhotoInfo taskPhotoInfo : list_url) {
                            if (taskPhotoInfo.getPath().equals(path)) {
                                isAgain = true;
                                break;
                            }
                        }
                        if (isAgain) {
                            Tools.showToast(this, "这张图片已经选择过了哦～再去选一张吧～");
                            return;
                        }
                        TaskPhotoInfo taskPhotoInfo = new TaskPhotoInfo();
                        taskPhotoInfo.setPath(path);
                        taskPhotoInfo.setLocal(false);
                        int size = list_url.size();
                        list_url.set(size - 1, taskPhotoInfo);
                        if (canUpdata) {
                            sendOSSData(size - 1);
                        }
                        imageResetAdapter.notifyDataSetChanged();
                        handler.sendEmptyMessageDelayed(4, 500);
                        handler.sendEmptyMessageDelayed(4, 1000);
                        handler.sendEmptyMessageDelayed(4, 2000);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void deleteItem(int position) {//删除整条数据
        list.remove(position);
        taskRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void editItem(int position) {//编辑数据
        QuestionListInfo questionListInfo = list.get(position);
        Intent intent = new Intent(this, AddSubjectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("questionListInfo", questionListInfo);
        intent.putExtra("data", bundle);
        intent.putExtra("question_num", question_num);
        intent.putExtra("which_page", "1");//编辑
        intent.putExtra("postion", position);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskPhotoInfo taskPhotoInfo = list_url.get(position);
        if ("add_photo".equals(taskPhotoInfo.getPath())) {
            SelectPhotoDialog.showPhotoSelecter(TaskRecordActivity.this, true, takeListener, pickListener);
        }
    }

    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            if (list_url.size() - 1 < MAXPHOTONUM) {
                imagePath = FileCache.getDirForCamerase2(TaskRecordActivity.this).getPath();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(imagePath + "/" + Tools.getTimeSS() + ".jpg");
                imagePath = file.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, 2);
            } else {
                Tools.showToast(TaskRecordActivity.this, "已到照片数量上限！");
            }
        }
    };
    //从相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();

            if (list_url.size() - 1 < MAXPHOTONUM) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            } else {
                Tools.showToast(TaskRecordActivity.this, "已到照片数量上限！");
            }
        }
    };

    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;
    private boolean canUpdata = true;//是否可以上传

    public void sendOSSData(final int position) {
        if (!canUpdata) {
            return;
        }
        canUpdata = false;
        try {
            TaskPhotoInfo taskPhotoInfo = list_url.get(position);
            File file = new File(taskPhotoInfo.getPath());
            String objectKey = file.getName();
            objectKey = file.hashCode() + "_" + objectKey;
            objectKey = Urls.EndpointDir + "/" + objectKey;
            String photourl = "http://ouye.oss-cn-hangzhou.aliyuncs.com/";
            taskPhotoInfo.setUpUrl(photourl + objectKey);
            if (credentialProvider == null)
                credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                        "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
            if (oss == null)
                oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, taskPhotoInfo.getPath());
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    int rate = (int) (currentSize * 100 / totalSize);
                    Message message = new Message();
                    message.arg1 = position;
                    message.arg2 = rate;
                    message.what = 1;
                    handler.sendMessage(message);
                    Tools.d("currentSize: " + currentSize + " totalSize: " + totalSize);
                }
            });
            task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Tools.d("上传成功flag" + "list.size()：" + list.size());
                    Message message = new Message();
                    message.what = 2;
                    message.arg1 = position;
                    handler.sendMessage(message);
                    canUpdata = true;
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    Tools.d("onFailure");
                    CustomProgressDialog.Dissmiss();
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                        ConfirmDialog.showDialog(TaskRecordActivity.this, "提示", 1, "网络异常，点击图片重新上传", "", "我知道了",
                                null, true, null).goneLeft();
                    }
                    if (serviceException != null) {
                        // 服务异常
                        serviceException.printStackTrace();
                        Tools.d("-------图片上传失败");
                    }
                    task.cancel();
                    canUpdata = true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.Dissmiss();
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {//正在上传
                    final int position = msg.arg1;
                    int index = taskrecord_gridview.getFirstVisiblePosition();
                    View view = taskrecord_gridview.getChildAt(position - index);
                    if (view == null || view.getTag() == null) {
                        return;
                    }
                    final ImageResetAdapter2ViewHold viewHolder = (ImageResetAdapter2ViewHold) view.getTag();
                    int rate = msg.arg2;
                    if (rate == 0) {
                        viewHolder.itemimage_img2.setText(rate + "%" + "\n等待上传");
                        viewHolder.itemimage_img2.setAlpha(0.4f);
                    } else if (rate == 100) {
                        viewHolder.itemimage_img2.setText(rate + "%" + "\n上传成功");
                        viewHolder.itemimage_img2.setAlpha(1f);
                    } else {
                        viewHolder.itemimage_img2.setText(rate + "%" + "\n正在上传");
                        viewHolder.itemimage_img2.setAlpha(0.4f);
                    }
                }
                break;
                case 2: {//完成上传
                    list_url.get(msg.arg1).setUped(true);
                    //继续上传
                    int size = list_url.size();
                    for (int i = 0; i < size; i++) {
                        TaskPhotoInfo taskPhotoInfo1 = list_url.get(i);
                        if (!taskPhotoInfo1.getPath().startsWith("add") && !taskPhotoInfo1.isUped()) {
                            sendOSSData(i);
                            break;
                        }
                    }
                    imageResetAdapter.notifyDataSetChanged();
                }
                break;
                case 3: {//继续上传
                    int size = list_url.size();
                    for (int i = 0; i < size; i++) {
                        TaskPhotoInfo taskPhotoInfo = list_url.get(i);
                        if (!taskPhotoInfo.getPath().startsWith("add") && !taskPhotoInfo.isUped()) {
                            sendOSSData(i);
                            break;
                        }
                    }
                }
                break;
                case 4: {//添加最后的添加图标
                    if (imageResetAdapter != null) {
                        if (!list_url.isEmpty() && !list_url.get(list_url.size() - 1).getPath().startsWith("add")) {
                            if (list_url.size() < MAXPHOTONUM) {//最大值9
                                TaskPhotoInfo addphtot = new TaskPhotoInfo();
                                addphtot.setPath("add_photo");
                                list_url.add(addphtot);
                            }
                        }
                        imageResetAdapter.notifyDataSetChanged();
                    }
                }
                break;
                case 5: {
                    if (imageResetAdapter != null) {
                        imageResetAdapter.notifyDataSetChanged();
                    } else {
                        imageResetAdapter = new ImageResetAdapter2(TaskRecordActivity.this, list_url);
                        taskrecord_gridview.setAdapter(imageResetAdapter);
                    }
                    if (taskRecordAdapter != null) {
                        taskRecordAdapter.notifyDataSetChanged();
                    } else {
                        taskRecordAdapter = new TaskRecordAdapter(TaskRecordActivity.this, list);
                        taskrecord_listview.setAdapter(taskRecordAdapter);
                    }
                }
                break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "摄像头权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(1);
            handler.removeMessages(2);
            handler.removeMessages(3);
            handler.removeMessages(4);
        }
        if (list_url != null) {
            list_url.clear();
        }
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void onDelete(int position) {
        list_url.remove(position);
        imageResetAdapter.notifyDataSetChanged();
    }
}
