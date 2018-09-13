package com.orange.oy.activity.experience;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.CommentSelectAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.QuestionListInfo;
import com.orange.oy.info.StoreInfo;
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
 * 体验项目---评选页面~~~
 */
public class CommentSelectActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.commitselect_title);
        appTitle.settingName("评选");
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            @Override
            public void onBack() {
                ConfirmDialog.showDialog(CommentSelectActivity.this, "您的体验任务已完成，确定返回首页吗？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                    @Override
                    public void leftClick(Object object) {

                    }

                    @Override
                    public void rightClick(Object object) {
                        baseFinish();
                    }
                });
            }
        }, "首页");
    }

    @Override
    public void onBackPressed() {
        ConfirmDialog.showDialog(CommentSelectActivity.this, "您的体验任务已完成，确定返回首页吗？", true, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {

            }

            @Override
            public void rightClick(Object object) {
                CommentSelectActivity.super.onBackPressed();
            }
        });
    }

    private void initNetworkConnection() {
        selection = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CommentSelectActivity.this));
                params.put("projectid", projectid);
                return params;
            }
        };
        selectionComplete = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CommentSelectActivity.this));
                params.put("projectid", projectid);
                params.put("result", result.toString());
                return params;
            }
        };
        selectionComplete.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (selection != null) {
            selection.stop(Urls.Selection);
        }
        if (selectionComplete != null) {
            selectionComplete.stop(Urls.SelectionComplete);
        }
    }

    private CommentSelectAdapter commentSelectAdapter;
    private PullToRefreshListView commitselect_listview;
    private NetworkConnection selection, selectionComplete;
    private String projectid;
    private ArrayList<QuestionListInfo> questionlists = new ArrayList<>();
    private ArrayList<StoreInfo> storeinfos = new ArrayList<>();
    private JSONObject result = new JSONObject();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_select);
        initTitle();
        projectid = getIntent().getStringExtra("projectid");
        initNetworkConnection();
        commitselect_listview = (PullToRefreshListView) findViewById(R.id.commitselect_listview);
        commentSelectAdapter = new CommentSelectAdapter(this, questionlists, storeinfos);
        commitselect_listview.setAdapter(commentSelectAdapter);
        findViewById(R.id.commitselect_carry).setOnClickListener(this);
        findViewById(R.id.commitselect_finish).setOnClickListener(this);
        getData();
    }

    private void getData() {
        selection.sendPostRequest(Urls.Selection, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray1 = jsonObject.optJSONArray("storeinfo");
                        if (jsonArray1 != null) {
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                StoreInfo storeInfo = new StoreInfo();
                                storeInfo.setPhotoUrl(object.getString("photoUrl"));
                                storeInfo.setStoreid(object.getString("storeid"));
                                storeInfo.setStoreName(object.getString("storeName"));
                                storeinfos.add(storeInfo);
                            }
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("questionlist");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                QuestionListInfo questionListInfo = new QuestionListInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                questionListInfo.setQuestion(object.getString("question"));
                                questionListInfo.setNum(object.getString("num"));
                                questionListInfo.setSelectionId(object.getString("selectionId"));
                                questionListInfo.setStoreinfos(storeinfos);
                                questionlists.add(questionListInfo);
                            }
                        }
                        if (commentSelectAdapter != null) {
                            commentSelectAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(CommentSelectActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(CommentSelectActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CommentSelectActivity.this, getResources().getString(R.string.network_volleyerror));
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
            case R.id.commitselect_finish: {
                sendData();
            }
            break;
            case R.id.commitselect_carry: {
                Intent intent = new Intent(CommentSelectActivity.this, RecommendExperienceActivity.class);
                intent.putExtra("projectid", projectid);
                startActivity(intent);
                baseFinish();
            }
            break;
        }
    }

    private void sendData() {
        try {
            if (commentSelectAdapter != null) {
                JSONArray jsonArray = commentSelectAdapter.getJsonArray();
                if (jsonArray == null) {
                    Tools.showToast(this, "所有题目均为必填！");
                    return;
                }
                try {
                    result.put("result", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Tools.showToast(this, "所有题目均为必填！");
        }
        selectionComplete.sendPostRequest(Urls.SelectionComplete, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        baseFinish();
                    } else {
                        Tools.showToast(CommentSelectActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }
}
