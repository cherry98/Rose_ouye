package com.orange.oy.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.karics.library.zxing.android.CaptureActivity;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.activity.FeedbackActivity;
import com.orange.oy.activity.InvitefriendActivity;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.TaskitemEditActivity;
import com.orange.oy.activity.TaskitemMapActivity;
import com.orange.oy.activity.TaskitemPhotographyNextYActivity;
import com.orange.oy.activity.TaskitemRecodillustrateActivity;
import com.orange.oy.activity.TaskitemShotActivity;
import com.orange.oy.activity.bigchange.MyDetailInfoActivity;
import com.orange.oy.activity.createtask_321.TaskRecordActivity;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.mycorps_314.MyCorpsActivity;
import com.orange.oy.activity.mycorps_315.AssignActivity;
import com.orange.oy.activity.mycorps_315.MyoumiActivity;
import com.orange.oy.activity.newtask.MyaccountActivity;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.shakephoto_320.MyAddressActivity;
import com.orange.oy.activity.shakephoto_320.MyCommercialActivity;
import com.orange.oy.activity.shakephoto_320.MyMessageActivity;
import com.orange.oy.activity.shakephoto_320.PrizeCardActivity;
import com.orange.oy.allinterface.BroadcastReceiverBackListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.MenuItem;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.TopRightMenu;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class MyFragment extends BaseFragment implements View.OnClickListener, BroadcastReceiverBackListener {
    public MyFragment() {
    }

    @Override
    public void listener(Context context, Intent intent) {
        if (wdzd_View != null) {
            wdzd_View.findViewById(R.id.itemmf_redpoint).setVisibility(View.VISIBLE);
        }
    }

    public interface OnLocationOpenChangeListener {
        void locationChange(boolean open);
    }

    public interface OnMydetailClickListener {
        void myDetailClick();
    }

    private void initNetworkConnection() {
        Userinfo = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                return params;
            }
        };
        Dataconnection = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> map = new HashMap<>();
                map.put("usermobile", AppInfo.getName(getContext()));
                map.put("flag", flag + "");
                return map;
            }
        };

        Addstatistout = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_mobile", mobile);
                params.put("token", Tools.getToken());
                params.put("name", getResources().getString(R.string.app_name));
                try {
                    params.put("versionnum", Tools.getVersionName(getContext()));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(getContext()));
                params.put("resolution", Tools.getScreeInfoWidth(getContext()) + "*" + Tools.getScreeInfoHeight
                        (getContext()));
                params.put("outtime", Tools.getTimeByPattern("yyyy-MM-dd HH:mm:ss"));
                params.put("mac", Tools.getLocalMacAddress(getContext()));
                params.put("imei", Tools.getDeviceId(getContext()));
                return params;
            }
        };
    }

    private NetworkConnection Userinfo, Dataconnection, Addstatistout;
    private View mView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_new, container, false);
        return mView;
    }

    public void setOnMydetailClickListener(OnMydetailClickListener listener) {
        onMydetailClickListener = listener;
    }

    public void setOnLocationOpenChangeListener(OnLocationOpenChangeListener listener) {
        onLocationOpenChangeListener = listener;
    }

    private TextView my_name, my_logined;
    private ImageView Ivscan; //扫码
    private CircularImageView my_img;
    private ImageLoader imageLoader;
    private OnLocationOpenChangeListener onLocationOpenChangeListener;
    private OnMydetailClickListener onMydetailClickListener;
    private AppDBHelper appDBHelper;
    private int flag;//标记网络状态
    private UpdataDBHelper updataDBHelper;
    private TextView setting_loginout;  //退出登录
    private String mobile;
    private TextView my_account, my_oumi;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(getContext());
        appDBHelper = new AppDBHelper(getContext());
        imageLoader = new ImageLoader(getContext());
