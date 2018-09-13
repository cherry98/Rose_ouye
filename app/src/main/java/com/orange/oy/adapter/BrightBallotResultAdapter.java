package com.orange.oy.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.BrightBallotInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/1/11.
 */

public class BrightBallotResultAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BrightBallotInfo> list;

    public BrightBallotResultAdapter(Context context, ArrayList list) {
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

    private int completeNum = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_ballot_result);
            viewHolder.item_ballotresult_circle = (TextView) convertView.findViewById(R.id.item_ballotresult_circle);
            viewHolder.item_ballotresult_name = (TextView) convertView.findViewById(R.id.item_ballotresult_name);
            viewHolder.item_ballotresult_sex = (TextView) convertView.findViewById(R.id.item_ballotresult_sex);
            viewHolder.item_ballotresult_phone = (TextView) convertView.findViewById(R.id.item_ballotresult_phone);
            viewHolder.item_ballotresult_id = (TextView) convertView.findViewById(R.id.item_ballotresult_id);
            viewHolder.item_ballotresult_img = (TextView) convertView.findViewById(R.id.item_ballotresult_img);
            viewHolder.item_ballotresult_viewfirst = convertView.findViewById(R.id.item_ballotresult_viewfirst);
            viewHolder.item_ballotresult_viewlast = convertView.findViewById(R.id.item_ballotresult_viewlast);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BrightBallotInfo brightBallotInfo = list.get(position);
        viewHolder.item_ballotresult_name.setText(brightBallotInfo.getName());
        viewHolder.item_ballotresult_sex.setText(brightBallotInfo.getSex());
        viewHolder.item_ballotresult_phone.setText(brightBallotInfo.getMobile());
        viewHolder.item_ballotresult_id.setText(brightBallotInfo.getIdcardnum());
        if (position == 0) {
            viewHolder.item_ballotresult_viewfirst.setVisibility(View.GONE);
        } else {
            viewHolder.item_ballotresult_viewfirst.setVisibility(View.VISIBLE);
        }
        if (position == list.size() - 1) {
            viewHolder.item_ballotresult_viewlast.setVisibility(View.VISIBLE);
        } else {
            viewHolder.item_ballotresult_viewlast.setVisibility(View.GONE);
        }
        if (brightBallotInfo.getIscomplete() == 1) {
            completeNum++;
            viewHolder.item_ballotresult_circle.setBackgroundResource(R.drawable.bright_ballot_finished);
            viewHolder.item_ballotresult_img.setBackgroundResource(R.mipmap.bright_finished);
            isClick = false;
        } else if (brightBallotInfo.getIscomplete() == 0) {
            viewHolder.item_ballotresult_circle.setBackgroundResource(R.drawable.bright_ballot_test);
            viewHolder.item_ballotresult_img.setBackgroundResource(R.mipmap.bright_starttest);
            viewHolder.item_ballotresult_img.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (v.getId() == R.id.item_ballotresult_img) {
                        isClick = true;
                    }
                    return false;
                }
            });
        }
        return convertView;
    }

    public boolean isClick = false;

//    public boolean isClick() {
//        return isClick;
//    }

    class ViewHolder {
        private TextView item_ballotresult_circle, item_ballotresult_name, item_ballotresult_sex,
                item_ballotresult_phone, item_ballotresult_id;
        private TextView item_ballotresult_img;
        private View item_ballotresult_viewfirst, item_ballotresult_viewlast;
    }
}
