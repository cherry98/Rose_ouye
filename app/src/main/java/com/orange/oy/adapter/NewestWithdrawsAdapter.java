package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.NewestWithdrawsInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/4/16.
 */

public class NewestWithdrawsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NewestWithdrawsInfo> list;
    private ImageLoader imageLoader;

    public NewestWithdrawsAdapter(Context context, ArrayList<NewestWithdrawsInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_newest);
            viewHolder.itemnewest_time = (TextView) convertView.findViewById(R.id.itemnewest_time);
            viewHolder.itemnewest_img = (CircularImageView) convertView.findViewById(R.id.itemnewest_img);
            viewHolder.itemnewest_content = (TextView) convertView.findViewById(R.id.itemnewest_content);
            viewHolder.itemnewest_layout = convertView.findViewById(R.id.itemnewest_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            viewHolder.itemnewest_layout.setBackgroundColor(convertView.getResources().getColor(R.color.newest_bg));
        } else {
            viewHolder.itemnewest_layout.setBackgroundColor(convertView.getResources().getColor(R.color.app_background2));
        }
        NewestWithdrawsInfo newestWithdrawsInfo = list.get(position);
        if (TextUtils.isEmpty(newestWithdrawsInfo.getImg_url()) || "null".equals(newestWithdrawsInfo.getImg_url())) {
            viewHolder.itemnewest_img.setImageResource(R.mipmap.grxx_icon_mrtx);
        } else {
            imageLoader.DisplayImage(Urls.ImgIp + newestWithdrawsInfo.getImg_url(), viewHolder.itemnewest_img, R.mipmap.grxx_icon_mrtx);
        }
        viewHolder.itemnewest_content.setText(newestWithdrawsInfo.getRecord());
        viewHolder.itemnewest_time.setText(newestWithdrawsInfo.getDate());
        return convertView;
    }

    class ViewHolder {
        private CircularImageView itemnewest_img;
        private TextView itemnewest_content, itemnewest_time;
        private View itemnewest_layout;
    }
}
