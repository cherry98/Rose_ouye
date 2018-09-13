package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.createtask_321.ImportEmailActivity;
import com.orange.oy.activity.shakephoto_316.CollectPhotoActivity;
import com.orange.oy.activity.shakephoto_318.TelephoneAlllistActivity;
import com.orange.oy.adapter.AddPhoneAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DotagDialog;
import com.orange.oy.info.ItemaddPhoneInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orange.oy.R.id.collect_premission;
import static com.orange.oy.R.id.taskILL_title;
import static com.orange.oy.R.id.themeclassify_listview;

/**
 * beibei  添加手机号页面
 */
public class AddPhoneActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AddPhoneAdapter.PhoneEdit {


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.add_title);
        appTitle.settingName("添加手机号");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.kefu, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(AddPhoneActivity.this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(AddPhoneActivity.this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(AddPhoneActivity.this));
                    info.setUname(AppInfo.getUserName(AddPhoneActivity.this));
                }
                SobotApi.startSobotChat(AddPhoneActivity.this, info);
            }
        });
    }

    public void onStop() {
        super.onStop();
        if (labelEdit != null) {
            labelEdit.stop(Urls.LabelEdit);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void iniNetworkConnection() {
        labelEdit = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(AddPhoneActivity.this));
                params.put("token", Tools.getToken());
                params.put("label_id", label_id); // 标签id
                params.put("label_usermobile", usermobile_list); //  手机号
                params.put("type", type); //    类型，1 为添加，0 为删除
                Tools.d("tag", params.toString());
                return params;
            }
        };
    }

    private String label_id, label_usermobile, type;
    private MyListView mListView;
    private LinearLayout lin_addphone;  //添加按钮
    private List<ItemaddPhoneInfo> mData;
    private TextView addphone_button;
    private AddPhoneAdapter addPhoneAdapter;
    private NetworkConnection labelEdit; //添加或者删除标签
    private String Isvisible;  //1可见  2 不可见
    private String ischart;      //  1是集图活动   2是任务模板
    private String invisible_type;
    private String label_name;//标签名称
    private String usermobile_list; //手机号
    private String invisible_label;
    private LinearLayout lin_phonelist, lin_import_phonelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);
        mData = new ArrayList<ItemaddPhoneInfo>();
        initTitle();
        iniNetworkConnection();

        Isvisible = getIntent().getStringExtra("Isvisible");
        label_id = getIntent().getStringExtra("label_id");
        label_name = getIntent().getStringExtra("label_name");
        usermobile_list = getIntent().getStringExtra("usermobile_list"); //从标签页传来的手机号
        ischart = getIntent().getStringExtra("ischart");    //  1是集图活动   2是任务模板


        if (!Tools.isEmpty(usermobile_list)) {
            String[] key_concent = usermobile_list.split(",");
            for (String temp : key_concent) {
                ItemaddPhoneInfo itemaddPhoneInfo = new ItemaddPhoneInfo();
                itemaddPhoneInfo.setText(temp);
                mData.add(itemaddPhoneInfo);
            }
        } else {
            mData.add(new ItemaddPhoneInfo());
        }

        //2为谁不可见红包，3为谁可见红包
        if (!Tools.isEmpty(Isvisible)) {
            if (!Tools.isEmpty(ischart)) {
                if ("1".equals(ischart)) {
                    if (Isvisible.equals("1")) { //1可见  2 不可见
                        invisible_type = "4";
                    } else {
                        invisible_type = "3";
                    }
                } else {  //invisible_type	对谁可见的类型【必传】1为全部，2为仅自己可见，3为谁不可见任务，4为谁可见任务
                    if (Isvisible.equals("1")) { //1可见  2 不可见
                        invisible_type = "4";
                    } else {
                        invisible_type = "3";
                    }
                }
            }
        }
        mListView = (MyListView) findViewById(R.id.list_view);
        lin_addphone = (LinearLayout) findViewById(R.id.lin_addphone);
        addphone_button = (TextView) findViewById(R.id.addphone_button);
        lin_phonelist = (LinearLayout) findViewById(R.id.lin_phonelist);
        lin_import_phonelist = (LinearLayout) findViewById(R.id.lin_import_phonelist);

        lin_phonelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPhoneActivity.this, TelephoneAlllistActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        lin_import_phonelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPhoneActivity.this, ImportEmailActivity.class);
                intent.putExtra("invisible_type", invisible_type);
                intent.putExtra("ischart", ischart);
                startActivityForResult(intent, 2);
            }
        });

       /*   for (int i = 0; i < 8; i++) {
            ItemaddPhoneInfo itemaddPhoneInfo=new ItemaddPhoneInfo;
            itemaddPhoneInfo.setText("1222222"+i);
        }*/

        addPhoneAdapter = new AddPhoneAdapter(this, mData);
        mListView.setAdapter(addPhoneAdapter);
        addPhoneAdapter.setPhoneEditListener(this);
        add();
        commmit();
    }

    String temp = "";

    private void commmit() {

        addphone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check()) {
                    for (int i = 0; i < mData.size(); i++) {
                        temp += mData.get(i).getText() + ",";
                    }
                    final String lookPhoneNumber = temp.substring(0, temp.length() - 1);

                    if (!Tools.isEmpty(label_id)) {
                        //走添加接口
                        type = "1";
                        usermobile_list = lookPhoneNumber;
                        abelEdit();
                        addPhoneAdapter.notifyDataSetChanged();

                    } else {
                        DotagDialog.showDialog(AddPhoneActivity.this, "保存为标签，下次可直接选用", false, new DotagDialog.OnDataUploadClickListener() {
                            @Override
                            public void firstClick() {
                                //存为标签
                                Intent intent = new Intent(AddPhoneActivity.this, AddTagActivity.class);
                                intent.putExtra("Isvisible", Isvisible);
                                intent.putExtra("temp", lookPhoneNumber);
                                startActivityForResult(intent, 0);
                            }

                            @Override
                            public void secondClick() {
                                //忽略
                                Intent intent = new Intent();
                                intent.putExtra("invisible_type", invisible_type);
                                intent.putExtra("invisible_label", label_id);
                                intent.putExtra("usermobile_list", lookPhoneNumber);
                                setResult(AppInfo.REQUEST_CODE_ISVISIBLE, intent);
                                baseFinish();

                                DotagDialog.dissmisDialog();
                            }

                        });
                    }
                }

            }
        });
    }

    private String name;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_ADD) {
            switch (requestCode) {
                case 0: {//从标签页面返回的
                    if (data != null) {
                        invisible_label = data.getStringExtra("invisible_label");
                        usermobile_list = data.getStringExtra("usermobile_list");
                        invisible_type = data.getStringExtra("invisible_type");
                        Tools.d("tag", "addphone-=====>>>>" + usermobile_list);
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", invisible_type);
                        intent.putExtra("invisible_label", label_id);
                        intent.putExtra("usermobile_list", usermobile_list);
                        setResult(AppInfo.REQUEST_CODE_ISVISIBLE, intent);
                        baseFinish();
                    }
                }
                case 1: {  //手机通讯录传回的
                    name = data.getStringExtra("name");
                    Tools.d("tag", name);
                    if (!Tools.isEmpty(name)) {
                        String[] key_concent = name.split(",");
                        for (String temp : key_concent) {
                            ItemaddPhoneInfo itemaddPhoneInfo = new ItemaddPhoneInfo();
                            itemaddPhoneInfo.setText(temp);
                            mData.add(itemaddPhoneInfo);
                            addPhoneAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
                case 2: {  //从写邮件页面返回的
                    name = data.getStringExtra("name");
                    if (!Tools.isEmpty(name)) {
                        String[] key_concent = name.replaceAll("\\[\"", "").replaceAll("\"]", "").split("\",\"");
                        for (String temp : key_concent) {
                            ItemaddPhoneInfo itemaddPhoneInfo = new ItemaddPhoneInfo();
                            itemaddPhoneInfo.setText(temp);
                            mData.add(itemaddPhoneInfo);
                            addPhoneAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
            }
        }
    }

    private void abelEdit() {
        labelEdit.sendPostRequest(Urls.LabelEdit, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(AddPhoneActivity.this, jsonObject.getString("msg"));
                        addPhoneAdapter.notifyDataSetChanged();
                        if (type.equals("1")) {
                            Intent intent = new Intent();
                            intent.putExtra("invisible_type", invisible_type);
                            intent.putExtra("invisible_label", label_id);
                            intent.putExtra("usermobile_list", usermobile_list);
                            setResult(AppInfo.REQUEST_CODE_TAGLIST, intent);
                            Tools.d("tag", "返回到标签页面=====>>>>" + usermobile_list + "  " + label_name + "  " + invisible_type);
                            baseFinish();
                        }

                    } else {
                        Tools.showToast(AddPhoneActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddPhoneActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddPhoneActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void add() {
        lin_addphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemaddPhoneInfo itemaddPhoneInfo = new ItemaddPhoneInfo();
                itemaddPhoneInfo.setText("");
                mData.add(itemaddPhoneInfo);
                addPhoneAdapter.notifyDataSetChanged();
            }
        });
    }

    //验证不等于空 and  手机号码格式验证（11位数字）
    public boolean check() {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getText() == null || mData.get(i).getText().length() != 11) {
                Tools.showToast(getBaseContext(), "请填写正确的手机号");
                return false;
            }
        }
        return true;
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    private String phoneNum;

    @Override
    public void delete(int pos) {

        if (Tools.isEmpty(label_id)) {
            mData.remove(pos);
            addPhoneAdapter.notifyDataSetChanged();
        } else {
            phoneNum = mData.get(pos).getText();
            if (phoneNum == null || phoneNum.equals("")) {
                Tools.showToast(getBaseContext(), "请填写手机号~");
            } else {
                type = "0";
                usermobile_list = phoneNum;
                abelEdit();
                mData.remove(pos);
                addPhoneAdapter.notifyDataSetChanged();
            }
        }

    }
}
