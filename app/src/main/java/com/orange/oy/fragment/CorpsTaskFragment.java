package com.orange.oy.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.CreateCorpActivity;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.mycorps_314.JoinCorpActivity;
import com.orange.oy.activity.mycorps_315.CorpsNopriceActivity;
import com.orange.oy.activity.mycorps_315.CorpsPriceActivity;
import com.orange.oy.activity.mycorps_315.TeamMemberTodoActivity;
import com.orange.oy.activity.mycorps_315.TeamtaskProjectStateActivity;
import com.orange.oy.activity.scan.IdentityVerActivity;
import com.orange.oy.adapter.CorpsTaskAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.info.mycorps.CorpsTaskInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
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
 * v3.15 zhangpengfei
 * 战队任务
 */
public class CorpsTaskFragment extends Fragment implements View.OnClickListener {


    public CorpsTaskFragment() {
    }

    private void iniNetworkConnection() {
        WaitExecutePackage = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        TeamProjectState = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    public void onResume() {
        super.onResume();
        if (assign_oneimg != null && isJoind) {
            if (assign_oneimg.getVisibility() == View.VISIBLE) {
                getDataLeft();
            } else {
                getDataRight();
            }
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        if (WaitExecutePackage != null) {
            WaitExecutePackage.stop(Urls.WaitExecutePackage);
        }
        if (TeamProjectState != null) {
            TeamProjectState.stop(Urls.TeamProjectState);
        }
    }

    private Rect outRect = new Rect();
    public static final String TAG = CorpsTaskFragment.class.getName();
    private NetworkConnection WaitExecutePackage, TeamProjectState;
    private ArrayList<CorpsTaskInfo> listLeft, listRight;
    private PullToRefreshListView assign_listview, assign_listview2;
    private NetworkView assign_networkview, assign_networkview2;
    private CorpsTaskAdapter corpsTaskAdapterLeft, corpsTaskAdapterRight;
    private TextView assign_one, assign_two;
    private View assign_oneimg, assign_twoimg, assign_layout1, assign_layout2;
    private boolean isJoind = false;//是否加入或创建过战队
    private boolean bindidcard = false;//是否进行过身份认证

    public void setJoind(boolean joind, boolean bindidcard) {
        isJoind = joind;
        this.bindidcard = bindidcard;
        if (assign_layout1 != null && assign_layout2 != null) {
            if (isJoind) {
                assign_layout1.setVisibility(View.VISIBLE);
                assign_layout2.setVisibility(View.GONE);
                if (assign_oneimg.getVisibility() == View.VISIBLE) {
                    getDataLeft();
                } else {
                    getDataRight();
                }
            } else {
                assign_layout1.setVisibility(View.GONE);
                assign_layout2.setVisibility(View.VISIBLE);
                assign_networkview2.getLin_Nodata_prompt().setGravity(Gravity.LEFT);
                assign_networkview2.NoSearch("由品牌商直接指派给战队的任务，加入的战队越多，得到的奖金越多。您还没有加入战队哦，快加入战队或创建自己的战队吧。");
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = Tools.loadLayout(getContext(), R.layout.fragment_assign);
        assign_layout1 = view.findViewById(R.id.assign_layout1);
        assign_layout2 = view.findViewById(R.id.assign_layout2);
        assign_listview = (PullToRefreshListView) view.findViewById(R.id.assign_listview);
        assign_listview2 = (PullToRefreshListView) view.findViewById(R.id.assign_listview2);
        assign_networkview = (NetworkView) view.findViewById(R.id.assign_networkview);
        assign_networkview2 = (NetworkView) view.findViewById(R.id.assign_networkview2);
        assign_one = (TextView) view.findViewById(R.id.assign_one);
        assign_two = (TextView) view.findViewById(R.id.assign_two);
        assign_oneimg = view.findViewById(R.id.assign_oneimg);
        assign_twoimg = view.findViewById(R.id.assign_twoimg);
        view.findViewById(R.id.assign_join).setOnClickListener(this);
        view.findViewById(R.id.assign_create).setOnClickListener(this);
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniNetworkConnection();
        try {
            initView();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void initView() throws NullPointerException {
        listLeft = new ArrayList<>();
        listRight = new ArrayList<>();
        assign_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        assign_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataLeft();
            }
        });
        assign_listview2.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        assign_listview2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataRight();
            }
        });
        corpsTaskAdapterLeft = new CorpsTaskAdapter(getContext(), listLeft, 0);
        corpsTaskAdapterRight = new CorpsTaskAdapter(getContext(), listRight, 1);
        assign_listview.setAdapter(corpsTaskAdapterLeft);
        assign_listview2.setAdapter(corpsTaskAdapterRight);
        assign_one.setOnClickListener(this);
        assign_two.setOnClickListener(this);
        assign_listview.setOnItemClickListener(onItemClickListenerLeft);
        assign_listview2.setOnItemClickListener(onItemClickListenerRight);
        if (isJoind) {
            assign_layout1.setVisibility(View.VISIBLE);
            assign_layout2.setVisibility(View.GONE);
            onClick(assign_one);
        } else {
            assign_layout1.setVisibility(View.GONE);
            assign_layout2.setVisibility(View.VISIBLE);
            assign_networkview2.getLin_Nodata_prompt().setGravity(Gravity.LEFT);
            assign_networkview2.NoSearch("由品牌商直接指派给战队的任务，加入的战队越多，得到的奖金越多。您还没有加入战队哦，快加入战队或创建自己的战队吧。");
        }
    }

