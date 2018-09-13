package com.zmer.testsdkdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.zmer.zmersainuo.common.CommConst;
import com.zmer.zmersainuo.utils.CommonUtils;
import com.zmer.zmersainuo.utils.ControllZmerDevice;
import com.zmer.zmersainuo.utils.WifiAdmin;
import com.zmer.zmersainuo.utils.WifiUtil;
import com.zmer.zmersainuo.utils.sputils.SharedPreferencesUtil;

import java.util.List;
import java.util.Timer;

public class ConnectWifiActivity extends Activity implements View.OnClickListener {
    private String TAG = ConnectWifiActivity.class.getSimpleName();
    private ImageView imgConnectWifiBack;
    private TextView textSettingTitle;
    private ListView listWifi;
    private List<ScanResult> results;
    private Timer timer;
    private TextView textZmerToWifi;
    private ImageView imgZmerToWifi;
    private RelativeLayout rlZmerToWifi;
    private TextView textConnectPhone;
    private TextView textZmerConnetSuccess;
    private RelativeLayout rlConnectPhone;
    private RelativeLayout rlConnectSuccess;
    private ImageView imgConnectPhone;
    private Button btn_reSearchWifi;
    private String curClickWifiSSID = "";
    private String ip = "";
    private String zmerName = "";
    private boolean connectZmer = true;
    private int connectZmerToWifiTimes = 0;
    private WifiAdmin mWifiAdmin;
    private ControllZmerDevice controllZmerDevice;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;

