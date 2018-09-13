package com.orange.oy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orange.oy.R;
import com.orange.oy.adapter.GridImageAdapter;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DataUploadDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.orange.oy.R.id.lin_alls;
import static com.orange.oy.R.id.lin_bottom;
import static com.orange.oy.R.id.lin_edit;
import static com.orange.oy.R.id.taskitempgnexty_button;

/**
 * 任务分类-拍照任务-任务说明页-信息录入页
 */
public class CloseTaskitemPhotographyNextYActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle(String title) {
        AppTitle taskitempgnext_title = (AppTitle) findViewById(R.id.taskitempgnexty_title);
        taskitempgnext_title.showBack(this);

        if (index != null && "0".equals(index)) {
            taskitempgnext_title.settingName("拍照任务（预览）");
        } else {
            taskitempgnext_title.settingName("拍照任务");
        }
    }

    private void returnTips() {
        ConfirmDialog.showDialog(CloseTaskitemPhotographyNextYActivity.this, "提示！", 3, "您的照片将会被清空。",
                "继续返回", "等待上传", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                    @Override
                    public void leftClick(Object object) {
                        for (int i = 0; i < uniqueList.size(); i++) {
                            updataDBHelper.removeTask(project_id + uniqueList.get(i));
                        }
                        baseFinish();
                    }

                    @Override
                    public void rightClick(Object object) {

                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isLoading) {
                selectUploadMode();
            } else if (!isBackEnable) {
                returnTips();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBack() {
        if (isBackEnable) {
            baseFinish();
        } else {
            if (isLoading) {//如果已经进行资料回收
                selectUploadMode();
            } else {
                returnTips();
            }
        }
    }

    protected void onStop() {
        super.onStop();
    }

    private void initNetworkConnection() {
        Closepackagetask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("tasktype", "1");
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Closepackagetask.setIsShowDialog(true);
        Closepackage = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("pname", task_pack_name);
                params.put("storeid", store_id);
                params.put("storename", store_name);
                int size;
                if ("1".equals(photo_type)) {//单备注
                    params.put("note", Tools.filterEmoji(taskitempgnexty_edit.getText().toString().trim()));
                } else {//多备注
                    String str = "";
                    size = selectImgList.size();
                    for (int i = 0; i < size; i++) {
                        if (selectImgList.get(i).equals("camera_default")) {
                            continue;
                        }
                        if (TextUtils.isEmpty(str)) {
                            str = ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                                    .view_checkreqpgnext_edit)).getText().toString().trim();
                            if (TextUtils.isEmpty(str)) {
                                str = " ";
                            } else {
                                str = str.replaceAll("&", " ");
                            }
                        } else {
                            String temp = ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                                    .view_checkreqpgnext_edit)).getText().toString().trim();
                            if (TextUtils.isEmpty(temp)) {
                                temp = " ";
                            } else {
                                temp = temp.replaceAll("&", " ");
                            }
                            str = str + "&" + temp;
                        }
                    }
                    params.put("note", str);
                }
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                params.put("taskid", taskid);
                return params;
            }
        };
        Closepackage.setIsShowDialog(true);
    }

    private String isphoto, photo_type, taskid, tasktype, task_pack_id, store_id, task_name, task_pack_name, store_name,
            project_id, project_name, store_num;
    private String id;
    private LinearLayout taskitempgnexty_questionlayout;
    private ListView taskitempgnexty_bg2;
    private EditText taskitempgnexty_edit;
    private GridImageAdapter adapter;
    private ArrayList<String> selectImgList = new ArrayList<>();
    private ArrayList<String> originalImgList = new ArrayList<>();//原图路径
    private static int selectIndex;
    private NetworkConnection Closepackage, Closepackagetask;
    private int maxSelect;
    private String photo_compression;
    private String is_desc, picStr;//示例图片
    private GridView checkreqpgnext_gridview;
    private MyGridView taskitempgnexty_gridview1;
    private int gridViewItemHeigth;
    private int is_watermark;
    private String codeStr, brand;
    private String outlet_batch, p_batch, index;
    private boolean isOffline;
    private OfflineDBHelper offlineDBHelper;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter2;
    private ImageLoader imageLoader;
    private AppDBHelper appDBHelper;
    private boolean isBackEnable = true;//是否可返回上一页
    private ArrayList<String> uniqueList;//存储上传记录的唯一标识（用于页面返回的清空未上传记录）
    private TextView taskitemedit_desc;
    private boolean isSpread = false;//说明是否展开
    private ImageView spread_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitempgnext_y);
        taskitempgnexty_gridview1 = (MyGridView) findViewById(R.id.taskitempgnexty_gridview1);
        taskitempgnexty_edit = (EditText) findViewById(R.id.taskitempgnexty_edit);
        taskitempgnexty_questionlayout = (LinearLayout) findViewById(R.id.taskitempgnexty_questionlayout);
        updataDBHelper = new UpdataDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        appDBHelper = new AppDBHelper(this);
        offlineDBHelper = new OfflineDBHelper(this);
        uniqueList = new ArrayList<>();
        registerReceiver(this);
        initNetworkConnection();
        selectIndex = 0;
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        imageLoader = new ImageLoader(this);
        gridViewItemHeigth = (Tools.getScreeInfoWidth(this) - Tools.dipToPx(this, 60)) / 3 + Tools.dipToPx(this, 10);
        String num = data.getStringExtra("num");
        String title = data.getStringExtra("task_name");
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        isphoto = data.getStringExtra("isphoto");
        photo_type = data.getStringExtra("photo_type");
        taskid = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_name = data.getStringExtra("store_name");
        task_name = data.getStringExtra("task_name");
        if (TextUtils.isEmpty(task_name) || "null".equals(task_name)) {
            task_name = "拍照任务";
        }
        index = data.getStringExtra("index");
        // Tools.d("tag", "index444===>" + index);
        checkreqpgnext_gridview = (GridView) findViewById(R.id.taskitempgnexty_gridview);

        if (index != null && "0".equals(index)) {
            checkreqpgnext_gridview.setVisibility(View.GONE);
            findViewById(R.id.taskitempgnexty_txt).setVisibility(View.GONE);
            taskitempgnexty_edit.setVisibility(View.GONE);
            taskitempgnexty_questionlayout.setVisibility(View.GONE);
            findViewById(R.id.taskitempgnexty_button).setVisibility(View.GONE);

        } else {
            checkreqpgnext_gridview.setVisibility(View.VISIBLE);
            findViewById(R.id.taskitempgnexty_txt).setVisibility(View.VISIBLE);
            taskitempgnexty_edit.setVisibility(View.VISIBLE);
            taskitempgnexty_questionlayout.setVisibility(View.VISIBLE);
            findViewById(R.id.taskitempgnexty_button).setVisibility(View.VISIBLE);
        }

        tasktype = data.getStringExtra("tasktype");
        store_id = data.getStringExtra("store_id");
        store_num = data.getStringExtra("store_num");
        is_desc = data.getStringExtra("is_desc");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        is_watermark = Tools.StringToInt(data.getStringExtra("is_watermark"));
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        isOffline = data.getBooleanExtra("isOffline", false);
        initTitle(title);
        photo_compression = data.getStringExtra("photo_compression");
        maxSelect = Tools.StringToInt(num);
        if (maxSelect == -1) {
            maxSelect = 9;
        }
        appDBHelper.deletePhotoUrl(project_id, store_id, taskid);
        selectImgList.add("camera_default");

        spread_button = (ImageView) findViewById(R.id.spread_button);
        findViewById(taskitempgnexty_button).setOnClickListener(this);
        taskitemedit_desc = (TextView) findViewById(R.id.taskitempgnexty_desc);

        adapter2 = new TaskitemReqPgAdapter(this, picList);
        taskitempgnexty_gridview1.setAdapter(adapter2);
        taskitempgnexty_gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoView imageView = new PhotoView(CloseTaskitemPhotographyNextYActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageLoader.DisplayImage(picList.get(position), imageView);
                SelecterDialog.showView(CloseTaskitemPhotographyNextYActivity.this, imageView);
            }
        });
        getData();
        initLocation();
    }

    private void getData() {
        Closepackagetask.sendPostRequest(Urls.Closepackagetask, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String task_name = jsonObject.getString("task_name");
                        ((TextView) findViewById(R.id.taskitempgnexty_name)).setText(task_name);
                        taskitemedit_desc.setText(jsonObject.getString("task_note"));
                        if (taskitemedit_desc.getLineCount() > 1) {
                            taskitemedit_desc.setSingleLine(true);
                            isSpread = false;
                            findViewById(R.id.spread_button_layout).setOnClickListener(CloseTaskitemPhotographyNextYActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                            if (index != null && "0".equals(index))
                                onClick(findViewById(R.id.spread_button_layout));
                        } else {
                            findViewById(R.id.spread_button_layout).setVisibility(View.GONE);
                        }
                        photo_type = jsonObject.getString("note_type");
                        isphoto = jsonObject.getString("is_photo");
//                        num = jsonObject.getString("photo_num");
                        is_watermark = Tools.StringToInt(jsonObject.getString("is_watermark"));
                        picStr = jsonObject.getString("photo_url");
                        picStr = picStr.replaceAll("\\\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
                        findViewById(R.id.shili).setVisibility(View.GONE);
                        taskitempgnexty_gridview1.setVisibility(View.GONE);
                        if (TextUtils.isEmpty(picStr) || "null".equals(picStr)) {
                        } else {
                            findViewById(R.id.spread_button_layout).setOnClickListener(CloseTaskitemPhotographyNextYActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                            if (index != null && "0".equals(index))
                                onClick(findViewById(R.id.spread_button_layout));
//                            picStr = picStr.substring(1, picStr.length() - 1);
                            String[] pics = picStr.split(",");
                            for (int i = 0; i < pics.length; i++) {
                                picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
                            }
//                            if (pics.length > 0) {
//                                int t = (int) Math.ceil(pics.length / 3d);
//                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitempgnexty_gridview1.getLayoutParams();
//                                lp.height = (int) ((Tools.getScreeInfoWidth(CloseTaskitemPhotographyNextYActivity.this) -
//                                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 - getResources()
//                                        .getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
//                                taskitempgnexty_gridview1.setLayoutParams(lp);
//                            }
                            adapter2.notifyDataSetChanged();
                        }
                        if ("1".equals(photo_type) && !(index != null && "0".equals(index))) {//单备注
                            adapter = new GridImageAdapter(CloseTaskitemPhotographyNextYActivity.this, selectImgList);
                            checkreqpgnext_gridview.setAdapter(adapter);
                            checkreqpgnext_gridview.setOnItemClickListener(CloseTaskitemPhotographyNextYActivity.this);
                        } else if ("2".equals(photo_type)) {//多备注
                            findViewById(R.id.taskitempgnexty_bg1).setVisibility(View.GONE);
                            taskitempgnexty_bg2 = (ListView) findViewById(R.id.taskitempgnexty_bg2);
                            taskitempgnexty_bg2.setVisibility(View.VISIBLE);
                            myAdapter = new MyAdapter();
                            taskitempgnexty_bg2.setAdapter(myAdapter);
                        }
                    } else {
                        Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    private MyAdapter myAdapter;

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return selectImgList.size();
        }

        public Object getItem(int position) {
            return selectImgList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = Tools.loadLayout(CloseTaskitemPhotographyNextYActivity.this, R.layout.view_checkreqpgnext_add);
                imageView = (ImageView) convertView.findViewById(R.id.view_checkreqpgnext_img);
                imageView.setOnClickListener(onClickListener);
                convertView.setTag(imageView);
            } else {
                imageView = (ImageView) convertView.getTag();
            }
            imageView.setTag(position);
            String path = selectImgList.get(position);
            if (path.equals("camera_default")) {
                imageView.setImageResource(R.mipmap.pzp_button_tjzp);
            } else {
                imageView.setImageBitmap(Tools.getBitmap(path, 200, 200));
            }
            return convertView;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                selectIndex = Integer.parseInt(v.getTag().toString());
                if ("0".equals(isphoto)) {//不可用相册
                    takeListener.onClick(null);
                } else {
                    SelectPhotoDialog.showPhotoSelecter(CloseTaskitemPhotographyNextYActivity.this, takeListener, pickListener);
                }
            } catch (NumberFormatException exception) {
                selectIndex = 0;
                Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, "应用异常");
            }
        }
    };
    private UpdataDBHelper updataDBHelper;

    private void sendDataOffline() {
        String username = AppInfo.getName(CloseTaskitemPhotographyNextYActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("token", Tools.getToken());
        params.put("pid", task_pack_id);
        params.put("pname", task_pack_name);
        params.put("storeid", store_id);
        params.put("storename", store_name);
        int size;
        if ("1".equals(photo_type)) {//单备注
            params.put("note", Tools.filterEmoji(taskitempgnexty_edit.getText().toString().trim()));
        } else {//多备注
            String str = "";
            size = selectImgList.size();
            for (int i = 0; i < size; i++) {
                if (selectImgList.get(i).equals("camera_default")) {
                    continue;
                }
                if (TextUtils.isEmpty(str)) {
                    str = ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                            .view_checkreqpgnext_edit)).getText().toString().trim();
                    if (TextUtils.isEmpty(str)) {
                        str = " ";
                    } else {
                        str = str.replaceAll("&", " ");
                    }
                } else {
                    String temp = ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                            .view_checkreqpgnext_edit)).getText().toString().trim();
                    if (TextUtils.isEmpty(temp)) {
                        temp = " ";
                    } else {
                        temp = temp.replaceAll("&", " ");
                    }
                    str = str + "&" + temp;
                }
            }
            params.put("note", str);
        }
        params.put("outlet_batch", outlet_batch);
        params.put("p_batch", p_batch);
        params.put("taskid", taskid);
        String imgs = "";
        String key = "";
        size = originalImgList.size();
        for (int i = 0; i < size; i++) {
            String path = originalImgList.get(i);
            if (path.equals("camera_default")) {
                continue;
            }
            if (TextUtils.isEmpty(imgs)) {
                imgs = originalImgList.get(i);
            } else {
                imgs = imgs + "," + originalImgList.get(i);
            }
            if (TextUtils.isEmpty(key)) {
                key = "img" + (i + 1);
            } else {
                key = key + ",img" + (i + 1);
            }
        }
        updataDBHelper.addUpdataTask(username, project_id, project_name, codeStr, brand,
                store_id, store_num, task_pack_id,
                task_pack_name, "02", taskid, task_name, null, null, null,
                username + project_id + store_id + task_pack_id, Urls.Closepackagecomplete,
                key, imgs, UpdataDBHelper.Updata_file_type_img, params, photo_compression,
                true, Urls.Closepackage, paramsToString(), true);
        if (isOffline) {
            offlineDBHelper.closePackage(username, project_id, store_id, task_pack_id);
        }
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
        baseFinish();
    }

    private void sendData() {
        if (isComplete) {
            baseFinish();
            return;
        }
        if (!isLoading) {
            Closepackage.sendPostRequest(Urls.Closepackage, new Response.Listener<String>() {
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200 || code == 2) {
                            isLoading = true;
                            String username = AppInfo.getName(CloseTaskitemPhotographyNextYActivity.this);
                            Map<String, String> params = new HashMap<>();
                            params.put("token", Tools.getToken());
                            params.put("pid", task_pack_id);
                            params.put("pname", task_pack_name);
                            params.put("storeid", store_id);
                            params.put("storename", store_name);
                            int size;
                            if ("1".equals(photo_type)) {//单备注
                                params.put("note", Tools.filterEmoji(taskitempgnexty_edit.getText().toString().trim()));
                            } else {//多备注
                                String str = "";
                                size = selectImgList.size();
                                for (int i = 0; i < size; i++) {
                                    if (selectImgList.get(i).equals("camera_default")) {
                                        continue;
                                    }
                                    if (TextUtils.isEmpty(str)) {
                                        str = ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                                                .view_checkreqpgnext_edit)).getText().toString().trim();
                                        if (TextUtils.isEmpty(str)) {
                                            str = " ";
                                        } else {
                                            str = str.replaceAll("&", " ");
                                        }
                                    } else {
                                        String temp = ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                                                .view_checkreqpgnext_edit)).getText().toString().trim();
                                        if (TextUtils.isEmpty(temp)) {
                                            temp = " ";
                                        } else {
                                            temp = temp.replaceAll("&", " ");
                                        }
                                        str = str + "&" + temp;
                                    }
                                }
                                params.put("note", str);
                            }
                            params.put("outlet_batch", outlet_batch);
                            params.put("p_batch", p_batch);
                            params.put("taskid", taskid);
                            updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                    store_id, store_name, task_pack_id,
                                    task_pack_name, "111", taskid, task_name, null, null, null,
                                    username + project_id + store_id + task_pack_id, Urls.Closepackagecomplete,
                                    null, null, UpdataDBHelper.Updata_file_type_img, params, photo_compression,
                                    true, Urls.Closepackage, paramsToString(), false);
                            if (isOffline) {
                                offlineDBHelper.closePackage(username, project_id, store_id, task_pack_id);
                            }
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                            TaskitemDetailActivity.isRefresh = true;
                            TaskitemDetailActivity_12.isRefresh = true;
                            TaskitemDetailActivity.taskid = taskid;
                            TaskitemDetailActivity_12.taskid = taskid;
                            TaskFinishActivity.isRefresh = true;
                            TaskitemListActivity.isRefresh = true;
                            OfflineStoreActivity.isRefresh = true;
