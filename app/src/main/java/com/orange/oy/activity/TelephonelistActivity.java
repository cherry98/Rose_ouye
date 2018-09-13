package com.orange.oy.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.TelephonelistAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CloseTaskDialog;
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
 * 手机通讯录
 */
public class TelephonelistActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView
        .OnItemClickListener {

    private AppTitle telephonelt_title;

    private void initTitle() {
        telephonelt_title = (AppTitle) findViewById(R.id.telephonelt_title);
        telephonelt_title.settingName(getResources().getString(R.string.telephonelist));
        telephonelt_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (sendData != null) {
            sendData.stop(Urls.Finduserphone);
        }
        if (messageTemplate != null) {
            messageTemplate.stop(Urls.MessageTemplate);
        }
    }

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("phonelist", getPhoneString());
                params.put("usermobile", AppInfo.getName(TelephonelistActivity.this));
                params.put("team_id", team_id);
                return params;
            }
        };
        sendData.setIsShowDialog(true);
        messageTemplate = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TelephonelistActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                return params;
            }
        };
    }

    private ArrayList<MyteamNewfdInfo> list;
    private ArrayList<MyteamNewfdInfo> mainList;
    private NetworkConnection sendData, messageTemplate;
    private TelephonelistAdapter telephonelistAdapter;
    private ListView telephonelt_listview;
    private SideBar telephonelt_sidebar;
    private CharacterParser characterParser;
    private PinyinComparatorForMyteam pinyinComparatorForMyteam;
    private EditText telephonelt_search;
    private String team_id, mould;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephonelist);
        team_id = getIntent().getStringExtra("team_id");
        initTitle();
        initNetworkConnection();
        characterParser = CharacterParser.getInstance();
        pinyinComparatorForMyteam = new PinyinComparatorForMyteam();
