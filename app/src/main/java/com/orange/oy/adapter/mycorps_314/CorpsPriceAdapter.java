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
 * Created by Lenovo on 2018/5/25.
 * 战队任务执行===有价格
 */

public class CorpsPriceAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CorpGrabDetailInfo> list;
    private OnItemCheckListener onItemCheckListener;
    private boolean isClick1;
    private boolean isClick2;
    private boolean isClick3;

    public CorpsPriceAdapter(Context context, ArrayList<CorpGrabDetailInfo> list) {
        this.context = context;
        this.list = list;
    }

    public boolean isClick1() {
        return isClick1;
    }

    public boolean isClick2() {
        return isClick2;
    }

    public boolean isClick3() {
        return isClick3;
    }

    public void clearClick() {
        isClick1 = false;
        isClick2 = false;
        isClick3 = false;
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
            convertView = Tools.loadLayout(context, R.layout.item_corpprice);
            viewHolder.itemprice_name = (TextView) convertView.findViewById(R.id.itemprice_name);
            viewHolder.itemprice_money = (TextView) convertView.findViewById(R.id.itemprice_money);
            viewHolder.itemprice_code = (TextView) convertView.findViewById(R.id.itemprice_code);
            viewHolder.itemprice_nickname = (TextView) convertView.findViewById(R.id.itemprice_nickname);
            viewHolder.itemprice_state = (TextView) convertView.findViewById(R.id.itemprice_state);
            viewHolder.itemprice_addr = (TextView) convertView.findViewById(R.id.itemprice_addr);
            viewHolder.itemprice_carrytime = (TextView) convertView.findViewById(R.id.itemprice_carrytime);
            viewHolder.itemprice_waitname = (TextView) convertView.findViewById(R.id.itemprice_waitname);
            viewHolder.itemprice_dis = (TextView) convertView.findViewById(R.id.itemprice_dis);
            viewHolder.itemprice_carry = (TextView) convertView.findViewById(R.id.itemprice_carry);
            viewHolder.itemprice_chatname = (TextView) convertView.findViewById(R.id.itemprice_chatname);
            viewHolder.itemprice_chattime = (TextView) convertView.findViewById(R.id.itemprice_chattime);
            viewHolder.itemprice_reason = (TextView) convertView.findViewById(R.id.itemprice_reason);
            viewHolder.itemprice_check = (CheckBox) convertView.findViewById(R.id.itemprice_check);
            viewHolder.itemprice_chat_ly = convertView.findViewById(R.id.itemprice_chat_ly);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
        viewHolder.itemprice_name.setText(corpGrabDetailInfo.getOutlet_name());
        viewHolder.itemprice_money.setText("¥" + corpGrabDetailInfo.getPrimary());
        viewHolder.itemprice_code.setText(corpGrabDetailInfo.getOutlet_num());
        if (!Tools.isEmpty(corpGrabDetailInfo.getAccessed_name())) {
            viewHolder.itemprice_nickname.setVisibility(View.VISIBLE);
            viewHolder.itemprice_nickname.setText(corpGrabDetailInfo.getAccessed_name());
            viewHolder.itemprice_nickname.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isClick3 = true;
                    return false;
                }
            });
        } else {
            viewHolder.itemprice_nickname.setVisibility(View.GONE);
        }

        String exe_state = corpGrabDetailInfo.getExe_state();
        if ("2".equals(exe_state)) {//待执行
            viewHolder.itemprice_state.setText("待执行");
            viewHolder.itemprice_carry.setText("立即执行");
        } else if ("3".equals(exe_state)) {//执行中
            viewHolder.itemprice_state.setText("执行中");
            viewHolder.itemprice_carry.setText("立即执行");
        } else if ("9".equals(exe_state)) {//待分配
            viewHolder.itemprice_state.setText("待分配");
        } else if ("10".equals(exe_state)) {//确认中
            viewHolder.itemprice_state.setText("待确认");
            viewHolder.itemprice_carry.setText("接受任务");
        }
        viewHolder.itemprice_addr.setText(corpGrabDetailInfo.getOutlet_address());
        viewHolder.itemprice_carrytime.setText(corpGrabDetailInfo.getTimeDetail());
        String confirm_time = corpGrabDetailInfo.getConfirm_time();
        if (!TextUtils.isEmpty(confirm_time) && !"null".equals(confirm_time)) {
            String time = "已等待队员确认" + confirm_time;
            int start = time.indexOf(confirm_time);
            int end = start + confirm_time.length();
            SpannableStringBuilder builder = new SpannableStringBuilder(time);
            builder.setSpan(new ForegroundColorSpan(convertView.getResources().getColor(R.color.corps_bule)),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.itemprice_waitname.setText(builder);
            viewHolder.itemprice_waitname.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemprice_waitname.setVisibility(View.GONE);
        }
        if (corpGrabDetailInfo.is_haveReason()) {
            viewHolder.itemprice_chat_ly.setVisibility(View.VISIBLE);
            viewHolder.itemprice_chatname.setText(corpGrabDetailInfo.getUser_name());
            viewHolder.itemprice_chattime.setText(corpGrabDetailInfo.getCreate_time());
            if (!Tools.isEmpty(corpGrabDetailInfo.getReason())) {
                viewHolder.itemprice_reason.setText(corpGrabDetailInfo.getReason());
            } else {
                viewHolder.itemprice_reason.setText(corpGrabDetailInfo.getUser_name() + "放弃此任务");
            }
        } else {
            viewHolder.itemprice_chat_ly.setVisibility(View.GONE);
        }
        if (AppInfo.getName(context).equals(corpGrabDetailInfo.getAccessed_num())) {
            viewHolder.itemprice_dis.setVisibility(View.VISIBLE);
            viewHolder.itemprice_carry.setVisibility(View.VISIBLE);
            viewHolder.itemprice_check.setVisibility(View.GONE);
        } else {
            viewHolder.itemprice_dis.setVisibility(View.VISIBLE);
            viewHolder.itemprice_carry.setVisibility(View.GONE);
            viewHolder.itemprice_check.setVisibility(View.GONE);
        }
        if (corpGrabDetailInfo.isShowCheck()) {//显示复选框
            viewHolder.itemprice_check.setVisibility(View.VISIBLE);
            viewHolder.itemprice_carry.setVisibility(View.GONE);
            viewHolder.itemprice_dis.setVisibility(View.GONE);
        }
        viewHolder.itemprice_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        viewHolder.itemprice_carry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick1 = true;
                return false;
            }
        });
        viewHolder.itemprice_dis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick2 = true;
                return false;
            }
        });
        viewHolder.itemprice_check.setChecked(corpGrabDetailInfo.isCheck());
        return convertView;
    }

    class ViewHolder {
        private TextView itemprice_name, itemprice_money, itemprice_code, itemprice_nickname, itemprice_state, itemprice_addr,
                itemprice_carrytime, itemprice_waitname, itemprice_dis, itemprice_carry;
        private CheckBox itemprice_check;
        private View itemprice_chat_ly;
        private TextView itemprice_chatname, itemprice_chattime, itemprice_reason;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }
}
