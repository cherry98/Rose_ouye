package com.orange.oy.activity.mycorps_314;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.CheckNewMemberAdapter;
import com.orange.oy.adapter.mycorps_314.TalklistAdapter;
import com.orange.oy.allinterface.DiscussCallback;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DiscussPopDialog;
import com.orange.oy.info.mycorps.CheckNewMemberInfo;
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

/***
 *
 *
 *    审核新队员
 */

public class CheckNewMemberActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle
        , View.OnClickListener, CheckNewMemberAdapter.CheckNewMemberAdapterCallback, TalklistAdapter.ReplyButton {
    private AppTitle taskILL_title;
    private CheckNewMemberAdapter checkNewMemberAdapter;
    private ArrayList<Object> list;
    private PullToRefreshListView applylistview_one;
    private DiscussPopDialog discussPop;
    private LinearLayout main;
    private NetworkConnection applyUserlist, checkApplyUser, reply;
    private String team_id;
    private int page = 1;
    private ArrayList<CheckNewMemberInfo> checkNewMemberList;
    private String state;
    private String applicant2, apply_id;
    private String text;
    private SystemDBHelper systemDBHelper;
    private NetworkView lin_Nodata;

    private void initNetworkConnection() {
        applyUserlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CheckNewMemberActivity.this));
                params.put("team_id", team_id);//战队id
                return params;
            }
        };
        checkApplyUser = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CheckNewMemberActivity.this));
                params.put("team_id", team_id); //战队id
                params.put("applicant", applicant2);  //申请人
                params.put("state", state);   //	审核状态(1为审核通过，0为不通过)

                return params;
            }
        };

        reply = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CheckNewMemberActivity.this));
                params.put("team_id", team_id); //战队id
                params.put("apply_id", apply_id);  //申请的id
                params.put("text", text);   //	发送的内容


                return params;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_new_member);
        applylistview_one = (PullToRefreshListView) findViewById(R.id.applylistview_one);
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        main = (LinearLayout) findViewById(R.id.main);
        initTitle();
        initNetworkConnection();

        checkNewMemberList = new ArrayList<>();
        checkNewMemberAdapter = new CheckNewMemberAdapter(this, checkNewMemberList);
        applylistview_one.setAdapter(checkNewMemberAdapter);
        checkNewMemberAdapter.setCallback(this);
        refreshListView();
        getData();

        systemDBHelper = new SystemDBHelper(this);
        systemDBHelper.deleteMessage(team_id);
    }

    private void initTitle() {
        team_id = getIntent().getStringExtra("team_id");
        taskILL_title = (AppTitle) findViewById(R.id.titleview);
        taskILL_title.settingName("新申请队员");
        taskILL_title.showBack(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (applyUserlist != null) {
            applyUserlist.stop(Urls.APPLYUSERLIST);
        }
        if (checkApplyUser != null) {
            checkApplyUser.stop(Urls.CHECKAPPLYUSER);
        }
        if (reply != null) {
            reply.stop(Urls.REPLY);
        }
    }

    private void refreshListView() {
        applylistview_one.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
    }

    private void getRest() {
        checkApplyUser.sendPostRequest(Urls.CHECKAPPLYUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        getData();
                        Tools.showToast(CheckNewMemberActivity.this, jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(CheckNewMemberActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CheckNewMemberActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CheckNewMemberActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void getReply() {
        reply.sendPostRequest(Urls.REPLY, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        getData();
                    } else {
                        Tools.showToast(CheckNewMemberActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CheckNewMemberActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CheckNewMemberActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void getData() {
        applyUserlist.sendPostRequest(Urls.APPLYUSERLIST, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            if (!checkNewMemberList.isEmpty()) {
                                checkNewMemberList.clear();

                            }
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        if (jsonArray != null) {
                            applylistview_one.setVisibility(View.VISIBLE);
                            lin_Nodata.setVisibility(View.GONE);
                            int length = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                CheckNewMemberInfo checkNewMemberInfo = new CheckNewMemberInfo();
                                checkNewMemberInfo.setApply_id(object.optString("apply_id"));
                                checkNewMemberInfo.setCity(object.optString("city"));
                                checkNewMemberInfo.setApplicant(object.optString("applicant"));
                                checkNewMemberInfo.setCreate_time(object.optString("create_time"));
                                checkNewMemberInfo.setUser_img(object.optString("user_img"));
                                checkNewMemberInfo.setUser_name(object.optString("user_name"));
                                checkNewMemberInfo.setInviter(object.optString("inviter"));
                                checkNewMemberInfo.setReason(object.optString("reason"));
                                String mobile = object.optString("mobile");
                                if ("null".equals(mobile) || mobile.length() < 11) {
                                    if ("null".equals(mobile)) {
                                        mobile = "";
                                    }
                                    checkNewMemberInfo.setMobile(mobile);
                                } else {
                                    checkNewMemberInfo.setMobile(mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length()));
                                }
                                JSONArray jsonArray2 = object.optJSONArray("reply");  //回复信息
                                if (jsonArray2 != null) {
                                    ArrayList<CheckNewMemberInfo.ReplyBean> replyBeanArrayList = new ArrayList<>();
                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                        JSONObject object2 = jsonArray2.optJSONObject(j);
                                        CheckNewMemberInfo.ReplyBean replyBean = new CheckNewMemberInfo.ReplyBean();
                                        replyBean.setReceiver(object2.optString("receiver"));
                                        replyBean.setText(object2.optString("text"));
                                        replyBean.setSender(object2.optString("sender"));
                                        replyBean.setType(object2.optInt("type"));
                                        replyBean.setUsername(object.optString("user_name"));
                                        replyBean.setReason(object.optString("reason"));
                                        replyBeanArrayList.add(replyBean);
                                        checkNewMemberInfo.setReply(replyBeanArrayList);
                                    }
                                }
                                checkNewMemberList.add(checkNewMemberInfo);
                            }

                            if (length < 15) {
                                applylistview_one.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                applylistview_one.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (checkNewMemberAdapter != null) {
                                checkNewMemberAdapter.notifyDataSetChanged();
                            }
                            applylistview_one.onRefreshComplete();
                        } else {
                            applylistview_one.setVisibility(View.GONE);
                            lin_Nodata.setVisibility(View.VISIBLE);
                            lin_Nodata.NoSearch("没有新申请的成员哦!");
                        }

                    } else {
                        applylistview_one.onRefreshComplete();
                        Tools.showToast(CheckNewMemberActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(CheckNewMemberActivity.this, getResources().getString(R.string.network_error));
                }
                applylistview_one.onRefreshComplete();
            }

        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                applylistview_one.onRefreshComplete();
                applylistview_one.setVisibility(View.GONE);
                lin_Nodata.NoNetwork();
                lin_Nodata.setVisibility(View.VISIBLE);
                Tools.showToast(CheckNewMemberActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    @Override
    public void onBack() {
        baseFinish();
    }


    //回复
    @Override
    public void onclick(final int position) {
        new DiscussPopDialog(this, new DiscussCallback() {
            @Override
            public void onDiscuss(String discuss_content) {
                //上传回复
                text = discuss_content;
                apply_id = checkNewMemberList.get(position).getApply_id();
                //-======接口
                getReply();
            }
        });

    }

    @Override
    public void getCheckNewMemberAdapter(TalklistAdapter adapter) {
        adapter.setReplyButtonButtonListener(this);
    }


    @Override
    public void refuse(int pos, final String applicant) {
        ConfirmDialog.showDialog(this, "提示！", 2, "您确认拒绝此成员加入你的战队吗？", "取消", "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {

            }

            @Override
            public void rightClick(Object object) {
                applicant2 = applicant;
                state = "0";
                getRest();
            }
        });
    }


    @Override
    public void pass(int pos, String applicant) {
        applicant2 = applicant;
        state = "1";
        getRest();
    }

    @Override
    public void onReplyclick(final int position) {
        new DiscussPopDialog(this, new DiscussCallback() {
            @Override
            public void onDiscuss(String discuss_content) {
                //上传回复
                text = discuss_content;
                apply_id = checkNewMemberList.get(position).getApply_id();
                //-======接口
                getReply();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
