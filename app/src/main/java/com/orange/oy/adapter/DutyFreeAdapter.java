package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.DutyFreeInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/3/6.
 * 免税额度页面
 */

public class DutyFreeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DutyFreeInfo> list;

    public DutyFreeAdapter(Context context, ArrayList<DutyFreeInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_dutyfree);
            viewHolder.itemduty_type = (TextView) convertView.findViewById(R.id.itemduty_type);
            viewHolder.itemduty_money = (TextView) convertView.findViewById(R.id.itemduty_money);
            viewHolder.itemduty_time = (TextView) convertView.findViewById(R.id.itemduty_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DutyFreeInfo dutyFreeInfo = list.get(position);
        String money = dutyFreeInfo.getMoney();
        if (TextUtils.isEmpty(money)) {
            money = "-";
        } else {
            double d = Tools.StringToDouble(money);
            if (d - (int) d > 0) {
                money = String.valueOf(d);
            } else {
                money = String.valueOf((int) d);
            }
        }
        if ("-1".equals(money)) {
            money = "本月∞";
        }
        if ("1".equals(dutyFreeInfo.getType())) {
            money = "+" + money;
        } else if ("0".equals(dutyFreeInfo.getType())) {
            money = "-" + money;
        }
        viewHolder.itemduty_money.setText(money);
        viewHolder.itemduty_time.setText(dutyFreeInfo.getObtainTime());
        viewHolder.itemduty_type.setText(dutyFreeInfo.getRemark());
        return convertView;
    }

    class ViewHolder {
        private TextView itemduty_type, itemduty_money, itemduty_time;
    }
}
