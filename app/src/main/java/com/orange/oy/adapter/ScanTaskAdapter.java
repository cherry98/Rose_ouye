package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.ScanTaskInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/2/16.
 */

public class ScanTaskAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ScanTaskInfo> list;
    private ImageLoader imageLoader;
    public boolean isclick = false;

    public boolean isclick() {
        return isclick;
    }

    public ScanTaskAdapter(Context context, ArrayList<ScanTaskInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_scantask);
            viewHolder.itemscantask_img = (ImageView) convertView.findViewById(R.id.itemscantask_img);
            viewHolder.itemscantask_name = (TextView) convertView.findViewById(R.id.itemscantask_name);
            viewHolder.itemscantask_spec = (TextView) convertView.findViewById(R.id.itemscantask_spec);
            viewHolder.itemscantask_state = (TextView) convertView.findViewById(R.id.itemscantask_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        ScanTaskInfo scanTaskInfo = list.get(position);
        viewHolder.itemscantask_name.setText(scanTaskInfo.getName());
        viewHolder.itemscantask_spec.setText(scanTaskInfo.getSize());
        String state = scanTaskInfo.getState();
        if ("1".equals(state)) {
            viewHolder.itemscantask_state.setText("扫描成功");
            viewHolder.itemscantask_state.setTextColor(convertView.getResources().getColor(R.color.homepage_select));
            viewHolder.itemscantask_state.setBackgroundResource(R.color.app_background2);
            viewHolder.itemscantask_state.setOnTouchListener(null);
        } else if ("2".equals(state)) {
            viewHolder.itemscantask_state.setText("无此商品");
            viewHolder.itemscantask_state.setTextColor(convertView.getResources().getColor(R.color.app_background2));
            viewHolder.itemscantask_state.setBackgroundResource(R.drawable.dialog_upload1);
            viewHolder.itemscantask_state.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isclick = true;
                    return false;
                }
            });
        } else if ("3".equals(state)) {
            viewHolder.itemscantask_state.setOnTouchListener(null);
            viewHolder.itemscantask_state.setText("无此商品");
            viewHolder.itemscantask_state.setTextColor(convertView.getResources().getColor(R.color.homepage_select));
            viewHolder.itemscantask_state.setBackgroundResource(R.color.app_background2);
        } else if ("4".equals(state)) {
            viewHolder.itemscantask_state.setText("补扫");
            viewHolder.itemscantask_state.setTextColor(convertView.getResources().getColor(R.color.homepage_select));
            viewHolder.itemscantask_state.setBackgroundResource(R.drawable.dialog_upload3);
            viewHolder.itemscantask_state.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isclick = true;
                    return false;
                }
            });
        } else {
            isclick = false;
            viewHolder.itemscantask_state.setOnTouchListener(null);
            viewHolder.itemscantask_state.setText("");
        }
        String url = scanTaskInfo.getPicurl();
        if ("null".equals(url) || url == null || TextUtils.isEmpty(url)) {
            viewHolder.itemscantask_img.setVisibility(View.GONE);
        } else {
            viewHolder.itemscantask_img.setVisibility(View.VISIBLE);
            imageLoader.DisplayImage(Urls.ImgIp + scanTaskInfo.getPicurl(), viewHolder.itemscantask_img);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView itemscantask_img;
        private TextView itemscantask_name, itemscantask_spec, itemscantask_state;
    }
}
