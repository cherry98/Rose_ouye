package com.orange.oy.adapter.mycorps_314;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.fragment.TaskPublishFragment;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/6/25.
 * 随手发任务 V3.17
 */

public class TaskPublicAdapter extends BaseAdapter {
    private Context context;
    private DisplayMetrics dm;
    private ArrayList<TaskPublishFragment.TemplateInfo> list;
    private ImageLoader imageLoader;

    public TaskPublicAdapter(Context context, ArrayList<TaskPublishFragment.TemplateInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
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
            convertView = Tools.loadLayout(context, R.layout.item_taskpublic);
            viewHolder.itempublic_text = (TextView) convertView.findViewById(R.id.itempublic_text);
            viewHolder.itempublic_img = (ImageView) convertView.findViewById(R.id.itempublic_img);
            viewHolder.itempublic_ly = convertView.findViewById(R.id.itempublic_ly);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(12, 0, 12, 0);
        viewHolder.itempublic_ly.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.itempublic_ly.getLayoutParams();
        lp.height = 220;
        viewHolder.itempublic_ly.setLayoutParams(lp);
        TaskPublishFragment.TemplateInfo templateInfo = list.get(position);
        imageLoader.DisplayImage(Urls.ImgIp + templateInfo.getTemplate_img(), viewHolder.itempublic_img);
        viewHolder.itempublic_text.setText(templateInfo.getTemplate_name());
        return convertView;
    }

    class ViewHolder {
        private View itempublic_ly;
        private ImageView itempublic_img;
        private TextView itempublic_text;
    }

    public int dipToPx(int dip) {
        return (int) (dip * dm.density + 0.5f);
    }
}
