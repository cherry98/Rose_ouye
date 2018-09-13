package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.Camerase;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * V3.14企业认证界面
 */

public class IdentityCompanyActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.identity_title);
        appTitle.settingName("企业认证");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        identityInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(IdentityCompanyActivity.this));
                params.put("team_id", teamId);
                params.put("enterprise_name", enterprise_name); //企业公司
                params.put("business_term", business_term);  // 营业期限
                params.put("org_code", org_code); // 组织机构代码
                params.put("business_license", photourl);  //营业执照照片Url

                return params;
            }
        };
        identityInfo.setIsShowDialog(true);

        enterpriseAuthInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(IdentityCompanyActivity.this));
                params.put("team_id", teamId);
                return params;
            }
        };
    }

    private String enterprise_name, business_term, org_code;

    private String teamId;
    private ImageLoader imageLoader;

    private void initView() {
        teamId = getIntent().getStringExtra("teamId");  //战队id
        ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 20);
        identitytext_name = (EditText) findViewById(R.id.identitytext_name);
        identitytext_qixian = (EditText) findViewById(R.id.identitytext_qixian);
        identitytext_code = (EditText) findViewById(R.id.identitytext_code);
        identity_img_face = (ImageView) findViewById(R.id.identity_img_face);
        findViewById(R.id.identity_button).setOnClickListener(this);
        identity_img_face.setOnClickListener(this);
    }

    private ImageView identity_img_face;
    private EditText identitytext_name, identitytext_qixian, identitytext_code;
    private NetworkConnection identityInfo, enterpriseAuthInfo;
    private SystemDBHelper systemDBHelper;
    private boolean isUpdata = false;
    private String name, sex, idcardnum, photourl;//上传数据
    private String enterprise_name2, org_code2, business_license2, business_term2;//回显数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_company);
        imageLoader = new ImageLoader(getBaseContext());
        systemDBHelper = new SystemDBHelper(this);
        initTitle();
        initNetworkConnection();
        checkCameraPermission();
        initView();
        getData();
    }


    //战队认证接口
    private void sendData() {
        identityInfo.sendPostRequest(Urls.ENTERPRISEAUTH, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(IdentityCompanyActivity.this, jsonObject.getString("msg"));
                        baseFinish();
                    } else {
                        Tools.showToast(IdentityCompanyActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(IdentityCompanyActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityCompanyActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }


    private void getData() {
        enterpriseAuthInfo.sendPostRequest(Urls.ENTERPRISEAUTHINFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONObject object = jsonObject.optJSONObject("data");
                        enterprise_name2 = object.optString("enterprise_name");
                        org_code2 = object.optString("org_code");
                        business_term2 = object.optString("business_term2");
                        business_license2 = object.optString("business_license");
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
                            //  identity_img_face.setScaleType(business_license2);
                            imageLoader.DisplayImage(Urls.Endpoint3 + business_license2, identity_img_face, R.mipmap.jgrz_button_yyzz);
                        }
                    } else {
                        Tools.showToast(IdentityCompanyActivity.this, jsonObject.getString("msg"));
                    }
                } catch (
                        JSONException e)

                {
                    Tools.showToast(IdentityCompanyActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityCompanyActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    public void startThread(final String imgUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                decrypt(imgUrl);
            }
        }).start();
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //营业执照副本
            case R.id.identity_img_face: {//权限获取(正面)
                Intent intent = new Intent(IdentityCompanyActivity.this, Camerase.class);
                intent.putExtra("maxTake", 1);
                intent.putExtra("returnThumbnail", 1024);
                intent.putExtra("identityview", true);
                intent.putExtra("state", 1);
                intent.putExtra("storeid", "");
                startActivityForResult(intent, 1);
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

    private void unIdBind() {//未绑定状态
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
        if (faceList.isEmpty()) {
            Tools.showToast(IdentityCompanyActivity.this, "请拍摄营业执照正面照片");
            return;
        } else {
            list.add(faceList.get(faceList.size() - 1));
        }
        sendOSSData(list.get(0));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppInfo.REQUEST_CODE_ASK_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCameraPermission();
            } else {
                Tools.showToast(IdentityCompanyActivity.this, "拍照权限获取失败");
                baseFinish();
            }
        }
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, AppInfo
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

    public void decrypt(String imgUrl) {
        String[] ss = imgUrl.trim().split("/");
        File file = new File(FileCache.getDirForCamerase(this) + "/" + ss[ss.length - 1]);
        if (file.isFile() && file.exists()) {
            handler.sendEmptyMessage(2);
        } else {
            try {
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                fileOutputStream = new FileOutputStream(file);
                if (credentialProvider == null)
                    credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                            "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
                if (oss == null) {
                    oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
                }
                GetObjectRequest request = new GetObjectRequest(bucketName, imgUrl);
                inputStream = oss.getObject(request).getObjectContent();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    for (int i = 0; i < len; i++) {
                        buffer[i] = (byte) (255 - buffer[i]);
                    }
                    fileOutputStream.write(buffer, 0, len);
                }
                handler.sendEmptyMessage(2);
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                CustomProgressDialog.Dissmiss();
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {//上传完成
                    enterprise_name = identitytext_name.getText().toString().trim();
                    business_term = identitytext_qixian.getText().toString().trim();
                    org_code = identitytext_code.getText().toString().trim();
                    photourl = photourl.trim().replaceAll("\\[", "").replaceAll("\\]", "");
                    sendData();
                }
                break;
                case 2: {//显示图片
                    String[] str = business_license2.split("/");
                    identity_img_face.setImageBitmap(getBitmap(str[str.length - 1]));
                    CustomProgressDialog.Dissmiss();
                }
                break;
            }
            Tools.d("handler");
        }
    };


    public Bitmap getBitmap(String fileName) {
        Bitmap bitmap = null;
        File file = new File(FileCache.getDirForCamerase(this), fileName);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 10;
            bitmap = BitmapFactory.decodeStream(fis, null, options);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
            String originalPath = systemDBHelper.searchForOriginalpath(s);
            File file = new File(originalPath);
            if (!file.exists() || !file.isFile()) {
                if (TextUtils.isEmpty(photourl)) {
                    photourl = s;
                } else {
                    photourl += "," + s;
                }
                handler.sendEmptyMessage(1);
                return;
            }
            String objectKey = file.getName();
            objectKey = objectKey + ".jpg";
            objectKey = Urls.Businesslicense + "/" + objectKey;
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
            PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, originalPath);
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
                    for (int i = 0; i < faceList.size(); i++) {
                        systemDBHelper.deletePictureForThum(faceList.get(i));
                        systemDBHelper.deletePicture(faceList.get(i));
                    }
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


    private ArrayList<String> faceList = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String path;
            if (requestCode == 1) {
                path = data.getStringExtra("path");
                Tools.d("path:" + path);
                faceList.add(path);
                identity_img_face.setImageBitmap(Tools.getBitmap(path, 400, 400));
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