//                            if (code == 2) {
//                                ConfirmDialog.showDialog(CloseTaskitemPhotographyNextYActivity.this, null, jsonObject.getString("msg"), null,
//                                        "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
//                                            @Override
//                                            public void leftClick(Object object) {
//
//                                            }
//
//                                            @Override
//                                            public void rightClick(Object object) {
//                                                baseFinish();
//                                            }
//                                        }).goneLeft();
//                            } else if (code == 200) {
                            String fileurl = appDBHelper.getAllPhotoUrl(username, project_id, store_id, taskid);
                            if (fileurl != null && (appDBHelper.getPhotoUrlIsCompete(fileurl, project_id, store_id, taskid))) {
                                baseFinish();
                            } else {
                                selectUploadMode();
                            }
//                            }
                        } else {
                            Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, getResources().getString(R.string
                                .network_error));
                    }
                    CustomProgressDialog.Dissmiss();
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError volleyError) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else {
            selectUploadMode();
        }
    }

    private void selectUploadMode() {
        if (!isComplete) {
            String mode = appDBHelper.getDataUploadMode(store_id);
            Tools.d("上传模式....." + mode);
            if ("1".equals(mode)) {//弹框选择==1
                DataUploadDialog.showDialog(CloseTaskitemPhotographyNextYActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        baseFinish();
                    }
                });
            } else if ("2".equals(mode)) {//弹框选择===2
                DataUploadDialog.showDialog(CloseTaskitemPhotographyNextYActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        baseFinish();
                    }
                });
            } else if ("3".equals(mode)) {//直接关闭
                appDBHelper.addDataUploadRecord(store_id, "3");
                baseFinish();
            }
        }
    }

    private String paramsToString() {
        Map<String, String> params = new HashMap<>();
        params.put("token", Tools.getToken());
        params.put("pid", task_pack_id);
        params.put("pname", task_pack_name);
        params.put("storeid", store_id);
        params.put("storename", store_name);
        int size;
        if ("1".equals(photo_type)) {//单备注
            params.put("note", Tools.filterEmoji(taskitempgnexty_edit.getText().toString().trim()));
        } else {//多备注
            String str = "";
            size = selectImgList.size();
            for (int i = 0; i < size; i++) {
                if (selectImgList.get(i).equals("camera_default")) {
                    continue;
                }
                if (TextUtils.isEmpty(str)) {
                    str = ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                            .view_checkreqpgnext_edit)).getText().toString().trim();
                    if (TextUtils.isEmpty(str)) {
                        str = " ";
                    } else {
                        str = str.replaceAll("&", " ");
                    }
                } else {
                    String temp = ((EditText) taskitempgnexty_bg2.getChildAt(i).findViewById(R.id
                            .view_checkreqpgnext_edit)).getText().toString().trim();
                    if (TextUtils.isEmpty(temp)) {
                        temp = " ";
                    } else {
                        temp = temp.replaceAll("&", " ");
                    }
                    str = str + "&" + temp;
                }
            }
            params.put("note", str);
        }
        params.put("outlet_batch", outlet_batch);
        params.put("p_batch", p_batch);
        params.put("taskid", taskid);
        String data = "";
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + params.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + params.get(key).trim();
                }
            }
        }
        return data;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case taskitempgnexty_button: {
                if (selectImgList == null || selectImgList.isEmpty() || selectImgList.get(0).equals("camera_default")) {
                    Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, "请拍照");
                    return;
                }
                sendData();
                TaskitemDetailActivity_12.isRefresh = false;
