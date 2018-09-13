package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.LocalPhotoInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/6/6.
 * 图片选择删除
 */

public class ImageSelectAdadpter extends BaseAdapter {
    private Context context;
    private ArrayList<LocalPhotoInfo.PhotoListBean> list;
    private ImageLoader imageLoader;
    private OnItemCheckListener onItemCheckListener;
    private int parPosition;

    public ImageSelectAdadpter(Context context, ArrayList<LocalPhotoInfo.PhotoListBean> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    public int getParPosition() {
        return parPosition;
    }

    public void setParPosition(int parPosition) {
        this.parPosition = parPosition;
    }

    public void setList(ArrayList<LocalPhotoInfo.PhotoListBean> list) {
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
        ImageSelectAdapterViewhold viewHolder;
        if (convertView == null) {
            viewHolder = new ImageSelectAdapterViewhold();
            convertView = Tools.loadLayout(context, R.layout.itemimage_reset);
            viewHolder.itemimage_img = (ImageView) convertView.findViewById(R.id.itemimage_img);
            viewHolder.itemimage_check = (CheckBox) convertView.findViewById(R.id.itemimage_check);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageSelectAdapterViewhold) convertView.getTag();
        }
        viewHolder.parPosition = parPosition;
        final LocalPhotoInfo.PhotoListBean photoListBean = list.get(position);
        if (photoListBean.isShow()) {
            viewHolder.itemimage_check.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemimage_check.setVisibility(View.GONE);
        }
        imageLoader.DisplayImage(Urls.Endpoint3 + photoListBean.getFile_url() + "?x-oss-process=image/resize,l_100", viewHolder.itemimage_img);
        viewHolder.itemimage_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    photoListBean.setCheck(true);
                } else {
                    photoListBean.setCheck(false);
                }
                onItemCheckListener.onItemCheck(photoListBean);
            }
        });
        return convertView;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public interface OnItemCheckListener {
        void onItemCheck(LocalPhotoInfo.PhotoListBean photoListBean);
    }
}
