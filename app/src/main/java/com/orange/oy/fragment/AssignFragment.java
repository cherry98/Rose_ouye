package com.orange.oy.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.activity.newtask.TaskDistActivity;
import com.orange.oy.adapter.TaskNewAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.OpenTimeDialog;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * 指派任务页面
 */
public class AssignFragment extends BaseFragment {


    public AssignFragment() {
        // Required empty public constructor
    }

    private void iniNetworkConnection() {
        assignedProjectList = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
//                params.put("projectname", );
                params.put("city", city);
                params.put("page", page + "");
                return params;
            }
        };
        assignedProjectList.setIsShowDialog(true);
        checktime = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                params.put("usermobile", AppInfo.getName(getContext()));
                return params;
            }
        };
    }

    private boolean isRefresh = false;

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            getData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isRefresh = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isRefresh = false;
    }

    public static final String TAG = AssignFragment.class.getName();
    private NetworkConnection assignedProjectList, checktime;
    private String city, projectid;
    private int page = 1;
    private ArrayList<TaskNewInfo> list;
    private PullToRefreshListView assign_listview;
    private TaskNewAdapter taskNewAdapter;
    private AppDBHelper appDBHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assign_old, container, false);
        city = AppInfo.getCityName(getContext());
        appDBHelper = new AppDBHelper(getContext());
        iniNetworkConnection();
        list = new ArrayList<>();
        assign_listview = (PullToRefreshListView) view.findViewById(R.id.assign_listview);
        taskNewAdapter = new TaskNewAdapter(getContext(), list, true);
        assign_listview.setAdapter(taskNewAdapter);
        getData();
        assign_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
        onItemClick();
        return view;
    }

    private void onItemClick() {
        assign_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaskNewInfo taskNewInfo = list.get(position - 1);
                projectid = taskNewInfo.getId();
                if ("城市".equals(city) || TextUtils.isEmpty(city)) {
                    Tools.showToast(getContext(), "请重新定位~");
                    return;
                }
                checktime.sendPostRequest(Urls.CheckTime, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Tools.d(s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getInt("code") == 200) {
                                String msg = jsonObject.getString("msg");
                                if ("0".equals(msg)) {
                                    String type = taskNewInfo.getType();
                                    if (type.equals("1")) {//正常任务
                                        if (appDBHelper.getIsShow(taskNewInfo.getId()) && "1".equals(taskNewInfo.getStandard_state())) {//判断是否显示任务介绍页面
                                            Intent intent = new Intent(getContext(), TaskillustratesActivity.class);
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
                                            intent.putExtra("type", taskNewInfo.getType() + "");
                                            intent.putExtra("show_type", taskNewInfo.getShow_type());
                                            intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                            intent.putExtra("project_property", taskNewInfo.getProject_property());
                                            intent.putExtra("city", city);
                                            intent.putExtra("type", type);
                                            intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                            intent.putExtra("isHomePage", "1");//哪个页面传过来的 1首页 0我的任务列表 2地图
                                            startActivity(intent);
                                        } else {//不显示任务介绍页面
                                            Intent intent = new Intent(getContext(), TaskDistActivity.class);
                                            intent.putExtra("project_person", taskNewInfo.getProject_person());
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
                                            intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                            startActivity(intent);
                                        }
                                    } else if (type.equals("2")) {// 暗访任务
                                        if (appDBHelper.getIsShow(taskNewInfo.getProjectid()) && "1".equals(taskNewInfo.getStandard_state())) {
                                            Intent intent = new Intent(getContext(), TaskillustratesActivity.class);
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
                                            intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                            intent.putExtra("isHomePage", "1");//是否是首页传过来的 1首页 0我的任务列表 2地图
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getContext(), BlackDZXListActivity.class);
                                            intent.putExtra("project_person", taskNewInfo.getProject_person());
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
                                            intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                            startActivity(intent);
                                        }
                                    }
                                } else if (Tools.StringToInt(msg) > 0) {//不到网点时间提醒
                                    OpenTimeDialog.createFloatView(getContext(), Tools.StringToInt(msg));
                                }
                            } else {
                                Tools.showToast(getContext(), jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                    }
                }, null);
            }
        });
    }

    public void getData() {
        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            ConfirmDialog.showDialog(getContext(), null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    });
            return;
        }
        assignedProjectList.sendPostRequest(Urls.AssignedProjectList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<TaskNewInfo>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
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
                                taskNewInfo.setCheck_time(object.getString("check_time") + "");
                                taskNewInfo.setMin_reward(object.getString("min_reward"));
                                taskNewInfo.setMax_reward(object.getString("max_reward"));
                                taskNewInfo.setProject_property(object.getString("project_property"));
                                taskNewInfo.setPublish_time(object.optString("publish_time"));
                                taskNewInfo.setProject_person(object.optString("project_person"));
                                taskNewInfo.setMoney_unit(object.getString("money_unit"));
                                taskNewInfo.setCertification(object.getString("certification"));
                                taskNewInfo.setStandard_state(object.getString("standard_state"));
                                list.add(taskNewInfo);
                            }
                        }
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                    if (taskNewAdapter != null) {
                        taskNewAdapter.notifyDataSetChanged();
                    }
                    assign_listview.onRefreshComplete();
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }
}
