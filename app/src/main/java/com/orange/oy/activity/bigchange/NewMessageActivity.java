package com.orange.oy.activity.bigchange;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.activity.experience.ExperienceLocationActivity;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.newtask.NoOutletsActivity;
import com.orange.oy.activity.newtask.ProjectRecruitmentActivity;
import com.orange.oy.activity.newtask.TaskDistActivity;
import com.orange.oy.activity.newtask.TaskGrabActivity;
import com.orange.oy.adapter.NewMessageAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MessageLeftInfo;
import com.orange.oy.info.NewmessageInfo;
import com.orange.oy.info.TaskNewInfo;
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
 * V3.1.2新的消息列表
 */
public class NewMessageActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, NewMessageAdapter.OrlikeClickListener, AdapterView.OnItemClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.message_title);
        appTitle.settingName("消息");
        appTitle.showBack(this);

    }

    private void initNetworkConnection() {
        pushmessage = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", username); //用户账号
                params.put("page", page + "");
                params.put("pagesize", "3");  //pagesize 每页显示的条数
                return params;
            }
        };

        getProjectInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", username); //用户账号
                params.put("projectid", projectid);
                params.put("record", "0");

                return params;
            }
        };
        getProjectInfo.setIsShowDialog(true);

        likemessage = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", username); //用户账号
                params.put("message_id", messageId);  // 消息信息id
                params.put("state", state);  // 评价状态，1为喜欢，2为不喜欢
                return params;
            }
        };
        likemessage.setIsShowDialog(true);

        checkapply = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(Key)) {
                    params.put("usermobile", username);
                    params.put("token", Tools.getToken());
                }
                params.put("projectid", projectid);
                return params;
            }
        };
    }

    protected void onStop() {
        super.onStop();
        if (pushmessage != null) {
            pushmessage.stop(Urls.Pushmessage);
        }
        if (getProjectInfo != null) {
            getProjectInfo.stop(Urls.GetProjectInfo);
        }
        if (likemessage != null) {
            likemessage.stop(Urls.Likemessage);
        }
        if (checkapply != null) {
            checkapply.stop(Urls.CheckApply);
        }
    }

    private PullToRefreshListView message_listview;
    private NewMessageAdapter adapter;
    private SystemDBHelper systemDBHelper;
    private ArrayList<MessageLeftInfo> list_left;//总数据
    private LinearLayout message_delete_layout;
    private static boolean isShow; // 是否显示CheckBox标识
    private NetworkConnection pushmessage, likemessage, getProjectInfo, checkapply;
    private String username;
    private int page = 1;
    private int code;
    private String messageId, projectid, Key;
    private String state;
    private ArrayList<NewmessageInfo> newmessageInfosList;
    private String outletId, latitude = "", longitude = "", address = "", city = "";

    private AppDBHelper appDBHelper;
    private String projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        systemDBHelper = new SystemDBHelper(this);
        username = AppInfo.getName(this);
        projectId = getIntent().getStringExtra("projectId");
        address = getIntent().getStringExtra("address");
        longitude = getIntent().getStringExtra("longitude");
        latitude = getIntent().getStringExtra("latitude");
        city = getIntent().getStringExtra("city");
        list_left = new ArrayList<>();
        initTitle();
        Key = AppInfo.getKey(this);
        appDBHelper = new AppDBHelper(this);
        newmessageInfosList = new ArrayList<>();
        message_listview = (PullToRefreshListView) findViewById(R.id.message_listview);
        message_delete_layout = (LinearLayout) findViewById(R.id.message_delete_layout);
        initListview(message_listview);
        initNetworkConnection();
        if (!TextUtils.isEmpty(projectId)) {
            projectid = projectId;
            getItemInfo();
        } else {
            getData();
        }
        message_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        adapter = new NewMessageAdapter(this, newmessageInfosList);
        message_listview.setAdapter(adapter);
        adapter.setOnOrlikeClickListener(this);
        message_listview.setOnItemClickListener(this);

    }

    public void getData() {
        if (TextUtils.isEmpty(AppInfo.getKey(this))) {
            ConfirmDialog.showDialog(this, null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            Intent intent = new Intent(NewMessageActivity.this, IdentifycodeLoginActivity.class);
                            startActivity(intent);
                        }
                    });
            return;
        }
        pushmessage.sendPostRequest(Urls.Pushmessage, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (page == 1) {
                            if (newmessageInfosList != null) {
                                newmessageInfosList.clear();
                            } else {
                                newmessageInfosList = new ArrayList<NewmessageInfo>();
                            }
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            // lin_Nodata.setVisibility(View.GONE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                NewmessageInfo newmessageInfo = new NewmessageInfo();
                                newmessageInfo.setProjectid(object.getInt("projectid"));
                                newmessageInfo.setMessage_id(object.getInt("message_id"));
                                newmessageInfo.setPhoto_url(object.getString("photo_url"));
                                newmessageInfo.setBegin_date(object.getString("begin_date"));
                                newmessageInfo.setEnd_data(object.getString("end_data"));
                                newmessageInfo.setState(object.getString("state"));
                                newmessageInfosList.add(newmessageInfo);
                            }
                            message_listview.onRefreshComplete();
                            if (length < 3) {
                                message_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                message_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                    } else {
                        Tools.showToast(NewMessageActivity.this, jsonObject.getString("msg"));
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.showToast(NewMessageActivity.this, getResources().getString(R.string.network_error));
                }
                message_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NewMessageActivity.this, getResources().getString(R.string.network_volleyerror));
                message_listview.onRefreshComplete();
            }
        }, null);
    }


    private void Orlike() {
        //  Tools.d("zpf", "state=======>>>" + state);
        likemessage.sendPostRequest(Urls.Likemessage, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        adapter.notifyDataSetChanged();
                        Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(NewMessageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NewMessageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NewMessageActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    public void getItemInfo() {
        getProjectInfo.sendPostRequest(Urls.ProjectInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONObject object = jsonObject.optJSONObject("datas");
                        if (object != null) {
                            TaskNewInfo taskNewInfo = new TaskNewInfo();
                            taskNewInfo.setId(object.getString("id"));
                            taskNewInfo.setProject_name(object.getString("project_name"));
                            taskNewInfo.setProject_code(object.getString("project_code"));
                            taskNewInfo.setProject_type(object.getString("project_type"));
                            taskNewInfo.setIs_record(Tools.StringToInt(object.getString("is_record")));
                            taskNewInfo.setPhoto_compression(object.getString("photo_compression"));
                            taskNewInfo.setBegin_date(object.getString("begin_date"));
                            taskNewInfo.setEnd_date(object.getString("end_date"));
                            taskNewInfo.setIs_download(Tools.StringToInt(object.getString("is_download")));
                            taskNewInfo.setIs_watermark(Tools.StringToInt(object.getString("is_watermark")));
                            taskNewInfo.setCode(object.getString("code"));
                            taskNewInfo.setBrand(object.getString("brand"));
                            taskNewInfo.setIs_takephoto(Tools.StringToInt(object.getString("is_takephoto")));
                            taskNewInfo.setType(object.getString("type"));
                            taskNewInfo.setShow_type(object.getString("show_type"));
                            taskNewInfo.setCheck_time(object.getString("check_time"));
                            taskNewInfo.setMin_reward(object.getString("min_reward"));
                            taskNewInfo.setMax_reward(object.getString("max_reward"));
                            taskNewInfo.setProject_property(object.getString("project_property"));
                            taskNewInfo.setPublish_time(object.optString("publish_time"));
                            taskNewInfo.setProject_person(object.optString("project_person"));
                            taskNewInfo.setMoney_unit(object.getString("money_unit"));
                            taskNewInfo.setCertification(object.getString("certification"));
                            OnitemNewsclick(taskNewInfo);
                        }
                    } else {
                        Tools.showToast(NewMessageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NewMessageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NewMessageActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setPullLabel(getResources().getString(R.string.listview_down));// 刚下拉时，显示的提示
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));// 刷新时
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));// 下来达到一定距离时，显示的提示
    }

    @Override
    public void onBack() {
        baseFinish();
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onlike(int pos) {
        NewmessageInfo newmessageInfo = newmessageInfosList.get(pos);
        messageId = newmessageInfo.getMessage_id() + "";
        state = newmessageInfo.getState();  //state	评价状态，0为为评价,1为喜欢，2为不喜欢

        if (state.equals("0")) {
            state = "1";
            Orlike();
        } else if (state.equals("1")) {
            state = "0";
            Orlike();
        } else if (state.equals("2")) {
            state = "1";
            Orlike();
        }
    }

    @Override
    public void ondislike(int pos) {
        NewmessageInfo newmessageInfo = newmessageInfosList.get(pos);
        messageId = newmessageInfo.getMessage_id() + "";
        state = newmessageInfo.getState();  //state	评价状态，0为为评价,1为喜欢，2为不喜欢

        if (state.equals("0")) {
            state = "2";
            Orlike();
        } else if (state.equals("1")) {
            state = "2";
            Orlike();
        } else if (state.equals("2")) {
            state = "0";
            Orlike();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        projectid = newmessageInfosList.get(position - 1).projectid + "";
        getItemInfo();
    }


    private void OnitemNewsclick(final TaskNewInfo taskNewInfo) {

        if (taskNewInfo.getProject_property().equals("2")) {//众包
            final String type = taskNewInfo.getType();
            if ("5".equals(type)) {//无店单的项目

                Intent intent = new Intent(this, NoOutletsActivity.class);
                intent.putExtra("preview", "2");
                intent.putExtra("project_person", taskNewInfo.getProject_person());
                intent.putExtra("projectname", taskNewInfo.getProject_name());
                intent.putExtra("projectid", taskNewInfo.getId());
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", city);
                intent.putExtra("type", taskNewInfo.getType());
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("address", address);
                startActivity(intent);
                if (!TextUtils.isEmpty(projectId)) {
                    finish();
                }
            } else {
                if (appDBHelper.getIsShow(taskNewInfo.getId()) && "1".equals(taskNewInfo.getStandard_state())) {

                    Intent intent = new Intent(this, TaskillustratesActivity.class);
                    intent.putExtra("project_person", taskNewInfo.getProject_person());
                    intent.putExtra("projectname", taskNewInfo.getProject_name());
                    intent.putExtra("projectid", taskNewInfo.getId());
                    intent.putExtra("id", taskNewInfo.getId());
                    intent.putExtra("project_name", taskNewInfo.getProject_name());
                    intent.putExtra("project_code", taskNewInfo.getProject_code());
                    intent.putExtra("project_type", taskNewInfo.getProject_type());
                    intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                    intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                    intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                    intent.putExtra("end_date", taskNewInfo.getEnd_date());
                    intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                    intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                    intent.putExtra("code", taskNewInfo.getCode());
                    intent.putExtra("brand", taskNewInfo.getBrand());
                    intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                    intent.putExtra("show_type", taskNewInfo.getShow_type());
                    intent.putExtra("check_time", taskNewInfo.getCheck_time());
                    intent.putExtra("project_property", taskNewInfo.getProject_property());
                    intent.putExtra("city", city);
                    intent.putExtra("type", taskNewInfo.getType());
                    intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                    intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("address", address);
                    intent.putExtra("isHomePage", "1");//是否是首页传过来的 1首页 0我的任务列表 2地图
                    startActivity(intent);
                    if (!TextUtils.isEmpty(projectId)) {
                        finish();
                    }
                } else {
                    checkapply.sendPostRequest(Urls.CheckApply, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (jsonObject.getInt("code") == 200) {
                                    if ("1".equals(type)) {
                                        Intent intent = new Intent(NewMessageActivity.this, TaskGrabActivity.class);
                                        intent.putExtra("id", taskNewInfo.getId());
                                        intent.putExtra("project_person", taskNewInfo.getProject_person());
                                        intent.putExtra("project_name", taskNewInfo.getProject_name());
                                        intent.putExtra("project_code", taskNewInfo.getProject_code());
                                        intent.putExtra("project_type", taskNewInfo.getProject_type());
                                        intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                        intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                        intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                        intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                        intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                        intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                        intent.putExtra("code", taskNewInfo.getCode());
                                        intent.putExtra("brand", taskNewInfo.getBrand());
                                        intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                        intent.putExtra("type", taskNewInfo.getType());
                                        intent.putExtra("show_type", taskNewInfo.getShow_type());
                                        intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                        intent.putExtra("project_property", taskNewInfo.getProject_property());
                                        intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                        intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                        intent.putExtra("city", city);
                                        intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                        intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                        intent.putExtra("province", getIntent().getStringExtra("province"));
                                        intent.putExtra("type1", "0");//首页跳转
                                        startActivity(intent);
                                        if (!TextUtils.isEmpty(projectId)) {
                                            finish();
                                        }
                                    } else if ("4".equals(type)) {
                                        Intent intent = new Intent(NewMessageActivity.this, ExperienceLocationActivity.class);
                                        intent.putExtra("id", taskNewInfo.getId());
                                        intent.putExtra("project_person", taskNewInfo.getProject_person());
                                        intent.putExtra("project_name", taskNewInfo.getProject_name());
                                        intent.putExtra("project_code", taskNewInfo.getProject_code());
                                        intent.putExtra("project_type", taskNewInfo.getProject_type());
                                        intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                        intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                        intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                        intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                        intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                        intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                        intent.putExtra("code", taskNewInfo.getCode());
                                        intent.putExtra("brand", taskNewInfo.getBrand());
                                        intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                        intent.putExtra("type", taskNewInfo.getType());
                                        intent.putExtra("show_type", taskNewInfo.getShow_type());
                                        intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                        intent.putExtra("project_property", taskNewInfo.getProject_property());
                                        intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                        intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                        intent.putExtra("city", city);
                                        intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                        intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                        startActivity(intent);
                                        if (!TextUtils.isEmpty(projectId)) {
                                            finish();
                                        }
                                    }
                                } else if (jsonObject.getInt("code") == 2) {//点击进入招募令
                                    Intent intent = new Intent(NewMessageActivity.this, ProjectRecruitmentActivity.class);
                                    intent.putExtra("projectid", projectid);
                                    startActivity(intent);
                                } else {
                                    Tools.showToast(NewMessageActivity.this, jsonObject.getString("msg"));
                                }
                            } catch (JSONException e) {
                                Tools.showToast(NewMessageActivity.this, getResources().getString(R.string.network_error));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Tools.showToast(NewMessageActivity.this, getResources().getString(R.string.network_error));
                        }
                    }, null);
                }
            }
        } else if (taskNewInfo.getProject_property().equals("3")) {//演练---暂时不用未做修改
            String type = taskNewInfo.getType();
            if (type.equals("1")) {//正常任务
                Intent intent = new Intent(this, TaskDistActivity.class);
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_person", taskNewInfo.getProject_person());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("type", taskNewInfo.getType() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", city);
                intent.putExtra("type", type);
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                startActivity(intent);
                if (!TextUtils.isEmpty(projectId)) {
                    finish();
                }
            } else if (type.equals("2")) {// 暗访任务
                Intent intent = new Intent(this, BlackDZXListActivity.class);
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_person", taskNewInfo.getProject_person());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("type", taskNewInfo.getType() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", city);
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("type", type);
                startActivity(intent);
                if (!TextUtils.isEmpty(projectId)) {
                    finish();
                }
            } else if (type.equals("3")) {//明访任务
                Intent intent = new Intent(this, TaskDistActivity.class);
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("type", taskNewInfo.getType() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", city);
                intent.putExtra("type", type);
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                startActivity(intent);
                if (!TextUtils.isEmpty(projectId)) {
                    finish();
                }
            }
        }
    }
}
