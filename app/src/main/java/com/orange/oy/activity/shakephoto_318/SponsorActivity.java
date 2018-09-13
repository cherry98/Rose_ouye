package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.TaskContentActivity;
import com.orange.oy.activity.createtask_317.TaskMouldActivity;
import com.orange.oy.activity.createtask_317.ToWhomVisibleActivity;
import com.orange.oy.activity.shakephoto_316.SearchLocationActivity;
import com.orange.oy.activity.shakephoto_320.AllmodelActivity;
import com.orange.oy.activity.shakephoto_320.IdentityCommercialTenantActivity;
import com.orange.oy.activity.shakephoto_320.PresentManagementActivity;
import com.orange.oy.activity.shakephoto_320.PrizeSettingActivity;
import com.orange.oy.activity.shakephoto_320.TaskPrizeActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.PrizeSettingDialog;
import com.orange.oy.info.shakephoto.TaskListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 赞助活动 V3.18
 */
public class SponsorActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.sponsor_title);
        appTitle.settingName("赞助活动");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        sponsorActivity = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SponsorActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                return params;
            }
        };
        sponsorPayInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SponsorActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                params.put("red_pack_type", red_pack_type);
                params.put("sponsorship_fee", sponsorship_fee);
                params.put("ad_links", ad_links);
                params.put("ad_url", ad_url);
                if ("2".equals(red_pack_type)) {//到店红包
                    params.put("red_pack_address", red_pack_address);
                    params.put("visible_type", visible_type);
                    if (city == null) {
                        city = "";
                    }
                    params.put("city", city);
                    params.put("tasklist", tasklist);
                    params.put("gift_name", gift_name);
                    params.put("gift_money", gift_money);
                    params.put("gift_num", gift_num);
                    params.put("gift_url", gift_url);
                } else {
                    params.put("sponsor_name", sponsor_name);
                }
                return params;
            }
        };
        checkMerchantCA = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SponsorActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        checkMerchantCA.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkMerchantCA != null) {
            checkMerchantCA.stop(Urls.CheckMerchantCA);
        }
    }

    private NetworkConnection sponsorActivity, sponsorPayInfo, checkMerchantCA;
    private String ai_id;
    private RadioGroup sponsor_group;
    private EditText sponsor_price, sponsor_sponsorname, sponsor_advlinks;
    private TextView sponsor_location, sponsor_permission;
    private ImageView sponsor_img;
    private String red_pack_type, sponsorship_fee, sponsor_name, ad_links, ad_url,
            red_pack_address, visible_type, city;
    private String min_sponsorship_fee;
    private String gift_name, gift_money, gift_num, gift_url, tasklist;//V3.20 到店礼品需传
    private TextView sponsor_total, sponsor_mould, sponsor_prize, sponsor_serice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor);
        ai_id = getIntent().getStringExtra("ai_id");
        initTitle();
        initNetwork();
        initView();
        sponsor_group.setOnCheckedChangeListener(this);
        findViewById(R.id.sponsor_pay).setOnClickListener(this);
        findViewById(R.id.sponsor_location_ly).setOnClickListener(this);
        findViewById(R.id.sponsor_permission_ly).setOnClickListener(this);
        findViewById(R.id.sponsor_mould_ly).setOnClickListener(this);
        findViewById(R.id.sponsor_prize_ly).setOnClickListener(this);
        findViewById(R.id.sponsor_img).setOnClickListener(this);
        sponsor_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double money = Tools.StringToDouble(s.toString());
                if (money > 0) {
                    findViewById(R.id.sponsor_total_ly).setVisibility(View.VISIBLE);
                    sponsor_total.setText(Tools.removePoint(Tools.savaTwoByte(1.2 * money)));
                    sponsor_serice.setText("(含20%服务费)");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void initView() {
        sponsor_group = (RadioGroup) findViewById(R.id.sponsor_group);
        sponsor_price = (EditText) findViewById(R.id.sponsor_price);
        sponsor_sponsorname = (EditText) findViewById(R.id.sponsor_sponsorname);
        sponsor_advlinks = (EditText) findViewById(R.id.sponsor_advlinks);
        sponsor_location = (TextView) findViewById(R.id.sponsor_location);
        sponsor_permission = (TextView) findViewById(R.id.sponsor_permission);
        sponsor_total = (TextView) findViewById(R.id.sponsor_total);
        sponsor_serice = (TextView) findViewById(R.id.sponsor_serice);
        sponsor_mould = (TextView) findViewById(R.id.sponsor_mould);
        sponsor_prize = (TextView) findViewById(R.id.sponsor_prize);
        sponsor_img = (ImageView) findViewById(R.id.sponsor_img);
        //默认随机红包
        findViewById(R.id.sponsor_layout).setVisibility(View.GONE);
        red_pack_type = "1";
        sponsor_sponsorname.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editable = sponsor_sponsorname.getText().toString();
                String str = Tools.stringFilter(editable);
                if (!editable.equals(str)) {
                    sponsor_sponsorname.setText(str);
                    sponsor_sponsorname.setSelection(str.length());
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkMerchantCA() {
        checkMerchantCA.sendPostRequest(Urls.CheckMerchantCA, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        String certification_state = jsonObject.getString("certification_state");
                        if ("0".equals(certification_state)) {
                            ConfirmDialog.showDialog(SponsorActivity.this, "提示！", 3, "为确保用户真实收到礼品，请先完成商户认证，认证审核周期为1-3个工作日，认证成功后，即可发布礼品奖励任务。",
                                    "以后再说", "商户认证", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            Intent intent = new Intent(SponsorActivity.this, IdentityCommercialTenantActivity.class);
                                            intent.putExtra("isHaveTag", "1");
                                            startActivity(intent);
                                        }
                                    });
                        } else {
                            String gift_library = jsonObject.getString("gift_library");
                            final String merchant_id = jsonObject.getString("merchant_id");
                            if ("1".equals(gift_library)) {//已有礼品库 弹窗选择
                                PrizeSettingDialog.showDialog(SponsorActivity.this, new PrizeSettingDialog.OnPrizeSettingListener() {
                                    @Override
                                    public void firstClick() {
                                        Intent intent = new Intent(SponsorActivity.this, PrizeSettingActivity.class);
                                        intent.putExtra("gift_name", gift_name);
                                        intent.putExtra("gift_money", gift_money);
                                        intent.putExtra("gift_num", gift_num);
                                        intent.putExtra("gift_url", gift_url);
                                        startActivityForResult(intent, 2);
                                    }

                                    @Override
                                    public void secondClick() {
                                        Intent intent = new Intent(SponsorActivity.this, PresentManagementActivity.class);
                                        intent.putExtra("isOnclick", "1");
                                        intent.putExtra("merchant_id", merchant_id);
                                        startActivityForResult(intent, 3);
                                    }
                                });
                            }
                        }
                    } else {
                        Tools.showToast(SponsorActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SponsorActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SponsorActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void getData() {
        sponsorActivity.sendPostRequest(Urls.SponsorActivity, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        jsonObject = jsonObject.optJSONObject("activity_info");
                        if (jsonObject != null) {
                            ((TextView) findViewById(R.id.sponsor_name)).setText(jsonObject.getString("activity_name"));
                            ((TextView) findViewById(R.id.sponsor_targetnum)).setText(jsonObject.getString("target_num"));
                            String create_sponsor_name = jsonObject.getString("create_sponsor_name");
                            if (!Tools.isEmpty(create_sponsor_name)) {
                                ((TextView) findViewById(R.id.sponsor_initiator)).setText(create_sponsor_name);
                                findViewById(R.id.sponsor_initiator_ly).setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.sponsor_initiator_ly).setVisibility(View.GONE);
                            }
                            String merchant_name = jsonObject.getString("merchant_name");
                            if (!Tools.isEmpty(merchant_name)) {
                                ((TextView) findViewById(R.id.sponsor_sponsors)).setText(merchant_name);
                                findViewById(R.id.sponsor_sponsors_ly).setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.sponsor_sponsors_ly).setVisibility(View.GONE);
                            }
                            String location_type = jsonObject.getString("location_type");
                            if ("1".equals(location_type)) {//精确位置
                                findViewById(R.id.sponsor_placename_ly).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.sponsor_address)).setText(jsonObject.getString("address"));
                            } else {//模糊位置
                                findViewById(R.id.sponsor_placename_ly).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.sponsor_placename)).setText(jsonObject.getString("place_name"));
                                String province = jsonObject.getString("province");
                                String city = jsonObject.getString("city");
                                String county = jsonObject.getString("county");
                                if (Tools.isEmpty(province)) {
                                    province = "";
                                }
                                if (Tools.isEmpty(city)) {
                                    city = "";
                                }
                                if (Tools.isEmpty(county)) {
                                    county = "";
                                }
                                if (province.equals(city)) {
                                    ((TextView) findViewById(R.id.sponsor_address)).setText(city + county);
                                } else {
                                    ((TextView) findViewById(R.id.sponsor_address)).setText(province + city + county);
                                }
                            }
                            min_sponsorship_fee = jsonObject.getString("min_sponsorship_fee");
                            sponsor_price.setHint("请输入大于" + min_sponsorship_fee + "的金额");
                        }
                    } else {
                        Tools.showToast(SponsorActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SponsorActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SponsorActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.sponsor_radio1) {
            findViewById(R.id.sponsor_layout).setVisibility(View.GONE);
            findViewById(R.id.sponsor_sponsorname_ly).setVisibility(View.VISIBLE);
            findViewById(R.id.sponsor_price_ly).setVisibility(View.VISIBLE);
            red_pack_type = "1";
        } else {
            checkMerchantCA();
            findViewById(R.id.sponsor_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.sponsor_sponsorname_ly).setVisibility(View.GONE);
            findViewById(R.id.sponsor_price_ly).setVisibility(View.GONE);
            red_pack_type = "2";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sponsor_pay: {//支付
                if (Tools.isEmpty(red_pack_type)) {
                    Tools.showToast(this, "请选择红包类型");
                    return;
                }
                if ("2".equals(red_pack_type)) {//到店红包
                    if (Tools.isEmpty(sponsor_location.getText().toString().trim())) {
                        Tools.showToast(this, "请设置红包投放位置");
                        return;
                    }
                    if (Tools.isEmpty(sponsor_permission.getText().toString().trim())) {
                        Tools.showToast(this, "请设置可见类型");
                        return;
                    }
                    if (Tools.isEmpty(sponsor_prize.getText().toString().trim())) {
                        Tools.showToast(this, "请设置礼品");
                        return;
                    }
                    if (Tools.isEmpty(sponsor_mould.getText().toString().trim())) {
                        Tools.showToast(this, "请选择模板");
                        return;
                    }
                    sponsorship_fee = money + "";
                } else {//现金红包
                    sponsorship_fee = sponsor_price.getText().toString().trim();
                    if (Tools.StringToDouble(sponsorship_fee) <= Tools.StringToDouble(min_sponsorship_fee)) {
                        Tools.showToast(this, "请输入大于" + min_sponsorship_fee + "的金额");
                        return;
                    }
                    sponsor_name = sponsor_sponsorname.getText().toString().trim();
                    if (Tools.isEmpty(sponsor_name)) {
                        Tools.showToast(this, "请填写赞助商名称");
                        return;
                    }
                }
                ad_links = sponsor_advlinks.getText().toString().trim();
                if (Tools.isEmpty(ad_url)) {
                    Tools.showToast(this, "请上传广告图");
                    return;
                }
                if (!isUpdata) {
                    Tools.showToast(this, "图片还未上传完成呢~");
                    return;
                }
                sponsorPayInfo();
            }
            break;
            case R.id.sponsor_location_ly: {//位置设置
                Intent intent = new Intent(this, SearchLocationActivity.class);
                intent.putExtra("isPrecise", false);
                intent.putExtra("title", "红包位置");
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.sponsor_permission_ly: {//对谁可见
                Intent intent = new Intent(this, ToWhomVisibleActivity.class);
                intent.putExtra("isFrist", "2");
                intent.putExtra("visible_type", visible_type);
                intent.putExtra("city", city);
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.sponsor_img: {//上传广告图(从手机相册选择)
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
            }
            break;
            case R.id.sponsor_mould_ly: {//任务模板选择
                Intent intent = new Intent(SponsorActivity.this, AllmodelActivity.class);
                intent.putExtra("state", "2");
                startActivityForResult(intent, toAllmodelActivity);
            }
            break;
            case R.id.sponsor_prize_ly: {//礼品设置
                Intent intent = new Intent(this, PrizeSettingActivity.class);
                intent.putExtra("gift_name", gift_name);
                intent.putExtra("gift_money", gift_money);
                intent.putExtra("gift_num", gift_num);
                intent.putExtra("gift_url", gift_url);
                startActivityForResult(intent, 2);
            }
            break;
        }
    }

    private final int toAllmodelActivity = 0x100;


    private void sponsorPayInfo() {
        sponsorPayInfo.sendPostRequest(Urls.SponsorPayInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        Intent intent = new Intent(SponsorActivity.this, PaymentActivity.class);
                        intent.putExtra("account_money", jsonObject.getString("account_money"));
                        intent.putExtra("esfi_id", jsonObject.getString("esfi_id"));
                        intent.putExtra("total_money", sponsor_total.getText().toString().trim());
                        startActivity(intent);
                    } else {
                        String msg = jsonObject.getString("msg");
                        if ("商户未认证".equals(msg)) {
                            Intent intent = new Intent(SponsorActivity.this, IdentityCommercialTenantActivity.class);
                            intent.putExtra("isHaveTag", "1");
                            startActivity(intent);
                        } else {
                            Tools.showToast(SponsorActivity.this, msg);
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(SponsorActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SponsorActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public String getTasklist(ArrayList<TaskListInfo> list) {
        JSONObject jsonObject1 = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                TaskListInfo taskListInfo = list.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("task_id", taskListInfo.getTask_id());
                jsonObject.put("task_type", taskListInfo.getTask_type());
                jsonObject.put("task_name", taskListInfo.getTask_name());
                jsonObject.put("note", taskListInfo.getNote());
                jsonObject.put("is_watermark", taskListInfo.getIs_watermark());
                jsonObject.put("local_photo", taskListInfo.getLocal_photo());
                JSONArray jsonArray1 = new JSONArray(taskListInfo.getPhotourl());
                jsonObject.put("photourl", jsonArray1);
                jsonArray.put(jsonObject);
            }
            jsonObject1.put("task_list", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1.toString();
    }

    private double money;//礼品设置总额

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case toAllmodelActivity: {// 模板返回
                    if (data != null) {
                        tasklist = getTasklist((ArrayList<TaskListInfo>) data.getSerializableExtra("tasklist"));
                        sponsor_mould.setText("已设置");
                    }
                }
                break;
                case AppInfo.MyDetailRequestCodeForPick: {
                    if (data != null) {
                        Uri uri = data.getData();
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(uri, "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 2);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 1000);
                        intent.putExtra("outputY", 500);
                        intent.putExtra("scale", true);//黑边
                        intent.putExtra("scaleUpIfNeeded", true);//黑边
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/myImg.jpg")));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                    }
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg.jpg";
                    sponsor_img.setImageBitmap(Tools.getBitmap(filePath, 200, 200));
                    sendOSSData(filePath);
                }
                break;
                case 0: {//位置信息
                    if (data != null) {
                        String dai_id = data.getStringExtra("dai_id");
                        String address_name = data.getStringExtra("item1");
                        String province = data.getStringExtra("province");
                        String city = data.getStringExtra("city");
                        String county = data.getStringExtra("county");
                        String address = data.getStringExtra("address");
                        String longitude = data.getStringExtra("longitude");
                        String latitude = data.getStringExtra("latitude");
                        sponsor_location.setText(address_name);
                        locationToJson(dai_id, province, city, county, address, address_name, longitude, latitude);
                    }
                }
                break;
                case 1: { //对谁可见
                    if (data != null) {
                        city = data.getStringExtra("city");
                        visible_type = data.getStringExtra("visible_type");
                        if ("1".equals(visible_type)) {
                            sponsor_permission.setText("全部可见");
                        } else if ("2".equals(visible_type)) {
                            sponsor_permission.setText("仅自己可见");
                        } else {
                            sponsor_permission.setText("部分区域可见");
                        }
                    }
                }
                break;
                case 2: {
                    if (data != null) {
                        gift_url = data.getStringExtra("gift_url");
                        gift_name = data.getStringExtra("gift_name");
                        gift_money = data.getStringExtra("gift_money");
                        gift_num = data.getStringExtra("gift_num");
                        sponsor_prize.setText("已设置");
                        findViewById(R.id.sponsor_total_ly).setVisibility(View.VISIBLE);
                        money = Tools.StringToDouble(gift_money) * Tools.StringToDouble(gift_num);
                        sponsor_total.setText(Tools.removePoint(Tools.savaTwoByte(1.1 * money)));
                        sponsor_serice.setText("(含10%服务费)");
                    }
                }
                break;
                case 3: {
                    if (data != null) {
                        gift_url = data.getStringExtra("gift_url");
                        gift_name = data.getStringExtra("gift_name");
                        gift_money = data.getStringExtra("gift_money");
                        Intent intent = new Intent(this, PrizeSettingActivity.class);
                        intent.putExtra("gift_name", gift_name);
                        intent.putExtra("gift_money", gift_money);
                        intent.putExtra("gift_num", gift_num);
                        intent.putExtra("gift_url", gift_url);
                        startActivityForResult(intent, 2);
                    }
                }
                break;
            }
        }
    }

    private void locationToJson(String dai_id, String province, String city, String county, String address,
                                String address_name, String longitude, String latitude) {
        try {
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            if (!Tools.isEmpty(dai_id)) {
                jsonObject.put("dai_id", dai_id);
            }
            jsonObject.put("province", province);
            jsonObject.put("city", city);
            jsonObject.put("county", county);
            jsonObject.put("address", address);
            jsonObject.put("address_name", address_name);
            jsonObject.put("longitude", longitude);
            jsonObject.put("latitude", latitude);
            jsonObject1.put("address_info", jsonObject);
            red_pack_address = jsonObject1.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;
    private boolean isUpdata;

    public void sendOSSData(String s) {
        try {
            isUpdata = false;
            CustomProgressDialog.showProgressDialog(this, "正在上传");
            Tools.d(s);
            File file = new File(s);
            if (!file.exists() || !file.isFile()) {
                return;
            }
            String objectKey = file.getName();
            objectKey = Urls.Shakephoto + "/" + Tools.getTimeSS() + "_" + objectKey;
            ad_url = objectKey;
            Tools.d("图片地址：" + ad_url);
            if (credentialProvider == null)
                credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                        "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
            if (oss == null)
                oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, s);
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                Log.i("-------", "图片上传中-----");
                    Tools.d("currentSize: " + currentSize + " totalSize: " + totalSize);
                }
            });
            task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    CustomProgressDialog.Dissmiss();
                    Tools.d("上传成功");
                    isUpdata = true;
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    Tools.d("onFailure");
                    CustomProgressDialog.Dissmiss();
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                    }
                    if (serviceException != null) {
                        // 服务异常
                        serviceException.printStackTrace();
                        Tools.d("-------图片上传失败");
                    }
                    task.cancel();
//                task.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.Dissmiss();
        }
    }
}
