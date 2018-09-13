package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.FriendsDetailedInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/3/6.
 */

public class FriendsDetailedAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FriendsDetailedInfo> list;

    public FriendsDetailedAdapter(Context context, ArrayList<FriendsDetailedInfo> list) {
        this.context = context;
        this.list = list;
    }

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
            convertView = Tools.loadLayout(context, R.layout.item_friendsdetailed);
            viewHolder.itemfriends_mobile = (TextView) convertView.findViewById(R.id.itemfriends_mobile);
            viewHolder.itemfriends_type = (TextView) convertView.findViewById(R.id.itemfriends_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FriendsDetailedInfo friendsDetailedInfo = list.get(position);
        viewHolder.itemfriends_mobile.setText(friendsDetailedInfo.getFriendMobile());
        String state = friendsDetailedInfo.getState();
        if ("0".equals(state)) {
            viewHolder.itemfriends_type.setText("未验证");
        } else if ("1".equals(state)) {
            viewHolder.itemfriends_type.setText("已验证");
        } else if ("2".equals(state)) {
            viewHolder.itemfriends_type.setText("已失效");
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemfriends_mobile, itemfriends_type;
    }
}
