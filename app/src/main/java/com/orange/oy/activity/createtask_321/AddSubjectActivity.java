package com.orange.oy.activity.createtask_321;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.orange.oy.adapter.mycorps_314.AddSubjectAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.shakephoto.OptionsListInfo;
import com.orange.oy.info.shakephoto.QuestionListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;

import java.io.File;
import java.util.ArrayList;

/**
 * 问卷任务添加题目
 *
 * @author Lenovo
 */
public class AddSubjectActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AddSubjectAdapter.OnSubjectListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.addsubject_title);
        appTitle.settingName("添加题目");
        appTitle.showBack(this);
    }

    private ArrayList<OptionsListInfo> list1, list2;
    private AddSubjectAdapter addSubjectAdapter, addSubjectAdapter2;
    private ListView addsubject_listview1, addsubject_listview2;
    private EditText addsubject_qname, addsubject_max;
    private String question_type = "1", isrequired = "1", question_num;
    //0-->添加 1--编辑
    private String which_page;
    private Intent data;
    private CheckBox addsubject_isrequire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
        initTitle();
        data = getIntent();
        question_num = data.getStringExtra("question_num");
        which_page = data.getStringExtra("which_page");

        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        addsubject_qname = (EditText) findViewById(R.id.addsubject_qname);
        addsubject_max = (EditText) findViewById(R.id.addsubject_max);
        addsubject_listview1 = (ListView) findViewById(R.id.addsubject_listview1);
        addsubject_listview2 = (ListView) findViewById(R.id.addsubject_listview2);
        addsubject_isrequire = (CheckBox) findViewById(R.id.addsubject_isrequire);
        RadioGroup addsubject_group = (RadioGroup) findViewById(R.id.addsubject_group);
        if ("0".equals(which_page)) {
            addData();
        } else {
            editData();
        }
        addSubjectAdapter = new AddSubjectAdapter(this, list1);
        addSubjectAdapter.setOnSubjectListener(this);
        addsubject_listview1.setAdapter(addSubjectAdapter);//
        measureHeight(list1, addsubject_listview1);
        //多选
        addSubjectAdapter2 = new AddSubjectAdapter(this, list2);
        addSubjectAdapter2.setOnSubjectListener(this);
        addsubject_listview2.setAdapter(addSubjectAdapter2);//
        measureHeight(list2, addsubject_listview2);
        if ("1".equals(which_page)) {
            addSubjectAdapter.setEdit(true);
            addSubjectAdapter2.setEdit(true);
        }
        findViewById(R.id.addsubject_submit).setOnClickListener(this);
        addsubject_group.setOnCheckedChangeListener(this);
        addsubject_isrequire.setOnCheckedChangeListener(this);
        checkPermission();
    }

    public void measureHeight(ArrayList<OptionsListInfo> list, ListView listView) {
        int height = Tools.dipToPx(this, (140 + 10) * (list.size() - 1) + 45);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listView.getLayoutParams();
        layoutParams.height = height;
        listView.setLayoutParams(layoutParams);
    }

    private void editData() {
        RadioButton addsubject_button1 = (RadioButton) findViewById(R.id.addsubject_button1);
        RadioButton addsubject_button2 = (RadioButton) findViewById(R.id.addsubject_button2);
        RadioButton addsubject_button3 = (RadioButton) findViewById(R.id.addsubject_button3);
        QuestionListInfo questionListInfo = (QuestionListInfo) data.getBundleExtra("data").getSerializable("questionListInfo");
        question_type = questionListInfo.getQuestion_type();
        addsubject_qname.setText(questionListInfo.getQuestion_name());
        isrequired = questionListInfo.getIsrequired();
        addsubject_isrequire.setChecked("1".equals(isrequired));
        OptionsListInfo optionsListInfo = new OptionsListInfo();
        optionsListInfo.setOption_id("-1");
        if ("1".equals(question_type)) {
            addsubject_button1.setChecked(true);
            list1 = questionListInfo.getOptions();
            list1.add(optionsListInfo);
            addsubject_listview1.setVisibility(View.VISIBLE);
            addsubject_listview2.setVisibility(View.GONE);
            findViewById(R.id.addsubject_max_ly).setVisibility(View.GONE);
        } else if ("2".equals(question_type)) {//多选
            addsubject_button2.setChecked(true);
            addsubject_max.setText(questionListInfo.getMax_option());
            findViewById(R.id.addsubject_max_ly).setVisibility(View.VISIBLE);
            list2 = questionListInfo.getOptions();
            list2.add(optionsListInfo);
            addsubject_listview1.setVisibility(View.GONE);
            addsubject_listview2.setVisibility(View.VISIBLE);
        } else {
            addsubject_button3.setChecked(true);
            addsubject_listview1.setVisibility(View.GONE);
            addsubject_listview2.setVisibility(View.GONE);
            findViewById(R.id.addsubject_max_ly).setVisibility(View.GONE);
        }
    }

    private void addData() {
        OptionsListInfo optionsListInfo1 = new OptionsListInfo();//单选
        optionsListInfo1.setOption_num("1");
        list1.add(optionsListInfo1);
        OptionsListInfo optionsListInfo2 = new OptionsListInfo();
        optionsListInfo2.setOption_num("2");
        list1.add(optionsListInfo2);

        OptionsListInfo optionsListInfo4 = new OptionsListInfo();
        optionsListInfo4.setOption_num("1");
        list2.add(optionsListInfo4);
        OptionsListInfo optionsListInfo5 = new OptionsListInfo();
        optionsListInfo5.setOption_num("2");
        list2.add(optionsListInfo5);//不可复用

        OptionsListInfo optionsListInfo3 = new OptionsListInfo();
        optionsListInfo3.setOption_id("-1");
        list1.add(optionsListInfo3);
        list2.add(optionsListInfo3);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void addItem() {//添加选项
        int size;
        OptionsListInfo info = new OptionsListInfo();
        if ("1".equals(question_type)) {
            size = list1.size();
            if (size - 1 >= 10) {
                Tools.showToast(this, "已达到最大限制~");
                return;
            }
            info.setOption_num(size + "");
            list1.add(size - 1, info);
            measureHeight(list1, addsubject_listview1);
            addSubjectAdapter.notifyDataSetChanged();
        } else {
            size = list2.size();
            if (size - 1 >= 10) {
                Tools.showToast(this, "已达到最大限制~");
                return;
            }
            info.setOption_num(size + "");
            list2.add(size - 1, info);
            measureHeight(list2, addsubject_listview2);
            addSubjectAdapter2.notifyDataSetChanged();
        }
    }

    private int position;//标记需上传的图片是list的哪个选项里的

    @Override
    public void deleteItem(int position) {//删除选项
        if ("1".equals(question_type)) {
            list1.remove(position);
            for (int i = position; i < list1.size() - 1; i++) {
                list1.get(i).setOption_num((i + 1) + "");
            }
            measureHeight(list1, addsubject_listview1);
            addSubjectAdapter.notifyDataSetChanged();
        } else {
            list2.remove(position);
            for (int i = position; i < list2.size() - 1; i++) {
                list2.get(i).setOption_num((i + 1) + "");
            }
            measureHeight(list2, addsubject_listview2);
            addSubjectAdapter2.notifyDataSetChanged();
        }
    }

    @Override
    public void addImg(int position) {//添加图片
        ArrayList<OptionsListInfo> list;
        if ("1".equals(question_type)) {
            list = list1;
        } else {
            list = list2;
        }
        if (Tools.isEmpty(list.get(position).getPath())) {
            this.position = position;
            SelectPhotoDialog.showPhotoSelecter(AddSubjectActivity.this, true, takeListener, pickListener);
        }
    }

    @Override
    public void deleteImg(int positon) {//删除图片
        OptionsListInfo optionsListInfo;
        if ("1".equals(question_type)) {
            optionsListInfo = list1.get(positon);
        } else {
            optionsListInfo = list2.get(positon);
        }
        optionsListInfo.setPath("");
        optionsListInfo.setUped(false);
        optionsListInfo.setPhoto_url("");
        if ("1".equals(question_type)) {
            list1.set(positon, optionsListInfo);
            addSubjectAdapter.notifyDataSetChanged();
        } else {
            list2.set(positon, optionsListInfo);
            addSubjectAdapter2.notifyDataSetChanged();
        }
    }

    /**
     * 拍照
     */
    private View.OnClickListener takeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto
                    (AddSubjectActivity.this).getPath() + "/myImg0.jpg")));
            intent.putExtra("camerasensortype", 1);
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForTake);
        }
    };
    /**
     * 从相册选取
     */
    private View.OnClickListener pickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    if ("1".equals(question_type)) {
                        list1.get(msg.arg1).setUped(true);
                        //继续上传
                        int size = list1.size();
                        for (int i = 0; i < size; i++) {
                            OptionsListInfo optionsListInfo = list1.get(i);
                            if (!"-1".equals(optionsListInfo.getOption_id()) && !optionsListInfo.isUped()
                                    && !Tools.isEmpty(optionsListInfo.getPath())) {
                                sendOSSData(optionsListInfo.getPath(), i);
                                break;
                            }
                        }
                        addSubjectAdapter.notifyDataSetChanged();
                    } else {
                        list2.get(msg.arg1).setUped(true);
                        //继续上传
                        int size = list2.size();
                        for (int i = 0; i < size; i++) {
                            OptionsListInfo optionsListInfo = list2.get(i);
                            if (!"-1".equals(optionsListInfo.getOption_id()) && !optionsListInfo.isUped()
                                    && !Tools.isEmpty(optionsListInfo.getPath())) {
                                sendOSSData(optionsListInfo.getPath(), i);
                                break;
                            }
                        }
                        addSubjectAdapter2.notifyDataSetChanged();
                    }
                }
                default:
            }
        }
    };
    private String times;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppInfo.MyDetailRequestCodeForTake: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg0.jpg";
                    if (!new File(filePath).isFile()) {
                    } else {
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/myImg0.jpg")), "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 400);
                        intent.putExtra("aspectY", 399);
                        intent.putExtra("outputX", 400);
                        intent.putExtra("outputY", 399);
                        times = Tools.getTimeSS() + "_addsubject";
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/" + times + ".jpg")));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                    }
                }
                break;
                case AppInfo.MyDetailRequestCodeForPick: {
                    Uri uri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("crop", "true");
                    if (Build.MANUFACTURER.equals("HUAWEI")) {
                        intent.putExtra("aspectX", 9998);
                        intent.putExtra("aspectY", 9999);
                    } else {
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                    }
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    times = Tools.getTimeSS() + "_addsubject";
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                            .getPath() + "/" + times + ".jpg")));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/" + times + ".jpg";
                    if ("1".equals(question_type)) {
                        list1.get(position).setPath(filePath);
                        if (canUpdata) {
                            sendOSSData(filePath, position);
                        }
                        addSubjectAdapter.notifyDataSetChanged();
                    } else {
                        list1.get(position).setPath(filePath);
                        if (canUpdata) {
                            sendOSSData(filePath, position);
                        }
                        addSubjectAdapter.notifyDataSetChanged();
                    }
                }
                break;
                default:
            }
        }
    }

    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;
    //是否可以上传
    private boolean canUpdata = true;

    public void sendOSSData(String path, final int position) {
        CustomProgressDialog.showProgressDialog(this, "正在上传");
        if (!canUpdata) {
            return;
        }
        canUpdata = false;
        File file = new File(path);
        String objectKey = file.getName();
        objectKey = file.hashCode() + "_" + objectKey;
        objectKey = Urls.EndpointDir + "/" + objectKey;
        if ("1".equals(question_type)) {
            list1.get(position).setPhoto_url(Urls.Endpoint3 + objectKey);
        } else {
            list2.get(position).setPhoto_url(Urls.Endpoint3 + objectKey);
        }
        Tools.d("url+position：" + position + "object:" + objectKey);
        if (credentialProvider == null) {
            credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                    "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
        }
        if (oss == null) {
            oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
        }
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, path);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
            }
        });
        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                CustomProgressDialog.Dissmiss();
                Message message = new Message();
                message.what = 1;
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

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addsubject_submit) {
            ArrayList<OptionsListInfo> list;
            if ("1".equals(question_type)) {
                list = list1;
            } else {
                list = list2;
            }
            String question_name = addsubject_qname.getText().toString().trim();
            String max_option = "";
            if (Tools.isEmpty(question_name)) {
                Tools.showToast(this, "请填写题目标题");
                return;
            }
            if (!"4".equals(question_type)) {
                int size = list.size();
                for (int i = 0; i < size - 1; i++) {
                    OptionsListInfo optionsListInfo = list.get(i);
                    if (Tools.isEmpty(optionsListInfo.getOption_name())) {
                        Tools.showToast(this, "请填写第" + (i + 1) + "个选项内容");
                        return;
                    }
                    if (!Tools.isEmpty(optionsListInfo.getPath())) {
                        if (!optionsListInfo.isUped()) {
                            Tools.showToast(this, "图片还未上传完成呢~");
                            return;
                        }
                    }
                }
                if ("2".equals(question_type)) {
                    max_option = addsubject_max.getText().toString().trim();
                    if (Tools.isEmpty(max_option)) {
                        Tools.showToast(this, "请输入最多选项");
                        return;
                    }
                    if (Tools.StringToInt(max_option) > size - 1) {
                        Tools.showToast(this, "最多选项不可大于选项数量");
                        return;
                    }
                }
                if (size < 3) {
                    Tools.showToast(this, "至少添加两个选项");
                    return;
                }
            }
            QuestionListInfo questionListInfo = new QuestionListInfo();
            questionListInfo.setQuestion_id("");
            questionListInfo.setQuestion_type(question_type);
            questionListInfo.setQuestion_name(question_name);
            if ("4".equals(question_type)) {
                questionListInfo.setOptions(null);
            } else {
                list.remove(list.size() - 1);
                questionListInfo.setOptions(list);
            }
            questionListInfo.setMax_option(max_option);
            questionListInfo.setMin_option("1");
            questionListInfo.setIsrequired(isrequired);
            questionListInfo.setQuestion_num(question_num);
            //回传
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("questionListInfo", questionListInfo);
            intent.putExtra("data", bundle);
            intent.putExtra("position", data.getIntExtra("position", 0));
            setResult(RESULT_OK, intent);
            baseFinish();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.addsubject_button1) {//单选
            question_type = "1";
            addsubject_listview1.setVisibility(View.VISIBLE);
            addsubject_listview2.setVisibility(View.GONE);
            findViewById(R.id.addsubject_max_ly).setVisibility(View.GONE);
            findViewById(R.id.view).setVisibility(View.VISIBLE);
            if ("1".equals(which_page) && list1.isEmpty()) {//编辑的时候
                OptionsListInfo optionsListInfo1 = new OptionsListInfo();//单选
                optionsListInfo1.setOption_num("1");
                list1.add(optionsListInfo1);
                OptionsListInfo optionsListInfo2 = new OptionsListInfo();
                optionsListInfo2.setOption_num("2");
                list1.add(optionsListInfo2);
                OptionsListInfo optionsListInfo3 = new OptionsListInfo();
                optionsListInfo3.setOption_id("-1");
                list1.add(optionsListInfo3);
                measureHeight(list1, addsubject_listview1);
                addSubjectAdapter.notifyDataSetChanged();
            }
        } else if (checkedId == R.id.addsubject_button2) {//多选
            question_type = "2";
            addsubject_listview1.setVisibility(View.GONE);
            addsubject_listview2.setVisibility(View.VISIBLE);
            findViewById(R.id.addsubject_max_ly).setVisibility(View.VISIBLE);
            findViewById(R.id.view).setVisibility(View.VISIBLE);
            if ("1".equals(which_page) && list2.isEmpty()) {//编辑的时候
                OptionsListInfo optionsListInfo1 = new OptionsListInfo();//单选
                optionsListInfo1.setOption_num("1");
                list2.add(optionsListInfo1);
                OptionsListInfo optionsListInfo2 = new OptionsListInfo();
                optionsListInfo2.setOption_num("2");
                list2.add(optionsListInfo2);
                OptionsListInfo optionsListInfo3 = new OptionsListInfo();
                optionsListInfo3.setOption_id("-1");
                list2.add(optionsListInfo3);
                measureHeight(list2, addsubject_listview2);
                addSubjectAdapter2.notifyDataSetChanged();
            }
        } else if (checkedId == R.id.addsubject_button3) {//填空
            question_type = "4";
            addsubject_listview1.setVisibility(View.GONE);
            addsubject_listview2.setVisibility(View.GONE);
            findViewById(R.id.addsubject_max_ly).setVisibility(View.GONE);
            findViewById(R.id.view).setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isrequired = "1";
        } else {
            isrequired = "0";
        }
    }
}
