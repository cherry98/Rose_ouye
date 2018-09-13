package com.orange.oy.activity.createtask_317;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.orange.oy.activity.shakephoto_316.LargeImagePageActivity;
import com.orange.oy.adapter.ImageResetAdapter2;
import com.orange.oy.adapter.ImageResetAdapter2ViewHold;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.TaskPhotoInfo;
import com.orange.oy.info.shakephoto.TaskListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyGridView;

import java.io.File;
import java.util.ArrayList;

/**
 * 拍照任务 V3.17
 */
public class TaskPhotoActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener
        , CompoundButton.OnCheckedChangeListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskphoto_title);
        if ("0".equals(which_page)) {
            appTitle.settingName("编辑拍照");
        } else {
            appTitle.settingName("拍照任务");
        }
        appTitle.showBack(this);
    }

    private String which_page;//0编辑 1添加
    private EditText taskphoto_name, taskphoto_desc;
    private ImageView taskphoto_delete;
    private TextView taskphoto_finish;
    private MyGridView taskphoto_gridview;
    private ImageResetAdapter2 imageResetAdapter;
    private ArrayList<TaskPhotoInfo> list;
    private String imagePath;
    private TaskListInfo taskListInfo;
    private CheckBox taskphoto_check1, taskphoto_check2;
    private int position;
    private String task_id, task_type, task_name, note, is_watermark, local_photo;
    private ArrayList<String> photourls;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_photo);
        Intent data = getIntent();
        list = new ArrayList<>();
