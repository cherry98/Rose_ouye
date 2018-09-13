package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CorpGrabDetailInfo;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;


/**
 * 战队任务队长，队员列表，项目状态
 */

public class TeamMemberTodoAdapter extends BaseAdapter {
    private Context context;
    private ImageLoader imageLoader;
    private ArrayList<CorpGrabDetailInfo> list;
    private boolean isDelet;
    private final int TYPE_1 = 0;
    private final int TYPE_2 = 1;
    private String Type;
    private int delWidth;


    public TeamMemberTodoAdapter(Context context, ArrayList<CorpGrabDetailInfo> list, String type) {
        this.context = context;
        this.list = list;
        this.Type = type;
    }

    public void setDelet(boolean delet) {
        isDelet = delet;
        notifyDataSetChanged();
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

    /**
     * 根据数据列表的position返回需要展示的layout的对应的type
     * type的值必须从0开始
     */
    @Override
    public int getItemViewType(int position) {

        if (Type.equals("1")) {  // "type":"类型，1为众包显示金额，2为分包不显示金额",
            return TYPE_1;
        } else {
            return TYPE_2;
        }
    }

    @Override
    public int getViewTypeCount() { // 返回多少个不同的布局
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder = null;
        ViewHolder2 viewHolder2 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    viewHolder = new ViewHolder1();
                    convertView = Tools.loadLayout(context, R.layout.item_teammember_todo);
                    viewHolder.itemmyreward_pjname = (TextView) convertView.findViewById(R.id.itemmyreward_pjname);
                    viewHolder.itemmyreward_price = (TextView) convertView.findViewById(R.id.itemmyreward_price);
                    //  viewHolder.itemmyreward_name = (TextView) convertView.findViewById(R.id.itemmyreward_name);
                    viewHolder.itemmyreward_num = (TextView) convertView.findViewById(R.id.itemmyreward_num);
                    viewHolder.itemmyreward_addr = (TextView) convertView.findViewById(R.id.itemmyreward_addr);
                    viewHolder.itemmyreward_time = (TextView) convertView.findViewById(R.id.itemmyreward_time);
                    viewHolder.itemmyreward_nickname = (TextView) convertView.findViewById(R.id.itemmyreward_nickname);
                    viewHolder.itemmyreward_state = (TextView) convertView.findViewById(R.id.itemmyreward_state);

                    viewHolder.itemapplyone_runnow = (TextView) convertView.findViewById(R.id.itemapplyone_runnow);
                    viewHolder.itemapplyone_accepts = (TextView) convertView.findViewById(R.id.itemapplyone_accept);
                    viewHolder.item_teammember_del = (TextView) convertView.findViewById(R.id.item_teammember_del); //删除按钮

                    viewHolder.itemprice_chat_ly = (LinearLayout) convertView.findViewById(R.id.itemprice_chat_ly);
                    viewHolder.itemprice_chatname = (TextView) convertView.findViewById(R.id.itemprice_chatname);
                    viewHolder.itemprice_chattime = (TextView) convertView.findViewById(R.id.itemprice_chattime);
                    viewHolder.itemprice_reason = (TextView) convertView.findViewById(R.id.itemprice_reason);
                    viewHolder.item_main = (LinearLayout) convertView.findViewById(R.id.item_main);
                    viewHolder.item_teammember_rightbg = (LinearLayout) convertView.findViewById(R.id.item_teammember_rightbg);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_2:
                    viewHolder2 = new ViewHolder2();
                    convertView = Tools.loadLayout(context, R.layout.item_temmber_noprice);
                    viewHolder2.item_main = (LinearLayout) convertView.findViewById(R.id.item_main);
                    viewHolder2.itemnoprice_name = (TextView) convertView.findViewById(R.id.itemnoprice_name);
                    viewHolder2.itemnoprice_nickname = (TextView) convertView.findViewById(R.id.itemnoprice_nickname);
                    viewHolder2.itemnoprice_state = (TextView) convertView.findViewById(R.id.itemnoprice_state);
                    viewHolder2.itemnoprice_code = (TextView) convertView.findViewById(R.id.itemnoprice_code);
                    viewHolder2.itemnoprice_addr = (TextView) convertView.findViewById(R.id.itemnoprice_addr);
                    viewHolder2.itemnoprice_carry = (TextView) convertView.findViewById(R.id.itemnoprice_carry);
                    viewHolder2.itemnoprice_carrytime = (TextView) convertView.findViewById(R.id.itemnoprice_carrytime);//立即执行
                    viewHolder2.itemnoprice_accept = (TextView) convertView.findViewById(R.id.itemnoprice_accept); //接受任务

                    viewHolder2.itemnoprice_chatname = (TextView) convertView.findViewById(R.id.itemnoprice_chatname);
                    viewHolder2.itemnoprice_chattime = (TextView) convertView.findViewById(R.id.itemnoprice_chattime);
                    viewHolder2.itemnoprice_reason = (TextView) convertView.findViewById(R.id.itemnoprice_reason);
                    viewHolder2.itemnoprice_chat_ly = (LinearLayout) convertView.findViewById(R.id.itemnoprice_chat_ly);
                    viewHolder2.item_teammember_del = (TextView) convertView.findViewById(R.id.item_teammember_del); //删除按钮
                    viewHolder2.item_teammember_rightbg = (LinearLayout) convertView.findViewById(R.id.item_teammember_rightbg);
                    convertView.setTag(viewHolder2);
                    break;
            }
        } else {
            switch (type) {
                case TYPE_1:
                    viewHolder = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }

        // 设置资源
        switch (type) {

            case TYPE_1:
                final CorpGrabDetailInfo corpGrabDetailInfo2 = list.get(position);
                viewHolder.itemmyreward_pjname.setText(corpGrabDetailInfo2.getOutlet_name());
                viewHolder.itemmyreward_price.setText(corpGrabDetailInfo2.getPrimary());
                viewHolder.itemmyreward_num.setText(corpGrabDetailInfo2.getOutlet_num());
                viewHolder.itemmyreward_nickname.setText(corpGrabDetailInfo2.getAccessed_name());
                viewHolder.itemmyreward_time.setText(corpGrabDetailInfo2.getTimeDetail());
                viewHolder.itemmyreward_addr.setText(corpGrabDetailInfo2.getOutlet_address());
                String exe_state = corpGrabDetailInfo2.getExe_state();
                if ("2".equals(exe_state)) {//待执行
                    viewHolder.itemmyreward_state.setText("待执行");
                    viewHolder.itemapplyone_runnow.setVisibility(View.VISIBLE);
                    viewHolder.itemapplyone_accepts.setVisibility(View.GONE);
                } else if ("3".equals(exe_state)) {//执行中
                    viewHolder.itemmyreward_state.setText("执行中");
                    viewHolder.itemapplyone_runnow.setVisibility(View.VISIBLE);
                    viewHolder.itemapplyone_accepts.setVisibility(View.GONE);
                } else if ("9".equals(exe_state) || "1".equals(exe_state)) {//待分配
                    viewHolder.itemmyreward_state.setText("待分配");
                } else if ("10".equals(exe_state)) {//确认中
                    viewHolder.itemmyreward_state.setText("待确认");
                    viewHolder.itemapplyone_accepts.setVisibility(View.VISIBLE);
                    viewHolder.itemapplyone_runnow.setVisibility(View.GONE);
                }
                if (corpGrabDetailInfo2.is_haveReason()) {
                    viewHolder.itemprice_chat_ly.setVisibility(View.VISIBLE);
                    viewHolder.itemprice_chatname.setText(corpGrabDetailInfo2.getUser_name());
                    viewHolder.itemprice_chattime.setText(corpGrabDetailInfo2.getCreate_time());
                    if (!Tools.isEmpty(corpGrabDetailInfo2.getReason())) {
                        viewHolder.itemprice_reason.setText(corpGrabDetailInfo2.getReason());
                    } else {
                        viewHolder.itemprice_reason.setText(corpGrabDetailInfo2.getUser_name() + "放弃此任务");
                    }

                } else {
                    viewHolder.itemprice_chat_ly.setVisibility(View.GONE);
                }

                viewHolder.itemapplyone_runnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //立即执行
                        todoInterface.onCarryclick(position);
                    }
                });
                viewHolder.item_teammember_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除按钮
                        todoInterface.Delete(position, corpGrabDetailInfo2.getOutlet_id());
                    }
                });
                viewHolder.itemapplyone_accepts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //接受任务
                        todoInterface.onAccept(position, corpGrabDetailInfo2.getOutlet_id());
                    }
                });

                if (isDelet) {
                    viewHolder.item_main.scrollTo(220, 0);
                    viewHolder.item_teammember_del.setVisibility(View.VISIBLE);
                    viewHolder.item_teammember_del.setBackgroundResource(R.drawable.item_teammember_del_bg);
                    viewHolder.item_teammember_rightbg.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
                    viewHolder.item_teammember_del.setText("放弃");
                    viewHolder.item_teammember_del.setTextColor(Color.WHITE);
                } else {
                    viewHolder.item_main.scrollTo(0, 0);
                    viewHolder.item_teammember_del.setVisibility(View.GONE);
                    viewHolder.item_teammember_rightbg.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
                }
                break;
            case TYPE_2:
                final CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
                viewHolder2.itemnoprice_name.setText(corpGrabDetailInfo.getOutlet_name());
                ///  accessed_name
                viewHolder2.itemnoprice_nickname.setText(corpGrabDetailInfo.getAccessed_name());
                String exe_state2 = corpGrabDetailInfo.getExe_state();
                if ("2".equals(exe_state2)) {//待执行
                    viewHolder2.itemnoprice_state.setText("待执行");
                    viewHolder2.itemnoprice_carry.setVisibility(View.VISIBLE);
                    viewHolder2.itemnoprice_accept.setVisibility(View.GONE);
                } else if ("3".equals(exe_state2)) {//执行中
                    viewHolder2.itemnoprice_state.setText("执行中");
                    viewHolder2.itemnoprice_carry.setVisibility(View.VISIBLE);
                    viewHolder2.itemnoprice_accept.setVisibility(View.GONE);
                } else if ("9".equals(exe_state2) || "1".equals(exe_state2)) {//待分配
                    viewHolder2.itemnoprice_state.setText("待分配");
                } else if ("10".equals(exe_state2)) {//确认中
                    viewHolder2.itemnoprice_state.setText("待确认");
                    viewHolder2.itemnoprice_carry.setVisibility(View.GONE);
                    viewHolder2.itemnoprice_accept.setVisibility(View.VISIBLE);
                }
                viewHolder2.itemnoprice_code.setText(corpGrabDetailInfo.getOutlet_num());
                viewHolder2.itemnoprice_addr.setText(corpGrabDetailInfo.getOutlet_address());
                viewHolder2.itemnoprice_carrytime.setText(corpGrabDetailInfo.getTimeDetail());

                if (corpGrabDetailInfo.is_haveReason()) {
                    viewHolder2.itemnoprice_chat_ly.setVisibility(View.VISIBLE);
                    viewHolder2.itemnoprice_chatname.setText(corpGrabDetailInfo.getUser_name());
                    viewHolder2.itemnoprice_chattime.setText(corpGrabDetailInfo.getCreate_time());
                    if (!Tools.isEmpty(corpGrabDetailInfo.getReason())) {
                        viewHolder2.itemnoprice_reason.setText(corpGrabDetailInfo.getReason());
                    } else {
                        viewHolder2.itemnoprice_reason.setText(corpGrabDetailInfo.getUser_name() + "放弃此任务");
                    }

                } else {
                    viewHolder2.itemnoprice_chat_ly.setVisibility(View.GONE);
                }

                viewHolder2.itemnoprice_carry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //立即执行
                        todoInterface.onCarryclick(position);
                    }
                });
                viewHolder2.item_teammember_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除按钮
                        todoInterface.Delete(position, corpGrabDetailInfo.getOutlet_id());
                    }
                });
                viewHolder2.itemnoprice_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //接受任务
                        todoInterface.onAccept(position, corpGrabDetailInfo.getOutlet_id());
                    }
                });
                if (isDelet) {
                    viewHolder2.item_main.scrollTo(220, 0);
                    viewHolder2.item_teammember_del.setVisibility(View.VISIBLE);
                    viewHolder2.item_teammember_del.setBackgroundResource(R.drawable.item_teammember_del_bg);
                    viewHolder2.item_teammember_rightbg.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
                    viewHolder2.item_teammember_del.setText("放弃");
                    viewHolder2.item_teammember_del.setTextColor(Color.WHITE);
                } else {
                    viewHolder2.item_main.scrollTo(0, 0);
                    viewHolder2.item_teammember_del.setVisibility(View.GONE);
                    viewHolder2.item_teammember_rightbg.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
                }

                break;
        }
        return convertView;
    }

    class ViewHolder1 {
        private LinearLayout itemprice_chat_ly, item_main, item_teammember_rightbg;
        private TextView itemmyreward_nickname, itemapplyone_accepts, itemapplyone_runnow, item_teammember_del,
                itemmyreward_pjname, itemmyreward_num, itemmyreward_addr, itemmyreward_time,
                itemmyreward_price, itemmyreward_state;//可提现列表参数
        private TextView itemprice_chattime, itemprice_chatname, itemprice_reason;

    }

    class ViewHolder2 {
        LinearLayout itemnoprice_chat_ly, item_main, item_teammember_rightbg;
        TextView itemnoprice_chatname, itemnoprice_chattime, itemnoprice_reason, itemnoprice_accept, item_teammember_del;
        TextView itemnoprice_name, itemnoprice_nickname, itemnoprice_state, itemnoprice_code, itemnoprice_addr, itemnoprice_carry, itemnoprice_carrytime;
    }

    private TeammemberTodoInterface todoInterface;

    public interface TeammemberTodoInterface {
        void onCarryclick(int position);

        void onAccept(int position, String storeid);

        void Delete(int position, String storeid);
    }

    public void setTeammemberTodoListener(TeammemberTodoInterface todoInterface) {
        this.todoInterface = todoInterface;
    }
}
