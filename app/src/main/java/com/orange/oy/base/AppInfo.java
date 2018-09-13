package com.orange.oy.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.orange.oy.activity.mycorps_315.OftenGotoPlaceActivity;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.network.Urls;

public class AppInfo {
    public static final int CAPTCHATIME = 60;//验证码倒计时/秒
    public final static int PaintSize = 20;//水印字体大小
    private static final String APPNAME = "app_name";
    private static final String KEYS = "keys";
    private static final String ISAUTOLOGIN = "isAutoLogin";
    private static final String NAME = "name";
    private static final String USERNAME = "userName";
    private static final String USERDISTRIC = "userDistric";//市
    private static final String USERDISTRIC_PROVINCE = "userDistric_province";//省
    private static final String USERDISTRIC_COUNTY = "userDistric_county";//区
    private static final String USERIMG = "userImg";
    private static final String USERSEX = "userSex";
    private static final String USERPHONE = "userphone";
    private static final String INVITECODE = "inviteCode";
    private static final String ISOPENLOCATION = "isOpenLocation";
    private static final String REDPOINT = "red_point";
    private static final String ISOPEN4GUPDATA = "isOpen4GUpdata";
    private static final String JOINTEAMJPUSH = "jointeamjpush";

    private static final String CACHESIZE = "cach_size";

    public static final String BroadcastReceiverMyteam_Redpoint = "com.orange.oy.myteam.redpoint";
    public static final String BroadcastReceiver_TAKEPHOTO = "com.orange.oy.activity.takephoto";
    public static final String LOCATIONINFO = "com.orange.oy.fragment.locationinfo";
    public static final String BroadcastReceiverMyFragment_Redpoint = "com.orange.oy.fragment.myfragment.redpoint";

    public static final int SelectDistrictResultCode1 = 100;
    public static final int SelectDistrictResultCode2 = 101;
    public static final int LoginCheckResultCode = 103;
    public static final int ShotSuccessResultCode = 104;
    public static final int RevisePasswordResultCode = 105;
    public static final int SelectCityResultCode = 106;
    public static final int SettingLocationResultCode = 107;
    public static final int RESULT_MAIN_SHOWMIDDLE_RIGHT = 108;
    public static final int RESULT_MAIN_SHOWSHAKEPHOTO_AI = 102;
    public static final int RESULT_ACTIVITY_FINISH_FOR_DATA = 109;//此返回值要求activity使用setResult方法带着上个页面的返回值关闭
    public static final int RESULT_EDITPRICE_JUMP_ASSIGN = 110;//跳转分配人员页

    public static final int MyDetailRequestCodeForTake = 150;
    public static final int MyDetailRequestCodeForPick = 151;
    public static final int MyDetailRequestCodeForCut = 158;
    public static final int TaskitemDetailRequestCodeForTake = 152;
    public static final int TaskitemShotRequestCodeForShot = 153;
    public static final int TaskitemPhotogpnCodeForShot = 154;
    public static final int RevisePasswordRequestCode = 155;
    public static final int SelectCityRequestCode = 156;
    public static final int TaskitemListRequestCode = 157;
    public static final int phototaskNYRequestCodeForPick = 159;
    public static final int TeammemberheadremarkRequestCode = 160;

