package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.SpreadTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TaskitemMapResetActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitemmap_title_reset);
        appTitle.settingName("定位任务");
        appTitle.showBack(this);
        appTitle.settingExit("重做");
        appTitle.settingExitColor(Color.parseColor("#F65D57"));
        appTitle.showExit(new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Mapreset();
            }
        });
    }

    private UpdataDBHelper updataDBHelper;
    private String username;
    private String project_id, store_id, task_pack_id, category1, category2, category3, task_id, project_name,
            task_name, task_pack_name, store_num, store_name, outlet_batch, p_batch, is_desc, codeStr, brand;
    private ImageView taskitemmap_mapview_reset;
    private ImageLoader imageLoader;
    private NetworkConnection netMap, netMapReDo;
    private SpreadTextView taskitmrecodill_desc;

    private void initNetworkConnection() {
        netMap = new NetworkConnection(this) {
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
        netMap.setIsShowDialog(true);
        netMapReDo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", task_id);
                params.put("usermobile", username);
                return params;
            }
        };
        netMapReDo.setIsShowDialog(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitem_map_reset);
        taskitmrecodill_desc = (SpreadTextView) findViewById(R.id.taskitmrecodill_desc);
        Intent data = getIntent();
        updataDBHelper = new UpdataDBHelper(this);
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
        is_desc = data.getStringExtra("is_desc");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        taskitemmap_mapview_reset = (ImageView) findViewById(R.id.taskitemmap_mapview_reset);
        initNetworkConnection();
        imageLoader = new ImageLoader(this);
        getData();

    }

    private void Mapreset() {
        ConfirmDialog.showDialog(TaskitemMapResetActivity.this, "确定重做吗？", null, null, null, null
                , true, new ConfirmDialog.OnSystemDialogClickListener() {
                    public void leftClick(Object object) {
                    }

                    public void rightClick(Object object) {
                        netMapReDo.sendPostRequest(Urls.TaskReDo, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (jsonObject.optInt("code") == 200) {
                                        Intent intent = new Intent(TaskitemMapResetActivity.this, TaskitemMapActivity.class);
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
                                        TaskitemMapResetActivity.this.startActivity(intent);
                                        baseFinish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Tools.showToast(TaskitemMapResetActivity.this, getResources().getString(R.string
                                            .network_error));
                                }
                                CustomProgressDialog.Dissmiss();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                CustomProgressDialog.Dissmiss();
                                Tools.showToast(TaskitemMapResetActivity.this, getResources().getString(R.string
                                        .network_volleyerror));
                            }
                        }, null);
                    }
                });
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    public void getData() {
        netMap.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String picStr = jsonObject.getString("positionpic");
                        String tasknote = jsonObject.getString("task_note");
                        taskitmrecodill_desc.setDesc(tasknote);
                        if (!TextUtils.isEmpty(picStr) && !"null".equals(picStr)) {
                            imageLoader.DisplayImage(picStr.replaceAll("\\\\", ""), taskitemmap_mapview_reset);
                        } else {
                            String paths[] = updataDBHelper.getTaskFiles(username, project_id,
                                    store_id,
                                    task_pack_id, category1, category2,
                                    category3, task_id);
                            if (paths != null) {
                                imageLoader.DisplayImage(paths[0].replaceAll("\\\\", ""), taskitemmap_mapview_reset);
                            }
                        }
                        ((TextView) findViewById(R.id.taskitemmap_name_reset)).setText(jsonObject.getString("task_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskitemMapResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemMapResetActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }
}
