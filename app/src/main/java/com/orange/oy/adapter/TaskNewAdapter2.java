package com.orange.oy.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;


/**
 * Created by Lenovo on 2018/2/5.
 */

public class TaskNewAdapter2 extends BaseAdapter {
    private Context context;
    private ArrayList<TaskNewInfo> list;
    private boolean isClick1;
    private boolean isClick2;
    private ImageLoader imageLoader;

    public TaskNewAdapter2(Context context, ArrayList<TaskNewInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    public boolean isClick1() {
        return isClick1;
    }

    public boolean isClick2() {
        return isClick2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    public void clearClick() {
        isClick1 = false;
        isClick2 = false;
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
            convertView = Tools.loadLayout(context, R.layout.item_alltask3);
            viewHolder.itemalltask_name = (TextView) convertView.findViewById(R.id.itemalltask_name);
            viewHolder.itemalltask_person = (TextView) convertView.findViewById(R.id.itemalltask_person);
            viewHolder.itemalltask_time = (TextView) convertView.findViewById(R.id.itemalltask_time);
            viewHolder.itemalltask_money = (TextView) convertView.findViewById(R.id.itemalltask_money);
            // viewHolder.itemalltask_apply = (TextView) convertView.findViewById(R.id.itemalltask_apply);
            viewHolder.itemalltask_check = (TextView) convertView.findViewById(R.id.itemalltask_check);
            viewHolder.itemalltask_check1 = (TextView) convertView.findViewById(R.id.itemalltask_check1);
            viewHolder.itemalltask_img = (ImageView) convertView.findViewById(R.id.itemalltask_img);
            viewHolder.itemalltask_img2 = (ImageView) convertView.findViewById(R.id.itemalltask_img2); //已抢完图标
            viewHolder.item_main = (LinearLayout) convertView.findViewById(R.id.item_main);
            viewHolder.itemalltask_url = (SimpleDraweeView) convertView.findViewById(R.id.itemalltask_url);
            viewHolder.itemalltask_bg = (FrameLayout) convertView.findViewById(R.id.itemalltask_bg);
            viewHolder.itemalltask_top = (ImageView) convertView.findViewById(R.id.itemalltask_top);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskNewInfo taskNewInfo = list.get(position);
        viewHolder.itemalltask_url.setTag(taskNewInfo.getGift_url());
        viewHolder.itemalltask_url.setImageBitmap(null);

        // "rob_state":"是否可领取，1为可以领取，0为已抢完"
        if (!Tools.isEmpty(taskNewInfo.getRob_state()) && "0".equals(taskNewInfo.getRob_state())) { //是否置灰
            viewHolder.itemalltask_img2.setVisibility(View.VISIBLE);
            viewHolder.itemalltask_bg.setVisibility(View.GONE);
            viewHolder.itemalltask_check1.setVisibility(View.VISIBLE);
            viewHolder.itemalltask_check.setVisibility(View.GONE);
//            viewHolder.itemalltask_check.setBackgroundResource(R.drawable.dialog_upload8);
//            viewHolder.itemalltask_check.setTextColor(Color.parseColor("#A0A0A0"));
            viewHolder.item_main.setBackgroundResource(R.drawable.unify_input);
            // viewHolder.itemalltask_apply.setVisibility(View.GONE);
        } else {
            viewHolder.itemalltask_check1.setVisibility(View.GONE);
            viewHolder.itemalltask_check.setVisibility(View.VISIBLE);
            //  viewHolder.itemalltask_apply.setVisibility(View.VISIBLE);
            viewHolder.itemalltask_img2.setVisibility(View.GONE);
            viewHolder.itemalltask_bg.setVisibility(View.VISIBLE);
//            viewHolder.itemalltask_check.setBackgroundResource(R.drawable.dialog_upload3);
//            viewHolder.itemalltask_check.setTextColor(Color.parseColor("#F65D57"));
            viewHolder.item_main.setBackgroundResource(R.drawable.itemalltask_background);
        }

        if ("1".equals(taskNewInfo.getProject_model())) {
            if ("6".equals(taskNewInfo.getType())) {
                viewHolder.itemalltask_img.setImageResource(R.mipmap.task_redpackage);
            } else {
                viewHolder.itemalltask_img.setImageResource(R.mipmap.itemcorps_task1);
            }
        } else {
            viewHolder.itemalltask_img.setImageResource(R.mipmap.itemcorps_task2);
        }
        viewHolder.itemalltask_name.setText(taskNewInfo.getProject_name());
        viewHolder.itemalltask_person.setText("商家:" + taskNewInfo.getProject_person() + "");
        if ("null".equals(taskNewInfo.getPublish_time()) || TextUtils.isEmpty(taskNewInfo.getPublish_time())) {
            viewHolder.itemalltask_time.setVisibility(View.GONE);
        } else {
            viewHolder.itemalltask_time.setText(taskNewInfo.getPublish_time());
            viewHolder.itemalltask_time.setVisibility(View.VISIBLE);
        }

        // "reward_type":"奖励类型，1为现金，2为礼品，3为现金+礼品",
        if (!TextUtils.isEmpty(taskNewInfo.getReward_type())) {
            String url = taskNewInfo.getGift_url();
            if ("1".equals(taskNewInfo.getReward_type())) {
                viewHolder.itemalltask_top.setVisibility(View.GONE);
                viewHolder.itemalltask_money.setText("¥" + taskNewInfo.getMin_reward() + "-" + taskNewInfo.getMax_reward());
                if (viewHolder.itemalltask_url.getTag().equals(url)) {
                    viewHolder.itemalltask_url.setBackgroundResource(R.mipmap.chai_button_ling);
                }

            } else if ("2".equals(taskNewInfo.getReward_type())) {
                viewHolder.itemalltask_top.setVisibility(View.GONE);
                viewHolder.itemalltask_money.setText("");
                if (!TextUtils.isEmpty(url)) {
                    if (viewHolder.itemalltask_url.getTag().equals(url)) {
                        url = Urls.Endpoint3 + url + "?x-oss-process=image/resize,m_fill,h_100,w_100";
                        Uri uri = Uri.parse(url);
                        viewHolder.itemalltask_url.setImageURI(uri);
                    }
                }

            } else if ("3".equals(taskNewInfo.getReward_type())) {
                viewHolder.itemalltask_money.setText("¥" + taskNewInfo.getMin_reward() + "-" + taskNewInfo.getMax_reward());
                if (!TextUtils.isEmpty(url)) {
                    if (viewHolder.itemalltask_url.getTag().equals(url)) {
                        url = Urls.Endpoint3 + url + "?x-oss-process=image/resize,m_fill,h_100,w_100";
                        Uri uri = Uri.parse(url);
                        viewHolder.itemalltask_url.setImageURI(uri);
                    }
                }
                viewHolder.itemalltask_top.setVisibility(View.VISIBLE);
            }
        }


        viewHolder.itemalltask_money.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick1 = true;
                return false;
            }
        });
        viewHolder.itemalltask_check.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick2 = true;
                return false;
            }
        });
        if ("1".equals(taskNewInfo.getAnonymous_state())) {//不显示发布商家
            viewHolder.itemalltask_person.setVisibility(View.GONE);
        } else {
            viewHolder.itemalltask_person.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemalltask_name, itemalltask_person, itemalltask_time,
                itemalltask_money, itemalltask_check, itemalltask_check1;
        private ImageView itemalltask_img, itemalltask_img2, itemalltask_top;
        private LinearLayout item_main;
        private SimpleDraweeView itemalltask_url;
        private FrameLayout itemalltask_bg;
    }
}
