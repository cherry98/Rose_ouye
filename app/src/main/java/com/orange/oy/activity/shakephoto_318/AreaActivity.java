package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.CityInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * V3.18 选择省份
 */
public class AreaActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.seconderdes_title);
        appTitle.settingName("按地区挑选");
        appTitle.showBack(this);
    }

    private NetworkConnection getProvinceData, getCityData;
    private String Isvisible;

    private void initNetworkConnection() {
        getProvinceData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                return params;
            }
        };
        getProvinceData.setIsShowDialog(true);
    }

    protected void onStop() {
        super.onStop();
        if (getProvinceData != null) {
            getProvinceData.stop(Urls.AllProvince);
        }
    }

    private ListView mlistview;
    private ArrayList<CityInfo> list;
    private MyAdapter myAdapter;
    private TextView citysearch_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        initTitle();
        initNetworkConnection();
        list = new ArrayList<>();
        visible_type = getIntent().getStringExtra("visible_type");
        city = getIntent().getStringExtra("city");
        mlistview = (ListView) findViewById(R.id.mlistview);
        citysearch_finish = (TextView) findViewById(R.id.citysearch_finish);
        getProvinceData();
        mlistview.setOnItemClickListener(this);
        onclick();
    }

    private void onclick() {
        citysearch_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //提交按钮
                if (!Tools.isEmpty(city)) {
                    Intent intent = new Intent();
                    intent.putExtra("visible_type", visible_type);
                    intent.putExtra("city", city); // 山西省-太原市,山西省-大同市,山西省-阳泉市河北省-唐山市,河北省-秦皇岛市,河北省-邯郸市,河北省-邢台市
                    setResult(AppInfo.REQUEST_CODE_ISVISIBLE, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data = new Intent(this, CityActivity.class);
       /* if (position == 0) {
            city = "全国";
            visible_type = data.getStringExtra("visible_type");
            CityInfo cityInfo = list.get(position);
            cityInfo.setChecked(true);
            setResult(AppInfo.REQUEST_CODE_ISVISIBLE, data);
            baseFinish();
        } else {*/
        CityInfo cityInfo = list.get(position);
        cityInfo.setChecked(true);
        data.putExtra("id", list.get(position).getCode());
        data.putExtra("visible_type", visible_type);
        data.putExtra("name", list.get(position).getName());
        data.putExtra("temp2", temp2.toString().replaceAll(" ", ""));
        startActivityForResult(data, 0);


    }

    private String city, visible_type;//返回的参数
    private String provicename; //已选择的省份的名字
    private List<String> temp = new ArrayList<>();  //省
    private List<String> temp2 = new ArrayList<>();  //市


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CITYANDPROVINCE) {
            switch (requestCode) {    //点击完成的时候才有
                case 0: {//可见或者是不可见
                    if (data != null) {
                        if (Tools.isEmpty(city)) {
                            city = data.getStringExtra("city");
                        } else {
                            city = city + "," + data.getStringExtra("city");
                            Tools.d("city==========>>>" + city);
                        }
                        provicename = data.getStringExtra("provice");
                        visible_type = data.getStringExtra("visible_type");

                        //  河北省-秦皇岛市,河北省-邯郸市,河北省-邢台市

                        city = city.replaceAll(" ", "");
                        String[] shengshi = city.split(",");

                        for (int i = 0; i < shengshi.length; i++) {
                            String str = shengshi[i];
                            int index = str.indexOf("-");
                            String str1 = str.substring(0, index);
                            if (!temp.contains(str1)) {
                                temp.add(str1);
                            }
                            String str2 = str.substring(index + 1, str.length());
                            if (!temp2.contains(str2)) {
                                temp2.add(str2);
                            }
                        }
                        //已选择的省弄成已选
                        for (int i = 0; i < list.size(); i++) {
                            for (int j = 0; j < temp.size(); j++) {
                                if (list.get(i).getName().contains(temp.get(j))) {
                                    list.get(i).setChecked(true);
                                }
                            }

                        }
                        myAdapter.notifyDataSetChanged();

                    }
                }
                break;
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(AreaActivity.this, R.layout.view_item_citysearch4);
                viewHolder.name = (TextView) convertView.findViewById(R.id.citysearch3_name);
                viewHolder.tv_tag = (TextView) convertView.findViewById(R.id.tv_tag);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CityInfo cityInfo = list.get(position);
            viewHolder.name.setText(cityInfo.getName());
            if (cityInfo.isChecked()) {
                viewHolder.tv_tag.setText("已选");
            } else {
                viewHolder.tv_tag.setText("");
            }
            return convertView;
        }

        private class ViewHolder {
            TextView title, name, tv_tag;
        }

    }

    private void getProvinceData() {//省份列表
        getProvinceData.sendPostRequest(Urls.AllProvince, new Response.Listener<String>() {
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
                        String name, codeid;
                     /*   CityInfo cityInfo = new CityInfo();
                        cityInfo.setName("全国");
                        cityInfo.setCode("-1");
                        list.add(0, cityInfo);*/
                        for (int i = 0; i < length; i++) {
                            name = jsonArray.getJSONObject(i).getString("province");
                            codeid = jsonArray.getJSONObject(i).getString("id");
                            CityInfo sortModel = new CityInfo();
                            sortModel.setName(name);
                            sortModel.setCode(codeid);
                            list.add(sortModel);
                        }

                        myAdapter = new MyAdapter();
                        mlistview.setAdapter(myAdapter);
                        if (!Tools.isEmpty(city)) {
                            city = city.replaceAll(" ", "");
                            String[] shengshi = city.split(",");

                            for (int i = 0; i < shengshi.length; i++) {
                                String str = shengshi[i];
                                int index = str.indexOf("-");
                                String str1 = str.substring(0, index);
                                if (!temp.contains(str1)) {
                                    temp.add(str1);
                                }
                                String str2 = str.substring(index + 1, str.length());
                                if (!temp2.contains(str2)) {
                                    temp2.add(str2);
                                }
                            }
                            //已选择的省弄成已选
                            for (int i = 0; i < list.size(); i++) {
                                for (int j = 0; j < temp.size(); j++) {
                                    if (list.get(i).getName().contains(temp.get(j))) {
                                        list.get(i).setChecked(true);
                                    }
                                }

                            }
                            myAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Tools.showToast(AreaActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AreaActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(AreaActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

}
