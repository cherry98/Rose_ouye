package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
 * V3.18 选择多个市
 */
public class CityActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private NetworkConnection getCityData;

    private void initNetworkConnection() {
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
    }

    private ListView mlistview;
    private ArrayList<CityInfo> list;
    private MyAdapter myAdapter;
    private String id, CityName;
    private TextView tv_name;
    private String provinceId;
    private CheckBox citysearch_all;//城市全选
    private TextView citysearch_finish;//城市选择完成按钮
    private String visible_type;
    private ImageView iv_back;
    private String temp2; //所选择的市
    private List<String> selectCitylist;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        tv_name = (TextView) findViewById(R.id.tv_name); //省的名字
        mlistview = (ListView) findViewById(R.id.mlistview);
        citysearch_all = (CheckBox) findViewById(R.id.citysearch_all);
        citysearch_finish = (TextView) findViewById(R.id.citysearch_finish);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        citysearch_finish.setOnClickListener(this);
        citysearch_all.setOnCheckedChangeListener(this);
        Intent data = getIntent();
        visible_type = data.getStringExtra("visible_type");
        provinceId = data.getStringExtra("id");
        CityName = data.getStringExtra("name");
        temp2 = data.getStringExtra("temp2");
        if (!Tools.isEmpty(CityName)) {
            tv_name.setText(CityName);
        }
        selectCitylist = new ArrayList<>();
        initNetworkConnection();
        list = new ArrayList<>();
        if (!Tools.isEmpty(temp2)) {
            String s = temp2.substring(1, temp2.length() - 1);
            String[] str = s.split(",");
            for (int i = 0; i < str.length; i++) {
                selectCitylist.add(str[i]);
            }
        }

        myAdapter = new MyAdapter();
        mlistview.setAdapter(myAdapter);
        getCityData();
        mlistview.setOnItemClickListener(this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseFinish();
            }
        });

    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private boolean isAllSelect;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CityInfo cityInfo = list.get(position);
        cityInfo.setChecked(!cityInfo.isChecked());
        if (getChechedSum() == list.size()) {
            citysearch_all.setChecked(true);
        } else {
            citysearch_all.setChecked(false);
        }
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 计算选择的数目 验证不等于空
     *
     * @return
     */
    private int getChechedSum() {
        int checkNum = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) {
                checkNum++;
            }
        }
        return checkNum;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.citysearch_finish: { //点击提交，回到上个页面
                if (0 == getChechedSum()) {
                    Tools.showToast(getBaseContext(), "请选择城市");
                    return;
                } else {
                    String name = null;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isChecked()) {
                            if (name == null) {
                                name = CityName + "-" + list.get(i).getName();
                            } else {
                                name = name + "," + CityName + "-" + list.get(i).getName();
                            }
                        }
                    }
                    Intent data = new Intent();
                    data.putExtra("city", name);
                    data.putExtra("provice", CityName);
                    data.putExtra("visible_type", visible_type);

                    setResult(AppInfo.REQUEST_CITYANDPROVINCE, data);
                    baseFinish();
                }
            }

            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (int i = 0; i < list.size(); i++) {
            CityInfo cityInfo = list.get(i);
            cityInfo.setChecked(isChecked);
        }
        myAdapter.notifyDataSetChanged();
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
                convertView = Tools.loadLayout(CityActivity.this, R.layout.view_item_citysearch5);
                viewHolder.name = (TextView) convertView.findViewById(R.id.citysearch3_name);
                viewHolder.citysearch3_checkbox = (ImageView) convertView.findViewById(R.id.citysearch3_checkbox);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CityInfo cityInfo = list.get(position);
            viewHolder.name.setText(cityInfo.getName());
            if (cityInfo.isChecked()) {
                viewHolder.citysearch3_checkbox.setImageResource(R.mipmap.round_selected);
            } else {
                viewHolder.citysearch3_checkbox.setImageResource(R.mipmap.round_notselect);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView title, name;
            ImageView citysearch3_checkbox;
        }
    }

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
                        String name, codeid;
                        for (int i = 0; i < length; i++) {
                            name = jsonArray.getJSONObject(i).getString("city");
                            codeid = jsonArray.getJSONObject(i).getString("provinceid");
                            CityInfo sortModel = new CityInfo();
                            sortModel.setName(name);
                            sortModel.setCode(codeid);
                            list.add(sortModel);
                        }


                        //已选择的市弄成已选
                        for (int i = 0; i < list.size(); i++) {
                            for (int j = 0; j < selectCitylist.size(); j++) {
                                if (list.get(i).getName().equals(selectCitylist.get(j))) {
                                    list.get(i).setChecked(true);

                                }
                            }
                        }

                        if (null != myAdapter) {
                            myAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Tools.showToast(CityActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CityActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CityActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }
}
