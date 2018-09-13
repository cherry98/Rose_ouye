package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.MyCorpsInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/5/9.
 * 我的战队页面信息==
 */

public class MyCorpsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MyCorpsInfo> list;
    private ImageLoader imageLoader;

    public MyCorpsAdapter(Context context, ArrayList<MyCorpsInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_mycorps);
            viewHolder.itemmycorps_img = (CircularImageView) convertView.findViewById(R.id.itemmycorps_img);
            viewHolder.itemmycorps_name = (TextView) convertView.findViewById(R.id.itemmycorps_name);
            viewHolder.itemmycorps_msg = (TextView) convertView.findViewById(R.id.itemmycorps_msg);
            viewHolder.itemmycorps_num = (TextView) convertView.findViewById(R.id.itemmycorps_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyCorpsInfo myCorpsInfo = list.get(position);
        String url = myCorpsInfo.getTeam_img();
        if (!TextUtils.isEmpty(url) && !"null".equals(url)) {
            imageLoader.DisplayImage(Urls.ImgIp + url, viewHolder.itemmycorps_img, R.mipmap.grxx_icon_mrtx);
        } else {
            viewHolder.itemmycorps_img.setImageResource(R.mipmap.grxx_icon_mrtx);
        }
        viewHolder.itemmycorps_name.setText(myCorpsInfo.getTeam_name());
        String msg = myCorpsInfo.getApply_user_num();
        if (!"0".equals(msg) && !TextUtils.isEmpty(msg) && !"null".equals(msg)) {
            viewHolder.itemmycorps_msg.setVisibility(View.VISIBLE);
            viewHolder.itemmycorps_msg.setText(msg);
        } else {
            viewHolder.itemmycorps_msg.setVisibility(View.GONE);
        }
        viewHolder.itemmycorps_num.setText(myCorpsInfo.getUser_num());
        return convertView;
    }

    class ViewHolder {
        private CircularImageView itemmycorps_img;
        private TextView itemmycorps_name, itemmycorps_msg, itemmycorps_num;
    }
}
