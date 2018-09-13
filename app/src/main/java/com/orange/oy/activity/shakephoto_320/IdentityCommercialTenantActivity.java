package com.orange.oy.activity.shakephoto_320;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.common.StringUtils;
import com.orange.oy.R;
import com.orange.oy.activity.Camerase;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.bright_addr_personinfo;
import static com.orange.oy.R.id.iv_logo;
import static com.orange.oy.base.Tools.getBitmap;
import static com.orange.oy.network.Urls.MerchantCA;
import static com.orange.oy.network.Urls.MerchantCAInfo;
import static com.sobot.chat.utils.LogUtils.path;
import static com.zmer.zmersainuo.utils.CommonUtils.isNumeric;

/**
 * V3.20  商户认证界面
 */

public class IdentityCommercialTenantActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.identity_title);
        appTitle.settingName("认证商户");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        merchantCA = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(IdentityCommercialTenantActivity.this));
                params.put("brand_name", identitytext_name.getText().toString().trim());  //品牌名称【必传】
                params.put("busniss_term", identitytext_qixian.getText().toString().trim());  // 营业期限
                params.put("organization_code", identitytext_code.getText().toString().trim()); // 社会信用代码
                if (!Tools.isEmpty(business_license2)) {
                    params.put("license_url", business_license2);
                } else {
                    params.put("license_url", photourl);  //营业执照照片Url
                }
                return params;
            }
        };

        merchantCAInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(IdentityCommercialTenantActivity.this));
                return params;
            }
        };
    }

    private String enterprise_name, business_term, org_code;

    private String teamId;
    private ImageLoader imageLoader;
    private String isHaveTag;
    private String merchant; //等于1是已认证，0是未认证

    private void initView() {
        isHaveTag = getIntent().getStringExtra("isHaveTag");
        merchant = getIntent().getStringExtra("merchant");
        ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 20);
        lin_tag = (LinearLayout) findViewById(R.id.lin_tag);
        identitytext_name = (EditText) findViewById(R.id.identitytext_name);
        identitytext_qixian = (EditText) findViewById(R.id.identitytext_qixian);
        identitytext_code = (EditText) findViewById(R.id.identitytext_code);
        identity_img_face = (ImageView) findViewById(R.id.identity_img_face);

        findViewById(R.id.identity_button).setOnClickListener(this);
        identity_img_face.setOnClickListener(this);
        if (!Tools.isEmpty(isHaveTag) && "1".equals(isHaveTag)) {
            lin_tag.setVisibility(View.VISIBLE);
        } else {
            lin_tag.setVisibility(View.GONE);
        }
    }

    private LinearLayout lin_tag;
    private ImageView identity_img_face;
    private EditText identitytext_name, identitytext_qixian, identitytext_code;
    private NetworkConnection merchantCAInfo, merchantCA;
    private boolean isUpdata = false;
    private String photourl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_commercial);
        imageLoader = new ImageLoader(getBaseContext());
        initTitle();
        initNetworkConnection();
        checkPermission();
        initView();
        getData();
    }


    //企业认证接口
    private void sendData() {
        merchantCA.sendPostRequest(Urls.MerchantCA, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        baseFinish();
                        Tools.showToast(IdentityCommercialTenantActivity.this, jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(IdentityCommercialTenantActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(IdentityCommercialTenantActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityCommercialTenantActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }

    private String enterprise_name2, org_code2, business_license2, business_term2, merchant_id, check_state;//回显数据

    private void getData() {
        merchantCAInfo.sendPostRequest(Urls.MerchantCAInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!jsonObject.isNull("data")) {
                            JSONObject object = jsonObject.optJSONObject("data");
                            merchant_id = object.getString("merchant_id");
                            check_state = object.optString("check_state");  //审核状态（0为未认证，1为认证审核成功，2为认证审核失败）
                            enterprise_name2 = object.optString("brand_name");  //商户的品牌名称
                            org_code2 = object.optString("organization_code");    // 统一社会信用代码
                            business_term2 = object.optString("busniss_term");  // 营业期限
                            business_license2 = object.optString("license_url");  //  营业执照图片
                        }
                        if (!TextUtils.isEmpty(enterprise_name2)) {
                            identitytext_name.setText(enterprise_name2);
                        }
                        if (!TextUtils.isEmpty(org_code2)) {
                            identitytext_code.setText(org_code2);
                        }
                        if (!TextUtils.isEmpty(business_term2)) {
                            identitytext_qixian.setText(business_term2);
                        }
                        if (!TextUtils.isEmpty(business_license2)) {
                            imageLoader.DisplayImage(Urls.Endpoint3 + business_license2, identity_img_face, R.mipmap.jgrz_button_yyzz);
                        }
                    } else {
                        String msg = jsonObject.getString("msg");
                        if (!"没有查询到商户信息".equals(msg)) {
                            Tools.showToast(IdentityCommercialTenantActivity.this, msg);
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(IdentityCommercialTenantActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityCommercialTenantActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //营业执照副本
            case R.id.identity_img_face: {
                SelectPhotoDialog.showPhotoSelecter(IdentityCommercialTenantActivity.this, true, takeListener, pickListener);
            }
            break;

            //提交
            case R.id.identity_button: {
                //business_license照片;
                enterprise_name = identitytext_name.getText().toString().trim();
                business_term = identitytext_qixian.getText().toString().trim();
                org_code = identitytext_code.getText().toString().trim();

                unIdBind();
            }
            break;
        }
    }

    private void unIdBind() {
        if (isUpdata) {
            Tools.showToast(this, "正在上传");
            return;
        }
        if (enterprise_name == null || "".equals(enterprise_name)) {
            Tools.showToast(this, "请填写企业姓名");
            return;
        }

        if (business_term == null || "".equals(business_term)) {
            Tools.showToast(this, "请填写营业期限");
            return;
        }
        if (org_code == null || "".equals(org_code)) {
            Tools.showToast(this, "请填写组织机构代码");
            return;
        }
        if (!isNumeric(org_code)) {
            Tools.showToast(this, "组织机构代码只能是数字哦~");
            return;
        }
        if (Tools.isEmpty(photourl)) {
            Tools.showToast(IdentityCommercialTenantActivity.this, "请拍摄营业执照正面照片");
            return;
        }
        sendData();
    }


    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto
                    (IdentityCommercialTenantActivity.this).getPath() + "/myImg0.jpg")));
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
                    intent.putExtra("aspectX", 2);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 1000);
                    intent.putExtra("outputY", 500);
                    intent.putExtra("scale", true);//黑边
                    intent.putExtra("scaleUpIfNeeded", true);//黑边
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                            .getPath() + "/myImg.jpg")));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg.jpg";
                    identity_img_face.setImageBitmap(Tools.getBitmap(filePath, 200, 200));
                    sendOSSData(filePath);
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    protected void onDestroy() {
        if (task != null) {
            task.cancel();
        }
        super.onDestroy();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;

    public void sendOSSData(String s) {
        try {
            CustomProgressDialog.showProgressDialog(this, "正在上传");
            Tools.d(s);
            isUpdata = true;
            File file = new File(s);
            String objectKey = file.getName() + Tools.getTimeSS() + "_" + file.hashCode();
            objectKey = Urls.Businesslicense + "/" + objectKey;
            photourl = objectKey;
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
                    CustomProgressDialog.Dissmiss();
                    isUpdata = false;
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
