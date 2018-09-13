package com.orange.oy.activity.shakephoto_320;

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
import android.text.TextUtils;
import android.view.View;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.base.Tools.getBitmap;


/**
 * V3.20商品信息
 */
public class ShopInformationActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.titleview);
        appTitle.settingName("商户信息");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        merchantInfoSubmit = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (bitmap != null) {
                    params.put("ad_url", photourl);
                }
                if (!Tools.isEmpty(tv_http.getText().toString().trim())) {
                    params.put("ad_link", tv_http.getText().toString().trim());  //广告图片地址
                }
                params.put("merchant_name", tv_name.getText().toString().trim()); //商户名称
                params.put("brand_name", tv_name2.getText().toString().trim()); // 品牌名称
                params.put("merchant_id", merchant_id);
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ShopInformationActivity.this));
                return params;
            }
        };
        merchantInfoSubmit.setIsShowDialog(true);

        merchantInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ShopInformationActivity.this));
                params.put("merchant_id", merchant_id);
                return params;
            }
        };
    }

    private TextView tv_name2, tv_name, tv_http;
    private ImageView iv_pic;
    private NetworkConnection merchantInfo, merchantInfoSubmit;
    private ImageLoader imageLoader;
    private Bitmap bitmap;
    private String merchant_id, http_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_information);
        initTitle();
        merchant_id = getIntent().getStringExtra("merchant_id");
        initNetworkConnection();
        imageLoader = new ImageLoader(this);
        tv_name2 = (TextView) findViewById(R.id.tv_name2);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        tv_http = (TextView) findViewById(R.id.tv_http);
        iv_pic.setOnClickListener(this);
        findViewById(R.id.tv_submit).setOnClickListener(this);

        getData();


    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit: {
                if (isUpdata) {
                    Tools.showToast(this, "正在上传");
                    return;
                }
                if (Tools.isEmpty(tv_name.getText().toString())) {
                    Tools.showToast(this, "请填写商户名称");
                    return;
                }

                if (Tools.isEmpty(tv_name2.getText().toString())) {
                    Tools.showToast(this, "请填写品牌名称");
                    return;
                }

                http_url = tv_http.getText().toString().trim();
                if (!Tools.isEmpty(http_url)) {
                    if (!Tools.isHttpUrl(http_url)) {
                        Tools.showToast(this, "请正确填写广告链接");
                        return;
                    }
                }


                if (TextUtils.isEmpty(ad_url)) {
                    if (faceList.isEmpty()) {
                        Tools.showToast(this, "请拍摄营业执照正面照片");
                        return;
                    } else {
                        list.add(faceList.get(faceList.size() - 1));
                    }
                }
                if (!faceList.isEmpty()) {
                    sendOSSData(list.get(0));
                } else {
                    sendData();
                }
            }
            break;
            case R.id.iv_pic: { //广告图
                SelectPhotoDialog.showPhotoSelecter(ShopInformationActivity.this, true, takeListener, pickListener);
            }
            break;
        }

    }

    private String merchant_name, brand_name, ad_url, ad_link;

    private void getData() {
        merchantInfo.sendPostRequest(Urls.MerchantInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.optJSONObject("data");
                            merchant_name = jsonObject.getString("merchant_name");
                            brand_name = jsonObject.getString("brand_name");
                            ad_url = jsonObject.getString("ad_url");  //广告图片地址
                            ad_link = jsonObject.getString("ad_link");  // 广告跳转链接"
                            if (!Tools.isEmpty(merchant_name)) {
                                tv_name.setText(merchant_name);
                            }
                            if (!Tools.isEmpty(brand_name)) {
                                tv_name2.setText(brand_name);
                            }
                            if (!Tools.isEmpty(ad_url)) {
                                imageLoader.DisplayImage(Urls.Endpoint3 + ad_url, iv_pic);
                            }
                            if (!Tools.isEmpty(ad_link)) {
                                tv_http.setText(ad_link);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(ShopInformationActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ShopInformationActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    private void sendData() {
        merchantInfoSubmit.sendPostRequest(Urls.MerchantInfoSubmit, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject job = new JSONObject(s);
                    int code = job.getInt("code");
                    if (code == 200) {
                        Tools.showToast(ShopInformationActivity.this, job.getString("msg"));
                        finish();

                    } else {
                        Tools.showToast(ShopInformationActivity.this, job.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(ShopInformationActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                SelectPhotoDialog.dissmisDialog();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                SelectPhotoDialog.dissmisDialog();
                Tools.showToast(ShopInformationActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在修改...");
    }

    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto
                    (ShopInformationActivity.this).getPath() + "/myImg0.jpg")));
            intent.putExtra("camerasensortype", 1);
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForTake);
        }
    };

    //相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 200);
                        intent.putExtra("outputY", 200);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/myImg.jpg")));
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
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                            .getPath() + "/myImg.jpg")));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg.jpg";
                    settingImgPath(filePath);
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private ArrayList<String> faceList = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();

    private void settingImgPath(String path) {
        faceList.clear();
        list.clear();

        if (!new File(path).isFile()) {
            Tools.showToast(ShopInformationActivity.this, "拍照方式错误");
            return;
        }
        bitmap = Tools.imageZoom(getBitmap(path), 50);
        if (bitmap == null) {
            Tools.showToast(ShopInformationActivity.this, "头像设置失败");
            return;
        }

        Tools.d("path:" + path);
        faceList.add(path);
        list.add(path);
        iv_pic.setImageBitmap(getBitmap(path, 400, 400));
        iv_pic.setTag(path);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {//上传完成
                    photourl = photourl.trim().replaceAll("\\[", "").replaceAll("\\]", "");
                    sendData();
                }
                break;
              /*  case 2: {//显示图片
                    String[] str = business_license2.split("/");
                    iv_pic.setImageBitmap(getBitmap(str[str.length - 1]));
                    CustomProgressDialog.Dissmiss();
                }
                break;*/
            }
            Tools.d("handler");
        }
    };


    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;
    private String photourl;
    private boolean isUpdata = false;

    public void sendOSSData(String s) {
        try {
            CustomProgressDialog.showProgressDialog(this, "正在上传");
            Tools.d(s);
            isUpdata = true;
            // String originalPath = systemDBHelper.searchForOriginalpath(s);
            File file = new File(s);
            String objectKey = file.getName();
            objectKey = Tools.getTimeSS() + "_" + objectKey;
            objectKey = Urls.EndpointDir + "/" + objectKey;
            if (TextUtils.isEmpty(photourl)) {
                photourl = objectKey;
            } else {
                photourl += "," + objectKey;
            }
            Tools.d("图片地址：" + photourl);
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
                    Tools.d(result.getStatusCode() + "");

                    handler.sendEmptyMessage(1);//第三张上传完毕
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.Dissmiss();
        }
    }
}
