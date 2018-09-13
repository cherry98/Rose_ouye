package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.orange.oy.adapter.ImageResetAdapter2ViewHold;
import com.orange.oy.adapter.ImageUpAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TeamSpecialtyInfo;
import com.orange.oy.info.shakephoto.LocalPhotoInfo;
import com.orange.oy.info.shakephoto.PhotoListBean;
import com.orange.oy.info.shakephoto.ShakePhotoInfo2;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.FlowLayoutView;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.MyImageView;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * V3.18  自由拍上传照片页面
 */
public class UploadPicturesActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskphoto_title);
        appTitle.settingName("上传照片");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        scenePhotoUpload = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(UploadPicturesActivity.this));
                params.put("type", "1");    // App传1，小程序传2【必传】
                params.put("photo_list", photo_list); // 照片信息【必传】
                params.put("ai_id", ai_id);  //活动id【必传】
                params.put("cat_id", cat_id); // 主题分类id【必传】
                params.put("key_concent", nums);  //  标签，多个以逗号分隔【必传】
                if (!Tools.isEmpty(taskphoto_desc.getText().toString())) {
                    params.put("comment", taskphoto_desc.getText().toString()); //评论
                }
                Tools.d("tag", params.toString());
                return params;
            }
        };
    }

    private String photo_list; //json参数
    private EditText taskphoto_name, taskphoto_desc;
    private MyGridView taskphoto_gridview;
    private ImageUpAdapter imageResetAdapter;
    private int position;
    private LinearLayout lin_activity_classify, lin_theme_classify;
    private TextView tv_activity_classify, tv_theme_classify;
    private NetworkConnection scenePhotoUpload;
    private LinearLayout lin_teamSpeciality; //标签tag
    private String[] str;
    private FlowLayoutView createcorps_special;
    private ImageLoader imageLoader;
    private TextView tv_submit;
    private ArrayList<PhotoListBean> photoList = new ArrayList<>();
    private AppDBHelper appDBHelper;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pictures);
        appDBHelper = new AppDBHelper(this);
        initTitle();
        initView();
        initNetworkConnection();
        Intent data = getIntent();
        photoList = (ArrayList<PhotoListBean>) data.getSerializableExtra("photoList");
        if (null != photoList) {
            BeJsonString();
            for (int i = 0; i < photoList.size(); i++) {
                if (Tools.isEmpty(photoList.get(i).getProvince())) {
                    local_photo = "1";
                }
            }
        }


        imageResetAdapter = new ImageUpAdapter(this, photoList);
        taskphoto_gridview.setAdapter(imageResetAdapter);
        taskphoto_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoListBean taskPhotoInfo = photoList.get(position);
                if (imageLoader == null) {
                    imageLoader = new ImageLoader(UploadPicturesActivity.this);
                }
                PhotoView imageView = new PhotoView(UploadPicturesActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageLoader.DisplayImage(taskPhotoInfo.getFile_url(), imageView);
                SelecterDialog.showView(UploadPicturesActivity.this, imageView);
            }
        });
//        handler.sendEmptyMessage(2);
    }

    private String mphoto_list; //活动主题等需要的参数

    private void BeJsonString() {
//        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < photoList.size(); i++) {
                PhotoListBean photoListBean = photoList.get(i);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("province", photoListBean.getProvince());
                jsonObject1.put("city", photoListBean.getCity());
                jsonObject1.put("county", photoListBean.getCounty());
                jsonObject1.put("address", photoListBean.getAddress());
                jsonObject1.put("latitude", photoListBean.getLatitude());
                jsonObject1.put("longitude", photoListBean.getLongitude());
                jsonArray.put(jsonObject1);
            }
            // jsonObject.put("photo_list", jsonArray);
            mphoto_list = jsonArray.toString();
        } catch (JSONException e) {
            Tools.showToast(this, "地址存储失败");
        }
    }

    private void BeJsonStringTwo() {
//        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < photoList.size(); i++) {
                PhotoListBean photoListBean = photoList.get(i);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("oss_name", photoListBean.getOss_name());
                jsonObject1.put("show_name", photoListBean.getShow_name());
                jsonObject1.put("file_type", "jpg");
                jsonObject1.put("file_url", photoListBean.getUpUrl());
                jsonObject1.put("province", photoListBean.getProvince());
                jsonObject1.put("city", photoListBean.getCity());
                jsonObject1.put("county", photoListBean.getCounty());
                jsonObject1.put("area", photoListBean.getArea());
                jsonObject1.put("address", photoListBean.getAddress());
                jsonObject1.put("latitude", photoListBean.getLatitude());
                jsonObject1.put("longitude", photoListBean.getLongitude());
                jsonObject1.put("dai_id", photoListBean.getDai_id());
                jsonObject1.put("show_address", photoListBean.getShow_address());
                jsonArray.put(jsonObject1);
            }
