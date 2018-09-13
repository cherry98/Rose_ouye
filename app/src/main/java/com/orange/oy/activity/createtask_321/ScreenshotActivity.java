package com.orange.oy.activity.createtask_321;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.TaskitemListActivity_12;
import com.orange.oy.activity.shakephoto_318.ThemeDetailActivity;
import com.orange.oy.adapter.mycorps_314.ScreenshotAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.WebpageComListInfo;
import com.orange.oy.info.WebpagetaskDBInfo;
import com.orange.oy.info.shakephoto.ScreenshotInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 查看截图页面 V3.21
 */
public class ScreenshotActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.screenshot_title);
        appTitle.settingName("查看截图");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        experienceTaskDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("task_id", task_id);
                params.put("storeid", storeid);
                params.put("outlet_batch", outlet_batch);
                if (!Tools.isEmpty(pid)) {
                    params.put("pid", pid);
                }
                if (!Tools.isEmpty(p_batch)) {
                    params.put("p_batch", p_batch);
                }
                return params;
            }
        };
        printscreenList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ScreenshotActivity.this));
                params.put("page_url", page_url);
                return params;
            }
        };
        printscreenList.setIsShowDialog(true);
        experienceTaskComplete = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ScreenshotActivity.this));
                if (TextUtils.isEmpty(pid)) {
                    pid = "";
                }
                params.put("pid", pid);
                params.put("task_id", task_id);
                params.put("storeid", storeid);
                params.put("task_batch", task_batch);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                return params;
            }
        };
        experienceTaskComplete.setIsShowDialog(true);
    }

    private NetworkConnection experienceTaskDetail, printscreenList, experienceTaskComplete;
    private String task_id, storeid, pid, p_batch, outlet_batch;
    private ArrayList<ScreenshotInfo> list;
    private ScreenshotAdapter screenshotAdapter;
    private String which_page;//0-->执行任务 1-->查看详情（执行完成）
    private SystemDBHelper systemDBHelper;
    private String projectid, page_url;
    private UpdataDBHelper updataDBHelper;
    private PullToRefreshListView screenshot_listview;
    private String project_name, store_num, p_name, task_name, task_batch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);
        systemDBHelper = new SystemDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        list = new ArrayList<>();
        initTitle();
        Intent data = getIntent();
        task_id = data.getStringExtra("task_id");
        storeid = data.getStringExtra("storeid");
        pid = data.getStringExtra("pid");
        p_batch = data.getStringExtra("p_batch");
        outlet_batch = data.getStringExtra("outlet_batch");
        which_page = data.getStringExtra("which_page");
        projectid = data.getStringExtra("projectid");
        outlet_batch = data.getStringExtra("outlet_batch");
        task_batch = data.getStringExtra("taskbath");
        project_name = data.getStringExtra("project_name");
        store_num = data.getStringExtra("store_num");
        p_name = data.getStringExtra("p_name");
        task_name = data.getStringExtra("task_name");
        initNetwork();
        screenshot_listview = (PullToRefreshListView) findViewById(R.id.screenshot_listview);
        screenshot_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        screenshotAdapter = new ScreenshotAdapter(this, list);
        screenshot_listview.setAdapter(screenshotAdapter);
        screenshotAdapter.setOnScreenshotItemClickListener(onScreenshotItemClickListener);
    }

    protected void onResume() {
        super.onResume();
        if ("1".equals(which_page)) {
            getData();
            findViewById(R.id.screenshot_end).setVisibility(View.GONE);
        } else {
            getFromDBData();
            findViewById(R.id.screenshot_end).setVisibility(View.VISIBLE);
            findViewById(R.id.screenshot_end).setOnClickListener(this);
        }
    }

    private ScreenshotAdapter.OnScreenshotItemClickListener onScreenshotItemClickListener =
            new ScreenshotAdapter.OnScreenshotItemClickListener() {
                public void screeshotitemClick(int parPosition, int position) {
                    int clickPosition = 0;
                    if ("1".equals(which_page)) {
                        Tools.d("parPosition:" + parPosition + ",position:" + position);
                        for (int i = 0; i < parPosition; i++) {
                            clickPosition += list.get(i).getPrintscreen_list().size();
                        }
                        clickPosition += position;
                        Intent intent = new Intent(ScreenshotActivity.this, LargeImageWebtaskActivity.class);
                        intent.putExtra("position", clickPosition);
                        intent.putExtra("listinfo", listinfo);
                        startActivity(intent);
                    } else {
                        String str = list.get(parPosition).getPrintscreen_list().get(position).getFile_url();
                        int size = listinfo.size();
                        for (int i = 0; i < size; i++) {
                            WebpagetaskDBInfo webpagetaskDBInfo = listinfo.get(i);
                            if (webpagetaskDBInfo.getPath().contains(str)) {
                                clickPosition = i;
                                break;
                            }
                        }
                        Intent intent = new Intent(ScreenshotActivity.this, LargeImageWebtaskActivity.class);
                        intent.putExtra("position", clickPosition);
                        intent.putExtra("task_id", task_id);
                        intent.putExtra("store_id", storeid);
                        intent.putExtra("task_bath", task_batch);
                        intent.putExtra("project_id", projectid);
                        startActivity(intent);
                    }
                }
            };
    private ArrayList<WebpagetaskDBInfo> listinfo;//上传时候使用

    private void getFromDBData() {
        listinfo = systemDBHelper.getWebpagephoto(projectid, storeid, task_id, task_batch, AppInfo.getName(this));
        Set<String> set = new LinkedHashSet<>();
        for (int i = 0; i < listinfo.size(); i++) {
            set.add(listinfo.get(i).getWebUrl());
        }
        JSONArray jsonArray = new JSONArray();
        for (String str : set) {
            jsonArray.put(str);
        }
        page_url = jsonArray.toString();
        printscreenList();
    }

    private void printscreenList() {
        printscreenList.sendPostRequest(Urls.PrintscreenList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("page_list");
                        if (jsonArray != null) {
                            list.clear();
                            int size = jsonArray.length();
                            int listinfoSize = listinfo.size();
                            ArrayList<Integer> add = new ArrayList<Integer>();
                            for (int i = 0; i < size; i++) {
                                ScreenshotInfo screenshotInfo = new ScreenshotInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                String page_url = object.getString("page_url");
                                String praise_num = object.getString("praise_num");
                                for (int j = 0; j < listinfoSize; j++) {
                                    WebpagetaskDBInfo webpagetaskDBInfo = listinfo.get(j);
                                    if (!add.contains(j) && webpagetaskDBInfo.getWebUrl().equals(page_url)) {
                                        if (screenshotInfo.getPrintscreen_list() == null) {
                                            screenshotInfo.setPage_url(page_url);
                                            screenshotInfo.setPraise_num(praise_num);
                                            screenshotInfo.setPage_name(webpagetaskDBInfo.getWebName());
                                            ArrayList<ScreenshotInfo.PrintscreenListBean> temp2 = new ArrayList<>();
                                            ScreenshotInfo.PrintscreenListBean listBean = new ScreenshotInfo.PrintscreenListBean();
                                            listBean.setComment_content(webpagetaskDBInfo.getCommentTxt());
                                            listBean.setComment_type(webpagetaskDBInfo.getCommentState());
                                            listBean.setFile_url(webpagetaskDBInfo.getPath());
                                            temp2.add(listBean);
                                            screenshotInfo.setPrintscreen_list(temp2);
                                        } else {
                                            ArrayList<ScreenshotInfo.PrintscreenListBean> temp2 = screenshotInfo.getPrintscreen_list();
                                            ScreenshotInfo.PrintscreenListBean listBean = new ScreenshotInfo.PrintscreenListBean();
                                            listBean.setComment_content(webpagetaskDBInfo.getCommentTxt());
                                            listBean.setComment_type(webpagetaskDBInfo.getCommentState());
                                            listBean.setFile_url(webpagetaskDBInfo.getPath());
                                            temp2.add(listBean);
                                        }
                                        add.add(j);
                                    }
                                }
                                list.add(screenshotInfo);
                            }
                            if (screenshotAdapter != null) {
                                screenshotAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(ScreenshotActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ScreenshotActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ScreenshotActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void getData() {
        experienceTaskDetail.sendPostRequest(Urls.ExperienceTaskDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        list.clear();
                        jsonObject = jsonObject.getJSONObject("data");
                        appTitle.settingExit("共" + jsonObject.getString("total_num") + "张");
                        JSONArray jsonArray = jsonObject.optJSONArray("page_list");
                        if (jsonArray != null) {
                            if (listinfo == null) {
                                listinfo = new ArrayList<WebpagetaskDBInfo>();
                            } else {
                                listinfo.clear();
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ScreenshotInfo screenshotInfo = new ScreenshotInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                screenshotInfo.setPage_name(object.getString("page_name"));
                                screenshotInfo.setPraise_num(object.getString("praise_num"));
                                screenshotInfo.setPage_url(object.getString("page_url"));
                                JSONArray array = object.optJSONArray("printscreen_list");
                                if (array != null) {
                                    ArrayList<ScreenshotInfo.PrintscreenListBean> listBeens = new ArrayList<ScreenshotInfo.PrintscreenListBean>();
                                    for (int j = 0; j < array.length(); j++) {
                                        ScreenshotInfo.PrintscreenListBean listBean = new ScreenshotInfo.PrintscreenListBean();
                                        JSONObject object1 = array.getJSONObject(j);
                                        listBean.setComment_content(object1.getString("comment_content"));
                                        listBean.setComment_type(object1.getString("comment_type"));
                                        listBean.setFi_id(object1.getString("fi_id"));
                                        listBean.setFile_url(Urls.Endpoint3 + object1.getString("file_url"));
                                        listBeens.add(listBean);
                                        WebpagetaskDBInfo webpagetaskDBInfo = new WebpagetaskDBInfo();
                                        webpagetaskDBInfo.setPath(listBean.getFile_url());
                                        webpagetaskDBInfo.setCommentState(listBean.getComment_type());
                                        webpagetaskDBInfo.setCommentTxt(listBean.getComment_content());
                                        listinfo.add(webpagetaskDBInfo);
                                    }
                                    screenshotInfo.setPrintscreen_list(listBeens);
                                }
                                list.add(screenshotInfo);
                            }
                            if (screenshotAdapter != null) {
                                screenshotAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(ScreenshotActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ScreenshotActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ScreenshotActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    private int position;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.screenshot_end) {//结束体验
            if (isAllSign()) {//已全部标记
                sendData();
            } else {
                ConfirmDialog.showDialog(this, "提示！", 3, "亲，请将您逛网店的感受标记在截图上，越详细越容易通过审核哦~", null, "去标记",
                        null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                screenshotAdapter.notifyDataSetInvalidated();//通知adapter数据有变化
                                ListView listView = screenshot_listview.getRefreshableView();
                                listView.setSelection(position);//定位到未标记的行
                            }
                        }).goneLeft();
            }
        }
    }

    public boolean isAllSign() {
        boolean isAll = true;
        for (int i = 0; i < list.size(); i++) {
            ScreenshotInfo screenshotInfo = list.get(i);
            for (int j = 0; j < screenshotInfo.getPrintscreen_list().size(); j++) {
                ScreenshotInfo.PrintscreenListBean listBean = screenshotInfo.getPrintscreen_list().get(j);
                if (Tools.isEmpty(listBean.getComment_type())) {
                    position = i;
                    isAll = false;
                    break;
                }
            }
        }
        return isAll;
    }

    private void sendData() {
        experienceTaskComplete.sendPostRequest(Urls.ExperienceTaskComplete, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String executeid = jsonObject.getString("executeid");
                        String username = AppInfo.getName(ScreenshotActivity.this);
                        Map<String, String> params = new HashMap<>();
                        params.put("task_id", task_id);
                        params.put("user_mobile", username);
                        params.put("executeid", executeid);
                        params.put("task_pack_id", pid);
                        params.put("storeid", storeid);
                        params.put("outlet_batch", outlet_batch);
                        params.put("outlet_batch", outlet_batch);
                        params.put("p_batch", p_batch);
                        params.put("batch", task_batch);
                        int size = listinfo.size();
                        String imgs = "";
                        String key = "";
                        size = listinfo.size();
                        for (int i = 0; i < size; i++) {
                            String path = listinfo.get(i).getPath();
                            if (path.equals("camera_default")) {
                                break;
                            }
                            if (TextUtils.isEmpty(imgs)) {
                                imgs = listinfo.get(i).getPath();
                            } else {
                                imgs = imgs + "," + listinfo.get(i).getPath();
                            }
                            if (TextUtils.isEmpty(key)) {
                                key = "img" + (i + 1);
                            } else {
                                key = key + ",img" + (i + 1);
                            }
                        }
                        updataDBHelper.addUpdataTask(username, projectid, project_name, store_num, "",
                                storeid, store_num, pid,
                                p_name, "tyjt", task_id, task_name, null, null
                                , null, username + projectid + storeid + p_name + task_id,
                                Urls.Filecomplete,
                                key, imgs, UpdataDBHelper.Updata_file_type_img, params, "",
                                true, Urls.ExperienceTaskComplete, paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        TaskitemListActivity_12.isRefresh = true;
                        TaskitemDetailActivity_12.isRefresh = true;
                        ScreenManager.getScreenManager().finishActivity(WebpageTaskActivity.class);
                        baseFinish();
                    } else {
                        Tools.showToast(ScreenshotActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(ScreenshotActivity.this, getResources().getString(R.string
                            .network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ScreenshotActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    private String paramsToString() {
        Map<String, String> params = new HashMap<>();
        params.put("usermobile", AppInfo.getName(ScreenshotActivity.this));
        params.put("pid", pid);
        params.put("task_id", task_id);
        params.put("storeid", storeid);
        params.put("task_batch", task_batch);
        params.put("p_batch", p_batch);
        params.put("p_batch", p_batch);
        params.put("outlet_batch", outlet_batch);
        String data = "";
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + params.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + params.get(key).trim();
                }
            }
        }
        return data;
    }
}
