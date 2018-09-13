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

import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 任务分类-拍照任务-任务说明页
 */
public class OfflineTaskitemPhotographyActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener {
    private AppTitle taskitmpg_title;

    private void initTitle(String title) {
        taskitmpg_title = (AppTitle) findViewById(R.id.taskitmpg_title);
        taskitmpg_title.settingName("拍照任务");
        taskitmpg_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
    }

    private String taskid, task_pack_id, task_name, store_id, project_id, project_name, store_num, task_pack_name;
    private TextView taskitmpg_name, taskitmpg_desc;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter;
    private OfflineDBHelper offlineDBHelper;
    private String num, isphoto, photo_type, min_num;
    private GridView taskitmpg_gridview;
    private Intent data;
    private int is_watermark;
    private Button b1, b2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();
        setContentView(R.layout.activity_taskitemphotography);
        offlineDBHelper = new OfflineDBHelper(this);
        imageLoader = new ImageLoader(this);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        project_id = data.getStringExtra("project_id");
        taskid = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        store_id = data.getStringExtra("store_id");
        initTitle(task_name);
        taskitmpg_desc = (TextView) findViewById(R.id.taskitmpg_desc);
        taskitmpg_name = (TextView) findViewById(R.id.taskitmpg_name);
        b1 = (Button) findViewById(R.id.taskitmpg_button);
        b2 = (Button) findViewById(R.id.taskitmpg_button2);
        taskitmpg_gridview = (GridView) findViewById(R.id.taskitmpg_gridview);
        adapter = new TaskitemReqPgAdapter(this, picList);
        adapter.setIsOffline(true);
        taskitmpg_gridview.setAdapter(adapter);
        taskitmpg_gridview.setOnItemClickListener(this);
        getData();
    }

    private String batch;
    private String outlet_batch = null, p_batch = null;

    /**
     * 说明：
     * wuxiao：1为可置无效，0为不可置无效
     * isphoto：1为可调用相册，0为不可调用相册
     * photo_type：备注类型,1为单备注，2为多备注
     * sta_location：是否可启用定位，1为可启用，0为不可启用
     * num:拍照数量
     */
    private void getData() {
        String username = AppInfo.getName(this);
        String result = offlineDBHelper.getTaskDetail(username, project_id, store_id, task_pack_id, taskid);
        outlet_batch = offlineDBHelper.getTaskOutletBatch(username, project_id, store_id, task_pack_id, taskid);
        p_batch = offlineDBHelper.getTaskPBatch(username, project_id, store_id, task_pack_id, taskid);
        Tools.d(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
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
                    lp.height = (int) ((Tools.getScreeInfoWidth(OfflineTaskitemPhotographyActivity.this) -
                            getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                            getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                    taskitmpg_gridview.setLayoutParams(lp);
                }
                adapter.notifyDataSetChanged();
            }
            b1.setOnClickListener(OfflineTaskitemPhotographyActivity.this);
            b2.setOnClickListener(OfflineTaskitemPhotographyActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmpg_button: {
                if (outlet_batch == null || p_batch == null) {
                    Tools.showToast(this, getResources().getString(R.string.batch_error));
                    return;
                }
                data.setClass(this, OfflineTaskitemPhotographyNextYActivity.class);
                data.putExtra("tasktype", "1");
                data.putExtra("photo_type", photo_type);
                data.putExtra("num", num);
                data.putExtra("min_num", min_num);
                data.putExtra("isphoto", isphoto);
                data.putExtra("batch", batch);
                data.putExtra("outlet_batch", outlet_batch);
                data.putExtra("p_batch", p_batch);
                data.putExtra("is_watermark", is_watermark);
                startActivity(data);
                baseFinish();
            }
            break;
            case R.id.taskitmpg_button2: {
                if (outlet_batch == null || p_batch == null) {
                    Tools.showToast(this, getResources().getString(R.string.batch_error));
                    return;
                }
                data.setClass(this, OfflineTaskitemPhotographyNextNActivity.class);
                data.putExtra("tasktype", "1");
                data.putExtra("photo_type", photo_type);
                data.putExtra("num", num);
                data.putExtra("isphoto", isphoto);
                data.putExtra("batch", batch);
                data.putExtra("outlet_batch", outlet_batch);
                data.putExtra("p_batch", p_batch);
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
