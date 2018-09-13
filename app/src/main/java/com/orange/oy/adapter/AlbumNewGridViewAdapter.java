package com.orange.oy.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.orange.oy.R;
import com.orange.oy.util.ImageManager2;

import java.util.ArrayList;

public class AlbumNewGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> dataList;
    private ArrayList<String> selectedDataList;
    private DisplayMetrics dm;
    private int onlyShow = 0;
//    private boolean isExperience;

    public AlbumNewGridViewAdapter(Context c, ArrayList<String> dataList, ArrayList<String> selectedDataList) {
        mContext = c;
        this.dataList = dataList;
        this.selectedDataList = selectedDataList;
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public void setOnlyShow(int onlyShow) {
        this.onlyShow = onlyShow;
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    /**
     * 存放列表项控件句柄
     */
    private class ViewHolder {
        public ImageView imageView;
        public ToggleButton toggleButton;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.select_imageview, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.toggleButton = (ToggleButton) convertView.findViewById(R.id.toggle_button);
            if (onlyShow == 1) {
                viewHolder.toggleButton.setVisibility(View.GONE);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path;
        if (position == 0) {
            path = "camera_default";
        } else {
            path = dataList.get(position);
        }
        if (path.equals("camera_default")) {
            viewHolder.imageView.setImageResource(R.mipmap.pzp_button_tjzp);
        } else {
            ImageManager2.from(mContext).displayImage(viewHolder.imageView, path, R.mipmap.camera_default, 250, 250);
        }
        viewHolder.toggleButton.setTag(position);
        // viewHolder.toggleButton.setOnClickListener(this);
        if (isInSelectedDataList(path)) {
            viewHolder.toggleButton.setChecked(true);
        } else {
            viewHolder.toggleButton.setChecked(false);
        }
        return convertView;
    }

    private boolean isInSelectedDataList(String selectedString) {
        for (int i = 0; i < selectedDataList.size(); i++) {
            if (selectedDataList.get(i).equals(selectedString)) {
                return true;
            }
        }
        return false;
    }
}