//        list_delete = new ArrayList<>();
//        temp = new ArrayList<>();
        which_page = data.getStringExtra("which_page");
        initTitle();
        initView();
        position = data.getIntExtra("position", 0);
        findViewById(R.id.taskphoto_button).setOnClickListener(this);
        taskphoto_delete.setOnClickListener(this);
        taskphoto_finish.setOnClickListener(this);
        if ("0".equals(which_page)) {//编辑时执行
            taskListInfo = (TaskListInfo) data.getBundleExtra("data").getSerializable("taskListInfo");
            task_id = taskListInfo.getTask_id();
            task_type = taskListInfo.getTask_type();
            task_name = taskListInfo.getTask_name();
            note = taskListInfo.getNote();
            is_watermark = taskListInfo.getIs_watermark();
            local_photo = taskListInfo.getLocal_photo();
            taskphoto_name.setText(taskListInfo.getTask_name());
            taskphoto_desc.setText(taskListInfo.getNote());
            if ("1".equals(is_watermark)) {
                taskphoto_check1.setChecked(true);
            } else {
                taskphoto_check1.setChecked(false);
            }
            if ("1".equals(local_photo)) {
                taskphoto_check2.setChecked(true);
            } else {
                taskphoto_check2.setChecked(false);
            }
            getData();
        } else {
            if (taskphoto_check1.isChecked()) {
                is_watermark = "1";
            } else {
                is_watermark = "0";
            }
            if (taskphoto_check2.isChecked()) {
                local_photo = "1";
            } else {
                local_photo = "0";
            }
        }
        if (list.size() < MAXPHOTONUM) {
            TaskPhotoInfo addphtot = new TaskPhotoInfo();
            addphtot.setPath("add_photo");
            list.add(addphtot);
        }
        imageResetAdapter = new ImageResetAdapter2(this, list);
        taskphoto_gridview.setAdapter(imageResetAdapter);
        taskphoto_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskPhotoInfo taskPhotoInfo = list.get(position);
                if ("add_photo".equals(taskPhotoInfo.getPath())) {
                    SelectPhotoDialog.showPhotoSelecter(TaskPhotoActivity.this, true, takeListener, pickListener);
                } else {
                    if (imageResetAdapter.isEdit()) {
                        taskPhotoInfo.setSelect(!taskPhotoInfo.isSelect());
                        if (taskPhotoInfo.isSelect()) {
                            selDeleNum++;
                        } else {
                            selDeleNum--;
                        }
                        imageResetAdapter.notifyDataSetChanged();
                    } else {
                        if (taskPhotoInfo.isUped() || taskPhotoInfo.isLocal()) {
//                            if (imageLoader == null) {
//                                imageLoader = new ImageLoader(TaskPhotoActivity.this);
//                            }
//                            PhotoView imageView = new PhotoView(TaskPhotoActivity.this);
//                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                            imageLoader.DisplayImage(taskPhotoInfo.getPath(), imageView);
//                            SelecterDialog.showView(TaskPhotoActivity.this, imageView);

                            if (list != null && !list.isEmpty()) {
                                ArrayList<LargeImagePageInfo> largeImagePageInfos = new ArrayList<>();
                                for (TaskPhotoInfo taskPhotoInfo1 : list) {
                                    if (taskPhotoInfo1.getPath().startsWith("add")) {
                                        continue;
                                    }
                                    LargeImagePageInfo largeImagePageInfo = new LargeImagePageInfo();
                                    largeImagePageInfo.setFile_url(taskPhotoInfo1.getPath());
                                    largeImagePageInfos.add(largeImagePageInfo);
                                }
                                Intent intent = new Intent(TaskPhotoActivity.this, LargeImagePageActivity.class);
                                intent.putExtra("isList", true);
                                intent.putExtra("list", largeImagePageInfos);
                                intent.putExtra("position", position);
                                intent.putExtra("state", 1);
                                startActivity(intent);
                            }
                        } else if (canUpdata) {
                            handler.sendEmptyMessage(3);
                        }
                    }
                }
            }
        });
        taskphoto_check1.setOnCheckedChangeListener(this);
        taskphoto_check2.setOnCheckedChangeListener(this);
        checkPermission();
    }

    public void onBackPressed() {
        if (!canUpdata) {
            ConfirmDialog.showDialog(this, "提示", 1, "任务设置还没有保存确认返回吗？", "取消", "确认", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                }

                public void rightClick(Object object) {
                    baseFinish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    private int selDeleNum = 0;//已选择的删除数量

    private void getData() {
        photourls = taskListInfo.getPhotourl();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (photourls != null) {
                    for (String pic : photourls) {
                        if (!(pic.startsWith("http://") || pic.startsWith("https://"))) {
                            TaskPhotoInfo taskPhotoInfo = new TaskPhotoInfo();
                            taskPhotoInfo.setPath(Urls.ImgIp + pic);
                            taskPhotoInfo.setUpUrl(Urls.ImgIp + pic);
                            taskPhotoInfo.setUped(true);
                            taskPhotoInfo.setLocal(true);
                            list.add(taskPhotoInfo);
                        } else {
                            TaskPhotoInfo taskPhotoInfo = new TaskPhotoInfo();
                            taskPhotoInfo.setPath(pic);
                            taskPhotoInfo.setUpUrl(pic);
                            taskPhotoInfo.setUped(true);
                            taskPhotoInfo.setLocal(true);
                            list.add(taskPhotoInfo);
                        }
                    }
                    handler.sendEmptyMessage(5);
                }
            }
        }).start();
    }

    private void initView() {
        taskphoto_name = (EditText) findViewById(R.id.taskphoto_name);
        taskphoto_desc = (EditText) findViewById(R.id.taskphoto_desc);
        taskphoto_check1 = (CheckBox) findViewById(R.id.taskphoto_check1);
        taskphoto_check2 = (CheckBox) findViewById(R.id.taskphoto_check2);
        taskphoto_delete = (ImageView) findViewById(R.id.taskphoto_delete);
        taskphoto_finish = (TextView) findViewById(R.id.taskphoto_finish);
        taskphoto_gridview = (MyGridView) findViewById(R.id.taskphoto_gridview);
    }

    public void onBack() {
        if (!canUpdata) {
            ConfirmDialog.showDialog(this, "提示", 1, "任务设置还没有保存确认返回吗？", "取消", "确认", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                }

                public void rightClick(Object object) {
                    baseFinish();
                }
            });
        } else {
            baseFinish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskphoto_delete: {//删除按钮
                taskphoto_finish.setVisibility(View.VISIBLE);
                taskphoto_delete.setVisibility(View.GONE);
                imageResetAdapter.setEdit(true);
                imageResetAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.taskphoto_finish: {//删除完成按钮
                taskphoto_finish.setVisibility(View.GONE);
                taskphoto_delete.setVisibility(View.VISIBLE);
                if (selDeleNum == 0) {
                    imageResetAdapter.setEdit(false);
                    imageResetAdapter.notifyDataSetChanged();
                    return;
                }
                selDeleNum = 0;
                if (task != null)
                    task.cancel();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    TaskPhotoInfo taskPhotoInfo = list.get(i);
                    if (taskPhotoInfo.isSelect()) {
                        list.remove(i);
                        i--;
                        size--;
                    }
                }
                imageResetAdapter.setEdit(false);
                imageResetAdapter.notifyDataSetChanged();
                canUpdata = true;
                handler.sendEmptyMessage(3);
                if (!list.isEmpty() && !list.get(list.size() - 1).getPath().startsWith("add")) {
                    if (list.size() < MAXPHOTONUM) {//最大值9
                        TaskPhotoInfo addphtot = new TaskPhotoInfo();
                        addphtot.setPath("add_photo");
                        list.add(addphtot);
                    }
                }
                imageResetAdapter.notifyDataSetChanged();
                handler.sendEmptyMessageDelayed(4, 100);
            }
            break;
            case R.id.taskphoto_button: {//页面提交按钮
                task_name = taskphoto_name.getText().toString().trim();
                if (Tools.isEmpty(task_name)) {
                    Tools.showToast(this, "请输入任务名称");
                    return;
                }
                note = Tools.filterEmoji(taskphoto_desc.getText().toString().trim());
                if (Tools.isEmpty(is_watermark)) {
                    Tools.showToast(this, "请选择是否获取任务执行位置");
                    return;
                }
                if (Tools.isEmpty(local_photo)) {
                    Tools.showToast(this, "请选择是否允许执行者调取本地相册");
                    return;
                }
                if (list.size() >= 1) {
                    boolean isUpfinish = false;
                    for (TaskPhotoInfo taskPhotoInfo : list) {
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
                    for (TaskPhotoInfo taskPhotoInfo : list) {
                        if (!TextUtils.isEmpty(taskPhotoInfo.getUpUrl())) {
                            photourls.add(taskPhotoInfo.getUpUrl());
                        }
                    }
                }
                TaskListInfo taskListInfo = new TaskListInfo();
                taskListInfo.setTask_id(task_id);
                if (TextUtils.isEmpty(task_type)) {
                    task_type = "1";
                }
                taskListInfo.setTask_type(task_type);
                taskListInfo.setTask_name(task_name);
                taskListInfo.setNote(note);
                taskListInfo.setIs_watermark(is_watermark);
                taskListInfo.setLocal_photo(local_photo);
                taskListInfo.setPhotourl(photourls);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskListInfo", taskListInfo);
                intent.putExtra("data", bundle);
                intent.putExtra("position", position);
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();
            }
            break;
        }
    }

    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            if (list.size() - 1 < 9) {
                imagePath = FileCache.getDirForCamerase2(TaskPhotoActivity.this).getPath();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(imagePath + "/" + Tools.getTimeSS() + ".jpg");
                imagePath = file.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, 1);
            } else {
                Tools.showToast(TaskPhotoActivity.this, "已到照片数量上限！");
            }
        }
    };
    //从相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            if (list.size() - 1 < 9) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            } else {
                Tools.showToast(TaskPhotoActivity.this, "已到照片数量上限！");
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(1);
            handler.removeMessages(2);
            handler.removeMessages(3);
            handler.removeMessages(4);
        }
        if (list != null) {
            list.clear();
        }
        if (task != null) {
            task.cancel();
        }
    }

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
            TaskPhotoInfo taskPhotoInfo = list.get(position);
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
                        ConfirmDialog.showDialog(TaskPhotoActivity.this, "提示", 1, "网络异常，点击图片重新上传", "", "我知道了",
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
                    int index = taskphoto_gridview.getFirstVisiblePosition();
                    View view = taskphoto_gridview.getChildAt(msg.arg1 - index);
                    if (view == null || view.getTag() == null) {
                        return;
                    }
                    ImageResetAdapter2ViewHold viewHolder = (ImageResetAdapter2ViewHold) view.getTag();
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
                    list.get(msg.arg1).setUped(true);
                    //继续上传
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        TaskPhotoInfo taskPhotoInfo1 = list.get(i);
                        if (!taskPhotoInfo1.getPath().startsWith("add") && !taskPhotoInfo1.isUped()) {
                            sendOSSData(i);
                            break;
                        }
                    }
                    imageResetAdapter.notifyDataSetChanged();
                }
                break;
                case 3: {//继续上传
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        TaskPhotoInfo taskPhotoInfo = list.get(i);
                        if (!taskPhotoInfo.getPath().startsWith("add") && !taskPhotoInfo.isUped()) {
                            sendOSSData(i);
                            break;
                        }
                    }
                }
                break;
                case 4: {//添加最后的添加图标
                    if (imageResetAdapter != null) {
                        if (!list.isEmpty() && !list.get(list.size() - 1).getPath().startsWith("add")) {
                            if (list.size() < MAXPHOTONUM) {//最大值9
                                TaskPhotoInfo addphtot = new TaskPhotoInfo();
                                addphtot.setPath("add_photo");
                                list.add(addphtot);
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
                        imageResetAdapter = new ImageResetAdapter2(TaskPhotoActivity.this, list);
                        taskphoto_gridview.setAdapter(imageResetAdapter);
                    }
                }
                break;
            }
        }
    };

    private final int MAXPHOTONUM = 9;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (R.id.taskphoto_check1 == buttonView.getId()) {
            if (isChecked) {
                is_watermark = "1";
            } else {
                is_watermark = "0";
            }
        } else if (R.id.taskphoto_check2 == buttonView.getId()) {
            if (isChecked) {
                local_photo = "1";
            } else {
                local_photo = "0";
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    boolean isAgain = false;
                    for (TaskPhotoInfo taskPhotoInfo : list) {
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
                    int size = list.size();
                    list.set(size - 1, taskPhotoInfo);
                    if (canUpdata) {
                        sendOSSData(size - 1);
                    }
                    imageResetAdapter.notifyDataSetChanged();
                    handler.sendEmptyMessageDelayed(4, 500);
                    handler.sendEmptyMessageDelayed(4, 1000);
                    handler.sendEmptyMessageDelayed(4, 2000);
                }
                break;
                case 2: {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        cursor.close();
                        boolean isAgain = false;
                        for (TaskPhotoInfo taskPhotoInfo : list) {
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
                        int size = list.size();
                        list.set(size - 1, taskPhotoInfo);
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
}
