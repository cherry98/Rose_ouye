package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CorpGrabDetailInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/5/23.
 * 网点分布的明细==战队 V3.15
 */

public class CorpGrabDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CorpGrabDetailInfo> list;

    public CorpGrabDetailAdapter(Context context, ArrayList<CorpGrabDetailInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_grabdetail);
            viewHolder.itemgrabdetail_name = (TextView) convertView.findViewById(R.id.itemgrabdetail_name);
            viewHolder.itemgrabdetail_num = (TextView) convertView.findViewById(R.id.itemgrabdetail_num);
            viewHolder.itemgrabdetail_addr = (TextView) convertView.findViewById(R.id.itemgrabdetail_addr);
            viewHolder.itemgrabdetail_looktime = (TextView) convertView.findViewById(R.id.itemgrabdetail_looktime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
        viewHolder.itemgrabdetail_name.setText(corpGrabDetailInfo.getOutlet_name());
        viewHolder.itemgrabdetail_addr.setText(corpGrabDetailInfo.getOutlet_address());
        viewHolder.itemgrabdetail_num.setText(corpGrabDetailInfo.getOutlet_num());
        if (!TextUtils.isEmpty(corpGrabDetailInfo.getTimeDetail())) {
            viewHolder.itemgrabdetail_looktime.setText(corpGrabDetailInfo.getTimeDetail());
            viewHolder.itemgrabdetail_looktime.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemgrabdetail_looktime.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemgrabdetail_name, itemgrabdetail_num, itemgrabdetail_addr, itemgrabdetail_looktime;
    }
}
