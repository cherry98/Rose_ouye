package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MyRecommendInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/11/16.
 */

public class MyRecommedAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MyRecommendInfo> list;

    public MyRecommedAdapter(Context context, ArrayList<MyRecommendInfo> list) {
        this.context = context;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_myrecommend);
            viewHolder.itemrecommend_num = (TextView) convertView.findViewById(R.id.itemrecommend_num);
            viewHolder.itemrecommend_reward = (TextView) convertView.findViewById(R.id.itemrecommend_reward);
            viewHolder.itemrecommend_time = (TextView) convertView.findViewById(R.id.itemrecommend_time);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyRecommendInfo myRecommendInfo = list.get(position);
        String isreward = myRecommendInfo.getIsreward();
        if ("0".equals(isreward)) {
            viewHolder.itemrecommend_reward.setText("暂未做任务");
            viewHolder.itemrecommend_reward.setTextColor(convertView.getResources().getColor(R.color.homepage_notselect));
            viewHolder.img.setVisibility(View.GONE);
        } else {
            viewHolder.itemrecommend_reward.setText("+ " + myRecommendInfo.getOmnum() + "偶米");
            viewHolder.itemrecommend_reward.setTextColor(convertView.getResources().getColor(R.color.homepage_select));
            viewHolder.img.setVisibility(View.VISIBLE);
        }
        viewHolder.itemrecommend_num.setText("注册账号：" + myRecommendInfo.getUsermobile());
        viewHolder.itemrecommend_time.setText("任务审核通过时间：" + myRecommendInfo.getTime());
        return convertView;
    }

    class ViewHolder {
        private TextView itemrecommend_num, itemrecommend_reward, itemrecommend_time;
        private ImageView img;
    }
}
