package com.orange.oy.activity.shakephoto_316;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.DetailsAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.DetailsInfo;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.PullToRefreshLayout;
import com.orange.oy.view.recyclerview.SpaceItem;
import com.orange.oy.view.recyclerview.YRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/***
 * beibei 活动详情页面
 */
public class DetailsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, DetailsAdapter.OnItemClickListener {
    private String ai_id;
    private ArrayList<DetailsInfo> list = new ArrayList<>();
    private String activity_name, target_num, begin_date, end_date, get_num, key_concent;
    private ArrayList<String> pic_tags = new ArrayList<>();
    private MyGridView mGridview;
    private PullToRefreshLayout refreshLayout;
    private Context context;
    private NetworkConnection activityDetail;
    private TextView task_name, task_target_num, task_time, task_getNum;
    private ImageLoader imageLoader;
    private int page = 1;   //每页18条
    private YRecyclerView recycleview;
    private DetailsAdapter myAdapter;


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mydetail_title);
        appTitle.settingName("活动详情");
        appTitle.showBack(this);
    }

    public void onStop() {
        super.onStop();
        if (activityDetail != null) {
            activityDetail.stop(Urls.ActivityDetail);
        }
    }


    private void initNetworkConnection() {

        activityDetail = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(DetailsActivity.this));
                params.put("ai_id", ai_id);  //	活动id【必传】
                params.put("page", page + "");
                Tools.d("tag", params.toString());
                return params;
            }
        };

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclyview);
        initTitle();
        initNetworkConnection();
        ai_id = getIntent().getStringExtra("ai_id");
        context = DetailsActivity.this;
        imageLoader = new ImageLoader(this);
        recycleview = (YRecyclerView) findViewById(R.id.recycleview);
        activityDetail();
        View headView1 = View.inflate(context, R.layout.head_details, null);
        task_name = (TextView) headView1.findViewById(R.id.task_name);
        task_target_num = (TextView) headView1.findViewById(R.id.task_target_num);
        task_time = (TextView) headView1.findViewById(R.id.task_time);
        task_getNum = (TextView) headView1.findViewById(R.id.task_getNum);
        mGridview = (MyGridView) headView1.findViewById(R.id.mGridview);

        myAdapter = new DetailsAdapter(list, context);
        myAdapter.setOnItemClickListener(this);
        recycleview.addHeadView(headView1);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.app_textsize_small3);
        recycleview.addItemDecoration(new SpaceItem(spacingInPixels));
        recycleview.setLayoutManager(new GridLayoutManager(context, 3));
        recycleview.setAdapter(myAdapter);
        recycleview.setRefreshAndLoadMoreListener(new YRecyclerView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                recycleview.setNoMoreData(false);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        page = 1;
                        activityDetail();
                        recycleview.setReFreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                //  Tools.d("加载更多000==========");
                if (length >= 18) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            page++;
                            activityDetail();
                            recycleview.setloadMoreComplete();
                        }
                    }, 1000);
                } else {
                    recycleview.setloadMoreComplete();
                }
            }
        });


    }


    private int length;


    @Override
    public void onBack() {
        baseFinish();
    }


    private void activityDetail() {
        largeImagePageInfos = null;
        activityDetail.sendPostRequest(Urls.ActivityDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            if (!list.isEmpty()) {
                                list.clear();
                            }
                            if (recycleview != null) {
                                recycleview.reSetStatus();
                            }
                        }
                        JSONObject object = jsonObject.optJSONObject("data").optJSONObject("activity_info");
                        activity_name = object.optString("activity_name");
                        target_num = object.optString("target_num");
                        begin_date = object.optString("begin_date");
                        end_date = object.optString("end_date");
                        get_num = object.optString("get_num"); //共接收照片数量
                        if (!Tools.isEmpty(activity_name)) {
                            task_name.setText(activity_name);
                        }
                        if (!Tools.isEmpty(target_num)) {
                            task_target_num.setText("目标参与人数：" + target_num + "人");
                        }
                        if (!Tools.isEmpty(begin_date) && !Tools.isEmpty(end_date)) {
                            task_time.setText("活动起止日期：" + begin_date + "~" + end_date);
                        }
                        if (!Tools.isEmpty(get_num)) {
                            task_getNum.setText(get_num);
                        }
                        if (!jsonObject.isNull("data")) {
                            JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("photo_list");
                            length = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                DetailsInfo detailsInfo = new DetailsInfo();
                                JSONObject object2 = jsonArray.getJSONObject(i);
                                detailsInfo.setAitivity_name(object2.getString("aitivity_name"));
                                detailsInfo.setCreate_time(object2.getString("create_time"));
                                detailsInfo.setAddress(object2.getString("address"));
                                detailsInfo.setProvince(object2.getString("province"));
                                detailsInfo.setCity(object2.getString("city"));
                                detailsInfo.setCounty(object2.getString("county"));
                                detailsInfo.setFi_id(object2.getString("fi_id"));  //文件ID
                                detailsInfo.setFile_url(object2.getString("file_url")); //原图文件地址
                                detailsInfo.setLongitude(object2.getString("longitude")); // 经度
                                detailsInfo.setLatitude(object2.getString("latitude")); // 纬度
                                detailsInfo.setUser_name(object2.getString("user_name"));
                                // JSONArray jsonArray2 = object2.optJSONArray("key_concent");
                                String key_concent = object2.getString("key_concent");   //常去地点
                                detailsInfo.setKey_concent(key_concent);
                                detailsInfo.setShow_address(object2.getString("show_address")); // 是否显示地址（0：不显示；1：显示）
                                list.add(detailsInfo);
                            }

                            length = list.size();
                            Tools.d("tag", "list.size======" + list.size());
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        if (jsonObject.isNull("data")) {
                            recycleview.setNoMoreData(true);
                        }
                        //Tools.showToast(DetailsActivity2.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    recycleview.reSetStatus();
                    Tools.showToast(DetailsActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                recycleview.reSetStatus();
                Tools.d(volleyError.toString());
                Tools.showToast(DetailsActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private ArrayList<LargeImagePageInfo> largeImagePageInfos;

    public void onItemClick(int pos) {
//        DetailsInfo detailsInfo = list.get(pos);
//        //查看大图页面
//        Intent intent = new Intent(context, LargeImagePageActivity.class);
//        intent.putExtra("IsHaveShare", "2");
//        intent.putExtra("IsHaveDelete", "1");
//        intent.putExtra("fi_id", detailsInfo.getFi_id());
//        intent.putExtra("key_concent", detailsInfo.getKey_concent());
//        intent.putExtra("file_url", detailsInfo.getFile_url());
//        intent.putExtra("address", detailsInfo.getAddress());
//        intent.putExtra("create_time", detailsInfo.getCreate_time());
//        intent.putExtra("show_address", detailsInfo.getShow_address());
//        intent.putExtra("aitivity_name", detailsInfo.getAitivity_name());
//        intent.putExtra("user_name", detailsInfo.getUser_name());
//        intent.putExtra("ai_id", ai_id);
//        startActivity(intent);
        if (list != null && !list.isEmpty()) {
            if (largeImagePageInfos == null) {
                largeImagePageInfos = new ArrayList<>();
                for (DetailsInfo shakePhotoInfo : list) {
                    LargeImagePageInfo largeImagePageInfo = new LargeImagePageInfo();
                    largeImagePageInfo.setKey_concent(shakePhotoInfo.getKey_concent());
                    largeImagePageInfo.setFile_url(shakePhotoInfo.getFile_url());
                    largeImagePageInfo.setAddress(shakePhotoInfo.getAddress());
                    largeImagePageInfo.setShow_address(shakePhotoInfo.getShow_address());
                    largeImagePageInfo.setCreate_time(shakePhotoInfo.getCreate_time());
                    largeImagePageInfo.setAitivity_name(shakePhotoInfo.getAitivity_name());
                    largeImagePageInfo.setIsHaveShare("2");
                    largeImagePageInfo.setIsHaveDelete("1");
                    largeImagePageInfo.setAi_id(ai_id);
                    largeImagePageInfo.setFi_id(shakePhotoInfo.getFi_id());
                    largeImagePageInfos.add(largeImagePageInfo);
                }
            }
            Intent intent = new Intent(this, LargeImagePageActivity.class);
            intent.putExtra("isList", true);
            intent.putExtra("list", largeImagePageInfos);
            intent.putExtra("position", pos);
            startActivity(intent);
        }
    }


}
