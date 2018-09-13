package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.ThemeActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskPhotoInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.view.MyImageView;

import java.util.ArrayList;

import static com.orange.oy.R.id.itemimage_img2;
import static com.orange.oy.R.id.iv_delete;
import static com.orange.oy.R.id.taskitemshot_shot_play;
import static com.orange.oy.R.id.taskitemshot_video1;
import static com.orange.oy.R.id.url;
import static com.sobot.chat.utils.LogUtils.path;

/**
 * 示例视频任务的 adapter
 */

public class VideoAdapter extends BaseAdapter {
    Context context;
    private ArrayList<TaskPhotoInfo> list;
    private boolean isEdit;

    public VideoAdapter(Context context, ArrayList<TaskPhotoInfo> list) {
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageResetAdapter2ViewHold viewHolder;
        if (convertView == null) {
            viewHolder = new ImageResetAdapter2ViewHold();
            convertView = Tools.loadLayout(context, R.layout.item_video);
            viewHolder.itemimage_check = (ImageView) convertView.findViewById(R.id.itemimage_check);
            viewHolder.itemimage_img2 = (MyImageView) convertView.findViewById(itemimage_img2);
            viewHolder.itemimage_img2.getmImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.taskitemshot_shot_play = (ImageView) convertView.findViewById(R.id.taskitemshot_shot_play);
            viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageResetAdapter2ViewHold) convertView.getTag();
        }
        final TaskPhotoInfo taskPhotoInfo = list.get(position);
        taskPhotoInfo.setBindView(convertView);

        viewHolder.itemimage_img2.setVisibility(View.VISIBLE);
        if (taskPhotoInfo.getPath().startsWith("add")) {
            viewHolder.itemimage_img2.setText("");
            viewHolder.itemimage_img2.setAlpha(1f);
            viewHolder.itemimage_img2.setImageResource(R.mipmap.psp_button_shipin);
            viewHolder.taskitemshot_shot_play.setVisibility(View.GONE);
            viewHolder.iv_delete.setVisibility(View.GONE);

        } else {
            viewHolder.taskitemshot_shot_play.setVisibility(View.VISIBLE);
            if (!Tools.isEmpty(taskPhotoInfo.getPath())) {
                viewHolder.itemimage_img2.setmImageThumbnail(taskPhotoInfo.getPath());
            }

            if (!taskPhotoInfo.isLocal()) {
                if (taskPhotoInfo.isUped()) {
                    viewHolder.itemimage_img2.setText("100%" + "\n上传成功");
                    viewHolder.itemimage_img2.setAlpha(1f);
                } else {
                    viewHolder.itemimage_img2.setText(" " + "\n等待上传");
                    viewHolder.itemimage_img2.setAlpha(0.4f);
                }
            } else {
                viewHolder.itemimage_img2.setText("");
                viewHolder.itemimage_img2.setAlpha(1f);
            }

            if (null != taskPhotoInfo.getPath()) {
                if (!"add_photo".equals(taskPhotoInfo.getPath())) {
                    if (taskPhotoInfo.isUped()) {
                        viewHolder.iv_delete.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.iv_delete.setVisibility(View.GONE);
                    }
                }
            } else {
                viewHolder.iv_delete.setVisibility(View.GONE);
            }


            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != taskPhotoInfo.getUpUrl()) {
                        deleteInf.deleteClick(position);
                    }
                }
            });
        }
        return convertView;
    }

    private DeleteInf deleteInf;

    public void setDeleteInf(DeleteInf d) {
        this.deleteInf = d;
    }

    public interface DeleteInf {
        void deleteClick(int pos);
    }
}
