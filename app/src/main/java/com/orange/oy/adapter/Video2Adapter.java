package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.activity.VideoViewActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.MyImageView;

import java.util.List;

/**
 * 示例视频任务的 adapter
 */

public class Video2Adapter extends BaseAdapter {
    Context context;
    private List list;
    private boolean isEdit;

    public Video2Adapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isEdit() {
        return isEdit;
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
        ImageResetAdapter2ViewHold viewHolder;
        if (convertView == null) {
            viewHolder = new ImageResetAdapter2ViewHold();
            convertView = Tools.loadLayout(context, R.layout.item_video2);
            viewHolder.itemimage_img2 = (MyImageView) convertView.findViewById(R.id.itemimage_img);
            viewHolder.taskitemshot_shot_play = (ImageView) convertView.findViewById(R.id.taskitemshot_shot_play);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageResetAdapter2ViewHold) convertView.getTag();
        }
        String path = list.get(position).toString();
        viewHolder.itemimage_img2.setmImageThumbnail(path);
        viewHolder.taskitemshot_shot_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoViewActivity.class);
                intent.putExtra("path", list.get(position).toString());
                context.startActivity(intent);
            }
        });
        return convertView;
    }


}
