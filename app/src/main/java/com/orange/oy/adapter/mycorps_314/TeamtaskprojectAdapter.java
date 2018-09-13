package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.ProjectStateInfo;
import com.orange.oy.view.SpreadTextView;

import java.util.ArrayList;


/**
 * 战队任务队长，队员列表，项目状态
 */

public class TeamtaskprojectAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ProjectStateInfo> projectStateInfoList;
    private String state;
    private final int TYPE_1 = 0;
    private final int TYPE_2 = 1;
    private final int TYPE_3 = 2;
    private String identity; //是否为队长
    private String Type;


    public TeamtaskprojectAdapter(Context context, ArrayList<ProjectStateInfo> projectStateInfoList, String type, String identity) {
        this.context = context;
        this.projectStateInfoList = projectStateInfoList;
        this.Type = type;
        this.identity = identity;
    }

    @Override
    public int getCount() {
        return projectStateInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return projectStateInfoList.get(position);
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
        ProjectStateInfo projectStateInfo = projectStateInfoList.get(position);
        state = projectStateInfo.getState(); //-1为上传中，0为审核中，2为未通过，3为已通过",
        if (Type.equals("1")) {  // "type":"类型，1为众包显示金额，2为分包不显示金额",
            if (state.equals("2")) {
                return TYPE_1;
            } else {
                return TYPE_2;
            }
        } else {
            if (state.equals("2")) {
                return TYPE_1;
            } else {
                return TYPE_3;
            }
        }

    }

    @Override
    public int getViewTypeCount() { // 返回多少个不同的布局
        return 3;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder1 viewHolder = null;
        ViewHolder2 viewHolder2 = null;
        ViewHolder3 viewHolder3 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:
                    convertView = Tools.loadLayout(context, R.layout.item_taskprojectstate);
                    viewHolder = new ViewHolder1();
                    viewHolder.tv_isno = (TextView) convertView.findViewById(R.id.tv_isno);
                    viewHolder.itemnoprice_distribute = (TextView) convertView.findViewById(R.id.itemnoprice_distribute); //重新分配
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
                    viewHolder.itemmyreward_layout = convertView.findViewById(R.id.itemmyreward_layout);
                    viewHolder.itemmyreward_time = (TextView) convertView.findViewById(R.id.itemmyreward_time);
                    viewHolder.imageView3 = (ImageView) convertView.findViewById(R.id.imageView3);
                    viewHolder.itemmyreward_img = (ImageView) convertView.findViewById(R.id.itemmyreward_img);
                    viewHolder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
                    viewHolder.itemnoprice_nickname = (TextView) convertView.findViewById(R.id.itemnoprice_nickname);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_2:

                    convertView = Tools.loadLayout(context, R.layout.item_team_project_state);
                    viewHolder2 = new ViewHolder2();
                    viewHolder2.itemmyreward_state = (TextView) convertView.findViewById(R.id.itemmyreward_state);
                    viewHolder2.itemmyreward_pjname = (TextView) convertView.findViewById(R.id.itemmyreward_pjname);
                    viewHolder2.itemmyreward_price = (TextView) convertView.findViewById(R.id.itemmyreward_price);
                    viewHolder2.itemmyreward_num = (TextView) convertView.findViewById(R.id.itemmyreward_num);
                    viewHolder2.itemmyreward_addr = (TextView) convertView.findViewById(R.id.itemmyreward_addr);
                    viewHolder2.itemmyreward_time = (TextView) convertView.findViewById(R.id.itemmyreward_time);
                    viewHolder2.itemnoprice_nickname = (TextView) convertView.findViewById(R.id.itemnoprice_nickname);
                    convertView.setTag(viewHolder2);
                    break;
                case TYPE_3:
                    convertView = Tools.loadLayout(context, R.layout.item_projectnoprice);
                    viewHolder3 = new ViewHolder3();
                    viewHolder3.itemnoprice_name = (TextView) convertView.findViewById(R.id.itemnoprice_name);
                    viewHolder3.itemnoprice_nickname = (TextView) convertView.findViewById(R.id.itemnoprice_nickname);
                    viewHolder3.itemnoprice_state = (TextView) convertView.findViewById(R.id.itemnoprice_state);
                    viewHolder3.itemnoprice_code = (TextView) convertView.findViewById(R.id.itemnoprice_code);
                    viewHolder3.itemnoprice_addr = (TextView) convertView.findViewById(R.id.itemnoprice_addr);
                    viewHolder3.itemnoprice_carrytime = (TextView) convertView.findViewById(R.id.itemnoprice_carrytime);
                    convertView.setTag(viewHolder3);
                    break;
                default:
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
                case TYPE_3:
                    viewHolder3 = (ViewHolder3) convertView.getTag();
                    break;
            }
        }
        // 设置资源
        switch (type) {

            case TYPE_1:     //未通过的状态
               /* viewHolder.itemmyreward_reason.setDescType2("车确认发二维分为非人文氛围飞飞涂鸦跳跃突然一额风热我认为二而且惹我热热热热" +
                        "我去条条同一部分的白癜风郭德纲得分王天人", true);*/
                final ProjectStateInfo myRewardInfo = projectStateInfoList.get(position);
                state = myRewardInfo.getState();
                viewHolder.itemmyreward_pjname.setText(myRewardInfo.getProject_name());
                viewHolder.itemmyreward_name.setText(myRewardInfo.getOutlet_name());
                viewHolder.itemmyreward_num.setText(myRewardInfo.getOutlet_num());

                viewHolder.itemmyreward_time.setText(myRewardInfo.getTimedetail());
                if (!TextUtils.isEmpty(myRewardInfo.getOutlet_address()) || !myRewardInfo.getOutlet_address().equals("")) {
                    viewHolder.itemmyreward_addr.setVisibility(View.VISIBLE);
                    viewHolder.itemmyreward_addr.setText(myRewardInfo.getOutlet_address());
                } else {
                    viewHolder.itemmyreward_addr.setVisibility(View.GONE);
                }

                if (!Tools.isEmpty(myRewardInfo.getAccessed_name())) {
                    viewHolder.itemnoprice_nickname.setVisibility(View.VISIBLE);
                    viewHolder.itemnoprice_nickname.setText(myRewardInfo.getAccessed_name());
                    viewHolder.itemnoprice_nickname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            abandonUnpass.onInfo(position);
                        }
                    });
                } else {
                    viewHolder.itemnoprice_nickname.setVisibility(View.GONE);
                }

                if (Type.equals("2")) {
                    viewHolder.tv_isno.setVisibility(View.GONE);
                    viewHolder.itemmyreward_price.setVisibility(View.GONE);
                } else {
                    viewHolder.tv_isno.setVisibility(View.VISIBLE);
                    viewHolder.itemmyreward_price.setText(myRewardInfo.getMoney());
                }

                viewHolder.itemmyreward_state.setText("未通过");
                viewHolder.itemmyreward_fail.setVisibility(View.VISIBLE); //未通过原因

                if (identity.equals("1")) {  //是队长

                } else {//是队员

                }
                if (myRewardInfo.getIs_exe().equals("0")) {   // "is_exe": "是否可执行，1为可执行，0为不可执行"
                    viewHolder.tv_reason.setText("此任务为不通过不可重新执行，则此任务的奖励金为0");
                    viewHolder.itemmyreward_carry.setVisibility(View.GONE);
                    viewHolder.itemmyreward_abondon.setVisibility(View.GONE);
                    if (myRewardInfo.getIs_distribute().equals("1")) {  // 是否可分配，1为可以，0为不可以
                        viewHolder.itemnoprice_distribute.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.itemnoprice_distribute.setVisibility(View.GONE);
                    }

                } else {
                    viewHolder.tv_reason.setText("可执行时间内未完成重做，则表示放弃该任务奖金");
                    viewHolder.itemmyreward_carry.setVisibility(View.VISIBLE);
                    viewHolder.itemmyreward_abondon.setVisibility(View.VISIBLE);
                    viewHolder.itemnoprice_distribute.setVisibility(View.GONE);
                }

                if (TextUtils.isEmpty(myRewardInfo.getUnpass_reason()) || myRewardInfo.getUnpass_reason().equals("null")) {
                    viewHolder.itemmyreward_reason.setVisibility(View.GONE);
                } else {
                    viewHolder.itemmyreward_reason.setVisibility(View.VISIBLE);
                    viewHolder.itemmyreward_reason.setDescType2(myRewardInfo.getUnpass_reason(), true);
                }

                viewHolder.itemmyreward_abondon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //放弃按钮
                        abandonUnpass.onAnondonclick(position, myRewardInfo.getOutlet_id());
                    }
                });

                //重做按钮
                viewHolder.itemmyreward_carry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abandonUnpass.onRest(position, myRewardInfo.getOutlet_id());
                    }
                });

                viewHolder.itemnoprice_distribute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abandonUnpass.onDistribute(position, myRewardInfo.getOutlet_id());
                    }
                });


                break;
            case TYPE_2:
                ProjectStateInfo myRewardInfo2 = projectStateInfoList.get(position);
                state = myRewardInfo2.getState();
                viewHolder2.itemmyreward_price.setText(myRewardInfo2.getMoney());
                viewHolder2.itemmyreward_pjname.setText(myRewardInfo2.getProject_name());
                viewHolder2.itemmyreward_num.setText(myRewardInfo2.getOutlet_num());
                viewHolder2.itemmyreward_time.setText(myRewardInfo2.getTimedetail());

                if (!Tools.isEmpty(myRewardInfo2.getOutlet_address())) {
                    viewHolder2.itemmyreward_addr.setVisibility(View.VISIBLE);
                    viewHolder2.itemmyreward_addr.setText(myRewardInfo2.getOutlet_address());
                } else {
                    viewHolder2.itemmyreward_addr.setVisibility(View.GONE);
                }

                if (!Tools.isEmpty(myRewardInfo2.getAccessed_name())) {
                    viewHolder2.itemnoprice_nickname.setVisibility(View.VISIBLE);
                    viewHolder2.itemnoprice_nickname.setText(myRewardInfo2.getAccessed_name());
                    viewHolder2.itemnoprice_nickname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            abandonUnpass.onInfo(position);
                        }
                    });
                } else {
                    viewHolder2.itemnoprice_nickname.setVisibility(View.GONE);
                }

                //----  "state": 2,//状态，-1为上传中，0为审核中，2为未通过，3为已通过
                if (state.equals("-1")) {
                    viewHolder2.itemmyreward_state.setText("上传中");
                } else if (state.equals("0")) {
                    viewHolder2.itemmyreward_state.setText("审核中");
                } else if (state.equals("3")) {
                    viewHolder2.itemmyreward_state.setText("已通过");
                }
                break;
            case TYPE_3:
                ProjectStateInfo myRewardInfo3 = projectStateInfoList.get(position);
                state = myRewardInfo3.getState();
                viewHolder3.itemnoprice_name.setText(myRewardInfo3.getProject_name());
                viewHolder3.itemnoprice_code.setText(myRewardInfo3.getOutlet_num());
                viewHolder3.itemnoprice_carrytime.setText(myRewardInfo3.getTimedetail());
                viewHolder3.itemnoprice_addr.setText(myRewardInfo3.getOutlet_address());
                if (!Tools.isEmpty(myRewardInfo3.getAccessed_name())) {
                    viewHolder3.itemnoprice_nickname.setVisibility(View.VISIBLE);
                    viewHolder3.itemnoprice_nickname.setText(myRewardInfo3.getAccessed_name());
                    viewHolder3.itemnoprice_nickname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            abandonUnpass.onInfo(position);
                        }
                    });
                } else {
                    viewHolder3.itemnoprice_nickname.setVisibility(View.GONE);
                }

                //----  "state": 2,//状态，-1为上传中，0为审核中，2为未通过，3为已通过
                if (state.equals("-1")) {
                    viewHolder3.itemnoprice_state.setText("上传中");
                } else if (state.equals("0")) {
                    viewHolder3.itemnoprice_state.setText("审核中");
                } else if (state.equals("3")) {
                    viewHolder3.itemnoprice_state.setText("已通过");
                }
                break;
        }
        return convertView;
    }

    private class ViewHolder1 {
        private TextView itemmyreward_pjname, itemmyreward_name, itemnoprice_nickname, itemmyreward_num, itemmyreward_addr, itemmyreward_time,
                itemmyreward_price, itemmyreward_yuan, itemmyreward_state, tv_reason, tv_isno;
        private View itemmyreward_fail;//不通过原因view

        private TextView itemmyreward_carry, itemnoprice_distribute, itemmyreward_overtime, itemmyreward_abondon, itemmyreward_checktime;
        private View itemmyreward_layout;
        private SpreadTextView itemmyreward_reason;
        private ImageView imageView3, itemmyreward_img;
    }

    private class ViewHolder2 {
        private TextView itemmyreward_pjname, itemmyreward_price, itemnoprice_nickname, itemmyreward_num, itemmyreward_addr, itemmyreward_time, itemmyreward_state;
    }

    private class ViewHolder3 {
        private TextView itemnoprice_name, itemmyreward_num, itemnoprice_nickname, itemnoprice_state, itemnoprice_code, itemnoprice_addr,
                itemnoprice_carrytime;
    }

    private AbandonUnpass abandonUnpass;

    public interface AbandonUnpass {
        void onAnondonclick(int position, String storeid);

        void onDistribute(int position, String storeid);

        void onRest(int position, String storeid);

        void onInfo(int position);
    }

    public void setAbandonButtonListener(AbandonUnpass abandonUnpass) {
        this.abandonUnpass = abandonUnpass;
    }
}



