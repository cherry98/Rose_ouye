package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.NewCommentInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

/**
 * 新评论 item 里面的listitem
 */

public class ItemCommentAdadpter extends BaseAdapter {
    private Context context;
    private ArrayList<NewCommentInfo.CommentsBean> list;
    private ImageLoader imageLoader;
    private OnItemCheckListener onItemCheckListener;
    private int parPosition;

    public ItemCommentAdadpter(Context context, ArrayList<NewCommentInfo.CommentsBean> list) {
        this.list = list;
        this.context = context;
        imageLoader = new ImageLoader(context);
    }


    @SuppressWarnings("unchecked")
    public void setList(ArrayList<NewCommentInfo.CommentsBean> list1) {
        if (list1 != null) {
            this.list = list1;
            list = (ArrayList<NewCommentInfo.CommentsBean>) list1.clone();
            notifyDataSetChanged();
        }
    }

    public void clearDeviceList() {
        if (list != null) {
            list.clear();
        }
        notifyDataSetChanged();
    }

    public int getParPosition() {
        return parPosition;
    }

    public void setParPosition(int parPosition) {
        this.parPosition = parPosition;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
        final ImageSelectAdapterViewhold viewHolder;
        if (convertView == null) {
            viewHolder = new ImageSelectAdapterViewhold();
            convertView = Tools.loadLayout(context, R.layout.item_comment_item);
            viewHolder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            viewHolder.iv_header = (CircularImageView) convertView.findViewById(R.id.iv_header);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.item_reply = (TextView) convertView.findViewById(R.id.item_reply);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_ems = (TextView) convertView.findViewById(R.id.tv_ems);
            viewHolder.tv_names = (TextView) convertView.findViewById(R.id.tv_names);
            viewHolder.tv_praisenum = (TextView) convertView.findViewById(R.id.tv_praisenum);
            viewHolder.lin_bottom = (LinearLayout) convertView.findViewById(R.id.lin_bottom);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageSelectAdapterViewhold) convertView.getTag();
        }
        viewHolder.parPosition = parPosition;
        final NewCommentInfo.CommentsBean commentsBean = list.get(position);

        viewHolder.tv_ems.setText(commentsBean.getComment());
        viewHolder.tv_names.setText(commentsBean.getPraise_user());
        viewHolder.tv_name.setText(commentsBean.getUser_name());
        viewHolder.tv_praisenum.setText(commentsBean.getPraise_num());
        viewHolder.tv_time.setText(commentsBean.getCreate_time());

        if (!Tools.isEmpty(commentsBean.getPraise_user()) && !"0".equals(commentsBean.getPraise_num()) && !Tools.isEmpty(commentsBean.getPraise_num())) {
            viewHolder.lin_bottom.setVisibility(View.VISIBLE);
        } else {
            viewHolder.lin_bottom.setVisibility(View.GONE);
        }

        viewHolder.item_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemCheckListener.onItemCheck(commentsBean);
            }
        });
        String url = Urls.Endpoint3 + commentsBean.getFile_url();
        imageLoader.setShowWH(200).DisplayImage(url, viewHolder.iv_img, -2);
        imageLoader.setShowWH(200).DisplayImage(Urls.ImgIp + commentsBean.getUser_img(), viewHolder.iv_header, -2);
        return convertView;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public interface OnItemCheckListener {
        void onItemCheck(NewCommentInfo.CommentsBean commentsBean);
    }

    public class ImageSelectAdapterViewhold {
        ImageView iv_img;
        int parPosition;
        TextView item_reply, tv_name, tv_time, tv_ems, tv_names, tv_praisenum;
        CircularImageView iv_header;
        LinearLayout lin_bottom;
    }

}