//              jsonObject.put("photo_list", jsonArray);
            photo_list = jsonArray.toString();
        } catch (JSONException e) {
            Tools.showToast(this, "图片信息存储失败zzz");
        }
    }

    public void onBackPressed() {
        if (!canUpdata) {
            ConfirmDialog.showDialog(UploadPicturesActivity.this, "提示", 1, "页面内容还没有保存确认返回吗？", "取消", "确认", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                }

                public void rightClick(Object object) {
                    baseFinish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }


    private void initView() {
        lin_teamSpeciality = (LinearLayout) findViewById(R.id.lin_teamSpeciality);
        taskphoto_desc = (EditText) findViewById(R.id.taskphoto_desc);  //评论
        taskphoto_gridview = (MyGridView) findViewById(R.id.taskphoto_gridview);
        tv_activity_classify = (TextView) findViewById(R.id.tv_activity_classify);
        tv_theme_classify = (TextView) findViewById(R.id.tv_theme_classify);
        lin_activity_classify = (LinearLayout) findViewById(R.id.lin_activity_classify);
        lin_theme_classify = (LinearLayout) findViewById(R.id.lin_theme_classify);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        createcorps_special = (FlowLayoutView) findViewById(R.id.createcorps_special);

        lin_activity_classify.setOnClickListener(this);
        lin_theme_classify.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
    }


    public void onBack() {
        if (!canUpdata) {
            ConfirmDialog.showDialog(this, "提示", 1, "页面内容还没有保存确认返回吗？", "取消", "确认", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                }

                public void rightClick(Object object) {
                    baseFinish();
                }
            });
        } else {
            baseFinish();
        }
    }

    private boolean isUpfinish;  //是否上传完成
    private String local_photo = "0"; //是否有本地相册选择的照片，1为有，0为没有

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_activity_classify: { //活动分类
                Intent intent = new Intent(this, ThemeClassifyActivity3_18.class);
                intent.putExtra("photo_list", mphoto_list);
                intent.putExtra("local_photo", local_photo);
                startActivityForResult(intent, 3);
            }
            break;
            case R.id.lin_theme_classify: {  //活动主题
                if (Tools.isEmpty(tv_activity_classify.getText().toString())) {
                    Tools.showToast(this, "请先选择活动分类");
                    return;
                }
                Intent intent = new Intent(this, ThemeActivity.class);
                intent.putExtra("photo_list", mphoto_list);
                intent.putExtra("cat_id", cat_id);
                intent.putExtra("local_photo", local_photo);
                intent.putExtra("local_photo", local_photo);
                startActivityForResult(intent, 4);
            }
            break;
            case R.id.tv_submit: {//页面提交按钮
                if (Tools.isEmpty(tv_activity_classify.getText().toString().trim())) {
                    Tools.showToast(this, "请选择活动分类");
                    return;
                }
                if (Tools.isEmpty(tv_theme_classify.getText().toString().trim())) {
                    Tools.showToast(this, "请选择活动主题");
                    return;
                }
                if (0 == nums.split(",").length || TextUtils.isEmpty(nums)) {
                    nums = "";
                    Tools.showToast(this, "请选择标签~");
                    return;
                }
                tv_submit.setText("等待图片上传完成...");
                tv_submit.setOnClickListener(null);
                if (isUpfinish) {
                    handler.sendEmptyMessage(3);
                    return;
                }
                handler.sendEmptyMessage(2);
//                if (!isUpfinish) {
//                    Tools.showToast(this, "图片还未上传完成");
//                    return;
//                }
//                if (!isStarted) {
//                    handler.sendEmptyMessage(2);
//                }
//                int index = 0;
//                for (int i = 0; i < photoList.size(); i++) {
//                    if (photoList.get(i).isUped()) {
//                        index++;
//                    }
//                }
//                if (index == photoList.size()) {
//                    isUpfinish = true;
////                        handler.sendEmptyMessage(3);
//                } else {
//                    handler.sendEmptyMessage(2);
//                    canUpdata = true;
//                }
//                BeJsonStringTwo();
//                onCommit();
            }
            break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(1);
            handler.removeMessages(2);
            handler.removeMessages(3);
        }
        if (photoList != null) {
            photoList.clear();
        }
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask<PutObjectResult> task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;
    private boolean canUpdata = true;//是否可以上传
    private boolean isStarted;


    public void sendOSSData(final int position) {
        isStarted = true;
        if (!canUpdata) {
            return;
        }
        canUpdata = false;
        try {
            PhotoListBean plist = photoList.get(position);
            File file = new File(plist.getFile_url());
            String objectKey = file.getName();
            objectKey = file.hashCode() + "_" + objectKey;
            plist.setOss_name(file.hashCode() + "_" + objectKey);
            objectKey = Urls.EndpointDir + "/" + objectKey;

            //=========== 赋值（剩余参数）
            // plist.setUpUrl(Urls.Endpoint3 + objectKey);
            plist.setUpUrl(objectKey);
            plist.setShow_name(file.hashCode() + "_" + objectKey);
            // plist.setDai_id();  //"dai_id":"用户自定义地址ID，当地址为自己创建的时候需要有"
            plist.setFile_type("jpg");  //文件格式
            plist.setShow_address("1");   //   "show_address":"是否显示地址（0：不显示；1：显示）


            if (credentialProvider == null)
                credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                        "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
            if (oss == null)
                oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, plist.getFile_url());
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    int rate = (int) (currentSize * 100 / totalSize);
                    Message message = new Message();
                    message.arg1 = position;
                    message.arg2 = rate;
                    message.what = 1;
                    handler.sendMessage(message);
                    //Tools.d("currentSize: " + currentSize + " totalSize: " + totalSize);
                }
            });
            task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Tools.d("上传成功flag" + photoList.get(position).getFile_url());
                    photoList.get(position).setUped(true);
                    int index = 0;
                    for (int i = 0; i < photoList.size(); i++) {
                        if (photoList.get(i).isUped()) {
                            index++;
                        }
                    }
                    if (index == photoList.size()) {
                        isUpfinish = true;
                        handler.sendEmptyMessage(3);
                    } else {
                        handler.sendEmptyMessage(2);
                        canUpdata = true;
                    }
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    isStarted = false;
                    Tools.d("onFailure");
                    CustomProgressDialog.Dissmiss();
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                        ConfirmDialog.showDialog(UploadPicturesActivity.this, "提示", 1, "网络异常，点击图片重新上传", "", "我知道了",
                                null, true, null).goneLeft();
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
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.Dissmiss();
        }
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {//正在上传
                    int index = taskphoto_gridview.getFirstVisiblePosition();
                    View view = taskphoto_gridview.getChildAt(msg.arg1 - index);
                    if (view == null || view.getTag() == null) {
                        return;
                    }
                    ImageResetAdapter2ViewHold viewHolder = (ImageResetAdapter2ViewHold) view.getTag();
                    int rate = msg.arg2;
                    if (rate == 0) {
                        // Tools.d("position:" + msg.arg1 + "url等待上传:" + photoList.get(msg.arg1).getFile_url());
                        viewHolder.itemimage_img2.setText(rate + "%" + "\n等待上传");
                        viewHolder.itemimage_img2.setAlpha(0.4f);
                    } else if (rate == 100) {
                        //  Tools.d("position:" + msg.arg1 + "url上传成功:" + photoList.get(msg.arg1).getFile_url());
                        viewHolder.itemimage_img2.setText(rate + "%" + "\n上传成功");
                        viewHolder.itemimage_img2.setAlpha(1f);
                    } else {
                        // Tools.d("position:" + msg.arg1 + "url正在上传:" + photoList.get(msg.arg1).getFile_url());
                        viewHolder.itemimage_img2.setText(rate + "%" + "\n正在上传");
                        viewHolder.itemimage_img2.setAlpha(0.4f);
                    }
                }
                break;
                case 2: { //继续上传
                    int size = photoList.size();
                    for (int i = 0; i < size; i++) {
                        PhotoListBean photoListBean = photoList.get(i);
                        if (!photoListBean.isUped() && canUpdata) {
                            sendOSSData(i);
                            break;
                        }
                    }
                }
                break;
                case 3: {
//                    if (Tools.isEmpty(tv_activity_classify.getText().toString().trim())) {
//                        Tools.showToast(UploadPicturesActivity.this, "请选择活动分类");
//                        return;
//                    }
//                    if (Tools.isEmpty(tv_theme_classify.getText().toString().trim())) {
//                        Tools.showToast(UploadPicturesActivity.this, "请选择活动主题");
//                        return;
//                    }
//                    if (0 == nums.split(",").length || TextUtils.isEmpty(nums)) {
//                        nums = "";
//                        Tools.showToast(UploadPicturesActivity.this, "请选择标签~");
//                        return;
//                    }
                    BeJsonStringTwo();
                    onCommit();
                }
                break;
            }
        }
    };

    String cat_id, theme_name, ai_id, activity_name;
    private ArrayList<TeamSpecialtyInfo> specialty_list;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_UPLOAD_PICTURES) {
            switch (requestCode) {
                case 3: {//活动分类
                    if (data != null) {
                        cat_id = data.getStringExtra("cat_id");
                        theme_name = data.getStringExtra("theme_name");
                        tv_activity_classify.setText(theme_name);
                    }
                    break;
                }
                case 4: {//活动主题
                    if (data != null) {
                        ai_id = data.getStringExtra("ai_id");
                        activity_name = data.getStringExtra("activity_name");
                        String key_cencent = data.getStringExtra("key_cencent");
                        tv_theme_classify.setText(activity_name);
                        str = null;
                        if (!Tools.isEmpty(key_cencent)) {
                            str = key_cencent.split(",");
                            getNum();
                        }
                    }
                    break;
                }
            }
        }
    }

    private void onCommit() {
        scenePhotoUpload.sendPostRequest(Urls.ScenePhotoUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //删除甩吧本地图片
                        for (int i = 0; i < photoList.size(); i++) {
                            if (!Tools.isEmpty(photoList.get(i).getProvince())) {
                                //图片上传完成后根据路径，地点时间删除照片
                                appDBHelper.deleteShakePhoto(photoList.get(i).getFile_url());
                                Tools.deleteFile(photoList.get(i).getFile_url());
                                Tools.deleteFile(photoList.get(i).getFile_url2());
                            }
                        }
                        Tools.showToast(UploadPicturesActivity.this, "提交成功~");
                        baseFinish();
                    } else {
                        Tools.showToast(UploadPicturesActivity.this, jsonObject.getString("msg"));
                        tv_submit.setText("提交");
                        tv_submit.setOnClickListener(UploadPicturesActivity.this);
                    }
                } catch (JSONException e) {
                    Tools.showToast(UploadPicturesActivity.this, getResources().getString(R.string.network_error));
                    tv_submit.setText("提交");
                    tv_submit.setOnClickListener(UploadPicturesActivity.this);
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                tv_submit.setText("提交");
                tv_submit.setOnClickListener(UploadPicturesActivity.this);
                Tools.showToast(UploadPicturesActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private String nums = "";

    private void getNum() {
        createcorps_special.removeAllViews();
        for (int i = 0; i < str.length; i++) {
            final TextView textView = new TextView(UploadPicturesActivity.this);
            textView.setText(str[i]);
            textView.setPadding(5, 5, 5, 5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(14);
            textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
            textView.setBackgroundResource(R.drawable.flowlayout_shape1);
            createcorps_special.addView(textView);
            final int[] tag = {1};
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tag[0]++;
                    if (tag[0] % 2 == 0) {  //选择
                        textView.setTextColor(getResources().getColor(R.color.app_background2));
                        textView.setBackgroundResource(R.drawable.flowlayout_shape2);
                        if (nums == null || "".equals(nums)) {
                            nums = textView.getText().toString();
                        } else {
                            if (!nums.contains(textView.getText().toString())) {
                                nums = nums + "," + textView.getText().toString();
                            }
                        }
                    } else {  //不选
                        textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                        textView.setBackgroundResource(R.drawable.flowlayout_shape1);
                        if (nums.equals(textView.getText().toString())) {
                            nums = nums.replace(textView.getText().toString(), "");
                        } else if ((nums.substring(0, textView.getText().toString().length() - 1)).equals(textView.getText().toString())) {
                            nums = nums.replace(textView.getText().toString() + ",", "");
                        } else {
                            nums = nums.replace("," + textView.getText().toString(), "");
                        }
                    }
                }
            });
        }
    }
}