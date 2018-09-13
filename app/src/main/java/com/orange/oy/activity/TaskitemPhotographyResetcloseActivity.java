package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 拍照任务置无效详情
 */
public class TaskitemPhotographyResetcloseActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle
        , View.OnClickListener {

    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitempgnexty_title_reset);
        appTitle.settingName("拍照任务");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        photo = new NetworkConnection(this) {
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
        photo.setIsShowDialog(true);
        photoReDo = new NetworkConnection(this) {
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
        photoReDo.setIsShowDialog(true);
    }

    private NetworkConnection photo, photoReDo;
    private String username;
    private String project_id, store_id, task_pack_id, category1 = "", category2 = "", category3 = "", task_id,
            project_name, task_name, task_pack_name,
            store_num, store_name, outlet_batch, p_batch, photo_compression, codeStr, brand, is_desc, batch;
    private String is_watermark;
    private ImageView taskitempgnextn_video1, taskitempgnextn_video2, taskitempgnextn_video3;
    private TextView taskitempgnexty_text_reset;
    private UpdataDBHelper updataDBHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitem_photography_resetclose);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        Intent data = getIntent();
        username = AppInfo.getName(this);
        initTitle(data.getStringExtra("task_name"));
        project_id = data.getStringExtra("project_id");
        store_id = data.getStringExtra("store_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_id = data.getStringExtra("task_id");
        project_name = data.getStringExtra("project_name");
        task_name = data.getStringExtra("task_name");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_num = data.getStringExtra("store_num");
        store_name = data.getStringExtra("store_name");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        photo_compression = data.getStringExtra("photo_compression");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_desc = data.getStringExtra("is_desc");
        is_watermark = data.getStringExtra("is_watermark");
        findViewById(R.id.taskitempgnexty_button_reset).setOnClickListener(this);
        taskitempgnextn_video1 = (ImageView) findViewById(R.id.taskitempgnextn_video1);
        taskitempgnextn_video2 = (ImageView) findViewById(R.id.taskitempgnextn_video2);
        taskitempgnextn_video3 = (ImageView) findViewById(R.id.taskitempgnextn_video3);
        taskitempgnexty_text_reset = (TextView) findViewById(R.id.taskitempgnexty_text_reset);
        getData();
    }

    protected void onStop() {
        super.onStop();
        if (photo != null) {
            photo.stop(Urls.TaskFinish);
        }
    }

    public void getData() {
        photo.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        String beizhu = jsonObject.getString("beizhu");
                        if (!"null".equals(beizhu)) {
                            taskitempgnexty_text_reset.setText(beizhu.replaceAll("\\[\"", "")
                                    .replaceAll("\"]", ""));
                        }
                        String video_datas = jsonObject.getString("photo_datas");
                        if (!TextUtils.isEmpty(video_datas) && !"null".equals(video_datas)) {
                            video_datas = video_datas.replaceAll("\\[\"", "").replaceAll("\"]", "");
                            if (!TextUtils.isEmpty(video_datas)) {//有链接
                                String[] vs = video_datas.split("\",\"");
                                settingValue(vs);
                            }
                        } else {//找本地
                            String[] paths = updataDBHelper.getTaskFiles(username, project_id, store_id, task_pack_id,
                                    null, null, null, task_id);
                            if (paths != null && paths.length > 0) {
                                settingValue(paths);
                            }
                        }
                    } else {
                        Tools.showToast(TaskitemPhotographyResetcloseActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskitemPhotographyResetcloseActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemPhotographyResetcloseActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    public void settingValue(String[] shots) {
        if (shots.length >= 1 && !TextUtils.isEmpty(shots[0])) {
            taskitempgnextn_video1.setVisibility(View.VISIBLE);
            taskitempgnextn_video1.setTag(shots[0].replaceAll("\\\\", ""));
            File f = new File(shots[0]);
            if (f.isFile()) {
                taskitempgnextn_video1.setImageBitmap(Tools.createVideoThumbnail(shots[0]));
            } else {
                taskitempgnextn_video1.setImageResource(R.mipmap.bof);
                new getVideoThumbnail(1).execute(new Object[]{shots[0]});
            }
            taskitempgnextn_video1.setOnClickListener(this);
        }
        if (shots.length >= 2 && !TextUtils.isEmpty(shots[1])) {
            taskitempgnextn_video2.setVisibility(View.VISIBLE);
            taskitempgnextn_video2.setTag(shots[1]);
            File f = new File(shots[1].replaceAll("\\\\", ""));
            if (f.isFile()) {
                taskitempgnextn_video2.setImageBitmap(Tools.createVideoThumbnail(shots[1]));
            } else {
                taskitempgnextn_video2.setImageResource(R.mipmap.bof);
                new getVideoThumbnail(2).execute(new Object[]{shots[1]});
            }
            taskitempgnextn_video2.setOnClickListener(this);
        }
        if (shots.length >= 3 && !TextUtils.isEmpty(shots[2])) {
            taskitempgnextn_video3.setVisibility(View.VISIBLE);
            taskitempgnextn_video3.setTag(shots[2]);
            File f = new File(shots[2].replaceAll("\\\\", ""));
            if (f.isFile()) {
                taskitempgnextn_video3.setImageBitmap(Tools.createVideoThumbnail(shots[2]));
            } else {
                taskitempgnextn_video3.setImageResource(R.mipmap.bof);
                new getVideoThumbnail(3).execute(new Object[]{shots[2]});
            }
            taskitempgnextn_video3.setOnClickListener(this);
        }
    }

    class getVideoThumbnail extends AsyncTask {
        private int index;

        getVideoThumbnail(int index) {
            this.index = index;
        }

        protected Object doInBackground(Object[] params) {
            String url = null;
            try {
                url = URLDecoder.decode(params[0].toString(), "utf-8");
                return Tools.createVideoThumbnail(url, 400, 300);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Object o) {
            if (o != null) {
                switch (index) {
                    case 1: {
                        if (taskitempgnextn_video1 != null)
                            taskitempgnextn_video1.setImageBitmap((Bitmap) o);
                    }
                    break;
                    case 2: {
                        if (taskitempgnextn_video2 != null)
                            taskitempgnextn_video2.setImageBitmap((Bitmap) o);
                    }
                    break;
                    case 3: {
                        if (taskitempgnextn_video3 != null)
                            taskitempgnextn_video3.setImageBitmap((Bitmap) o);
                    }
                    break;
                }
            }
        }
    }

    public void onBack() {
        baseFinish();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitempgnextn_video1: {
                Intent intent = new Intent(this, VideoViewActivity.class);
                intent.putExtra("path", taskitempgnextn_video1.getTag().toString());
                startActivity(intent);
            }
            break;
            case R.id.taskitempgnextn_video2: {
                Intent intent = new Intent(this, VideoViewActivity.class);
                intent.putExtra("path", taskitempgnextn_video2.getTag().toString());
                startActivity(intent);
            }
            break;
            case R.id.taskitempgnextn_video3: {
                Intent intent = new Intent(this, VideoViewActivity.class);
                intent.putExtra("path", taskitempgnextn_video3.getTag().toString());
                startActivity(intent);
            }
            break;
            case R.id.taskitempgnexty_button_reset:
                ConfirmDialog.showDialog(TaskitemPhotographyResetcloseActivity.this, "确定重做吗？", null, null, null, null
                        , true, new ConfirmDialog.OnSystemDialogClickListener() {
                            public void leftClick(Object object) {
                            }

                            public void rightClick(Object object) {
                                photoReDo.sendPostRequest(Urls.TaskReDo, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(s);
                                            if (jsonObject.optInt("code") == 200) {
                                                Intent intent = new Intent(TaskitemPhotographyResetcloseActivity.this,
                                                        TaskitemPhotographyNextYActivity.class);
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
                                                intent.putExtra("photo_compression", photo_compression);
                                                intent.putExtra("p_batch", p_batch);
                                                intent.putExtra("is_watermark", is_watermark);
                                                intent.putExtra("is_desc", is_desc);
                                                intent.putExtra("code", codeStr);
                                                intent.putExtra("brand", brand);
                                                startActivity(intent);
                                                TaskitemDetailActivity_12.isRefresh = true;
                                                baseFinish();
                                            } else {
                                                Tools.showToast(TaskitemPhotographyResetcloseActivity.this, jsonObject
                                                        .optString("msg"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Tools.showToast(TaskitemPhotographyResetcloseActivity.this, getResources()
                                                    .getString(R.string
                                                            .network_error));
                                        } finally {
                                            CustomProgressDialog.Dissmiss();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        CustomProgressDialog.Dissmiss();
                                        Tools.showToast(TaskitemPhotographyResetcloseActivity.this, getResources().getString(R
                                                .string
                                                .network_volleyerror));
                                    }
                                }, null);
                            }
                        });
                break;
        }
    }
}
