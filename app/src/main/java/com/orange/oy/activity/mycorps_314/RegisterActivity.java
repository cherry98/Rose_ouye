package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.encryption.AESException;
import com.orange.oy.encryption.EncryptTool;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 偶业设置密码注册页面 V3.14
 */
public class RegisterActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    public static final int resultCode = 201;


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.register_title);
        appTitle.transparentbg();
        appTitle.settingName(getResources().getString(R.string.register), Color.BLACK);
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("password", pwd);
                params.put("aeskey", aeskey);
                params.put("token", Tools.getToken());
                params.put("province", province);
                params.put("city", city);
                params.put("address", address);
                return params;
            }
        };
        sendData.setIsShowDialog(true);
    }

    protected void onStop() {
        super.onStop();
    }

    public void onBack() {
        baseFinish();
    }

    private EditText register_password;
    private CheckBox register_checkbox;
    private NetworkConnection sendData;
    private String aeskey;
    private EncryptTool encryptTool;
    private String mobile, pwd;//加密过后的账号密码
    private String province = "", city = "", address = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mobile = getIntent().getStringExtra("mobile");
        encryptTool = new EncryptTool(this);
        initTitle();
        initNetworkConnection();
        register_password = (EditText) findViewById(R.id.register_password);
        register_checkbox = (CheckBox) findViewById(R.id.register_checkbox);
        CheckBox mCbDisplayPassword = (CheckBox) findViewById(R.id.mCbDisplayPassword);
        mCbDisplayPassword.setOnClickListener(this);
        findViewById(R.id.register_submit).setOnClickListener(this);
        findViewById(R.id.register_protocol).setOnClickListener(this);
        register_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                register_checkbox.setChecked(isChecked);
            }
        });
        mCbDisplayPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    register_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用  InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    register_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        initLocation();
    }

    private void sendData() {
        try {
            pwd = encryptTool.AESencode(register_password.getText().toString());
            aeskey = encryptTool.getAeskey();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (AESException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        sendData.sendPostRequest(Urls.SetPassword, new Response.Listener<String>() {
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        Tools.showToast(RegisterActivity.this, getResources().getString(R.string.register_toast));
                        Intent data = new Intent();
                        data.putExtra("password", register_password.getText().toString());
                        ScreenManager.getScreenManager().finishActivity(IdentifycodeLoginActivity.class);
                        setResult(resultCode, data);
                        baseFinish();
                    } else {
                        Tools.showToast(RegisterActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(RegisterActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(RegisterActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_submit: {
                if (!register_checkbox.isChecked()) {
                    Tools.showToast(this, "请勾选保密协议");
                    return;
                }
                sendData();
            }
            break;
            case R.id.register_protocol: {//注册协议
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("flag", BrowserActivity.flag_protocol);
                startActivity(intent);
            }
            break;
        }
    }


    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    public static double location_latitude, location_longitude;

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        }
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        setLocationOption();
        mLocationClient.start();
    }

    // 设置相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng point = new LatLng(location_latitude, location_longitude);
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }

    private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (!(reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR)) {
                ReverseGeoCodeResult.AddressComponent addressComponent = reverseGeoCodeResult.getAddressDetail();
                province = addressComponent.province;
                city = addressComponent.city;
                address = addressComponent.district;
            }
        }
    };

    protected void onDestroy() {
        if (mLocationClient != null)
            mLocationClient.stop();
        if (mSearch != null) {
            mSearch.destroy();
        }
        super.onDestroy();
    }

}
