package com.orange.oy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.ImageSelectAdadpter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.LocalPhotoInfo;
import com.orange.oy.info.shakephoto.PhotoListBean;
import com.orange.oy.info.shakephoto.ShakePhotoInfo2;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

import static com.orange.oy.R.id.picture;
import static com.orange.oy.R.mipmap.image_check;

/**
 * 图片选择和上传
 */

public class ImageSelect2Adadpter extends BaseAdapter {
    // ImageSelectAdadpter
    private Context context;
    private ArrayList<PhotoListBean> list;
    private ImageLoader imageLoader;
    private OnItemCheckListener onItemCheckListener;
    private int parPosition;

    public ImageSelect2Adadpter(Context context, ArrayList<PhotoListBean> list) {
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

    public void setList(ArrayList<PhotoListBean> list) {
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
        final ImageSelectAdapterViewhold viewHolder;
        if (convertView == null) {
            viewHolder = new ImageSelectAdapterViewhold();
            convertView = Tools.loadLayout(context, R.layout.itemimage_reset3);
            viewHolder.itemimage_img = (ImageView) convertView.findViewById(R.id.itemimage_img);
            viewHolder.itemimage_check = (ImageView) convertView.findViewById(R.id.itemimage_check);
            View itemimager3 = convertView.findViewById(R.id.itemimager3);
            ViewGroup.LayoutParams lp = itemimager3.getLayoutParams();
            lp.width = (Tools.getScreeInfoWidth(context) - Tools.dipToPx((Activity) context, 35)) / 2;
            lp.height = (Tools.getScreeInfoWidth(context) - Tools.dipToPx((Activity) context, 35)) / 2;
            itemimager3.setLayoutParams(lp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageSelectAdapterViewhold) convertView.getTag();
        }
        viewHolder.parPosition = parPosition;
        final PhotoListBean photoListBean = list.get(position);
        viewHolder.mPath = photoListBean.getFile_url();
        if (photoListBean.isShow()) {
            viewHolder.itemimage_check.setVisibility(View.VISIBLE);

            viewHolder.itemimage_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (photoListBean.isShow()) {
                        if (photoListBean.isCheck()) {
                            photoListBean.setCheck(false);
                            viewHolder.itemimage_check.setImageResource(R.mipmap.image_uncheck);
                        } else {
                            photoListBean.setCheck(true);
                            viewHolder.itemimage_check.setImageResource(R.mipmap.image_check);
                        }
                        onItemCheckListener.onItemCheck(photoListBean);
                    }
                }
            });
        } else {
            viewHolder.itemimage_check.setVisibility(View.GONE);
        }
        imageLoader.setShowWH(200).DisplayImage(photoListBean.getFile_url(), viewHolder.itemimage_img, -2);
        return convertView;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public interface OnItemCheckListener {
        void onItemCheck(PhotoListBean photoListBean);
    }

    public class ImageSelectAdapterViewhold {
        ImageView itemimage_img;
        ImageView itemimage_check;
        int parPosition;
        String mPath;
    }

}
