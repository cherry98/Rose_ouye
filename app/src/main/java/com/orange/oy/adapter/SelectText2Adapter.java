package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.ClassifyActivity;
import com.orange.oy.base.Tools;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/6/8.
 * 选择主题分类（地址）
 */

public class SelectText2Adapter extends BaseAdapter {
    private Context context;
    private ArrayList<ClassifyActivity.MyThemeInfo> list;

    public SelectText2Adapter(Context context, ArrayList<ClassifyActivity.MyThemeInfo> list) {
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
        TextView textView;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.itemselect_text);
            textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }
        textView.setText(list.get(position).getTheme_name());
        return convertView;
    }
}
