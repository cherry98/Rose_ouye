package com.orange.oy.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.MyDialog;
import com.orange.oy.info.TaskDetailLeftInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/9/12.
 * 申请的任务页面适配器（待执行，已上传）
 */

public class ApplyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Object> list;
    private AbandonButton abandonButton;
    private String isSwif;
    private int delWidth = 132;

    public ApplyAdapter(Context context, ArrayList<Object> list) {
        this.context = context;
        this.list = list;
        delWidth = (int) context.getResources().getDimension(R.dimen.task_del_width);
    }

    public void setIsSwif(String isSwif1) {
        this.isSwif = isSwif1;
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
            convertView = Tools.loadLayout(context, R.layout.item_applyone);
            viewHolder = new ViewHolder();
            viewHolder.itemapplyone_name = (TextView) convertView.findViewById(R.id.itemapplyone_name);
            viewHolder.itemapplyone_num = (TextView) convertView.findViewById(R.id.itemapplyone_num);
            viewHolder.itemapplyone_addr = (TextView) convertView.findViewById(R.id.itemapplyone_addr);
            viewHolder.itemapplyone_price = (TextView) convertView.findViewById(R.id.itemapplyone_price);
            viewHolder.itemapplyone_yuan = (TextView) convertView.findViewById(R.id.itemapplyone_yuan);
            viewHolder.itemapplyone_looktime = (TextView) convertView.findViewById(R.id.itemapplyone_looktime);
            viewHolder.itemapplyone_overtime = (TextView) convertView.findViewById(R.id.itemapplyone_overtime);
            viewHolder.btnDelete = (TextView) convertView.findViewById(R.id.btnDelete);
            viewHolder.main = (LinearLayout) convertView.findViewById(R.id.main);
            viewHolder.itemapplyone_runnow = (TextView) convertView.findViewById(R.id.itemapplyone_runnow);
            viewHolder.itemapplyone_square = (TextView) convertView.findViewById(R.id.itemapplyone_square);
            viewHolder.swipemenulib = (LinearLayout) convertView.findViewById(R.id.swipemenulib);
            viewHolder.applytag_name = (TextView) convertView.findViewById(R.id.applytag_name);
            viewHolder.itemapplyone_lay1 = (LinearLayout) convertView.findViewById(R.id.itemapplyone_lay1);
            viewHolder.itemapplyone_time_ly = (LinearLayout) convertView.findViewById(R.id.itemapplyone_time_ly);
            viewHolder.imageViewlogo = (ImageView) convertView.findViewById(R.id.imageView3);
            viewHolder.lin_money = (LinearLayout) convertView.findViewById(R.id.lin_money);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);  //小图标

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.swipemenulib.scrollTo(0, 0);


        final TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) list.get(position);

        viewHolder.applytag_name.setText(taskDetailLeftInfo.getProject_name());
        viewHolder.itemapplyone_name.setText(taskDetailLeftInfo.getName());

        viewHolder.itemapplyone_price.setText(taskDetailLeftInfo.getMoney());

        String project_type = taskDetailLeftInfo.getProject_type();
        if (!TextUtils.isEmpty(project_type) && "5".equals(project_type)) {//无店单
            String position_limit = taskDetailLeftInfo.getPosition_limit();
            String limit_province = taskDetailLeftInfo.getLimit_province();
            String limit_city = taskDetailLeftInfo.getLimit_city();
            viewHolder.imageViewlogo.setImageResource(R.mipmap.task_unhavstore);
            viewHolder.itemapplyone_lay1.setVisibility(View.GONE);
            viewHolder.itemapplyone_overtime.setVisibility(View.VISIBLE);
            if ("1".equals(position_limit)) {//有定位限制
                viewHolder.itemapplyone_addr.setText(limit_province + " " + limit_city);
            } else {
                viewHolder.itemapplyone_addr.setText("任意位置");
            }
            viewHolder.itemapplyone_num.setText(taskDetailLeftInfo.getId());
            viewHolder.itemapplyone_square.setVisibility(View.GONE);
            viewHolder.itemapplyone_addr.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(project_type) && "6".equals(project_type)) {//到店红包项目
            viewHolder.imageViewlogo.setImageResource(R.mipmap.task_redpackage);
            viewHolder.itemapplyone_lay1.setVisibility(View.GONE);
            viewHolder.itemapplyone_addr.setVisibility(View.INVISIBLE);
            viewHolder.itemapplyone_looktime.setVisibility(View.GONE);
            viewHolder.itemapplyone_overtime.setVisibility(View.GONE);
            viewHolder.itemapplyone_num.setText(taskDetailLeftInfo.getCity3());
            viewHolder.itemapplyone_square.setVisibility(View.VISIBLE);
            viewHolder.itemapplyone_square.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    abandonButton.onShareSquare(position);
                }
            });
        } else {
            viewHolder.imageViewlogo.setImageResource(R.mipmap.task_havstore);
            viewHolder.itemapplyone_lay1.setVisibility(View.VISIBLE);
            viewHolder.itemapplyone_overtime.setVisibility(View.VISIBLE);
            viewHolder.itemapplyone_addr.setTextColor(convertView.getResources().getColor(R.color.homepage_notselect));
            viewHolder.itemapplyone_addr.setText(taskDetailLeftInfo.getCity3());
            viewHolder.itemapplyone_square.setVisibility(View.GONE);
            viewHolder.itemapplyone_addr.setVisibility(View.VISIBLE);
            viewHolder.itemapplyone_num.setText(taskDetailLeftInfo.getId());
        }
        if (!TextUtils.isEmpty(taskDetailLeftInfo.getTimedetail())) {
            viewHolder.itemapplyone_looktime.setVisibility(View.VISIBLE);
            viewHolder.itemapplyone_looktime.setText(taskDetailLeftInfo.getTimedetail());
            final View finalConvertView = convertView;
            viewHolder.itemapplyone_looktime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = new TextView(context);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(15);
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView.setText("\n可执行时间\n\n" + taskDetailLeftInfo.getTimedetail());
                    textView.setHeight(Tools.getScreeInfoHeight(context) / 2);
                    MyDialog myDialog = new MyDialog((Activity) context, textView, false, 0);
                    myDialog.setMyDialogWidth(Tools.getScreeInfoWidth(context) - 40);
                    myDialog.showAtLocation((finalConvertView.findViewById(R.id.main)),
                            Gravity.CENTER_VERTICAL, 0, 0); //设置layout在PopupWindow中显示的位置
                }
            });
        } else {
            viewHolder.itemapplyone_looktime.setVisibility(View.GONE);
            viewHolder.itemapplyone_looktime.setOnClickListener(null);
        }

        if (taskDetailLeftInfo.getExe_type() == 1 || taskDetailLeftInfo.getHavetime().equals("0")) {
            viewHolder.itemapplyone_overtime.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(taskDetailLeftInfo.getExe_time()) && !"null".equals(taskDetailLeftInfo.getExe_time())) {
            viewHolder.itemapplyone_overtime.setVisibility(View.VISIBLE);
            viewHolder.itemapplyone_overtime.setText("剩余" + taskDetailLeftInfo.getExe_time());
        } else {
            viewHolder.itemapplyone_overtime.setVisibility(View.GONE);
        }
        if (viewHolder.itemapplyone_overtime.getVisibility() == View.GONE && taskDetailLeftInfo.getProject_type().equals("1")) {
            viewHolder.itemapplyone_runnow.setVisibility(View.GONE);
        }
