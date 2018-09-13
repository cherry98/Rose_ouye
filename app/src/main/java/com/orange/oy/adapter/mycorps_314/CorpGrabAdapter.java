package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CorpGrabInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/5/23.
 * 众包任务批量申领==战队 V3.15
 */

public class CorpGrabAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CorpGrabInfo> list;

    public CorpGrabAdapter(Context context, ArrayList<CorpGrabInfo> list) {
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
        ViewHolder viewholder;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_corpgrab);
            viewholder.itemcorpgrab_district = (TextView) convertView.findViewById(R.id.itemcorpgrab_district);
            viewholder.itemcorpgrab_money = (TextView) convertView.findViewById(R.id.itemcorpgrab_money);
            viewholder.itemcorpgrab_account = (TextView) convertView.findViewById(R.id.itemcorpgrab_account);
            viewholder.itemcorpgrab_img = (ImageView) convertView.findViewById(R.id.itemcorpgrab_img);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        CorpGrabInfo corpGrabInfo = list.get(position);
        if ("1".equals(corpGrabInfo.getProject_type())) {//1为有网点，5为无网点
            viewholder.itemcorpgrab_img.setImageResource(R.mipmap.task_package);
        } else {
            viewholder.itemcorpgrab_img.setImageResource(R.mipmap.task_nooutlets);
        }
        if ("1".equals(corpGrabInfo.getType())) {//1为省份的包，2为城市的包
            viewholder.itemcorpgrab_district.setText(corpGrabInfo.getProvince());
        } else if ("2".equals(corpGrabInfo.getType())) {
            viewholder.itemcorpgrab_district.setText(corpGrabInfo.getProvince() + "-" + corpGrabInfo.getCity());
        }
        if (TextUtils.isEmpty(corpGrabInfo.getNum())) {
            viewholder.itemcorpgrab_account.setVisibility(View.GONE);
        } else {
            viewholder.itemcorpgrab_account.setVisibility(View.VISIBLE);
            viewholder.itemcorpgrab_account.setText(corpGrabInfo.getNum() + "个");
        }
        if (TextUtils.isEmpty(corpGrabInfo.getTotal_money())) {
            viewholder.itemcorpgrab_money.setVisibility(View.GONE);
        } else {
            viewholder.itemcorpgrab_money.setVisibility(View.VISIBLE);
            viewholder.itemcorpgrab_money.setText("¥" + Tools.removePoint(corpGrabInfo.getTotal_money()));
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemcorpgrab_district, itemcorpgrab_account, itemcorpgrab_money;
        private ImageView itemcorpgrab_img;
    }
}
