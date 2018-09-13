package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.DraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.orange.oy.R;
import com.orange.oy.activity.alipay.OuMiExchangeActivity;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.activity.experience.ExperienceLocationActivity;
import com.orange.oy.activity.newtask.MyTaskDetailActivity;
import com.orange.oy.activity.newtask.ProjectRecruitmentActivity;
import com.orange.oy.activity.newtask.TaskDistActivity;
import com.orange.oy.activity.newtask.TaskGrabActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.TaskNewFragment;
import com.orange.oy.info.ProjectRewardInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.util.ImageManager2;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.lin_Nodata;
import static com.orange.oy.R.id.oumiexchange_listview;
import static com.orange.oy.R.id.oumiexchange_totalom;
import static com.orange.oy.network.Urls.OmExchangeInfo;

/**
 * 任务标准说明
 * <p>
 * V3.12 项目说明
 */
public class TaskillustratesActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle
        , View.OnClickListener {
    private AppTitle taskILL_title;

    public void onBack() {
        baseFinish();
    }

    private void initTitle() {
        taskILL_title = (AppTitle) findViewById(R.id.taskILL_title);
        taskILL_title.settingName(getResources().getString(R.string.taskILL));
        taskILL_title.showBack(this);
        taskILL_title.showIllustrate(R.mipmap.kefu, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(TaskillustratesActivity.this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(TaskillustratesActivity.this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(TaskillustratesActivity.this));
                    info.setUname(AppInfo.getUserName(TaskillustratesActivity.this));
                }
                SobotApi.startSobotChat(TaskillustratesActivity.this, info);
            }
        });
    }

    private void initNetworkConnection() {
        checkapply = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(TaskillustratesActivity.this))) {
                    params.put("usermobile", AppInfo.getName(TaskillustratesActivity.this));
                    params.put("token", Tools.getToken());
                }
                params.put("projectid", projectid);
                return params;
            }
        };
        applyNoOutletsProject = new NetworkConnection(TaskillustratesActivity.this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TaskillustratesActivity.this));
                params.put("projectId", projectid);
                return params;
            }
        };
        projectReward = new NetworkConnection(TaskillustratesActivity.this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TaskillustratesActivity.this));
                params.put("project_id", projectid); //项目id【必传】
                return params;
            }
        };

        applyNoOutletsProject.setIsShowDialog(true);
        checkinvalid = new NetworkConnection(TaskillustratesActivity.this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskillustratesActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", outletId);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address + "");
                return params;
            }
        };
    }

    private TextView taskILL_name;
    private WebView taskILL_webview;
    private String projectid;
    private AppDBHelper appDBHelper;
    private Intent data;
    private ListView my_lv;
    private String project_property, type, isHomePage, outletId;//项目类型 任务类型   //isHomePage哪个页面传过来的 1首页 0我的任务列表 2地图
    private NetworkConnection checkapply, applyNoOutletsProject, checkinvalid, projectReward;
    private String address, latitude, longitude;
    private CheckBox checkBox;
    private TextView tv_warn;
    private ArrayList<ProjectRewardInfo> list;
    private MyAdapter myAdapter;
    private ImageLoader imageLoader;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskillustrates);
        my_lv = (ListView) findViewById(R.id.my_lv);
        list = new ArrayList<>();
        imageLoader = new ImageLoader(this);
        appDBHelper = new AppDBHelper(this);
        initTitle();
        data = getIntent();
        if (data == null) {
            Tools.showToast(this, "缺少参数");
            return;
        }
        address = data.getStringExtra("address");
        latitude = data.getStringExtra("latitude");
        longitude = data.getStringExtra("longitude");
        initNetworkConnection();
        String projectname = data.getStringExtra("projectname");
        projectid = data.getStringExtra("projectid");
        project_property = data.getStringExtra("project_property");
        type = data.getStringExtra("type");
        isHomePage = data.getStringExtra("isHomePage");
        //    taskILL_name = (TextView) findViewById(R.id.taskILL_name);
        tv_warn = (TextView) findViewById(R.id.tv_warn);
        //   taskILL_name.setText(projectname);
        taskILL_webview = (WebView) findViewById(R.id.taskILL_webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            taskILL_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        taskILL_webview.loadUrl(Urls.Standard + "?projectid=" + projectid);
        String isShow = data.getStringExtra("isShow");
        if ("0".equals(isShow)) {//不显示
            findViewById(R.id.taskILL_button).setOnClickListener(null);
//            findViewById(R.id.taskILL_layout).setVisibility(View.GONE);
            findViewById(R.id.taskILL_button).setVisibility(View.GONE);
            findViewById(R.id.lin_box).setVisibility(View.GONE);
        } else {
            findViewById(R.id.taskILL_button).setOnClickListener(this);
            findViewById(R.id.lin_box).setVisibility(View.VISIBLE);
//            findViewById(R.id.taskILL_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.taskILL_button).setVisibility(View.VISIBLE);
        }
        checkBox = (CheckBox) findViewById(R.id.taskILL_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_warn.setTextColor(getResources().getColor(R.color.homepage_select));
                } else {
                    tv_warn.setTextColor(getResources().getColor(R.color.notselected));
                }
            }
        });
        myAdapter = new MyAdapter();
        my_lv.setAdapter(myAdapter);
        getData();


    }
    //=====================记录是否显示任务页面

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.taskILL_button) {
            if (checkBox.isChecked()) {
                appDBHelper.addIsShow(projectid);
            }
            doShip();
        }
    }

    private void doShip() {
        if ("1".equals(isHomePage)) {//首页传过来的页面
            if ("1".equals(project_property)) {//分包
                if ("1".equals(type)) {//正常任务
                    data.setClass(this, TaskDistActivity.class);
                } else if ("2".equals(type)) {// 暗访任务
                    data.setClass(this, BlackDZXListActivity.class);
                } else if ("3".equals(type)) {//明访任务
                    data.setClass(this, TaskDistActivity.class);
                }
                startActivity(data);
                baseFinish();
            } else if ("2".equals(project_property)) {//众包
                checkapply();
            }
        } else if ("0".equals(isHomePage)) {//MyTaskListActivity传过来的页面
            if ("1".equals(type)) {
                data.setClass(this, MyTaskDetailActivity.class);
            } else if ("2".equals(type)) {
                data.setClass(this, BlackDZXListActivity.class);
            }
            startActivity(data);
            baseFinish();
        } else if ("2".equals(isHomePage)) {
            data.setClass(this, TaskGrabActivity.class);
            startActivity(data);
            baseFinish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (projectReward != null) {
            projectReward.stop(Urls.ProjectReward);
        }
    }

    private void getData() {
        projectReward.sendPostRequest(Urls.ProjectReward, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.optJSONArray("reward_list");
                            if (null != jsonArray && jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ProjectRewardInfo projectRewardInfo = new ProjectRewardInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    projectRewardInfo.setReward_type(object.getString("reward_type"));
                                    projectRewardInfo.setGift_url(object.getString("gift_url"));
                                    projectRewardInfo.setGift_name(object.getString("gift_name"));
                                    projectRewardInfo.setMerchant(object.getString("merchant"));
                                    projectRewardInfo.setMoney(object.getString("money"));
                                    list.add(projectRewardInfo);
                                }

                            }
                            int height = Tools.dipToPx(TaskillustratesActivity.this, (80 + 10) * list.size());
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) my_lv.getLayoutParams();
                            layoutParams.height = height;
                            my_lv.setLayoutParams(layoutParams);
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                        }

                    } else {
                        Tools.showToast(TaskillustratesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskillustratesActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskillustratesActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void checkapply() {
        checkapply.sendPostRequest(Urls.CheckApply, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!TextUtils.isEmpty(AppInfo.getKey(TaskillustratesActivity.this))) {
                        if (jsonObject.getInt("code") == 200) {
                            if ("1".equals(type)) {
                                data.setClass(TaskillustratesActivity.this, TaskGrabActivity.class);
                                data.putExtra("type1", "0");
                                startActivity(data);
                                baseFinish();
                            } else if ("4".equals(type)) {
                                data.setClass(TaskillustratesActivity.this, ExperienceLocationActivity.class);
                                startActivity(data);
                                baseFinish();
                            } else if ("5".equals(type)) {
                                ConfirmDialog.showDialog(TaskillustratesActivity.this, "是否确认申请该网点？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                        applyNoOutletsProject();
                                    }
                                });
                            }
                        } else if (jsonObject.getInt("code") == 2) {//点击进入招募令
                            data.setClass(TaskillustratesActivity.this, ProjectRecruitmentActivity.class);
                            startActivity(data);
                            baseFinish();
                        } else {
                            Tools.showToast(TaskillustratesActivity.this, jsonObject.getString("msg"));
                        }
                    } else {
                        if (jsonObject.getInt("code") == 200) {
                            if ("1".equals(type)) {
                                data.setClass(TaskillustratesActivity.this, TaskGrabActivity.class);
                                data.putExtra("type1", "0");
                                startActivity(data);
                            } else if ("4".equals(type)) {
                                data.setClass(TaskillustratesActivity.this, ExperienceLocationActivity.class);
                                startActivity(data);
                            }
                            baseFinish();
                        } else if (jsonObject.getInt("code") == 1) {//点击进入招募令
                            data.setClass(TaskillustratesActivity.this, ProjectRecruitmentActivity.class);
                            startActivity(data);
                            baseFinish();
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskillustratesActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskillustratesActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }

    private void applyNoOutletsProject() {
        applyNoOutletsProject.sendPostRequest(Urls.ApplyNoOutletsProject, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    outletId = jsonObject.getString("outletId");
                    if (code == 200) {
                        ConfirmDialog.showDialog(TaskillustratesActivity.this, "申请成功", msg, "继续申请", "开始执行", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                TaskNewFragment.isRefresh = true;
                                baseFinish();
                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType();
                            }
                        });
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(TaskillustratesActivity.this, "申请成功", msg, null, "开始执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType();
                            }
                        }).goneLeft();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(TaskillustratesActivity.this, "申请失败", msg, null, "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                    } else {
                        Tools.showToast(TaskillustratesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskillustratesActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskillustratesActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void doSelectType() {
        checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        doExecute();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(TaskillustratesActivity.this, null, jsonObject.getString("msg"), null,
                                "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                    }
                                }).goneLeft();
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(TaskillustratesActivity.this, null, jsonObject.getString("msg"), "取消",
                                "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                        doExecute();
                                    }
                                });
                    } else {
                        Tools.showToast(TaskillustratesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskillustratesActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskillustratesActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    private void doExecute() {
        data.setClass(TaskillustratesActivity.this, TaskitemDetailActivity_12.class);
        data.putExtra("id", outletId);
        data.putExtra("project_id", projectid);
        data.putExtra("store_name", "网点名称");
        data.putExtra("store_num", "网点编号");
        startActivity(data);
        baseFinish();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(TaskillustratesActivity.this, R.layout.item_task_illsutrate);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_storeName = (TextView) convertView.findViewById(R.id.tv_storeName);
                viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                viewHolder.lin_money = (LinearLayout) convertView.findViewById(R.id.lin_money);
                viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Tools.d("调用postion：" + position);
            ProjectRewardInfo projectRewardInfo = list.get(position);
            viewHolder.iv_pic.setTag(projectRewardInfo.getGift_url());
            viewHolder.iv_pic.setImageResource(R.mipmap.chai_button_ling);
            //"reward_type":"奖励类型，1为现金，2为礼品",
            viewHolder.tv_money.setText(projectRewardInfo.getMoney());

            if (!Tools.isEmpty(projectRewardInfo.getMerchant())) {
                viewHolder.tv_storeName.setText("商家 ：" + projectRewardInfo.getMerchant());
            }

            if (!Tools.isEmpty(projectRewardInfo.getReward_type())) {

                String url = projectRewardInfo.getGift_url();
                Tools.d("tag是啥：" + viewHolder.iv_pic.getTag());
                //  1为现金，2为礼品
                if ("2".equals(projectRewardInfo.getReward_type())) {
                    viewHolder.tv_name.setText(projectRewardInfo.getGift_name());
                    viewHolder.lin_money.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(url)) {
                        if (viewHolder.iv_pic.getTag().equals(url)) {
                            if (url.startsWith("http")) {
                                url = url + "?x-oss-process=image/resize,m_fill,h_100,w_100";
                            } else {
                                url = Urls.Endpoint3 + url + "?x-oss-process=image/resize,m_fill,h_100,w_100";
                            }
                            imageLoader.setShowWH(200).DisplayImage(url, viewHolder.iv_pic, R.mipmap.chai_button_ling);
                        }
                    }
                } else {
                    if (viewHolder.iv_pic.getTag().equals(url)) {
                        viewHolder.iv_pic.setImageResource(R.mipmap.chai_button_ling);
                    }
                    viewHolder.tv_name.setText("现金红包");
                    viewHolder.lin_money.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }

        class ViewHolder {
            private TextView tv_name, tv_storeName, tv_money;
            private ImageView iv_pic;
            private LinearLayout lin_money;
        }
    }
}