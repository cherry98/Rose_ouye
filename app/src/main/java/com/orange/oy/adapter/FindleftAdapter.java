package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.FindleftInfo;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

public class FindleftAdapter extends BaseAdapter {
    private ArrayList<FindleftInfo> list;
    private Context context;
    private ImageLoader imageLoader;

    public FindleftAdapter(ArrayList<FindleftInfo> list, Context context) {
        this.list = list;
        this.context = context;
        imageLoader = new ImageLoader(context);
    }

    public void resetList(ArrayList<FindleftInfo> list) {
        this.list = list;
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

    class ItemView {
        ImageView img, distanceico;
        TextView name, time, address, distance;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView itemView;
        if (convertView == null) {
            itemView = new ItemView();
            convertView = Tools.loadLayout(context, R.layout.view_item_findleft);
            itemView.img = (ImageView) convertView.findViewById(R.id.findleft_img);
            itemView.name = (TextView) convertView.findViewById(R.id.findleft_name);
            itemView.address = (TextView) convertView.findViewById(R.id.findleft_address);
            itemView.time = (TextView) convertView.findViewById(R.id.findleft_time);
            itemView.distance = (TextView) convertView.findViewById(R.id.findleft_distance);
            itemView.distanceico = (ImageView) convertView.findViewById(R.id.findleft_distance_ico);
            convertView.setTag(itemView);
        } else {
            itemView = (ItemView) convertView.getTag();
        }
        FindleftInfo findleftInfo = list.get(position);
        imageLoader.DisplayImage(findleftInfo.getImg(), itemView.img);
        itemView.name.setText(findleftInfo.getName());
        itemView.time.setText(findleftInfo.getTime());
        itemView.address.setText(findleftInfo.getAddress());
        if (findleftInfo.getDistance() == null || findleftInfo.getDistance().equals("")) {
            itemView.distanceico.setVisibility(View.GONE);
            itemView.distance.setVisibility(View.GONE);
        } else {
            itemView.distance.setVisibility(View.VISIBLE);
            itemView.distanceico.setVisibility(View.VISIBLE);
            itemView.distance.setText(findleftInfo.getDistance());
        }
        return convertView;
    }
}
