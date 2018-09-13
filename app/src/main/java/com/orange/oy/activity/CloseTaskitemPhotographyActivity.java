package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务分类-拍照任务-任务说明页
 */
public class CloseTaskitemPhotographyActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle(String str) {
        AppTitle taskitmpg_title = (AppTitle) findViewById(R.id.taskitmpg_title);
        taskitmpg_title.settingName("拍照任务");
        taskitmpg_title.showBack(this);
    }

    public void onBack() {
        TaskitemDetailActivity_12.isRefresh = true;
        baseFinish();
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
    }

    private TextView taskitmpg_name, taskitmpg_desc;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter;
    private NetworkConnection Closepackagetask;
    private Intent data;
    private String taskid, task_pack_id;
    private int is_watermark;
    private GridView taskitmpg_gridview;
    private Button b1;
    private boolean isOffline;
    private OfflineDBHelper offlineDBHelper;
    private String project_id, store_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();
        setContentView(R.layout.activity_taskitemphotography);
        offlineDBHelper = new OfflineDBHelper(this);
        initNetworkConnection();
        initTitle("");
        imageLoader = new ImageLoader(this);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        project_id = data.getStringExtra("project_id");
        store_id = data.getStringExtra("store_id");
        taskid = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        isOffline = data.getBooleanExtra("isOffline", false);
        taskitmpg_desc = (TextView) findViewById(R.id.taskitmpg_desc);
        taskitmpg_name = (TextView) findViewById(R.id.taskitmpg_name);
        b1 = (Button) findViewById(R.id.taskitmpg_button);
        findViewById(R.id.taskitmpg_button2).setVisibility(View.GONE);
        b1.setLayoutParams(findViewById(R.id.taskitmpg_button3).getLayoutParams());
        taskitmpg_gridview = (GridView) findViewById(R.id.taskitmpg_gridview);
        adapter = new TaskitemReqPgAdapter(this, picList);
        taskitmpg_gridview.setAdapter(adapter);
        taskitmpg_gridview.setOnItemClickListener(this);
        if (isOffline) {//如果是离线
            getDataOffline();
        } else {
            getData();
        }
    }

    private String num, isphoto, photo_type;
    private String outlet_batch = null, p_batch = null;

    private void getDataOffline() {
        String username = AppInfo.getName(this);
        String result = offlineDBHelper.getTaskDetail(username, project_id, store_id, task_pack_id, taskid);
        outlet_batch = offlineDBHelper.getTaskOutletBatch(username, project_id, store_id, task_pack_id, taskid);
        p_batch = offlineDBHelper.getTaskPBatch(username, project_id, store_id, task_pack_id, taskid);
        Tools.d(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            taskitmpg_name.setText(jsonObject.getString("name"));
            taskitmpg_desc.setText(jsonObject.getString("desc"));
            photo_type = jsonObject.getString("photo_type");
            isphoto = jsonObject.getString("isphoto");
            num = jsonObject.getString("num");
            is_watermark = Tools.StringToInt(jsonObject.getString("is_watermark"));
            String picStr = jsonObject.getString("pics");
            picStr = picStr.substring(1, picStr.length() - 1);
            if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 2) {
                findViewById(R.id.shili).setVisibility(View.GONE);
            } else {
                String[] pics = picStr.split(",");
                String name = AppInfo.getName(this);
                for (int i = 0; i < pics.length; i++) {
                    String temp = pics[i].replaceAll("\"", "").replaceAll("\\\\", "");
                    if (!TextUtils.isEmpty(temp) && temp.length() > 1) {
                        String urlstr = Urls.ImgIp + temp;
                        String path = offlineDBHelper.getDownPath(name, project_id, store_id, task_pack_id, taskid, urlstr);
                        if (!TextUtils.isEmpty(path))
                            picList.add(path);
                    }
                }
                if (picList.size() > 0) {
                    int t = (int) Math.ceil(pics.length / 3d);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmpg_gridview.getLayoutParams();
                    lp.height = (int) ((Tools.getScreeInfoWidth(CloseTaskitemPhotographyActivity.this) -
                            getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                            getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                    taskitmpg_gridview.setLayoutParams(lp);
                }
                adapter.notifyDataSetChanged();
            }
            b1.setOnClickListener(CloseTaskitemPhotographyActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 说明：
     * wuxiao：1为可置无效，0为不可置无效
     * isphoto：1为可调用相册，0为不可调用相册
     * photo_type：备注类型,1为单备注，2为多备注
     * sta_location：是否可启用定位，1为可启用，0为不可启用
     * num:拍照数量
     */
    private void getData() {
        Closepackagetask.sendPostRequest(Urls.Closepackagetask, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String task_name = jsonObject.getString("task_name");
                        data.putExtra("task_name", task_name);
                        taskitmpg_name.setText(jsonObject.getString("task_name"));
                        taskitmpg_desc.setText(jsonObject.getString("task_note"));
                        photo_type = jsonObject.getString("note_type");
                        isphoto = jsonObject.getString("is_photo");
                        num = jsonObject.getString("photo_num");
                        is_watermark = Tools.StringToInt(jsonObject.getString("is_watermark"));
                        String picStr = jsonObject.getString("photo_url");
                        picStr = picStr.substring(1, picStr.length() - 1);
                        String[] pics = picStr.split(",");
                        for (int i = 0; i < pics.length; i++) {
                            picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
                        }
                        if (pics.length > 0) {
                            int t = (int) Math.ceil(pics.length / 3d);
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmpg_gridview.getLayoutParams();
                            lp.height = (int) ((Tools.getScreeInfoWidth(CloseTaskitemPhotographyActivity.this) -
                                    getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 - getResources()
                                    .getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                            taskitmpg_gridview.setLayoutParams(lp);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(CloseTaskitemPhotographyActivity.this, jsonObject.getString("msg"));
                    }
                    b1.setOnClickListener(CloseTaskitemPhotographyActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(CloseTaskitemPhotographyActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CloseTaskitemPhotographyActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmpg_button: {
                data.setClass(this, CloseTaskitemPhotographyNextYActivity.class);
                data.putExtra("tasktype", "1");
                data.putExtra("isOffline", isOffline);
                data.putExtra("photo_type", photo_type);
                data.putExtra("num", num);
                data.putExtra("isphoto", isphoto);
                data.putExtra("is_watermark", is_watermark);
                if (isOffline) {
                    data.putExtra("outlet_batch", outlet_batch);
                    data.putExtra("p_batch", p_batch);
                }
                startActivity(data);
                baseFinish();
            }
            break;
        }
    }

    private ImageLoader imageLoader;

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PhotoView imageView = new PhotoView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageLoader.DisplayImage(picList.get(position), imageView);
        SelecterDialog.showView(this, imageView);
    }
}
