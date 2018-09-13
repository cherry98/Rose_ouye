package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
 * 问卷任务说明页
 */
public class OfflineTaskitemEditillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmeditill_title);
        appTitle.settingName("问卷任务");
        appTitle.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    private TextView taskitmeditill_desc, taskitmeditill_name;
    private Intent data;
    private String task_id;
    private OfflineDBHelper offlineDBHelper;
    private String task_pack_id, project_id, store_id;
    private GridView taskitmpg_gridview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemeditillustrate);
        offlineDBHelper = new OfflineDBHelper(this);
        initTitle();
        data = getIntent();
        project_id = data.getStringExtra("project_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        store_id = data.getStringExtra("store_id");
        task_id = data.getStringExtra("taskid");
        taskitmeditill_desc = (TextView) findViewById(R.id.taskitmeditill_desc);
        taskitmeditill_name = (TextView) findViewById(R.id.taskitmeditill_name);
        taskitmpg_gridview = (GridView) findViewById(R.id.taskitmpg_gridview);
        adapter = new TaskitemReqPgAdapter(this, picList);
        adapter.setIsOffline(true);
        taskitmpg_gridview.setAdapter(adapter);
        taskitmpg_gridview.setOnItemClickListener(this);
        findViewById(R.id.taskitmeditill_button).setOnClickListener(this);
        Recorddesc();
    }

    private String batch;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter;

    private void Recorddesc() {
        String result = offlineDBHelper.getTaskDetail(AppInfo.getName(this), project_id, store_id, task_pack_id, task_id);
        try {
            JSONObject jsonObject = new JSONObject(result);
            taskitmeditill_name.setText(jsonObject.getString("task_name"));
            taskitmeditill_desc.setText(jsonObject.getString("note"));
            batch = jsonObject.getString("batch");
            String picStr = jsonObject.getString("pics");
            if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 4) {
                findViewById(R.id.shili).setVisibility(View.GONE);
                taskitmpg_gridview.setVisibility(View.GONE);
            } else {
                picStr = picStr.substring(1, picStr.length() - 1);
                String[] pics = picStr.split(",");
                String name = AppInfo.getName(this);
                for (int i = 0; i < pics.length; i++) {
                    String temp = pics[i].replaceAll("\"", "").replaceAll("\\\\", "");
                    if (!TextUtils.isEmpty(temp) && temp.length() > 1) {
                        String urlstr = Urls.ImgIp + temp;
                        String path = offlineDBHelper.getDownPath(name, project_id, store_id, task_pack_id, task_id, urlstr);
                        if (!TextUtils.isEmpty(path))
                            picList.add(path);
                    }
                }
                if (picList.size() > 0) {
                    int t = (int) Math.ceil(pics.length / 3d);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmpg_gridview.getLayoutParams();
                    lp.height = (int) ((Tools.getScreeInfoWidth(OfflineTaskitemEditillustrateActivity.this) -
                            getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                            getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                    taskitmpg_gridview.setLayoutParams(lp);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmeditill_button: {
                data.setClass(this, OfflineTaskitemEditActivity.class);
                data.putExtra("batch", batch);
                startActivity(data);
                baseFinish();
            }
            break;
        }
    }

    private ImageLoader imageLoader;

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this);
        }
        PhotoView imageView = new PhotoView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageLoader.DisplayImage(picList.get(position), imageView);
        SelecterDialog.showView(this, imageView);
    }
}
