package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.EditPriceAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.mycorps.CorpGrabDetailInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 调整价格页面 V3.15
 */
public class EditPriceActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.editprice_title);
        appTitle.settingName("价格调整");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        priceAdjustment = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(EditPriceActivity.this));
                params.put("storemoney", storemoney);
                return params;
            }
        };
        priceAdjustment.setIsShowDialog(true);
    }

    private EditPriceAdapter editPriceAdapter;
    private ArrayList<CorpGrabDetailInfo> list;
    private TextView editprice_result;
    private String total_money;
    private double total_adjust_money;
    private NetworkConnection priceAdjustment;
    private String storemoney;
    private ImageView editprice_plus, editprice_minus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_price);
        initTitle();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        list = (ArrayList<CorpGrabDetailInfo>) data.getBundleExtra("data").getSerializable("list");
        boolean isSingle = data.getBooleanExtra("isSingle", false);
        initNetwork();
        String total_adjust = data.getStringExtra("total_adjust_money");
        if (!Tools.isEmpty(total_adjust)) {
            total_adjust_money = Double.parseDouble(total_adjust);
        }
        total_money = data.getStringExtra("total_money");
        editprice_result = (TextView) findViewById(R.id.editprice_result);
        editprice_result.setText("任务包总金额" + total_money + "元 - 分给队员" + Tools.removePoint(total_adjust_money + "") + "元 = 队长奖励");
        MyListView editprice_listview = (MyListView) findViewById(R.id.editprice_listview);
        editPriceAdapter = new EditPriceAdapter(this, list);
        editprice_listview.setAdapter(editPriceAdapter);
        ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 20);
        findViewById(R.id.editprice_finish).setOnClickListener(this);
        editprice_listview.setOnItemClickListener(this);
        editprice_plus = (ImageView) findViewById(R.id.editprice_plus);
        editprice_minus = (ImageView) findViewById(R.id.editprice_minus);
        if (isSingle) {
            findViewById(R.id.editprice_ly).setVisibility(View.GONE);
        } else {
            findViewById(R.id.editprice_ly).setVisibility(View.VISIBLE);
            editprice_plus.setOnClickListener(this);
            editprice_minus.setOnClickListener(this);
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (editPriceAdapter != null) {
            CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
            double money = Double.parseDouble(corpGrabDetailInfo.getCurrent());
            if (editPriceAdapter.isClick1()) {
                double primary = Double.parseDouble(corpGrabDetailInfo.getPrimary());
                if (money < primary) {
                    money++;
                    if (money >= primary) {
                        corpGrabDetailInfo.setMax(true);
                        corpGrabDetailInfo.setMin(false);
                    } else {
                        corpGrabDetailInfo.setMin(false);
                        corpGrabDetailInfo.setMax(false);
                    }
                    total_adjust_money++;
                    editprice_result.setText("任务包总金额" + total_money + "元 - 分给队员" +
                            Tools.removePoint(total_adjust_money + "") + "元 = 队长奖励");
                } else {
                    Tools.showToast(this, "已到上限");
                    corpGrabDetailInfo.setMax(true);
                    corpGrabDetailInfo.setMin(false);
                }
            } else if (editPriceAdapter.isClick2()) {
                if (money > 1) {
                    money--;
                    total_adjust_money--;
                    if (money <= 1) {
                        corpGrabDetailInfo.setMin(true);
                        corpGrabDetailInfo.setMax(false);
                    } else {
                        corpGrabDetailInfo.setMin(false);
                        corpGrabDetailInfo.setMax(false);
                    }
                    editprice_result.setText("任务包总金额" + total_money + "元 - 分给队员" +
                            Tools.removePoint(total_adjust_money + "") + "元 = 队长奖励");
                } else {
                    Tools.showToast(this, "已到下限");
                    corpGrabDetailInfo.setMin(true);
                    corpGrabDetailInfo.setMax(false);
                }
            }
            corpGrabDetailInfo.setCurrent(Tools.removePoint(money + ""));
            list.set(position, corpGrabDetailInfo);
            editPriceAdapter.setCurrent(true);
            editPriceAdapter.notifyDataSetChanged();
            editPriceAdapter.clearClick();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editprice_plus: {
                if (!list.isEmpty()) {
                    int index = 0;
                    int maxSize = 0;
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        CorpGrabDetailInfo corpGrabDetailInfo = list.get(i);
                        double money = Double.parseDouble(corpGrabDetailInfo.getCurrent());
                        double primary = Double.parseDouble(corpGrabDetailInfo.getPrimary());
                        if (money < primary) {
                            money++;
                            index++;
                            if (money == primary) {
                                maxSize++;
                                corpGrabDetailInfo.setMin(false);
                                corpGrabDetailInfo.setMax(true);
                            } else {
                                corpGrabDetailInfo.setMax(false);
                                corpGrabDetailInfo.setMin(false);
                            }
                            corpGrabDetailInfo.setCurrent(Tools.removePoint(money + ""));
                        } else {
                            maxSize++;
                            corpGrabDetailInfo.setMin(false);
                            corpGrabDetailInfo.setMax(true);
                        }
                        list.set(i, corpGrabDetailInfo);
                    }
                    if (maxSize == size) {
                        editprice_plus.setImageResource(R.mipmap.price_plus2);
                    } else {
                        editprice_plus.setImageResource(R.mipmap.price_plus);
                    }
                    if (index > 0) {
                        editprice_minus.setImageResource(R.mipmap.price_minus);
                    }
                    total_adjust_money = total_adjust_money + index;
                    editprice_result.setText("任务包总金额" + total_money + "元 - 分给队员" +
                            Tools.removePoint(total_adjust_money + "") + "元 = 队长奖励");
                    editPriceAdapter.setCurrent(true);
                    editPriceAdapter.notifyDataSetChanged();
                }
            }
            break;
            case R.id.editprice_minus: {
                if (!list.isEmpty()) {
                    int index = 0;
                    int size = list.size();
                    int minsize = 0;
                    for (int i = 0; i < size; i++) {
                        CorpGrabDetailInfo corpGrabDetailInfo = list.get(i);
                        double money = Double.parseDouble(corpGrabDetailInfo.getCurrent());
                        if (money > 1) {
                            money--;
                            index++;
                            if (money == 1) {
                                minsize++;
                                corpGrabDetailInfo.setMax(false);
                                corpGrabDetailInfo.setMin(true);
                            } else {
                                corpGrabDetailInfo.setMax(false);
                                corpGrabDetailInfo.setMin(false);
                            }
                            corpGrabDetailInfo.setCurrent(Tools.removePoint(money + ""));
                        } else {
                            minsize++;
                            corpGrabDetailInfo.setMax(false);
                            corpGrabDetailInfo.setMin(true);
                        }
                        list.set(i, corpGrabDetailInfo);
                    }
                    if (minsize == size) {
                        editprice_minus.setImageResource(R.mipmap.price_minus2);
                    } else {
                        editprice_minus.setImageResource(R.mipmap.price_minus);
                    }
                    if (index > 0) {
                        editprice_plus.setImageResource(R.mipmap.price_plus);
                    }
                    total_adjust_money = total_adjust_money - index;
                    editprice_result.setText("任务包总金额" + total_money + "元 - 分给队员" +
                            Tools.removePoint(total_adjust_money + "") + "元 = 队长奖励");
                    editPriceAdapter.setCurrent(true);
                    editPriceAdapter.notifyDataSetChanged();
                }
            }
            break;
            case R.id.editprice_finish: {
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        CorpGrabDetailInfo corpGrabDetailInfo = list.get(i);
                        if (storemoney == null) {
                            storemoney = corpGrabDetailInfo.getOutlet_id() + "_" + corpGrabDetailInfo.getCurrent();
                        } else {
                            storemoney = storemoney + "," + corpGrabDetailInfo.getOutlet_id() + "_" + corpGrabDetailInfo.getCurrent();
                        }
                    }
                    priceAdjustment();
                }
            }
            break;
        }
    }

    private void priceAdjustment() {
        priceAdjustment.sendPostRequest(Urls.PriceAdjustment, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(getApplicationContext(), "调整成功");
                        setResult(AppInfo.RESULT_EDITPRICE_JUMP_ASSIGN);
                        baseFinish();
                    } else {
                        Tools.showToast(EditPriceActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(EditPriceActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(EditPriceActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }
}
