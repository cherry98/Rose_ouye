package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.clusterutil.projection.Point;
import com.orange.oy.info.shakephoto.QuestionListInfo;
import com.orange.oy.view.MyListView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/9/6.
 * 问卷任务 V3.21
 */

public class TaskRecordAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<QuestionListInfo> list;

    public TaskRecordAdapter(Context context, ArrayList<QuestionListInfo> list) {
        this.context = context;
        this.list = list;
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
            convertView = Tools.loadLayout(context, R.layout.item_taskrecord);
            viewHolder.itemrecord_min = (TextView) convertView.findViewById(R.id.itemrecord_min);
            viewHolder.itemrecord_name = (TextView) convertView.findViewById(R.id.itemrecord_name);
            viewHolder.itemrecord_edit = (TextView) convertView.findViewById(R.id.itemrecord_edit);
            viewHolder.itemrecord_delete = (ImageView) convertView.findViewById(R.id.itemrecord_delete);
            viewHolder.itemrecord_listview = (MyListView) convertView.findViewById(R.id.itemrecord_listview);
            viewHolder.itemrecord_blank = (EditText) convertView.findViewById(R.id.itemrecord_blank);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        QuestionListInfo questionListInfo = list.get(position);

        viewHolder.itemrecord_name.setText(questionListInfo.getQuestion_num() + "." + questionListInfo.getQuestion_name());
        String question_type = questionListInfo.getQuestion_type();//类型
        SubjectAdapter subjectAdapter = new SubjectAdapter(context, questionListInfo.getOptions());
        if ("1".equals(question_type)) {//单选
            viewHolder.itemrecord_min.setVisibility(View.GONE);
            viewHolder.itemrecord_listview.setVisibility(View.VISIBLE);
            viewHolder.itemrecord_blank.setVisibility(View.GONE);
            viewHolder.itemrecord_listview.setAdapter(subjectAdapter);
            subjectAdapter.setSingle(true);
        } else if ("2".equals(question_type)) {//多选
            viewHolder.itemrecord_min.setVisibility(View.VISIBLE);
            viewHolder.itemrecord_listview.setVisibility(View.VISIBLE);
            viewHolder.itemrecord_blank.setVisibility(View.GONE);
            subjectAdapter.setSingle(false);
            viewHolder.itemrecord_listview.setAdapter(subjectAdapter);
            viewHolder.itemrecord_min.setText("[至少选择" + questionListInfo.getMin_option() + "个]");
        } else if ("4".equals(question_type)) {//填空
            viewHolder.itemrecord_min.setVisibility(View.GONE);
            viewHolder.itemrecord_listview.setVisibility(View.GONE);
            viewHolder.itemrecord_blank.setVisibility(View.VISIBLE);
        }
        viewHolder.itemrecord_delete.setOnClickListener(new MyOnClickListener(1, position));
        viewHolder.itemrecord_edit.setOnClickListener(new MyOnClickListener(2, position));
        return convertView;
    }

    class ViewHolder {
        private TextView itemrecord_min, itemrecord_name, itemrecord_edit;
        private ImageView itemrecord_delete;
        private MyListView itemrecord_listview;//单选多选
        private EditText itemrecord_blank;//填空
    }

    private OnTaskRecordListener onTaskRecordListener;

    public void setOnTaskRecordListener(OnTaskRecordListener onTaskRecordListener) {
        this.onTaskRecordListener = onTaskRecordListener;
    }

    public interface OnTaskRecordListener {
        void deleteItem(int position);

        void editItem(int position);
    }

    class MyOnClickListener implements View.OnClickListener {
        int type;
        int position;

        public MyOnClickListener(int type, int position) {
            this.type = type;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (type == 1) {
                onTaskRecordListener.deleteItem(position);
            } else {
                onTaskRecordListener.editItem(position);
            }
        }
    }
}

