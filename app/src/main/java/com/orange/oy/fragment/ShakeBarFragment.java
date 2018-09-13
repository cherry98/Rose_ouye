package com.orange.oy.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.NewCommentActivity;
import com.orange.oy.activity.shakephoto_318.OpenRedpackageActivity;
import com.orange.oy.activity.shakephoto_318.ShakephotoActivity;
import com.orange.oy.activity.shakephoto_318.ThemeDetailActivity;
import com.orange.oy.adapter.mycorps_314.ShakeAlbumAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.ShakeAlbumInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.OnDoubleClickListener;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * 首页甩吧页面 V3.19
 */
public class ShakeBarFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        AppTitle.OnExitClickForAppTitle, View.OnClickListener, OnDoubleClickListener.DoubleClickCallback {


    public ShakeBarFragment() {
        // Required empty public constructor
    }

    private void initView(View inflate) {
        AppTitle appTitle = (AppTitle) inflate.findViewById(R.id.shakebar_title);
        appTitle.setImageTitle(R.mipmap.shaketitle);
        appTitle.getTitle_name_img().setPadding(0, Tools.dipToPx(getActivity(), 5), 0, Tools.dipToPx(getActivity(), 5));
        appTitle.showIllustrate(R.mipmap.shake_ablum, this);
        appTitle.getTitle_illustrate().setPadding(5, 5, 5, 5);
        appTitle.setOnTouchListener(new OnDoubleClickListener(this));
        shakebar_comment = (TextView) inflate.findViewById(R.id.shakebar_comment);
        shakebar_edit = (EditText) inflate.findViewById(R.id.shakebar_edit);
        shakebar_listview = (PullToRefreshListView) inflate.findViewById(R.id.shakebar_listview);
        shakebar_comment_ly = inflate.findViewById(R.id.shakebar_comment_ly);
        shakebar_comment_ly.setOnClickListener(this);
    }

    private void initNetwork() {
        allActivitiy = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                String usermobile = AppInfo.getName(getContext());
                if (!Tools.isEmpty(usermobile)) {
                    params.put("usermobile", usermobile);
                }
                String token = Tools.getToken();
                if (!Tools.isEmpty(token)) {
                    params.put("token", token);
                }
                if (!TextUtils.isEmpty(keyword)) {
                    params.put("keyword", keyword);
                }
                params.put("page", page + "");
                params.put("type", "1");//App传1，小程序传2【必传】
                return params;
            }
        };
    }

    private TextView shakebar_comment;
    private EditText shakebar_edit;
    private PullToRefreshListView shakebar_listview;
    private NetworkConnection allActivitiy;
    private String keyword;
    private int page = 1;
    private ArrayList<ShakeAlbumInfo> list;
    private ShakeAlbumAdapter albumAdapter;
    private String ai_id;
    private View shakebar_comment_ly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_shake_bar, container, false);
        initView(inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = new ArrayList<>();
        initNetwork();
        shakebar_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = shakebar_edit.getText().toString();
                    allActivitiy.stop(Urls.AllActivitiy);
                    getData();
                    return true;
                }
                return false;
            }
        });
        albumAdapter = new ShakeAlbumAdapter(getContext(), list);
        shakebar_listview.setAdapter(albumAdapter);
        shakebar_listview.setOnItemClickListener(this);
        shakebar_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (albumAdapter != null)
                    albumAdapter.clearClick();
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        shakebar_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        page = 1;
        getData();
    }

    public void onResume() {
        super.onResume();
    }

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
                        if (jsonObject != null) {
                            String comment_num = jsonObject.getString("comment_num");
                            if (!Tools.isEmpty(comment_num) && !"0".equals(comment_num)) {
                                shakebar_comment.setText("有" + comment_num + "条评论");
                                shakebar_comment_ly.setVisibility(View.VISIBLE);
                            } else {
                                shakebar_comment_ly.setVisibility(View.GONE);
                            }
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
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
                                    String prize = object.getString("prize");
                                    if (!TextUtils.isEmpty(prize)) {
                                        if (prize.length() > 7) {
                                            int width = Tools.dipToPx(getActivity(), 102);
                                            TextPaint paint = shakebar_comment.getPaint();
                                            String result = prize.substring(0, 7);
                                            int length = prize.length();
                                            for (int index = 0; index < length; index++) {
                                                int them = (int) paint.measureText(result);
                                                if (them < width) {
                                                    result = result + prize.substring(index + 7, index + 8);
                                                } else {
                                                    break;
                                                }
                                            }
                                            if (result.length() < prize.length()) {
                                                prize = result + "...";
                                            } else {
                                                prize = result;
                                            }
                                        }
                                    } else {
                                        prize = "";
                                    }
                                    shakeAlbumInfo.setPrize(prize);
                                    shakeAlbumInfo.setSponsor_num(object.getString("sponsor_num"));
                                    shakeAlbumInfo.setIs_join(object.getString("is_join"));
                                    shakeAlbumInfo.setRedpack_state(object.getString("redpack_state"));
                                    shakeAlbumInfo.setActivity_status(object.getString("activity_status"));
                                    shakeAlbumInfo.setSponsor_money(object.getString("sponsor_money"));
                                    shakeAlbumInfo.setIs_join(object.getString("is_join"));
                                    list.add(shakeAlbumInfo);
                                }
                                shakebar_listview.onRefreshComplete();
                                if (jsonArray.length() < 15) {
                                    shakebar_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    shakebar_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                if (albumAdapter != null) {
                                    albumAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        shakebar_listview.onRefreshComplete();
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                shakebar_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                shakebar_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShakeAlbumInfo shakeAlbumInfo = list.get(position - 1);
        ai_id = shakeAlbumInfo.getAi_id();
        if (albumAdapter != null) {
            if (albumAdapter.isClick1()) {//拆红包
                Intent intent = new Intent(getContext(), OpenRedpackageActivity.class);
                intent.putExtra("ai_id", ai_id);
                startActivity(intent);
            } else {//详情
                Intent intent = new Intent(getContext(), ThemeDetailActivity.class);
                intent.putExtra("ai_id", ai_id);
                intent.putExtra("acname", shakeAlbumInfo.getActivity_name());
                startActivity(intent);
            }
            albumAdapter.clearClick();
        }
    }

    public void onExit() {
        Intent intent = new Intent(getContext(), ShakephotoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shakebar_comment_ly) {//评论
            Intent intent = new Intent(getContext(), NewCommentActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDoubleClick() {
        ListView listView = shakebar_listview.getRefreshableView();
        if (!listView.isStackFromBottom()) {
            listView.setStackFromBottom(true);
        }
        listView.setStackFromBottom(false);
    }
}
