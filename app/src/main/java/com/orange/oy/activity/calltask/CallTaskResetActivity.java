package com.orange.oy.activity.calltask;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskitemDetailActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.TaskitemListActivity;
import com.orange.oy.activity.TaskitemListActivity_12;
import com.orange.oy.activity.TaskitemRecodResetActivity;
import com.orange.oy.activity.scan.ScanTaskillustrateActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.Mp3Model;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.Player;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.RecodePlayView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * 电话任务重做页
 */
public class CallTaskResetActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle appTitle;

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.calltaskreset_title);
        appTitle.settingName("电话任务");
        appTitle.showBack(this);

        appTitle.settingExit("重做");
        appTitle.settingExitColor(Color.parseColor("#F65D57"));
        appTitle.showExit(new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                callReset();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (taskFinish != null) {
            taskFinish.stop(Urls.TaskFinish);
        }
        if (callTaskRedo != null) {
            callTaskRedo.stop(Urls.TaskReDo);
        }

        RecodePlayView.closeAllRecodeplay();
        RecodePlayView.clearRecodePlayViewMap();
    }

    public void initNetworkConnection() {
        taskFinish = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", taskid);
                return params;
            }
        };
        taskFinish.setIsShowDialog(true);
        callTaskRedo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", taskid);
                params.put("usermobile", AppInfo.getName(CallTaskResetActivity.this));
                return params;
            }
        };
        callTaskRedo.setIsShowDialog(true);
    }

    private NetworkConnection taskFinish, callTaskRedo;
    private String store_id, task_pack_id, p_batch, outlet_batch, taskid;
    private Intent data;
    private ImageView iv_control;
    private SeekBar MusicSeekBar;
    private TextView MusicTime;
    private RecodePlayView iv_recodeplayview;
    private String soundStr;
    private List<Mp3Model> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_task_reset);
        iv_control = (ImageView) findViewById(R.id.iv_control);
        MusicSeekBar = (SeekBar) findViewById(R.id.MusicSeekBar);
        MusicTime = (TextView) findViewById(R.id.MusicTime);
        iv_recodeplayview = (RecodePlayView) findViewById(R.id.iv_recodeplayview);
        initTitle();
        initNetworkConnection();
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        store_id = data.getStringExtra("store_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        p_batch = data.getStringExtra("p_batch");
        outlet_batch = data.getStringExtra("outlet_batch");
        taskid = data.getStringExtra("task_id");
        getData();
        findViewById(R.id.calltaskreset_botton).setOnClickListener(this);

    }



    private void getData() {
        taskFinish.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("callinfo");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        String task_note = jsonObject.getString("task_note");
                        soundStr = jsonObject.getString("sound_datas");
                        try {
                            soundStr = URLDecoder.decode(soundStr.replaceAll("\\[\"", "").replaceAll("\"]", ""), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            MobclickAgent.reportError(CallTaskResetActivity.this, "录音地址解析ERROR：" + soundStr);
                            Tools.showToast(CallTaskResetActivity.this, "录音路径解析失败");
                        }
                        Mp3Model mp3Model = new Mp3Model(soundStr);
                        datas.add(mp3Model);
                        iv_recodeplayview.settingREC(datas.get(0).getPath());

                        ((TextView) findViewById(R.id.calltaskreset_note)).setText(task_note);
                       /* ((TextView) findViewById(R.id.calltaskreset_name)).setText("网点名称：" + object.getString("storename"));
                        ((TextView) findViewById(R.id.calltaskreset_num)).setText("电话号码：" + object.getString("telphone"));
                        ((TextView) findViewById(R.id.calltaskreset_time)).setText("拨打电话：" + object.getString("time"));
                        ((TextView) findViewById(R.id.calltaskreset_calltime)).setText("拨打时长：" + object.getString("calltime") + "s");
                        */
                    }
                } catch (JSONException e) {
                    Tools.showToast(CallTaskResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CallTaskResetActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iv_recodeplayview.onFinishView();

    }

    @Override
    public void onBack() {
        baseFinish();
    }


    private void callReset() {
        ConfirmDialog.showDialog(CallTaskResetActivity.this, "确定重做吗？", null, null, null, null, true,
                new ConfirmDialog.OnSystemDialogClickListener() {
                    @Override
                    public void leftClick(Object object) {

                    }

                    @Override
                    public void rightClick(Object object) {
                        callTaskRedo.sendPostRequest(Urls.TaskReDo, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (jsonObject.getInt("code") == 200) {
                                        data.setClass(CallTaskResetActivity.this, CallTaskActivity.class);
                                        startActivity(data);
                                        TaskitemDetailActivity.isRefresh = true;
                                        TaskitemDetailActivity_12.isRefresh = true;
                                        TaskFinishActivity.isRefresh = true;
                                        TaskitemListActivity.isRefresh = true;
                                        TaskitemListActivity_12.isRefresh = true;
                                        baseFinish();
                                    } else {
                                        Tools.showToast(CallTaskResetActivity.this, jsonObject.getString("msg"));
                                    }
                                } catch (JSONException e) {
                                    Tools.showToast(CallTaskResetActivity.this, getResources().getString(R.string.network_error));
                                }
                                CustomProgressDialog.Dissmiss();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Tools.showToast(CallTaskResetActivity.this, getResources().getString(R.string.network_volleyerror));
                                CustomProgressDialog.Dissmiss();
                            }
                        }, null);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.calltaskreset_botton) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
