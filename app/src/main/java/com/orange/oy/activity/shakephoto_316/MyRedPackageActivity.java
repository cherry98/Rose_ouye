package com.orange.oy.activity.shakephoto_316;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.lin_Nodata_prompt;
import static com.orange.oy.R.id.myaccount_money1;

/***
 *
 * beibei   我的红包页面
 *
 */
public class MyRedPackageActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private ListView myredpackage_listview;
    private MyAdapter myAdapter;
    private NetworkConnection myRedPack;
    private int page;
    private ArrayList<RedPackageInfo> list = new ArrayList<>();
    private LinearLayout lin_totalmoney;
    private TextView oumi_money;
    private NetworkView lin_Nodata;


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.titleview);
        appTitle.settingName("我的红包");
        appTitle.showBack(this);

    }

    private void initNetworkConnection() {
        myRedPack = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyRedPackageActivity.this));
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myRedPack != null) {
            myRedPack.stop(Urls.MyRedPack);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_red_package);
        myredpackage_listview = (ListView) findViewById(R.id.myredpackage_listview);
        lin_totalmoney = (LinearLayout) findViewById(R.id.lin_totalmoney);
        oumi_money = (TextView) findViewById(R.id.oumi_money);
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        initTitle();
        initNetworkConnection();
        getData();
        myAdapter = new MyAdapter();
        myredpackage_listview.setAdapter(myAdapter);
    }

    private void getData() {
        myRedPack.sendPostRequest(Urls.MyRedPack, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {

                        if (!jsonObject.isNull("data")) {
                            list = new ArrayList<RedPackageInfo>();
                            String total_money = jsonObject.optJSONObject("data").getString("total_money");//红包总金额

                            if (total_money != null) {
                                if (TextUtils.isEmpty(total_money)) {
                                    total_money = "-";
                                } else {
                                    double d = Tools.StringToDouble(total_money);
                                    if (d - (int) d > 0) {
                                        total_money = String.valueOf(d);
                                    } else {
                                        total_money = String.valueOf((int) d);
                                    }
                                }
                                oumi_money.setText(String.format(getResources().getString(R.string.account_money),
                                        "¥" + total_money));
                            } else {
                                oumi_money.setText("-");
                            }

                            JSONArray jsonArray = jsonObject.optJSONObject("data").getJSONArray("list");
                            if (jsonArray.length() > 0) {
                                lin_Nodata.setVisibility(View.GONE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    RedPackageInfo redPackageInfo = new RedPackageInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    redPackageInfo.setActivity_name(object.getString("activity_name"));
                                    redPackageInfo.setCreate_time(object.getString("create_time"));
                                    redPackageInfo.setMoney(object.getString("money"));
                                    list.add(redPackageInfo);
                                }
                            } else {
                                myredpackage_listview.setVisibility(View.GONE);
                                lin_Nodata.setVisibility(View.VISIBLE);
                                lin_Nodata.NoSearch("无已领取的红包哦~,快去领取任务吧！");
                            }

                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(MyRedPackageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyRedPackageActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyRedPackageActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }


    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder(); //R.layout.item_redpackage
                convertView = Tools.loadLayout(MyRedPackageActivity.this, R.layout.item_red_list);

                viewHolder.item_project = (TextView) convertView.findViewById(R.id.item_project);
                viewHolder.item_money = (TextView) convertView.findViewById(R.id.item_money);
                viewHolder.item_time = (TextView) convertView.findViewById(R.id.item_time);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (list != null) {
                RedPackageInfo redPackageInfo = list.get(position);
                viewHolder.item_project.setText(redPackageInfo.activity_name);
                viewHolder.item_money.setText("¥" + redPackageInfo.getMoney() + "");
                viewHolder.item_time.setText(redPackageInfo.getCreate_time());
            }
            return convertView;
        }

        class ViewHolder {
            TextView item_project, item_money, item_time;
        }
    }

    class RedPackageInfo {


        /**
         * activity_name : 活动名称
         * create_time : 创建时间
         * money : 金额
         */

        private String activity_name;
        private String create_time;
        private String money;

        public String getActivity_name() {
            return activity_name;
        }

        public void setActivity_name(String activity_name) {
            this.activity_name = activity_name;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
