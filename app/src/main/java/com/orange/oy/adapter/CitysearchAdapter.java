package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.info.CityInfo;

import java.util.ArrayList;

public class CitysearchAdapter extends BaseAdapter implements View.OnTouchListener {
    private Context context;
    private ArrayList<CityInfo> list;
    private boolean isSearch;
    private View citysearch, citysearch2;
    private String selectCity;
    private TextView address;

    public CitysearchAdapter(Context context, ArrayList<CityInfo> list) {
        this.context = context;
        this.list = list;
        isSearch = false;
        citysearch2 = Tools.loadLayout(context, R.layout.view_item_citysearch2);
        citysearch = Tools.loadLayout(context, R.layout.view_item_citysearch);
        citysearch.findViewById(R.id.item_cs_item1).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item11).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item2).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item3).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item4).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item5).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item6).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item7).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item8).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item9).setOnTouchListener(this);
        citysearch.findViewById(R.id.item_cs_item10).setOnTouchListener(this);
        citysearch2.findViewById(R.id.citysearch2_name).setOnTouchListener(this);
        address = (TextView) citysearch2.findViewById(R.id.citysearch2_tv_addr);
    }

    public String getSelectCity() {
        return selectCity;
    }

    public void updateListView(boolean isSearch, ArrayList<CityInfo> list) {
        this.isSearch = isSearch;
        this.list = list;
        notifyDataSetChanged();
    }

    public void setLocation(String str) {
        if (address != null) {
            address.setText(str);
        }
    }

    public String getAddressStr() {
        if (address != null) {
            return address.getText().toString();
        }
        return "";
    }

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
        if (position == 0 && !isSearch) {
            convertView = citysearch2;
            String[] addresss = AppInfo.getAddress(context);
            if (TextUtils.isEmpty(addresss[1])) {
                convertView.findViewById(R.id.citysearch2_layout).setVisibility(View.GONE);
                //  convertView.findViewById(R.id.citysearch2_location).setVisibility(View.VISIBLE);
                address.setText("正在定位...");
            } else {
                convertView.findViewById(R.id.citysearch2_layout).setVisibility(View.GONE);
                convertView.findViewById(R.id.citysearch2_location).setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.citysearch2_name)).setText(MainActivity.localCity);
                if (TextUtils.isEmpty(addresss[2])) {
                    address.setText(addresss[1]);
                } else {
                    address.setText(addresss[1] + "-" + addresss[2]);
                }
            }
        } else if (position == 1 && !isSearch) {
            convertView = citysearch;
        } else {
            ItemView itemView;
            if (convertView == null) {
                convertView = Tools.loadLayout(context, R.layout.view_item_citysearch3);
                itemView = new ItemView();
                itemView.title = (TextView) convertView.findViewById(R.id.citysearch3_title);
                itemView.name = (TextView) convertView.findViewById(R.id.citysearch3_name);
//                itemView.line = convertView.findViewById(R.id.citysearch3_line);
                convertView.setTag(itemView);
            } else {
                Object tag = convertView.getTag();
                if (tag instanceof ItemView) {
                    itemView = (ItemView) tag;
                } else {
                    convertView = Tools.loadLayout(context, R.layout.view_item_citysearch3);
                    itemView = new ItemView();
                    itemView.title = (TextView) convertView.findViewById(R.id.citysearch3_title);
                    itemView.name = (TextView) convertView.findViewById(R.id.citysearch3_name);
//                    itemView.line = convertView.findViewById(R.id.citysearch3_line);
                    convertView.setTag(itemView);
                }
            }
            CityInfo cityInfo = list.get(position);
            if (cityInfo != null) {
                //根据position获取分类的首字母的Char ascii值
                int section = getSectionForPosition(position);
                //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                if (position == getPositionForSection(section)) {
//                    itemView.line.setVisibility(View.VISIBLE);
                    itemView.title.setVisibility(View.VISIBLE);
                    itemView.title.setText(cityInfo.getSortLetters());
                } else {
//                    itemView.line.setVisibility(View.GONE);
                    itemView.title.setVisibility(View.GONE);
                }
                itemView.name.setText((TextUtils.isEmpty(cityInfo.getCounty()) ? "" : (cityInfo.getCounty() + " "))
                        + cityInfo.getName() + " " + cityInfo.getProvince());
            } else {
//                itemView.line.setVisibility(View.GONE);
                itemView.title.setVisibility(View.GONE);
                itemView.name.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && v instanceof TextView) {
            switch (v.getId()) {
                case R.id.item_cs_item1:
                case R.id.item_cs_item2:
                case R.id.item_cs_item3:
                case R.id.item_cs_item4:
                case R.id.item_cs_item5:
                case R.id.item_cs_item6:
                case R.id.item_cs_item7:
                case R.id.item_cs_item8:
                case R.id.item_cs_item9:
                case R.id.item_cs_item10:
                case R.id.citysearch2_name:
                case R.id.item_cs_item11: {
                    selectCity = ((TextView) v).getText().toString();
                }
                break;
            }
        }
        return false;
    }

    class ItemView {
        TextView title, name;
//        View line;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = (isSearch) ? 0 : 2; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
