package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MyRewardInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/3/9.
 */

/**
 * 我的奖励界面适配器
 */
public class MyRewardAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MyRewardInfo> list;

    public MyRewardAdapter(Context context, ArrayList<MyRewardInfo> list) {
        this.context = context;
        this.list = list;
    }

    private boolean isClick1 = false;
    private boolean isClick2 = false;
    private boolean isClick3 = false;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void clearClick() {
        isClick1 = false;
        isClick2 = false;
        isClick3 = false;
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_myreward);
            viewHolder.itemmyreward_pjname = (TextView) convertView.findViewById(R.id.itemmyreward_pjname);
            viewHolder.itemmyreward_name = (TextView) convertView.findViewById(R.id.itemmyreward_name);
            viewHolder.itemmyreward_price = (TextView) convertView.findViewById(R.id.itemmyreward_price);
            viewHolder.itemmyreward_state = (TextView) convertView.findViewById(R.id.itemmyreward_state);
            viewHolder.itemmyreward_fail = convertView.findViewById(R.id.itemmyreward_fail);
//            viewHolder.itemmyreward_fold = (TextView) convertView.findViewById(R.id.itemmyreward_fold);
            viewHolder.itemmyreward_reason = (TextView) convertView.findViewById(R.id.itemmyreward_reason);
            viewHolder.itemmyreward_carry = (TextView) convertView.findViewById(R.id.itemmyreward_carry);
            viewHolder.itemmyreward_overtime = (TextView) convertView.findViewById(R.id.itemmyreward_overtime);
            viewHolder.itemmyreward_yuan = (TextView) convertView.findViewById(R.id.itemmyreward_yuan);
            viewHolder.itemmyreward_layout = convertView.findViewById(R.id.itemmyreward_layout);
           // viewHolder.itemmyreward_layout2 = convertView.findViewById(R.id.itemmyreward_layout2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyRewardInfo myRewardInfo = list.get(position);
        viewHolder.itemmyreward_pjname.setText(myRewardInfo.getProjectName());
        viewHolder.itemmyreward_name.setText(myRewardInfo.getOutletName());
        viewHolder.itemmyreward_yuan.setText(myRewardInfo.getMoney_unit());
        if (myRewardInfo.getState().equals("0")) {//待审核-标题
            viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney2());
            viewHolder.itemmyreward_state.setText("待审核");
            viewHolder.itemmyreward_name.setTextColor(context.getResources().getColor(R.color.myrewardstorid));
            viewHolder.itemmyreward_pjname.setTextColor(context.getResources().getColor(R.color.myrewardproject));
            viewHolder.itemmyreward_price.setTextColor(context.getResources().getColor(R.color.myrewardmoney));
            viewHolder.itemmyreward_state.setTextColor(context.getResources().getColor(R.color.myrewardstate));
        } else if (myRewardInfo.getState().equals("1")) {//审核通过-标题
            viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney2());
            viewHolder.itemmyreward_state.setText("初审通过");
            viewHolder.itemmyreward_name.setTextColor(context.getResources().getColor(R.color.myrewardstorid));
            viewHolder.itemmyreward_pjname.setTextColor(context.getResources().getColor(R.color.myrewardproject));
            viewHolder.itemmyreward_price.setTextColor(context.getResources().getColor(R.color.myrewardmoney));
            viewHolder.itemmyreward_state.setTextColor(context.getResources().getColor(R.color.myrewardstate));
        } else if (myRewardInfo.getState().equals("2")) {//审核未通过-标题
            viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney2());
            reason = myRewardInfo.getOutletDesc();
            viewHolder.itemmyreward_reason.setText(myRewardInfo.getOutletDesc());
            viewHolder.itemmyreward_state.setText("审核不通过");
            viewHolder.itemmyreward_name.setTextColor(context.getResources().getColor(R.color.myrewardstorid));
            viewHolder.itemmyreward_pjname.setTextColor(context.getResources().getColor(R.color.myrewardproject));
            viewHolder.itemmyreward_price.setTextColor(context.getResources().getColor(R.color.myrewardmoney));
            viewHolder.itemmyreward_state.setTextColor(context.getResources().getColor(R.color.myrewardstate));
            viewHolder.itemmyreward_fail.setVisibility(View.VISIBLE);
