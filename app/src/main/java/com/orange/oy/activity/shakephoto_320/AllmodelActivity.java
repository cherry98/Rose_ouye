package com.orange.oy.activity.shakephoto_320;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.TaskContentActivity;
import com.orange.oy.activity.createtask_317.TaskMouldActivity;
import com.orange.oy.activity.shakephoto_316.CollectPhotoActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.AllmodelCategoryInfo;
import com.orange.oy.info.AllmodelTemplateInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
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
 * Created by Administrator on 2018/8/22.
 * 全部模板
 */

public class AllmodelActivity extends BaseActivity {
    private PullToRefreshListView model_listview;
    private LinearLayout model_horizontalScrollView;
    private ArrayList<AllmodelCategoryInfo> categorylist = new ArrayList<>();
    private ArrayList<AllmodelTemplateInfo> templateList;
    private NetworkConnection TemplateList, SponsorshipTemplateList;
    private MyAdapter myAdapter;
    private int selPosition = 0;//当前选择的分类下标
    private String state;//1:全部模板，2：赞助模板需返回id

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allmodel);
        state = getIntent().getStringExtra("state");
        imageLoader = new ImageLoader(this);
        AppTitle model_titile = (AppTitle) findViewById(R.id.model_titile);
        model_titile.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        model_titile.showIllustrate(R.mipmap.kefu, new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(AllmodelActivity.this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(AllmodelActivity.this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(AllmodelActivity.this));
                    info.setUname(AppInfo.getUserName(AllmodelActivity.this));
                }
                SobotApi.startSobotChat(AllmodelActivity.this, info);
            }
        });
        TemplateList = new NetworkConnection(AllmodelActivity.this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AllmodelActivity.this));
                return params;
            }
        };
        SponsorshipTemplateList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AllmodelActivity.this));
                params.put("page", page + "");
                return params;
            }
        };
        model_horizontalScrollView = (LinearLayout) findViewById(R.id.model_horizontalScrollView);
        model_listview = (PullToRefreshListView) findViewById(R.id.model_listview);
        if ("1".equals(state)) {
            model_titile.settingName("全部模板");
            model_listview.setMode(PullToRefreshBase.Mode.DISABLED);
            model_listview.setOnItemClickListener(onItemClickListener);
            getData();
        } else if ("2".equals(state)) {
            model_titile.settingName("任务模板选择");
            model_listview.setMode(PullToRefreshBase.Mode.BOTH);
            model_listview.setOnItemClickListener(onItemClickListener);
            model_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    page = 1;
                    getSponsorshipData();
                }

                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    page++;
                    getSponsorshipData();
                }
            });
            findViewById(R.id.model_horizontalScrollView_bg).setVisibility(View.GONE);
            findViewById(R.id.model_line).setVisibility(View.GONE);
            getSponsorshipData();
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AllmodelTemplateInfo allmodelTemplateInfo = templateList.get(position - 1);
            if ("1".equals(allmodelTemplateInfo.getTemplate_type())) {//类型，1为集图活动，2为甩投任务
                Intent intent = new Intent(AllmodelActivity.this, CollectPhotoActivity.class);
                intent.putExtra("template_id", allmodelTemplateInfo.getTemplate_id());
                intent.putExtra("which_page", "3");
                startActivity(intent);
            } else if ("2".equals(allmodelTemplateInfo.getTemplate_type())) {
                Intent intent = new Intent(AllmodelActivity.this, TaskMouldActivity.class);
                intent.putExtra("which_page", "3");
                intent.putExtra("template_id", allmodelTemplateInfo.getTemplate_id());
                startActivity(intent);
            } else {
                if ("2".equals(state)) {
                    Intent intent = new Intent(AllmodelActivity.this, TaskContentActivity.class);
                    intent.putExtra("state", "4");
                    intent.putExtra("template_id", allmodelTemplateInfo.getTemplate_id());
                    startActivityForResult(intent, toTaskContentActivity);
                }
            }
        }
    };
    private final int toTaskContentActivity = 0x100;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case toTaskContentActivity: {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    baseFinish();
                }
            }
            break;
        }
    }

    private int page = 1;

    //获取赞助活动任务模板列表
    private void getSponsorshipData() {
        SponsorshipTemplateList.sendPostRequest(Urls.SponsorshipTemplateList, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        if (jsonObject != null) {
                            JSONArray template_list = jsonObject.optJSONArray("template_list");
                            if (template_list != null) {
                                if (page == 1) {
                                    if (templateList == null) {
                                        templateList = new ArrayList<>();
                                    }
                                    templateList.clear();
                                }
                                int size = template_list.length();
                                for (int j = 0; j < size; j++) {
                                    JSONObject js = template_list.optJSONObject(j);
                                    AllmodelTemplateInfo allmodelTemplateInfo = new AllmodelTemplateInfo();
                                    allmodelTemplateInfo.setTemplate_id(js.optString("template_id"));
                                    allmodelTemplateInfo.setTemplate_img(js.optString("template_img"));
                                    allmodelTemplateInfo.setTemplate_name(js.optString("template_name"));
                                    allmodelTemplateInfo.setTemplate_type(js.optString("template_type"));
                                    templateList.add(allmodelTemplateInfo);
                                }
                                model_listview.onRefreshComplete();
                                if (myAdapter == null) {
                                    myAdapter = new MyAdapter();
                                    model_listview.setAdapter(myAdapter);
                                }
                                myAdapter.notifyDataSetChanged();
                                if (size < 15) {
                                    model_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    model_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                            }
                        }
                        model_listview.onRefreshComplete();
                    } else {
                        Tools.showToast(AllmodelActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AllmodelActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AllmodelActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    //获取全部模板列表
    private void getData() {
        TemplateList.sendPostRequest(Urls.TemplateList, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        if (jsonObject != null) {
                            JSONArray category_list = jsonObject.optJSONArray("category_list");
                            if (category_list != null) {
                                int length = category_list.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject temp = category_list.getJSONObject(i);
                                    AllmodelCategoryInfo allmodelCategoryInfo = new AllmodelCategoryInfo();
                                    allmodelCategoryInfo.setCategory_name(temp.optString("category_name"));
                                    allmodelCategoryInfo.setIs_recommend(temp.optString("is_recommend"));
                                    ArrayList<AllmodelTemplateInfo> templateInfos = allmodelCategoryInfo.getTemplate_list();
                                    JSONArray template_list = temp.optJSONArray("template_list");
                                    if (template_list != null) {
                                        int size = template_list.length();
                                        for (int j = 0; j < size; j++) {
                                            JSONObject js = template_list.optJSONObject(j);
                                            AllmodelTemplateInfo allmodelTemplateInfo = new AllmodelTemplateInfo();
                                            allmodelTemplateInfo.setTemplate_id(js.optString("template_id"));
                                            allmodelTemplateInfo.setTemplate_img(js.optString("template_img"));
                                            allmodelTemplateInfo.setTemplate_name(js.optString("template_name"));
                                            allmodelTemplateInfo.setTemplate_type(js.optString("template_type"));
                                            templateInfos.add(allmodelTemplateInfo);
                                        }
                                    }
                                    categorylist.add(allmodelCategoryInfo);
                                }
                                addCategoryTab();
                                selPosition = 0;
                                categoryTabClickListener.onClick(categorylist.get(0).getBindView());
                            }
                        }
                    } else {
                        Tools.showToast(AllmodelActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AllmodelActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AllmodelActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private ImageLoader imageLoader;

    private class MyAdapter extends BaseAdapter {

        public int getCount() {
            return templateList.size();
        }

        public Object getItem(int position) {
            return templateList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                convertView = Tools.loadLayout(AllmodelActivity.this, R.layout.item_allmodel);
                viewHold = new ViewHold();
                viewHold.textView = (TextView) convertView.findViewById(R.id.itemamodel_txt);
                viewHold.imageView = (ImageView) convertView.findViewById(R.id.itemamodel_img);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            AllmodelTemplateInfo allmodelTemplateInfo = templateList.get(position);
            if ("1".equals(allmodelTemplateInfo.getTemplate_type())) {
                if ("null".equals(allmodelTemplateInfo.getTemplate_img()) || TextUtils.isEmpty(allmodelTemplateInfo.getTemplate_img())) {
                    viewHold.imageView.setImageResource(R.mipmap.ssfrw_button_ji);
                } else {
                    imageLoader.DisplayImage(Urls.ImgIp + allmodelTemplateInfo.getTemplate_img(), viewHold.imageView,
                            R.mipmap.ssfrw_button_ji);
                }
            } else {
                if ("null".equals(allmodelTemplateInfo.getTemplate_img()) || TextUtils.isEmpty(allmodelTemplateInfo.getTemplate_img())) {
                    viewHold.imageView.setImageResource(R.mipmap.round_pai);
                } else {
                    imageLoader.DisplayImage(Urls.ImgIp + allmodelTemplateInfo.getTemplate_img(), viewHold.imageView,
                            R.mipmap.round_pai);
                }
            }
            viewHold.textView.setText(allmodelTemplateInfo.getTemplate_name());
            return convertView;
        }
    }

    private class ViewHold {
        ImageView imageView;
        TextView textView;
    }

    //添加分类tab
    private void addCategoryTab() {
        int size = categorylist.size();
        int margin = Tools.dipToPx(AllmodelActivity.this, 12);
        for (int i = 0; i < size; i++) {
            AllmodelCategoryInfo allmodelCategoryInfo = categorylist.get(i);
            MyTextView myTextView = new MyTextView(this);
            myTextView.setTag(i);
            myTextView.setText(allmodelCategoryInfo.getCategory_name());
            myTextView.setOnClickListener(categoryTabClickListener);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.leftMargin = margin;
            lp.rightMargin = margin;
            model_horizontalScrollView.addView(myTextView, lp);
            allmodelCategoryInfo.setBindView(myTextView);
        }
    }

    private View.OnClickListener categoryTabClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ((MyTextView) categorylist.get(selPosition).getBindView()).selState(false);
            selPosition = (Integer) v.getTag();
            AllmodelCategoryInfo allmodelCategoryInfo = categorylist.get(selPosition);
            ((MyTextView) allmodelCategoryInfo.getBindView()).selState(true);
            templateList = allmodelCategoryInfo.getTemplate_list();
            if (myAdapter == null) {
                myAdapter = new MyAdapter();
                model_listview.setAdapter(myAdapter);
            }
            myAdapter.notifyDataSetChanged();
        }
    };

    private class MyTextView extends LinearLayout {
        private TextView textView;
        private View bottomView;

        public MyTextView(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);
            textView = new TextView(context);
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(0xFFA0A0A0);
            addView(textView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
            bottomView = new View(context);
            bottomView.setBackgroundColor(0xFFF65D57);
            addView(bottomView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Tools.dipToPx(AllmodelActivity.this, 2)));
            bottomView.setVisibility(INVISIBLE);
        }

        public void setText(String str) {
            textView.setText(str);
        }

        public void selState(boolean sel) {
            if (sel) {
                textView.setTextColor(0xFFF65D57);
                bottomView.setVisibility(VISIBLE);
            } else {
                textView.setTextColor(0xFFA0A0A0);
                bottomView.setVisibility(INVISIBLE);
            }
        }
    }
}
