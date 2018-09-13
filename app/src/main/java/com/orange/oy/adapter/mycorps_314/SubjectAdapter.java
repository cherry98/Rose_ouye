package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.OptionsListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/9/6.
 * 多选单选题
 */

public class SubjectAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<OptionsListInfo> list;
    private ImageLoader imageLoader;
    private boolean isSingle;//是否是单选

    public SubjectAdapter(Context context, ArrayList<OptionsListInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    public void setSingle(boolean single) {
        isSingle = single;
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
            convertView = Tools.loadLayout(context, R.layout.item_subject);
            viewHolder.itemsubject_text = (TextView) convertView.findViewById(R.id.itemsubject_text);
            viewHolder.itemsubject_img1 = (ImageView) convertView.findViewById(R.id.itemsubject_img1);
            viewHolder.itemsubject_img2 = (ImageView) convertView.findViewById(R.id.itemsubject_img2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (isSingle) {
            viewHolder.itemsubject_img1.setImageResource(R.mipmap.single_notselect);
        } else {
            viewHolder.itemsubject_img1.setImageResource(R.mipmap.checkbox1311);
        }
        OptionsListInfo optionsListInfo = list.get(position);
        String url = optionsListInfo.getPhoto_url();
        if (Tools.isEmpty(url)) {
            viewHolder.itemsubject_img2.setVisibility(View.GONE);
        } else {
            viewHolder.itemsubject_img2.setVisibility(View.VISIBLE);
            if (url.startsWith("http") || url.startsWith("https")) {
                imageLoader.setShowWH(200).DisplayImage(url, viewHolder.itemsubject_img2);
            } else {
                imageLoader.setShowWH(200).DisplayImage(Urls.ImgIp + url, viewHolder.itemsubject_img2);
            }
        }
        viewHolder.itemsubject_text.setText(optionsListInfo.getOption_name());
        return convertView;
    }

    class ViewHolder {
        private TextView itemsubject_text;
        private ImageView itemsubject_img1, itemsubject_img2;
    }
}