//        displayRecords();
        telephonelt_listview = (ListView) findViewById(R.id.telephonelt_listview);
        telephonelt_search = (EditText) findViewById(R.id.telephonelt_search);
        telephonelt_sidebar = (SideBar) findViewById(R.id.telephonelt_sidebar);
        telephonelt_sidebar.setTextView(((TextView) findViewById(R.id.telephonelt_dialog)));//设置右侧触摸监听
        telephonelt_sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public void onTouchingLetterChanged(String s) {
                if (telephonelistAdapter != null) {
                    int position = telephonelistAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        telephonelt_listview.setSelection(position);
                    }
                }
            }
        });
        //根据输入框输入值的改变来过滤搜索
        telephonelt_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        telephonelt_listview.setOnItemClickListener(this);
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, AppInfo
                        .REQUEST_CODE_ASK_READ_PHONE_STATE);
            } else {
                new getTele().execute();
            }
        } else {
            new getTele().execute();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new getTele().execute();
                } else {
                    Tools.showToast(TelephonelistActivity.this, "权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.InvitePhonelist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
                        int length = jsonArray.length();
                        if (mainList == null) {
                            mainList = new ArrayList<MyteamNewfdInfo>();
                        } else {
                            mainList.clear();
                        }
                        if (length == list.size()) {
                            for (int i = 0; i < length; i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                String mobile = jsonObject.optString("mobile");
                                for (MyteamNewfdInfo myteamNewfdInfo : list) {
                                    if (myteamNewfdInfo.getState() == -1 && mobile.equals(myteamNewfdInfo.getPhone())) {
                                        int state = Tools.StringToInt(jsonObject.getString("state"));//0为未入队，1为已入队
                                        if (state != -1) {
                                            myteamNewfdInfo.setState(state);
                                        }
                                        break;
                                    }
                                }
                            }
                            int size = list.size();
                            for (int i = 0; i < size; i++) {
                                mainList.add(list.get(i));
                            }
                            if (telephonelistAdapter == null) {
                                telephonelistAdapter = new TelephonelistAdapter(TelephonelistActivity.this, mainList);
                                telephonelt_listview.setAdapter(telephonelistAdapter);
                            } else {
                                telephonelistAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Tools.showToast(TelephonelistActivity.this, getResources().getString(R.string
                                    .network_error));
                        }
                        messageTemplate();
                    } else {
                        Tools.showToast(TelephonelistActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TelephonelistActivity.this, getResources().getString(R.string.network_error));
                } catch (Exception e) {
                    Tools.showToast(TelephonelistActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TelephonelistActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void messageTemplate() {
        messageTemplate.sendPostRequest(Urls.MessageTemplate, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        mould = jsonObject.getString("text");
                    } else {
                        Tools.showToast(TelephonelistActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TelephonelistActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TelephonelistActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private String getPhoneString() {
        if (list == null) return "";
        String str = "";
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.isEmpty(str)) {
                str = list.get(i).getId();
            } else {
                str = str + "," + list.get(i).getId();
            }
        }
        return str;
    }

    /**
     * 获取手机通讯录
     */
    private void displayRecords() {
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        String columns[] = new String[]{};
        ContentResolver cr = this.getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, columns, null, null, null);
        String phoneNumber = null;
        String name = null;
        String userName = AppInfo.getName(this);
        if (cur != null && cur.moveToFirst()) {
            do {
                name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (!TextUtils.isEmpty(phoneNumber) && !phoneNumber.equals(userName)) {
                    MyteamNewfdInfo myteamNewfdInfo = new MyteamNewfdInfo();
                    myteamNewfdInfo.setName((name == null) ? "" : name);
                    myteamNewfdInfo.setPhone(phoneNumber.replaceAll(" ", ""));
                    //汉字转换成拼音
                    String pinyin = characterParser.getSelling(name);
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    // 正则表达式，判断首字母是否是英文字母
                    if (sortString.matches("[A-Z]")) {
                        myteamNewfdInfo.setSortLetters(sortString.toUpperCase());
                    } else {
                        myteamNewfdInfo.setSortLetters("#");
                    }
                    myteamNewfdInfo.setId(phoneNumber.replaceAll(" ", ""));
                    list.add(myteamNewfdInfo);
                }
            } while (cur.moveToNext());
        }
        if (cur != null)
            cur.close();
        cur = cr.query(Uri.parse("content://icc/adn"), columns, null, null, null);
        if (cur != null && cur.moveToFirst()) {
            do {
                name = cur.getString(cur.getColumnIndex(Contacts.People.NAME));
                phoneNumber = cur.getString(cur.getColumnIndex(Contacts.People.NUMBER));
                if (!TextUtils.isEmpty(phoneNumber) && !phoneNumber.equals(userName)) {
                    MyteamNewfdInfo myteamNewfdInfo = new MyteamNewfdInfo();
                    myteamNewfdInfo.setName((name == null) ? "" : name);
                    myteamNewfdInfo.setPhone(phoneNumber.replaceAll(" ", ""));
                    //汉字转换成拼音
                    String pinyin = characterParser.getSelling(name);
                    String sortString;
                    if (!TextUtils.isEmpty(pinyin)) {
                        sortString = pinyin.substring(0, 1).toUpperCase();
                    } else {
                        sortString = "";
                    }
                    // 正则表达式，判断首字母是否是英文字母
                    if (sortString.matches("[A-Z]")) {
                        myteamNewfdInfo.setSortLetters(sortString.toUpperCase());
                    } else {
                        myteamNewfdInfo.setSortLetters("#");
                    }
                    myteamNewfdInfo.setId(phoneNumber.replaceAll(" ", ""));
                    list.add(myteamNewfdInfo);
                }
            } while (cur.moveToNext());
        }
        if (cur != null)
            cur.close();
        int size = list.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = size - 1; j > i; j--) {
                if (list.get(j).getId().equals(list.get(i).getId())) {
                    list.remove(j);
                    size = list.size();
                }
            }
        }
        Collections.sort(list, pinyinComparatorForMyteam);
    }

    private boolean isSearch;

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        if (telephonelistAdapter == null) return;
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
        telephonelistAdapter.updateListView(isSearch, mainList);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MyteamNewfdInfo myteamNewfdInfo = mainList.get(position);
        if (myteamNewfdInfo.getState() == 0) {//未入队
            CloseTaskDialog.showDialog(this, "将以下信息以短信形式发送好友，你的好友就可以加入你的战队啦！", "发送",
                    mould, true, new CloseTaskDialog.OnCloseTaskDialogListener() {
                        @Override
                        public void sumbit(String edittext) {
                            Uri smsToUri = Uri.parse("smsto:" + myteamNewfdInfo.getPhone());
                            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                            //短信内容
                            intent.putExtra("sms_body", edittext);
                            startActivity(intent);
                        }
                    });
        }
    }

    private class getTele extends AsyncTask {
        protected Object doInBackground(Object[] params) {
            displayRecords();
            return null;
        }

        protected void onPostExecute(Object o) {
            if (list == null || list.isEmpty()) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TelephonelistActivity.this, "没有发现联系人唉...");
            } else {
                sendData();
            }
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(TelephonelistActivity.this, "");
        }
    }
}
