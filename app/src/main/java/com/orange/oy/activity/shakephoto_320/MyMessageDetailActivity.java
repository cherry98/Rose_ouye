package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.activity.experience.ExperienceLocationActivity;
import com.orange.oy.activity.newtask.NoOutletsActivity;
import com.orange.oy.activity.newtask.ProjectRecruitmentActivity;
import com.orange.oy.activity.newtask.TaskDistActivity;
import com.orange.oy.activity.newtask.TaskGrabActivity;
import com.orange.oy.adapter.NewMessageAdapter;
import com.orange.oy.adapter.mycorps_314.MyMessageDetailAdapter;
import com.orange.oy.adapter.mycorps_314.MyMessageDetailAdapter2;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MenuItem;
import com.orange.oy.info.NewmessageInfo;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.info.shakephoto.MyMessageDetailInfo;
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
 * 我的消息详情 V3.20
 */
public class MyMessageDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, NewMessageAdapter.OrlikeClickListener, AdapterView.OnItemClickListener {

    private void initTitle(String name) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.messagedetail_title);
        appTitle.settingName(name);
        appTitle.showBack(this);
    }

    private void initNetwork() {
        messageDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MyMessageDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("user_id", user_id);
                params.put("is_ouye", is_ouye);
                return params;
            }
        };
        likemessage = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyMessageDetailActivity.this)); //用户账号
                params.put("message_id", message_id);  // 消息信息id
                params.put("state", state);  // 评价状态，1为喜欢，2为不喜欢
                return params;
            }
        };
        likemessage.setIsShowDialog(true);

        getProjectInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyMessageDetailActivity.this)); //用户账号
                params.put("projectid", projectid);
                params.put("record", "0");

                return params;
            }
        };
        getProjectInfo.setIsShowDialog(true);
        checkapply = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(MyMessageDetailActivity.this))) {
                    params.put("usermobile", AppInfo.getName(MyMessageDetailActivity.this));
                    params.put("token", Tools.getToken());
                }
                params.put("projectid", projectid);
                return params;
            }
        };
        checkapply.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkapply != null) {
            checkapply.stop(Urls.CheckApply);
        }
        if (getProjectInfo != null) {
            getProjectInfo.stop(Urls.GetProjectInfo);
        }
    }

    private NetworkConnection messageDetail, likemessage, getProjectInfo, checkapply;
    private PullToRefreshListView messagedetail_listview;
    private String is_ouye, user_id;
    private ArrayList<NewmessageInfo> list1;//偶业小秘
    private ArrayList<MyMessageDetailInfo> list2;//个人用户
    private MyMessageDetailAdapter myMessageDetailAdapter;
    private MyMessageDetailAdapter2 myMessageDetailAdapter2;
    private String message_id, state, projectid, city, longitude, address, province, latitude;
    private AppDBHelper appDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message_detail);
        appDBHelper = new AppDBHelper(this);
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        Intent data = getIntent();
        user_id = data.getStringExtra("user_id");
        is_ouye = data.getStringExtra("is_ouye");
        latitude = data.getStringExtra("latitude");
        longitude = data.getStringExtra("longitude");
        address = data.getStringExtra("address");
        province = data.getStringExtra("province");
        city = data.getStringExtra("city");
        initTitle(data.getStringExtra("user_name"));
        initNetwork();
        messagedetail_listview = (PullToRefreshListView) findViewById(R.id.messagedetail_listview);
        if ("1".equals(is_ouye)) {
            myMessageDetailAdapter = new MyMessageDetailAdapter(this, list1);
            messagedetail_listview.setAdapter(myMessageDetailAdapter);
            myMessageDetailAdapter.setOnOrlikeClickListener(this);
        } else {
            myMessageDetailAdapter2 = new MyMessageDetailAdapter2(this, list2);
            messagedetail_listview.setAdapter(myMessageDetailAdapter2);
        }
        getData();
        messagedetail_listview.setOnItemClickListener(this);
    }

    private void getData() {
        messageDetail.sendPostRequest(Urls.MessageDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if ("1".equals(is_ouye)) {//偶业小秘
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    NewmessageInfo newmessageInfo = new NewmessageInfo();
                                    newmessageInfo.setProject_id(object.getString("projectid"));
                                    newmessageInfo.setMessageid(object.getString("message_id"));
                                    newmessageInfo.setPhoto_url(object.getString("photo_url"));
                                    newmessageInfo.setBegin_date(object.getString("begin_date"));
                                    newmessageInfo.setEnd_data(object.getString("end_date"));
                                    newmessageInfo.setState(object.getString("state"));
                                    newmessageInfo.setCreate_time(object.getString("create_time"));
                                    newmessageInfo.setType(object.getString("type"));
                                    newmessageInfo.setTitle(object.getString("title"));
                                    newmessageInfo.setContent(object.getString("content"));
                                    list1.add(newmessageInfo);
                                }
                                if (myMessageDetailAdapter != null) {
                                    myMessageDetailAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {//普通用户
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    MyMessageDetailInfo detailInfo = new MyMessageDetailInfo();
                                    detailInfo.setProject_name(object.getString("project_name"));
                                    detailInfo.setBegin_date(object.getString("begin_date"));
                                    detailInfo.setEnd_date(object.getString("end_date"));
                                    detailInfo.setShare_username(object.getString("share_username"));
                                    detailInfo.setOutlet_id(object.getString("outlet_id"));
                                    detailInfo.setOutlet_name(object.getString("outlet_name"));
                                    detailInfo.setUser_name(object.getString("user_name"));
                                    detailInfo.setUser_mobile(object.getString("user_mobile"));
                                    detailInfo.setComplete_time(object.getString("complete_time"));
                                    detailInfo.setCreate_time(object.getString("create_time"));
                                    list2.add(detailInfo);
                                }
                                if (myMessageDetailAdapter2 != null) {
                                    myMessageDetailAdapter2.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        Tools.showToast(MyMessageDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyMessageDetailActivity.this, getResources().getString(R.string.network_error));
                }
                messagedetail_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyMessageDetailActivity.this, getResources().getString(R.string.network_volleyerror));
                messagedetail_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onlike(int pos) {//喜欢操作
        NewmessageInfo newmessageInfo = list1.get(pos);
        message_id = newmessageInfo.getMessageid();
        state = newmessageInfo.getState();  //state	评价状态，0为为评价,1为喜欢，2为不喜欢

        switch (state) {
            case "0":
                state = "1";
                Orlike();
                break;
            case "1":
                state = "0";
                Orlike();
                break;
            case "2":
                state = "1";
                Orlike();
                break;
        }
    }

    @Override
    public void ondislike(int pos) {//不喜欢操作
        NewmessageInfo newmessageInfo = list1.get(pos);
        message_id = newmessageInfo.getMessage_id() + "";
        state = newmessageInfo.getState();  //state	评价状态，0为为评价,1为喜欢，2为不喜欢

        switch (state) {
            case "0":
                state = "2";
                Orlike();
                break;
            case "1":
                state = "2";
                Orlike();
                break;
            case "2":
                state = "0";
                Orlike();
                break;
        }
    }

    private void Orlike() {
        likemessage.sendPostRequest(Urls.Likemessage, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        myMessageDetailAdapter.notifyDataSetChanged();
                        Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(MyMessageDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyMessageDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyMessageDetailActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ("1".equals(is_ouye)) {
            NewmessageInfo newmessageInfo = list1.get(position - 1);
            projectid = newmessageInfo.getProject_id();
            if ("1".equals(newmessageInfo.getType())) {
                getItemInfo();
            }
        } else {
            if (myMessageDetailAdapter2 != null) {
                MyMessageDetailInfo detailInfo = list2.get(position - 1);
                Intent intent = new Intent(this, TaskFinishActivity.class);
                intent.putExtra("projectname", detailInfo.getProject_name());
                intent.putExtra("store_name", detailInfo.getOutlet_name());
//                intent.putExtra("store_num", detailInfo.getCode());
//                intent.putExtra("project_id", detailInfo.getProject_id());
//                intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                intent.putExtra("store_id", detailInfo.getOutlet_id());
                intent.putExtra("state", "1");
//                intent.putExtra("is_watermark", myRewardInfo.getIs_watermark());
//                intent.putExtra("code", myRewardInfo.getCode());
//                intent.putExtra("brand", myRewardInfo.getBrand());
                intent.putExtra("isAgain", false);
                startActivity(intent);
            }
        }
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
                        Tools.showToast(MyMessageDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyMessageDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyMessageDetailActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
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
                } else {
                    checkapply.sendPostRequest(Urls.CheckApply, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (jsonObject.getInt("code") == 200) {
                                    if ("1".equals(type)) {
                                        Intent intent = new Intent(MyMessageDetailActivity.this, TaskGrabActivity.class);
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
                                        intent.putExtra("province", province);
                                        intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                        intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                        intent.putExtra("province", getIntent().getStringExtra("province"));
                                        intent.putExtra("type1", "0");//首页跳转
                                        startActivity(intent);
                                    } else if ("4".equals(type)) {
                                        Intent intent = new Intent(MyMessageDetailActivity.this, ExperienceLocationActivity.class);
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
                                    }
                                } else if (jsonObject.getInt("code") == 2) {//点击进入招募令
                                    Intent intent = new Intent(MyMessageDetailActivity.this, ProjectRecruitmentActivity.class);
                                    intent.putExtra("projectid", projectid);
                                    startActivity(intent);
                                } else {
                                    Tools.showToast(MyMessageDetailActivity.this, jsonObject.getString("msg"));
                                }
                            } catch (JSONException e) {
                                Tools.showToast(MyMessageDetailActivity.this, getResources().getString(R.string.network_error));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Tools.showToast(MyMessageDetailActivity.this, getResources().getString(R.string.network_error));
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
            }
        }
    }
}
