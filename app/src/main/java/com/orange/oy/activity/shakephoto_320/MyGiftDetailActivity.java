package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.MyGiftDetailAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.info.MyGiftDetailInfo;
import com.orange.oy.view.AppTitle;

import java.util.ArrayList;

/**
 * 我的礼品物流详情 V3.20(多个物流信息时使用)
 */
public class MyGiftDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mygiftdetail_title);
        appTitle.settingName("礼品详情");
        appTitle.showBack(this);
    }

    private ArrayList<MyGiftDetailInfo> list;
    private MyGiftDetailAdapter myGiftDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gift_detail);
        initTitle();
        PullToRefreshListView mygiftdetail_listview = (PullToRefreshListView) findViewById(R.id.mygiftdetail_listview);
        list = (ArrayList<MyGiftDetailInfo>) getIntent().getBundleExtra("data").getSerializable("expressInfos");
        myGiftDetailAdapter = new MyGiftDetailAdapter(this, list);
        mygiftdetail_listview.setAdapter(myGiftDetailAdapter);
        mygiftdetail_listview.setOnItemClickListener(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myGiftDetailAdapter != null) {
            MyGiftDetailInfo myGiftDetailInfo = list.get(position - 1);
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
