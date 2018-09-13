package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.BrightPersonInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/1/16.
 */

public class BrightPersonInfoAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BrightPersonInfo> list;

    public BrightPersonInfoAdapter(Context context, ArrayList<BrightPersonInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_bright_info);
            viewHolder.itembrightinfo_name = (TextView) convertView.findViewById(R.id.itembrightinfo_name);
            viewHolder.itembrightinfo_test_state = (TextView) convertView.findViewById(R.id.itembrightinfo_test_state);
            viewHolder.itembrightinfo_sex = (TextView) convertView.findViewById(R.id.itembrightinfo_sex);
            viewHolder.itembrightinfo_job = (TextView) convertView.findViewById(R.id.itembrightinfo_job);
            viewHolder.itembrightinfo_dealer = (TextView) convertView.findViewById(R.id.itembrightinfo_dealer);
            viewHolder.itembrightinfo_phone = (TextView) convertView.findViewById(R.id.itembrightinfo_phone);
            viewHolder.itembrightinfo_email = (TextView) convertView.findViewById(R.id.itembrightinfo_email);
            viewHolder.itembrightinfo_id = (TextView) convertView.findViewById(R.id.itembrightinfo_id);
            viewHolder.itembrightinfo_on = (TextView) convertView.findViewById(R.id.itembrightinfo_on);
            viewHolder.itembrightinfo_off = (TextView) convertView.findViewById(R.id.itembrightinfo_off);
            viewHolder.itembrightinfo_left = (TextView) convertView.findViewById(R.id.itembrightinfo_left);
            viewHolder.itembrightinfo_none = (TextView) convertView.findViewById(R.id.itembrightinfo_none);
            viewHolder.itembrightinfo_note = (TextView) convertView.findViewById(R.id.itembrightinfo_note);
            viewHolder.itembrightinfo_note_layout = (LinearLayout) convertView.findViewById(R.id.itembrightinfo_note_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BrightPersonInfo brightPersonInfo = list.get(position);
        //判空
        if (!TextUtils.isEmpty(brightPersonInfo.getName()) && !"null".equals(brightPersonInfo.getName())) {
            viewHolder.itembrightinfo_name.setText(brightPersonInfo.getName());
        } else {
            viewHolder.itembrightinfo_name.setText("");
        }
        if (!TextUtils.isEmpty(brightPersonInfo.getSex()) && !"null".equals(brightPersonInfo.getSex())) {
            viewHolder.itembrightinfo_sex.setText(brightPersonInfo.getSex());
        } else {
            viewHolder.itembrightinfo_sex.setText("");
        }
        if (!TextUtils.isEmpty(brightPersonInfo.getDealer()) && !"null".equals(brightPersonInfo.getDealer())) {
            viewHolder.itembrightinfo_dealer.setText(brightPersonInfo.getDealer());
        } else {
            viewHolder.itembrightinfo_dealer.setText("");
        }
        if (!TextUtils.isEmpty(brightPersonInfo.getMobile()) && !"null".equals(brightPersonInfo.getMobile())) {
            viewHolder.itembrightinfo_phone.setText(brightPersonInfo.getMobile());
        } else {
            viewHolder.itembrightinfo_phone.setText("");
        }
        if (!TextUtils.isEmpty(brightPersonInfo.getEmail()) && !"null".equals(brightPersonInfo.getEmail())) {
            viewHolder.itembrightinfo_email.setText(brightPersonInfo.getEmail());
        } else {
            viewHolder.itembrightinfo_email.setText("");
        }
        if (!TextUtils.isEmpty(brightPersonInfo.getIdcardnum()) && !"null".equals(brightPersonInfo.getIdcardnum())) {
            viewHolder.itembrightinfo_id.setText(brightPersonInfo.getIdcardnum());
        } else {
            viewHolder.itembrightinfo_id.setText("");
        }
        if (!TextUtils.isEmpty(brightPersonInfo.getJob()) && !"null".equals(brightPersonInfo.getJob())) {
            viewHolder.itembrightinfo_job.setText(brightPersonInfo.getJob());
        } else {
            viewHolder.itembrightinfo_job.setText("");
        }
        if (!TextUtils.isEmpty(brightPersonInfo.getState()) && !"null".equals(brightPersonInfo.getState())) {
            if ("1".equals(brightPersonInfo.getState())) {
                viewHolder.itembrightinfo_on.setBackgroundResource(R.drawable.bright_info_showblue);
                viewHolder.itembrightinfo_on.setTextColor(context.getResources().
                        getColor(R.color.app_background2));//on
                viewHolder.itembrightinfo_off.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_off.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//off
                viewHolder.itembrightinfo_left.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_left.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//left
                viewHolder.itembrightinfo_none.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_none.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//none
            } else if ("2".equals(brightPersonInfo.getState())) {
                viewHolder.itembrightinfo_on.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_on.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//on
                viewHolder.itembrightinfo_off.setBackgroundResource(R.drawable.bright_info_showblue);
                viewHolder.itembrightinfo_off.setTextColor(context.getResources().
                        getColor(R.color.app_background2));//off
                viewHolder.itembrightinfo_left.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_left.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//left
                viewHolder.itembrightinfo_none.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_none.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//none
            } else if ("3".equals(brightPersonInfo.getState())) {
                viewHolder.itembrightinfo_on.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_on.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//on
                viewHolder.itembrightinfo_off.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_off.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//off
                viewHolder.itembrightinfo_left.setBackgroundResource(R.drawable.bright_info_showblue);
                viewHolder.itembrightinfo_left.setTextColor(context.getResources().
                        getColor(R.color.app_background2));//left
                viewHolder.itembrightinfo_none.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_none.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//none
            } else if ("4".equals(brightPersonInfo.getState())) {
                viewHolder.itembrightinfo_on.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_on.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//on
                viewHolder.itembrightinfo_off.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_off.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//off
                viewHolder.itembrightinfo_left.setBackgroundResource(R.drawable.bright_info_state);
                viewHolder.itembrightinfo_left.setTextColor(context.getResources().
                        getColor(R.color.brightpersoninfo_text_state2));//left
                viewHolder.itembrightinfo_none.setBackgroundResource(R.drawable.bright_info_showblue);
                viewHolder.itembrightinfo_none.setTextColor(context.getResources().
                        getColor(R.color.app_background2));//none
            }
        }
        if (brightPersonInfo.getIs_note() == 1) {
            viewHolder.itembrightinfo_note_layout.setVisibility(View.VISIBLE);
            viewHolder.itembrightinfo_note.setText("备注：" + brightPersonInfo.getNote());
        } else if (brightPersonInfo.getIs_note() == 0) {
            viewHolder.itembrightinfo_note_layout.setVisibility(View.GONE);
        }
        if (brightPersonInfo.getIscomplete() == 0) {
            viewHolder.itembrightinfo_test_state.setText("待考试");
            viewHolder.itembrightinfo_test_state.setTextColor(context.getResources().
                    getColor(R.color.brightpersoninfo_test_state1));
        } else if (brightPersonInfo.getIscomplete() == 1) {
            viewHolder.itembrightinfo_test_state.setText("考试中");
            viewHolder.itembrightinfo_test_state.setTextColor(context.getResources().
                    getColor(R.color.brightpersoninfo_test_state2));
        } else if (brightPersonInfo.getIscomplete() == 2) {
            viewHolder.itembrightinfo_test_state.setText("已考试");
            viewHolder.itembrightinfo_test_state.setTextColor(context.getResources().
                    getColor(R.color.brightpersoninfo_test_state3));
        } else if (brightPersonInfo.getIscomplete() == 3) {
            viewHolder.itembrightinfo_test_state.setText("已回收");
            viewHolder.itembrightinfo_test_state.setTextColor(context.getResources().
                    getColor(R.color.brightpersoninfo_test_state4));
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itembrightinfo_name, itembrightinfo_test_state, itembrightinfo_sex, itembrightinfo_job,
                itembrightinfo_dealer, itembrightinfo_phone, itembrightinfo_email, itembrightinfo_id;//人员信息
        private TextView itembrightinfo_on, itembrightinfo_off, itembrightinfo_left, itembrightinfo_none; //在职状态
        private TextView itembrightinfo_note;//备注
        private LinearLayout itembrightinfo_note_layout;
    }
}
