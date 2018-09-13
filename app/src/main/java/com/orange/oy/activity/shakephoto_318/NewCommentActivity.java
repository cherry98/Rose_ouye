package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.orange.oy.R;
import com.orange.oy.activity.alipay.OuMiDetailActivity;
import com.orange.oy.adapter.CommentAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.info.NewCommentInfo;
import com.orange.oy.info.shakephoto.PhotoListBean;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.oumidetail_listview;
import static com.orange.oy.R.id.swipemenulib;

/**
 * V3.19 新评论页面
 */
public class NewCommentActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, CommentAdapter.OnItemCheckListener {


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.title);
        appTitle.settingName("新评论");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        newComments = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!Tools.isEmpty(Tools.getToken())) {
                    params.put("token", Tools.getToken());
                }
                if (!Tools.isEmpty(AppInfo.getName(NewCommentActivity.this))) {
                    params.put("usermobile", AppInfo.getName(NewCommentActivity.this));
                }
                return params;
            }
        };
    }

    private ListView comment_listview;
    private NetworkConnection newComments;
    private CommentAdapter commentAdapter;
    private ArrayList<NewCommentInfo> list;

    public static boolean IsRefresh = false; //是否刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        initTitle();
        initNetworkConnection();
        comment_listview = (ListView) findViewById(R.id.comment_listview);
        getData();
        list = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, list);
        comment_listview.setAdapter(commentAdapter);
        commentAdapter.setOnItemCheckListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        list.clear();
//        if (IsRefresh) {
//            getData();
//        }
//        IsRefresh = false;
    }


    private void getData() {
        newComments.sendPostRequest(Urls.NewComments, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<NewCommentInfo>();
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("activitylist");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                NewCommentInfo newCommentInfo = new NewCommentInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                newCommentInfo.setActivity_name(object.getString("activity_name"));
                                newCommentInfo.setAi_id(object.getString("ai_id"));
                                JSONArray jsonArray1 = object.getJSONArray("comments");
                                ArrayList<NewCommentInfo.CommentsBean> commentsBeanArrayList = new ArrayList<>();
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    NewCommentInfo.CommentsBean commentsBean = new NewCommentInfo.CommentsBean();
                                    JSONObject object1 = jsonArray1.getJSONObject(j);
                                    commentsBean.setFile_url(object1.getString("file_url"));
                                    commentsBean.setFi_id(object1.getString("fi_id"));
                                    commentsBean.setCreate_time(object1.getString("create_time"));
                                    commentsBean.setUser_img(object1.getString("user_img"));
                                    commentsBean.setUser_name(object1.getString("user_name"));
                                    commentsBean.setPraise_num(object1.getString("praise_num"));
                                    commentsBean.setPraise_user(object1.getString("praise_user"));
                                    commentsBean.setComment(object1.getString("comment"));
                                    commentsBean.setComment_id(object1.getString("comment_id"));
                                    commentsBean.setActivity_name(object.getString("activity_name"));
                                    commentsBean.getCommentList().add(commentsBean);
                                    commentsBeanArrayList.add(commentsBean);
                                }
                                newCommentInfo.setComments(commentsBeanArrayList);
                                list.add(newCommentInfo);
                            }
                            if (commentAdapter != null) {
                                commentAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(NewCommentActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NewCommentActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NewCommentActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private NewCommentInfo.CommentsBean commentsBean;

    public void onItemReply(NewCommentInfo.CommentsBean commentsBean) {  //回复按钮的点击
        this.commentsBean = commentsBean;
        Intent intent = new Intent(this, CommentDesActivity.class);
        intent.putExtra("commentList", commentsBean.getCommentList());
        startActivityForResult(intent, 0x10);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0x10: {
                if (resultCode == RESULT_OK) {
                    commentsBean.setCommentList((ArrayList<NewCommentInfo.CommentsBean>) data.getSerializableExtra("commentList"));
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (newComments != null) {
            newComments.stop(Urls.NewComments);
        }
        IsRefresh = false;
    }

}
