package com.orange.oy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.MessageRightAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.MessageLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 帮助与反馈
 */
public class FeedbackActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle feedback_title = (AppTitle) findViewById(R.id.feedback_title);
        feedback_title.settingName(getResources().getString(R.string.feedback));
        feedback_title.showBack(this);
    }

    protected void onStop() {
        if (sendData != null) {
            sendData.stop(Urls.Addquestion);
        }
        if (Questionlist != null) {
            Questionlist.stop(Urls.Questionlist);
        }
        super.onStop();
    }

    private String search = "";

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("type", "意见反馈");
                params.put("question", feedback_edittext.getText().toString().trim());
                params.put("createtime", Tools.getTimeByPattern("yyyy-MM-dd HH:mm:ss"));
                if (!TextUtils.isEmpty(AppInfo.getKey(FeedbackActivity.this))) {
                    params.put("usermobile", AppInfo.getName(FeedbackActivity.this));
                }
                try {
                    params.put("appversion", Tools.getVersionName(FeedbackActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("appversion", "not found");
                }
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(FeedbackActivity.this));
                params.put("screensize", Tools.getScreeInfoWidth(FeedbackActivity.this) + "*" + Tools
                        .getScreeInfoHeight(FeedbackActivity.this));
                params.put("mac", Tools.getLocalMacAddress(FeedbackActivity.this));
                params.put("imei", Tools.getDeviceId(FeedbackActivity.this));
                if (!TextUtils.isEmpty(pictureUrl)) {
                    params.put("pictureUrl", pictureUrl);
                }
                return params;
            }
        };
        sendData.setIsShowDialog(true);
        Questionlist = new NetworkConnection(FeedbackActivity.this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_right + "");
                if (!TextUtils.isEmpty(search)) {
                    params.put("title", search);
                }
                return params;
            }
        };
    }

    private EditText feedback_edittext;
    private NetworkConnection Questionlist;
    private NetworkConnection sendData;
    private TextView feedback_tab_feedback, feedback_tab_question;
    private View feedback_tab_line2, feedback_tab_line1;
    private View feedback_left;
    private PullToRefreshListView feedback_right;
    private MessageRightAdapter messageRightAdapter;
    private GridView feedback_gridview;
    private MyAdapter myAdapter;
    private ArrayList<String> list, temp;//拍照的图片
    private DisplayMetrics dm;
    private String imagePath, pictureUrl = "";
    private ArrayList<String> deleteList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_feedback);
        list = new ArrayList<>();
        temp = new ArrayList<>();
        deleteList = new ArrayList<>();
        dm = new DisplayMetrics();
        list.add(0, "camera_default");
        initNetworkConnection();
        initTitle();
        feedback_edittext = (EditText) findViewById(R.id.feedback_edittext);
        feedback_tab_feedback = (TextView) findViewById(R.id.feedback_tab_feedback);
        feedback_tab_question = (TextView) findViewById(R.id.feedback_tab_question);
        feedback_tab_line2 = findViewById(R.id.feedback_tab_line2);
        feedback_tab_line1 = findViewById(R.id.feedback_tab_line1);
        feedback_left = findViewById(R.id.feedback_left);
        feedback_right = (PullToRefreshListView) findViewById(R.id.feedback_right);
        feedback_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        feedback_right.setPullLabel(getResources().getString(R.string.listview_down));
        feedback_right.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        feedback_right.setReleaseLabel(getResources().getString(R.string.listview_down2));
        feedback_gridview = (GridView) findViewById(R.id.feedback_gridview);
        myAdapter = new MyAdapter();
        feedback_gridview.setAdapter(myAdapter);
        feedback_gridview.setOnItemClickListener(this);
        feedback_right.setVisibility(View.GONE);
        feedback_tab_feedback.setOnClickListener(this);
        feedback_tab_question.setOnClickListener(this);
        findViewById(R.id.feedback_button).setOnClickListener(this);
        rightList = new ArrayList<>();
        messageRightAdapter = new MessageRightAdapter(FeedbackActivity.this, rightList);
        feedback_right.setAdapter(messageRightAdapter);
        feedback_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageLeftInfo messageLeftInfo = rightList.get(position - 1);
                Intent intent = new Intent(FeedbackActivity.this, BrowserActivity.class);
                intent.putExtra("title", messageLeftInfo.getTitle());
                intent.putExtra("flag", BrowserActivity.flag_question);
                intent.putExtra("content", messageLeftInfo.getMessage());
                startActivity(intent);
            }
        });
        feedback_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_right = 1;
                getDataRight();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_right++;
                getDataRight();
            }
        });
        onClick(feedback_tab_question);
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.Addquestion, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = 0;
                    code = jsonObject.getInt("code");
                    if (code == 200) {
                        Tools.showToast(FeedbackActivity.this, jsonObject.getString("msg"));
                        baseFinish();
                    } else {
                        Tools.showToast(FeedbackActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(FeedbackActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(FeedbackActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在上传...");
    }

    public void onBack() {
        baseFinish();
    }

    /**
     * 刷新右侧列表
     */
    private void refreshListViewRight() {
        page_right = 1;
        getDataRight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < deleteList.size(); i++) {
            if (!TextUtils.isEmpty(deleteList.get(i))) {
                File file = new File(deleteList.get(i));
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    private void getDataRight() {
        Questionlist.sendPostRequest(Urls.Questionlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (rightList == null) {
                            rightList = new ArrayList<MessageLeftInfo>();
                            messageRightAdapter.resetList(rightList);
                        } else {
                            if (page_right == 1)
                                rightList.clear();
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int lenght = jsonArray.length();
                        for (int i = 0; i < lenght; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            MessageLeftInfo messageLeftInfo = new MessageLeftInfo();
                            messageLeftInfo.setTitle(jsonObject.getString("title"));
                            messageLeftInfo.setMessage(Urls.API + "question?id=" + jsonObject.getString("id"));
                            rightList.add(messageLeftInfo);
                        }
                        feedback_right.onRefreshComplete();
                        if (lenght < 15) {
                            feedback_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            feedback_right.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        messageRightAdapter.notifyDataSetChanged();
                    } else {
                        feedback_right.onRefreshComplete();
                        Tools.showToast(FeedbackActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    feedback_right.onRefreshComplete();
                    Tools.showToast(FeedbackActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                feedback_right.onRefreshComplete();
                Tools.showToast(FeedbackActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private int page_right;
    private ArrayList<MessageLeftInfo> rightList;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback_button: {
                if (TextUtils.isEmpty(feedback_edittext.getText())) {
                    Tools.showToast(this, getResources().getString(R.string.feedback_error));
                    return;
                }
                if (list.size() > 1 && !temp.isEmpty()) {
                    Tools.showToast(this, "图片还没有上传完呢~");
                    return;
                }
                sendData();
            }
            break;
            case R.id.feedback_tab_feedback: {
                feedback_left.setVisibility(View.VISIBLE);
                feedback_right.setVisibility(View.GONE);
                feedback_tab_line2.setVisibility(View.VISIBLE);
                feedback_tab_line1.setVisibility(View.INVISIBLE);
                feedback_tab_feedback.setTextColor(getResources().getColor(R.color.feedback));
                feedback_tab_question.setTextColor(getResources().getColor(R.color.myreward_two));
                findViewById(R.id.feedback_button).setVisibility(View.VISIBLE);
            }
            break;
            case R.id.feedback_tab_question: {
                feedback_left.setVisibility(View.GONE);
                feedback_right.setVisibility(View.VISIBLE);
                feedback_tab_line1.setVisibility(View.VISIBLE);
                feedback_tab_line2.setVisibility(View.INVISIBLE);
                feedback_tab_feedback.setTextColor(getResources().getColor(R.color.myreward_two));
                feedback_tab_question.setTextColor(getResources().getColor(R.color.feedback));
                findViewById(R.id.feedback_button).setVisibility(View.GONE);
                if (rightList == null || rightList.isEmpty()) {
                    refreshListViewRight();
                }
            }
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ("camera_default".equals(list.get(position))) {
            SelectPhotoDialog.showPhotoSelecter(this, true, takeListener, pickListener);
        }
    }

    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            if (list.size() - 1 < 9) {
                imagePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(imagePath, Tools.getTimeSS() + ".jpg");
                imagePath = file.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, 1);
            } else {
                Tools.showToast(FeedbackActivity.this, "已到照片数量上限！");
            }
        }
    };
    private int index = 0;
    //从相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            if (list.size() - 1 < 9) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            } else {
                Tools.showToast(FeedbackActivity.this, "已到照片数量上限！");
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    deleteList.add(imagePath);
                    if (index == 0 || isUpdata) {
                        sendOSSData(imagePath);
                    }
                    list.add(imagePath);
                    temp.add(imagePath);
                    myAdapter.notifyDataSetChanged();
                }
                break;
                case 2: {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        if (index == 0 || isUpdata) {
                            sendOSSData(path);
                        }
                        list.add(path);
                        temp.add(path);
                        myAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
        }
    }

    class MyAdapter extends BaseAdapter {
        private int rate;
        private String path;
        private int height;
        private int topMar;

        MyAdapter() {
            height = (Tools.getScreeInfoWidth(FeedbackActivity.this) - Tools.dipToPx(FeedbackActivity.this, 50)) / 3;
            topMar = Tools.dipToPx(FeedbackActivity.this, 10);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setUpData(int rate, String path) {
            this.rate = rate;
            this.path = path;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyImageView imageView;
            if (convertView == null) {
                imageView = new MyImageView(FeedbackActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(
                        GridView.LayoutParams.MATCH_PARENT, height));
                imageView.setAdjustViewBounds();
                imageView.setScaleType();
            } else {
                imageView = (MyImageView) convertView;
            }
            if ("camera_default".equals(list.get(position))) {
                imageView.setImageResource(R.mipmap.pzp_button_tjzp);
            } else {
                if (list.get(position).equals(path)) {
                    if (rate == 0) {
                        imageView.setText(rate + "%" + "\n等待上传");
                        imageView.setAlpha(0.4f);
                    } else if (rate == 100) {
                        imageView.setText(rate + "%" + "\n上传成功");
                        imageView.setAlpha(1f);
                    } else {
                        imageView.setText(rate + "%" + "\n正在上传");
                        imageView.setAlpha(0.4f);
                    }
                }
                imageView.setImageBitmap2(list.get(position));
            }
            return imageView;
        }

        public void notifyDataSetChanged() {
            int index = (int) Math.ceil(list.size() / 3d);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) feedback_gridview.getLayoutParams();
            lp.height = (height + topMar) * index;
            super.notifyDataSetChanged();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    int rate = msg.getData().getInt("rate");
                    String path = msg.getData().getString("path");
                    myAdapter.setUpData(rate, path);
                    myAdapter.notifyDataSetChanged();
                }
                break;
                case 2: {
                    if (!temp.isEmpty()) {
                        sendOSSData(temp.get(0));
                    }
                }
                break;
            }
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

    public void sendOSSData(final String s) {
        isUpdata = false;
        try {
            Tools.d(s);
            final File file = new File(s);
            String objectKey = file.getName();
            objectKey = index + "_" + objectKey;
            index++;
            objectKey = Urls.EndpointDir + "/" + objectKey;
            String url = "http://ouye.oss-cn-hangzhou.aliyuncs.com/";
            if (TextUtils.isEmpty(pictureUrl)) {
                pictureUrl = url + objectKey;
            } else {
                pictureUrl += "," + url + objectKey;
            }
            Tools.d("图片地址：" + pictureUrl);
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
                    int rate = (int) (currentSize * 100 / totalSize);
                    Tools.d("rate：" + rate + "path：" + s);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("rate", rate);
                    bundle.putString("path", s);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    message.what = 1;
//                    Tools.d("currentSize: " + currentSize + " totalSize: " + totalSize);
                }
            });
            task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Tools.d("上传成功flag" + "list.size()：" + list.size());
                    temp.remove(0);
                    handler.sendEmptyMessage(2);
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
