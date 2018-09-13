package com.orange.oy.activity.shakephoto_316;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.TaskCheckDesActivity;
import com.orange.oy.activity.createtask_317.TaskMouldActivity;
import com.orange.oy.adapter.PutInTaskAdapter2;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
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


/**
 * beibei 我发布的任务(已投放，草稿箱，已结束)
 */
public class PutInTaskActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener,
        PutInTaskAdapter2.IPutInTask {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.titleview);
        appTitle.settingName("我发布的任务");
        appTitle.showBack(this);

    }

    private void iniNetworkConnection() {
        activityList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(PutInTaskActivity.this));
                params.put("token", Tools.getToken());
                params.put("activity_status", activity_status);  // 活动状态1：草稿箱未发布；2：投放中；3：已结束
                params.put("page", page + "");
                return params;
            }
        };
        closeProject = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(PutInTaskActivity.this));
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                return params;
            }
        };
    }

    public void onResume() {
        super.onResume();
        if (putin_oneing != null) {
            if (putin_oneing.getVisibility() == View.VISIBLE) {
                activity_status = "2";
                getDataLeft();
            } else if (putin_twoing.getVisibility() == View.VISIBLE) {
                activity_status = "1";
                getDataLeft();
            } else if (putin_threeing.getVisibility() == View.VISIBLE) {
                activity_status = "3";
                getDataLeft();
            }
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
    }

    private String activity_status, project_id;
    public static final String TAG = PutInTaskActivity.class.getName();
    private NetworkConnection activityList;
    private NetworkConnection closeProject;
    private ArrayList<PutInTaskInfo> listLeft;
    private PullToRefreshListView putin_listview, putin_listview2, putin_listview3;
    private NetworkView putin_networkview;
    private PutInTaskAdapter2 putInTaskAdapter1, putInTaskAdapter2, putInTaskAdapter3;
    private TextView putin_one, putin_two, putin_three;
    private View putin_oneing, putin_twoing, putin_threeing;
    private TextView lin_Nodata_prompt;
    private int page = 1;
    private ImageView lin_Nodata_img;
    private String IsGrantInAidActivity; //判断是否从赞助费存为草稿来的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_putin_task);
        putin_listview = (PullToRefreshListView) findViewById(R.id.putin_listview);
        putin_listview2 = (PullToRefreshListView) findViewById(R.id.putin_listview2);
        putin_listview3 = (PullToRefreshListView) findViewById(R.id.putin_listview3);
        putin_networkview = (NetworkView) findViewById(R.id.putin_networkview);

        putin_one = (TextView) findViewById(R.id.putin_one);
        putin_two = (TextView) findViewById(R.id.putin_two);
        putin_three = (TextView) findViewById(R.id.putin_three);
        putin_oneing = findViewById(R.id.putin_oneing);
        putin_twoing = findViewById(R.id.putin_twoing);
        putin_threeing = findViewById(R.id.putin_threeing);
        initTitle();
        iniNetworkConnection();
        initView();
        refreshListView();
        IsGrantInAidActivity = getIntent().getStringExtra("IsGrantInAidActivity");
        if (!Tools.isEmpty(IsGrantInAidActivity) && IsGrantInAidActivity.equals("1")) {
            onClick(putin_two);
        } else {
            onClick(putin_one);
        }
    }


    private void initView() throws NullPointerException {
        listLeft = new ArrayList<>();
        putInTaskAdapter1 = new PutInTaskAdapter2(PutInTaskActivity.this, listLeft, activity_status);
        putInTaskAdapter2 = new PutInTaskAdapter2(PutInTaskActivity.this, listLeft, activity_status);
        putInTaskAdapter3 = new PutInTaskAdapter2(PutInTaskActivity.this, listLeft, activity_status);
        putin_listview.setAdapter(putInTaskAdapter1);
        putin_listview2.setAdapter(putInTaskAdapter2);
        putin_listview3.setAdapter(putInTaskAdapter3);
        putInTaskAdapter1.setiPutInTask(this);
        putInTaskAdapter2.setiPutInTask(this);
        putInTaskAdapter3.setiPutInTask(this);

        putin_one.setOnClickListener(this);
        putin_two.setOnClickListener(this);
        putin_three.setOnClickListener(this);
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

                                    // 活动状态1：草稿箱未发布；2：投放中；3：已结束
                                    listLeft.add(putInTaskInfo);
                                }
                                if ("2".equals(activity_status) && putInTaskAdapter1 != null) {
                                    putInTaskAdapter1.notifyDataSetChanged();
                                } else if ("1".equals(activity_status) && putInTaskAdapter2 != null) {
                                    putInTaskAdapter2.notifyDataSetChanged();
                                } else if ("3".equals(activity_status) && putInTaskAdapter3 != null) {
                                    putInTaskAdapter3.notifyDataSetChanged();
                                }
                                if (length < 15) {
                                    if ("2".equals(activity_status)) {
                                        putin_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                                    } else if ("1".equals(activity_status)) {
                                        putin_listview2.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                                    } else if ("3".equals(activity_status)) {
                                        putin_listview3.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    }

                                } else {
                                    if ("2".equals(activity_status)) {
                                        putin_listview.setMode(PullToRefreshBase.Mode.BOTH);

                                    } else if ("1".equals(activity_status)) {
                                        putin_listview2.setMode(PullToRefreshBase.Mode.BOTH);

                                    } else if ("3".equals(activity_status)) {
                                        putin_listview3.setMode(PullToRefreshBase.Mode.BOTH);
                                    }
                                }
                            } else {
                                putin_networkview.setVisibility(View.VISIBLE);
                                if ("2".equals(activity_status)) {
                                    putin_listview.setVisibility(View.GONE);
                                    putin_networkview.SettingMSG(R.mipmap.grrw_image, "没有投放中的任务哦~");
                                } else if ("1".equals(activity_status)) {
                                    putin_listview2.setVisibility(View.GONE);
                                    putin_networkview.SettingMSG(R.mipmap.grrw_image, "草稿箱是空的哦~");
                                } else if ("3".equals(activity_status)) {
                                    putin_listview3.setVisibility(View.GONE);
                                    putin_networkview.SettingMSG(R.mipmap.grrw_image, "暂无结束活动哦~");
                                }
                            }
                        }
                    } else {
                        Tools.showToast(PutInTaskActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PutInTaskActivity.this, getResources().getString(R.string.network_error));
                    putin_listview.onRefreshComplete();
                    putin_listview2.onRefreshComplete();
                    putin_listview3.onRefreshComplete();
                }
                putin_listview.onRefreshComplete();
                putin_listview2.onRefreshComplete();
                putin_listview3.onRefreshComplete();

            }
        }, new Response.ErrorListener()

        {
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    activityList.stop(Urls.ActivityList);
                    putin_listview.onRefreshComplete();
                    putin_listview2.onRefreshComplete();
                    putin_listview3.onRefreshComplete();

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
                activity_status = "2";
                getDataLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                activity_status = "2";
                getDataLeft();
            }
        });

        putin_listview2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                activity_status = "1";
                getDataLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                activity_status = "1";
                getDataLeft();
            }
        });


        putin_listview3.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                activity_status = "3";
                getDataLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                activity_status = "3";
                getDataLeft();
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {  //1：草稿箱未发布；2：投放中；3：已结束
            case R.id.putin_one: {
                putin_one.setTextColor(Color.parseColor("#F65D57"));
                putin_two.setTextColor(Color.parseColor("#A0A0A0"));
                putin_three.setTextColor(Color.parseColor("#A0A0A0"));
                putin_oneing.setVisibility(View.VISIBLE);
                putin_twoing.setVisibility(View.INVISIBLE);
                putin_threeing.setVisibility(View.INVISIBLE);
                putin_listview.setVisibility(View.VISIBLE);
                putin_listview2.setVisibility(View.GONE);
                putin_listview3.setVisibility(View.GONE);
                putin_networkview.setVisibility(View.GONE);
                if (listLeft != null) {
                    listLeft.clear();
                }
                activity_status = "2";
                getDataLeft();
            }
            break;
            case R.id.putin_two: {
                putin_one.setTextColor(Color.parseColor("#A0A0A0"));
                putin_two.setTextColor(Color.parseColor("#F65D57"));
                putin_three.setTextColor(Color.parseColor("#A0A0A0"));
                putin_oneing.setVisibility(View.INVISIBLE);
                putin_twoing.setVisibility(View.VISIBLE);
                putin_threeing.setVisibility(View.INVISIBLE);
                putin_listview.setVisibility(View.GONE);
                putin_listview2.setVisibility(View.VISIBLE);
                putin_listview3.setVisibility(View.GONE);
                putin_networkview.setVisibility(View.GONE);
                activity_status = "1";
                if (listLeft != null) {
                    listLeft.clear();
                }
                getDataLeft();
            }
            break;
            case R.id.putin_three: {
                putin_one.setTextColor(Color.parseColor("#A0A0A0"));
                putin_two.setTextColor(Color.parseColor("#A0A0A0"));
                putin_three.setTextColor(Color.parseColor("#F65D57"));
                putin_oneing.setVisibility(View.INVISIBLE);
                putin_twoing.setVisibility(View.INVISIBLE);
                putin_threeing.setVisibility(View.VISIBLE);
                putin_listview.setVisibility(View.GONE);
                putin_listview2.setVisibility(View.GONE);
                putin_listview3.setVisibility(View.VISIBLE);
                putin_networkview.setVisibility(View.GONE);
                activity_status = "3";
                if (listLeft != null) {
                    listLeft.clear();
                }
                getDataLeft();
            }
            break;
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void PutIntask(int position) {//草稿箱投放（可编辑）
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            PutInTaskInfo putInTaskInfo = listLeft.get(position);
            project_id = putInTaskInfo.getProject_id();
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
            Intent intent = new Intent(PutInTaskActivity.this, TaskCheckDesActivity.class);
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
            Intent intent = new Intent(PutInTaskActivity.this, TaskCheckDesActivity.class);
            intent.putExtra("IsTaskFinish", "1");
            intent.putExtra("project_id", putInTaskInfo.getProject_id());
            startActivity(intent);
        }
    }


    //==================================集图活动==================================//
    @Override
    public void Edit(int position) {   //集图活动编辑
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
            Intent intent = new Intent(PutInTaskActivity.this, DetailsActivity.class);
            intent.putExtra("ai_id", putInTaskInfo.getAi_id());
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

    public void PutAgain(int position) {//集图再次投放
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            Intent intent = new Intent(this, CollectPhotoActivity.class);
            intent.putExtra("ai_id", listLeft.get(position).getAi_id());
            intent.putExtra("which_page", "2");
            intent.putExtra("putagain", true);
            startActivity(intent);
        }
    }

    @Override
    public void deleteDraft(int position) {

    }

    @Override
    public void additionalexpenses(int position) {

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
                        ConfirmDialog.showDialog(PutInTaskActivity.this, "项目已关闭", 2, "剩余金额将在24小时内转回您的账户。", "",
                                "我知道了", null, true, null);
                    } else {
                        Tools.showToast(PutInTaskActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PutInTaskActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PutInTaskActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public void Share(int position) {
        if (listLeft != null && !listLeft.isEmpty() && position < listLeft.size()) {
            final String webUrl = Urls.InviteToActivity + "usermobile=" +
                    AppInfo.getName(PutInTaskActivity.this) + "&ai_id=" + listLeft.get(position).getAi_id();
            UMShareDialog.showDialog(PutInTaskActivity.this, false, new UMShareDialog.UMShareListener() {
                public void shareOnclick(int type) {
                    MyUMShareUtils.umShare(PutInTaskActivity.this, type, webUrl);
                }
            });
        }
    }

   /* TaskCheckInfo taskCheckInfo = list.get(position);
    Intent intent = new Intent(this, CheckTaskActivity.class);
        intent.putExtra("outlet_id", taskCheckInfo.getOutlet_id());
    startActivity(intent);*/
}
