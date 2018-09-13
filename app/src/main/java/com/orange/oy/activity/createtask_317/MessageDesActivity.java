package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.iv_pic;
import static com.orange.oy.R.id.putin_listview2;
import static com.orange.oy.R.id.putin_networkview;


/**
 * 消息---任务详情
 */
public class MessageDesActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.oumidetail_title);
        appTitle.settingName("任务详情");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.share2, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                //http://47.93.120.58:8031/ouye/mobile/shareProjectInfo?project_id=1&
                // usermobile=16767656564&sign=c2076a349685a12f2d623b0bd18a35801f80795680d2cc05
                UMShareDialog.showDialog(MessageDesActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        String webUrl = Urls.ShareProjectInfo + "?&project_id=" + project_id + "&usermobile=" +
                                AppInfo.getName(MessageDesActivity.this) + "&sign=" + sign;
                        MyUMShareUtils.umShare(MessageDesActivity.this, type, webUrl);
                    }
                });
            }
        });
    }

    private void initNetworkConnection() {
        shareMessageDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MessageDesActivity.this));
                params.put("project_id", project_id);
                params.put("page", page + "");
                return params;
            }
        };
        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                String key = "project_id=" + project_id + "&usermobile=" + AppInfo.getName(MessageDesActivity.this);
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", key);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shareMessageDetail != null) {
            shareMessageDetail.stop(Urls.ShareMessageDetail);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private PullToRefreshListView oumidetail_listview;
    private MyAdapter myAdapter;
    private NetworkConnection shareMessageDetail, Sign;
    private int page;
    private ArrayList<Messagelist> list;
    private String project_id, project_name, template_img, begin_date, end_date, project_total_money, check_time, standard_state, sign;
    private View headview; //头布局
    private TextView tv_name, tv_money, tv_time, tv_period;
    private ImageView tv_preview, iv_standard, iv_img;
    private NetworkView lin_Nodata;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_des);
        list = new ArrayList<>();
        imageLoader = new ImageLoader(this);
        initTitle();
        initNetworkConnection();
        project_id = getIntent().getStringExtra("project_id");
        oumidetail_listview = (PullToRefreshListView) findViewById(R.id.oumidetail_listview);
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        iv_img = (ImageView) findViewById(R.id.iv_img);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_period = (TextView) findViewById(R.id.tv_period);
        tv_preview = (ImageView) findViewById(R.id.tv_preview);
        iv_standard = (ImageView) findViewById(R.id.iv_standard);
        tv_period.setVisibility(View.INVISIBLE);


        myAdapter = new MyAdapter();
        oumidetail_listview.setAdapter(myAdapter);
        oumidetail_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });

    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private void getData() {
        shareMessageDetail.sendPostRequest(Urls.ShareMessageDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<Messagelist>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.optJSONObject("data");
                            JSONObject object2 = jsonObject.optJSONObject("project_info");

                            project_id = object2.getString("project_id");
                            project_name = object2.getString("project_name");
                            template_img = object2.getString("template_img");
                            begin_date = object2.getString("begin_date");
                            end_date = object2.getString("end_date");
                            project_total_money = object2.getString("project_total_money"); // 总金额
                            check_time = object2.getString("check_time"); // 审核周期
                            standard_state = object2.getString("standard_state"); // "是否有项目说明，1为有，0为没有"

                            imageLoader.DisplayImage(Urls.ImgIp + template_img, iv_img, R.mipmap.round_pai);
                            tv_name.setText(project_name);
                            tv_money.setText("任务总金额： " + project_total_money);
                            tv_time.setText("任务起止日期: " + begin_date + "~" + end_date);
                            tv_preview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //跳转至任务预览页面；
                                    Intent intent = new Intent(MessageDesActivity.this, TaskitemDetailActivity_12.class);
                                    intent.putExtra("id", project_id);//网点id
                                    intent.putExtra("projectname", project_name);
                                    intent.putExtra("store_name", "网点名称");
                                    intent.putExtra("store_num", "网点编号");
                                    intent.putExtra("province", "");
                                    intent.putExtra("city", "");
                                    intent.putExtra("project_id", project_id);
                                    intent.putExtra("photo_compression", "");
                                    intent.putExtra("is_record", "");
                                    intent.putExtra("is_watermark", "");//int
                                    intent.putExtra("code", "");
                                    intent.putExtra("brand", "");
                                    intent.putExtra("is_takephoto", "");//String
                                    intent.putExtra("project_type", "1");
                                    intent.putExtra("is_desc", "");
                                    intent.putExtra("index", "0");
                                    startActivity(intent);
                                }
                            });
                            iv_standard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //跳转至任务说明页面；
                                    Intent intent = new Intent(MessageDesActivity.this, TaskillustratesActivity.class);
                                    intent.putExtra("projectid", project_id);
                                    intent.putExtra("projectname", project_name);
                                    intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                                    startActivity(intent);
                                }
                            });

                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            if (null != jsonArray) {
                                lin_Nodata.setVisibility(View.GONE);
                                oumidetail_listview.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Messagelist messagelist = new Messagelist();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    messagelist.setOutlet_id(object.getString("outlet_id"));
                                    messagelist.setOutlet_name(object.getString("outlet_name"));
                                    messagelist.setUser_name(object.getString("user_name"));
                                    messagelist.setUser_mobile(object.getString("user_mobile"));
                                    messagelist.setComplete_time(object.getString("complete_time"));
                                    list.add(messagelist);
                                }

                                oumidetail_listview.onRefreshComplete();
                                if (jsonArray.length() < 15) {
                                    oumidetail_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    oumidetail_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                if (myAdapter != null) {
                                    myAdapter.notifyDataSetChanged();
                                }
                            } else {
                                lin_Nodata.setVisibility(View.VISIBLE);
                                oumidetail_listview.setVisibility(View.GONE);
                                lin_Nodata.SettingMSG(R.mipmap.grrw_image, "没有消息哦~");

                            }
                        }
                        Sign();
                    } else {
                        Tools.showToast(MessageDesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MessageDesActivity.this, getResources().getString(R.string.network_error));
                }
                oumidetail_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                oumidetail_listview.onRefreshComplete();
                Tools.showToast(MessageDesActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void Sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                    } else {
                        Tools.showToast(MessageDesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MessageDesActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(MessageDesActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(MessageDesActivity.this, R.layout.item_messagedes);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.item_time);
                viewHolder.item_looked = (TextView) convertView.findViewById(R.id.item_looked);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Messagelist messagelist = list.get(position);

            if (!Tools.isEmpty(messagelist.getOutlet_name())) {
                viewHolder.tv_name.setText(messagelist.getOutlet_name());
            }
            if (!Tools.isEmpty(messagelist.getUser_name())) {
                viewHolder.tv_username.setText("用户昵称:  " + messagelist.getUser_name());

            }
            if (!Tools.isEmpty(messagelist.getComplete_time())) {
                viewHolder.tv_time.setText("执行时间： " + messagelist.getComplete_time());
            }
            return convertView;
        }

        class ViewHolder {
            TextView tv_name, tv_username, tv_time, item_looked;
        }
    }

    class Messagelist {
        /**
         * outlet_id : 网点id
         * outlet_name : 网点名称
         * user_name : 用户昵称
         * user_mobile : 用户账号
         * complete_time : 完成时间
         */

        private String outlet_id;
        private String outlet_name;
        private String user_name;
        private String user_mobile;
        private String complete_time;

        public String getOutlet_id() {
            return outlet_id;
        }

        public void setOutlet_id(String outlet_id) {
            this.outlet_id = outlet_id;
        }

        public String getOutlet_name() {
            return outlet_name;
        }

        public void setOutlet_name(String outlet_name) {
            this.outlet_name = outlet_name;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_mobile() {
            return user_mobile;
        }

        public void setUser_mobile(String user_mobile) {
            this.user_mobile = user_mobile;
        }

        public String getComplete_time() {
            return complete_time;
        }

        public void setComplete_time(String complete_time) {
            this.complete_time = complete_time;
        }
    }
}
