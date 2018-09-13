package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.BrightBallotInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/1/17.
 */

public class BrightBallotAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BrightBallotInfo> list;

    public BrightBallotAdapter(Context context, ArrayList<BrightBallotInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_brightballot);
//            viewHolder.item_ballot_layout_big = (LinearLayout) convertView.findViewById(R.id.item_ballot_layout_big);
            viewHolder.item_ballot_type = (TextView) convertView.findViewById(R.id.item_ballot_type);
            viewHolder.item_ballot_num = (TextView) convertView.findViewById(R.id.item_ballot_num);
            viewHolder.item_ballot_view = convertView.findViewById(R.id.item_ballot_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BrightBallotInfo brightBallotInfo = list.get(position);
        if (position == list.size() - 1) {
//            viewHolder.item_ballot_layout_big.setVisibility(View.VISIBLE);
            viewHolder.item_ballot_view.setVisibility(View.GONE);
        } else {
//            viewHolder.item_ballot_layout_big.setVisibility(View.GONE);
            viewHolder.item_ballot_view.setVisibility(View.VISIBLE);
        }
        viewHolder.item_ballot_type.setText(brightBallotInfo.getType());
        if (brightBallotInfo.getComplete() == 1) {
            viewHolder.item_ballot_num.setText("");
            viewHolder.item_ballot_num.setBackgroundResource(R.mipmap.bright_ballot_finish);
        } else {
            viewHolder.item_ballot_num.setText("共抽取" + brightBallotInfo.getNum() + "人");
        }
        return convertView;
    }

    class ViewHolder {
        private LinearLayout item_ballot_layout_big;
        private TextView item_ballot_type, item_ballot_num;
        private View item_ballot_view;
    }
}
