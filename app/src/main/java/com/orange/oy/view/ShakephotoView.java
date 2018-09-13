package com.orange.oy.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.allinterface.ShakephotoListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.ThemeInfo;
import com.orange.oy.info.shakephoto.ShakeThemeInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/14.
 * 甩图分类view
 */

public class ShakephotoView extends LinearLayout {
    public ShakephotoView(Context context) {
        this(context, null);
    }

    public ShakephotoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShakephotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Tools.loadLayout(this, R.layout.view_shakephoto);
        viewshakephoto_viewpager = (ViewPager) findViewById(R.id.viewshakephoto_viewpager);
        viewshakephoto_horizontalScrollView = (LinearLayout) findViewById(R.id.viewshakephoto_horizontalScrollView);
        viewshakephoto_classify_point = (TextView) findViewById(R.id.viewshakephoto_classify_point);
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                int size = pagelist.size();
                if (size > 1) {
                    viewshakephoto_classify_point.setText((position + 1) + "/" + size);
                } else {
                    viewshakephoto_classify_point.setText("");
                }
                if (gridviewAdapter1 != null)
                    gridviewAdapter1.notifyDataSetChanged();
                if (gridviewAdapter2 != null)
                    gridviewAdapter2.notifyDataSetChanged();
                if (gridviewAdapter3 != null)
                    gridviewAdapter3.notifyDataSetChanged();
            }

            public void onPageScrollStateChanged(int state) {
            }
        };
        viewshakephoto_viewpager.addOnPageChangeListener(onPageChangeListener);
        activityListByTheme = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("cat_id", cat_id);
                return params;
            }
        };
        activityListByTheme.setIsShowDialog(true);
    }

    private GridView getGridview() {
        GridView gridView = new GridView(getContext());
        gridView.setHorizontalSpacing((int) getResources().getDimension(R.dimen.shakephotoview_hspacing));
        gridView.setVerticalSpacing((int) getResources().getDimension(R.dimen.shakephotoview_vspacing));
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(onItemClickListener);
        return gridView;
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (view.getTag() != null) {
                int currentItem = viewshakephoto_viewpager.getCurrentItem();
//                int currentPosition = 0;
//                if (currentItem > 0) {
//                    currentPosition = position + 9;//其实大于4就行，主要用于后面判断
//                } else {
//                    currentPosition = position;
//                }
//                ShakeThemeInfo[] shakeThemeInfos = pagelist.get(0);
//                int length;
//                boolean isNor = true;
//                int selPostion;
//                if (shakeThemeInfos.length > 5) {
//                    if (currentPosition > 4) {
//                        isNor = false;
//                        length = 4;
//                    }
//                    length = 5;
//                } else {
//                    length = shakeThemeInfos.length;
//                }
//                ShakeThemeInfo[] shakeThemeInfos1 = new ShakeThemeInfo[length];
//                for (int i = 0; i < length; i++) {
//                    shakeThemeInfos1[i] = shakeThemeInfos[i];
//                }
//                if (!isNor) {
//                    shakeThemeInfos1[4] = pagelist.get(currentItem)[position];
//                    selPostion = 4;
//                } else {
//                    selPostion = currentPosition;
//                }
                int size;
                int pageSize = pagelist.size();
                size = (pageSize - 1) * 9 + pagelist.get(pageSize - 1).length;
                ShakeThemeInfo[] shakeThemeInfos1 = new ShakeThemeInfo[size];
                for (int i = 0, j = 0; i < pageSize; i++) {
                    ShakeThemeInfo[] shakeThemeInfos = pagelist.get(i);
                    for (ShakeThemeInfo shakeThemeInfo : shakeThemeInfos) {
                        shakeThemeInfos1[j++] = shakeThemeInfo;
                    }
                }
                if (shakephotoListener != null) {
                    shakephotoListener.ThemesSelectListener(shakeThemeInfos1, position + (9 * currentItem));
                }
//                Tools.showToast(getContext(), shakeThemeInfos[position].getActivity_name());
            }
        }
    };

    public void stopNet() {
        if (activityListByTheme != null) {
            activityListByTheme.stop(Urls.ActivityListByTheme);
        }
    }

    public void setShakephotoListener(ShakephotoListener shakephotoListener) {
        this.shakephotoListener = shakephotoListener;
    }

    private ShakephotoListener shakephotoListener;
    private String cat_id = "";
    private ViewPager viewshakephoto_viewpager;
    private LinearLayout viewshakephoto_horizontalScrollView;
    private GridView gridView1, gridView2, gridView3;
    private GridviewAdapter gridviewAdapter1, gridviewAdapter2, gridviewAdapter3;
    private NetworkConnection activityListByTheme;
    private ArrayList<ThemeInfo> themeInfos;
    private TextView viewshakephoto_classify_point;

    public ArrayList<ThemeInfo> getThemeInfos() {
        return themeInfos;
    }

    public void settingData(ArrayList<ThemeInfo> themeInfos) {
        stopNet();
        viewshakephoto_horizontalScrollView.removeAllViews();
        this.themeInfos = themeInfos;
        int size = themeInfos.size();
        View firshView = null;
        for (int i = 0; i < size; i++) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ThemeInfo themeInfo = themeInfos.get(i);
            TextView textView = getTextView(themeInfo.theme_name);
            textView.setTag(i);
            if (firshView == null) {
                firshView = textView;
            }
            viewshakephoto_horizontalScrollView.addView(textView, lp);
        }
        gridView1 = getGridview();
        gridView2 = getGridview();
        gridView3 = getGridview();
        if (firshView != null)
            textviewClick.onClick(firshView);
    }

    private TextView getTextView(String str) {
        TextView textView = new TextView(getContext());
        textView.setText(str);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setPadding((int) getResources().getDimension(R.dimen.shakephotoview_btheme_textview_lpadding), 0,
                (int) getResources().getDimension(R.dimen.shakephotoview_btheme_textview_lpadding), 0);
        textView.setOnClickListener(textviewClick);
        textView.setGravity(Gravity.CENTER);
        textView.setMinWidth(80);
        return textView;
    }

    private int themePosition;
    private OnClickListener textviewClick = new OnClickListener() {
        public void onClick(View v) {
            themePosition = (int) v.getTag();
            int length = viewshakephoto_horizontalScrollView.getChildCount();
            for (int i = 0; i < length; i++) {
                TextView view = (TextView) viewshakephoto_horizontalScrollView.getChildAt(i);
                if (i == themePosition) {
                    view.setTextColor(0xFFF65D57);
                } else {
                    view.setTextColor(Color.WHITE);
                }
            }
            ThemeInfo themeInfo = themeInfos.get(themePosition);
            if (themeInfo.ShakeThemeInfos == null) {
                cat_id = themeInfo.cat_id;
                getData();
            } else {
                settingPageview(themeInfo);
            }
        }
    };

    private void settingPageview(ThemeInfo themeInfo) {
        if (pagelist == null) {
            pagelist = new ArrayList<>();
        } else {
            pagelist.clear();
        }
        if (myPageAdapter == null) {
            myPageAdapter = new MyPageAdapter();
            viewshakephoto_viewpager.setAdapter(myPageAdapter);
        } else {
            myPageAdapter.notifyDataSetChanged();
        }
        int size = themeInfo.ShakeThemeInfos.size();
        double length = Math.ceil(size / 9d);
        for (int j = 0, i = 0; j < length && i < size; j++) {
            ShakeThemeInfo[] shakeThemeInfos = new ShakeThemeInfo[9];
            for (int index = 0; index < 9 && i < size; index++) {
                ShakeThemeInfo shakeThemeInfo = themeInfo.ShakeThemeInfos.get(i++);
                shakeThemeInfo.setActivity_name(shakeThemeInfo.getActivity_name());
                shakeThemeInfos[index] = shakeThemeInfo;
            }
            pagelist.add(shakeThemeInfos);
        }
        myPageAdapter.notifyDataSetChanged();
        viewshakephoto_viewpager.setCurrentItem(0);
        int pagesize = pagelist.size();
        if (pagesize > 1) {
            viewshakephoto_classify_point.setText("1/" + pagesize);
        } else {
            viewshakephoto_classify_point.setText("");
        }
        if (!pagelist.isEmpty()) {
            if (gridviewAdapter1 != null) {
                gridviewAdapter1.upList(pagelist.get(0));
                gridviewAdapter1.notifyDataSetChanged();
            } else {
                gridviewAdapter1 = new GridviewAdapter(pagelist.get(0));
                gridView1.setAdapter(gridviewAdapter1);
            }
            if (pagelist.size() > 1) {
                if (gridviewAdapter2 != null) {
                    gridviewAdapter2.upList(pagelist.get(1));
                    gridviewAdapter2.notifyDataSetChanged();
                } else {
                    gridviewAdapter2 = new GridviewAdapter(pagelist.get(1));
                    gridView2.setAdapter(gridviewAdapter2);
                }
            } else {
                if (gridviewAdapter2 != null) {
                    gridviewAdapter2.upList(new ShakeThemeInfo[0]);
                    gridviewAdapter2.notifyDataSetChanged();
                } else {
                    gridviewAdapter2 = new GridviewAdapter(new ShakeThemeInfo[0]);
                    gridView2.setAdapter(gridviewAdapter2);
                }
            }
        } else {
            if (gridviewAdapter1 != null) {
                gridviewAdapter1.upList(new ShakeThemeInfo[0]);
                gridviewAdapter1.notifyDataSetChanged();
            } else {
                gridviewAdapter1 = new GridviewAdapter(new ShakeThemeInfo[0]);
                gridView1.setAdapter(gridviewAdapter1);
            }
            if (gridviewAdapter2 != null) {
                gridviewAdapter2.upList(new ShakeThemeInfo[0]);
                gridviewAdapter2.notifyDataSetChanged();
            } else {
                gridviewAdapter2 = new GridviewAdapter(new ShakeThemeInfo[0]);
                gridView2.setAdapter(gridviewAdapter2);
            }
            if (gridviewAdapter3 != null) {
                gridviewAdapter3.upList(new ShakeThemeInfo[0]);
                gridviewAdapter3.notifyDataSetChanged();
            } else {
                gridviewAdapter3 = new GridviewAdapter(new ShakeThemeInfo[0]);
                gridView3.setAdapter(gridviewAdapter3);
            }
        }
    }

    private ArrayList<ShakeThemeInfo[]> pagelist;
    private MyPageAdapter myPageAdapter;

    private class MyPageAdapter extends PagerAdapter {

        public int getCount() {
            return pagelist.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view = ShakephotoView.this.instantiateItem(position);
            if (view.getParent() != null) {
                container.removeView(view);
            }
            container.addView(view);
            return view;
        }

        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }
    }

    View instantiateItem(int position) {
        int index = position + 1;
        if (index > 3) {
            index = index % 3;
        }
        switch (index) {
            case 1: {
                if (gridviewAdapter1 != null) {
                    gridviewAdapter1.upList(pagelist.get(position));
                    gridviewAdapter1.notifyDataSetChanged();
                } else {
                    gridviewAdapter1 = new GridviewAdapter(pagelist.get(position));
                    gridView1.setAdapter(gridviewAdapter1);
                }
            }
            return gridView1;
            case 2: {
                if (gridviewAdapter2 != null) {
                    gridviewAdapter2.upList(pagelist.get(position));
                    gridviewAdapter2.notifyDataSetChanged();
                } else {
                    gridviewAdapter2 = new GridviewAdapter(pagelist.get(position));
                    gridView2.setAdapter(gridviewAdapter2);
                }
            }
            return gridView2;
            default: {
                if (gridviewAdapter3 != null) {
                    gridviewAdapter3.upList(pagelist.get(position));
                    gridviewAdapter3.notifyDataSetChanged();
                } else {
                    gridviewAdapter3 = new GridviewAdapter(pagelist.get(position));
                    gridView3.setAdapter(gridviewAdapter3);
                }
            }
            return gridView3;
        }
    }

    private class GridviewAdapter extends BaseAdapter {
        private ShakeThemeInfo[] ShakeThemeInfos;
        int itemWidth, itemHeight;

        GridviewAdapter(ShakeThemeInfo[] shakeThemeInfos) {
            ShakeThemeInfos = shakeThemeInfos;
            int totalHeight = (int) (ShakephotoView.this.getHeight() - getResources().getDimension(R.dimen.shakephotoview_point_height) -
                    getResources().getDimension(R.dimen.shakephotoview_point_tmargin) -
                    getResources().getDimension(R.dimen.shakephotoview_point_bmargin) -
                    getResources().getDimension(R.dimen.shakephotoview_theme_height)) - 40;
            int totalWidht = viewshakephoto_viewpager.getWidth();
            int hspacing = (int) getResources().getDimension(R.dimen.shakephotoview_hspacing);
            int vspacing = (int) getResources().getDimension(R.dimen.shakephotoview_vspacing) + 1;
            itemWidth = (totalWidht - hspacing * 2) / 3;
            itemHeight = (totalHeight - vspacing * 2) / 3;
        }

        public void upList(ShakeThemeInfo[] shakeThemeInfos) {
            ShakeThemeInfos = shakeThemeInfos;
        }

        public int getCount() {
            return ShakeThemeInfos.length;
        }

        public Object getItem(int position) {
            return ShakeThemeInfos[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Viewhold viewhold;
            if (convertView == null) {
                convertView = Tools.loadLayout(getContext(), R.layout.item_view_shakphoto);
                viewhold = new Viewhold();
                viewhold.item_shakephoto_txt = (TextView) convertView.findViewById(R.id.item_shakephoto_txt);
                viewhold.item_shakephoto = convertView.findViewById(R.id.item_shakephoto);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) viewhold.item_shakephoto_txt.getLayoutParams();
                lp.width = itemWidth;
                lp.height = itemHeight;
                viewhold.item_shakephoto_txt.setLayoutParams(lp);
                convertView.setTag(viewhold);
            } else {
                viewhold = (Viewhold) convertView.getTag();
            }
            ShakeThemeInfo shakeThemeInfo = ShakeThemeInfos[position];
            if (viewhold != null) {
                viewhold.shakeThemeInfo = shakeThemeInfo;
                if (viewhold.item_shakephoto_txt != null && shakeThemeInfo != null) {
                    viewhold.item_shakephoto.setVisibility(VISIBLE);
                    viewhold.item_shakephoto_txt.setText(shakeThemeInfo.getActivity_name() + "");
                } else {
                    viewhold.item_shakephoto.setVisibility(INVISIBLE);
                }
            }
            return convertView;
        }
    }

    private class Viewhold {
        ShakeThemeInfo shakeThemeInfo;
        TextView item_shakephoto_txt;
        View item_shakephoto;
    }

    private void getData() {
        activityListByTheme.sendPostRequest(Urls.ActivityListByTheme, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            if (!jsonObject.isNull("activity_list")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("activity_list");
                                ThemeInfo themeInfo = themeInfos.get(themePosition);
                                int length = jsonArray.length();
                                themeInfo.ShakeThemeInfos = new ArrayList<ShakeThemeInfo>();
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    ShakeThemeInfo headInfo = new ShakeThemeInfo();
                                    headInfo.setAi_id(jsonObject1.getString("ai_id"));
                                    headInfo.setCat_id(jsonObject1.getString("cat_id"));
                                    headInfo.setTheme_name(jsonObject1.getString("theme_name"));
                                    headInfo.setActivity_name(jsonObject1.getString("activity_name"));
                                    headInfo.setLocation_type(jsonObject1.getString("location_type"));
                                    headInfo.setPlace_name(jsonObject1.getString("place_name"));
                                    headInfo.setProvince(jsonObject1.getString("province"));
                                    headInfo.setCity(jsonObject1.getString("city"));
                                    headInfo.setCounty(jsonObject1.getString("county"));
                                    headInfo.setAddress(jsonObject1.getString("address"));
                                    headInfo.setLatitude(jsonObject1.optString("latitude"));
                                    headInfo.setLongitude(jsonObject1.optString("longitude"));
                                    JSONArray jsonArray1 = jsonObject1.optJSONArray("key_cencent");
                                    if (jsonArray1 != null) {
                                        int l2 = jsonArray1.length();
                                        String[] key_cencent = new String[l2];
                                        for (int j = 0; j < l2; j++) {
                                            key_cencent[j] = jsonArray1.getString(j);
                                        }
                                        headInfo.setKey_cencent(key_cencent);
                                    }
                                    themeInfo.ShakeThemeInfos.add(headInfo);
                                }
                                settingPageview(themeInfo);
                            }
                        }
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        });
    }
}