//        mView.findViewById(R.id.my_feedback).setOnClickListener(this);
//        mView.findViewById(R.id.mytask_publish).setOnClickListener(this);
//        mView.findViewById(R.id.my_redPackage).setOnClickListener(this);
        my_account = (TextView) mView.findViewById(R.id.my_account);
        my_oumi = (TextView) mView.findViewById(R.id.my_oumi);
        setting_loginout = (TextView) mView.findViewById(R.id.setting_loginout);
        Ivscan = (ImageView) mView.findViewById(R.id.iv_scan);
        Ivscan.setOnClickListener(this);
        my_name = (TextView) mView.findViewById(R.id.my_name);
        my_logined = (TextView) mView.findViewById(R.id.my_logined);
        my_img = (CircularImageView) mView.findViewById(R.id.my_img);
        my_img.setImageResource(R.mipmap.grxx_icon_mrtx);
        if (!TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            my_name.setOnClickListener(null);
            String name = AppInfo.getUserName(getContext());
            String imgUrl = AppInfo.getUserImagurl(getContext());
            if (!TextUtils.isEmpty(name)) {
                my_name.setText(name);
            }
            if (!TextUtils.isEmpty(imgUrl)) {
                imageLoader.DisplayImage(imgUrl, my_img, R.mipmap.grxx_icon_mrtx);
            }
        }
        my_account.setOnClickListener(this);
        my_oumi.setOnClickListener(this);
        my_img.setOnClickListener(this);
