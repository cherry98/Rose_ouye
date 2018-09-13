package com.orange.oy.activity.shakephoto_320;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.orange.oy.R;
import com.orange.oy.activity.bigchange.MyDetailInfoActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.identity_img_face;
import static com.orange.oy.R.id.oumidetail_listview;
import static com.orange.oy.R.id.teamtaskproject_headmoney;
import static com.orange.oy.base.Tools.getBitmap;

/**
 * V3.20 我的商户
 */
public class MyCommercialActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private ImageView iv_logo;
    private ImageLoader imageLoader;
    private Bitmap bitmap;
    private NetworkConnection merchantLogoUpload, myMerchant;

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.titleview);
        appTitle.settingName("我的商户");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        merchantLogoUpload = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (bitmap != null) {
                    params.put("logo_url", photourl);
                }
                params.put("merchant_id", merchant_id);
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyCommercialActivity.this));
                return params;
            }
        };
        merchantLogoUpload.setIsShowDialog(true);

        myMerchant = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyCommercialActivity.this));
                return params;
            }
        };
    }

    private TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycommercial);
        initTitle();
        initNetworkConnection();
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);  //logo

        imageLoader = new ImageLoader(this);

        getData();
        findViewById(R.id.iv_logo).setOnClickListener(this);
        findViewById(R.id.lin_updateInformation).setOnClickListener(this);
        findViewById(R.id.lin_account).setOnClickListener(this);
        findViewById(R.id.lin_Present).setOnClickListener(this);
        findViewById(R.id.lin_address).setOnClickListener(this);
        findViewById(R.id.lin_onlineshop).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_logo: {
                SelectPhotoDialog.showPhotoSelecter(MyCommercialActivity.this, true, takeListener, pickListener);
            }
            break;
            case R.id.lin_updateInformation: {  //修改商户信息
                Intent intent = new Intent(this, ShopInformationActivity.class);
                intent.putExtra("merchant_id", merchant_id); //商户id
                startActivity(intent);

            }
            break;
            case R.id.lin_account: {  //商户账号
                Intent intent = new Intent(this, ShopaccountActivity.class);
                intent.putExtra("merchant_id", merchant_id); //商户id
                startActivity(intent);
            }
            break;
            case R.id.lin_Present: { //礼品库管理
                Intent intent = new Intent(this, PresentManagementActivity.class);
                intent.putExtra("merchant_id", merchant_id); //商户id
                startActivity(intent);
            }
            break;
            case R.id.lin_address: { //店铺地址管理
                Intent intent = new Intent(this, StoreAddressActivity.class);
                intent.putExtra("merchant_id", merchant_id); //商户id
                startActivity(intent);
            }
            break;
            case R.id.lin_onlineshop: { //网店管理
                Intent intent = new Intent(this, StoreListActivity.class);
                intent.putExtra("merchant_id", merchant_id); //商户id
                startActivity(intent);
            }
            break;
        }
    }


    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto
                    (MyCommercialActivity.this).getPath() + "/myImg0.jpg")));
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

    private String brand_name, logo_url, merchant_id;

    private void getData() {
        myMerchant.sendPostRequest(Urls.MyMerchant, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.optJSONObject("data");
                            brand_name = jsonObject.getString("brand_name"); //品牌名称
                            logo_url = jsonObject.getString("logo_url");  // logo地址
                            merchant_id = jsonObject.getString("merchant_id"); // 商户id
                            if (!Tools.isEmpty(logo_url)) {
                                imageLoader.DisplayImage(Urls.Endpoint3 + logo_url, iv_logo);
                            }
                            tv_name.setText(brand_name);
                        }

                    }
                } catch (JSONException e) {
                    Tools.showToast(MyCommercialActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyCommercialActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void sendData() {
        merchantLogoUpload.sendPostRequest(Urls.MerchantLogoUpload, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject job = new JSONObject(s);
                    int code = job.getInt("code");
                    if (code == 200) {
                        //
                        Tools.showToast(MyCommercialActivity.this, "更换成功~");
//
                    } else {
                        Tools.showToast(MyCommercialActivity.this, job.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(MyCommercialActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                SelectPhotoDialog.dissmisDialog();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                SelectPhotoDialog.dissmisDialog();
                Tools.showToast(MyCommercialActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在修改...");
    }

    public void onStop() {
        super.onStop();
        if (merchantLogoUpload != null) {
            merchantLogoUpload.stop(Urls.UpateUser);
        }
    }

    @Override
    public void onBack() {
        finish();
    }

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
                        if (Build.MANUFACTURER.equals("HUAWEI")) {
                            intent.putExtra("aspectX", 9998);
                            intent.putExtra("aspectY", 9999);
                        } else {
                            intent.putExtra("aspectX", 1);
                            intent.putExtra("aspectY", 1);
                        }
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
            Tools.showToast(MyCommercialActivity.this, "拍照方式错误");
            return;
        }
        bitmap = imageZoom(getBitmap(path), 50);
        if (bitmap == null) {
            Tools.showToast(MyCommercialActivity.this, "头像设置失败");
            return;
        }

        Tools.d("path:" + path);
        faceList.add(path);
        list.add(path);
        iv_logo.setImageBitmap(getBitmap(path, 400, 400));
        iv_logo.setTag(path);
        sendOSSData(list.get(0));
    }


    private Bitmap imageZoom(Bitmap bitMap, double maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        double mid = b.length / 1024;
        if (mid > maxSize) {
            double i = mid / maxSize;
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }

    private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
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
                /*case 2: {//显示图片
                    String[] str = business_license2.split("/");
                    iv_logo.setImageBitmap(getBitmap(str[str.length - 1]));
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
    private boolean isUpdata = false;
    private String photourl;

    public void sendOSSData(String s) {
        photourl = "";
        try {
            CustomProgressDialog.showProgressDialog(this, "正在上传");
            Tools.d(s);
            isUpdata = true;
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
