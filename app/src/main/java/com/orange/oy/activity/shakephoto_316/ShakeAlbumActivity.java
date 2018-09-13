package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.LeftActivity;
import com.orange.oy.activity.shakephoto_318.LocalAlbumActivity;
import com.orange.oy.activity.shakephoto_318.SponsorActivity;
import com.orange.oy.adapter.mycorps_314.ShakeAlbumAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.shakephoto.ShakeAlbumInfo;
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
 * 相册查看==我参与的活动 V3.16
 */
public class ShakeAlbumActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.shakealbum_title);
        appTitle.settingName("相册");
        appTitle.showBack(this);
        appTitle.settingExit("甩吧相册", new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Intent intent = new Intent(ShakeAlbumActivity.this, LeftActivity.class);
                intent.putExtra("dai_id", getIntent().getStringExtra("dai_id"));
                startActivity(intent);
            }
        });
    }

    private void initNetwork() {
        allActivitiy = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ShakeAlbumActivity.this));
                params.put("token", Tools.getToken());
                if (!TextUtils.isEmpty(keyword)) {
                    params.put("keyword", keyword);
                }
                params.put("page", page + "");
                params.put("type", "1");//App传1，小程序传2【必传】
                params.put("is_join", is_join);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (allActivitiy != null) {
            allActivitiy.stop(Urls.AllActivitiy);
        }
    }

    private PullToRefreshListView shakealbum_listview1, shakealbum_listview2;//我参与的活动 全部活动
    private ShakeAlbumAdapter shakeAlbumAdapter1, shakeAlbumAdapter2;//我参与的活动
    private TextView shakealbum_join, shakealbum_all;
    private NetworkConnection allActivitiy;
    private EditText shakealbum_edit;
    private String keyword;
    private int page = 1;
    private TextView shakealbum_money, shakealbum_account;
    private ArrayList<ShakeAlbumInfo> list;
    private String is_join = "0";//是否是参与的活动，1为是，0为否
    private View shakealbum_ly1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_album);
        list = new ArrayList<>();
        initTitle();
        initView();
        initNetwork();
        shakealbum_ly1 = findViewById(R.id.shakealbum_ly1);
//        shakeAlbumAdapter1 = new ShakeAlbumAdapter(this, list, true);
        shakealbum_listview1.setAdapter(shakeAlbumAdapter1);
