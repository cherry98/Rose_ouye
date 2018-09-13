package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import java.io.File;

/**
 * 礼品设置
 */
public class PrizeSettingActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.prizesetting_title);
        appTitle.settingName("礼品设置");
        appTitle.showBack(this);
    }

    private EditText prizesetting_name, prizesetting_price, prizesetting_num;
    private ImageView prizesetting_img;
    private String gift_url, gift_name, gift_money, gift_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prize_setting);
        Intent data = getIntent();
        gift_url = data.getStringExtra("gift_url");
        gift_money = data.getStringExtra("gift_money");
        gift_num = data.getStringExtra("gift_num");
        gift_name = data.getStringExtra("gift_name");
        initTitle();
        prizesetting_name = (EditText) findViewById(R.id.prizesetting_name);
        prizesetting_price = (EditText) findViewById(R.id.prizesetting_price);
        prizesetting_num = (EditText) findViewById(R.id.prizesetting_num);
        prizesetting_img = (ImageView) findViewById(R.id.prizesetting_img);
        if (!Tools.isEmpty(gift_name)) {
            prizesetting_name.setText(gift_name);
        }
        if (!Tools.isEmpty(gift_money)) {
            prizesetting_price.setText(gift_money);
        }
        if (!Tools.isEmpty(gift_num)) {
            prizesetting_num.setText(gift_num);
        }
        if (!Tools.isEmpty(gift_url)) {
            ImageLoader imageLoader = new ImageLoader(this);
            imageLoader.setShowWH(200).DisplayImage(Urls.Endpoint3 + gift_url, prizesetting_img);
            isUpdata = true;
        }
        findViewById(R.id.prizesetting_submit).setOnClickListener(this);
        prizesetting_img.setOnClickListener(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prizesetting_img: {//礼品图片
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
            }
            break;
            case R.id.prizesetting_submit: {//提交
                gift_name = prizesetting_name.getText().toString().trim();
                if (Tools.isEmpty(gift_name)) {
                    Tools.showToast(this, "请输入礼品名称");
                    return;
                }
                gift_money = prizesetting_price.getText().toString().trim();
                if (Tools.isEmpty(gift_money)) {
                    Tools.showToast(this, "请输入礼品价值的金额");
                    return;
                }
                gift_num = prizesetting_num.getText().toString().trim();
                if (Tools.isEmpty(gift_num)) {
                    Tools.showToast(this, "请输入发放礼品的数量");
                    return;
                }
                if (Tools.isEmpty(gift_url)) {
                    Tools.showToast(this, "请上传礼品图片");
                    return;
                }
                if (!(Tools.StringToDouble(gift_money) > 1)) {
                    Tools.showToast(this, "请输入大于1的礼品价值的金额");
                    return;
                }
                if (!(Tools.StringToDouble(gift_num) > 1)) {
                    Tools.showToast(this, "请输入大于1的礼品的数量");
                    return;
                }
                if (!isUpdata) {
                    Tools.showToast(this, "图片还未上传完成呢~");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("gift_name", gift_name);
                intent.putExtra("gift_money", gift_money);
                intent.putExtra("gift_num", gift_num);
                intent.putExtra("gift_url", gift_url);
                setResult(RESULT_OK, intent);
                baseFinish();
            }
            break;
        }
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
                    prizesetting_img.setImageBitmap(Tools.getBitmap(filePath, 200, 200));
                    sendOSSData(filePath);
                }
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
