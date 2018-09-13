package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

/**
 * 支付总金额 V3.17
 */
public class TotalPaymentActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.totalpayment_title);
        appTitle.settingName("支付总金额");
        appTitle.showBack(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_payment);
        initTitle();
        Intent data = getIntent();
        double total_cash = data.getDoubleExtra("total_cash", 0);
        double total_gift = data.getDoubleExtra("total_gift", 0);
        TextView totalpayment_cash = (TextView) findViewById(R.id.totalpayment_cash);
        TextView totalpayment_gift = (TextView) findViewById(R.id.totalpayment_gift);
        TextView totalpayment_service = (TextView) findViewById(R.id.totalpayment_service);
        TextView totalpayment_total = (TextView) findViewById(R.id.totalpayment_total);
        totalpayment_cash.setText(Tools.removePoint(total_cash + "") + "元");
        totalpayment_gift.setText(Tools.removePoint(total_gift + "") + "元");
        totalpayment_service.setText(Tools.removePoint(Tools.savaTwoByte((0.2 * total_cash + 0.1 * total_gift))) + "元");

        totalpayment_total.setText("支付总额 " + Tools.removePoint(Tools.savaTwoByte((1.2 * total_cash + 1.1 * total_gift))) + " 元");
        findViewById(R.id.totalpayment_button).setOnClickListener(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        //跳转支付页面
        baseFinish();
    }
}
