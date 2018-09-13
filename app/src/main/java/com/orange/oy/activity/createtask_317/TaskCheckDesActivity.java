package com.orange.oy.activity.createtask_317;

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
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.adapter.PutInTaskDesAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.TaskCheckInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.lin_Nodata_img;
import static com.orange.oy.R.id.lin_Nodata_prompt;
import static com.orange.oy.R.id.putin_three;


/**
 * beibei 投放任务的 验收和代验收
 */
public class TaskCheckDesActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, PutInTaskDesAdapter.IPutInTask {

    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.titleview);
        appTitle.settingName("任务详情");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.share2, onExitClickForAppTitle);
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            UMShareDialog.showDialog(TaskCheckDesActivity.this, false, new UMShareDialog.UMShareListener() {
                public void shareOnclick(int type) {
                    String webUrl = Urls.ShareProjectInfo + "?&project_id=" + project_id + "&usermobile=" +
                            AppInfo.getName(TaskCheckDesActivity.this) + "&sign=" + sign;
                    MyUMShareUtils.umShare(TaskCheckDesActivity.this, type, webUrl);
                }
            });
        }
    };

    private void iniNetworkConnection() {
        outletStateDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskCheckDesActivity.this));
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                params.put("type", type);////状态0为待验收，1为已验收,2为通过，3为不通过
                params.put("page", page + "");
                Tools.d("tag", params.toString());
                return params;
            }
        };

        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                String key = "project_id=" + project_id + "&usermobile=" + AppInfo.getName(TaskCheckDesActivity.this);
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", key);
                return params;
            }
        };
    }

    private PullToRefreshListView putin_listview, putin_listview2, putin_listview3;
    private NetworkView putin_networkview;
    private PutInTaskDesAdapter putInTaskDesAdapter1, putInTaskDesAdapter2, putInTaskDesAdapter3;
    private TextView putin_one, putin_two, putin_three;
    private View putin_oneing, putin_twoing, putin_threeing;
    private NetworkConnection outletStateDetail, Sign;
    private int page = 1;
    private String type, sign;
    private View headview; //头布局
    private ArrayList<TaskCheckInfo> list;
    private String IsTaskFinish; //1投放中 2已结束
    private TextView tv_name, tv_money, tv_time, tv_period, tv_des;
    private ImageView tv_preview, iv_standard, iv_pic;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_check_des);
        initTitle();
        iniNetworkConnection();
        project_id = getIntent().getStringExtra("project_id");
        IsTaskFinish = getIntent().getStringExtra("IsTaskFinish");
        imageLoader = new ImageLoader(this);
        putin_listview = (PullToRefreshListView) findViewById(R.id.putin_listview);
        putin_listview2 = (PullToRefreshListView) findViewById(R.id.putin_listview2);
        putin_listview3 = (PullToRefreshListView) findViewById(R.id.putin_listview3);

        putin_networkview = (NetworkView) findViewById(R.id.putin_networkview);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_period = (TextView) findViewById(R.id.tv_period);
        tv_preview = (ImageView) findViewById(R.id.tv_preview);
        iv_standard = (ImageView) findViewById(R.id.iv_standard);
        tv_des = (TextView) findViewById(R.id.tv_des);
        putin_one = (TextView) findViewById(R.id.putin_one);
        putin_two = (TextView) findViewById(R.id.putin_two);
        putin_three = (TextView) findViewById(R.id.putin_three);
        putin_oneing = findViewById(R.id.putin_oneing);
        putin_twoing = findViewById(R.id.putin_twoing);
        putin_threeing = findViewById(R.id.putin_threeing);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);


        Sign();
        initView();
        refreshListView();
    }

    private void initView() throws NullPointerException {
        list = new ArrayList<>();
        putInTaskDesAdapter1 = new PutInTaskDesAdapter(TaskCheckDesActivity.this, list);
        putInTaskDesAdapter2 = new PutInTaskDesAdapter(TaskCheckDesActivity.this, list);
        putInTaskDesAdapter3 = new PutInTaskDesAdapter(TaskCheckDesActivity.this, list);
        putin_listview.setAdapter(putInTaskDesAdapter1);
        putin_listview2.setAdapter(putInTaskDesAdapter2);
        putin_listview3.setAdapter(putInTaskDesAdapter3);
        putInTaskDesAdapter1.setiPutInTask(this);
        putInTaskDesAdapter2.setiPutInTask(this);
        putInTaskDesAdapter3.setiPutInTask(this);
        tv_preview.setOnClickListener(this);
        iv_standard.setOnClickListener(this);

        putin_one.setOnClickListener(this);
        putin_two.setOnClickListener(this);
        putin_three.setOnClickListener(this);

        if (!Tools.isEmpty(IsTaskFinish)) {
            if (IsTaskFinish.equals("2")) {
                onClick(putin_one);
            } else {
                onClick(putin_three);
            }
        }

    }

    private String total_outlet, project_id, project_name, template_img, begin_date, end_date, project_total_money,
            check_time, standard_state;

    public void onResume() {
        super.onResume();
        if (putin_oneing != null) {
            if (putin_oneing.getVisibility() == View.VISIBLE) {  //已通过
              /*  if (IsTaskFinish.equals("1")) {
                    type = "0";
                    tv_des.setVisibility(View.VISIBLE);
                } else {*/
                type = "2";
                tv_des.setVisibility(View.GONE);
                //  }
                getDataLeft();
            } else if (putin_twoing.getVisibility() == View.VISIBLE) {  //未通过
                type = "3";
                tv_des.setVisibility(View.GONE);
                getDataLeft();
            } else if (putin_threeing.getVisibility() == View.VISIBLE) {  //待验收
                /*if (IsTaskFinish.equals("1")) {
                    type = "1";
                    tv_des.setVisibility(View.GONE);
                } else {
                    type = "3";
                    tv_des.setVisibility(View.GONE);
                }*/
                type = "0";
                tv_des.setVisibility(View.VISIBLE);
                getDataLeft();
            }
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        if (outletStateDetail != null) {
            outletStateDetail.stop(Urls.OutletStateDetail);
        }

    }

    private String num, check_num, complete_num, pass_num, unpass_num;

    public void getDataLeft() {
        outletStateDetail.sendPostRequest(Urls.OutletStateDetail, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        if (page == 1) {
                            if (list != null)
                                list.clear();
                            else
                                list = new ArrayList<TaskCheckInfo>();
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.optJSONObject("data");
                            JSONObject object2 = jsonObject.optJSONObject("project_info");
                            // total_outlet = jsonObject.optString("total_outlet");// 该状态总的网点数
                            pass_num = jsonObject.optString("pass_num");  //已通过的总数量
                            unpass_num = jsonObject.optString("unpass_num"); //未通过的总数量
                            check_num = jsonObject.optString("check_num"); //待验收的总数量
                            complete_num = jsonObject.optString("complete_num"); //已验收的总数量

                            if (!Tools.isEmpty(IsTaskFinish)) {
                                if (IsTaskFinish.equals("2")) {
                                    //type);  状态0为待验收，1为已验收,2为通过，3为不通过
                                    tv_period.setVisibility(View.INVISIBLE);

                                    putin_three.setVisibility(View.GONE);
                                    putin_threeing.setVisibility(View.GONE);
                                    //  putin_oneing.setVisibility(View.VISIBLE);
                                    putin_one.setText("通过 " + pass_num);
                                    putin_two.setText("不通过 " + unpass_num);


                                } else {
                                    putin_three.setText("待验收 " + check_num);
                                    putin_one.setText("已通过 " + pass_num);
                                    putin_two.setText("未通过 " + unpass_num);

                                    if ("0".equals(type)) {
                                        putin_three.setVisibility(View.VISIBLE);
                                        putin_threeing.setVisibility(View.VISIBLE);
                                        tv_period.setVisibility(View.VISIBLE);

                                    } else if ("2".equals(type)) {

                                    } else if ("3".equals(type)) {

                                    }
                                }
                            }
                            project_id = object2.getString("project_id");
                            project_name = object2.getString("project_name");
                            template_img = object2.getString("template_img");
                            begin_date = object2.getString("begin_date");
                            end_date = object2.getString("end_date");
                            project_total_money = object2.getString("project_total_money"); // 总金额
                            check_time = object2.getString("check_time");  // 审核周期
                            standard_state = object2.getString("standard_state"); // 是否有项目说明，1为有，0为没有

                            imageLoader.DisplayImage(Urls.ImgIp + template_img, iv_pic, R.mipmap.round_pai);

                            if (!Tools.isEmpty(project_name)) {
                                tv_name.setText(project_name);
                            }
                            if (!Tools.isEmpty(begin_date) && !Tools.isEmpty(end_date)) {
                                tv_time.setText("任务起止日期：" + begin_date + "~" + end_date);
                            }
                            if (!Tools.isEmpty(project_total_money)) {
                                tv_money.setText("任务总金额：" + Tools.removePoint(project_total_money) + "元");
                            }
                            if (!Tools.isEmpty(check_time)) {
                                int time = (int) Tools.StringToDouble(check_time);
                                if (time < 0) {
                                    time = 0;
                                }
                                tv_period.setText("审核周期：" + time + "天");
                            }

                            JSONArray jsonArray = jsonObject.optJSONArray("outlet_list");
                            if (null != jsonArray) {
                                int length = jsonArray.length();
                                if (length > 0) {
                                    putin_networkview.setVisibility(View.GONE);
                                    for (int i = 0; i < length; i++) {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        TaskCheckInfo taskCheckInfo = new TaskCheckInfo();
                                        taskCheckInfo.setOutlet_id(jsonObject.getString("outlet_id"));
                                        taskCheckInfo.setOutlet_name(jsonObject.getString("outlet_name"));
                                        taskCheckInfo.setUser_name(jsonObject.getString("user_name"));
                                        taskCheckInfo.setUser_mobile(jsonObject.getString("user_mobile"));
                                        taskCheckInfo.setComplete_time(jsonObject.getString("complete_time"));
                                        taskCheckInfo.setPass_state(jsonObject.getString("pass_state")); //通过状态  1为通过，0为不通过
                                        taskCheckInfo.setMoney(jsonObject.getString("money"));
                                        taskCheckInfo.setAddress(jsonObject.getString("address"));
                                        taskCheckInfo.setType(type);
                                        list.add(taskCheckInfo);
                                    }
                                    //type    ////状态0为待验收，1为已验收,2为通过，3为不通过
                                    if ("0".equals(type) && putInTaskDesAdapter3 != null) {
                                        putInTaskDesAdapter3.notifyDataSetChanged();

                                    } else if ("1".equals(type) && putInTaskDesAdapter2 != null) {
                                        putInTaskDesAdapter2.notifyDataSetChanged();

                                    } else if ("2".equals(type) && putInTaskDesAdapter1 != null) {

                                        putInTaskDesAdapter1.notifyDataSetChanged();

                                    } else if ("3".equals(type) && putInTaskDesAdapter2 != null) {
                                        putInTaskDesAdapter2.notifyDataSetChanged();
                                    }
                                    if (length < 15) {
                                        if ("0".equals(type)) {
                                            putin_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        } else if ("1".equals(type)) {
                                            putin_listview2.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                                        } else if ("2".equals(type)) {
                                            putin_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        } else if ("3".equals(type)) {
                                            putin_listview2.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                                        }

                                    } else {
                                        if ("0".equals(type)) {
                                            putin_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                        } else if ("1".equals(type)) {
                                            putin_listview2.setMode(PullToRefreshBase.Mode.BOTH);

                                        } else if ("2".equals(type)) {
                                            putin_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                        } else if ("3".equals(type)) {
                                            putin_listview2.setMode(PullToRefreshBase.Mode.BOTH);
                                        }
                                    }
                                }
                            } else {
                                putin_networkview.setVisibility(View.VISIBLE);
                                if ("0".equals(type)) {
                                    putin_listview.setVisibility(View.GONE);
                                    putin_networkview.SettingMSG(R.mipmap.grrw_image, "没有待验收的任务哦~");
                                } else if ("1".equals(type)) {
                                    putin_listview2.setVisibility(View.GONE);
                                    putin_networkview.SettingMSG(R.mipmap.grrw_image, "没有已验收的任务哦~");
                                } else if ("2".equals(type)) {
                                    putin_listview.setVisibility(View.GONE);
                                    putin_networkview.SettingMSG(R.mipmap.grrw_image, "没有已通过的任务哦~");
                                } else if ("3".equals(type)) {
                                    putin_listview2.setVisibility(View.GONE);
                                    putin_networkview.SettingMSG(R.mipmap.grrw_image, "没有不通过的任务哦~");
                                }

                            }
                        }
                    } else {
                        Tools.showToast(TaskCheckDesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskCheckDesActivity.this, getResources().getString(R.string.network_error));
                    putin_listview.onRefreshComplete();
                    putin_listview2.onRefreshComplete();
                }
                putin_listview.onRefreshComplete();
                putin_listview2.onRefreshComplete();

            }
        }, new Response.ErrorListener()

        {
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    outletStateDetail.stop(Urls.OutletStateDetail);
                    putin_listview.onRefreshComplete();
                    putin_listview2.onRefreshComplete();

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
               /* if (IsTaskFinish.equals("1")) {
                    type = "0";
                    tv_des.setVisibility(View.VISIBLE);
                } else {*/
                type = "2";
                tv_des.setVisibility(View.GONE);
                // }
                getDataLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
               /* if (IsTaskFinish.equals("1")) {
                    type = "0";
                    tv_des.setVisibility(View.GONE);
                } else {*/
                type = "2";
                tv_des.setVisibility(View.GONE);
                // }
                getDataLeft();
            }
        });

        putin_listview2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
               /* if (IsTaskFinish.equals("1")) {
                    //已验收或者是不通过
                    type = "1";
                    tv_des.setVisibility(View.VISIBLE);
                } else {*/
                type = "3";
                tv_des.setVisibility(View.GONE);
                //   }
                getDataLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                if (IsTaskFinish.equals("1")) {
                    //已验收或者是不通过
                    type = "1";
                    tv_des.setVisibility(View.GONE);
                } else {
                    type = "3";
                    tv_des.setVisibility(View.GONE);
                }
                getDataLeft();
            }
        });

        putin_listview3.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                type = "0";
                tv_des.setVisibility(View.VISIBLE);
                getDataLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                type = "0";
                tv_des.setVisibility(View.GONE);
                getDataLeft();
            }
        });
    }

    private void Sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                    } else {
                        Tools.showToast(TaskCheckDesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskCheckDesActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskCheckDesActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    //     //type);  状态0为待验收，1为已验收,2为通过，3为不通过
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.putin_three: {
                appTitle.hideIllustrate();
                putin_three.setTextColor(Color.parseColor("#F65D57"));
                putin_one.setTextColor(Color.parseColor("#A0A0A0"));
                putin_two.setTextColor(Color.parseColor("#A0A0A0"));

                putin_threeing.setVisibility(View.VISIBLE);
                putin_oneing.setVisibility(View.INVISIBLE);
                putin_twoing.setVisibility(View.INVISIBLE);

                putin_listview3.setVisibility(View.VISIBLE);
                putin_listview.setVisibility(View.GONE);
                putin_listview2.setVisibility(View.GONE);

                putin_networkview.setVisibility(View.GONE);

                type = "0";
                tv_des.setVisibility(View.VISIBLE);

                if (list != null) {
                    list.clear();
                }
                getDataLeft();
            }
            break;
            case R.id.putin_one: {  //已通过
                appTitle.hideIllustrate();

                putin_one.setTextColor(Color.parseColor("#F65D57"));
                putin_two.setTextColor(Color.parseColor("#A0A0A0"));
                putin_three.setTextColor(Color.parseColor("#A0A0A0"));

                if (!Tools.isEmpty(IsTaskFinish)) {
                    if (IsTaskFinish.equals("2")) {
                        putin_oneing.setVisibility(View.VISIBLE);
                        putin_twoing.setVisibility(View.INVISIBLE);
                        putin_threeing.setVisibility(View.GONE);
                    } else {
                        putin_oneing.setVisibility(View.VISIBLE);
                        putin_twoing.setVisibility(View.INVISIBLE);
                        putin_threeing.setVisibility(View.INVISIBLE);
                    }
                }


                putin_listview.setVisibility(View.VISIBLE);
                putin_listview2.setVisibility(View.GONE);
                putin_listview3.setVisibility(View.GONE);

                putin_networkview.setVisibility(View.GONE);
              /*  if (IsTaskFinish.equals("1")) {
                    //通过或者是待验收
                    type = "0";
                    tv_des.setVisibility(View.VISIBLE);
                } else {*/
                type = "2";
                tv_des.setVisibility(View.GONE);
                //  }

                if (list != null) {
                    list.clear();
                }
                getDataLeft();
            }
            break;
            case R.id.putin_two: {  //未通过
                appTitle.showIllustrate(R.mipmap.share2, onExitClickForAppTitle);
                putin_three.setTextColor(Color.parseColor("#A0A0A0"));
                putin_one.setTextColor(Color.parseColor("#A0A0A0"));
                putin_two.setTextColor(Color.parseColor("#F65D57"));

                if (!Tools.isEmpty(IsTaskFinish)) {
                    if (IsTaskFinish.equals("2")) {
                        putin_twoing.setVisibility(View.VISIBLE);
                        putin_threeing.setVisibility(View.GONE);
                        putin_oneing.setVisibility(View.INVISIBLE);
                    } else {
                        putin_twoing.setVisibility(View.VISIBLE);
                        putin_threeing.setVisibility(View.INVISIBLE);
                        putin_oneing.setVisibility(View.INVISIBLE);
                    }
                }

                putin_listview.setVisibility(View.GONE);
                putin_listview2.setVisibility(View.VISIBLE);
                putin_listview3.setVisibility(View.GONE);

                putin_networkview.setVisibility(View.GONE);
              /*  if (IsTaskFinish.equals("1")) {
                    //已验收或者是不通过
                    type = "1";
                    tv_des.setVisibility(View.GONE);
                } else {*/
                type = "3";
                tv_des.setVisibility(View.GONE);
                // }
                if (list != null) {
                    list.clear();
                }
                getDataLeft();
            }
            break;
            case R.id.tv_preview: {//跳转至任务预览页面；
                Intent intent = new Intent(TaskCheckDesActivity.this, TaskitemDetailActivity_12.class);
                intent.putExtra("id", project_id);//网点id
                intent.putExtra("projectname", project_name);
                intent.putExtra("store_name", "网点名称");
                intent.putExtra("store_num", "网点编号");
                intent.putExtra("province", "");
                intent.putExtra("city", "");
                intent.putExtra("project_id", project_id);
                intent.putExtra("photo_compression", "");
                intent.putExtra("is_record", "");
                intent.putExtra("is_watermark", "");//int
                intent.putExtra("code", "");
                intent.putExtra("brand", "");
                intent.putExtra("is_takephoto", "");//String
                intent.putExtra("project_type", "1");
                intent.putExtra("is_desc", "");
                intent.putExtra("index", "0");
                startActivity(intent);
            }
            break;
            case R.id.iv_standard: {  //跳转至任务说明页面；
                Intent intent = new Intent(TaskCheckDesActivity.this, TaskillustratesActivity.class);
                intent.putExtra("projectid", project_id);
                intent.putExtra("projectname", project_name);
                intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                startActivity(intent);
            }
            break;
        }
    }

    public void onBack() {
        baseFinish();
    }

    public void check(int position) { //验收资料页面
        TaskCheckInfo taskCheckInfo = list.get(position);
        Intent intent = new Intent(this, CheckTaskActivity.class);
        intent.putExtra("outlet_id", taskCheckInfo.getOutlet_id());
        intent.putExtra("name", project_name);
        intent.putExtra("time", tv_time.getText().toString());
        intent.putExtra("money", tv_money.getText().toString());
        intent.putExtra("nametask", taskCheckInfo.getOutlet_name());
        intent.putExtra("timetask", taskCheckInfo.getComplete_time());
        intent.putExtra("addresstask", taskCheckInfo.getAddress());
        startActivity(intent);
    }

    public void Look(int position) { //查看资料页面
        TaskCheckInfo taskCheckInfo = list.get(position);
        Intent intent = new Intent(this, TaskFinishActivity.class);
        intent.putExtra("store_id", taskCheckInfo.getOutlet_id());
        intent.putExtra("store_name", taskCheckInfo.getOutlet_name());
        intent.putExtra("projectname", project_name);
        intent.putExtra("store_num", "");
        intent.putExtra("project_id", project_id);
        intent.putExtra("state", "2");
        intent.putExtra("photo_compression", "");
        intent.putExtra("is_watermark", "");
        intent.putExtra("code", "");
        intent.putExtra("brand", "");
        intent.putExtra("isAgain", false);
        intent.putExtra("isShare", true);
        startActivity(intent);
    }
}
