package com.orange.oy.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.CityInfo;

import java.util.ArrayList;

/**
 * 原代理申请选择城市===创建战队选择城市
 */
public class Citysearch2Adapter extends BaseAdapter {
    private Context context;
    private ArrayList<CityInfo> list;
    private String localStr;
    private boolean isSearch;
    private boolean isShow;

    public Citysearch2Adapter(Context context, ArrayList<CityInfo> list, boolean isShow) {
        this.context = context;
        this.list = list;
        isSearch = false;
        this.isShow = isShow;
    }

    public void updateListView(boolean isSearch, ArrayList<CityInfo> list) {
        this.isSearch = isSearch;
        this.list = list;
        notifyDataSetChanged();
    }

    public void setLocal(String name) {
        localStr = name;
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
        ItemView itemView = null;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.view_item_citysearch3);
            itemView = new ItemView();
            itemView.title = (TextView) convertView.findViewById(R.id.citysearch3_title);
            itemView.name = (TextView) convertView.findViewById(R.id.citysearch3_name);
            itemView.citysearch3_checkbox = (ImageView) convertView.findViewById(R.id.citysearch3_checkbox);
            convertView.setTag(itemView);
        } else {
            itemView = (ItemView) convertView.getTag();
        }
        final CityInfo cityInfo = list.get(position);
        if (isShow) {
            itemView.citysearch3_checkbox.setVisibility(View.VISIBLE);
            if (cityInfo.isChecked()) {
                itemView.citysearch3_checkbox.setImageResource(R.mipmap.round_selected);
            } else {
                itemView.citysearch3_checkbox.setImageResource(R.mipmap.round_notselect);
            }
        } else {
            itemView.citysearch3_checkbox.setVisibility(View.GONE);
        }

        if (cityInfo != null) {
            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                itemView.title.setVisibility(View.VISIBLE);
                itemView.title.setText(cityInfo.getSortLetters());
            } else {
                itemView.title.setVisibility(View.GONE);
            }
            itemView.name.setText(cityInfo.getName());
        } else {
            itemView.title.setVisibility(View.GONE);
            itemView.name.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ItemView {
        private TextView title, name;
        private ImageView citysearch3_checkbox;
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
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }
}
