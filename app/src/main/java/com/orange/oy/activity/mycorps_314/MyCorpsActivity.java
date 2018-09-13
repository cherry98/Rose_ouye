package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.scan.IdentityVerActivity;
import com.orange.oy.adapter.mycorps_314.MyCorpsAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.MyCorpsInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的战队--战队信息
 */
public class MyCorpsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mycorps_title);
        appTitle.settingName("我的战队");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        myTeams = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MyCorpsActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myTeams != null) {
            myTeams.stop(Urls.MyTeams);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private NetworkConnection myTeams;
    private ArrayList<MyCorpsInfo> build_list, join_list;
    private ListView create_listview, apply_listview;
    private View mycrops_create_ly, mycrops_apply_ly;
    private View mycrops_create, mycrops_apply;
    private String bindidcard;//是否进行账号认证
    private MyCorpsAdapter myCorpsAdapter1, myCorpsAdapter2;
    private PullToRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_crops);
        join_list = new ArrayList<>();
        build_list = new ArrayList<>();
        initTitle();
        initNetwork();
        initView();
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        myCorpsAdapter1 = new MyCorpsAdapter(this, build_list);
        create_listview.setAdapter(myCorpsAdapter1);
        myCorpsAdapter2 = new MyCorpsAdapter(this, join_list);
        apply_listview.setAdapter(myCorpsAdapter2);
        onItemClick();
        refreshLayoutListener();
    }

    private void onItemClick() {
        create_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyCorpsActivity.this, TeamInformationActivity.class);
                intent.putExtra("team_id", build_list.get(position).getTeam_id());
                intent.putExtra("Apply_user_num", build_list.get(position).getApply_user_num());
                startActivity(intent);
            }
        });
        apply_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyCorpsActivity.this, TeamInformationActivity.class);
                intent.putExtra("team_id", join_list.get(position).getTeam_id());
                intent.putExtra("Apply_user_num", join_list.get(position).getApply_user_num());
                startActivity(intent);
            }
        });
    }

    private void refreshLayoutListener() {
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                getData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });
        refreshLayout.setCompleteListener(new PullToRefreshLayout.OnRetreshComplentListener() {
            @Override
            public void OnComplete() {
            }
        });
    }

    private void initView() {
        create_listview = (ListView) findViewById(R.id.create_listview);
        apply_listview = (ListView) findViewById(R.id.apply_listview);
        mycrops_apply_ly = findViewById(R.id.mycrops_apply_ly);
        mycrops_create_ly = findViewById(R.id.mycrops_create_ly);
        mycrops_create = findViewById(R.id.mycrops_create);
        mycrops_apply = findViewById(R.id.mycrops_apply);
    }

    private void getData() {
        myTeams.sendPostRequest(Urls.MyTeams, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!build_list.isEmpty()) {
                            build_list.clear();
                        }
                        if (!join_list.isEmpty()) {
                            join_list.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        String is_build = jsonObject.getString("is_build");//是否可创建战队
                        if ("1".equals(is_build)) {
                            mycrops_create.setVisibility(View.VISIBLE);
                            mycrops_create.setOnClickListener(MyCorpsActivity.this);
                        } else {
                            mycrops_create.setVisibility(View.GONE);
                            mycrops_create.setOnClickListener(null);
                        }
                        String is_join = jsonObject.getString("is_join");//是否可加入战队
                        if ("1".equals(is_join)) {
                            mycrops_apply.setVisibility(View.VISIBLE);
                            mycrops_apply.setOnClickListener(MyCorpsActivity.this);
                        } else {
                            mycrops_apply.setVisibility(View.GONE);
                            mycrops_apply.setOnClickListener(null);
                        }
                        bindidcard = jsonObject.getString("bindidcard");//是否进行身份认证
                        JSONArray jsonArray = jsonObject.optJSONArray("build_list");//组建的战队信息
                        if (jsonArray != null) {
                            if (jsonArray.length() > 0) {
                                create_listview.setVisibility(View.VISIBLE);
                                mycrops_create_ly.setVisibility(View.GONE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    MyCorpsInfo myCorpsInfo = new MyCorpsInfo();
                                    myCorpsInfo.setTeam_id(object.getString("team_id"));
                                    myCorpsInfo.setTeam_img(object.getString("team_img"));
                                    myCorpsInfo.setTeam_name(object.getString("team_name"));
                                    myCorpsInfo.setUser_num(object.getString("user_num"));
                                    myCorpsInfo.setApply_user_num(object.getString("apply_user_num"));
                                    build_list.add(myCorpsInfo);
                                }
                            } else {
                                create_listview.setVisibility(View.GONE);
                                mycrops_create_ly.setVisibility(View.VISIBLE);
                            }
                        } else {
                            create_listview.setVisibility(View.GONE);
                            mycrops_create_ly.setVisibility(View.VISIBLE);
                        }
                        JSONArray jsonArray1 = jsonObject.optJSONArray("join_list");//加入的战队信息
                        if (jsonArray1 != null) {
                            if (jsonArray1.length() > 0) {
                                apply_listview.setVisibility(View.VISIBLE);
                                mycrops_apply_ly.setVisibility(View.GONE);
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject object = jsonArray1.getJSONObject(i);
                                    MyCorpsInfo myCorpsInfo = new MyCorpsInfo();
                                    myCorpsInfo.setTeam_id(object.getString("team_id"));
                                    myCorpsInfo.setTeam_img(object.getString("team_img"));
                                    myCorpsInfo.setTeam_name(object.getString("team_name"));
                                    myCorpsInfo.setUser_num(object.getString("user_num"));
                                    join_list.add(myCorpsInfo);
                                }
                            } else {
                                apply_listview.setVisibility(View.GONE);
                                mycrops_apply_ly.setVisibility(View.VISIBLE);
                            }
                        } else {
                            apply_listview.setVisibility(View.GONE);
                            mycrops_apply_ly.setVisibility(View.VISIBLE);
                        }
                        int size = build_list.size();
                        if (size != 0) {
                            int height = Tools.dipToPx(MyCorpsActivity.this, (50 + 10) * size);
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) create_listview.getLayoutParams();
                            layoutParams.height = height;
                            create_listview.setLayoutParams(layoutParams);
                        }
                        size = join_list.size();
                        if (size != 0) {
                            int height = Tools.dipToPx(MyCorpsActivity.this, (50 + 10) * size);
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) apply_listview.getLayoutParams();
                            layoutParams.height = height;
                            apply_listview.setLayoutParams(layoutParams);
                        }
                        if (myCorpsAdapter1 != null) {
                            myCorpsAdapter1.notifyDataSetChanged();
                        }
                        if (myCorpsAdapter2 != null) {
                            myCorpsAdapter2.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(MyCorpsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyCorpsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyCorpsActivity.this, getResources().getString(R.string.network_volleyerror));
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mycrops_create: {//创建战队
                if ("1".equals(bindidcard)) {
                    Intent intent = new Intent(this, CreateCorpActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, IdentityVerActivity.class));
                }
            }
            break;
            case R.id.mycrops_apply: {
                Intent intent = new Intent(this, JoinCorpActivity.class);
                startActivity(intent);
            }
            break;
        }
    }
}
