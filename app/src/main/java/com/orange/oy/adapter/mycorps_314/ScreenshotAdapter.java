package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.ScreenshotInfo;
import com.orange.oy.view.MyGridView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/9/7.
 */

public class ScreenshotAdapter extends BaseAdapter {
    public interface OnScreenshotItemClickListener {
        void screeshotitemClick(int parPosition, int position);
    }

    private Context context;
    private ArrayList<ScreenshotInfo> list;
    private OnScreenshotItemClickListener onScreenshotItemClickListener;

    public void setOnScreenshotItemClickListener(OnScreenshotItemClickListener onScreenshotItemClickListener) {
        this.onScreenshotItemClickListener = onScreenshotItemClickListener;
    }

    public ScreenshotAdapter(Context context, ArrayList<ScreenshotInfo> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_screenshot);
            viewHolder.itemscreen_name = (TextView) convertView.findViewById(R.id.itemscreen_name);
            viewHolder.itemscreen_love = (TextView) convertView.findViewById(R.id.itemscreen_love);
            viewHolder.itemscreen_gridview = (MyGridView) convertView.findViewById(R.id.itemscreen_gridview);
            viewHolder.itemscreen_view = convertView.findViewById(R.id.itemscreen_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ScreenshotInfo screenshotInfo = list.get(position);
        viewHolder.itemscreen_name.setText(screenshotInfo.getPage_name());
        String praise_num = screenshotInfo.getPraise_num();
        if (Tools.isEmpty(praise_num)) {
            viewHolder.itemscreen_love.setText("");
        } else {
            viewHolder.itemscreen_love.setText(praise_num);
        }
        if (position == list.size() - 1) {
            viewHolder.itemscreen_view.setVisibility(View.GONE);
        } else {
            viewHolder.itemscreen_view.setVisibility(View.VISIBLE);
        }
        ArrayList<ScreenshotInfo.PrintscreenListBean> listBeens = screenshotInfo.getPrintscreen_list();
        if (listBeens != null && listBeens.size() > 0) {
            ScreenImageAdapter screenImageAdapter = new ScreenImageAdapter(context, listBeens);
            viewHolder.itemscreen_gridview.setAdapter(screenImageAdapter);
            if (onScreenshotItemClickListener != null) {
                viewHolder.itemscreen_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int pi, long id) {
                        onScreenshotItemClickListener.screeshotitemClick(position, pi);
                    }
                });
            }
        } else {
            viewHolder.itemscreen_gridview.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemscreen_name, itemscreen_love;
        private MyGridView itemscreen_gridview;
        private View itemscreen_view;
    }
}