    public static final int REQUEST_CODE_ASK_WINDOW = 201;
    public static final int REQUEST_CODE_ASK_CAMERA = 202;
    public static final int REQUEST_CODE_ASK_READ_PHONE_STATE = 203;
    public static final int REQUEST_CODE_ASK_LOCATION = 204;
    public static final int REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE = 205;
    public static final int REQUEST_CODE_ASK_RECORD_AUDIO = 206;
    public static final int REQUEST_CODE_ASK_CALL_PHONE = 207;
    public static final int REQUEST_CODE_ASK_READ_CALL_LOG = 208;
    public static final int REQUEST_CODE_READ_CONTACTS = 209;
    public static final int REQUEST_CODE_NICKNAME = 210;
    public static final int REQUEST_WRITE_CONTACTS = 211;
    public static final int REQUEST_CODE_NICKTELPHONE = 212;
    public static final int REQUEST_CODE_FILTER = 213;
    public static final int REQUEST_CODE_SETCAPTAIN = 214;
    public static final int REQUEST_CODE_AGE = 215;
    public static final int REQUEST_CODE_OftenGoTOPlace = 216;
    public static final int REQUEST_CODE_FREETIME = 217;
    public static final int REQUEST_CODE_SPCIAL = 218;
    public static final int REQUEST_CODE_AGREE = 219;
    public static final int REQUEST_CODE_THEME = 220;
    public static final int REQUEST_CODE_COLLECT = 221;
    public static final int REQUEST_CODE_ALL = 222;  //对谁可见里面
    public static final int REQUEST_CODE_ISVISIBLE = 223;   // 对谁可见or不可见页面
    public static final int REQUEST_CODE_TAGLIST = 224;   //标签列表页面
    public static final int REQUEST_CODE_ADD = 225;   //添加手机号页面
    public static final int REQUEST_CODE_IDENTITY = 228;   //商户认证页面

    public static final int REQUEST_CODE_UPLOAD_PICTURES = 226;
    public static final int REQUEST_CITYANDPROVINCE = 227;

    public static final String TASKITEMEDIT_TASKTYPE = "34";//记录任务带录音

    private static final String isFirst = "isfirst";

    public static boolean isFirst(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean(isFirst, true);
    }

