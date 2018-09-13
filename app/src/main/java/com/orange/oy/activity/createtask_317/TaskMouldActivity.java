package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_316.TimeSelectActivity;
import com.orange.oy.activity.shakephoto_320.IdentityCommercialTenantActivity;
import com.orange.oy.activity.shakephoto_320.TaskPrizeActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.ObtainMoreDialog;
import com.orange.oy.info.shakephoto.OptionsListInfo;
import com.orange.oy.info.shakephoto.QuestionListInfo;
import com.orange.oy.info.shakephoto.TaskListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * XXX场景模板
 */
public class TaskMouldActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskmould_title);
        appTitle.settingName("任务模板");
        appTitle.showBack(this);
        if (!"2".equals(which_page)) {
            appTitle.settingExit("存为草稿", new AppTitle.OnExitClickForAppTitle() {
                @Override
                public void onExit() {
                    operation_type = "0";
                    projectPayInfo.setIsShowDialog(false);
                    if (Tools.isEmpty(project_id)) {
                        sendData();
                    } else {
                        baseFinish();
                    }
                }
            });
        }
    }

    private void initNetwork() {
        projectPayInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskMouldActivity.this));
                params.put("token", Tools.getToken());
                if (!TextUtils.isEmpty(template_id)) {
                    params.put("template_id", template_id);
                }
                if (!TextUtils.isEmpty(project_name)) {
                    params.put("project_name", project_name);
                }
                if (!TextUtils.isEmpty(project_note)) {
                    params.put("project_note", project_note);
                }
                if (!TextUtils.isEmpty(tasklist)) {
                    params.put("tasklist", tasklist);
                }
                if (!TextUtils.isEmpty(begin_date)) {
                    params.put("begin_date", begin_date);
                }
                if (!TextUtils.isEmpty(end_date)) {
                    params.put("end_date", end_date);
                }
                if (!TextUtils.isEmpty(address_list)) {
                    params.put("address_list", address_list);
                }
                if (!TextUtils.isEmpty(exe_num)) {
                    params.put("exe_num", exe_num);
                }
                if (Tools.isEmpty(invisible_type)) {
                    invisible_type = "1";
                }
                params.put("invisible_type", invisible_type);
                if (!Tools.isEmpty(invisible_label)) {
                    params.put("invisible_label", invisible_label);
                }
                if (!Tools.isEmpty(invisible_mobile)) {
                    params.put("invisible_mobile", invisible_mobile);
                }
                if (!Tools.isEmpty(show_name)) {
                    params.put("show_name", show_name);
                }
//                params.put("total_money", total_money);//V3.20不用传
                params.put("operation_type", operation_type);
                if (!TextUtils.isEmpty(reward_type)) {
                    params.put("reward_type", reward_type);
                }
                if ("1".equals(reward_type) || "3".equals(reward_type)) {
                    params.put("money", money);
                }
                if ("0".equals(operation_type) && Tools.isEmpty(reward_type)) {//如果是存超过 礼品未设置reward_type默认为1
                    params.put("reward_type", "1");
                }
                if ("2".equals(reward_type) || "3".equals(reward_type)) {
                    params.put("gift_name", gift_name);
                    params.put("gift_money", gift_money);
                    params.put("gift_url", gift_url);
                    if ("2".equals(reward_type)) {
                        params.put("money", "0");
                    }
                }//V3.20
                if (!Tools.isEmpty(invisible_team)) {
                    params.put("invisible_team", invisible_team);
                }//V3.21
                if (!Tools.isEmpty(outlet_package_type)) {
                    params.put("outlet_package_type", outlet_package_type);
                }
                return params;
            }
        };
        projectPayInfo.setIsShowDialog(true);
        bigCustomersSubmit = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskMouldActivity.this));
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                params.put("phone_number", phone_number);
                params.put("company_name", company_name);
                params.put("position", position);
                params.put("name", name);
                params.put("sex", sex);
                return params;
            }
        };
        templateTasklist = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskMouldActivity.this));
                params.put("token", Tools.getToken());
                params.put("template_id", template_id);
                return params;
            }
        };
