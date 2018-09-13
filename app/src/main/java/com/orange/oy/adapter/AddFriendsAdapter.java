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

public class AddFriendsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MyteamNewfdInfo> list;
    private ImageLoader imageLoader;

    public AddFriendsAdapter(Context context, ArrayList<MyteamNewfdInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_newfriends);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_newfds_name);
            viewHolder.img = (CircularImageView) convertView.findViewById(R.id.item_newfds_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyteamNewfdInfo myteamNewfdInfo = list.get(position);
        imageLoader.DisplayImage(myteamNewfdInfo.getImg(), viewHolder.img, R.mipmap.my_img_de);
        viewHolder.name.setText(myteamNewfdInfo.getName());
        return convertView;
    }

    class ViewHolder {
        TextView name;
        CircularImageView img;
    }
}
