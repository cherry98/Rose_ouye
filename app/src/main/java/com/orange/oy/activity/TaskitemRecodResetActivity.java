package com.orange.oy.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.MPAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.Mp3Model;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;
import com.orange.oy.view.RecodePlayView;
import com.orange.oy.view.SpreadTextView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orange.oy.R.id.middle_del;

public class TaskitemRecodResetActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, MPAdapter.MPdelInterface, AppTitle.OnExitClickForAppTitle {
    private void initTitle(String str) {
        appTitle = (AppTitle) findViewById(R.id.taskitemrecod_reset_title);
        appTitle.settingName("录音任务");
        appTitle.showBack(this);
        appTitle.settingExit("编辑", getResources().getColor(R.color.homepage_select), this);
    }

    private boolean isEdit;
    private AppTitle appTitle;

    //点击编辑,出现删除和重做
    @Override
    public void onExit() {
        isEdit = !isEdit;
        if (isEdit) {
            appTitle.settingExit("取消");
            mpAdapter.setVisibility(!mpAdapter.isShow());
            mpAdapter.notifyDataSetChanged();
        } else {
            appTitle.settingExit("编辑");
            mpAdapter.setVisibility(!mpAdapter.isShow());
            mpAdapter.notifyDataSetChanged();
        }
    }

