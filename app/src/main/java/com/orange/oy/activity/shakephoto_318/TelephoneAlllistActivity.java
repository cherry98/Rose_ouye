package com.orange.oy.activity.shakephoto_318;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.AddPhoneActivity;
import com.orange.oy.adapter.TelephoneAlllistAdapter;
import com.orange.oy.adapter.TelephonelistAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CloseTaskDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.CityInfo;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.CharacterParser;
import com.orange.oy.util.PinyinComparatorForMyteam;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;
import com.orange.oy.view.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 手机通讯录  带有全选
 */
public class TelephoneAlllistActivity extends BaseActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener {


    public void onBack() {
        baseFinish();
    }

    private ArrayList<MyteamNewfdInfo> list;
    private ArrayList<MyteamNewfdInfo> mainList;
    private TelephoneAlllistAdapter telephonelistAdapter;
    private MyListView telephonelt_listview;
    private SideBar telephonelt_sidebar;
    private CharacterParser characterParser;
    private PinyinComparatorForMyteam pinyinComparatorForMyteam;
    private EditText telephonelt_search;
    private String team_id, mould;
    private CheckBox citysearch_all;  //通讯录全选
    private TextView citysearch_finish;//完成按钮

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephonelist2);
        team_id = getIntent().getStringExtra("team_id");
        characterParser = CharacterParser.getInstance();
        pinyinComparatorForMyteam = new PinyinComparatorForMyteam();
        //  displayRecords();
        citysearch_all = (CheckBox) findViewById(R.id.citysearch_all);
        telephonelt_listview = (MyListView) findViewById(R.id.telephonelt_listview);
        telephonelt_search = (EditText) findViewById(R.id.telephonelt_search);
        telephonelt_sidebar = (SideBar) findViewById(R.id.telephonelt_sidebar);
        citysearch_finish = (TextView) findViewById(R.id.citysearch_finish);
        citysearch_finish.setOnClickListener(this);
        citysearch_all.setOnCheckedChangeListener(this);
        telephonelt_listview.setOnItemClickListener(this);
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
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, AppInfo
                        .REQUEST_CODE_ASK_READ_PHONE_STATE);
            } else {
                //new getTele().execute();
                displayRecords();
            }
        } else {
            //new getTele().execute();
            displayRecords();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.citysearch_finish: {
                String name = null;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChecked()) {
                        if (name == null) {
                            name = list.get(i).getPhone();
                        } else {
                            name = name + "," + list.get(i).getPhone();
                        }
                    }
                }
                Intent intent = new Intent(this, AddPhoneActivity.class);
                intent.putExtra("name", name);
                Tools.d("name======>>>" + name);
                setResult(AppInfo.REQUEST_CODE_ADD, intent);
                finish();

            }
            break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (int i = 0; i < list.size(); i++) {
            MyteamNewfdInfo myteamNewfdInfo = list.get(i);
            myteamNewfdInfo.setChecked(isChecked);
        }
        telephonelistAdapter.notifyDataSetChanged();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // new getTele().execute();
                    displayRecords();
                } else {
                    Tools.showToast(TelephoneAlllistActivity.this, "权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
        if (mainList == null) {
            mainList = new ArrayList<>();
        } else {
            mainList.clear();
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
                    mainList.add(myteamNewfdInfo);
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
                    mainList.add(myteamNewfdInfo);
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

        if (telephonelistAdapter == null) {
            telephonelistAdapter = new TelephoneAlllistAdapter(TelephoneAlllistActivity.this, list);
            telephonelt_listview.setAdapter(telephonelistAdapter);
        } else {
            telephonelistAdapter.notifyDataSetChanged();
        }
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
                if (name.contains(filterStr) || characterParser.getSelling(name).startsWith(filterStr)) {
                    mainList.add(sortModel);
                }
            }

        }
        telephonelistAdapter.updateListView(isSearch, mainList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyteamNewfdInfo myteamNewfdInfo = list.get(position);
        myteamNewfdInfo.setChecked(!myteamNewfdInfo.isChecked());
        telephonelistAdapter.notifyDataSetChanged();
    }
}
