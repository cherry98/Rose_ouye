package com.orange.oy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.OfflinePackageActivity;
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.bigchange.NewMessageActivity;
import com.orange.oy.activity.bright.BrightPersonInfoActivity;
import com.orange.oy.allinterface.OnCheckVersionResult;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.MyApplication;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.SuspendDialog;
import com.orange.oy.info.JpushBaseModel;
import com.orange.oy.info.JpushInfo;
import com.orange.oy.info.SystemMessageInfo;
import com.orange.oy.info.TransferInfo;
import com.orange.oy.network.CheckVersion;
import com.orange.oy.util.ListDataSave;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class JpushReceiver extends BroadcastReceiver {
    private ArrayList<TransferInfo> list;
    private String projectid;

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Tools.d("[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            Tools.d(bundle.getString(JPushInterface.EXTRA_MESSAGE));
            //所有自定义的消息才会进入到这个方法里
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            if (!TextUtils.isEmpty(message) && !"null".equals(message)) {
                try {
                    Tools.d(message);
                    JSONObject jsonObject = new JSONObject(message);
                    String code = jsonObject.optString("code");
                    String name = AppInfo.getName(context);
                    projectid = jsonObject.optString("projectid");
                    // Tools.d("tag","projectid======ih======>>>"+projectid);
                    //  Tools.d("code","code============>>>"+code);
                    if (code != null) {
                        if ("1".equals(code)) {//添加好友
                            AppDBHelper appDBHelper = new AppDBHelper(context);
                            if (1 == appDBHelper.addNewfriendsData(AppInfo.getName(context), jsonObject.getString
                                            ("username"), jsonObject.getString("usermobile"), jsonObject.getString
                                            ("userid"),
                                    jsonObject.getString("imgurl"))) {
                                AppInfo.setRedpoint(context, AppInfo.getRedpoint(context) + 1);
                            }
                            Intent send = new Intent();
                            send.setAction(AppInfo.BroadcastReceiverMyteam_Redpoint);
                            context.sendBroadcast(send);
                        } else if ("2".equals(code) && !TextUtils.isEmpty(name)) {//分配任务
                            SystemDBHelper systemDBHelper = new SystemDBHelper(context);
                            JSONArray jsonArray = jsonObject.getJSONArray("msg");
                            int length = jsonArray.length();
                            JSONObject temp;
                            String string = null;
                            SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                            systemMessageInfo.setUsermobile(name);
                            systemMessageInfo.setCode("2");
                            systemMessageInfo.setTitle("收到负责任务消息");
                            for (int i = 0; i < length; i++) {
                                temp = jsonArray.getJSONObject(i);
                                if (string == null) {
                                    string = temp.getString("outletNum") + " " + temp.getString("outletName") + " " + temp
                                            .getString("province") + " " + temp.getString("city") + " " + temp.getString
                                            ("outletAddress");
                                } else {
                                    string = string + "\n" + temp.getString("outletNum") + " " + temp.getString
                                            ("outletName") + " " + temp.getString("province") + " " + temp.getString
                                            ("city") + " " + temp.getString("outletAddress");
                                }
                            }
                            systemMessageInfo.setMessage("负责店铺数量：" + length);
                            systemMessageInfo.setMessage2(string);
                            systemMessageInfo.setTime(Tools.getYear() + "-" + Tools.getMonth() + "-" + Tools
                                    .getCurrentMonthDay());
                            systemDBHelper.addSystemMessage(systemMessageInfo);
                        } else if ("3".equals(code) && !TextUtils.isEmpty(name)) {//执行完成/资料已回收
                            SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                            systemMessageInfo.setUsermobile(name);
                            jsonObject = jsonObject.getJSONObject("msg");
                            if ("1".equals(jsonObject.getString("state"))) {
                                systemMessageInfo.setTitle("执行完成");
                            } else {
                                systemMessageInfo.setTitle("已完成资料回收上传任务");
                            }
                            SystemDBHelper systemDBHelper = new SystemDBHelper(context);
                            systemMessageInfo.setCode("3");
                            systemMessageInfo.setMessage("");
                            systemMessageInfo.setTime(Tools.getYear() + "-" + Tools.getMonth() + "-" + Tools
                                    .getCurrentMonthDay());
                            systemDBHelper.addSystemMessage(systemMessageInfo);
                        } else if ("4".equals(code)) {//异地登录
                            Tools.d("异地登录");
                            String kick_token = jsonObject.getString("kick_token");
                            if (kick_token.equals(AppInfo.getKey(context))) {
                                AppInfo.clearKey(context);
                                JPushInterface.clearAllNotifications(context);
                                JPushInterface.stopPush(context);
                                Intent intent1 = new Intent("com.orange.oy.VRService");
                                intent1.setPackage("com.orange.oy");
                                context.stopService(intent1);
                                new SuspendDialog().createFloatView(MyApplication.getInstance());
                                ScreenManager.getScreenManager().AppExit(null);
                            }
                        } else if ("5".equals(code)) {//明访任务抽签完成
                            Tools.d("抽签已经完成");
                            ListDataSave listDataSave = new ListDataSave(context, "xie");
                            ArrayList<TransferInfo> list = (ArrayList<TransferInfo>) listDataSave.getDataList("transferInfo");
                            TransferInfo transferInfo = list.get(0);
                            //判断进入考试任务还是普通任务
                            String mytype = transferInfo.getMytype();
                            if ("1".equals(mytype)) {
                                Intent it = new Intent(context, BrightPersonInfoActivity.class);
                                it.putExtra("store_num", transferInfo.getStore_num());
                                it.putExtra("city", transferInfo.getCity3());
                                it.putExtra("outletid", transferInfo.getOutletid());
                                it.putExtra("project_id", transferInfo.getProject_id());
                                it.putExtra("projectname", transferInfo.getProjectname());
                                it.putExtra("code", transferInfo.getCode());
                                it.putExtra("brand", transferInfo.getBrand());
                                it.putExtra("store_name", transferInfo.getStore_name());
                                it.putExtra("photo_compression", transferInfo.getPhoto_compression());
                                it.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(it);
                            } else if ("0".equals(mytype)) {
                                String fynum = transferInfo.getNumber();
                                if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName(context)
                                )) {
                                    if (transferInfo.getIs_exe().equals("1")) {
                                        if (transferInfo.getIsOffline() == 1) {//离线任务
                                            Intent intent1 = new Intent(context, OfflinePackageActivity.class);
                                            intent1.putExtra("id", transferInfo.getId());
                                            intent1.putExtra("projectname", transferInfo.getProjectname());
                                            intent1.putExtra("store_name", transferInfo.getStore_name());
                                            intent1.putExtra("store_num", transferInfo.getStore_num());
                                            intent1.putExtra("province", transferInfo.getProvince());
                                            intent1.putExtra("city", transferInfo.getCity());
                                            intent1.putExtra("longtitude", transferInfo.getLongtitude());
                                            intent1.putExtra("latitude", transferInfo.getLatitude());
                                            intent1.putExtra("project_id", transferInfo.getProject_id());
                                            intent1.putExtra("photo_compression", transferInfo.getPhoto_compression());
                                            intent1.putExtra("is_record", transferInfo.getIs_record());
                                            intent1.putExtra("is_watermark", transferInfo.getIs_watermark());
                                            intent1.putExtra("code", transferInfo.getCode());
                                            intent1.putExtra("brand", transferInfo.getBrand());
                                            intent1.putExtra("is_takephoto", transferInfo.getIs_takephoto());
                                            context.startActivity(intent1);
                                        } else {
                                            if (transferInfo.getIs_desc().equals("1")) {//有网点说明
                                                Intent intent2 = new Intent(context, StoreDescActivity.class);
                                                intent2.putExtra("id", transferInfo.getId());
                                                intent2.putExtra("projectname", transferInfo.getProjectname());
                                                intent2.putExtra("store_name", transferInfo.getStore_name());
                                                intent2.putExtra("store_num", transferInfo.getStore_num());
                                                intent2.putExtra("province", transferInfo.getProvince());
                                                intent2.putExtra("city", transferInfo.getCity());
                                                intent2.putExtra("longtitude", transferInfo.getLongtitude());
                                                intent2.putExtra("latitude", transferInfo.getLatitude());
                                                intent2.putExtra("project_id", transferInfo.getProject_id());
                                                intent2.putExtra("photo_compression", transferInfo.getPhoto_compression());
                                                intent2.putExtra("is_desc", "1");
                                                intent2.putExtra("is_watermark", transferInfo.getIs_watermark());
                                                intent2.putExtra("code", transferInfo.getCode());
                                                intent2.putExtra("brand", transferInfo.getBrand());
                                                intent2.putExtra("is_takephoto", transferInfo.getIs_takephoto());
                                                intent2.putExtra("type", transferInfo.getType());
                                                context.startActivity(intent2);
                                            } else {
                                                Intent intent3 = new Intent(context, TaskitemDetailActivity_12
                                                        .class);
                                                intent3.putExtra("id", transferInfo.getId());
                                                intent3.putExtra("projectname", transferInfo.getProjectname());
                                                intent3.putExtra("store_name", transferInfo.getStore_name());
                                                intent3.putExtra("store_num", transferInfo.getStore_num());
                                                intent3.putExtra("province", transferInfo.getProvince());
                                                intent3.putExtra("city", transferInfo.getCity());
                                                intent3.putExtra("longtitude", transferInfo.getLongtitude());
                                                intent3.putExtra("latitude", transferInfo.getLatitude());
                                                intent3.putExtra("project_id", transferInfo.getProject_id());
                                                intent3.putExtra("photo_compression", transferInfo.getPhoto_compression());
                                                intent3.putExtra("is_desc", "0");
                                                intent3.putExtra("is_watermark", transferInfo.getIs_watermark());
                                                intent3.putExtra("code", transferInfo.getCode());
                                                intent3.putExtra("brand", transferInfo.getBrand());
                                                intent3.putExtra("is_takephoto", transferInfo.getIs_takephoto());
                                                intent3.putExtra("project_type", transferInfo.getType());
                                                context.startActivity(intent3);
                                            }
                                        }
                                    } else {
                                        Tools.showToast(context, "未到执行时间");
                                    }
                                } else {
                                    Tools.showToast(context, "您不是访员！");
                                }
                            }
                        } else if (code.equals("6")) {//支付成功
                            SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                            systemMessageInfo.setUsermobile(name);
                            jsonObject = jsonObject.getJSONObject("msg");
                            String projectName = jsonObject.getString("projectName");
                            String payAccount = jsonObject.getString("payAccount");
                            String money = jsonObject.getString("money");
                            systemMessageInfo.setTitle("支付成功");
                            SystemDBHelper systemDBHelper = new SystemDBHelper(context);
                            systemMessageInfo.setCode("6");
                            systemMessageInfo.setMessage("尊敬的偶业用户，您好。恭喜您" + projectName + "获得一笔" + money +
                                    "元的奖励金,请至您的支付宝账户" + payAccount + "查看。感谢您的使用，偶业会频繁更新项目，大量任务不停歇~~");
                            systemMessageInfo.setTime(Tools.getYear() + "-" + Tools.getMonth() + "-" + Tools
                                    .getCurrentMonthDay());
                            systemDBHelper.addSystemMessage(systemMessageInfo);
                        } else if (code.equals("7")) {//支付失败
                            SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                            systemMessageInfo.setUsermobile(name);
                            jsonObject = jsonObject.getJSONObject("msg");
                            String projectName = jsonObject.getString("projectName");
                            String payAccount = jsonObject.getString("payAccount");
                            String money = jsonObject.getString("money");
                            systemMessageInfo.setTitle("支付失败");
                            SystemDBHelper systemDBHelper = new SystemDBHelper(context);
                            systemMessageInfo.setCode("7");
                            systemMessageInfo.setMessage("尊敬的偶业用户，您好。非常抱歉地通知您，您提取的" + projectName + "项目" + money +
                                    "元的奖励金转账失败，请校验您填写的姓名与支付宝账号是否对应，在“我的”-->“我的支付宝”页面可修改姓名，若需修改支付宝账号，请联系偶业客服（微信号：17072293227）；若姓名与支付宝账号对应，请于三个工作日后再次进行提现操作。");
                            systemMessageInfo.setTime(Tools.getYear() + "-" + Tools.getMonth() + "-" + Tools
                                    .getCurrentMonthDay());
                            systemDBHelper.addSystemMessage(systemMessageInfo);
                        } else if (code.equals("8")) {
                            SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                            systemMessageInfo.setUsermobile(name);
                            jsonObject = jsonObject.getJSONObject("msg");
                            String num = jsonObject.getString("num");
                            String usermobile = jsonObject.getString("usermobile");

                            systemMessageInfo.setTitle("偶米奖励");
                            SystemDBHelper systemDBHelper = new SystemDBHelper(context);
                            systemMessageInfo.setCode("8");
                            systemMessageInfo.setMessage("您推荐的" + usermobile + "用户完成一家网点，奖励您" + num + "偶米，已入账，请查收。");
                            systemMessageInfo.setTime(Tools.getYear() + "-" + Tools.getMonth() + "-" + Tools
                                    .getCurrentMonthDay());
                            systemDBHelper.addSystemMessage(systemMessageInfo);
                        } else if (code.equals("9")) {
                            /*SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                            systemMessageInfo.setUsermobile(name);
                            String msg = jsonObject.getString("msg");
                            systemMessageInfo.setTitle("系统消息");
                            SystemDBHelper systemDBHelper = new SystemDBHelper(context);
                            systemMessageInfo.setCode("9");
                            systemMessageInfo.setMessage(msg);
                            systemMessageInfo.setTime(Tools.getYear() + "-" + Tools.getMonth() + "-" + Tools
                                    .getCurrentMonthDay());
                            systemDBHelper.addSystemMessage(systemMessageInfo);*/

                            AppInfo.setJPush(context, true);
                            Intent send = new Intent();
                            send.setAction(AppInfo.LOCATIONINFO);
                            send.putExtra("type", "0");//Jpush发送的广播

                            context.sendBroadcast(send);
                        } else if (code.equals("12")) {//请求加入战队
                            AppInfo.setJointeamjpush(context, true);
                            Intent send = new Intent();
                            send.setAction(AppInfo.BroadcastReceiverMyFragment_Redpoint);
                            context.sendBroadcast(send);
                        } else if (code.equals("checkupdata")) {
                            CheckVersion.check(context, onCheckVersionResult);//检查更新
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.showToast(context, "数据异常");
                }
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            //所有普通推送都会进入到这个部分，而且JPush会自己进行Notification的显示
            Tools.d("[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Tools.d("[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Tools.d("[MyReceiver] 用户点击打开了通知" + bundle.getString(JPushInterface.EXTRA_EXTRA));
            String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
            if (!TextUtils.isEmpty(message) && !"null".equals(message)) {
                Gson gson = new Gson();
                JpushBaseModel mode = gson.fromJson(message.trim(), JpushBaseModel.class);
                try {
                    JpushInfo mode2 = gson.fromJson(mode.getMsg().trim(), JpushInfo.class);
                    if (mode2.code.equals("9")) {
                        Intent intent9 = new Intent(context, NewMessageActivity.class);
                        intent9.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent9.putExtra("projectId", projectid);
                        context.startActivity(intent9);
                    }
                } catch (Exception e) {
                    Intent intent1 = new Intent(context, MainActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(intent1);
                }
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Tools.d("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Tools.d("[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        }
    }

    private OnCheckVersionResult onCheckVersionResult = new OnCheckVersionResult() {
        public void checkversion(String versionName) {
//            if (versionName != null) {
//                ConfirmDialog.showDialogForHint(MainActivity.this, "发现新版！正在更新...");
//            }
        }
    };
}