//        mView.findViewById(R.id.my_contactcustomer).setOnClickListener(this);
        flag = AppInfo.getOpen4GUpdata(getContext());//1是WiFi、4G都可以 2仅WiFi
        isRefresh = true;
    }

    public static boolean isRefresh = false;

    public void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            getUserInfo();
        } else {
            upLoginico();
        }
    }

    public void upLoginico() {
        try {
            initTabView();
            if (!TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                if (AppInfo.getJointeamjpush(getContext())) {
                    if (wdzd_View != null) {
                        wdzd_View.findViewById(R.id.itemmf_redpoint).setVisibility(View.VISIBLE);
                    }
                } else {
                    if (wdzd_View != null) {
                        wdzd_View.findViewById(R.id.itemmf_redpoint).setVisibility(View.GONE);
                    }
                }
                my_account.setOnClickListener(this);
                my_oumi.setOnClickListener(this);
//                mView.findViewById(R.id.my_contactcustomer).setOnClickListener(this);
                setting_loginout.setOnClickListener(this);
                my_img.setOnClickListener(this);
                my_name.setOnClickListener(null);

                String name = AppInfo.getUserName(getContext());
                String imgUrl = AppInfo.getUserImagurl(getContext());
                if (!TextUtils.isEmpty(name)) {
                    my_name.setText(name);
                }
                if (!TextUtils.isEmpty(imgUrl)) {
                    imageLoader.DisplayImage(imgUrl, my_img, R.mipmap.grxx_icon_mrtx);
                }
                if ("1".equals(merchant)) {
                    views.add(0, getTabView(R.mipmap.wd_button_wdsh, "我的商户", wdsh));
                }
                if ("1".equals(assignedproject)) {
                    views.add(getTabView(R.mipmap.wode_button_zprw, "指派任务", fbrw));
                }
                my_name.setVisibility(View.VISIBLE);
                my_logined.setVisibility(View.GONE);
                Ivscan.setVisibility(View.VISIBLE);
//                setting_loginout.setVisibility(View.VISIBLE);
                my_account.setVisibility(View.VISIBLE);
                my_oumi.setVisibility(View.VISIBLE);
            } else {
                my_name.setVisibility(View.GONE);
                my_logined.setVisibility(View.VISIBLE);
                my_logined.setText("立即登录");
                // .....
                Ivscan.setVisibility(View.INVISIBLE);
                setting_loginout.setVisibility(View.INVISIBLE);
                my_account.setVisibility(View.GONE);
                my_oumi.setVisibility(View.GONE);
                my_logined.setOnClickListener(this);
                my_img.setImageResource(R.mipmap.grxx_icon_mrtx);
            }
            settingTab();
        } catch (Exception e) {
            Tools.showToast(getContext(), getResources().getString(R.string.network_error));
        }
    }

    private ArrayList<View> views = new ArrayList<>();
    private LinearLayout myfragment_content;
    private final int wdsh = 0x10;
    private final int yqzq = 0x11;
    private final int wdzd = 0x12;
    private final int sjcz = 0x13;
    private final int xx = 0x14;
    private final int lpkq = 0x15;
    private final int lxwm = 0x16;
    private final int fbrw = 0x17;
    private View wdzd_View;

    private void initTabView() {
        views.clear();
        myfragment_content = (LinearLayout) mView.findViewById(R.id.myfragment_content);
//        views.add(getTabView(R.mipmap.wode_button_wdtd, "我的商户",wdsh));
        views.add(getTabView(R.mipmap.wode_button_yqzq, "邀请赚钱", yqzq));
        views.add(wdzd_View = getTabView(R.mipmap.wode_button_wdtd, "我的战队", wdzd));
        views.add(getTabView(R.mipmap.wd_button_sjcz, "手机充值", sjcz));
        views.add(getTabView(R.mipmap.wd_button_sdqb, "消息", xx));
        views.add(getTabView(R.mipmap.wd_button_lpkq, "礼品卡券", lpkq));
        views.add(getTabView(R.mipmap.wode_button_gywm, "联系我们", lxwm));
//        views.add(getTabView(R.mipmap.wode_button_zprw, "指派任务", fbrw));
    }

    public View getTabView(int rid, String name, int id) {
        View view = Tools.loadLayout(getContext(), R.layout.item_myfragment);
        ((ImageView) view.findViewById(R.id.itemmf_img)).setImageResource(rid);
        ((TextView) view.findViewById(R.id.itemmf_txt)).setText(name);
        view.setId(id);
        view.setOnClickListener(this);
        return view;
    }

    public void settingTab() {
        if (views == null) {
            return;
        }
        myfragment_content.removeAllViews();
        int size = views.size();
        int dp100 = Tools.dipToPx(getActivity(), 100);
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp100);
        myfragment_content.addView(linearLayout, lp);
        int index = 1;
        for (int i = 0; i < size; i++) {
            View view = views.get(i);
            switch (index) {
                case 1: {
                    view.findViewById(R.id.itemmf_left).setVisibility(View.GONE);
                    view.findViewById(R.id.itemmf_right).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.itemmf_bottom).setVisibility(View.VISIBLE);
                }
                break;
                case 2: {
                    view.findViewById(R.id.itemmf_left).setVisibility(View.GONE);
                    view.findViewById(R.id.itemmf_right).setVisibility(View.GONE);
                    view.findViewById(R.id.itemmf_bottom).setVisibility(View.VISIBLE);
                }
                break;
                case 3: {
                    view.findViewById(R.id.itemmf_left).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.itemmf_right).setVisibility(View.GONE);
                    view.findViewById(R.id.itemmf_bottom).setVisibility(View.VISIBLE);
                }
                break;
            }
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0, dp100, 1);
            linearLayout.addView(view, llp);
            if (i + 1 == size) {
                for (int j = 0; j < 3 - index; j++) {
                    View view1 = Tools.loadLayout(getContext(), R.layout.item_myfragment);
                    ((ImageView) view1.findViewById(R.id.itemmf_img)).setVisibility(View.INVISIBLE);
                    ((TextView) view1.findViewById(R.id.itemmf_txt)).setVisibility(View.INVISIBLE);
                    if (index == 2 && j == 0) {
                        view1.findViewById(R.id.itemmf_left).setVisibility(View.VISIBLE);
                    }
                    LinearLayout.LayoutParams llp1 = new LinearLayout.LayoutParams(0, dp100, 1);
                    linearLayout.addView(view1, llp1);
                }
                break;
            }
            if (i > 1 && (i + 1) % 3 == 0) {
                linearLayout = new LinearLayout(getContext());
                LinearLayout.LayoutParams llp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp100);
                myfragment_content.addView(linearLayout, llp1);
                index = 1;
            } else {
                index++;
            }
        }
