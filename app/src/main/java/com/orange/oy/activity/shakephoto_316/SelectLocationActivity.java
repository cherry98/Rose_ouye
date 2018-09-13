package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.SelectCityActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

import static com.orange.oy.base.AppInfo.SelectCityRequestCode;

/**
 * 集图活动->选择位置 V3.16
 */
public class SelectLocationActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.selectlocation_title);
        appTitle.settingName("任务位置");
        appTitle.showBack(this);
    }

    private RadioButton selectlocation_button2;
    private String location_type;
    private TextView selectlocation_clearinfo, selectlocation_type, selectlocation_dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        initTitle();
        location_type = getIntent().getStringExtra("location_type");
//        selectlocation_button1 = (RadioButton) findViewById(R.id.selectlocation_button1);
        selectlocation_button2 = (RadioButton) findViewById(R.id.selectlocation_button2);
        selectlocation_clearinfo = (TextView) findViewById(R.id.selectlocation_clearinfo);
        selectlocation_type = (TextView) findViewById(R.id.selectlocation_type);
        selectlocation_dist = (TextView) findViewById(R.id.selectlocation_dist);
        RadioGroup selectlocation_group = (RadioGroup) findViewById(R.id.selectlocation_group);
//        selectlocation_group.setOnCheckedChangeListener(this);
        findViewById(R.id.selectlocation_button).setOnClickListener(this);
//        if (selectlocation_button1.isChecked()) {
//            location_type = "1";
//        } else if (selectlocation_button2.isChecked()) {
//            location_type = "2";
//        }
        findViewById(R.id.selectlocation_clear).setOnClickListener(this);
        findViewById(R.id.selectlocation_dist_ly).setOnClickListener(this);
        findViewById(R.id.selectlocation_type_ly).setOnClickListener(this);
        if ("2".equals(location_type)) {
            selectlocation_button2.setChecked(true);
            findViewById(R.id.selectlocation_clear).setVisibility(View.GONE);
            findViewById(R.id.selectlocation_vague).setVisibility(View.VISIBLE);
            Intent data = getIntent();
            province = data.getStringExtra("province");
            if (province.equals("全国")) {
                city = "";
                county = "";
                selectlocation_dist.setText(province);
            } else {
                city = data.getStringExtra("cityName");
                county = data.getStringExtra("county");
                String str = "";
                if (!Tools.isEmpty(province)) {
                    if (!province.equals(city))
                        str = province;
                }
                if (!Tools.isEmpty(city)) {
                    str = str + "-" + city;
                }
                if (!Tools.isEmpty(county)) {
                    if (!county.equals(city))
                        str = str + "-" + county;
                }
                selectlocation_dist.setText(str);
            }
            place_name = data.getStringExtra("place_name");
            selectlocation_type.setText(place_name);
            cap_id = data.getStringExtra("cap_id");
        } else {
//            selectlocation_button1.setChecked(true);
            if ("1".equals(location_type)) {
                Intent data = getIntent();
                selectlocation_clearinfo.setText(data.getStringExtra("address"));
                province = data.getStringExtra("province");
                city = data.getStringExtra("cityName");
                county = data.getStringExtra("county");
                dai_id = data.getStringExtra("dai_id");
                longitude = data.getStringExtra("longitude");
                latitude = data.getStringExtra("latitude");
                findViewById(R.id.selectlocation_clear).setVisibility(View.VISIBLE);
                findViewById(R.id.selectlocation_vague).setVisibility(View.GONE);
            }
            location_type = "1";
        }
    }

//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        if (checkedId == selectlocation_button1.getId()) {
//            location_type = "1";
//            findViewById(R.id.selectlocation_clear).setVisibility(View.VISIBLE);
//            findViewById(R.id.selectlocation_vague).setVisibility(View.GONE);
//        } else if (checkedId == selectlocation_button2.getId()) {
//            location_type = "2";
//            findViewById(R.id.selectlocation_clear).setVisibility(View.GONE);
//            findViewById(R.id.selectlocation_vague).setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectlocation_button: {//提交按钮
//                if (TextUtils.isEmpty(location_type)) {
//                    Tools.showToast(this, "请选择投放位置");
//                    return;
//                }
//                if ("1".equals(location_type)) {//准确位置
//                    if (TextUtils.isEmpty(selectlocation_clearinfo.getText().toString().trim())) {
//                        Tools.showToast(this, "请选择地址信息");
//                        return;
//                    }
//                } else if ("2".equals(location_type)) {//模糊位置
                if (TextUtils.isEmpty(selectlocation_type.getText().toString().trim())) {
                    Tools.showToast(this, "请选择场景类型");
                    return;
                }
                if (TextUtils.isEmpty(selectlocation_dist.getText().toString().trim())) {
                    Tools.showToast(this, "请选择位置区域");
                    return;
                }
//                }
                location_type = "2";
                Intent intent = new Intent();
                intent.putExtra("location_type", location_type);
//                if ("1".equals(location_type)) {//准确位置
//                    intent.putExtra("address", selectlocation_clearinfo.getText().toString().trim());
//                    intent.putExtra("province", province);
//                    intent.putExtra("city", city);
//                    intent.putExtra("county", county);
//                    intent.putExtra("dai_id", dai_id);
//                    intent.putExtra("longitude", longitude);
//                    intent.putExtra("latitude", latitude);
//                } else {
                intent.putExtra("place_name", place_name);
                intent.putExtra("cap_id", cap_id);
                intent.putExtra("province", province);
                intent.putExtra("city", city);
                intent.putExtra("county", county);
//                }
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();
            }
            break;
            case R.id.selectlocation_clear: {//准确位置->位置搜索
                Intent intent = new Intent(this, SearchLocationActivity.class);
                intent.putExtra("isPrecise", false);
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.selectlocation_type_ly: {//场景类型选择
                Intent intent = new Intent(this, LocationTypeActivity.class);
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.selectlocation_dist_ly: {//区域选择
                Intent intent = new Intent(this, SelectCityActivity.class);
                intent.putExtra("isShowAll2", true);
                startActivityForResult(intent, SelectCityRequestCode);
            }
            break;
        }
    }

    private String city, province, county, place_name, cap_id, dai_id, longitude, latitude;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == AppInfo.REQUEST_CODE_THEME && data != null) {
            cap_id = data.getStringExtra("cap_id");
            place_name = data.getStringExtra("place_name");
            selectlocation_type.setText(place_name);
        } else if (requestCode == SelectCityRequestCode && data != null) {
            province = data.getStringExtra("province");
            if (province.equals("全国")) {
                city = "";
                county = "";
                selectlocation_dist.setText(province);
            } else {
                city = data.getStringExtra("cityName");
                county = data.getStringExtra("county");
                String str = city;
                if (!TextUtils.isEmpty(province)) {
                    if (!province.equals(city))
                        str = province + "-" + str;
                }
                if (!TextUtils.isEmpty(county)) {
                    if (!county.equals(city))
                        str = str + "-" + county;
                }
                selectlocation_dist.setText(str);
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            province = data.getStringExtra("province");
            city = data.getStringExtra("city");
            county = data.getStringExtra("county");
            dai_id = data.getStringExtra("dai_id");
            longitude = data.getStringExtra("longitude");
            latitude = data.getStringExtra("latitude");
            selectlocation_clearinfo.setText(data.getStringExtra("address"));
        }
    }
}
