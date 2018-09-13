package com.orange.oy.activity.createtask_317;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.FinishtaskView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/3.
 * 验收资料
 */

public class CheckTaskActivity extends BaseActivity implements View.OnClickListener {
    private ListView checktask_listview;
    private ArrayList<CheckTaskInfo> lists = new ArrayList<>();
    private NetworkConnection taskdetail;
    private NetworkConnection checkOutlet;

    protected void onStop() {
        super.onStop();
        if (taskdetail != null) {
            taskdetail.stop(Urls.Taskdetail);
        }
        if (checkOutlet != null) {
            checkOutlet.stop(Urls.CheckOutlet);
        }
    }

    private void initNetworkConnection() {
        taskdetail = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("store_id", outlet_id);
                params.put("state", "2");
                params.put("token", Tools.getToken());
                return params;
            }
        };
        checkOutlet = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("state", checkOutlet_state);//通过状态，1为通过，0为不通过【必传】
                params.put("token", Tools.getToken());
                params.put("outlet_id", outlet_id);
                if (!Tools.isEmpty(reason)) {
                    params.put("reason", reason);
                }
                params.put("usermobile", AppInfo.getName(CheckTaskActivity.this));
                return params;
            }
        };
        checkOutlet.setIsShowDialog(true);
    }

    private String outlet_id, checkOutlet_state;
    private String reason;//不通过原因
    private ImageView checktask_ico;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checktask);
        AppTitle checktask_title = (AppTitle) findViewById(R.id.checktask_title);
        checktask_title.settingName("验收资料");
        checktask_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        outlet_id = getIntent().getStringExtra("outlet_id");
        String name = getIntent().getStringExtra("name");
        String time = getIntent().getStringExtra("time");
        String money = getIntent().getStringExtra("money");
        initNetworkConnection();
        View view = Tools.loadLayout(this, R.layout.activity_checktask_listviewhead);
        checktask_listview = (ListView) findViewById(R.id.checktask_listview);
        findViewById(R.id.checktask_left).setOnClickListener(this);
        findViewById(R.id.checktask_right).setOnClickListener(this);
        TextView checktask_name = (TextView) findViewById(R.id.checktask_name);
        TextView checktask_time = (TextView) findViewById(R.id.checktask_time);
        TextView checktask_money = (TextView) findViewById(R.id.checktask_money);
        TextView checktasklvh_name = (TextView) view.findViewById(R.id.checktasklvh_name);
        TextView checktasklvh_time = (TextView) view.findViewById(R.id.checktasklvh_time);
        TextView checktasklvh_money = (TextView) view.findViewById(R.id.checktasklvh_money);
        checktasklvh_name.setText(getIntent().getStringExtra("nametask"));
        checktasklvh_time.setText("执行时间：" + getIntent().getStringExtra("timetask"));
        checktasklvh_money.setText("位置地址：" + getIntent().getStringExtra("addresstask"));
        checktask_name.setText(name);
        checktask_money.setText(money);
        checktask_time.setText(time);
        checktask_listview.addHeaderView(view);
        getData();
    }

    private MyAdapter myAdapter;

    /**
     * 获取数据&加载布局
     */
    private void getData() {
        taskdetail.sendPostRequest(Urls.Taskdetail, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (lists == null) {
                            lists = new ArrayList<>();
                        } else {
                            lists.clear();
                            if (myAdapter != null)
                                myAdapter.notifyDataSetChanged();
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            String isclose = jsonObject.getString("isclose");
                            if (TextUtils.isEmpty(isclose) || isclose.equals("null")) {//底层任务
                                creatView(jsonObject.getJSONArray("datas").getJSONObject(0).getJSONArray("datas"));
                            }
                        }
                        if (myAdapter == null) {
                            myAdapter = new MyAdapter();
                            checktask_listview.setAdapter(myAdapter);
                        } else {
                            myAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(CheckTaskActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(CheckTaskActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CheckTaskActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void creatView(JSONArray jsonArray) throws JSONException {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = Tools.dipToPx(this, 15);
        if (jsonArray == null) {
            return;
        }
        int length = jsonArray.length();
        JSONObject jsonObject;
        for (int i = 0; i < length; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            String task_type = jsonObject.getString("task_type");
            if ("1".equals(task_type) || "8".equals(task_type)) {//拍照任务
                if ("1".equals(jsonObject.getString("note_type"))) {//单备注
                    CheckTaskInfo checkTaskInfo = new CheckTaskInfo();
                    String photos = jsonObject.getString("photo_datas");
                    try {
                        photos = URLDecoder.decode(photos, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(photos) && !"null".equals(photos)) {
                        photos = photos.replaceAll("\\[\"", "").replaceAll("\"]", "");
                        String[] photo_datas = photos.split("\",\"");
                        Collections.addAll(checkTaskInfo.list, photo_datas);
                    }
                    checkTaskInfo.note = jsonObject.getString("beizhu").replaceAll("\\[\"", "").replaceAll("\"]", "");
                    checkTaskInfo.name = jsonObject.getString("task_name");
                    lists.add(checkTaskInfo);
                }
            }
        }
    }

    private void sendData() {
        checkOutlet.sendPostRequest(Urls.CheckOutlet, new Response.Listener<String>() {
            public void onResponse(String s) {
                CustomProgressDialog.Dissmiss();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        baseFinish();
                    } else {
                        Tools.showToast(CheckTaskActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CheckTaskActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private EditText diaglogEditText;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checktask_left: {//不通过
                checkOutlet_state = "0";
                diaglogEditText = ConfirmDialog.showDialog(CheckTaskActivity.this, "验收不通过！", 3, 0, "资料验收不通过，请填写不通过原因。",
                        0, "取消", 0xFFFFC0BD, "确定", Color.WHITE, null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            public void leftClick(Object object) {
                            }

                            public void rightClick(Object object) {
                            }
                        }).showEditText(200, "请输入不通过原因，此原因会通知执行者。").settingRightOnClick(new View.OnClickListener() {
                    public void onClick(View v) { //不通过通过
                        if (diaglogEditText != null) {
                            reason = diaglogEditText.getText().toString();
                        }
                        if (!TextUtils.isEmpty(reason)) {
                            checkOutlet_state = "0";
                            sendData();
                            ConfirmDialog.dissmisDialog();
                        } else {
                            Tools.showToast(CheckTaskActivity.this, "请填写不通过原因");
                        }
                    }
                }).getConfirm_edittext();
            }
            break;
            case R.id.checktask_right: {//通过
                ConfirmDialog.showDialog(CheckTaskActivity.this, "验收通过！", 2, 0, "", 0, "取消", 0xFFFFC0BD, "确定",
                        Color.WHITE, null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            public void leftClick(Object object) {
                            }

                            public void rightClick(Object object) { //通过
                                checkOutlet_state = "1";
                                sendData();
                            }
                        });
            }
            break;
        }
    }

    private class MyAdapter extends BaseAdapter {

        public int getCount() {
            return lists.size();
        }

        public Object getItem(int position) {
            return lists.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            FinishtaskView finishtaskView;
            if (convertView == null) {
                finishtaskView = new FinishtaskView(CheckTaskActivity.this, false);
            } else {
                finishtaskView = (FinishtaskView) convertView;
            }
            CheckTaskInfo checkTaskInfo = lists.get(position);
            finishtaskView.settingValue(checkTaskInfo.name, lists.get(position).list, checkTaskInfo.note);
            finishtaskView.setTag(position);
            finishtaskView.setOnTitleClickListener(onTitleClickListener);
            if (checkTaskInfo.isShow) {
                finishtaskView.showView();
            } else {
                finishtaskView.hideView();
            }
            return finishtaskView;
        }
    }

    private FinishtaskView.OnTitleClickListener onTitleClickListener = new FinishtaskView.OnTitleClickListener() {
        public void titleClick(Object tag) {
            int position = (int) tag;
            CheckTaskInfo checkTaskInfo = lists.get(position);
            checkTaskInfo.isShow = !checkTaskInfo.isShow;
        }
    };

    private class CheckTaskInfo {
        ArrayList<String> list = new ArrayList<>();
        String name;
        String note;
        boolean isShow = false;
    }
}
