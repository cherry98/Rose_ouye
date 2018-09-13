package com.orange.oy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.adapter.MessageLeftAdapter;
import com.orange.oy.adapter.MessageMiddleAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MessageLeftInfo;
import com.orange.oy.info.SystemMessageInfo;
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
 * 消息主页
 */
public class MessageFragment extends BaseFragment implements AppTitle.OnExitClickForAppTitle, View.OnClickListener {
    private View mView;
    private AppTitle message_title;

    private void initTitle() {
        message_title = (AppTitle) mView.findViewById(R.id.message_title);
        message_title.settingName(getResources().getString(R.string.message));
        message_title.settingExit(getResources().getString(R.string.message_delete), this);
    }

    public void onExit() {
        if (messageLeftAdapter != null) {
            message_delete_layout.setVisibility(View.VISIBLE);
            messageLeftAdapter.setDelete(true);
            message_title.hideExit();
            int size = list_left.size();
            for (int i = 0; i < size; i++) {
                list_left.get(i).setIsOpen(false);
            }
            messageLeftAdapter.notifyDataSetChanged();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message, container, false);
        return mView;
    }

    private int page_right = 1, page_middle = 1;
    private String search;

    private void initNetworkConnection() {
        Questionlist = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_right + "");
                if (!TextUtils.isEmpty(search)) {
                    params.put("title", search);
                }
                return params;
            }
        };
        Announcementlist = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_middle + "");
                return params;
            }
        };
    }

    private View message_tab1, message_tab2, message_listview_right_search;
    private TextView message_text_tab1, message_text_tab2;
    private EditText message_search;
    private PullToRefreshListView message_listview_left, message_listview_middle;
    private MessageLeftAdapter messageLeftAdapter;
    private MessageMiddleAdapter messageMiddleAdapter;
    private ArrayList<MessageLeftInfo> middleList;
    private NetworkConnection Questionlist, Announcementlist;
    private SystemDBHelper systemDBHelper;
    private View message_delete_layout;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initNetworkConnection();
        initTitle();
        systemDBHelper = new SystemDBHelper(getContext());
        message_text_tab1 = (TextView) mView.findViewById(R.id.message_text_tab1);
        message_text_tab2 = (TextView) mView.findViewById(R.id.message_text_tab2);
        message_listview_right_search = mView.findViewById(R.id.message_listview_right_search);
        message_delete_select_ico = (ImageView) mView.findViewById(R.id.message_delete_select_ico);
        message_delete_layout = mView.findViewById(R.id.message_delete_layout);
        message_search = (EditText) mView.findViewById(R.id.message_search);
        message_tab1 = mView.findViewById(R.id.message_tab1);
        message_tab2 = mView.findViewById(R.id.message_tab2);
        message_listview_left = (PullToRefreshListView) mView.findViewById(R.id.message_listview_left);
        message_listview_middle = (PullToRefreshListView) mView.findViewById(R.id.message_listview_middle);
        message_listview_left.setVisibility(View.VISIBLE);
        message_listview_middle.setVisibility(View.GONE);
        message_listview_right_search.setVisibility(View.GONE);
        initListview(message_listview_left);
        initListview(message_listview_middle);
        message_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new getLeftData(false).execute();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        message_listview_middle.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshListViewMiddle();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_middle++;
                getDataMiddle();
            }
        });
        message_listview_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (messageLeftAdapter.isDelete()) {
                    MessageLeftInfo messageLeftInfo = list_left.get(position - 1);
                    messageLeftInfo.setIsSelect(!messageLeftInfo.isSelect());
                    if (messageLeftInfo.isSelect()) {
                        selectNum++;
                    } else {
                        selectNum--;
                    }
                    if (selectNum == list_left.size()) {
                        message_delete_select_ico.setImageResource(R.mipmap.message_del_yes);
                    } else {
                        message_delete_select_ico.setImageResource(R.mipmap.message_del_no);
                    }
                } else {
                    MessageLeftInfo messageLeftInfo = list_left.get(position - 1);
                    messageLeftInfo.setIsOpen(!messageLeftInfo.isOpen());
                }
                messageLeftAdapter.notifyDataSetChanged();
            }
        });
        message_listview_middle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MessageLeftInfo messageLeftInfo = middleList.get(position - 1);
                    Intent intent = new Intent(getContext(), BrowserActivity.class);
                    intent.putExtra("flag", BrowserActivity.flag_broadcast);
                    intent.putExtra("title", messageLeftInfo.getTitle());
                    intent.putExtra("content", messageLeftInfo.getMessage2());
