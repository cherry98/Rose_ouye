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
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
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
public class TaskitemPhotographyActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle(String str) {
        AppTitle taskitmpg_title = (AppTitle) findViewById(R.id.taskitmpg_title);
        taskitmpg_title.settingName("拍照任务");
        taskitmpg_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
    }

    private void initNetworkConnection() {
        Photo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Photo.setIsShowDialog(true);
    }

    private TextView taskitmpg_name, taskitmpg_desc;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter;
    private NetworkConnection Photo;
    private Intent data;
    private String taskid;
    private int is_watermark;
    private GridView taskitmpg_gridview;
    private Button b1, b2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();
        setContentView(R.layout.activity_taskitemphotography);
        initNetworkConnection();
        imageLoader = new ImageLoader(this);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        taskid = data.getStringExtra("task_id");
        initTitle(data.getStringExtra("task_name"));
        taskitmpg_desc = (TextView) findViewById(R.id.taskitmpg_desc);
        taskitmpg_name = (TextView) findViewById(R.id.taskitmpg_name);
        b1 = (Button) findViewById(R.id.taskitmpg_button);
        b2 = (Button) findViewById(R.id.taskitmpg_button2);
        taskitmpg_gridview = (GridView) findViewById(R.id.taskitmpg_gridview);
        adapter = new TaskitemReqPgAdapter(this, picList);
        taskitmpg_gridview.setAdapter(adapter);
        taskitmpg_gridview.setOnItemClickListener(this);
        getData();
    }

    private String num, isphoto, photo_type, min_num;
    private String batch;

    /**
     * 说明：
     * wuxiao：1为可置无效，0为不可置无效
     * isphoto：1为可调用相册，0为不可调用相册
     * photo_type：备注类型,1为单备注，2为多备注
     * sta_location：是否可启用定位，1为可启用，0为不可启用
     * num:拍照数量
     */
    private void getData() {
        Photo.sendPostRequest(Urls.Photo, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        taskitmpg_name.setText(jsonObject.getString("name"));
                        taskitmpg_desc.setText(jsonObject.getString("desc"));
                        if ("1".equals(jsonObject.getString("wuxiao"))) {
                            b2.setVisibility(View.VISIBLE);
                        } else {
                            b1.setLayoutParams(findViewById(R.id.taskitmpg_button3).getLayoutParams());
                            b2.setVisibility(View.GONE);
                        }
                        photo_type = jsonObject.getString("photo_type");
                        isphoto = jsonObject.getString("isphoto");
                        num = jsonObject.getString("num");
                        min_num = jsonObject.getString("min_num");
                        batch = jsonObject.getString("batch");
                        is_watermark = Tools.StringToInt(jsonObject.getString("is_watermark"));
                        String picStr = jsonObject.getString("pics");
                        if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 4) {
                            findViewById(R.id.shili).setVisibility(View.GONE);
                            taskitmpg_gridview.setVisibility(View.GONE);
                        } else {
                            picStr = picStr.substring(1, picStr.length() - 1);
                            String[] pics = picStr.split(",");
                            for (int i = 0; i < pics.length; i++) {
                                String temp = pics[i].replaceAll("\"", "").replaceAll("\\\\", "");
                                if (!(temp.startsWith("http://") || temp.startsWith("https://"))) {
                                    temp = Urls.ImgIp + temp;
                                }
                                picList.add(temp);
                            }
                            if (pics.length > 0) {
                                int t = (int) Math.ceil(pics.length / 3d);
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmpg_gridview.getLayoutParams();
                                lp.height = (int) ((Tools.getScreeInfoWidth(TaskitemPhotographyActivity.this) -
                                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                                taskitmpg_gridview.setLayoutParams(lp);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(TaskitemPhotographyActivity.this, jsonObject.getString("msg"));
                    }
                    b1.setOnClickListener(TaskitemPhotographyActivity.this);
                    b2.setOnClickListener(TaskitemPhotographyActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskitemPhotographyActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemPhotographyActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmpg_button: {
                data.setClass(this, TaskitemPhotographyNextYActivity.class);
                data.putExtra("photo_type", photo_type);
                data.putExtra("num", num);
                data.putExtra("min_num", min_num);
                data.putExtra("isphoto", isphoto);
                data.putExtra("batch", batch);
                data.putExtra("is_watermark", is_watermark);
                startActivity(data);
                baseFinish();
            }
            break;
            case R.id.taskitmpg_button2: {
                data.setClass(this, TaskitemPhotographyNextNActivity.class);
                data.putExtra("photo_type", photo_type);
                data.putExtra("num", num);
                data.putExtra("isphoto", isphoto);
                data.putExtra("batch", batch);
                data.putExtra("is_watermark", is_watermark);
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
