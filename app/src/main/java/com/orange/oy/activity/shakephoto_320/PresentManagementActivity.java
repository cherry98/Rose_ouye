package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.PresentManagementAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.GiftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * V3.20礼品库管理
 */
public class PresentManagementActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, PresentManagementAdapter.AbandonButton {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.title);
        appTitle.settingName("礼品管理");
        appTitle.showBack(this);
        settingDel1();
    }

    private void settingDel1() {
        appTitle.hideExit();
        if (adapter != null) {
            adapter.setDelet(false);
        }
        appTitle.showIllustrate(R.mipmap.grrw_button_shanchu, new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel2();
            }
        });
    }

    private void settingDel2() {
        appTitle.hideIllustrate();
        if (adapter != null) {
            adapter.setDelet(true);
        }
        appTitle.settingExit("完成", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel1();
            }
        });
    }

    private void initNetworkConnection() {
        giftLibrary = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(PresentManagementActivity.this));
                params.put("merchant_id", merchant_id);  //	商户id【必传】
                return params;
            }
        };
        delGift = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(PresentManagementActivity.this));
                params.put("gift_id", gift_id);  //		礼品id【必传】
                return params;
            }
        };
    }

    public static boolean isRefresh;

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            getData();
        }
    }

    private PresentManagementAdapter adapter;
    private NetworkConnection giftLibrary, delGift;
    private String merchant_id;
    private ArrayList<GiftInfo> list;
    private MyListView list_view;
    private String gift_id;
    private String isOnclick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_management);
        initTitle();
        list = new ArrayList<>();
        merchant_id = getIntent().getStringExtra("merchant_id");
        isOnclick = getIntent().getStringExtra("isOnclick");
        list_view = (MyListView) findViewById(R.id.list_view);
        LinearLayout lin_Present = (LinearLayout) findViewById(R.id.lin_Present);
        initNetworkConnection();
        getData();
        adapter = new PresentManagementAdapter(PresentManagementActivity.this, list);
        list_view.setAdapter(adapter);
        adapter.setAbandonButtonListener(this);
        lin_Present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加礼品页面
                Intent intent = new Intent(PresentManagementActivity.this, AddPresentActivity.class);
                intent.putExtra("merchant_id", merchant_id);
                startActivity(intent);
            }
        });
        isRefresh = false;
    }


    private void getData() {
        giftLibrary.sendPostRequest(Urls.GiftLibrary, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<GiftInfo>();
                        } else {
                            list.clear();
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("gift_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                GiftInfo giftInfo = new GiftInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                giftInfo.setGift_id(object.getString("gift_id"));
                                giftInfo.setGift_money(object.getString("gift_money"));
                                giftInfo.setGift_name(object.getString("gift_name"));
                                giftInfo.setImg_url(object.getString("img_url"));
                                list.add(giftInfo);
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(PresentManagementActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PresentManagementActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PresentManagementActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != giftLibrary) {
            giftLibrary.stop(Urls.GiftLibrary);
        }
        if (null != delGift) {
            delGift.stop(Urls.DelGift);
        }
    }

    private void abandon() {
        delGift.sendPostRequest(Urls.DelGift, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        getData();
                        adapter.setDelet(false);
                        Tools.showToast(PresentManagementActivity.this, "删除成功");
                    } else {
                        Tools.showToast(PresentManagementActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PresentManagementActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PresentManagementActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onclick(int position) {
        gift_id = list.get(position).getGift_id();

        ConfirmDialog.showDialog(this, "您确定要删除吗？", true, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {
                adapter.setDelet(false);
            }

            @Override
            public void rightClick(Object object) {
                abandon();
            }
        });
    }

    @Override
    public void onitemclick(int position) {
        if (!Tools.isEmpty(isOnclick) && "1".equals(isOnclick)) {
            GiftInfo info = list.get(position);
            Intent intent = new Intent();
            intent.putExtra("gift_url", info.getImg_url());
            intent.putExtra("gift_name", info.getGift_name());
            intent.putExtra("gift_money", info.getGift_money());
            setResult(RESULT_OK, intent);
            baseFinish();
        }
    }
}
