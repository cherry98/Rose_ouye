package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskPhotoInfo;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.MyImageView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/3/31.
 * 拍照任务重做页面照片编辑adapter
 */

public class ImageResetAdapter2 extends BaseAdapter {
    Context context;
    private ArrayList<TaskPhotoInfo> list;
    private boolean isEdit;
    private boolean isRecord;//是否是问卷任务

    public ImageResetAdapter2(Context context, ArrayList<TaskPhotoInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setRecord(boolean record) {
        isRecord = record;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageResetAdapter2ViewHold viewHolder;
        if (convertView == null) {
            viewHolder = new ImageResetAdapter2ViewHold();
            convertView = Tools.loadLayout(context, R.layout.itemimage_reset2);
            viewHolder.itemimage_check = (ImageView) convertView.findViewById(R.id.itemimage_check);
            viewHolder.itemimage_delete = (ImageView) convertView.findViewById(R.id.itemimage_delete);
            viewHolder.itemimage_img2 = (MyImageView) convertView.findViewById(R.id.itemimage_img2);
            viewHolder.itemimage_img2.getmImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageResetAdapter2ViewHold) convertView.getTag();
        }
        TaskPhotoInfo taskPhotoInfo = list.get(position);
        taskPhotoInfo.setBindView(convertView);

        if (isRecord) {//问卷任务需要
            viewHolder.itemimage_check.setVisibility(View.GONE);
            if (isEdit) {//如果是编辑
                if (!"add_photo".equals(taskPhotoInfo.getPath())) {
                    viewHolder.itemimage_delete.setVisibility(View.VISIBLE);
                    viewHolder.itemimage_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDeleteClickListener.onDelete(position);
                        }
                    });
                } else {
                    viewHolder.itemimage_delete.setVisibility(View.GONE);
                }
            } else {
                viewHolder.itemimage_delete.setVisibility(View.GONE);
            }
        } else {
            if (isEdit) {
                if (!"add_photo".equals(taskPhotoInfo.getPath())) {
                    viewHolder.itemimage_check.setVisibility(View.VISIBLE);
                    if (taskPhotoInfo.isSelect()) {
                        viewHolder.itemimage_check.setImageResource(R.mipmap.image_check);
                    } else {
                        viewHolder.itemimage_check.setImageResource(R.mipmap.image_uncheck);
                    }
                }
            } else {
                viewHolder.itemimage_check.setVisibility(View.GONE);
            }
        }
        viewHolder.itemimage_img2.setVisibility(View.VISIBLE);
        if (taskPhotoInfo.getPath().startsWith("add")) {
            viewHolder.itemimage_img2.setText("");
            viewHolder.itemimage_img2.setAlpha(1f);
            viewHolder.itemimage_img2.setImageResource(R.mipmap.pzp_button_tjzp);
        } else {
            viewHolder.itemimage_img2.setImageBitmap4(taskPhotoInfo.getPath());
            if (!taskPhotoInfo.isLocal()) {
                if (taskPhotoInfo.isUped()) {
                    viewHolder.itemimage_img2.setText("100%" + "\n上传成功");
                    viewHolder.itemimage_img2.setAlpha(1f);
                } else {
                    viewHolder.itemimage_img2.setText(" " + "\n等待上传");
                    viewHolder.itemimage_img2.setAlpha(0.4f);
                }
            } else {
                viewHolder.itemimage_img2.setText("");
                viewHolder.itemimage_img2.setAlpha(1f);
            }
        }
        return convertView;
    }

    private OnDeleteClickListener onDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }
}
