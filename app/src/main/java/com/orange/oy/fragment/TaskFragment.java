package com.orange.oy.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskscheduleDetailActivity;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.activity.bright.BrightDZXListActivity;
import com.orange.oy.adapter.TaskAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.OpenTimeDialog;
import com.orange.oy.info.TaskInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务页
 */
public class TaskFragment extends BaseFragment implements View.OnClickListener {
    private View mView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_task, container, false);
        return mView;
    }

    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceiver(getContext());
    }

    public interface OnCitysearchClickListener {
        void clickforTask();
    }

    public void setOnCitysearchClickListener(OnCitysearchClickListener listener) {
        onCitysearchClickListener = listener;
    }

    private BroadcastReceiver ChangeRedPointBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(AppInfo.LOCATIONINFO)) {
                settingDistric(intent.getStringExtra("city"), null);
                if (list == null || list.isEmpty()) {
                    refreshData();
                }
            }
        }
    };

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.LOCATIONINFO);
        context.registerReceiver(ChangeRedPointBroadcastReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(ChangeRedPointBroadcastReceiver);
    }

    private void initNetworkConnection() {
        getData = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page + "");
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("city", task_distric.getText().toString());
                String search = task_search.getText().toString().trim();
                if (!TextUtils.isEmpty(search))
                    params.put("projectname", search);
                return params;
            }
        };
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

    private PullToRefreshListView task_listview_left;
    private EditText task_search;
    private TextView task_distric;
    private ImageView task_right;
    private OnCitysearchClickListener onCitysearchClickListener;
    private TaskAdapter taskAdapter;
    private ArrayList<TaskInfo> list;
    private NetworkConnection getData, checktime;
    private int page;
    private String projectid, projectname, city, photo_compression, is_record, is_watermark, code, brand, is_takephoto;
    private String type;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initNetworkConnection();
        registerReceiver(getContext());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            RelativeLayout title_layout = (RelativeLayout) mView.findViewById(R.id.title_layout);
            int height = (int) getResources().getDimension(R.dimen.apptitle_height);
            if (title_layout.getHeight() != height) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) title_layout.getLayoutParams();
                lp.height = height;
                title_layout.setLayoutParams(lp);
                title_layout.setPadding(0, 0, 0, 0);
            }
        }
        task_distric = (TextView) mView.findViewById(R.id.task_distric);
        task_listview_left = (PullToRefreshListView) mView.findViewById(R.id.task_listview_left);
        task_search = (EditText) mView.findViewById(R.id.task_search);
        task_right = (ImageView) mView.findViewById(R.id.task_right);
        task_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        task_listview_left.setPullLabel(getResources().getString(R.string.listview_down));
        task_listview_left.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        task_listview_left.setReleaseLabel(getResources().getString(R.string.listview_down2));
        city = task_distric.getText().toString();
        task_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        task_listview_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (taskAdapter != null) {
                    if (taskAdapter.isSelectItem1()) {
                        TaskInfo taskInfo = list.get(position - 1);
                        Intent intent = new Intent(getContext(), TaskillustratesActivity.class);
                        intent.putExtra("projectid", taskInfo.getId());
                        intent.putExtra("projectname", taskInfo.getName());
                        startActivity(intent);
                    } else if (taskAdapter.isSelectItem2()) {
                        String city = task_distric.getText().toString();
                        if (!TextUtils.isEmpty(city)) {
                            TaskInfo taskInfo = list.get(position - 1);
                            projectid = taskInfo.getId();
                            projectname = taskInfo.getName();
                            photo_compression = taskInfo.getPhoto_compression();
                            is_record = taskInfo.getIs_record();
                            is_watermark = taskInfo.getIs_watermark();
                            code = taskInfo.getCode();
                            brand = taskInfo.getBrand();
                            is_takephoto = taskInfo.getIs_takephoto();
                            type = taskInfo.getType();
                            checkTime();//判断是否有可执行的网点
                        } else {
                            Tools.showToast(getContext(), "请重新定位");
                        }
                    } else {
//                        String city = task_distric.getText().toString();
//                        if (!TextUtils.isEmpty(city)) {
//                            TaskInfo taskInfo = list.get(position - 1);
//                            Intent intent = new Intent(getContext(), TaskListDetailActivity.class);
//                            intent.putExtra("projectname", taskInfo.getName());
//                            intent.putExtra("project_id", taskInfo.getId());
//                            intent.putExtra("city", city);
//                            intent.putExtra("is_record", taskInfo.getIs_record());
//                            /***添加图片大小限制 2016/5/26*****/
//                            intent.putExtra("photo_compression", taskInfo.getPhoto_compression());
//                            /*** end *****/
//                            startActivity(intent);
//                        } else {
//                            Tools.showToast(getContext(), "请重新定位");
//                        }
                    }
                    taskAdapter.clearSelect();
                }
            }
        });
        task_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    refreshData();
                    return true;
                }
                return false;
            }
        });
        Map<String, String> map = ((MainActivity) getActivity()).getLocalMap();
        if (map != null) {
            settingDistric(map.get("name"), map.get("id"));
        }
        mView.findViewById(R.id.task_citysearch).setOnClickListener(this);
        task_right.setOnClickListener(this);
        list = new ArrayList<TaskInfo>();
        taskAdapter = new TaskAdapter(getContext(), list);
        task_listview_left.setAdapter(taskAdapter);
        task_search.setText("");
        refreshData();
    }

    public void checkTime() {
        checktime.sendPostRequest(Urls.CheckTime, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        String msg = jsonObject.getString("msg");
                        if ("0".equals(msg)) {
                            if ("2".equals(type)) {//神秘客户任务
                                Intent intent = new Intent(getContext(), BlackDZXListActivity.class);
                                intent.putExtra("project_id", projectid);
                                intent.putExtra("projectname", projectname);
                                intent.putExtra("city", city);
                                intent.putExtra("photo_compression", photo_compression);
                                intent.putExtra("is_record", is_record);
                                intent.putExtra("is_watermark", Tools.StringToInt(is_watermark));
                                intent.putExtra("code", code);
                                intent.putExtra("brand", brand);
                                intent.putExtra("is_takephoto", is_takephoto);
                                startActivity(intent);
                            } else if ("1".equals(type)) {//正常任务
                                Intent intent = new Intent(getContext(), TaskscheduleDetailActivity.class);
                                intent.putExtra("project_id", projectid);
                                intent.putExtra("projectname", projectname);
                                intent.putExtra("city", city);
                                intent.putExtra("photo_compression", photo_compression);
                                intent.putExtra("is_record", is_record);
                                intent.putExtra("is_watermark", Tools.StringToInt(is_watermark));
                                intent.putExtra("code", code);
                                intent.putExtra("brand", brand);
                                intent.putExtra("is_takephoto", is_takephoto);
                                startActivity(intent);
                            } else if ("3".equals(type)) {//明访任务
                                Intent intent = new Intent(getContext(), BrightDZXListActivity.class);
                                intent.putExtra("project_id", projectid);
                                intent.putExtra("projectname", projectname);
                                intent.putExtra("city", city);
                                intent.putExtra("photo_compression", photo_compression);
                                intent.putExtra("is_record", is_record);
                                intent.putExtra("is_watermark", Tools.StringToInt(is_watermark));
                                intent.putExtra("code", code);
                                intent.putExtra("brand", brand);
                                intent.putExtra("is_takephoto", is_takephoto);
                                intent.putExtra("type", type);
                                startActivity(intent);
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
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        task_search.setText("");
    }

    private void getData() {
        if (TextUtils.isEmpty(task_distric.getText()) || task_distric.getText().toString().equals(getResources()
                .getString(R.string.find_city))) {
            Tools.showToast(getContext(), "未选择城市");
            task_listview_left.onRefreshComplete();
            return;
        }
        getData.sendPostRequest(Urls.Projectlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (list == null) {
                            list = new ArrayList<TaskInfo>();
                            if (taskAdapter != null) {
                                taskAdapter.resetList(list);
                            } else {
                                taskAdapter = new TaskAdapter(getContext(), list);
                                task_listview_left.setAdapter(taskAdapter);
                            }
                        } else {
                            if (page == 1)
                                list.clear();
                        }
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            TaskInfo taskInfo = new TaskInfo();
                            taskInfo.setName(jsonObject.getString("project_name"));
                            taskInfo.setId(jsonObject.getString("id"));
                            taskInfo.setCode(jsonObject.getString("project_code"));
                            taskInfo.setIs_record(jsonObject.getString("is_record"));
                            taskInfo.setPhoto_compression(jsonObject.getString("photo_compression"));
                            taskInfo.setIs_watermark(jsonObject.getString("is_watermark"));
                            taskInfo.setCodeStr(jsonObject.getString("code"));
                            taskInfo.setBrand(jsonObject.getString("brand"));
                            taskInfo.setIs_takephoto(jsonObject.getString("is_takephoto"));
                            taskInfo.setType(jsonObject.optString("type"));
                            list.add(taskInfo);
                        }
                        task_listview_left.onRefreshComplete();
                        if (length < 15) {
                            task_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            task_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        if (taskAdapter == null) {
                            taskAdapter = new TaskAdapter(getContext(), list);
                            task_listview_left.setAdapter(taskAdapter);
                        } else {
                            taskAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                task_listview_left.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                task_listview_left.onRefreshComplete();
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    public void settingDistric(String name, String id) {
        task_distric.setText(name);
        task_distric.setTag(id);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_citysearch: {
                if (onCitysearchClickListener != null) {
                    onCitysearchClickListener.clickforTask();
                }
            }
            break;
            case R.id.task_right: {
                if (onShowCalendarListener != null) {
                    onShowCalendarListener.showCalendar();
                }
            }
            break;
        }
    }

    private OnShowCalendarListener onShowCalendarListener;

    public interface OnShowCalendarListener {
        void showCalendar();
    }

    public void setOnShowCalendarListener(OnShowCalendarListener onShowCalendarListener) {
        this.onShowCalendarListener = onShowCalendarListener;
    }
}
