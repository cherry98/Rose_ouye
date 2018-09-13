package com.orange.oy.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

public class TelephoneAlllistAdapter extends BaseAdapter implements View.OnTouchListener {
    private Context context;
    private ArrayList<MyteamNewfdInfo> list;
    private String localStr;
    private boolean isSearch;
    private ImageLoader imageLoader;

    public TelephoneAlllistAdapter(Context context, ArrayList<MyteamNewfdInfo> list) {
        this.context = context;
        this.list = list;
        isSearch = false;
        imageLoader = new ImageLoader(context);
    }

    public void updateListView(boolean isSearch, ArrayList<MyteamNewfdInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.view_item_telephonelist2);
            itemView = new ItemView();
            itemView.title = (TextView) convertView.findViewById(R.id.item_teleph_title);
            itemView.name = (TextView) convertView.findViewById(R.id.item_teleph_name);
            itemView.phone = (TextView) convertView.findViewById(R.id.item_teleph);
            itemView.img = (CircularImageView) convertView.findViewById(R.id.item_teleph_img);
            itemView.citysearch3_checkbox = (ImageView) convertView.findViewById(R.id.citysearch3_checkbox);
            convertView.setTag(itemView);
        } else {
            itemView = (ItemView) convertView.getTag();
        }
        MyteamNewfdInfo myteamNewfdInfo = list.get(position);
        if (myteamNewfdInfo != null) {
            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                itemView.title.setVisibility(View.VISIBLE);
                itemView.title.setText(myteamNewfdInfo.getSortLetters());
            } else {
                itemView.title.setVisibility(View.GONE);
            }
            itemView.name.setText(myteamNewfdInfo.getName());
            itemView.phone.setText(myteamNewfdInfo.getPhone());
            imageLoader.DisplayImage(myteamNewfdInfo.getImg(), itemView.img, R.mipmap.my_img_de);

            itemView.citysearch3_checkbox.setVisibility(View.VISIBLE);
            if (myteamNewfdInfo.isChecked()) {
                itemView.citysearch3_checkbox.setImageResource(R.mipmap.round_selected);
            } else {
                itemView.citysearch3_checkbox.setImageResource(R.mipmap.round_notselect);
            }
        } else {
            convertView.setVisibility(View.GONE);
        }
        return convertView;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void clearSelect() {
        isSelect = false;
    }

    private boolean isSelect;

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isSelect = true;
        }
        return false;
    }

    class ItemView {
        TextView title, name, button, phone;
        CircularImageView img;
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
