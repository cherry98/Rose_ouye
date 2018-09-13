package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.PhotoListBean;
import com.orange.oy.info.shakephoto.ShakePhotoInfo2;
import com.orange.oy.view.MyImageView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/3/31.
 * 拍照任务重做页面照片编辑adapter
 */

public class ImageUpAdapter extends BaseAdapter {
    Context context;
    private ArrayList<PhotoListBean> list;
    private boolean isEdit;

    public ImageUpAdapter(Context context, ArrayList<PhotoListBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isEdit() {
        return isEdit;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageResetAdapter2ViewHold viewHolder;
        if (convertView == null) {
            viewHolder = new ImageResetAdapter2ViewHold();
            convertView = Tools.loadLayout(context, R.layout.itemimage_reset2);
            viewHolder.itemimage_img2 = (MyImageView) convertView.findViewById(R.id.itemimage_img2);
            viewHolder.itemimage_img2.getmImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageResetAdapter2ViewHold) convertView.getTag();
        }
        PhotoListBean taskPhotoInfo = list.get(position);
        taskPhotoInfo.setBindView(convertView);

        viewHolder.itemimage_img2.setVisibility(View.VISIBLE);

        viewHolder.itemimage_img2.setImageBitmap(taskPhotoInfo.getFile_url());

        if (taskPhotoInfo.isUped()) {
            Tools.d("上传完成======");
            viewHolder.itemimage_img2.setText("100%" + "\n上传成功");
            viewHolder.itemimage_img2.setAlpha(1f);
        } else {
            Tools.d("等待上传======");
            viewHolder.itemimage_img2.setText(" " + "\n等待上传");
            viewHolder.itemimage_img2.setAlpha(0.4f);
        }
        return convertView;
    }
}
