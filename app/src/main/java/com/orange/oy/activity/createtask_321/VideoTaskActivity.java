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
import com.orange.oy.activity.OfflineTaskitemShotActivity;
import com.orange.oy.activity.ShotActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.VideoViewActivity;
import com.orange.oy.activity.shakephoto_316.LargeImagePageActivity;
import com.orange.oy.adapter.ImageResetAdapter2;
import com.orange.oy.adapter.ImageResetAdapter2ViewHold;
import com.orange.oy.adapter.VideoAdapter;
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

import static com.orange.oy.R.id.taskitemshot_shotimg;
import static com.orange.oy.R.id.taskitemshot_video1;
import static com.orange.oy.R.id.taskphoto_check2;
import static com.orange.oy.R.id.themeclassify_listview;

/**
 * 示例视频任务 V3.21
 */
public class VideoTaskActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener
        , CompoundButton.OnCheckedChangeListener, VideoAdapter.DeleteInf {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskphoto_title);
        if ("0".equals(which_page)) {
            appTitle.settingName("编辑视频");
        } else {
            appTitle.settingName("视频任务");
        }
        appTitle.showBack(this);
    }

    private String which_page;//0编辑 1添加
    private EditText taskphoto_name, taskphoto_desc;
    private ImageView taskphoto_delete;
    private TextView taskphoto_finish;
    private MyGridView taskphoto_gridview;
    private VideoAdapter videoAdapter;
    private ArrayList<TaskPhotoInfo> list;
    private TaskListInfo taskListInfo;
    private CheckBox taskphoto_check1;
    private int position;
    private String task_id, task_type, task_name, note, sta_location, local_photo;
    private ArrayList<String> photourls;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_video);
        Intent data = getIntent();
        list = new ArrayList<>();
        which_page = data.getStringExtra("which_page");
        initTitle();
        initView();
        position = data.getIntExtra("position", 0);
        findViewById(R.id.taskphoto_button).setOnClickListener(this);
        taskphoto_delete.setOnClickListener(this);
        taskphoto_finish.setOnClickListener(this);
        if ("0".equals(which_page)) {//编辑时执行
            taskListInfo = (TaskListInfo) data.getBundleExtra("data").getSerializable("taskListInfo");
            if (taskListInfo != null) {
                photourls = taskListInfo.getVideourl();
            }
            getData();
            if (photourls != null) {
                for (String pic : photourls) {
                    if (!(pic.startsWith("http://") || pic.startsWith("https://"))) {
                        TaskPhotoInfo taskPhotoInfo = new TaskPhotoInfo();
                        taskPhotoInfo.setPath(Urls.Endpoint3 + pic);
                        taskPhotoInfo.setUpUrl(Urls.Endpoint3 + pic);
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
            }
        } else {
            if (taskphoto_check1.isChecked()) {
                sta_location = "1";
            } else {
                sta_location = "0";
            }

        }
        if (list.size() < MAXPHOTONUM) {
            TaskPhotoInfo addphtot = new TaskPhotoInfo();
            addphtot.setPath("add_photo");
            list.add(addphtot);
        }
        videoAdapter = new VideoAdapter(this, list);
        videoAdapter.setDeleteInf(this);
        taskphoto_gridview.setAdapter(videoAdapter);

        taskphoto_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskPhotoInfo taskPhotoInfo = list.get(position);
                if ("add_photo".equals(taskPhotoInfo.getPath())) {

                    if (list.size() - 1 < 9) {
                        Intent intent = new Intent(VideoTaskActivity.this, ShotActivity.class);
                        intent.putExtra("index", 1);
                        intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(VideoTaskActivity.this));
                        intent.putExtra("dirName", AppInfo.getName(VideoTaskActivity.this));
                        startActivityForResult(intent, AppInfo.TaskitemShotRequestCodeForShot);
                    } else {
                        Tools.showToast(VideoTaskActivity.this, "已到视频数量上限！");
                    }


                } else {
                    if (videoAdapter.isEdit()) {
                        taskPhotoInfo.setSelect(!taskPhotoInfo.isSelect());
                        videoAdapter.notifyDataSetChanged();
                    } else {
                        if (taskPhotoInfo.isUped() || taskPhotoInfo.isLocal()) {

                            if (list != null && !list.isEmpty()) {
                                //点击看视频
                                Intent intent = new Intent(VideoTaskActivity.this, VideoViewActivity.class);
                                intent.putExtra("path", taskPhotoInfo.getUpUrl());
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

    private void getData() {
        task_id = taskListInfo.getTask_id();
        task_type = taskListInfo.getTask_type();
        task_name = taskListInfo.getTask_name();
        note = taskListInfo.getNote();
        sta_location = taskListInfo.getSta_location();
        local_photo = taskListInfo.getLocal_photo();
        taskphoto_name.setText(taskListInfo.getTask_name());
        taskphoto_desc.setText(taskListInfo.getNote());
        if ("1".equals(sta_location)) {
            taskphoto_check1.setChecked(true);
        } else {
            taskphoto_check1.setChecked(false);
        }

    }

    private void initView() {
        taskphoto_name = (EditText) findViewById(R.id.taskphoto_name);
        taskphoto_desc = (EditText) findViewById(R.id.taskphoto_desc);
        taskphoto_check1 = (CheckBox) findViewById(R.id.taskphoto_check1);
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
            case R.id.taskphoto_button: {//页面提交按钮
                task_name = taskphoto_name.getText().toString().trim();
                if (Tools.isEmpty(task_name)) {
                    Tools.showToast(this, "请输入任务名称");
                    return;
                }
                note = Tools.filterEmoji(taskphoto_desc.getText().toString().trim());
            /*    if (Tools.isEmpty(sta_location)) {
                    Tools.showToast(this, "请选择是否获取任务执行位置");
                    return;
                }
*/
                if (list.size() >= 1) {
                    boolean isUpfinish = false;
                    for (TaskPhotoInfo taskPhotoInfo : list) {
                        if (!taskPhotoInfo.getPath().startsWith("add") && !taskPhotoInfo.isUped()) {
                            isUpfinish = true;
                            break;
                        }
                    }
                    if (isUpfinish) {
                        Tools.showToast(this, "视频还没有上传完呢~");
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
                    task_type = "2";
                }
                taskListInfo.setTask_type(task_type);
                taskListInfo.setTask_name(task_name);
                taskListInfo.setNote(note);
                taskListInfo.setSta_location(sta_location);
                taskListInfo.setLocal_photo(local_photo);
                taskListInfo.setVideourl(photourls);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskListInfo", taskListInfo);
                intent.putExtra("data", bundle);
                intent.putExtra("position", position);
                intent.putExtra("which_page", which_page);
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();
            }
            break;
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
                        ConfirmDialog.showDialog(VideoTaskActivity.this, "提示", 1, "网络异常，点击图片重新上传", "", "我知道了",
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
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AppInfo
                        .REQUEST_CODE_ASK_RECORD_AUDIO);
                return;
            }
            checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(VideoTaskActivity.this, "拍照权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(VideoTaskActivity.this, "录音权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    videoAdapter.notifyDataSetChanged();
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
                    if (videoAdapter != null) {
                        if (!list.isEmpty() && !list.get(list.size() - 1).getPath().startsWith("add")) {
                            if (list.size() < MAXPHOTONUM) {//最大值9
                                TaskPhotoInfo addphtot = new TaskPhotoInfo();
                                addphtot.setPath("add_photo");
                                list.add(addphtot);
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
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
                sta_location = "1";
            } else {
                sta_location = "0";
            }
        } else if (taskphoto_check2 == buttonView.getId()) {
            if (isChecked) {
                local_photo = "1";
            } else {
                local_photo = "0";
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case AppInfo.TaskitemShotRequestCodeForShot: {
                if (resultCode == AppInfo.ShotSuccessResultCode) {
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    if (index == 1) {
                            /*taskitemshot_video1.setImageBitmap(Tools.createVideoThumbnail(path));
                            taskitemshot_video1.setTag(path);*/
                    }
                    //path:::::::>>>>/storage/emulated/0/OY/data/video/15300239174/null/22344336868392034552945null.mp4
                    Tools.d("path:::::::>>>>" + path);

                    TaskPhotoInfo taskPhotoInfo = new TaskPhotoInfo();
                    taskPhotoInfo.setPath(path);
                    taskPhotoInfo.setLocal(false);
                    int size = list.size();
                    list.set(size - 1, taskPhotoInfo);
                    if (canUpdata) {
                        sendOSSData(size - 1);
                    }
                    videoAdapter.notifyDataSetChanged();
                    handler.sendEmptyMessageDelayed(4, 500);
                    handler.sendEmptyMessageDelayed(4, 1000);
                    handler.sendEmptyMessageDelayed(4, 2000);
                }
            }
            break;
        }
    }

    @Override
    public void deleteClick(int pos) {
        if (task != null)
            task.cancel();

        //  TaskPhotoInfo taskPhotoInfo = list.get(pos);
        list.remove(pos);
        videoAdapter.notifyDataSetChanged();
        canUpdata = true;
        handler.sendEmptyMessage(3);
        if (!list.isEmpty() && !list.get(list.size() - 1).getPath().startsWith("add")) {
            if (list.size() < MAXPHOTONUM) {//最大值9
                TaskPhotoInfo addphtot = new TaskPhotoInfo();
                addphtot.setPath("add_photo");
                list.add(addphtot);
            }
        }
        videoAdapter.notifyDataSetChanged();
        handler.sendEmptyMessageDelayed(4, 100);
    }
}
