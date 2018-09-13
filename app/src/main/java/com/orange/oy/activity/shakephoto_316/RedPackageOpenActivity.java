package com.orange.oy.activity.shakephoto_316;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;

import static com.orange.oy.R.id.tv_chai;

/***
 * beibei  拆红包 {1.有钱，没钱 }
 */
public class RedPackageOpenActivity extends BaseActivity {
    private ImageView iv_closed;
    private TextView tv_theme, tv_time, tv_nomoney;
    private double money;  //是否拆到钱  =0表示没钱
    private String theme;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_package_open);

        iv_closed = (ImageView) findViewById(R.id.iv_closed);
        tv_theme = (TextView) findViewById(R.id.tv_theme);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_nomoney = (TextView) findViewById(R.id.tv_nomoney);
        initview();

        iv_closed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShakeAlbumDetailActivity.isRefresh = true;
                finish();
            }
        });


    }

    private void initview() {
        money = getIntent().getDoubleExtra("money", 0);
        theme = getIntent().getStringExtra("theme");

        if (!Tools.isEmpty(theme)) {
            tv_theme.setText(theme);
        }
        if (!Tools.isEmpty(time)) {
            tv_time.setText(time);
        }
        if (money != 0) {
            tv_nomoney.setText("¥" + money);
            tv_nomoney.setTextColor(Color.parseColor("#FFD42526"));
            tv_nomoney.setTextSize(26);

        } else {
            tv_nomoney.setText("红包已抢完");
            tv_nomoney.setTextColor(Color.parseColor("#FF231916"));
            tv_nomoney.setTextSize(24);

        }
    }
}
