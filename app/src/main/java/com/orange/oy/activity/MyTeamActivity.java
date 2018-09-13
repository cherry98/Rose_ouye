package com.orange.oy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.adapter.MyTeamAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.CharacterParser;
import com.orange.oy.util.PinyinComparatorForMyteam;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的团队
 */
public class MyTeamActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle myteam_title;

    private void initTitle() {
        myteam_title = (AppTitle) findViewById(R.id.myteam_title);
        myteam_title.settingName(getResources().getString(R.string.myteam));
        myteam_title.showBack(this);
        myteam_title.settingExit("添加", new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                startActivity(new Intent(MyTeamActivity.this, AddFriendsActivity.class));
            }
        });
    }


    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (getData != null) {
            getData.stop(Urls.Myteam);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this);
    }

    private void initNetworkConnection() {
        getData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyTeamActivity.this));
                return params;
            }
        };
        getData.setIsShowDialog(true);
    }

    private BroadcastReceiver ChangeRedPointBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent arg1) {
            if (myteam_redpoint == null) return;
            int num = AppInfo.getRedpoint(MyTeamActivity.this);
            if (num > 0) {
                myteam_redpoint.setVisibility(View.VISIBLE);
                if (num > 99) {
                    num = 99;
                }
                myteam_redpoint.setText(num + "");
            } else {
                myteam_redpoint.setVisibility(View.GONE);
            }
        }
    };

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiverMyteam_Redpoint);
        context.registerReceiver(ChangeRedPointBroadcastReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(ChangeRedPointBroadcastReceiver);
    }

    private NetworkConnection getData;
    private ArrayList<MyteamNewfdInfo> list;
    private ArrayList<MyteamNewfdInfo> mainList;
    private CharacterParser characterParser;
    private PinyinComparatorForMyteam pinyinComparatorForMyteam;
    private MyTeamAdapter adapter;
    private ListView myteam_listview;
    private SideBar myteam_sidebar;
    private EditText myteam_search;
    private TextView myteam_redpoint;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myteam);
        if (TextUtils.isEmpty(AppInfo.getKey(MyTeamActivity.this))) {
            ConfirmDialog.showDialog(MyTeamActivity.this, null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            Intent intent = new Intent(MyTeamActivity.this, IdentifycodeLoginActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    });
            return;
        }
        initTitle();
        initNetworkConnection();
        registerReceiver(this);
        characterParser = CharacterParser.getInstance();
        pinyinComparatorForMyteam = new PinyinComparatorForMyteam();
        myteam_listview = (ListView) findViewById(R.id.myteam_listview);
        myteam_redpoint = (TextView) findViewById(R.id.myteam_redpoint);
        myteam_search = (EditText) findViewById(R.id.myteam_search);
        myteam_sidebar = (SideBar) findViewById(R.id.myteam_sidebar);
        myteam_sidebar.setTextView(((TextView) findViewById(R.id.myteam_dialog)));//设置右侧触摸监听
        myteam_sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public void onTouchingLetterChanged(String s) {
                if (adapter != null) {
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        myteam_listview.setSelection(position);
                    }
                }
            }
        });
        //根据输入框输入值的改变来过滤搜索
        myteam_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        findViewById(R.id.myteam_newfds).setOnClickListener(this);
        isGetdata = true;
    }

    public static boolean isGetdata;

    protected void onResume() {
        super.onResume();
        if (myteam_redpoint != null) {
            int num = AppInfo.getRedpoint(this);
            if (num > 0) {
                myteam_redpoint.setVisibility(View.VISIBLE);
                if (num > 99) {
                    num = 99;
                }
                myteam_redpoint.setText(num + "");
            } else {
                myteam_redpoint.setVisibility(View.GONE);
            }
        }
        if (isGetdata) {
            getData();
            isGetdata = false;
        }
    }

    private void getData() {
        getData.sendPostRequest(Urls.Myteam, new Response.Listener<String>() {
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list == null) {
                            list = new ArrayList<MyteamNewfdInfo>();
                        } else {
                            list.clear();
                        }
                        if (mainList == null) {
                            mainList = new ArrayList<MyteamNewfdInfo>();
                        } else {
                            mainList.clear();
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        String name, note;
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            MyteamNewfdInfo myteamNewfdInfo = new MyteamNewfdInfo();
                            name = jsonObject.getString("user_name");
                            myteamNewfdInfo.setName(name);
                            myteamNewfdInfo.setImg(Urls.ImgIp + jsonObject.getString("img_url"));
                            myteamNewfdInfo.setId(jsonObject.getString("user_mobile"));
                            note = jsonObject.getString("note");
                            if (!TextUtils.isEmpty(note) && !note.equals("null")) {
                                myteamNewfdInfo.setNote(note);
                                name = note;
                            }
                            //汉字转换成拼音
                            String pinyin = characterParser.getSelling(name);
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                myteamNewfdInfo.setSortLetters(sortString.toUpperCase());
                            } else {
                                myteamNewfdInfo.setSortLetters("#");
                            }
                            list.add(myteamNewfdInfo);
                        }
                        Collections.sort(list, pinyinComparatorForMyteam);
                        int size = list.size();
                        for (int i = 0; i < size; i++) {
                            mainList.add(list.get(i));
                        }
                        adapter = new MyTeamAdapter(MyTeamActivity.this, mainList);
                        myteam_listview.setAdapter(adapter);
                    } else {
                        Tools.showToast(MyTeamActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyTeamActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(MyTeamActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myteam_newfds: {
                AppInfo.setRedpoint(this, 0);
                startActivity(new Intent(this, NewFriendsActivity.class));
            }
            break;
        }
    }

    private boolean isSearch;

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        if (adapter == null) return;
        if (TextUtils.isEmpty(filterStr)) {
            if (mainList == null) {
                mainList = new ArrayList<>();
            } else {
                mainList.clear();
            }
            int size = list.size();
            for (int i = 0; i < size; i++) {
                mainList.add(list.get(i));
            }
            isSearch = false;
        } else {
            isSearch = true;
            mainList.clear();
            int size = list.size();
            MyteamNewfdInfo sortModel;
            for (int i = 0; i < size; i++) {
                sortModel = list.get(i);
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr
                        .toString())) {
                    mainList.add(sortModel);
                }
            }
        }
        adapter.updateListView(isSearch, mainList);
    }

}
