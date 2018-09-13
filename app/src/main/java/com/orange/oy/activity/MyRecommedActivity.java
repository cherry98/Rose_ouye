package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.alipay.OuMiDetailActivity;
import com.orange.oy.adapter.MyRecommedAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.info.MyRecommendInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyRecommedActivity extends BaseActivity {

    private void initNetworkConnection() {
        myInvite = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MyRecommedActivity.this));
                params.put("token", Tools.getToken());
                params.put("page", page + "");
                params.put("keyword", myrecommend_edittext.getText().toString());
                return params;
            }
        };
    }

//    private TextView myrecommend_remind;
    private PullToRefreshListView myrecommend_listview;
    private MyRecommedAdapter myRecommedAdapter;
    private NetworkConnection myInvite;
    private EditText myrecommend_edittext;
    private int page = 1;
    private ArrayList<MyRecommendInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recommed);
        initNetworkConnection();
        list = new ArrayList<>();
//        myrecommend_remind = (TextView) findViewById(R.id.myrecommend_remind);
        myrecommend_listview = (PullToRefreshListView) findViewById(R.id.myrecommend_listview);
        myrecommend_edittext = (EditText) findViewById(R.id.myrecommend_edittext);
//        String str2 = "“偶米数”";
//        SpannableString ss = new SpannableString(str2);
//        MyClickableSpan myClickableSpan = new MyClickableSpan();
//        ss.setSpan(myClickableSpan, 0, str2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        myrecommend_remind.setText("偶米奖励可在");
//        myrecommend_remind.append(ss);
//        myrecommend_remind.append("查看及兑换");
//        myrecommend_remind.setMovementMethod(LinkMovementMethod.getInstance());
        findViewById(R.id.myrecommend_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseFinish();
            }
        });
        myRecommedAdapter = new MyRecommedAdapter(this, list);
        myrecommend_listview.setAdapter(myRecommedAdapter);
        myrecommend_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        getData();
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private void getData() {
        myInvite.sendPostRequest(Urls.MyInvite, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<MyRecommendInfo>();
                            page = 1;
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                MyRecommendInfo myRecommendInfo = new MyRecommendInfo();
                                myRecommendInfo.setUsermobile(object.getString("usermobile"));
                                myRecommendInfo.setTime(object.getString("time"));
                                myRecommendInfo.setIsreward(object.getString("isreward"));
                                myRecommendInfo.setOmnum(object.getString("omnum"));
                                list.add(myRecommendInfo);
                            }
                            myrecommend_listview.onRefreshComplete();
                            if (length < 15) {
                                myrecommend_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                myrecommend_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            myRecommedAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(MyRecommedActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyRecommedActivity.this, getResources().getString(R.string.network_error));
                }
                myrecommend_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                myrecommend_listview.onRefreshComplete();
                Tools.showToast(MyRecommedActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

//    class MyClickableSpan extends ClickableSpan {
//        //在这里设置字体的大小，等待各种属性
//        public void updateDrawState(TextPaint ds) {
//            ds.setColor(Color.BLUE);
//        }
//
//        @Override
//        public void onClick(View widget) {
//            Intent intent = new Intent(MyRecommedActivity.this, OuMiDetailActivity.class);
//            startActivity(intent);
//        }
//    }

}
