package com.orange.oy.activity.shakephoto_318;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.TaskCheckDesActivity;
import com.orange.oy.activity.createtask_317.TaskMouldActivity;
import com.orange.oy.activity.createtask_321.AddfeeActivity;
import com.orange.oy.activity.shakephoto_316.CollectPhotoActivity;
import com.orange.oy.adapter.PutInTaskAdapter2;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.PutInTaskInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.putin_listview2;
import static com.orange.oy.R.id.putin_listview3;
import static com.orange.oy.R.id.putin_one;
import static com.orange.oy.R.id.putin_oneing;
import static com.orange.oy.R.id.putin_three;
import static com.orange.oy.R.id.putin_threeing;
import static com.orange.oy.R.id.putin_two;
import static com.orange.oy.R.id.putin_twoing;


/**
 * beibei 我发布的任务(已投放，草稿箱，已结束)  把V3.17拆成三个页面
 */
public class PutInTask2Activity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        PutInTaskAdapter2.IPutInTask {

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.titleview);
        appTitle.showBack(this);
        if (!Tools.isEmpty(activity_status)) {
            switch (activity_status) {
                case "1":
                    appTitle.settingName("草稿箱");
                    //右侧有删除按钮
                    settingDel1();
                    break;
                case "2":
                    appTitle.settingName("投放中");
                    break;
                default:
                    appTitle.settingName("已结束");
                    break;
            }

        }

    }

    private void settingDel1() {
        appTitle.hideExit();
        if (putInTaskAdapter != null) {
            putInTaskAdapter.setDelet(false);
        }
        appTitle.showIllustrate(R.mipmap.grrw_button_shanchu, new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel2();
            }
        });
    }

    private void settingDel2() {
        appTitle.hideIllustrate();
        if (putInTaskAdapter != null) {
            putInTaskAdapter.setDelet(true);
        }
        appTitle.settingExit("完成", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel1();
            }
        });
    }

    private void iniNetworkConnection() {
        activityList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(PutInTask2Activity.this));
                params.put("token", Tools.getToken());
                params.put("activity_status", activity_status);  // 活动状态1：草稿箱未发布；2：投放中；3：已结束
                params.put("page", page + "");
                params.put("sort_type", "2");//按创建时间排序，1为正序，2为倒序，不传时默认为正序
                return params;
            }
        };
        closeProject = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(PutInTask2Activity.this));
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                return params;
            }
        };
        delActivityInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(PutInTask2Activity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);  // ai_id	活动id【必传】
                return params;
            }
        };
    }

    public void onResume() {
        super.onResume();
        if (activity_status != null) {
            getDataLeft();
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        if (activityList != null) {
            activityList.stop(Urls.ActivityList);
        }
        if (closeProject != null) {
            closeProject.stop(Urls.CloseProject);
        }
        if (delActivityInfo != null) {
            delActivityInfo.stop(Urls.DelActivityInfo);
        }
    }

    private String activity_status, project_id, ai_id;
    public static final String TAG = PutInTask2Activity.class.getName();
    private NetworkConnection activityList, delActivityInfo;
    private NetworkConnection closeProject;
    private ArrayList<PutInTaskInfo> listLeft;
    private PullToRefreshListView putin_listview;
    private NetworkView putin_networkview;
    private PutInTaskAdapter2 putInTaskAdapter;
    private AppTitle appTitle;

    private int page = 1;
    private String IsGrantInAidActivity; //判断是否从赞助费存为草稿来的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_putin_task);
        putin_listview = (PullToRefreshListView) findViewById(R.id.putin_listview);
        putin_networkview = (NetworkView) findViewById(R.id.putin_networkview);
        activity_status = getIntent().getStringExtra("activity_status");
        initTitle();
        iniNetworkConnection();
        initView();
        refreshListView();
        IsGrantInAidActivity = getIntent().getStringExtra("IsGrantInAidActivity");

    }


    private void initView() throws NullPointerException {
        listLeft = new ArrayList<>();
        putInTaskAdapter = new PutInTaskAdapter2(PutInTask2Activity.this, listLeft, activity_status);
        putin_listview.setAdapter(putInTaskAdapter);
        putInTaskAdapter.setiPutInTask(this);
    }


    public void getDataLeft() {
        activityList.stop(Urls.ActivityList);
        activityList.sendPostRequest(Urls.ActivityList, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        if (page == 1) {
                            if (listLeft != null)
                                listLeft.clear();
                            else
                                listLeft = new ArrayList<PutInTaskInfo>();
                        }
                        putin_listview.onRefreshComplete();
                        if (!jsonObject.isNull("data")) {
                            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
                            int length = jsonArray.length();
                            if (length > 0) {
                                putin_networkview.setVisibility(View.GONE);
                                for (int i = 0; i < length; i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    PutInTaskInfo putInTaskInfo = new PutInTaskInfo();
                                    putInTaskInfo.setAi_id(jsonObject.optString("ai_id"));
                                    putInTaskInfo.setActivity_name(jsonObject.optString("activity_name"));
                                    putInTaskInfo.setTarget_num(jsonObject.optString("target_num")); //目标照片数
                                    putInTaskInfo.setGet_num(jsonObject.optString("get_num")); //收到的照片数
                                    putInTaskInfo.setBegin_date(jsonObject.optString("begin_date"));
                                    putInTaskInfo.setEnd_date(jsonObject.optString("end_date"));
                                    putInTaskInfo.setActivity_status(jsonObject.optString("activity_status"));
                                    putInTaskInfo.setActivity_type(jsonObject.optString("activity_type")); //活动类型（1：集图活动；2：偶业项目）
                                    putInTaskInfo.setProject_id(jsonObject.optString("project_id"));
                                    putInTaskInfo.setTemplate_img(jsonObject.optString("template_img"));   //图标
                                    putInTaskInfo.setProject_total_money(jsonObject.optString("project_total_money"));
                                    putInTaskInfo.setMoney(jsonObject.optString("money")); // 执行单价
                                    putInTaskInfo.setTotal_num(jsonObject.optString("total_num")); //执行总量
                                    putInTaskInfo.setGettask_num(jsonObject.optString("gettask_num"));//已领数量
                                    putInTaskInfo.setDone_num(jsonObject.optString("done_num")); // 已做数量
                                    putInTaskInfo.setCheck_num(jsonObject.optString("check_num")); // 待审核数量
                                    putInTaskInfo.setComplete_num(jsonObject.optString("complete_num")); // 已做数量
                                    putInTaskInfo.setPass_num(jsonObject.optString("pass_num"));
                                    putInTaskInfo.setUnpass_num(jsonObject.optString("unpass_num"));
                                    putInTaskInfo.setReward_money(jsonObject.optString("reward_money")); //发放奖励金额"
                                    putInTaskInfo.setAd_show_num(jsonObject.optString("ad_show_num"));
                                    putInTaskInfo.setAd_click_num(jsonObject.optString("ad_click_num"));
                                    putInTaskInfo.setSponsor_num(jsonObject.optString("sponsor_num"));
                                    // 活动状态1：草稿箱未发布；2：投放中；3：已结束
                                    listLeft.add(putInTaskInfo);
                                }
                                if (putInTaskAdapter != null) {
                                    putInTaskAdapter.notifyDataSetChanged();
                                }
                                if (length < 15) {
                                    putin_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    putin_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                            } else {
                                if (page == 1) {
                                    putin_networkview.setVisibility(View.VISIBLE);
                                    if ("2".equals(activity_status)) {
                                        putin_listview.setVisibility(View.GONE);
                                        putin_networkview.SettingMSG(R.mipmap.grrw_image, "没有投放中的任务哦~");
                                    } else if ("1".equals(activity_status)) {
                                        putin_listview.setVisibility(View.GONE);
                                        putin_networkview.SettingMSG(R.mipmap.grrw_image, "草稿箱是空的哦~");
                                    } else if ("3".equals(activity_status)) {
                                        putin_listview.setVisibility(View.GONE);
                                        putin_networkview.SettingMSG(R.mipmap.grrw_image, "暂无结束活动哦~");
                                    }
                                } else {
                                    putin_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                }
                            }
                        }
                    } else {
                        Tools.showToast(PutInTask2Activity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PutInTask2Activity.this, getResources().getString(R.string.network_error));
                    putin_listview.onRefreshComplete();
                }
                putin_listview.onRefreshComplete();

            }
        }, new Response.ErrorListener()

        {
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    activityList.stop(Urls.ActivityList);
                    putin_listview.onRefreshComplete();

                    putin_networkview.setVisibility(View.VISIBLE);
                    putin_networkview.NoNetwork(getResources().getString(R.string.network_fail) + "点击重试");
                    putin_networkview.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            putin_networkview.setOnClickListener(null);
                            putin_networkview.NoNetwork("正在重试...");
                            getDataLeft();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshListView() {
        putin_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getDataLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getDataLeft();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    // "activity_type":"活动类型（1：集图活动；2：偶业项目）"
    @Override
    public void PutIntask(int position) {//草稿箱投放（可编辑）
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            PutInTaskInfo putInTaskInfo = listLeft.get(position);
            if (putInTaskInfo.getActivity_type().equals("2")) {
                project_id = putInTaskInfo.getProject_id();
                Intent intent = new Intent(this, TaskMouldActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("which_page", "1");
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, CollectPhotoActivity.class);
                intent.putExtra("ai_id", listLeft.get(position).getAi_id());
                intent.putExtra("which_page", "2");//草稿箱 集图活动 V3.20
                startActivity(intent);
            }
        }
    }

    @Override
    public void NextPutIntask(int position) {     //再投放
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            PutInTaskInfo putInTaskInfo = listLeft.get(position);
            project_id = putInTaskInfo.getProject_id();
            Intent intent = new Intent(this, TaskMouldActivity.class);
            intent.putExtra("project_id", project_id);
            intent.putExtra("which_page", "2");
            startActivity(intent);
        }
    }

    @Override
    public void Look(int position) {  //已经结束的
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            PutInTaskInfo putInTaskInfo = listLeft.get(position);
            project_id = putInTaskInfo.getProject_id();
            Intent intent = new Intent(PutInTask2Activity.this, TaskCheckDesActivity.class);
            intent.putExtra("IsTaskFinish", "2");
            intent.putExtra("project_id", putInTaskInfo.getProject_id());
            startActivity(intent);
        }
    }

    @Override
    public void Looktwo(int position) {  //已投放的
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            PutInTaskInfo putInTaskInfo = listLeft.get(position);
            project_id = putInTaskInfo.getProject_id();
            Intent intent = new Intent(PutInTask2Activity.this, TaskCheckDesActivity.class);
            intent.putExtra("IsTaskFinish", "1");
            intent.putExtra("project_id", putInTaskInfo.getProject_id());
            startActivity(intent);
        }
    }


    //==================================集图活动==================================//
    @Override
    public void Edit(int position) {   //集图活动编辑 V3.20
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            Intent intent = new Intent(this, CollectPhotoActivity.class);
            intent.putExtra("ai_id", listLeft.get(position).getAi_id());
            intent.putExtra("which_page", "1");//已投放->编辑
            startActivity(intent);
        }
    }

    @Override
    public void Lookthree(int position) {   //集图活动查看
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            PutInTaskInfo putInTaskInfo = listLeft.get(position);
            Intent intent = new Intent(PutInTask2Activity.this, NewActivityDetailActivity.class);
            intent.putExtra("activity_status", putInTaskInfo.getActivity_status());
            intent.putExtra("ai_id", putInTaskInfo.getAi_id());
            intent.putExtra("template_img", putInTaskInfo.getTemplate_img());
            startActivity(intent);
        }
    }

    @Override
    public void PutIntasktwo(int position) {  //草稿箱投放（可编辑） 集图活动
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            Intent intent = new Intent(this, CollectPhotoActivity.class);
            intent.putExtra("ai_id", listLeft.get(position).getAi_id());
            intent.putExtra("which_page", "2");
            startActivity(intent);
        }
    }

    public void FinishTask(int position) { //结束按钮
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            PutInTaskInfo putInTaskInfo = listLeft.get(position);
            ConfirmDialog.showDialog(this, "提示", 1, "确定要结束这个活动吗？", "取消", "确定", putInTaskInfo, true,
                    new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            PutInTaskInfo putInTaskInfo = (PutInTaskInfo) object;
                            project_id = putInTaskInfo.getProject_id();
                            closeProject();
                        }
                    });
        }
    }

    public void PutAgain(int position) {//集图再次投放 V3.20
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            Intent intent = new Intent(this, CollectPhotoActivity.class);
            intent.putExtra("ai_id", listLeft.get(position).getAi_id());
            intent.putExtra("which_page", "4");
            intent.putExtra("putagain", true);
            startActivity(intent);
        }
    }

    @Override
    public void deleteDraft(int position) { //删除草稿
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            ai_id = listLeft.get(position).getAi_id();
            ConfirmDialog.showDialog(this, "您确定要删除草稿？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {
                    putInTaskAdapter.setDelet(false);
                    putInTaskAdapter.notifyDataSetChanged();
                    settingDel1();
                }

                @Override
                public void rightClick(Object object) {
                    putInTaskAdapter.setDelet(false);
                    putInTaskAdapter.notifyDataSetChanged();
                    abandon();
                    settingDel1();
                }
            });
        }
    }

    @Override
    public void additionalexpenses(int position) {
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            Intent intent = new Intent(this, AddfeeActivity.class);
            intent.putExtra("project_id", listLeft.get(position).getProject_id());
            startActivity(intent);
        }
    }

    private void abandon() {
        delActivityInfo.sendPostRequest(Urls.DelActivityInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        getDataLeft();
                        Tools.showToast(PutInTask2Activity.this, "删除成功");
                    } else {
                        Tools.showToast(PutInTask2Activity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PutInTask2Activity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PutInTask2Activity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void closeProject() {
        closeProject.sendPostRequest(Urls.CloseProject, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        page = 1;
                        activity_status = "2";
                        getDataLeft();
                        ConfirmDialog.showDialog(PutInTask2Activity.this, "项目已关闭", 2, "剩余金额将在24小时内转回您的账户。", "",
                                "我知道了", null, true, null);
                    } else {
                        Tools.showToast(PutInTask2Activity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PutInTask2Activity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PutInTask2Activity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public void Share(int position) {
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            final String webUrl = Urls.InviteToActivity + "usermobile=" +
                    AppInfo.getName(PutInTask2Activity.this) + "&ai_id=" + listLeft.get(position).getAi_id();
            UMShareDialog.showDialog(PutInTask2Activity.this, false, new UMShareDialog.UMShareListener() {
                public void shareOnclick(int type) {
                    MyUMShareUtils.umShare(PutInTask2Activity.this, type, webUrl);
                }
            });
        }
    }
}
