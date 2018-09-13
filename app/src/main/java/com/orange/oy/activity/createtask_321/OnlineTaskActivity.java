package com.orange.oy.activity.createtask_321;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.activity.TelephonelistActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.TaskListInfo;
import com.orange.oy.view.AppTitle;

import java.util.regex.Pattern;

import static com.orange.oy.R.id.recard_task_check;


/**
 * V3.21  任务内容-->添加任务---->>>>>>网店体验任务
 */
public class OnlineTaskActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.record_task_title);
        appTitle.settingName("网店体验任务");
        appTitle.showBack(this);
    }

    private EditText recard_task_name, recard_task_desc, recard_task_text, recard_task_shopname;
    private String name, desc, is_watermark, shopname;


    private String which_page; ////0编辑 1添加
    private int position;
    private TaskListInfo taskListInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_task);

        Intent data = getIntent();
        recard_task_name = (EditText) findViewById(R.id.recard_task_name);
        recard_task_desc = (EditText) findViewById(R.id.recard_task_desc);
        recard_task_text = (EditText) findViewById(R.id.recard_task_text);
        recard_task_shopname = (EditText) findViewById(R.id.recard_task_shopname);

        findViewById(R.id.preview_check).setOnClickListener(this);

        which_page = data.getStringExtra("which_page");
        position = data.getIntExtra("position", 0);
        initTitle();
        if ("0".equals(which_page)) {//编辑时执行
            taskListInfo = (TaskListInfo) data.getBundleExtra("data").getSerializable("taskListInfo");
            getData();

        } else { //1添加

        }
        OnClick();

    }

    private void OnClick() {
        //提交
        findViewById(R.id.recard_task_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                desc = recard_task_desc.getText().toString().trim();
                name = recard_task_name.getText().toString().trim();
                shopname = recard_task_shopname.getText().toString().trim();
                if (Tools.isEmpty(name)) {
                    Tools.showToast(OnlineTaskActivity.this, "请输入任务名称");
                    return;
                }

                if (Tools.isEmpty(shopname)) {
                    Tools.showToast(OnlineTaskActivity.this, "请输入目标网店名称");
                    return;
                }

                pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
                Url = recard_task_text.getText().toString().trim();
                if (Tools.isEmpty(Url)) {
                    Tools.showToast(OnlineTaskActivity.this, "请先输入网址");
                    return;
                }
                if (!pattern.matcher(Url).matches()) {
                    Tools.showToast(OnlineTaskActivity.this, "请输入正确的网址链接");
                    return;
                }

                TaskListInfo taskListInfo = new TaskListInfo();
                taskListInfo.setTask_id(task_id);
                if (TextUtils.isEmpty(task_type)) {
                    task_type = "9";
                }

                online_store_name = recard_task_name.getText().toString().trim();
                note = recard_task_desc.getText().toString().trim();
                online_store_url = recard_task_text.getText().toString().trim();

                taskListInfo.setTask_type(task_type);
                taskListInfo.setOnline_store_name(online_store_name);
                taskListInfo.setTask_name(online_store_name);
                taskListInfo.setNote(note);
                taskListInfo.setOnline_store_url(online_store_url);

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

    private String task_id, task_name, online_store_name, online_store_url, note, task_type;

    private void getData() {
        task_name = taskListInfo.getTask_name();
        task_id = taskListInfo.getTask_id();
        task_type = taskListInfo.getTask_type();
        online_store_name = taskListInfo.getOnline_store_name();
        note = taskListInfo.getNote();
        online_store_url = taskListInfo.getOnline_store_url();
        taskListInfo.getTask_name();

        recard_task_name.setText(task_name);
        recard_task_shopname.setText(online_store_name);
        recard_task_desc.setText(note);
        recard_task_text.setText(online_store_url);
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    private String Url; //输入的网址
    private Pattern pattern;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_check: {
                pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
                Url = recard_task_text.getText().toString().trim();
                if (Tools.isEmpty(Url)) {
                    Tools.showToast(OnlineTaskActivity.this, "请先输入网址");
                    return;
                }
                if (!pattern.matcher(Url).matches()) {
                    Tools.showToast(OnlineTaskActivity.this, "请输入正确的网址链接");
                    return;
                }
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("flag", BrowserActivity.flag_readurl);
                intent.putExtra("content", Url);
                startActivity(intent);
            }
            break;
        }
    }
}