    public static void setIsFirst(Context context) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putBoolean(isFirst, false);
        e.commit();
    }

    public static int getRedpoint(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(REDPOINT, 0);
    }

    public static void setRedpoint(Context context, int redpoint) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putInt(REDPOINT, redpoint);
        e.commit();
    }

    public static String getKey(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(KEYS, null);
    }

    public static void setKey(Context context, String key) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString(KEYS, key);
        e.commit();
    }

    public static void clearKey(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.remove(sharedPreferences.getString(NAME, "") + "_" + JOINTEAMJPUSH);
        e.remove(KEYS);
        e.remove(USERNAME);
        e.remove(USERDISTRIC);
        e.remove(USERDISTRIC_PROVINCE);
        e.remove(USERDISTRIC_COUNTY);
        e.remove(USERIMG);
        e.remove(USERSEX);
        e.remove(NAME);
        e.remove(USERPHONE);
        e.remove(REDPOINT);
        e.commit();
        Tools.setToken("");
    }

    public static String getUserphone(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USERPHONE, "");
    }

    public static void setUserphone(Context context, String userphone) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString(USERPHONE, userphone);
        e.commit();
    }

    public static boolean isAutoLogin(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean(ISAUTOLOGIN, false);
    }

    public static void setAutoLogin(Context context, boolean isAutoLogin, String key) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putBoolean(ISAUTOLOGIN, isAutoLogin);
        e.putString(KEYS, key);
        e.commit();
    }

    public static void setAutoLogin(Context context, boolean isAutoLogin) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putBoolean(ISAUTOLOGIN, isAutoLogin);
        e.commit();
    }

    public static void setName(Context context, String name) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString(NAME, name);
        e.commit();
    }

    public static void setUserImg(Context context, String url) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString(USERIMG, url);
        e.commit();
    }

    public static String getName(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(NAME, "");
    }

    public static String getUserName(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USERNAME, "");
    }

    public static String getUserDistric(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USERDISTRIC, "");
    }

    public static String getUserdistricProvince(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USERDISTRIC_PROVINCE, "");
    }

    public static String getUserdistricCounty(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USERDISTRIC_COUNTY, "");
    }

    public static String[] getUserdistrics(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        String[] userdistrics = new String[3];
        userdistrics[0] = sharedPreferences.getString(USERDISTRIC_PROVINCE, "");
        userdistrics[1] = sharedPreferences.getString(USERDISTRIC, "");
        userdistrics[2] = sharedPreferences.getString(USERDISTRIC_COUNTY, "");
        return userdistrics;
    }

    public static String getUserImagurl(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USERIMG, "");
    }

    public static String getUserSex(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USERSEX, "");
    }

    public static String getInviteCode(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(INVITECODE, "");
    }

    public static void setUserinfo(Context context, String username, String userSex, String userDistric, String province, String county,
                                   String img, String inviteCode) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        if (username != null && !username.equals("null"))
            e.putString(USERNAME, username);
        if (userDistric != null && !userDistric.equals("null"))
            e.putString(USERDISTRIC, userDistric);
        if (province != null && !province.equals("null"))
            e.putString(USERDISTRIC_PROVINCE, province);
        if (county != null && !county.equals("null"))
            e.putString(USERDISTRIC_COUNTY, county);
        if (img != null && !img.equals("null"))
            e.putString(USERIMG, Urls.ImgIp + img);
        if (userSex != null && !userSex.equals("null"))
            e.putString(USERSEX, userSex);
        if (inviteCode != null && !inviteCode.equals("null"))
            e.putString(INVITECODE, inviteCode);
        e.commit();
    }

    //依照setUserinfo删除性别
    public static void setUserinfo2(Context context, String username, String userDistric, String province, String county,
                                    String img, String inviteCode) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        if (username != null && !username.equals("null"))
            e.putString(USERNAME, username);
        if (userDistric != null && !userDistric.equals("null"))
            e.putString(USERDISTRIC, userDistric);
        if (province != null && !province.equals("null"))
            e.putString(USERDISTRIC_PROVINCE, province);
        if (county != null && !county.equals("null"))
            e.putString(USERDISTRIC_COUNTY, county);
        if (img != null && !img.equals("null"))
            e.putString(USERIMG, Urls.ImgIp + img);
        if (inviteCode != null && !inviteCode.equals("null"))
            e.putString(INVITECODE, inviteCode);
        e.commit();
    }

    public static final int netSetting_1 = 1;
    public static final int netSetting_2 = 2;
    public static final int netSetting_3 = 3;

    public static void setOpen4GUpdata(Context context, int netSetting) {
//        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
//        e.putString(ISOPEN4GUPDATA, netSetting + "");
//        return e.commit();
        SystemDBHelper systemDBHelper = new SystemDBHelper(context);
        systemDBHelper.insertSetting(netSetting);
    }

    public static int getOpen4GUpdata(Context context) {//默认开启
        SystemDBHelper systemDBHelper = new SystemDBHelper(context);
        return systemDBHelper.getNetworkSetting();
    }

    public static void setOpenLocation(Context context, Boolean isOpenLocation) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putBoolean(ISOPENLOCATION, isOpenLocation);
        e.commit();
    }

    public static boolean isOpenLocation(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean(ISOPENLOCATION, false);
    }

    public static void addCachesize(Context context, long size) {
        SharedPreferences sp = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putLong(CACHESIZE, sp.getLong(CACHESIZE, 0) + size);
        e.commit();
    }

    public static long getCachesize(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getLong(CACHESIZE, 0);
    }

    public static void clearCachesize(Context context) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.remove(CACHESIZE);
        e.commit();
    }

    public static void isbindaccount(Context context, String bindaccount) {//支付宝(设置)
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString("bindaccount", bindaccount);
        e.commit();
    }

    public static void isbindidcard(Context context, String bindidcard) {//身份证(设置)
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString("bindidcard", bindidcard);
        e.commit();
    }

    public static boolean isBindAccount(Context context) {//支付宝
        String bindaccount = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("bindaccount", null);
        return "0".equals(bindaccount);
    }

    public static boolean isBindidCard(Context context) {//身份证
        String bindidcard = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("bindidcard", null);
        return "0".equals(bindidcard);//true 为未绑定
    }

    public static void setNewTask(Context context, int size, int shade) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putInt("size", size);
        e.putInt("shade", shade);
        e.commit();
    }

    public static int getSize(Context context) {//size为视频录制窗口大小（1为大，0为小，2为关闭）
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt("size", 0);
    }

    public static int getShade(Context context) {//shade为是否可开启遮罩（1为可以开启，0为不可以开启）
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt("shade", 0);
    }

    public static void setIsShow(Context context, boolean isShow) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putBoolean("isShow", isShow);
        e.commit();
    }

    public static boolean getIsShow(Context context) {//是否需要显示新手页面
        boolean isShow = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean("isShow", true);
        return isShow;
    }

    public static void setCityName(Context context, String province, String city, String county) {//广场页的城市需要传到指派的任务页面（暂时这么做比较简单==）
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString("city", city);
        e.putString("province", province);
        e.putString("county", county);
        e.commit();
    }

    public static String getCityName(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("city", "");
    }

    public static String getProvince(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("province", "");
    }

    public static String getCounty(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("county", "");
    }

    public static String[] getAddress(Context context) {
        String[] address = new String[3];
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        address[0] = sharedPreferences.getString("province", "");
        address[1] = sharedPreferences.getString("city", "");
        address[2] = sharedPreferences.getString("county", "");
        return address;
    }

    public static void saveStartTime(Context context, String startTime) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString("startTime", startTime);
        e.commit();
    }

    public static String getStartTime(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("startTime", "");
    }

    public static void setH5Data(Context context, String h5projectid, String h5usermobile) {
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putString("h5usermobile", h5usermobile);
        e.putString("h5projectid", h5projectid);
        e.commit();
    }

    public static String getH5projectid(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("h5projectid", null);
    }

    public static String getH5usermobile(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("h5usermobile", null);
    }

    public static void setIsNeedUpdata(Context context, int need_update) {//是否需要更新
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putInt("need_update", need_update);
        e.commit();
    }

    public static int getNeedUpdata(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt("need_update", 0);
    }

    public static void setJPush(Context context, boolean isJPush) {//推送消息
        SharedPreferences.Editor e = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit();
        e.putBoolean("isJPush", isJPush);
        e.commit();
    }

    public static boolean getIsJPush(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean("isJPush", false);
    }

    /**
     * 请求加入战队
     *
     * @param context
     * @param isJPush
     */
    public static void setJointeamjpush(Context context, boolean isJPush) {//推送消息
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(sharedPreferences.getString(NAME, "") + "_" + JOINTEAMJPUSH, isJPush);
        e.commit();
    }

    public static boolean getJointeamjpush(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(sharedPreferences.getString(NAME, "") + "_" + JOINTEAMJPUSH, false);
    }

    /**
     * 甩图页闪光灯
     *
     * @param isON
     */
    public static void setFLASH_MODLE_SHAKE(Context context, boolean isON) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(NAME, "");
        if (!TextUtils.isEmpty(name)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(name + "_shake_flashmodle", isON);
            editor.commit();
        }
    }

    public static boolean getFLASH_MODLE_SHAKE(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(sharedPreferences.getString(NAME, "") + "_shake_flashmodle", false);
    }

    /**
     * 用于甩图自由拍页面左下角预览图片存储（选择最后一张图片）
     */
    public static void setShakePhotoUrl(Context context, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("shakePhotoUrl", path);
        editor.commit();
    }

    public static String getShakePhotoUrl(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString("shakePhotoUrl", "");
    }

    /**
     * 是否显示甩图滚动条
     *
     * @param context
     * @param isShow
     */
    public static void setShakePhotoShowAutoTextview(Context context, boolean isShow) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("shakePhotoisShowAutoTextView", isShow);
        editor.commit();
    }

    public static boolean getShakePhotoShowAutoTextview(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean("shakePhotoisShowAutoTextView", true);
    }
}