package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.LocationInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/11/28.
 */

public class RecommendAdapter extends BaseAdapter {

    private Context context;
    ArrayList<LocationInfo> list;
    private ImageLoader imageLoader;

    public RecommendAdapter(Context context, ArrayList<LocationInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
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
            convertView = Tools.loadLayout(context, R.layout.item_recommendep);
            viewHolder.itemrecommendep_img = (ImageView) convertView.findViewById(R.id.itemrecommendep_img);
            viewHolder.itemrecommendep_name = (TextView) convertView.findViewById(R.id.itemrecommendep_name);
            viewHolder.itemrecommendep_addr = (TextView) convertView.findViewById(R.id.itemrecommendep_addr);
            viewHolder.itemrecommendep_dis = (TextView) convertView.findViewById(R.id.itemrecommendep_dis);
            viewHolder.itemrecommendep_money = (TextView) convertView.findViewById(R.id.itemrecommendep_money);
            viewHolder.itemrecommendep_unit = (TextView) convertView.findViewById(R.id.itemrecommendep_unit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LocationInfo locationInfo = list.get(position);
        imageLoader.DisplayImage(Urls.ImgIp + locationInfo.getPhotoUrl(), viewHolder.itemrecommendep_img);
        viewHolder.itemrecommendep_name.setText(locationInfo.getStoreName());
        viewHolder.itemrecommendep_addr.setText(locationInfo.getAddress());
        viewHolder.itemrecommendep_dis.setText("<" + locationInfo.getDist());
        viewHolder.itemrecommendep_money.setText(locationInfo.getOutletMoney());
        viewHolder.itemrecommendep_unit.setText(locationInfo.getMoney_unit());
        return convertView;
    }

    class ViewHolder {
        private ImageView itemrecommendep_img;
        private TextView itemrecommendep_name, itemrecommendep_addr, itemrecommendep_dis, itemrecommendep_money, itemrecommendep_unit;
    }
}
