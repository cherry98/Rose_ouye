package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_321.OnlineTaskActivity;
import com.orange.oy.activity.createtask_321.RecardTaskActivity;
import com.orange.oy.activity.createtask_321.TaskRecordActivity;
import com.orange.oy.activity.createtask_321.VideoTaskActivity;
import com.orange.oy.adapter.mycorps_314.TaskContentAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DialogTaskType;
import com.orange.oy.info.shakephoto.TaskListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务列表 V 3.17
 * @author Lenovo
 */
public class TaskContentActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.taskcontent_title);
        appTitle.settingName("任务列表");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle1 = new AppTitle.OnExitClickForAppTitle() {
        @Override
        public void onExit() {
            appTitle.hideIllustrate();
            appTitle.settingExit("完成", onExitClickForAppTitle2);
            if (taskContentAdapter != null) {
                taskContentAdapter.setDelete(true);
                taskContentAdapter.notifyDataSetChanged();
            }
        }
    };
    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle2 = new AppTitle.OnExitClickForAppTitle() {
        @Override
        public void onExit() {
            appTitle.hideExit();
            appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
            if (taskContentAdapter != null) {
                taskContentAdapter.setDelete(false);
                taskContentAdapter.notifyDataSetChanged();
            }
        }
    };
    private TaskContentAdapter taskContentAdapter;
    private ArrayList<TaskListInfo> list;
    private TextView taskcontent_num;
    private String template_id = "";
    private String state; //4是模板但只返回id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_content);
        initTitle();
        state = getIntent().getStringExtra("state");
        if ("4".equals(state)) {
            View taskcontent_sumbit = findViewById(R.id.taskcontent_sumbit);
            taskcontent_sumbit.setVisibility(View.VISIBLE);
            taskcontent_sumbit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("tasklist", list);
                    setResult(RESULT_OK, intent);
                    baseFinish();
                }
            });
            template_id = getIntent().getStringExtra("template_id");
            list = new ArrayList<>();
            getModel();
        } else {
            list = (ArrayList<TaskListInfo>) getIntent().getBundleExtra("data").getSerializable("list_content");
        }
        TaskListInfo taskListInfo = new TaskListInfo();
        taskListInfo.setTask_type("-1");
        list.add(0, taskListInfo);
        if (list.size() <= 1) {
            appTitle.hideIllustrate();
        } else {
            appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
        }
        ListView taskcontent_listview = (ListView) findViewById(R.id.taskcontent_listview);
        taskContentAdapter = new TaskContentAdapter(this, list);
        taskcontent_num = (TextView) findViewById(R.id.taskcontent_num);
        taskcontent_num.setText("可发布的任务总量：" + list.size() + "/15");
        taskcontent_listview.setAdapter(taskContentAdapter);
        taskcontent_listview.setOnItemClickListener(this);
    }

    protected void onStop() {
        super.onStop();
        if (SponsorTemplateDetail != null) {
            SponsorTemplateDetail.stop(Urls.SponsorTemplateDetail);
        }
    }

    private NetworkConnection SponsorTemplateDetail;

    private void getModel() {
        SponsorTemplateDetail = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskContentActivity.this));
                params.put("token", Tools.getToken());
                params.put("template_id", template_id);
                return params;
            }
        };
        SponsorTemplateDetail.setIsShowDialog(true);
        SponsorTemplateDetail.sendPostRequest(Urls.SponsorTemplateDetail, new Response.Listener<String>() {
            public void onResponse(String s) {
                CustomProgressDialog.Dissmiss();
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        taskcontent_num.setText("可发布的任务总量：" + jsonObject.optString("total_num") + "/15");
                        JSONArray jsonArray = jsonObject.optJSONArray("task_list");
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                TaskListInfo taskListInfo = new TaskListInfo();
                                taskListInfo.setIs_watermark(object.optString("is_watermark"));
                                taskListInfo.setLocal_photo(object.optString("local_photo"));
                                taskListInfo.setNote(object.optString("note"));
                                JSONArray photourls = object.optJSONArray("photourl");
                                int count = photourls.length();
                                ArrayList<String> pls = new ArrayList<>();
                                for (int j = 0; j < count; j++) {
                                    pls.add(photourls.getString(j));
                                }
                                taskListInfo.setPhotourl(pls);
                                taskListInfo.setTask_name(object.optString("task_name"));
                                taskListInfo.setTask_id(object.optString("task_id"));
                                taskListInfo.setTask_type(object.optString("task_type"));
                                list.add(taskListInfo);
                            }
                            taskContentAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(TaskContentActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskContentActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskContentActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        if (!"4".equals(state)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("tasklist", list);
            intent.putExtra("data", bundle);
            setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
        }
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (taskContentAdapter != null) {
            final TaskListInfo taskListInfo = list.get(position);
            if (taskContentAdapter.isClick1()) {//编辑任务
                String type = taskListInfo.getTask_type();
                if ("1".equals(type)) {//编辑拍照
                    Intent intent = new Intent(this, TaskPhotoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("taskListInfo", taskListInfo);
                    intent.putExtra("which_page", "0");//编辑
                    intent.putExtra("position", position);
                    intent.putExtra("data", bundle);
                    startActivityForResult(intent, 0);
                } else if ("2".equals(type)) {//视频
                    Intent intent = new Intent(TaskContentActivity.this, VideoTaskActivity.class);
                    intent.putExtra("which_page", "0");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("taskListInfo", taskListInfo);
                    intent.putExtra("position", position);
                    intent.putExtra("data", bundle);
                    startActivityForResult(intent, 4);
                } else if ("3".equals(type)) {//问卷
                    Intent intent = new Intent(TaskContentActivity.this, TaskRecordActivity.class);
                    intent.putExtra("which_page", "1");//添加
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("taskListInfo", taskListInfo);
                    intent.putExtra("position", position);
                    intent.putExtra("data", bundle);
                    startActivityForResult(intent, 5);

                } else if ("5".equals(type)) {//录音
                    Intent intent = new Intent(TaskContentActivity.this, RecardTaskActivity.class);
                    intent.putExtra("which_page", "0");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("taskListInfo", taskListInfo);
                    intent.putExtra("position", position);
                    intent.putExtra("data", bundle);
                    startActivityForResult(intent, 2);
                } else if ("9".equals(type)) {//体验
                    Intent intent = new Intent(TaskContentActivity.this, OnlineTaskActivity.class);
                    intent.putExtra("which_page", "0");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("taskListInfo", taskListInfo);
                    intent.putExtra("position", position);
                    intent.putExtra("data", bundle);
                    startActivityForResult(intent, 3);
                }

            } else if (taskContentAdapter.isClick2()) {//添加任务
                DialogTaskType.showDialog(this, new DialogTaskType.OnTaskTypeSelectListener() {
                    @Override
                    public void select(int type) {
                        if (type == 1) {//拍照
                            Intent intent = new Intent(TaskContentActivity.this, TaskPhotoActivity.class);
                            intent.putExtra("which_page", "1");//添加
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("taskListInfo", taskListInfo);
                            intent.putExtra("position", position);
                            intent.putExtra("data", bundle);
                            startActivityForResult(intent, 1);
                        } else if (type == 2) {//视频
                            Intent intent = new Intent(TaskContentActivity.this, VideoTaskActivity.class);
                            intent.putExtra("which_page", "1");//添加
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("taskListInfo", taskListInfo);
                            intent.putExtra("position", position);
                            intent.putExtra("data", bundle);
                            startActivityForResult(intent, 4);

                        } else if (type == 3) {//录音
                            Intent intent = new Intent(TaskContentActivity.this, RecardTaskActivity.class);
                            intent.putExtra("which_page", "1");//添加
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("taskListInfo", taskListInfo);
                            intent.putExtra("position", position);
                            intent.putExtra("data", bundle);
                            startActivityForResult(intent, 2);
                        } else if (type == 4) {//问卷
                            Intent intent = new Intent(TaskContentActivity.this, TaskRecordActivity.class);
                            intent.putExtra("which_page", "0");//添加
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("taskListInfo", taskListInfo);
                            intent.putExtra("position", position);
                            intent.putExtra("data", bundle);
                            startActivityForResult(intent, 5);
                        } else if (type == 5) {//体验
                            Intent intent = new Intent(TaskContentActivity.this, OnlineTaskActivity.class);
                            intent.putExtra("which_page", "1");//添加
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("taskListInfo", taskListInfo);
                            intent.putExtra("position", position);
                            intent.putExtra("data", bundle);
                            startActivityForResult(intent, 3);
                        }
                    }
                });
            } else if (taskContentAdapter.isClick3()) {//删除按钮
                list.remove(position);
                taskContentAdapter.setDelete(false);
                taskContentAdapter.notifyDataSetChanged();
                if (list.size() <= 1) {
                    appTitle.hideExit();
                    appTitle.hideIllustrate();
                } else {
                    appTitle.hideExit();
                    appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
                }
            }
            taskContentAdapter.clearClick();
        }
    }

    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_COLLECT) {
            switch (requestCode) {
                case 0: {//编辑拍照
                    if (data != null) {
                        editResultData(data);
                    }
                }
                break;
                case 1: {//添加拍照
                    if (data != null) {
                        addResultData(data);
                    }
                }
                break;
                case 2: {  //录音任务
                    //which_page; ////0编辑 1添加
                    String which_page = data.getStringExtra("which_page");
                    if ("1".equals(which_page)) {
                        addResultData(data);
                    } else {
                        editResultData(data);
                    }
                }
                break;
                case 3: { //体验任务
                    //which_page; ////0编辑 1添加
                    String which_page = data.getStringExtra("which_page");

                    if ("1".equals(which_page)) {
                        addResultData(data);
                    } else {
                        editResultData(data);
                    }
                }
                break;
                case 4: {  //视频任务
                    //which_page; ////0编辑 1添加
                    String which_page = data.getStringExtra("which_page");

                    if ("1".equals(which_page)) {
                        addResultData(data);
                    } else {
                        editResultData(data);
                    }
                }
                break;
                case 5: {//问卷任务 1 编辑 0 添加
                    if (data != null) {
                        String which_page = data.getStringExtra("which_page");
                        if ("0".equals(which_page)) {
                            addResultData(data);
                        } else {
                            editResultData(data);
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * 添加任务返回结果
     *
     * @param data
     */
    public void addResultData(Intent data) {
        TaskListInfo taskListInfo = (TaskListInfo) data.getBundleExtra("data").getSerializable("taskListInfo");
        int pos = data.getIntExtra("position", 0);
        list.add(pos + 1, taskListInfo);
        taskContentAdapter.notifyDataSetChanged();
        if (list.size() <= 1) {
            appTitle.hideExit();
            appTitle.hideIllustrate();
        } else {
            appTitle.hideExit();
            appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
        }
    }

    /**
     * 编辑任务返回结果
     *
     * @param data
     */
    public void editResultData(Intent data) {
        int pos = data.getIntExtra("position", 0);
        list.remove(pos);
        TaskListInfo taskListInfo = (TaskListInfo) data.getBundleExtra("data").getSerializable("taskListInfo");
        list.add(pos, taskListInfo);
        taskContentAdapter.notifyDataSetChanged();
        if (list.size() <= 1) {
            appTitle.hideExit();
            appTitle.hideIllustrate();
        } else {
            appTitle.hideExit();
            appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
        }
    }

    @Override
    public void onBackPressed() {
        if (!"4".equals(state)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("tasklist", list);
            intent.putExtra("data", bundle);
            setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
        }
        super.onBackPressed();
    }
}
