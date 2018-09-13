package com.orange.oy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.TaskscheduleDetailAdapter;
import com.orange.oy.adapter.TaskscheduleDetailMiddleAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.MyDialog;
import com.orange.oy.dialog.TaskChangeDialog;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.CharacterParser;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.PinyinComparatorForMyteam;
import com.orange.oy.view.AppTitle;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 进度详情
 */
public class TaskscheduleDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, TaskChangeDialog.OnItemClickListener {
    private AppTitle taskschdetail_title;

    public void onBack() {
        baseFinish();
    }

    private void initTitle() {
        taskschdetail_title = (AppTitle) findViewById(R.id.taskschdetail_title);
        showSearch();
        taskschdetail_title.settingName(getResources().getString(R.string.taskschdetail));
        taskschdetail_title.showBack(this);
    }

    private String searchStr = "";

    public void showSearch() {
        taskschdetail_title.settingHint("可搜索网点名称、网点编号、省份、城市");
        taskschdetail_title.showSearch(new TextWatcher() {
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
                    if (taskschdetail_listview_left.getVisibility() == View.VISIBLE) {
                        Dzxstartlist.setIsShowDialog(true);
                        refreshLeft();
                    } else if (taskschdetail_listview_middle.getVisibility() == View.VISIBLE) {
                        Zxwcstartlist.setIsShowDialog(true);
                        refreshMiddle();
                    } else if (taskschdetail_listview_right.getVisibility() == View.VISIBLE) {
                        Zlyhsstartlist.setIsShowDialog(true);
                        refreshRight();
                    }
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    protected void onStop() {
        super.onStop();
        if (Dzxstartlist != null) {
            Dzxstartlist.stop(Urls.Dzxstartlist);
        }
        if (Zxwcstartlist != null) {
            Zxwcstartlist.stop(Urls.Zxwcstartlist);
        }
        if (Zlyhsstartlist != null) {
            Zlyhsstartlist.stop(Urls.Zlyhsstartlist);
        }
        if (Downloadtasklist != null) {
            Downloadtasklist.stop(Urls.Downloadtasklist);
        }
        if (getTeamData != null) {
            getTeamData.stop(Urls.Myteam);
        }
        if (changeAccessed != null) {
            changeAccessed.stop(Urls.Changeaccessed);
        }
        if (Redo != null) {
            Redo.stop(Urls.Redo);
        }
        if (Selectprojectwcjd != null) {
            Selectprojectwcjd.stop(Urls.Selectprojectwcjd);
        }
        if (Startupload != null) {
            Startupload.stop(Urls.Startupload);
        }
    }

    private String project_id;
    private int page_left, page_middle, page_right;
    private NetworkConnection getTeamData, changeAccessed, Selectprojectwcjd;

    private void initNetworkConnection() {
        getTeamData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TaskscheduleDetailActivity.this));
                return params;
            }
        };
        getTeamData.setIsShowDialog(true);
        changeAccessed = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", storenum);
                params.put("accessednum", accessednum);
                params.put("usermobile", AppInfo.getName(TaskscheduleDetailActivity.this));
                return params;
            }
        };
        changeAccessed.setIsShowDialog(true);
        Dzxstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_left + "");
                params.put("project_id", project_id);
                params.put("city", city);
                params.put("user_mobile", AppInfo.getName(TaskscheduleDetailActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Zxwcstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_middle + "");
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(TaskscheduleDetailActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Zlyhsstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_right + "");
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(TaskscheduleDetailActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Redo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", selStoreid);
                params.put("usermobile", AppInfo.getName(TaskscheduleDetailActivity.this));
                return params;
            }
        };
        Redo.setIsShowDialog(true);
        Downloadtasklist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("store_id", selStoreid);
                return params;
            }
        };
        Downloadtasklist.setIsShowDialog(true);
        Selectprojectwcjd = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(TaskscheduleDetailActivity.this));
                return params;
            }
        };
        Startupload = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", selStoreid);
                params.put("usermobile", AppInfo.getName(TaskscheduleDetailActivity.this));
                return params;
            }
        };
        Startupload.setIsShowDialog(true);
    }

    private String selStoreid;
    private int selMiddlePosition = 0;

    private TextView taskschdetail_tab_left, taskschdetail_tab_middle, taskschdetail_tab_right;
    private TextView taskschdetail_name;
    private View taskschdetail_tab_left_line, taskschdetail_tab_middle_line, taskschdetail_tab_right_line;
    private PullToRefreshListView taskschdetail_listview_left, taskschdetail_listview_middle,
            taskschdetail_listview_right;
    private TaskscheduleDetailAdapter taskscheduleDetailAdapterLeft, taskscheduleDetailAdapterRight;
    private TaskscheduleDetailMiddleAdapter taskscheduleDetailAdapterMiddle;
    private NetworkConnection Dzxstartlist, Zxwcstartlist, Zlyhsstartlist, Redo, Downloadtasklist, Startupload;
    private ArrayList<TaskDetailLeftInfo> list_left, list_middle, list_right;
    private String projectname;
    public static boolean isRefresh;
    private String city;
    private String photo_compression, is_takephoto;
    private OfflineDBHelper offlineDBHelper;
    private UpdataDBHelper updataDBHelper;
    private SystemDBHelper systemDBHelper;
    private String is_record;
    private int is_watermark;
    private String storenum;
    private CharacterParser characterParser;
    private PinyinComparatorForMyteam pinyinComparatorForMyteam;
    private ImageView taskschdetail_tab_left_ico, taskschdetail_tab_right_ico, taskschdetail_tab_middle_ico;
    private TextView taskschdetail_tab_left_small, taskschdetail_tab_right_small, taskschdetail_tab_middle_small;
    private String code, brand;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskscheduledetail);
        offlineDBHelper = new OfflineDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        initNetworkConnection();
        initTitle();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        project_id = data.getStringExtra("project_id");
        projectname = data.getStringExtra("projectname");
        city = data.getStringExtra("city");
        is_record = data.getStringExtra("is_record");
        photo_compression = data.getStringExtra("photo_compression");
        is_takephoto = data.getStringExtra("is_takephoto");
        is_watermark = data.getIntExtra("is_watermark", 0);
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        characterParser = CharacterParser.getInstance();
        pinyinComparatorForMyteam = new PinyinComparatorForMyteam();
        taskschdetail_name = (TextView) findViewById(R.id.taskschdetail_name);
        taskschdetail_name.setText(projectname);
        taskschdetail_tab_left_ico = (ImageView) findViewById(R.id.taskschdetail_tab_left_ico);
        taskschdetail_tab_right_ico = (ImageView) findViewById(R.id.taskschdetail_tab_right_ico);
        taskschdetail_tab_middle_ico = (ImageView) findViewById(R.id.taskschdetail_tab_middle_ico);
        taskschdetail_tab_left_small = (TextView) findViewById(R.id.taskschdetail_tab_left_small);
        taskschdetail_tab_right_small = (TextView) findViewById(R.id.taskschdetail_tab_right_small);
        taskschdetail_tab_middle_small = (TextView) findViewById(R.id.taskschdetail_tab_middle_small);
        taskschdetail_tab_left = (TextView) findViewById(R.id.taskschdetail_tab_left);
        taskschdetail_tab_middle = (TextView) findViewById(R.id.taskschdetail_tab_middle);
        taskschdetail_tab_right = (TextView) findViewById(R.id.taskschdetail_tab_right);
        taskschdetail_tab_left_line = findViewById(R.id.taskschdetail_tab_left_line);
        taskschdetail_tab_middle_line = findViewById(R.id.taskschdetail_tab_middle_line);
        taskschdetail_tab_right_line = findViewById(R.id.taskschdetail_tab_right_line);
        taskschdetail_listview_left = (PullToRefreshListView) findViewById(R.id.taskschdetail_listview_left);
        taskschdetail_listview_middle = (PullToRefreshListView) findViewById(R.id.taskschdetail_listview_middle);
        taskschdetail_listview_right = (PullToRefreshListView) findViewById(R.id.taskschdetail_listview_right);
        initListview(taskschdetail_listview_left);
        initListview(taskschdetail_listview_middle);
        initListview(taskschdetail_listview_right);
        taskschdetail_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshLeft();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_left++;
                getDataForLeft();
            }
        });
        taskschdetail_listview_middle.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshMiddle();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_middle++;
                getDataForMiddle();
            }
        });
        taskschdetail_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshRight();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_right++;
                getDataForRight();
            }
        });
        taskschdetail_listview_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInfo = list_left.get(position - 1);
                String fynum = taskDetailLeftInfo.getNumber();
                switch (taskscheduleDetailAdapterLeft.getSelect()) {
                    case 3: {
                        TextView textView = new TextView(TaskscheduleDetailActivity.this);
                        textView.setBackgroundColor(Color.WHITE);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(15);
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        textView.setText("\n可执行时间\n\n" + taskDetailLeftInfo.getTimedetail());
                        textView.setHeight(Tools.getScreeInfoHeight(TaskscheduleDetailActivity.this) / 2);
                        MyDialog myDialog = new MyDialog(TaskscheduleDetailActivity.this, textView, false, 0);
                        myDialog.setMyDialogWidth(Tools.getScreeInfoWidth(TaskscheduleDetailActivity.this) - 40);
                        myDialog.showAtLocation((TaskscheduleDetailActivity.this.findViewById(R.id.main)),
                                Gravity.CENTER_VERTICAL, 0, 0); //设置layout在PopupWindow中显示的位置
                    }
                    break;
                    case 4: {
                        selStoreid = taskDetailLeftInfo.getId();
                        Downloadtasklist(taskDetailLeftInfo.getTimedetail());
                    }
                    break;
                    case -1: {
                        if (taskscheduleDetailAdapterLeft.isClickButton2()) {//换人
                            storenum = taskDetailLeftInfo.getId();
                            getTeamData();
                        } else if (taskscheduleDetailAdapterLeft.isClickButton()) {//执行
                            if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName
                                    (TaskscheduleDetailActivity.this))) {
                                if (taskDetailLeftInfo.getIs_exe().equals("1")) {
                                    if (taskDetailLeftInfo.getIsOffline() == 1) {
                                        Intent intent = new Intent(TaskscheduleDetailActivity.this, OfflinePackageActivity.class);
                                        intent.putExtra("id", taskDetailLeftInfo.getId());
                                        intent.putExtra("projectname", projectname);
                                        intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                        intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                        intent.putExtra("province", taskDetailLeftInfo.getCity());
                                        intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                        intent.putExtra("project_id", project_id);
                                        intent.putExtra("photo_compression", photo_compression);
                                        intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                                        intent.putExtra("is_watermark", is_watermark);
                                        intent.putExtra("code", code);
                                        intent.putExtra("brand", brand);
                                        intent.putExtra("is_takephoto", is_takephoto);
                                        startActivity(intent);
                                    } else {
                                        if (taskDetailLeftInfo.getIs_desc().equals("1")) {//有网点说明
                                            Intent intent = new Intent(TaskscheduleDetailActivity.this, StoreDescActivity.class);
                                            intent.putExtra("id", taskDetailLeftInfo.getId());
                                            intent.putExtra("projectname", projectname);
                                            intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                            intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                            intent.putExtra("province", taskDetailLeftInfo.getCity());
                                            intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                            intent.putExtra("project_id", project_id);
                                            intent.putExtra("photo_compression", photo_compression);
                                            intent.putExtra("is_desc", "1");
                                            intent.putExtra("is_watermark", is_watermark);
                                            intent.putExtra("code", code);
                                            intent.putExtra("brand", brand);
                                            intent.putExtra("is_takephoto", is_takephoto);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(TaskscheduleDetailActivity.this, TaskitemDetailActivity_12
                                                    .class);
                                            intent.putExtra("id", taskDetailLeftInfo.getId());
                                            intent.putExtra("projectname", projectname);
                                            intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                            intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                            intent.putExtra("province", taskDetailLeftInfo.getCity());
                                            intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                            intent.putExtra("project_id", project_id);
                                            intent.putExtra("photo_compression", photo_compression);
                                            intent.putExtra("is_desc", "0");
                                            intent.putExtra("is_watermark", is_watermark);
                                            intent.putExtra("code", code);
                                            intent.putExtra("brand", brand);
                                            intent.putExtra("is_takephoto", is_takephoto);
                                            startActivity(intent);
                                        }
                                    }
                                } else {
                                    Tools.showToast(TaskscheduleDetailActivity.this, "未到执行时间");
                                }
                            } else {
                                Tools.showToast(TaskscheduleDetailActivity.this, "您不是访员！");
                            }
                        }
                    }
                    break;
                }
                taskscheduleDetailAdapterLeft.clearClickButton();
            }
        });
        taskschdetail_listview_middle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selMiddlePosition = position - 1;
                if (taskscheduleDetailAdapterMiddle.isClickButton2()) {
                    TaskDetailLeftInfo taskDetailLeftInfo = list_middle.get(selMiddlePosition);
                    Intent intent = new Intent(TaskscheduleDetailActivity.this, TaskFinishActivity.class);
                    intent.putExtra("projectname", projectname);
                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("photo_compression", photo_compression);
                    intent.putExtra("store_id", taskDetailLeftInfo.getId());
                    intent.putExtra("state", "1");
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    intent.putExtra("isAgain", false);
                    startActivity(intent);
                } else if (taskscheduleDetailAdapterMiddle.isClickButton()) {
                    ConfirmDialog.showDialog(TaskscheduleDetailActivity.this, "确定重做吗？", null, null, null, list_middle
                            .get(selMiddlePosition), true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            if (object instanceof TaskDetailLeftInfo) {// 重做
                                TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) object;
                                selStoreid = taskDetailLeftInfo.getId();
                                offlineDBHelper.deleteOfflineForRedo(AppInfo.getName(TaskscheduleDetailActivity.this),
                                        project_id, selStoreid);
                                Redo();
                            }
                        }
                    });
                } else if (taskscheduleDetailAdapterMiddle.isClickButton3()) {//整店上传 TODO
                    ConfirmDialog.showDialog(TaskscheduleDetailActivity.this, "确定上传吗？", null, null, null, list_middle
                            .get(selMiddlePosition), true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            if (object instanceof TaskDetailLeftInfo) {// 整店上传
                                TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) object;
                                selStoreid = taskDetailLeftInfo.getId();
                                if (updataDBHelper.startUp(AppInfo.getName(TaskscheduleDetailActivity.this), project_id,
                                        selStoreid)) {
                                    sendStartUpload();
                                } else {
                                    Tools.showToast(TaskscheduleDetailActivity.this, "数据更新失败！");
                                }
                            }
                        }
                    });
                }
                taskscheduleDetailAdapterMiddle.clearClickButton();
            }
        });
        taskschdetail_listview_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (taskscheduleDetailAdapterRight.isClickButton2()) {
                    TaskDetailLeftInfo taskDetailLeftInfo = list_right.get(position - 1);
                    Intent intent = new Intent(TaskscheduleDetailActivity.this, TaskFinishActivity.class);
                    intent.putExtra("projectname", projectname);
                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("photo_compression", photo_compression);
                    intent.putExtra("store_id", taskDetailLeftInfo.getId());
                    intent.putExtra("state", "2");
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    intent.putExtra("isAgain", false);
                    startActivity(intent);
                } else if (taskscheduleDetailAdapterRight.isClickButton()) {
                    ConfirmDialog.showDialog(TaskscheduleDetailActivity.this, "确定重做吗？", null, null, null, list_right
                            .get(position - 1), true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            if (object instanceof TaskDetailLeftInfo) {// 重做
                                TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) object;
                                selStoreid = taskDetailLeftInfo.getId();
                                offlineDBHelper.deleteOfflineForRedo(AppInfo.getName(TaskscheduleDetailActivity.this),
                                        project_id, selStoreid);
                                Redo();
                            }
                        }
                    });
                }
                taskscheduleDetailAdapterRight.clearClickButton();
            }
        });
        taskschdetail_listview_middle.setOnHeaderPullingListener(new PullToRefreshBase.HeaderPullingListener() {
            public void onHeaderPulling(int orientation, int scrollDistance) {
                taskscheduleDetailAdapterMiddle.clearClickButton();
            }
        });
        taskschdetail_listview_right.setOnHeaderPullingListener(new PullToRefreshBase.HeaderPullingListener() {
            public void onHeaderPulling(int orientation, int scrollDistance) {
                taskscheduleDetailAdapterRight.clearClickButton();
            }
        });
        taskschdetail_listview_middle.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        taskscheduleDetailAdapterMiddle.clearClickButton();
                        break;
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        taskschdetail_listview_right.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        taskscheduleDetailAdapterRight.clearClickButton();
                        break;
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        list_left = new ArrayList<>();
        taskscheduleDetailAdapterLeft = new TaskscheduleDetailAdapter(this, list_left, true);
        taskscheduleDetailAdapterLeft.isShowButton(false);
        taskschdetail_listview_left.setAdapter(taskscheduleDetailAdapterLeft);
        list_middle = new ArrayList<>();
        taskscheduleDetailAdapterMiddle = new TaskscheduleDetailMiddleAdapter(this, list_middle);
        taskschdetail_listview_middle.setAdapter(taskscheduleDetailAdapterMiddle);
        list_right = new ArrayList<>();
        taskscheduleDetailAdapterRight = new TaskscheduleDetailAdapter(this, list_right);
        taskscheduleDetailAdapterRight.setShowButton2(false);
        taskscheduleDetailAdapterRight.isShowButton(true);
        taskschdetail_listview_right.setAdapter(taskscheduleDetailAdapterRight);
        View view = findViewById(R.id.taskschdetail_tab_left_layout);
        view.setOnClickListener(this);
        findViewById(R.id.taskschdetail_tab_middle_layout).setOnClickListener(this);
        findViewById(R.id.taskschdetail_tab_right_layout).setOnClickListener(this);
        Selectprojectwcjd();
        checkPermission();
        onClick(view);
        isRefresh = false;
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
                    Tools.showToast(this, "拍照权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "录音权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            Selectprojectwcjd();
            refreshLeft();
        }
    }

    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setPullLabel(getResources().getString(R.string.listview_down));
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));
    }

    private void sendStartUpload() {
        Startupload.sendPostRequest(Urls.Startupload, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (selMiddlePosition >= 0 && selMiddlePosition < list_middle.size()) {
                            list_middle.get(selMiddlePosition).setAgain(false);
                            taskscheduleDetailAdapterMiddle.notifyDataSetChanged();
                            systemDBHelper.packPhotoUpload(TaskscheduleDetailActivity.this, AppInfo.getName
                                    (TaskscheduleDetailActivity.this), project_id, selStoreid);
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                        }
                    } else {
                        Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_error));
                } finally {
                    CustomProgressDialog.Dissmiss();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    /**
     * 下载离线文件
     */
    private void Downloadtasklist(final String time) {
        Downloadtasklist.sendPostRequest(Urls.Downloadtasklist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                CustomProgressDialog.Dissmiss();
                try {
                    AppInfo.addCachesize(TaskscheduleDetailActivity.this, s.getBytes().length);
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if ("1".equals(jsonObject.getString("is_exe"))) {
                            ConfirmDialog.showDialog(TaskscheduleDetailActivity.this, "提示", "该网点已做过任务，下载离线再做会覆盖之前的任务，是否继续？", "否",
                                    "继续", jsonObject, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        public void leftClick(Object object) {
                                        }

                                        public void rightClick(Object object) {
                                            if (object != null) {
                                                try {
                                                    parseData(((JSONObject) object).getJSONArray("datas"), time);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                        } else {
                            parseData(jsonObject.getJSONArray("datas"), time);
                        }
                    } else if (code == 1) {
                        Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string
                            .network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, "正在下载离线....");
    }

    private void parseData(JSONArray jsonArray, String time) throws JSONException {
        Tools.d("解析存储离线数据");
        if (offlineDBHelper == null) return;
        new parseDataAsyncTask(jsonArray, time).executeOnExecutor(Executors.newCachedThreadPool());
    }

    private ArrayList<MyteamNewfdInfo> listForTeam;

    private void getTeamData() {
        getTeamData.sendPostRequest(Urls.Myteam, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (listForTeam == null) {
                        listForTeam = new ArrayList<MyteamNewfdInfo>();
                    } else {
                        listForTeam.clear();
                    }
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        String name, note;
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            MyteamNewfdInfo myteamNewfdInfo = new MyteamNewfdInfo();
                            name = jsonObject.getString("user_name");
                            myteamNewfdInfo.setName(name);
                            myteamNewfdInfo.setImg(Urls.ImgIp + jsonObject.getString("img_url"));
                            myteamNewfdInfo.setId(jsonObject.getString("user_mobile"));
                            note = jsonObject.getString("note");
                            if (!TextUtils.isEmpty(note) && !note.equals("null")) {
                                myteamNewfdInfo.setNote(note);
                                name = note;
                            }
                            //汉字转换成拼音
                            String pinyin = characterParser.getSelling(name);
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                myteamNewfdInfo.setSortLetters(sortString.toUpperCase());
                            } else {
                                myteamNewfdInfo.setSortLetters("#");
                            }
                            listForTeam.add(myteamNewfdInfo);
                        }
                        Collections.sort(listForTeam, pinyinComparatorForMyteam);
                        TaskChangeDialog.showDialog(TaskscheduleDetailActivity.this, listForTeam,
                                TaskscheduleDetailActivity.this);
                    } else {
                        Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                        TaskChangeDialog.showDialog(TaskscheduleDetailActivity.this, listForTeam,
                                TaskscheduleDetailActivity.this);
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    /**
     * 换人接口
     */
    private void changeAccessed() {
        changeAccessed.sendPostRequest(Urls.Changeaccessed, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        refreshLeft();
                    } else {
                        Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String accessednum;

    /**
     * 换人
     *
     * @param myteamNewfdInfo
     */
    public void onItemClick(MyteamNewfdInfo myteamNewfdInfo) {
        accessednum = myteamNewfdInfo.getId();
        new ClearCache().executeOnExecutor(Executors.newCachedThreadPool());
    }

    /**
     * 换成自己
     */
    public void oneself() {
        accessednum = AppInfo.getName(this);
        changeAccessed();
    }

    class ClearCache extends AsyncTask {
        protected void onPreExecute() {
            if (offlineDBHelper == null) {
                offlineDBHelper = new OfflineDBHelper(TaskscheduleDetailActivity.this);
            }
            CustomProgressDialog.showProgressDialog(TaskscheduleDetailActivity.this, "清理缓存");
        }


        protected Object doInBackground(Object[] params) {
            offlineDBHelper.clearCache();
            recurDelete(new File(FileCache.getCacheDir(TaskscheduleDetailActivity.this).getPath() + "/download"));
            AppInfo.clearCachesize(TaskscheduleDetailActivity.this);
            return null;
        }

        public void recurDelete(File f) {
            if (f == null || !f.exists()) {
                return;
            }
            for (File fi : f.listFiles()) {
                if (fi.isDirectory()) {
                    recurDelete(fi);
                } else {
                    fi.delete();
                }
            }
        }

        protected void onPostExecute(Object o) {
            CustomProgressDialog.Dissmiss();
            changeAccessed();
        }
    }

    class parseDataAsyncTask extends AsyncTask {
        JSONArray jsonArray;
        String time;

        parseDataAsyncTask(JSONArray jsonArray, String time) {
            this.jsonArray = jsonArray;
            this.time = time;
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(TaskscheduleDetailActivity.this, "正在离线数据");
        }


        protected Object doInBackground(Object[] params) {
            int length = jsonArray.length();
            boolean isCheck = false;
            String username = AppInfo.getName(TaskscheduleDetailActivity.this);
            boolean issuccess = false;
            try {
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!isCheck) {
                        offlineDBHelper.checkClear(username, jsonObject.getString("project_id"), jsonObject.getString
                                ("store_id"));
                        isCheck = true;
                    }
                    String projectid = jsonObject.getString("project_id");
                    String storeid = jsonObject.getString("store_id");
                    String taskid = jsonObject.getString("task_id");
                    String packageid = jsonObject.getString("p_id");
                    String tasktype = jsonObject.getString("task_type");
                    String taskdetail = jsonObject.getString("task_detail");
//                    issuccess = offlineDBHelper.insertOfflinedata(username, projectid,
//                            jsonObject.getString("project_name"), jsonObject.getString("code"), jsonObject.getString("brand"),
//                            storeid, jsonObject.getString("store_name"),
//                            time, jsonObject.getString("store_num"), jsonObject.getString("store_address"),
//                            jsonObject.getString("accessed_num"), jsonObject.getString("photo_compression"),
//                            ("1".equals(jsonObject.getString("is_watermark"))),
//                            ("1".equals(jsonObject.getString("is_record"))), packageid,
//                            jsonObject.getString("p_name"), jsonObject.getString("category1_name"),
//                            jsonObject.getString("category2_name"), jsonObject.getString("category3_name"),
//                            jsonObject.getString("category1_content"), jsonObject.getString("category2_content"),
//                            jsonObject.getString("category3_content"), ("1".equals(jsonObject.getString("p_is_invalid"))),
//                            ("1".equals(jsonObject.getString("package_attribute"))),
//                            ("1".equals(jsonObject.getString("is_package"))), taskid, jsonObject.getString("task_name"),
//                            tasktype, jsonObject.getString("outlet_batch"), jsonObject.getString("p_batch"),
//                            taskdetail, jsonObject.getString("task_content"));
                    if ("1".equals(tasktype)) {
                        JSONObject jsonObject1 = new JSONObject(taskdetail);
                        String picStr = jsonObject1.getString("pics");
                        picStr = picStr.substring(1, picStr.length() - 1);
                        String[] pics = picStr.split(",");
                        for (int index = 0; index < pics.length; index++) {
                            String temp = pics[index].replaceAll("\"", "").replaceAll("\\\\", "");
                            if (!TextUtils.isEmpty(temp) && temp.length() > 1)
                                offlineDBHelper.insertOfflinedownload(username, projectid, storeid, packageid, taskid, Urls
                                        .ImgIp + temp);
                        }
                    } else if ("2".equals(tasktype)) {
                        JSONObject jsonObject1 = new JSONObject(taskdetail);
                        String url = jsonObject1.getString("url");
                        if (!TextUtils.isEmpty(url) && !url.equals("null")) {
                            offlineDBHelper.insertOfflinedownload(username, projectid, storeid, packageid, taskid, url);
                        }
                    }
                    if (!issuccess) {
                        break;
                    }
                }
            } catch (JSONException e) {
                issuccess = false;
                e.printStackTrace();
            }
            return issuccess;
        }

        protected void onPostExecute(Object o) {
            CustomProgressDialog.Dissmiss();
            if ((boolean) o) {
                Intent service = new Intent("com.orange.oy.DownloadDataService");
                service.setPackage("com.orange.oy");
                startService(service);
                refreshLeft();
            } else {
                Tools.showToast(TaskscheduleDetailActivity.this, "数据存储失败！");
            }
        }
    }

    /**
     * 刷新左侧列表
     */
    private void refreshLeft() {
        page_left = 1;
        getDataForLeft();
    }

    /**
     * 刷新中间列表
     */
    private void refreshMiddle() {
        page_middle = 1;
        getDataForMiddle();
    }

    /**
     * 刷新右侧列表
     */
    private void refreshRight() {
        page_right = 1;
        getDataForRight();
    }

    private void getDataForLeft() {
        Dzxstartlist.sendPostRequest(Urls.Dzxstartlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Dzxstartlist.setIsShowDialog(false);
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list_left == null) {
                            list_left = new ArrayList<TaskDetailLeftInfo>();
                        } else {
                            if (page_left == 1) {
                                list_left.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                            jsonObject = jsonArray.getJSONObject(i);
                            String isdetail = jsonObject.getString("isdetail");
                            String timeDetail = "";
                            if ("0".equals(isdetail)) {
                                String[] datelist = jsonObject.getString("datelist").replaceAll("\\[\"", "").replaceAll("\"]",
                                        "").split("\",\"");
                                for (String str : datelist) {
                                    if (TextUtils.isEmpty(timeDetail)) {
                                        timeDetail += str;
                                    } else {
                                        timeDetail = timeDetail + "\n" + str;
                                    }
                                }
                            } else {
                                for (int index = 1; index < 8; index++) {
                                    String date = jsonObject.getString("date" + index);
                                    if (!TextUtils.isEmpty(date) && !"null".equals(date)) {
                                        String detailtemp = jsonObject.getString("details" + index);
                                        if (!"null".equals(detailtemp)) {
                                            String[] ss = detailtemp.replaceAll("\\[\"", "").replaceAll("\"]", "").split("\",\"");
                                            for (int j = 0; j < ss.length; j++) {
                                                date = date + " " + ((TextUtils.isEmpty(ss[j])) ? "" : ss[j]);
                                            }
                                        }
                                        if (TextUtils.isEmpty(timeDetail)) {
                                            timeDetail = date;
                                        } else {
                                            timeDetail = timeDetail + "\n" + date;
                                        }
                                    }
                                }
                            }
                            taskDetailLeftInfo.setIs_exe(jsonObject.getString("is_exe"));
                            taskDetailLeftInfo.setIs_desc(jsonObject.getString("is_desc"));
                            taskDetailLeftInfo.setId(jsonObject.getString("storeid"));
                            taskDetailLeftInfo.setName(jsonObject.getString("storeName"));
                            taskDetailLeftInfo.setCode(jsonObject.getString("storeNum"));
                            taskDetailLeftInfo.setIdentity(jsonObject.getString("proxy_num"));
                            taskDetailLeftInfo.setCity(jsonObject.getString("province"));
                            taskDetailLeftInfo.setCity2(jsonObject.getString("city"));
                            taskDetailLeftInfo.setCity3(jsonObject.getString("address"));
                            taskDetailLeftInfo.setNumber(jsonObject.getString("accessed_num"));
                            taskDetailLeftInfo.setTimedetail(timeDetail);
                            list_left.add(taskDetailLeftInfo);
                        }
                        try {
                            offlineDBHelper.isCompletedForStore(AppInfo.getName(TaskscheduleDetailActivity.this), project_id,
                                    list_left);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MobclickAgent.reportError(TaskscheduleDetailActivity.this,
                                    "taskscheduledetailactivity error1:" + e.getMessage());
                        }
                        taskschdetail_listview_left.onRefreshComplete();
                        if (length < 15) {
                            taskschdetail_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskschdetail_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskscheduleDetailAdapterLeft.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(TaskscheduleDetailActivity.this,
                            "TaskscheduleDetailActivity getData ParseJson:" + e.getMessage());
                    Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                taskschdetail_listview_left.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Dzxstartlist.setIsShowDialog(false);
                taskschdetail_listview_left.onRefreshComplete();
                Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    private void getDataForMiddle() {
        Zxwcstartlist.sendPostRequest(Urls.Zxwcstartlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Zxwcstartlist.setIsShowDialog(false);
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list_middle == null) {
                            list_middle = new ArrayList<TaskDetailLeftInfo>();
                        } else {
                            if (page_middle == 1) {
                                list_middle.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                            jsonObject = jsonArray.getJSONObject(i);
                            taskDetailLeftInfo.setId(jsonObject.getString("storeid"));
                            taskDetailLeftInfo.setName(jsonObject.getString("storeName"));
                            taskDetailLeftInfo.setCode(jsonObject.getString("storeNum"));
                            taskDetailLeftInfo.setIdentity(jsonObject.getString("proxy_num"));
                            taskDetailLeftInfo.setCity(jsonObject.getString("province"));
                            taskDetailLeftInfo.setCity2(jsonObject.getString("city"));
                            taskDetailLeftInfo.setCity3(jsonObject.getString("address"));
                            taskDetailLeftInfo.setNumber(jsonObject.getString("accessed_num"));
                            taskDetailLeftInfo.setAgain("0".equals(jsonObject.getString("is_upload")));
                            list_middle.add(taskDetailLeftInfo);
                        }
                        taskschdetail_listview_middle.onRefreshComplete();
                        if (length < 15) {
                            taskschdetail_listview_middle.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskschdetail_listview_middle.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskscheduleDetailAdapterMiddle.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_error));
                }
                taskschdetail_listview_middle.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zxwcstartlist.setIsShowDialog(false);
                taskschdetail_listview_middle.onRefreshComplete();
                Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    private void getDataForRight() {
        Zlyhsstartlist.sendPostRequest(Urls.Zlyhsstartlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Zlyhsstartlist.setIsShowDialog(false);
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list_right == null) {
                            list_right = new ArrayList<TaskDetailLeftInfo>();
                        } else {
                            if (page_right == 1) {
                                list_right.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                            jsonObject = jsonArray.getJSONObject(i);
                            taskDetailLeftInfo.setId(jsonObject.getString("storeid"));
                            taskDetailLeftInfo.setName(jsonObject.getString("storeName"));
                            taskDetailLeftInfo.setCode(jsonObject.getString("storeNum"));
                            taskDetailLeftInfo.setIdentity(jsonObject.getString("proxy_num"));
                            taskDetailLeftInfo.setCity(jsonObject.getString("province"));
                            taskDetailLeftInfo.setCity2(jsonObject.getString("city"));
                            taskDetailLeftInfo.setCity3(jsonObject.getString("address"));
                            taskDetailLeftInfo.setNumber(jsonObject.getString("accessed_num"));
                            list_right.add(taskDetailLeftInfo);
                        }
                        taskschdetail_listview_right.onRefreshComplete();
                        if (length < 15) {
                            taskschdetail_listview_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskschdetail_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskscheduleDetailAdapterRight.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_error));
                }
                taskschdetail_listview_right.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zlyhsstartlist.setIsShowDialog(false);
                taskschdetail_listview_right.onRefreshComplete();
                Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    private void Redo() {
        Redo.sendPostRequest(Urls.Redo, new Response.Listener<String>() {
                    public void onResponse(String s) {
                        Tools.d(s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int code = jsonObject.getInt("code");
                            if (code == 200) {
                                String username = AppInfo.getName(TaskscheduleDetailActivity.this);
                                offlineDBHelper.deleteTraffic(username, project_id, selStoreid);
                                updataDBHelper.removeTask(username, project_id, selStoreid);
                                int temp = Tools.StringToInt(taskschdetail_tab_left_small.getText().toString());
                                if (temp == -1) {
                                    temp = 0;
                                }
                                temp++;
                                taskschdetail_tab_left_small.setText((temp > 999) ? "..." : temp + "");
                                temp = Tools.StringToInt(taskschdetail_tab_middle_small.getText().toString());
                                temp--;
                                if (temp == -1) {
                                    temp = 0;
                                }
                                taskschdetail_tab_middle_small.setText((temp > 999) ? "..." : temp + "");
                                refreshMiddle();
                                Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                            } else {
                                Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string
                                    .network_error));
                        }
                        CustomProgressDialog.Dissmiss();
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string
                                .network_volleyerror));
                    }
                }
        );
    }

    private void Selectprojectwcjd() {
        Selectprojectwcjd.sendPostRequest(Urls.Selectprojectwcjd, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        jsonObject = jsonObject.getJSONObject("datas");
                        int temp = Tools.StringToInt(jsonObject.getString("dzxnum"));
                        if (temp == -1) {
                            temp = 0;
                        }
                        taskschdetail_tab_left_small.setText((temp > 999) ? "..." : temp + "");
                        temp = Tools.StringToInt(jsonObject.getString("zlhsnum"));
                        if (temp == -1) {
                            temp = 0;
                        }
                        taskschdetail_tab_right_small.setText((temp > 999) ? "..." : temp + "");
                        temp = Tools.StringToInt(jsonObject.getString("wcnum"));
                        if (temp == -1) {
                            temp = 0;
                        }
                        taskschdetail_tab_middle_small.setText((temp > 999) ? "..." : temp + "");
                    } else {
                        Tools.showToast(TaskscheduleDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskscheduleDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskschdetail_tab_left_layout: {
                taskschdetail_tab_left_ico.setImageResource(R.mipmap.wangdian_dzx_1);
                taskschdetail_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                taskschdetail_tab_left_line.setVisibility(View.VISIBLE);
                taskschdetail_tab_middle_ico.setImageResource(R.mipmap.wangdian_zxwc_2);
                taskschdetail_tab_middle.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                taskschdetail_tab_middle_line.setVisibility(View.INVISIBLE);
                taskschdetail_tab_right_ico.setImageResource(R.mipmap.wangdian_zlyhs_2);
                taskschdetail_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                taskschdetail_tab_right_line.setVisibility(View.INVISIBLE);
                taskschdetail_listview_left.setVisibility(View.VISIBLE);
                taskschdetail_listview_middle.setVisibility(View.GONE);
                taskschdetail_listview_right.setVisibility(View.GONE);
//                showSearch();
                if (list_left == null || list_left.isEmpty()) {
                    refreshLeft();
                }
            }
            break;
            case R.id.taskschdetail_tab_middle_layout: {
                taskschdetail_tab_left_ico.setImageResource(R.mipmap.wangdian_dzx_2);
                taskschdetail_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                taskschdetail_tab_left_line.setVisibility(View.INVISIBLE);
                taskschdetail_tab_middle_ico.setImageResource(R.mipmap.wangdian_zxwc_1);
                taskschdetail_tab_middle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                taskschdetail_tab_middle_line.setVisibility(View.VISIBLE);
                taskschdetail_tab_right_ico.setImageResource(R.mipmap.wangdian_zlyhs_2);
                taskschdetail_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                taskschdetail_tab_right_line.setVisibility(View.INVISIBLE);
                taskschdetail_listview_left.setVisibility(View.GONE);
                taskschdetail_listview_middle.setVisibility(View.VISIBLE);
                taskschdetail_listview_right.setVisibility(View.GONE);
//                taskschdetail_title.hideSearchEditText();
                if (list_middle == null || list_middle.isEmpty()) {
                    refreshMiddle();
                }
            }
            break;
            case R.id.taskschdetail_tab_right_layout: {
                taskschdetail_tab_left_ico.setImageResource(R.mipmap.wangdian_dzx_2);
                taskschdetail_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                taskschdetail_tab_left_line.setVisibility(View.INVISIBLE);
                taskschdetail_tab_middle_ico.setImageResource(R.mipmap.wangdian_zxwc_2);
                taskschdetail_tab_middle.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                taskschdetail_tab_middle_line.setVisibility(View.INVISIBLE);
                taskschdetail_tab_right_ico.setImageResource(R.mipmap.wangdian_zlyhs_1);
                taskschdetail_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                taskschdetail_tab_right_line.setVisibility(View.VISIBLE);
                taskschdetail_listview_left.setVisibility(View.GONE);
                taskschdetail_listview_middle.setVisibility(View.GONE);
                taskschdetail_listview_right.setVisibility(View.VISIBLE);
//                taskschdetail_title.hideSearchEditText();
                if (list_right == null || list_right.isEmpty()) {
                    refreshRight();
                }
            }
            break;
        }
    }
}
