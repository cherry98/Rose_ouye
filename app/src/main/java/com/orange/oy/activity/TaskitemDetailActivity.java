package com.orange.oy.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.scan.ScanTaskillustrateActivity;
import com.orange.oy.adapter.TaskitemDetailNewAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ChangeshopDialog;
import com.orange.oy.dialog.CloseTaskDialog;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskitemDetailNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务包列表
 */
public class TaskitemDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView
        .OnItemClickListener, AppTitle.OnExitClickForAppTitle, View.OnClickListener {
    public interface OnRefreshListener {
        void refresh(String packageid);
    }

    private AppTitle taskitdt_title;
    private String searchStr;

    private void initTitle(String title) {
        taskitdt_title = (AppTitle) findViewById(R.id.taskitdt_title);
        taskitdt_title.showBack(this);
        taskitdt_title.settingExit("更改店铺", this);
        taskitdt_title.settingHint("可搜索任务名称、任务包名称");
        taskitdt_title.showSearch(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
            }
        }, new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchStr = v.getText().toString().trim();
                    refreshData();
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    public void onExit() {
//        ArrayList<Map<String, String>> list = new ArrayList<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("124124", "店铺啊啊啊啊");
//        list.add(map);
//        list.add(map);
//        ChangeshopDialog.showDialog(this, list, onItemClickListener);
        if (RecordService.isStart()) {
            Tools.showToast(this, "请先关闭进店录音！！");
        } else {
            Changestore();
        }
    }

    private ChangeshopDialog.OnItemClickListener onItemClickListener = new ChangeshopDialog.OnItemClickListener() {
        public void itemClickForChangeshop(int position, Object object) {
            storeid = storeList.get(position);
            page = 1;
            getData();
        }
    };

    public void onBack() {
        if (RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
            showDialog();
        } else {
            baseFinish();
        }
    }

    protected void onStop() {
        super.onStop();
        if (taskitemDetailAdapter != null) {
            taskitemDetailAdapter.stopUpdata();
        }
        if (Taskindex != null) {
            Taskindex.stop(Urls.Taskindex);
        }
        if (Changestore != null) {
            Changestore.stop(Urls.Changestore);
        }
        TaskListDetailActivity.isRefresh = true;
        TaskscheduleDetailActivity.isRefresh = true;
    }

    private String storeid;
    private int page;

    private void initNetworkConnection() {
        Taskindex = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", storeid);
                params.put("page", page + "");
                params.put("token", Tools.getToken());
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Changestore = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(TaskitemDetailActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Changestore.setIsShowDialog(true);
    }

    private PullToRefreshListView taskitdt_listview_left, taskitdt_listview_right;
    private View taskitdt_recode_layout, taskitdt_recode_layout_line;
    private TaskitemDetailNewAdapter taskitemDetailAdapter;
    private ArrayList<TaskitemDetailNewInfo> list;
    private NetworkConnection Taskindex, Changestore;
    private View taskitdt_start_store, taskitdt_stop_store;
    private String project_id;
    private String photo_compression;
    private String is_desc;
    private String codeStr, brand;
    private int is_watermark;
    private boolean is_takephoto;
    private View taskitdt_stroe_layout;
    private ImageView taskitemdetail_tab_left_ico, taskitemdetail_tab_right_ico;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemdetail);
        initNetworkConnection();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        //TODO 此页面可能会在后台隐藏很长时间，要手动添加变量保存！
        is_takephoto = "1".equals(data.getStringExtra("is_takephoto"));
        storeid = data.getStringExtra("id");
        projectname = data.getStringExtra("projectname");
        project_id = data.getStringExtra("project_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        province = data.getStringExtra("province");
        city = data.getStringExtra("city");
        is_desc = data.getStringExtra("is_desc");
        photo_compression = data.getStringExtra("photo_compression");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_watermark = data.getIntExtra("is_watermark", 0);
        initTitle(projectname);
        list = new ArrayList<>();
        if (is_takephoto) {
            list.add(null);
            list.add(null);
        }
        taskitemDetailAdapter = new TaskitemDetailNewAdapter(this, list);
        taskitemDetailAdapter.setPhoto_compression(photo_compression);
        taskitemDetailAdapter.setShowTitle(true);
        taskitemDetailAdapter.setIs_takephoto(is_takephoto);
        taskitemDetailAdapter.setOnRefushListener(onRefreshListener);
        taskitdt_recode_layout_line = findViewById(R.id.taskitdt_recode_layout_line);
        taskitdt_recode_layout = findViewById(R.id.taskitdt_recode_layout);
        taskitdt_listview_left = (PullToRefreshListView) findViewById(R.id.taskitdt_listview_left);
        taskitdt_listview_right = (PullToRefreshListView) findViewById(R.id.taskitdt_listview_right);
//        findViewById(R.id.taskitemdetail_serial_layout).setOnClickListener(this);
//        findViewById(R.id.taskitemdetail_preview_layout).setOnClickListener(this);
        taskitdt_start_store = findViewById(R.id.taskitdt_start_store);
        taskitdt_stop_store = findViewById(R.id.taskitdt_stop_store);
        taskitdt_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
        taskitdt_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        taskitdt_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
        taskitdt_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        taskitdt_start_store.setOnClickListener(this);
        taskitdt_stop_store.setOnClickListener(this);
        if (RecordService.isStart()) {
            taskitdt_start_store.setVisibility(View.INVISIBLE);
            taskitdt_stop_store.setVisibility(View.VISIBLE);
        } else {
            taskitdt_start_store.setVisibility(View.VISIBLE);
            taskitdt_stop_store.setVisibility(View.INVISIBLE);
        }
        getData();
        isRefresh = false;
        checkPermission();
        long sdsize = Tools.getSDFreeSize();
        if (sdsize < 51) {
            ConfirmDialog.showDialog(this, "提示", getResources().getString(R.string.sdcardsize), null, null, null, true,
                    new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                        }
                    });
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AppInfo
                        .REQUEST_CODE_ASK_RECORD_AUDIO);
                return;
            }
            checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(TaskitemDetailActivity.this, "拍照权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(TaskitemDetailActivity.this, "录音权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static boolean isRefresh;
    public static String taskid;
    public static String packageid;

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            if (list == null) return;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(i);
                if (taskitemDetailNewInfo == null) {
                    continue;
                }
                if (taskid != null && (!taskitemDetailNewInfo.getIsPackage().equals("1")) && taskitemDetailNewInfo.getId().equals
                        (taskid)) {//是任务
                    list.remove(i);
                    taskitemDetailAdapter.notifyDataSetChanged();
                    taskid = null;
                    break;
                } else if (packageid != null && taskitemDetailNewInfo.getIsPackage().equals("1") && packageid.equals
                        (taskitemDetailNewInfo.getId())) {
                    list.remove(i);
                    taskitemDetailAdapter.notifyDataSetChanged();
                    packageid = null;
                    break;
                }
            }
        }
    }

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        public void refresh(String packageid) {
            isRefresh = false;
            if (list == null) return;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(i);
                if (taskitemDetailNewInfo == null) {
                    continue;
                }
                if (taskitemDetailNewInfo.getIsPackage().equals("1") && packageid.equals(taskitemDetailNewInfo.getId())) {//是任务包
                    list.remove(i);
                    taskitemDetailAdapter.notifyDataSetChanged();
                    break;
                }
            }
