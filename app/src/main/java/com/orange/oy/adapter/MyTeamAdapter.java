package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

public class MyTeamAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MyteamNewfdInfo> list;
    private String localStr;
    private boolean isSearch;
    private ImageLoader imageLoader;

    public MyTeamAdapter(Context context, ArrayList<MyteamNewfdInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.view_item_myteam);
            itemView = new ItemView();
            itemView.title = (TextView) convertView.findViewById(R.id.item_myteam_title);
            itemView.name = (TextView) convertView.findViewById(R.id.item_myteam_name);
//            itemView.line = convertView.findViewById(R.id.item_myteam_line);
            itemView.img = (CircularImageView) convertView.findViewById(R.id.item_myteam_img);
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
//                itemView.line.setVisibility(View.VISIBLE);
//                itemView.title.setVisibility(View.VISIBLE);
                itemView.title.setText(myteamNewfdInfo.getSortLetters());
            } else {
//                itemView.line.setVisibility(View.GONE);
                itemView.title.setVisibility(View.GONE);
            }
            itemView.name.setText(myteamNewfdInfo.getName());
            imageLoader.DisplayImage(myteamNewfdInfo.getImg(), itemView.img, R.mipmap.my_img_de);
        } else {
            convertView.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ItemView {
        TextView title, name;
//        View line;
        CircularImageView img;
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