//        int num = 3 - ((size > 3) ? size % 3 : size);
//        if (num != 3) {
//            for (int i = 0; i < num; i++) {
//                View view = Tools.loadLayout(getContext(), R.layout.item_myfragment);
//                ((ImageView) view.findViewById(R.id.itemmf_img)).setVisibility(View.INVISIBLE);
//                ((TextView) view.findViewById(R.id.itemmf_txt)).setVisibility(View.INVISIBLE);
//                if (i == 0) {
//                    view.findViewById(R.id.itemmf_left).setVisibility(View.VISIBLE);
//                }
//                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0, dp100, 1);
//                linearLayout.addView(view, llp);
//            }
//        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case lpkq: {//礼品卡券
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                startActivity(new Intent(getContext(), PrizeCardActivity.class));
            }
            break;
            case wdsh: {//我的商户
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                startActivity(new Intent(getContext(), MyCommercialActivity.class));
            }
            break;
            case fbrw: {//进入老分包列表
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                startActivity(new Intent(getContext(), AssignActivity.class));
            }
            break;
            case wdzd: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                AppInfo.setJointeamjpush(getContext(), false);
                startActivity(new Intent(getContext(), MyCorpsActivity.class));
            }
            break;
//            //退出登录
//            case R.id.setting_loginout: {
//                SobotApi.exitSobotChat(getContext());
//                MyFragment.isRefresh = true;
//                mobile = AppInfo.getName(getContext());
//                Addstatistout.sendPostRequest(Urls.Addstatistout, new Response.Listener<String>() {
//                    public void onResponse(String s) {
//                    }
//                }, new Response.ErrorListener() {
//                    public void onErrorResponse(VolleyError volleyError) {
//                    }
//                });
//                AppInfo.clearKey(getContext());
//                JPushInterface.clearAllNotifications(getContext());
//                JPushInterface.setAlias(getContext(), "", null);
//                JPushInterface.stopPush(getContext());
//                upLoginico();
//                Intent intent11 = new Intent("com.orange.oy.VRService");
//                intent11.setPackage("com.orange.oy");
//                getContext().stopService(intent11);
//            }
//
//            break;
            case R.id.iv_scan:
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                        ConfirmDialog.showDialog(getContext(), null, 2,
                                getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {
                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                        Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        return;
                    }
                }
                TopRightMenu mTopRightMenu = new TopRightMenu(getActivity());
                ArrayList<MenuItem> menuItems = new ArrayList<>();
                menuItems.add(new MenuItem(-1, "扫一扫"));
                menuItems.add(new MenuItem(-1, "收货地址"));
                menuItems.add(new MenuItem(-1, "退出登录"));
                mTopRightMenu
                        .setHeight(Tools.dipToPx(getActivity(), 149))     //默认高度
                        .setWidth(Tools.dipToPx(getActivity(), 133))      //默认宽度wrap_content
                        .showIcon(false)     //显示菜单图标，默认为true
                        .dimBackground(true)           //背景变暗，默认为true
                        .addMenuList(menuItems)
                        .showAsDropDown(Ivscan, -Tools.dipToPx(getActivity(), 110), Tools.dipToPx(getActivity(), 5));
                mTopRightMenu.setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int position) {
                        if (position == 0) {//扫一扫
                            Intent intent5 = new Intent(getContext(), CaptureActivity.class);
                            intent5.putExtra("flag", "2");//我的页面扫一扫
                            startActivity(intent5);
                        } else if (position == 1) {//收货地址
                            startActivity(new Intent(getContext(), MyAddressActivity.class));
                        } else if (position == 2) {//退出登录
                            merchant = "";
                            assignedproject = "";
                            SobotApi.exitSobotChat(getContext());
                            MyFragment.isRefresh = true;
                            Addstatistout.sendPostRequest(Urls.Addstatistout, new Response.Listener<String>() {
                                public void onResponse(String s) {
                                }
                            }, new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError volleyError) {
                                }
                            });
                            AppInfo.clearKey(getContext());
                            JPushInterface.clearAllNotifications(getContext());
                            JPushInterface.setAlias(getContext(), "", null);
                            JPushInterface.stopPush(getContext());
                            UMShareAPI.get(getContext()).deleteOauth(getActivity(), SHARE_MEDIA.WEIXIN, new UMAuthListener() {
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
                            getActivity().stopService(intent11);
                            upLoginico();
                        }
                    }
                });
                break;
            case yqzq: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                startActivity(new Intent(getActivity(), InvitefriendActivity.class));
            }
            break;
