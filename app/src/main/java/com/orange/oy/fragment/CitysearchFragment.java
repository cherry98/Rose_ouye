package com.orange.oy.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.CitysearchAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
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

/**
 * 城市搜索页
 */
public class CitysearchFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    public CitysearchFragment() {
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.citysearch_exit: {
                if (onCitysearchExitClickListener != null) {
                    onCitysearchExitClickListener.exitClick();
                }
            }
            break;
        }
    }

    public void onStop() {
        super.onStop();
        if (getData != null) {
            getData.stop(Urls.CountyByCity);
        }
        if (myHandler != null) {
            myHandler.removeMessages(1);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 1 || isSearch) {
            if (onCitysearchExitItemClickListener != null && onCitysearchExitClickListener != null) {
                CityInfo cityInfo = mainList.get(position);
                Map<String, String> map = new HashMap<>();
                map.put("id", cityInfo.getCode());
                map.put("name", cityInfo.getName());
                map.put("province", cityInfo.getProvince());
                map.put("county", cityInfo.getCounty());
                if (!isChange) {
                    AppInfo.setCityName(getContext(), cityInfo.getProvince(), cityInfo.getName(), cityInfo.getCounty());
                }
                onCitysearchExitItemClickListener.ItemClick(map);
            }
        } else if (citysearchAdapter.getSelectCity() != null) {//热门城市
            String city = citysearchAdapter.getSelectCity();
            String province = "";
            if (!Tools.isEmpty(city)) {
                switch (city) {
                    case "北京市":
                    case "上海市":
                    case "天津市":
                    case "重庆市":
                        province = city;
                        break;
                    case "广州市":
                    case "深圳市":
                        province = "广东省";
                        break;
                    case "武汉市":
                        province = "湖北省";
                        break;
                    case "西安市":
                        province = "陕西省";
                        break;
                    case "南京市":
                        province = "江苏省";
                        break;
                    case "杭州市":
                        province = "浙江省";
                        break;
                    case "成都市":
                        province = "四川省";
                        break;
                }
            }
            Map<String, String> map = new HashMap<>();
            map.put("id", "");
            map.put("name", city);
            map.put("province", province);
            map.put("county", "");
            if (!isChange) {
                AppInfo.setCityName(getContext(), province, city, "");
            }
            onCitysearchExitItemClickListener.ItemClick(map);
        }
        find_search.setText("");
    }

    public void setOnCitysearchItemClickListener(OnCitysearchItemClickListener listener) {
        onCitysearchExitItemClickListener = listener;
    }

    public interface OnCitysearchExitClickListener {
        void exitClick();
    }

    public interface OnCitysearchItemClickListener {
        void ItemClick(Map<String, String> map);
    }

    private void initNetworkConnection() {
        getData = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                return params;
            }
        };
        getData.setIsShowDialog(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_citysearch, container, false);
        return mView;
    }

    public void setOnCitysearchExitClickListener(OnCitysearchExitClickListener listener) {
        this.onCitysearchExitClickListener = listener;
    }

    private View mView;
    private ListView citysearch_listview;
    private EditText find_search;
    private SideBar citysearch_sidebar;
    private CitysearchAdapter citysearchAdapter;
    private ArrayList<CityInfo> list;
    private ArrayList<CityInfo> mainList;
    private OnCitysearchExitClickListener onCitysearchExitClickListener;
    private OnCitysearchItemClickListener onCitysearchExitItemClickListener;
    private CharacterParser characterParser;
    private TextView citysearch_dialog;
    private NetworkConnection getData;
    private MyHandler myHandler;
    private boolean isChange;
    private boolean isShowAll2;

    public void setShowAll2(boolean showAll2) {
        isShowAll2 = showAll2;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initNetworkConnection();
        myHandler = new MyHandler();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mView.findViewById(R.id.citysearch_exit).setOnClickListener(this);
        find_search = (EditText) mView.findViewById(R.id.find_search);
        citysearch_sidebar = (SideBar) mView.findViewById(R.id.citysearch_sidebar);
        citysearch_dialog = (TextView) mView.findViewById(R.id.citysearch_dialog);
        citysearch_sidebar.setTextView(citysearch_dialog);//设置右侧触摸监听
        citysearch_sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public void onTouchingLetterChanged(String s) {
                if (s.equals("#")) {
                    citysearch_listview.setSelection(0);
                } else if (s.equals("$")) {
                    citysearch_listview.setSelection(1);
                } else {
                    if (citysearchAdapter != null) {
                        int position = citysearchAdapter.getPositionForSection(s.charAt(0));
                        if (position != -1) {
                            citysearch_listview.setSelection(position);
                        }
                    }
                }
            }
        });
        //根据输入框输入值的改变来过滤搜索
        find_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myHandler.removeMessages(1);
                Message message = myHandler.obtainMessage();
                message.what = 1;
                message.obj = s.toString();
                myHandler.sendMessageDelayed(message, 200);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        citysearch_listview = (ListView) mView.findViewById(R.id.citysearch_listview);
        citysearch_listview.setOnItemClickListener(this);
        if (isShowAll2) {
            mView.findViewById(R.id.citysearch_all2).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.citysearch_all2).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {//选全国
                    if (onCitysearchExitItemClickListener != null) {
                        Map<String, String> map = new HashMap<>();
                        map.put("province", "全国");
                        map.put("id", "");
                        map.put("name", "");
                        map.put("county", "");
                        onCitysearchExitItemClickListener.ItemClick(map);
                    }
                }
            });
        }
        getData();
    }

    private void getData() {
        getData.sendPostRequest(Urls.CountyByCity, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
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
                            mainList = new ArrayList<>();
                        } else {
                            mainList.clear();
                        }
                        String name, provinceId, province, county;
                        for (int i = 0; i < length; i++) {
                            name = jsonArray.getJSONObject(i).getString("city");
                            provinceId = jsonArray.getJSONObject(i).optString("provinceid");
                            province = jsonArray.getJSONObject(i).getString("province");
                            county = jsonArray.getJSONObject(i).getString("county");
                            CityInfo sortModel = new CityInfo();
                            sortModel.setName(name);
                            sortModel.setCode(provinceId);
                            sortModel.setProvince(province);
                            sortModel.setCounty(county);
                            //汉字转换成拼音
                            String pinyin = characterParser.getSelling((TextUtils.isEmpty(county)) ? name : county);
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
                        CityInfo ci = new CityInfo();
                        list.add(0, ci);
                        list.add(0, ci);
                        mainList.add(0, ci);
                        mainList.add(0, ci);
                        citysearchAdapter = new CitysearchAdapter(getContext(), mainList);
                        citysearch_listview.setAdapter(citysearchAdapter);
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private PinyinComparator pinyinComparator;

    private boolean isSearch;

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        if (list == null) return;
        if (TextUtils.isEmpty(filterStr)) {
            if (mainList == null) {
                mainList = new ArrayList<>();
            }
            mainList.clear();
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
            for (int i = 2; i < size; i++) {
                sortModel = list.get(i);
                String name = sortModel.getProvince() + sortModel.getName() + sortModel.getCounty();
                if (name.contains(filterStr) ||
                        characterParser.getSelling(name).startsWith(filterStr)) {
                    mainList.add(sortModel);
                }
            }
        }
        if (citysearchAdapter != null)
            citysearchAdapter.updateListView(isSearch, mainList);
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            filterData(msg.obj + "");
        }
    }

}
