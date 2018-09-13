package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.OnItemCheckListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CorpGrabDetailInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/5/24.
 * 战队任务执行===无价格
 */

public class CorpNopriceAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CorpGrabDetailInfo> list;
    private OnItemCheckListener onItemCheckListener;
    private boolean isClick1;
    private boolean isClick2;

    public CorpNopriceAdapter(Context context, ArrayList<CorpGrabDetailInfo> list) {
        this.context = context;
        this.list = list;
    }

    public boolean isClick1() {
        return isClick1;
    }

    public boolean isClick2() {
        return isClick2;
    }

    public void clearClick() {
        isClick1 = false;
        isClick2 = false;
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
            convertView = Tools.loadLayout(context, R.layout.item_corpnoprice);
            viewHolder.itemnoprice_name = (TextView) convertView.findViewById(R.id.itemnoprice_name);
            viewHolder.itemnoprice_nickname = (TextView) convertView.findViewById(R.id.itemnoprice_nickname);
            viewHolder.itemnoprice_state = (TextView) convertView.findViewById(R.id.itemnoprice_state);
            viewHolder.itemnoprice_code = (TextView) convertView.findViewById(R.id.itemnoprice_code);
            viewHolder.itemnoprice_addr = (TextView) convertView.findViewById(R.id.itemnoprice_addr);
            viewHolder.itemnoprice_dis = (TextView) convertView.findViewById(R.id.itemnoprice_dis);
            viewHolder.itemnoprice_carry = (TextView) convertView.findViewById(R.id.itemnoprice_carry);
            viewHolder.itemnoprice_carrytime = (TextView) convertView.findViewById(R.id.itemnoprice_carrytime);
            viewHolder.itemnoprice_waitname = (TextView) convertView.findViewById(R.id.itemnoprice_waitname);
            viewHolder.itemnoprice_chatname = (TextView) convertView.findViewById(R.id.itemnoprice_chatname);
            viewHolder.itemnoprice_chattime = (TextView) convertView.findViewById(R.id.itemnoprice_chattime);
            viewHolder.itemnoprice_reason = (TextView) convertView.findViewById(R.id.itemnoprice_reason);
            viewHolder.itemnoprice_chat_ly = convertView.findViewById(R.id.itemnoprice_chat_ly);
            viewHolder.itemnoprice_check = (CheckBox) convertView.findViewById(R.id.itemnoprice_check);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
        viewHolder.itemnoprice_name.setText(corpGrabDetailInfo.getOutlet_name());
        if (!Tools.isEmpty(corpGrabDetailInfo.getAccessed_name())) {
            viewHolder.itemnoprice_nickname.setText(corpGrabDetailInfo.getAccessed_name());
            viewHolder.itemnoprice_nickname.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemnoprice_nickname.setVisibility(View.GONE);
        }
        String exe_state = corpGrabDetailInfo.getExe_state();
        if ("2".equals(exe_state)) {//待执行
            viewHolder.itemnoprice_state.setText("待执行");
        } else if ("3".equals(exe_state)) {//执行中
            viewHolder.itemnoprice_state.setText("执行中");
        } else if ("9".equals(exe_state)) {//待分配
            viewHolder.itemnoprice_state.setText("待分配");
        } else if ("10".equals(exe_state)) {//确认中
            viewHolder.itemnoprice_state.setText("待确认");
        }
        viewHolder.itemnoprice_code.setText(corpGrabDetailInfo.getOutlet_num());
        viewHolder.itemnoprice_addr.setText(corpGrabDetailInfo.getOutlet_address());
        viewHolder.itemnoprice_carrytime.setText(corpGrabDetailInfo.getTimeDetail());
        String confirm_time = corpGrabDetailInfo.getConfirm_time();
        if (!TextUtils.isEmpty(confirm_time) && !"null".equals(confirm_time)) {
            String time = "已等待队员确认" + confirm_time;
            int start = time.indexOf(confirm_time);
            int end = start + confirm_time.length();
            SpannableStringBuilder builder = new SpannableStringBuilder(time);
            builder.setSpan(new ForegroundColorSpan(convertView.getResources().getColor(R.color.corps_bule)),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.itemnoprice_waitname.setText(builder);
            viewHolder.itemnoprice_waitname.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemnoprice_waitname.setVisibility(View.GONE);
        }
        if (corpGrabDetailInfo.is_haveReason()) {
            viewHolder.itemnoprice_chat_ly.setVisibility(View.VISIBLE);
            viewHolder.itemnoprice_chatname.setText(corpGrabDetailInfo.getUser_name());
            viewHolder.itemnoprice_chattime.setText(corpGrabDetailInfo.getCreate_time());
            if (!Tools.isEmpty(corpGrabDetailInfo.getReason())) {
                viewHolder.itemnoprice_reason.setText(corpGrabDetailInfo.getReason());
            } else {
                viewHolder.itemnoprice_reason.setText(corpGrabDetailInfo.getUser_name() + "放弃此任务");
            }
        } else {
            viewHolder.itemnoprice_chat_ly.setVisibility(View.GONE);
        }
        if (AppInfo.getName(context).equals(corpGrabDetailInfo.getAccessed_num())) {
            viewHolder.itemnoprice_dis.setVisibility(View.VISIBLE);
            viewHolder.itemnoprice_carry.setVisibility(View.VISIBLE);
            viewHolder.itemnoprice_check.setVisibility(View.GONE);
        } else {
            viewHolder.itemnoprice_dis.setVisibility(View.VISIBLE);
            viewHolder.itemnoprice_carry.setVisibility(View.GONE);
            viewHolder.itemnoprice_check.setVisibility(View.GONE);
        }
        if (corpGrabDetailInfo.isShowCheck()) {//显示复选框
            viewHolder.itemnoprice_check.setVisibility(View.VISIBLE);
            viewHolder.itemnoprice_carry.setVisibility(View.GONE);
            viewHolder.itemnoprice_dis.setVisibility(View.GONE);
        }
        viewHolder.itemnoprice_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    corpGrabDetailInfo.setCheck(true);
                } else {
                    corpGrabDetailInfo.setCheck(false);
                }
                onItemCheckListener.onItemCheck(corpGrabDetailInfo);
            }
        });
        viewHolder.itemnoprice_carry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick1 = true;
                return false;
            }
        });
        viewHolder.itemnoprice_dis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick2 = true;
                return false;
            }
        });
        viewHolder.itemnoprice_check.setChecked(corpGrabDetailInfo.isCheck());
        return convertView;
    }

    class ViewHolder {
        private TextView itemnoprice_name, itemnoprice_nickname, itemnoprice_state, itemnoprice_code, itemnoprice_addr,
                itemnoprice_dis, itemnoprice_carry, itemnoprice_carrytime, itemnoprice_waitname;
        private View itemnoprice_chat_ly;
        private TextView itemnoprice_chatname, itemnoprice_chattime, itemnoprice_reason;
        private CheckBox itemnoprice_check;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }
}
