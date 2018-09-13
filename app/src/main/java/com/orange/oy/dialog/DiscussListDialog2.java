package com.orange.oy.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.LargeImageActivity;
import com.orange.oy.activity.shakephoto_318.ThemeActivity;
import com.orange.oy.adapter.DiscussListAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.CommentListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.DiscussListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.discussView;


/**
 * V3.18查看广告大图 ------评论列表dialog   V3.21修改为view
 */
public class DiscussListDialog2 extends LinearLayout implements DiscussListAdapter.OrlikeClickListener, View.OnClickListener {


    private ArrayList<CommentListInfo> listInfos; //评论列表
    private ImageView iv_dismiss;
    private TextView tv_totalnum, tv_sure;
    private PullToRefreshListView discuss_listview;
    private EditText ed_discuss;
    private DiscussListAdapter adapter;
    private NetworkConnection commentList, commentPhoto, praiseComment, adCommentList, commentAd;
    private int page = 1;

    private String total_num, content;

    private Activity mcontext;


    private static MyDialog myDialog;
    private static DiscussListDialog2 discussListDialog;

    private void initNetworkConnection() {
        // 	图片评论信息页接口
        commentList = new NetworkConnection(mcontext) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(mcontext));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("fi_id", fi_id);
                params.put("page", page + ""); //页码，从1开始（每页15条）
                return params;
            }
        };

        //广告图片评论信息页面
        adCommentList = new NetworkConnection(mcontext) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(mcontext));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("sai_id", sai_id);  //sai_id	广告id【必传】
                params.put("page", page + ""); //页码，从1开始（每页15条）
                return params;
            }
        };
        //	评论点赞接口
        praiseComment = new NetworkConnection(mcontext) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(mcontext));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("comment_id", comment_id); // 评论id【必传】
                params.put("praise", praise); // praise	是否是赞，1为赞，0为不赞
                return params;
            }
        };
        //	图片评论提交接口
        commentPhoto = new NetworkConnection(mcontext) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(mcontext));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("fi_id", fi_id);
                params.put("content", content); //评论的内容【必传】
                return params;
            }
        };
        //	广告图片评论提交接口
        commentAd = new NetworkConnection(mcontext) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(mcontext));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("sai_id", sai_id); // sai_id	广告id【必传】
                params.put("content", content); //评论的内容【必传】
                return params;
            }
        };
    }

    protected void onStop() {
        if (commentList != null) {
            commentList.stop(Urls.CommentList);
        }
        if (commentPhoto != null) {
            commentPhoto.stop(Urls.CommentPhoto);
        }
        if (praiseComment != null) {
            praiseComment.stop(Urls.PraiseComment);
        }
        if (commentAd != null) {
            commentAd.stop(Urls.CommentAd);
        }
        if (adCommentList != null) {
            adCommentList.stop(Urls.AdCommentList);
        }
    }

    public static void SetData(String fi_id1, String sai_id1, String is_advertisement1) {
        fi_id = fi_id1;
        sai_id = sai_id1;
        is_advertisement = is_advertisement1;
    }

    public DiscussListDialog2(Context context) {
        super(context);
        mcontext = (Activity) context;
        Tools.loadLayout(this, R.layout.activity_discuss_list);

        init();
        initNetworkConnection();
        discuss_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                if (!Tools.isEmpty(is_advertisement)) {
                    if (is_advertisement.equals("1")) {
                        getData2();
                    } else if (is_advertisement.equals("0")) {
                        getData();
                    }
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                if (!Tools.isEmpty(is_advertisement)) {
                    if (is_advertisement.equals("1")) {
                        getData2();
                    } else if (is_advertisement.equals("0")) {
                        getData();
                    }
                }
            }
        });
        listInfos = new ArrayList<>();
        adapter = new DiscussListAdapter(mcontext, listInfos);
        discuss_listview.setAdapter(adapter);
        adapter.setOnOrlikeClickListener(this);
        if (!Tools.isEmpty(is_advertisement)) {
            if (is_advertisement.equals("1")) {
                getData2();
            } else if (is_advertisement.equals("0")) {
                getData();
            }
        }
    }

    private void init() {
        iv_dismiss = (ImageView) findViewById(R.id.iv_dismiss);
        tv_totalnum = (TextView) findViewById(R.id.tv_totalnum);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        discuss_listview = (PullToRefreshListView) findViewById(R.id.discuss_listview);
        ed_discuss = (EditText) findViewById(R.id.ed_discuss);
        iv_dismiss.setOnClickListener(this);
        tv_sure.setOnClickListener(this);

        ed_discuss.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editable = ed_discuss.getText().toString();
                String str = Tools.stringFilter(editable);
                if (!editable.equals(str)) {
                    ed_discuss.setText(str);
                    ed_discuss.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static MyDialog showDialog(Context context, boolean cancelable, OnPrizeSettingListener listener) {
        return showDialog(context, cancelable, null, null, null, listener);
    }

    private static String fi_id; //照片id
    private static String sai_id; //广告图片id
    private static String is_advertisement; //是否是广告

    public static MyDialog showDialog(Context context, boolean cancelable, String fi_id, String sai_id, String is_advertisement, OnPrizeSettingListener listener) {
        if (myDialog != null && myDialog.isShowing()) {
            dissmisDialog();
        }
        discussListDialog = new DiscussListDialog2(context);
        discussListDialog.setOnPrizeSettingListener(listener);

        SetData(fi_id, sai_id, is_advertisement);
        myDialog = new MyDialog((BaseActivity) context, discussListDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0);
        return myDialog;
    }

    public static void dissmisDialog() {
        try {
            if (myDialog != null && myDialog.isShowing()) {
                listener.firstClick(num);
                myDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static OnPrizeSettingListener listener;

    public void setOnPrizeSettingListener(OnPrizeSettingListener listener) {
        this.listener = listener;
    }


    public interface OnPrizeSettingListener {
        void firstClick(int num);

        void secondClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //弹框消失
            case R.id.iv_dismiss: {
              /*  Intent intent = new Intent(mcontext, LargeImageActivity.class);
                intent.putExtra("commentSize", listInfos.size());
                mcontext.startActivity(intent);*/
                if (myDialog != null && myDialog.isShowing()) {
                    listener.firstClick(listInfos.size());
                    dissmisDialog();
                }
            }
            break;
            // 评论发送按钮
            case R.id.tv_sure: {
                content = ed_discuss.getText().toString();
                if (Tools.isEmpty(content)) {
                    Tools.showToast(mcontext, "请填写评论内容~");
                    return;
                }
                ed_discuss.setText("");
                if (!Tools.isEmpty(is_advertisement)) {
                    if (is_advertisement.equals("1")) {
                        Comment2();
                    } else if (is_advertisement.equals("0")) {
                        Comment();
                    }
                }

            }
            break;
        }
    }

    private String comment_id, praise;


    @Override
    public void onlike(int pos) { //评论点赞点击
        CommentListInfo commentListInfo = listInfos.get(pos);
        comment_id = commentListInfo.getComment_id();
        praise = commentListInfo.getIs_praise();  // praise	是否是赞，1为赞，0为不赞

        if (praise.equals("0")) {
            praise = "1";
            Oiliest();
        } else if (praise.equals("1")) {
            praise = "0";
            Oiliest();
        }
    }

    //点赞
    private void Oiliest() {
        praiseComment.sendPostRequest(Urls.PraiseComment, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!Tools.isEmpty(is_advertisement)) {
                            if (is_advertisement.equals("1")) {
                                getData2();
                            } else if (is_advertisement.equals("0")) {
                                getData();
                            }
                        }
                        adapter.notifyDataSetChanged();
                        // Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(mcontext, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(mcontext, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(mcontext, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    //评论提交
    private void Comment() {
        commentPhoto.sendPostRequest(Urls.CommentPhoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        adapter.notifyDataSetChanged();
                        getData();
                        //  Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(mcontext, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(mcontext, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(mcontext, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void Comment2() { //广告大图
        commentAd.sendPostRequest(Urls.CommentAd, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        adapter.notifyDataSetChanged();
                        getData2();
                        //   Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(mcontext, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(mcontext, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(mcontext, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private static int num;
    //列表
    private void getData() {  //评论信息接口
        commentList.sendPostRequest(Urls.CommentList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (listInfos == null) {
                            listInfos = new ArrayList<CommentListInfo>();
                        } else {
                            if (page == 1) {
                                listInfos.clear();
                            }
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            total_num = jsonObject.getString("total_num"); //总评论数
                            if (!Tools.isEmpty(total_num) && !"0".equals(total_num)) {
                                tv_totalnum.setText(total_num + "条评论");
                            }
                            JSONArray jsonArray = jsonObject.optJSONArray("comment_list");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    CommentListInfo commentListInfo = new CommentListInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    commentListInfo.setComment_id(object.getString("comment_id"));
                                    commentListInfo.setContent(object.getString("content"));
                                    commentListInfo.setCreate_time(object.getString("create_time"));
                                    commentListInfo.setIs_praise(object.getString("is_praise")); // "is_praise ":"是否点过赞，1为点过，0为没点过"
                                    commentListInfo.setPraise_num(object.getInt("praise_num"));
                                    commentListInfo.setUser_img(object.getString("user_img"));
                                    commentListInfo.setUser_name(object.getString("user_name"));  //
                                    commentListInfo.setComment_username(object.getString("comment_username"));
                                    listInfos.add(commentListInfo);
                                }

                                num = listInfos.size();
                                if (jsonArray.length() < 15) {
                                    discuss_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    discuss_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                    myHandler.sendEmptyMessageDelayed(0, 500);
                                }
                            }
                        }
                        discuss_listview.onRefreshComplete();

                    } else {
                        Tools.showToast(mcontext, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(mcontext, getResources().getString(R.string.network_error));
                }
                discuss_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                discuss_listview.onRefreshComplete();
                Tools.showToast(mcontext, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    //列表
    private void getData2() {  //评论信息接口
        adCommentList.sendPostRequest(Urls.AdCommentList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (listInfos == null) {
                            listInfos = new ArrayList<CommentListInfo>();
                        } else {
                            if (page == 1) {
                                listInfos.clear();
                            }
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            total_num = jsonObject.getString("total_num"); //总评论数
                            if (!Tools.isEmpty(total_num) && !"0".equals(total_num)) {
                                tv_totalnum.setText(total_num + "条评论");
                            }
                            JSONArray jsonArray = jsonObject.optJSONArray("comment_list");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    CommentListInfo commentListInfo = new CommentListInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    commentListInfo.setComment_id(object.getString("comment_id"));
                                    commentListInfo.setContent(object.getString("content"));
                                    commentListInfo.setCreate_time(object.getString("create_time"));
                                    commentListInfo.setIs_praise(object.getString("is_praise")); // "is_praise ":"是否点过赞，1为点过，0为没点过"
                                    commentListInfo.setPraise_num(object.getInt("praise_num"));
                                    commentListInfo.setUser_img(object.getString("user_img"));
                                    commentListInfo.setUser_name(object.getString("user_name"));
                                    commentListInfo.setComment_username(object.getString("comment_username"));
                                    listInfos.add(commentListInfo);
                                }
                                if (jsonArray.length() < 15) {
                                    discuss_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    discuss_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                    //
                                    myHandler.sendEmptyMessageDelayed(0, 500);
                                }
                            }
                        }
                        discuss_listview.onRefreshComplete();

                    } else {
                        Tools.showToast(mcontext, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(mcontext, getResources().getString(R.string.network_error));
                }
                discuss_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                discuss_listview.onRefreshComplete();
                Tools.showToast(mcontext, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    /**
     * @param listView 动态测量listview的高度
     */
    public void setListViewHeightBasedOnChildren(PullToRefreshListView listView) {
        int height = getTotalHeightofListView(listView);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = height;
        listView.setLayoutParams(params);
    }

    public int getTotalHeightofListView(PullToRefreshListView listView) {
        if (adapter == null) {
            return 0;
        }
        int totalHeight = 0;
        ListView lv = listView.getRefreshableView();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View mView = adapter.getView(i, null, listView);
            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            //mView.measure(0, 0);
            totalHeight += mView.getMeasuredHeight() + 43;
            Tools.d(String.valueOf(totalHeight));
        }
        View view = findViewById(R.id.discuss_layout);
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        int heightdiv = Tools.dipToPx(mcontext, 10);
        int height = totalHeight + ((adapter.getCount() - 1) * heightdiv);
        params.height = Tools.dipToPx(mcontext, 110) + height;
        if (params.height > Tools.getScreeInfoHeight(mcontext) / 5 * 3) {
            params.height = Tools.getScreeInfoHeight(mcontext) / 5 * 3;
        }
        Tools.d("listview总高度=" + params.height);
        view.setLayoutParams(params);
        return totalHeight;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private MyHandler myHandler = new MyHandler();

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {//更新listview高度
                    setListViewHeightBasedOnChildren(discuss_listview);
                }
                break;
            }
        }
    }
}
