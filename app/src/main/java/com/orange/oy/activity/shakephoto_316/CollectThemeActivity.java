package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TeamSpecialtyInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.HorizontalListView;
import com.orange.oy.view.TagsView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的-->随手发任务-->集图活动->活动主题 V3.16
 */
public class CollectThemeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, TagsView.OnOtherClickListener, View.OnClickListener, TagsView.OnClick {


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.collecttheme_title);
        appTitle.settingName("活动主题");
        appTitle.showBack(this);
    }

    private TagsView collecttheme_tags;
    private TextView collecttheme_classify, collecttheme_tagsnum, tv_tags;
    private EditText collecttheme_theme;
    private String cat_id, theme_name;
    private NetworkConnection themeSytle;
    private HorizontalListView horizontalListView;
    private ArrayList<ThemeSytleInfo> list;
    private MyAdapter myAdapter;
    private ImageLoader imageLoader;
    private LinearLayout lin_tags;
    private String style_url, ts_id;
    private int currentItem = 0; //当前被选中的item位置

    private void initNetworkConnection() {
        themeSytle = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CollectThemeActivity.this));
                params.put("cat_id", cat_id); // 	分类id【必传】
                Tools.d(params.toString());
                return params;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_theme);
        list = new ArrayList<ThemeSytleInfo>();
        initTitle();
        initNetworkConnection();
        imageLoader = new ImageLoader(this);
        tv_tags = (TextView) findViewById(R.id.tv_tags);
        collecttheme_classify = (TextView) findViewById(R.id.collecttheme_classify);
        collecttheme_theme = (EditText) findViewById(R.id.collecttheme_theme);
        collecttheme_tagsnum = (TextView) findViewById(R.id.collecttheme_tagsnum);
        collecttheme_tags = (TagsView) findViewById(R.id.collecttheme_tags);
        horizontalListView = (HorizontalListView) findViewById(R.id.horizontalListView);
        lin_tags = (LinearLayout) findViewById(R.id.lin_tags);
        collecttheme_tags.setTeamSpecialtyDefaultLabels(new ArrayList<TeamSpecialtyInfo>());
        collecttheme_tags.setOnOtherClickListener(this, "请输入活动关键词，5个字以内", 10, 5, false);
        collecttheme_tags.setOnClick(this);
        findViewById(R.id.collecttheme_button).setOnClickListener(this);
        findViewById(R.id.collecttheme_classify_ly).setOnClickListener(this);
        lin_tags.setOnClickListener(this);
        myAdapter = new MyAdapter();
        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ThemeSytleInfo themeSytleInfo = list.get(position);
                style_url = themeSytleInfo.getStyle_url();
                ts_id = themeSytleInfo.getTs_id();
                //点击之后红色框
                currentItem = position; //重新赋值
                myAdapter.notifyDataSetChanged(); //通知adapter更新
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void clickOther() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collecttheme_classify_ly: {
                Intent intent = new Intent(this, ThemeClassifyActivity.class);
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.collecttheme_button: {
                if (TextUtils.isEmpty(collecttheme_classify.getText().toString().trim())) {
                    Tools.showToast(this, "请选择主题分类");
                    return;
                }
                if (TextUtils.isEmpty(collecttheme_theme.getText().toString().trim())) {
                    Tools.showToast(this, "请填写主题名称");
                    return;
                }
                if (index == 0) {
                    Tools.showToast(this, "请输入关键内容");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("key_concent", collecttheme_tags.getSelectLabelForNet()[1]);
                intent.putExtra("theme_name", theme_name);
                intent.putExtra("activity_name", collecttheme_theme.getText().toString().trim());
                intent.putExtra("cat_id", cat_id);
                intent.putExtra("style_url", style_url);
                intent.putExtra("ts_id", ts_id);
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();
            }
            break;
            case R.id.lin_tags: {
                if (TextUtils.isEmpty(collecttheme_classify.getText().toString().trim())) {
                    Tools.showToast(this, "请先选择主题分类");
                    return;
                }
            }
            break;
        }
    }

    private int index = 0;

    @Override
    public void clickPlus() {//增加
        index++;
        collecttheme_tagsnum.setText(index + "/10");
    }

    @Override
    public void clickMinus() {//减少
        index--;
        collecttheme_tagsnum.setText(index + "/10");
    }

    private void getData() {
        themeSytle.sendPostRequest(Urls.ThemeSytle, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<ThemeSytleInfo>();
                        } else {
                            list.clear();
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("style_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ThemeSytleInfo themeSytleInfo = new ThemeSytleInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                themeSytleInfo.setTs_id(object.getString("ts_id")); //奖品图片地址
                                themeSytleInfo.setStyle_url(object.getString("style_url"));//奖品名
                                list.add(themeSytleInfo);
                            }
                            if (null != myAdapter) {
                                myAdapter.notifyDataSetChanged();
                            }
                            horizontalListView.setVisibility(View.VISIBLE);
                            horizontalListView.setAdapter(myAdapter);
                        }
                    } else {
                        Tools.showToast(CollectThemeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CollectThemeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CollectThemeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_THEME && requestCode == 0) {
            if (data != null) {
                cat_id = data.getStringExtra("cat_id");
                theme_name = data.getStringExtra("theme_name");
                collecttheme_classify.setText(theme_name);
                getData();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != themeSytle) {
            themeSytle.stop(Urls.ThemeSytle);
        }
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(CollectThemeActivity.this, R.layout.item_tags_pic);
                viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                viewHolder.iv_pic2 = (ImageView) convertView.findViewById(R.id.iv_pic2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ThemeSytleInfo themeSytleInfo = list.get(position);


            if (position == currentItem) {     //点击之后红色框
                viewHolder.iv_pic2.setVisibility(View.VISIBLE);
                ts_id = list.get(currentItem).getTs_id();
                style_url = list.get(currentItem).getStyle_url();
            } else {
                viewHolder.iv_pic2.setVisibility(View.GONE);
            }
            imageLoader.DisplayImage(themeSytleInfo.getStyle_url(), viewHolder.iv_pic);
            return convertView;
        }

        private class ViewHolder {
            ImageView iv_pic, iv_pic2;
        }
    }

    class ThemeSytleInfo {

        /**
         * ts_id : 主题样式id
         * style_url : 主题样式url
         */

        private String ts_id;
        private String style_url;

        public String getTs_id() {
            return ts_id;
        }

        public void setTs_id(String ts_id) {
            this.ts_id = ts_id;
        }

        public String getStyle_url() {
            return style_url;
        }

        public void setStyle_url(String style_url) {
            this.style_url = style_url;
        }
    }
}
