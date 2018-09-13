package com.orange.oy.activity.bigchange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.orange.oy.R;
import com.orange.oy.activity.AlbumActivity;
import com.orange.oy.activity.Camerase;
import com.orange.oy.activity.CloseTaskitemPhotographyNextYActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.mydetail_city;
import static com.orange.oy.R.id.title_exit;

/**
 * 头像设置-选择更改
 */
public class ModifyheadActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private ImageView mydetail_img;
    private ImageLoader imageLoader;
    private Bitmap bitmap;
    private NetworkConnection sendData;

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (bitmap != null) {
                    params.put("img", Tools.bitmapToBase64(bitmap));
                }
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ModifyheadActivity.this));
                return params;
            }
        };
        sendData.setIsShowDialog(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyhead);
        initNetworkConnection();
        mydetail_img = (ImageView) findViewById(R.id.iv_pics);
        TextView title_exit = (TextView) findViewById(R.id.title_exit);
        String imgUrl = AppInfo.getUserImagurl(this);
        imageLoader = new ImageLoader(this);
        imageLoader.DisplayImage(imgUrl, mydetail_img, R.mipmap.grxx_icon_mrtx);
        findViewById(R.id.title_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPhotoDialog.showPhotoSelecter(ModifyheadActivity.this, true, takeListener, pickListener);
            }
        });
    }

    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto
                    (ModifyheadActivity.this).getPath() + "/myImg0.jpg")));
            intent.putExtra("camerasensortype", 1);
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForTake);
        }
    };

    //相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        public void onClick(View v) {
//            SelectPhotoDialog.dissmisDialog();
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.setType("image/*");
//            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("outputX", 200);
//            intent.putExtra("outputY", 200);
//            intent.putExtra("return-data", false);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(ModifyheadActivity.this)
//                    .getPath() + "/myImg.jpg")));
//            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//            intent.putExtra("noFaceDetection", true);
//            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
        }
    };

    private void sendData() {
        sendData.sendPostRequest(Urls.UpateUser, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject job = new JSONObject(s);
                    int code = job.getInt("code");
                    if (code == 200) {
                        Tools.showToast(ModifyheadActivity.this, job.getString("msg"));
                        String[] strings = AppInfo.getUserdistrics(ModifyheadActivity.this);
                        AppInfo.setUserinfo2(ModifyheadActivity.this, AppInfo.getUserName(ModifyheadActivity.this),
                                strings[1], strings[0], strings[2],
                                null, null);
                        Object ob = mydetail_img.getTag();
                        String filePath = "";
                        if (ob != null) {
                            filePath = ob.toString();
                            AppInfo.setUserImg(ModifyheadActivity.this, filePath);
                            MyDetailInfoActivity.isRefresh = true;
                            finish();
                        }
//                        noEdit();
                    } else {
                        Tools.showToast(ModifyheadActivity.this, job.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(ModifyheadActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                SelectPhotoDialog.dissmisDialog();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                SelectPhotoDialog.dissmisDialog();
                Tools.showToast(ModifyheadActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在修改...");
    }

    public void onStop() {
        super.onStop();
        if (sendData != null) {
            sendData.stop(Urls.UpateUser);
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

    private void settingImgPath(String path) {
        if (!new File(path).isFile()) {
            Tools.showToast(ModifyheadActivity.this, "拍照方式错误");
            return;
        }
        bitmap = imageZoom(Tools.getBitmap(path), 50);
        if (bitmap == null) {
            Tools.showToast(ModifyheadActivity.this, "头像设置失败");
            return;
        }
        mydetail_img.setImageBitmap(bitmap);
        mydetail_img.setTag(path);
        onSearch();
    }

    public void onSearch() {
        if (bitmap == null) {
//            noEdit();
        } else {
            sendData();
        }
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
}
