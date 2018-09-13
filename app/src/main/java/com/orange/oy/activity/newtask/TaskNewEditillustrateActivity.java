package com.orange.oy.activity.newtask;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.info.ProjectRecListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.photoview.PhotoView;

import java.util.ArrayList;

public class TaskNewEditillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmeditill_title);
        appTitle.settingName("问卷任务");
        appTitle.showBack(this);
    }

    private ArrayList<String> picList = new ArrayList<>();
    private GridView taskitmpg_gridview;
    private TaskitemReqPgAdapter adapter;
    private ArrayList<ProjectRecListInfo> list;
    private String projectid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemeditillustrate);
        initTitle();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        projectid = data.getStringExtra("projectid");
        imageLoader = new ImageLoader(this);
        list = (ArrayList<ProjectRecListInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        ProjectRecListInfo projectRecListInfo = list.get(0);
        ((TextView) findViewById(R.id.taskitmeditill_name)).setText(projectRecListInfo.getTaskname());
        ((TextView) findViewById(R.id.taskitmeditill_desc)).setText(projectRecListInfo.getNote());
        taskitmpg_gridview = (GridView) findViewById(R.id.taskitmpg_gridview);
        adapter = new TaskitemReqPgAdapter(this, picList);
        taskitmpg_gridview.setAdapter(adapter);
        String picStr = projectRecListInfo.getPics();
        if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 4) {
            findViewById(R.id.shili).setVisibility(View.GONE);
            findViewById(R.id.taskitmpg_gridview).setVisibility(View.GONE);
        } else {
            picStr = picStr.substring(1, picStr.length() - 1);
            String[] pics = picStr.split(",");
            for (int i = 0; i < pics.length; i++) {
                picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
            }
            if (pics.length > 0) {
                int t = (int) Math.ceil(pics.length / 3d);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmpg_gridview.getLayoutParams();
                lp.height = (int) ((Tools.getScreeInfoWidth(TaskNewEditillustrateActivity.this) -
                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                taskitmpg_gridview.setLayoutParams(lp);
            }
            adapter.notifyDataSetChanged();
        }
        findViewById(R.id.taskitmeditill_button).setOnClickListener(this);
        taskitmpg_gridview.setOnItemClickListener(this);
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.taskitmeditill_button) {
            Intent intent = new Intent(TaskNewEditillustrateActivity.this, TaskNewEditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", list);
            intent.putExtra("data", bundle);
            intent.putExtra("projectid", projectid);
            startActivity(intent);
            baseFinish();
        }
    }

    private ImageLoader imageLoader;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PhotoView imageView = new PhotoView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageLoader.DisplayImage(picList.get(position), imageView);
        SelecterDialog.showView(this, imageView);
    }
}
