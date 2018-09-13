package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.PrizeSettingDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务奖励 V3.20
 */
public class TaskPrizeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskprize_title);
        appTitle.settingName("任务奖励");
        appTitle.showBack(this);
    }

    protected void onStop() {
        super.onStop();
        if (checkMerchantCA != null) {
            checkMerchantCA.stop(Urls.CheckMerchantCA);
        }
    }

    private CheckBox taskprize_check1, taskprize_check2;
    private String reward_type, gift_url;
    private EditText taskprize_unit, taskprize_name, taskprize_price;
    private ImageView taskprize_img;
    private NetworkConnection checkMerchantCA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_prize);
        initTitle();
        initNetwork();
        taskprize_check1 = (CheckBox) findViewById(R.id.taskprize_check1);
        taskprize_check2 = (CheckBox) findViewById(R.id.taskprize_check2);
        taskprize_unit = (EditText) findViewById(R.id.taskprize_unit);
        taskprize_name = (EditText) findViewById(R.id.taskprize_name);
        taskprize_price = (EditText) findViewById(R.id.taskprize_price);
        taskprize_img = (ImageView) findViewById(R.id.taskprize_img);
        findViewById(R.id.taskprize_submit).setOnClickListener(this);
        taskprize_img.setOnClickListener(this);
        taskprize_check1.setOnCheckedChangeListener(this);
        taskprize_check2.setOnCheckedChangeListener(this);
    }

    private void initNetwork() {
        checkMerchantCA = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskPrizeActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        checkMerchantCA.setIsShowDialog(true);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.taskprize_submit) {
            if (taskprize_check1.isChecked() && taskprize_check2.isChecked()) {
                reward_type = "3";
            } else if (taskprize_check1.isChecked() && !taskprize_check2.isChecked()) {
                reward_type = "1";
            } else if (!taskprize_check1.isChecked() && taskprize_check2.isChecked()) {
                reward_type = "2";
            } else {
                reward_type = "";
            }
            if (Tools.isEmpty(reward_type)) {
                Tools.showToast(this, "请选择奖励类型");
                return;
            }
            String money = taskprize_unit.getText().toString().trim();
            if ("1".equals(reward_type) || "3".equals(reward_type)) {//现金奖励
                if (Tools.isEmpty(money)) {
                    Tools.showToast(this, "请填写任务单价");
                    return;
                }
                if (Tools.StringToDouble(money) <= 2) {
                    Tools.showToast(this, "请输入大于2的任务单价");
                    return;
                }
            }
            String gift_name = taskprize_name.getText().toString().trim();
            String gift_money = taskprize_price.getText().toString().trim();
            if ("2".equals(reward_type) || "3".equals(reward_type)) {//礼品奖励
                if (Tools.isEmpty(gift_name)) {
                    Tools.showToast(this, "请填写礼品名称");
                    return;
                }
                if (Tools.isEmpty(gift_money)) {
                    Tools.showToast(this, "请填写礼品价值");
                    return;
                }
                if (Tools.isEmpty(gift_url)) {
                    Tools.showToast(this, "请上传礼品图片");
                    return;
                }
                if (Tools.StringToDouble(gift_money) <= 2) {
                    Tools.showToast(this, "请输入大于2的礼品价值");
                    return;
                }
                if (!isUpdata) {
                    Tools.showToast(this, "图片还未上传完成呢~");
                    return;
                }
            }
            Intent intent = new Intent();
            intent.putExtra("reward_type", reward_type);
            if ("1".equals(reward_type) || "3".equals(reward_type)) {
                intent.putExtra("money", money);
            }
            if ("2".equals(reward_type) || "3".equals(reward_type)) {
                intent.putExtra("gift_name", gift_name);
                intent.putExtra("gift_money", gift_money);
                intent.putExtra("gift_url", gift_url);
            }
            setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
            baseFinish();
        } else if (v.getId() == R.id.taskprize_img) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.taskprize_check1: {
                if (isChecked) {
                    findViewById(R.id.taskprize_ly1).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.taskprize_ly1).setVisibility(View.GONE);
                }
            }
            break;
            case R.id.taskprize_check2: {
                if (isChecked) {
                    findViewById(R.id.taskprize_ly2).setVisibility(View.VISIBLE);
                    checkMerchantCA();
                } else {
                    findViewById(R.id.taskprize_ly2).setVisibility(View.GONE);
                }
            }
            break;
        }
    }

    private void checkMerchantCA() {
        checkMerchantCA.sendPostRequest(Urls.CheckMerchantCA, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        String certification_state = jsonObject.getString("certification_state");
                        final String merchant_id = jsonObject.getString("merchant_id");
                        if ("0".equals(certification_state)) {
                            taskprize_check2.setChecked(false);
                            findViewById(R.id.taskprize_ly2).setVisibility(View.GONE);
                            ConfirmDialog.showDialog(TaskPrizeActivity.this, "提示", 1, "为确保用户真实收到礼品，请先完成商户认证，认证审核周期为1-3个工作日，认证成功后，即可发布礼品奖励任务。",
                                    "以后再说", "商户认证", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        public void leftClick(Object object) {
                                        }

                                        public void rightClick(Object object) {
                                            Intent intent = new Intent(TaskPrizeActivity.this, IdentityCommercialTenantActivity.class);
                                            intent.putExtra("isHaveTag", "1");
                                            startActivity(intent);
                                        }
                                    });
                        } else {
                            String gift_library = jsonObject.getString("gift_library");
                            if ("1".equals(gift_library)) {//已有礼品库 弹窗选择
                                PrizeSettingDialog.showDialog(TaskPrizeActivity.this, new PrizeSettingDialog.OnPrizeSettingListener() {
                                    @Override
                                    public void firstClick() {
                                        //新建礼品
                                    }

                                    @Override
                                    public void secondClick() {
                                        Intent intent = new Intent(TaskPrizeActivity.this, PresentManagementActivity.class);
                                        intent.putExtra("isOnclick", "1");
                                        intent.putExtra("merchant_id", merchant_id);
                                        startActivityForResult(intent, 3);
                                    }
                                });
                            }
                        }
                    } else {
                        Tools.showToast(TaskPrizeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskPrizeActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskPrizeActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppInfo.MyDetailRequestCodeForPick: {
                    if (data != null) {
                        Uri uri = data.getData();
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(uri, "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 400);
                        intent.putExtra("aspectY", 399);
                        intent.putExtra("outputX", 400);
                        intent.putExtra("outputY", 399);
                        intent.putExtra("scale", true);//黑边
                        intent.putExtra("scaleUpIfNeeded", true);//黑边
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/myImg.jpg")));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                    }
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg.jpg";
                    taskprize_img.setImageBitmap(Tools.getBitmap(filePath, 200, 200));
                    sendOSSData(filePath);
                }
                break;
                case 3: {
                    if (data != null) {
                        gift_url = data.getStringExtra("gift_url");
                        taskprize_name.setText(data.getStringExtra("gift_name"));
                        taskprize_price.setText(data.getStringExtra("gift_money"));
                        isUpdata = true;
                        ImageLoader imageLoader = new ImageLoader(this);
                        imageLoader.setShowWH(200).DisplayImage(Urls.Endpoint3 + gift_url, taskprize_img);
                    }
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;
    private boolean isUpdata;

    public void sendOSSData(String s) {
        try {
            isUpdata = false;
            CustomProgressDialog.showProgressDialog(this, "正在上传");
            Tools.d(s);
            File file = new File(s);
            if (!file.exists() || !file.isFile()) {
                return;
            }
            String objectKey = file.getName();
            objectKey = Urls.Shakephoto + "/" + Tools.getTimeSS() + "_" + objectKey;
            gift_url = objectKey;
            Tools.d("图片地址：" + gift_url);
            if (credentialProvider == null)
                credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                        "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
            if (oss == null)
                oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, s);
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                Log.i("-------", "图片上传中-----");
                    Tools.d("currentSize: " + currentSize + " totalSize: " + totalSize);
                }
            });
            task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    CustomProgressDialog.Dissmiss();
                    Tools.d("上传成功");
                    isUpdata = true;
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
//                task.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.Dissmiss();
        }
    }
}