    public static boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return networkProvider || gpsProvider;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_SETTINGS: {
                if (isLocationEnable(this)) {
                    changeConnectUI(1, "");
                    results = WifiUtil.getWifiScanResult(getBaseContext());
                    WifiAdapter adapter = new WifiAdapter(this, results);
                    listWifi.setAdapter(adapter);
                } else {
                    Tools.showToast(this, "请开启GPS");
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        mWifiAdmin = new WifiAdmin(ConnectWifiActivity.this);
        setContentView(R.layout.activity_connect_wifi);
        imgConnectWifiBack = (ImageView) findViewById(R.id.img_connect_wifi_back);
        textSettingTitle = (TextView) findViewById(R.id.text_setting_title);
        listWifi = (ListView) findViewById(R.id.list_wifi);

        // 连接手机
        rlConnectPhone = (RelativeLayout) findViewById(R.id.rl_connect_phone);
        imgConnectPhone = (ImageView) findViewById(R.id.img_connect_phone);
        textConnectPhone = (TextView) findViewById(R.id.text_connect_phone_info);

        // 将zmer连接wifi
        rlZmerToWifi = (RelativeLayout) findViewById(R.id.rl_connect_zmer_to_wifi);
        imgZmerToWifi = (ImageView) findViewById(R.id.img_connect_zmer_to_wifi);
        textZmerToWifi = (TextView) findViewById(R.id.text_connect_zmer_info);

        // 连接成功或失败
        rlConnectSuccess = (RelativeLayout) findViewById(R.id.rl_connect_success);
        btn_reSearchWifi = (Button) findViewById(R.id.btn_reconnect_search_wifi);
        btn_reSearchWifi.setOnClickListener(this);
        textZmerConnetSuccess = (TextView) findViewById(R.id.text_connect_success);
        results = WifiUtil.getWifiScanResult(ConnectWifiActivity.this);
        zmerName = WifiUtil.getWifiName(this);
        textSettingTitle.setText(zmerName);
        WifiAdapter adapter = new WifiAdapter(this, results);
        listWifi.setAdapter(adapter);
        setListener();
        controllZmerDevice = ControllZmerDevice.getControllZmerDeviceInstance();
        if (results.isEmpty() && Build.VERSION.SDK_INT >= 23 && !isLocationEnable(this)) {
            Tools.showToast(this, "请先开启GPS");
            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
        }
    }

    /**
     * @param status 1:wifi界面 2：连接phone界面 3：连接zmer界面 4：连接成功界面 5:连接失败界面
     */
    private void changeConnectUI(int status, String ssid) {
        if (status == 1) {
            listWifi.setVisibility(View.VISIBLE);
            rlConnectPhone.setVisibility(View.GONE);
            rlZmerToWifi.setVisibility(View.GONE);
            rlConnectSuccess.setVisibility(View.GONE);
        } else if (status == 2) {
            listWifi.setVisibility(View.GONE);
            rlConnectPhone.setVisibility(View.VISIBLE);
            rlZmerToWifi.setVisibility(View.GONE);
            rlConnectSuccess.setVisibility(View.GONE);
            textConnectPhone.setText("正在尝试将您的手机连接至" + ssid);
            addPhoneAnimation();
        } else if (status == 3) {
            listWifi.setVisibility(View.GONE);
            rlConnectPhone.setVisibility(View.GONE);
            rlZmerToWifi.setVisibility(View.VISIBLE);
            rlConnectSuccess.setVisibility(View.GONE);
            textZmerToWifi.setText("正在尝试将您的ZMER连接至" + ssid);
            addZmerAnimation();
        } else if (status == 4) {
            listWifi.setVisibility(View.GONE);
            rlConnectPhone.setVisibility(View.GONE);
            rlZmerToWifi.setVisibility(View.GONE);
            rlConnectSuccess.setVisibility(View.VISIBLE);
            btn_reSearchWifi.setVisibility(View.GONE);
            textZmerConnetSuccess.setText("连接成功！");
            textZmerConnetSuccess.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap
                    .connect_success), null, null, null);
            textZmerConnetSuccess.setCompoundDrawablePadding(CommonUtils.dip2px(this, 10));
        } else if (status == 5) {
            listWifi.setVisibility(View.GONE);
            rlConnectPhone.setVisibility(View.GONE);
            rlZmerToWifi.setVisibility(View.GONE);
            rlConnectSuccess.setVisibility(View.VISIBLE);
            btn_reSearchWifi.setVisibility(View.VISIBLE);
            textZmerConnetSuccess.setText("连接失败！");
            textZmerConnetSuccess.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.connnect_fail),
                    null, null, null);
            textZmerConnetSuccess.setCompoundDrawablePadding(CommonUtils.dip2px(this, 10));
        }
    }

    private void addZmerAnimation() {
        AnimationDrawable anim = new AnimationDrawable();
        Drawable loading1 = getResources().getDrawable(R.mipmap.zmer_to_wifi_1);
        anim.addFrame(loading1, 1000);
        Drawable loading2 = getResources().getDrawable(R.mipmap.zmer_to_wifi_2);
        anim.addFrame(loading2, 1000);
        Drawable loading3 = getResources().getDrawable(R.mipmap.zmer_to_wifi_3);
        anim.addFrame(loading3, 1000);
        anim.setOneShot(false);
        imgZmerToWifi.setBackgroundDrawable(anim);
        anim.start();
    }

    private void addPhoneAnimation() {
        AnimationDrawable anim = new AnimationDrawable();
        Drawable loading1 = getResources().getDrawable(R.mipmap.phone_1);
        anim.addFrame(loading1, 1000);
        Drawable loading2 = getResources().getDrawable(R.mipmap.phone_2);
        anim.addFrame(loading2, 1000);
        Drawable loading3 = getResources().getDrawable(R.mipmap.phone_3);
        anim.addFrame(loading3, 1000);
        anim.setOneShot(false);
        imgConnectPhone.setBackgroundDrawable(anim);
        anim.start();
    }

    private void setListener() {
        imgConnectWifiBack.setOnClickListener(this);
        listWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO 点击弹窗输入密码
                String ssid = results.get(position).SSID;
                String str = results.get(position).capabilities;
                showConnectWifiDialog(ssid);
            }
        });
    }

    public void showConnectWifiDialog(String ssid) {
        final ConnectWifiDialog dialog = new ConnectWifiDialog(this, R.style.DialogTheme);
        WindowManager wm = this.getWindowManager();
        Window window = dialog.getWindow();
        dialog.setTitle(ssid);
        window.setGravity(Gravity.CENTER);
        dialog.setClickConfirmListener(new ConnectWifiDialog.DialogConnectClickListener() {

            @Override
            public void clickConfirmBtn(final String password, final String ssid) {
                curClickWifiSSID = ssid;
                // TODO 处理切换wifi
                if (password == null || "".equals(password)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    // TODO 设置Zmer连接wifi
                    controllZmerDevice.controlZmerToConnectWifi(password, ssid, handler);
                    changeConnectUI(3, ssid);
                }
            }
        });
        dialog.show();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CommConst.CONNECT_SUCCESS) {
                // TODO 检测Zmer是否连接成功
                if (ip.equals("") && connectZmerToWifiTimes < 8) {
                    connectZmerToWifiTimes++;
                    checkZmerToWifi();
                } else if (connectZmerToWifiTimes >= 8) {
                    connectZmerToWifiTimes = 0;
                    changeConnectUI(5, "");
                }
            } else if (msg.what == CommConst.CONNECT_WIFI) {
                Bundle bundle = msg.getData();
                String ssid = (String) bundle.get(CommConst.ssid);
                String pwd = (String) bundle.get(CommConst.psd);
                String ipAddress = bundle.getString(CommConst.ip);
                SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(getBaseContext());
                sp.setString(CommConst.wifi + ssid, ssid);
                sp.setString(CommConst.ip + ssid, ipAddress);
                sp.setString(CommConst.ssid, zmerName);
                changeConnectUI(4, ssid);
                controllZmerDevice.updateDeviceIpAddress(ipAddress);

                handler.sendEmptyMessageDelayed(100, 1000);
            } else if (msg.what == 100) {
                finish();
            }
        }
    };

    private void checkZmerToWifi() {
        controllZmerDevice.checkZmerConnectToWifi(this, handler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_connect_wifi_back:
                finish();
                break;
            case R.id.btn_reconnect_search_wifi:
                changeConnectUI(1, "");
                results = WifiUtil.getWifiScanResult(getBaseContext());
                WifiAdapter adapter = new WifiAdapter(this, results);
                listWifi.setAdapter(adapter);
                break;
        }
    }
}
