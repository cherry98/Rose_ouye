package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.ScreenshotInfo;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/9/7.
 */

public class ScreenImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ScreenshotInfo.PrintscreenListBean> list;
    private ImageLoader imageLoader;

    public ScreenImageAdapter(Context context, ArrayList<ScreenshotInfo.PrintscreenListBean> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_screenimg);
            viewHolder.itemscreenimg_img1 = (ImageView) convertView.findViewById(R.id.itemscreenimg_img1);
            viewHolder.itemscreenimg_img2 = (ImageView) convertView.findViewById(R.id.itemscreenimg_img2);
            viewHolder.itemscreenimg_view = convertView.findViewById(R.id.itemscreenimg_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ScreenshotInfo.PrintscreenListBean listBean = list.get(position);
        imageLoader.setShowWH(200).DisplayImage(listBean.getFile_url(), viewHolder.itemscreenimg_img1, -2);
        String type = listBean.getComment_type();
        if ("1".equals(type)) {//吻
            viewHolder.itemscreenimg_img2.setImageResource(R.mipmap.xq_button_wen);
            viewHolder.itemscreenimg_view.setBackgroundColor(context.getResources().getColor(R.color.xq_button_huawen));
        } else if ("2".equals(type)) {//花
            viewHolder.itemscreenimg_img2.setImageResource(R.mipmap.xq_button_hua);
            viewHolder.itemscreenimg_view.setBackgroundColor(context.getResources().getColor(R.color.xq_button_huawen));
        } else if ("3".equals(type)) {//鸡蛋
            viewHolder.itemscreenimg_img2.setImageResource(R.mipmap.xq_button_jidan);
            viewHolder.itemscreenimg_view.setBackgroundColor(context.getResources().getColor(R.color.xq_button_jidan));
        } else if ("4".equals(type)) {//板砖
            viewHolder.itemscreenimg_img2.setImageResource(R.mipmap.xq_button_banzhuan);
            viewHolder.itemscreenimg_view.setBackgroundColor(context.getResources().getColor(R.color.xq_button_banzhuan));
        } else if ("5".equals(type)) {//粑粑
            viewHolder.itemscreenimg_img2.setImageResource(R.mipmap.xq_button_shi);
            viewHolder.itemscreenimg_view.setBackgroundColor(context.getResources().getColor(R.color.xq_button_shi));
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView itemscreenimg_img1, itemscreenimg_img2;
        private View itemscreenimg_view;
    }
}
