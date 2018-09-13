package com.orange.oy.activity.bigchange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.SelectCityActivity;
import com.orange.oy.activity.mycorps_315.FreetimeActivity;
import com.orange.oy.activity.mycorps_315.OftenGotoPlaceActivity;
import com.orange.oy.activity.mycorps_315.PersonalSpecialtyActivity;
import com.orange.oy.activity.shakephoto_320.IdentityCommercialTenantActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.BaseView;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.sobot.chat.SobotApi;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;


public class MyDetailInfoActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mydetail_title);
        appTitle.settingName("个人信息");
        appTitle.showBack(this);
    }

    public void onStop() {
        super.onStop();
        if (sendData != null) {
            sendData.stop(Urls.UpateUser);
        }
        if (PersonalSquare != null) {
            PersonalSquare.stop(Urls.PersonalSquare);
        }
        isRefresh = false;
    }

    public void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private void initNetworkConnection() {

        Addstatistout = new NetworkConnection(MyDetailInfoActivity.this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_mobile", AppInfo.getName(MyDetailInfoActivity.this));
                params.put("token", Tools.getToken());
                params.put("name", getResources().getString(R.string.app_name));
                try {
                    params.put("versionnum", Tools.getVersionName(MyDetailInfoActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(MyDetailInfoActivity.this));
                params.put("resolution", Tools.getScreeInfoWidth(MyDetailInfoActivity.this) + "*" + Tools.getScreeInfoHeight
                        (MyDetailInfoActivity.this));
                params.put("outtime", Tools.getTimeByPattern("yyyy-MM-dd HH:mm:ss"));
                params.put("mac", Tools.getLocalMacAddress(MyDetailInfoActivity.this));
                params.put("imei", Tools.getDeviceId(MyDetailInfoActivity.this));
                return params;
            }
        };
        PersonalSquare = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyDetailInfoActivity.this));
                params.put("close_square", squareed);//close_square	是否关闭广场（0：不关闭，1：关闭）【必传】
                return params;
            }
        };
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                String username = mydetail_name.getText().toString().trim();
                String mydetail_ages = mydetail_age.getText().toString().trim();
                String mydetail_citysed = mydetail_citys.getText().toString().trim();
                String mydetail_freetimeed = mydetail_freetime.getText().toString().trim();
                String mydetail_specialityed = mydetail_speciality.getText().toString().trim();

                if (!mydetail_ages.equals(age)) {
                    params.put("age", mydetail_ages);
                }
                if (!mydetail_citysed.equals(team_usualplace.substring(1, team_usualplace.length() - 1))) {
                    if (TextUtils.isEmpty(mydetail_citysed)) {
                        params.put("usual_place", "-1");
                    } else {
                        params.put("usual_place", mydetail_citysed);
                    }
                }

                if (!mydetail_freetimeed.equals(free_time.substring(1, free_time.length() - 1))) {
                    if (TextUtils.isEmpty(mydetail_freetimeed)) {
                        params.put("free_time", "-1");
                    } else {
                        params.put("free_time", mydetail_freetimeed);
                    }
                }
                if (!mydetail_specialityed.equals(personal_specialty.substring(1, personal_specialty.length() - 1))) {
                    if (TextUtils.isEmpty(mydetail_specialityed)) {
                        params.put("personal_specialty", "-1");
                    } else {
                        params.put("personal_specialty", mydetail_specialityed);
                    }
                }
                //  String address = mydetail_city.getText().toString().trim();
                if (!username.equals(AppInfo.getUserName(MyDetailInfoActivity.this))) {
                    params.put("username", username);
                }
                String[] strings = AppInfo.getUserdistrics(MyDetailInfoActivity.this);
                if (TextUtils.isEmpty(address)) {
                    address = city;
                }
                if (TextUtils.isEmpty(province)) {
                    province = city;
                }
                if (!address.equals(strings[2])) {
                    params.put("address", address);
                }
                if (!city.equals(strings[1])) {
                    params.put("city", city);
                }
                if (!province.equals(strings[0])) {
                    params.put("province", province);
                }
                if (bitmap != null) {
                    params.put("img", Tools.bitmapToBase64(bitmap));
                }
                if (!TextUtils.isEmpty(Restelphone)) {
                    params.put("userPhoneNum", Restelphone);
                }
                if (!TextUtils.isEmpty(Restelphone)) {

                }

                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyDetailInfoActivity.this));
                return params;
            }
        };
        sendData.setIsShowDialog(true);
    }

    private ImageView mydetail_img;
    private ImageLoader imageLoader;
    private TextView mydetail_name, mydetail_phone, mydetail_authentication, mydetail_age, mydetail_citys, mydetail_freetime, mydetail_speciality, tv_weixin;
    private TextView mydetail_city;
    private ImageView mydetail_sq;
    private Bitmap bitmap;
    private NetworkConnection sendData, PersonalSquare, Addstatistout;
    private LinearLayout mydetail_city_layout, mydetail_citys_layout, lin_authentication;
    private String userPhoneNum, isIndividualAccount, city, Restelphone, province, address, age, bind_wechat, merchant;
    /// private ArrayList<String> team_usualplace = new ArrayList<>();
    //private ArrayList<String> free_time = new ArrayList<>();
    // private ArrayList<String> personal_specialty = new ArrayList<>();
    private String usualplace = null;
    private String free_time;
    private String personal_specialty;
    private String squareed; //开启广场
    private String team_usualplace;
    private View mydetail_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_detail_info);
        initTitle();
        initNetworkConnection();
        imageLoader = new ImageLoader(this);
        mydetail_authentication = (TextView) findViewById(R.id.mydetail_authentication);
        mydetail_img = (ImageView) findViewById(R.id.mydetail_img);
        mydetail_name = (TextView) findViewById(R.id.mydetail_name);
        mydetail_phone = (TextView) findViewById(R.id.mydetail_phone);
        mydetail_age = (TextView) findViewById(R.id.mydetail_age);
        lin_authentication = (LinearLayout) findViewById(R.id.lin_authentication);
        mydetail_age.setOnClickListener(this);
        mydetail_city = (TextView) findViewById(R.id.mydetail_city);
        mydetail_citys = (TextView) findViewById(R.id.mydetail_citys);
        mydetail_freetime = (TextView) findViewById(R.id.mydetail_freetime);
        tv_weixin = (TextView) findViewById(R.id.tv_weixin);
        mydetail_freetime.setOnClickListener(this);
        mydetail_speciality = (TextView) findViewById(R.id.mydetail_speciality);
        mydetail_speciality.setOnClickListener(this);
        mydetail_city_layout = (LinearLayout) findViewById(R.id.mydetail_city_layout);
        mydetail_citys_layout = (LinearLayout) findViewById(R.id.mydetail_citys_layout);
        mydetail_sq = (ImageView) findViewById(R.id.mydetail_sq);
        mydetail_exit = findViewById(R.id.mydetail_exit);
        mydetail_exit.setOnClickListener(this);
        mydetail_sq.setOnClickListener(this);
        userPhoneNum = getIntent().getStringExtra("userPhoneNum");  //手机号
        age = getIntent().getStringExtra("age");
        team_usualplace = getIntent().getStringExtra("team_usualplace");
        free_time = getIntent().getStringExtra("free_time");
        personal_specialty = getIntent().getStringExtra("personal_specialty");
        squareed = getIntent().getStringExtra("close_square");
        isIndividualAccount = getIntent().getStringExtra("isIndividualAccount"); //是否是个性化账号，1为是，0为否
        bind_wechat = getIntent().getStringExtra("bind_wechat");
        merchant = getIntent().getStringExtra("merchant");
        //"merchant": “是否是已认证商户，1为是，0为否”,
        //   "bind_wechat": “是否已绑定微信，1为是，0为否”

        if (!TextUtils.isEmpty(bind_wechat) && "1".equals(bind_wechat)) {
            tv_weixin.setText("已绑定");
        } else {
            tv_weixin.setText("未绑定");
            findViewById(R.id.lin_weixin).setOnClickListener(this);
        }
        if (!TextUtils.isEmpty(merchant) && "1".equals(merchant)) {
            mydetail_authentication.setText("已认证");
            //已认证就不显示
            lin_authentication.setVisibility(View.GONE);
        } else {
            mydetail_authentication.setText("未认证");
            lin_authentication.setVisibility(View.VISIBLE);
        }
        mydetail_authentication.setOnClickListener(this);
        if (!TextUtils.isEmpty(age)) {
            mydetail_age.setText(age);
        }
        mydetail_citys.setText(team_usualplace.substring(1, team_usualplace.length() - 1));
        mydetail_freetime.setText(free_time.substring(1, free_time.length() - 1));
        mydetail_speciality.setText(personal_specialty.substring(1, personal_specialty.length() - 1));


        if ("1".equals(squareed)) {  //是否关闭广场（0：不关闭，1：关闭）
            mydetail_sq.setImageResource(R.mipmap.switch2_off2);
        } else if ("0".equals(squareed)) {
            mydetail_sq.setImageResource(R.mipmap.switch2_open2);
        } else {
            squareed = "0";
        }

        city = getIntent().getStringExtra("city");
        String[] addresss;
        if (TextUtils.isEmpty(city) || city.equals("null")) {
            addresss = AppInfo.getAddress(getBaseContext());
        } else {
            addresss = AppInfo.getUserdistrics(this);
        }
        city = addresss[1];
        province = addresss[0];
        this.address = addresss[2];
        String str = city;
        if (!TextUtils.isEmpty(province)) {
            if (!province.equals(city))
                str = province + "-" + str;
        }
        if (!TextUtils.isEmpty(this.address)) {
            if (!this.address.equals(city))
                str = str + "-" + this.address;
        }
        mydetail_city.setText(str);


        findViewById(R.id.main).setOnClickListener(this);
        imageLoader.DisplayImage(AppInfo.getUserImagurl(this), mydetail_img, R.mipmap.grxx_icon_mrtx);
        //昵称默认为用户注册的手机号
        if (TextUtils.isEmpty(AppInfo.getUserName(this))) {
            mydetail_name.setText(AppInfo.getName(this));
        } else {
            mydetail_name.setText(AppInfo.getUserName(this));
        }
        String name = AppInfo.getName(this);

        if (Tools.isMobile(name)) {
            ((TextView) findViewById(R.id.mydetail_phone)).setText(name);
        } else {
            ((TextView) findViewById(R.id.mydetail_phone)).setText(userPhoneNum);
        }
        ((TextView) findViewById(R.id.mydetail_invite)).setText(AppInfo.getInviteCode(this));

        mydetail_city_layout.setOnClickListener(this);
        mydetail_citys_layout.setOnClickListener(this);

        checkPermission();
        mydetail_img.setOnClickListener(this);
        mydetail_name.setOnClickListener(this);
        if (!TextUtils.isEmpty(isIndividualAccount) && isIndividualAccount.equals("1")) {
            findViewById(R.id.mydetail_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(MyDetailInfoActivity.this, NicktelphoneReviseActivity.class), AppInfo.REQUEST_CODE_NICKTELPHONE);
                }
            });
        } else {
            findViewById(R.id.mydetail_phone_right).setVisibility(View.INVISIBLE);
        }
        mShareAPI = UMShareAPI.get(this);
    }

    private UMShareAPI mShareAPI;

    @Override
    public void onBack() {
        baseFinish();
    }

    private void bindWX() {
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener);
    }

    private UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Tools.d("成功了");
            String openid = data.get("openid");
            if (openid == null) {
                Tools.showToast(MyDetailInfoActivity.this, "绑定失败");
            } else {
                Tools.d("openid:" + openid);
                checkWX(openid);
            }
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Tools.d("失败：" + t.getMessage());
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Tools.d("取消了");
        }
    };

    private void checkWX(String openid) {
        NetworkConnection bindWeChat = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                return null;
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("token", Tools.getToken());
        params.put("usermobile", AppInfo.getName(MyDetailInfoActivity.this));
        params.put("wechat_account", openid);
        bindWeChat.setMapParams(params);
        bindWeChat.setIsShowDialog(true);
        bindWeChat.sendPostRequest(Urls.BindWeChat, new Response.Listener<String>() {
            public void onResponse(String s) {
                CustomProgressDialog.Dissmiss();
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        bind_wechat = "1";
                        tv_weixin.setText("已绑定");
                        findViewById(R.id.lin_weixin).setOnClickListener(null);
                    } else {
                        Tools.showToast(MyDetailInfoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyDetailInfoActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(MyDetailInfoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, 2000);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mydetail_authentication: {   //跳转至商户证明
                Intent intent = new Intent(this, IdentityCommercialTenantActivity.class);
                intent.putExtra("merchant", merchant);
                startActivityForResult(intent, AppInfo.REQUEST_CODE_IDENTITY);
            }
            break;
            case R.id.lin_weixin: { //跳转至微信授权
                bindWX();
            }
            break;
            case R.id.mydetail_exit: {
                SobotApi.exitSobotChat(this);
                MyFragment.isRefresh = true;
                Addstatistout.sendPostRequest(Urls.Addstatistout, new Response.Listener<String>() {
                    public void onResponse(String s) {
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
                AppInfo.clearKey(this);
                JPushInterface.clearAllNotifications(this);
                JPushInterface.setAlias(this, "", null);
                JPushInterface.stopPush(this);
                mShareAPI.deleteOauth(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        Tools.d("成功了");
                    }

                    public void onError(SHARE_MEDIA share_media, int i, Throwable t) {
                        Tools.d("失败：" + t.getMessage());
                    }

                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        Tools.d("取消了");
                    }
                });
//                upLoginico();
                Intent intent11 = new Intent("com.orange.oy.VRService");
                intent11.setPackage("com.orange.oy");
                this.stopService(intent11);
                baseFinish();
            }
            break;
            case R.id.mydetail_speciality: {
                Intent intent = new Intent(this, PersonalSpecialtyActivity.class);
                intent.putExtra("labels", mydetail_speciality.getText().toString());
                startActivityForResult(intent, AppInfo.REQUEST_CODE_SPCIAL);
            }
            break;
            case R.id.mydetail_freetime: {
                Intent intent = new Intent(this, FreetimeActivity.class);
                intent.putExtra("labels", mydetail_freetime.getText().toString());
                startActivityForResult(intent, AppInfo.REQUEST_CODE_FREETIME);
            }
            break;
            case R.id.mydetail_sq: {
                if (squareed.equals("0")) {
                    squareed = "1";
                } else {
                    squareed = "0";
                }
                personalSquare();
            }
            break;
            //昵称
            case R.id.mydetail_name: {
                startActivityForResult(new Intent(this, NicknameReviseActivity.class), AppInfo.REQUEST_CODE_NICKNAME);
            }
            break;

            case R.id.mydetail_age: {
                startActivityForResult(new Intent(this, NickageReviseActivity.class), AppInfo.REQUEST_CODE_AGE);
            }
            break;

            case R.id.mydetail_city_layout: {
                startActivityForResult(new Intent(this, SelectCityActivity.class), AppInfo
                        .SelectCityRequestCode);
            }
            break;
            case R.id.mydetail_citys_layout: {
                Intent intent = new Intent(this, OftenGotoPlaceActivity.class);
                intent.putExtra("labels", mydetail_citys.getText().toString());
                startActivityForResult(intent, AppInfo.REQUEST_CODE_OftenGoTOPlace);
            }
            break;
            case R.id.mydetail_img: {
                // SelecterDialog.showPhotoSelecter();
                Intent intent = new Intent(MyDetailInfoActivity.this, ModifyheadActivity.class);
                startActivity(intent);
            }


            break;
        }
    }

    private void personalSquare() {
        PersonalSquare.sendPostRequest(Urls.PersonalSquare, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (squareed.equals("1")) {  //是否关闭广场（0：不关闭，1：关闭）
                            mydetail_sq.setImageResource(R.mipmap.switch2_off2);
                        } else if (squareed.equals("0")) {
                            mydetail_sq.setImageResource(R.mipmap.switch2_open2);
                        }
                    } else {
                        Tools.showToast(MyDetailInfoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyDetailInfoActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyDetailInfoActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }


    public static boolean isRefresh = false;

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            imageLoader.DisplayImage(AppInfo.getUserImagurl(this), mydetail_img, R.mipmap.grxx_icon_mrtx);
            sendData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isRefresh = true;
        MyFragment.isRefresh = true;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppInfo.MyDetailRequestCodeForTake: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg0.jpg";
                    if (!new File(filePath).isFile()) {
                    } else {
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/myImg0.jpg")), "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 200);
                        intent.putExtra("outputY", 200);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/myImg.jpg")));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                    }
                }
                break;
                case AppInfo.MyDetailRequestCodeForPick: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg.jpg";
                    settingImgPath(filePath);
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/myImg.jpg";
                    settingImgPath(filePath);
                }
                break;
            }
        } else if (resultCode == AppInfo.SelectCityResultCode && data != null) {
            city = data.getStringExtra("cityName");
            province = data.getStringExtra("province");
            address = data.getStringExtra("county");
            String str = city;
            if (!TextUtils.isEmpty(province)) {
                if (!province.equals(city))
                    str = province + "-" + str;
            }
            if (!TextUtils.isEmpty(this.address)) {
                if (!this.address.equals(city))
                    str = str + "-" + this.address;
            }
            mydetail_city.setText(str);
            onSearch();
        } else if (resultCode == AppInfo.REQUEST_CODE_NICKNAME && data != null) {
            mydetail_name.setText(data.getStringExtra("nickname"));
            onSearch();
        } else if (resultCode == AppInfo.REQUEST_CODE_NICKTELPHONE && data != null) {
            Restelphone = data.getStringExtra("nicktel");
            mydetail_phone.setText(data.getStringExtra("nicktel"));
            onSearch();
        } else if (resultCode == AppInfo.REQUEST_CODE_AGE && data != null) {
            mydetail_age.setText(data.getStringExtra("age"));
            sendData();
        } else if (resultCode == AppInfo.REQUEST_CODE_OftenGoTOPlace && data != null) {
            mydetail_citys.setText(data.getStringExtra("oftengotoPlace"));
            sendData();
        } else if (requestCode == AppInfo.REQUEST_CODE_FREETIME && data != null) {
            mydetail_freetime.setText(data.getStringExtra("freetime"));
            sendData();
        } else if (requestCode == AppInfo.REQUEST_CODE_SPCIAL && data != null) {
            mydetail_speciality.setText(data.getStringExtra("SPCIAL"));
            sendData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void settingImgPath(String path) {
        if (!new File(path).isFile()) {
            Tools.showToast(MyDetailInfoActivity.this, "拍照方式错误");
            return;
        }
        bitmap = imageZoom(Tools.getBitmap(path), 50);
        if (bitmap == null) {
            Tools.showToast(MyDetailInfoActivity.this, "头像设置失败");
            return;
        }
        mydetail_img.setImageBitmap(bitmap);
        mydetail_img.setTag(path);
        onSearch();
    }

    public void onSearch() {
        if (mydetail_name.getText().toString().trim().equals(AppInfo.getUserName(this)) &&
                city.equals(AppInfo.getUserDistric(this)) &&
                province.equals(AppInfo.getUserdistricProvince(this)) &&
                address.equals(AppInfo.getUserdistricCounty(this)) && bitmap == null) {
//            noEdit();
        } else {
            sendData();
        }
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.UpateUser, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject job = new JSONObject(s);
                    int code = job.getInt("code");
                    if (code == 200) {
                        Tools.showToast(MyDetailInfoActivity.this, job.getString("msg"));
                        AppInfo.setUserinfo2(MyDetailInfoActivity.this, mydetail_name.getText().toString().trim(),
                                city, province, address, null, null);
                        Object ob = mydetail_img.getTag();
                        String filePath = "";
                        if (ob != null) {
                            filePath = ob.toString();
                            AppInfo.setUserImg(MyDetailInfoActivity.this, filePath);
                        }
//                        noEdit();
                    } else {
                        Tools.showToast(MyDetailInfoActivity.this, job.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(MyDetailInfoActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(MyDetailInfoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在修改...");
    }

    private Bitmap imageZoom(Bitmap bitMap, double maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        double mid = b.length / 1024;
        if (mid > maxSize) {
            double i = mid / maxSize;
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }

    private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "摄像头权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
}
