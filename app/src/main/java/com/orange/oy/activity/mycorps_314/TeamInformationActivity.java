package com.orange.oy.activity.mycorps_314;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.mycorps.TeamInformInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.util.UMShareUtils;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * V3.14 战队信息(已认证，未认证)
 */
public class TeamInformationActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle
        , View.OnClickListener {
    private LinearLayout lin_teamSpeciality;//战队特长根布局
    private LinearLayout lin_team_citys; //队员城市
    private String team_id;
    private ImageLoader imageLoader;

    private NetworkConnection TeamInform, exitTeam, Sign;
    private LinearLayout lin_notice, lin_percent, lin_LeaderTeamMember, lin_attestation, lin_certified, lin_alltask, lin_TeamMembers, lin_TeamMembers2, lin_NewApplicant,
            lin_invitemember, lin_editTeamInformation, lin_teamsetting, lin_NotTeamMember, lin_friendInvite, lin_quitTeam;
    private View lin_Nocertified;
    private ImageView iv_point, iv_point2;
    private CircularImageView iv_pic1, iv_pic2, myaccount_headimg;
    private TextView tv_renzheng, tv_name1, tv_slogan1, tv_headman1, tv_leadertwo, tv_leadertwo2, tv_teamAddress1, tv_Notrenzheng;
    private ImageView iv_fiveStar1, iv_fiveStar2, iv_fiveStar3, iv_fiveStar4, iv_fiveStar5;
    private TextView tv_name2, tv_slogan2, tv_headman2, tv_teamAddress2, tv_teamToal, tv_allmoney, tv_totalpeople, tv_noticeNumber,
            tv_allPeople, tv_percentOfPass, tv_EarlyCompletionRate, tv_TeamMembers, tv_TeamMembers2, tv_newMerbers, myaccount_percent1, myaccount_percent3;
    private TextView tv_headman3, tv_headman4;
    private String sign;
    private ProgressBar myaccount_percent2;

    public void onBack() {
        baseFinish();
    }

    private void initNetworkConnection() {
        TeamInform = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamInformationActivity.this));
                params.put("team_id", team_id);//战队id
                return params;
            }
        };
        TeamInform.setIsShowDialog(true);

        exitTeam = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamInformationActivity.this));
                params.put("team_id", team_id);//战队id
                return params;
            }
        };

        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", "team_id=" + team_id + "&usermobile=" + AppInfo.getName(TeamInformationActivity.this));
                return params;
            }
        };

    }


    private void initTitle() {
        team_id = getIntent().getStringExtra("team_id");
        AppTitle taskILL_title = taskILL_title = (AppTitle) findViewById(R.id.titleview);
        taskILL_title.settingName("战队信息");
        taskILL_title.showBack(this);
        //分享
        taskILL_title.showIllustrate(R.mipmap.zdxx_button_fenxiang, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                UMShareDialog.showDialog(TeamInformationActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        String webUrl = Urls.InviteToTeam + "?&team_id=" + team_id + "&usermobile=" + AppInfo.getName(TeamInformationActivity.this) + "&sign=" + sign;
                        MyUMShareUtils.umShare(TeamInformationActivity.this, type, webUrl);
                    }
                });
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_information);
        deputy = new ArrayList<>();
        team_speciality = new ArrayList<>();
        imageLoader = new ImageLoader(getBaseContext());
        initView();
        initTitle();
        initNetworkConnection();

    }

    private void sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                    } else {
                        Tools.showToast(TeamInformationActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamInformationActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamInformationActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        sign();
    }

    private void initView() {
        lin_percent = (LinearLayout) findViewById(R.id.lin_percent);
        lin_teamSpeciality = (LinearLayout) findViewById(R.id.lin_teamSpeciality);
        lin_team_citys = (LinearLayout) findViewById(R.id.lin_team_citys);
        lin_notice = (LinearLayout) findViewById(R.id.lin_notice);
        tv_noticeNumber = (TextView) findViewById(R.id.tv_noticeNumber);
        lin_LeaderTeamMember = (LinearLayout) findViewById(R.id.lin_LeaderTeamMember);
        lin_attestation = (LinearLayout) findViewById(R.id.lin_attestation);
        tv_TeamMembers = (TextView) findViewById(R.id.tv_TeamMembers);
        tv_TeamMembers2 = (TextView) findViewById(R.id.tv_TeamMembers2);
        myaccount_headimg = (CircularImageView) findViewById(R.id.myaccount_headimg);
        myaccount_percent1 = (TextView) findViewById(R.id.myaccount_percent1);
        myaccount_percent2 = (ProgressBar) findViewById(R.id.myaccount_percent2);
        myaccount_percent3 = (TextView) findViewById(R.id.myaccount_percent3);

        /*****************  战队已认证  *********************/

        lin_certified = (LinearLayout) findViewById(R.id.lin_certified);
        iv_point = (ImageView) findViewById(R.id.iv_point);
        iv_pic1 = (CircularImageView) findViewById(R.id.iv_pic1);
        iv_fiveStar1 = (ImageView) findViewById(R.id.iv_fiveStar1);
        iv_fiveStar2 = (ImageView) findViewById(R.id.iv_fiveStar2);
        iv_fiveStar3 = (ImageView) findViewById(R.id.iv_fiveStar3);
        iv_fiveStar4 = (ImageView) findViewById(R.id.iv_fiveStar4);
        iv_fiveStar5 = (ImageView) findViewById(R.id.iv_fiveStar5);
        tv_name1 = (TextView) findViewById(R.id.tv_name1);
        tv_renzheng = (TextView) findViewById(R.id.tv_renzheng);
        tv_slogan1 = (TextView) findViewById(R.id.tv_slogan1);
        tv_headman1 = (TextView) findViewById(R.id.tv_headman1);
        tv_leadertwo = (TextView) findViewById(R.id.tv_leadertwo);
        tv_leadertwo2 = (TextView) findViewById(R.id.tv_leadertwo2);
        tv_teamAddress1 = (TextView) findViewById(R.id.tv_teamAddress1);
        lin_notice.setOnClickListener(this);

        /************** 战队未认证  ****************************/
        lin_Nocertified = findViewById(R.id.lin_Nocertified);
        tv_Notrenzheng = (TextView) findViewById(R.id.tv_Notrenzheng);
        iv_pic2 = (CircularImageView) findViewById(R.id.iv_pic2);
        tv_name2 = (TextView) findViewById(R.id.tv_name2);
        tv_slogan2 = (TextView) findViewById(R.id.tv_slogan2);
        tv_headman2 = (TextView) findViewById(R.id.tv_headman2);
        tv_headman3 = (TextView) findViewById(R.id.tv_headman3);
        tv_headman4 = (TextView) findViewById(R.id.tv_headman4);
        tv_teamAddress2 = (TextView) findViewById(R.id.tv_teamAddress2);

        /***************** 完成任务量**************************/
        lin_alltask = (LinearLayout) findViewById(R.id.lin_alltask);
        tv_teamToal = (TextView) findViewById(R.id.tv_teamToal);
        tv_allmoney = (TextView) findViewById(R.id.tv_allmoney);
        tv_totalpeople = (TextView) findViewById(R.id.tv_totalpeople);
        tv_allPeople = (TextView) findViewById(R.id.tv_allPeople);
        tv_totalpeople.setOnClickListener(this);
        tv_allPeople.setOnClickListener(this);
        tv_percentOfPass = (TextView) findViewById(R.id.tv_percentOfPass);
        tv_EarlyCompletionRate = (TextView) findViewById(R.id.tv_EarlyCompletionRate);

        /***********************最下面的点击*********************************/
        tv_newMerbers = (TextView) findViewById(R.id.tv_newMerbers);  //新申请队员数
        lin_TeamMembers = (LinearLayout) findViewById(R.id.lin_TeamMembers);
        lin_TeamMembers2 = (LinearLayout) findViewById(R.id.lin_TeamMembers2);
        lin_TeamMembers.setOnClickListener(this);
        lin_TeamMembers2.setOnClickListener(this);
        lin_NewApplicant = (LinearLayout) findViewById(R.id.lin_NewApplicant);
        lin_invitemember = (LinearLayout) findViewById(R.id.lin_invitemember);
        lin_editTeamInformation = (LinearLayout) findViewById(R.id.lin_editTeamInformation);
        lin_teamsetting = (LinearLayout) findViewById(R.id.lin_teamsetting);
        lin_NotTeamMember = (LinearLayout) findViewById(R.id.lin_NotTeamMember);
        lin_friendInvite = (LinearLayout) findViewById(R.id.lin_friendInvite);
        lin_quitTeam = (LinearLayout) findViewById(R.id.lin_quitTeam);
    }

    private String notice, team_img, team_name, team_slogan, team_credit, personal_auth, enterprise_auth;
    private String leadername, leadermobile, province, task_num, total_money, open_total_amount, user_num, pass_percent, ahead_percent, invitation,
            user_identity;
    private int task_rank;

    List<String> team_speciality;
    List<TeamInformInfo.DeputyBean> deputy;

    private void getData() {
        TeamInform.sendPostRequest(Urls.TEAMINFO, new Response.Listener<String>() {
            public void onResponse(String s) {
                TeamInform.setIsShowDialog(false);
                Tools.d(s);
                CustomProgressDialog.Dissmiss();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONObject object = jsonObject.optJSONObject("data");
                        TeamInformInfo teamInformInfo = new TeamInformInfo();
                        invitation = object.getString("invitation");  //是否允许邀请加入战队，1为是，0为否

                        notice = object.getString("notice");
                        if (notice.equals("0")) {
                            tv_noticeNumber.setVisibility(View.GONE);
                        } else {
                            tv_noticeNumber.setVisibility(View.VISIBLE);
                            tv_noticeNumber.setText(notice);
                        }
                        team_img = object.getString("team_img");
                        team_name = object.getString("team_name");
                        task_rank = object.optInt("task_rank"); //本省已超过的百分比数值，不带百分号"
                        myaccount_percent1.setText(String.format(getResources().getString(R.string.account_money2), task_rank + "%"));
                        myaccount_percent2.setProgress(task_rank);
                        myaccount_percent3.setText(task_rank + "%");

                        if (object.isNull("team_slogan")) {
                            team_slogan = null;
                        } else {
                            team_slogan = object.getString("team_slogan");
                        }

                        if (!TextUtils.isEmpty(team_slogan)) {
                            tv_slogan1.setText(team_slogan);
                            tv_slogan2.setText(team_slogan);
                        } else {
                            tv_slogan1.setVisibility(View.GONE);
                            tv_slogan2.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(team_img) && !"null".equals(team_img)) {
                            imageLoader.DisplayImage(Urls.ImgIp + team_img, iv_pic1, R.mipmap.grxx_icon_mrtx);
                            imageLoader.DisplayImage(Urls.ImgIp + team_img, iv_pic2, R.mipmap.grxx_icon_mrtx);
                            imageLoader.DisplayImage(Urls.ImgIp + team_img, myaccount_headimg, R.mipmap.grxx_icon_mrtx);
                        }
                        tv_name1.setText(team_name);
                        tv_name2.setText(team_name);
                        team_credit = object.getString("team_credit");

                        user_identity = object.getString("user_identity");  //用户在战队中身份， 1 为队长 2为队副 3为普通成员
                        personal_auth = object.getString("personal_auth"); //是否进行了个人认证，1为是，0为否
                        enterprise_auth = object.getString("enterprise_auth"); //是否进行了企业认证，1为是，0为否

                        if (user_identity.equals("1") || user_identity.equals("2")) {  // 1 为队长 2为队副
                            if (user_identity.equals("2")) {
                                lin_NotTeamMember.setVisibility(View.VISIBLE);
                                lin_friendInvite.setVisibility(View.GONE);
                                lin_TeamMembers2.setVisibility(View.GONE);
                                findViewById(R.id.lin_friendInvite_line).setVisibility(View.GONE);
                                lin_quitTeam.setOnClickListener(TeamInformationActivity.this);
                                lin_editTeamInformation.setVisibility(View.GONE);
                                findViewById(R.id.lin_editTeamInformation_line).setVisibility(View.GONE);
                            } else {
                                lin_editTeamInformation.setVisibility(View.VISIBLE);
                                findViewById(R.id.lin_editTeamInformation_line).setVisibility(View.VISIBLE);
                                lin_NotTeamMember.setVisibility(View.GONE);
                                lin_quitTeam.setOnClickListener(null);
                            }
                            String apply_user_num = object.getString("apply_member_num");
                            if (TextUtils.isEmpty(apply_user_num) || apply_user_num.equals("0")) {
                                tv_newMerbers.setVisibility(View.GONE);
                            } else {
                                tv_newMerbers.setVisibility(View.VISIBLE);
                                tv_newMerbers.setText(apply_user_num);
                            }
                            lin_LeaderTeamMember.setVisibility(View.VISIBLE);
                            if (personal_auth.equals("1")) { //个人认证
                                lin_NewApplicant.setVisibility(View.VISIBLE);
                                lin_NewApplicant.setOnClickListener(TeamInformationActivity.this);
                                lin_TeamMembers.setVisibility(View.VISIBLE);
                                lin_invitemember.setVisibility(View.VISIBLE);
                                lin_teamsetting.setVisibility(View.VISIBLE);
                                lin_attestation.setVisibility(View.VISIBLE);
                                findViewById(R.id.lin_attestation_line).setVisibility(View.VISIBLE);

                                lin_Nocertified.setVisibility(View.GONE);
                                lin_certified.setVisibility(View.VISIBLE);
                                lin_alltask.setVisibility(View.VISIBLE);
                                iv_point.setImageResource(R.mipmap.zdrz_button_grrz);
                                tv_renzheng.setText("已认证");
                                lin_editTeamInformation.setVisibility(View.GONE);
                                findViewById(R.id.lin_editTeamInformation_line).setVisibility(View.GONE);
                            } else {
                                if (enterprise_auth.equals("1")) {   //企业认证
                                    lin_NewApplicant.setVisibility(View.VISIBLE);
                                    lin_NewApplicant.setOnClickListener(TeamInformationActivity.this);
                                    lin_TeamMembers.setVisibility(View.VISIBLE);
                                    lin_invitemember.setVisibility(View.VISIBLE);
                                    lin_teamsetting.setVisibility(View.VISIBLE);
                                    lin_attestation.setVisibility(View.GONE); //战队认证
                                    findViewById(R.id.lin_attestation_line).setVisibility(View.GONE);

                                    lin_Nocertified.setVisibility(View.GONE);
                                    lin_certified.setVisibility(View.VISIBLE);
                                    lin_alltask.setVisibility(View.VISIBLE);
                                    iv_point.setImageResource(R.mipmap.zdrz_button_qyrz);
                                    tv_renzheng.setText("已认证");
                                    lin_editTeamInformation.setVisibility(View.GONE);
                                    findViewById(R.id.lin_editTeamInformation_line).setVisibility(View.GONE);
                                } else {  //未认证
                                    if (user_identity.equals("1")) {
                                        lin_attestation.setVisibility(View.VISIBLE);
                                        findViewById(R.id.lin_attestation_line).setVisibility(View.VISIBLE);
                                        tv_Notrenzheng.setOnClickListener(TeamInformationActivity.this);
                                        lin_attestation.setOnClickListener(TeamInformationActivity.this);
                                    } else {
                                        lin_attestation.setVisibility(View.GONE);
                                        findViewById(R.id.lin_attestation_line).setVisibility(View.GONE);
                                    }
                                    lin_Nocertified.setVisibility(View.VISIBLE);
                                    lin_certified.setVisibility(View.GONE);
                                    lin_alltask.setVisibility(View.GONE);
                                    lin_NewApplicant.setVisibility(View.VISIBLE);
                                    lin_NewApplicant.setOnClickListener(TeamInformationActivity.this);
                                    lin_TeamMembers.setVisibility(View.VISIBLE);
                                    lin_invitemember.setVisibility(View.VISIBLE);
                                    lin_teamsetting.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {  //==============个人
                            lin_NotTeamMember.setVisibility(View.VISIBLE);
                            lin_LeaderTeamMember.setVisibility(View.GONE);
                            lin_TeamMembers2.setVisibility(View.VISIBLE);
                            lin_quitTeam.setOnClickListener(TeamInformationActivity.this);
                            if ("0".equals(invitation)) {//不允许队员邀请
                                lin_friendInvite.setVisibility(View.GONE);
                                findViewById(R.id.lin_friendInvite_line).setVisibility(View.GONE);
                            } else {
                                lin_friendInvite.setVisibility(View.VISIBLE);
                                findViewById(R.id.lin_friendInvite_line).setVisibility(View.VISIBLE);
                            }
                            if (personal_auth.equals("1")) { //个人认证
                                lin_Nocertified.setVisibility(View.GONE);
                                lin_certified.setVisibility(View.VISIBLE);
                                lin_alltask.setVisibility(View.VISIBLE);
                                iv_point.setImageResource(R.mipmap.zdrz_button_grrz);
                            } else {
                                if (enterprise_auth.equals("1")) {   //企业认证
                                    lin_Nocertified.setVisibility(View.GONE);
                                    lin_certified.setVisibility(View.VISIBLE);
                                    lin_alltask.setVisibility(View.VISIBLE);
                                    iv_point.setImageResource(R.mipmap.zdrz_button_qyrz);
                                } else {  //未认证
                                    lin_Nocertified.setVisibility(View.VISIBLE);
                                    lin_certified.setVisibility(View.GONE);
                                    lin_alltask.setVisibility(View.GONE);
                                }
                            }
                        }

                        JSONObject object1 = object.getJSONObject("captain");  //队长信息
                        leadername = object1.optString("name");
                        leadermobile = object1.optString("mobile");
                        tv_headman1.setText("队长：" + leadername + "  " + leadermobile);
                        tv_headman2.setText("队长：" + leadername + "  " + leadermobile);
                        JSONArray jsonArray = object.optJSONArray("deputy");  //队副信息
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                TeamInformInfo.DeputyBean deputyBean = new TeamInformInfo.DeputyBean();
                                JSONObject object2 = jsonArray.getJSONObject(i);
                                deputyBean.setMobile(object2.getString("mobile"));
                                deputyBean.setName(object2.getString("name"));
                                deputy.add(deputyBean);
                            }

                            if (jsonArray.length() == 1) {
                                tv_leadertwo.setVisibility(View.VISIBLE);
                                tv_leadertwo2.setVisibility(View.GONE);
                                tv_leadertwo.setText("队副：" + deputy.get(0).getName() + "   " + deputy.get(0).getMobile());
                                tv_headman3.setText("队副：" + deputy.get(0).getName() + "   " + deputy.get(0).getMobile());
                                tv_headman3.setVisibility(View.VISIBLE);
                                tv_headman4.setVisibility(View.GONE);
                            } else if (jsonArray.length() == 2) {
                                tv_leadertwo.setVisibility(View.VISIBLE);
                                tv_leadertwo2.setVisibility(View.VISIBLE);
                                tv_leadertwo.setText("队副：" + deputy.get(0).getName() + "   " + deputy.get(0).getMobile());
                                tv_leadertwo2.setText("队副：" + deputy.get(1).getName() + "   " + deputy.get(1).getMobile());
                                tv_headman3.setText("队副：" + deputy.get(0).getName() + "   " + deputy.get(0).getMobile());
                                tv_headman4.setText("队副：" + deputy.get(1).getName() + "   " + deputy.get(1).getMobile());
                                tv_headman3.setVisibility(View.VISIBLE);
                                tv_headman4.setVisibility(View.VISIBLE);
                            } else {
                                tv_leadertwo.setVisibility(View.GONE);
                                tv_leadertwo2.setVisibility(View.GONE);
                                tv_headman3.setVisibility(View.GONE);
                                tv_headman4.setVisibility(View.GONE);
                            }
                        } else {
                            tv_leadertwo.setVisibility(View.GONE);
                            tv_leadertwo2.setVisibility(View.GONE);
                            tv_headman3.setVisibility(View.GONE);
                            tv_headman4.setVisibility(View.GONE);
                            if (lin_Nocertified.getVisibility() == View.VISIBLE) {
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lin_Nocertified.getLayoutParams();
                                lp.height = Tools.dipToPx(TeamInformationActivity.this, 160);
                                lin_Nocertified.setLayoutParams(lp);
                            }
                        }
                        if (lin_team_citys.getChildCount() > 1) {
                            View view = lin_team_citys.getChildAt(0);
                            lin_team_citys.removeAllViews();
                            lin_team_citys.addView(view);
                        }
                        int length;
                        JSONArray jsonArray3 = object.optJSONArray("user_city");  //队员城市
                        length = jsonArray3.length();
                        HotTabInfo[] hotTabInfos = new HotTabInfo[length];
                        for (int i = 0; i < length; i++) {
                            JSONObject tempJson = jsonArray3.getJSONObject(i);
                            HotTabInfo hotTabInfo = new HotTabInfo();
                            hotTabInfo.city = tempJson.optString("city");
                            if (!TextUtils.isEmpty(hotTabInfo.city)) {
                                hotTabInfo.num = tempJson.optString("num");
                                hotTabInfos[i] = hotTabInfo;
                            }
                        }
                        autoAddTab2(hotTabInfos);

                        province = object.getString("province");
                        task_num = object.getString("task_num");
                        total_money = object.getString("total_money");
                        open_total_amount = object.getString("open_total_amount");  //是否显示金额，1为是，0为否
                        user_num = object.getString("user_num");
                        pass_percent = object.getString("pass_percent");
                        ahead_percent = object.getString("ahead_percent");

                        tv_teamAddress1.setText("地域：" + province);
                        tv_teamAddress2.setText("地域：" + province);
                        tv_teamToal.setText((task_num.equals("0") ? "--" : task_num));
                        tv_totalpeople.setText(user_num);
                        tv_TeamMembers.setText(user_num + "人");
                        tv_TeamMembers2.setText(user_num + "人");
                        tv_percentOfPass.setText((pass_percent.equals("0") ? "--" : pass_percent + "%"));
                        tv_EarlyCompletionRate.setText((ahead_percent.equals("0") ? "--" : ahead_percent + "%"));
                        if (open_total_amount.equals("1")) {
                            tv_allmoney.setText((total_money.equals("0") ? "--" : total_money));
                        } else {
                            tv_allmoney.setText("--");
                        }
                        //====特长
                        if (team_speciality == null) {
                            team_speciality = new ArrayList<String>();
                        } else {
                            team_speciality.clear();
                        }
                        if (lin_teamSpeciality.getChildCount() > 1) {
                            View view = lin_teamSpeciality.getChildAt(0);
                            lin_teamSpeciality.removeAllViews();
                            lin_teamSpeciality.addView(view);
                        }
                        JSONArray jsonArray2 = object.optJSONArray("team_speciality");  //战队特长
                        if (jsonArray2 != null) {
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                String string = jsonArray2.getString(i);
                                team_speciality.add(string);
                            }
                            teamInformInfo.setTeam_speciality(team_speciality);
                        }
                        int size = teamInformInfo.getTeam_speciality().size();
                        String[] array = teamInformInfo.getTeam_speciality().toArray(new String[size]);
                        autoAddTab(array);
                        if (lin_invitemember.getVisibility() == View.VISIBLE) {
                            lin_invitemember.setOnClickListener(TeamInformationActivity.this);
                        }
                        if (lin_editTeamInformation.getVisibility() == View.VISIBLE) {
                            lin_editTeamInformation.setOnClickListener(TeamInformationActivity.this);
                        }
                        if (lin_teamsetting.getVisibility() == View.VISIBLE) {
                            lin_teamsetting.setOnClickListener(TeamInformationActivity.this);
                        }
                        if (lin_friendInvite.getVisibility() == View.VISIBLE) {
                            lin_friendInvite.setOnClickListener(TeamInformationActivity.this);
                        }
                    } else {
                        Tools.showToast(TeamInformationActivity.this, jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(TeamInformationActivity.this, getResources().getString(R.string.network_error));
                }
            }

        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamInformationActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private class HotTabInfo {
        String city, num;
    }

    private void autoAddTab(String[] tabInfos) {
        final int mar = (int) getResources().getDimension(R.dimen.searchhot_bg_marginLeftRight) + 1;//最外层左右边距
        final int tabMar = (int) getResources().getDimension(R.dimen.searchhot_tab_marginLeftRight) + 1;//标签外的右边距
        final int tabHeight = (int) getResources().getDimension(R.dimen.searchhot_tab_height);//标签高度
        final int tabTextmarg = (int) getResources().getDimension(R.dimen.searchhot_tab_text_margLeftRight) + 1;//标签内边距
        final int windowWidth = Tools.getScreeInfoWidth(this) - mar * 2;
        int layoutwidth = 0;
        LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tabParams.topMargin = (int) getResources().getDimension(R.dimen.searchhot_tab_marginLeftRight) + 1;//标签上边距
        LinearLayout tempLayout = new LinearLayout(this);
        tempLayout.setOrientation(LinearLayout.HORIZONTAL);
        lin_teamSpeciality.addView(tempLayout, tabParams);
        boolean isAddMar;
        for (String temp : tabInfos) {
            int length = temp.length();
            //------start--------设置标签样式
            TextView tv = new TextView(this);
            tv.setTextSize(12);
            tv.setTextColor(Color.parseColor("#FF7C7C7C"));
            tv.setText(temp);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.search_hot_bg);
            tv.setTag(temp);
            tv.setOnClickListener(hotTabOnClickListener);
            //------end--------设置标签样式
            TextPaint paint = tv.getPaint();
            int minus = length - 4;
            int textWidth;
            if (minus <= 0) {
                textWidth = (int) (tv.getTextSize() * 6) + 1;
            } else {
                textWidth = (int) (paint.measureText(temp) + tabTextmarg * 2) + 1;
            }
            layoutwidth = layoutwidth + textWidth + tabMar;
            isAddMar = true;
            if (layoutwidth >= windowWidth) {
                layoutwidth = layoutwidth - tabMar;
                if (layoutwidth >= windowWidth) {
                    layoutwidth = textWidth + tabMar;
                    tempLayout = new LinearLayout(this);
                    tempLayout.setOrientation(LinearLayout.HORIZONTAL);
                    lin_teamSpeciality.addView(tempLayout, tabParams);
                } else {
                    isAddMar = false;
                }
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(textWidth, tabHeight);
            if (isAddMar) {
                params.rightMargin = tabMar;
            }
            tempLayout.addView(tv, params);
        }
    }

    /**
     * 生成标签
     */
    private void autoAddTab2(HotTabInfo[] tabInfos) {
        final int mar = (int) getResources().getDimension(R.dimen.searchhot_bg_marginLeftRight) + 1;//最外层左右边距
        final int tabMar = (int) getResources().getDimension(R.dimen.teaminfornation_marginLeftRight) + 1;//标签外的右边距
        final int tabHeight = (int) getResources().getDimension(R.dimen.teaminfornation_tabheight);//标签高度
        final int tabTextmarg = (int) getResources().getDimension(R.dimen.teaminfornation_text_margLeftRight) + 1;//标签内边距
        final int windowWidth = Tools.getScreeInfoWidth(this) - mar * 2;
        int layoutwidth = 0;
        LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tabParams.topMargin = (int) getResources().getDimension(R.dimen.teaminfornation_marginLeftRight) + 1;//标签上边距
        LinearLayout tempLayout = new LinearLayout(this);
        tempLayout.setOrientation(LinearLayout.HORIZONTAL);
        lin_team_citys.addView(tempLayout, tabParams);
        boolean isAddMar;
        for (HotTabInfo temp : tabInfos) {
            if (temp == null) continue;
            String string = temp.city + " " + temp.num;
            //------start--------设置标签样式
            TextView tv = new TextView(this);
            tv.setTextSize(12);
            tv.setTextColor(Color.parseColor("#FF7C7C7C"));
            tv.setText(string);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.teaminfomation_tab_bg2);
            tv.setTag(temp);
            tv.setOnClickListener(hotTabOnClickListener);
            //------end--------设置标签样式
            TextPaint paint = tv.getPaint();
            int textWidth;
            textWidth = (int) (paint.measureText(string) + tabTextmarg * 2) + 1;
            layoutwidth = layoutwidth + textWidth + tabMar;
            isAddMar = true;
            if (layoutwidth >= windowWidth) {
                layoutwidth = layoutwidth - tabMar;
                if (layoutwidth >= windowWidth) {
                    layoutwidth = textWidth + tabMar;
                    tempLayout = new LinearLayout(this);
                    tempLayout.setOrientation(LinearLayout.HORIZONTAL);
                    lin_teamSpeciality.addView(tempLayout, tabParams);
                } else {
                    isAddMar = false;
                }
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(textWidth, tabHeight);
            if (isAddMar) {
                params.rightMargin = tabMar;
            }
            tempLayout.addView(tv, params);
        }
    }

    private View.OnClickListener hotTabOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //search_main_edit.setText(((HotTabInfo) v.getTag()).name);
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            //公告
            case R.id.lin_notice: {
                Intent intent = new Intent(this, CorpsNoticeActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("user_identity", user_identity);
                startActivity(intent);
            }
            break;
            //
            case R.id.lin_certified: {

            }
            break;
            //未认证 按钮  只有队长可以点击
            case R.id.tv_Notrenzheng: {
                Intent intent = new Intent(this, TeamShallActivity.class);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }

            break;
            case R.id.lin_attestation: {
                Intent intent = new Intent(this, TeamShallActivity.class);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }
            break;

            //战队人数
            case R.id.tv_totalpeople: {

            }
            break;
            case R.id.tv_allPeople: {

            }
            break;
            //新申请成员
            case R.id.lin_NewApplicant: {

                Intent intent = new Intent(this, CheckNewMemberActivity.class);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }
            break;
            //战队成员
            case R.id.lin_TeamMembers: {
                Intent intent = new Intent(this, TeammemberActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("state", user_identity.equals("1") ? 1 : 0);
                startActivity(intent);
            }
            break;
            case R.id.lin_TeamMembers2: {
                Intent intent = new Intent(this, TeammemberActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("state", user_identity.equals("1") ? 1 : 0);
                startActivity(intent);
            }
            break;
            //邀请加入战队
            case R.id.lin_invitemember: {
                Intent intent = new Intent(this, AddPlayersActivity.class);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }
            break;
            //编辑战队信息
            case R.id.lin_editTeamInformation: {
                Intent intent = new Intent(this, CreateCorpActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }
            break;
            //战队设置
            case R.id.lin_teamsetting: {
                Intent intent = new Intent(this, SetCorpsActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("personal_auth", personal_auth);
                intent.putExtra("enterprise_auth", enterprise_auth);
                intent.putExtra("isCaptain", "1".equals(user_identity));
                startActivity(intent);
            }
            break;
            //队员里面  邀请好友
            case R.id.lin_friendInvite: {
                Intent intent = new Intent(this, AddPlayersActivity.class);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }
            break;
            //退出战队
            case R.id.lin_quitTeam: {
                ConfirmDialog.showDialog(this, "提示！", 3, "您确认退出战队吗?", "取消", "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                    @Override
                    public void leftClick(Object object) {

                    }

                    @Override
                    public void rightClick(Object object) {
                        getExitTeam();
                    }
                });
            }
            break;
        }


    }

    private void getExitTeam() {
        exitTeam.sendPostRequest(Urls.EXITTEAM, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //  jsonObject.getString("data");
                        Tools.showToast(TeamInformationActivity.this, jsonObject.getString("msg"));
                        finish();
                    } else {
                        Tools.showToast(TeamInformationActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamInformationActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamInformationActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }
}
