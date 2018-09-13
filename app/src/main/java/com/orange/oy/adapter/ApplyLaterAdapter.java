package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.FindleftInfo;
import com.orange.oy.info.MyRewardInfo;
import com.orange.oy.view.SpreadTextView;

import java.util.ArrayList;


/**
 * Created by xiedongyan on 2017/9/14.
 * 申请的任务页面适配器（未通过，可提现）
 */

public class ApplyLaterAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MyRewardInfo> list;
    private boolean isClick1;
    private boolean isClick2;

    String state;

    private boolean isClick3;


    public ApplyLaterAdapter(Context context, ArrayList<MyRewardInfo> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_myreward);
            viewHolder.itemmyreward_pjname = (TextView) convertView.findViewById(R.id.itemmyreward_pjname);
            viewHolder.itemmyreward_name = (TextView) convertView.findViewById(R.id.itemmyreward_name);
            viewHolder.itemmyreward_num = (TextView) convertView.findViewById(R.id.itemmyreward_num);
            viewHolder.itemmyreward_addr = (TextView) convertView.findViewById(R.id.itemmyreward_addr);
            viewHolder.itemmyreward_price = (TextView) convertView.findViewById(R.id.itemmyreward_price);
            viewHolder.itemmyreward_yuan = (TextView) convertView.findViewById(R.id.itemmyreward_yuan);
            viewHolder.itemmyreward_state = (TextView) convertView.findViewById(R.id.itemmyreward_state);
            viewHolder.itemmyreward_fail = convertView.findViewById(R.id.itemmyreward_fail);
            viewHolder.itemmyreward_reason = (SpreadTextView) convertView.findViewById(R.id.itemmyreward_reason);
            viewHolder.itemmyreward_carry = (TextView) convertView.findViewById(R.id.itemmyreward_carry);
            viewHolder.itemmyreward_abondon = (TextView) convertView.findViewById(R.id.itemmyreward_abondon);
            viewHolder.itemmyreward_checktime = (TextView) convertView.findViewById(R.id.itemmyreward_checktime);
            viewHolder.itemmyreward_overtime = (TextView) convertView.findViewById(R.id.itemmyreward_overtime);
            viewHolder.itemmyreward_Withdraw = (TextView) convertView.findViewById(R.id.itemmyreward_Withdraw); //提现按钮
            viewHolder.itemmyreward_layout = convertView.findViewById(R.id.itemmyreward_layout);
            viewHolder.itemmyreward_time = (TextView) convertView.findViewById(R.id.itemmyreward_time);
            viewHolder.imageView3 = (ImageView) convertView.findViewById(R.id.imageView3);
            viewHolder.itemmyreward_img = (ImageView) convertView.findViewById(R.id.itemmyreward_img);
            viewHolder.lin_money = (LinearLayout) convertView.findViewById(R.id.lin_money);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);  //小图标
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyRewardInfo myRewardInfo = list.get(position);
        state = myRewardInfo.getState();
        viewHolder.itemmyreward_pjname.setText(myRewardInfo.getProjectName());
        viewHolder.itemmyreward_name.setText(myRewardInfo.getOutletName());
        viewHolder.itemmyreward_num.setText(myRewardInfo.getOutletId());

        viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney2());
        viewHolder.itemmyreward_yuan.setText(myRewardInfo.getMoney_unit());
        viewHolder.itemmyreward_time.setText(myRewardInfo.getBegin_date() + "~" + myRewardInfo.getEnd_date());
        //  "reward_type":"奖励类型，1为现金，2为礼品，3为现金+礼品",
        if (!TextUtils.isEmpty(myRewardInfo.getReward_type())) {
            if ("1".equals(myRewardInfo.getReward_type())) {
                viewHolder.lin_money.setVisibility(View.VISIBLE);
                viewHolder.iv_icon.setImageResource(R.mipmap.grrw_icon_hb);
            } else if ("2".equals(myRewardInfo.getReward_type())) {
                viewHolder.iv_icon.setImageResource(R.mipmap.rw_button_liwu);
                viewHolder.lin_money.setVisibility(View.GONE);
            } else {
                viewHolder.lin_money.setVisibility(View.VISIBLE);
                viewHolder.iv_icon.setImageResource(R.mipmap.rw_button_liwu);
            }
        }

        if ("5".equals(myRewardInfo.getType())) {//无店单
            viewHolder.imageView3.setImageResource(R.mipmap.task_unhavstore);
            viewHolder.itemmyreward_img.setVisibility(View.GONE);
            viewHolder.itemmyreward_name.setVisibility(View.GONE);
            String position_limit = myRewardInfo.getPosition_limit();
            String limit_province = myRewardInfo.getLimit_province();
            String limit_city = myRewardInfo.getLimit_city();
            if ("1".equals(position_limit)) {//有定位限制
                viewHolder.itemmyreward_addr.setText(limit_province + " " + limit_city);
            } else {
                viewHolder.itemmyreward_addr.setText("任意位置");
            }
        } else {
            viewHolder.itemmyreward_img.setVisibility(View.VISIBLE);
            viewHolder.itemmyreward_name.setVisibility(View.VISIBLE);
            viewHolder.imageView3.setImageResource(R.mipmap.task_havstore);
            if (!TextUtils.isEmpty(myRewardInfo.getOutlet_address())) {
                viewHolder.itemmyreward_addr.setVisibility(View.VISIBLE);
                viewHolder.itemmyreward_addr.setText(myRewardInfo.getOutlet_address());
            } else {
                viewHolder.itemmyreward_addr.setVisibility(View.GONE);
            }
        }

        //----  "state": 2,//状态，-1为上传中，0为审核中，2为未通过，3为已通过

        if (state.equals("2")) {

            //未通过
            viewHolder.itemmyreward_state.setText("未通过");
            viewHolder.itemmyreward_Withdraw.setVisibility(View.GONE);
            viewHolder.itemmyreward_fail.setVisibility(View.VISIBLE); //未通过原因
            if (myRewardInfo.getIs_exe().equals("0")) {     //是否可执行，1为可执行，0为不可执行
                viewHolder.itemmyreward_layout.setVisibility(View.GONE);
                viewHolder.itemmyreward_carry.setVisibility(View.GONE);
                viewHolder.itemmyreward_abondon.setVisibility(View.GONE);
            } else {
                viewHolder.itemmyreward_carry.setVisibility(View.VISIBLE);
                viewHolder.itemmyreward_abondon.setVisibility(View.VISIBLE);
            }
            viewHolder.itemmyreward_reason.setDesc(myRewardInfo.getOutletDesc());
            //viewHolder.itemmyreward_reason.setDesc("车确认发二维分为非人文氛围飞飞涂鸦跳跃突然一额风热我认为二而且惹我热热热热我去条条同一部分的白癜风郭德纲得分王天人");
            viewHolder.itemmyreward_overtime.setText(myRewardInfo.getExeTime());  //剩余执行时间，单位为分钟，数字类型
            viewHolder.itemmyreward_abondon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //放弃按钮
                    abandonUnpass.onAnondonclick(position);
                }
            });
            if ("0".equals(myRewardInfo.getUnpass_state())) {//已超时没有重做 放弃
                viewHolder.itemmyreward_abondon.setVisibility(View.GONE);
                viewHolder.itemmyreward_carry.setVisibility(View.GONE);
                viewHolder.itemmyreward_abondon.setOnTouchListener(null);
                viewHolder.itemmyreward_carry.setOnTouchListener(null);
                viewHolder.itemmyreward_overtime.setText("已超时");
                viewHolder.itemmyreward_layout.setVisibility(View.VISIBLE);
            } else if ("2".equals(myRewardInfo.getUnpass_state())) {
                viewHolder.itemmyreward_layout.setVisibility(View.GONE);
            } else {
                viewHolder.itemmyreward_layout.setVisibility(View.VISIBLE);
            }
            if ("1".equals(myRewardInfo.getIs_exe()) && !TextUtils.isEmpty(myRewardInfo.getExeTime())) {
                viewHolder.itemmyreward_layout.setVisibility(View.VISIBLE);
            } else {
                viewHolder.itemmyreward_layout.setVisibility(View.GONE);
            }

        } else if (state.equals("-1")) {
            //-1为上传中
            viewHolder.itemmyreward_state.setText("上传中");
            viewHolder.itemmyreward_fail.setVisibility(View.GONE);
            viewHolder.itemmyreward_layout.setVisibility(View.GONE);
            viewHolder.itemmyreward_carry.setVisibility(View.GONE);
            viewHolder.itemmyreward_abondon.setVisibility(View.GONE);
            viewHolder.itemmyreward_Withdraw.setVisibility(View.GONE);

        } else if (state.equals("0")) {

            //0为审核中
            viewHolder.itemmyreward_state.setText("审核中");
            viewHolder.itemmyreward_fail.setVisibility(View.GONE);
            viewHolder.itemmyreward_layout.setVisibility(View.GONE);
            viewHolder.itemmyreward_carry.setVisibility(View.GONE);
            viewHolder.itemmyreward_abondon.setVisibility(View.GONE);
            viewHolder.itemmyreward_checktime.setVisibility(View.VISIBLE);
            viewHolder.itemmyreward_Withdraw.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(myRewardInfo.getCheck_time())) {
                viewHolder.itemmyreward_checktime.setText(myRewardInfo.getCheck_time() + "个工作日内审核完成");
            } else {
                viewHolder.itemmyreward_checktime.setVisibility(View.GONE);
            }


        } else if (state.equals("3")) {
            //3为已通过
            viewHolder.itemmyreward_state.setText("已通过");
            viewHolder.itemmyreward_fail.setVisibility(View.GONE);
            viewHolder.itemmyreward_layout.setVisibility(View.GONE);
            viewHolder.itemmyreward_carry.setVisibility(View.GONE);
            viewHolder.itemmyreward_abondon.setVisibility(View.GONE);
            viewHolder.itemmyreward_Withdraw.setVisibility(View.GONE);

            viewHolder.itemmyreward_Withdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //提现按钮
                    abandonUnpass.onWithdraw(position);
                }
            });


        }
        //重做按钮
        viewHolder.itemmyreward_carry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick2 = true;
                return false;
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView itemmyreward_pjname, itemmyreward_name, itemmyreward_num, itemmyreward_addr, itemmyreward_time,
                itemmyreward_price, itemmyreward_yuan, itemmyreward_state;//可提现列表参数
        private View itemmyreward_fail;//不通过原因view

        private TextView itemmyreward_carry, itemmyreward_overtime, itemmyreward_abondon, itemmyreward_checktime, itemmyreward_Withdraw;
        private View itemmyreward_layout;
        private SpreadTextView itemmyreward_reason;
        private ImageView imageView3, itemmyreward_img, iv_icon;
        private LinearLayout lin_money;
    }

    private AbandonUnpass abandonUnpass;

    public interface AbandonUnpass {
        void onAnondonclick(int position);

        void onWithdraw(int position);
    }

    public void setAbandonButtonListener(AbandonUnpass abandonUnpass) {
        this.abandonUnpass = abandonUnpass;
    }
}