//            case R.id.my_feedback: {
//                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
//                    ConfirmDialog.showDialog(getContext(), null, 2,
//                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
//                                @Override
//                                public void leftClick(Object object) {
//                                }
//
//                                @Override
//                                public void rightClick(Object object) {
//                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
//                                    startActivity(intent);
//                                }
//                            });
//                    return;
//                }
//                startActivity(new Intent(getActivity(), FeedbackActivity.class));
//            }
//            break;
            case R.id.my_logined: {
                Intent intent = new Intent(getActivity(), IdentifycodeLoginActivity.class);
                startActivity(intent);
            }
            break;
            //头像
            case R.id.my_img: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                Intent intent2 = new Intent(getContext(), MyDetailInfoActivity.class);
                intent2.putExtra("userPhoneNum", userPhoneNum);
                intent2.putExtra("isIndividualAccount", isIndividualAccount);
                intent2.putExtra("city", city);

                intent2.putExtra("team_usualplace", team_usualplace.toString());
                intent2.putExtra("free_time", free_time.toString());
                intent2.putExtra("personal_specialty", personal_specialty.toString());
                intent2.putExtra("age", age);
                intent2.putExtra("close_square", close_square);
                intent2.putExtra("bind_wechat", bind_wechat);
                intent2.putExtra("merchant", merchant);
                startActivity(intent2);
                //startActivity(new Intent(getContext(), MyDetailInfoActivity.class));
            }
            break;
            case R.id.my_account: {//我的账户
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                Intent intent = new Intent(getContext(), MyaccountActivity.class);
                startActivity(intent);
            }
            break;
            //智齿客服
//            case R.id.my_contactcustomer: {
//                Information info = new Information();
//                info.setAppkey(Urls.ZHICHI_KEY);
//                info.setColor("#FFFFFF");
//                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
//                    info.setUname("游客");
//                } else {
//                    String netHeadPath = AppInfo.getUserImagurl(getContext());
//                    info.setFace(netHeadPath);
//                    info.setUid(AppInfo.getKey(getContext()));
//                    info.setUname(AppInfo.getUserName(getContext()));
//                }
//                SobotApi.startSobotChat(getContext(), info);
//            }
//            break;

            case xx: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                String[] strs = AppInfo.getAddress(getContext());
                AppInfo.setJPush(getContext(), false);
                Intent intent = new Intent(getContext(), MyMessageActivity.class);
                intent.putExtra("latitude", MainActivity.location_latitude + "");
                intent.putExtra("longitude", MainActivity.location_longitude + "");
                intent.putExtra("address", strs[2]);
                intent.putExtra("province", strs[0]);
                intent.putExtra("city", strs[1]);
                startActivity(intent);
            }
            break;
            case R.id.my_oumi: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                startActivity(new Intent(getContext(), MyoumiActivity.class));
            }
            break;

