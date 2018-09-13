package com.orange.oy.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.SearchActivity;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.TaskitemEditActivity;
import com.orange.oy.activity.TaskitemMapActivity;
import com.orange.oy.activity.TaskitemPhotographyNextYActivity;
import com.orange.oy.activity.TaskitemRecodillustrateActivity;
import com.orange.oy.activity.TaskitemShotActivity;
import com.orange.oy.activity.alipay.NewestWithdrawsActivity;
import com.orange.oy.activity.bigchange.NewMessageActivity;
import com.orange.oy.activity.experience.ExperienceLocationActivity;
import com.orange.oy.activity.guide.TaskLocationActivity;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.mycorps_315.CorpGrabActivity;
import com.orange.oy.activity.newtask.NoOutletsActivity;
import com.orange.oy.activity.newtask.ProjectRecruitmentActivity;
import com.orange.oy.activity.newtask.TaskGrabActivity;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.shakephoto_320.MyMessageActivity;
import com.orange.oy.adapter.TaskNewAdapter2;
import com.orange.oy.allinterface.BroadcastReceiverBackListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.galllery.ZQImageViewRoundOval;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.TopRightMenu;
import com.orange.oy.view.ViewPagerScroller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * 1.16版本修改的任务页面
 */
public class TaskNewFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener,
        NetworkConnection.OnOutTimeListener, BroadcastReceiverBackListener {

    public void listener(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(AppInfo.LOCATIONINFO)) {
            String type = intent.getStringExtra("type");//0==Jpush  1==main
            if ("0".equals(type)) {
                AppInfo.setJPush(context, true);
                setting_more.setImageResource(R.mipmap.task_message2);
            } else if ("1".equals(type)) {
                address = intent.getStringExtra("address");
                longitude = intent.getStringExtra("longitude");
                latitude = intent.getStringExtra("latitude");
                province = intent.getStringExtra("province");
                county = intent.getStringExtra("county");
                city = intent.getStringExtra("city");
                String oldCity = AppInfo.getCityName(getContext());
                if (TextUtils.isEmpty(oldCity)) {
                    settingDistric(province, city, county, null);
                    getData();
                } else if (!city.equals(oldCity)) {
                    ConfirmDialog.showDialog(getContext(), null, 2, "您是否切换定位城市", "否", "是", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            settingDistric(province, city, county, null);
                            if (list == null || list.isEmpty()) {
                                getData();
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void outTime() {
        if (!isLoadSuccess) {
            mView.findViewById(R.id.unsuccess_view).setVisibility(View.VISIBLE);
        }
    }

    public interface OnCitysearchClickListener {
        void clickforTask();
    }

    public void setOnCitysearchClickListener(OnCitysearchClickListener listener) {
        onCitysearchClickListener = listener;
    }

    public static boolean isRefresh = false;

    @Override
    public void onPause() {
        super.onPause();
//        if (!TextUtils.isEmpty(tasknew_distric.getText().toString()) && !"城市".equals(tasknew_distric.getText().toString())) {
//            AppInfo.setCityName(getContext(), province, city, county);
//        }
        AppInfo.setH5Data(getContext(), null, null);
        isRefresh = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        tag = 1;
        if (isRefresh) {
            refreshData();
        }
//        if (imgList2.size() != 0) {
        sendScrollMessage(DEFAULT_INTERVAL);
//        }
//        tasknew_viewBanner.startAutoScroll();
    }

    @Override
    public void onStop() {
        AppInfo.setH5Data(getContext(), null, null);
        isRefresh = false;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        tasknew_viewBanner.stopAutoScroll();
//        if (imgList2.size() != 0) {
        mHandler.removeMessages(SCROLL_WHAT);
//        }
    }

    private void initNetworkConnection() {
        checkapply = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    params.put("usermobile", AppInfo.getName(getContext()));
                    params.put("token", Tools.getToken());
                }
                params.put("projectid", projectid);
                return params;
            }
        };
        projectList2 = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    params.put("token", Tools.getToken());
                    params.put("usermobile", AppInfo.getName(getContext()));
                }

                String city = tasknew_distric.getText().toString().trim();
                if (city.contains("-")) {
                    int index = city.indexOf("-");
                    city = city.substring(index + 1, city.length());
                }
                params.put("city", city);
                params.put("province", province);

                h5projectid = AppInfo.getH5projectid(getContext());
                h5usermobile = AppInfo.getH5usermobile(getContext());
                Tools.d("h5usermobile2:" + h5usermobile + "h5projectid2:" + h5projectid);
                if (h5projectid != null && h5usermobile != null) {
                    params.put("h5projectid", h5projectid);
                    params.put("h5usermobile", h5usermobile);
                }
                return params;
            }
        };
        projectList2.setOnOutTimeListener(this);
        projectList2.setTimeCount(true);
    }

    private View mView;
    private TextView tasknew_distric;
    private PullToRefreshListView tasknew_listview_left;
    private ImageView setting_more;
    //        private BannerView tasknew_viewBanner;
    private OnCitysearchClickListener onCitysearchClickListener;
    private ArrayList<TaskNewInfo> list;//数据集合(推荐项目)
    private ArrayList<TaskNewInfo> imgList;
    private ArrayList<String> imgList2;
    private String projectid;
    private TaskNewAdapter2 taskNewAdapter;
    private NetworkConnection projectList2, checkapply;
    private ImageLoader imageLoader;
    private int tag = 1;
    private AppDBHelper appDBHelper;
    //3.3大概本修改内容
    private TopRightMenu mTopRightMenu;
    private String h5projectid, h5usermobile;
    private ZQImageViewRoundOval newhand_taskimg;
    private ArrayList<TaskNewInfo> list_index_datas;
    private ViewPager tasknew_viewpager;
    private MyPagerAdapter myPagerAdapter;
    private ZQImageViewRoundOval recommendtask_img;
    private String outletId, latitude = "", longitude = "", address = "";
    private CircularImageView withdraw_img;
    private TextView withdraw_content, withdraw_time;
    private String province = "", city = "", county = "";
    private boolean isLoadSuccess = false;//是否加载成功

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_task_new1, container, false);
        return mView;
    }

    private View headViewHold;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initNetworkConnection();
        mHandler = new PollingHandler();
        appDBHelper = new AppDBHelper(getContext());
        list = new ArrayList<>();
        imgList = new ArrayList<>();
        imgList2 = new ArrayList<>();
        list_index_datas = new ArrayList<>();
        imageLoader = new ImageLoader(getContext());
        headViewHold = Tools.loadLayout(getContext(), R.layout.fragment_task_new_listhead);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            RelativeLayout title_layout = (RelativeLayout) mView.findViewById(R.id.titlenew_layout);
            int height = (int) getResources().getDimension(R.dimen.apptitle_height);
            if (title_layout.getHeight() != height) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) title_layout.getLayoutParams();
                lp.height = height;
                title_layout.setLayoutParams(lp);
                title_layout.setPadding(0, 0, 0, 0);
            }
        }
        withdraw_time = (TextView) headViewHold.findViewById(R.id.withdraw_time);
        withdraw_content = (TextView) headViewHold.findViewById(R.id.withdraw_content);
        withdraw_img = (CircularImageView) headViewHold.findViewById(R.id.withdraw_img);
        tasknew_distric = (TextView) mView.findViewById(R.id.tasknew_distric);
        tasknew_listview_left = (PullToRefreshListView) mView.findViewById(R.id.tasknew_listview_left);
        setting_more = (ImageView) mView.findViewById(R.id.setting_more);
        if (AppInfo.getIsJPush(getContext())) {
            setting_more.setImageResource(R.mipmap.task_message2);
        } else {
            setting_more.setImageResource(R.mipmap.task_message);
        }
        setting_more.setOnClickListener(this);
        headViewHold.findViewById(R.id.task_location).setOnClickListener(this);
        tasknew_viewpager = (ViewPager) headViewHold.findViewById(R.id.tasknew_viewpager);
        ViewPagerScroller pagerScroller = new ViewPagerScroller(getContext());//轮播图滑动速度设置
        pagerScroller.setScrollDuration(1000);
        pagerScroller.initViewPagerScroll(tasknew_viewpager);
        recommendtask_img = (ZQImageViewRoundOval) headViewHold.findViewById(R.id.recommendtask_img);
        myPagerAdapter = new MyPagerAdapter();
        newhand_taskimg = (ZQImageViewRoundOval) headViewHold.findViewById(R.id.newhand_taskimg);
        newhand_taskimg.setRoundRadius(10);
        newhand_taskimg.setType(ZQImageViewRoundOval.TYPE_ROUND);
        newhand_taskimg.setScaleType(ImageView.ScaleType.FIT_XY);
        newhand_taskimg.setOnClickListener(this);
        //计算新手任务高度 347×90
        int newhandWidth = (int) (Tools.getScreeInfoWidth(getContext()) - 2 * getResources().getDimension(R.dimen.newhand_padding));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) newhand_taskimg.getLayoutParams();
        lp.height = (int) (newhandWidth * 90f / 347f);
        newhand_taskimg.setLayoutParams(lp);
        tasknew_listview_left.getRefreshableView().addHeaderView(headViewHold);
        tasknew_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        tasknew_listview_left.setOnItemClickListener(this);
        tasknew_listview_left.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (taskNewAdapter != null)
                    taskNewAdapter.clearClick();
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        refreshLayoutListener();
        String[] strings = AppInfo.getAddress(getContext());
        province = strings[0];
        city = strings[1];
        county = strings[2];
        if (TextUtils.isEmpty(county) || "null".equals(county)) {
            tasknew_distric.setText(city);
        } else {
            tasknew_distric.setText(county + "-" + province + "-" + city);
        }
        mView.findViewById(R.id.tasknew_citysearch).setOnClickListener(this);
        taskNewAdapter = new TaskNewAdapter2(getContext(), list);
        tasknew_listview_left.setAdapter(taskNewAdapter);
        headViewHold.findViewById(R.id.withdraw_layout).setOnClickListener(this);
        mView.findViewById(R.id.titlenew_name).setOnClickListener(this);
        if (!TextUtils.isEmpty(result)) {
            parseData(result);
        }
        getData();
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZQImageViewRoundOval imageView = new ZQImageViewRoundOval(getContext());
            imageView.setRoundRadius(10);
            imageView.setType(ZQImageViewRoundOval.TYPE_ROUND);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);
            TaskNewInfo taskNewInfo = imgList.get(position % imgList2.size());
            imageLoader.DisplayImage(Urls.ImgIp + imgList2.get(position % imgList2.size()), imageView);
            onTouchViewPager(imageView, position, taskNewInfo);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

    private Handler mHandler;
    public static final int SCROLL_WHAT = 0;
    private long mInterval = DEFAULT_INTERVAL;
    public static final int DEFAULT_INTERVAL = 3000;

    private void sendScrollMessage(long delayTimeInMills) {
        /** remove messages before, keeps one message is running at most **/
        mHandler.removeMessages(SCROLL_WHAT);
        mHandler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }

    @SuppressLint("HandlerLeak")
    class PollingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCROLL_WHAT:
                    scroll();
                    sendScrollMessage(mInterval);
                default:
                    break;
            }
        }
    }

    private boolean mIsBorderAnimation = false;

    private void scroll() {
        PagerAdapter adapter = tasknew_viewpager.getAdapter();
        int currentItem = tasknew_viewpager.getCurrentItem();
        int totalCount;
        if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
            return;
        }
        if (currentItem == totalCount - 1) {
            tasknew_viewpager.setCurrentItem(0, mIsBorderAnimation);

        } else {
            tasknew_viewpager.setCurrentItem(++currentItem, true);
        }

    }

    private void refreshLayoutListener() {
        tasknew_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private void refreshData() {
        getData();
    }


    private void viewpager() {
        int size = imgList2.size();
        if (size > 1) {
            tasknew_viewpager.setVisibility(View.VISIBLE);
            recommendtask_img.setVisibility(View.GONE);
            tasknew_viewpager.setAdapter(myPagerAdapter);
            tasknew_viewpager.setPageMargin(40);
//            tasknew_viewpager.setPageTransformer(true, new MyGallyPageTransformer());
        } else if (size == 1) {
            tasknew_viewpager.setVisibility(View.GONE);
            recommendtask_img.setVisibility(View.VISIBLE);
            recommendtask_img.setRoundRadius(10);
            recommendtask_img.setType(ZQImageViewRoundOval.TYPE_ROUND);
            recommendtask_img.setScaleType(ImageView.ScaleType.FIT_XY);
            imageLoader.DisplayImage(Urls.ImgIp + imgList2.get(0), recommendtask_img);
            onTouchViewPager(recommendtask_img, 0, imgList.get(0));
        }
    }

    private String user_indentity;
    private String result;

    private void getData() {
        if (TextUtils.isEmpty(tasknew_distric.getText()) || tasknew_distric.getText().toString().equals(getResources()
                .getString(R.string.find_city)) ||
                !tasknew_distric.getText().toString().equals(AppInfo.getCityName(getContext()))) {
            String[] s = AppInfo.getAddress(getContext());
            province = s[0];
            city = s[1];
            county = s[2];
            if (TextUtils.isEmpty(county) || "null".equals(county)) {
                tasknew_distric.setText(city);
            } else {
                tasknew_distric.setText(county + "-" + province + "-" + city);
            }
        }
        projectList2.sendPostRequest(Urls.ProjectList2, new Response.Listener<String>() {
            public void onResponse(String s) {
                parseData(s);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                tasknew_listview_left.onRefreshComplete();
            }
        });
    }

    private void parseData(String s) {
        tasknew_listview_left.onRefreshComplete();
        isLoadSuccess = true;
        mView.findViewById(R.id.unsuccess_view).setVisibility(View.GONE);
        Tools.d(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                result = s;
                user_indentity = jsonObject.optString("user_indentity");//用户身份，1 队长 2队副 3普通成员
                if ("1".equals(jsonObject.optString("show_map"))) {//1显示地图
                    headViewHold.findViewById(R.id.task_location).setVisibility(View.VISIBLE);
                } else {
                    headViewHold.findViewById(R.id.task_location).setVisibility(View.INVISIBLE);
                }
                String jump_projectid = jsonObject.getString("jump_projectid");//jump_projectid值为-1时不跳转
                //删除了之前的新手类型和指派任务类型--可在上个版本找回
                JSONArray jsonArray1 = jsonObject.optJSONArray("recommend_datas");//推荐项目
                if (jsonArray1 != null) {
                    headViewHold.findViewById(R.id.recommend_task).setVisibility(View.VISIBLE);
                    if (imgList == null) {
                        imgList = new ArrayList<TaskNewInfo>();
                    } else {
                        imgList.clear();
                    }
                    if (imgList2 == null) {
                        imgList2 = new ArrayList<String>();
                    } else {
                        imgList2.clear();
                    }
                    for (int i = 0; i < jsonArray1.length(); i++) {//推荐项目
                        JSONObject object = jsonArray1.getJSONObject(i);
                        TaskNewInfo taskNewInfo = new TaskNewInfo();
                        taskNewInfo.setId(object.getString("id"));
                        taskNewInfo.setProject_name(object.getString("project_name"));
                        taskNewInfo.setRob_state(object.getString("rob_state"));
                        taskNewInfo.setGift_url(object.getString("gift_url"));
                        taskNewInfo.setReward_type(object.getString("reward_type"));
                        taskNewInfo.setProject_code(object.getString("project_code"));
                        taskNewInfo.setProject_type(object.getString("project_type"));
                        taskNewInfo.setIs_record(Tools.StringToInt(object.getString("is_record")));
                        taskNewInfo.setPhoto_compression(object.getString("photo_compression"));
                        taskNewInfo.setBegin_date(object.getString("begin_date"));
                        taskNewInfo.setEnd_date(object.getString("end_date"));
                        taskNewInfo.setIs_download(Tools.StringToInt(object.getString("is_download")));
                        taskNewInfo.setIs_watermark(Tools.StringToInt(object.getString("is_watermark")));
                        taskNewInfo.setCode(object.getString("code"));
                        taskNewInfo.setBrand(object.getString("brand"));
                        taskNewInfo.setIs_takephoto(Tools.StringToInt(object.getString("is_takephoto")));
                        taskNewInfo.setType(object.getString("type"));
                        taskNewInfo.setShow_type(object.getString("show_type"));
                        taskNewInfo.setCheck_time(object.getString("check_time"));
                        taskNewInfo.setMin_reward(object.getString("min_reward"));
                        taskNewInfo.setMax_reward(object.getString("max_reward"));
                        taskNewInfo.setProject_property(object.getString("project_property"));
                        taskNewInfo.setPublish_time(object.optString("publish_time"));
                        taskNewInfo.setProject_person(object.optString("project_person"));
                        taskNewInfo.setMoney_unit(object.getString("money_unit"));
                        taskNewInfo.setCertification(object.getString("certification"));
                        taskNewInfo.setStandard_state(object.getString("standard_state"));
                        taskNewInfo.setPhoto_url(object.getString("photo_url"));
                        taskNewInfo.setIs_project(object.getString("is_project"));
                        taskNewInfo.setLink_url(object.getString("link_url"));
                        taskNewInfo.setProject_model(object.optString("project_model"));
                        imgList2.add(object.getString("photo_url"));
                        imgList.add(taskNewInfo);
                    }
                    viewpager();
                } else {
                    headViewHold.findViewById(R.id.recommend_task).setVisibility(View.GONE);
                }
                JSONArray jsonArray2 = jsonObject.optJSONArray("index_datas");//新手项目
                if (jsonArray2 != null) {//有新手项目
                    if (list_index_datas == null) {
                        list_index_datas = new ArrayList<TaskNewInfo>();
                    } else {
                        list_index_datas.clear();
                    }
                    headViewHold.findViewById(R.id.newhand_task).setVisibility(View.VISIBLE);
                    newhand_taskimg.setVisibility(View.VISIBLE);
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject object = jsonArray2.getJSONObject(i);
                        TaskNewInfo taskNewInfo = new TaskNewInfo();
                        taskNewInfo.setId(object.getString("id"));
                        taskNewInfo.setProject_name(object.getString("project_name"));
                        taskNewInfo.setRob_state(object.getString("rob_state"));
                        taskNewInfo.setProject_code(object.getString("project_code"));
                        taskNewInfo.setProject_type(object.getString("project_type"));
                        taskNewInfo.setIs_record(Tools.StringToInt(object.getString("is_record")));
                        taskNewInfo.setPhoto_compression(object.getString("photo_compression"));
                        taskNewInfo.setBegin_date(object.getString("begin_date"));
                        taskNewInfo.setEnd_date(object.getString("end_date"));
                        taskNewInfo.setIs_download(Tools.StringToInt(object.getString("is_download")));
                        taskNewInfo.setIs_watermark(Tools.StringToInt(object.getString("is_watermark")));
                        taskNewInfo.setCode(object.getString("code"));
                        taskNewInfo.setBrand(object.getString("brand"));
                        taskNewInfo.setIs_takephoto(Tools.StringToInt(object.getString("is_takephoto")));
                        taskNewInfo.setType(object.getString("type"));
                        taskNewInfo.setShow_type(object.getString("show_type"));
                        taskNewInfo.setCheck_time(object.getString("check_time"));
                        taskNewInfo.setMin_reward(object.getString("min_reward"));
                        taskNewInfo.setMax_reward(object.getString("max_reward"));
                        taskNewInfo.setProject_property(object.getString("project_property"));
                        taskNewInfo.setPublish_time(object.optString("publish_time"));
                        taskNewInfo.setProject_person(object.optString("project_person"));
                        taskNewInfo.setMoney_unit(object.getString("money_unit"));
                        taskNewInfo.setCertification(object.getString("certification"));
                        taskNewInfo.setStandard_state(object.getString("standard_state"));
                        taskNewInfo.setPhoto_url(object.getString("photo_url"));
                        list_index_datas.add(taskNewInfo);
                    }
                    imageLoader.DisplayImage(Urls.ImgIp + list_index_datas.get(0).getPhoto_url(), newhand_taskimg);
                } else {
                    headViewHold.findViewById(R.id.newhand_task).setVisibility(View.GONE);
                }
                JSONArray jsonArray3 = jsonObject.optJSONArray("datas");//全部项目
                if (jsonArray3 != null) {
                    if (list == null) {
                        list = new ArrayList<TaskNewInfo>();
                    } else {
                        list.clear();
                    }
                    headViewHold.findViewById(R.id.all_task).setVisibility(View.VISIBLE);
                    for (int i = 0; i < jsonArray3.length(); i++) {
                        JSONObject object = jsonArray3.getJSONObject(i);
                        TaskNewInfo taskNewInfo = new TaskNewInfo();
                        taskNewInfo.setId(object.getString("id"));
                        taskNewInfo.setAnonymous_state(object.getString("anonymous_state"));
                        taskNewInfo.setProject_name(object.getString("project_name"));
                        taskNewInfo.setProject_code(object.getString("project_code"));

                        taskNewInfo.setRob_state(object.getString("rob_state"));
                        taskNewInfo.setGift_url(object.getString("gift_url"));
                        taskNewInfo.setReward_type(object.getString("reward_type"));

                        taskNewInfo.setProject_type(object.getString("project_type"));
                        taskNewInfo.setIs_record(Tools.StringToInt(object.getString("is_record")));
                        taskNewInfo.setPhoto_compression(object.getString("photo_compression"));
                        taskNewInfo.setBegin_date(object.getString("begin_date"));
                        taskNewInfo.setEnd_date(object.getString("end_date"));
                        taskNewInfo.setIs_download(Tools.StringToInt(object.getString("is_download")));
                        taskNewInfo.setIs_watermark(Tools.StringToInt(object.getString("is_watermark")));
                        taskNewInfo.setCode(object.getString("code"));
                        taskNewInfo.setBrand(object.getString("brand"));
                        taskNewInfo.setIs_takephoto(Tools.StringToInt(object.getString("is_takephoto")));
                        taskNewInfo.setType(object.getString("type"));
                        taskNewInfo.setShow_type(object.getString("show_type"));
                        taskNewInfo.setCheck_time(object.getString("check_time"));
                        taskNewInfo.setMin_reward(object.getString("min_reward"));
                        taskNewInfo.setMax_reward(object.getString("max_reward"));
                        taskNewInfo.setProject_property(object.getString("project_property"));
                        taskNewInfo.setPublish_time(object.optString("publish_time"));
                        taskNewInfo.setProject_person(object.optString("project_person"));
                        taskNewInfo.setMoney_unit(object.getString("money_unit"));
                        taskNewInfo.setCertification(object.getString("certification"));
                        taskNewInfo.setStandard_state(object.getString("standard_state"));
                        taskNewInfo.setProject_model(object.optString("project_model"));
                        list.add(taskNewInfo);
                    }
                } else {
                    headViewHold.findViewById(R.id.all_task).setVisibility(View.GONE);
                }
                JSONArray jsonArray5 = jsonObject.optJSONArray("task_datas");//注册项目弹题
                ArrayList<TaskNewInfo> list_task_datas = new ArrayList<TaskNewInfo>();
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
                        taskNewInfo.setRob_state(object.getString("rob_state"));
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
                }
                JSONObject jsonObject1 = jsonObject.optJSONObject("withdrawal_info");//提现榜信息
                if (jsonObject1 != null) {
                    mView.findViewById(R.id.withdraw_layout).setVisibility(View.VISIBLE);
                    String img_url = jsonObject1.optString("img_url");
                    if (!TextUtils.isEmpty(img_url) && !"null".equals(img_url)) {
                        imageLoader.DisplayImage(Urls.ImgIp + img_url, withdraw_img, R.mipmap.grxx_icon_mrtx);
                    } else {
                        withdraw_img.setImageResource(R.mipmap.grxx_icon_mrtx);
                    }
                    withdraw_content.setText(jsonObject1.getString("record"));
                    withdraw_time.setText(jsonObject1.getString("date"));
                } else {
                    mView.findViewById(R.id.withdraw_layout).setVisibility(View.GONE);
                }
                int size = jsonObject.getInt("size");
                int shade = jsonObject.getInt("shade");
                AppInfo.setNewTask(getContext(), size, shade);
                if (myPagerAdapter != null) {
                    myPagerAdapter.notifyDataSetChanged();
                }
                if ("0".equals(jump_projectid)) {
                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                    intent.putExtra("nologin", "1");
                    startActivityForResult(intent, 0);
                } else if (!"-1".equals(jump_projectid) && !"0".equals(jump_projectid)) {//跳转项目信息
                    JSONObject object = jsonObject.optJSONObject("jump_projectinfo");
                    if (object != null) {
                        projectid = jump_projectid;
                        doJumpProject(object);
                    }
                }
                if (taskNewAdapter != null) {
                    taskNewAdapter.notifyDataSetChanged();
                }
                String remind = jsonObject.optString("remind");
                if ("1".equals(remind)) {
                    Tools.showToast2(getContext(), jsonObject.optString("remind_msg"));
                }
                if (!list_task_datas.isEmpty() && list_task_datas != null) {
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
                    if ("1".equals(type) || "8".equals(type)) {//拍照任务
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
            } else {
                Tools.showToast(getContext(), jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            Tools.showToast(getContext(), getResources().getString(R.string.network_error));
        }
    }

    public void doJumpProject(final JSONObject jsonObject) {
        try {
            String project_property = jsonObject.getString("project_property");
            if (project_property.equals("2")) {//众包
                final String type = jsonObject.getString("type");
                final String id = jsonObject.getString("id");
                Tools.d(appDBHelper.getIsShow(id) + "-----" + "1".equals(jsonObject.getString("standard_state")));
                if (appDBHelper.getIsShow(id) && "1".equals(jsonObject.getString("standard_state"))) {
                    Intent intent = new Intent(getContext(), TaskillustratesActivity.class);
                    intent.putExtra("project_person", jsonObject.getString("project_person"));
                    intent.putExtra("id", id);
                    intent.putExtra("projectid", id);
                    intent.putExtra("project_name", jsonObject.getString("project_name"));
                    intent.putExtra("project_code", jsonObject.getString("project_code"));
                    intent.putExtra("project_type", jsonObject.getString("project_type"));
                    intent.putExtra("is_record", jsonObject.getString("is_record") + "");
                    intent.putExtra("photo_compression", jsonObject.getString("photo_compression"));
                    intent.putExtra("begin_date", jsonObject.getString("begin_date"));
                    intent.putExtra("end_date", jsonObject.getString("end_date"));
                    intent.putExtra("is_download", jsonObject.getString("is_download") + "");
                    intent.putExtra("is_watermark", jsonObject.getString("is_watermark") + "");
                    intent.putExtra("code", jsonObject.getString("code"));
                    intent.putExtra("brand", jsonObject.getString("brand"));
                    intent.putExtra("is_takephoto", jsonObject.getString("is_takephoto") + "");
                    intent.putExtra("show_type", jsonObject.getString("show_type"));
                    intent.putExtra("check_time", jsonObject.getString("check_time"));
                    intent.putExtra("project_property", jsonObject.getString("project_property"));
                    intent.putExtra("city", tasknew_distric.getText().toString());
                    intent.putExtra("type", jsonObject.getString("type"));
                    intent.putExtra("money_unit", jsonObject.getString("money_unit"));
                    intent.putExtra("standard_state", jsonObject.getString("standard_state"));
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("address", address);
                    intent.putExtra("province", province);
                    intent.putExtra("isHomePage", "1");//是否是首页传过来的 1首页 0我的任务列表 2地图
                    startActivity(intent);
                } else {
                    projectid = id;
                    checkapply.sendPostRequest(Urls.CheckApply, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject object = new JSONObject(s);
                                if (object.getInt("code") == 200) {
                                    if ("1".equals(type)) {
                                        Intent intent = new Intent(getContext(), TaskGrabActivity.class);
                                        intent.putExtra("id", id);
                                        intent.putExtra("project_person", jsonObject.getString("project_person"));
                                        intent.putExtra("project_name", jsonObject.getString("project_name"));
                                        intent.putExtra("project_code", jsonObject.getString("project_code"));
                                        intent.putExtra("project_type", jsonObject.getString("project_type"));
                                        intent.putExtra("is_record", jsonObject.getString("is_record") + "");
                                        intent.putExtra("photo_compression", jsonObject.getString("photo_compression"));
                                        intent.putExtra("begin_date", jsonObject.getString("begin_date"));
                                        intent.putExtra("end_date", jsonObject.getString("end_date"));
                                        intent.putExtra("is_download", jsonObject.getString("is_download") + "");
                                        intent.putExtra("is_watermark", jsonObject.getString("is_watermark") + "");
                                        intent.putExtra("code", jsonObject.getString("code"));
                                        intent.putExtra("brand", jsonObject.getString("brand"));
                                        intent.putExtra("is_takephoto", jsonObject.getString("is_takephoto") + "");
                                        intent.putExtra("type", jsonObject.getString("type"));
                                        intent.putExtra("show_type", jsonObject.getString("show_type"));
                                        intent.putExtra("check_time", jsonObject.getString("check_time"));
                                        intent.putExtra("project_property", jsonObject.getString("project_property"));
                                        intent.putExtra("min_reward", jsonObject.getString("min_reward") + "");
                                        intent.putExtra("max_reward", jsonObject.getString("max_reward") + "");
                                        intent.putExtra("city", tasknew_distric.getText().toString());
                                        intent.putExtra("money_unit", jsonObject.getString("money_unit"));
                                        intent.putExtra("standard_state", jsonObject.getString("standard_state"));
                                        intent.putExtra("province", province);
                                        intent.putExtra("type1", "0");//首页跳转
                                        startActivity(intent);
                                    } else if ("4".equals(type)) {
                                        Intent intent = new Intent(getContext(), ExperienceLocationActivity.class);
                                        intent.putExtra("id", id);
                                        intent.putExtra("project_person", jsonObject.getString("project_person"));
                                        intent.putExtra("project_name", jsonObject.getString("project_name"));
                                        intent.putExtra("project_code", jsonObject.getString("project_code"));
                                        intent.putExtra("project_type", jsonObject.getString("project_type"));
                                        intent.putExtra("is_record", jsonObject.getString("is_record") + "");
                                        intent.putExtra("photo_compression", jsonObject.getString("photo_compression"));
                                        intent.putExtra("begin_date", jsonObject.getString("begin_date"));
                                        intent.putExtra("end_date", jsonObject.getString("end_date"));
                                        intent.putExtra("is_download", jsonObject.getString("is_download") + "");
                                        intent.putExtra("is_watermark", jsonObject.getString("is_watermark") + "");
                                        intent.putExtra("code", jsonObject.getString("code"));
                                        intent.putExtra("brand", jsonObject.getString("brand"));
                                        intent.putExtra("is_takephoto", jsonObject.getString("is_takephoto") + "");
                                        intent.putExtra("type", jsonObject.getString("type"));
                                        intent.putExtra("show_type", jsonObject.getString("show_type"));
                                        intent.putExtra("check_time", jsonObject.getString("check_time"));
                                        intent.putExtra("project_property", jsonObject.getString("project_property"));
                                        intent.putExtra("min_reward", jsonObject.getString("min_reward") + "");
                                        intent.putExtra("max_reward", jsonObject.getString("max_reward") + "");
                                        intent.putExtra("city", tasknew_distric.getText().toString());
                                        intent.putExtra("money_unit", jsonObject.getString("money_unit"));
                                        intent.putExtra("standard_state", jsonObject.getString("standard_state"));
                                        startActivity(intent);
                                    }
                                } else if (object.getInt("code") == 2) {//点击进入招募令
                                    Intent intent = new Intent(getContext(), ProjectRecruitmentActivity.class);
                                    intent.putExtra("projectid", projectid);
                                    startActivity(intent);
                                } else {
                                    Tools.showToast(getContext(), object.getString("msg"));
                                }
                            } catch (JSONException e) {
                                Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                        }
                    }, null);
                }
            }
        } catch (JSONException e) {
        }
    }

    private String rob_state;//是否可领取，1为可以领取，0为已抢完"

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 2;
        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            ConfirmDialog.showDialog(getContext(), null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                            intent.putExtra("nologin", "1");
                            startActivityForResult(intent, 0);
                        }
                    });
            return;
        }
        TaskNewInfo taskNewInfo = list.get(position);
        if (!Tools.isEmpty(taskNewInfo.getRob_state()) && "0".equals(taskNewInfo.getRob_state())) {
            return;
        }
        projectid = taskNewInfo.getId();
        rob_state = taskNewInfo.getRob_state(); //是否可领取，1为可以领取，0为已抢完"
        if (taskNewAdapter != null) {
            if (taskNewAdapter.isClick2() && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {//项目预览
                Intent intent = new Intent(getContext(), TaskitemDetailActivity_12.class);
                intent.putExtra("id", outletId);
                intent.putExtra("projectname", taskNewInfo.getProject_name());
                intent.putExtra("store_name", "网点名称");
                intent.putExtra("store_num", "网点编号");
                intent.putExtra("province", "");
                intent.putExtra("city", "");
                intent.putExtra("project_id", taskNewInfo.getId());
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("is_record", taskNewInfo.getIs_record());
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");//int
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");//String
                intent.putExtra("is_desc", "");
                intent.putExtra("index", "0");
                startActivity(intent);
            } else {
                if ("1".equals(taskNewInfo.getProject_model()) && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {
                    projectClick1(taskNewInfo);
                } else if ("2".equals(taskNewInfo.getProject_model()) && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {//战队+个人
                    if ("1".equals(user_indentity) && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {
                        Intent intent = new Intent(getContext(), CorpGrabActivity.class);
                        intent.putExtra("projectname", taskNewInfo.getProject_name());
                        intent.putExtra("projectid", projectid);
                        startActivityForResult(intent, 0);
                    } else {
                        projectClick1(taskNewInfo);
                    }
                } else {
                    if ("1".equals(user_indentity) && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {
                        Intent intent = new Intent(getContext(), CorpGrabActivity.class);
                        intent.putExtra("projectname", taskNewInfo.getProject_name());
                        intent.putExtra("projectid", projectid);
                        startActivityForResult(intent, 0);
                    }
                }
            }
            taskNewAdapter.clearClick();
        }
    }

    private void projectClick1(final TaskNewInfo taskNewInfo) {//个人任务
        projectid = taskNewInfo.getId();
        if (taskNewInfo.getProject_property().equals("2")) {//众包
            final String type = taskNewInfo.getType();
            if (appDBHelper.getIsShow(taskNewInfo.getId()) && "1".equals(taskNewInfo.getStandard_state()) && !"5".equals(type) && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {
                Intent intent = new Intent(getContext(), TaskillustratesActivity.class);
                intent.putExtra("project_person", taskNewInfo.getProject_person());
                intent.putExtra("projectname", taskNewInfo.getProject_name());
                intent.putExtra("projectid", taskNewInfo.getId());
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", tasknew_distric.getText().toString());
                intent.putExtra("type", taskNewInfo.getType());
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("address", address);
                intent.putExtra("province", province);
                intent.putExtra("isHomePage", "1");//是否是首页传过来的 1首页 0我的任务列表 2地图
                startActivity(intent);
            } else {
                checkapply.sendPostRequest(Urls.CheckApply, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getInt("code") == 200 && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {
                                if ("1".equals(type) || "6".equals(type)) {
                                    Intent intent = new Intent(getContext(), TaskGrabActivity.class);
                                    intent.putExtra("id", taskNewInfo.getId());
                                    intent.putExtra("project_person", taskNewInfo.getProject_person());
                                    intent.putExtra("project_name", taskNewInfo.getProject_name());
                                    intent.putExtra("project_code", taskNewInfo.getProject_code());
                                    intent.putExtra("project_type", taskNewInfo.getProject_type());
                                    intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                    intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                    intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                    intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                    intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                    intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                    intent.putExtra("code", taskNewInfo.getCode());
                                    intent.putExtra("brand", taskNewInfo.getBrand());
                                    intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                    intent.putExtra("type", taskNewInfo.getType());
                                    intent.putExtra("show_type", taskNewInfo.getShow_type());
                                    intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                    intent.putExtra("project_property", taskNewInfo.getProject_property());
                                    intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                    intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                    intent.putExtra("city", tasknew_distric.getText().toString());
                                    intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                    intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                    intent.putExtra("province", province);
                                    intent.putExtra("type1", "0");//首页跳转
                                    startActivity(intent);
                                } else if ("4".equals(type) && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {
                                    Intent intent = new Intent(getContext(), ExperienceLocationActivity.class);
                                    intent.putExtra("id", taskNewInfo.getId());
                                    intent.putExtra("project_person", taskNewInfo.getProject_person());
                                    intent.putExtra("project_name", taskNewInfo.getProject_name());
                                    intent.putExtra("project_code", taskNewInfo.getProject_code());
                                    intent.putExtra("project_type", taskNewInfo.getProject_type());
                                    intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                    intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                    intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                    intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                    intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                    intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                    intent.putExtra("code", taskNewInfo.getCode());
                                    intent.putExtra("brand", taskNewInfo.getBrand());
                                    intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                    intent.putExtra("type", taskNewInfo.getType());
                                    intent.putExtra("show_type", taskNewInfo.getShow_type());
                                    intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                    intent.putExtra("project_property", taskNewInfo.getProject_property());
                                    intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                    intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                    intent.putExtra("city", tasknew_distric.getText().toString());
                                    intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                    intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                    startActivity(intent);
                                } else if ("5".equals(type) && !Tools.isEmpty(rob_state) && "1".equals(rob_state)) {
                                    Intent intent = new Intent(getContext(), NoOutletsActivity.class);
                                    intent.putExtra("project_person", taskNewInfo.getProject_person());
                                    intent.putExtra("projectname", taskNewInfo.getProject_name());
                                    intent.putExtra("projectid", taskNewInfo.getId());
                                    intent.putExtra("id", taskNewInfo.getId());
                                    intent.putExtra("project_name", taskNewInfo.getProject_name());
                                    intent.putExtra("project_code", taskNewInfo.getProject_code());
                                    intent.putExtra("project_type", taskNewInfo.getProject_type());
                                    intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                    intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                    intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                    intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                    intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                    intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                    intent.putExtra("code", taskNewInfo.getCode());
                                    intent.putExtra("brand", taskNewInfo.getBrand());
                                    intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                    intent.putExtra("show_type", taskNewInfo.getShow_type());
                                    intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                    intent.putExtra("project_property", taskNewInfo.getProject_property());
                                    intent.putExtra("city", tasknew_distric.getText().toString());
                                    intent.putExtra("type", taskNewInfo.getType());
                                    intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                    intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                    intent.putExtra("longitude", longitude);
                                    intent.putExtra("latitude", latitude);
                                    intent.putExtra("address", address);
                                    startActivity(intent);
                                }
                            } else if (jsonObject.getInt("code") == 2) {//点击进入招募令
                                Intent intent = new Intent(getContext(), ProjectRecruitmentActivity.class);
                                intent.putExtra("project_person", taskNewInfo.getProject_person());
                                intent.putExtra("projectname", taskNewInfo.getProject_name());
                                intent.putExtra("id", taskNewInfo.getId());
                                intent.putExtra("project_name", taskNewInfo.getProject_name());
                                intent.putExtra("project_code", taskNewInfo.getProject_code());
                                intent.putExtra("project_type", taskNewInfo.getProject_type());
                                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                intent.putExtra("code", taskNewInfo.getCode());
                                intent.putExtra("brand", taskNewInfo.getBrand());
                                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                intent.putExtra("show_type", taskNewInfo.getShow_type());
                                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                intent.putExtra("project_property", taskNewInfo.getProject_property());
                                intent.putExtra("city", tasknew_distric.getText().toString());
                                intent.putExtra("type", taskNewInfo.getType());
                                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                intent.putExtra("longitude", longitude);
                                intent.putExtra("latitude", latitude);
                                intent.putExtra("address", address);
                                intent.putExtra("projectid", projectid);
                                startActivity(intent);
                            } else {
                                if (jsonObject.getInt("code") == 1) {
                                    if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {//进入招募令
                                        Intent intent = new Intent(getContext(), ProjectRecruitmentActivity.class);
                                        intent.putExtra("projectid", projectid);
                                        startActivity(intent);
                                    } else {
                                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                                    }
                                } else {
                                    Tools.showToast(getContext(), jsonObject.getString("msg"));
                                }
                            }
                        } catch (JSONException e) {
                            Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                    }
                }, null);
            }
        }
    }

    public void onTouchViewPager(View view, final int position, final TaskNewInfo taskNewInfo) {
        projectid = taskNewInfo.getId();
        view.setOnTouchListener(new View.OnTouchListener() {
            private long downTime;
            private int downX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = (int) event.getX();
                        downTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        if ((System.currentTimeMillis() - downTime < 500) && (Math.abs(downX - (int) event.getX()) < 30)) {
                            if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                                ConfirmDialog.showDialog(getContext(), null, 2,
                                        getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                            @Override
                                            public void leftClick(Object object) {
                                            }

                                            @Override
                                            public void rightClick(Object object) {
                                                Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                                intent.putExtra("nologin", "1");
                                                startActivityForResult(intent, 0);
                                            }
                                        });
                            } else {
                                if ("1".equals(taskNewInfo.getIs_project())) {//是项目
                                    if ("1".equals(taskNewInfo.getProject_model())) {
                                        rob_state = "1";
                                        projectClick1(taskNewInfo);
                                    } else if ("2".equals(taskNewInfo.getProject_model())) {//战队+个人
                                        if ("1".equals(user_indentity)) {
                                            Intent intent = new Intent(getContext(), CorpGrabActivity.class);
                                            intent.putExtra("projectname", taskNewInfo.getProject_name());
                                            intent.putExtra("projectid", projectid);
                                            startActivityForResult(intent, 0);
                                        } else {
                                            rob_state = "1";
                                            projectClick1(taskNewInfo);
                                        }
                                    } else {
                                        if ("1".equals(user_indentity)) {
                                            Intent intent = new Intent(getContext(), CorpGrabActivity.class);
                                            intent.putExtra("projectname", taskNewInfo.getProject_name());
                                            intent.putExtra("projectid", projectid);
                                            startActivityForResult(intent, 0);
                                        }
                                    }
                                } else {//外部链接
//                                    Intent intent = new Intent(getContext(), BrowserActivity.class);
//                                    intent.putExtra("content", taskNewInfo.getLink_url());
//                                    intent.putExtra("flag", "7");
//                                    startActivity(intent);
                                    Uri uri = Uri.parse(taskNewInfo.getLink_url());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public void settingDistric(String province, String city, String county, String id) {
        AppInfo.setCityName(getContext(), province, city, county);
        if (TextUtils.isEmpty(county) || "null".equals(county)) {
            tasknew_distric.setText(city);
        } else {
            tasknew_distric.setText(county + "-" + province + "-" + city);
        }
    }

    private OnShowCalendarListener onShowCalendarListener;

    public interface OnShowCalendarListener {
        void showCalendar();

    }

    public void setOnShowCalendarListener(OnShowCalendarListener onShowCalendarListener) {
        this.onShowCalendarListener = onShowCalendarListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tasknew_citysearch: {
                if (onCitysearchClickListener != null) {
                    onCitysearchClickListener.clickforTask();
                }
            }
            break;
            case R.id.setting_more: {
                AppInfo.setJPush(getContext(), false);
                setting_more.setImageResource(R.mipmap.task_message);
//                Intent intent = new Intent(getContext(), NewMessageActivity.class);
//                intent.putExtra("latitude", latitude);
//                intent.putExtra("longitude", longitude);
//                intent.putExtra("address", address);
//                intent.putExtra("province", province);
//                intent.putExtra("city", city);
//                startActivity(intent);
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
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("address", address);
                intent.putExtra("province", province);
                intent.putExtra("city", city);
                startActivity(intent);
//                mTopRightMenu = new TopRightMenu(getActivity());
//                ArrayList<MenuItem> menuItems = new ArrayList<>();
//                menuItems.add(new MenuItem(R.mipmap.setting_message, "消息"));
//                menuItems.add(new MenuItem(R.mipmap.setting_scan, "扫一扫"));
//                menuItems.add(new MenuItem(R.mipmap.setting_location, "地图查看"));
//                float ratioWidth = (float) Tools.getScreeInfoWidth(getContext()) / 720;
//                float ratioHeight = (float) Tools.getScreeInfoHeight(getContext()) / 1080;
//                float ratioMetrics = Math.min(ratioWidth, ratioHeight);
//                Tools.d("----" + ratioMetrics);
//                mTopRightMenu
//                        .setHeight((int) (268 * ratioMetrics))     //默认高度
//                        .setWidth((int) (266 * ratioMetrics))      //默认宽度wrap_content
//                        .showIcon(true)     //显示菜单图标，默认为true
//                        .dimBackground(true)           //背景变暗，默认为true
//                        .addMenuList(menuItems)
//                        .showAsDropDown(setting_more, -150, 0);
//                mTopRightMenu.setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
//                    @Override
//                    public void onMenuItemClick(int position) {
//                        if (position == 0) {//消息
//                            startActivity(new Intent(getContext(), MessageActivity.class));
//                        } else if (position == 1) {//扫一扫
//                            if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
//                                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
//                                    ConfirmDialog.showDialog(getContext(), "该功能需登录后方能操作", null, "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
//                                        @Override
//                                        public void leftClick(Object object) {
//                                        }
//
//                                        @Override
//                                        public void rightClick(Object object) {
//                                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
//                                            intent.putExtra("nologin", "1");
//                                            startActivityForResult(intent, 0);
//                                        }
//                                    });
//                                    return;
//                                }
//                            }
//                            Intent intent = new Intent(getContext(), CaptureActivity.class);
//                            intent.putExtra("flag", "0");//首页扫一扫
//                            startActivity(intent);
//                        } else if (position == 2) {//地图查看
//                            Intent intent = new Intent(getContext(), TaskLocationActivity.class);
//                            intent.putExtra("city", tasknew_distric.getText());
//                            startActivity(intent);
//                        }
//                    }
//                });
            }
            break;
            case R.id.newhand_taskimg: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    intent.putExtra("nologin", "1");
                                    startActivityForResult(intent, 0);
                                }
                            });
                    return;
                }
                final TaskNewInfo taskNewInfo = list_index_datas.get(0);
                final String type = taskNewInfo.getType();
                if (appDBHelper.getIsShow(taskNewInfo.getId()) && "1".equals(taskNewInfo.getStandard_state())) {
                    Intent intent = new Intent(getContext(), TaskillustratesActivity.class);
                    intent.putExtra("project_person", taskNewInfo.getProject_person());
                    intent.putExtra("projectname", taskNewInfo.getProject_name());
                    intent.putExtra("projectid", taskNewInfo.getId());
                    intent.putExtra("id", taskNewInfo.getId());
                    intent.putExtra("project_name", taskNewInfo.getProject_name());
                    intent.putExtra("project_code", taskNewInfo.getProject_code());
                    intent.putExtra("project_type", taskNewInfo.getProject_type());
                    intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                    intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                    intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                    intent.putExtra("end_date", taskNewInfo.getEnd_date());
                    intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                    intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                    intent.putExtra("code", taskNewInfo.getCode());
                    intent.putExtra("brand", taskNewInfo.getBrand());
                    intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                    intent.putExtra("show_type", taskNewInfo.getShow_type());
                    intent.putExtra("check_time", taskNewInfo.getCheck_time());
                    intent.putExtra("project_property", taskNewInfo.getProject_property());
                    intent.putExtra("city", tasknew_distric.getText().toString());
                    intent.putExtra("type", taskNewInfo.getType());
                    intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                    intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                    intent.putExtra("longitude", longitude + "");
                    intent.putExtra("latitude", latitude + "");
                    intent.putExtra("address", address + "");
                    intent.putExtra("province", province);
                    intent.putExtra("isHomePage", "1");//是否是首页传过来的 1首页 0我的任务列表 2地图
                    startActivity(intent);
                } else {
                    checkapply.sendPostRequest(Urls.CheckApply, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (!TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                                    if (jsonObject.getInt("code") == 200) {
                                        if ("1".equals(type)) {
                                            Intent intent = new Intent(getContext(), TaskGrabActivity.class);
                                            intent.putExtra("id", taskNewInfo.getId());
                                            intent.putExtra("project_person", taskNewInfo.getProject_person());
                                            intent.putExtra("project_name", taskNewInfo.getProject_name());
                                            intent.putExtra("project_code", taskNewInfo.getProject_code());
                                            intent.putExtra("project_type", taskNewInfo.getProject_type());
                                            intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                            intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                            intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                            intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                            intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                            intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                            intent.putExtra("code", taskNewInfo.getCode());
                                            intent.putExtra("brand", taskNewInfo.getBrand());
                                            intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                            intent.putExtra("type", taskNewInfo.getType());
                                            intent.putExtra("show_type", taskNewInfo.getShow_type());
                                            intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                            intent.putExtra("project_property", taskNewInfo.getProject_property());
                                            intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                            intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                            intent.putExtra("city", tasknew_distric.getText().toString());
                                            intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                            intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                            intent.putExtra("province", province);
                                            intent.putExtra("type1", "0");//首页跳转
                                            startActivity(intent);
                                        } else if ("4".equals(type)) {
                                            Intent intent = new Intent(getContext(), ExperienceLocationActivity.class);
                                            intent.putExtra("id", taskNewInfo.getId());
                                            intent.putExtra("project_person", taskNewInfo.getProject_person());
                                            intent.putExtra("project_name", taskNewInfo.getProject_name());
                                            intent.putExtra("project_code", taskNewInfo.getProject_code());
                                            intent.putExtra("project_type", taskNewInfo.getProject_type());
                                            intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                            intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                            intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                            intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                            intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                            intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                            intent.putExtra("code", taskNewInfo.getCode());
                                            intent.putExtra("brand", taskNewInfo.getBrand());
                                            intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                            intent.putExtra("type", taskNewInfo.getType());
                                            intent.putExtra("show_type", taskNewInfo.getShow_type());
                                            intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                            intent.putExtra("project_property", taskNewInfo.getProject_property());
                                            intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                            intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                            intent.putExtra("city", tasknew_distric.getText().toString());
                                            intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                            intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                            startActivity(intent);
                                        }
                                    } else if (jsonObject.getInt("code") == 2) {//点击进入招募令
                                        Intent intent = new Intent(getContext(), ProjectRecruitmentActivity.class);
                                        intent.putExtra("projectid", projectid);
                                        startActivity(intent);
                                    } else {
                                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                                    }
                                } else {
                                    if (jsonObject.getInt("code") == 200) {
                                        if ("1".equals(type)) {
                                            Intent intent = new Intent(getContext(), TaskGrabActivity.class);
                                            intent.putExtra("id", taskNewInfo.getId());
                                            intent.putExtra("project_person", taskNewInfo.getProject_person());
                                            intent.putExtra("project_name", taskNewInfo.getProject_name());
                                            intent.putExtra("project_code", taskNewInfo.getProject_code());
                                            intent.putExtra("project_type", taskNewInfo.getProject_type());
                                            intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                            intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                            intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                            intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                            intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                            intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                            intent.putExtra("code", taskNewInfo.getCode());
                                            intent.putExtra("brand", taskNewInfo.getBrand());
                                            intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                            intent.putExtra("type", taskNewInfo.getType());
                                            intent.putExtra("show_type", taskNewInfo.getShow_type());
                                            intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                            intent.putExtra("project_property", taskNewInfo.getProject_property());
                                            intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                            intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                            intent.putExtra("city", tasknew_distric.getText().toString());
                                            intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                            intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                            intent.putExtra("province", province);
                                            intent.putExtra("type1", "0");//首页跳转
                                            startActivity(intent);
                                        } else if ("4".equals(type)) {
                                            Intent intent = new Intent(getContext(), ExperienceLocationActivity.class);
                                            intent.putExtra("id", taskNewInfo.getId());
                                            intent.putExtra("project_person", taskNewInfo.getProject_person());
                                            intent.putExtra("project_name", taskNewInfo.getProject_name());
                                            intent.putExtra("project_code", taskNewInfo.getProject_code());
                                            intent.putExtra("project_type", taskNewInfo.getProject_type());
                                            intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                            intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                            intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                            intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                            intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                            intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                            intent.putExtra("code", taskNewInfo.getCode());
                                            intent.putExtra("brand", taskNewInfo.getBrand());
                                            intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                            intent.putExtra("type", taskNewInfo.getType());
                                            intent.putExtra("show_type", taskNewInfo.getShow_type());
                                            intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                            intent.putExtra("project_property", taskNewInfo.getProject_property());
                                            intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                            intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                            intent.putExtra("city", tasknew_distric.getText().toString());
                                            intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                            intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                            startActivity(intent);
                                        }
                                    } else if (jsonObject.getInt("code") == 1) {//点击进入招募令
                                        Intent intent = new Intent(getContext(), ProjectRecruitmentActivity.class);
                                        intent.putExtra("projectid", projectid);
                                        startActivity(intent);
                                    }
                                }
                            } catch (JSONException e) {
                                Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                        }
                    }, null);
                }
            }
            break;
            case R.id.withdraw_layout: {
                startActivity(new Intent(getContext(), NewestWithdrawsActivity.class));
            }
            break;
            case R.id.task_location: {
                Intent intent = new Intent(getContext(), TaskLocationActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.titlenew_name: {
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    ConfirmDialog.showDialog(getContext(), null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                                    intent.putExtra("nologin", "1");
                                    startActivityForResult(intent, 0);
                                }
                            });
                } else {
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("province", province);
                    startActivity(intent);
                }
            }
            break;
        }
    }
}