//        shakeAlbumAdapter2 = new ShakeAlbumAdapter(this, list, false);
        shakealbum_listview2.setAdapter(shakeAlbumAdapter2);
        shakealbum_listview1.setMode(PullToRefreshBase.Mode.BOTH);
        shakealbum_listview2.setMode(PullToRefreshBase.Mode.BOTH);

        shakealbum_join.setOnClickListener(this);
        shakealbum_all.setOnClickListener(this);
        onItemClick();
        onRefresh();
        shakealbum_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = shakealbum_edit.getText().toString();
                    allActivitiy.stop(Urls.AllActivitiy);
                    getData();
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.shakealbum_redpackage).setOnClickListener(this);
        findViewById(R.id.shakealbum_local).setOnClickListener(this);
        View shakealbum_all = findViewById(R.id.shakealbum_all);
        onClick(shakealbum_all);
    }

    private void onRefresh() {
        shakealbum_listview1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                keyword = "";
                shakealbum_edit.setText("");
                page = 1;
                is_join = "1";
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                is_join = "1";
                getData();
            }
        });
        shakealbum_listview2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                keyword = "";
                shakealbum_edit.setText("");
                page = 1;
                is_join = "0";
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                is_join = "0";
                getData();
            }
        });
    }

    private void initView() {
        shakealbum_join = (TextView) findViewById(R.id.shakealbum_join);
        shakealbum_all = (TextView) findViewById(R.id.shakealbum_all);
        shakealbum_money = (TextView) findViewById(R.id.shakealbum_money);
        shakealbum_account = (TextView) findViewById(R.id.shakealbum_account);
        shakealbum_listview1 = (PullToRefreshListView) findViewById(R.id.shakealbum_listview1);
        shakealbum_listview2 = (PullToRefreshListView) findViewById(R.id.shakealbum_listview2);
        shakealbum_edit = (EditText) findViewById(R.id.shakealbum_edit);
    }

    //我参与的活动
    private void getData() {
        allActivitiy.sendPostRequest(Urls.AllActivitiy, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            list.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        if ("1".equals(is_join)) {
                            shakealbum_account.setText(jsonObject.getString("photo_num"));
                            shakealbum_money.setText("¥" + Tools.removePoint(jsonObject.getString("get_redpack")));
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        shakealbum_listview1.onRefreshComplete();
                        shakealbum_listview2.onRefreshComplete();
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                ShakeAlbumInfo shakeAlbumInfo = new ShakeAlbumInfo();
                                shakeAlbumInfo.setActivity_name(object.getString("activity_name"));
                                shakeAlbumInfo.setAi_id(object.getString("ai_id"));
                                shakeAlbumInfo.setPhoto_url(object.getString("photo_url"));
                                shakeAlbumInfo.setInitiator(object.getString("initiator"));
                                shakeAlbumInfo.setLeft_target(object.getString("left_target"));
                                shakeAlbumInfo.setLeft_time(object.getString("left_time"));
                                shakeAlbumInfo.setPrize(object.getString("prize"));
                                shakeAlbumInfo.setSponsor_num(object.getString("sponsor_num"));
                                shakeAlbumInfo.setIs_join(object.getString("is_join"));
                                shakeAlbumInfo.setRedpack_state(object.getString("redpack_state"));
                                shakeAlbumInfo.setActivity_status(object.getString("activity_status"));
                                list.add(shakeAlbumInfo);
                            }
                            if (jsonArray.length() == 0) {
                                if (shakealbum_ly1.getVisibility() == View.VISIBLE) {
                                    shakealbum_listview1.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    shakealbum_listview2.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                }
                            } else {
                                if (shakealbum_ly1.getVisibility() == View.VISIBLE) {
                                    shakealbum_listview1.setMode(PullToRefreshBase.Mode.BOTH);
                                } else {
                                    shakealbum_listview2.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                            }
                            if (shakeAlbumAdapter1 != null) {
                                shakeAlbumAdapter1.notifyDataSetChanged();
                            }
                            if (shakeAlbumAdapter2 != null) {
                                shakeAlbumAdapter2.notifyDataSetChanged();
                            }
                        } else {
                            if (shakealbum_listview1.getVisibility() == View.VISIBLE) {
                                shakealbum_listview1.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                shakealbum_listview2.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                        }
                    } else {
                        Tools.showToast(ShakeAlbumActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ShakeAlbumActivity.this, getResources().getString(R.string.network_error));
                }
                shakealbum_listview1.onRefreshComplete();
                shakealbum_listview2.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ShakeAlbumActivity.this, getResources().getString(R.string.network_volleyerror));
                shakealbum_listview1.onRefreshComplete();
                shakealbum_listview2.onRefreshComplete();
            }
        });
    }


    private void onItemClick() {
//        shakealbum_listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (shakeAlbumAdapter1 != null) {
//                    final ShakeAlbumInfo shakeAlbumInfo = list.get(position - 1);
//                    final String ai_id = shakeAlbumInfo.getAi_id();
//                    if (shakeAlbumAdapter1.isClick1()) {//邀请
//                        inviteFriends(ai_id);
//                    } else if (shakeAlbumAdapter1.isClick2()) {//拆红包
//                        if ("1".equals(shakeAlbumInfo.getRedpack_state()) || "2".equals(shakeAlbumInfo.getRedpack_state())) {
//                            Intent intent = new Intent(ShakeAlbumActivity.this, OpenRedpackageActivity.class);
//                            intent.putExtra("ai_id", ai_id);
//                            startActivity(intent);
//                        } else {
//                            UMShareDialog.showDialog(ShakeAlbumActivity.this, false, new UMShareDialog.UMShareListener() {
//                                public void shareOnclick(int type) {
//                                    String webUrl = Urls.ShareActivityIndex + "?ai_id=" + ai_id + "&type=1";
//                                    MyUMShareUtils.umShare(ShakeAlbumActivity.this, type, webUrl);
//                                }
//                            });
//                        }
//                    } else if (shakeAlbumAdapter1.isClick3()) {//赞助
//                        sponsorActivity(ai_id);
//                    } else if (shakeAlbumAdapter1.isClick4()) {//活动大奖
//                        Intent intent = new Intent(ShakeAlbumActivity.this, AwardsShowActivity.class);
//                        intent.putExtra("ai_id", ai_id);
//                        startActivity(intent);
//                    } else if (shakeAlbumAdapter1.isClick5()) {//赞助人详情
//                        Intent intent = new Intent(ShakeAlbumActivity.this, SeconderDesActivity.class);
//                        intent.putExtra("ai_id", ai_id);
//                        startActivity(intent);
//                    } else {//查看详情
//                        Intent intent = new Intent(ShakeAlbumActivity.this, ThemeDetailActivity.class);
//                        intent.putExtra("is_join", shakeAlbumInfo.getIs_join());
//                        intent.putExtra("ai_id", shakeAlbumInfo.getAi_id());
//                        intent.putExtra("acname", shakeAlbumInfo.getActivity_name());
//                        startActivityForResult(intent, jump_themedetail);
//                    }
//                    shakeAlbumAdapter1.clearClick();
//                }
//            }
//        });
//        shakealbum_listview1.setOnScrollListener(new AbsListView.OnScrollListener() {
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (shakeAlbumAdapter1 != null)
//                    shakeAlbumAdapter1.clearClick();
//            }
//
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            }
//        });
//        shakealbum_listview2.setOnScrollListener(new AbsListView.OnScrollListener() {
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (shakeAlbumAdapter2 != null)
//                    shakeAlbumAdapter2.clearClick();
//            }
//
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            }
//        });
//        shakealbum_listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (shakeAlbumAdapter2 != null) {
//                    ShakeAlbumInfo shakeAlbumInfo = list.get(position - 1);
//                    String ai_id = shakeAlbumInfo.getAi_id();
//                    if (shakeAlbumAdapter2.isClick1()) {//邀请
//                        inviteFriends(ai_id);
//                    } else if (shakeAlbumAdapter2.isClick3()) {//赞助
//                        sponsorActivity(ai_id);
//                    } else if (shakeAlbumAdapter2.isClick4()) {//活动大奖
//                        Intent intent = new Intent(ShakeAlbumActivity.this, AwardsShowActivity.class);
//                        intent.putExtra("ai_id", ai_id);
//                        startActivity(intent);
//                    } else if (shakeAlbumAdapter2.isClick5()) {//赞助人详情
//                        Intent intent = new Intent(ShakeAlbumActivity.this, SeconderDesActivity.class);
//                        intent.putExtra("ai_id", ai_id);
//                        startActivity(intent);
//                    } else {//查看详情
//                        Intent intent = new Intent(ShakeAlbumActivity.this, ThemeDetailActivity.class);
//                        intent.putExtra("is_join", shakeAlbumInfo.getIs_join());
//                        intent.putExtra("ai_id", shakeAlbumInfo.getAi_id());
//                        intent.putExtra("acname", shakeAlbumInfo.getActivity_name());
//                        startActivityForResult(intent, jump_themedetail);
//                    }
//                    shakeAlbumAdapter2.clearClick();
//                }
//            }
//        });
    }

    public static final int jump_themedetail = 0x10;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == jump_themedetail && resultCode == AppInfo.RESULT_MAIN_SHOWSHAKEPHOTO_AI) {
            setResult(AppInfo.RESULT_MAIN_SHOWSHAKEPHOTO_AI, data);
            baseFinish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sponsorActivity(String ai_id) {
        Intent intent = new Intent(this, SponsorActivity.class);
        intent.putExtra("ai_id", ai_id);
        startActivity(intent);
    }

    private void inviteFriends(final String ai_id) {
        UMShareDialog.showDialog(ShakeAlbumActivity.this, false, new UMShareDialog.UMShareListener() {
            public void shareOnclick(int type) {
                String webUrl = Urls.ShareActivityIndex + "?ai_id=" + ai_id + "&type=1";
                MyUMShareUtils.umShare_shakephoto(ShakeAlbumActivity.this, type, webUrl);
            }
        });
    }

    public static boolean isRefresh = false;

    protected void onResume() {
        super.onResume();
        if (isRefresh) {//拆完红包之后刷新
            if ("1".equals(is_join)) {
                if (!list.isEmpty()) {
                    list.clear();
                }
                if (shakeAlbumAdapter2 != null) {
                    shakeAlbumAdapter2.notifyDataSetChanged();
                }
                page = 1;
                getData();
            }
        }
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shakealbum_join: {//我参加的活动
                is_join = "1";
                if (!list.isEmpty()) {
                    list.clear();
                }
                page = 1;
                getData();
                shakealbum_join.setTextColor(getResources().getColor(R.color.homepage_select));
                shakealbum_all.setTextColor(getResources().getColor(R.color.homepage_notselect));
                findViewById(R.id.shakealbum_line1).setVisibility(View.INVISIBLE);
                findViewById(R.id.shakealbum_line2).setVisibility(View.VISIBLE);
                findViewById(R.id.shakealbum_ly1).setVisibility(View.VISIBLE);
                findViewById(R.id.shakealbum_ly2).setVisibility(View.GONE);
            }
            break;
            case R.id.shakealbum_all: {//全部活动
                is_join = "0";
                if (!list.isEmpty()) {
                    list.clear();
                }
                page = 1;
                getData();
                shakealbum_join.setTextColor(getResources().getColor(R.color.homepage_notselect));
                shakealbum_all.setTextColor(getResources().getColor(R.color.homepage_select));
                shakealbum_listview2.setVisibility(View.VISIBLE);
                findViewById(R.id.shakealbum_line1).setVisibility(View.VISIBLE);
                findViewById(R.id.shakealbum_line2).setVisibility(View.INVISIBLE);
                findViewById(R.id.shakealbum_ly1).setVisibility(View.GONE);
                findViewById(R.id.shakealbum_ly2).setVisibility(View.VISIBLE);
            }
            break;
            case R.id.shakealbum_local: {
                startActivity(new Intent(this, LocalAlbumActivity.class));
            }
            break;
            case R.id.shakealbum_redpackage: {
                startActivity(new Intent(this, MyRedPackageActivity.class));
            }
            break;
        }
    }
}
