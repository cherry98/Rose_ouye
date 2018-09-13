package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.orange.oy.R;
import com.orange.oy.activity.Camerase;
import com.orange.oy.activity.shakephoto_318.SetPrizeActivity;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.identitytext_code;
import static com.orange.oy.R.id.identitytext_name;
import static com.orange.oy.R.id.identitytext_qixian;
import static com.orange.oy.R.id.iv_pic;
import static com.orange.oy.base.Tools.getBitmap;

/**
 * V3.20  添加礼品界面
 */

public class AddPresentActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.identity_title);
        appTitle.settingName("添加礼品");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        addGift = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddPresentActivity.this));
                params.put("gift_name", ed_name.getText().toString().trim());
                params.put("gift_money", ed_money.getText().toString().trim()); //礼品价值金额【必传】
                params.put("merchant_id", merchant_id); //
                if (bitmap != null) {
                    params.put("img_url", photourl);  //  	图片地址【必传】
                }
                return params;
            }
        };
    }


    private String gift_name, gift_money;

    private ImageLoader imageLoader;
    private ImageView identity_img_face;
    private EditText ed_name, ed_money;
    private NetworkConnection addGift;
    private SystemDBHelper systemDBHelper;
    private boolean isUpdata = false;
    private String enterprise_name2, org_code2, business_license2, business_term2;//回显数据
    private String merchant_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_present);
        merchant_id = getIntent().getStringExtra("merchant_id");
        imageLoader = new ImageLoader(getBaseContext());
        systemDBHelper = new SystemDBHelper(this);
        initTitle();
        initNetworkConnection();
        initView();
    }

    private void initView() {
        ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 20);
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_money = (EditText) findViewById(R.id.ed_money);
        identity_img_face = (ImageView) findViewById(R.id.identity_img_face);
        findViewById(R.id.identity_button).setOnClickListener(this);
        identity_img_face.setOnClickListener(this);
    }

    //	礼品添加提交接口
    private void sendData() {
        addGift.sendPostRequest(Urls.AddGift, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {

                        PresentManagementActivity.isRefresh = true;
                        baseFinish();

                        Tools.showToast(AddPresentActivity.this, jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(AddPresentActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddPresentActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddPresentActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    private String name, money, photourl;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //礼品图片
            case R.id.identity_img_face: {
                SelectPhotoDialog.dissmisDialog();
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
            }
            break;

            //提交
            case R.id.identity_button: {
                name = ed_name.getText().toString().trim();
                money = ed_money.getText().toString().trim();

                if (isUpdata) {
                    Tools.showToast(this, "正在上传");
                    return;
                }
                if (name == null || "".equals(name)) {
                    Tools.showToast(this, "请输入礼品名称");
                    return;
                }
                if (money == null || "".equals(money)) {
                    Tools.showToast(this, "请输入礼品价值的金额");
                    return;
                }
               /* BigDecimal bigDecimal = new BigDecimal(money);
                if (bigDecimal.equals(BigDecimal.ONE)) {
                    Tools.showToast(AddPresentActivity.this, "礼品价值金额要大于0哦~");
                    return;
                }*/

                if (!(Tools.StringToDouble(money) > 1)) {
                    Tools.showToast(this, "请输入大于1的礼品价值的金额");
                    return;
                }
                if (TextUtils.isEmpty(photourl)) {
                    Tools.showToast(this, "请上传礼品图片");
                    return;
                }
                sendData();

            }
            break;
        }
    }

    private Bitmap bitmap;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppInfo.MyDetailRequestCodeForPick: {
                    Uri uri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 400);
                    intent.putExtra("aspectY", 399);
                    intent.putExtra("outputX", 400);
                    intent.putExtra("outputY", 399);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                            .getPath() + "/myImg.jpg")));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg.jpg";
                    settingImgPath(filePath);
                    sendOSSData(filePath);
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//
//    private ArrayList<String> faceList = new ArrayList<>();
//    private ArrayList<String> list = new ArrayList<>();

    private void settingImgPath(String path) {
        if (!new File(path).isFile()) {
            Tools.showToast(AddPresentActivity.this, "拍照方式错误");
            return;
        }
        bitmap = Tools.imageZoom(getBitmap(path), 50);
        if (bitmap == null) {
            Tools.showToast(AddPresentActivity.this, "设置失败");
            return;
        }

        Tools.d("path:" + path);
        identity_img_face.setImageBitmap(getBitmap(path, 400, 400));
        identity_img_face.setTag(path);
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
                    isUpdata = false;
                    Tools.d(result.getStatusCode() + "");
                    photourl = photourl.trim().replaceAll("\\[", "").replaceAll("\\]", "");
                    CustomProgressDialog.Dissmiss();
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
