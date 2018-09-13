package com.orange.oy.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.Citysearch2Adapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.CityInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.CharacterParser;
import com.orange.oy.util.PinyinComparator;
import com.orange.oy.view.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SelectDistrictActivity extends BaseActivity implements View.OnClickListener, AdapterView
        .OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    private void initNetworkConnection() {
        getProvinceData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                return params;
            }
        };
        getProvinceData.setIsShowDialog(true);
        getCityData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("provinceId", provinceId);
                return params;
            }
        };
        getCityData.setIsShowDialog(true);
    }

    protected void onStop() {
        super.onStop();
        if (getCityData != null) {
            getCityData.stop(Urls.GetCityByProvince);
        }
        if (getProvinceData != null) {
            getProvinceData.stop(Urls.AllProvince);
        }
    }

    private ListView citysearch_listview;
    private EditText find_search;
    private SideBar citysearch_sidebar;
    private Citysearch2Adapter citysearchAdapter;
    private ArrayList<CityInfo> list;
    private ArrayList<CityInfo> mainList;
    private CharacterParser characterParser;
    private TextView citysearch_dialog;
    private NetworkConnection getProvinceData, getCityData;
    private PinyinComparator pinyinComparator;
    private String provinceId;
    private int flag;
    private CheckBox citysearch_all;//城市全选
    private TextView citysearch_finish;//城市选择完成按钮

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_citysearch);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
        } else {
            flag = data.getIntExtra("flag", -1);
            if (flag == -1) {
                baseFinish();
            } else {
                initNetworkConnection();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    RelativeLayout title_layout = (RelativeLayout) findViewById(R.id.title_layout);
                    int height = (int) getResources().getDimension(R.dimen.apptitle_height);
                    if (title_layout.getHeight() != height) {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) title_layout.getLayoutParams();
                        lp.height = height;
                        title_layout.setLayoutParams(lp);
                        title_layout.setPadding(0, 0, 0, 0);
                    }
                }
                characterParser = CharacterParser.getInstance();
                pinyinComparator = new PinyinComparator();
                findViewById(R.id.citysearch_exit).setOnClickListener(this);
                find_search = (EditText) findViewById(R.id.find_search);
                citysearch_sidebar = (SideBar) findViewById(R.id.citysearch_sidebar);
                citysearch_dialog = (TextView) findViewById(R.id.citysearch_dialog);
                citysearch_sidebar.setTextView(citysearch_dialog);//设置右侧触摸监听
                citysearch_sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
                    public void onTouchingLetterChanged(String s) {
                        int position = citysearchAdapter.getPositionForSection(s.charAt(0));
                        if (position != -1) {
                            citysearch_listview.setSelection(position);
                        }
                    }
                });
                //根据输入框输入值的改变来过滤搜索
                find_search.addTextChangedListener(new TextWatcher() {
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                        filterData(s.toString());
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    public void afterTextChanged(Editable s) {
                    }
                });
                citysearch_listview = (ListView) findViewById(R.id.citysearch_listview);
                if (flag == 0) {
                    getProvinceData();
                    citysearch_listview.setOnItemClickListener(this);
                } else if (flag == 1) {
                    citysearch_all = (CheckBox) findViewById(R.id.citysearch_all);
                    citysearch_all.setVisibility(View.VISIBLE);
                    citysearch_finish = (TextView) findViewById(R.id.citysearch_finish);
                    citysearch_finish.setVisibility(View.VISIBLE);
                    citysearch_finish.setOnClickListener(this);
                    citysearch_all.setOnCheckedChangeListener(this);
                    provinceId = data.getStringExtra("provinceId");
                    citysearch_listview.setOnItemClickListener(onItemClickListener);
                    if (TextUtils.isEmpty(provinceId)) {
                        baseFinish();
                    }
                    getCityData();
                } else if (flag == 2) {
                    provinceId = data.getStringExtra("provinceId");
                    getCityData();
                    citysearch_listview.setOnItemClickListener(this);
                } else {
                    baseFinish();
                }
            }
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CityInfo cityInfo = mainList.get(position);
            cityInfo.setChecked(!cityInfo.isChecked());
            citysearchAdapter.notifyDataSetChanged();
        }
    };

    private void getCityData() {//城市列表
        getCityData.sendPostRequest(Urls.GetCityByProvince, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        if (list == null) {
                            list = new ArrayList<>();
                        } else {
                            list.clear();
                        }
                        if (mainList == null) {
                            mainList = new ArrayList<CityInfo>();
                        } else {
                            mainList.clear();
                        }
                        String name, codeid;
                        for (int i = 0; i < length; i++) {
                            name = jsonArray.getJSONObject(i).getString("city");
                            codeid = jsonArray.getJSONObject(i).getString("provinceid");
                            CityInfo sortModel = new CityInfo();
                            sortModel.setName(name);
                            sortModel.setCode(codeid);
                            //汉字转换成拼音
                            String pinyin = characterParser.getSelling(name);
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                sortModel.setSortLetters(sortString.toUpperCase());
                            } else {
                                sortModel.setSortLetters("#");
                            }
                            list.add(sortModel);
                            mainList.add(sortModel);
                        }
                        Collections.sort(list, pinyinComparator);
                        Collections.sort(mainList, pinyinComparator);
                        boolean isShow = false;
                        if (flag == 1) {
                            isShow = true;
                        } else if (flag == 2) {
                            isShow = false;
                        }
                        citysearchAdapter = new Citysearch2Adapter(SelectDistrictActivity.this, mainList, isShow);
                        citysearch_listview.setAdapter(citysearchAdapter);
                    } else {
                        Tools.showToast(SelectDistrictActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SelectDistrictActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(SelectDistrictActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void getProvinceData() {//省份列表
        getProvinceData.sendPostRequest(Urls.AllProvince, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        if (list == null) {
                            list = new ArrayList<>();
                        } else {
                            list.clear();
                        }
                        if (mainList == null) {
                            mainList = new ArrayList<CityInfo>();
                        } else {
                            mainList.clear();
                        }
                        String name, codeid;
                        for (int i = 0; i < length; i++) {
                            name = jsonArray.getJSONObject(i).getString("province");
                            codeid = jsonArray.getJSONObject(i).getString("id");
                            CityInfo sortModel = new CityInfo();
                            sortModel.setName(name);
                            sortModel.setCode(codeid);
                            //汉字转换成拼音
                            String pinyin = characterParser.getSelling(name);
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                sortModel.setSortLetters(sortString.toUpperCase());
                            } else {
                                sortModel.setSortLetters("#");
                            }
                            list.add(sortModel);
                            mainList.add(sortModel);
                        }
                        Collections.sort(list, pinyinComparator);
                        Collections.sort(mainList, pinyinComparator);
                        citysearchAdapter = new Citysearch2Adapter(SelectDistrictActivity.this, mainList, false);
                        citysearch_listview.setAdapter(citysearchAdapter);
                    } else {
                        Tools.showToast(SelectDistrictActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SelectDistrictActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(SelectDistrictActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private boolean isSearch;

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
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
            CityInfo sortModel;
            for (int i = 0; i < size; i++) {
                sortModel = list.get(i);
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr
                        .toString())) {
                    mainList.add(sortModel);
                }
            }
        }
        citysearchAdapter.updateListView(isSearch, mainList);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.citysearch_exit: {
                baseFinish();
            }
            case R.id.citysearch_finish: {
                String name = null;
                for (int i = 0; i < mainList.size(); i++) {
                    if (mainList.get(i).isChecked()) {
                        if (name == null) {
                            name = mainList.get(i).getName();
                        } else {
                            name = name + "," + mainList.get(i).getName();
                        }
                    }
                }
                if (name != null) {
                    String[] str = name.split(",");
                    if (str.length == mainList.size()) {
                        name = "0";
                    }
                }
                Intent data = new Intent();
                data.putExtra("name", name);
                setResult(AppInfo.SelectDistrictResultCode2, data);
                baseFinish();
            }
            break;
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (flag) {
            case 0: {
                Intent data = new Intent();
                data.putExtra("id", mainList.get(position).getCode());
                data.putExtra("name", mainList.get(position).getName());
                setResult(AppInfo.SelectDistrictResultCode1, data);
                baseFinish();
            }
            break;
            case 2: {
                Intent data = new Intent();
                data.putExtra("name", mainList.get(position).getName());
                setResult(AppInfo.SelectDistrictResultCode2, data);
                baseFinish();
            }
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (int i = 0; i < mainList.size(); i++) {
            CityInfo cityInfo = mainList.get(i);
            cityInfo.setChecked(isChecked);
        }
        citysearchAdapter.notifyDataSetChanged();
    }
}
