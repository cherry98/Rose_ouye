package com.orange.oy.activity.scan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
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
 * V3.9身份证绑定页面
 * V.3.13修改为上传一张图片 上传三张图片可查看V3.12
 */

public class IdentityVerActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.identity_title);
        appTitle.settingName("账号认证");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        bindIdCard = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(IdentityVerActivity.this));
                params.put("truename", name);
                params.put("sex", sex);
                params.put("idcardnum", idcardnum);
                params.put("photourl", photourl);
                return params;
            }
        };
        bindIdCard.setIsShowDialog(true);
        identityInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(IdentityVerActivity.this));
                return params;
            }
        };
        identityInfo.setIsShowDialog(true);
    }

    private void initView() {
        ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 20);
        identitytext_name = (EditText) findViewById(R.id.identitytext_name);
        identitytext_sex = (RadioGroup) findViewById(R.id.identitytext_sex);
        identitytext_female = (RadioButton) findViewById(R.id.identitytext_female);
        identitytext_male = (RadioButton) findViewById(R.id.identitytext_male);
        identity_img_face = (ImageView) findViewById(R.id.identity_img_face);
        identitytext_id = (EditText) findViewById(R.id.identitytext_id);
        identity_pass = findViewById(R.id.identity_pass);
        identity_ispass = (TextView) findViewById(R.id.identity_ispass);
        identity_remark = (TextView) findViewById(R.id.identity_remark);
        findViewById(R.id.identity_button).setOnClickListener(this);
        identity_img_face.setOnClickListener(this);
        identitytext_sex.setOnCheckedChangeListener(this);
        int height = (int) (Tools.dipToPx(IdentityVerActivity.this, 280) / (674f / 452f));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) identity_img_face.getLayoutParams();
        lp.height = height;
        identity_img_face.setLayoutParams(lp);
    }

    private ImageView identity_img_face;
    private EditText identitytext_name, identitytext_id;
    private NetworkConnection bindIdCard, identityInfo;
    private SystemDBHelper systemDBHelper;
    private boolean isUpdata = false;
    private RadioGroup identitytext_sex;
    private RadioButton identitytext_female, identitytext_male;
    private View identity_pass;//审核状态View
    private TextView identity_ispass, identity_remark;
    private String name, sex, idcardnum, photourl;//上传数据
    private String ispass, bind_idcard, data_sex, data_name, data_id, data_face;//回显数据
    private boolean isJudge;//是否跳支付宝绑定页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_ver);
        systemDBHelper = new SystemDBHelper(this);
        initTitle();
        isJudge = getIntent().getBooleanExtra("isJudge", false);
        initNetworkConnection();
        checkCameraPermission();
        initView();
        getData();
    }

    private void getData() {
        identityInfo.sendPostRequest(Urls.IdentityInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        bind_idcard = jsonObject.getString("bind_idcard");
                        if ("1".equals(bind_idcard)) {//已绑定
                            data_sex = jsonObject.getString("user_sex");
                            data_id = jsonObject.getString("idcardnum");
                            data_name = jsonObject.getString("true_name");
                            identitytext_id.setText(data_id);
                            identitytext_name.setText(data_name);
                            if ("男".equals(data_sex)) {
                                identitytext_male.setChecked(true);
                            } else {
                                identitytext_female.setChecked(true);
                            }
                            identity_pass.setVisibility(View.VISIBLE);
                            ispass = jsonObject.getString("is_pass");
                            if (!"1".equals(ispass)) {//审核通过不需要回显照片
                                //身份证照片
                                JSONArray jsonArray = jsonObject.optJSONArray("idcard_photo");
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    data_face = jsonArray.getString(0);
                                    startThread(data_face);
                                }
                            } else {
                                CustomProgressDialog.Dissmiss();
                            }
                            if ("1".equals(ispass)) {//审核通过不能修改
                                findViewById(R.id.identity_button).setVisibility(View.GONE);
                                identitytext_name.setFocusable(false);
                                identitytext_id.setFocusable(false);
                                identitytext_female.setEnabled(false);
                                identitytext_male.setEnabled(false);
                                findViewById(R.id.identity_photoly).setVisibility(View.GONE);
                            } else {//审核未通过
                                findViewById(R.id.identity_photoly).setVisibility(View.VISIBLE);
                                identitytext_name.setFocusable(true);
                                identitytext_id.setFocusable(true);
                                identitytext_female.setEnabled(true);
                                identitytext_male.setEnabled(true);
                                identity_img_face.setOnClickListener(IdentityVerActivity.this);
                            }
                            if ("0".equals(ispass)) {
                                identity_ispass.setText("待审核");
                                identity_remark.setVisibility(View.GONE);
                            } else if ("1".equals(ispass)) {
                                identity_ispass.setText("审核通过");
                                identity_remark.setVisibility(View.GONE);
                            } else if ("2".equals(ispass)) {
                                identity_ispass.setText("审核不通过");
                                identity_remark.setVisibility(View.VISIBLE);
                                identity_remark.setText("不通过原因：\n" + jsonObject.getString("remark"));
                            }
                        } else {
                            identity_ispass.setText("未认证");
                            identity_remark.setVisibility(View.GONE);
                            CustomProgressDialog.Dissmiss();
                        }
                    } else {
                        Tools.showToast(IdentityVerActivity.this, jsonObject.getString("msg"));
                        CustomProgressDialog.Dissmiss();
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(IdentityVerActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityVerActivity.this, getResources().getString(R.string.network_volleyerror));
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
            case R.id.identity_img_face: {//权限获取(正面)
                Intent intent = new Intent(IdentityVerActivity.this, Camerase.class);
                intent.putExtra("maxTake", 1);
                intent.putExtra("returnThumbnail", 1024);
                intent.putExtra("identityview", true);
                intent.putExtra("storeid", "");
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.identity_button: {
                name = identitytext_name.getText().toString().trim();
                idcardnum = identitytext_id.getText().toString().trim();
                if ("1".equals(bind_idcard) && !"1".equals(ispass)) {//已绑定过但是未通过
                    idBinded();
                } else {//未绑定过
                    unIdBind();
                }
            }
            break;
        }
    }

    private void unIdBind() {//未绑定状态
        if (isUpdata) {
            Tools.showToast(this, "正在上传");
            return;
        }
        if (name == null || "".equals(name)) {
            Tools.showToast(this, "请填写您的姓名");
            return;
        }
        if (sex == null || "".equals(sex)) {
            Tools.showToast(this, "请填写您的性别");
            return;
        }
        if (idcardnum == null || "".equals(idcardnum)) {
            Tools.showToast(this, "请填写您的身份证号码");
            return;
        }
        if (idcardnum.length() != 18) {
            Tools.showToast(this, "身份证号码填写有误");
            return;
        }
        if (faceList.isEmpty()) {
            Tools.showToast(IdentityVerActivity.this, "请拍摄身份证正面照片");
            return;
        } else {
            list.add(faceList.get(faceList.size() - 1));
        }
        sendOSSData(list.get(0));
    }

    private void idBinded() {//已绑定过
        if (data_id.equals(idcardnum) && data_name.equals(name) && data_sex.equals(sex)
                && faceList.isEmpty()) {
            Tools.showToast(this, "提交成功");
            baseFinish();
            return;
        }
        if (isUpdata) {
            Tools.showToast(this, "正在上传");
            return;
        }
        if (name == null || "".equals(name)) {
            Tools.showToast(this, "请填写您的姓名");
            return;
        }
        if (sex == null || "".equals(sex)) {
            Tools.showToast(this, "请填写您的性别");
            return;
        }
        if (idcardnum == null || "".equals(idcardnum)) {
            Tools.showToast(this, "请填写您的身份证号码");
            return;
        }
        if (idcardnum.length() != 18) {
            Tools.showToast(this, "身份证填写有误");
            return;
        }
        if (!faceList.isEmpty()) {
            list.add(faceList.get(faceList.size() - 1));
        } else {
            list.add(data_face);
        }
        if (faceList.isEmpty()) {
            photourl = list.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "");
            photourl = photourl.replaceAll(" ", "");
            sendData();
        } else {
            sendOSSData(list.get(0));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppInfo.REQUEST_CODE_ASK_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCameraPermission();
            } else {
                Tools.showToast(IdentityVerActivity.this, "拍照权限获取失败");
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
                    name = identitytext_name.getText().toString();
                    idcardnum = identitytext_id.getText().toString();
                    photourl = photourl.trim().replaceAll("\\[", "").replaceAll("\\]", "");
                    sendData();
                }
                break;
                case 2: {//显示图片
                    String[] str = data_face.split("/");
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
            objectKey = Urls.Identitycard2 + "/" + objectKey;
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
//                task.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.Dissmiss();
        }
    }

    private void sendData() {
        bindIdCard.sendPostRequest(Urls.BindIdCard, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //判断是否需要跳支付宝页面
                        if (isJudge) {
                            Intent intent = new Intent(IdentityVerActivity.this, IdentityActivity.class);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }
                        baseFinish();
                    } else {
                        Tools.showToast(IdentityVerActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(IdentityVerActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityVerActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
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
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (identitytext_female.getId() == checkedId) {//女
            sex = "女";
        } else if (identitytext_male.getId() == checkedId) {//男
            sex = "男";
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
