package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CheckNewMemberInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/2/5.
 */

public class TalklistAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CheckNewMemberInfo.ReplyBean> replyBeanArrayList = new ArrayList<>();
    private String he = "";
    private int index = 0;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setHe(String he) {
        this.he = he;
    }

    public TalklistAdapter(Context context, ArrayList<CheckNewMemberInfo.ReplyBean> list) {
        this.context = context;
        this.replyBeanArrayList = list;
    }


    @Override
    public int getCount() {
        return replyBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return replyBeanArrayList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_talklist);
            viewHolder.tv_talk = (TextView) convertView.findViewById(R.id.tv_talk);
            viewHolder.tv_btn_reply = (TextView) convertView.findViewById(R.id.tv_btn_reply);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CheckNewMemberInfo.ReplyBean replyBean = replyBeanArrayList.get(position);
        if (position == replyBeanArrayList.size() - 1) {
            viewHolder.tv_btn_reply.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_btn_reply.setVisibility(View.GONE);
        }
        if (replyBean.getType() == 1) {
            viewHolder.tv_talk.setText(settingStyle(he, "我", replyBean.getText()));
        } else {
            viewHolder.tv_talk.setText(settingStyle("我", he, replyBean.getText()));
        }
        viewHolder.tv_btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyButton.onclick(index);
            }
        });
        return convertView;
    }

    private Spannable settingStyle(String left, String right, String msg) {
        int ll = left.length();
        int rl = right.length();
        Spannable spannable = new SpannableString(left + "回复" + right + msg);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#231916")), 0, spannable.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(Tools.spToPx(context, 12)), 0, spannable.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), 0, ll,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F65D57")), ll + 2, ll + 2 + rl,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    class ViewHolder {
        private TextView tv_talk, tv_btn_reply;
    }

    public ReplyButton replyButton;

    public interface ReplyButton {
        void onclick(int position);
    }

    public void setReplyButtonButtonListener(ReplyButton replyButton) {
        this.replyButton = replyButton;
    }
}
