package com.orange.oy.activity.alipay;

import android.os.Bundle;
import android.view.View;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;

/**
 * 提交成功
 */
public class CommitsuccessActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commitsuccess);
        findViewById(R.id.myaccount_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                baseFinish();
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
