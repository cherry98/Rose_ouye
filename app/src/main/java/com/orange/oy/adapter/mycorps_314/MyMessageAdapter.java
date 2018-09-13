package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.MyMessageInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/23.
 * 我的-> 消息页面 V3.20
 */

public class MyMessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MyMessageInfo> list;
    private ImageLoader imageLoader;

    public MyMessageAdapter(Context context, ArrayList<MyMessageInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_mymessage);
            viewHolder.itemessage_img = (CircularImageView) convertView.findViewById(R.id.itemessage_img);
            viewHolder.itemessage_name = (TextView) convertView.findViewById(R.id.itemessage_name);
            viewHolder.itemessage_content = (TextView) convertView.findViewById(R.id.itemessage_content);
            viewHolder.itemessage_time = (TextView) convertView.findViewById(R.id.itemessage_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyMessageInfo myMessageInfo = list.get(position);
        if ("1".equals(myMessageInfo.getIs_ouye())) {
            viewHolder.itemessage_name.setText("偶业小秘");
            viewHolder.itemessage_img.setImageResource(R.mipmap.ic_launcher);
        } else {
            viewHolder.itemessage_name.setText(myMessageInfo.getUser_name());
            imageLoader.DisplayImage(Urls.ImgIp + myMessageInfo.getUser_img(), viewHolder.itemessage_img, R.mipmap.grxx_icon_mrtx);
        }
        viewHolder.itemessage_content.setText(myMessageInfo.getMessage());
        viewHolder.itemessage_time.setText(myMessageInfo.getCreate_time());
        return convertView;
    }

    class ViewHolder {
        private CircularImageView itemessage_img;
        private TextView itemessage_name, itemessage_content, itemessage_time;
    }
}
