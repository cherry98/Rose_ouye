package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyMediaController;
import com.orange.oy.view.MyVideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.surface_view;


public class TaskitemShotResetActivity extends BaseActivity implements View.OnClickListener, AppTitle.OnBackClickForAppTitle {
    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitemshot_title_reset);
        appTitle.settingName("视频任务");
        appTitle.showBack(this);
        appTitle.settingExit("重做");
        appTitle.settingExitColor(getResources().getColor(R.color.meau_textcolor_sel));

        appTitle.showExit(new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                ConfirmDialog.showDialog(TaskitemShotResetActivity.this, "确定重做吗？", null, null, null, null
                        , true, new ConfirmDialog.OnSystemDialogClickListener() {
                            public void leftClick(Object object) {
                            }

                            public void rightClick(Object object) {
                                vedioReDo.sendPostRequest(Urls.TaskReDo, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        Tools.d(s);
                                        appDBHelper.deletePhotoUrl(project_id, store_id, task_id);
                                        try {
                                            JSONObject jsonObject = new JSONObject(s);
                                            if (jsonObject.optInt("code") == 200) {
                                                Intent in = new Intent(TaskitemShotResetActivity.this,
                                                        TaskitemShotActivity.class);
                                                in.putExtra("task_pack_id", task_pack_id);
                                                in.putExtra("task_id", task_id);
                                                in.putExtra("store_id", store_id);
                                                in.putExtra("category1", category1);
                                                in.putExtra("category2", category2);
                                                in.putExtra("category3", category3);
                                                in.putExtra("project_id", project_id);
                                                in.putExtra("project_name", project_name);
                                                in.putExtra("task_pack_name", task_pack_name);
                                                in.putExtra("task_name", task_name);
                                                in.putExtra("store_num", store_num);
                                                in.putExtra("store_name", store_name);
                                                in.putExtra("outlet_batch", outlet_batch);
                                                in.putExtra("p_batch", p_batch);
                                                in.putExtra("is_desc", is_desc);
                                                in.putExtra("code", codeStr);
                                                in.putExtra("brand", brand);
                                                startActivity(in);
                                                TaskitemDetailActivity_12.isRefresh = true;
                                                baseFinish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Tools.showToast(TaskitemShotResetActivity.this, getResources().getString(R
                                                    .string.network_error));
                                        }
                                        CustomProgressDialog.Dissmiss();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        CustomProgressDialog.Dissmiss();
                                        Tools.showToast(TaskitemShotResetActivity.this, getResources().getString(R.string
                                                .network_volleyerror));
                                    }
                                }, null);
                            }
                        });
            }
        });

    }

    private void initNetworkConnection() {
        vedio = new NetworkConnection(this) {
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
        vedio.setIsShowDialog(true);
        vedioReDo = new NetworkConnection(this) {
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
        vedioReDo.setIsShowDialog(true);
    }

    private NetworkConnection vedio, vedioReDo;
    private Intent data;
    private UpdataDBHelper updataDBHelper;
    private String username;
    private String project_id, store_id, task_pack_id, category1, category2, category3, task_id, project_name,
            task_name, task_pack_name, store_num, store_name, outlet_batch, p_batch, is_desc, codeStr, brand;
    private ImageView taskitmshotill_shotplay_reset;
    private AppDBHelper appDBHelper;
    private TextView taskitemshot_video_title, taskitemshot_desc;
    private RelativeLayout taskitemshot_video_layout;
    private ImageView spread_button, taskitemshot_shotimg, viewfdt_shot_video4;
    private MyVideoView myVideoView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitem_shot_reset);
        data = getIntent();
        updataDBHelper = new UpdataDBHelper(this);
        appDBHelper = new AppDBHelper(this);
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
        codeStr = data.getStringExtra("codeStr");
        brand = data.getStringExtra("brand");

        taskitemshot_video_title = (TextView) findViewById(R.id.taskitemshot_video_title);
        taskitemshot_video_layout = (RelativeLayout) findViewById(R.id.taskitemshot_video_layout);
        taskitemshot_desc = (TextView) findViewById(R.id.taskitemshot_desc);
        spread_button = (ImageView) findViewById(R.id.spread_button);
        taskitemshot_shotimg = (ImageView) findViewById(R.id.taskitemshot_shotimg);


        initNetworkConnection();
        getData();
        findViewById(R.id.spread_button_layout).setOnClickListener(this);
        findViewById(R.id.taskitemshot_shot_play).setOnClickListener(this);

        myVideoView = (MyVideoView) findViewById(R.id.viewfdt_shot_videoview);
        MyMediaController myMediaController = (MyMediaController) findViewById(R.id.viewfdt_shot_mediacontroller);
        myVideoView.setMediaController(myMediaController);
        myVideoView.setVisibility(View.GONE);
        viewfdt_shot_video4 = (ImageView) findViewById(R.id.viewfdt_shot_video4);
        taskitmshotill_shotplay_reset = (ImageView) findViewById(R.id.taskitmshotill_shotplay_reset);
        taskitmshotill_shotplay_reset.setOnClickListener(this);
        viewfdt_shot_video4.setVisibility(View.VISIBLE);
        taskitmshotill_shotplay_reset.setVisibility(View.VISIBLE);
    }

    private int totoll = 1;

    public void onClick(View v) {
        if (code == 200) {
            switch (v.getId()) {
                case R.id.taskitemshot_shot_play: {
                    Intent intent = new Intent(this, VideoViewActivity.class);
                    intent.putExtra("path", taskitemshot_shotimg.getTag().toString());
                    startActivity(intent);
                }
                break;

                case R.id.spread_button_layout:
                    //初始状态,  totoll == 1是展开
                    if (totoll == 1) {
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        taskitemshot_desc.setSingleLine(false);
                        if (!TextUtils.isEmpty(taskitemshot_shotimg.getTag().toString())) {
                            findViewById(R.id.taskitemshot_video_title).setVisibility(View.VISIBLE);
                            findViewById(R.id.taskitemshot_video_layout).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                            findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                        }
                        totoll = 2;
                    } else {
                        //收缩
                        totoll = 1;
                        if (taskitemshot_desc.getLineCount() > 1) {
                            spread_button.setImageResource(R.mipmap.spread_button_up);
                            taskitemshot_desc.setSingleLine(true);
                            findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                            findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                        }
                    }

                    break;

                case R.id.taskitmshotill_shotplay_reset: {
                    if (myVideoView == null) return;
                    if (vs != null) {
                        String path = vs[0];
                        try {
                            path = URLDecoder.decode(path, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            Tools.d(path);
                            path = "";
                        }
                        if (TextUtils.isEmpty(path)) {
                            Tools.showToast(this, "播放路径异常！");
                            baseFinish();
                        } else {
                            File f = new File(path);
                            if (!f.exists() || !f.isFile()) {
                                if (!path.startsWith("http://"))
                                    path = Urls.VideoIp + path;
                            }
                            myVideoView.setVisibility(View.VISIBLE);
                            myVideoView.setVideoURI(Uri.parse(path));
                            myVideoView.requestFocus();
                            myVideoView.start();
                            viewfdt_shot_video4.setVisibility(View.GONE);
                            taskitmshotill_shotplay_reset.setVisibility(View.GONE);

                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onBack() {
        baseFinish();
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
                        if (viewfdt_shot_video4 != null)
                            viewfdt_shot_video4.setImageBitmap((Bitmap) o);
                    }
                    break;
                }
            }
        }
    }


    class getVideoThumbnail2 extends AsyncTask {
        protected Object doInBackground(Object[] params) {
            try {
                String url = params[0].toString();
                return Tools.createVideoThumbnail(url, 400, 300);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Object o) {
            if (o != null) {
                if (taskitemshot_shotimg != null) {
                    taskitemshot_shotimg.setImageBitmap((Bitmap) o);
                }
            }
        }
    }

    private int code;
    private String[] vs;

    public void getData() {
        vedio.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    code = jsonObject.optInt("code");
                    if (code == 200) {

                        String video_datas = jsonObject.optString("video_datas");
                        String[] beizhu = jsonObject.getString("beizhu").replaceAll("\\[\"", "").replaceAll("\"]", "").split
                                ("\",\"");
                        if (beizhu.length > 0) {
                            if (beizhu[0].equals("null")) {
                                ((TextView) findViewById(R.id.taskitemshot_text_reset)).setText("");
                            } else {
                                ((TextView) findViewById(R.id.taskitemshot_text_reset)).setText(beizhu[0]);
                            }
                        }
                        ((TextView) findViewById(R.id.taskitemshot_name_reset)).setText(jsonObject.optString("task_name"));

                        if (!TextUtils.isEmpty(video_datas) && !"null".equals(video_datas)) {
                            video_datas = video_datas.replaceAll("\\[\"", "").replaceAll("\"]", "");
                            if (!TextUtils.isEmpty(video_datas)) {//有链接
                                vs = video_datas.split("\",\"");
                            } else {
                                String paths[] = updataDBHelper.getTaskFiles(username, project_id, store_id, task_pack_id,
                                        category1, category2, category3, task_id);
                                if (paths != null && paths.length > 0) {
                                    vs = paths;
                                }
                            }
                        } else {
                            String paths[] = updataDBHelper.getTaskFiles(username, project_id, store_id, task_pack_id,
                                    category1, category2, category3, task_id);
                            if (paths != null && paths.length > 0) {
                                vs = paths;
                            }
                        }
                        if (vs != null) {
                            if (vs.length >= 1 && !TextUtils.isEmpty(vs[0])) {
//                            taskitmshotill_shotplay_reset.setTag(vs[0].replaceAll("\\\\", ""));
                                File f = new File(vs[0]);
                                if (f.isFile()) {
                                    viewfdt_shot_video4.setImageBitmap(Tools.createVideoThumbnail(vs[0]));
                                } else {
                                    taskitmshotill_shotplay_reset.setImageResource(R.mipmap.bof);
                                    new getVideoThumbnail(1).execute(new Object[]{vs[0]});
                                }
                            }
                        }

                        JSONObject jsonObject1 = jsonObject.getJSONObject("videotask_data");
                        if (jsonObject1 != null) {
                            jsonObject1.getString("taskName");
                            jsonObject1.getString("taskid");
                            String url = jsonObject1.getString("url");
                            String note = jsonObject1.getString("note");
                            String batch = jsonObject1.getString("batch");
                            int noteType = jsonObject1.getInt("noteType");
                            //Tools.d("tag", "打印=====》》》" + url);
                            taskitemshot_desc.setText(note);
                          /*  taskitemshot_desc.setText("该片讲述了一对男女在过年回家的火车上相识，从那之后，" +
                                    "二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片讲述了一对男女在过年回家的火车上相识，" +
                                    "从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片讲述了一对男女在过年" +
                                    "回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片" +
                                    "讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事");*/

                            if (taskitemshot_desc.getLineCount() > 1) {
                                taskitemshot_desc.setSingleLine(true);
                                findViewById(R.id.spread_button_layout).setOnClickListener(TaskitemShotResetActivity.this);
                                findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                                findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                                findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);

                            } else {
                                findViewById(R.id.spread_button_layout).setVisibility(View.GONE);
                                findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                                findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                            }
                            if (TextUtils.isEmpty(url) || url.equals("null")) {
                                taskitemshot_video_title.setVisibility(View.GONE);
                                taskitemshot_video_layout.setVisibility(View.GONE);
                                taskitemshot_shotimg.setTag(jsonObject1.getString("url"));
                            } else {
                                taskitemshot_shotimg.setTag(jsonObject1.getString("url"));
                            }
                            new getVideoThumbnail2().execute(new Object[]{taskitemshot_shotimg.getTag()});

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskitemShotResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemShotResetActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);


    }

    protected void onPause() {
        super.onPause();
        if (myVideoView != null) {
            myVideoView.pause();
        }
    }

    protected void onResume() {
        super.onResume();
        if (myVideoView != null) {
            myVideoView.resume();
        }
    }

    protected void onStop() {
        super.onStop();
        if (myVideoView != null) {
            myVideoView.stopPlayback();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (myVideoView != null) {
            myVideoView.destroyDrawingCache();
        }
    }
}