//        republish = new NetworkConnection(this) {
//            @Override
//            public Map<String, String> getNetworkParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("usermobile", AppInfo.getName(TaskMouldActivity.this));
//                params.put("token", Tools.getToken());
//                params.put("project_id", project_id);
//                return params;
//            }
//        };
        projectPayInfoShow = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskMouldActivity.this));
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                return params;
            }
        };
    }

    private EditText taskmould_name, taskmould_desc;
    private TextView taskmould_content, taskmould_time, taskmould_location, taskmould_permission,
            taskmould_total, taskmould_prize;
    private String template_id, project_name, project_note, tasklist, begin_date, end_date, address_list, exe_num,
            invisible_type, invisible_label, invisible_mobile, show_name = "0", total_money;
    private NetworkConnection projectPayInfo, bigCustomersSubmit, templateTasklist, projectPayInfoShow;
    private String project_id, phone_number, company_name, position, name, sex;
    private ArrayList<TaskListInfo> list_content;
    private String task_size;
    private TextView taskmould_button;
    private RadioButton taskmould_radio2;
    private String money, reward_type, gift_name, gift_money, gift_url;//V3.20
    private String operation_type;//0存草稿 1提交
    private double total_cash, total_gift;//(不含服务费)
    private String which_page, invisible_team, outlet_package_type;//V3.21 TODO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_mould);
        list_content = new ArrayList<>();
        initTitle();
        initView();
        taskmould_radio2.setText(AppInfo.getUserName(this));
        which_page = getIntent().getStringExtra("which_page");//0是创建 //1是编辑 2再投放
        template_id = getIntent().getStringExtra("template_id");
        project_id = getIntent().getStringExtra("project_id");
        initNetwork();
        findViewById(R.id.taskmould_content_ly).setOnClickListener(this);
        findViewById(R.id.taskmould_time_ly).setOnClickListener(this);
        findViewById(R.id.taskmould_location_ly).setOnClickListener(this);
        findViewById(R.id.taskmould_permission_ly).setOnClickListener(this);
        findViewById(R.id.taskmould_total_ly).setOnClickListener(this);
        findViewById(R.id.taskmould_prize_ly).setOnClickListener(this);
        findViewById(R.id.taskmould_kefu).setOnClickListener(this);
        taskmould_button.setOnClickListener(this);
        ((RadioGroup) findViewById(R.id.taskmould_group)).setOnCheckedChangeListener(this);
        switch (which_page) {
            case "3":
            case "0": {//创建
                templateTasklist();
            }
            break;
            case "1": {//编辑
                projectPayInfoShow();
            }
            break;
            case "2": {//再投放原republish
                projectPayInfoShow();
            }
            break;
        }
    }

    private void projectPayInfoShow() {
        projectPayInfoShow.sendPostRequest(Urls.ProjectPayInfoShow, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        template_id = jsonObject.getString("template_id");
                        project_name = jsonObject.getString("project_name");
                        taskmould_name.setText(project_name);
                        taskmould_desc.setText(jsonObject.getString("project_note"));
                        taskmould_time.setText("");
                        if ("1".equals(which_page)) {
                            String begin_date = jsonObject.optString("begin_date");
                            String end_date = jsonObject.optString("end_date");
                            if (!TextUtils.isEmpty(begin_date) && !TextUtils.isEmpty(end_date)) {
                                TaskMouldActivity.this.begin_date = begin_date;
                                TaskMouldActivity.this.end_date = end_date;
                                taskmould_time.setText(begin_date + "~" + end_date);
                            }
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("task_list");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("task_list", jsonArray);
                            tasklist = jsonObject1.toString();
                            int length = jsonArray.length();
                            taskmould_content.setText("共设置了" + length + "个任务");
                            for (int i = 0; i < length; i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                TaskListInfo taskListInfo = new TaskListInfo();
                                taskListInfo.setIs_watermark(object.getString("is_watermark"));
                                taskListInfo.setLocal_photo(object.getString("local_photo"));
                                taskListInfo.setNote(object.getString("note"));

                                JSONArray photourls = object.optJSONArray("photourl");//示例图片
                                if (photourls != null) {
                                    int count = photourls.length();
                                    ArrayList<String> pls = new ArrayList<>();
                                    for (int j = 0; j < count; j++) {
                                        pls.add(photourls.getString(j));
                                    }
                                    taskListInfo.setPhotourl(pls);
                                }

                                taskListInfo.setTask_name(object.getString("task_name"));
                                taskListInfo.setTask_type(object.getString("task_type"));

                                //3.21新增
                                taskListInfo.setSta_location(object.getString("sta_location"));
                                taskListInfo.setOnline_store_name(object.getString("online_store_name"));
                                taskListInfo.setOnline_store_url(object.getString("online_store_url"));

                                JSONArray vedeourls = object.optJSONArray("videourl");//示例视频
                                if (vedeourls != null) {
                                    int size = vedeourls.length();
                                    ArrayList<String> vls = new ArrayList<>();
                                    for (int e = 0; e < size; e++) {
                                        vls.add(vedeourls.getString(e));
                                    }
                                    taskListInfo.setVideourl(vls);
                                }
                                JSONArray question_list = object.optJSONArray("question_list");
                                if (question_list != null) {
                                    ArrayList<QuestionListInfo> qls = new ArrayList<QuestionListInfo>();
                                    for (int j = 0; j < question_list.length(); j++) {
                                        JSONObject object1 = question_list.getJSONObject(j);
                                        QuestionListInfo questionListInfo = new QuestionListInfo();
                                        questionListInfo.setQuestion_id(object1.getString("question_id"));
                                        questionListInfo.setQuestion_type(object1.getString("question_type"));
                                        questionListInfo.setQuestion_name(object1.getString("question_name"));
                                        questionListInfo.setMax_option(object1.getString("max_option"));
                                        questionListInfo.setMin_option(object1.getString("min_option"));
                                        questionListInfo.setIsrequired(object1.getString("isrequired"));
                                        questionListInfo.setQuestion_num(object1.getString("question_num"));
                                        JSONArray array = object1.optJSONArray("options");
                                        if (array != null) {
                                            ArrayList<OptionsListInfo> opls = new ArrayList<OptionsListInfo>();
                                            for (int k = 0; k < array.length(); k++) {
                                                OptionsListInfo optionsListInfo = new OptionsListInfo();
                                                JSONObject object2 = array.getJSONObject(k);
                                                optionsListInfo.setOption_id(object2.getString("option_id"));
                                                optionsListInfo.setOption_name(object2.getString("option_name"));
                                                optionsListInfo.setOption_num(object2.getString("option_num"));
                                                optionsListInfo.setPhoto_url(object2.getString("photo_url"));
                                                opls.add(optionsListInfo);
                                            }
                                            questionListInfo.setOptions(opls);
                                        }
                                        qls.add(questionListInfo);
                                    }
                                    taskListInfo.setQuestion_list(qls);
                                }
                                list_content.add(taskListInfo);
                            }
                        }
                        JSONArray jsonArray1 = jsonObject.optJSONArray("address_list");
                        exe_num = jsonObject.optString("exe_num");
                        if (jsonArray1 != null && jsonArray1.length() > 0 && !Tools.isEmpty(exe_num)) {
                            task_size = jsonArray1.length() + "";
                            taskmould_location.setText("共设置了" + jsonArray1.length() + "个位置");
                            JSONObject jsonObject2 = new JSONObject();
                            jsonObject2.put("address_list", jsonArray1);
                            address_list = jsonObject2.toString();
                        }
                        invisible_type = jsonObject.getString("invisible_type");
                        if (Tools.isEmpty(invisible_type)) {
                            invisible_type = "";
                        }
                        if ("1".equals(invisible_type)) {
                            taskmould_permission.setText("全部");
                            taskmould_button.setText("支付");
                        } else if ("2".equals(invisible_type)) {
                            taskmould_button.setText("确定");
                            taskmould_permission.setText("仅自己");
                        } else {
                            taskmould_button.setText("支付");
                            taskmould_permission.setText("部分");
                        }
//                        invisible_label = jsonObject.getString("invisible_label");
                        invisible_mobile = jsonObject.getString("invisible_mobile");
                        show_name = jsonObject.getString("show_name");
                        reward_type = jsonObject.getString("reward_type");

//                        total_money = jsonObject.getString("total_money");
//                        taskmould_total.setText(Tools.removePoint(total_money));
                        //奖励设置回显数据
                        if ("1".equals(reward_type)) {
                            money = jsonObject.getString("money");
                            if (Tools.StringToDouble(money) <= 0) {
                                money = "";
                            } else {
                                taskmould_prize.setText("现金");
                            }
                            if (!Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size) && !Tools.isEmpty(money)) {
                                total_cash = Tools.StringToDouble(money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                            }
                        } else if ("2".equals(reward_type)) {
                            taskmould_prize.setText("礼品");
                            gift_name = jsonObject.getString("gift_name");
                            gift_money = jsonObject.getString("gift_money");
                            gift_url = jsonObject.getString("gift_url");
                            if (!Tools.isEmpty(gift_money) && !Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size)) {
                                total_gift = Tools.StringToDouble(gift_money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                            }
                        } else if ("3".equals(reward_type)) {
                            money = jsonObject.getString("money");
                            taskmould_prize.setText("现金+礼品");
                            gift_name = jsonObject.getString("gift_name");
                            gift_money = jsonObject.getString("gift_money");
                            gift_url = jsonObject.getString("gift_url");
                            if (!Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size) && !Tools.isEmpty(money)) {
                                total_cash = Tools.StringToDouble(money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                            }
                            if (!Tools.isEmpty(gift_money) && !Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size)) {
                                total_gift = Tools.StringToDouble(gift_money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                            }
                        }

                        //计算总金额
                        if (total_cash + total_gift > 0) {
                            findViewById(R.id.taskmould_total_ly).setVisibility(View.VISIBLE);
                            taskmould_total.setText(Tools.removePoint(Tools.savaTwoByte(1.2 * total_cash + 1.1 * total_gift)));
                        } else {
                            findViewById(R.id.taskmould_total_ly).setVisibility(View.GONE);
                        }
                    } else {
                        Tools.showToast(TaskMouldActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    /*private void republish() {
        republish.sendPostRequest(Urls.Republish, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        template_id = jsonObject.getString("template_id");
                        project_name = jsonObject.getString("project_name");
                        taskmould_name.setText(project_name);
                        taskmould_desc.setText(jsonObject.getString("project_note"));
                        taskmould_time.setText("");
//                        String begin_date = jsonObject.optString("begin_date");
//                        String end_date = jsonObject.optString("end_date");
//                        if (!TextUtils.isEmpty(begin_date) && !TextUtils.isEmpty(end_date)) {
//                            TaskMouldActivity.this.begin_date = begin_date;
//                            TaskMouldActivity.this.end_date = end_date;
//                            taskmould_time.setText(begin_date + "~" + end_date);
//                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("tasklist");
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("task_list", jsonArray);
                        tasklist = jsonObject1.toString();
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            taskmould_content.setText("共设置了" + length + "个任务");
                            for (int i = 0; i < length; i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                TaskListInfo taskListInfo = new TaskListInfo();
                                taskListInfo.setIs_watermark(object.getString("is_watermark"));
                                taskListInfo.setLocal_photo(object.getString("local_photo"));
                                taskListInfo.setNote(object.getString("note"));
                                JSONArray photourls = object.optJSONArray("photourl");
                                int count = photourls.length();
                                ArrayList<String> pls = new ArrayList<>();
                                for (int j = 0; j < count; j++) {
                                    pls.add(photourls.getString(j));
                                }
                                taskListInfo.setPhotourl(pls);
                                taskListInfo.setTask_name(object.getString("task_name"));
//                                taskListInfo.setTask_id(object.getString("task_id"));
                                taskListInfo.setTask_type(object.getString("task_type"));

                                //3.21新增
                                taskListInfo.setSta_location(object.getString("sta_location"));
                                taskListInfo.setOnline_store_name(object.getString("online_store_name"));
                                taskListInfo.setOnline_store_url(object.getString("online_store_url"));

                                JSONArray vedeourls = object.optJSONArray("videourl");//示例视频
                                if (vedeourls != null) {
                                    int size = vedeourls.length();
                                    ArrayList<String> vls = new ArrayList<>();
                                    for (int e = 0; e < size; e++) {
                                        vls.add(vedeourls.getString(e));
                                    }
                                    taskListInfo.setVideourl(vls);
                                }
                                JSONArray question_list = object.optJSONArray("question_list");
                                if (question_list != null) {
                                    ArrayList<QuestionListInfo> qls = new ArrayList<QuestionListInfo>();
                                    for (int j = 0; j < question_list.length(); j++) {
                                        JSONObject object1 = question_list.getJSONObject(j);
                                        QuestionListInfo questionListInfo = new QuestionListInfo();
                                        questionListInfo.setQuestion_id(object1.getString("question_id"));
                                        questionListInfo.setQuestion_type(object1.getString("question_type"));
                                        questionListInfo.setQuestion_name(object1.getString("question_name"));
                                        questionListInfo.setMax_option(object1.getString("max_option"));
                                        questionListInfo.setMin_option(object1.getString("min_option"));
                                        questionListInfo.setIsrequired(object1.getString("isrequired"));
                                        questionListInfo.setQuestion_num(object1.getString("question_num"));
                                        JSONArray array = object1.optJSONArray("options");
                                        if (array != null) {
                                            ArrayList<OptionsListInfo> opls = new ArrayList<OptionsListInfo>();
                                            for (int k = 0; k < array.length(); k++) {
                                                OptionsListInfo optionsListInfo = new OptionsListInfo();
                                                JSONObject object2 = array.getJSONObject(k);
                                                optionsListInfo.setOption_id(object2.getString("option_id"));
                                                optionsListInfo.setOption_name(object2.getString("option_name"));
                                                optionsListInfo.setOption_num(object2.getString("option_num"));
                                                optionsListInfo.setPhoto_url(object2.getString("photo_url"));
                                                opls.add(optionsListInfo);
                                            }
                                            questionListInfo.setOptions(opls);
                                        }
                                        qls.add(questionListInfo);
                                    }
                                    taskListInfo.setQuestion_list(qls);
                                }
                                list_content.add(taskListInfo);
                            }
                        }
                        JSONArray jsonArray1 = jsonObject.optJSONArray("address_list");
                        task_size = jsonArray1.length() + "";
                        taskmould_location.setText("共设置了" + jsonArray1.length() + "个位置");
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("address_list", jsonArray1);
                        address_list = jsonObject2.toString();
                        exe_num = jsonObject.optString("exe_num");
                        invisible_type = jsonObject.getString("invisible_type");
                        if (Tools.isEmpty(invisible_type)) {
                            invisible_type = "";
                        }
                        if ("1".equals(invisible_type)) {
                            taskmould_permission.setText("全部可见");
                            taskmould_button.setText("支付");
                        } else if ("2".equals(invisible_type)) {
                            taskmould_button.setText("确定");
                            taskmould_permission.setText("仅自己可见");
                        } else {
                            taskmould_button.setText("支付");
                            taskmould_permission.setText("部分可见");
                        }
                        invisible_label = jsonObject.getString("invisible_label");
                        invisible_mobile = jsonObject.getString("invisible_mobile");
                        show_name = jsonObject.getString("show_name");
                        if ("0".equals(show_name)) {
                            taskmould_radio1.setChecked(true);
                            taskmould_radio2.setChecked(false);
                        } else {
                            taskmould_radio1.setChecked(false);
                            taskmould_radio2.setChecked(true);
                        }
                        //奖励设置回显数据
                        reward_type = jsonObject.getString("reward_type");
                        if ("1".equals(reward_type)) {
                            money = jsonObject.getString("money");
                            if (Tools.StringToDouble(money) <= 0) {
                                money = "";
                            } else {
                                taskmould_prize.setText("现金");
                            }
                            if (!Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size) && !Tools.isEmpty(money)) {
                                total_cash = Tools.StringToDouble(money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                            }
                        } else if ("2".equals(reward_type)) {
                            taskmould_prize.setText("礼品");
                            gift_name = jsonObject.getString("gift_name");
                            gift_money = jsonObject.getString("gift_money");
                            gift_url = jsonObject.getString("gift_url");
                            if (!Tools.isEmpty(gift_money) && !Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size)) {
                                total_gift = Tools.StringToDouble(gift_money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                            }
                        } else if ("3".equals(reward_type)) {
                            money = jsonObject.getString("money");
                            taskmould_prize.setText("现金+礼品");
                            gift_name = jsonObject.getString("gift_name");
                            gift_money = jsonObject.getString("gift_money");
                            gift_url = jsonObject.getString("gift_url");
                            if (!Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size) && !Tools.isEmpty(money)) {
                                total_cash = Tools.StringToDouble(money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                            }
                            if (!Tools.isEmpty(gift_money) && !Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size)) {
                                total_gift = Tools.StringToDouble(gift_money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                            }
                        }

                        //计算总金额
                        if (total_cash + total_gift > 0) {
                            findViewById(R.id.taskmould_total_ly).setVisibility(View.VISIBLE);
                            taskmould_total.setText(Tools.removePoint(Tools.savaTwoByte(1.2 * total_cash + 1.1 * total_gift)));
                        } else {
                            findViewById(R.id.taskmould_total_ly).setVisibility(View.GONE);
                        }

                    } else {
                        Tools.showToast(TaskMouldActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }*/

    private void templateTasklist() {
        templateTasklist.sendPostRequest(Urls.TemplateTasklist, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        project_name = jsonObject.optString("project_name");
                        if (!TextUtils.isEmpty(project_name)) {
                            taskmould_name.setText(project_name);
                        }
                        project_note = jsonObject.optString("project_note");
                        if (!TextUtils.isEmpty(project_note)) {
                            taskmould_desc.setText(project_note);
                        }
                        begin_date = jsonObject.optString("begin_date");
                        end_date = jsonObject.optString("end_date");
                        if (!TextUtils.isEmpty(begin_date) && !TextUtils.isEmpty(end_date)) {
                            TaskMouldActivity.this.begin_date = begin_date;
                            TaskMouldActivity.this.end_date = end_date;
                            taskmould_time.setText(begin_date + "~" + end_date);
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("task_list");
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                TaskListInfo taskListInfo = new TaskListInfo();
                                taskListInfo.setIs_watermark(object.getString("is_watermark"));
                                taskListInfo.setLocal_photo(object.getString("local_photo"));
                                taskListInfo.setNote(object.getString("note"));
                                JSONArray photourls = object.optJSONArray("photourl");
                                if (photourls != null) {
                                    int count = photourls.length();
                                    ArrayList<String> pls = new ArrayList<>();
                                    for (int j = 0; j < count; j++) {
                                        String url = photourls.getString(j);
                                        if (!Tools.isEmpty(url)) {
                                            pls.add(url);
                                        }
                                    }
                                    taskListInfo.setPhotourl(pls);
                                }
                                taskListInfo.setTask_name(object.getString("task_name"));
                                taskListInfo.setTask_type(object.getString("task_type"));
                                //V3.21新增
                                JSONArray videourl = object.optJSONArray("videourl");
                                if (videourl != null) {
                                    ArrayList<String> vls = new ArrayList<String>();
                                    for (int j = 0; j < videourl.length(); j++) {
                                        String url = videourl.getString(j);
                                        if (!Tools.isEmpty(url)) {
                                            vls.add(url);
                                        }
                                    }
                                    taskListInfo.setVideourl(vls);
                                }
                                taskListInfo.setSta_location(object.getString("sta_location"));
                                taskListInfo.setOnline_store_url(object.getString("online_store_url"));
                                taskListInfo.setOnline_store_name(object.getString("online_store_name"));
                                JSONArray question_list = object.optJSONArray("question_list");
                                if (question_list != null) {
                                    ArrayList<QuestionListInfo> qls = new ArrayList<QuestionListInfo>();
                                    for (int j = 0; j < question_list.length(); j++) {
                                        QuestionListInfo questionListInfo = new QuestionListInfo();
                                        JSONObject object1 = question_list.getJSONObject(j);
                                        questionListInfo.setQuestion_id(object1.getString("question_id"));
                                        questionListInfo.setQuestion_name(object1.getString("question_name"));
                                        questionListInfo.setQuestion_num(object1.getString("question_num"));
                                        questionListInfo.setQuestion_type(object1.getString("question_type"));
                                        questionListInfo.setMin_option(object1.getString("min_option"));
                                        questionListInfo.setMax_option(object1.getString("max_option"));
                                        questionListInfo.setIsrequired(object1.getString("isrequired"));
                                        JSONArray options = object1.optJSONArray("options");
                                        if (options != null) {
                                            ArrayList<OptionsListInfo> ols = new ArrayList<OptionsListInfo>();
                                            for (int k = 0; k < options.length(); k++) {
                                                JSONObject object2 = options.getJSONObject(k);
                                                OptionsListInfo optionsListInfo = new OptionsListInfo();
                                                optionsListInfo.setOption_id(object2.getString("option_id"));
                                                optionsListInfo.setOption_num(object2.getString("option_num"));
                                                optionsListInfo.setOption_name(object2.getString("option_name"));
                                                optionsListInfo.setPhoto_url(object2.getString("photo_url"));
                                                ols.add(optionsListInfo);
                                            }
                                            questionListInfo.setOptions(ols);
                                        }
                                        qls.add(questionListInfo);
                                    }
                                    taskListInfo.setQuestion_list(qls);
                                }
                                list_content.add(taskListInfo);
                            }
                            if (!list_content.isEmpty()) {
                                tasklist = getTasklist(list_content);
                                taskmould_content.setText("共设置了" + list_content.size() + "个任务");
                            }
                        }
                    } else {
                        Tools.showToast(TaskMouldActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void initView() {
        taskmould_name = (EditText) findViewById(R.id.taskmould_name);
        taskmould_desc = (EditText) findViewById(R.id.taskmould_desc);
        taskmould_content = (TextView) findViewById(R.id.taskmould_content);
        taskmould_time = (TextView) findViewById(R.id.taskmould_time);
        taskmould_location = (TextView) findViewById(R.id.taskmould_location);
        taskmould_permission = (TextView) findViewById(R.id.taskmould_permission);
        taskmould_total = (TextView) findViewById(R.id.taskmould_total);
        taskmould_button = (TextView) findViewById(R.id.taskmould_button);
        taskmould_prize = (TextView) findViewById(R.id.taskmould_prize);
        taskmould_radio2 = (RadioButton) findViewById(R.id.taskmould_radio2);
    }

    public void onBack() {
        projectPayInfo.setIsShowDialog(false);
        if (!"1".equals(which_page) && Tools.isEmpty(project_id)) {
            ConfirmDialog.showDialog(TaskMouldActivity.this, "提示", 2, "需要存为草稿吗？", "不需要", "需要", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                    baseFinish();
                }

                public void rightClick(Object object) {
                    operation_type = "0";
                    sendData();
                    baseFinish();
                }
            });
        } else {
            baseFinish();
        }
    }

    public void onBackPressed() {
        projectPayInfo.setIsShowDialog(false);
        if (!"1".equals(which_page) && Tools.isEmpty(project_id)) {
            ConfirmDialog.showDialog(TaskMouldActivity.this, "提示", 2, "需要存为草稿吗？", "不需要", "需要", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                    baseFinish();
                }

                public void rightClick(Object object) {
                    operation_type = "0";
                    sendData();
                    baseFinish();
                }
            });
        } else {
            baseFinish();
        }
    }

    private boolean isClick = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskmould_content_ly: {//任务内容选择
                Intent intent = new Intent(this, TaskContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list_content", list_content);
                intent.putExtra("data", bundle);
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.taskmould_time_ly: {//起始时间选择
                Intent intent = new Intent(this, TimeSelectActivity.class);
                intent.putExtra("begin_date", begin_date);
                intent.putExtra("end_date", end_date);
                intent.putExtra("isTask", true);
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.taskmould_location_ly: {//位置选择
                Intent intent = new Intent(this, LocationListActivity.class);
                intent.putExtra("template_id", template_id);
                intent.putExtra("isCreate", "0".equals(which_page));
                if (!"0".equals(which_page)) {
                    intent.putExtra("address_list", address_list);
                }
                intent.putExtra("exe_num", exe_num);
                startActivityForResult(intent, 2);
            }
            break;
            //V3.21  invisible_type	对谁可见的类型【必传】1为全部，2为仅自己可见，3为谁不可见任务，4为谁可见任务
            case R.id.taskmould_permission_ly: {//可见权限选择
                Intent intent = new Intent(this, ToWhomVisibleActivity.class);
                intent.putExtra("isFrist", "1");
                intent.putExtra("ischart", "2");
                startActivityForResult(intent, 3);
            }
            break;
            case R.id.taskmould_button: {//提交按钮
                operation_type = "1";
                projectPayInfo.setIsShowDialog(true);
                project_name = taskmould_name.getText().toString().trim();
                if (Tools.isEmpty(project_name)) {
                    Tools.showToast(this, "请填写任务名称");
                    return;
                }
                project_note = taskmould_desc.getText().toString().trim();
                if (Tools.isEmpty(taskmould_content.getText().toString().trim())) {
                    Tools.showToast(this, "请设置可执行的任务内容");
                    return;
                }
                if (Tools.isEmpty(taskmould_time.getText().toString().trim())) {
                    Tools.showToast(this, "请选择任务起止日期");
                    return;
                }
                if (Tools.isEmpty(address_list)) {
                    Tools.showToast(this, "请设置投放位置");
                    return;
                }
                if (Tools.isEmpty(taskmould_permission.getText().toString().trim())) {
                    Tools.showToast(this, "请选择可见权限");
                    return;
                }
                if (Tools.isEmpty(taskmould_prize.getText().toString().trim())) {
                    Tools.showToast(this, "请设置任务奖励");
                    return;
                }
                total_money = taskmould_total.getText().toString().trim();
                //金额超过10000元
                isClick = false;
                sendData();
            }
            case R.id.taskmould_total_ly: {//支付总金额
                if (!isClick) {
                    return;
                }
                if (Tools.isEmpty(taskmould_total.getText().toString().trim())) {
                    return;
                }
                Intent intent = new Intent(TaskMouldActivity.this, TotalPaymentActivity.class);
                intent.putExtra("total_cash", total_cash);
                intent.putExtra("total_gift", total_gift);
                startActivity(intent);
            }
            break;
            case R.id.taskmould_prize_ly: {//任务奖励
                Intent intent = new Intent(this, TaskPrizeActivity.class);
                startActivityForResult(intent, 4);
            }
            break;
            case R.id.taskmould_kefu: {//客服
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(this));
                    info.setUname(AppInfo.getUserName(this));
                }
                SobotApi.startSobotChat(this, info);
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClick = true;
    }

    private void bigCustomersSubmit() {
        bigCustomersSubmit.sendPostRequest(Urls.BigCustomersSubmit, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        ConfirmDialog.showDialog(TaskMouldActivity.this, "提示", 1, "您的资料客服已收到，我们会尽快联系您！", "", "我知道了",
                                null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                    public void leftClick(Object object) {
                                    }

                                    public void rightClick(Object object) {
                                        baseFinish();
                                    }
                                }).goneLeft();
                    } else {
                        Tools.showToast(TaskMouldActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void sendData() {
        projectPayInfo.sendPostRequest(Urls.ProjectPayInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                CustomProgressDialog.Dissmiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if ("0".equals(operation_type)) {//存草稿
                            baseFinish();
                        } else {
                            jsonObject = jsonObject.optJSONObject("data");
                            if (jsonObject != null) {
                                String account_money = jsonObject.getString("account_money");
                                project_id = jsonObject.getString("project_id");
                                if (Tools.StringToDouble(taskmould_total.getText().toString().trim()) >= 10000) {
                                    ObtainMoreDialog.showDialog(TaskMouldActivity.this, new ObtainMoreDialog.OnObtainMoreListener() {
                                        @Override
                                        public void onSubmit(String phone_number, String company_name, String position, String name, String sex) {
                                            TaskMouldActivity.this.phone_number = phone_number;
                                            TaskMouldActivity.this.company_name = company_name;
                                            TaskMouldActivity.this.position = position;
                                            TaskMouldActivity.this.name = name;
                                            TaskMouldActivity.this.sex = sex;
                                            bigCustomersSubmit();
                                        }

                                        @Override
                                        public void cancel() {
                                            isClick = true;
                                        }
                                    });
                                } else {
                                    Intent intent = new Intent(TaskMouldActivity.this, PayCaseActivity.class);
                                    intent.putExtra("project_id", project_id);
                                    intent.putExtra("total_money", total_money);
                                    intent.putExtra("account_money", account_money);
                                    startActivity(intent);
                                }
                            }
                        }
                    } else {
                        String msg = jsonObject.getString("msg");
                        if ("商户未认证".equals(msg)) {
                            Intent intent = new Intent(TaskMouldActivity.this, IdentityCommercialTenantActivity.class);
                            intent.putExtra("isHaveTag", "1");
                            startActivity(intent);
                        } else {
                            Tools.showToast(TaskMouldActivity.this, msg);
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskMouldActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, 3000);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.taskmould_radio1) {//匿名
            show_name = "0";
        } else if (checkedId == R.id.taskmould_radio2) {//昵称
            show_name = "1";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_COLLECT) {
            switch (requestCode) {
                case 0: {//任务内容
                    if (data != null) {
                        list_content.clear();
                        list_content.addAll((ArrayList<TaskListInfo>) data.getBundleExtra("data").getSerializable("tasklist"));
                        list_content.remove(0);
                        if (!list_content.isEmpty()) {
                            tasklist = getTasklist(list_content);
                            taskmould_content.setText("共设置了" + list_content.size() + "个任务");
                        }
                    }
                }
                break;
                case 1: {//时间选择
                    if (data != null) {
                        begin_date = data.getStringExtra("begin_time");
                        end_date = data.getStringExtra("end_time");
                        taskmould_time.setText(begin_date + "~" + end_date);
                    }
                }
                break;
                case 2: {//位置信息
                    if (data != null) {
                        exe_num = data.getStringExtra("exe_num");
                        address_list = data.getStringExtra("address_list");
                        task_size = data.getStringExtra("task_size");
                        taskmould_location.setText("共设置了" + task_size + "个位置");
                        if (Tools.isEmpty(money) && Tools.isEmpty(gift_money)) {
                            taskmould_total.setText("");
                        } else {
                            if ("1".equals(reward_type) || "3".equals(reward_type)) {
                                total_cash = Tools.StringToDouble(money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                                if ("1".equals(reward_type)) {
                                    total_gift = 0;
                                }
                            }
                            if ("2".equals(reward_type) || "3".equals(reward_type)) {
                                total_gift = Tools.StringToDouble(gift_money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                                if ("2".equals(reward_type)) {
                                    total_cash = 0;
                                }
                            }
//                            if ("1".equals(reward_type)) {
//                                taskmould_prize.setText("现金");
//                            } else if ("2".equals(reward_type)) {
//                                taskmould_prize.setText("礼品");
//                            } else if ("3".equals(reward_type)) {
//                                taskmould_prize.setText("现金+礼品");
//                            }
                            findViewById(R.id.taskmould_total_ly).setVisibility(View.VISIBLE);
                            taskmould_total.setText(Tools.removePoint(Tools.savaTwoByte((1.2 * total_cash + 1.1 * total_gift))));
                        }
                    }
                }
                break;
                case 3: {//可见权限
                    if (data != null) {
                        invisible_type = data.getStringExtra("invisible_type");
                        invisible_mobile = data.getStringExtra("usermobile_list");
                        invisible_label = data.getStringExtra("invisible_label");
                        invisible_team = data.getStringExtra("invisible_team");
                        outlet_package_type = data.getStringExtra("outlet_package_type");
                        if ("2".equals(invisible_type)) {
                            taskmould_permission.setText("仅自己可见");
                            taskmould_button.setText("确定");
                        } else if ("1".equals(invisible_type)) {
                            taskmould_permission.setText("全部可见");
                            taskmould_button.setText("支付");
                        } else {
                            taskmould_button.setText("支付");
                            taskmould_permission.setText("部分可见");
                        }
                    }
                }
                break;
                case 4: {//设置奖励
                    if (data != null) {
                        reward_type = data.getStringExtra("reward_type");
                        if ("1".equals(reward_type)) {
                            taskmould_prize.setText("现金");
                        } else if ("2".equals(reward_type)) {
                            taskmould_prize.setText("礼品");
                        } else if ("3".equals(reward_type)) {
                            taskmould_prize.setText("现金+礼品");
                        }
                        if ("1".equals(reward_type) || "3".equals(reward_type)) {
                            money = data.getStringExtra("money");
                        }
                        if ("2".equals(reward_type) || "3".equals(reward_type)) {
                            gift_name = data.getStringExtra("gift_name");
                            gift_url = data.getStringExtra("gift_url");
                            gift_money = data.getStringExtra("gift_money");
                        }
                        if (!Tools.isEmpty(exe_num) && !Tools.isEmpty(task_size)) {
                            if ("1".equals(reward_type) || "3".equals(reward_type)) {
                                total_cash = Tools.StringToDouble(money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                                if ("1".equals(reward_type)) {
                                    total_gift = 0;
                                }
                            }
                            if ("2".equals(reward_type) || "3".equals(reward_type)) {
                                total_gift = Tools.StringToDouble(gift_money) * Tools.StringToDouble(exe_num) * Tools.StringToDouble(task_size);
                                if ("2".equals(reward_type)) {
                                    total_cash = 0;
                                }
                            }
                            findViewById(R.id.taskmould_total_ly).setVisibility(View.VISIBLE);
                            taskmould_total.setText(Tools.removePoint(Tools.savaTwoByte((1.2 * total_cash + 1.1 * total_gift))));
                        }
                    }
                }
                break;
            }
        }
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
                JSONArray jsonArray2 = new JSONArray(taskListInfo.getVideourl());
                jsonObject.put("videourl", jsonArray2);
                jsonObject.put("online_store_name", taskListInfo.getOnline_store_name());
                jsonObject.put("online_store_url", taskListInfo.getOnline_store_url());
                jsonObject.put("sta_location", taskListInfo.getSta_location());
                ArrayList<QuestionListInfo> question_list = taskListInfo.getQuestion_list();
                if (question_list != null && !question_list.isEmpty()) {
                    JSONArray jsonArray3 = getQuestionList(question_list);
                    jsonObject.put("question_list", jsonArray3);
                } else {
                    jsonObject.put("question_list", new JSONArray());
                }
                jsonArray.put(jsonObject);
            }
            jsonObject1.put("task_list", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1.toString();
    }

    private JSONArray getQuestionList(ArrayList<QuestionListInfo> list) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            QuestionListInfo questionListInfo = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("question_id", questionListInfo.getQuestion_id());
            jsonObject.put("question_type", questionListInfo.getQuestion_type());
            jsonObject.put("question_name", questionListInfo.getQuestion_name());
            jsonObject.put("max_option", questionListInfo.getMax_option());
            jsonObject.put("min_option", questionListInfo.getMin_option());
            jsonObject.put("isrequired", questionListInfo.getIsrequired());
            jsonObject.put("question_num", questionListInfo.getQuestion_num());
            ArrayList<OptionsListInfo> options = questionListInfo.getOptions();
            if (options != null && !options.isEmpty()) {
                JSONArray jsonArray1 = getOptionList(questionListInfo.getOptions());
                jsonObject.put("options", jsonArray1);
            } else {
                jsonObject.put("options", new JSONArray());
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    private JSONArray getOptionList(ArrayList<OptionsListInfo> list) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            OptionsListInfo optionsListInfo = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("option_id", optionsListInfo.getOption_id());
            jsonObject.put("option_name", optionsListInfo.getOption_name());
            jsonObject.put("option_num", optionsListInfo.getOption_num());
            jsonObject.put("photo_url", optionsListInfo.getPhoto_url());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }
}
