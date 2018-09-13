package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskitemListInfo;

import java.util.ArrayList;

/**
 * 任务列表适配器 //TODO NEW
 */
public class TaskitemListAdapter extends BaseAdapter {
    private ArrayList<TaskitemListInfo> list;
    private Context context;

    public TaskitemListAdapter(Context context, ArrayList<TaskitemListInfo> list) {
        this.list = list;
        this.context = context;
    }

    public void resetList(ArrayList<TaskitemListInfo> list) {
        this.list = list;
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold = null;
        if (convertView == null) {
            viewHold = new ViewHold();
            convertView = Tools.loadLayout(context, R.layout.listviewitem_taskitemlist);
            viewHold.image = (ImageView) convertView.findViewById(R.id.item_taskitemlist_ico);
            viewHold.name = (TextView) convertView.findViewById(R.id.item_taskitemlist_package_name);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        TaskitemListInfo taskitemListInfo = list.get(position);
        viewHold.name.setText(taskitemListInfo.getTaskname());
        if (taskitemListInfo.getType().equals("1")) {
            viewHold.image.setImageResource(R.mipmap.take_photo);
        } else if (taskitemListInfo.getType().equals("2")) {
            viewHold.image.setImageResource(R.mipmap.take_viedo);
        } else if (taskitemListInfo.getType().equals("3")) {
            viewHold.image.setImageResource(R.mipmap.take_record);
        } else if (taskitemListInfo.getType().equals("4")) {
            viewHold.image.setImageResource(R.mipmap.take_location);
        } else if (taskitemListInfo.getType().equals("5")) {
            viewHold.image.setImageResource(R.mipmap.take_record);
        }else if (taskitemListInfo.getType().equals("8")) {
            viewHold.image.setImageResource(R.mipmap.take_photo);
        }else if (taskitemListInfo.getType().equals("9")) {
            viewHold.image.setImageResource(R.mipmap.take_exp);
        }
        return convertView;
    }

    class ViewHold {
        private ImageView image;
        private TextView name;
    }
}