//            TaskitemDetailActivity.this.refresh();
        }
    };

    private void refreshData() {
        if (list != null) {
            list.clear();
            taskitemDetailAdapter.notifyDataSetChanged();
        }
        page = 1;
        getData();
    }

    private void getData() {
        Taskindex.sendPostRequest(Urls.Taskindex, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (jsonObject.getString("record").equals("1")) {
                        taskitdt_recode_layout.setVisibility(View.VISIBLE);
                        taskitdt_recode_layout_line.setVisibility(View.VISIBLE);
                    }
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            if (list == null) {
                                list = new ArrayList<>();
                                if (is_takephoto) {
                                    list.add(null);
                                    list.add(null);
                                }
                                page = 1;
                                taskitemDetailAdapter.resetList(list);
                            } else {
                                if (page == 1) {
                                    list.clear();
                                    if (is_takephoto) {
                                        list.add(null);
                                        list.add(null);
                                    }
                                }
                            }
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                TaskitemDetailNewInfo taskitemDetailNewInfo = new TaskitemDetailNewInfo();
                                taskitemDetailNewInfo.setIsPackage(jsonObject.getString("is_package"));
                                taskitemDetailNewInfo.setOutlet_batch(jsonObject.getString("outlet_batch"));
                                taskitemDetailNewInfo.setP_batch(jsonObject.getString("p_batch"));
                                taskitemDetailNewInfo.setStoreid(storeid);
                                taskitemDetailNewInfo.setStoreNum(store_num);
                                taskitemDetailNewInfo.setStorename(store_name);
                                taskitemDetailNewInfo.setProjectid(project_id);
                                taskitemDetailNewInfo.setProjectname(projectname);
                                taskitemDetailNewInfo.setCode(codeStr);
                                taskitemDetailNewInfo.setBrand(brand);
                                if (taskitemDetailNewInfo.getIsPackage().equals("1")) {//任务包
                                    taskitemDetailNewInfo.setName(jsonObject.getString("p_name"));
                                    taskitemDetailNewInfo.setId(jsonObject.getString("p_id"));
                                    taskitemDetailNewInfo.setIsClose(jsonObject.getString("is_close"));
                                    taskitemDetailNewInfo.setIs_invalid(jsonObject.getString("is_invalid"));
                                    taskitemDetailNewInfo.setCloseInvalidtype(jsonObject.getString("invalid_type"));
                                    taskitemDetailNewInfo.setCloseTaskname(jsonObject.getString("task_name"));
                                    taskitemDetailNewInfo.setCloseTaskid(jsonObject.getString("task_id"));
                                    taskitemDetailNewInfo.setCloseTasktype(jsonObject.getString("task_type"));
                                    String category = jsonObject.getString("category1_name");
                                    if (TextUtils.isEmpty(category) || category.equals("null")) {
                                        taskitemDetailNewInfo.setIsCategory(false);
                                    } else {
                                        taskitemDetailNewInfo.setIsCategory(true);
                                    }
                                } else {//任务
                                    taskitemDetailNewInfo.setName(jsonObject.getString("task_name"));
                                    taskitemDetailNewInfo.setId(jsonObject.getString("task_id"));
                                    taskitemDetailNewInfo.setTask_type(jsonObject.getString("task_type"));
                                }
                                list.add(taskitemDetailNewInfo);
                            }
                            taskitdt_listview_left.onRefreshComplete();
                            if (length < 15) {
                                taskitdt_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                taskitdt_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            taskitemDetailAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(TaskitemDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemDetailActivity.this, getResources().getString(R.string.network_error));
                }
                taskitdt_listview_left.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                taskitdt_listview_left.onRefreshComplete();
                Tools.showToast(TaskitemDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private ArrayList<String> storeList;

    private void Changestore() {
        Changestore.sendPostRequest(Urls.Changestore, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (storeList == null) {
                            storeList = new ArrayList<>();
                        } else {
                            storeList.clear();
                        }
                        int length = jsonArray.length();
                        ArrayList<Map<String, String>> list = new ArrayList<>();
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            Map<String, String> map = new HashMap<>();
                            map.put(jsonObject.getString("store_code"), jsonObject.getString("si_name"));
                            list.add(map);
                            storeList.add(jsonObject.getString("id"));
                        }
                        ChangeshopDialog.showDialog(TaskitemDetailActivity.this, list, onItemClickListener);
                    } else {
                        String msg = jsonObject.getString("msg");
                        if (TextUtils.isEmpty(msg) || "null".equals(msg)) {
                            Tools.showToast(TaskitemDetailActivity.this, "无店铺");
                        } else {
                            Tools.showToast(TaskitemDetailActivity.this, jsonObject.getString("msg"));
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //TODO 点击事件
        if (!RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
            Tools.showToast(this, "请先点击开始进店按钮");
            return;
        }
        if (is_takephoto) {
            if (position < 3) {
                if (position == 1) {
                    Intent intent = new Intent(TaskitemDetailActivity.this, Camerase.class);
                    intent.putExtra("projectid", project_id);
                    intent.putExtra("storeid", storeid);
                    intent.putExtra("storecode", store_num);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(TaskitemDetailActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("projectid", project_id);
                    bundle.putString("storeid", storeid);
                    bundle.putInt("onlyShow", 1);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);
                }
                return;
            }
        }
        TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(position - 1);
        if (taskitemDetailNewInfo.getIsPackage().equals("1")) {
            if (taskitemDetailNewInfo.getIsClose().equals("1")) {
                Intent intent = new Intent(this, TaskitemListActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", taskitemDetailNewInfo.getId());
                intent.putExtra("pack_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("isCategory", taskitemDetailNewInfo.isCategory());
                intent.putExtra("photo_compression", photo_compression);
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("is_watermark", is_watermark);
                intent.putExtra("code", codeStr);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                startActivity(intent);
            }
        } else {
            if (taskitemDetailNewInfo.getTask_type().equals("1") || taskitemDetailNewInfo.getTask_type().equals("8")) {
                Intent intent = new Intent(this, TaskitemPhotographyActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_type", taskitemDetailNewInfo.getTask_type());
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("photo_compression", photo_compression);
                intent.putExtra("is_watermark", is_watermark);
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", codeStr);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("2")) {
                Intent intent = new Intent(this, TaskitemShotillustrateActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", codeStr);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("3")) {
                Intent intent = new Intent(this, TaskitemEditillustrateActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("taskid", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("tasktype", "3");
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("4")) {
                Intent intent = new Intent(this, TaskitemMapActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", codeStr);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("5")) {
                Intent intent = new Intent(this, TaskitemRecodillustrateActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", codeStr);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("6")) {
                Intent intent = new Intent(this, ScanTaskNewActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", codeStr);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                startActivity(intent);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppInfo.TaskitemDetailRequestCodeForTake: {
                if (resultCode == AppInfo.ShotSuccessResultCode && CloseTaskDialog.isShow()) {
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    if (index != 0) {
                    }
                }
            }
            break;
        }
    }

    protected void onDestroy() {
//        if (RecordService.isStart()) { TODO
//            unbindService(conn);
//        }
        if (RecordService.isStart()) {
            stopService(new Intent(this, RecordService.class));
        }
        CustomProgressDialog.Dissmiss();
        super.onDestroy();
    }

    public boolean isVoicePermission() {
        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            record.release();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitdt_start_store: {
                if (!RecordService.isStart()) {
//                    Intent service = new Intent(this, RecordService.class);
                    if (!isVoicePermission()) {
                        Tools.showToast(this, "录音无法正常启动，请检查权限设置！");
                    } else {
                        Intent service = new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy");
                        service.putExtra("province", province);
                        service.putExtra("usermobile", AppInfo.getName(TaskitemDetailActivity.this));
                        service.putExtra("project_id", project_id);
                        service.putExtra("projectname", projectname);
                        service.putExtra("store_name", store_name);
                        service.putExtra("store_num", store_num);
                        service.putExtra("city", city);
                        service.putExtra("storeid", storeid);
                        service.putExtra("dirName", AppInfo.getName(TaskitemDetailActivity.this) + "/" + storeid);
                        service.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(TaskitemDetailActivity.this) +
                                storeid);
                        service.putExtra("isOffline", false);
                        service.putExtra("code", codeStr);
                        service.putExtra("brand", brand);
                        startService(service);
                        taskitdt_start_store.setVisibility(View.INVISIBLE);
                        taskitdt_stop_store.setVisibility(View.VISIBLE);
                    }
//                    bindService(service, conn, Context.BIND_AUTO_CREATE);
                } else {
                    Tools.showToast(this, "录音已经启动");
                }
            }
            break;
            case R.id.taskitdt_stop_store: {
                try {
                    if (RecordService.isStart()) {
//                        unbindService(conn);
                        Intent service = new Intent(this, RecordService.class);
                        stopService(service);
                        taskitdt_start_store.setVisibility(View.VISIBLE);
                        taskitdt_stop_store.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                }
            }
            break;
        }
    }

    private String projectname, store_name, store_num, province, city;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (CloseTaskDialog.isOpen()) {
                return true;
            } else if (RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
                showDialog();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        ConfirmDialog.showDialog(this, "提示", "退出页面会中止录音，确定退出吗？", null, null, null, true, new ConfirmDialog
                .OnSystemDialogClickListener() {
            public void leftClick(Object object) {
            }

            public void rightClick(Object object) {
                baseFinish();
            }
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            Tools.d("onServiceDisconnected");
            taskitdt_start_store.setVisibility(View.VISIBLE);
            taskitdt_stop_store.setVisibility(View.GONE);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            RecordService.MyBinder binder = (RecordService.MyBinder) service;
            RecordService bindService = binder.getService();
            Intent intent = new Intent();
            intent.putExtra("province", province);
            intent.putExtra("usermobile", AppInfo.getName(TaskitemDetailActivity.this));
            intent.putExtra("projectname", projectname);
            intent.putExtra("store_name", store_name);
            intent.putExtra("store_num", store_num);
            intent.putExtra("city", city);
            intent.putExtra("storeid", storeid);
            bindService.MyMethod(AppInfo.getName(TaskitemDetailActivity.this) + "/" + storeid, "record",
                    false, intent);
            taskitdt_start_store.setVisibility(View.GONE);
            taskitdt_stop_store.setVisibility(View.VISIBLE);
        }
    };
}
