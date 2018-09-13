package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.TeamInformationActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CorpsTaskInfo;

import java.util.ArrayList;

/**
 * Created by zhangpengfei on 2018/5/28.
 * 战队列表适配器
 */
public class CorpsTaskAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private ArrayList<CorpsTaskInfo> list;
    private View.OnClickListener onClickListener;
    private String[] waitTypes = new String[]{"任务总量:", "待分配:", "确认中:", "待执行:", "执行中:"};
    private String[] stateTypes = new String[]{"审核中:", "未通过:", "已通过:"};
    private int state;//0:等待执行，1：项目状态

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public CorpsTaskAdapter(Context context, ArrayList<CorpsTaskInfo> list, int state, View.OnClickListener onClickListener) {
        this.context = context;
        this.list = list;
        this.state = state;
        setOnClickListener(onClickListener);
    }

    public CorpsTaskAdapter(Context context, ArrayList<CorpsTaskInfo> list, int state) {
        this.context = context;
        this.list = list;
        this.state = state;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.item_corpstask);
            viewHolder = new ViewHolder();
            viewHolder.itemcorpstask_item1 = (TextView) convertView.findViewById(R.id.itemcorpstask_item1);
            viewHolder.itemcorpstask_item2 = (TextView) convertView.findViewById(R.id.itemcorpstask_item2);
            viewHolder.itemcorpstask_item3 = (TextView) convertView.findViewById(R.id.itemcorpstask_item3);
            viewHolder.itemcorpstask_item4 = (TextView) convertView.findViewById(R.id.itemcorpstask_item4);
            viewHolder.itemcorpstask_type1 = (TextView) convertView.findViewById(R.id.itemcorpstask_type1);
            viewHolder.itemcorpstask_type2 = (TextView) convertView.findViewById(R.id.itemcorpstask_type2);
            viewHolder.itemcorpstask_type3 = (TextView) convertView.findViewById(R.id.itemcorpstask_type3);
            viewHolder.itemcorpstask_type4 = (TextView) convertView.findViewById(R.id.itemcorpstask_type4);
            viewHolder.itemcorpstask_type5 = (TextView) convertView.findViewById(R.id.itemcorpstask_type5);
            viewHolder.itemcorpstask_money = (TextView) convertView.findViewById(R.id.itemcorpstask_money);
            viewHolder.itemcorpstask_buttong = (TextView) convertView.findViewById(R.id.itemcorpstask_buttong);
            viewHolder.itemcorpstask_ico = (ImageView) convertView.findViewById(R.id.itemcorpstask_ico);
            if (onClickListener != null) {
                viewHolder.itemcorpstask_buttong.setOnClickListener(onClickListener);
            }
            viewHolder.itemcorpstask_item3.setOnClickListener(this);
            if (state == 0) {
                viewHolder.itemcorpstask_buttong.setText("分配");
                viewHolder.itemcorpstask_type4.setVisibility(View.VISIBLE);
                viewHolder.itemcorpstask_type5.setVisibility(View.VISIBLE);
            } else {
                viewHolder.itemcorpstask_buttong.setText("查看");
                viewHolder.itemcorpstask_type4.setVisibility(View.GONE);
                viewHolder.itemcorpstask_type5.setVisibility(View.GONE);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (list != null && !list.isEmpty()) {
            CorpsTaskInfo corpsTaskInfo = list.get(position);
            viewHolder.itemcorpstask_buttong.setTag(position);
            viewHolder.itemcorpstask_item1.setText(corpsTaskInfo.getProject_name());
            if ("1".equals(corpsTaskInfo.getIdentity())) {
                viewHolder.itemcorpstask_item2.setText("商家:" + corpsTaskInfo.getCompany_abbreviation());
            } else {
                viewHolder.itemcorpstask_item2.setText("队长:" + corpsTaskInfo.getCaptain_name());
            }
            viewHolder.itemcorpstask_item3.setText(settingStyle("战队名称:", corpsTaskInfo.getTeam_name(), "#A0A0A0", "#4A90E2"));
            viewHolder.itemcorpstask_item3.setTag(corpsTaskInfo.getTeam_id());
            if (state == 0) {//待执行列表
                viewHolder.itemcorpstask_type1.setText(settingStyle(waitTypes[0], corpsTaskInfo.getTotal_outlet()));
                viewHolder.itemcorpstask_type2.setText(settingStyle(waitTypes[1], corpsTaskInfo.getDistribution_outlet()));
                viewHolder.itemcorpstask_type3.setText(settingStyle(waitTypes[2], corpsTaskInfo.getConfirm_outlet()));
                viewHolder.itemcorpstask_type4.setText(settingStyle(waitTypes[3], corpsTaskInfo.getWait_exe_outlet()));
                viewHolder.itemcorpstask_type5.setText(settingStyle(waitTypes[4], corpsTaskInfo.getExecution_outlet()));
                if ("1".equals(corpsTaskInfo.getIdentity())) {
                    viewHolder.itemcorpstask_buttong.setBackgroundResource(R.drawable.item_corpstask_buttong_bg);
                    viewHolder.itemcorpstask_buttong.setTextColor(Color.parseColor("#F65D57"));
                    viewHolder.itemcorpstask_buttong.setText("分配");
                } else {
                    viewHolder.itemcorpstask_buttong.setBackgroundResource(R.drawable.item_corpstask_buttong_bg2);
                    viewHolder.itemcorpstask_buttong.setTextColor(Color.WHITE);
                    viewHolder.itemcorpstask_buttong.setText("立即执行");
                }
            } else {
                viewHolder.itemcorpstask_type1.setText(settingStyle(stateTypes[0], corpsTaskInfo.getCheck_outlet()));
                viewHolder.itemcorpstask_type2.setText(settingStyle(stateTypes[1], corpsTaskInfo.getUnpass_outlet()));
                viewHolder.itemcorpstask_type3.setText(settingStyle(stateTypes[2], corpsTaskInfo.getPass_outlet()));
            }
            if ("1".equals(corpsTaskInfo.getProject_type())) {//有网点
                viewHolder.itemcorpstask_ico.setImageResource(R.mipmap.task_havstore);
            } else {
                viewHolder.itemcorpstask_ico.setImageResource(R.mipmap.task_unhavstore);
            }
            viewHolder.itemcorpstask_item4.setText(corpsTaskInfo.getBegin_date() + "-" + corpsTaskInfo.getEnd_date() + "可执行");
            if (!("null".equals(corpsTaskInfo.getTotal_money()) || TextUtils.isEmpty(corpsTaskInfo.getTotal_money()))) {
                viewHolder.itemcorpstask_money.setText("¥" + corpsTaskInfo.getTotal_money());
            } else {
                viewHolder.itemcorpstask_money.setText("");
            }
        }
        return convertView;
    }

    private Spannable settingStyle(String left, String right) {
        return settingStyle(left, right, "#A0A0A0", "#F65D57");
    }

    private Spannable settingStyle(String left, String right, String leftcolor, String rightcolor) {
        int ll = left.length();
        int rl = right.length();
        Spannable spannable = new SpannableString(left + right);
        spannable.setSpan(new AbsoluteSizeSpan(Tools.spToPx(context, 12)), 0, ll + rl,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor(leftcolor)), 0, ll,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor(rightcolor)), ll, ll + rl,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public void onClick(View v) {
        //战队跳转用
        String team_id = (String) v.getTag();
        Intent intent = new Intent(context, TeamInformationActivity.class);
        intent.putExtra("team_id", team_id);
        context.startActivity(intent);
    }

    class ViewHolder {
        TextView itemcorpstask_item1, itemcorpstask_item2, itemcorpstask_item3, itemcorpstask_item4;
        TextView itemcorpstask_type1, itemcorpstask_type2, itemcorpstask_type3, itemcorpstask_type4, itemcorpstask_type5;
        TextView itemcorpstask_buttong, itemcorpstask_money;
        ImageView itemcorpstask_ico;
    }
}
