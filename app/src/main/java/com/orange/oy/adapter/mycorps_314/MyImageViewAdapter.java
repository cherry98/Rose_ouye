package com.orange.oy.adapter.mycorps_314;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.ShakePhotoInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

import static com.orange.oy.R.id.imageView;

/**
 * Created by Lenovo on 2018/6/6.
 * 仅单张图片显示
 */

public class MyImageViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ShakePhotoInfo> list;
    private ImageLoader imageLoader;

    public MyImageViewAdapter(Context context, ArrayList<ShakePhotoInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_imageview);
            viewHolder.itemimg_img = (ImageView) convertView.findViewById(R.id.itemimg_img);
            viewHolder.itemimg_text = (TextView) convertView.findViewById(R.id.itemimg_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ShakePhotoInfo shakePhotoInfo = list.get(position);
        imageLoader.DisplayImage(Urls.Endpoint3 + shakePhotoInfo.getFile_url() + "?x-oss-process=image/resize,l_100", viewHolder.itemimg_img);
        viewHolder.itemimg_text.setText("我的总收益：¥" + Tools.removePoint(shakePhotoInfo.getMoney()));
        return convertView;
    }

    class ViewHolder {
        private ImageView itemimg_img;
        private TextView itemimg_text;
    }
}
