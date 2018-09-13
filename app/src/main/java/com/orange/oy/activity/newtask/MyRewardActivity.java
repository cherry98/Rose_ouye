package com.orange.oy.activity.newtask;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.adapter.MyRewardAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MyRewardInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的奖励页面
 */
public class MyRewardActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.myreward_title);
        showSearch();
        appTitle.settingName(getResources().getString(R.string.taskschdetail));
        appTitle.showBack(this);
    }

    private String searchStr = "";

    public void showSearch() {
        appTitle.settingHint("可搜索各状态下的网点、项目");
        appTitle.showSearch(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable s) {

            }
        }, new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchStr = v.getText().toString().trim();
                    myreward_listview_search.setVisibility(View.VISIBLE);
                    findViewById(R.id.myreward_title_layout).setVisibility(View.GONE);
                    findViewById(R.id.myreward_underline).setVisibility(View.GONE);
                    myreward_listview_one.setVisibility(View.GONE);
                    myreward_listview_two.setVisibility(View.GONE);
                    myreward_listview_three.setVisibility(View.GONE);
                    myreward_listview_four.setVisibility(View.GONE);
                    state = "4";
                    page = 1;
                    getData();
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myReward != null) {
            myReward.stop(Urls.MyReward);
        }
    }

    public void initNetworkConnection() {
        myReward = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MyRewardActivity.this));
                params.put("state", state);
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("outletName", searchStr);
                }
                params.put("token", Tools.getToken());
                params.put("page", page + "");
                return params;
            }
        };
        myReward.setIsShowDialog(true);
    }

    private PullToRefreshListView myreward_listview_one, myreward_listview_two, myreward_listview_three,
            myreward_listview_four;
    private PullToRefreshListView myreward_listview_search;
    private TextView myreward_one, myreward_two, myreward_three, myreward_four;
    private View myreward_viewone, myreward_viewtwo, myreward_viewthree, myreward_viewfour;
    private MyRewardAdapter myRewardAdapter;
    private NetworkConnection myReward;
    private String state = "";
    private ArrayList<MyRewardInfo> list;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reward);
        initView();
        initTitle();
        initNetworkConnection();
        list = new ArrayList<>();
        OnItemClick();
        initListView(myreward_listview_one);
        initListView(myreward_listview_two);
        initListView(myreward_listview_three);
        initListView(myreward_listview_four);
        myreward_listview_one.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                refreshOneData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                refreshOneData();
            }
        });
        myreward_listview_two.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                refreshTwoData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                refreshTwoData();
            }
        });
        myreward_listview_three.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                refreshThereData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                refreshThereData();
            }
        });
        myreward_listview_four.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                refreshFourData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                refreshFourData();
            }
        });
        myRewardAdapter = new MyRewardAdapter(this, list);
        myreward_listview_one.setAdapter(myRewardAdapter);
        myreward_listview_two.setAdapter(myRewardAdapter);
        myreward_listview_three.setAdapter(myRewardAdapter);
        myreward_listview_four.setAdapter(myRewardAdapter);
        myreward_listview_search.setAdapter(myRewardAdapter);
        myreward_one.setOnClickListener(this);
        myreward_two.setOnClickListener(this);
        myreward_three.setOnClickListener(this);
        myreward_four.setOnClickListener(this);
        onClick(myreward_four);
    }

    private void OnItemClick() {
        myreward_listview_two.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyRewardInfo myRewardInfo = list.get(position - 1);
                if (myRewardAdapter != null) {
                    if (myRewardAdapter.isClick2()) {//TODO
                        if (myRewardInfo.getType().equals("1")) {//普通
                            Intent intent = new Intent(MyRewardActivity.this, TaskDistActivity.class);
                            intent.putExtra("id", myRewardInfo.getProject_id());
                            intent.putExtra("project_name", myRewardInfo.getProjectName());
                            intent.putExtra("project_code", myRewardInfo.getProject_code());
                            intent.putExtra("is_record", myRewardInfo.getIs_record() + "");
                            intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                            intent.putExtra("begin_date", myRewardInfo.getBegin_date());
                            intent.putExtra("end_date", myRewardInfo.getEnd_date());
                            intent.putExtra("is_watermark", Tools.StringToInt(myRewardInfo.getIs_watermark() + ""));
                            intent.putExtra("code", myRewardInfo.getCode());
                            intent.putExtra("brand", myRewardInfo.getBrand());
                            intent.putExtra("is_takephoto", myRewardInfo.getIs_takephoto() + "");
                            intent.putExtra("type", myRewardInfo.getType() + "");
                            intent.putExtra("check_time", myRewardInfo.getCheck_time());
                            intent.putExtra("project_property", myRewardInfo.getProject_property());
                            intent.putExtra("type", myRewardInfo.getType());
                            intent.putExtra("money_unit", myRewardInfo.getMoney_unit());
                            startActivity(intent);
                        } else if (myRewardInfo.getType().equals("2")) {//暗访
                            Intent intent = new Intent(MyRewardActivity.this, BlackDZXListActivity.class);
                            intent.putExtra("id", myRewardInfo.getProject_id());
                            intent.putExtra("project_name", myRewardInfo.getProjectName());
                            intent.putExtra("project_code", myRewardInfo.getProject_code());
                            intent.putExtra("is_record", myRewardInfo.getIs_record() + "");
                            intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                            intent.putExtra("begin_date", myRewardInfo.getBegin_date());
                            intent.putExtra("end_date", myRewardInfo.getEnd_date());
                            intent.putExtra("is_watermark", Tools.StringToInt(myRewardInfo.getIs_watermark() + ""));
                            intent.putExtra("code", myRewardInfo.getCode());
                            intent.putExtra("brand", myRewardInfo.getBrand());
                            intent.putExtra("is_takephoto", myRewardInfo.getIs_takephoto() + "");
                            intent.putExtra("type", myRewardInfo.getType() + "");
                            intent.putExtra("check_time", myRewardInfo.getCheck_time());
                            intent.putExtra("project_property", myRewardInfo.getProject_property());
                            intent.putExtra("type", myRewardInfo.getType());
                            intent.putExtra("money_unit", myRewardInfo.getMoney_unit());
                            startActivity(intent);
                        } else if (myRewardInfo.getType().equals("3")) {//明访
                            Intent intent = new Intent(MyRewardActivity.this, TaskDistActivity.class);
                            intent.putExtra("id", myRewardInfo.getProject_id());
                            intent.putExtra("project_name", myRewardInfo.getProjectName());
                            intent.putExtra("project_code", myRewardInfo.getProject_code());
                            intent.putExtra("is_record", myRewardInfo.getIs_record() + "");
                            intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                            intent.putExtra("begin_date", myRewardInfo.getBegin_date());
                            intent.putExtra("end_date", myRewardInfo.getEnd_date());
                            intent.putExtra("is_watermark", Tools.StringToInt(myRewardInfo.getIs_watermark() + ""));
                            intent.putExtra("code", myRewardInfo.getCode());
                            intent.putExtra("brand", myRewardInfo.getBrand());
                            intent.putExtra("is_takephoto", myRewardInfo.getIs_takephoto() + "");
                            intent.putExtra("type", myRewardInfo.getType() + "");
                            intent.putExtra("check_time", myRewardInfo.getCheck_time());
                            intent.putExtra("project_property", myRewardInfo.getProject_property());
                            intent.putExtra("type", myRewardInfo.getType());
                            intent.putExtra("money_unit", myRewardInfo.getMoney_unit());
                            startActivity(intent);
                        }
                    } else if (myRewardAdapter.isClick1()) {
                        String reason = myRewardAdapter.getReason();
                        Intent intent = new Intent(MyRewardActivity.this, BrowserActivity.class);
                        intent.putExtra("flag", BrowserActivity.flag_fold);
                        intent.putExtra("title", "审核结果");
                        intent.putExtra("content", reason);
                        startActivity(intent);
                    }
                    myRewardAdapter.clearClick();
                }
            }
        });
        myreward_listview_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyRewardInfo myRewardInfo = list.get(position - 1);
                if (myRewardAdapter != null) {
                    if (myRewardAdapter.isClick2()) {//TODO
                        if (myRewardInfo.getType().equals("1")) {//普通
                            Intent intent = new Intent(MyRewardActivity.this, TaskDistActivity.class);
                            intent.putExtra("id", myRewardInfo.getProject_id());
                            intent.putExtra("project_name", myRewardInfo.getProjectName());
                            intent.putExtra("project_code", myRewardInfo.getProject_code());
                            intent.putExtra("is_record", myRewardInfo.getIs_record() + "");
                            intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                            intent.putExtra("begin_date", myRewardInfo.getBegin_date());
                            intent.putExtra("end_date", myRewardInfo.getEnd_date());
                            intent.putExtra("is_watermark", Tools.StringToInt(myRewardInfo.getIs_watermark() + ""));
                            intent.putExtra("code", myRewardInfo.getCode());
                            intent.putExtra("brand", myRewardInfo.getBrand());
                            intent.putExtra("is_takephoto", myRewardInfo.getIs_takephoto() + "");
                            intent.putExtra("type", myRewardInfo.getType() + "");
                            intent.putExtra("check_time", myRewardInfo.getCheck_time());
                            intent.putExtra("project_property", myRewardInfo.getProject_property());
                            intent.putExtra("type", myRewardInfo.getType());
                            startActivity(intent);
                        } else if (myRewardInfo.getType().equals("2")) {//暗访
                            Intent intent = new Intent(MyRewardActivity.this, BlackDZXListActivity.class);
                            intent.putExtra("id", myRewardInfo.getProject_id());
                            intent.putExtra("project_name", myRewardInfo.getProjectName());
                            intent.putExtra("project_code", myRewardInfo.getProject_code());
                            intent.putExtra("is_record", myRewardInfo.getIs_record() + "");
                            intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                            intent.putExtra("begin_date", myRewardInfo.getBegin_date());
                            intent.putExtra("end_date", myRewardInfo.getEnd_date());
                            intent.putExtra("is_watermark", Tools.StringToInt(myRewardInfo.getIs_watermark() + ""));
                            intent.putExtra("code", myRewardInfo.getCode());
                            intent.putExtra("brand", myRewardInfo.getBrand());
                            intent.putExtra("is_takephoto", myRewardInfo.getIs_takephoto() + "");
                            intent.putExtra("type", myRewardInfo.getType() + "");
                            intent.putExtra("check_time", myRewardInfo.getCheck_time());
                            intent.putExtra("project_property", myRewardInfo.getProject_property());
                            intent.putExtra("type", myRewardInfo.getType());
                            startActivity(intent);
                        } else if (myRewardInfo.getType().equals("3")) {//明访
                            Intent intent = new Intent(MyRewardActivity.this, TaskDistActivity.class);
                            intent.putExtra("id", myRewardInfo.getProject_id());
                            intent.putExtra("project_name", myRewardInfo.getProjectName());
                            intent.putExtra("project_code", myRewardInfo.getProject_code());
                            intent.putExtra("is_record", myRewardInfo.getIs_record() + "");
                            intent.putExtra("photo_compression", myRewardInfo.getPhoto_compression());
                            intent.putExtra("begin_date", myRewardInfo.getBegin_date());
                            intent.putExtra("end_date", myRewardInfo.getEnd_date());
                            intent.putExtra("is_watermark", Tools.StringToInt(myRewardInfo.getIs_watermark() + ""));
                            intent.putExtra("code", myRewardInfo.getCode());
                            intent.putExtra("brand", myRewardInfo.getBrand());
                            intent.putExtra("is_takephoto", myRewardInfo.getIs_takephoto() + "");
                            intent.putExtra("type", myRewardInfo.getType() + "");
                            intent.putExtra("check_time", myRewardInfo.getCheck_time());
                            intent.putExtra("project_property", myRewardInfo.getProject_property());
                            intent.putExtra("type", myRewardInfo.getType());
                            startActivity(intent);
                        }
                    } else if (myRewardAdapter.isClick1()) {
                        String reason = myRewardAdapter.getReason();
                        Intent intent = new Intent(MyRewardActivity.this, BrowserActivity.class);
                        intent.putExtra("flag", BrowserActivity.flag_fold);
                        intent.putExtra("title", "审核结果");
                        intent.putExtra("content", reason);
                        startActivity(intent);
                    } else if (myRewardAdapter.isClick3()) {
                        Intent intent = new Intent(MyRewardActivity.this, MyaccountActivity.class);
                        startActivity(intent);
                    }
                    myRewardAdapter.clearClick();
                }
            }
        });
        myreward_listview_four.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (myRewardAdapter != null) {
                    if (myRewardAdapter.isClick3()) {
                        Intent intent = new Intent(MyRewardActivity.this, MyaccountActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void refreshFourData() {
        state = "3";
        getData();
    }

    private void refreshThereData() {
        state = "2";
        getData();
    }

    private void refreshTwoData() {
        state = "1";
        getData();
    }

    private void refreshOneData() {
        state = "0";
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getData() {
        myReward.sendPostRequest(Urls.MyReward, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                if (list == null) {
                    list = new ArrayList<MyRewardInfo>();
                } else {
                    if (page == 1) {
                        list.clear();
                    }
                }
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("datas");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        MyRewardInfo myRewardInfo = new MyRewardInfo();
                        myRewardInfo.setId(object.getString("id"));
                        myRewardInfo.setOutletId(object.getString("outletId"));
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
                        int size = jsonObject.optInt("size");
                        int shade = jsonObject.optInt("shade");
                        AppInfo.setNewTask(MyRewardActivity.this, size, shade);
                        list.add(myRewardInfo);
                    }
                    if (myRewardAdapter != null) {
                        myRewardAdapter.notifyDataSetChanged();
                    }
                    if (state.equals("0")) {
                        myreward_listview_one.onRefreshComplete();
                        if (jsonArray.length() < 15) {
                            myreward_listview_one.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            myreward_listview_one.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    } else if (state.equals("1")) {
                        myreward_listview_two.onRefreshComplete();
                        if (jsonArray.length() < 15) {
                            myreward_listview_two.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            myreward_listview_two.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    } else if (state.equals("2")) {
                        myreward_listview_three.onRefreshComplete();
                        if (jsonArray.length() < 15) {
                            myreward_listview_three.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            myreward_listview_three.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    } else if (state.equals("3")) {
                        myreward_listview_four.onRefreshComplete();
                        if (jsonArray.length() < 15) {
                            myreward_listview_four.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            myreward_listview_four.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyRewardActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                if (state.equals("0")) {
                    myreward_listview_one.onRefreshComplete();
                } else if (state.equals("1")) {
                    myreward_listview_two.onRefreshComplete();
                } else if (state.equals("2")) {
                    myreward_listview_three.onRefreshComplete();
                } else if (state.equals("3")) {
                    myreward_listview_four.onRefreshComplete();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyRewardActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
                if (state.equals("0")) {
                    myreward_listview_one.onRefreshComplete();
                } else if (state.equals("1")) {
                    myreward_listview_two.onRefreshComplete();
                } else if (state.equals("2")) {
                    myreward_listview_three.onRefreshComplete();
                } else if (state.equals("3")) {
                    myreward_listview_four.onRefreshComplete();
                }
            }
        }, null);
    }

    public void initView() {
        myreward_one = (TextView) findViewById(R.id.myreward_one);
        myreward_two = (TextView) findViewById(R.id.myreward_two);
        myreward_three = (TextView) findViewById(R.id.myreward_three);
        myreward_four = (TextView) findViewById(R.id.myreward_four);
        myreward_viewone = findViewById(R.id.myreward_viewone);
        myreward_viewtwo = findViewById(R.id.myreward_viewtwo);
        myreward_viewthree = findViewById(R.id.myreward_viewthree);
        myreward_viewfour = findViewById(R.id.myreward_viewfour);
        myreward_listview_search = (PullToRefreshListView) findViewById(R.id.myreward_listview_search);
        myreward_listview_one = (PullToRefreshListView) findViewById(R.id.myreward_listview_one);
        myreward_listview_two = (PullToRefreshListView) findViewById(R.id.myreward_listview_two);
        myreward_listview_three = (PullToRefreshListView) findViewById(R.id.myreward_listview_three);
        myreward_listview_four = (PullToRefreshListView) findViewById(R.id.myreward_listview_four);
    }

    public void initListView(PullToRefreshListView listView) {
        listView.setMode(PullToRefreshListView.Mode.PULL_FROM_START);
        listView.setPullLabel("下拉刷新");
        listView.setRefreshingLabel("正在刷新");
        listView.setReleaseLabel("释放刷新");
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myreward_one: {
                if (list != null || !list.isEmpty()) {
                    list.clear();
                }
                state = "0";
                page = 1;
                getData();
                myreward_viewone.setVisibility(View.VISIBLE);
                myreward_viewtwo.setVisibility(View.INVISIBLE);
//                myreward_viewthree.setVisibility(View.INVISIBLE);
                myreward_viewfour.setVisibility(View.INVISIBLE);//下划线
                myreward_one.setTextColor(getResources().getColor(R.color.myreward_one));
                myreward_two.setTextColor(getResources().getColor(R.color.myreward_two));
                myreward_three.setTextColor(getResources().getColor(R.color.myreward_two));
                myreward_four.setTextColor(getResources().getColor(R.color.myreward_two));//标题颜色
                myreward_listview_one.setVisibility(View.VISIBLE);
                myreward_listview_two.setVisibility(View.GONE);
                myreward_listview_three.setVisibility(View.GONE);
                myreward_listview_four.setVisibility(View.GONE);//listview
                myreward_listview_search.setVisibility(View.GONE);
            }
            break;
            case R.id.myreward_two: {
                if (list != null || !list.isEmpty()) {
                    list.clear();
                }
                state = "1";
                page = 1;
                getData();
                myreward_viewone.setVisibility(View.INVISIBLE);
                myreward_viewtwo.setVisibility(View.VISIBLE);
//                myreward_viewthree.setVisibility(View.INVISIBLE);
                myreward_viewfour.setVisibility(View.INVISIBLE);//下划线
                myreward_one.setTextColor(getResources().getColor(R.color.myreward_two));
                myreward_two.setTextColor(getResources().getColor(R.color.myreward_one));
                myreward_three.setTextColor(getResources().getColor(R.color.myreward_two));
                myreward_four.setTextColor(getResources().getColor(R.color.myreward_two));//标题颜色
                myreward_listview_one.setVisibility(View.GONE);
                myreward_listview_two.setVisibility(View.VISIBLE);
                myreward_listview_three.setVisibility(View.GONE);
                myreward_listview_four.setVisibility(View.GONE);//listview
                myreward_listview_search.setVisibility(View.GONE);
            }
            break;
//            case R.id.myreward_three: {
//                if (list != null || !list.isEmpty()) {
//                    list.clear();
//                }
//                state = "2";
//                page = 1;
//                getData();
//                myreward_viewone.setVisibility(View.INVISIBLE);
//                myreward_viewtwo.setVisibility(View.INVISIBLE);
////                myreward_viewthree.setVisibility(View.VISIBLE);
//                myreward_viewfour.setVisibility(View.INVISIBLE);//下划线
//                myreward_one.setTextColor(getResources().getColor(R.color.myreward_two));
//                myreward_two.setTextColor(getResources().getColor(R.color.myreward_two));
//                myreward_three.setTextColor(getResources().getColor(R.color.myreward_one));
//                myreward_four.setTextColor(getResources().getColor(R.color.myreward_two));//标题颜色
//                myreward_listview_one.setVisibility(View.GONE);
//                myreward_listview_two.setVisibility(View.GONE);
//                myreward_listview_three.setVisibility(View.VISIBLE);
//                myreward_listview_four.setVisibility(View.GONE);//listview
//                myreward_listview_search.setVisibility(View.GONE);
//            }
//            break;
            case R.id.myreward_four: {
                if (list != null || !list.isEmpty()) {
                    list.clear();
                }
                state = "3";
                page = 1;
                getData();
                myreward_viewone.setVisibility(View.INVISIBLE);
                myreward_viewtwo.setVisibility(View.INVISIBLE);
//                myreward_viewthree.setVisibility(View.INVISIBLE);
                myreward_viewfour.setVisibility(View.VISIBLE);//下划线
                myreward_one.setTextColor(getResources().getColor(R.color.myreward_two));
                myreward_two.setTextColor(getResources().getColor(R.color.myreward_two));
                myreward_three.setTextColor(getResources().getColor(R.color.myreward_two));
                myreward_four.setTextColor(getResources().getColor(R.color.myreward_one));//标题颜色
                myreward_listview_one.setVisibility(View.GONE);
                myreward_listview_two.setVisibility(View.GONE);
                myreward_listview_three.setVisibility(View.GONE);
                myreward_listview_four.setVisibility(View.VISIBLE);//listview
                myreward_listview_search.setVisibility(View.GONE);
            }
            break;
        }
    }
}