//        if ("6".equals(project_type)) {
//            viewHolder.itemapplyone_time_ly.setVisibility(View.GONE);
//        } else {
//            viewHolder.itemapplyone_time_ly.setVisibility(View.VISIBLE);
//        }

        //  "reward_type":"奖励类型，1为现金，2为礼品，3为现金+礼品",
        if (!TextUtils.isEmpty(taskDetailLeftInfo.getReward_type())) {
            if ("1".equals(taskDetailLeftInfo.getReward_type())) {
                viewHolder.lin_money.setVisibility(View.VISIBLE);
                viewHolder.iv_icon.setImageResource(R.mipmap.grrw_icon_hb);
            } else if ("2".equals(taskDetailLeftInfo.getReward_type())) {
                viewHolder.iv_icon.setImageResource(R.mipmap.rw_button_liwu);
                viewHolder.lin_money.setVisibility(View.GONE);
            } else {
                viewHolder.lin_money.setVisibility(View.VISIBLE);
                viewHolder.iv_icon.setImageResource(R.mipmap.rw_button_liwu);
            }
        }

        if (!TextUtils.isEmpty(isSwif)) {
            if (isSwif.equals("1")) {
                viewHolder.btnDelete.setVisibility(View.VISIBLE);
                viewHolder.swipemenulib.scrollTo(delWidth, 0);
                viewHolder.itemapplyone_runnow.setVisibility(View.GONE);
                viewHolder.main.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
            } else {
                viewHolder.swipemenulib.scrollTo(0, 0);
                viewHolder.itemapplyone_runnow.setVisibility(View.VISIBLE);
                viewHolder.main.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
            }
        }

        viewHolder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abandonButton.onitemclick(position);
            }
        });
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "删除:" + position, Toast.LENGTH_SHORT).show();
                //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                // viewHolder.swipemenulib.quickClose();
                abandonButton.onclick(position);
            }
        });

        return convertView;
    }


    class ViewHolder {
        private TextView applytag_name, itemapplyone_runnow, itemapplyone_name, itemapplyone_num, itemapplyone_addr, itemapplyone_price, itemapplyone_yuan,
                itemapplyone_looktime, itemapplyone_overtime, itemapplyone_square;
        private TextView btnDelete;
        private LinearLayout main, itemapplyone_lay1, itemapplyone_time_ly;
        private LinearLayout swipemenulib;
        private ImageView imageViewlogo, iv_icon;
        private LinearLayout lin_money;
    }

    public interface AbandonButton {
        void onclick(int position);

        void onShareSquare(int position);

        void onitemclick(int position);

        void onRightitemclick(int position);
    }

    public void setAbandonButtonListener(AbandonButton abandonButton) {
        this.abandonButton = abandonButton;
    }

}
