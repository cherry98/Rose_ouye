package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.fragment.CitysearchFragment;

import java.util.Map;

public class SelectCityActivity extends BaseActivity implements CitysearchFragment.OnCitysearchExitClickListener,
        CitysearchFragment.OnCitysearchItemClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcity);
        boolean isShowAll2 = getIntent().getBooleanExtra("isShowAll2", false);
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction ft = fMgr.beginTransaction();
        CitysearchFragment citysearchFragment = new CitysearchFragment();
        citysearchFragment.setShowAll2(isShowAll2);
        citysearchFragment.setChange(true);
        citysearchFragment.setOnCitysearchExitClickListener(this);
        citysearchFragment.setOnCitysearchItemClickListener(this);
        ft.replace(R.id.aboveLayout, citysearchFragment, "citysearchFragment");
        ft.commit();
    }

    public void exitClick() {
        baseFinish();
    }

    public void ItemClick(Map<String, String> map) {
        Intent data = new Intent();
        data.putExtra("cityName", map.get("name"));
        data.putExtra("county", map.get("county"));
        data.putExtra("province", map.get("province"));
        setResult(AppInfo.SelectCityResultCode, data);
        baseFinish();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