//                    if ("资料".equals(messageLeftInfo.getFlag())) {
                    intent.putExtra("id", messageLeftInfo.getId());
//                    }
                    startActivity(intent);
                } catch (Exception e) {
                    Tools.showToast(getContext(), "数据异常，退出应用试试吧");
                }
            }
        });
        message_text_tab1.setOnClickListener(this);
        message_text_tab2.setOnClickListener(this);
        message_delete_select_ico.setOnClickListener(this);
        mView.findViewById(R.id.message_delete_select_text).setOnClickListener(this);
        mView.findViewById(R.id.message_delete_ico).setOnClickListener(this);
        mView.findViewById(R.id.message_delete_text).setOnClickListener(this);
        middleList = new ArrayList<>();
        list_left = new ArrayList<>();
        messageLeftAdapter = new MessageLeftAdapter(getContext(), list_left);
        messageMiddleAdapter = new MessageMiddleAdapter(getContext(), middleList);
        message_listview_left.setAdapter(messageLeftAdapter);
        message_listview_middle.setAdapter(messageMiddleAdapter);
        onClick(message_text_tab1);
    }

    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setPullLabel(getResources().getString(R.string.listview_down));// 刚下拉时，显示的提示
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));// 刷新时
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));// 下来达到一定距离时，显示的提示
    }

    /**
     * 刷新中间列表
     */
    private void refreshListViewMiddle() {
        page_middle = 1;
        getDataMiddle();
    }

    private void getDataMiddle() {
        Announcementlist.sendPostRequest(Urls.Announcementlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (middleList == null) {
                            middleList = new ArrayList<MessageLeftInfo>();
                        } else {
                            if (page_middle == 1)
                                middleList.clear();
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int lenght = jsonArray.length();
                        for (int i = 0; i < lenght; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            MessageLeftInfo messageLeftInfo = new MessageLeftInfo();
                            messageLeftInfo.setId(jsonObject.getString("id"));
                            messageLeftInfo.setTitle(jsonObject.getString("title"));
                            String message = jsonObject.getString("brief");
                            if (TextUtils.isEmpty(message) || "null".equals(message)) {
                                message = "";
                            }
                            messageLeftInfo.setMessage(message);
                            messageLeftInfo.setMessage2(jsonObject.getString("content"));
                            messageLeftInfo.setTime(jsonObject.getString("date"));
                            messageLeftInfo.setFlag(jsonObject.getString("type"));
                            middleList.add(messageLeftInfo);
                        }
                        message_listview_middle.onRefreshComplete();
                        if (lenght < 15) {
                            message_listview_middle.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            message_listview_middle.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        messageMiddleAdapter.notifyDataSetChanged();
                    } else {
                        message_listview_middle.onRefreshComplete();
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    message_listview_middle.onRefreshComplete();
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                message_listview_middle.onRefreshComplete();
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    /**
     * 另两个tab用，不显示删除按钮
     */
    private void initSystemMessage() {
        if (!messageLeftAdapter.isDelete()) {
            return;
        }
        int size = list_left.size();
        for (int i = 0; i < size; i++) {
            list_left.get(i).setIsSelect(false);
        }
        messageLeftAdapter.setDelete(false);
        message_delete_select_ico.setImageResource(R.mipmap.message_del_no);
        message_delete_layout.setVisibility(View.GONE);
    }

    private int selectNum;
    private ImageView message_delete_select_ico;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_delete_select_ico:
            case R.id.message_delete_select_text: {
                int size = list_left.size();
                if (selectNum == size) {
                    for (int i = 0; i < size; i++) {
                        list_left.get(i).setIsSelect(false);
                    }
                    selectNum = 0;
                    message_delete_select_ico.setImageResource(R.mipmap.message_del_no);
                } else {
                    for (int i = 0; i < size; i++) {
                        list_left.get(i).setIsSelect(true);
                    }
                    selectNum = size;
                    message_delete_select_ico.setImageResource(R.mipmap.message_del_yes);
                }
                messageLeftAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.message_delete_ico:
            case R.id.message_delete_text: {
                CustomProgressDialog.showProgressDialog(getContext(), "删除中...");
                new getLeftData(true).execute();
            }
            break;
            case R.id.message_text_tab1: {
                message_text_tab1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                message_text_tab2.setTextColor(getResources().getColor(R.color.app_textcolor));
                message_tab1.setVisibility(View.VISIBLE);
                message_tab2.setVisibility(View.INVISIBLE);
                message_listview_left.setVisibility(View.VISIBLE);
                message_listview_middle.setVisibility(View.GONE);
                message_listview_right_search.setVisibility(View.GONE);
                message_title.settingExit(getResources().getString(R.string.message_delete), this);
                if (list_left == null || list_left.isEmpty()) {
                    new getLeftData(false).execute();
                }
            }
            break;
            case R.id.message_text_tab2: {
                message_text_tab2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                message_text_tab1.setTextColor(getResources().getColor(R.color.app_textcolor));
                message_tab2.setVisibility(View.VISIBLE);
                message_tab1.setVisibility(View.INVISIBLE);
                message_listview_middle.setVisibility(View.VISIBLE);
                message_listview_left.setVisibility(View.GONE);
                message_listview_right_search.setVisibility(View.GONE);
                message_title.hideExit();
                initSystemMessage();
                if (middleList == null || middleList.isEmpty()) {
                    refreshListViewMiddle();
                }
            }
            break;
        }
    }

    private ArrayList<MessageLeftInfo> list_left;

    /**
     * 获取系统消息
     */
    class getLeftData extends AsyncTask {
        boolean isDelete;

        getLeftData(boolean isDelete) {
            this.isDelete = isDelete;
        }

        protected Object doInBackground(Object[] params) {
            if (isDelete && list_left != null) {
                int size = list_left.size();
                for (int i = 0; i < size; i++) {
                    if (list_left.get(i).isSelect()) {
                        systemDBHelper.deleteMessage(list_left.get(i).getId());
                    }
                }
            }
            Context context = getContext();
            if (context != null) {
                ArrayList<SystemMessageInfo> list = systemDBHelper.getAll(AppInfo.getName(context));
                int size = list.size();
                if (list_left == null) {
                    list_left = new ArrayList<>();
                } else {
                    list_left.clear();
                }
                for (int i = 0; i < size; i++) {
                    SystemMessageInfo systemMessageInfo = list.get(i);
                    MessageLeftInfo messageLeftInfo = new MessageLeftInfo();
                    messageLeftInfo.setTime(systemMessageInfo.getTime());
                    messageLeftInfo.setTitle(systemMessageInfo.getTitle());
                    if ("2".equals(systemMessageInfo.getCode())) {
                        messageLeftInfo.setMessage(systemMessageInfo.getMessage());
                        messageLeftInfo.setMessage2(systemMessageInfo.getMessage2());
                    } else {
                        messageLeftInfo.setMessage(systemMessageInfo.getMessage());
                    }
                    messageLeftInfo.setFlag(systemMessageInfo.getCode());
                    messageLeftInfo.setId(systemMessageInfo.getId());
                    list_left.add(messageLeftInfo);
                }
            }
            return null;
        }

        protected void onPostExecute(Object o) {
            if (isDelete) {
                messageLeftAdapter.setDelete(false);
                message_title.settingExit(getResources().getString(R.string.message_delete), MessageFragment.this);
                message_delete_select_ico.setImageResource(R.mipmap.message_del_no);
                message_delete_layout.setVisibility(View.GONE);
            } else {
                messageLeftAdapter.notifyDataSetChanged();
            }
            message_listview_left.onRefreshComplete();
            CustomProgressDialog.Dissmiss();
        }
    }
}
