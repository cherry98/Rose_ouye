package com.orange.oy.activity.alipay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.WithdrawalMoneyListInfo;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 提现明细详情
 */
public class WithdrawalMoneyDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private TextView code, account, money, type, tax, realmoney, creattime, tip;
    private ArrayList<FriendsInfo> list = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawalmoneydetail);
        AppTitle title = (AppTitle) findViewById(R.id.apptitle);
        title.showBack(this);
        title.settingName("提现明细");
        Intent data = getIntent();
        WithdrawalMoneyListInfo withdrawalMoneyListInfo = (WithdrawalMoneyListInfo) data.getSerializableExtra("data");
        code = (TextView) findViewById(R.id.code);
        tax = (TextView) findViewById(R.id.tax);
        realmoney = (TextView) findViewById(R.id.realmoney);
        account = (TextView) findViewById(R.id.account);
        creattime = (TextView) findViewById(R.id.creattime);
        tip = (TextView) findViewById(R.id.tip);
        money = (TextView) findViewById(R.id.money);
        type = (TextView) findViewById(R.id.type);
        ListView listView = (ListView) findViewById(R.id.wmd_listview);
        settingData(withdrawalMoneyListInfo);
        if (!TextUtils.isEmpty(withdrawalMoneyListInfo.getFriends())) {
            try {
                JSONArray jsonArray = new JSONArray(withdrawalMoneyListInfo.getFriends());
                int size = jsonArray.length();
                JSONObject jsonObject;
                for (int i = 0; i < size; i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    FriendsInfo friendsInfo = new FriendsInfo();
                    friendsInfo.phone = jsonObject.getString("friendMobile");
                    friendsInfo.phone = friendsInfo.phone.substring(0, 3) + "****" + friendsInfo.phone.substring(friendsInfo.phone.length() - 4, friendsInfo.phone.length());
                    friendsInfo.state = jsonObject.getString("state");
                    if ("1".equals(friendsInfo.state)) {
                        friendsInfo.state = "该用户已实名认证";
                    } else {
                        friendsInfo.state = "该用户未实名认证";
                    }
                    list.add(friendsInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listView.setAdapter(new Myadapter());
    }

    private void settingData(WithdrawalMoneyListInfo withdrawalMoneyListInfo) {
        if ("0".equals(withdrawalMoneyListInfo.getType())) {//审核中
            tax.setVisibility(View.VISIBLE);
            realmoney.setVisibility(View.VISIBLE);
            creattime.setVisibility(View.VISIBLE);
            tip.setVisibility(View.GONE);
            type.setText("审核中");
            code.setText(withdrawalMoneyListInfo.getWithdrawaCode());
            account.setText(withdrawalMoneyListInfo.getAccount());
            money.setText(withdrawalMoneyListInfo.getMoney());
            tax.setText(withdrawalMoneyListInfo.getTaxMoney());
            realmoney.setText(withdrawalMoneyListInfo.getRealMoney());
            creattime.setText(withdrawalMoneyListInfo.getCreatDate());
        } else if ("1".equals(withdrawalMoneyListInfo.getType())) {//已提现
            tax.setVisibility(View.GONE);
            realmoney.setVisibility(View.GONE);
            creattime.setVisibility(View.VISIBLE);
            tip.setVisibility(View.GONE);
            type.setText("已提现");
            code.setText(withdrawalMoneyListInfo.getWithdrawaCode());
            account.setText(withdrawalMoneyListInfo.getAccount());
            money.setText(withdrawalMoneyListInfo.getMoney());
            creattime.setText(withdrawalMoneyListInfo.getCreatDate());
        } else {//异常
            tax.setVisibility(View.GONE);
            realmoney.setVisibility(View.GONE);
            creattime.setVisibility(View.GONE);
            tip.setVisibility(View.VISIBLE);
            type.setText("提现失败");
            code.setText(withdrawalMoneyListInfo.getWithdrawaCode());
            account.setText(withdrawalMoneyListInfo.getAccount());
            money.setText(withdrawalMoneyListInfo.getMoney());
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private class Myadapter extends BaseAdapter {

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
            Views views;
            if (convertView == null) {
                views = new Views();
                convertView = Tools.loadLayout(WithdrawalMoneyDetailActivity.this, R.layout.item_withdrawalmoneydetail);
                views.phone = (TextView) convertView.findViewById(R.id.phone);
                views.state = (TextView) convertView.findViewById(R.id.type);
                convertView.setTag(views);
            } else {
                views = (Views) convertView.getTag();
            }
            if (!list.isEmpty()) {
                FriendsInfo friendsInfo = list.get(position);
                views.phone.setText(friendsInfo.phone);
                views.state.setText(friendsInfo.state);
            }
            return convertView;
        }

        class Views {
            TextView phone, state;
        }
    }

    private class FriendsInfo {
        String phone;
        String state;//状态：0未认证，1已认证。
    }
}
