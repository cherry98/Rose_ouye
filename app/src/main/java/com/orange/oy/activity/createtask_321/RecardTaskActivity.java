package com.orange.oy.activity.createtask_321;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskPhotoInfo;
import com.orange.oy.info.shakephoto.TaskListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.TagsView;

import static com.orange.oy.R.id.taskphoto_check1;
import static com.orange.oy.R.id.taskphoto_check2;
import static com.orange.oy.R.id.taskphoto_desc;
import static com.orange.oy.R.id.taskphoto_name;


/**
 * V3.21  任务内容-->添加任务---->>>>>>录音任务
 */
public class RecardTaskActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, CompoundButton.OnCheckedChangeListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.record_task_title);
        if ("0".equals(which_page)) {
            appTitle.settingName("编辑录音");
        } else {
            appTitle.settingName("录音任务");
        }
        appTitle.showBack(this);
    }

    private EditText recard_task_name, recard_task_desc;
    private CheckBox recard_task_check;
    private String name, desc, sta_location;
    private String which_page; ////0编辑 1添加
    private int position;
    private TaskListInfo taskListInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recard_task);

        Intent data = getIntent();
        recard_task_name = (EditText) findViewById(R.id.recard_task_name);
        recard_task_desc = (EditText) findViewById(R.id.recard_task_desc);
        recard_task_check = (CheckBox) findViewById(R.id.recard_task_check);

        which_page = data.getStringExtra("which_page");
        position = data.getIntExtra("position", 0);
        initTitle();

        if ("0".equals(which_page)) {//编辑时执行
            taskListInfo = (TaskListInfo) data.getBundleExtra("data").getSerializable("taskListInfo");
            getData();

        } else { //1添加
            if (recard_task_check.isChecked()) {
                sta_location = "1";
            } else {
                sta_location = "0";
            }
        }

        if ("1".equals(sta_location)) {
            recard_task_check.setChecked(true);
        } else {
            recard_task_check.setChecked(false);
        }

        OnClick();
    }

    private String task_id, task_type, task_name, note;

    private void OnClick() {
        //提交
        findViewById(R.id.recard_task_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = recard_task_name.getText().toString().trim();
                if (Tools.isEmpty(name)) {
                    Tools.showToast(RecardTaskActivity.this, "请输入任务名称");
                    return;
                }
                desc = recard_task_desc.getText().toString().trim();

             /*   if (Tools.isEmpty(is_watermark)) {
                    Tools.showToast(RecardTaskActivity.this, "请选择是否获取任务执行位置");
                    return;
                }*/
                //task_type : 任务类型（1：拍照任务；2：视频任务；3：记录/问卷任务；4：定位任务；5录音任务；6：扫码任务；7：电话任务；8：防止翻拍任务）
                TaskListInfo taskListInfo = new TaskListInfo();
                taskListInfo.setTask_id(task_id);
                if (TextUtils.isEmpty(task_type)) {
                    task_type = "5";
                }

                task_name = recard_task_name.getText().toString().trim();
                note = recard_task_desc.getText().toString().trim();

                taskListInfo.setTask_type(task_type);
                taskListInfo.setTask_name(task_name);
                taskListInfo.setNote(note);
                taskListInfo.setSta_location(sta_location);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskListInfo", taskListInfo);
                intent.putExtra("data", bundle);
                intent.putExtra("position", position);
                intent.putExtra("which_page", which_page);
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();

            }
        });
    }

    private void getData() {
        task_id = taskListInfo.getTask_id();
        task_type = taskListInfo.getTask_type();
        task_name = taskListInfo.getTask_name();
        note = taskListInfo.getNote();
        sta_location = taskListInfo.getSta_location();

        recard_task_name.setText(taskListInfo.getTask_name());
        recard_task_desc.setText(taskListInfo.getNote());
        if ("1".equals(sta_location)) {
            recard_task_check.setChecked(true);
        } else {
            recard_task_check.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            sta_location = "1";
        } else {
            sta_location = "0";
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }


}