    private AdapterView.OnItemClickListener onItemClickListenerRight = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            position--;
            CorpsTaskInfo corpsTaskInfo = listRight.get(position);
            Intent intent = new Intent(getContext(), TeamtaskProjectStateActivity.class);
            intent.putExtra("team_id", corpsTaskInfo.getTeam_id());
            intent.putExtra("package_id", corpsTaskInfo.getPackage_id());
            intent.putExtra("package_team_id", corpsTaskInfo.getPackage_team_id());
            //intent.putExtra("id","id"); //网点id
            intent.putExtra("identity", corpsTaskInfo.getIdentity());
            intent.putExtra("package_team_id", corpsTaskInfo.getPackage_team_id()); //网点id
            intent.putExtra("type", corpsTaskInfo.getType());
            startActivity(intent);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListenerLeft = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            position--;
            CorpsTaskInfo corpsTaskInfo = listLeft.get(position);
            if ("1".equals(corpsTaskInfo.getIdentity())) {//队长点分配
                if ("2".equals(corpsTaskInfo.getType())) {//不显示金额
                    Intent intent = new Intent(getContext(), CorpsNopriceActivity.class);
                    intent.putExtra("projectname", corpsTaskInfo.getProject_name());
                    intent.putExtra("team_id", corpsTaskInfo.getTeam_id());
                    intent.putExtra("projectid", corpsTaskInfo.getProject_id());
                    intent.putExtra("package_team_id", corpsTaskInfo.getPackage_team_id());
                    getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), CorpsPriceActivity.class);
                    intent.putExtra("projectname", corpsTaskInfo.getProject_name());
                    intent.putExtra("team_id", corpsTaskInfo.getTeam_id());
                    intent.putExtra("projectid", corpsTaskInfo.getProject_id());
                    intent.putExtra("package_team_id", corpsTaskInfo.getPackage_team_id());
                    intent.putExtra("package_id", corpsTaskInfo.getPackage_id());
                    getContext().startActivity(intent);
                }
            } else {//立即执行
                Intent intent = new Intent(getContext(), TeamMemberTodoActivity.class);
                intent.putExtra("projectname", corpsTaskInfo.getProject_name());
                intent.putExtra("team_id", corpsTaskInfo.getTeam_id());
                intent.putExtra("projectid", corpsTaskInfo.getProject_id());
                intent.putExtra("package_team_id", corpsTaskInfo.getPackage_team_id());
                intent.putExtra("type", corpsTaskInfo.getType());
                intent.putExtra("package_id", corpsTaskInfo.getPackage_id());
                getContext().startActivity(intent);
            }
        }
    };

    private int listviewHeight = 0;

    public void getDataLeft() {
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
            assign_listview.onRefreshComplete();
            return;
        }
        WaitExecutePackage.sendPostRequest(Urls.WaitExecutePackage, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        if (listLeft != null)
                            listLeft.clear();
                        else
                            listLeft = new ArrayList<CorpsTaskInfo>();
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            if (!jsonObject.isNull("list")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("list");
                                if (jsonArray != null) {
                                    assign_networkview.setVisibility(View.GONE);
                                    assign_listview.setVisibility(View.VISIBLE);
                                    int length = jsonArray.length();
                                    for (int i = 0; i < length; i++) {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        CorpsTaskInfo corpsTaskInfo = new CorpsTaskInfo();
                                        corpsTaskInfo.setProject_id(jsonObject.getString("project_id"));
                                        corpsTaskInfo.setProject_name(jsonObject.getString("project_name"));
                                        corpsTaskInfo.setPackage_id(jsonObject.getString("package_id"));
                                        corpsTaskInfo.setTeam_id(jsonObject.getString("team_id"));
                                        corpsTaskInfo.setPackage_team_id(jsonObject.getString("package_team_id"));
                                        corpsTaskInfo.setType(jsonObject.getString("type"));
                                        corpsTaskInfo.setIdentity(jsonObject.getString("identity"));
                                        corpsTaskInfo.setTotal_money(jsonObject.getString("total_money"));
                                        corpsTaskInfo.setTotal_outlet(jsonObject.getString("total_outlet"));
                                        corpsTaskInfo.setDistribution_outlet(jsonObject.getString("distribution_outlet"));
                                        corpsTaskInfo.setConfirm_outlet(jsonObject.getString("confirm_outlet"));
                                        corpsTaskInfo.setWait_exe_outlet(jsonObject.getString("wait_exe_outlet"));
                                        corpsTaskInfo.setExecution_outlet(jsonObject.getString("execution_outlet"));
                                        corpsTaskInfo.setBegin_date(jsonObject.getString("begin_date"));
                                        corpsTaskInfo.setEnd_date(jsonObject.getString("end_date"));
                                        corpsTaskInfo.setCompany_abbreviation(jsonObject.getString("company_abbreviation"));
                                        corpsTaskInfo.setCaptain_name(jsonObject.getString("captain_name"));
                                        corpsTaskInfo.setTeam_name(jsonObject.getString("team_name"));
                                        corpsTaskInfo.setProject_type(jsonObject.optString("project_type"));
                                        listLeft.add(corpsTaskInfo);
                                    }
                                } else {
                                    assign_listview.setVisibility(View.GONE);
                                    assign_listview2.setVisibility(View.GONE);
                                    assign_networkview.setVisibility(View.VISIBLE);
                                    assign_networkview.NoSearch("没有等待执行的任务哦~");
                                }
                            }
                        } else {
                            assign_listview.setVisibility(View.GONE);
                            assign_listview2.setVisibility(View.GONE);
                            assign_networkview.setVisibility(View.VISIBLE);
                            assign_networkview.NoSearch("没有等待执行的任务哦~");
                        }
                        if (corpsTaskAdapterLeft != null)
                            corpsTaskAdapterLeft.notifyDataSetChanged();
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                if (assign_listview != null) {
                    assign_listview.onRefreshComplete();
                    if (listviewHeight == 0) {
                        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
                        listviewHeight = outRect.height() - Tools.dipToPx(getActivity(), 72 + 50);
                    }
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) assign_listview.getRefreshableViewWrapper().getLayoutParams();
                    if (layoutParams.height == 0) {
                        layoutParams.height = listviewHeight;
                        assign_listview.getRefreshableViewWrapper().setLayoutParams(layoutParams);
                    }
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    WaitExecutePackage.stop(Urls.WaitExecutePackage);
                    assign_listview.onRefreshComplete();
                    assign_listview.setVisibility(View.GONE);
                    assign_listview2.setVisibility(View.GONE);
                    assign_networkview.setVisibility(View.VISIBLE);
                    assign_networkview.NoNetwork(getResources().getString(R.string.network_fail) + "点击重试");
                    assign_networkview.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            assign_networkview.setOnClickListener(null);
                            assign_networkview.NoNetwork("正在重试...");
                            getDataLeft();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getDataRight() {
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
            assign_listview2.onRefreshComplete();
            return;
        }
        TeamProjectState.sendPostRequest(Urls.TeamProjectState, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        if (listRight != null)
                            listRight.clear();
                        else
                            listRight = new ArrayList<CorpsTaskInfo>();
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            if (!jsonObject.isNull("list")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("list");
                                if (null != jsonArray) {
                                    assign_networkview.setVisibility(View.GONE);
                                    assign_listview2.setVisibility(View.VISIBLE);
                                    int length = jsonArray.length();
                                    for (int i = 0; i < length; i++) {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        CorpsTaskInfo corpsTaskInfo = new CorpsTaskInfo();
                                        corpsTaskInfo.setProject_id(jsonObject.getString("project_id"));
                                        corpsTaskInfo.setProject_name(jsonObject.getString("project_name"));
                                        corpsTaskInfo.setPackage_id(jsonObject.getString("package_id"));
                                        corpsTaskInfo.setTeam_id(jsonObject.getString("team_id"));
                                        corpsTaskInfo.setPackage_team_id(jsonObject.getString("package_team_id"));
                                        corpsTaskInfo.setType(jsonObject.getString("type"));
                                        corpsTaskInfo.setIdentity(jsonObject.getString("identity"));
                                        corpsTaskInfo.setTotal_money(jsonObject.getString("total_money"));
                                        corpsTaskInfo.setTotal_outlet(jsonObject.getString("total_outlet"));
                                        corpsTaskInfo.setBegin_date(jsonObject.getString("begin_date"));
                                        corpsTaskInfo.setEnd_date(jsonObject.getString("end_date"));
                                        corpsTaskInfo.setCompany_abbreviation(jsonObject.getString("company_abbreviation"));
                                        corpsTaskInfo.setCaptain_name(jsonObject.getString("captain_name"));
                                        corpsTaskInfo.setTeam_name(jsonObject.getString("team_name"));
                                        corpsTaskInfo.setUser_upload_outlet(jsonObject.getString("user_upload_outlet"));
                                        corpsTaskInfo.setUser_get_outlet(jsonObject.getString("user_get_outlet"));
                                        corpsTaskInfo.setCheck_outlet(jsonObject.getString("check_outlet"));
                                        corpsTaskInfo.setUnpass_outlet(jsonObject.getString("unpass_outlet"));
                                        corpsTaskInfo.setPass_outlet(jsonObject.getString("pass_outlet"));
                                        corpsTaskInfo.setProject_type(jsonObject.optString("project_type"));
                                        listRight.add(corpsTaskInfo);
                                    }
                                } else {
                                    assign_listview.setVisibility(View.GONE);
                                    assign_listview2.setVisibility(View.GONE);
                                    assign_networkview.setVisibility(View.VISIBLE);
                                    assign_networkview.NoSearch("暂无项目状态");
                                }
                            }
                        } else {
                            assign_listview.setVisibility(View.GONE);
                            assign_listview2.setVisibility(View.GONE);
                            assign_networkview.setVisibility(View.VISIBLE);
                            assign_networkview.NoSearch("暂无项目状态");
                        }
                        if (corpsTaskAdapterRight != null)
                            corpsTaskAdapterRight.notifyDataSetChanged();
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                if (assign_listview2 != null) {
                    assign_listview2.onRefreshComplete();
                    if (listviewHeight == 0) {
                        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
                        listviewHeight = outRect.height() - Tools.dipToPx(getActivity(), 72 + 50);
                    }
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) assign_listview2.getRefreshableViewWrapper().getLayoutParams();
                    if (layoutParams.height == 0) {
                        layoutParams.height = listviewHeight;
                        assign_listview2.getRefreshableViewWrapper().setLayoutParams(layoutParams);
                    }
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    TeamProjectState.stop(Urls.TeamProjectState);
                    assign_listview2.onRefreshComplete();
                    assign_listview.setVisibility(View.GONE);
                    assign_listview2.setVisibility(View.GONE);
                    assign_networkview.setVisibility(View.VISIBLE);
                    assign_networkview.NoNetwork(getResources().getString(R.string.network_fail) + "点击重试");
                    assign_networkview.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            assign_networkview.setOnClickListener(null);
                            assign_networkview.NoNetwork("正在重试...");
                            getDataLeft();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (isJoind) {
                assign_layout1.setVisibility(View.VISIBLE);
                assign_layout2.setVisibility(View.GONE);
                if (assign_oneimg.getVisibility() == View.VISIBLE) {
                    getDataLeft();
                } else {
                    getDataRight();
                }
            } else {
                assign_layout1.setVisibility(View.GONE);
                assign_layout2.setVisibility(View.VISIBLE);
                assign_networkview2.getLin_Nodata_prompt().setGravity(Gravity.LEFT);
                assign_networkview2.NoSearch("由品牌商直接指派给战队的任务，加入的战队越多，得到的奖金越多。您还没有加入战队哦，快加入战队或创建自己的战队吧。");
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.assign_one: {
                assign_one.setTextColor(Color.parseColor("#F65D57"));
                assign_two.setTextColor(Color.parseColor("#A0A0A0"));
                assign_oneimg.setVisibility(View.VISIBLE);
                assign_twoimg.setVisibility(View.INVISIBLE);
                assign_listview.setVisibility(View.VISIBLE);
                assign_listview2.setVisibility(View.GONE);
                assign_networkview.setVisibility(View.GONE);
                getDataLeft();
            }
            break;
            case R.id.assign_two: {
                assign_one.setTextColor(Color.parseColor("#A0A0A0"));
                assign_two.setTextColor(Color.parseColor("#F65D57"));
                assign_oneimg.setVisibility(View.INVISIBLE);
                assign_twoimg.setVisibility(View.VISIBLE);
                assign_listview.setVisibility(View.GONE);
                assign_listview2.setVisibility(View.VISIBLE);
                assign_networkview.setVisibility(View.GONE);
                getDataRight();
            }
            break;
            case R.id.assign_join: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                Intent intent = new Intent(getContext(), JoinCorpActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.assign_create: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                if (bindidcard) {
                    Intent intent = new Intent(getContext(), CreateCorpActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(getContext(), IdentityVerActivity.class));
                }
            }
            break;
        }
    }
}
