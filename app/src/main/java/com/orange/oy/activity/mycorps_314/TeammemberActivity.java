package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_315.TeamheadRemarkActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.mycorps.TeammemberInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.FlowLayoutView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/14.
 * 团队成员列表
 */

public class TeammemberActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private PullToRefreshListView teammember_listview;
    private EditText teammember_main_edit;
    private ArrayList<TeammemberInfo> list = new ArrayList<>();
    private NetworkConnection teamUserList;
    private NetworkConnection delUserFromTeam;
    private NetworkConnection checkDelUserFromTeam;
    private int page = 1;
    private MyAdapter myAdapter;
    private AppTitle appTitle;

    protected void onStop() {
        super.onStop();
        if (myHandler != null)
            myHandler.removeMessages(0);
        if (teamUserList != null) {
            if (state == 3) {
                teamUserList.stop(Urls.DistributeTeamUserList);
            } else {
                teamUserList.stop(Urls.TeamUserList);
            }
        }
        if (delUserFromTeam != null) {
            delUserFromTeam.stop(Urls.DelUserFromTeam);
        }
        if (checkDelUserFromTeam != null) {
            checkDelUserFromTeam.stop(Urls.CheckDelUserFromTeam);
        }
    }

    private void initNetworkConnection() {
        teamUserList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                if (state == 3) {
                    Map<String, String> params = new HashMap<>();
                    params.put("usermobile", AppInfo.getName(TeammemberActivity.this));
                    params.put("token", Tools.getToken());
                    params.put("team_id", team_id);
                    params.put("project_id", project_id);
                    params.put("package_team_id", package_team_id);
                    return params;
                } else {
                    Map<String, String> params = new HashMap<>();
                    params.put("usermobile", AppInfo.getName(TeammemberActivity.this));
                    params.put("token", Tools.getToken());
                    params.put("team_id", team_id);
                    params.put("keyword", keyword);
                    params.put("page", page + "");
                    return params;
                }
            }
        };
        teamUserList.setTimeCount(true);
        delUserFromTeam = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TeammemberActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("user_id", list.get(selPosition).getUserId());
                return params;
            }
        };
        delUserFromTeam.setIsShowDialog(true);
        checkDelUserFromTeam = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TeammemberActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("user_id", list.get(selPosition).getUserId());
                return params;
            }
        };
        checkDelUserFromTeam.setIsShowDialog(true);
        teamUserList.setOnOutTimeListener(new NetworkConnection.OnOutTimeListener() {
            public void outTime() {

            }
        });
    }

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.teammember_apptitle);
        appTitle.settingName("战队成员");
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        switch (state) {
            case 1: {//踢出队员
                settingDel1();
            }
            break;
            case 3: {//分配时的全选
                appTitle.settingName("分配人员");
//                settingAll();
            }
            break;
        }
    }

    private boolean isAll = false;

    private void settingAll() {
        appTitle.settingSearch(R.mipmap.round_notselect, null);
        appTitle.settingExit("全选", Color.parseColor("#A0A0A0"), null);
        appTitle.settingRightListener(onRightClickForAppTitle);
    }

    private AppTitle.OnRightClickForAppTitle onRightClickForAppTitle = new AppTitle.OnRightClickForAppTitle() {
        public void onRightClick() {
            if (isAll) {
                appTitle.settingSearch(R.mipmap.round_notselect, null);
            } else {
                appTitle.settingSearch(R.mipmap.round_selected, null);
            }
            isAll = !isAll;
        }
    };

    private void settingDel1() {
        appTitle.hideExit();
        if (myAdapter != null) {
            myAdapter.setDelet(false);
        }
        appTitle.showIllustrate(R.mipmap.grrw_button_shanchu, new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel2();
            }
        });
    }

    private void settingDel2() {
        appTitle.hideIllustrate();
        if (myAdapter != null) {
            myAdapter.setDelet(true);
        }
        appTitle.settingExit("完成", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel1();
            }
        });
    }

    private String team_id, keyword = "";
    private int selPosition = -1;
    private MyHandler myHandler = new MyHandler();
    private NetworkView lin_Nodata;
    private int state = 0;//0：只能看无法进行任何操作；1：队长进入可以进行踢人操作；2：进行副队选择无法踢人；3：分配人员用；
    private View teammember_search;
    private String project_id, package_team_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammember);
        state = getIntent().getIntExtra("state", 0);
        initTitle();
        initNetworkConnection();
        team_id = getIntent().getStringExtra("team_id");
        project_id = getIntent().getStringExtra("project_id");
        package_team_id = getIntent().getStringExtra("package_team_id");
        if (TextUtils.isEmpty(team_id)) {
            Tools.showToast(this, "战队id是空");
        }
        findViewById(R.id.teammember_join).setOnClickListener(this);
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        teammember_search = findViewById(R.id.teammember_search);
        teammember_main_edit = (EditText) findViewById(R.id.teammember_main_edit);
        teammember_main_edit.addTextChangedListener(textWatcher);
        teammember_listview = (PullToRefreshListView) findViewById(R.id.teammember_listview);
        teammember_listview.setMode(PullToRefreshBase.Mode.BOTH);
        teammember_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        myAdapter = new MyAdapter();
        teammember_listview.setAdapter(myAdapter);
        if (state == 2 || state == 3) {
            teammember_listview.setOnItemClickListener(this);
            if (state == 3) {
                teammember_search.setVisibility(View.GONE);
            }
        }
        getData();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            myHandler.removeMessages(0);
            keyword = s.toString();
            myHandler.sendEmptyMessageDelayed(0, 200);
        }
    };

    private void getData() {
        String url;
        if (state == 3) {
            url = Urls.DistributeTeamUserList;
        } else {
            url = Urls.TeamUserList;
        }
        teamUserList.sendPostRequest(url, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        JSONArray jsonArray = jsonObject.getJSONObject("data").optJSONArray("list");
                        if (jsonArray != null) {
                            teammember_listview.setVisibility(View.VISIBLE);
                            lin_Nodata.setVisibility(View.GONE);
                            String usernum = jsonObject.getJSONObject("data").getString("user_num");
                            Spannable string = new SpannableString("战队成员 " + usernum + "人");
                            string.setSpan(new ForegroundColorSpan(Color.parseColor("#231916")), 0, 4,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            string.setSpan(new AbsoluteSizeSpan(Tools.spToPx(TeammemberActivity.this, 16)), 0, 4,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            string.setSpan(new ForegroundColorSpan(Color.parseColor("#A0A0A0")), 4, string.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            string.setSpan(new AbsoluteSizeSpan(Tools.spToPx(TeammemberActivity.this, 14)), 4, string.length(),
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            appTitle.settingName(string);
                            int length = jsonArray.length();
                            if (page == 1) {
                                list.clear();
                            }
                            for (int i = 0; i < length; i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                TeammemberInfo teammemberInfo = new TeammemberInfo();
                                teammemberInfo.setIco(jsonObject.optString("user_img"));
                                teammemberInfo.setName(jsonObject.optString("user_name"));
                                teammemberInfo.setAddress(jsonObject.optString("address"));
                                teammemberInfo.setUserId(jsonObject.optString("user_id"));
                                teammemberInfo.setCity(jsonObject.optString("city"));
                                teammemberInfo.setProvince(jsonObject.optString("province"));
                                teammemberInfo.setLevel(jsonObject.optString("user_level"));
                                teammemberInfo.setPhone(jsonObject.optString("mobile"));
                                teammemberInfo.setIdentity(jsonObject.optString("identity"));
                                teammemberInfo.setCompletemissionNumber(jsonObject.optString("task_num"));
                                teammemberInfo.setCompletePercentage(jsonObject.optString("pass_percent"));
                                teammemberInfo.setCompleteaheadPercentage(jsonObject.optString("ahead_percent"));
                                teammemberInfo.setIsdel(jsonObject.optString("is_del"));
                                teammemberInfo.setAge(jsonObject.optString("age"));
                                teammemberInfo.setSex(jsonObject.optString("sex"));//-1未填；0男；1女、
                                if (!jsonObject.isNull("remark")) {
                                    teammemberInfo.setRemark(jsonObject.optString("remark"));
                                } else {
                                    teammemberInfo.setRemark("");
                                }
                                teammemberInfo.setUser_mobile(jsonObject.optString("user_mobile"));
                                if (state == 3) {
                                    teammemberInfo.setState(jsonObject.optString("state"));
                                }
                                String agephone;
//                                if (TextUtils.isEmpty(teammemberInfo.getAge())) {
//                                    agephone = "电话：" + teammemberInfo.getPhone();
//                                } else {
//                                    agephone = "年龄：" + teammemberInfo.getAge() + " 电话：" + teammemberInfo.getPhone();
//                                }
                                if (TextUtils.isEmpty(teammemberInfo.getAge())) {
                                    agephone = "";
                                } else {
                                    agephone = "年龄：" + teammemberInfo.getAge();
                                }
                                teammemberInfo.setAgephone(agephone);
                                String personal_specialty = jsonObject.optString("personal_specialty");
                                if (!("null".equals(personal_specialty) || TextUtils.isEmpty(personal_specialty))) {
                                    String[] specialty = personal_specialty.replaceAll("\\[\"", "").replaceAll("\"]", "").split("\",\"");
                                    teammemberInfo.setSpecialtys(specialty);
                                }
                                String freetime = jsonObject.optString("free_time");
                                if (!("null".equals(freetime) || TextUtils.isEmpty(freetime))) {
                                    String[] freetimes = freetime.replaceAll("\\[\"", "").replaceAll("\"]", "").split("\",\"");
                                    for (String str : freetimes) {
                                        if (TextUtils.isEmpty(teammemberInfo.getFreetime())) {
                                            teammemberInfo.setFreetime("空闲时间:" + str);
                                        } else {
                                            teammemberInfo.setFreetime(teammemberInfo.getFreetime() + "、" + str);
                                        }
                                    }
                                }
                                String usual_place = jsonObject.optString("usual_place");
                                if (!("null".equals(usual_place) || TextUtils.isEmpty(usual_place))) {
                                    String[] freetimes = usual_place.replaceAll("\\[\"", "").replaceAll("\"]", "").split("\",\"");
                                    for (String str : freetimes) {
                                        if (TextUtils.isEmpty(teammemberInfo.getAddress2())) {
                                            teammemberInfo.setAddress2("常去地址:" + str);
                                        } else {
                                            teammemberInfo.setAddress2(teammemberInfo.getAddress2() + "、" + str);
                                        }
                                    }
                                }
                                //整合地区显示方式
                                String address;
                                if (TextUtils.isEmpty(teammemberInfo.getCity()) || "null".equals(teammemberInfo.getCity())) {
                                    teammemberInfo.setAddress("");
                                } else {
                                    if (teammemberInfo.getCity().equals(teammemberInfo.getProvince())) {
                                        address = teammemberInfo.getCity();
                                    } else {
                                        address = teammemberInfo.getProvince() + "-" + teammemberInfo.getCity();
                                    }
                                    if (!teammemberInfo.getCity().equals(teammemberInfo.getAddress()) &&
                                            !TextUtils.isEmpty(teammemberInfo.getAddress())) {
                                        address = address + "-" + teammemberInfo.getAddress();
                                    }
                                    teammemberInfo.setAddress("区域：" + address);
                                }
                                list.add(teammemberInfo);
                            }
                            teammember_listview.onRefreshComplete();
                            if (length < 15) {
                                teammember_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                teammember_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            myAdapter.notifyDataSetChanged();
                        } else {
                            if (!TextUtils.isEmpty(keyword)) {
                                teammember_listview.setVisibility(View.GONE);
                                lin_Nodata.setVisibility(View.VISIBLE);
                                lin_Nodata.NoSearch("没有搜索到成员");
                            }
                        }
                    } else {
                        Tools.showToast(TeammemberActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeammemberActivity.this, getResources().getString(R.string.network_error));
                }
                teammember_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                teammember_listview.onRefreshComplete();
                teammember_listview.setVisibility(View.GONE);
                lin_Nodata.NoNetwork();
                lin_Nodata.setVisibility(View.VISIBLE);
            }
        });
    }

    private void delete() {
        delUserFromTeam.sendPostRequest(Urls.DelUserFromTeam, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        Tools.showToast(TeammemberActivity.this, "已踢出战队");
                        list.remove(selPosition);
                        myAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TeammemberActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeammemberActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TeammemberActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void checkDelete() {
        checkDelUserFromTeam.sendPostRequest(Urls.CheckDelUserFromTeam, new Response.Listener<String>() {
            public void onResponse(String s) {//data：是否需要强制踢出（0：可正常踢出；1：需要强制踢出）
                Tools.d(s);
                CustomProgressDialog.Dissmiss();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        String data = jsonObject.getString("data");
                        if ("0".equals(data)) {
                            Tools.showToast(TeammemberActivity.this, "已踢出战队");
                            list.remove(selPosition);
                            myAdapter.notifyDataSetChanged();
                        } else if ("1".equals(data)) {//强制踢出提示框
                            ConfirmDialog.showDialog(TeammemberActivity.this, "提示", 3, "此队员有执行中的任务是否强制踢出\n" +
                                    "战队？如踢出战队则此队员的任务将\n" +
                                    "重新分配！", "取消", "确认", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    delete();
                                }
                            });
                        } else {
                            ConfirmDialog.showDialog(TeammemberActivity.this, "踢还是不踢？这是个问题...需要联系客服...", null, true, null);
                        }
                    } else {
                        Tools.showToast(TeammemberActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeammemberActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TeammemberActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.teammember_join: {
                Intent intent = new Intent(this, AddPlayersActivity.class);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }
            break;
        }
    }

    //设置战队副队长
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TeammemberInfo teammemberInfo = list.get(position - 1);
        switch (state) {
            case 2: {//选副队
                if ("1".equals(teammemberInfo.getIdentity())) {
                    Tools.showToast(this, "您已经是队长了");
                    return;
                }
                if ("2".equals(teammemberInfo.getIdentity())) {
                    Tools.showToast(this, "此人已是副队长");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("team_img", teammemberInfo.getIco());
                intent.putExtra("team_name", teammemberInfo.getName());
                intent.putExtra("team_phone", teammemberInfo.getPhone());
                intent.putExtra("user_id", teammemberInfo.getUserId());
                setResult(AppInfo.REQUEST_CODE_SETCAPTAIN, intent);
                baseFinish();
            }
            break;
            case 3: {//分配成员
                if (teammemberInfo.getState().equals("0")) {//不可分配状态点击无效
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("accessed_num", teammemberInfo.getUser_mobile());
                setResult(RESULT_OK, intent);
                baseFinish();
            }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppInfo.TeammemberheadremarkRequestCode: {//编辑备注
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position", -1);
                    if (position > -1 && position < list.size()) {
                        list.get(position).setRemark(data.getStringExtra("remark"));
                        if (myAdapter != null)
                            myAdapter.notifyDataSetChanged();
                    }
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyAdapter extends BaseAdapter implements View.OnClickListener {
        private ImageLoader imageLoader;
        private boolean isDelet;
        private int delWidth;

        public void setDelet(boolean delet) {
            isDelet = delet;
            notifyDataSetChanged();
        }

        MyAdapter() {
            delWidth = (int) getResources().getDimension(R.dimen.teammember_del_width);
            imageLoader = new ImageLoader(TeammemberActivity.this);
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                convertView = Tools.loadLayout(TeammemberActivity.this, R.layout.item_teammember);
                viewHold = new ViewHold();
                viewHold.name = (TextView) convertView.findViewById(R.id.item_teammember_name);
                viewHold.ico = (CircularImageView) convertView.findViewById(R.id.item_teammember_ico);
                viewHold.address = (TextView) convertView.findViewById(R.id.item_teammember_address);
                viewHold.identity = (ImageView) convertView.findViewById(R.id.item_teammember_identity);
                viewHold.phone = (TextView) convertView.findViewById(R.id.item_teammember_phone);
                viewHold.task1 = (TextView) convertView.findViewById(R.id.item_teammember_task1);
                viewHold.task2 = (TextView) convertView.findViewById(R.id.item_teammember_task2);
                viewHold.item_teammember_main = convertView.findViewById(R.id.item_teammember_main);
                viewHold.item_teammember_del = (TextView) convertView.findViewById(R.id.item_teammember_del);
                viewHold.item_teammember_rightbg = convertView.findViewById(R.id.item_teammember_rightbg);
                viewHold.item_teammember_labels = (FlowLayoutView) convertView.findViewById(R.id.item_teammember_labels);
                viewHold.item_teammember_label_1 = (TextView) convertView.findViewById(R.id.item_teammember_label_1);
                viewHold.item_teammember_label_2 = (TextView) convertView.findViewById(R.id.item_teammember_label_2);
                viewHold.item_teammember_label_3 = (TextView) convertView.findViewById(R.id.item_teammember_label_3);
                viewHold.item_teammember_label_4 = (TextView) convertView.findViewById(R.id.item_teammember_label_4);
                viewHold.item_teammember_label_5 = (TextView) convertView.findViewById(R.id.item_teammember_label_5);
                viewHold.line2 = convertView.findViewById(R.id.line2);
                viewHold.line3 = convertView.findViewById(R.id.line3);
                viewHold.item_teammember_note = convertView.findViewById(R.id.item_teammember_note);
                viewHold.item_teammember_main_layout = convertView.findViewById(R.id.item_teammember_main_layout);
                viewHold.item_teammember_address2 = (TextView) convertView.findViewById(R.id.item_teammember_address2);
                viewHold.item_teammember_freetime = (TextView) convertView.findViewById(R.id.item_teammember_freetime);
                viewHold.item_teammember_remark = (TextView) convertView.findViewById(R.id.item_teammember_remark);
                viewHold.item_teammember_sex = (ImageView) convertView.findViewById(R.id.item_teammember_sex);
                if (state == 1) {
                    viewHold.line3.setVisibility(View.VISIBLE);
                    viewHold.item_teammember_note.setVisibility(View.VISIBLE);
                    viewHold.item_teammember_note.setOnClickListener(editNoteListener);
                } else {
                    viewHold.line3.setVisibility(View.GONE);
                    viewHold.item_teammember_note.setVisibility(View.GONE);
                }
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            if (list != null && !list.isEmpty()) {
                TeammemberInfo teammemberInfo = list.get(position);
                if (state == 1) {//只有队长进入才有可能进备注页
                    if ("1".equals(teammemberInfo.getIdentity())) {//队长不显示备注
                        viewHold.line3.setVisibility(View.GONE);
                        viewHold.item_teammember_note.setVisibility(View.GONE);
                    } else {
                        viewHold.line3.setVisibility(View.VISIBLE);
                        viewHold.item_teammember_note.setVisibility(View.VISIBLE);
                    }
                    viewHold.item_teammember_note.setTag(position);
                }
                //设置性别 //-1未填；0男；1女
                if ("-1".equals(teammemberInfo.getSex())) {
                    viewHold.item_teammember_sex.setVisibility(View.GONE);
                } else if ("0".equals(teammemberInfo.getSex())) {
                    viewHold.item_teammember_sex.setImageResource(R.mipmap.sex_man);
                } else {
                    viewHold.item_teammember_sex.setImageResource(R.mipmap.sex_woman);
                }
                //添加特长
                if (teammemberInfo.getSpecialtys() != null && teammemberInfo.getSpecialtys().length > 0) {
                    for (int i = 0, j = 0; i < 5; i++) {
                        if (i < teammemberInfo.getSpecialtys().length && !TextUtils.isEmpty(teammemberInfo.getSpecialtys()[i])) {
                            if (viewHold.item_teammember_labels.getVisibility() == View.GONE) {
                                viewHold.item_teammember_labels.setVisibility(View.VISIBLE);
                                viewHold.line2.setVisibility(View.VISIBLE);
                            }
                            View view = viewHold.item_teammember_labels.getChildAt(i);
                            view.setVisibility(View.VISIBLE);
                            ((TextView) view).setText(teammemberInfo.getSpecialtys()[i]);
                        } else {
                            j++;
                            View view = viewHold.item_teammember_labels.getChildAt(i);
                            view.setVisibility(View.GONE);
                            if (j == 5) {
                                viewHold.item_teammember_labels.setVisibility(View.GONE);
                                viewHold.line2.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    viewHold.item_teammember_labels.setVisibility(View.GONE);
                    viewHold.line2.setVisibility(View.GONE);
                }
                //判断item背景
                if (state == 3) {//如果是分配成员
                    if ("0".equals(teammemberInfo.getState())) {//不可选择：灰色
                        viewHold.item_teammember_main_layout.setBackgroundResource(R.drawable.item_teammember_bg3);
                    } else {
                        viewHold.item_teammember_main_layout.setBackgroundResource(R.drawable.item_teammember_bg);
                    }
                }
                viewHold.name.setText(teammemberInfo.getName());
                String url = teammemberInfo.getIco();
                if (!"null".equals(url) && !TextUtils.isEmpty(url)) {
                    imageLoader.DisplayImage(Urls.ImgIp + url, viewHold.ico, R.mipmap.grxx_icon_mrtx);
                } else {
                    viewHold.ico.setImageResource(R.mipmap.grxx_icon_mrtx);
                }
                viewHold.address.setText(teammemberInfo.getAddress());
                if ("1".equals(teammemberInfo.getIdentity())) {
                    viewHold.identity.setImageResource(R.mipmap.item_teammember_identity1);
                } else if ("2".equals(teammemberInfo.getIdentity())) {
                    viewHold.identity.setImageResource(R.mipmap.item_teammember_identity2);
                } else {
                    viewHold.identity.setVisibility(View.INVISIBLE);
                }
                if (TextUtils.isEmpty(teammemberInfo.getAddress2())) {
                    viewHold.item_teammember_address2.setVisibility(View.GONE);
                } else {
                    viewHold.item_teammember_address2.setVisibility(View.VISIBLE);
                    viewHold.item_teammember_address2.setText(teammemberInfo.getAddress2());
                }
                if (TextUtils.isEmpty(teammemberInfo.getFreetime())) {
                    viewHold.item_teammember_freetime.setVisibility(View.GONE);
                } else {
                    viewHold.item_teammember_freetime.setVisibility(View.VISIBLE);
                    viewHold.item_teammember_freetime.setText(teammemberInfo.getFreetime());
                }
                if (TextUtils.isEmpty(teammemberInfo.getRemark()) || state != 1) {
                    viewHold.item_teammember_remark.setVisibility(View.GONE);
                } else {
                    viewHold.item_teammember_remark.setVisibility(View.VISIBLE);
                    viewHold.item_teammember_remark.setText(teammemberInfo.getRemark());
                }
                viewHold.phone.setText(teammemberInfo.getAgephone());
                viewHold.task1.setText(teammemberInfo.getCompletemissionNumber());
                viewHold.task2.setText(teammemberInfo.getCompletePercentage() + "%");
                viewHold.item_teammember_del.setTag(position);
                if (isDelet) {
                    viewHold.item_teammember_main.scrollTo(delWidth, 0);
                    viewHold.item_teammember_del.setVisibility(View.VISIBLE);
                    if ("0".equals(teammemberInfo.getIsdel())) {
                        viewHold.item_teammember_del.setBackgroundResource(R.drawable.item_teammember_del_bg2);
                        viewHold.item_teammember_rightbg.setBackgroundColor(Color.parseColor("#F3F3F3"));
                        viewHold.item_teammember_del.setText("不可踢出");
                        viewHold.item_teammember_del.setTextColor(Color.parseColor("#A0A0A0"));
                        viewHold.item_teammember_del.setOnClickListener(null);
                    } else {
                        viewHold.item_teammember_del.setBackgroundResource(R.drawable.item_teammember_del_bg);
                        viewHold.item_teammember_rightbg.setBackgroundColor(Color.parseColor("#F65D57"));
                        viewHold.item_teammember_del.setText("踢出战队");
                        viewHold.item_teammember_del.setTextColor(Color.WHITE);
                        viewHold.item_teammember_del.setOnClickListener(this);
                    }
                } else {
                    viewHold.item_teammember_main.scrollTo(0, 0);
                    viewHold.item_teammember_del.setVisibility(View.GONE);
                    viewHold.item_teammember_rightbg.setBackgroundColor(Color.alpha(0));
                }
            }
            return convertView;
        }

        private View.OnClickListener editNoteListener = new View.OnClickListener() {//填写备注监听
            public void onClick(View v) {
                Intent intent = new Intent(TeammemberActivity.this, TeamheadRemarkActivity.class);
                TeammemberInfo teammemberInfo = list.get((int) v.getTag());
                intent.putExtra("teammemberinfo", teammemberInfo);
                intent.putExtra("team_id", team_id);
                intent.putExtra("position", (int) v.getTag());
                startActivityForResult(intent, AppInfo.TeammemberheadremarkRequestCode);
            }
        };

        public void onClick(View v) {
            ConfirmDialog.showDialog(TeammemberActivity.this, "提示!", 1, "您真的要踢出吗？",
                    "取消", "确定", v.getTag(), false, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            selPosition = (int) object;
                            checkDelete();
                        }
                    });
        }

        private class ViewHold {
            TextView name, phone, address, task1, task2, item_teammember_del;
            CircularImageView ico;
            ImageView identity, item_teammember_sex;
            View item_teammember_main, item_teammember_rightbg;
            FlowLayoutView item_teammember_labels;
            TextView item_teammember_label_1, item_teammember_label_2, item_teammember_label_3, item_teammember_label_4,
                    item_teammember_label_5;
            View item_teammember_note, line2, line3, item_teammember_main_layout;
            TextView item_teammember_address2, item_teammember_freetime, item_teammember_remark;
        }
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            page = 1;
            getData();
        }
    }
}
