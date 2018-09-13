package com.orange.oy.activity.mycorps_314;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.SelectDistrictActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.TeamSpecialtyInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.TeamSpecialtyView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateCorpActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, TeamSpecialtyView.OnOtherClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.createcorps_title);
        String name;
        if (isEdit) {
            name = "编辑战队";
        } else {
            name = "创建战队";
        }
        appTitle.settingName(name);
        appTitle.showBack(this);
    }

    private void initNetwork() {
        teamSpeciality = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                return params;
            }
        };
        bulidTeam = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CreateCorpActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_img", Tools.bitmapToBase64(bitmap));
                params.put("team_slogan", createcorps_slogan.getText().toString().trim());
                params.put("team_name", createcorps_name.getText().toString().trim());
                params.put("mobile", createcorps_phone.getText().toString().trim());
                params.put("email", createcorps_email.getText().toString().trim());
                params.put("qq", createcorps_qq.getText().toString().trim());
                params.put("province", province);
                params.put("city", city);
                params.put("sys_speciality", str[0]);
                params.put("custom_speciality", str[1]);
                return params;
            }
        };
        bulidTeam.setIsShowDialog(true);
        editTeamInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CreateCorpActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                return params;
            }
        };
        updateTeam = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CreateCorpActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                if (bitmap != null) {
                    params.put("team_img", Tools.bitmapToBase64(bitmap));
                }
                params.put("team_slogan", createcorps_slogan.getText().toString().trim());
                params.put("team_name", createcorps_name.getText().toString().trim());
                params.put("mobile", createcorps_phone.getText().toString().trim());
                params.put("email", createcorps_email.getText().toString().trim());
                params.put("qq", createcorps_qq.getText().toString().trim());
                if (!TextUtils.isEmpty(province)) {
                    params.put("province", province);
                }
                if (!TextUtils.isEmpty(city)) {
                    params.put("city", city);
                }
                params.put("sys_speciality", str[0]);
                params.put("custom_speciality", str[1]);
                return params;
            }
        };
    }

    private CircularImageView createcorps_img;
    private EditText createcorps_slogan, createcorps_name, createcorps_phone, createcorps_email, createcorps_qq;
    private TextView createcorps_province, createcorps_city;
    private TextView createcorps_sumbit;
    private Bitmap bitmap;//战队队徽
    private NetworkConnection teamSpeciality, bulidTeam, editTeamInfo, updateTeam;
    private ArrayList<TeamSpecialtyInfo> specialty_list;
    private TeamSpecialtyView createcorps_special;
    private ScrollView createcorps_scrollview;
    private String province, city;
    private String team_id;
    private boolean isEdit;//是否是编辑信息
    private ImageLoader imageLoader;
    private String[] str;//战队特长信息
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_corp);
        handler = new Handler();
        imageLoader = new ImageLoader(this);
        specialty_list = new ArrayList<>();
        Intent data = getIntent();
        isEdit = data.getBooleanExtra("isEdit", false);
        team_id = data.getStringExtra("team_id");
        initTitle();
        initView();
        createcorps_special.setOnOtherClickListener(this);
        initNetwork();
        createcorps_img.setImageResource(R.mipmap.grxx_icon_mrtx);
        createcorps_img.setOnClickListener(this);
        createcorps_sumbit.setOnClickListener(this);
        createcorps_province.setOnClickListener(this);
        createcorps_city.setOnClickListener(this);
        createcorps_scrollview.smoothScrollTo(20, 0);
        checkPermission();
        if (isEdit) {//回显数据
            editTeamInfo();
        } else {
            getTeamSpeciality();
            createcorps_phone.setText(AppInfo.getUserphone(this));
        }
    }

    private void editTeamInfo() {
        editTeamInfo.sendPostRequest(Urls.EditTeamInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        String team_img = jsonObject.getString("team_img");
                        if (!"null".equals(team_img) && !TextUtils.isEmpty(team_img)) {
                            imageLoader.DisplayImage(Urls.ImgIp + team_img, createcorps_img, R.mipmap.grxx_icon_mrtx);
                        } else {
                            createcorps_img.setImageResource(R.mipmap.grxx_icon_mrtx);
                        }
                        createcorps_name.setText(jsonObject.getString("team_name"));
                        if (jsonObject.isNull("team_slogan")) {
                            createcorps_slogan.setText("");
                        } else {
                            createcorps_slogan.setText(jsonObject.optString("team_slogan"));
                        }
                        createcorps_phone.setText(jsonObject.getString("mobile"));
                        createcorps_province.setTag(jsonObject.getString("province_id"));
                        String email = jsonObject.getString("email");
                        if (!"null".equals(email) && !TextUtils.isEmpty(email)) {
                            createcorps_email.setText(email);
                        }
                        String qq = jsonObject.getString("qq");
                        if (!"null".equals(qq) && !TextUtils.isEmpty(qq)) {
                            createcorps_qq.setText(qq);
                        }
                        createcorps_province.setText(jsonObject.getString("province"));
                        JSONArray jsonArray = jsonObject.getJSONArray("city");
                        String citys = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (citys == null) {
                                citys = jsonArray.getString(i);
                            } else {
                                citys = citys + "," + jsonArray.getString(i);
                            }
                        }
                        if ("0".equals(citys)) {
                            citys = "全部城市";
                        }
                        createcorps_city.setText(citys);
                        JSONArray jsonArray1 = jsonObject.optJSONArray("sys_speciality");
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            TeamSpecialtyInfo teamSpecialityInfo = new TeamSpecialtyInfo();
                            JSONObject object = jsonArray1.optJSONObject(i);
                            teamSpecialityInfo.setId(object.getString("speciality_id"));
                            teamSpecialityInfo.setName(object.getString("speciality_name"));
                            teamSpecialityInfo.setSelect("1".equals(object.getString("select")));
                            specialty_list.add(teamSpecialityInfo);
                        }
                        JSONArray jsonArray2 = jsonObject.optJSONArray("custom_speciality");
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            TeamSpecialtyInfo teamSpecialtyInfo = new TeamSpecialtyInfo();
                            teamSpecialtyInfo.setSelect(true);
                            teamSpecialtyInfo.setCustom(true);
                            teamSpecialtyInfo.setName(jsonArray2.getString(i));
                            specialty_list.add(teamSpecialtyInfo);
                        }
                        createcorps_special.setTeamSpecialtyDefaultLabels(specialty_list);
                        createcorps_special.notifyDataSetChanged();
                    } else {
                        Tools.showToast(CreateCorpActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CreateCorpActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CreateCorpActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void initView() {
        createcorps_img = (CircularImageView) findViewById(R.id.createcorps_img);
        createcorps_slogan = (EditText) findViewById(R.id.createcorps_slogan);
        createcorps_name = (EditText) findViewById(R.id.createcorps_name);
        createcorps_phone = (EditText) findViewById(R.id.createcorps_phone);
        createcorps_email = (EditText) findViewById(R.id.createcorps_email);
        createcorps_qq = (EditText) findViewById(R.id.createcorps_qq);
        createcorps_province = (TextView) findViewById(R.id.createcorps_province);
        createcorps_city = (TextView) findViewById(R.id.createcorps_city);
        createcorps_sumbit = (TextView) findViewById(R.id.createcorps_sumbit);
        createcorps_special = (TeamSpecialtyView) findViewById(R.id.createcorps_special);
        createcorps_scrollview = (ScrollView) findViewById(R.id.createcorps_scrollview);
    }

    private void getTeamSpeciality() {
        teamSpeciality.sendPostRequest(Urls.TeamSpeciality, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                TeamSpecialtyInfo teamSpecialityInfo = new TeamSpecialtyInfo();
                                JSONObject object = jsonArray.optJSONObject(i);
                                teamSpecialityInfo.setId(object.getString("speciality_id"));
                                teamSpecialityInfo.setName(object.getString("speciality_name"));
                                specialty_list.add(teamSpecialityInfo);
                            }
                            createcorps_special.setTeamSpecialtyDefaultLabels(specialty_list);
                            createcorps_special.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(CreateCorpActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CreateCorpActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CreateCorpActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createcorps_img: {//战队队徽
                SelectPhotoDialog.showPhotoSelecter(this, true, takeListener, pickListener);
            }
            break;
            case R.id.createcorps_province: {//选择省份
                Intent intent = new Intent(this, SelectDistrictActivity.class);
                intent.putExtra("flag", 0);
                startActivityForResult(intent, 200);
            }
            break;
            case R.id.createcorps_city: {//选择城市
                if (createcorps_province.getTag() == null) {
                    Tools.showToast(this, "请先选择省份");
                    return;
                }
                Intent intent = new Intent(this, SelectDistrictActivity.class);
                intent.putExtra("flag", 1);
                intent.putExtra("provinceId", createcorps_province.getTag().toString());
                startActivityForResult(intent, 200);
            }
            break;
            case R.id.createcorps_sumbit: {//提交
                if (!isEdit) {
                    if (bitmap == null) {
                        Tools.showToast(this, "请上传战队队徽~");
                        return;
                    }
                }
                if (!TextUtils.isEmpty(createcorps_slogan.getText().toString().trim())) {
                    if (byteLength(createcorps_slogan) > 100) {
                        Tools.showToast(this, "请填写100个字符以内的口号");
                        return;
                    }
                }
                if (TextUtils.isEmpty(createcorps_name.getText().toString().trim())) {
                    Tools.showToast(this, "请填写战队名称~");
                    return;
                }
                if (byteLength(createcorps_name) > 20) {//名字 20个字符
                    Tools.showToast(this, "请填写20个字符以内的名称~");
                    return;
                }
                if (TextUtils.isEmpty(createcorps_phone.getText().toString().trim())) {
                    Tools.showToast(this, "请填写联系电话~");
                    return;
                }
                if (!Tools.isMobile(createcorps_phone.getText().toString().trim())) {
                    Tools.showToast(this, "请填写正确的手机号码~");
                    return;
                }
                if (!TextUtils.isEmpty(createcorps_email.getText().toString().trim())) {
                    if (!Tools.isEmail(createcorps_email.getText().toString().trim())) {
                        Tools.showToast(this, "请填写正确的邮箱~");
                        return;
                    }
                }
                if (TextUtils.isEmpty(createcorps_province.getText().toString().trim())) {
                    Tools.showToast(this, "请选择省份~");
                    return;
                }
                if (TextUtils.isEmpty(createcorps_city.getText().toString().trim())) {
                    Tools.showToast(this, "请选择城市~");
                    return;
                }
                str = createcorps_special.getSelectLabelForNet();
                if (str.length == 0 || str == null) {
                    Tools.showToast(this, "请选择战队特长~");
                    return;
                }
                if (!isEdit) {
                    sendData();
                } else {
                    updateTeam();
                }
            }
            break;
        }
    }

    private void updateTeam() {
        updateTeam.sendPostRequest(Urls.UpdateTeam, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        baseFinish();
                    } else {
                        Tools.showToast(CreateCorpActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CreateCorpActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CreateCorpActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    public int byteLength(EditText editText) {
        String content = editText.getText().toString().trim();
        try {
            byte[] bt = content.getBytes("gb2312");
            return bt.length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void sendData() {
        bulidTeam.sendPostRequest(Urls.BulidTeam, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(CreateCorpActivity.this, "创建成功");
                        baseFinish();
                    } else {
                        Tools.showToast(CreateCorpActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CreateCorpActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CreateCorpActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto
                    (CreateCorpActivity.this).getPath() + "/corpImg0.jpg")));
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForTake);
        }
    };
    //从手机相册选择
    private View.OnClickListener pickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
        }
    };

    private void settingImgPath(String path) {
        if (!new File(path).isFile()) {
            Tools.showToast(CreateCorpActivity.this, "拍照方式错误");
            return;
        }
        bitmap = imageZoom(Tools.getBitmap(path), 500);
        if (bitmap == null) {
            Tools.showToast(CreateCorpActivity.this, "选取失败，请重试");
            return;
        }
        createcorps_img.setImageBitmap(bitmap);
        createcorps_img.setTag(path);
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

    public void onBackPressed() {
        ConfirmDialog.showDialog(this, "提示", 1, "您确认放弃此次编辑，返回吗？", "取消", "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
            public void leftClick(Object object) {
            }

            public void rightClick(Object object) {
                baseFinish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppInfo.MyDetailRequestCodeForTake: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/corpImg0.jpg";
                    if (new File(filePath).isFile()) {
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/corpImg0.jpg")), "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 400);
                        intent.putExtra("outputY", 400);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/corpImg.jpg")));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                    }
                }
                break;
                case AppInfo.MyDetailRequestCodeForPick: {
                    Uri uri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 400);
                    intent.putExtra("outputY", 400);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                            .getPath() + "/corpImg.jpg")));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(this).getPath() + "/corpImg.jpg";
                    settingImgPath(filePath);
                }
                break;
                case 2: {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        bitmap = BitmapFactory.decodeFile(path);
                        createcorps_img.setImageBitmap(bitmap);
                    }
                }
                break;
            }
        } else if (resultCode == AppInfo.SelectDistrictResultCode1 && requestCode == 200) {
            if (data != null) {
                createcorps_province.setTag(data.getStringExtra("id"));
                String name = data.getStringExtra("name");
                createcorps_province.setText(name);
                if (!name.equals(province)) {
                    city = "";
                    createcorps_city.setText(city);
                }
                province = name;
            }
        } else if (resultCode == AppInfo.SelectDistrictResultCode2 && requestCode == 200) {
            if (data != null) {
                String name = data.getStringExtra("name");
                city = name;
                if ("0".equals(name)) {
                    name = "全部城市";
                }
                createcorps_city.setText(name);
            }
        }
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

    @Override
    public void clickOther() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                createcorps_scrollview.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
            }
        });
    }
}
