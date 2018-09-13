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
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.MyImageView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/3/31.
 * 拍照任务重做页面照片编辑adapter
 */

public class ImageResetAdapter extends BaseAdapter {
    Context context;
    private ArrayList<String> list;
    private boolean isEdit;
    private ImageLoader imageLoader;
    private OnItemClickedListener listener;
    private int rate;
    private String path;
    private boolean isShowProgress;

    public ImageResetAdapter(Context context, ArrayList<String> list) {
        imageLoader = new ImageLoader(context);
        this.context = context;
        this.list = list;
    }

    public ImageResetAdapter(Context context, ArrayList<String> list, boolean isShowProgress) {
        imageLoader = new ImageLoader(context);
        this.context = context;
        this.list = list;
        this.isShowProgress = isShowProgress;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
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

    public void setUpData(int rate, String path) {
        this.rate = rate;
        this.path = path;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.itemimage_reset);
            viewHolder.itemimage_img = (ImageView) convertView.findViewById(R.id.itemimage_img);
            viewHolder.itemimage_check = (CheckBox) convertView.findViewById(R.id.itemimage_check);
            viewHolder.itemimage_img2 = (MyImageView) convertView.findViewById(R.id.itemimage_img2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (isEdit) {
            if (!"add_photo".equals(list.get(position))) {
                viewHolder.itemimage_check.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.itemimage_check.setVisibility(View.GONE);
        }
        viewHolder.itemimage_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isEdit) {
                    viewHolder.itemimage_check.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.itemimage_check.setVisibility(View.GONE);
                }
                listener.onItemClicked(viewHolder.itemimage_check, list.get(position));
            }
        });
        if (isShowProgress) {
            viewHolder.itemimage_img.setVisibility(View.GONE);
            viewHolder.itemimage_img2.setVisibility(View.VISIBLE);
            if ("add_photo".equals(list.get(position))) {
                viewHolder.itemimage_img2.setImageResource(R.mipmap.pzp_button_tjzp);
            } else {
                if (list.get(position).startsWith("http://")) {
                    viewHolder.itemimage_img2.setImageBitmap3(list.get(position));
                } else {
                    if (list.get(position).equals(path)) {
                        if (rate == 0) {
                            viewHolder.itemimage_img2.setText(rate + "%" + "\n等待上传");
                            viewHolder.itemimage_img2.setAlpha(0.4f);
                        } else if (rate == 100) {
                            viewHolder.itemimage_img2.setText(rate + "%" + "\n上传成功");
                            viewHolder.itemimage_img2.setAlpha(1f);
                        } else {
                            viewHolder.itemimage_img2.setText(rate + "%" + "\n正在上传");
                            viewHolder.itemimage_img2.setAlpha(0.4f);
                        }
                    }
                    viewHolder.itemimage_img2.setImageBitmap2(list.get(position));
                }
            }
        } else {
            if ("add_photo".equals(list.get(position))) {
                viewHolder.itemimage_img.setImageResource(R.mipmap.pzp_button_tjzp);
                viewHolder.itemimage_check.setVisibility(View.GONE);
            } else {
                String url = list.get(position);
                if (url.startsWith(Urls.Endpoint3)) {
                    url += "?x-oss-process=image/resize,l_350";
                }
                imageLoader.setShowWH(1024).DisplayImage(url, viewHolder.itemimage_img);
            }
            viewHolder.itemimage_img.setVisibility(View.VISIBLE);
            viewHolder.itemimage_img2.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView itemimage_img;
        private MyImageView itemimage_img2;
        private CheckBox itemimage_check;
    }

    public interface OnItemClickedListener {
        void onItemClicked(CheckBox view, String photoUrl);
    }

    public void setOnShowItemClickListener(OnItemClickedListener listener) {
        this.listener = listener;
    }
}
