package com.orange.oy.activity.createtask_321;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.createtask_317.AddPhoneActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.mycorps.CheckNewMemberInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.orange.oy.network.Urls.CheckImportMobile;
import static com.orange.oy.network.Urls.ImportMobileSubmit;

/**
 * V3.21  导入邮件
 */
public class ImportEmailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.add_title);
        appTitle.settingName("邮件附件导入");
        appTitle.showBack(this);
    }

    private void iniNetworkConnection() {
        importMobileSubmit = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ImportEmailActivity.this));
                params.put("token", Tools.getToken());
                params.put("email", ed_email.getText().toString().trim()); // 邮箱地址【必传】
                params.put("invisible_type", invisible_type); // 对谁可见或不可见类型，0 可见，1 不可见
                params.put("uuid", UUID); //    唯一的UUID码【必传】
                Tools.d("tag", params.toString());
                return params;
            }
        };

        checkImportMobile = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ImportEmailActivity.this));
                params.put("token", Tools.getToken());
                params.put("uuid", UUID); //    唯一的UUID码【必传】
                Tools.d("tag", params.toString());
                return params;
            }
        };
    }

    private String import_state, phone_list, ischart;
    private EditText ed_email;
    private TextView tv_sure;
    private NetworkConnection importMobileSubmit, checkImportMobile;
    private String invisible_type;
    private String inputText;
    private String UUID;


    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(?:\\.[0-9A-Za-z]+)+$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_email);
        UUID = Tools.getUUID();
        Intent data = getIntent();
        invisible_type = data.getStringExtra("invisible_type");
        ischart = data.getStringExtra("ischart");
        //0 可见，1 不可见
        if (!Tools.isEmpty(ischart)) {
            if ("1".equals(ischart)) {
                if (invisible_type.equals("2")) { //3可见  2 不可见
                    invisible_type = "1";
                } else {
                    invisible_type = "0";
                }
            } else {
                if (invisible_type.equals("3")) { //4可见  3 不可见
                    invisible_type = "1";
                } else {
                    invisible_type = "0";
                }
            }
        }
        initTitle();
        iniNetworkConnection();
        ed_email = (EditText) findViewById(R.id.ed_email);
        tv_sure = (TextView) findViewById(R.id.tv_sure);

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  获取EditText中的输入内容
                inputText = ed_email.getText().toString();
                if (tv_sure.getText().equals("提交")) {
                    if (!checkEmail(inputText)) {
                        Toast.makeText(ImportEmailActivity.this, "请输入正确的邮箱哦~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    getData();

                } else {
                    getData2();
                }
            }
        });

    }

    private static boolean checkEmail(String email) {
        try {
            //正常邮箱 /^\w+((-\w)|(\.\w))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/

            // 含有特殊 字符的 个人邮箱  和 正常邮箱
            //js: 个人邮箱     /^[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+@[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+(\.[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+)+$/

            //java：个人邮箱  [\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+@[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+\\.[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+

            // 范围 更广的 邮箱验证 “/^[^@]+@.+\\..+$/”
            final String pattern1 = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(?:\\.[0-9A-Za-z]+)+$";

            final Pattern pattern = Pattern.compile(pattern1);
            final Matcher mat = pattern.matcher(email);
            return mat.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private void getData() {
        importMobileSubmit.sendPostRequest(Urls.ImportMobileSubmit, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                ConfirmDialog.dissmisDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //  Tools.showToast(getBaseContext(), jsonObject.optString("msg"));
                        //提交成功~
                        tv_sure.setText("批量上传完成后请点这里");
                    } else {
                        Tools.showToast(getBaseContext(), jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(ImportEmailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ImportEmailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, false);
    }

    private void getData2() {
        checkImportMobile.sendPostRequest(Urls.CheckImportMobile, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                ConfirmDialog.dissmisDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONObject object = jsonObject.optJSONObject("data");
                        import_state = object.getString("import_state");  // "是否导入成功的状态，1为成功，0为未成功"
                        phone_list = object.getString("phone_list");//手机号
                  //   ["15011456789","15011456789","15011456780"]
                        if (!Tools.isEmpty(import_state)) {
                            if ("1".equals(import_state)) {     //回到上一个页面
                                Intent intent = new Intent(ImportEmailActivity.this, AddPhoneActivity.class);
                                intent.putExtra("name", phone_list);
                                Tools.d("name======>>>" + phone_list);
                                setResult(AppInfo.REQUEST_CODE_ADD, intent);
                                finish();
                            }
                        }


                    } else {
                        Tools.showToast(getBaseContext(), jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(ImportEmailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ImportEmailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, false);
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
