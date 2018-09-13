package com.orange.oy.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.OfflinePackageActivity;
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.black.BlackillustrateActivity;
import com.orange.oy.activity.experience.ExperienceCommentActivity;
import com.orange.oy.activity.experience.ExperienceEditActivity;
import com.orange.oy.activity.experience.ExperienceillActivity;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.newtask.MyaccountActivity;
import com.orange.oy.activity.shakephoto_320.PrizeListActivity;
import com.orange.oy.adapter.ApplyAdapter;
import com.orange.oy.adapter.ApplyLaterAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MyRewardInfo;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.info.UpdataInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.pass_num;

/**
 * A simple {@link Fragment} subclass.
 * 申请的任务页面
 */
public class ApplyFragment extends BaseFragment implements View.OnClickListener, ApplyAdapter.AbandonButton, ApplyLaterAdapter.AbandonUnpass, NetworkConnection.OnOutTimeListener {


    public ApplyFragment() {
        // Required empty public constructor
    }

    private void initNetworkConnection() {
        applyStartList = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("page", page + "");
                return params;
            }
        };
        applyStartList.setOnOutTimeListener(this);
        applyStartList.setTimeCount(true);
        checkinvalid = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("token", Tools.getToken());
                params.put("outletid", storeid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address);
                return params;
            }
        };
        myReward = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                /**
                 *
                 * //*********state	状态值（不传为全部，-1为上传中，0为审核中，2为未通过，3为已通过）
                 *
                 */
                params.put("state", state);
                params.put("page", page + "");
                return params;
            }
        };
        myReward.setIsShowDialog(true);
        abandon = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("storeid", storeid);
                return params;
            }
        };
        abandon.setIsShowDialog(true);


        abandonUnpass = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));  //usermobile	用户账号【必填】
                params.put("storeid", storeid);   // storeid	网点id【必填】
                return params;
            }
        };
        abandonUnpass.setIsShowDialog(true);
        shareToSquare = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("storeid", storeid);
                return params;
            }
        };
    }

    private boolean isRefresh = false;

    public void onHiddenChanged(boolean hidden) {

    /*    if (hidden) {   // 不在最前端显示 相当于调用了onPause();
            return;
        }else{  // 在最前端显示 相当于调用了onResume();
            //网络数据刷新nn..
        }
        */
        if (isFirst || hidden) {
            return;
        }
        if (applylistview_one.getVisibility() == View.VISIBLE) {
            EventBus.getDefault().post("4");
        } else {
            EventBus.getDefault().post("3");
        }
    }

    //---------状态值（不传为全部，-1为上传中，0为审核中，2为未通过，3为已通过）
    @Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
        }
        if (isRefresh) {
            isRefresh = false;
            if (applylistview_one.getVisibility() == View.VISIBLE) {
                refreshDataOne();
            } else if (applylistview_two.getVisibility() == View.VISIBLE) {
                state = "";
                refreshDataTwo();
            } else if (applylistview_three.getVisibility() == View.VISIBLE) {
                state = "2";
                refreshDataTwo();
            } else if (applylistview_four.getVisibility() == View.VISIBLE) {
                state = "-1";
                refreshDataTwo();
            } else if (applylistview_five.getVisibility() == View.VISIBLE) {
                state = "0";
                refreshDataTwo();
            } else if (applylistview_six.getVisibility() == View.VISIBLE) {
                state = "3";
                refreshDataTwo();
            }
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
        if (applyStartList != null) {
            applyStartList.stop(Urls.ApplyStartList);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
        if (myReward != null) {
            myReward.stop(Urls.MyReward2);
        }
        if (abandon != null) {
            abandon.stop(Urls.Abandon);
        }
        if (abandonUnpass != null) {
            abandonUnpass.stop(Urls.abandonUnpass4);
        }
    }

    public static final String TAG = ApplyFragment.class.getName();
    private NetworkConnection applyStartList, checkinvalid, myReward, abandon, abandonUnpass, shareToSquare;
    private int page = 1;
    private String state;
    private TextView apply_one, apply_two;
    private LinearLayout lin_task_state;
    private View apply_oneimg, apply_twoimg;
    private PullToRefreshListView applylistview_one, applylistview_two, applylistview_three, applylistview_four,
            applylistview_five, applylistview_six;
    private ArrayList<TaskNewInfo> groupkey;
    private ArrayList<Object> list;
    private ArrayList<MyRewardInfo> list_myReward;
    private ApplyAdapter applyAdapter1;
    private ApplyLaterAdapter laterAdapter1, laterAdapter2, laterAdapter3, laterAdapter4, laterAdapter5;
    private double longitude, latitude;//经纬度
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private int locType;
    private String storeid, address;
    private UpdataDBHelper updataDBHelper;
    private TextView apply_one2, apply_two2, apply_three2, apply_four2, tv_tag;
    private LinearLayout lin_task1, lin_task2, lin_task3, lin_task4;
    private TextView tv_No, tv_now, tv_shen, tv_Yes;
    private LinearLayout lin_Nodata; //没有数据的布局
    private TextView lin_Nodata_prompt;
    private ImageView lin_Nodata_img;
    private boolean isFirst = false;// 是否第一次进入
    private boolean isLoadSuccess;//是否加载成功
    private LinearLayout lin_ly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isFirst = true;
        View view = inflater.inflate(R.layout.fragment_apply, container, false);
        initView(view);
        EventBus.getDefault().register(this);
        updataDBHelper = new UpdataDBHelper(getContext());
        initNetworkConnection();
        list = new ArrayList<>();
        groupkey = new ArrayList<>();
        list_myReward = new ArrayList<>();
        onClick(apply_one);
        refreshListView();
        lin_ly.setVisibility(View.GONE);

        applyAdapter1 = new ApplyAdapter(getContext(), list);
        applylistview_one.setAdapter(applyAdapter1);
        applyAdapter1.setAbandonButtonListener(this);

        laterAdapter1 = new ApplyLaterAdapter(getContext(), list_myReward);
        applylistview_two.setAdapter(laterAdapter1);
        laterAdapter1.setAbandonButtonListener(this);

        laterAdapter2 = new ApplyLaterAdapter(getContext(), list_myReward);
        applylistview_three.setAdapter(laterAdapter2);
        laterAdapter2.setAbandonButtonListener(this);

        laterAdapter3 = new ApplyLaterAdapter(getContext(), list_myReward);
        applylistview_four.setAdapter(laterAdapter3);


        laterAdapter4 = new ApplyLaterAdapter(getContext(), list_myReward);
        applylistview_five.setAdapter(laterAdapter4);


        laterAdapter5 = new ApplyLaterAdapter(getContext(), list_myReward);
        applylistview_six.setAdapter(laterAdapter5);
        laterAdapter5.setAbandonButtonListener(this);
        onItemClickAll();
        onItemClickOne1();
        onItemClickOne2();
        onItemClickOne3();
        onItemClickOne4();
        initLocation();
        return view;
    }

    private void onItemClickOne2() {
        applylistview_four.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //上传中
                MyRewardInfo myRewardInfo = list_myReward.get(position - 1);
                String type = myRewardInfo.getType();
                String isclose = myRewardInfo.getIsclose();
                storeid = myRewardInfo.getOutletId();
                if (null != laterAdapter4) {


                    if ("2".equals(type)) {
                        Tools.showToast(getContext(), "此项目类型不支持查看详情");
                        return;
                    }
                    if ("4".equals(type)) {
                        if ("1".equals(isclose)) {
                            Tools.showToast(getContext(), "该网点已被置无效，不能查看详情");
                            return;
                        }
                        Intent intent = new Intent(getContext(), ExperienceCommentActivity.class);
                        intent.putExtra("taskid", "");
                        intent.putExtra("storeid", storeid);
                        intent.putExtra("projectid", myRewardInfo.getProject_id());
                        intent.putExtra("packageid", "");
                        intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                        intent.putExtra("project_name", myRewardInfo.getPersonName());
                        intent.putExtra("brand", myRewardInfo.getBrand());
                        intent.putExtra("storeName", myRewardInfo.getOutletName());
                        intent.putExtra("storecode", myRewardInfo.getCode());
                        intent.putExtra("source", "1");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), TaskFinishActivity.class);
                        intent.putExtra("projectname", myRewardInfo.getProjectName());
                        intent.putExtra("store_name", myRewardInfo.getOutletName());
                        intent.putExtra("store_num", myRewardInfo.getCode());
                        intent.putExtra("project_id", myRewardInfo.getProject_id());
                        intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                        intent.putExtra("store_id", myRewardInfo.getOutletId());
                        intent.putExtra("state", "1");
                        intent.putExtra("is_watermark", myRewardInfo.getIs_watermark());
                        intent.putExtra("code", myRewardInfo.getCode());
                        intent.putExtra("brand", myRewardInfo.getBrand());
                        intent.putExtra("isAgain", false);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String data) {
        if (null != applyAdapter1) {
            applyAdapter1.setIsSwif(data);
            applyAdapter1.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isRefresh = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void onItemClickOne1() {
        applylistview_three.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyRewardInfo myRewardInfo = list_myReward.get(position - 1);
                if (laterAdapter2 != null) {
                    /*Intent intent = new Intent(getContext(), TaskitemDetailActivity_12.class);
                    intent.putExtra("id", myRewardInfo.getOutletId());
                    intent.putExtra("projectname", myRewardInfo.getProjectName());
                    intent.putExtra("store_name", myRewardInfo.getOutletName());
                    intent.putExtra("store_num", myRewardInfo.getCode());
//                        intent.putExtra("province", myRewardInfo.getc);
//                        intent.putExtra("city", myRewardInfo.getCity2());
                    intent.putExtra("project_id", myRewardInfo.getProject_id());
                    intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                    intent.putExtra("is_record", myRewardInfo.getIs_record());
                    intent.putExtra("is_watermark", myRewardInfo.getIs_watermark() + "");//int
                    intent.putExtra("project_type", myRewardInfo.getType());
                    intent.putExtra("code", myRewardInfo.getCode());
                    intent.putExtra("brand", myRewardInfo.getBrand());
                    intent.putExtra("is_takephoto", myRewardInfo.getIs_takephoto());//String
                    startActivity(intent);*/
                    Intent intent = new Intent(getContext(), TaskFinishActivity.class);
                    intent.putExtra("projectname", myRewardInfo.getProjectName());
                    intent.putExtra("store_name", myRewardInfo.getOutletName());
                    intent.putExtra("store_num", myRewardInfo.getCode());
                    intent.putExtra("project_id", myRewardInfo.getProject_id());
                    intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                    intent.putExtra("store_id", myRewardInfo.getOutletId());
                    intent.putExtra("state", "2");
                    intent.putExtra("is_watermark", myRewardInfo.getIs_watermark());
                    intent.putExtra("code", myRewardInfo.getCode());
                    intent.putExtra("brand", myRewardInfo.getBrand());
                    intent.putExtra("isAgain", false);
                    startActivity(intent);
                }

            }
        });
    }

    private void onItemClickOne3() {
        applylistview_five.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //审核中，就是已上传
                MyRewardInfo myRewardInfo = list_myReward.get(position - 1);
                String type = myRewardInfo.getType();
                String isclose = myRewardInfo.getIsclose();
                storeid = myRewardInfo.getOutletId();
                if (null != laterAdapter4) {


                    if ("2".equals(type)) {
                        Tools.showToast(getContext(), "此项目类型不支持查看详情");
                        return;
                    }
                    if ("4".equals(type)) {
                        if ("1".equals(isclose)) {
                            Tools.showToast(getContext(), "该网点已被置无效，不能查看详情");
                            return;
                        }
                        Intent intent = new Intent(getContext(), ExperienceCommentActivity.class);
                        intent.putExtra("taskid", "");
                        intent.putExtra("storeid", storeid);
                        intent.putExtra("projectid", myRewardInfo.getProject_id());
                        intent.putExtra("packageid", "");
                        intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                        intent.putExtra("project_name", myRewardInfo.getPersonName());
                        intent.putExtra("brand", myRewardInfo.getBrand());
                        intent.putExtra("storeName", myRewardInfo.getOutletName());
                        intent.putExtra("storecode", myRewardInfo.getCode());
                        intent.putExtra("source", "1");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), TaskFinishActivity.class);
                        intent.putExtra("projectname", myRewardInfo.getProjectName());
                        intent.putExtra("store_name", myRewardInfo.getOutletName());
                        intent.putExtra("store_num", myRewardInfo.getCode());
                        intent.putExtra("project_id", myRewardInfo.getProject_id());
                        intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                        intent.putExtra("store_id", myRewardInfo.getOutletId());
                        intent.putExtra("state", "2");
                        intent.putExtra("is_watermark", myRewardInfo.getIs_watermark());
                        intent.putExtra("code", myRewardInfo.getCode());
                        intent.putExtra("brand", myRewardInfo.getBrand());
                        intent.putExtra("isAgain", false);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void onItemClickOne4() {
        applylistview_six.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MyRewardInfo myRewardInfo = list_myReward.get(position - 1);
                String type = myRewardInfo.getType();
                String isclose = myRewardInfo.getIsclose();
                storeid = myRewardInfo.getOutletId();
                if (null != laterAdapter5) {
                    // Intent intent = new Intent(getContext(), MyaccountActivity.class);
                    //startActivity(intent);

                    if ("2".equals(type)) {
                        Tools.showToast(getContext(), "此项目类型不支持查看详情");
                        return;
                    }
                    if ("4".equals(type)) {
                        if ("1".equals(isclose)) {
                            Tools.showToast(getContext(), "该网点已被置无效，不能查看详情");
                            return;
                        }
                        Intent intent = new Intent(getContext(), ExperienceCommentActivity.class);
                        intent.putExtra("taskid", "");
                        intent.putExtra("storeid", storeid);
                        intent.putExtra("projectid", myRewardInfo.getProject_id());
                        intent.putExtra("packageid", "");
                        intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                        intent.putExtra("project_name", myRewardInfo.getPersonName());
                        intent.putExtra("brand", myRewardInfo.getBrand());
                        intent.putExtra("storeName", myRewardInfo.getOutletName());
                        intent.putExtra("storecode", myRewardInfo.getCode());
                        intent.putExtra("source", "1");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), TaskFinishActivity.class);
                        intent.putExtra("projectname", myRewardInfo.getProjectName());
                        intent.putExtra("store_name", myRewardInfo.getOutletName());
                        intent.putExtra("store_num", myRewardInfo.getCode());
                        intent.putExtra("project_id", myRewardInfo.getProject_id());
                        intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                        intent.putExtra("store_id", myRewardInfo.getOutletId());
                        intent.putExtra("state", "2");
                        intent.putExtra("is_watermark", myRewardInfo.getIs_watermark());
                        intent.putExtra("code", myRewardInfo.getCode());
                        intent.putExtra("brand", myRewardInfo.getBrand());
                        intent.putExtra("isAgain", false);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    //----------------------任务状态(全部的时候)  -1为上传中，0为审核中，2为未通过，3为已通过
    private void onItemClickAll() {
        applylistview_two.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (laterAdapter1 != null) {
                    MyRewardInfo myRewardInfo = list_myReward.get(position - 1);
                    String states = myRewardInfo.getState();
                    Tools.d("tag", "laterAdapter1===========>>" + states);
                    if (states.equals("2")) {
//
//                        Intent intent = new Intent(getContext(), TaskitemDetailActivity_12.class);
//                        intent.putExtra("id", myRewardInfo.getOutletId());
//                        intent.putExtra("projectname", myRewardInfo.getProjectName());
//                        intent.putExtra("store_name", myRewardInfo.getOutletName());
//                        intent.putExtra("store_num", myRewardInfo.getCode());
//                        intent.putExtra("project_id", myRewardInfo.getProject_id());
//                        intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
//                        intent.putExtra("is_record", myRewardInfo.getIs_record());
//                        intent.putExtra("is_watermark", myRewardInfo.getIs_watermark() + "");//int
//                        intent.putExtra("code", myRewardInfo.getCode());
//                        intent.putExtra("project_type", myRewardInfo.getType());
//                        intent.putExtra("brand", myRewardInfo.getBrand());
//                        intent.putExtra("is_takephoto", myRewardInfo.getIs_takephoto());//String
//                        startActivity(intent);

                        Intent intent = new Intent(getContext(), TaskFinishActivity.class);
                        intent.putExtra("projectname", myRewardInfo.getProjectName());
                        intent.putExtra("store_name", myRewardInfo.getOutletName());
                        intent.putExtra("store_num", myRewardInfo.getCode());
                        intent.putExtra("project_id", myRewardInfo.getProject_id());
                        intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                        intent.putExtra("store_id", myRewardInfo.getOutletId());
                        intent.putExtra("state", "2");
                        intent.putExtra("is_watermark", myRewardInfo.getIs_watermark());
                        intent.putExtra("code", myRewardInfo.getCode());
                        intent.putExtra("brand", myRewardInfo.getBrand());
                        intent.putExtra("isAgain", false);
                        startActivity(intent);
                    } else if (states.equals("-1")) {


                    } else if (states.equals("0")) {

                        String type = myRewardInfo.getType();
                        String isclose = myRewardInfo.getIsclose();
                        storeid = myRewardInfo.getOutletId();
                        if ("2".equals(type)) {
                            Tools.showToast(getContext(), "此项目类型不支持查看详情");
                            return;
                        }
                        if ("4".equals(type)) {
                            if ("1".equals(isclose)) {
                                Tools.showToast(getContext(), "该网点已被置无效，不能查看详情");
                                return;
                            }
                            Intent intent = new Intent(getContext(), ExperienceCommentActivity.class);
                            intent.putExtra("taskid", "");
                            intent.putExtra("storeid", storeid);
                            intent.putExtra("projectid", myRewardInfo.getProject_id());
                            intent.putExtra("packageid", "");
                            intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                            intent.putExtra("project_name", myRewardInfo.getPersonName());
                            intent.putExtra("brand", myRewardInfo.getBrand());
                            intent.putExtra("storeName", myRewardInfo.getOutletName());
                            intent.putExtra("storecode", myRewardInfo.getCode());
                            intent.putExtra("source", "1");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getContext(), TaskFinishActivity.class);
                            intent.putExtra("projectname", myRewardInfo.getProjectName());
                            intent.putExtra("store_name", myRewardInfo.getOutletName());
                            intent.putExtra("store_num", myRewardInfo.getCode());
                            intent.putExtra("project_id", myRewardInfo.getProject_id());
                            intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                            intent.putExtra("store_id", myRewardInfo.getOutletId());
                            intent.putExtra("state", "2");
                            intent.putExtra("is_watermark", myRewardInfo.getIs_watermark());
                            intent.putExtra("code", myRewardInfo.getCode());
                            intent.putExtra("brand", myRewardInfo.getBrand());
                            intent.putExtra("isAgain", false);
                            startActivity(intent);
                        }
                    } else if (states.equals("3")) {
                        Intent intent = new Intent(getContext(), MyaccountActivity.class);
                        startActivity(intent);

                    }
                }
            }
        });
    }

    private void doSelectType(final TaskDetailLeftInfo taskDetailLeftInfo, final String tasktype) {
        if (locType == 61 || locType == 161) {
            checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            doExecute(taskDetailLeftInfo, tasktype);
                        } else if (code == 2) {
                            ConfirmDialog.showDialog(getContext(), null, jsonObject.getString("msg"), null,
                                    "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                        }
                                    }).goneLeft();
                        } else if (code == 3) {
                            ConfirmDialog.showDialog(getContext(), null, jsonObject.getString("msg"), "取消",
                                    "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            doExecute(taskDetailLeftInfo, tasktype);
                                        }
                                    });
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
                    Tools.showToast(getContext(), getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else if (locType == 167) {
            Tools.showToast2(getContext(), "请您检查是否开启权限，尝试重新请求定位");
        } else {
            Tools.showToast2(getContext(), "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
        }
    }

    private void doExecute(TaskDetailLeftInfo taskDetailLeftInfo, String tasktype) {
        isRefresh = true;
        if ("1".equals(tasktype) || "6".equals(tasktype)) {//正常任务/到店红包任务
            String fynum = taskDetailLeftInfo.getNumber();
            if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName(getContext()))) {
                if (taskDetailLeftInfo.getIs_exe().equals("1")) {
                    if (taskDetailLeftInfo.getIsOffline() == 1) {
                        Intent intent = new Intent(getContext(), OfflinePackageActivity.class);
                        intent.putExtra("id", taskDetailLeftInfo.getId());
                        intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                        intent.putExtra("store_name", taskDetailLeftInfo.getName());
                        intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                        intent.putExtra("province", taskDetailLeftInfo.getCity());
                        intent.putExtra("city", taskDetailLeftInfo.getCity2());
                        intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                        intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                        intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                        intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark());//int
                        intent.putExtra("code", taskDetailLeftInfo.getCode());
                        intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                        intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());//String
                        startActivity(intent);
                    } else {
                        if (taskDetailLeftInfo.getIs_desc().equals("1")) {//有网点说明
                            Intent intent = new Intent(getContext(), StoreDescActivity.class);
                            intent.putExtra("id", taskDetailLeftInfo.getId());
                            intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                            intent.putExtra("store_name", taskDetailLeftInfo.getName());
                            intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                            intent.putExtra("province", taskDetailLeftInfo.getCity());
                            intent.putExtra("city", taskDetailLeftInfo.getCity2());
                            intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                            intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                            intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                            intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark());//int
                            intent.putExtra("code", taskDetailLeftInfo.getCode());
                            intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                            intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());//String
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getContext(), TaskitemDetailActivity_12.class);
                            intent.putExtra("id", taskDetailLeftInfo.getId());
                            intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                            intent.putExtra("store_name", taskDetailLeftInfo.getName());
                            intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                            intent.putExtra("province", taskDetailLeftInfo.getCity());
                            intent.putExtra("city", taskDetailLeftInfo.getCity2());
                            intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                            intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                            intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                            intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark() + "");//int
                            intent.putExtra("code", taskDetailLeftInfo.getCode());
                            intent.putExtra("project_type", taskDetailLeftInfo.getProject_type());
                            intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                            intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());//String
                            startActivity(intent);
                        }
                    }
                } else {
                    Tools.showToast(getContext(), "未到执行时间");
                }
            } else {
                Tools.showToast(getContext(), "您不是访员！");
            }
        } else if ("2".equals(tasktype)) {//暗访任务
            Intent intent = new Intent(getContext(), BlackillustrateActivity.class);
            intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
            intent.putExtra("project_name", taskDetailLeftInfo.getProjectname());
            intent.putExtra("store_id", taskDetailLeftInfo.getId());
            intent.putExtra("store_name", taskDetailLeftInfo.getName());
            intent.putExtra("store_num", taskDetailLeftInfo.getCode());
            intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
            intent.putExtra("isUpdata", taskDetailLeftInfo.getIsUpdata());
            intent.putExtra("province", taskDetailLeftInfo.getCity());
            intent.putExtra("city", taskDetailLeftInfo.getCity2());
            intent.putExtra("address", taskDetailLeftInfo.getCity3());
            intent.putExtra("isNormal", true);
            startActivity(intent);
        } else if ("4".equals(tasktype)) {
            String experience_state = taskDetailLeftInfo.getExperience_state();
            if ("0".equals(experience_state)) {//从头开始
                Intent intent = new Intent(getContext(), ExperienceillActivity.class);
                intent.putExtra("id", taskDetailLeftInfo.getProjectid());
                intent.putExtra("projectName", taskDetailLeftInfo.getProjectname());
                intent.putExtra("storeNum", taskDetailLeftInfo.getCode());
                intent.putExtra("storeName", taskDetailLeftInfo.getName());
                intent.putExtra("store_id", taskDetailLeftInfo.getId());
                intent.putExtra("city", taskDetailLeftInfo.getCity());
                intent.putExtra("money_unit", taskDetailLeftInfo.getMoney_unit());
                intent.putExtra("end_date", taskDetailLeftInfo.getEnd_date());
                intent.putExtra("check_time", taskDetailLeftInfo.getCheck_time());
                intent.putExtra("begin_date", taskDetailLeftInfo.getBegin_date());
                intent.putExtra("longitude", taskDetailLeftInfo.getLongtitude());
                intent.putExtra("latitude", taskDetailLeftInfo.getLatitude());
                intent.putExtra("project_person", taskDetailLeftInfo.getProject_person());
                intent.putExtra("standard_state", taskDetailLeftInfo.getStandard_state());
                intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark());
                intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                startActivity(intent);
            } else if ("1".equals(experience_state)) {//已离店 问卷任务
                Intent intent = new Intent(getContext(), ExperienceEditActivity.class);
                intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                intent.putExtra("project_name", taskDetailLeftInfo.getProjectname());
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskDetailLeftInfo.getRecord_taskid());
                intent.putExtra("task_name", "");
                intent.putExtra("tasktype", "3");
                intent.putExtra("store_id", taskDetailLeftInfo.getId());
                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("outlet_batch", taskDetailLeftInfo.getOutlet_batch());
                startActivity(intent);
            } else if ("2".equals(experience_state)) {//执行完成未评论
                Intent intent = new Intent(getContext(), ExperienceCommentActivity.class);
                intent.putExtra("taskid", taskDetailLeftInfo.getRecord_taskid());
                intent.putExtra("storeid", storeid);
                intent.putExtra("projectid", taskDetailLeftInfo.getProjectid());
                intent.putExtra("packageid", "");
                intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                intent.putExtra("project_name", taskDetailLeftInfo.getProjectname());
                intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                intent.putExtra("storeName", taskDetailLeftInfo.getName());
                intent.putExtra("storecode", taskDetailLeftInfo.getCode());
                intent.putExtra("source", "0");
                startActivity(intent);
            } else {
                Tools.showToast(getContext(), "资料未回传完毕，稍后您可在“已上传”里查看。");
            }
        } else if ("5".equals(tasktype)) {
            Intent intent = new Intent(getContext(), TaskitemDetailActivity_12.class);
            intent.putExtra("id", taskDetailLeftInfo.getId());
            intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
            intent.putExtra("store_name", "网点名称");
            intent.putExtra("store_num", "网点编号");
            intent.putExtra("province", taskDetailLeftInfo.getCity());
            intent.putExtra("city", taskDetailLeftInfo.getCity2());
            intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
            intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
            intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
            intent.putExtra("project_type", taskDetailLeftInfo.getProject_type());
            intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark() + "");//int
            intent.putExtra("code", taskDetailLeftInfo.getCode());
            intent.putExtra("brand", taskDetailLeftInfo.getBrand());
            intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());//String
            startActivity(intent);
        }
    }

    private void getLaterData() {//未通过，可提现 数据获取
        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            lin_Nodata.setVisibility(View.VISIBLE);
            lin_Nodata_img.setImageResource(R.mipmap.grrw_image2);
            lin_Nodata_prompt.setText("您还没有登录呢，点击登录");
            lin_Nodata_prompt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    isRefresh = true;
                    lin_Nodata.setVisibility(View.GONE);
                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
            ConfirmDialog.showDialog(getContext(), null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            isRefresh = true;
                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    });
            return;
        }
        lin_Nodata.setVisibility(View.GONE);
        myReward.sendPostRequest(Urls.MyReward2, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            if (list_myReward != null) {
                                list_myReward.clear();
                            } else {
                                list_myReward = new ArrayList<MyRewardInfo>();
                            }
                        }
                        int unpass_num = jsonObject.getInt("unpass_num");
                        int upload_num = jsonObject.getInt("upload_num");
                        int check_num = jsonObject.getInt("check_num");
                        int pass_num = jsonObject.getInt("pass_num");
                        int gift_num = jsonObject.getInt("gift_num"); //待领取礼品数

                        if (gift_num != 0) {
                            lin_ly.setVisibility(View.VISIBLE);
                            tv_tag.setText("有" + gift_num + "个礼品待领取");
                            tv_tag.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //礼品奖励列表
                                    Intent intent = new Intent(getContext(), PrizeListActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            lin_ly.setVisibility(View.GONE);
                        }
                        tv_No.setText(unpass_num + "");
                        tv_now.setText(upload_num + "");
                        tv_shen.setText(check_num + "");
                        tv_Yes.setText(pass_num + "");

                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        Tools.d("tag", "jsonArray----------->>>" + jsonArray.length());
                        if (jsonArray != null) {
                            lin_Nodata.setVisibility(View.GONE);
                            int length = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                MyRewardInfo myRewardInfo = new MyRewardInfo();
                                myRewardInfo.setId(object.getString("id"));
                                myRewardInfo.setOutletId(object.getString("outletId"));
                                myRewardInfo.setReward_type(object.getString("reward_type"));  // 奖励类型，1为现金，2为礼品，3为现金+礼品
                                myRewardInfo.setGift_url(object.getString("gift_url"));   //  礼品奖励图片地址
                                myRewardInfo.setProjectName(object.getString("projectName"));
                                myRewardInfo.setPersonId(object.getString("personId"));
                                myRewardInfo.setOutletDesc(object.getString("outletDesc"));
                                myRewardInfo.setState(object.getString("state"));
                                myRewardInfo.setOutletName(object.getString("outletName"));
                                myRewardInfo.setMoney(object.getString("money"));
                                myRewardInfo.setMoney2(object.getString("money"));
                                myRewardInfo.setExeTime(object.getString("exeTime"));
                                myRewardInfo.setProject_code(object.getString("project_code"));
                                myRewardInfo.setProject_property(object.getString("project_property"));
                                myRewardInfo.setIs_record(object.getString("is_record"));
                                myRewardInfo.setIs_watermark(object.getString("is_watermark"));
                                myRewardInfo.setBrand(object.getString("brand"));
                                myRewardInfo.setCode(object.getString("code"));
                                myRewardInfo.setIs_takephoto(object.getString("is_takephoto"));
                                myRewardInfo.setIs_exe(object.getString("is_exe"));
                                myRewardInfo.setIs_desc(object.getString("is_desc"));
                                myRewardInfo.setIs_upload(object.getString("is_upload"));
                                myRewardInfo.setType(object.getString("type"));
                                myRewardInfo.setEnd_date(object.getString("end_date"));
                                myRewardInfo.setBegin_date(object.getString("begin_date"));
                                myRewardInfo.setCheck_time(object.getString("check_time"));
                                myRewardInfo.setProject_id(object.getString("project_id"));
                                myRewardInfo.setPhoto_compression(object.getString("photo_compression"));
                                myRewardInfo.setMoney_unit(object.getString("money_unit"));
                                myRewardInfo.setOutletNum(object.getString("outletNum"));
                                myRewardInfo.setOutlet_address(object.getString("outlet_address"));
                                myRewardInfo.setPosition_limit(object.getString("position_limit"));
                                myRewardInfo.setLimit_province(object.getString("limit_province"));
                                myRewardInfo.setLimit_city(object.getString("limit_city"));
                                list_myReward.add(myRewardInfo);
                            }
                            applylistview_two.onRefreshComplete();
                            applylistview_three.onRefreshComplete();
                            applylistview_four.onRefreshComplete();
                            applylistview_five.onRefreshComplete();
                            applylistview_six.onRefreshComplete();
                            if (length < 15) {
                                if ("".equals(state)) {
                                    applylistview_two.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                                } else if ("2".equals(state)) {
                                    applylistview_three.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                                } else if ("-1".equals(state)) {
                                    applylistview_four.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                                } else if ("0".equals(state)) {
                                    applylistview_five.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                                } else if ("3".equals(state)) {
                                    applylistview_six.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                }

                            } else {
                                if ("".equals(state)) {
                                    applylistview_two.setMode(PullToRefreshBase.Mode.BOTH);

                                } else if ("2".equals(state)) {
                                    applylistview_three.setMode(PullToRefreshBase.Mode.BOTH);

                                } else if ("-1".equals(state)) {
                                    applylistview_four.setMode(PullToRefreshBase.Mode.BOTH);

                                } else if ("0".equals(state)) {
                                    applylistview_five.setMode(PullToRefreshBase.Mode.BOTH);

                                } else if ("3".equals(state)) {
                                    applylistview_six.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                            }
                        } else {
                            if ("-1".equals(state)) {
                                lin_Nodata.setVisibility(View.VISIBLE);
                                lin_Nodata_prompt.setText("太棒了,您的所有任务都上传完毕了~");
                            }
                            if ("2".equals(state)) {
                                lin_Nodata.setVisibility(View.VISIBLE);
                                lin_Nodata_prompt.setText("恭喜您！目前没有未通过任务哦！");
                            }
                        }

                        if (list_myReward.size() == 0) {
                            if ("-1".equals(state)) {
                                lin_Nodata.setVisibility(View.VISIBLE);
                                lin_Nodata_prompt.setText("太棒了,您的所有任务都上传完毕了~");
                            }

                            if ("2".equals(state)) {
                                lin_Nodata.setVisibility(View.VISIBLE);
                                lin_Nodata_prompt.setText("恭喜您！目前没有未通过任务哦！");
                            }
                        }
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                    if ("".equals(state) && laterAdapter1 != null) {
                        laterAdapter1.notifyDataSetChanged();
                    } else if ("2".equals(state) && laterAdapter2 != null) {
                        laterAdapter2.notifyDataSetChanged();
                    } else if ("-1".equals(state) && laterAdapter3 != null) {
                        laterAdapter3.notifyDataSetChanged();
                    } else if ("0".equals(state) && laterAdapter4 != null) {
                        laterAdapter4.notifyDataSetChanged();
                    } else if ("3".equals(state) && laterAdapter5 != null) {
                        laterAdapter5.notifyDataSetChanged();
                    }
                } catch (
                        JSONException e)

                {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                    applylistview_two.onRefreshComplete();
                    applylistview_three.onRefreshComplete();
                    applylistview_four.onRefreshComplete();
                    applylistview_five.onRefreshComplete();
                    applylistview_six.onRefreshComplete();
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                applylistview_two.onRefreshComplete();
                applylistview_three.onRefreshComplete();
                applylistview_four.onRefreshComplete();
                applylistview_five.onRefreshComplete();
                applylistview_six.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }

    public void getLeftData() {//待执行 已上传数据获取
        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            lin_Nodata.setVisibility(View.VISIBLE);
            lin_Nodata_img.setImageResource(R.mipmap.grrw_image2);
            lin_Nodata_prompt.setText("您还没有登录呢，点击登录");
            lin_Nodata_prompt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    lin_Nodata.setVisibility(View.GONE);
                    isRefresh = true;
                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
            ConfirmDialog.showDialog(getContext(), null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            isRefresh = true;
                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    });
            return;
        }
        applyStartList.sendPostRequest(Urls.ApplyStartList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                isLoadSuccess = true;
                lin_Nodata.setVisibility(View.VISIBLE);
                applyStartList.stopTimer();
                Tools.d(s);
                lin_ly.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (list == null || groupkey == null) {
                        list = new ArrayList<Object>();
                        groupkey = new ArrayList<TaskNewInfo>();
                    } else {
                        if (page == 1) {
                            list.clear();
                            groupkey.clear();
                        }
                    }
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("project_datas");
                        if (jsonArray != null) {
                            lin_Nodata.setVisibility(View.GONE);
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
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
//                                groupkey.add(taskNewInfo);
//                                list.add(taskNewInfo);
                                //以上为项目参数 以下为网点参数
                                JSONArray jsonArray1 = object.getJSONArray("outlet_datas");
                                if (jsonArray1 != null) {
                                    for (int e = 0; e < jsonArray1.length(); e++) {
                                        TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(e);
                                        String isdetail = jsonObject1.getString("isdetail");
                                        String timeDetail = "";
                                        if ("0".equals(isdetail)) {
                                            String[] datelist = jsonObject1.getString("datelist").replaceAll("\\[\"", "").replaceAll("\"]",
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
                                                String date = jsonObject1.getString("date" + index);
                                                if (!TextUtils.isEmpty(date) && !"null".equals(date)) {
                                                    String detailtemp = jsonObject1.getString("details" + index);
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
                                        taskDetailLeftInfo.setPosition_limit(object.getString("position_limit"));
                                        taskDetailLeftInfo.setLimit_province(object.getString("limit_province"));
                                        taskDetailLeftInfo.setLimit_city(object.getString("limit_city"));
                                        taskDetailLeftInfo.setProject_name(object.getString("project_name"));
                                        taskDetailLeftInfo.setProject_type(object.getString("project_type"));
                                        taskDetailLeftInfo.setIs_exe(jsonObject1.getString("is_exe"));
                                        taskDetailLeftInfo.setIs_desc(jsonObject1.getString("is_desc"));
                                        taskDetailLeftInfo.setId(jsonObject1.getString("storeid"));
                                        taskDetailLeftInfo.setIsclose(jsonObject1.getString("isclose"));
                                        taskDetailLeftInfo.setName(jsonObject1.getString("storeName"));
                                        taskDetailLeftInfo.setCode(jsonObject1.getString("storeNum"));
                                        taskDetailLeftInfo.setIdentity(jsonObject1.getString("proxy_num"));
                                        taskDetailLeftInfo.setCity(jsonObject1.getString("province"));
                                        taskDetailLeftInfo.setCity2(jsonObject1.getString("city"));
                                        taskDetailLeftInfo.setCity3(jsonObject1.getString("address"));
                                        taskDetailLeftInfo.setNumber(jsonObject1.getString("accessed_num"));
                                        taskDetailLeftInfo.setNickname(jsonObject1.getString("accessed_name"));
                                        taskDetailLeftInfo.setMoney(jsonObject1.getString("money"));
                                        taskDetailLeftInfo.setExe_time(jsonObject1.getString("exe_time"));
                                        taskDetailLeftInfo.setHavetime(jsonObject1.getString("havetime"));
                                        taskDetailLeftInfo.setIsUpdata(jsonObject1.getString("is_upload"));
                                        taskDetailLeftInfo.setTimedetail(timeDetail);
                                        taskDetailLeftInfo.setType(object.getString("type"));
                                        taskDetailLeftInfo.setMoney_unit(object.getString("money_unit"));
                                        taskDetailLeftInfo.setProjectname(object.getString("project_name"));
                                        taskDetailLeftInfo.setProjectid(object.getString("id"));
                                        taskDetailLeftInfo.setBrand(object.getString("brand"));
                                        taskDetailLeftInfo.setPhoto_compression(object.getString("photo_compression"));
                                        taskDetailLeftInfo.setIs_watermark(Tools.StringToInt(object.getString("is_watermark")));
                                        taskDetailLeftInfo.setIs_taskphoto(object.getString("is_takephoto"));
                                        taskDetailLeftInfo.setExe_type(Tools.StringToInt(jsonObject1.optString("exe_type")));
                                        taskDetailLeftInfo.setExperience_state(jsonObject1.getString("experience_state"));
                                        taskDetailLeftInfo.setCheck_time(object.getString("check_time") + "");
                                        taskDetailLeftInfo.setBegin_date(object.getString("begin_date"));
                                        taskDetailLeftInfo.setEnd_date(object.getString("end_date"));
                                        taskDetailLeftInfo.setStandard_state(object.getString("standard_state"));
                                        taskDetailLeftInfo.setProject_person(object.optString("project_person"));
                                        taskDetailLeftInfo.setOutlet_batch(jsonObject1.getString("outlet_batch"));
                                        taskDetailLeftInfo.setRecord_taskid(jsonObject1.getString("record_taskid"));
                                        taskDetailLeftInfo.setAbandon(jsonObject1.getString("abandon"));
                                        taskDetailLeftInfo.setReward_type(object.getString("reward_type"));
                                        list.add(taskDetailLeftInfo);
                                    }
                                }
                            }
                            applylistview_one.onRefreshComplete();
                            if (length < 15) {
                                applylistview_one.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                            } else {
                                applylistview_one.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        } else {
                            lin_Nodata.setVisibility(View.VISIBLE);
                            lin_Nodata_img.setImageResource(R.mipmap.grrw_image);
                            lin_Nodata_prompt.setText("很遗憾，您还没有待执行任务，\n快去广场领取任务吧！");
                        }
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                    if (applyAdapter1 != null) {
                        applyAdapter1.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                applylistview_one.onRefreshComplete();
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                applyStartList.stopTimer();
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
                applylistview_one.onRefreshComplete();
                lin_Nodata.setVisibility(View.VISIBLE);
                lin_Nodata_img.setImageResource(R.mipmap.grrw_image2);
                lin_Nodata_prompt.setText("网络连接中断，\n请检查下您的网络吧！");
            }
        }, null);
    }

    private void refreshListView() {
        applylistview_one.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                EventBus.getDefault().post("4");
                refreshDataOne();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getLeftData();
            }
        });

        applylistview_two.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "";
                refreshDataTwo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "";
                page++;
                getLaterData();
            }
        });

        ///------------状态值（不传为全部，-1为上传中，0为审核中，2为未通过，3为已通过）

        applylistview_three.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "2";
                refreshDataTwo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "2";
                page++;
                getLaterData();
            }
        });
        applylistview_four.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "-1";
                refreshDataTwo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "-1";
                page++;
                getLaterData();
            }
        });
        applylistview_five.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "0";
                refreshDataTwo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "0";
                page++;
                getLaterData();
            }
        });

        applylistview_six.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "3";
                refreshDataTwo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = "3";
                page++;
                getLaterData();
            }
        });
    }

    private void refreshDataTwo() {
        page = 1;
        getLaterData();
    }

    private void refreshDataOne() {
        page = 1;
        getLeftData();
    }

    private void initView(View view) {
        lin_ly = (LinearLayout) view.findViewById(R.id.lin_ly);
        lin_Nodata = (LinearLayout) view.findViewById(R.id.lin_Nodata);
        lin_Nodata_prompt = (TextView) view.findViewById(R.id.lin_Nodata_prompt);
        lin_Nodata_img = (ImageView) view.findViewById(R.id.lin_Nodata_img);
        apply_one = (TextView) view.findViewById(R.id.apply_one);
        apply_two = (TextView) view.findViewById(R.id.apply_two);

        apply_oneimg = view.findViewById(R.id.apply_oneimg);
        apply_twoimg = view.findViewById(R.id.apply_twoimg);

        applylistview_one = (PullToRefreshListView) view.findViewById(R.id.applylistview_one);
        applylistview_two = (PullToRefreshListView) view.findViewById(R.id.applylistview_two);
        applylistview_three = (PullToRefreshListView) view.findViewById(R.id.applylistview_three);
        applylistview_four = (PullToRefreshListView) view.findViewById(R.id.applylistview_four);
        applylistview_five = (PullToRefreshListView) view.findViewById(R.id.applylistview_five);
        applylistview_six = (PullToRefreshListView) view.findViewById(R.id.applylistview_six);

        apply_one.setOnClickListener(this);
        apply_two.setOnClickListener(this);

        lin_task_state = (LinearLayout) view.findViewById(R.id.lin_task_state);

        apply_one2 = (TextView) view.findViewById(R.id.apply_one2);
        apply_two2 = (TextView) view.findViewById(R.id.apply_two2);
        apply_three2 = (TextView) view.findViewById(R.id.apply_three2);
        apply_four2 = (TextView) view.findViewById(R.id.apply_four2);

        lin_task1 = (LinearLayout) view.findViewById(R.id.lin_task1);
        lin_task2 = (LinearLayout) view.findViewById(R.id.lin_task2);
        lin_task3 = (LinearLayout) view.findViewById(R.id.lin_task3);
        lin_task4 = (LinearLayout) view.findViewById(R.id.lin_task4);

        lin_task1.setOnClickListener(this);
        lin_task2.setOnClickListener(this);
        lin_task3.setOnClickListener(this);
        lin_task4.setOnClickListener(this);


        tv_No = (TextView) view.findViewById(R.id.tv_No);
        tv_now = (TextView) view.findViewById(R.id.tv_now);
        tv_shen = (TextView) view.findViewById(R.id.tv_shen);
        tv_Yes = (TextView) view.findViewById(R.id.tv_Yes);
        tv_tag = (TextView) view.findViewById(R.id.tv_tag);

    }
    //状态值（不传为全部，-1为上传中，0为审核中，2为未通过，3为已通过）

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //  2为未通过
            case R.id.lin_task1: {
                state = "2";
                apply_one2.setTextColor(getResources().getColor(R.color.homepage_select));
                apply_two2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_three2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_four2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_Yes.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_No.setTextColor(getResources().getColor(R.color.homepage_select));
                tv_now.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_shen.setTextColor(getResources().getColor(R.color.homepage_notselect));

                applylistview_one.setVisibility(View.GONE);
                applylistview_two.setVisibility(View.GONE);
                applylistview_three.setVisibility(View.VISIBLE);
                applylistview_four.setVisibility(View.GONE);
                applylistview_five.setVisibility(View.GONE);
                applylistview_six.setVisibility(View.GONE);
                if (list_myReward != null) {
                    list_myReward.clear();
                }
                refreshDataTwo();
            }

            break;
            //  -1为上传中
            case R.id.lin_task2: {
                state = "-1";
                apply_one2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_two2.setTextColor(getResources().getColor(R.color.homepage_select));
                apply_three2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_four2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_Yes.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_No.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_now.setTextColor(getResources().getColor(R.color.homepage_select));
                tv_shen.setTextColor(getResources().getColor(R.color.homepage_notselect));

                applylistview_one.setVisibility(View.GONE);
                applylistview_two.setVisibility(View.GONE);
                applylistview_three.setVisibility(View.GONE);
                applylistview_four.setVisibility(View.VISIBLE);
                applylistview_five.setVisibility(View.GONE);
                applylistview_six.setVisibility(View.GONE);
                if (list_myReward != null) {
                    list_myReward.clear();
                }
                refreshDataTwo();
            }

            break;
            //  0为审核中
            case R.id.lin_task3: {

                state = "0";
                apply_one2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_two2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_three2.setTextColor(getResources().getColor(R.color.homepage_select));
                apply_four2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_Yes.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_No.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_now.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_shen.setTextColor(getResources().getColor(R.color.homepage_select));

                applylistview_one.setVisibility(View.GONE);
                applylistview_two.setVisibility(View.GONE);
                applylistview_three.setVisibility(View.GONE);
                applylistview_four.setVisibility(View.GONE);
                applylistview_five.setVisibility(View.VISIBLE);
                applylistview_six.setVisibility(View.GONE);
                if (list_myReward != null) {
                    list_myReward.clear();
                }
                refreshDataTwo();
            }

            break;
            //  3为已通过
            case R.id.lin_task4: {
                state = "3";
                apply_one2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_two2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_three2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_four2.setTextColor(getResources().getColor(R.color.homepage_select));
                tv_Yes.setTextColor(getResources().getColor(R.color.homepage_select));
                tv_No.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_now.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_shen.setTextColor(getResources().getColor(R.color.homepage_notselect));

                applylistview_one.setVisibility(View.GONE);
                applylistview_two.setVisibility(View.GONE);
                applylistview_three.setVisibility(View.GONE);
                applylistview_four.setVisibility(View.GONE);
                applylistview_five.setVisibility(View.GONE);
                applylistview_six.setVisibility(View.VISIBLE);
                if (list_myReward != null) {
                    list_myReward.clear();
                }
                refreshDataTwo();
            }

            break;

            case R.id.apply_one: {
                EventBus.getDefault().post("4");
                lin_task_state.setVisibility(View.GONE);
                apply_one.setTextColor(getResources().getColor(R.color.homepage_select));
                apply_two.setTextColor(getResources().getColor(R.color.homepage_notselect));

                apply_oneimg.setVisibility(View.VISIBLE);
                apply_twoimg.setVisibility(View.INVISIBLE);

                applylistview_one.setVisibility(View.VISIBLE);
                applylistview_two.setVisibility(View.GONE);
                applylistview_three.setVisibility(View.GONE);
                applylistview_four.setVisibility(View.GONE);
                applylistview_five.setVisibility(View.GONE);
                applylistview_six.setVisibility(View.GONE);
                if (list != null || groupkey != null) {
                    list.clear();
                    groupkey.clear();
                }
                refreshDataOne();
            }
            break;
            case R.id.apply_two: {
                state = "";
                EventBus.getDefault().post("3");
                lin_task_state.setVisibility(View.VISIBLE);
                apply_one.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_two.setTextColor(getResources().getColor(R.color.homepage_select));

                apply_one2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_four2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_two2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                apply_three2.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_Yes.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_No.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_now.setTextColor(getResources().getColor(R.color.homepage_notselect));
                tv_shen.setTextColor(getResources().getColor(R.color.homepage_notselect));

                apply_oneimg.setVisibility(View.INVISIBLE);
                apply_twoimg.setVisibility(View.VISIBLE);
                //  apply_threeimg.setVisibility(View.INVISIBLE);
                //  apply_fourimg.setVisibility(View.INVISIBLE);
                applylistview_one.setVisibility(View.GONE);
                applylistview_two.setVisibility(View.VISIBLE);
                applylistview_three.setVisibility(View.GONE);
                applylistview_four.setVisibility(View.GONE);
                applylistview_five.setVisibility(View.GONE);
                applylistview_six.setVisibility(View.GONE);
                if (list_myReward != null) {
                    list_myReward.clear();
                }
                refreshDataTwo();
            }
            break;

        }
    }

    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(myListener);
        setLocationOption();
        mLocationClient.start();
    }

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onclick(int position) {
        final TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) list.get(position);
        storeid = taskDetailLeftInfo.getId();
        if ("1".equals(taskDetailLeftInfo.getAbandon())) {//可直接放弃
            ConfirmDialog.showDialog(getContext(), "您是否要放弃任务？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {
                    applyAdapter1.setIsSwif("2");
                    EventBus.getDefault().post("4");
                    applyAdapter1.notifyDataSetChanged();
                }

                @Override
                public void rightClick(Object object) {
                    applyAdapter1.setIsSwif("2");
                    EventBus.getDefault().post("4");
                    applyAdapter1.notifyDataSetChanged();
                    abandon();
                }
            });
        } else {
            ConfirmDialog.showDialog(getContext(), "提示！", 2, "您的任务已在执行中，如果放弃此任务将得不到奖励金", "不放弃", "继续放弃", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {
                    applyAdapter1.setIsSwif("2");
                    EventBus.getDefault().post("4");
                    applyAdapter1.notifyDataSetChanged();
                }

                @Override
                public void rightClick(Object object) {
                    applyAdapter1.setIsSwif("2");
                    EventBus.getDefault().post("4");
                    applyAdapter1.notifyDataSetChanged();
                    updataDBHelper.removeTask(AppInfo.getName(getContext()), taskDetailLeftInfo.getProjectid(), taskDetailLeftInfo.getId());
                    abandon();
                }
            });
        }
    }

    //分享到广场(到店红包)
    @Override
    public void onShareSquare(int position) {
        storeid = ((TaskDetailLeftInfo) list.get(position)).getId();
        shareToSquare();
    }

    private void shareToSquare() {
        shareToSquare.sendPostRequest(Urls.ShareToSquare, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list != null || groupkey != null) {
                            list.clear();
                            groupkey.clear();
                        }
                        refreshDataOne();
                        Tools.showToast(getContext(), "操作成功");
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
                Tools.showToast(getContext(), getResources().getString(R.string.network_batch_error));
            }
        });
    }

    /***
     * item onclick
     *
     * @param position
     */
    @Override
    public void onitemclick(int position) {
        final TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) list.get(position);
        ArrayList<UpdataInfo> list = updataDBHelper.getTask();
        if (!list.isEmpty()) {
            ConfirmDialog.showDialog(getContext(), "提示！", "亲，您当前有资料尚未上传完成，如果继续执行任务，可能会造成资料上传延迟。", "等待上传", "继续执行", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {

                }

                @Override
                public void rightClick(Object object) {
                    storeid = taskDetailLeftInfo.getId();
                    if (!"4".equals(taskDetailLeftInfo.getType())) {
                        int exe_type = taskDetailLeftInfo.getExe_type();
                        if (exe_type == 1) {
                            Tools.showToast(getContext(), "资料未回传完毕，稍后您可在“已上传”里查看。");
                            return;
                        }
                    }
                    doSelectType(taskDetailLeftInfo, taskDetailLeftInfo.getType());
                }
            });
        } else {
            storeid = taskDetailLeftInfo.getId();
            if (!"4".equals(taskDetailLeftInfo.getType())) {
                int exe_type = taskDetailLeftInfo.getExe_type();
                if (exe_type == 1) {
                    Tools.showToast(getContext(), "资料未回传完毕，稍后您可在“已上传”里查看。");
                    return;
                }
            }
            doSelectType(taskDetailLeftInfo, taskDetailLeftInfo.getType());
        }
    }

    /***
     * Right item onclick
     *
     * @param position
     */
    @Override
    public void onRightitemclick(int position) {

        TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) list.get(position);
        String type = taskDetailLeftInfo.getType();
        String isclose = taskDetailLeftInfo.getIsclose();
        storeid = taskDetailLeftInfo.getId();
        if ("2".equals(type)) {
            Tools.showToast(getContext(), "此项目类型不支持查看详情");
            return;
        }
        if ("4".equals(type)) {
            if ("1".equals(isclose)) {
                Tools.showToast(getContext(), "该网点已被置无效，不能查看详情");
                return;
            }
            Intent intent = new Intent(getContext(), ExperienceCommentActivity.class);
            intent.putExtra("taskid", taskDetailLeftInfo.getRecord_taskid());
            intent.putExtra("storeid", storeid);
            intent.putExtra("projectid", taskDetailLeftInfo.getProjectid());
            intent.putExtra("packageid", "");
            intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
            intent.putExtra("project_name", taskDetailLeftInfo.getProjectname());
            intent.putExtra("brand", taskDetailLeftInfo.getBrand());
            intent.putExtra("storeName", taskDetailLeftInfo.getName());
            intent.putExtra("storecode", taskDetailLeftInfo.getCode());
            intent.putExtra("source", "1");
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), TaskFinishActivity.class);
            intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
            intent.putExtra("store_name", taskDetailLeftInfo.getName());
            intent.putExtra("store_num", taskDetailLeftInfo.getCode());
            intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
            intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
            intent.putExtra("store_id", taskDetailLeftInfo.getId());
            intent.putExtra("state", "2");
            intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark());
            intent.putExtra("code", taskDetailLeftInfo.getCode());
            intent.putExtra("brand", taskDetailLeftInfo.getBrand());
            intent.putExtra("isAgain", false);
            startActivity(intent);
        }

    }

    private void abandon() {
        abandon.sendPostRequest(Urls.Abandon, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        refreshDataOne();
                        Tools.showToast(getContext(), "放弃成功");
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
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void abandonUnpass() {
        abandonUnpass.sendPostRequest(Urls.abandonUnpass4, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        state = "2";
                        refreshDataTwo();
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
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
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    //放弃按钮
    @Override
    public void onAnondonclick(int position) {
        MyRewardInfo myRewardInfo = (MyRewardInfo) list_myReward.get(position);
        storeid = myRewardInfo.getOutletId();

        ConfirmDialog.showDialog(getContext(), "您是否要放弃任务？", true, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {

            }

            @Override
            public void rightClick(Object object) {
                abandonUnpass();
            }
        });
    }

    //提现按钮
    public void onWithdraw(int position) {
        Intent intent = new Intent(getContext(), MyaccountActivity.class);
        startActivity(intent);
    }

    @Override
    public void outTime() {
        if (!isLoadSuccess) {
            lin_Nodata.setVisibility(View.VISIBLE);
            lin_Nodata_img.setImageResource(R.mipmap.grrw_image2);
            lin_Nodata_prompt.setText("加载中……");
        }
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(getContext(), getResources().getString(R.string.location_fail));
                return;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            locType = location.getLocType();
            address = location.getAddrStr();
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }
}
