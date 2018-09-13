package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CorpsNoticeInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/5/15.
 * 战队公告
 */

public class CorpsNoticeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CorpsNoticeInfo> list;
    private boolean isClick;
    private ImageLoader imageLoader;
    private boolean isEdit;

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public CorpsNoticeAdapter(Context context, ArrayList<CorpsNoticeInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    public boolean isClick() {
        return isClick;
    }

    public void clearClick() {
        isClick = false;
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
            convertView = Tools.loadLayout(context, R.layout.item_corpsnotice);
            viewHolder.itemcorpsnotice_title = (TextView) convertView.findViewById(R.id.itemcorpsnotice_title);
            viewHolder.itemcorpsnotice_time = (TextView) convertView.findViewById(R.id.itemcorpsnotice_time);
            viewHolder.itemcorpsnotice_content = (TextView) convertView.findViewById(R.id.itemcorpsnotice_content);
            viewHolder.itemcorpsnotice_hint = convertView.findViewById(R.id.itemcorpsnotice_hint);
            viewHolder.itemcorpsnotice_img = (CircularImageView) convertView.findViewById(R.id.itemcorpsnotice_img);
            viewHolder.itemcorpsnotice_delete = (TextView) convertView.findViewById(R.id.itemcorpsnotice_delete);
            viewHolder.itemcorpsnotice_move = convertView.findViewById(R.id.itemcorpsnotice_move);
            viewHolder.itemcorpsnotice_bg = convertView.findViewById(R.id.itemcorpsnotice_bg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CorpsNoticeInfo corpsNoticeInfo = list.get(position);
        String url = corpsNoticeInfo.getHead_img();
        if (!TextUtils.isEmpty(url) && !"null".equals(url)) {
            imageLoader.DisplayImage(Urls.ImgIp + url, viewHolder.itemcorpsnotice_img, R.mipmap.grxx_icon_mrtx);
        } else {
            viewHolder.itemcorpsnotice_img.setImageResource(R.mipmap.grxx_icon_mrtx);
        }
        viewHolder.itemcorpsnotice_content.setText(corpsNoticeInfo.getText());
        viewHolder.itemcorpsnotice_time.setText(corpsNoticeInfo.getCreate_time());
        viewHolder.itemcorpsnotice_title.setText(corpsNoticeInfo.getTitle());
        if ("0".equals(corpsNoticeInfo.getIs_read())) {
            viewHolder.itemcorpsnotice_hint.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemcorpsnotice_hint.setVisibility(View.GONE);
        }
        if (isEdit) {
            viewHolder.itemcorpsnotice_move.scrollTo(150, 0);
            viewHolder.itemcorpsnotice_delete.setVisibility(View.VISIBLE);
            viewHolder.itemcorpsnotice_bg.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
        } else {
            viewHolder.itemcorpsnotice_move.scrollTo(0, 0);
            viewHolder.itemcorpsnotice_delete.setVisibility(View.GONE);
            viewHolder.itemcorpsnotice_bg.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
        }
        viewHolder.itemcorpsnotice_delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick = true;
                return false;
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView itemcorpsnotice_title, itemcorpsnotice_time, itemcorpsnotice_content;
        private CircularImageView itemcorpsnotice_img;
        private View itemcorpsnotice_hint;//通知的小红点
        private TextView itemcorpsnotice_delete;//删除
        private View itemcorpsnotice_move;//移动的view
        private View itemcorpsnotice_bg;
    }
}
