package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.TaskListInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/6/26.
 * 任务内容 V3.17
 */

public class TaskContentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TaskListInfo> list;
    private boolean isDelete;
    private boolean isClick1;
    private boolean isClick2;
    private boolean isClick3;

    public TaskContentAdapter(Context context, ArrayList<TaskListInfo> list) {
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

    public void setDelete(boolean delete) {
        isDelete = delete;
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
            convertView = Tools.loadLayout(context, R.layout.item_taskcontent);
            viewHolder.itemcontent_img = (ImageView) convertView.findViewById(R.id.itemcontent_img);
            viewHolder.itemcontent_name = (TextView) convertView.findViewById(R.id.itemcontent_name);
            viewHolder.itemcontent_edit = (TextView) convertView.findViewById(R.id.itemcontent_edit);
            viewHolder.itemcontent_add = (LinearLayout) convertView.findViewById(R.id.itemcontent_add);
            viewHolder.itemcontent_ly = convertView.findViewById(R.id.itemcontent_ly);
            viewHolder.itemcontent_move = convertView.findViewById(R.id.itemcontent_move);
            viewHolder.itemcontent_delete = (TextView) convertView.findViewById(R.id.itemcontent_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemcontent_edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick1 = true;
                return false;
            }
        });
        viewHolder.itemcontent_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick2 = true;
                return false;
            }
        });
        TaskListInfo taskListInfo = list.get(position);
        if ("-1".equals(taskListInfo.getTask_type())) {
            viewHolder.itemcontent_move.setVisibility(View.GONE);
        } else {
            viewHolder.itemcontent_move.setVisibility(View.VISIBLE);
        }
        viewHolder.itemcontent_name.setText(taskListInfo.getTask_name());
        if (isDelete) {
            viewHolder.itemcontent_move.scrollTo(150, 0);
            viewHolder.itemcontent_delete.setVisibility(View.VISIBLE);
            viewHolder.itemcontent_ly.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
        } else {
            viewHolder.itemcontent_move.scrollTo(0, 0);
            viewHolder.itemcontent_delete.setVisibility(View.GONE);
            viewHolder.itemcontent_ly.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
        }
        viewHolder.itemcontent_delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick3 = true;
                return false;
            }
        });
        String type = taskListInfo.getTask_type();
        if ("1".equals(type)) {//拍照
            viewHolder.itemcontent_img.setImageResource(R.mipmap.take_photo);
        } else if ("2".equals(type)) {//视频
            viewHolder.itemcontent_img.setImageResource(R.mipmap.take_viedo);
        } else if ("3".equals(type)) {//问卷
            viewHolder.itemcontent_img.setImageResource(R.mipmap.take_record);
        } else if ("5".equals(type)) {//录音
            viewHolder.itemcontent_img.setImageResource(R.mipmap.take_tape);
        } else if ("9".equals(type)) {//体验
            viewHolder.itemcontent_img.setImageResource(R.mipmap.take_exp);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView itemcontent_img;
        private TextView itemcontent_name, itemcontent_edit, itemcontent_delete;
        private LinearLayout itemcontent_add;
        private View itemcontent_ly, itemcontent_move;
    }
}