//                new zoomImageAsyncTask().executeOnExecutor(Executors.newCachedThreadPool());
            }
            break;
            case R.id.spread_button_layout: {
                if (!TextUtils.isEmpty(picStr) && !"null".equals(picStr)) {
                    if (taskitempgnexty_gridview1.getVisibility() == View.VISIBLE) {
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        findViewById(R.id.shili).setVisibility(View.GONE);
                        taskitempgnexty_gridview1.setVisibility(View.GONE);
                        taskitemedit_desc.setSingleLine(true);
                    } else {
                        spread_button.setImageResource(R.mipmap.spread_button_up);
                        taskitemedit_desc.setSingleLine(false);
                        findViewById(R.id.shili).setVisibility(View.VISIBLE);
                        taskitempgnexty_gridview1.setVisibility(View.VISIBLE);
                    }
                } else {
                    findViewById(R.id.shili).setVisibility(View.GONE);
                    taskitempgnexty_gridview1.setVisibility(View.GONE);
                    if (isSpread) {//说明展开
                        isSpread = false;
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        taskitemedit_desc.setSingleLine(true);
                    } else {
                        isSpread = true;
                        spread_button.setImageResource(R.mipmap.spread_button_up);
                        taskitemedit_desc.setSingleLine(false);
                    }
                }
            }
            break;
        }
    }

    public static final int TakeRequest = 0x100;
    public static final int PickRequest = 0x101;
    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(CloseTaskitemPhotographyNextYActivity.this, Camerase.class);
            intent.putExtra("projectid", project_id);
            intent.putExtra("storeid", store_id);
            intent.putExtra("storecode", store_num);
            intent.putExtra("maxTake", 1);
            startActivityForResult(intent, TakeRequest);
        }
    };

    //相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(CloseTaskitemPhotographyNextYActivity.this, AlbumActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("maxsize", maxSelect - selectIndex);
            bundle.putString("projectid", project_id);
            bundle.putString("storeid", store_id);
            bundle.putString("packetid", task_pack_id);
            bundle.putString("taskid", taskid);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        }
    };

    private void settingImgPath(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            Tools.showToast(this, "拍照方式错误");
            return;
        }
        startZoomImageAsyncTask(path, "");
    }

    private SystemDBHelper systemDBHelper;

    public void startZoomImageAsyncTask(String path, String name) {
        if (selectIndex < selectImgList.size()) {
            String temp = selectImgList.remove(selectIndex);
            if (!temp.contains("default")) {
                systemDBHelper.updataStateTo2(new String[]{temp});
            }
            if (selectIndex == selectImgList.size()) {
                selectImgList.add(path);
            } else {
                selectImgList.add(selectIndex, path);
            }
        } else {
            selectImgList.add(path);
        }
        new zoomImageAsyncTask(path).executeOnExecutor(Executors.newCachedThreadPool());

    }

    private int current;

    public void startZoomImageAsyncTask(String name, ArrayList<String> list) {
        current = selectImgList.size() - 1;
        int size = list.size();
        if (size > 0) {
            if (size <= maxSelect - selectIndex) {
                int oldSize = selectImgList.size();
                for (int i = selectIndex, j = 0; j < size; j++, i++) {
                    if (i < oldSize) {
                        systemDBHelper.updataStateTo2(new String[]{selectImgList.get(i)});
                        selectImgList.set(i, list.get(j));
                    } else {
                        selectImgList.add(list.get(j));
                    }
                }
                systemDBHelper.updataStateTo1(list.toArray(new String[size]));
                new zoomImageAsyncTask(true).executeOnExecutor(Executors.newCachedThreadPool());
            } else {
                Tools.showToast(this, "选择异常，请重新选择图片！");
            }
        }
    }

    class zoomImageAsyncTask extends AsyncTask {
        String msg = "图片压缩失败！";
        String path, oPath;
        boolean isAblum;

        public zoomImageAsyncTask(String path) {
            this.path = path;
        }

        public zoomImageAsyncTask(boolean isAblum) {
            this.isAblum = isAblum;
        }

        protected void onPreExecute() {
            if (photo_compression.equals("-1")) {
                CustomProgressDialog.showProgressDialog(CloseTaskitemPhotographyNextYActivity.this, "图片处理中...");
            } else {
                CustomProgressDialog.showProgressDialog(CloseTaskitemPhotographyNextYActivity.this, "图片压缩中...");
            }
            super.onPreExecute();
        }

        private File getTempFile(String oPath) throws FileNotFoundException {
            File returnvalue = null;
            FileInputStream fis = null;
            FileOutputStream fos = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                if (!isLegal(oPath)) {
                    return null;
                }
                File oldfile = new File(oPath);
                if (!oldfile.exists()) {
                    return null;
                }
                if (!oldfile.isFile()) {
                    return null;
                }
                if (!oldfile.canRead()) {
                    return null;
                }
                File f = new File(oPath + "temp");
                fis = new FileInputStream(oldfile);
                bis = new BufferedInputStream(fis);
                fos = new FileOutputStream(f);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                while (bis.read(b) != -1) {
                    for (int i = 0; i < b.length; i++) {
                        b[i] = (byte) (255 - b[i]);
                    }
                    bos.write(b);
                }
                bos.flush();
                if (isLegal(oPath + "temp")) {
                    returnvalue = f;
                } else {
                    returnvalue = null;
                    f.delete();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new FileNotFoundException();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                throw new OutOfMemoryError();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return returnvalue;
        }

        protected Object doInBackground(Object[] params) {
            try {
                if (isAblum) {
                    int size = selectImgList.size();
                    for (int i = current; i < size; i++) {
                        String tPath = selectImgList.get(i);
                        if ("camera_default".equals(tPath)) {
                            continue;
                        }
                        String oPath = systemDBHelper.searchForOriginalpath(tPath);
                        Tools.d("opath照片1：" + oPath);
                        if (!TextUtils.isEmpty(oPath) && isLegal(oPath) && checkPicture(oPath)) {
                            if (photo_compression.equals("-1")) {
                                if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
                                    if (!originalImgList.contains(oPath)) {
                                        originalImgList.add(oPath);
                                    } else {
                                        msg = "发现重复照片，已自动去重，请重新提交";
                                        selectImgList.remove(i);
                                        i--;
                                        size--;
                                        isHadUnlegal = true;
                                    }
                                } else {
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                }
                            } else {//加水印
                                File tempFile = getTempFile(oPath);//生成临时文件
                                if (tempFile == null) {
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                    continue;
                                }
                                if (saveBitmap(imageZoom(Tools.getBitmap(tempFile.getPath(), 1024, 1024),
                                        Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath) != null) {
                                    Tools.d(oPath);
                                    if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
                                        if (!originalImgList.contains(oPath)) {
                                            originalImgList.add(oPath);
                                        } else {
                                            msg = "发现重复照片，已自动去重，请重新提交";
                                            selectImgList.remove(i);
                                            i--;
                                            size--;
                                            isHadUnlegal = true;
                                        }
                                    } else {
                                        selectImgList.remove(i);
                                        i--;
                                        size--;
                                        isHadUnlegal = true;
                                    }
                                } else {
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                }
                                if (tempFile.exists()) {
                                    tempFile.delete();
                                }
                            }
                        } else {
                            selectImgList.remove(i);
                            i--;
                            size--;
                            if (!TextUtils.isEmpty(oPath)) {//判定为异常图片，执行删除操作
                                new File(oPath).delete();
                                new File(tPath).delete();
                                systemDBHelper.deletePicture(oPath);
                            }
                            msg = "有图片异常，已自动删除异常图片,请重新提交";
                            isHadUnlegal = true;
                        }
                    }
                } else {
                    oPath = systemDBHelper.searchForOriginalpath(path);
                    Tools.d("opath照片2：" + oPath);
                    if (!TextUtils.isEmpty(oPath) && isLegal(oPath) && checkPicture(oPath)) {
                        if (photo_compression.equals("-1")) {
                            if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
                                if (!originalImgList.contains(oPath)) {
                                    originalImgList.add(oPath);
                                } else {
                                    msg = "发现重复照片，已自动去重，请重新提交";
                                    selectImgList.remove(path);
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(path);
                                isHadUnlegal = true;
                            }
                        } else {//加水印
                            File tempFile = getTempFile(oPath);//生成临时文件
                            if (tempFile == null) {
                                selectImgList.remove(path);
                                isHadUnlegal = true;
                            }
                            if (saveBitmap(imageZoom(Tools.getBitmap(tempFile.getPath(), 1024, 1024),
                                    Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath) != null) {
                                if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
                                    if (!originalImgList.contains(oPath)) {
                                        originalImgList.add(oPath);
                                    } else {
                                        msg = "发现重复照片，已自动去重，请重新提交";
                                        selectImgList.remove(path);
                                        isHadUnlegal = true;
                                    }
                                } else {
                                    selectImgList.remove(path);
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(path);
                                isHadUnlegal = true;
                            }
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    } else {
                        selectImgList.remove(path);
                        if (!TextUtils.isEmpty(oPath)) {//判定为异常图片，执行删除操作
                            new File(oPath).delete();
                            new File(path).delete();
                            systemDBHelper.deletePicture(oPath);
                        }
                        msg = "有图片异常，已自动删除异常图片,请重新提交";
                        isHadUnlegal = true;
                    }
                }
            } catch (OutOfMemoryError e) {
                msg = "内存不足，请清理内存或重启手机";
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private boolean isHadUnlegal = false;

        protected void onPostExecute(Object o) {
            if (o == null || !(boolean) o || isHadUnlegal) {
                Tools.showToast(CloseTaskitemPhotographyNextYActivity.this, msg);
                if (originalImgList != null) {
                    originalImgList.clear();
                }
                CustomProgressDialog.Dissmiss();
            } else {
                if (isAblum) {
                    String key = "", imgs = "";
                    int size = originalImgList.size();
                    String username = AppInfo.getName(CloseTaskitemPhotographyNextYActivity.this);
                    for (int i = 0; i < size; i++) {
                        appDBHelper.addPhotoUrlRecord(username, project_id, store_id, taskid, originalImgList.get(i), selectImgList.get(i));
                        appDBHelper.setFileNum(originalImgList.get(i), originalImgList.size() + "");
                        systemDBHelper.updataStateOPathTo3_2(originalImgList.get(i));
                        String path2 = originalImgList.get(i);
                        if (path2.equals("camera_default")) {
                            continue;
                        }
                        if (TextUtils.isEmpty(imgs)) {
                            imgs = originalImgList.get(i);
                        } else {
                            imgs = imgs + "," + originalImgList.get(i);
                        }
                        if (TextUtils.isEmpty(key)) {
                            key = "img" + (i + 1);
                        } else {
                            key = key + ",img" + (i + 1);
                        }
                    }
                    String uniquelyNum = username + project_id + store_id + task_pack_id + taskid + size;
                    uniqueList.add(uniquelyNum);
                    boolean isSuccess = updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                            store_id, store_name, task_pack_id,
                            task_pack_name, "11", taskid, task_name, null, null, null,
                            uniquelyNum, null, key, imgs, UpdataDBHelper.Updata_file_type_img, null, photo_compression,
                            false, null, null, false);
                    if (isSuccess) {
                        CustomProgressDialog.Dissmiss();
                    }
                } else {
                    String username = AppInfo.getName(CloseTaskitemPhotographyNextYActivity.this);
                    String uniquelyNum = username + project_id + store_id + task_pack_id + taskid + originalImgList.size();
                    uniqueList.add(uniquelyNum);
                    boolean isSuccess1 = updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                            store_id, store_name, task_pack_id,
                            task_pack_name, "11", taskid, task_name, null, null, null,
                            uniquelyNum, null, "img", oPath, UpdataDBHelper.Updata_file_type_img, null, photo_compression,
                            false, null, null, false);
                    boolean isSuccess2 = appDBHelper.addPhotoUrlRecord(username, project_id, store_id, taskid, oPath, path);
                    if (isSuccess1 && isSuccess2) {
                        appDBHelper.setFileNum(oPath, originalImgList.size() + "");
                        CustomProgressDialog.Dissmiss();
                    }
                    systemDBHelper.updataStateOPathTo3_2(oPath);
                }
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                startService(service);
            }
            refreshUI();
        }
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(takePhotoBroadcastReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(takePhotoBroadcastReceiver);
    }

    private boolean isComplete = false;
    private boolean isLoading = false;
    private BroadcastReceiver takePhotoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if ("1".equals(type)) {//可更新UI
                String rate = intent.getStringExtra("rate");
                String thumbnailPath = intent.getStringExtra("thumbnailPath");
                if (adapter != null && rate != null && thumbnailPath != null) {
                    adapter.setRateData(rate, thumbnailPath);
                    refreshUI();
                }
            } else if ("2".equals(type) && !isBackEnable) {//资料回收完成
                isComplete = true;
            }
        }
    };

    private void refreshUI() {
        if (taskitempgnexty_bg2 != null) {
            int size = selectImgList.size();
            if (size < maxSelect && (size == 0 || !selectImgList.get(size - 1).equals("camera_default"))) {
                selectImgList.add("camera_default");
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitempgnexty_bg2.getLayoutParams();
            lp.height = (int) (selectImgList.size() * (getResources().getDimension(R.dimen.taskitemphotoY_item_height) +
                    Tools.dipToPx(CloseTaskitemPhotographyNextYActivity.this, 10)));
            taskitempgnexty_bg2.setLayoutParams(lp);
            myAdapter.notifyDataSetChanged();
        } else {
            int size = selectImgList.size();
            if (size > 0) {
                if (size < maxSelect && !selectImgList.get(size - 1).equals("camera_default")) {
                    selectImgList.add("camera_default");
                }
            } else {
                selectImgList.add("camera_default");
            }
            int t = (int) Math.ceil(selectImgList.size() / 3d);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) checkreqpgnext_gridview.getLayoutParams();
            lp.height = t * gridViewItemHeigth;
            checkreqpgnext_gridview.setLayoutParams(lp);
            adapter.notifyDataSetChanged();
        }
    }

    //检测加密图片是否正常
    private boolean checkPicture(String path) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(path);
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            for (int i = 0; i < b.length; i++) {
                b[i] = (byte) (255 - b[i]);
            }
            value = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value != null && (value.equals("FFD8FFE1") || value.equals("89504E47"));
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte aSrc : src) {
            hv = Integer.toHexString(aSrc & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    private boolean isLegal(String path) {
        File file = new File(path);
        return file.length() > 51200;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakeRequest: {
                    isBackEnable = false;
//                    settingImgPath(FileCache.getDirForPhoto(TaskitemPhotographyNextYActivity.this, AppInfo.getName
//                            (TaskitemPhotographyNextYActivity.this) + "/" + taskid + tasktype + photo_type +
//                            categoryPath).getPath() + "/" + selectIndex + ".jpg");
                    settingImgPath(data.getStringExtra("path"));
                }
                break;
                case PickRequest: {
                    Uri selectedImage = data.getData();
                    String filePath = null;
                    try {
                        if (selectedImage.toString().startsWith("content")) {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                filePath = cursor.getString(columnIndex);
                            }
                            cursor.close();
                        } else {
                            filePath = selectedImage.getPath();
                        }
                    } catch (Exception e) {
                    }
                    if (filePath == null) {
                        Tools.showToast(this, "图片读取失败");
                        return;
                    }
                    settingImgPath(filePath);
                }
                break;
                case 0: {
                    isBackEnable = false;
                    Bundle bundle = data.getExtras();
                    ArrayList<String> tDataList = (ArrayList<String>) bundle.getSerializable("dataList");
                    startZoomImageAsyncTask(Tools.getTimeSS() + Tools.getDeviceId(CloseTaskitemPhotographyNextYActivity.this) +
                            taskid, tDataList);
                }
                break;
            }
        }
    }

    private Bitmap imageZoom(Bitmap bitMap, double maxSize) throws OutOfMemoryError {
        boolean isOutOfMemoryError = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            double mid = b.length / 1024;
            if (mid > maxSize) {
                double i = mid / maxSize;
                bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            isOutOfMemoryError = true;
            throw new OutOfMemoryError();
        } finally {
            if (isOutOfMemoryError && bitMap != null && !bitMap.isRecycled()) {
                bitMap.recycle();
            }
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
        Bitmap newBitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        if (newBitmap != null) {
            bgimage.recycle();
        }
        return newBitmap;
    }

    //加密图片
    private boolean encryptPicture(String oldPath, String newPath) {
        boolean returnvalue = false;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            if (!isLegal(oldPath)) {
                return false;
            }
            File oldfile = new File(oldPath);
            if (!oldfile.exists()) {
                return false;
            }
            if (!oldfile.isFile()) {
                return false;
            }
            if (!oldfile.canRead()) {
                return false;
            }
            File f = new File(newPath);
            fis = new FileInputStream(oldfile);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            while (bis.read(b) != -1) {
                for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (255 - b[i]);
                }
                bos.write(b);
            }
            bos.flush();
            if (isLegal(f.getPath())) {
                returnvalue = true;
            } else {
                if (f.exists())
                    f.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private File saveBitmap(Bitmap bm, String tempPath, String oPath) throws FileNotFoundException,
            OutOfMemoryError {
        File returnvalue = null;
        FileOutputStream out = null;
        try {
            File f = new File(tempPath);
            out = new FileOutputStream(f);
            ExifInterface exif = null;
            int pointIndex = oPath.lastIndexOf(".");
            exif = new ExifInterface(oPath.substring(0, pointIndex) + "_2.ouye");
            if (is_watermark == 1 && !systemDBHelper.isBindForPicture(oPath)) {
                if (locationStr == null) {
                    locationStr = "";
                } else {
                    locationStr = "\n" + locationStr;
                }
                bm = addWatermark(bm, systemDBHelper.searchForWatermark(oPath));
            }
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            if (exif != null) {
                ExifInterface exif2 = new ExifInterface(f.getPath());
                exif2.setAttribute(ExifInterface.TAG_ORIENTATION, exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                exif2.saveAttributes();
            }
            if (isLegal(f.getPath())) {
                if (encryptPicture(tempPath, oPath)) {
                    returnvalue = new File(oPath);
                    f.delete();
                } else {
                    f.delete();
                    returnvalue = null;
                }
            } else {
                f.delete();
                returnvalue = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            throw new OutOfMemoryError();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bm != null) {
                    bm.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private Bitmap addWatermark(Bitmap bitmap, String msg) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        int width = newBitmap.getWidth();
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAlpha(100);
        paint.setColor(Color.RED);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setTextSize(AppInfo.PaintSize);
        int xNum = width / AppInfo.PaintSize;
        String[] msgs = msg.split("\n");
        int xN = 1;
        for (String str : msgs) {
            if (paint.measureText(str) <= width) {
                canvas.drawText(str, 0, AppInfo.PaintSize * xN++, paint);
            } else {
                int yNum = (int) Math.ceil(str.length() * 1d / xNum);
                int yb = 0;
                for (int i = 1; i <= yNum; i++) {
                    int temp = yb + xNum;
                    if (temp > str.length()) {
                        temp = str.length();
                    }
                    canvas.drawText(str, yb, temp, 0, AppInfo.PaintSize * xN++, paint);
                    yb = temp;
                }
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();//存储
        return newBitmap;
    }

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    public static double location_latitude, location_longitude;
    public String locationStr = "";

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        }
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        setLocationOption();
        mLocationClient.start();
    }

    // 设置相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng point = new LatLng(location_latitude, location_longitude);
            if (mSearch != null)
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }

    private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            ReverseGeoCodeResult.AddressComponent addressComponent = reverseGeoCodeResult.getAddressDetail();
            int index = addressComponent.streetNumber.lastIndexOf("号");
            if (index > 0) {
                try {
                    String str = String.valueOf(addressComponent.streetNumber.charAt(index - 1));
                    Tools.d(str);
                    if (Tools.StringToInt(str) != -1) {
                        addressComponent.streetNumber = addressComponent.streetNumber.substring(0, index + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (addressComponent.province.endsWith("市")) {
                locationStr = addressComponent.city + addressComponent.district +
                        addressComponent.street + addressComponent.streetNumber;
            } else {
                locationStr = addressComponent.province + addressComponent.city +
                        addressComponent.district + addressComponent.street + addressComponent.streetNumber;
            }
        }
    };

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int size = selectImgList.size();
        if (position < size) {
            selectIndex = position;
            if ("0".equals(isphoto)) {//不可用相册
                if ("camera_default".equals(selectImgList.get(position))) {
                    takeListener.onClick(null);
                }
            } else {
                SelectPhotoDialog.showPhotoSelecter(CloseTaskitemPhotographyNextYActivity.this, takeListener, pickListener);
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
            mSearch = null;
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        unregisterReceiver(this);
        DataUploadDialog.dissmisDialog();
    }
}
