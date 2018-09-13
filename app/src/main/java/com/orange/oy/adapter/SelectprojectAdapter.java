package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.SelectprojectInfo;

import java.util.ArrayList;

/**
 * 提现-项目选择适配器
 */
public class SelectprojectAdapter extends BaseAdapter {
    private ArrayList<SelectprojectInfo> list = null;
    private Context mContext;
    private int selectPosition;

    public SelectprojectAdapter(Context context, ArrayList<SelectprojectInfo> list) {
        mContext = context;
        this.list = list;
        selectPosition = -1;
    }

    public void resetList(ArrayList<SelectprojectInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView itemView = null;
        if (convertView == null) {
            convertView = Tools.loadLayout(mContext, R.layout.item_selectproject);
            itemView = new ItemView();
            itemView.item_project = (TextView) convertView.findViewById(R.id.item_project);
            itemView.item_detail = (TextView) convertView.findViewById(R.id.item_detail);
            itemView.item_m2 = (TextView) convertView.findViewById(R.id.item_m2);
            itemView.item_img = (ImageView) convertView.findViewById(R.id.item_img);
            itemView.item_select = (ImageView) convertView.findViewById(R.id.item_select);
            itemView.item_time = (TextView) convertView.findViewById(R.id.item_time);
            convertView.setTag(itemView);
        } else {
            itemView = (ItemView) convertView.getTag();
        }
        SelectprojectInfo selectprojectInfo = list.get(position);
        itemView.item_project.setText(selectprojectInfo.getProjectName());
        itemView.item_m2.setText(selectprojectInfo.getMoney());
        if (selectPosition == position) {
            itemView.item_select.setImageResource(R.mipmap.single_selected);
        } else {
            itemView.item_select.setImageResource(R.mipmap.single_notselect);
        }
        if ("3".equals(selectprojectInfo.getType())) {//偶米提现的项目
            itemView.item_time.setVisibility(View.VISIBLE);
            itemView.item_detail.setVisibility(View.GONE);
            itemView.item_time.setText("兑换时间：" + selectprojectInfo.getExechangeTime());
        } else if ("4".equals(selectprojectInfo.getType())) {
            itemView.item_time.setVisibility(View.GONE);
            itemView.item_detail.setVisibility(View.VISIBLE);
            itemView.item_detail.setText("翻倍奖励");
        } else {
            itemView.item_time.setVisibility(View.GONE);
            itemView.item_detail.setVisibility(View.VISIBLE);
            itemView.item_detail.setText(String.format(mContext.getResources().getString(R.string.selectproject_item_detail),
                    selectprojectInfo.getOutletNum() + ""));
        }
        if ("2".equals(selectprojectInfo.getType())) {//招募问卷奖励金
            itemView.item_img.setImageResource(R.mipmap.recruit_money);
        } else if ("3".equals(selectprojectInfo.getType())) {//偶米兑换
            itemView.item_img.setImageResource(R.mipmap.oumi_withdraws);
        } else if ("4".equals(selectprojectInfo.getType())) {//帮忙加倍
            itemView.item_img.setImageResource(R.mipmap.double_money);
        } else {
            itemView.item_img.setImageResource(R.mipmap.projectname);
        }
        return convertView;
    }

    private class ItemView {
        private TextView item_project, item_m2, item_detail, item_time;
        private ImageView item_select, item_img;
    }
}