//            viewHolder.itemmyreward_fold.setText("查看");
//            viewHolder.itemmyreward_fold.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    isClick1 = true;
//                    return false;
//                }
//            });
//            viewHolder.itemmyreward_carry.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    isClick2 = true;
//                    return false;
//                }
//            });
            if (myRewardInfo.getIs_exe().equals("1")) {
                viewHolder.itemmyreward_layout.setVisibility(View.VISIBLE);
                viewHolder.itemmyreward_layout2.setVisibility(View.VISIBLE);
            } else if (myRewardInfo.getIs_exe().equals("0")) {
                viewHolder.itemmyreward_layout.setVisibility(View.GONE);
                viewHolder.itemmyreward_layout2.setVisibility(View.GONE);
            }
            if ("null".equals(myRewardInfo.getExeTime()) || TextUtils.isEmpty(myRewardInfo.getExeTime())) {
                viewHolder.itemmyreward_overtime.setVisibility(View.GONE);
            } else {
                viewHolder.itemmyreward_overtime.setVisibility(View.VISIBLE);
                int time = Tools.StringToInt(myRewardInfo.getExeTime());
                if (time < 60) {
                    viewHolder.itemmyreward_overtime.setText(time + "分");
                } else if (time < 1440 && time >= 60) {
                    int hour = time / 60;
                    int min = time % 60;
                    viewHolder.itemmyreward_overtime.setText(hour + "时" + min + "分");
                } else if (time >= 1440) {
                    int day = time / 1440;
                    int hour = (time % 1440) / 60;
                    int min = time % 60;
                    viewHolder.itemmyreward_overtime.setText(day + "天" + hour + "时" + min + "分");
                }
            }
        } else if (myRewardInfo.getState().equals("3")) {//可提现-标题
            viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney());
            viewHolder.itemmyreward_state.setText("可提现");
            viewHolder.itemmyreward_name.setTextColor(context.getResources().getColor(R.color.myrewardstorid));
            viewHolder.itemmyreward_pjname.setTextColor(context.getResources().getColor(R.color.myrewardproject));
            viewHolder.itemmyreward_price.setTextColor(context.getResources().getColor(R.color.myrewardmoney));
            viewHolder.itemmyreward_state.setTextColor(context.getResources().getColor(R.color.myrewardstate));
            viewHolder.itemmyreward_state.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isClick3 = true;
                    return false;
                }
            });
        } else if (myRewardInfo.getState().equals("4")) {
            viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney());
            viewHolder.itemmyreward_state.setText("待付款");
            viewHolder.itemmyreward_name.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_pjname.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_price.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_state.setTextColor(context.getResources().getColor(R.color.myrewardgray));
        } else if (myRewardInfo.getState().equals("5")) {
            viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney());
            viewHolder.itemmyreward_state.setText("已支付");
            viewHolder.itemmyreward_name.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_pjname.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_price.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_state.setTextColor(context.getResources().getColor(R.color.myrewardgray));
        } else if (myRewardInfo.getState().equals("6")) {
            viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney());
            viewHolder.itemmyreward_state.setText("审核通过");
            viewHolder.itemmyreward_name.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_pjname.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_price.setTextColor(context.getResources().getColor(R.color.myrewardgray));
            viewHolder.itemmyreward_state.setTextColor(context.getResources().getColor(R.color.myrewardgray));
        }
        if (position == list.size() - 1) {
            viewHolder.lastview.setVisibility(View.VISIBLE);
        } else {
            viewHolder.lastview.setVisibility(View.GONE);
        }
        return convertView;
    }


    class ViewHolder {
        private TextView itemmyreward_pjname, itemmyreward_name, itemmyreward_price, itemmyreward_state;
        private View itemmyreward_fail;//审核未通过
//        private TextView itemmyreward_fold;
        private TextView itemmyreward_reason, itemmyreward_carry, itemmyreward_overtime, itemmyreward_yuan;
        private View lastview, itemmyreward_layout, itemmyreward_layout2;
    }
}