    private void initNetworkConnection() {
        recode = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", task_id);
                return params;
            }
        };
        recode.setIsShowDialog(true);
        soundupdate = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);  //网点id
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);  //任务包id
                params.put("p_batch", p_batch); // 任务包批次
                params.put("outlet_batch", outlet_batch); //网点任务批次
                params.put("taskid", task_id);  //任务id
                params.put("usermobile", username); //用户账号
                params.put("original_url", original_url);  // 原始文件路径【必填】
                params.put("type", "1");  // （0为重做只替换路径，1为删除）

                return params;
            }
        };
        soundupdate.setIsShowDialog(true);
    }

    private NetworkConnection recode, soundupdate;  //// TODO: 2018/4/2
    private Intent data;
    private UpdataDBHelper updataDBHelper;
    private String username;
    private String project_id, store_id, task_pack_id, category1, category2, category3, task_id, project_name,
            task_name, task_pack_name, store_num, store_name, outlet_batch, p_batch, note, codeStr, is_desc, brand;
    private TextView taskitemrecod_reset_text, taskitemrecod_reset_name;
    private MyListView list_video;
    private MPAdapter mpAdapter;
    private String original_url, type, new_url;
    private List<Mp3Model> datas = new ArrayList<>();
    private Mp3Model mp3Model;
    private SpreadTextView spacer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitem_recodillustrate_reset);

        EventBus.getDefault().register(this);
        data = getIntent();
        initTitle(data.getStringExtra("task_name"));
        username = AppInfo.getName(this);
        project_id = data.getStringExtra("project_id");
        store_id = data.getStringExtra("store_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        task_id = data.getStringExtra("task_id");
        project_name = data.getStringExtra("project_name");
        task_name = data.getStringExtra("task_name");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_num = data.getStringExtra("store_num");
        store_name = data.getStringExtra("store_name");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        codeStr = data.getStringExtra("code");
        is_desc = data.getStringExtra("is_desc");
        brand = data.getStringExtra("brand");
        taskitemrecod_reset_text = (TextView) findViewById(R.id.taskitemrecod_reset_text);
        taskitemrecod_reset_name = (TextView) findViewById(R.id.taskitemrecod_reset_name);
        spacer = (SpreadTextView) findViewById(R.id.spacer);
        list_video = (MyListView) findViewById(R.id.list_video);
        initNetworkConnection();
        getData();
        mpAdapter = new MPAdapter(TaskitemRecodResetActivity.this, datas);
        list_video.setAdapter(mpAdapter);
        mpAdapter.setAbandonButtonListener(this);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String data) {
        if (data.equals("5")) {
            datas.clear();
            isEdit = true;
            onExit();
            getData();
        }

        Tools.d("tag", "onEvent===mill====>>" + data);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private int code;
    private String soundStr;
    private String taskNote;

    @Override
    protected void onStop() {
        super.onStop();
        RecodePlayView.closeAllRecodeplay();
        RecodePlayView.clearRecodePlayViewMap();
    }

    public void getData() {
        recode.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    code = jsonObject.getInt("code");
                    if (code == 1) {
                        getDatas();
                    }
                    if (code == 200) {
                        taskNote = jsonObject.getString("task_note");
                        spacer.setDesc(taskNote);
                        soundStr = jsonObject.getString("sound_datas");
                        soundStr = URLDecoder.decode(soundStr, "utf-8");
                        soundStr = soundStr.replaceAll("\\[\"", "").replaceAll("\"]", "");
                        if (!TextUtils.isEmpty(soundStr) && !"null".equals(soundStr)) {
                            if (soundStr.contains(",")) {
                                String[] temp = soundStr.split("\",\"");
                                for (int i = 0; i < temp.length; i++) {
                                    mp3Model = new Mp3Model(temp[i]);
                                    datas.add(mp3Model);
                                }
                            } else {
                                mp3Model = new Mp3Model(soundStr);
                                datas.add(mp3Model);
                            }
                        }
                        mpAdapter.notifyDataSetChanged();
                        taskitemrecod_reset_name.setText(jsonObject.optString("task_name"));
                        Tools.d("tag", "beizhu=============>" + jsonObject.optString("beizhu"));
                        if (jsonObject.optString("beizhu").equals("null")) {
                            taskitemrecod_reset_text.setText("暂无备注");
                        } else {
                            taskitemrecod_reset_text.setText(jsonObject.optString("beizhu").replaceAll("\\[\"", "").replaceAll
                                    ("\"]", ""));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.showToast(TaskitemRecodResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemRecodResetActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        for (int i = 0; i < datas.size(); i++) {
            if (null != datas.get(i).getRecodePlayView()) {
                datas.get(i).getRecodePlayView().onFinishView();
            }
        }
    }


    @Override
    public void Delonclick(int position, String path) {
        original_url = path;
        type = "1";
        ConfirmDialog.showDialog(TaskitemRecodResetActivity.this, "确定删除吗？", null, null, null, null
                , true, new ConfirmDialog.OnSystemDialogClickListener() {
                    public void leftClick(Object object) {
                    }

                    public void rightClick(Object object) {
                        if (!datas.isEmpty()) {
                            if (datas.size() > 1) {
                                delete();
                            } else {//TODO  重做页面
                                delete();
                                //Toast.makeText(getBaseContext(), "点击了重做", Toast.LENGTH_LONG).show();
                                // getDatas();
                            }
                        }

                    }
                });
    }

    @Override
    public void Replyonclick(int position, String path) {
        original_url = path;
        ConfirmDialog.showDialog(TaskitemRecodResetActivity.this, "确定重做吗？", null, null, null, null
                , true, new ConfirmDialog.OnSystemDialogClickListener() {
                    public void leftClick(Object object) {
                    }

                    public void rightClick(Object object) {

                        if (!datas.isEmpty()) {

                            Intent intent = new Intent(TaskitemRecodResetActivity.this, TaskitemRecodActivity.class);
                            intent.putExtra("original_url", original_url);  //原始文件路径
                            intent.putExtra("task_pack_id", task_pack_id);
                            intent.putExtra("task_id", task_id);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("category1", category1);
                            intent.putExtra("category2", category2);
                            intent.putExtra("category3", category3);
                            intent.putExtra("project_id", project_id);
                            intent.putExtra("project_name", project_name);
                            intent.putExtra("task_pack_name", task_pack_name);
                            intent.putExtra("task_name", task_name);
                            intent.putExtra("store_num", store_num);
                            intent.putExtra("store_name", store_name);
                            intent.putExtra("outlet_batch", outlet_batch);
                            intent.putExtra("p_batch", p_batch);
                            intent.putExtra("is_desc", is_desc);
                            intent.putExtra("code", codeStr);
                            intent.putExtra("brand", brand);
                            intent.putExtra("taskNote", taskNote);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void delete() {
        soundupdate.sendPostRequest(Urls.soundupdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        datas.clear();
                        getData();
                        Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(TaskitemRecodResetActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemRecodResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskitemRecodResetActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    //代执行页面
    private void getDatas() {
        Intent intent = new Intent(TaskitemRecodResetActivity.this,
                TaskitemRecodillustrateActivity
                        .class);
        intent.putExtra("task_pack_id", task_pack_id);
        intent.putExtra("task_id", task_id);
        intent.putExtra("store_id", store_id);
        intent.putExtra("category1", category1);
        intent.putExtra("category2", category2);
        intent.putExtra("category3", category3);
        intent.putExtra("project_id", project_id);
        intent.putExtra("project_name", project_name);
        intent.putExtra("task_pack_name", task_pack_name);
        intent.putExtra("task_name", task_name);
        intent.putExtra("store_num", store_num);
        intent.putExtra("store_name", store_name);
        intent.putExtra("outlet_batch", outlet_batch);
        intent.putExtra("p_batch", p_batch);
        intent.putExtra("is_desc", is_desc);
        intent.putExtra("code", codeStr);
        intent.putExtra("brand", brand);
        startActivity(intent);
        TaskitemDetailActivity_12.isRefresh = true;
        baseFinish();
    }

}