//            case R.id.my_redPackage: {//我的红包
//                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
//                    ConfirmDialog.showDialog(getContext(), null, 2,
//                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
//                                @Override
//                                public void leftClick(Object object) {
//                                }
//
//                                @Override
//                                public void rightClick(Object object) {
//                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
//                                    startActivity(intent);
//                                }
//                            });
//                    return;
//                }
//                startActivity(new Intent(getContext(), MyRedPackageActivity.class));
//            }
//            break;
            case lxwm: {//关于我们
                SelectPhotoDialog.showPhotoSelecterAll(getContext(), new View.OnClickListener() {
                    public void onClick(View v) {
                        SelectPhotoDialog.dissmisDialog();
                        Intent intent = new Intent(getContext(), BrowserActivity.class);
                        intent.putExtra("flag", BrowserActivity.flag_about);
                        startActivity(intent);
                    }
                }, new View.OnClickListener() {
                    public void onClick(View v) {
                        SelectPhotoDialog.dissmisDialog();
                        Information info = new Information();
                        info.setAppkey(Urls.ZHICHI_KEY);
                        info.setColor("#FFFFFF");
                        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                            info.setUname("游客");
                        } else {
                            String netHeadPath = AppInfo.getUserImagurl(getContext());
                            info.setFace(netHeadPath);
                            info.setUid(AppInfo.getKey(getContext()));
                            info.setUname(AppInfo.getUserName(getContext()));
                        }
                        SobotApi.startSobotChat(getContext(), info);
                    }
                }, new View.OnClickListener() {
                    public void onClick(View v) {
                        SelectPhotoDialog.dissmisDialog();
                        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                            ConfirmDialog.showDialog(getContext(), null, 2,
                                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {
                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            return;
                        }
                        startActivity(new Intent(getActivity(), FeedbackActivity.class));
                    }
                }).initShowStr("关于我们", "联系客服", "帮助与反馈").settingImg(getActivity(), R.mipmap.wode_button_gywm,
                        R.mipmap.wode_button_lxkf, R.mipmap.wode_button_bzyfk);
            }
            break;
            case sjcz: {//手机充值
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                Intent intent = new Intent(getContext(), BrowserActivity.class);
                intent.putExtra("flag", BrowserActivity.flag_phonepay);
                intent.putExtra("userPhoneNum", userPhoneNum);
                startActivity(intent);
            }
            break;
        }
    }

    private void changeNetwork() {
        if (flag != 0) {
            Tools.d("network change flag---" + flag);
            AppInfo.setOpen4GUpdata(getContext(), flag);
            if (isHavNetwork()) {
                if (updataDBHelper.isHave()) {
                    Intent service = new Intent("com.orange.oy.UpdataNewService");
                    service.setPackage("com.orange.oy");
                    getContext().startService(service);
                }
            }
            sendData();
        }
    }

    private void sendData() {
        Dataconnection.sendPostRequest(Urls.Dataconnection, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private boolean isHavNetwork() {
        String network = Tools.GetNetworkType(getContext());//网络状态
        Tools.d("network--" + network);
        switch (flag) {
            case AppInfo.netSetting_1: {
                return !TextUtils.isEmpty(network);
            }
            case AppInfo.netSetting_2: {
                return "WIFI".equals(network);
            }
            case AppInfo.netSetting_3: {
                return !TextUtils.isEmpty(network) && network.endsWith("G");
            }
        }
        return false;
    }

    public void onStop() {
        super.onStop();
        if (Userinfo != null) {
            Userinfo.stop(Urls.Userinfo);
        }
    }

    ArrayList<String> team_usualplace = new ArrayList<>();
    ArrayList<String> free_time = new ArrayList<>();
    ArrayList<String> personal_specialty = new ArrayList<>();

    private boolean isagent = false;
    private String bindaccount, bindidcard, bindvr, vrid, payaccount, isIndividualAccount, userPhoneNum, city, age, close_square;
    private String usualplace;
    private String merchant;//是否是已认证商户，1为是，0为否
    private String assignedproject;//是否有原来的指派项目（0：没有，1：有）
    private String bind_wechat;

    private void getUserInfo() {
        Userinfo.sendPostRequest(Urls.Userinfo, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        age = jsonObject.optString("age");
                        close_square = jsonObject.optString("close_square"); //  close_square":"是否关闭广场（0：不关闭，1：关闭）

                        JSONArray jsonArray2 = jsonObject.optJSONArray("usual_place");  //常去地点
                        team_usualplace.clear();
                        if (jsonArray2 != null) {
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                String string = jsonArray2.getString(i);
                                team_usualplace.add(string);
                            }
                        }
                        free_time.clear();
                        JSONArray jsonArray3 = jsonObject.optJSONArray("free_time");  //空闲时间
                        if (jsonArray3 != null) {
                            for (int i = 0; i < jsonArray3.length(); i++) {
                                String string = jsonArray3.getString(i);
                                free_time.add(string);
                            }
                        }
                        personal_specialty.clear();
                        JSONArray jsonArray4 = jsonObject.optJSONArray("personal_specialty");  //个人特长
                        if (jsonArray4 != null) {
                            for (int i = 0; i < jsonArray4.length(); i++) {
                                String string = jsonArray4.getString(i);
                                personal_specialty.add(string);
                            }
                        }
                        assignedproject = jsonObject.optString("assignedproject");

                        bind_wechat = jsonObject.getString("bind_wechat");
                        merchant = jsonObject.getString("merchant");
                        isagent = "1".equals(jsonObject.getString("is_agent"));
                        bindaccount = jsonObject.getString("bindaccount");
                        bindidcard = jsonObject.getString("bindidcard");
                        bindvr = jsonObject.getString("bindvr");
                        vrid = jsonObject.getString("vrid");
                        payaccount = jsonObject.getString("payaccount");
                        userPhoneNum = jsonObject.optString("userPhoneNum"); //用户手机号
                        city = jsonObject.getString("city");  //城市
                        isIndividualAccount = jsonObject.getString("isIndividualAccount");  //是否个性化帐号，1为是，0为否
                        AppInfo.setUserinfo(getContext(), jsonObject.getString("user_name"), jsonObject.getString("user_sex"),
                                jsonObject.getString("city"), jsonObject.getString("province"),
                                jsonObject.getString("user_address"), jsonObject.getString("img_url"), jsonObject.getString("invite_code"));
                        AppInfo.setUserphone(getContext(), userPhoneNum);
                        upLoginico();
                        String withdrawalmoney = jsonObject.getString("withdrawalmoney");
                        if (withdrawalmoney != null) {
                            if (TextUtils.isEmpty(withdrawalmoney)) {
                                withdrawalmoney = "-";
                            } else {
                                double d = Tools.StringToDouble(withdrawalmoney);
                                if (d - (int) d > 0) {
                                    withdrawalmoney = String.valueOf(d);
                                } else {
                                    withdrawalmoney = String.valueOf((int) d);
                                }
                            }
                            my_account.setText("我的红包 ¥" + withdrawalmoney);
                        } else {
                            my_account.setText("-");
                        }
                        String totalom = jsonObject.getString("totalom");
                        if (totalom != null) {
                            if (TextUtils.isEmpty(totalom)) {
                                totalom = "-";
                            } else {
                                double d = Tools.StringToDouble(totalom);
                                if (d - (int) d > 0) {
                                    totalom = String.valueOf(d);
                                } else {
                                    totalom = String.valueOf((int) d);
                                }
                            }
                            my_oumi.setText("我的偶米 " + totalom);
                        } else {
                            my_oumi.setText("-");
                        }
                        JSONArray jsonArray5 = jsonObject.optJSONArray("task_datas");//新手任务弹题
                        ArrayList<TaskNewInfo> list_task_datas = new ArrayList<TaskNewInfo>();
                        String username = AppInfo.getName(getContext());
                        if (jsonArray5 != null) {//需要做新手任务
                            for (int i = 0; i < jsonArray5.length(); i++) {
                                JSONObject object = jsonArray5.getJSONObject(i);
                                TaskNewInfo taskNewInfo = new TaskNewInfo();
                                taskNewInfo.setProjectid(object.getString("project_id"));
                                taskNewInfo.setProject_name(object.getString("project_name"));
                                taskNewInfo.setPhoto_compression(object.getString("photo_compression"));
                                taskNewInfo.setIs_record(Tools.StringToInt(object.getString("is_record")));
                                taskNewInfo.setIs_takephoto(Tools.StringToInt(object.getString("is_takephoto")));
                                taskNewInfo.setStore_id(object.getString("store_id"));
                                taskNewInfo.setStore_num(object.getString("store_num"));
                                taskNewInfo.setStore_address(object.getString("store_address"));
                                taskNewInfo.setStore_name(object.getString("store_name"));
                                taskNewInfo.setAccessed_num(object.getString("accessed_num"));
                                taskNewInfo.setP_id(object.getString("p_id"));
                                taskNewInfo.setP_name(object.getString("p_name"));
                                taskNewInfo.setP_desc(object.getString("p_desc"));
                                taskNewInfo.setP_is_invalid(object.getString("p_is_invalid"));
                                taskNewInfo.setTask_id(object.getString("task_id"));
                                taskNewInfo.setTask_name(object.getString("task_name"));
                                taskNewInfo.setTask_type(object.getString("task_type"));
                                taskNewInfo.setTask_note(object.getString("task_note"));
                                taskNewInfo.setIs_package(object.getString("is_package"));
                                taskNewInfo.setTask_detail(object.getString("task_detail"));
                                taskNewInfo.setTask_content(object.getString("task_content"));
                                taskNewInfo.setBatch(object.getString("batch"));
                                taskNewInfo.setIs_watermark(Tools.StringToInt(object.getString("is_watermark")));
                                taskNewInfo.setCode(object.getString("code"));
                                taskNewInfo.setBrand(object.getString("brand"));
                                taskNewInfo.setP_batch(object.getString("p_batch"));
                                taskNewInfo.setOutlet_batch(object.getString("outlet_batch"));
                                taskNewInfo.setIs_package_task(object.getString("is_package_task"));
                                taskNewInfo.setInvalid_type(object.getString("invalid_type"));
                                list_task_datas.add(taskNewInfo);
                            }
                            boolean isFirst = appDBHelper.getLoginnumber(username);
                            if (isFirst) {
                                AppInfo.setIsShow(getContext(), true);
                            }
                            if (AppInfo.getIsShow(getContext())) {//显示新手任务
//                                Intent intent = new Intent(getContext(), TaskGuideActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("list", list_task_datas);
//                                intent.putExtra("data", bundle);
//                                intent.putExtra("type", "1");//登录页面跳转为1 设置页面跳转为0
//                                startActivity(intent);
                            } else {
                                if (!list_task_datas.isEmpty() || list_task_datas != null) {
                                    TaskNewInfo taskNewInfo = list_task_datas.remove(0);
                                    String type = taskNewInfo.getTask_type();
                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("list", list_task_datas);
                                    intent.putExtra("data", bundle);
                                    intent.putExtra("project_id", taskNewInfo.getProjectid());
                                    intent.putExtra("project_name", taskNewInfo.getProject_name());
                                    intent.putExtra("task_pack_id", "");
                                    intent.putExtra("task_pack_name", "");
                                    intent.putExtra("task_id", taskNewInfo.getTask_id());
                                    intent.putExtra("task_name", taskNewInfo.getTask_name());
                                    intent.putExtra("store_id", taskNewInfo.getStore_id());
                                    intent.putExtra("store_num", taskNewInfo.getStore_num());
                                    intent.putExtra("store_name", taskNewInfo.getStore_name());
                                    intent.putExtra("category1", "");
                                    intent.putExtra("category2", "");
                                    intent.putExtra("category3", "");
                                    intent.putExtra("is_desc", "");
                                    intent.putExtra("code", taskNewInfo.getCode());
                                    intent.putExtra("brand", taskNewInfo.getBrand());
                                    intent.putExtra("outlet_batch", taskNewInfo.getOutlet_batch());
                                    intent.putExtra("p_batch", taskNewInfo.getP_batch());
                                    intent.putExtra("newtask", "1");//判断是否是新手任务 1是0否
                                    intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                    if ("1".equals(type)) {//拍照任务
                                        intent.setClass(getContext(), TaskitemPhotographyNextYActivity.class);
                                        startActivity(intent);
                                    } else if ("2".equals(type)) {//视频任务
                                        intent.setClass(getContext(), TaskitemShotActivity.class);
                                        startActivity(intent);
                                    } else if ("3".equals(type)) {//记录任务
                                        intent.setClass(getContext(), TaskitemEditActivity.class);
                                        startActivity(intent);
                                    } else if ("4".equals(type)) {//定位任务
                                        intent.setClass(getContext(), TaskitemMapActivity.class);
                                        startActivity(intent);
                                    } else if ("5".equals(type)) {//录音任务
                                        intent.setClass(getContext(), TaskitemRecodillustrateActivity.class);
                                        startActivity(intent);
                                    } else if ("6".equals(type)) {//扫码任务
                                        intent.setClass(getContext(), ScanTaskNewActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                        appDBHelper.addLoginNumber(username);
                        AppInfo.isbindaccount(getContext(), bindaccount);
                    } else if ("1".equals(jsonObject.getString("code"))) {
                        upLoginico();
                    }
                } catch (JSONException e) {
                    upLoginico();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                upLoginico();
            }
        });
    }

    /**
     * 生成极光alias
     */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Tools.d(logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Tools.d(logs);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Tools.d(logs);
            }
            // UI.showToast(logs);
        }
    };
}
