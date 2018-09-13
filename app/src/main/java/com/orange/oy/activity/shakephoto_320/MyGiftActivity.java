package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.MyGiftAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MyGiftDetailInfo;
import com.orange.oy.info.shakephoto.PrizeCardInfo;
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

/**
 * 我的礼品页面 V3.20
 */
public class MyGiftActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mygift_title);
        appTitle.settingName("我的礼品");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        myGiftDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyGiftActivity.this));
                params.put("page", page + "");
                return params;
            }
        };
    }

    private PullToRefreshListView mygift_listview;
    private NetworkConnection myGiftDetail;
    private int page = 1;
    private ArrayList<PrizeCardInfo> list;
    private MyGiftAdapter myGiftAdapter;
    private NetworkView lin_Nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gift);
        list = new ArrayList<>();
        initTitle();
        initNetwork();
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        mygift_listview = (PullToRefreshListView) findViewById(R.id.mygift_listview);
        myGiftAdapter = new MyGiftAdapter(this, list);
        mygift_listview.setAdapter(myGiftAdapter);
        getData();
        mygift_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        mygift_listview.setOnItemClickListener(this);
    }

    private void getData() {
        myGiftDetail.sendPostRequest(Urls.MyGiftDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            list.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("gift_list");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            lin_Nodata.setVisibility(View.GONE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                PrizeCardInfo prizeCardInfo = new PrizeCardInfo();
                                prizeCardInfo.setGift_user_id(object.getString("gift_user_id"));
                                prizeCardInfo.setGift_money(object.getString("gift_money"));
                                prizeCardInfo.setGift_name(object.getString("gift_name"));
                                prizeCardInfo.setImg_url(object.getString("img_url"));
                                prizeCardInfo.setMerchant(object.getString("merchant"));
                                prizeCardInfo.setDelivery_state(object.getString("delivery_state"));
                                prizeCardInfo.setAsconsignee_phone(object.getString("asconsignee_phone"));
                                prizeCardInfo.setConsignee_name(object.getString("consignee_name"));
                                prizeCardInfo.setOrder_no(object.getString("order_no"));
                                JSONArray jsonArray1 = object.getJSONArray("expressInfos");
                                ArrayList<MyGiftDetailInfo> list_detail = new ArrayList<MyGiftDetailInfo>();
                                if (jsonArray1 != null && jsonArray1.length() > 0) {
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        MyGiftDetailInfo myGiftDetailInfo = new MyGiftDetailInfo();
                                        JSONObject object1 = jsonArray1.getJSONObject(j);
                                        myGiftDetailInfo.setExpress_company(object1.getString("express_company"));
                                        myGiftDetailInfo.setExpress_number(object1.getString("express_number"));
                                        myGiftDetailInfo.setGift_name(object1.getString("gift_name"));
                                        myGiftDetailInfo.setOfficial_phone(object1.getString("official_phone"));
                                        myGiftDetailInfo.setExpress_type(object1.getString("express_type"));
                                        list_detail.add(myGiftDetailInfo);
                                    }
                                    if (jsonArray1.length() > 1) {
                                        prizeCardInfo.setSecond(true);
                                    } else {
                                        prizeCardInfo.setSecond(false);
                                    }
                                    prizeCardInfo.setExpressInfos(list_detail);
                                }
                                list.add(prizeCardInfo);
                            }
                            mygift_listview.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                mygift_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                mygift_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (myGiftAdapter != null) {
                                myGiftAdapter.notifyDataSetChanged();
                            }
                        } else {
                            lin_Nodata.setVisibility(View.VISIBLE);
                            lin_Nodata.NoSearch("还没有礼品卡券呢~");
                        }
                    } else {
                        Tools.showToast(MyGiftActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyGiftActivity.this, getResources().getString(R.string.network_error));
                }
                mygift_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyGiftActivity.this, getResources().getString(R.string.network_volleyerror));
                mygift_listview.onRefreshComplete();
                lin_Nodata.NoNetwork();
                lin_Nodata.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myGiftAdapter != null) {
            PrizeCardInfo prizeCardInfo = list.get(position - 1);
            if ("1".equals(prizeCardInfo.getDelivery_state())) {//已发货可查看详情
                if (prizeCardInfo.isSecond()) {//跳转多个物流信息详情
                    Intent intent = new Intent(this, MyGiftDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("expressInfos", prizeCardInfo.getExpressInfos());
                    intent.putExtra("data", bundle);
                    startActivity(intent);
                } else {//单个物流信息详情
                    MyGiftDetailInfo myGiftDetailInfo = prizeCardInfo.getExpressInfos().get(0);
                    Intent intent = new Intent(this, MyLogisticsActivity.class);
                    intent.putExtra("type", myGiftDetailInfo.getExpress_type());
                    intent.putExtra("gift_name", myGiftDetailInfo.getGift_name());
                    intent.putExtra("official_phone", myGiftDetailInfo.getOfficial_phone());
                    intent.putExtra("express_number", myGiftDetailInfo.getExpress_number());
                    intent.putExtra("express_company", myGiftDetailInfo.getExpress_company());
                    startActivity(intent);
                }
            }
        }
    }
}
