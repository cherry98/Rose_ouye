package com.orange.oy.activity.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orange.oy.R;
import com.orange.oy.activity.TaskitemEditActivity;
import com.orange.oy.activity.TaskitemMapActivity;
import com.orange.oy.activity.TaskitemPhotographyNextYActivity;
import com.orange.oy.activity.TaskitemRecodillustrateActivity;
import com.orange.oy.activity.TaskitemShotActivity;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.scan.ScanTaskillustrateActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskNewInfo;

import java.util.ArrayList;

public class TaskGuideActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void onBackPressed() {
        if ("0".equals(type)) {
            super.onBackPressed();
        }
    }

    private ViewPager taskguide_viewpager;
    private ArrayList<ImageView> list;
    private int[] resId = new int[]{R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3, R.mipmap.guide_4, R.mipmap.guide_5, R.mipmap.guide_6, R.mipmap.guide_7};
    private PagerAdapter pagerAdapter;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_guide);
        type = getIntent().getStringExtra("type");
        ImageView taskguide_skip = (ImageView) findViewById(R.id.taskguide_skip);
        if ("0".equals(type)) {
            taskguide_skip.setVisibility(View.GONE);
            taskguide_skip.setOnClickListener(null);
        } else {
            taskguide_skip.setVisibility(View.VISIBLE);
            taskguide_skip.setOnClickListener(this);
        }
        taskguide_viewpager = (ViewPager) findViewById(R.id.taskguide_viewpager);
        list = new ArrayList<>();
        for (int i = 0; i < resId.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(resId[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            list.add(imageView);
        }
        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(list.get(position));
                return list.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(list.get(position));
            }
        };
        taskguide_viewpager.setAdapter(pagerAdapter);
        //下面实现联动
        taskguide_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageView taskguide_end = (ImageView) findViewById(R.id.taskguide_end);
                if (position == resId.length - 1) {
                    taskguide_end.setVisibility(View.VISIBLE);
                    taskguide_end.setOnClickListener(TaskGuideActivity.this);
                    if ("1".equals(type)) {//登录页面跳转
                        taskguide_end.setImageResource(R.mipmap.startexperience);
                    } else if ("0".equals(type)) {
                        taskguide_end.setImageResource(R.mipmap.know);
                    }
                } else {
                    taskguide_end.setVisibility(View.GONE);
                    taskguide_end.setOnClickListener(null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if ("1".equals(type)) {
            if (v.getId() == R.id.taskguide_end) {
                startExperience();
            } else if (v.getId() == R.id.taskguide_skip) {
                startExperience();
            }
        } else if ("0".equals(type)) {
            if (v.getId() == R.id.taskguide_end) {
                baseFinish();
            } else if (v.getId() == R.id.taskguide_skip) {
                baseFinish();
            }
        }
    }

    public void startExperience() {
        ArrayList<TaskNewInfo> list = (ArrayList<TaskNewInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        if (list != null) {
            if (!list.isEmpty()) {
                TaskNewInfo taskNewInfo = list.remove(0);
                String type = taskNewInfo.getTask_type();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", list);
                intent.putExtra("data", bundle);
                intent.putExtra("project_id", taskNewInfo.getProjectid());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskNewInfo.getTask_id());
                intent.putExtra("task_name", taskNewInfo.getTask_name());
                intent.putExtra("store_id", taskNewInfo.getStore_id());
                intent.putExtra("store_num", taskNewInfo.getStore_num());
                intent.putExtra("store_name", taskNewInfo.getStore_name());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("is_desc", "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("outlet_batch", taskNewInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskNewInfo.getP_batch());
                intent.putExtra("newtask", "1");//判断是否是新手任务 1是0否
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                if ("1".equals(type)) {//拍照任务
                    intent.setClass(TaskGuideActivity.this, TaskitemPhotographyNextYActivity.class);
                    startActivity(intent);
                } else if ("2".equals(type)) {//视频任务
                    intent.setClass(TaskGuideActivity.this, TaskitemShotActivity.class);
                    startActivity(intent);
                } else if ("3".equals(type)) {//记录任务
                    intent.setClass(TaskGuideActivity.this, TaskitemEditActivity.class);
                    startActivity(intent);
                } else if ("4".equals(type)) {//定位任务
                    intent.setClass(TaskGuideActivity.this, TaskitemMapActivity.class);
                    startActivity(intent);
                } else if ("5".equals(type)) {//录音任务
                    intent.setClass(TaskGuideActivity.this, TaskitemRecodillustrateActivity.class);
                    startActivity(intent);
                } else if ("6".equals(type)) {//扫码任务
                    intent.setClass(TaskGuideActivity.this, ScanTaskNewActivity.class);
                    startActivity(intent);
                }
                AppInfo.setIsShow(TaskGuideActivity.this, false);
                baseFinish();
            }
        }
    }
}
