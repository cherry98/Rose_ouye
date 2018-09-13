package com.orange.oy.activity.bright;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.info.TransferInfo;
import com.orange.oy.util.ListDataSave;
import com.orange.oy.view.AppTitle;

import java.util.ArrayList;

public class BrightTwoCodeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private void initTitle() {
        AppTitle taskitemlist_title = (AppTitle) findViewById(R.id.bright_title_twocode);
        taskitemlist_title.settingName("身份证二维码");
        taskitemlist_title.showBack(this);
    }

    private TextView brighttwocode_id, brighttwocode_name, brighttwocode_addr;
    private ArrayList<TransferInfo> list = new ArrayList<>();
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_two_code);
        initTitle();
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        brighttwocode_id = (TextView) findViewById(R.id.brighttwocode_id);
        brighttwocode_name = (TextView) findViewById(R.id.brighttwocode_name);
        brighttwocode_addr = (TextView) findViewById(R.id.brighttwocode_addr);
        brighttwocode_id.setText(data.getStringExtra("store_num"));
        brighttwocode_name.setText(data.getStringExtra("store_name"));
        brighttwocode_addr.setText(data.getStringExtra("city3"));
        TransferInfo transferInfo = new TransferInfo();
        transferInfo.setBrand(data.getStringExtra("brand"));
        transferInfo.setCode(data.getStringExtra("code"));
        transferInfo.setCity3(data.getStringExtra("city3"));
        transferInfo.setOutletid(data.getStringExtra("outletid"));
        transferInfo.setProject_id(data.getStringExtra("project_id"));
        transferInfo.setProjectname(data.getStringExtra("projectname"));
        transferInfo.setStore_name(data.getStringExtra("store_name"));
        transferInfo.setStore_num(data.getStringExtra("store_num"));
        transferInfo.setMytype(data.getStringExtra("mytype"));
        transferInfo.setId(data.getStringExtra("id"));
        transferInfo.setProvince(data.getStringExtra("province"));
        transferInfo.setCity(data.getStringExtra("city"));
        transferInfo.setLongtitude(data.getStringExtra("longtitude"));
        transferInfo.setLatitude(data.getStringExtra("latitude"));
        transferInfo.setPhoto_compression(data.getStringExtra("photo_compression"));
        transferInfo.setIs_watermark(data.getIntExtra("is_watermark", 0));
        transferInfo.setIs_takephoto(data.getStringExtra("is_takephoto"));
        transferInfo.setType(data.getStringExtra("type"));
        transferInfo.setIs_exe(data.getStringExtra("is_exe"));
        transferInfo.setIs_desc(data.getStringExtra("is_desc"));
        transferInfo.setNumber(data.getStringExtra("number"));
        transferInfo.setIsOffline(data.getIntExtra("isOffline", 0));
        transferInfo.setIs_record(data.getIntExtra("is_record", 0));
        list.add(transferInfo);
        //SharedPreferences存储方式
        ListDataSave listDataSave = new ListDataSave(getApplicationContext(), "xie");
        listDataSave.setDataList("transferInfo", list);
    }


    public static boolean isRefresh = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            baseFinish();
        }
        isRefresh = false;
    }

    @Override
    public void onBack() {
        baseFinish();
    }

}
